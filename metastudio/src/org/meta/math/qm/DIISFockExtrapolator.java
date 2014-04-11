/**
 * DIISFockExtrapolator.java
 *
 * Created on 18/01/2010
 */

package org.meta.math.qm;

import java.util.ArrayList;
import org.meta.math.Matrix;
import org.meta.math.Vector;
import org.meta.math.la.GaussianElimination;
import org.meta.math.la.exception.SingularMatrixException;

/**
 * DIIS (Direct Inversion of ierative subspaces) proposed by Peter Pualay for
 * HF convergence acceleration.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DIISFockExtrapolator implements FockExtrapolator {

    private ArrayList<Fock> fockMatrixList;
    private ArrayList<Vector> errorMatrixList;

    private int diisStep = 0;

    private boolean diisStarted = false;

    private Fock oldFock = null;
    /** Initialize this interpolator */
    @Override
    public void init() {
        fockMatrixList  = new ArrayList<Fock>();
        errorMatrixList = new ArrayList<Vector>();

        diisStep = 0;

        errorThreshold = 0.1;
        diisStarted = false;

        oldFock = null;
    }

    /** Get the next exrapolated fock matrix
     *
     * @param currentFock the current fock
     * @param overlap the overlap matrix
     * @param density the current density matrix
     * @return exrapolated fock
     */
    @Override
    public Fock next(Fock currentFock, Overlap overlap, Density density) {
        Fock newFock = new Fock(((Matrix) currentFock.clone()).getMatrix());
        double [][] newFockMat = newFock.getMatrix();

        Matrix FPS = currentFock.mul(density).mul(overlap);
        Matrix SPF = overlap.mul(density).mul(currentFock);

        Vector errorMatrix = new Vector(FPS.sub(SPF));
        double mxerr = errorMatrix.maxNorm();

        if (mxerr < errorThreshold && !diisStarted) {
            System.out.println("Starting DIIS...");
            diisStarted = true;
        } // end if

        if (!diisStarted) {
            if (oldFock == null) {
                oldFock = currentFock;

                return currentFock;
            } else {
                newFockMat = oldFock.mul(0.5).add(currentFock.mul(0.5)).getMatrix();
                oldFock = currentFock;

                return newFock;
            } // end if
        } // end if
        
        errorMatrixList.add(errorMatrix);
        fockMatrixList.add(currentFock);

        newFock.makeZero();

        int noOfIterations = errorMatrixList.size();
        int N1 = noOfIterations + 1;

        Matrix A = new Matrix(N1);
        Vector B = new Vector(N1);

        double[][] aMatrix = A.getMatrix();
        double[] bVector = B.getVector();

        // set up A x = B to be solved
        for (int i = 0; i < noOfIterations; i++) {
            for (int j = 0; j < noOfIterations; j++) {
                aMatrix[i][j] = errorMatrixList.get(i).dot(errorMatrixList.get(j));
            } // end for
        } // end for

        for (int i = 0; i < noOfIterations; i++) {
            aMatrix[noOfIterations][i] = aMatrix[i][noOfIterations] = -1.0;
            bVector[i] = 0.0;
        } // end for

        aMatrix[noOfIterations][noOfIterations] = 0.0;
        bVector[noOfIterations] = -1.0;

        GaussianElimination gele = new GaussianElimination();
        gele.setMatrixA(A);
        gele.setVectorX(B);
        try {
            gele.findSolution();
            double[] solVec = gele.getVectorX().getVector();

            // System.out.println("Sol found: " + gele.getVectorX());

            for (int i = 0; i < noOfIterations; i++) {
                double[][] prevFockMat = fockMatrixList.get(i).getMatrix();
                for (int j = 0; j < newFockMat.length; j++) {
                    for (int k = 0; k < newFockMat.length; k++) {
                        newFockMat[j][k] += solVec[i] * prevFockMat[j][k];
                    } // end for
                } // end for
            } // end for

            oldFock = currentFock;
        } catch (SingularMatrixException ignored) {
            // System.out.println("No sol: " + diisStep);
            // ignored.printStackTrace();

            // no solution could be found, so return the current Fock as is
            diisStep++;
            return currentFock;
        } // end of try .. catch block

        diisStep++;

        return newFock;
    }

    protected double errorThreshold;

    /**
     * Get the value of errorThreshold
     *
     * @return the value of errorThreshold
     */
    public double getErrorThreshold() {
        return errorThreshold;
    }

    /**
     * Set the value of errorThreshold
     *
     * @param errorThreshold new value of errorThreshold
     */
    public void setErrorThreshold(double errorThreshold) {
        this.errorThreshold = errorThreshold;
    }
}
