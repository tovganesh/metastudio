package org.meta.math.optimizer;

/**
 * An evaluation of a one-dimensional function f(x) at a single point x.
 * @author J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FunctionPoint1D {
    
    /** A point x within the domain of the function. */
    public double x;

    /** The value of f(x). */
    public double fx;
    
    /** The value of the first derivate f'(x). */
    public double f1x;

    public FunctionPoint1D(double x) {
        this.x = x;
    }
}
