/*
 * MollerPlessetSCFMethod.java
 *
 * Created on August 15, 2004, 9:14 AM
 */

package org.meta.math.qm;


import org.meta.math.Vector;
import org.meta.math.qm.event.SCFEvent;
import org.meta.math.qm.integral.IntegralsUtil;
import org.meta.molecule.Molecule;

/**
 * Moller Plesset Perturbation Theory as an extension of HF method.
 * One of the first and the most popular post HF methods. <br>
 * This class only implements second order perturbation (MP2), but may be 
 * modified later to provide heigher order perturbation.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MollerPlessetSCFMethod extends HartreeFockSCFMethod {
    
    /**
     * Holds value of property mpLevel.
     */
    private int mpLevel;
    
    /**
     * transformed MO integrals
     */
    protected double [] moInts;
    
    /** Creates a new instance of MollerPlessetSCFMethod */
    public MollerPlessetSCFMethod(Molecule molecule, OneElectronIntegrals oneEI, 
                                  TwoElectronIntegrals twoEI) {
        super(molecule, oneEI, twoEI);
        
        mpLevel = 2; // second level correction (MP2) is default        
    }
    
    /**
     * Perform the SCF optimization of the molecular wave function until 
     * the energy converges.
     */
    @Override
    public void scf() {
        // first do the HF-SCF procedure
        super.scf();
        
        // transform AO -> MO ints
        transformAOIntsToMOInts();
        
        // now compute MP2 energy and then compute the total energy
        int noOfBasisFunctions = mos.getRowCount();        
        int noOfElectrons = molecule.getNumberOfElectrons();
        int noOfOccupancies = noOfElectrons / 2;
        int noOfVirtualOrbitals = noOfBasisFunctions - noOfOccupancies;
        int noOfUnOccupied  = noOfOccupancies + noOfVirtualOrbitals;
        int a, b, r, s;
        
        double mp2Energy = 0.0;
        double arbs, asbr;
        double [] orbE = mos.getOrbitalEnergies();
        
        for(a=0; a<noOfOccupancies; a++) {
            for(b=0; b<noOfOccupancies; b++) {
                for(r=noOfOccupancies; r<noOfUnOccupied; r++) {
                    for(s=noOfOccupancies; s<noOfUnOccupied; s++) {
                        arbs = moInts[IntegralsUtil.ijkl2intindex(a, r, b, s)];
                        asbr = moInts[IntegralsUtil.ijkl2intindex(a, s, b, r)];
                        
                        mp2Energy += arbs * (2.0 * arbs - asbr) /
                                     (orbE[a] + orbE[b] - orbE[r] - orbE[s]);
                    } // end s
                } // end r
            } // end b
        } // end a
        
        System.out.println(mp2Energy);
        
        energy += mp2Energy;
        
        scfEvent.setType(SCFEvent.CONVERGED_EVENT);
        scfEvent.setCurrentIteration(scfIteration);
        scfEvent.setCurrentEnergy(energy);
        fireSCFEventListenerScfEventOccured(scfEvent);
    }
    
    /**
     * O(N^5) 4-index transformation of the two-electron integrals.
     * Only transform the ones needed for MP2, which reduces the
     * scaling to O(nN^4), where n are the occs (<<N).
     */
    protected void transformAOIntsToMOInts() {
        // Start with (mu,nu|sigma,eta)
        // Unpack AOints and transform sigma -> b
        int mu, nu, sigma, eta, a, b, i, j;
        
        int noOfBasisFunctions = mos.getRowCount();
        int noOfMOS = mos.getColumnCount();
        int noOfElectrons = molecule.getNumberOfElectrons();
        int noOfOccupancies = noOfElectrons / 2;
        
        Vector tempVector = new Vector(noOfBasisFunctions);
        double [] tempvec = tempVector.getVector();
        double [] aoints  = twoEI.getTwoEIntegrals();
        
        double [][][][] temp = 
          new double[noOfBasisFunctions][noOfBasisFunctions]
                    [noOfOccupancies][noOfBasisFunctions];
        
        for(mu=0; mu<noOfBasisFunctions; mu++) {
            for(nu=0; nu<noOfBasisFunctions; nu++) {
                for(eta=0; eta<noOfBasisFunctions; eta++) {
                    for(b=0; b<noOfOccupancies; b++) {                        
                        for(sigma=0; sigma<noOfBasisFunctions; sigma++) {
                            tempvec[sigma] = 
                            aoints[IntegralsUtil.ijkl2intindex(mu, nu, sigma, eta)];
                        } // end sigma
                        
                        temp[mu][nu][b][eta] 
                                = mos.getRowVector(b).dot(tempVector);
                    } // end b
                } // end eta
            } // end nu
        } // end mu
        
        double [][][][] temp2 = 
          new double[noOfOccupancies][noOfBasisFunctions]
                    [noOfOccupancies][noOfBasisFunctions];
        
        for(nu=0; nu<noOfBasisFunctions; nu++) {
            for(eta=0; eta<noOfBasisFunctions; eta++) {
                for(b=0; b<noOfOccupancies; b++) {
                    for(a=0; a<noOfOccupancies; a++) {  
                        for(i=0; i<noOfBasisFunctions; i++) {
                            tempvec[i] = temp[i][nu][b][eta];
                        } // end i
                        
                        temp2[a][nu][b][eta] 
                                = mos.getRowVector(a).dot(tempVector);
                    } // end a
                } // end b
            } // end eta
        } // end nu
        
        temp = new double[noOfBasisFunctions][noOfBasisFunctions]
                         [noOfOccupancies][noOfBasisFunctions];
        
        for(a=0; a<noOfOccupancies; a++) {
            for(nu=0; nu<noOfBasisFunctions; nu++) {
                for(b=0; b<noOfOccupancies; b++) {
                    for(j=0; j<noOfMOS; j++) {  
                        for(i=0; i<noOfBasisFunctions; i++) {
                            tempvec[i] = temp2[a][nu][b][i];
                        } // end i
                        
                        temp[a][nu][b][j] = mos.getRowVector(j).dot(tempVector);
                    } // end a
                } // end b
            } // end eta
        } // end nu
        
        temp2 = null;
        
        moInts = new double[aoints.length];
        
        // Transform mu -> i and repack integrals
        for(a=0; a<noOfOccupancies; a++) {
            for(j=0; j<noOfMOS; j++) {
                for(b=0; b<noOfOccupancies; b++) {
                    for(i=0; i<noOfMOS; i++) {  
                        for(sigma=0; sigma<noOfBasisFunctions; sigma++) {
                            tempvec[sigma] = temp[a][sigma][b][j];
                        } // end sigma
                        
                        moInts[IntegralsUtil.ijkl2intindex(a, i, b, j)]
                                = mos.getRowVector(i).dot(tempVector);
                    } // end i
                } // end b
            } // end j
        } // end a
        
        temp = null;
    }
    
    /**
     * Getter for property mpLevel.
     * @return Value of property mpLevel.
     */
    public int getMpLevel() {
        return this.mpLevel;
    }
    
    /**
     * Setter for property mpLevel.
     * @param mpLevel New value of property mpLevel.
     */
    public void setMpLevel(int mpLevel) {
        this.mpLevel = mpLevel;
    }

    
} // end of class MollerPlessetSCFMethod
