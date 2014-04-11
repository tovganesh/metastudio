/**
 * GMatrix.java
 *
 * Created on Apr 16, 2009
 */
package org.meta.math.qm;

import java.util.ArrayList;
import org.meta.math.Matrix;
import org.meta.math.Vector;
import org.meta.math.qm.integral.IntegralsUtil;
import org.meta.parallel.AbstractSimpleParallelTask;
import org.meta.parallel.SimpleParallelTask;
import org.meta.parallel.SimpleParallelTaskExecuter;

/**
 * Represents the G Matrix used to form the Fock matrix
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GMatrix extends Matrix {

    /**
     * Creates a new instance of NxM Matrix
     *
     * @param n the first dimension
     * @param m the second dimension
     */
    public GMatrix(int n, int m) {
        super(n, m);
    }

    /**
     * Creates a new instance of square (NxN) Matrix
     *
     * @param n the dimension
     */
    public GMatrix(int n) {
        super(n, n);
    }

    /**
     * Creates a new instance of Matrix, based on already allocated 2D array
     *
     * @param a the 2D array
     */
    public GMatrix(double [][] a) {
        super(a);
    }

    private TwoElectronIntegrals twoEI;
    private Density density;
    
    /**
     * Form the GMatrix from two electron integrals and the density matrix.
     * 
     * @param scfType the SCFType
     * @param twoEI the 2E integrals
     * @param density the Density matrix
     */
    public void compute(SCFType scfType, TwoElectronIntegrals twoEI,
                        Density density) {
        this.twoEI   = twoEI;
        this.density = density;

        if (scfType == SCFType.HARTREE_FOCK_DIRECT) 
            makeGMatrixDirect();
        else 
            makeGMatrix();
    }

    /**
     * Make the G matrix <br>
     * i.e. Form the 2J-K integrals corresponding to a density matrix
     */
    protected void makeGMatrix() {
        // make sure if this is really the case
        // just if in case TwoElectronIntegrals class decided other wise
        if (twoEI.isOnTheFly()) {
            makeGMatrixDirect();
            return;
        } // end if

        int noOfBasisFunctions = density.getRowCount();
        Matrix theGMatrix  = this;
        Vector densityOneD = new Vector(density); // form 1D vector of density
        Vector tempVector  = new Vector(noOfBasisFunctions*noOfBasisFunctions);

        double [][] gMatrix = theGMatrix.getMatrix();
        double [] ints = twoEI.getTwoEIntegrals();
        double [] temp = tempVector.getVector();

        int i, j, k, l, kl, indexJ, indexK1, indexK2;
        for(i=0; i<noOfBasisFunctions; i++) {
            for(j=0; j<i+1; j++) {

                tempVector.makeZero();
                kl = 0;

                for(k=0; k<noOfBasisFunctions; k++) {
                    for(l=0; l<noOfBasisFunctions; l++) {
                        indexJ   = IntegralsUtil.ijkl2intindex(i, j, k, l);
                        indexK1  = IntegralsUtil.ijkl2intindex(i, k, j, l);
                        indexK2  = IntegralsUtil.ijkl2intindex(i, l, k, j);
                        temp[kl] = 2.0*ints[indexJ] - 0.5*ints[indexK1]
                                   - 0.5*ints[indexK2];
                        kl++;
                    } // end l loop
                } // end k loop

                gMatrix[i][j] = gMatrix[j][i] = tempVector.dot(densityOneD);
            } // end j loop
        } // end i loop
    }

    /**
     * Make the G matrix <br>
     * i.e. Form the 2J-K integrals corresponding to a density matrix
     *
     * This computes integrals on the fly rather than read in from
     * a precalculated storage.
     */
    protected void makeGMatrixDirect() {
        SimpleParallelTaskExecuter pTaskExecuter
                                   = new SimpleParallelTaskExecuter();

        // allocate memory for partial GMatrices
        partialGMatrixList = new ArrayList<GMatrix>();

        // start the threads
        GMatrixFormationThread tThread
                   = new GMatrixFormationThread();
        tThread.setTaskName("GMatrixFormationThread Thread");
        tThread.setTotalItems(density.getRowCount());

        pTaskExecuter.execute(tThread);

        if (partialGMatrixList.size() > 0) {
            // collect the result and sum the partial contributions
            this.makeZero();
            double [][] gMatrix = this.getMatrix();
            int N = this.getRowCount();

            // sum up the partial results
            for(GMatrix pgMat : partialGMatrixList) {
                double [][] pgm = pgMat.getMatrix();

                for(int i=0; i<N; i++) {
                    for(int j=0; j<N; j++) {
                        gMatrix[i][j] += pgm[i][j];
                    } // end for
                } // end for
            } // end for

            // half the elements
            for(int i=0; i<N; i++) {
                for(int j=0; j<N; j++) {
                    gMatrix[i][j] *= 0.5;
                } // end for
            } // end for
        } // end if

        // enable garbage collection
        partialGMatrixList.clear();
        partialGMatrixList = null;
        System.gc();
    }

    /** function to facilitate mulithreaded direct formation of GMatrix */
    private void makeGMatrixDirectOrg(int startBasisFunction, int endBasisFunction) {
        int noOfBasisFunctions = density.getRowCount();
        Matrix theGMatrix  = this;
        Vector densityOneD = new Vector(density); // form 1D vector of density
        Vector tempVector  = new Vector(noOfBasisFunctions*noOfBasisFunctions);

        double [][] gMatrix = theGMatrix.getMatrix();
        double [] temp = tempVector.getVector();

        double twoEIntVal1, twoEIntVal2, twoEIntVal3;

        int i, j, k, l, kl, indexJ, indexK1, indexK2;
        for(i=startBasisFunction; i<endBasisFunction; i++) {
            for(j=0; j<i+1; j++) {

                tempVector.makeZero();
                kl = 0;

                for(k=0; k<noOfBasisFunctions; k++) {
                    for(l=0; l<noOfBasisFunctions; l++) {
                        indexJ   = IntegralsUtil.ijkl2intindex(i, j, k, l);
                        indexK1  = IntegralsUtil.ijkl2intindex(i, k, j, l);
                        indexK2  = IntegralsUtil.ijkl2intindex(i, l, k, j);

                        twoEIntVal1 = twoEI.compute2E(i,j,k,l);
                        if (indexJ == indexK1) twoEIntVal2 = twoEIntVal1;
                        else                   twoEIntVal2 = twoEI.compute2E(i,k,j,l);

                        if (indexJ == indexK2)       twoEIntVal3 = twoEIntVal1;
                        else if (indexK1 == indexK2) twoEIntVal3 = twoEIntVal2;
                        else                         twoEIntVal3 = twoEI.compute2E(i,l,k,j);

                        temp[kl] = 2.0*twoEIntVal1 - 0.5*twoEIntVal2 - 0.5*twoEIntVal3;

                        kl++;
                    } // end l loop
                } // end k loop

                gMatrix[i][j] = gMatrix[j][i] = tempVector.dot(densityOneD);
            } // end j loop
        } // end i loop
    }

    private ArrayList<GMatrix> partialGMatrixList;
    
    /** function to facilitate mulithreaded direct formation of GMatrix */
    private void makeGMatrixDirect(int startBasisFunction, int endBasisFunction) {
        int noOfBasisFunctions = density.getRowCount();
        GMatrix theGMatrix  = new GMatrix(noOfBasisFunctions);
        theGMatrix.makeZero();
        
        double [][] gMatrix = theGMatrix.getMatrix();
        double [][] dMatrix = density.getMatrix();
        
        int i, j, k, l, m, ij, kl;
        int [] idx, jdx, kdx, ldx;
        idx = new int[8];
        jdx = new int[8];
        kdx = new int[8];
        ldx = new int[8];
        boolean [] validIdx = new boolean[8];
        validIdx[0] = true;
        
        double twoEIntVal, twoEIntVal2, twoEIntValHalf;
        for(i=startBasisFunction; i<endBasisFunction; i++) {
            idx[0] = i; jdx[1] = i; jdx[2] = i; idx[3] = i;
            kdx[4] = i; ldx[5] = i; kdx[6] = i; ldx[7] = i;
            for(j=0; j<(i+1); j++) {
                ij = i * (i+1) / 2+j;
                jdx[0] = j; idx[1] = j; idx[2] = j; jdx[3] = j;
                ldx[4] = j; kdx[5] = j; ldx[6] = j; kdx[7] = j;
                for(k=0; k<noOfBasisFunctions; k++) {
                    kdx[0] = k; kdx[1] = k; ldx[2] = k; ldx[3] = k;
                    jdx[4] = k; jdx[5] = k; idx[6] = k; idx[7] = k;
                    for(l=0; l<(k+1); l++) {
                        kl = k * (k+1) / 2+l;
                        if (ij >= kl) {
                            twoEIntVal     = twoEI.compute2E(i, j, k, l);
                            twoEIntVal2    = twoEIntVal + twoEIntVal;
                            twoEIntValHalf = 0.5 * twoEIntVal;

                            setGMatrixElements(gMatrix, dMatrix, i, j, k, l,
                                               twoEIntVal2, twoEIntValHalf);

                            // special case
                            if ((i|j|k|l) == 0) continue;

                            // else this is symmetry unique integral, so need to
                            // use this value for all 8 combinations
                            // (if unique)
                            ldx[0] = l; ldx[1] = l; kdx[2] = l; kdx[3] = l;
                            idx[4] = l; idx[5] = l; jdx[6] = l; jdx[7] = l;
                            validIdx[1] = true; validIdx[2] = true;
                            validIdx[3] = true; validIdx[4] = true;
                            validIdx[5] = true; validIdx[6] = true;
                            validIdx[7] = true;

                            // filter unique elements
                            filterUniqueElements(idx, jdx, kdx, ldx, validIdx);

                            // and evaluate them
                            for(m=1; m<8; m++) {
                                if (validIdx[m]) {
                                    setGMatrixElements(gMatrix, dMatrix, 
                                               idx[m], jdx[m], kdx[m], ldx[m],
                                               twoEIntVal2, twoEIntValHalf);
                                } // end if
                            } // end for
                        } // end if
                    } // end l loop
                } // end k loop
            } // end j loop
        } // end i loop

        partialGMatrixList.add(theGMatrix);
    }
    
    /** find unique elements and mark the onces that are not */
    private void filterUniqueElements(int [] idx, int [] jdx,
                                      int [] kdx, int [] ldx,
                                      boolean [] validIdx) {
        int i, j, k, l, m, n;
        
        for(m=0; m<8; m++) {
            i = idx[m]; j = jdx[m]; k = kdx[m]; l = ldx[m];
            for(n=m+1; n<8; n++) {
                if (i==idx[n] && j==jdx[n] && k==kdx[n] && l==ldx[n])
                    validIdx[n] = false;
            } // end for
        } // end for
    }

    /** Set the GMatrix value for a given combination */
    private void setGMatrixElements(double [][] gMatrix, double [][] dMatrix,
                                    int i, int j, int k, int l,
                                    double twoEIntVal2, double twoEIntValHalf) {
        gMatrix[i][j] += dMatrix[k][l] * twoEIntVal2;
        gMatrix[k][l] += dMatrix[i][j] * twoEIntVal2;
        gMatrix[i][k] -= dMatrix[j][l] * twoEIntValHalf;
        gMatrix[i][l] -= dMatrix[j][k] * twoEIntValHalf;
        gMatrix[j][k] -= dMatrix[i][l] * twoEIntValHalf;
        gMatrix[j][l] -= dMatrix[i][k] * twoEIntValHalf;
    }

    /**
     * Compute GMatrix partial derivative for an atom index.
     *
     * @param atomIndex the atom index with respect to which the derivative are
     *                  to be evaluated
     * @param scfMethod the reference to the SCFMethod
     * @return three element array of GMatrix elements represeting partial
     *               derivatives with respect to x, y and z of atom position
     */
    public ArrayList<GMatrix> computeDerivative(int atomIndex, SCFMethod scfMethod) {
        ArrayList<GMatrix> gDer = new ArrayList<GMatrix>(3);
        
        scfMethod.getTwoEI().compute2EDerivatives(atomIndex, scfMethod);
        ArrayList<double []> twoEDers = scfMethod.getTwoEI().getTwoEDer();
        double [] d2IntsDxa = twoEDers.get(0);
        double [] d2IntsDya = twoEDers.get(1);
        double [] d2IntsDza = twoEDers.get(2);

        density = scfMethod.getDensity();
        int noOfBasisFunctions = density.getRowCount();
        Vector densityOneD = new Vector(density); // form 1D vector of density

        GMatrix gdx = new GMatrix(noOfBasisFunctions);
        GMatrix gdy = new GMatrix(noOfBasisFunctions);
        GMatrix gdz = new GMatrix(noOfBasisFunctions);

        Vector xvec = new Vector(noOfBasisFunctions*noOfBasisFunctions);
        Vector yvec = new Vector(noOfBasisFunctions*noOfBasisFunctions);
        Vector zvec = new Vector(noOfBasisFunctions*noOfBasisFunctions);

        int i, j, k, l, kl, indexJ, indexK1, indexK2;
        for(i=0; i<noOfBasisFunctions; i++) {
            for(j=0; j<i+1; j++) {
                kl = 0;
                xvec.makeZero();
                yvec.makeZero();
                zvec.makeZero();
                double [] xtemp = xvec.getVector();
                double [] ytemp = xvec.getVector();
                double [] ztemp = xvec.getVector();
                for(k=0; k<noOfBasisFunctions; k++) {
                    for(l=0; l<noOfBasisFunctions; l++) {
                        indexJ  = IntegralsUtil.ijkl2intindex(i,j,k,l);
                        indexK1 = IntegralsUtil.ijkl2intindex(i,k,j,l);
                        indexK2 = IntegralsUtil.ijkl2intindex(i,l,k,j);

                        xtemp[kl] = 2.*d2IntsDxa[indexJ]-0.5*d2IntsDxa[indexK1]
                                    -0.5*d2IntsDxa[indexK2];
                        ytemp[kl] = 2.*d2IntsDya[indexJ]-0.5*d2IntsDya[indexK1]
                                    -0.5*d2IntsDya[indexK2];
                        ztemp[kl] = 2.*d2IntsDza[indexJ]-0.5*d2IntsDza[indexK1]
                                    -0.5*d2IntsDza[indexK2];
                        kl++;
                    } // end for
                } // end for

                gdx.matrix[i][j] = gdx.matrix[j][i] = xvec.dot(densityOneD);
                gdy.matrix[i][j] = gdy.matrix[j][i] = yvec.dot(densityOneD);
                gdz.matrix[i][j] = gdz.matrix[j][i] = zvec.dot(densityOneD);
            } // end for
        } // end for
        
        gDer.add(gdx);
        gDer.add(gdy);
        gDer.add(gdz);
        
        return gDer;
    }

    /**
     * Class encapsulating the way for forming GMatrix  in a way
     * useful for utilizing multi core (processor) systems.
     */
    protected class GMatrixFormationThread
              extends AbstractSimpleParallelTask {

        private int startBasisFunction, endBasisFunction;

        public GMatrixFormationThread() { }
        
        public GMatrixFormationThread(int startBasisFunction,
                                           int endBasisFunction) {
            this.startBasisFunction = startBasisFunction;
            this.endBasisFunction   = endBasisFunction;
            
            setTaskName("GMatrixFormationThread Thread");
        }

        /**
         * Overridden run()
         */
        @Override
        public void run() {
            makeGMatrixDirect(startBasisFunction, endBasisFunction);
        }

        /** Overridden init() */
        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new GMatrixFormationThread(startItem, endItem);
        }
    }
}
