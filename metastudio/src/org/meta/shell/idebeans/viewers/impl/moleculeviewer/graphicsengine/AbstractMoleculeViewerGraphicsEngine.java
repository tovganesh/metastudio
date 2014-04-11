/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import java.awt.Color;
import org.meta.shell.ide.MeTA;

/**
 * Abstract implementation of MoleculeViewerGraphicsEngine
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractMoleculeViewerGraphicsEngine 
             implements MoleculeViewerGraphicsEngine {   

    protected boolean showViewerPopup;
    protected ViewerPopupMenu popup;
    protected TransformState transformState;
    
    /**
     * the background color
     */
    protected Color backgroundColor;
    
    /**
     *  Get property of background color
     * 
     * @return the background color
     */
    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set property of background color
     * 
     * @param backgroundColor
     */
    @Override
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * the ide instance
     */
    protected MeTA ideInstance;
    
    /**
     * Set property of ideInstance
     * 
     * @param ideInstance
     */
    @Override
    public void setIDEInstance(MeTA ideInstance) {
        this.ideInstance = ideInstance;
    }

    /**
     * Get property of ideInstance
     * 
     * @return the ide instance
     */
    @Override
    public MeTA getIDEInstance() {
        return this.ideInstance;
    }   
    
    protected MoleculeViewer viewer;
    
    /**
     * Set property of enclosing molecule viewer
     * 
     * @param viewer the molecule viewer instance that encloses this engine
     */
    @Override
    public void setEnclosingMoleculeViewer(MoleculeViewer viewer) {
        this.viewer = viewer;
        
        this.backgroundColor = viewer.getBackground();
        this.ideInstance     = viewer.getIDEInstance();        
        this.showViewerPopup = viewer.isShowViewerPopup();
        this.popup           = viewer.getPopup();
        this.transformState  = viewer.getTransformState();
    }
    
    /**
     * Get property of enclosing molecule viewer
     * 
     * @return the instance of enclosing molecule viewer
     */
    @Override
    public MoleculeViewer getEnclosingMoleculeViewer() {
        return this.viewer;
    }
    
    /**
     * Set the current transform state of this engine
     * 
     * @param tstate the transform state
     */
    @Override
    public void setTransformState(TransformState tstate) {
        this.transformState = tstate;
    }
    
    /**
     * Return the current transform state of this engine
     * 
     * @return the current transform state
     */
    @Override
    public TransformState getTransformState() {
        return this.transformState;
    }

    protected SelectionState selectionState;
    
    /** Getter for property selectionState.
     * @return Value of property selectionState.
     *
     */
    @Override
    public SelectionState getSelectionState() {
        return this.selectionState;
    }

    /** Setter for property selectionState.
     * @param selectionState New value of property selectionState.
     *
     */
    @Override
    public void setSelectionState(SelectionState selectionState) {
        this.selectionState = selectionState;    
    }

    protected MoleculeDisplayModel moleculeDisplayModel;

    /**
     * Get the value of moleculeDisplayModel
     *
     * @return the value of moleculeDisplayModel
     */
    @Override
    public MoleculeDisplayModel getMoleculeDisplayModel() {
        return moleculeDisplayModel;
    }

    /**
     * Set the value of moleculeDisplayModel
     *
     * @param moleculeDisplayModel new value of moleculeDisplayModel
     */
    @Override
    public void setMoleculeDisplayModel(MoleculeDisplayModel moleculeDisplayModel) {
        this.moleculeDisplayModel = moleculeDisplayModel;
    }
}
