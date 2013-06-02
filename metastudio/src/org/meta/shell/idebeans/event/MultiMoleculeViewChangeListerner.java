/*
 * MultiMoleculeViewChangeListerner.java
 *
 * Created on August 9, 2007, 6:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.event;

/**
 * The event interface for those who want to get a notification on a view change
 * request originating from MultiMoleculeViewPanel class object.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MultiMoleculeViewChangeListerner 
                 extends java.util.EventListener {

    /**
     * The interface for call back on a view change request
     *
     * @param mmvce the object of the change event
     */
    public void moleculeViewChanged(MultiMoleculeViewChangeEvent mmvce);
} // end of interface MultiMoleculeViewChangeListerner
