/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

/**
 * Defines the interface for describing an additional info panel
 * attached to a MoleculeViewer object.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MoleculeViewerAdditionalInfoPanel {

    /**
     * Show this panel attached to a MoleculeViewer object
     * 
     * @param mv MoleculeViewer to which this viewer must be attached
     */
    public void showIt(MoleculeViewer mv);

    /**
     * Hide this panel
     */
    public void hideIt();
}
