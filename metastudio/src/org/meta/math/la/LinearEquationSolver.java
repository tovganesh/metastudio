/**
 * LinearEquationSolver.java
 *
 * Created on 30/01/2010
 */

package org.meta.math.la;

import org.meta.math.Matrix;
import org.meta.math.Vector;
import org.meta.math.la.exception.SingularMatrixException;

/**
 * An abstract class representing a linear equation solver of the form: Ax = B
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class LinearEquationSolver {

    /** Creates new instance of LinearEquationSolver */
    public LinearEquationSolver() {
    }

    /**
     * findSolution() - This method finds the solution to a set of linear
     *                  equations in the form AX = B. Matrix a[][] is
     *                  considered to represent the coefficients of the equation
     *                  whose solution is to be found out. To solve an equation
     *                  with 'N' unknowns 'a' should be an NxN matrix, with
     *                  the initial X representing the right hand side of the
     *                  equation i.e the matrix B.
     *                  Thus it should be noted that before calling findSolution()
     *                  both <code>setMatrixA()</code> and <code>setVectorX()</code>
     *                  must be called with matrix A and initial B respectively.
     * @throws SingularMatrixException
     */
    public abstract void findSolution() throws SingularMatrixException;

    protected Matrix matrixA;

    /**
     * Get the value of matrixA
     *
     * @return the value of matrixA
     */
    public Matrix getMatrixA() {
        return matrixA;
    }

    /**
     * Set the value of matrixA
     *
     * @param matrixA new value of matrixA
     */
    public void setMatrixA(Matrix matrixA) {
        this.matrixA = matrixA;
    }

    protected Vector vectorX;

    /**
     * Get the value of vectorX
     *
     * @return the value of vectorX
     */
    public Vector getVectorX() {
        return vectorX;
    }

    /**
     * Set the value of vectorX
     *
     * @param vectorX new value of vectorX
     */
    public void setVectorX(Vector vectorX) {
        this.vectorX = vectorX;
    }
}
