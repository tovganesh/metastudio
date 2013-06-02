/**
 * MolecularOrbitals.java
 *
 * Created on Apr 16, 2009
 */
package org.meta.math.qm;

import org.meta.math.Matrix;
import org.meta.math.la.Diagonalizer;
import org.meta.math.la.DiagonalizerFactory;

/**
 * Represents the Moleulear orbitals as coefficient matrix and the corresponding
 * eigenvalues representing the orbital energies.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MolecularOrbitals extends Matrix {

    /**
     * Creates a new instance of NxM Matrix
     *
     * @param n the first dimension
     * @param m the second dimension
     */
    public MolecularOrbitals(int n, int m) {
        super(n, m);
    }

    /**
     * Creates a new instance of square (NxN) Matrix
     *
     * @param n the dimension
     */
    public MolecularOrbitals(int n) {
        super(n, n);
    }

    /**
     * Creates a new instance of Matrix, based on already allocated 2D array
     *
     * @param a the 2D array
     */
    public MolecularOrbitals(double [][] a) {
        super(a);
    }

    /**
     * Get the coefficient Matrix for this MolecularOrbitals
     * @return the coefficient matrix
     */
    public double [][] getCoefficients() {
        return getMatrix();
    }

    protected double[] orbitalEnergies;

    /**
     * Get the value of orbitalEnergies
     *
     * @return the value of orbitalEnergies
     */
    public double[] getOrbitalEnergies() {
        return orbitalEnergies;
    }

    /**
     * Set the value of orbitalEnergies
     *
     * @param orbitalEnergies new value of orbitalEnergies
     */
    public void setOrbitalEnergies(double[] orbitalEnergies) {
        this.orbitalEnergies = orbitalEnergies;
    }
    
    /**
     * Compute the MO coefficients and the orbital energies
     *
     * @param hCore the HCore matrix
     * @param overlap the Overlap matrix
     */
    public void compute(HCore hCore, Overlap overlap) {
        compute((Matrix) hCore, overlap);
    }

    /**
     * Compute the MO coefficients and the orbital energies
     *
     * @param fock the Fock matrix
     * @param overlap the Overlap matrix
     */
    public void compute(Fock fock, Overlap overlap) {
        compute((Matrix) fock, overlap);
    }

    /** The actual computation is irrelavant of the type of matrix */
    private void compute(Matrix theMat, Overlap overlap) {
        Matrix x = overlap.getSHalf();
        Matrix a = theMat.similarityTransform(x);
        Diagonalizer diag = DiagonalizerFactory.getInstance()
                                               .getDefaultDiagonalizer();
        diag.diagonalize(a);

        orbitalEnergies = diag.getEigenValues();
        this.setMatrix(diag.getEigenVectors().mul(x).getMatrix());
    }
}
