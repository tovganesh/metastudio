/**
 * Overlap.java
 *
 * Created on Apr 16, 2009
 */
package org.meta.math.qm;

import java.util.ArrayList;
import org.meta.math.Matrix;
import org.meta.math.Vector3D;
import org.meta.math.qm.basis.ContractedGaussian;

/**
 * Represents the Overlap matrix.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Overlap extends Matrix {

    /**
     * Creates a new instance of NxM Matrix
     *
     * @param n the first dimension
     * @param m the second dimension
     */
    public Overlap(int n, int m) {
        super(n, m);
    }

    /**
     * Creates a new instance of square (NxN) Matrix
     *
     * @param n the dimension
     */
    public Overlap(int n) {
        super(n, n);
    }

    /**
     * Creates a new instance of Matrix, based on already allocated 2D array
     *
     * @param a the 2D array
     */
    public Overlap(double [][] a) {
        super(a);
    }

    private Matrix sHalf = null;

    /**
     * Get the S^1/2 matrix
     *
     * @return return the S half matrix
     */
    public Matrix getSHalf() {
        if (sHalf == null) sHalf = this.symmetricOrthogonalization();

        return sHalf;
    }

    private SCFMethod scfMethod;
    private int atomIndex;
    
    /**
     * Compute Overlap partial derivative for an atom index.
     *
     * @param atomIndex the atom index with respect to which the derivative are
     *                  to be evaluated
     * @param scfMethod the reference to the SCFMethod
     * @return three element array of Overlap elements represeting partial
     *               derivatives with respect to x, y and z of atom position
     */
    public ArrayList<Overlap> computeDerivative(int atomIndex, SCFMethod scfMethod) {
        this.scfMethod = scfMethod;
        this.atomIndex = atomIndex;
        
        ArrayList<Overlap> dOverlap = new ArrayList<Overlap>(3);

        int noOfBasisFunctions = this.getRowCount();
        Overlap dOverlapDx = new Overlap(noOfBasisFunctions);
        Overlap dOverlapDy = new Overlap(noOfBasisFunctions);
        Overlap dOverlapDz = new Overlap(noOfBasisFunctions);

        double [][] hdx = dOverlapDx.getMatrix();
        double [][] hdy = dOverlapDy.getMatrix();
        double [][] hdz = dOverlapDz.getMatrix();

        int i, j;
        for(i=0; i<noOfBasisFunctions; i++) {
            for(j=0; j<noOfBasisFunctions; j++) {
                Vector3D dOvrEle = computeOverlapDerElement(i, j);

                hdx[i][j] = dOvrEle.getI();
                hdy[i][j] = dOvrEle.getJ();
                hdz[i][j] = dOvrEle.getK();
            } // end for
        } // end for

        dOverlap.add(dOverlapDx);
        dOverlap.add(dOverlapDy);
        dOverlap.add(dOverlapDz);

        return dOverlap;
    }

    /** Compute one of the Overlap derivative elements, with respect to an atomIndex */
    private Vector3D computeOverlapDerElement(int i, int j) {
        BasisFunctions bfs = scfMethod.getOneEI().getBasisFunctions();
        ContractedGaussian cgi = bfs.getBasisFunctions().get(i);
        ContractedGaussian cgj = bfs.getBasisFunctions().get(j);

        return cgi.overlapDerivative(atomIndex, cgj);
    }
}
