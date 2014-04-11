/*
 * FederationServiceConsumptionFailed.java
 *
 * Created on March 19, 2006, 12:34 PM
 */

package org.meta.net.exception;

/**
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceConsumptionFailed extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>FederationServiceConsumptionFailed</code>
     * without detail message.
     */
    public FederationServiceConsumptionFailed() {
    }
    
    
    /**
     * Constructs an instance of <code>FederationServiceConsumptionFailed</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public FederationServiceConsumptionFailed(String msg) {
        super(msg);
    }
} // end of class FederationServiceConsumptionFailed
