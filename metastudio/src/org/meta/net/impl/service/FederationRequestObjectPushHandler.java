/**
 * FederationRequestObjectPushHandler.java
 *
 * Created on 27/08/2009
 */

package org.meta.net.impl.service;

import java.io.ObjectInputStream;
import java.io.Serializable;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * An "object push" handler.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestObjectPushHandler extends FederationRequestHandler {

    /** Creates a new instance of FederationRequestObjectPushHandler */
    public FederationRequestObjectPushHandler() {
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
        handlerThread.setName("Object push handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                  federationRequest.getFederationConnection().getInputStream());
            pushedObject = (Serializable) ois.readObject();
            ois.close();
            federationRequest.closeIt();

            // let those intersted know that we have received some thing
            fireRequestProcessedNotification();
        } catch (Exception e) {
            System.err.println("Service handling failed: " + e);
            e.printStackTrace();
        } // end of try .. catch block
    }

    private Serializable pushedObject;

    /**
     * Get the value of pushedObject
     *
     * @return the value of pushedObject
     */
    public Serializable getPushedObject() {
        return pushedObject;
    }   
}
