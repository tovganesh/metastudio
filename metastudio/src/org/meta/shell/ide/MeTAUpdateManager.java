/*
 * MeTAUpdateManager.java 
 *
 * Created on 14 Sep, 2008 
 */

package org.meta.shell.ide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;

/**
 * Update manager for MeTA Studio. Follows a singleton pattern.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MeTAUpdateManager {
    
    private static MeTAUpdateManager _updateManage;
    
    private static String updateURL
              = "http://metastudio.googlecode.com/files/";
    
    private static String onLineUpdateFileName = "meta.version";
    
    private static String onLineUpdatePackageFileName = "metaupdate";
    private static String onLineUpdatePackageFileExt = ".zip";
    
    private static String updateFileList = "meta.inf";
    
    private String serverBuildNumber;
    
    /**
     * Creates a new instance of MeTAUpdateManager
     */
    private MeTAUpdateManager() {        
    }    
    
    /**
     * Get the instance of MeTAUpdateManager.
     * 
     * @return instance of MeTAUpdateManager
     */
    public synchronized static MeTAUpdateManager getInstance() {
        if (_updateManage == null) {
            _updateManage = new MeTAUpdateManager();
        } // end if
        
        return _updateManage;
    }
    
    /**
     * Check to see if there is an available update?
     * 
     * @throws java.lang.Exception in case the check fails!
     */
    public synchronized void check() throws Exception {
        // first see if the update directory is present, else create it
        File updateAppsDir = new File(StringResource.getInstance()
                                                    .getUpdateDir());
        
        if (!updateAppsDir.exists()) {
            if (!updateAppsDir.mkdir()) {
                System.out.println("Could not make remoteapps directory: " +
                                    updateAppsDir.toString());
                
                throw new IOException("Could not make remoteapps directory: " +
                                      updateAppsDir.toString());
            } // end if
        } // end if
        
        URL onLineUpdateURL = new URL(updateURL + onLineUpdateFileName);
        ArrayList<String> lines = Utility.readLines(onLineUpdateURL);

        System.out.println("Server version: " + lines.get(0));
        
        System.out.println("server line: " + lines.get(0));
        String[] buildNumbers = lines.get(0).trim().split("\\.+");
        System.out.println("length: " + buildNumbers.length);
        String buildNumber = buildNumbers[buildNumbers.length - 1];

        serverBuildNumber = buildNumber;
        
        if (lines.get(0).trim().equals(StringResource.getInstance().getVersion())) {
            newVersionAvailable = false;
        } else {             
            // next check the build stamp
            
            
            System.out.println("MeTAUpdateManager: Build number from server is "
                                + buildNumber);
            System.out.println("MeTAUpdateManager: Build number of current instance "
                                + StringResource.getInstance().getBuildNumber());
            
            // the build number is always ddmmyyyy
            // check to see individual fields if they are newer
            try {                
                int dd   = Integer.parseInt(buildNumber.substring(0, 1));
                int mm   = Integer.parseInt(buildNumber.substring(2, 3));
                int yyyy = Integer.parseInt(buildNumber.substring(3, 6));
                
                int mydd   = Integer.parseInt(StringResource.getInstance().getBuildNumber().substring(0, 1));
                int mymm   = Integer.parseInt(StringResource.getInstance().getBuildNumber().substring(2, 3));
                int myyyyy = Integer.parseInt(StringResource.getInstance().getBuildNumber().substring(3, 6));
                
                newVersionAvailable = false;
                if (yyyy > myyyyy) {
                    if (mm > mymm) {
                        if (dd > mydd) {
                            newVersionAvailable = true;
                        } // end if
                    } // end if
                } // end if
            } catch(Exception e) {              
                newVersionAvailable = false;
                System.err.println("MeTAUpdateManager: cannot determine update number, perform manual update.");
            } // end of try .. catch block
        } // end if

        if (lines.get(0).indexOf("upgrade") >= 0) {
            upgradeRequired = true;
        } else {
            upgradeRequired = false;
        } // end if
    }

    private boolean upgradeRequired;

    /**
     * Get the value of upgradeRequired
     *
     * @return the value of upgradeRequired
     */
    public boolean isUpgradeRequired() {
        return upgradeRequired;
    }

    /**
     * Set the value of upgradeRequired
     *
     * @param upgradeRequired new value of upgradeRequired
     */
    public void setUpgradeRequired(boolean upgradeRequired) {
        this.upgradeRequired = upgradeRequired;
    }

    private boolean newVersionAvailable = false;
    
    /**
     * Is there a new version available?
     * 
     * @return a variable indicating a new version
     */
    public boolean isNewVersionAvailable() {
         return newVersionAvailable; 
    }
    
    /**
     * Update with the new downloaded package.
     * 
     * @throws java.lang.Exception in case we fail!
     */
    public synchronized void update() throws Exception {
        // TODO: to be [correctly] implemented
        
        // first download the package...
        System.out.println("Downloading: " + updateURL 
                                           + onLineUpdatePackageFileName
                                           + "_" + serverBuildNumber
                                           + onLineUpdatePackageFileExt);
        StringResource strings = StringResource.getInstance();
        
        URL onLineUpdatePackageURL = new URL(updateURL 
                                             + onLineUpdatePackageFileName);
        URLConnection conn = onLineUpdatePackageURL.openConnection(); 
        
        InputStream istream  = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(strings.getUpdateDir()
                                                 + File.separatorChar 
                                                 + onLineUpdatePackageFileName);

        Utility.streamCopy(istream, fos);
        
        fos.close();
        istream.close();        
        
        System.out.println("Download complete. Updating...");
        
        // then unzip the package, read meta.inf
        ZipFile updates = new ZipFile(strings.getUpdateDir()
                                      + File.separatorChar 
                                      + onLineUpdatePackageFileName);
        
        ZipEntry metaInf = updates.getEntry(updateFileList);
        ArrayList<String> updateFiles = Utility.readLines(new BufferedReader(
                     new InputStreamReader(updates.getInputStream(metaInf))));
        
        // extract the files in places indicated in meta.inf
        for(String updateFile : updateFiles) {
            String theFile    = updateFile.split(";")[0].trim();
            String updatePath = updateFile.split(";")[1].trim();
            
            System.out.println("Updating: " + theFile);
            
            ZipEntry toUpdate = updates.getEntry(theFile);
            InputStream toUpdateIS = updates.getInputStream(toUpdate);
            FileOutputStream fosUpdate   = new FileOutputStream(updatePath
                                          + File.separatorChar + theFile);
            Utility.streamCopy(toUpdateIS, fosUpdate);
            
            toUpdateIS.close(); fosUpdate.close();
        } // end for
        
        System.out.println("Update complete. Restart IDE to apply changes.");
    }
}
