/*
 * RDFSubscriptionConfiguration.java
 *
 * Created on December 5, 2004, 4:13 PM
 */

package org.meta.config.impl;

import java.io.*;
import java.net.*;
import java.util.*;

import java.beans.PropertyVetoException;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.config.Configuration;
import org.meta.config.Parameter;
import org.w3c.dom.*;

/**
 * Configuration of RDF URLs to which MeTA Studio client is subscribed.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RDFSubscriptionConfiguration implements Configuration {
    
    private static RDFSubscriptionConfiguration _rdfSubscriptionConfiguration;
    
    /** for xml parsing ... */
    private String element = new String("element");
    
    /** the name - URL hash list */
    private HashMap<String, String> urlList;
    
    /**
     * Holds value of property refreshRate.
     */
    private int refreshRate;
    
    /** Creates a new instance of RdfListConfiguration */
    public RDFSubscriptionConfiguration() {
        urlList = new HashMap<String, String>(5);
        
        refreshRate = 30; // 30 minutes
        
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
    public static RDFSubscriptionConfiguration getInstance() {
        if (_rdfSubscriptionConfiguration == null) {
            _rdfSubscriptionConfiguration = new RDFSubscriptionConfiguration();
        } // end if 
        
        return _rdfSubscriptionConfiguration;
    }
    
     /**
     * private method to set the default parameters
     */
    private void setDefaultParams() throws PropertyVetoException {
        StringResource strings   = StringResource.getInstance();
        try {
            // first check to see if we have a previously created recent 
            // file configuration
            if (!(new File(strings.getRdfList())).exists()) {
                createIt();
                return;
            } // end if
            
            // if the file already exsists...
            // read the XML config file
            Document configDoc = Utility.parseXML(strings.getRdfList());
            
            // and save the info. properly
            saveIt(configDoc);
        } catch (Exception e) {
            throw new PropertyVetoException("Exception in "
              + "RdfListConfiguration.setParameter()" + e.toString(), null);
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
        
        FileOutputStream fos = new FileOutputStream(strings.getRdfList());
        
        fos.write(strings.getXmlHeader().getBytes());
        fos.write("<rdfs refreshrate=\"30\"> \n".getBytes());
        fos.write("</rdfs> \n".getBytes());
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
             
            if (element.equals("rdfs")) {
               refreshRate = Integer.parseInt(
                               atts.getNamedItem("refreshrate").getNodeValue());
            } else if (element.equals("rdfentry")) {
               urlList.put(atts.getNamedItem("name").getNodeValue(),
                           atts.getNamedItem("url").getNodeValue());
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
     * Indexed getter for property parameter.
     * @param key of the property. the key here is a integer like "1", "2" etc..
     * @return Value of the property defined by key.
     */
    public Parameter getParameter(String key) {    
        try {
            return new URLImplParameter(new URL(urlList.get(key)));
        } catch (MalformedURLException mue) {
            System.err.println("Exception un RDFSubscriptionConfiguration" +
                               ".getParameter() : " + mue.toString());
            mue.printStackTrace();
            
            return null;
        } // end try .. catch block
    } 
    
    /**
     * Indexed setter for property parameter.
     * @param key of the property., and the new Parameter value
     *        the key here is a relevent name for the URL like "chem grid" etc.
     * @param parameter New value of the property at 'key'.
     */
    public void setParameter(String key, Parameter parameter) {    
        if (parameter == null) {
            urlList.remove(key);
        } else {
            // save in memory
            urlList.put(key, 
            ((URL) ((URLImplParameter) parameter).getValue()).toExternalForm());
        } // end if
        
        updateConfigFile();
    }    
    
    /**
     * update the config file
     */
    private void updateConfigFile() {
        // and then save into the file
        StringResource strings = StringResource.getInstance();
        try {
          FileOutputStream fos = new FileOutputStream(strings.getRdfList());
        
          fos.write(strings.getXmlHeader().getBytes());
          fos.write(("<rdfs refreshrate=\"" 
                     + refreshRate + "\" > \n").getBytes());
          
          Iterator keys = urlList.keySet().iterator();
          String theKey;
          while(keys.hasNext()) {
              theKey = (String) keys.next();
              
              fos.write(("\t<rdfentry name=\"" 
                         + theKey + "\" url=\"" 
                         + urlList.get(theKey) + "\" /> \n").getBytes());
          } // end while
        
          fos.write("</rdfs> \n".getBytes());
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
    public String getConfigFile() {
        return StringResource.getInstance().getRdfList();
    }
    
    /**
     * Getter for property storedAsFile.
     * @return Value of property storedAsFile.
     */
    public boolean isStoredAsFile() {
        return true;
    }
    
    /**
     * Setter for property storedAsFile.
     * @param storedAsFile New value of property storedAsFile.
     */
    public void setStoredAsFile(boolean storedAsFile) {
        // skip skip... do nothing
    }
    
    /**
     * Setter for property configFile.
     * @param configFile New value of property configFile.
     *
     * @throws PropertyVetoException
     */
    public void setConfigFile(String configFile) {
        // skip skip... do nothing
    }
    
    /**
     * Getter for property urlList.
     * @return Value of property urlList.
     */
    public HashMap<String, String> getUrlList() {
        return this.urlList;
    }
    
    /**
     * Getter for property refreshRate.
     * @return Value of property refreshRate.
     */
    public int getRefreshRate() {
        return this.refreshRate;
    }
    
    /**
     * Setter for property refreshRate.
     * @param refreshRate New value of property refreshRate.
     */
    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
        
        updateConfigFile();
    }
    
} // end of class RDFSubscriptionConfiguration

