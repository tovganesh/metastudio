/*
 * FederationDiscoveryListener.java
 *
 * Created on July 11, 2006, 5:05 PM  
 */

package org.meta.net.event;

/**
 * The listener interface for federation service discovery events.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FederationDiscoveryListener extends java.util.EventListener {
    /**
     * A federation service discovery event has occured!
     *
     * @param fde - the event discription
     */
    public void federated(FederationDiscoveryEvent fde);
} // end of interface FederationDiscoveryListener
