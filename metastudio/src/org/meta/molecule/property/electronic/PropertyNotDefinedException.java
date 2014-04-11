/*
 * PropertyNotDefinedException.java
 *
 * Created on October 11, 2007, 10:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.molecule.property.electronic;

/**
 * Exception thrown when a Property is not defined (at a requested loaction,
 * or some criterion is not met).
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PropertyNotDefinedException extends Exception {
    
    /** Creates a new instance of PropertyNotDefinedException */
    public PropertyNotDefinedException() {
    }
    
    private String errMsg = "Undefined Property"; 
    
    /** Creates a new instance of PropertyNotDefinedException */
    public PropertyNotDefinedException(String errMsg) {
        this.errMsg = errMsg;
    }
    
    /**
     * overloaded toString() method
     */
    public String toString() {
        return errMsg;
    }
} // end of class PropertyNotDefinedException
