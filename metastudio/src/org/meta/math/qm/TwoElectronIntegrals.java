/*
 * TwoElectronIntegrals.java
 *
 * Created on July 28, 2004, 7:03 AM
 */

package org.meta.math.qm;

import java.util.*;
import org.meta.math.geom.Point3D;
import org.meta.math.Vector3D;
import org.meta.math.qm.basis.ContractedGaussian;
import org.meta.math.qm.basis.Power;
import org.meta.math.qm.basis.PrimitiveGaussian;
import org.meta.math.qm.integral.Integrals;
import org.meta.math.qm.integral.IntegralsUtil;
import org.meta.molecule.Molecule;
import org.meta.parallel.AbstractSimpleParallelTask;
import org.meta.parallel.SimpleParallelTask;
import org.meta.parallel.SimpleParallelTaskExecuter;

/**
 * The 2E integral driver.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TwoElectronIntegrals {
    
    private BasisFunctions basisFunctions;

    private Molecule molecule;

    /**
     * Holds value of property twoEIntegrals.
     */
    private double[] twoEIntegrals;
    
    /** 
     * Creates a new instance of TwoElectronIntegrals 
     *
     * @param basisFunctions the basis functions to be used    
     */
    public TwoElectronIntegrals(BasisFunctions basisFunctions) {
        this(basisFunctions, false);
    }

    /**
     * Creates a new instance of TwoElectronIntegrals
     *
     * @param basisFunctions the basis functions to be used
     * @param onTheFly if true, the 2E integrals are not calculated and stored,
     *        they must be calculated individually by calling compute2E(i,j,k,l)
     */
    public TwoElectronIntegrals(BasisFunctions basisFunctions,
                                boolean onTheFly) {
        this.basisFunctions = basisFunctions;

        this.onTheFly = onTheFly;

        // compute the 2E integrals
        if (!onTheFly) {
            try {
                compute2E();  // try to do compute 2E incore
            } catch(OutOfMemoryError e) {
                // if no memory, resort to direct SCF
                System.err.println("No memory for in-core integral evaluation" +
                                   ". Switching to direct integral evaluation.");
                System.gc();
                this.onTheFly = true;
            } // end of try catch block
        } // end if
    }

    /**
     * Creates a new instance of TwoElectronIntegrals, and computes
     * two electron intergrals using shell-pair method rather than the
     * conventional loop over all basis functions approach. Note that, this
     * requires the Molecule object to be passed as an argument for it to
     * function correctly. Note that the Atom objects which are a part of
     * Molecule object must be specially configured to have a user defined
     * property called "basisFunctions" that is essentially a list of
     * ContractedGaussians that are centered on the atom. If this breaks, then
     * the code will silentely default to using the conventional way of
     * computing the two electron intergrals.
     *
     * @param molecule
     * @param onTheFly if true, the 2E integrals are not calculated and stored,
     *        they must be calculated individually by calling 
     *        compute2EShellPair(i,j,k,l)
     */
    public TwoElectronIntegrals(BasisFunctions basisFunctions,
                                Molecule molecule, boolean onTheFly) {
        this.basisFunctions = basisFunctions;
        this.molecule = molecule;
        
        this.onTheFly = onTheFly;

        // compute the 2E integrals
        if (!onTheFly) {
            try {
                // first try in - core method
                try {
                    compute2EShellPair();  // try to use shell pair algorithm
                } catch(Exception ignored) {
                    System.err.println("WARNING: Using a slower algorithm" +
                            " to compute two electron integrals. Error is: "
                            + ignored.toString());
                    ignored.printStackTrace();

                    compute2E(); // if it doesn't succeed then fall back to normal
                } // end if
            } catch(OutOfMemoryError e) {
                // if no memory, resort to direct SCF
                System.err.println("No memory for in-core integral evaluation" +
                                   ". Switching to direct integral evaluation.");
                System.gc();
                this.onTheFly = true;
            } // end if
        } // end if
    }

    /**
     * compute the 2E integrals, and store it in a single 1D array,
     * in the form [ijkl]. 
     *
     * This method has been modified to take advantage of multi core systems
     * where available.
     */
    protected void compute2E() {
        ArrayList<ContractedGaussian> bfs = basisFunctions.getBasisFunctions();
        
        // allocate required memory
        int noOfBasisFunctions = bfs.size();
        int noOfIntegrals = noOfBasisFunctions * (noOfBasisFunctions + 1)
                           * (noOfBasisFunctions * noOfBasisFunctions
                              + noOfBasisFunctions + 2) / 8;
        
        twoEIntegrals = new double[noOfIntegrals];
        
        SimpleParallelTaskExecuter pTaskExecuter
                                   = new SimpleParallelTaskExecuter();

        TwoElectronIntegralEvaluaterThread tThread
                   = new TwoElectronIntegralEvaluaterThread();
        tThread.setTaskName("TwoElectronIntegralEvaluater Thread");
        tThread.setTotalItems(noOfBasisFunctions);

        pTaskExecuter.execute(tThread);
    }        
    
    /**
     * Actually compute the 2E integrals
     */
    private void compute2E(int startBasisFunction, int endBasisFunction,
                           ArrayList<ContractedGaussian> bfs) {
        
        int i, j, k, l, ij, kl, ijkl;        
        int noOfBasisFunctions = bfs.size();
        
        ContractedGaussian bfi, bfj, bfk, bfl;
        
        // we only need i <= j, k <= l, and ij >= kl
        for(i=startBasisFunction; i<endBasisFunction; i++) {
            bfi = bfs.get(i);
            
            for(j=0; j<(i+1); j++) {
                bfj = bfs.get(j);
                ij = i * (i+1) / 2+j;
                
                for(k=0; k<noOfBasisFunctions; k++) {
                    bfk = bfs.get(k);
                    
                    for(l=0; l<(k+1); l++) {
                        bfl = bfs.get(l);
                        
                        kl = k * (k+1) / 2+l;
                        if (ij >= kl) {
                            ijkl = IntegralsUtil.ijkl2intindex(i, j, k, l);
                            
                            // record the 2E integrals
                            twoEIntegrals[ijkl] = Integrals.coulomb(bfi, bfj, 
                                                                    bfk, bfl);
                        } // end if
                    } // end l loop
                } // end k loop
            } // end of j loop
        } // end of i loop        
    }

    /**
     * Actually compute the 2E integrals derivatives
     */
    private void compute2EDerivative(int startBasisFunction, int endBasisFunction,
                                     ArrayList<ContractedGaussian> bfs) {

        int i, j, k, l, ij, kl, ijkl;
        int noOfBasisFunctions = bfs.size();

        ContractedGaussian bfi, bfj, bfk, bfl;

        double [] dxTwoE = twoEDer.get(0);
        double [] dyTwoE = twoEDer.get(1);
        double [] dzTwoE = twoEDer.get(2);

        // we only need i <= j, k <= l, and ij >= kl
        for(i=startBasisFunction; i<endBasisFunction; i++) {
            bfi = bfs.get(i);

            for(j=0; j<(i+1); j++) {
                bfj = bfs.get(j);
                ij = i * (i+1) / 2+j;

                for(k=0; k<noOfBasisFunctions; k++) {
                    bfk = bfs.get(k);

                    for(l=0; l<(k+1); l++) {
                        bfl = bfs.get(l);

                        kl = k * (k+1) / 2+l;
                        if (ij >= kl) {
                            ijkl = IntegralsUtil.ijkl2intindex(i, j, k, l);

                            // record derivative of the 2E integrals
                            Vector3D twoEDerEle
                               = compute2EDerivativeElement(bfi, bfj, bfk, bfl);

                            dxTwoE[ijkl] = twoEDerEle.getI();
                            dyTwoE[ijkl] = twoEDerEle.getJ();
                            dzTwoE[ijkl] = twoEDerEle.getK();
                        } // end if
                    } // end l loop
                } // end k loop
            } // end of j loop
        } // end of i loop
    }

    /** Compute a single 2E derivative element */
    private Vector3D compute2EDerivativeElement(ContractedGaussian bfi,
       ContractedGaussian bfj, ContractedGaussian bfk, ContractedGaussian bfl) {
        
        Vector3D twoEDerEle = new Vector3D();
        int [] paramIdx;
        Power currentPower;
        Point3D currentOrigin;
        double currentAlpha;

        if (bfi.getCenteredAtom().getIndex() == atomIndex) {
            paramIdx = new int[]{4, 1, 2, 3};
            for(PrimitiveGaussian iPG : bfi.getPrimitives()) {
                currentOrigin = iPG.getOrigin();
                currentPower  = iPG.getPowers();
                currentAlpha  = iPG.getExponent();
                for(PrimitiveGaussian jPG : bfj.getPrimitives()) {
                    for(PrimitiveGaussian kPG : bfk.getPrimitives()) {
                        for(PrimitiveGaussian lPG : bfl.getPrimitives()) {
                            compute2EDerivativeElementHelper(iPG, jPG,
                                   kPG, lPG, currentOrigin, currentPower,
                                   currentAlpha, paramIdx, twoEDerEle);
                        } // end for
                    } // end for
                } // end for
            } // end for
        } // end if

        if (bfj.getCenteredAtom().getIndex() == atomIndex) {
            paramIdx = new int[]{0, 4, 2, 3};
            for(PrimitiveGaussian iPG : bfi.getPrimitives()) {
                for(PrimitiveGaussian jPG : bfj.getPrimitives()) {
                    currentOrigin = jPG.getOrigin();
                    currentPower  = jPG.getPowers();
                    currentAlpha  = jPG.getExponent();
                    for(PrimitiveGaussian kPG : bfk.getPrimitives()) {
                        for(PrimitiveGaussian lPG : bfl.getPrimitives()) {
                            compute2EDerivativeElementHelper(iPG, jPG,
                                   kPG, lPG, currentOrigin, currentPower,
                                   currentAlpha, paramIdx, twoEDerEle);
                        } // end for
                    } // end for
                } // end for
            } // end for
        } // end if

        if (bfk.getCenteredAtom().getIndex() == atomIndex) {
            paramIdx = new int[]{0, 1, 4, 3};
            for(PrimitiveGaussian iPG : bfi.getPrimitives()) {
                for(PrimitiveGaussian jPG : bfj.getPrimitives()) {
                    for(PrimitiveGaussian kPG : bfk.getPrimitives()) {
                        currentOrigin = kPG.getOrigin();
                        currentPower  = kPG.getPowers();
                        currentAlpha  = kPG.getExponent();
                        for(PrimitiveGaussian lPG : bfl.getPrimitives()) {
                            compute2EDerivativeElementHelper(iPG, jPG,
                                   kPG, lPG, currentOrigin, currentPower,
                                   currentAlpha, paramIdx, twoEDerEle);
                        } // end for
                    } // end for
                } // end for
            } // end for
        } // end if

        if (bfl.getCenteredAtom().getIndex() == atomIndex) {
            paramIdx = new int[]{0, 1, 2, 4};
            for(PrimitiveGaussian iPG : bfi.getPrimitives()) {
                for(PrimitiveGaussian jPG : bfj.getPrimitives()) {
                    for(PrimitiveGaussian kPG : bfk.getPrimitives()) {
                        for(PrimitiveGaussian lPG : bfl.getPrimitives()) {
                            currentOrigin = lPG.getOrigin();
                            currentPower  = lPG.getPowers();
                            currentAlpha  = lPG.getExponent();

                            compute2EDerivativeElementHelper(iPG, jPG,
                                   kPG, lPG, currentOrigin, currentPower,
                                   currentAlpha, paramIdx, twoEDerEle);
                        } // end for
                    } // end for
                } // end for
            } // end for
        } // end if

        return twoEDerEle;
    }

    /** helper for computing 2E derivative */
    private void compute2EDerivativeElementHelper(PrimitiveGaussian iPG,
       PrimitiveGaussian jPG, PrimitiveGaussian kPG, PrimitiveGaussian lPG,
       Point3D currentOrigin, Power currentPower, double currentAlpha,
       int [] paramIdx, Vector3D derEle) {

        int l = currentPower.getL(),
            m = currentPower.getM(),
            n = currentPower.getN();
        
        double coeff = iPG.getCoefficient()*jPG.getCoefficient()
                       *kPG.getCoefficient()*lPG.getCoefficient();

        PrimitiveGaussian xPG = new PrimitiveGaussian(currentOrigin,
                                      new Power(l+1,m,n), currentAlpha, coeff);
        xPG.normalize();

        PrimitiveGaussian [] pgs 
                = new PrimitiveGaussian[]{iPG, jPG, kPG, lPG, xPG};
        
        double terma = Math.sqrt(currentAlpha*(2.0*l+1.0))*coeff*
                        Integrals.coulomb(pgs[paramIdx[0]], pgs[paramIdx[1]],
                                          pgs[paramIdx[2]], pgs[paramIdx[3]]),
               termb = 0.0;

        if (l>0) {
            xPG.setPowers(new Power(l-1,m,n));
            xPG.normalize();
            termb = -2.0*l*Math.sqrt(currentAlpha/(2.*l-1))*coeff*
                     Integrals.coulomb(pgs[paramIdx[0]], pgs[paramIdx[1]],
                                       pgs[paramIdx[2]], pgs[paramIdx[3]]);
        } // end if

        derEle.setI(derEle.getI() + terma + termb); // x component

        if (m>0) {
            xPG.setPowers(new Power(l,m-1,n));
            xPG.normalize();
            termb = -2.0*m*Math.sqrt(currentAlpha/(2.*m-1))*coeff*
                     Integrals.coulomb(pgs[paramIdx[0]], pgs[paramIdx[1]],
                                       pgs[paramIdx[2]], pgs[paramIdx[3]]);
        } // end if

        derEle.setJ(derEle.getJ() + terma + termb); // y component

        if (n>0) {
            xPG.setPowers(new Power(l,m,n-1));
            xPG.normalize();
            termb = -2.0*n*Math.sqrt(currentAlpha/(2.*n-1))*coeff*
                     Integrals.coulomb(pgs[paramIdx[0]], pgs[paramIdx[1]],
                                       pgs[paramIdx[2]], pgs[paramIdx[3]]);
        } // end if

        derEle.setK(derEle.getK() + terma + termb); // z component
    }

    /**
     * compute the 2E integrals using shell pair based method, and store it in
     * a single 1D array, in the form [ijkl].
     *
     * This method has been modified to take advantage of multi core systems
     * where available.
     */
    protected void compute2EShellPair() {
        // TODO : parallel

        ArrayList<ContractedGaussian> bfs = basisFunctions.getBasisFunctions();

        // allocate required memory
        int noOfBasisFunctions = bfs.size();
        int noOfIntegrals = noOfBasisFunctions * (noOfBasisFunctions + 1)
                           * (noOfBasisFunctions * noOfBasisFunctions
                              + noOfBasisFunctions + 2) / 8;

        twoEIntegrals = new double[noOfIntegrals];
        
        int noOfAtoms = molecule.getNumberOfAtoms();
        int a, b, c, d;
        int i, j, k, l;
        int naFunc, nbFunc, ncFunc, ndFunc, twoEIndx;
        ArrayList<ContractedGaussian> aFunc, bFunc, cFunc, dFunc;
        ContractedGaussian iaFunc, jbFunc, kcFunc, ldFunc;

        // center a
        for(a=0; a<noOfAtoms; a++) {
            aFunc = (ArrayList<ContractedGaussian>) molecule.getAtom(a)
                      .getUserDefinedAtomProperty("basisFunctions").getValue();
            naFunc = aFunc.size(); 
            // basis functions on a
            for(i=0; i<naFunc; i++) {
                iaFunc = aFunc.get(i);
                
                // center b
                for(b=0; b<=a; b++) {
                    bFunc = (ArrayList<ContractedGaussian>) molecule.getAtom(b)
                      .getUserDefinedAtomProperty("basisFunctions").getValue();
                    nbFunc = (b<a) ? bFunc.size() : i+1;
                    // basis functions on b
                    for(j=0; j<nbFunc; j++) {
                        jbFunc = bFunc.get(j);

                        // center c
                        for(c=0; c<noOfAtoms; c++) {
                            cFunc = (ArrayList<ContractedGaussian>)
                              molecule.getAtom(c).getUserDefinedAtomProperty(
                                                   "basisFunctions").getValue();
                            ncFunc = cFunc.size();
                            // basis functions on c
                            for(k=0; k<ncFunc; k++) {
                                kcFunc = cFunc.get(k);

                                // center d
                                for(d=0; d<=c; d++) {
                                    dFunc = (ArrayList<ContractedGaussian>)
                                        molecule.getAtom(d)
                                            .getUserDefinedAtomProperty(
                                                   "basisFunctions").getValue();
                                    ndFunc = (d<c) ? dFunc.size() : k+1;
                                    // basis functions on d
                                    for(l=0; l<ndFunc; l++) {
                                        ldFunc = dFunc.get(l);

                                        twoEIndx = IntegralsUtil.ijkl2intindex(
                                                           iaFunc.getIndex(),
                                                           jbFunc.getIndex(),
                                                           kcFunc.getIndex(),
                                                           ldFunc.getIndex());
                                        
                                        twoEIntegrals[twoEIndx] =
                                                compute2E(iaFunc, jbFunc,
                                                          kcFunc, ldFunc);
                                    } // end for (l)
                                } // end for (d)
                            } // end for (k)
                        } // end for (c)
                    }  // end for (j)
                } // end for (b)
            } // end for (i)
        } // end for (a)
    }
    
    /**
     *
     * Compute an integral centered at <ij|kl>
     * 
     * @return the value of two integral electron
     */
    public double compute2E(int i, int j, int k, int l) {
        // if we really need to compute, go ahead!
        ArrayList<ContractedGaussian> bfs = basisFunctions.getBasisFunctions();

        return Integrals.coulomb(bfs.get(i), bfs.get(j),
                                 bfs.get(k), bfs.get(l));
    }

    /**
     *
     * Compute an integral centered at <ij|kl>
     *
     * @return the value of two integral electron
     */
    public double compute2E(ContractedGaussian cgi, ContractedGaussian cgj,
                            ContractedGaussian cgk, ContractedGaussian cgl) {
        return Integrals.coulomb(cgi, cgj, cgk, cgl);
    }
    
    /**
     * Getter for property twoEIntegrals.
     * @return Value of property twoEIntegrals.
     */
    public double[] getTwoEIntegrals() {
        return this.twoEIntegrals;
    }
    
    /**
     * Setter for property twoEIntegrals.
     * @param twoEIntegrals New value of property twoEIntegrals.
     */
    public void setTwoEIntegrals(double[] twoEIntegrals) {
        this.twoEIntegrals = twoEIntegrals;
    }    

    private int atomIndex;
    private SCFMethod scfMethod;
    
    /**
     * Return the derivatives of the two electron integrals.
     *
     * @param atomIndex the reference atom index
     * @param scfMethod the reference SCF method
     */
    public void compute2EDerivatives(int atomIndex, SCFMethod scfMethod) {
        this.atomIndex = atomIndex;
        this.scfMethod = scfMethod;
        
        twoEDer = new ArrayList<double []>();
        
        double [] dxTwoE = new double[twoEIntegrals.length];
        double [] dyTwoE = new double[twoEIntegrals.length];
        double [] dzTwoE = new double[twoEIntegrals.length];

        twoEDer.add(dxTwoE);
        twoEDer.add(dyTwoE);
        twoEDer.add(dzTwoE);

        SimpleParallelTaskExecuter pTaskExecuter
                                   = new SimpleParallelTaskExecuter();

        TwoElectronIntegralDerivativeEvaluaterThread tThread
                   = new TwoElectronIntegralDerivativeEvaluaterThread();
        tThread.setTaskName("TwoElectronIntegralDerivativeEvaluater Thread");
        tThread.setTotalItems(basisFunctions.getBasisFunctions().size());

        pTaskExecuter.execute(tThread);
    }

    protected ArrayList<double []> twoEDer;

    /**
     * The currently computed list of two electron derivatives
     *
     * @return partial derivatives of 2E integrals, computed in previous call
     *         to compute2EDerivatives()
     */
    public ArrayList<double []> getTwoEDer() {
        return twoEDer;
    }
    
    /**
     * Class encapsulating the way to compute 2E electrons in a way 
     * useful for utilizing multi core (processor) systems.
     */
    protected class TwoElectronIntegralEvaluaterThread 
              extends AbstractSimpleParallelTask {
        
        private int startBasisFunction, endBasisFunction;
        private ArrayList<ContractedGaussian> bfs;

        public TwoElectronIntegralEvaluaterThread() { }
        
        public TwoElectronIntegralEvaluaterThread(int startBasisFunction, 
                                           int endBasisFunction,
                                           ArrayList<ContractedGaussian> bfs) {
            this.startBasisFunction = startBasisFunction;
            this.endBasisFunction   = endBasisFunction;
            
            this.bfs = bfs;
            
            setTaskName("TwoElectronIntegralEvaluater Thread");
        }

        /**
         * Overridden run()
         */
        @Override
        public void run() {
            compute2E(startBasisFunction, endBasisFunction, bfs);
        }

        /** Overridden init() */
        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new TwoElectronIntegralEvaluaterThread(
                          startItem, endItem,
                          basisFunctions.getBasisFunctions());
        }
    } // end of class TwoElectronIntegralEvaluaterThread

    /**
     * Class encapsulating the way to compute 2E electrons in a way
     * useful for utilizing multi core (processor) systems.
     */
    protected class TwoElectronIntegralDerivativeEvaluaterThread
              extends AbstractSimpleParallelTask {

        private int startBasisFunction, endBasisFunction;
        private ArrayList<ContractedGaussian> bfs;

        public TwoElectronIntegralDerivativeEvaluaterThread() { }

        public TwoElectronIntegralDerivativeEvaluaterThread(
                                           int startBasisFunction,
                                           int endBasisFunction,
                                           ArrayList<ContractedGaussian> bfs) {
            this.startBasisFunction = startBasisFunction;
            this.endBasisFunction   = endBasisFunction;

            this.bfs = bfs;
            
            setTaskName("TwoElectronIntegralDerivativeEvaluater Thread");
        }

        /**
         * Overridden run()
         */
        @Override
        public void run() {
            compute2EDerivative(startBasisFunction, endBasisFunction, bfs);
        }

        /** Overridden init() */
        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new TwoElectronIntegralDerivativeEvaluaterThread(
                          startItem, endItem,
                          basisFunctions.getBasisFunctions());
        }
    } // end of class TwoElectronIntegralEvaluaterThread
    
    /** Calculate integrals on the fly instead of storing them in an array */
    protected boolean onTheFly;

    /**
     * Get the value of onTheFly
     *
     * @return the value of onTheFly
     */
    public boolean isOnTheFly() {
        return onTheFly;
    }

    /**
     * Set the value of onTheFly
     *
     * @param onTheFly new value of onTheFly
     */
    public void setOnTheFly(boolean onTheFly) {
        this.onTheFly = onTheFly;
    }

} // end of class TwoElectronIntegrals
