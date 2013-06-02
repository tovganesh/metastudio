/*
 * FederationRequestHandler.java
 *
 * Created on February 6, 2006, 9:33 PM
 */

package org.meta.net;

import java.util.ArrayList;

/**
 * Interfaces / protcols that are mandatory for a FederationRequestHandler.
 * NOTE: You should create one instance of this (or its derived class) per
 * FederationRequest. The implementation of <code>handleRequest()</code>
 * method should ideally be threaded with proper synchronization.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class FederationRequestHandler implements Runnable {
    
    /** Creates a new instance of FederationRequestHandler */
    public FederationRequestHandler() {
    }

    /**
     * Holds value of property federationRequest.
     */
    protected FederationRequest federationRequest;

    /**
     * Getter for property federationRequest.
     * @return Value of property federationRequest.
     */
    public FederationRequest getFederationRequest() {
        return this.federationRequest;
    }

    /**
     * Setter for property federationRequest.
     * @param federationRequest New value of property federationRequest.
     */
    public void setFederationRequest(FederationRequest federationRequest) {
        this.federationRequest = federationRequest;
    }

    protected ArrayList<FederationRequestHandlerUINotifier> uiNotifiers;

    /**
     * Add an UI notifier, one who wants to be notified of events being
     * processed by the handler. Note that registering for notification
     * does not always mean that a notification will be sent. It is
     * entirely dependent on a handler implementation if it needs to
     * really send any notifications at all.
     *
     * @param uin the notifier instance
     */
    public void addUINotifier(FederationRequestHandlerUINotifier uin) {
        if (uiNotifiers == null)
            uiNotifiers = new ArrayList<FederationRequestHandlerUINotifier>();

        uiNotifiers.add(uin);
    }

    /**
     * Remove an UI notifier to stop receiving any notifications from this
     * handler.
     *
     * @param uin the notifier instance
     */
    public void removeUINotifier(FederationRequestHandlerUINotifier uin) {
        if (uiNotifiers == null) return;

        uiNotifiers.add(uin);
    }

    /**
     * Helper method for subclasses to send in a request received notification
     * 
     */
    protected void fireRequestReceivedNotification() {
        if (uiNotifiers == null) return;
        
        for(FederationRequestHandlerUINotifier uin : uiNotifiers)
            uin.requestReceived(this);
    }

    /**
     * Helper method for subclasses to send in a request processed notification
     */
    protected void fireRequestProcessedNotification() {
        if (uiNotifiers == null) return;

        for(FederationRequestHandlerUINotifier uin : uiNotifiers)
            uin.requestProcessed(this);
    }
    
    /**
     * Method where the processing of FederationRequest starts, this may 
     * inturn lead to passing the call to other FederationHandlers on the
     * local or remote machine, which may allow "routing" of FederationRequests
     * in future.
     * 
     * @param federationRequest the FederationRequest to be handled
     */
    public abstract void handleRequest(FederationRequest federationRequest);
} // end of class FederationRequestHandler
