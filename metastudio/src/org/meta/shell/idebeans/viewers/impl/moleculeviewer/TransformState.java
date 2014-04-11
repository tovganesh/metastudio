/*
 * TransformState.java
 *
 * Created on January 26, 2004, 7:19 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

/**
 * Specifies the transformation state of the MoleculeViewer
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TransformState implements ViewerState {
    
    /** no transformation state */
    public static final TransformState NO_STATE        = new TransformState(0);
    
    /** translate state */
    public static final TransformState TRANSLATE_STATE = new TransformState(1);
    
    /** rotate state */
    public static final TransformState ROTATE_STATE    = new TransformState(2);
    
    /** scale/ zoom state */
    public static final TransformState SCALE_STATE     = new TransformState(3);
    
    private int state;
    
    /** Creates a new instance of TransformState */
    private TransformState(int state) {
        this.state = state;
    }
    
    /**
     * overridden equals() method
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if ((obj instanceof TransformState)
            && (((TransformState) obj).state == this.state)) {
            return true;
        } else {
            return false;
        } // end if
    }
    
    /**
     * overriden toString()
     */
    public String toString() {
        if (this.equals(TransformState.NO_STATE)) {
            return "No transform state";
        } else if (this.equals(TransformState.TRANSLATE_STATE)) {
            return "Translation state";
        } else if (this.equals(TransformState.ROTATE_STATE)) {
            return "Rotation state";
        } else if (this.equals(TransformState.SCALE_STATE)) {
            return "Zoom state";
        } else {
            return "Unknown state";
        } // end if
    }
} // end of class TransformState
