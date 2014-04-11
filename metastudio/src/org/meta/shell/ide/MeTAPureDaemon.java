/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.ide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.net.ssl.SSLSocket;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.mar.MarObject;
import org.meta.mar.executer.MarExecuter;
import org.meta.mar.io.MarReader;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceDiscovery;
import org.meta.net.event.FederationDiscoveryEvent;
import org.meta.net.event.FederationDiscoveryListener;
import org.meta.net.security.FederationSecurityShield;
import org.meta.net.security.FederationSecurityShieldPromptHandler;

/**
 * Pure daemon mode of MeTA Studio.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MeTAPureDaemon {    
    
    /**
     * MeTA Studio in pure daemon mode, good for running background services.
     */
    public MeTAPureDaemon() {
        // init the serivices
        if (initServices()) {
            System.out.println(StringResource.getInstance().getVersion()
                               + " running in daemon mode...");
            System.out.print("Loading external libs...");
            loadExternalLibs();
            System.out.println(" Done.\n");
        } else {
            System.out.println(StringResource.getInstance().getVersion()
                    + " could not start in daemon mode... exiting!");
        } // end if
    }
    
    /**
     * MeTA Studio in pure daemon mode, good for running scripts from command
     * line. Note that it is the of the responsibility of executing script to 
     * exit out of the JVM that is spawned.
     * 
     * @param scriptFileName the name of the script file
     */
    public MeTAPureDaemon(String scriptFileName) {
        this(scriptFileName, true);        
    }
    
    /**
     * MeTA Studio in pure daemon mode, good for running scripts from command
     * line. Note that it is the responsibility of executing script to 
     * exit out of the JVM that is spawned.
     * 
     * @param scriptFileName the name of the script file
     * @param startFederation whether to start the federation service
     */
    public MeTAPureDaemon(String scriptFileName, boolean startFederation) {
        this(scriptFileName, startFederation, false);
    }
    
    /**
     * MeTA Studio in pure daemon mode, good for running scripts from command
     * line. Note that it is the responsibility of executing script to 
     * exit out of the JVM that is spawned.
     * 
     * @param scriptFileName the name of the script file
     * @param startFederation whether to start the federation service
     * @param isMar is this a MeTA Studio archive file?
     */
    public MeTAPureDaemon(String scriptFileName, boolean startFederation,
                          boolean isMar) {
        StringResource strings = StringResource.getInstance();
        
        // 'chroot' for the new application, in the process ensure that 
        // that the 'root' exists!
        System.out.print(strings.getVersion());
        String appName = Utility.getFileNameSansExtension(new File(scriptFileName));
        Utility.changeRoot(appName);
        
        // init the serivices
        if (startFederation) {
          if (initServices()) {
            System.out.println(" running in daemon mode... [Script execution]");
          } else {
            System.out.println(StringResource.getInstance().getVersion()
                    + " could not start in daemon mode... exiting!");
          } // end if
        } // end if

        System.out.print(" Loading external libs...");
        loadExternalLibs();
        System.out.println(" Done.\n");

        if (!isMar) {
            try {
                Utility.executeScriptFile(scriptFileName);
            } catch (Exception e) {
                System.err.println("Error in executing script '"
                        + scriptFileName + "' : " + e.toString());
                e.printStackTrace();
            } // end of try .. catch block
        } else {
            try {
                MarExecuter marExecuter = new MarExecuter();
                MarObject marObject = new MarReader().read(scriptFileName);

                marExecuter.execute(marObject);
            } catch (Exception e) {
                System.err.println("Error in executing script '"
                        + scriptFileName + "' : " + e.toString());
                e.printStackTrace();
            } // end of try .. catch block
        } // end if
    }

    /**
     * Add specified libraries to MeTA Studio installation.
     * 
     * Note: the first argument of the array is always ignored.
     * 
     * @param args the argument list
     */
    public static void addLibs(String[] args) throws Exception {
        StringResource strings = StringResource.getInstance();
        
        System.out.println("Adding external libraries to: "
                           + strings.getVersion());
        
        // check if lib/ext is present .. else create it and exit
        File extLibDir = new File(strings.getExtLibDir());
        
        if (!extLibDir.exists()) {
            extLibDir.mkdir();
            
            System.out.println("WARNING: " + extLibDir.getAbsolutePath() +
                               " is empty, kindly put in the libraries" +
                               " you want to load automatically in this" +
                               " directory and rerun with --addlibs option.");
            return;
        } // end if
        
        // then check if extLibs file is present, if not create it
        File extLibFile = new File(strings.getExtLibFile());
        if (!extLibFile.exists()) {
            FileOutputStream fos = new FileOutputStream(extLibFile);
            fos.close();
        } // end if
        
        // next, browse through each file, check if it is present
        // and then add it to extLibs file.
        ArrayList<String> extLibs = 
                             Utility.readLines(extLibFile.toURI().toURL());
        
        // first argument is the option --addlibs
        for(int i=1; i<args.length; i++) {
            String newExtLib = args[i] + ".jar";
            File newExtLibFile = new File(extLibDir.getAbsolutePath()
                                     + File.separatorChar + newExtLib);
            
            System.out.print("Adding '" + newExtLibFile.getAbsolutePath() 
                               + "' ... ");
            if (!newExtLibFile.exists()) { 
                System.out.println(" Skipping [Not Present].");
            } else {
                System.out.println(" Added.");
                if (!extLibs.contains(newExtLib))
                    extLibs.add(newExtLib);
            } // end if
        } // end for
        
        FileWriter writer = new FileWriter(extLibFile);  
        for(String extLib : extLibs) {
            writer.write(extLib + "\n");
        } // end for
        writer.close();
    }
    
    /**
     * init federation discovery / providers
     * 
     * @return true if success, else false
     */
    private boolean initServices() {
        // start federation discovery service
        FederationServiceDiscovery d = FederationServiceDiscovery.getInstance();
        
        // add listeners to discovery service
        d.addFederationDiscoveryListener(new FederationDiscoveryListener() {
            @Override
            public void federated(FederationDiscoveryEvent fde) {
                String msg = fde.getDiscoveryType() + ": " + fde.getMessage();
                
                System.out.println(msg);                                
            }
        });
        
        // start local federation service provider
        try {
            FederationService.getInstance();
        } catch (Exception e) {
            System.err.println("Federation service couldn't be started: " + e);
            e.printStackTrace();
            
            return false;
        } // end of try .. catch block                
        
        // Next start up the federation sercurity shield, and define the prompt
        // handler. If in deamon mode, the prompt handler always returns false.
        FederationSecurityShield fss = FederationSecurityShield.getInstance();
        fss.setPromptHandler(new FederationSecurityShieldPromptHandler() {
           @Override
           public boolean promptRequest(FederationRequestType requestType, 
                                         SSLSocket federatingClient) {
               // no prompts in daemon mode! 
               return false;                              
           }
        });
                
        // if control reaches here, probably we have done initing the services
        return true;
    }

    /**
     * try loading external libraries
     */
    private void loadExternalLibs() {        
        try {
            setNativeLibraryPath();
            
            String [] libs = Utility.getPlatformSpecificJars();
            int ii = 0;
            for(String lib : libs)
                libs[ii++] = Utility.getPlatformClassPath(lib);

            Utility.appendExtLibClassPaths(libs);
        } catch (Exception ex) {
            System.out.println("Unable to load external libraries: " 
                               + ex.toString());
            ex.printStackTrace();
        } // end try .. catch block
    }

    /**
     * WARNING: This should have been never like this, should
     * be changed when a appropriately supported solution is found.
     *
     * This hack is based on http://forums.sun.com/thread.jspa?threadID=707176
     * and changed the private variables of the JVM class loader to make
     * runtime access to native libraries.
     */
    private void setNativeLibraryPath() {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);

            String[] paths = (String[])field.get(null);
            String[] tmp = new String[paths.length+1];

            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = Utility.getPlatformSpecificLibraryPath();
            field.set(null, tmp);

            System.setProperty("java.library.path",
                               Utility.getPlatformLibraryPaths());
        } catch(Exception ex) {
            System.out.println("Unable to set paths for external native libraries: "
                               + ex.toString());
            ex.printStackTrace();
        } // end of try .. catch block
    }
}
