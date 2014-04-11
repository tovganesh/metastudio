/*
 * SelectionUndoableEdit.java
 *
 * Created on March 28, 2004, 10:22 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;

import java.util.Stack;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * Class to keep track of singular selections edits, undo and redo.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SelectionUndoableEdit extends AbstractUndoableEdit {
    
    private MoleculeViewer moleculeViewer;
    
    /** Holds value of property moleculeScene. */
    private MoleculeScene moleculeScene;
    
    /** Holds value of property initialSelectionStack. */
    private Stack<ScreenAtom> initialSelectionStack;
    
    /** Holds value of property finalSelectionStack. */
    private Stack<ScreenAtom> finalSelectionStack;
    
    /** Creates a new instance of SelectionUndoableEdit */
    public SelectionUndoableEdit(MoleculeViewer mv) {
        this.moleculeViewer = mv;
    }    
    
    /**
     * the redo method
     */
    public void redo() {
        super.redo();
        
        moleculeScene.setSelectionStack(finalSelectionStack);       
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();        
        
        moleculeScene.setSelectionStack(initialSelectionStack);        
    }
    
    /** Getter for property moleculeScene.
     * @return Value of property moleculeScene.
     *
     */
    public MoleculeScene getMoleculeScene() {
        return this.moleculeScene;
    }
    
    /** Setter for property moleculeScene.
     * @param moleculeScene New value of property moleculeScene.
     *
     */
    public void setMoleculeScene(MoleculeScene moleculeScene) {
        this.moleculeScene = moleculeScene;
    }
    
    /** Getter for property initialSelectionStack.
     * @return Value of property initialSelectionStack.
     *
     */
    public Stack<ScreenAtom> getInitialSelectionStack() {
        return this.initialSelectionStack;
    }
    
    /** Setter for property initialSelectionStack.
     * @param initialSelectionStack New value of property initialSelectionStack.
     *
     */
    public void setInitialSelectionStack(
                                   Stack<ScreenAtom> initialSelectionStack) {
        Stack<ScreenAtom> theClone = new Stack<ScreenAtom>();
        
        for(ScreenAtom atom : initialSelectionStack) {
            theClone.push(atom);
        } // end for
        
        this.initialSelectionStack = theClone;
    }
    
    /** Getter for property finalSelectionStack.
     * @return Value of property finalSelectionStack.
     *
     */
    public Stack<ScreenAtom> getFinalSelectionStack() {
        return this.finalSelectionStack;
    }
    
    /** Setter for property finalSelectionStack.
     * @param finalSelectionStack New value of property finalSelectionStack.
     *
     */
    public void setFinalSelectionStack(Stack<ScreenAtom> finalSelectionStack) {
        Stack<ScreenAtom> theClone = new Stack<ScreenAtom>();
        
        for(ScreenAtom atom : finalSelectionStack) {
            theClone.push(atom);
        } // end for
        
        this.finalSelectionStack = theClone;         
    }
    
} // end of class SelectionUndoableEdit
