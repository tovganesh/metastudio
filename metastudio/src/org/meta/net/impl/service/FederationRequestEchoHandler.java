/*
 * FederationRequestEchoHandler.java
 *
 * Created on March 19, 2006, 2:45 PM
 *
 */

package org.meta.net.impl.service;

import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * Simple "echo" service handler.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestEchoHandler extends FederationRequestHandler {
    
    /** Creates a new instance of FederationRequestEchoHandler */
    public FederationRequestEchoHandler() {
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
        handlerThread.setName("Echo handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        try {
            federationRequest.getWriter().write(
                      federationRequest.getReader().readLine() + '\n');
            federationRequest.getWriter().flush();
            
            federationRequest.closeIt();
        } catch (Exception e) {
            System.err.println("Service handling failed: " + e);
            e.printStackTrace();            
        } // end of try .. catch block
    }    
} // end of class FederationRequestEchoHandler
