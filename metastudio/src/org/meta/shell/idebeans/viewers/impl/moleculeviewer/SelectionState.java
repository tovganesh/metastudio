/*
 * SelectionState.java
 *
 * Created on February 15, 2004, 6:58 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

/**
 * Specifies the selection state of the MoleculeViewer
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SelectionState implements ViewerState {
    
    /** no selection state */
    public static SelectionState NO_STATE = new SelectionState(0);
    
    /** pointer selection state */
    public static SelectionState POINTER_SELECTION = new SelectionState(1);
    
    /** free hand selection state */
    public static SelectionState FREE_HAND_SELECTION = new SelectionState(2);
    
    /** cuboid based selection state */
    public static SelectionState CUBOID_SELECTION = new SelectionState(3);
    
    /** sphere based selection state */
    public static SelectionState SPHERE_SELECTION = new SelectionState(4);
    
    private int state;
    
    /** Creates a new instance of SelectionState */
    private SelectionState(int state) {
        this.state = state;        
    }
    
    /**
     * overridden equals() method
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if ((obj instanceof SelectionState)
            && (((SelectionState) obj).state == this.state)) {
            return true;
        } else {
            return false;
        } // end if
    }
    
    /**
     * overridden toString()
     */
    public String toString() {
        if (this.equals(SelectionState.NO_STATE)) {
            return "No selection state";
        } else if (this.equals(SelectionState.POINTER_SELECTION)) {
            return "Pointer selection state";
        } else if (this.equals(SelectionState.FREE_HAND_SELECTION)) {
            return "Free hand selection state";
        } else if (this.equals(SelectionState.CUBOID_SELECTION)) {
            return "Cuboid based selection state";
        } else if (this.equals(SelectionState.SPHERE_SELECTION)) {
            return "Sphere based selection state";
        } else {
            return "Unknown state";
        } // end if
    }
} // end of class SelectionState
