/*
 * AddAtomUndoableEdit.java 
 *
 * Created on 30 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor.undo;

import javax.swing.undo.*;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * Class to keep track of added atom.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AddAtomUndoableEdit extends AbstractUndoableEdit {
    
    private Molecule molecule;
    private Atom newAtom;
    
    /** Creates a new instance of AddAtomUndoableEdit */
    public AddAtomUndoableEdit(Molecule molecule, Atom newAtom) {
        this.molecule = molecule;
        this.newAtom  = newAtom;
    }
    
    /**
     * the redo method
     */
    @Override
    public void redo() {
        super.redo();        
        
        // TODO: check correctness
        newAtom.setIndex(molecule.getNumberOfAtoms());
        molecule.addAtom(newAtom);
    }
    
    /**
     * the undo method
     */
    @Override
    public void undo() {
        super.undo();        
           
        molecule.removeAtom(newAtom);
    }    
}
