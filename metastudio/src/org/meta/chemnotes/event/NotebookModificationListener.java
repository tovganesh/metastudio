/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes.event;

/**
 * Modification listener interface for ChemNotebook
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface NotebookModificationListener extends java.util.EventListener {
    /**
     * Callback when a notebook is modified
     * 
     * @param nme the notebook modification event
     */
    public void notebookModified(NotebookModificationEvent nme);
}
