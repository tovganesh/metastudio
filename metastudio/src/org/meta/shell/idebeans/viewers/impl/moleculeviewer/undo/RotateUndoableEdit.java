/*
 * RotateUndoableEdit.java
 *
 * Created on March 27, 2004, 10:18 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;

import org.meta.math.Matrix3D;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;

/**
 * Class to keep track of rotate edits, undo and redo.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RotateUndoableEdit extends AbstractUndoableEdit {
    
    private MoleculeViewer moleculeViewer;
    
    /** Holds value of property initialMatrix. */
    private Matrix3D initialMatrix;
    
    /** Holds value of property finalMatrix. */
    private Matrix3D finalMatrix;
    
    /** Creates a new instance of RotateUndoableEdit */
    public RotateUndoableEdit(MoleculeViewer mv) {
        this.moleculeViewer = mv;
    }    
    
    /**
     * the redo method
     */
    public void redo() {
        super.redo();
        
        moleculeViewer.setRotationMatrix(finalMatrix);
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();        
        
        moleculeViewer.setRotationMatrix(initialMatrix);
    }
    
    /** Getter for property initialMatrix.
     * @return Value of property initialMatrix.
     *
     */
    public Matrix3D getInitialMatrix() {
        return this.initialMatrix;
    }
    
    /** Setter for property initialMatrix.
     * @param initialMatrix New value of property initialMatrix.
     *
     */
    public void setInitialMatrix(Matrix3D initialMatrix) {
        this.initialMatrix = (Matrix3D) initialMatrix.clone();
    }
    
    /** Getter for property finalMatrix.
     * @return Value of property finalMatrix.
     *
     */
    public Matrix3D getFinalMatrix() {
        return this.finalMatrix;
    }
    
    /** Setter for property finalMatrix.
     * @param finalMatrix New value of property finalMatrix.
     *
     */
    public void setFinalMatrix(Matrix3D finalMatrix) {
        this.finalMatrix = (Matrix3D) finalMatrix.clone();
    }
    
} // end of class RotateUndoableEdit
