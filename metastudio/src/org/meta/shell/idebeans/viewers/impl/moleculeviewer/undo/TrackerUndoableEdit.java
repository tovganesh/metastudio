/*
 * TrackerUndoableEdit.java
 *
 * Created on March 29, 2004, 7:29 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.Tracker;

/**
 * Class to keep track of tracker edits, undo and redo.
 *
 * @author  V.Ganesh
 */
public class TrackerUndoableEdit extends AbstractUndoableEdit {
    
    private MoleculeScene moleculeScene;
    private Tracker       theTracker;
    
    /** Creates a new instance of TrackerUndoableEdit */
    public TrackerUndoableEdit(MoleculeScene ms, Tracker theTracker) {
        this.moleculeScene = ms;
        this.theTracker    = theTracker;
    }
    
    /**
     * the redo method
     */
    public void redo() {
        super.redo();
        
        moleculeScene.addTracker(theTracker);
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();        
        
        moleculeScene.removeTracker(theTracker);
    }
    
} // end of class TrackerUndoableEdit
