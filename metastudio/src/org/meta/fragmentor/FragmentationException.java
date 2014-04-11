/*
 * FragmentationException.java
 *
 * Created on August 29, 2004, 7:49 PM
 */

package org.meta.fragmentor;

/**
 * An exception thrown during an automated / manual fragmentation process.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentationException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>FragmentationException</code> 
     * without detail message.
     */
    public FragmentationException() {
    }
    
    
    /**
     * Constructs an instance of <code>FragmentationException</code> with 
     * the specified detail message.
     * @param msg the detail message.
     */
    public FragmentationException(String msg) {
        super(msg);
    }
} // end of class FragmentationException
