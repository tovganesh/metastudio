/*
 * BasisFunctions.java
 *
 * Created on July 26, 2004, 7:04 AM
 */

package org.meta.math.qm;

import java.util.*;
import org.meta.math.qm.basis.AtomicBasis;
import org.meta.math.qm.basis.BasisReader;
import org.meta.math.qm.basis.BasisSet;
import org.meta.math.qm.basis.ContractedGaussian;
import org.meta.math.qm.basis.Orbital;
import org.meta.math.qm.basis.Power;
import org.meta.math.qm.basis.PowerList;
import org.meta.math.qm.basis.ShellList;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.molecule.UserDefinedAtomProperty;
import org.meta.molecule.event.MoleculeStateChangeEvent;
import org.meta.molecule.event.MoleculeStateChangeListener;

/**
 * Class to construct basis functions of a given molecule and a basis set
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BasisFunctions {
    
    /**
     * Holds value of property basisFunctions.
     */
    private ArrayList<ContractedGaussian> basisFunctions;

    private ShellList shellList;

    private Molecule molecule;

    private MoleculeStateChangeListener molStateChangeListener;
    
    /** 
     * Creates a new instance of BasisFunctions 
     *
     * @param molecule the Molecule whose basis function is requested
     * @param basisName the name of the basis set (like sto3g)
     */
    public BasisFunctions(Molecule molecule, String basisName) 
                                             throws Exception {
        // initialize the basis functions
        getBasisFunctions(molecule, basisName);
        this.basisName = basisName;
        this.molecule  = molecule;

        // and initialize the shell list
        initShellList();

        molStateChangeListener = new MoleculeStateChangeListener() {
            @Override
            public void moleculeChanged(MoleculeStateChangeEvent event) {
                try {
                    getBasisFunctions(BasisFunctions.this.molecule,
                                      BasisFunctions.this.basisName);
                    initShellList();
                } catch (Exception e) {
                    System.err.println("Unable to update basis function! ");
                    e.printStackTrace();
                } // end of try .. catch block
            }
        };
        molecule.addMoleculeStateChangeListener(molStateChangeListener);
    }

    private String basisName;

    /**
     * Get the value of basisName
     *
     * @return the value of basisName
     */
    public String getBasisName() {
        return basisName;
    }

    /**
     * Set the value of basisName
     *
     * @param basisName new value of basisName
     */
    public void setBasisName(String basisName) {
        this.basisName = basisName;
    }

    /**
     * Getter for property basisFunctions.
     * @return Value of property basisFunctions.
     */
    public ArrayList<ContractedGaussian> getBasisFunctions() {
        return this.basisFunctions;
    }

    /**
     * Getter for property shellList.
     * @return Value of property shellList.
     */
    public ShellList getShellList() {
        return this.shellList;
    }

    /**
     * Getter for property basisFunctions.
     *
     * @param molecule the Molecule whose basis function is requested
     * @param basisName the name of the basis set (like sto3g)
     * @return Value of property basisFunctions.
     */
    private ArrayList<ContractedGaussian> getBasisFunctions(Molecule molecule,
                                                            String basisName)
                                                              throws Exception {
        BasisSet basis = BasisReader.getInstance().readBasis(basisName);
        Iterator atoms = molecule.getAtoms();
        
        basisFunctions = new ArrayList<ContractedGaussian>();
        
        Atom atom;
        AtomicBasis atomicBasis;
        while(atoms.hasNext()) { // loop over atoms
            atom = (Atom) atoms.next();
            atomicBasis = basis.getAtomicBasis(atom.getSymbol());
            
            Iterator<Orbital> orbitals = atomicBasis.getOrbitals().iterator();
            Orbital orbital;
            ArrayList<ContractedGaussian> atomicFunctions =
                    new ArrayList<ContractedGaussian>();
            
            while(orbitals.hasNext()) { // loop over atom orbitals
                orbital = orbitals.next();
                
                Iterator<Power> pList = PowerList.getInstance()
                                              .getPowerList(orbital.getType());
                Power power;
                while(pList.hasNext()) { // and the power list, sp2 etc..
                    power = pList.next();
                    
                    ContractedGaussian cg = new ContractedGaussian(atom, power);
                    Iterator<Double> coeff = orbital.getCoefficients().iterator();
                    Iterator<Double> exp = orbital.getExponents().iterator();
                    
                    while(coeff.hasNext()) { // build the CG from PGs
                        cg.addPrimitive(exp.next().doubleValue(), 
                                        coeff.next().doubleValue());
                    } // end while
                    
                    cg.normalize();
                    cg.setIndex(basisFunctions.size()); // send an index
                    basisFunctions.add(cg); // add this CG to list
                    atomicFunctions.add(cg); // add the reference to atom list
                } // end while
            } // end while

            // save a reference of the basis functions centered on
            // this atom as a user defined property of the atom
            try {
              atom.addUserDefinedAtomProperty(
                new UserDefinedAtomProperty("basisFunctions", atomicFunctions));
            } catch(UnsupportedOperationException e) {
                UserDefinedAtomProperty up =
                        atom.getUserDefinedAtomProperty("basisFunctions");
                up.setValue(atomicFunctions);
            } // end of try .. catch block
        } // end while
        
        return this.basisFunctions;
    }

    /**
     * Initialize the shell list
     */
    private void initShellList() {
        shellList = new ShellList();

        for(ContractedGaussian cg:basisFunctions) {
            shellList.addShellPrimitive(cg);
        } // end for
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        molecule.removeMoleculeStateChangeListener(molStateChangeListener);
    }
} // end of class BasisFunctions
