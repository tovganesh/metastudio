/*
 * ZoomUndoableEdit.java
 *
 * Created on March 10, 2004, 6:49 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;

/**
 * Class to keep track of zoom endits, undo and redo.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ZoomUndoableEdit extends AbstractUndoableEdit {
    
    /** Holds value of property initialScaleFactor. */
    private double initialScaleFactor;
    
    /** Holds value of property finalScaleFactor. */
    private double finalScaleFactor;
    
    private MoleculeViewer moleculeViewer;
    
    /** Creates a new instance of ZoomUndoableEdit */
    public ZoomUndoableEdit(MoleculeViewer mv) {
        this(mv, 0.0, 0.0);
    }
    
    public ZoomUndoableEdit(MoleculeViewer mv, double isf, double fsf) {
        this.moleculeViewer     = mv;
        this.initialScaleFactor = isf;
        this.finalScaleFactor   = fsf;
    }
    
    /** Getter for property initialScaleFactor.
     * @return Value of property initialScaleFactor.
     *
     */
    public double getInitialScaleFactor() {
        return this.initialScaleFactor;
    }
    
    /** Setter for property initialScaleFactor.
     * @param initialScaleFactor New value of property initialScaleFactor.
     *
     */
    public void setInitialScaleFactor(double initialScaleFactor) {
        this.initialScaleFactor = initialScaleFactor;
    }
    
    /** Getter for property finalScaleFactor.
     * @return Value of property finalScaleFactor.
     *
     */
    public double getFinalScaleFactor() {
        return this.finalScaleFactor;
    }
    
    /** Setter for property finalScaleFactor.
     * @param finalScaleFactor New value of property finalScaleFactor.
     *
     */
    public void setFinalScaleFactor(double finalScaleFactor) {
        this.finalScaleFactor = finalScaleFactor;
    }
    
    /**
     * the redo method
     */
    public void redo() {
        super.redo();
        moleculeViewer.setScaleFactor(finalScaleFactor);        
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();
        moleculeViewer.setScaleFactor(initialScaleFactor);
    }
    
} // end of class ZoomUndoableEdit
