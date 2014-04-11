/*
 * BondUndoableEdit.java
 *
 * Created on August 18, 2007, 8:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo;

import javax.swing.undo.*;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;

/**
 * Class to keep track of addition or deletion of Bonds in a molecule.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BondUndoableEdit extends AbstractUndoableEdit {
    
    private Molecule molecule;
    private BondType oldBond, newBond;
    private int atomIndex1, atomIndex2;
    
    /** Creates a new instance of BondUndoableEdit */
    public BondUndoableEdit(Molecule molecule, BondType oldBond, 
                            BondType newBond, int atomIndex1, int atomIndex2) {
        this.molecule   = molecule;
        this.oldBond    = oldBond;
        this.newBond    = newBond;
        this.atomIndex1 = atomIndex1;
        this.atomIndex2 = atomIndex2;
    }
    
    /**
     * the redo method
     */
    @Override
    public void redo() {
        super.redo();        
        
        molecule.setBondType(atomIndex1, atomIndex2, newBond);
    }
    
    /**
     * the undo method
     */
    @Override
    public void undo() {
        super.undo();        
           
        molecule.setBondType(atomIndex1, atomIndex2, oldBond);
    }    
} // end of class BondUndoableEdit
