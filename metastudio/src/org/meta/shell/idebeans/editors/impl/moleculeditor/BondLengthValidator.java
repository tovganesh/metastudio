/*
 * BondLengthValidator.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.MoleculeBuilder;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * A validator for checking bond lenght..
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BondLengthValidator extends MoleculeEditorCommandValidator {

    private MoleculeBuilder moleculeBuilder;
    
    /** Creates a new instance of BondLengthValidator */
    public BondLengthValidator() {
        try {
            moleculeBuilder   = (MoleculeBuilder) Utility.getDefaultImplFor(
                                          MoleculeBuilder.class).newInstance();
        } catch (Exception e) {
            System.out.println("Unexpected exception initing MoleculeEditor: "
                               + e.toString());
            e.printStackTrace();
            moleculeBuilder    = null;
        } // end of try .. catch block
    }

    /**
     * Validate if this command can be executed depending on the current
     * state of MoleculeScene and X and Y reference points.
     * 
     * @return true if successful, false other wise
     */ 
    @Override
    public boolean validate() {
       MoleculeEditorCommonData mecd = MoleculeEditorCommonData.getInstance();
       ScreenAtom nearestAtom = 
               mecd.getNearestScreenAtom(moleculeScene, referenceX, referenceY);
            
       if (nearestAtom == null) return false;
       
       // find the matrices (distance,...) form the nearest atom
       Point3D p1 
              = mecd.getTransformedPoint(moleculeScene, referenceX, referenceY);
       
       return moleculeBuilder.canFormBond(nearestAtom.getAtom(), 
                       new Atom(defaultAtomSymbol, 0.0, p1), defaultBondType);
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
