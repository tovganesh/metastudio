/*
 * FederationRequestBroadcastHandler.java
 *
 * Created on July 16, 2006, 8:28 PM
 */

package org.meta.net.impl.service;


import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * Simple "broadcast" service handler
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestBroadcastHandler extends FederationRequestHandler{
        
    /** Creates a new instance of FederationRequestBroadcastHandler */
    public FederationRequestBroadcastHandler() {
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
        handlerThread.setName("Broadcast handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        try {
            fireRequestReceivedNotification();
            
            msgReceived = "From: " + federationRequest.getFederationConnection()
                                             .getInetAddress().getHostAddress()
                          + " :> " + federationRequest.getReader().readLine();
            
            System.out.println("Broadcast message: " + msgReceived);             
            
            federationRequest.closeIt();    
            
            fireRequestProcessedNotification();
        } catch (Exception e) {
            System.err.println("Service handling failed: " + e);
            e.printStackTrace();            
        } // end of try .. catch block
    }    
    
    protected String msgReceived;

    /**
     * Get the value of msgReceived
     *
     * @return the value of msgReceived
     */
    public String getMsgReceived() {
        return msgReceived;
    }

    /**
     * Set the value of msgReceived
     *
     * @param msgReceived new value of msgReceived
     */
    public void setMsgReceived(String msgReceived) {
        this.msgReceived = msgReceived;
    }

} // end of class FederationRequestBroadcastHandler
