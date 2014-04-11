/*
 * FragmentationSchemeFactoryImpl.java
 *
 * Created on January 16, 2005, 9:38 PM
 */

package org.meta.fragmentor.impl;

import java.util.*;
import org.meta.common.resource.StringResource;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.fragmentor.FragmentationSchemeFactory;

/**
 * The factory method implementor for FragmentationSchemeFactory interface.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentationSchemeFactoryImpl 
             implements FragmentationSchemeFactory {
    
    private ResourceBundle resources;   
    private HashMap<String, String> schemes;
        
    /** Creates a new instance of FragmentationSchemeFactoryImpl */
    public FragmentationSchemeFactoryImpl() {        
        schemes = new HashMap<String, String>(5);        
        
        // the initial parameters
        setDefaultParams();                
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() {        
        StringResource strings   = StringResource.getInstance();
        resources = 
            ResourceBundle.getBundle(strings.getFragmentationSchemeResource());
        
        Enumeration<String> implKeys = resources.getKeys();        
        String theKey = "";
        String theScheme = "";
        
        try {
            while(implKeys.hasMoreElements()) {
                theKey = implKeys.nextElement();
                theScheme = resources.getString(theKey);
                
                schemes.put(theKey, theScheme);
            } // end while
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + theKey + "."
                      + " Exception is : " + e.toString());
        } // end try .. catch block
    }
    
    /**
     * Getter for property defaultScheme.
     * @return Value of property defaultScheme.
     */
    public FragmentationScheme getDefaultScheme() {   
        try {
            return ((FragmentationScheme)
                  Class.forName(schemes.get("defaultScheme")).newInstance());
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : defaultScheme."
                      + " Exception is : " + e.toString());
        } // end of try .. catch block
    }
    
    /**
     * Get a FragmentationScheme by its name.
     *
     * @param name name of the desired fragmentation scheme
     * @return instance of FragmentationScheme.
     */
    public FragmentationScheme getScheme(String name) {       
        try {
            return ((FragmentationScheme)
                  Class.forName(schemes.get(name)).newInstance());
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + name + "."
                      + " Exception is : " + e.toString());
        } // end of try .. catch block
    }
    
    /**
     * Getter for property allSchemes.
     * @return Value of property allSchemes.
     */
    public Iterator<String> getAllSchemes() {        
        return schemes.keySet().iterator();
    }   
} // end of class FragmentationSchemeFactoryImpl
