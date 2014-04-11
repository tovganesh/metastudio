/*
 * AddAtomChainCommand.java
 *
 * Created on 11 Feb, 2009
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import java.util.Stack;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;

/**
 * Command describing addition of a chain of atoms in the MoleculeEditor.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AddAtomChainCommand extends MoleculeEditorCommand {

    private AddSingleAtomCommand addSingleAtomCommand;

    private Stack<Atom> chainAtoms;
    
    /** Create new instance of AddAtomChainCommand */
    public AddAtomChainCommand() {
        // default symbol, to add atoms
        defaultAtomSymbol = "C"; // carbon

        // default bond type is single bond
        defaultBondType   = BondType.SINGLE_BOND;

        // this depends on AddAtomChainCommand for initial positioning
        // of the atom in the chain
        addSingleAtomCommand = new AddSingleAtomCommand();

        // init chain atoms stack
        chainAtoms = new Stack<Atom>();
    }

    /**
     * Validate if this command can be executed depending on the current
     * state of MoleculeScene and X and Y reference points.
     *
     * @return true if successful, false other wise
     */
    @Override
    public boolean validate() {
        if (chainAtoms.isEmpty()) {
           return addSingleAtomCommand.validate(moleculeScene,
                                                referenceX, referenceY);
        } else {
            return true;
        } // end if
    }

    /**
     * Update the structure based on the current state of MoleculeScene
     * and X and Y reference points.
     */
    @Override
    public void update() {
        if (chainAtoms.isEmpty()) {
           addSingleAtomCommand.update(moleculeScene, referenceX, referenceY);
        } else {
           // TODO:
        } // end if
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
