/*
 * Configuration.java
 *
 * Created on June 1, 2003, 10:39 AM
 */

package org.meta.config;

import java.beans.PropertyVetoException;

/**
 * The main super interface defining the functionality of system wide /
 * workspace wide configuration objects.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Configuration {
    
    /** Getter for property storedAsFile.
     * @return Value of property storedAsFile.
     */
    public boolean isStoredAsFile();
    
    /** Setter for property storedAsFile.
     * @param storedAsFile New value of property storedAsFile.
     */
    public void setStoredAsFile(boolean storedAsFile);
    
    /** Getter for property configFile.
     * @return Value of property configFile.
     */
    public String getConfigFile();
    
    /** Setter for property configFile.
     * @param configFile New value of property configFile.
     *
     * @throws PropertyVetoException
     */
    public void setConfigFile(String configFile) throws PropertyVetoException;
    
    /** Indexed setter for property parameter.
     * @param key of the property., and the new Parameter value
     * @param parameter New value of the property at 'key'.
     */
    public void setParameter(String key, Parameter parameter)
                                           throws PropertyVetoException;
    
    /** Indexed getter for property parameter.
     * @param key of the property.
     * @return Value of the property defined by key.     
     */
    public Parameter getParameter(String key);
    
} // end of interface Configuration
