/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.math.optimizer.impl;

import java.util.Iterator;
import org.meta.math.optimizer.AbstractOptimizer;
import org.meta.math.optimizer.ConvergenceCriteria;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * A conjugate gradient optimizer.
 *
 * @author J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ConjugateGradientOptimizer extends AbstractOptimizer {
    /**
     * Creates a new ConjugateGradientOptimizer.
     * @param function the optimizer function to use
     * @param convergenceTolerance used define a tolerance for optimizer
     *         convergece
     */
    public ConjugateGradientOptimizer(OptimizerFunction function,
                                      double convergenceTolerance) {

        super(function);
        if (!function.isDerivativeAvailable()) {
            throw new IllegalArgumentException(
                    "Attempted to use ConjugateGradientMinimizer, " +
                    "but function has no derivative!");
        }

        convergenceCriteria = new SimpleConvergenceCriteria() {
            private final double EPSILON = 1.0e-10;;
            
            @Override
            public boolean isConverged() {
                boolean isConverged =
                   2.0 * Math.abs(newValue - oldValue) <= tolerance *
                    (Math.abs(newValue) + Math.abs(oldValue) + EPSILON);

                if (isComposite()) {
                    Iterator<ConvergenceCriteria> ccList
                                = getAttachedSubCriteriaList();

                    while (ccList.hasNext()) {
                        ConvergenceCriteria cc = ccList.next();

                        switch (cc.getCriteriaCombinationType()) {
                            case AND:
                                isConverged = isConverged && cc.isConverged();
                                break;
                            case OR:
                                isConverged = isConverged || cc.isConverged();
                                break;
                        } // end of switch .. case
                    } // end while
                } // end if

                return isConverged;
            }
        };
        convergenceCriteria.setTolerance(convergenceTolerance);
    }

    @Override
    public void minimize() {
        double[] previousVariables = variables.clone();
        double minValue = optimizerFunction.evaluate(variables);
        convergenceCriteria.setOldValue(minValue);
        currentMinima = minValue;
        System.out.println("Starting value = " + minValue);
        
        double[] xi = optimizerFunction.getDerivatives();
        double[] g = new double[variables.length];
        double[] h = new double[variables.length];
        int sinceRestart = 0;
        int iter = 0;
        
        for (int j=0; j<variables.length; j++) {
            g[j]  = -xi[j];
            h[j]  = g[j];
            xi[j] = h[j];
        } // end for

        double gg, dgg, gam, newValue;

        do {
            if (sinceRestart >= variables.length) {
                // restart the algorithm using the gradient at
                // the current point
                // System.out.println("restarting");
                for (int j = 0; j < variables.length; j++) {
                    xi = optimizerFunction.getDerivatives();
                    g[j] = -xi[j];
                    h[j] = g[j];
                    xi[j] = h[j];
                }
                sinceRestart = 0;
            } // end if
            
            previousVariables = variables.clone();
            LineMinimizer lineMinimizer = new LineMinimizer(optimizerFunction, xi);
            lineMinimizer.setVariables(variables);
            lineMinimizer.minimize();
            newValue = lineMinimizer.getCurrentMinima();
            convergenceCriteria.setNewValue(newValue);
            System.out.println("newValue = " + newValue);
            if (convergenceCriteria.isConverged()) {
                // the normal end of minimization
                minValue = optimizerFunction.evaluate(previousVariables);
                System.out.println("final value = " + minValue);
                break;
            } // end if

            xi  = optimizerFunction.getDerivatives();
            gg  = 0.0;
            dgg = 0.0;
            for (int j = 0; j < variables.length; j++) {
                gg  = gg + g[j] * g[j];
                dgg = dgg + (xi[j] + g[j]) * xi[j];
            } // end for

            if (gg == 0.0) {
                // Unlikely.  If gradient is exactly zero then we are already done.
                break;
            } // end if

            gam = dgg / gg;
            for (int j = 0; j < variables.length; j++) {
                g[j]  = -xi[j];
                h[j]  = g[j] + gam * h[j];
                xi[j] = h[j];
            }
            currentMinima = minValue = newValue;
            convergenceCriteria.setOldValue(convergenceCriteria.getNewValue());
            optimizerFunction.resetVariables(variables);

            iter++;
            sinceRestart++;
        } while (iter < getMaxIterations());
    }
}
