/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.optimizer.impl;

import org.meta.math.optimizer.AbstractOptimizer;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * Line search based on Numerical Recieps, section 9.7
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LineSearch extends AbstractOptimizer {

    private double [] directions;
    
    public LineSearch(OptimizerFunction function, double [] directions) {
        super(function);

        this.directions = directions;
    }

    @Override
    public void minimize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
