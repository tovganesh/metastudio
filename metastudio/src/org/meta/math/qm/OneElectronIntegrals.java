/*
 * OneElectronIntegrals.java
 *
 * Created on July 28, 2004, 7:03 AM
 */

package org.meta.math.qm;

import java.util.*;
import org.meta.config.impl.AtomInfo;

import org.meta.math.qm.basis.ContractedGaussian;
import org.meta.molecule.Molecule;
import org.meta.parallel.AbstractSimpleParallelTask;
import org.meta.parallel.SimpleParallelTask;
import org.meta.parallel.SimpleParallelTaskExecuter;

/**
 * The 1E integral (overlap S matrix) and 1E hCore matrices driver.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class OneElectronIntegrals {
    
    /**
     * Holds value of property overlap.
     */
    private Overlap overlap;
    
    /**
     * Holds value of property hCore.
     */
    private HCore hCore;
    
    private BasisFunctions basisFunctions;
    private Molecule molecule;
    
    /** 
     * Creates a new instance of OneElectronIntegrals 
     *
     * @param basisFunctions the basis functions to be used
     * @param mol the Molecule object, of whose 1E integrals are to be evaliated
     */
    public OneElectronIntegrals(BasisFunctions basisFunctions, Molecule mol) {
        this.basisFunctions = basisFunctions;
        this.molecule       = mol;
        
        // compute the 1E integrals, form S matrix and hCore
        compute1E();
    }

    /**
     * Get BasisFunctions associated with this 1E evaluation
     * 
     * @return instance of basisFunctions
     */
    public BasisFunctions getBasisFunctions() {
        return basisFunctions;
    }
    
    /**
     * compute the 1E integrals, form S matrix and hCore
     */
    protected void compute1E() {
        ArrayList<ContractedGaussian> bfs = basisFunctions.getBasisFunctions();
        int noOfBasisFunctions = bfs.size();
        int i;
        
        // allocate memory
        this.overlap = new Overlap(noOfBasisFunctions);
        this.hCore = new HCore(noOfBasisFunctions);
                
        // read in the atomic numbers
        int [] atomicNumbers = new int[molecule.getNumberOfAtoms()];
        AtomInfo ai = AtomInfo.getInstance();
        
        for(i=0; i<atomicNumbers.length; i++) {
            atomicNumbers[i] = ai.getAtomicNumber(
                                     molecule.getAtom(i).getSymbol());
        } // end for
        
        SimpleParallelTaskExecuter pTaskExecuter
                                   = new SimpleParallelTaskExecuter();

        OneElectronIntegralEvaluaterThread tThread
                   = new OneElectronIntegralEvaluaterThread(atomicNumbers);
        tThread.setTaskName("OneElectronIntegralEvaluater Thread");
        tThread.setTotalItems(noOfBasisFunctions);

        pTaskExecuter.execute(tThread);
    }
    
    /**
     * method to actually compute 1E integrals
     */
    private void compute1E(int startBasisFunction, int endBasisFunction, 
                           double [][] overlap, double [][] hCore,
                           int [] atomicNumbers,
                           ArrayList<ContractedGaussian> bfs) {
        
        int noOfBasisFunctions = bfs.size();
        int i, j, k;
        
        ContractedGaussian bfi, bfj;
        
        // set up the S matrix and the hCore h
        for(i=startBasisFunction; i<endBasisFunction; i++) {
            bfi = (ContractedGaussian) bfs.get(i);
            
            for(j=0; j<noOfBasisFunctions; j++) {
                bfj = (ContractedGaussian) bfs.get(j);
                
                overlap[i][j] = bfi.overlap(bfj);  // the overlap matrix
                hCore[i][j] = bfi.kinetic(bfj); // KE matrix elements
                
                for(k=0; k<atomicNumbers.length; k++) {                    
                    hCore[i][j] += atomicNumbers[k] * bfi.nuclear(bfj,
                                      molecule.getAtom(k).getAtomCenterInAU());
                } // end for
            } // end for
        } // end for        
    }
    
    /**
     * Getter for property overlap.
     * @return Value of property overlap.
     */
    public Overlap getOverlap() {
        return this.overlap;
    }
    
    /**
     * Getter for property hCore.
     * @return Value of property hCore.
     */
    public HCore getHCore() {
        return this.hCore;
    }

    /**
     * Class encapsulating the way to compute 1E electrons in a way 
     * useful for utilizing multi core (processor) systems.
     */
    protected class OneElectronIntegralEvaluaterThread 
              extends AbstractSimpleParallelTask {
        
        private int startBasisFunction, endBasisFunction;
        private ArrayList<ContractedGaussian> bfs;
        private double [][] overlap, hCore;
        private int [] atomicNumbers;

        public OneElectronIntegralEvaluaterThread(int [] atomicNumbers) {
            this.atomicNumbers = atomicNumbers;
        }
        
        public OneElectronIntegralEvaluaterThread(int startBasisFunction, 
                                           int endBasisFunction,
                                           double [][] overlap, 
                                           double [][] hCore,
                                           int [] atomicNumbers,
                                           ArrayList<ContractedGaussian> bfs) {
            this.startBasisFunction = startBasisFunction;
            this.endBasisFunction   = endBasisFunction;
        
            this.overlap = overlap;
            this.hCore   = hCore;
            this.atomicNumbers = atomicNumbers;
            
            this.bfs = bfs;

            setTaskName("OneElectronIntegralEvaluater Thread");
        }

        /** overridden run() */
        @Override
        public void run() {            
            compute1E(startBasisFunction, endBasisFunction,
                      overlap, hCore,
                      atomicNumbers, bfs);
        }

        /** Overridden init() */
        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new OneElectronIntegralEvaluaterThread(
                          startItem, endItem,
                          OneElectronIntegrals.this.overlap.getMatrix(),
                          OneElectronIntegrals.this.hCore.getMatrix(),
                          this.atomicNumbers,
                          basisFunctions.getBasisFunctions());
        }
    } // end of class OneElectronIntegralEvaluaterThread
    
} // emd of class OneElectronIntegrals
