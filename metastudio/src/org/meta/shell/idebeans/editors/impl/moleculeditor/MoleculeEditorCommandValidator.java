/*
 * MoleculeEditorCommandValidator.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;

/**
 *
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class MoleculeEditorCommandValidator {

    /** Constructor for MoleculeEditorCommandValidator */
    public MoleculeEditorCommandValidator() {
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
     * Validate if this command can be executed depending on the current
     * state of MoleculeScene and X and Y reference points.
     * 
     * @return true if successful, false other wise
     */    
    public abstract boolean validate();
 
    /**
     * Validate if this command can be executed depending on the values of
     * of MoleculeScene and X and Y reference points. This method merely makes
     * values of MoleculeScene and X and Y reference points current and calls
     * <code>validate()</code>.
     * 
     * @param scene the MoleculeScene object under consideration.
     * @param referenceX current reference X point
     * @param referenceY current reference Y point
     * @return true if successful, false other wise
     */
    public boolean validate(MoleculeScene scene, int referenceX, int referenceY) {
        this.referenceX    = referenceX;
        this.referenceY    = referenceY;
        this.moleculeScene = scene;
               
        return validate();
    }
}
