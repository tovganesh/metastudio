/*
 * SCFEventListener.java
 *
 * Created on August 9, 2004, 10:15 PM
 */

package org.meta.math.qm.event;

/**
 * Represents the interface for SCFEvent listeners
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface SCFEventListener extends java.util.EventListener {
    
    /**
     * The method called when an event occurs in an SCF procedure
     *
     * @param scfEvent - the instance of SCFEvent describing the event
     */
    public void scfEventOccured(SCFEvent scfEvent);
    
} // end of interface SCFEventListener
