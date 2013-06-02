/*
 * OptimizerFunction.java
 *
 * Created on August 28, 2007, 11:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.optimizer;

import org.meta.math.Matrix;

/**
 * The interface for defining a function to be optimized.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface OptimizerFunction {
    
    /**
     * Evaluate the function with 'n' variables
     *
     * @param variables an array of variables
     * @return a double value evaluating F(x1, x2, ...)
     */
    public double evaluate(double [] variables);

    /**
     * For a method that encapsulates its own set of "base" variables (e.g. an
     * initial set of atom positions), resets the base variables to new values.
     * 
     * @param variables the new set of base variables
     */
    public void resetVariables(double[] variables);

    /**
     * Getter for property derivativeAvailable.
     * @return Value of property derivativeAvailable.
     */
    public boolean isDerivativeAvailable();

    /**
     * Getter for property hessianAvailable.
     * @return Value of property hessianAvailable.
     */
    public boolean isHessianAvailable();

    /**
     * Getter for property derivatives.
     * @return Value of property derivatives.
     */
    public double [] getDerivatives();

    /**
     * Return the max norm of the derivatives, if available
     * 
     * @return max norm of all the derivatives
     */
    public double getMaxNormOfDerivatives();

    /**
     * Return the RMS of the derivatives, if available
     *
     * @return max RMS all the derivatives
     */
    public double getRMSOfDerivatives();

    /**
     * Getter for property hessian.
     * @return Value of property hessian.
     */
    public Matrix getHessian();
    
    
} // end of interface OptimizerFunction
