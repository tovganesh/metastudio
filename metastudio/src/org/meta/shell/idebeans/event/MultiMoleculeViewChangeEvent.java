/*
 * MultiMoleculeViewChangeEvent.java
 *
 * Created on August 9, 2007, 6:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.event;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;

/**
 * The event fired when a view change is requested from MultiMoleculeViewPanel.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MultiMoleculeViewChangeEvent extends java.util.EventObject {
    
    /** Creates a new instance of MultiMoleculeViewChangeEvent */
    public MultiMoleculeViewChangeEvent(Object source) {
        super(source);
    }

    /**
     * Holds value of property moleculeViewer.
     */
    private MoleculeViewer moleculeViewer;

    /**
     * Getter for property moleculeViewer.
     * @return Value of property moleculeViewer.
     */
    public MoleculeViewer getMoleculeViewer() {
        return this.moleculeViewer;
    }

    /**
     * Setter for property moleculeViewer.
     * @param moleculeViewer New value of property moleculeViewer.
     */
    public void setMoleculeViewer(MoleculeViewer moleculeViewer) {
        this.moleculeViewer = moleculeViewer;
    }
    
} // end of class MultiMoleculeViewChangeEvent
