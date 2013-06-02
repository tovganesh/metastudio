package org.meta.math.optimizer;

/**
 * A one-dimensional function.
 * @author J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Function1D {

    /** 
     * Evaluates the function at point.x and sets the function value
     * point.fx (and higher derivatives).
     * @param point a single point in the domain of this function
     */
    public void evaluate(FunctionPoint1D point);
}
