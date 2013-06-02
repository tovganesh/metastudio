package org.meta.math.optimizer.impl;

import org.meta.math.optimizer.AbstractOptimizer;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * The steepest descent optimizer.
 *
 * @author J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SteepestDescentOptimizer extends AbstractOptimizer {
    private double stepScale;
    private double maxStep;
    private double[] previousVariables;

    /**
     * Creates a new SteepestDescentOptimizer.
     * @param function the optimizer function to use
     * @param stepSize the normal step scale to use
     * @param maxStep the maximum step distance for one iteration
     */
    public SteepestDescentOptimizer(OptimizerFunction function, double stepSize,
                                    double maxStep) {
        super(function);
        if (!function.isDerivativeAvailable()) {
            throw new IllegalArgumentException(
                    "Attempted to use SteepestDescentOptimizer, but function " +
                    "has no derivative!");
        } // end if
        this.stepScale = stepSize;
        this.maxStep = maxStep;

        convergenceCriteria = new SimpleConvergenceCriteria();
        convergenceCriteria.setTolerance(1.0e-8);
    }

    @Override
    public void minimize() {
        currentMinima = optimizerFunction.evaluate(variables);
        convergenceCriteria.setOldValue(currentMinima);
        double step = stepScale;
        double currentValue = 0.0;
        int iter = 0;
        do {
            // use gradients to update positions
            previousVariables = variables.clone();
            double[] gradients = optimizerFunction.getDerivatives();
            for (int i = 0; i < variables.length; i++) {
                double move = -gradients[i] * step;
                if (move > maxStep) {
                    move = maxStep;
                } else if (move < -maxStep) {
                    move = -maxStep;
                } // end if

                variables[i] += move;
            } // end for

            currentValue = optimizerFunction.evaluate(variables);
            convergenceCriteria.setOldValue(convergenceCriteria.getNewValue());
            convergenceCriteria.setNewValue(currentValue);

            if (currentValue > currentMinima ||
                    convergenceCriteria.isConverged()) {
                // didn't achieve a reduction (or minimization is converged)
                variables = previousVariables;
                currentValue = optimizerFunction.evaluate(variables);
                step *= 0.1;
            } else {
                step *= 2.0;
                System.out.println("newValue = " + currentValue);
            }

            currentMinima = currentValue;
            optimizerFunction.resetVariables(variables);

            iter++;
        } while (iter < getMaxIterations() &&
                !convergenceCriteria.isConverged());
    }
}

