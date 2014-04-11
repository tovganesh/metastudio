/**
 * Fock.java
 *
 * Created on Apr 16, 2009
 */
package org.meta.math.qm;

import org.meta.math.Matrix;

/**
 * Represents the Fock matrix.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Fock extends Matrix {

    /**
     * Creates a new instance of NxM Matrix
     *
     * @param n the first dimension
     * @param m the second dimension
     */
    public Fock(int n, int m) {
        super(n, m);
    }

    /**
     * Creates a new instance of square (NxN) Matrix
     *
     * @param n the dimension
     */
    public Fock(int n) {
        super(n, n);
    }

    /**
     * Creates a new instance of Matrix, based on already allocated 2D array
     *
     * @param a the 2D array
     */
    public Fock(double [][] a) {
        super(a);
    }

    /**
     * Form the Fock matrix from the HCore and the GMatrix
     *
     * @param hCore the HCore matrix
     * @param gMatrix the GMatrix
     */
    public void compute(HCore hCore, GMatrix gMatrix) {
        setMatrix(hCore.add(gMatrix).getMatrix());
    }
}
