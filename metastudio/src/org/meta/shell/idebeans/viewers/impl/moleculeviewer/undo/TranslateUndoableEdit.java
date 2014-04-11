/*
 * TranslateUndoableEdit.java
 *
 * Created on March 25, 2004, 6:48 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;

import org.meta.math.Matrix3D;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;

/**
 * Class to keep track of translate edits, undo and redo.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TranslateUndoableEdit extends AbstractUndoableEdit {
    
    private MoleculeViewer moleculeViewer;
    
    /** Holds value of property initialMatrix. */
    private Matrix3D initialMatrix;
    
    /** Holds value of property finalMatrix. */
    private Matrix3D finalMatrix;
    
    /** Creates a new instance of TranslateUndoableEdit */
    public TranslateUndoableEdit(MoleculeViewer mv) {
        this.moleculeViewer = mv;
    }    
    
    /**
     * the redo method
     */
    public void redo() {
        super.redo();
        
        moleculeViewer.setTranslateMatrix(finalMatrix);
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();        
        
        moleculeViewer.setTranslateMatrix(initialMatrix);
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
    
} // end of class TranslateUndoableEdit
