/*
 * MoleculeEditorCommandHelper.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import java.awt.Color;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;

/**
 * Small user-helpers while executing a MoleculeEditor command.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class MoleculeEditorCommandHelper {

    /** Constructor for MoleculeEditorCommandHelper */
    public MoleculeEditorCommandHelper() {        
    }
    
    protected MoleculeScene moleculeScene;

    /**
     * Get the value of moleculeScene
     *
     * @return the value of moleculeScene
     */
    public MoleculeScene getMoleculeScene() {
        return moleculeScene;
    }

    /**
     * Set the value of moleculeScene
     *
     * @param moleculeScene new value of moleculeScene
     */
    public void setMoleculeScene(MoleculeScene moleculeScene) {
        this.moleculeScene = moleculeScene;
    }

    protected int referenceX;

    /**
     * Get the value of referenceX
     *
     * @return the value of referenceX
     */
    public int getReferenceX() {
        return referenceX;
    }

    /**
     * Set the value of referenceX
     *
     * @param referenceX new value of referenceX
     */
    public void setReferenceX(int referenceX) {
        this.referenceX = referenceX;
    }

    protected int referenceY;

    /**
     * Get the value of referenceY
     *
     * @return the value of referenceY
     */
    public int getReferenceY() {
        return referenceY;
    }

    /**
     * Set the value of referenceY
     *
     * @param referenceY new value of referenceY
     */
    public void setReferenceY(int referenceY) {
        this.referenceY = referenceY;
    }
    
    /**
     * Update the helper based on the current state of MoleculeScene 
     * and X and Y reference points.
     */
    public abstract void update();
 
    /**
     * Update the helper based on the values of MoleculeScene 
     * and X and Y reference points. This method merely makes
     * values of MoleculeScene and X and Y reference points current and calls
     * <code>update()</code>.
     * 
     * @param scene the MoleculeScene object under consideration.
     * @param referenceX current reference X point
     * @param referenceY current reference Y point
     */
    public void update(MoleculeScene scene, int referenceX, int referenceY) {
        this.referenceX    = referenceX;
        this.referenceY    = referenceY;
        this.moleculeScene = scene;
               
        update();
    }
    
    protected Color helperColor;

    /**
     * Get the value of helperColor
     *
     * @return the value of helperColor
     */
    public Color getHelperColor() {
        return helperColor;
    }

    /**
     * Set the value of helperColor
     *
     * @param helperColor new value of helperColor
     */
    public void setHelperColor(Color helperColor) {
        this.helperColor = helperColor;
    }    
    
    /**
     * Clear this helper!
     */
    public abstract void clear();
}
