/*
 * DefaultConfiguration.java
 *
 * Created on September 3, 2003, 10:34 PM
 */

package org.meta.config.impl;

import java.util.*;

import java.beans.PropertyVetoException;
import org.meta.common.resource.StringResource;
import org.meta.config.GlobalConfiguration;
import org.meta.config.Parameter;

/**
 * Default IDE configuration.
 * Defines various default implementation class of the main interfaces that
 * make up the MeTA Studio v2.0.
 *
 * Follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DefaultConfiguration implements GlobalConfiguration {
    
    private static DefaultConfiguration _defaultConfiguration;
    
    private Hashtable<String, Parameter> configuration;
    
    /** Creates a new instance of DefaultConfiguration */
    private DefaultConfiguration() {
        configuration = new Hashtable<String, Parameter>();
        
        // the initial parameters
        try {
            setDefaultParams();
        } catch (PropertyVetoException ignored) {
            // because it never should happen in this context
            System.err.println(ignored.toString());
        } 
    }
    
    /**
     * method to return instance of this object.
     *
     * @return DefaultConfiguration a single global instance of this class
     */
    public static DefaultConfiguration getInstance() {
        if (_defaultConfiguration == null) {
            _defaultConfiguration = new DefaultConfiguration();            
        } // end if
        
        return _defaultConfiguration;
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() throws PropertyVetoException {
        StringResource strings   = StringResource.getInstance();
        ResourceBundle resources = ResourceBundle.getBundle(
                                              strings.getDefaultImplResource());
        
        // first, we map the important interface implementation classes
        // that define the concrete MeTA Studio implementation!        
        Enumeration implKeys = resources.getKeys();
        String theKey;
        
        while(implKeys.hasMoreElements()) {
            theKey = (String) implKeys.nextElement();
            setParameter(theKey, 
                         new ClassImplParameter(resources.getString(theKey)));
        } // end while                      
    }
    
    /**
     * Method returns the value of parameter pertaining to the key.
     * 
     * @return Parameter - the parameter value.
     * @throws NullPointerException if the key is not found
     */
    @Override
    public Parameter getParameter(String key) {
        return ((Parameter) configuration.get(key));
    }
    
    /**
     * method to set the new parameter value.
     *
     * @param key - the key whose value needs to be changed or added
     * @param parameter - the new parameter value
     * @throws PropertyVetoException - incase changing property value is 
     *         not supported
     */
    @Override
    public void setParameter(String key, Parameter parameter) 
                                                 throws PropertyVetoException {
        configuration.put(key, parameter);
    }
    
    /**
     * is the configuration stored as file?
     * 
     * @return boolean - true / false
     */
    @Override
    public boolean isStoredAsFile() {
        return true; 
    }
    
    /**
     * Method returns the absolute path of the configuration file if 
     * isStoredAsFile() call results in a true value, else a null is returned.
     * 
     * @return String - the absolute path of the configuration file
     */
    @Override
    public String getConfigFile() {
        return StringResource.getInstance().getDefaultImplResource();
    }
    
    /**
     * changes current value of file storage
     *
     * @param storedAsFile new value of this storage
     */
    @Override
    public void setStoredAsFile(boolean storedAsFile) {
        // igonored! ... we don't want to listen here
    } 
    
    /**
     * sets the value of a parameter
     * 
     * @param configFile - the new name of the config file
     * @throws PropertyVetoException some problem can't change the stuff!
     */
    @Override
    public void setConfigFile(String configFile) throws PropertyVetoException {
        throw new PropertyVetoException("Cannot change this property!", null);
    }
                   
} // end of class DefaultConfiguration
