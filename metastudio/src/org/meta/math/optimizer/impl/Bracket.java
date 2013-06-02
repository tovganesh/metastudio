package org.meta.math.optimizer.impl;

import org.meta.math.optimizer.FunctionPoint1D;

/**
 * A set of three points a, b, c that bracket a minimum of a function f(x).
 * The following relationships are guaranteed:
 * <ul>
 *  <li>a < b < c</li>
 *  <li>f(b) < f(a)</li>
 *  <li>f(b) < f(c)</li>
 * </ul>
 * @author J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Bracket {
    public FunctionPoint1D a,  b,  c;

    public Bracket(FunctionPoint1D a, FunctionPoint1D b, FunctionPoint1D c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
