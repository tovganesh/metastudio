/**
 * LineMinimizer.java
 *
 * Created on May 12, 2009
 */
package org.meta.math.optimizer.impl;

import org.meta.math.optimizer.AbstractOptimizer;
import org.meta.math.optimizer.Function1D;
import org.meta.math.optimizer.FunctionPoint1D;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * Line minimizer (based on Numerical Recieps )
 * 
 * @author  J. Milthorpe, V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LineMinimizer extends AbstractOptimizer {

    private double [] directions;

    public LineMinimizer(OptimizerFunction function, double [] directions) {
        super(function);

        this.directions = directions;
    }
    
    @Override
    public void minimize() {
        currentMinima = lineMinimization(getVariables(), directions);
    }

    /**
     * Performs line minimization of an n-dimensional function p along the line
     * xi.
     * Reference Numerical Recipes Ed. 3, p514
     * @param p an n-dimensional point.  On return, p is set to the the point
     * at which the function takes on a minimum value along the direction xi.
     * @param xi an n-dimensional direction. On return, xi is set to the actual
     * vector displacement that p was moved.
     * @return the minimum value of the function at the new point p
     */
    private double lineMinimization(double[] p, double[] xi) {
        if (p.length != xi.length) {
            throw new IllegalArgumentException("p and x are not of the same order");
        } // end if

        Function1D lineFunction = new LineFunction(p, xi, optimizerFunction);

        Bracket bracket = bracketMinimum(-0.05, 0.05, lineFunction);
        FunctionPoint1D minimum = getParabolicMinimum(bracket, lineFunction, 1.0e-8);
        for (int j = 0; j < p.length; j++) {
            xi[j] = minimum.x * xi[j];
            p[j]  = p[j] + xi[j];
        } // end if

        return minimum.fx;
    }

    /** Brackets a minimum of the function f(x), given distinct initial points
     * a and b (in bracket).
     * Reference: Numerical Recipes Ed. 3 p 494
     * @param ax initial point a
     * @param bx initial point b
     * @param function one-dimensional function for which to bracket the minimum
     * @return a bracket on the minimum of the function
     */
    private Bracket bracketMinimum(double ax, double bx,
                                   Function1D function) {

        /** Golden ratio by which successive intervals are magnified. */
        final double GOLD = 1.618034;
        /** A small non-zero perturbation, used to prevent possible division by zero. */
        final double TINY = 1.0e-20;
        /** The maximum magnification allowed for a parabolic-fit step. */
        final double MAX_MAGNIFICATION = 100.0;

        FunctionPoint1D a = new FunctionPoint1D(ax);
        function.evaluate(a);
        FunctionPoint1D b = new FunctionPoint1D(bx);
        function.evaluate(b);

        if (b.fx > a.fx) {
            // switch a and b so theat we can go downhill in direction from a to b
            FunctionPoint1D temp = a;
            a = b;
            b = temp;
        }

        FunctionPoint1D c = new FunctionPoint1D(b.x + GOLD * (b.x - a.x));
        function.evaluate(c);

        while (b.fx >= c.fx) {
            // compute u by parabolic extrapolation from a, b ,c
            Double r = (b.x - a.x) * (b.fx - c.fx);
            Double q = (b.x - c.x) * (b.fx - a.fx);
            FunctionPoint1D u = new FunctionPoint1D(b.x - ((b.x - c.x) * q - (b.x -
                    a.x) * r / 2.0 * Math.signum(q - r) * Math.max(
                    Math.abs(q - r), TINY)));
            Double ulim = b.x + MAX_MAGNIFICATION * (c.x - b.x);
            if ((b.x - u.x) * (u.x - c.x) > 0.0) {
                // parabolic u is between b and c: try it.
                function.evaluate(u);
                if (u.fx < c.fx) {
                    // got a minimum between b and c
                    a = b;
                    b = u;

                    break;
                } else if (u.fx > b.fx) {
                    // got a minimum between a and u
                    c = u;

                    break;
                }
                // Parabolic fit was no use.  Use default magnification.
                u = new FunctionPoint1D(c.x + GOLD * (c.x - b.x));
                function.evaluate(u);
            } else if ((c.x - u.x) * (u.x - ulim) > 0.0) {
                // Parabolic fit is between c and its allowed limit.
                function.evaluate(u);
                if (u.fx < c.fx) {
                    b = c;
                    c = u;
                    u = new FunctionPoint1D(c.x + GOLD * (c.x - b.x));
                    function.evaluate(u);

                }
            } else if ((u.x - ulim) * (ulim - c.x) > 0.0) {
                // Limit parabolic u to maximum allowed value.
                u = new FunctionPoint1D(ulim);
                function.evaluate(u);
            } else {
                // Reject parabolic u, use default magnification.
                u = new FunctionPoint1D(c.x + GOLD * (c.x - b.x));
                function.evaluate(u);
            }
            a = b;
            b = c;
            c = u;
        }

        return new Bracket(a, b, c);
    }
    
    /**
     * Given a function and a bracket on the minium, returns the point at which
     * the value of the function is minimized (to within tolerance), using a
     * modification of Brent's method that uses derivaties.
     * Reference Numerical Recipes Ed. 3, p500.
     * @param bracket
     * @param tolerance
     * @return
     */
    private FunctionPoint1D getParabolicMinimum(final Bracket bracket,
                     final Function1D function, final double tolerance) {
        final double EPSILON = 1.0e-10;

        FunctionPoint1D a = new FunctionPoint1D(Math.min(bracket.a.x,
                                                         bracket.c.x));
        FunctionPoint1D b = new FunctionPoint1D(Math.max(bracket.a.x,
                                                         bracket.c.x));
        FunctionPoint1D v = new FunctionPoint1D(bracket.b.x);
        function.evaluate(v);

        FunctionPoint1D w = v;
        FunctionPoint1D x = v;
        double d = 0.0;
        double e = 0.0;

        int i;
        for (i = 0; i<getMaxIterations(); i++) {
            double xmean = 0.5 * (a.x + b.x);
            double tol1 = tolerance * Math.abs(x.x) + EPSILON;
            double tol2 = 2.0 * tol1;
            if (Math.abs(x.x - xmean) <= tol2 - 0.5 * (b.x - a.x)) {
                // separation is within tolerance
                break;
            }

            if (Math.abs(e) > tol1) {
                double d1 = 2.0 * (b.x - a.x);
                double d2 = d1;
                // secant method with one point
                if (w.f1x != x.f1x) {
                    d1 = (w.x - x.x) * x.f1x / (x.f1x - w.f1x);
                }
                // and the other
                if (v.f1x != x.f1x) {
                    d2 = (v.x - x.x) * x.f1x / (x.f1x - v.f1x);
                }
                // Which of these two estimates of d shall we take?  We will
                // insist that they be within the bracket, and on the side
                // pointed to by the derivative at x:
                double u1 = x.x + d1;
                double u2 = x.x + d2;
                boolean ok1 = ((a.x - u1) * (u1 - b.x) > 0.0) && (x.f1x * d1 <= 0.0);
                boolean ok2 = ((a.x - u2) * (u2 - b.x) > 0.0) && (x.f1x * d2 <= 0.0);
                
                // movement on the step before last
                double olde = e;
                e = d;
                if (!(ok1 || ok2)) {
                    if (x.f1x >= 0.0) {
                        e = a.x - x.x;
                    } else {
                        e = b.x - x.x;
                    }
                    d = 0.5 * e; // bisect, not golden section
                } else {
                    // Take only an acceptable d, and if both are acceptable,
                    // then take the smallest one.
                    if (ok1 && ok2) {
                        if (Math.abs(d1) < Math.abs(d2)) {
                            d = d1;
                        } else {
                            d = d2;
                        }
                    } else if (ok1) {
                        d = d1;
                    } else {
                        d = d2;
                    }
                    if (Math.abs(d) > 0.5 * olde) {
                        if (x.f1x >= 0.0) {
                            e = a.x - x.x;
                        } else {
                            e = b.x - x.x;
                        }
                        d = 0.5 * e;
                    } else {
                        double u = x.x + d;
                        if (u - a.x < tol2 || b.x - u < tol2) {
                            d = Math.signum(xmean - x.x) * tol1;
                        }
                    }
                }
            } else {
                if (x.f1x >= 0.0) {
                    e = a.x - x.x;
                } else {
                    e = b.x - x.x;
                }
                d = 0.5 * e;
            }
            FunctionPoint1D u = new FunctionPoint1D(x.x + Math.signum(d) *
                                                   Math.max(Math.abs(d), tol1));

            function.evaluate(u);
            if (u.fx > x.fx && Math.abs(d) < tol1) {
                // if the minimum step in the downhill direction takes us
                // uphill, then we are done.
                break;
            }

            if (u.fx <= x.fx) {
                if (u.x >= x.x) {
                    a = x;
                } else {
                    b = x;
                }
                v = w;
                w = x;
                x = u;
            } else {
                if (u.x < x.x) {
                    a = u;
                } else {
                    b = u;
                }

                if (u.fx <= w.fx || w.x == x.x) {
                    v = w;
                    w = u;
                } else if (u.fx <= v.fx || v.x == x.x || v.x == w.x) {
                    v = u;
                }
            }
        } // end if

        if (i == getMaxIterations()) {
            System.err.println("getParabolicMinimum exceeded max iterations");
        }

        return x;
    }
}
