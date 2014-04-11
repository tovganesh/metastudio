/*
 * MoleculeEditorCommand.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;

/**
 * An abstract class representing a command in MoleculeEditor, this command
 * inturn represents a build activity.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class MoleculeEditorCommand {

    /** Constructor for MoleculeEditorCommand */
    public MoleculeEditorCommand() {
        enableUndo = true;
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
    public boolean validate() {
        boolean isValid = true;
        
        for(MoleculeEditorCommandValidator mecv : validatorList) {
            isValid = isValid 
                      && mecv.validate(moleculeScene, referenceX, referenceY);
        } // end for
        
        return isValid;
    }
 
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
    
    /**
     * Update the structure based on the current state of MoleculeScene 
     * and X and Y reference points.
     */
    public abstract void update();
 
    /**
     * Update the structure based on the values of MoleculeScene 
     * and X and Y reference points. This method merely makes
     * values of MoleculeScene and X and Y reference points current and calls
     * <code>update()</code>.
     * 
     * @param scene the MoleculeScene object under consideration.
     * @param referenceX current reference X point
     * @param referenceY current reference Y point
     */
    public void update(MoleculeScene scene, int referenceX, int referenceY) {
        this.referenceX    = referenceX;
        this.referenceY    = referenceY;
        this.moleculeScene = scene;
               
        update();
    }
    
    /**
     * validator list
     */
    protected ArrayList<MoleculeEditorCommandValidator> validatorList;
    
    /**
     * Add a new validator instance.
     * 
     * @param validator a MoleculeEditorCommandValidator instance
     */
    public void addValidator(MoleculeEditorCommandValidator validator) {
        if (validatorList == null) { 
            validatorList = new ArrayList<MoleculeEditorCommandValidator>();
        } // end if
        
        validatorList.add(validator);        
    }
    
    /**
     * Remove a validator instance.
     * 
     * @param validator a MoleculeEditorCommandValidator instance to be removed
     */
    public void removeValidator(MoleculeEditorCommandValidator validator) {
        if (validatorList == null) return;
        
        validatorList.remove(validator);
    }
    
    /** Utility field used by event firing mechanism. */
    private EventListenerList listenerList =  null;

    /** Registers UndoableEditListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addUndoableEditListener(
            UndoableEditListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList();
        }
        listenerList.add(UndoableEditListener.class, listener);
    }

    /** Removes UndoableEditListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeUndoableEditListener(
            UndoableEditListener listener) {
        listenerList.remove(UndoableEditListener.class, listener);
    }

    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     *
     */
    protected void fireUndoableEditListenerUndoableEditHappened(
            UndoableEditEvent event) {
        if (!enableUndo) return;

        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==UndoableEditListener.class) {
                ((UndoableEditListener)listeners[i+1])
                                       .undoableEditHappened(event);
            }
        }
    }

    /** enable / disable undoable events */
    protected boolean enableUndo;

    /**
     * enable undo
     */
    public synchronized void enableUndo() {
        enableUndo = true;
    }

    /**
     * disable undo
     */
    public synchronized void disableUndo() {
        enableUndo = false;
    }
}
