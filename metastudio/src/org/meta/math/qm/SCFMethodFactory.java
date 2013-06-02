/*
 * SCFMethodFactory.java
 *
 * Created on August 15, 2004, 2:35 PM
 */

package org.meta.math.qm;


import java.lang.ref.WeakReference;
import org.meta.molecule.Molecule;


/**
 * Factory of SCF methods. <br>
 * Follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SCFMethodFactory {
    
    private static WeakReference<SCFMethodFactory> _scfMethodFactory = null;
    
    /** Creates a new instance of SCFMethodFactory */
    private SCFMethodFactory() {
    }
    
    /**
     * Get an instance (and the only one) of SCFMethodFactory 
     *
     * @return SCFMethodFactory instance
     */
    public static SCFMethodFactory getInstance() {
        if (_scfMethodFactory == null) {
            _scfMethodFactory = 
                    new WeakReference<SCFMethodFactory>(new SCFMethodFactory());
        } // end if
        
        SCFMethodFactory scfMethodFactory = _scfMethodFactory.get();
        
        if (scfMethodFactory == null) {
            scfMethodFactory  = new SCFMethodFactory();
            _scfMethodFactory = 
                    new WeakReference<SCFMethodFactory>(scfMethodFactory);
        } // end if
        
        return scfMethodFactory;
    }
    
    /**
     * Return an appropriate class appropriate <code>SCFType</code>
     *
     * @param molecule the molecule for which calculations are to be performed
     * @param oneEI 1E integrals for this molecule at appropriate basis
     * @param twoEI 2E integrals for this molecule at appropriate basis
     * @param type the SCFType instance
     */
    public SCFMethod getSCFMethod(Molecule molecule, OneElectronIntegrals oneEI, 
                                  TwoElectronIntegrals twoEI, SCFType type) {
        if (type.equals(SCFType.HARTREE_FOCK)) {
            return new HartreeFockSCFMethod(molecule, oneEI, twoEI);
        } else if (type.equals(SCFType.HARTREE_FOCK_DIRECT)) {
            return new HartreeFockSCFMethod(molecule, oneEI, twoEI, type);
        } else if (type.equals(SCFType.MOLLER_PLESSET)) {
            return new MollerPlessetSCFMethod(molecule, oneEI, twoEI);
        } else {
            throw new UnsupportedOperationException("The type : " + type 
                                         + " has no known implementing class!");
        } // end if
    }
} // end of class SCFMethodFactory
