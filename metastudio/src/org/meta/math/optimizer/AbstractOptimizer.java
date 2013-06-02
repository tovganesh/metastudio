/*
 * AbstractOptimizer.java 
 *
 * Created on 1 Oct, 2008 
 */

package org.meta.math.optimizer;

/**
 * Abstract implementation of Optimizer.
 * 
 * @author V. Ganesh, J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractOptimizer implements Optimizer {

    protected final OptimizerFunction optimizerFunction;

    private static final int DEFAULT_MAX_ITER = 100;

    /** AbstractOptimizer default constructor */
    public AbstractOptimizer(OptimizerFunction optimizerFunction) {
        this.optimizerFunction = optimizerFunction;
        variables = null;

        maxIterations = DEFAULT_MAX_ITER;
    }

    /**
     * Get the value of optimizerFunction
     *
     * @return the value of optimizerFunction
     */
    @Override
    public OptimizerFunction getOptimizerFunction() {
        return optimizerFunction;
    }

    protected double[] variables;

    /**
     * Get the value of variables
     *
     * @return the value of variables
     */
    @Override
    public double[] getVariables() {
        return variables;
    }

    /**
     * Set the value of variables
     *
     * @param variables new value of variables
     */
    @Override
    public void setVariables(double[] variables) {
        this.variables = variables;
    }

    protected double currentMinima;

    /**
     * Return the current minimum value from this optimizer
     *
     * @return the current minimum value
     */
    @Override
    public double getCurrentMinima() {
        return currentMinima;
    }

    protected int maxIterations;

    /**
     * Setter for property maxIterations.
     * @param maxIterations New value of property maxIterations.
     */
    @Override
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Getter for property maxIterations.
     * @return maxIterations of property maxIterations.
     */
    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Apply the minimizer freezing the variables numbers specified in
     * the integer array. The variables are indexed from 0 to total number of
     * variables - 1.
     *
     * @param freezeVariables is an array of indices of variables to be freezed
     *        during the minimization process.
     */
    @Override
    public void minimize(int [] freezeVariables) {
        throw new UnsupportedOperationException("Method not yet implemented!");
    }

    protected ConvergenceCriteria convergenceCriteria;

    /**
     * Setter for property convergenceCriteria.
     * @param convergenceCriteria New value of property convergenceCriteria.
     */
    @Override
    public void setConvergenceCriteria(ConvergenceCriteria convergenceCriteria){
        this.convergenceCriteria = convergenceCriteria;
    }

    /**
     * Getter for property convergenceCriteria.
     * @return convergenceCriteria of property convergenceCriteria.
     */
    @Override
    public ConvergenceCriteria getConvergenceCriteria() {
        return convergenceCriteria;
    }
}
