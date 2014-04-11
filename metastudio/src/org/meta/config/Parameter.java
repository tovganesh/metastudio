/*
 * Parameter.java
 *
 * Created on June 8, 2003, 11:39 AM
 */

package org.meta.config;

/**
 * Interface defines the way new parameters can be added to a Configuration 
 * object. <br>
 * Follwoing the interface also means that the acceptance/ rejection of a new
 * property value is done at runtimes only... does not follow the static
 * constrained property definition of Java Beans. Thus each implimentation
 * of Parameter interface has its own ways of handling incompatible property
 * values.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Parameter {
    
    /** Getter for property value.
     * @return Value of property value.
     */
    public Object getValue();
    
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(Object value);
    
} // end of interface Parameter
