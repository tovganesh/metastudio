/*
 * AddSingleAtomCommand.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import javax.swing.event.UndoableEditEvent;
import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.shell.idebeans.editors.impl.moleculeditor.undo.AddAtomUndoableEdit;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * A command describing addition of a single atom in MoleculeEditor
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AddSingleAtomCommand extends MoleculeEditorCommand {

    private BondLengthValidator bondLengthValidator;
    
    /** Creates instance of AddSingleAtomCommand */
    public AddSingleAtomCommand() {
        // default symbol, to add atoms
        defaultAtomSymbol = "C"; // carbon
        
        // default bond type is single bond
        defaultBondType   = BondType.SINGLE_BOND;
        
        // add validators for this command
        addValidator((bondLengthValidator = new BondLengthValidator()));
    }       

    /**
     * Validate if this command can be executed depending on the current
     * state of MoleculeScene and X and Y reference points.
     * 
     * @return true if successful, false other wise
     */    
    @Override
    public boolean validate() {
        // init the vaidators
        bondLengthValidator.setDefaultAtomSymbol(defaultAtomSymbol);
        bondLengthValidator.setDefaultBondType(defaultBondType);
        
        return super.validate();
    }
    
    /**
     * Update the structure based on the current state of MoleculeScene 
     * and X and Y reference points.
     */
    @Override
    public void update() {
       MoleculeEditorCommonData mecd = MoleculeEditorCommonData.getInstance();
       ScreenAtom nearestAtom = 
               mecd.getNearestScreenAtom(moleculeScene, referenceX, referenceY);                   

       Point3D p1 
              = mecd.getTransformedPoint(moleculeScene, referenceX, referenceY);
       
       Molecule molecule = moleculeScene.getMolecule();
       Atom newAtom = new Atom(defaultAtomSymbol, 0.0, 
                               p1, molecule.getNumberOfAtoms());            
                           
       molecule.disableListeners();       
       if (validate()) {
        newAtom.addConnection(nearestAtom.getAtom().getIndex(), defaultBondType);
        nearestAtom.getAtom().addConnection(newAtom.getIndex(), defaultBondType);
       } // end if
       molecule.addAtom(newAtom);            
       molecule.enableListeners();

       // last added atom
       lastAddedAtom = newAtom;
       
       // fire up an undo event
       fireUndoableEditListenerUndoableEditHappened(new UndoableEditEvent(this,
                   new AddAtomUndoableEdit(molecule, newAtom)));
    }

    protected Atom lastAddedAtom;

    /**
     * Get the value of lastAddedAtom
     *
     * @return the value of lastAddedAtom
     */
    public Atom getLastAddedAtom() {
        return lastAddedAtom;
    }

    /**
     * Set the value of lastAddedAtom
     *
     * @param lastAddedAtom new value of lastAddedAtom
     */
    public void setLastAddedAtom(Atom lastAddedAtom) {
        this.lastAddedAtom = lastAddedAtom;
    }

    protected String defaultAtomSymbol;

    /**
     * Get the value of defaultAtomSymbol
     *
     * @return the value of defaultAtomSymbol
     */
    public String getDefaultAtomSymbol() {
        return defaultAtomSymbol;
    }

    /**
     * Set the value of defaultAtomSymbol
     *
     * @param defaultAtomSymbol new value of defaultAtomSymbol
     */
    public void setDefaultAtomSymbol(String defaultAtomSymbol) {
        this.defaultAtomSymbol = defaultAtomSymbol;
    }

    protected BondType defaultBondType;

    /**
     * Get the value of defaultBondType
     *
     * @return the value of defaultBondType
     */
    public BondType getDefaultBondType() {
        return defaultBondType;
    }

    /**
     * Set the value of defaultBondType
     *
     * @param defaultBondType new value of defaultBondType
     */
    public void setDefaultBondType(BondType defaultBondType) {
        this.defaultBondType = defaultBondType;
    }
}
