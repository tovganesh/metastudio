/*
 * FederationRequestShutdownHandler.java
 *
 * Created on February 25, 2006, 10:14 PM
 *
 */

package org.meta.net.impl.service;

import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * A simple shutdown handler for this service.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestShutdownHandler extends FederationRequestHandler {
    
    /** Creates a new instance of FederationRequestShutdownHandler */
    public FederationRequestShutdownHandler() {
    }

    /**
     * Method where the processing of FederationRequest starts, this may 
     * inturn lead to passing the call to other FederationHandlers on the
     * local or remote machine, which may allow "routing" of FederationRequests
     * in future.
     * 
     * @param federationRequest the FederationRequest to be handled
     */
    @Override
    public void handleRequest(FederationRequest federationRequest) {
        this.federationRequest = federationRequest;
        
        Thread handlerThread = new Thread(this);       
        handlerThread.setName("Shutdown handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        // TODO:
        System.out.println("This is right now a DUMMY!");
    }
    
} // end of class FederationRequestShutdownHandler
