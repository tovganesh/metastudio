/*
 * Optimizer.java
 *
 * Created on August 25, 2007, 11:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.optimizer;

/**
 * The Optimizer interface.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Optimizer {
    
    /**
     * Getter for property optimizerFunction.
     * @return Value of property optimizerFunction.
     */
    public OptimizerFunction getOptimizerFunction();
    
    /**
     * Apply the minimizer
     */
    public void minimize();

    /**
     * Apply the minimizer freezing the variables numbers specified in
     * the integer array. The variables are indexed from 0 to total number of
     * variables - 1.
     *
     * @param freezeVariables is an array of indices of variables to be freezed
     *        during the minimization process.
     */
    public void minimize(int [] freezeVariables);

    /**
     * Return the current minimum value from this optimizer
     * 
     * @return the current minimum value
     */
    public double getCurrentMinima();

    /**
     * Getter for property variables.
     * @return Value of property variables.
     */
    public double [] getVariables();

    /**
     * Setter for property variables.
     * @param variables New value of property variables.
     */
    public void setVariables(double [] variables);


    /**
     * Setter for property maxIterations.
     * @param maxIterations New value of property maxIterations.
     */
    public void setMaxIterations(int maxIterations);

    /**
     * Getter for property maxIterations.
     * @return maxIterations of property maxIterations.
     */
    public int getMaxIterations();

    /**
     * Setter for property convergenceCriteria.
     * @param convergenceCriteria New value of property convergenceCriteria.
     */
    public void setConvergenceCriteria(ConvergenceCriteria convergenceCriteria);

    /**
     * Getter for property convergenceCriteria.
     * @return convergenceCriteria of property convergenceCriteria.
     */
    public ConvergenceCriteria getConvergenceCriteria();

} // end of interface Optimizer
