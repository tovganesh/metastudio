/*
 * FederationServiceDiscoveryFailed.java
 *
 * Created on March 19, 2006, 11:20 AM
 */

package org.meta.net.exception;

/**
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceDiscoveryFailed extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>FederationServiceDiscoveryFailed</code> 
     * without detail message.
     */
    public FederationServiceDiscoveryFailed() {
    }
    
    
    /**
     * Constructs an instance of <code>FederationServiceDiscoveryFailed</code> 
     * with the specified detail message.
     * @param msg the detail message.
     */
    public FederationServiceDiscoveryFailed(String msg) {
        super(msg);
    }
} // end of class FederationServiceDiscoveryFailed
