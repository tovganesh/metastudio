/*
 * SceneUndoableEdit.java
 *
 * Created on June 25, 2004, 6:31 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;

/**
 * Class keeps track of scene addition/ deletion to the viewer.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SceneUndoableEdit extends AbstractUndoableEdit {
    
    private MoleculeViewer moleculeViewer;
    
    /**
     * Holds value of property moleculeScene.
     */
    private MoleculeScene moleculeScene;
    
    /** Creates a new instance of SceneUndoableEdit */
    public SceneUndoableEdit(MoleculeViewer mv) {
        this.moleculeViewer = mv;
    }
    
    /**
     * the redo method
     */
    public void redo() {
        super.redo();
        
        // careful, we need to avoid recursion!
        moleculeViewer.disableUndo();
        moleculeViewer.addScene(moleculeScene);
        moleculeViewer.enableUndo();
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();
        
        // careful, we need to avoid recursion!
        moleculeViewer.disableUndo();
        moleculeViewer.removeScene(moleculeScene);
        moleculeViewer.enableUndo();
    }    
    
    /**
     * Getter for property moleculeScene.
     * @return Value of property moleculeScene.
     */
    public MoleculeScene getMoleculeScene() {
        return this.moleculeScene;
    }
    
    /**
     * Setter for property moleculeScene.
     * @param moleculeScene New value of property moleculeScene.
     */
    public void setMoleculeScene(MoleculeScene moleculeScene) {
        this.moleculeScene = moleculeScene;
    }
    
} // end of class SceneUndoableEdit
