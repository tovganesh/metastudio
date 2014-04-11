/*
 * MoleculeBuildListener.java
 *
 * Created on May 11, 2003, 8:12 AM
 */

package org.meta.molecule.event;

/**
 * The interface defines how an HCI(*) should "listen" to what the algorithm
 * of MoleculeBuilder is doing? The HCI then can take steps to update its UI
 * and inform the user accordingly of the progress.
 * Note that it is completely on the implimentors of the MoleculeBuilder class
 * to fire MoleculeBuildEvent when necessary.
 *
 * (*) Human Computer Interface
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MoleculeBuildListener extends java.util.EventListener {
 
    /**
     * The callback method when a MoleculeBuildEvent occurs. "listeners", 
     * usually HCI may take appropriate action to notify the user of activity 
     * under way.
     * @param event the event object describing what had happened.
     */    
    public void buildEvent(MoleculeBuildEvent event);
    
} // end of interface MoleculeBuildListener
