/*
 * SimplexOptimizer.java 
 *
 * Created on 1 Oct, 2008 
 */

package org.meta.math.optimizer.impl;

import org.meta.math.optimizer.AbstractOptimizer;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * The simplex optimizer.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SimplexOptimizer extends AbstractOptimizer {

    /** Creates a new instance of SimplexOptimizer */
    public SimplexOptimizer(OptimizerFunction function) {
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
