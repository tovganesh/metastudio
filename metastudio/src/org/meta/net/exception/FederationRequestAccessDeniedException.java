/*
 * FederationRequestAccessDeniedException.java
 *
 * Created on February 8, 2006, 7:57 PM
 */

package org.meta.net.exception;

/**
 * Exception thrown when a FederationSecurityRule violation occurs.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestAccessDeniedException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>FederationRequestAccessDeniedException
     * </code> without detail message.
     */
    public FederationRequestAccessDeniedException() {
    }
    
    
    /**
     * Constructs an instance of <code>FederationRequestAccessDeniedException
     * </code> with the specified detail message.
     * @param msg the detail message.
     */
    public FederationRequestAccessDeniedException(String msg) {
        super(msg);
    }
} // end of class FederationRequestAccessDeniedException
