/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import java.awt.Color;
import java.awt.image.RenderedImage;
import java.util.Vector;
import javax.swing.JPanel;
import org.meta.shell.ide.MeTA;

/**
 * Interface for MoleculeViewerGraphicsEngine that delegates the graphics 
 * engine functionality to actual implementing classes.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MoleculeViewerGraphicsEngine {
    
    /**
     * Create the graphics of the list of basic MoleculeScene objects
     * 
     * @param msl the molecule scene list
     * @return the created graphics in a JPanel
     */
    public JPanel createEngineGraphics(Vector<MoleculeScene> msl);
    
    /**
     * update the engine graphics
     */
    public void updateEngineGraphics();
    
    /**
     * Get property of background color
     * 
     * @return the background color
     */
    public Color getBackgroundColor();
    
    /**
     * Set property of background color
     * 
     * @param backgroundColor
     */
    public void setBackgroundColor(Color backgroundColor);
    
    /**
     * Set property of ideInstance
     * 
     * @param ideInstance
     */
    public void setIDEInstance(MeTA ideInstance);
    
    /**
     * Get property of ideInstance
     * 
     * @return the ide instance
     */
    public MeTA getIDEInstance();
    
    /**
     * Set property of enclosing molecule viewer
     * 
     * @param viewer the molecule viewer instance that encloses this engine
     */
    public void setEnclosingMoleculeViewer(MoleculeViewer viewer);
    
    /**
     * Get property of enclosing molecule viewer
     * 
     * @return the instance of enclosing molecule viewer
     */
    public MoleculeViewer getEnclosingMoleculeViewer();
    
    /**
     * Return the current screen image
     * 
     * @return the instance of current screen image 
     */
    public RenderedImage getScreenImage();
    
    /**
     * Set the current transform state of this engine
     * 
     * @param tstate the transform state
     */
    public void setTransformState(TransformState tstate);
    
    /**
     * Return the current transform state of this engine
     * 
     * @return the current transform state
     */
    public TransformState getTransformState();

    /** Getter for property selectionState.
     * @return Value of property selectionState.
     *
     */
    public SelectionState getSelectionState();

    /** Setter for property selectionState.
     * @param selectionState New value of property selectionState.
     *
     */
    public void setSelectionState(SelectionState selectionState);

    /**
     * Set the display model for all the molecules rendered via this engine
     *
     * @param model the required MoleculeDisplayModel
     */
    public void setMoleculeDisplayModel(MoleculeDisplayModel model);

    /**
     * Get the current display model
     *
     * @return the current MoleculeDisplayModel
     */
    public MoleculeDisplayModel getMoleculeDisplayModel();
}
