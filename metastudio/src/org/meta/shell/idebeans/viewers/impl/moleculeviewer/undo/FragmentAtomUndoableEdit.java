/*
 * FragmentAtomUndoableEdit.java
 *
 * Created on August 18, 2007, 9:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.FragmentAtomEditEvent;

/**
 * Class to keep track of additions or deletions of atoms to a Fragment
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentAtomUndoableEdit extends AbstractUndoableEdit {
    
    private FragmentAtomEditEvent faee;
    
    /** Creates a new instance of FragmentAtomUndoableEdit */
    public FragmentAtomUndoableEdit(FragmentAtomEditEvent faee) {
        this.faee = faee;
    }

    /**
     * the redo method
     */
    public void redo() {
        super.redo();        
     
        if (faee.getEventType() 
             == FragmentAtomEditEvent.FragmentAtomEditEventType.ATOM_ADDED) {
            faee.getFragment().addFragmentAtom(faee.getFragmentAtom());
        } else if (faee.getEventType() 
             == FragmentAtomEditEvent.FragmentAtomEditEventType.ATOM_REMOVED) {
            faee.getFragment().removeFragmentAtom(faee.getFragmentAtom());
        } // end if
        
        // trigger update listeners
        faee.getFragmentationScheme().getFragmentList().triggerListeners();
    }
    
    /**
     * the undo method
     */
    public void undo() {
        super.undo();        
                   
        if (faee.getEventType() 
             == FragmentAtomEditEvent.FragmentAtomEditEventType.ATOM_ADDED) {
            faee.getFragment().removeFragmentAtom(faee.getFragmentAtom());
        } else if (faee.getEventType() 
             == FragmentAtomEditEvent.FragmentAtomEditEventType.ATOM_REMOVED) {
            faee.getFragment().addFragmentAtom(faee.getFragmentAtom());
        } // end if
        
        // trigger update listeners
        faee.getFragmentationScheme().getFragmentList().triggerListeners();
    }    
} // end of class FragmentAtomUndoableEdit
