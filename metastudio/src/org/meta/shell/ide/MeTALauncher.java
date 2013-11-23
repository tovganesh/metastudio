/*
 * MeTALauncher.java
 *
 * Created on November 15, 2005, 7:44 AM
 *
 */

package org.meta.shell.ide;

import java.io.*;

// JAXP packages
import org.w3c.dom.*;

import org.meta.common.Utility;
import org.meta.common.resource.StringResource;

/**
 * A simple platform independent launcher to take care of availability of 
 * more memory to the JVM launching MeTA Studio. 
 *
 * @author V.Ganesh 
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MeTALauncher {
    
    /** Creates a new instance of MeTALauncher
     * 
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException */
    public MeTALauncher() throws IOException, InterruptedException {
        // set the defaults
        javaBin = "bin";
        javaExe = "java";
        metaClass = "org.meta.shell.ide.MeTApp";
        maxJVMMemory = 256; // 256 MB;
        memoryUnits  = "m"; // for MB
        minAllowedMemoryInMB = 70; // 70 MB;
        
        // read the ones saved, or save these        
        setDefaultParameters();        
        String libPath = Utility.getPlatformLibraryPaths();
        String clsPath = getPlatformClassPaths();

        // start the actual meta process
        Process metaProcess = new ProcessBuilder(
                         System.getProperty("java.home") + File.separatorChar
                               + javaBin + File.separatorChar + javaExe, 
                         (clsPath==null ? "" : "-Djava.class.path="+clsPath),
                         "-Xmx" + maxJVMMemory + memoryUnits, 
                         (libPath==null ? "" : "-Djava.library.path="+libPath),
                         metaClass, 
                         "--morememory").start();
        // TODO: not needed ?
        // metaProcess.waitFor();
    }

    /** Creates a new instance of MeTALauncher */
    private MeTALauncher(boolean config) throws IOException {
        // this is a config version, 'config' itself is a dummy variable!
        // set the defaults
        javaBin = "bin";
        javaExe = "java";
        metaClass = "org.meta.shell.ide.MeTApp";
        maxJVMMemory = 256; // 256 MB;
        memoryUnits  = "m"; // for MB
        minAllowedMemoryInMB = 70; // 70 MB;
        
        // read the ones saved, or save these
        setDefaultParameters();
    }

    /**
     * setup paths to the platform classes
     */
    private String getPlatformClassPaths() {
        try {
            String libs = "";

            for(String lib : Utility.getPlatformSpecificJars())
              libs += File.pathSeparatorChar + Utility.getPlatformClassPath(lib);

            return (System.getProperty("java.class.path") + libs);
        } catch (Exception ignored) { return null; }
    }
    
    /**
     * the launcher instance
     */
    private static MeTALauncher _metaLauncher;
    
    /**
     * get configuration instance of meta launcher
     */
    public static MeTALauncher getConfigInstance() throws IOException {
        if (_metaLauncher == null) {
            _metaLauncher = new MeTALauncher(true);
        } // end if
        
        return _metaLauncher;
    }
    
    /** read the default parameters or create them if run for the first time */ 
    private void setDefaultParameters() throws IOException {
        StringResource strings = StringResource.getInstance();
        
        if ((new File(strings.getLauncherConfig())).exists()) {
            // this config file already exists, read in the default parameters
            // read the internal XML config file
            try {
                Document configDoc 
                               = Utility.parseXML(strings.getLauncherConfig());
                
                saveConfig(configDoc);
            } catch (Exception e) {
                throw new IOException("Parsing error, can't start launcher.");
            } // end of try .. catch block
        } else {
            // no config file, probably running first time; create with default
            // set of parameters
            saveLauncherConfig();
        } // end if
    }
    
    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveConfig(Node n) {
        int type = n.getNodeType();   // get node type
        
        switch (type) {
        case Node.ATTRIBUTE_NODE:
            String nodeName = n.getNodeName();
            
            if (nodeName.equals("javaBin")) { 
                javaBin = n.getNodeValue();
            } else if (nodeName.equals("javaExe")) { 
                javaExe = n.getNodeValue();
            } else if (nodeName.equals("maxJVMMemory")) {                
                maxJVMMemory = Long.parseLong(n.getNodeValue());
            } else if (nodeName.equals("units")) {                
                memoryUnits = n.getNodeValue();
            } else if (nodeName.equals("metaClass")) {                
                metaClass = n.getNodeValue();
            } // end if
            
            break;
        case Node.ELEMENT_NODE:            
            NamedNodeMap atts = n.getAttributes();
            
            // save the others
            for (int i = 0; i < atts.getLength(); i++) {
                Node att = atts.item(i);
                saveConfig(att);
            } // end for
            break;
        default:
            break;
        } // end of switch .. case block
        
        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveConfig(child);
        } // end for
    }
    
    /**
     * Saves the launcher config file to a default MeTA Studio config folder.
     */
    public void saveLauncherConfig() throws IOException {
        StringResource strings = StringResource.getInstance();        
        FileWriter fw = new FileWriter(strings.getLauncherConfig());
        
        fw.write(strings.getXmlHeader());
        fw.write("<!-- \n WARNING: This file contains sensitive parameters" +
                " that are crutial to the correct functioning of the" +
                " MeTA Studio launcher. \n Any inconsistant/ wrong changes" +
                " to this file could lead to improper functioning of the" +
                " launcher. \n --> \n");
        fw.write("<launcher> \n");
        fw.write("    <jvm javaBin=\"" + javaBin 
                           + "\" javaExe=\"" + javaExe + "\" /> \n");
        fw.write("    <jvmmemory maxJVMMemory=\"" + maxJVMMemory 
                           + "\" units=\"" + memoryUnits + "\" /> \n");
        fw.write("    <meta metaClass=\"" + metaClass + "\" /> \n");
        fw.write("</launcher> \n");
        
        fw.close();
    }
    
    /**
     * Holds value of property javaBin.
     */
    private String javaBin;

    /**
     * Getter for property javaBin.
     * @return Value of property javaBin.
     */
    public String getJavaBin()  {
        return this.javaBin;
    }

    /**
     * Setter for property javaBin.
     * @param javaBin New value of property javaBin.
     */
    public void setJavaBin(String javaBin)  {
        this.javaBin = javaBin;
    }

    /**
     * Holds value of property javaExe.
     */
    private String javaExe;

    /**
     * Getter for property javaExe.
     * @return Value of property javaExe.
     */
    public String getJavaExe() {
        return this.javaExe;
    }

    /**
     * Setter for property javaExe.
     * @param javaExe New value of property javaExe.
     */
    public void setJavaExe(String javaExe) {
        this.javaExe = javaExe;
    }

    /**
     * Holds value of property maxJVMMemory.
     */
    private long maxJVMMemory;

    /**
     * Getter for property maxJVMMemory.
     * @return Value of property maxJVMMemory.
     */
    public long getMaxJVMMemory() {
        return this.maxJVMMemory;
    }

    /**
     * Setter for property maxJVMMemory.
     * @param maxJVMMemory New value of property maxJVMMemory.
     */
    public void setMaxJVMMemory(long maxJVMMemory) {
        this.maxJVMMemory = maxJVMMemory;
    }

    /**
     * Holds value of property memoryUnits.
     */
    private String memoryUnits;

    /**
     * Getter for property memoryUnits.
     * @return Value of property memoryUnits.
     */
    public String getMemoryUnits() {
        return this.memoryUnits;
    }

    /**
     * Setter for property memoryUnits.
     * @param memoryUnits New value of property memoryUnits.
     */
    public void setMemoryUnits(String memoryUnits) {
        this.memoryUnits = memoryUnits;
    }

    /**
     * Holds value of property metaClass.
     */
    private String metaClass;

    /**
     * Getter for property metaJar.
     * @return Value of property metaJar.
     */
    public String getMetaClass() {
        return this.metaClass;
    }

    /**
     * Setter for property metaJar.
     * @param metaClass New value of property metaJar.
     */
    public void setMetaClass(String metaClass) {
        this.metaClass = metaClass;
    }

    /**
     * Holds value of property minAllowedMemoryInMB.
     */
    private long minAllowedMemoryInMB;

    /**
     * Getter for property minAllowedMemoryInMB.
     * @return Value of property minAllowedMemoryInMB.
     */
    public long getMinAllowedMemoryInMB() {
        return this.minAllowedMemoryInMB;
    }
    
} // end of class MeTALauncher
