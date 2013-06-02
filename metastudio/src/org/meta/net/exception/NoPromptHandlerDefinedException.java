/*
 * NoPromptHandlerDefinedException.java
 *
 * Created on February 8, 2006, 7:58 PM
 */

package org.meta.net.exception;

/**
 * Exception thrown when a null reference is found for the 
 * <code>FederationSecurityShieldPromptHandler</code>.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NoPromptHandlerDefinedException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>NoPromptHandlerDefinedException</code> 
     * without detail message.
     */
    public NoPromptHandlerDefinedException() {
    }
    
    
    /**
     * Constructs an instance of <code>NoPromptHandlerDefinedException</code> 
     * with the specified detail message.
     * @param msg the detail message.
     */
    public NoPromptHandlerDefinedException(String msg) {
        super(msg);
    }
} // end of class NoPromptHandlerDefinedException
