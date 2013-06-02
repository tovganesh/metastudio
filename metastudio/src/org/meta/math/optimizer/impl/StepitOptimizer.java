/*
 * StepitOptimizer.java 
 *
 * Created on 1 Oct, 2008 
 */
package org.meta.math.optimizer.impl;

import org.meta.math.optimizer.AbstractOptimizer;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * The "step it" optimizer.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class StepitOptimizer extends AbstractOptimizer {

    /** Creates a new instance of StepitOptimizer */
    public StepitOptimizer(OptimizerFunction function) {
        super(function);
    }

    /**
     * Apply the simplex minimizer
     */
    @Override
    public void minimize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
