/*
 * RecentFilesConfiguration.java
 *
 * Created on May 18, 2004, 6:42 AM
 */

package org.meta.config.impl;

import java.io.*;
import java.util.*;

import java.beans.PropertyVetoException;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.config.Configuration;
import org.meta.config.Parameter;
import org.w3c.dom.*;

/**
 * Manages a list of recently opened files. The number of files remembered 
 * depends on the value of <code>remember</code> property, which is by default
 * set to <code>5</code>. <br>
 * Follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RecentFilesConfiguration implements Configuration {
    
    private static RecentFilesConfiguration _recentFilesConfiguration;
    
    /**
     * Holds value of property remember.
     */
    private int remember;
    
    /** for keeping track of recent files */
    private Stack<String> recentFiles;
    
    /** for xml parsing ... */
    private String element = new String("element");
    
    /**
     * default number of files i have to remember
     */
    public static final int DEFAULT_REMEMBER = 5;
    
    /** Creates a new instance of RecentFilesConfiguration */
    private RecentFilesConfiguration() {
        remember = DEFAULT_REMEMBER;
        
        recentFiles = new Stack<String>();
        
        // the initial parameters
        try {
            setDefaultParams();
        } catch (PropertyVetoException ignored) {
            // because it never should happen in this context
            System.err.println(ignored.toString());
        } 
    }
    
    /**
     * Obtain an instance of this ...
     */
    public static RecentFilesConfiguration getInstance() {
        if (_recentFilesConfiguration == null) {
            _recentFilesConfiguration = new RecentFilesConfiguration();
        } // end if
        
        return _recentFilesConfiguration;
    }    
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() throws PropertyVetoException {
        StringResource strings   = StringResource.getInstance();
        try {
            // first check to see if we have a previously created recent 
            // file configuration
            if (!(new File(strings.getRecentFiles())).exists()) {
                createIt();
                return;
            } // end if
            
            // if the file already exists...
            // read the XML config file
            Document configDoc = Utility.parseXML(strings.getRecentFiles());
            
            // and save the info. properly
            saveIt(configDoc);
        } catch (Exception e) {
            throw new PropertyVetoException("Exception in "
              + "RecentFilesConfiguration.setParameter()" + e.toString(), null);
        } // end of try .. catch block  
    }
    
    /**
     * This method creates recent file settings if they are not already there.
     */
    private void createIt() throws IOException {
        StringResource strings = StringResource.getInstance();
        
        File appDir = new File(strings.getAppDir());
        
        if (!appDir.exists()) {
            appDir.mkdir(); // make the directory if not already present
        } // end if
        
        FileOutputStream fos = new FileOutputStream(strings.getRecentFiles());
        
        fos.write(strings.getXmlHeader().getBytes());
        fos.write("<recentfiles> \n".getBytes());
        fos.write("</recentfiles> \n".getBytes());
        fos.close();
    }
    
    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveIt(Node n) {
        int type = n.getNodeType();   // get node type
        
        switch (type) {       
        case Node.ELEMENT_NODE:
            element = n.getNodeName();
            
            NamedNodeMap atts = n.getAttributes();
            if (element.equals("file")) {
               // don't read more that required to remember
               if (recentFiles.size() >= remember) return;
               
               // and well remember the path
               recentFiles.push(atts.getNamedItem("path").getNodeValue());
            } // end if
            
            break;
        default:
            break;
        } // end switch..case

        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveIt(child);
        } // end for
    } // end of method saveIt()
    
    /**
     * get the recently used file list.
     */
    public Iterator<String> getFileList() {
        return recentFiles.iterator();
    }
    
    /**
     * Getter for property remember.
     * @return Value of property remember.
     */
    public int getRemember() {
        return this.remember;
    }
    
    /**
     * Setter for property remember.
     * @param remember New value of property remember.
     */
    public void setRemember(int remember) {
        this.remember = remember;
    }
    
    /**
     * Indexed getter for property parameter.
     * @param key of the property. the key here is a integer like "1", "2" etc..
     * @return Value of the property defined by key.
     */
    @Override
    public Parameter getParameter(String key) {        
        return new FileImplParameter(
                   new File((String) recentFiles.get(Integer.parseInt(key))));
    }    
   
    /**
     * Indexed setter for property parameter.
     * @param key of the property., and the new Parameter value
     *        the key here is a integer like "1", "2" etc.., but is irrelevant
     * @param parameter New value of the property at 'key'.
     */
    @Override
    public void setParameter(String key, Parameter parameter) {                
        // check if that is already in the list
        int indexToRemove = recentFiles.search(
         ((File) ((FileImplParameter) parameter).getValue()).getAbsolutePath());
                
        // .. if so quietly return
        if (indexToRemove >= 0) {
            return;
        } // end if
        
        // remove the one on the top if we are over flow
        if (recentFiles.size() == remember) recentFiles.remove(0);
        
        // save in memory
        recentFiles.push(
         ((File) ((FileImplParameter) parameter).getValue()).getAbsolutePath());
        
        // and then save into the file
        StringResource strings = StringResource.getInstance();
        try {
          FileOutputStream fos = new FileOutputStream(strings.getRecentFiles());
        
          fos.write(strings.getXmlHeader().getBytes());
          fos.write("<recentfiles> \n".getBytes());
        
          for(int i=0; i<recentFiles.size(); i++) {
              fos.write(("\t<file path=\"" 
                      + recentFiles.get(i) + "\"/> \n").getBytes());
          } // end for
        
          fos.write("</recentfiles> \n".getBytes());
          fos.close();
        } catch (IOException e) {
          System.out.println("Warning! Unable to save settings file : " 
                             + e.toString());
        } // end of try ... catch block
    }    
    
    /**
     * Getter for property configFile.
     * @return Value of property configFile.
     */
    @Override
    public String getConfigFile() {
        return StringResource.getInstance().getRecentFiles();
    }
    
    /**
     * Getter for property storedAsFile.
     * @return Value of property storedAsFile.
     */
    @Override
    public boolean isStoredAsFile() {
        return true;
    }
    
    /**
     * Setter for property storedAsFile.
     * @param storedAsFile New value of property storedAsFile.
     */
    @Override
    public void setStoredAsFile(boolean storedAsFile) {
        // skip skip... do nothing
    }
    
    /**
     * Setter for property configFile.
     * @param configFile New value of property configFile.
     *
     * @throws PropertyVetoException
     */
    @Override
    public void setConfigFile(String configFile) {
        // skip skip... do nothing
    }
    
} // end of class RecentFilesConfiguration
