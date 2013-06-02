/*
 * LabelUndoableEdit.java
 *
 * Created on June 25, 2004, 9:50 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import java.util.*;

import javax.swing.undo.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * Class keeps track of all the lable additions and deletions.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LabelUndoableEdit extends AbstractUndoableEdit {
    
    private MoleculeViewer moleculeViewer;
    
    /**
     * Holds value of property showingSymbol.
     */
    private boolean showingSymbol;
    
    /**
     * Holds value of property showingID.
     */
    private boolean showingID;
    
    /**
     * Holds value of property showingCenter.
     */
    private boolean showingCenter;
    
    /**
     * Holds value of property selectionStack.
     */
    private Stack<ScreenAtom> selectionStack;
    
    /**
     * Holds value of property moleculeScene.
     */
    private MoleculeScene moleculeScene;
    
    /**
     * Holds value of property currentSymbolState.
     */
    private boolean currentSymbolState;
    
    /**
     * Holds value of property currentIDState.
     */
    private boolean currentIDState;
    
    /**
     * Holds value of property currentCenterState.
     */
    private boolean currentCenterState;
    
    /** Creates a new instance of LabelUndoableEdit */
    public LabelUndoableEdit(MoleculeViewer mv) {
        this.moleculeViewer = mv;
        
        showingSymbol = showingID = showingCenter = false;
        currentSymbolState = currentIDState = currentSymbolState = false;
    }    
    
    /**
     * the redo method
     */
    public void redo() {
        super.redo();
        
        Stack<ScreenAtom> theClone = new Stack<ScreenAtom>();
        
        for(ScreenAtom atom : selectionStack) {
            theClone.push(atom);
        } // end for
        
        moleculeScene.setSelectionStack(theClone);
        
        if (showingSymbol) {
            moleculeScene.showSymbolLables(currentSymbolState);
        } else if (showingID) {
            moleculeScene.showIDLables(currentIDState);
        } else if (showingCenter) {
            moleculeScene.showCenterLables(currentCenterState);
        } // end if
        moleculeScene.getSelectionStack().clear();
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();               
        
        Stack<ScreenAtom> theClone = new Stack<ScreenAtom>();
        
        for(ScreenAtom atom : selectionStack) {
            theClone.push(atom);
        } // end for
        
        moleculeScene.setSelectionStack(theClone);
        
        if (showingSymbol) {
            moleculeScene.showSymbolLables(!currentSymbolState);
        } else if (showingID) {
            moleculeScene.showIDLables(!currentIDState);
        } else if (showingCenter) {
            moleculeScene.showCenterLables(!currentCenterState);
        } // end if
        moleculeScene.getSelectionStack().clear();
    }    
    
    /**
     * Getter for property showingSymbol.
     * @return Value of property showingSymbol.
     */
    public boolean isShowingSymbol() {
        return this.showingSymbol;
    }
    
    /**
     * Setter for property showingSymbol.
     * @param showingSymbol New value of property showingSymbol.
     */
    public void setShowingSymbol(boolean showingSymbol) {
        this.showingSymbol = showingSymbol;
    }
    
    /**
     * Getter for property showingID.
     * @return Value of property showingID.
     */
    public boolean isShowingID() {
        return this.showingID;
    }
    
    /**
     * Setter for property showingID.
     * @param showingID New value of property showingID.
     */
    public void setShowingID(boolean showingID) {
        this.showingID = showingID;
    }
    
    /**
     * Getter for property showingCenter.
     * @return Value of property showingCenter.
     */
    public boolean isShowingCenter() {
        return this.showingCenter;
    }
    
    /**
     * Setter for property showingCenter.
     * @param showingCenter New value of property showingCenter.
     */
    public void setShowingCenter(boolean showingCenter) {
        this.showingCenter = showingCenter;
    }
    
    /**
     * Getter for property selectionStack.
     * @return Value of property selectionStack.
     */
    public Stack<ScreenAtom> getSelectionStack() {
        return this.selectionStack;
    }
    
    /**
     * Setter for property selectionStack.
     * @param selectionStack New value of property selectionStack.
     */
    public void setSelectionStack(Stack<ScreenAtom> selectionStack) {
        Stack<ScreenAtom> theClone = new Stack<ScreenAtom>();
        
        for(ScreenAtom atom : selectionStack) {
            theClone.push(atom);
        } // end for
        
        this.selectionStack = theClone;
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
    
    /**
     * Getter for property currentSymbolState.
     * @return Value of property currentSymbolState.
     */
    public boolean isCurrentSymbolState() {
        return this.currentSymbolState;
    }
    
    /**
     * Setter for property currentSymbolState.
     * @param currentSymbolState New value of property currentSymbolState.
     */
    public void setCurrentSymbolState(boolean currentSymbolState) {
        this.currentSymbolState = currentSymbolState;
    }
    
    /**
     * Getter for property currentIDState.
     * @return Value of property currentIDState.
     */
    public boolean isCurrentIDState() {
        return this.currentIDState;
    }
    
    /**
     * Setter for property currentIDState.
     * @param currentIDState New value of property currentIDState.
     */
    public void setCurrentIDState(boolean currentIDState) {
        this.currentIDState = currentIDState;
    }
    
    /**
     * Getter for property currentCenterState.
     * @return Value of property currentCenterState.
     */
    public boolean isCurrentCenterState() {
        return this.currentCenterState;
    }
    
    /**
     * Setter for property currentCenterState.
     * @param currentCenterState New value of property currentCenterState.
     */
    public void setCurrentCenterState(boolean currentCenterState) {
        this.currentCenterState = currentCenterState;
    }
    
} // end of class LabelUndoableEdit
