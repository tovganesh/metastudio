/*
 * MoleculeSceneChangeListener.java
 *
 * Created on February 2, 2004, 6:29 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

/**
 * The listeners of MoleculeSceneChangeEvent
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MoleculeSceneChangeListener extends java.util.EventListener {
    
    /**
     * method invoked when a MoleculeSceneChangeEvent ocurs
     *
     * @param msce - the event obeject
     */
    public void sceneChanged(MoleculeSceneChangeEvent msce);
    
} // end of interface MoleculeSceneChangeListener
