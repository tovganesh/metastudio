/**
 * ObjectArrivalListener.java
 *
 * Created on 03/11/2009
 */

package org.meta.net.impl.event;

/**
 * Notification of object arrival by "object sync" handler
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface ObjectArrivalListener extends java.util.EventListener {

    /**
     * Called when an object has arrived from remote client and is requesting
     * this to be "synced"
     * 
     * @param oae
     */
    public void objectArrived(ObjectArrivalEvent oae);
}
