/**
 * LineFunction.java
 *
 * Created on May 12, 2009
 */
package org.meta.math.optimizer.impl;

import org.meta.math.optimizer.Function1D;
import org.meta.math.optimizer.FunctionPoint1D;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * Represents a target function as a line going through the n-dimensional
 * point p in the direction xi
 * 
 * @author  J. Milthorpe, V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LineFunction implements Function1D {
    private final double[] p;
    private final double[] xi;
    private final OptimizerFunction targetFunction;

    public LineFunction(double[] p, double[] xi,
                        OptimizerFunction targetFunction) {
        this.p = p;
        this.xi = xi;
        this.targetFunction = targetFunction;
    }

    @Override
    public void evaluate(FunctionPoint1D point) {
        double[] xt = new double[p.length];
        for (int j = 0;
                j < p.length; j++) {
            xt[j] = p[j] + point.x * xi[j];
        }
        point.fx = targetFunction.evaluate(xt);
        double[] derivatives = targetFunction.getDerivatives();
        point.f1x = 0.0;
        for (int j = 0; j < p.length; j++) {
            point.f1x += derivatives[j] * xi[j];
        }
    }
}

