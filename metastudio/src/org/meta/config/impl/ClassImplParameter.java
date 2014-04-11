/*
 * ClassImplParameter.java
 *
 * Created on September 9, 2003, 8:52 PM
 */

package org.meta.config.impl;

import org.meta.config.Parameter;

/**
 * This class defines the a simple implementation of Parameter interface 
 * for storing the implementation class of an interface for MeTA Studio.
 * The class may be either a concrete Java class or a class generated from
 * an embedded script engine.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ClassImplParameter implements Parameter {
    
    private String implClass;
    
    /** Creates a new instance of ClassImplParameter */
    public ClassImplParameter(String implClass) {
        this.implClass = implClass;
    }
    
    /** Getter for property value.
     * @return Value of property value.
     */
    @Override
    public Object getValue() {
        return implClass;
    }
    
    /** Setter for property value.
     * @param value New value of property value.
     * @throws may throw java.lang.ClassCastException
     */
    @Override
    public void setValue(Object value) {
        this.implClass = (String) value;
    }
    
} // end of class ClassImplParameter
