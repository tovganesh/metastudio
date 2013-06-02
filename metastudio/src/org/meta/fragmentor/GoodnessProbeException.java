/*
 * GoodnessProbeException.java
 *
 * Created on August 29, 2004, 7:50 PM
 */

package org.meta.fragmentor;

/**
 * An exception thrown during a goodness probe run on the fragments.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GoodnessProbeException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>GoodnessProbeException</code> without 
     * detail message.
     */
    public GoodnessProbeException() {
    }
    
    
    /**
     * Constructs an instance of <code>GoodnessProbeException</code> with the 
     * specified detail message.
     * @param msg the detail message.
     */
    public GoodnessProbeException(String msg) {
        super(msg);
    }
} // end of class GoodnessProbeException
