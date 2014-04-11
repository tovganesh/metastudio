/**
 * FederationRequestObjectSyncHandler.java
 *
 * Created on 03/11/2009
 */

package org.meta.net.impl.service;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;
import org.meta.net.impl.event.ObjectArrivalEvent;
import org.meta.net.impl.event.ObjectArrivalListener;
import org.meta.net.impl.event.ObjectChangeEvent;
import org.meta.net.impl.event.ObjectChangeListener;

/**
 * An "object sync" handler
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestObjectSyncHandler extends FederationRequestHandler {

    /** Creates a new instance of FederationRequestObjectSyncHandler */
    public FederationRequestObjectSyncHandler() {
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
        handlerThread.setName("Object sync handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        // TODO: 
        try {
            // step 1: receive the object
            ObjectInputStream ois = new ObjectInputStream(
                  federationRequest.getFederationConnection().getInputStream());
            pushedObject = (Serializable) ois.readObject();
            ois.close();
            federationRequest.closeIt();

            // let those intersted know that we have received some thing
            ObjectArrivalEvent oae = new ObjectArrivalEvent(this);
            oae.setObject(pushedObject);

            fireObjectArrivalEvent(oae);

            // step 2: keep "syncing" till the session is over
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

    protected ArrayList<ObjectArrivalListener> objectArrivalListeners;
    protected ArrayList<ObjectChangeListener> objectChangeListeners;

    /**
     * Add a listner to be notified when an object has arrived for "sync"
     *
     * @param ocl the object listener
     */
    public void addObjectArrivalListener(ObjectArrivalListener ocl) {
        if (objectArrivalListeners == null)
            objectArrivalListeners = new ArrayList<ObjectArrivalListener>();

        objectArrivalListeners.add(ocl);
    }

    /**
     * Remove a object arrival listener
     *
     * @param ocl the object listener
     */
    public void removeObjectArrivalListener(ObjectArrivalListener ocl) {
        if (objectArrivalListeners != null)
            objectArrivalListeners.remove(ocl);
    }

    /** fire the "object arrival" event */
    protected void fireObjectArrivalEvent(ObjectArrivalEvent oae) {
        if (objectArrivalListeners == null) return;

        for(ObjectArrivalListener oal : objectArrivalListeners) {
            oal.objectArrived(oae);
        } // end for
    }

    /**
     * Add a listner to be notified when an object has arrived for "sync"
     *
     * @param ocl the object listener
     */
    public void addObjectChangeListener(ObjectChangeListener ocl) {
        if (objectChangeListeners == null)
            objectChangeListeners = new ArrayList<ObjectChangeListener>();

        objectChangeListeners.add(ocl);
    }

    /**
     * Remove a object arrival listener
     *
     * @param ocl the object listener
     */
    public void removeObjectChangeListener(ObjectChangeListener ocl) {
        if (objectChangeListeners != null)
            objectChangeListeners.remove(ocl);
    }

    /** fire the "object change" event */
    protected void fireObjectChangeEvent(ObjectChangeEvent oae) {
        if (objectChangeListeners == null) return;

        for(ObjectChangeListener oal : objectChangeListeners) {
            oal.objectChanged(oae);
        } // end for
    }
}
