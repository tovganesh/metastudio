/*
 * TriLinearInterpolater.java
 *
 * Created on July 28, 2007, 6:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.interpolater;

/**
 * Expanded form of linear interpolation for a set of values on a cuboid.
 * Uses the following formula: <br>
 * <code>
 *  xd = x0 - x1 <br>
 *  yd = y0 - y1 <br>
 *  zd = z0 - z1 <br>
 *
 *  i1 = Vx1y1z1 * (1 - zd/incZ) + Vx1y1z2 * zd/incZ <br>
 *  i2 = Vx1y2z1 * (1 - zd/incZ) + Vx1y2z2 * zd/incZ <br>
 *  j1 = Vx2y1z1 * (1 - zd/incZ) + Vx2y1z2 * zd/incZ <br>
 *  j2 = Vx2y2z1 * (1 - zd/incZ) + Vx2y2z2 * zd/incZ <br>
 *
 *  w1 = i1 * (1 - yd/incY) + i2 * yd/incY <br>
 *  w2 = j1 * (1 - yd/incY) + j2 * yd/incY <br>
 *
 *  Vx0y0z0 = w1 * (1 - xd/incX) + w2 * xd/incX
 * </code>
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TriLinearInterpolater extends Interpolater {
    
    /** Creates a new instance of TriLinearInterpolater */
    public TriLinearInterpolater() {
    }
    
    /**
     * Interpolate value at X depending upon value at Y
     * Uses the formula: <br>
     * <code>
     *  xd = x0 - x1 <br>
     *  yd = y0 - y1 <br>
     *  zd = z0 - z1 <br>
     *
     *  i1 = Vx1y1z1 * (1 - zd/incZ) + Vx1y1z2 * zd/incZ <br>
     *  i2 = Vx1y2z1 * (1 - zd/incZ) + Vx1y2z2 * zd/incZ <br>
     *  j1 = Vx2y1z1 * (1 - zd/incZ) + Vx2y1z2 * zd/incZ <br>
     *  j2 = Vx2y2z1 * (1 - zd/incZ) + Vx2y2z2 * zd/incZ <br>
     *
     *  w1 = i1 * (1 - yd/incY) + i2 * yd/incY <br>
     *  w2 = j1 * (1 - yd/incY) + j2 * yd/incY <br>
     *
     *  Vx0y0z0 = w1 * (1 - xd/incX) + w2 * xd/incX <br>
     * </code>
     * Where, <br>
     *  Vx1y1z1 ... Vx2y2z2   =>  y[0] ... y[7] <br>
     *  x1, y1, z1            =>  x[0], x[1], x[2] <br>
     *  x0, y0, z0            =>  x[3], x[4], x[5] <br>
     *  incX, incY, incZ      =>  x[6], x[7], x[8] <br>
     *
     * @param y the Y values (results of fuction evaluation)
     * @param x the X values at fuction evaluation is performed or is expected
     * @return the interpolated value depending upon the interpolation formula
     */
    @Override
    public double interpolate(double [] y, double [] x) {
        double [] delta = new double[]{(x[3]-x[0])/x[6],
                                       (x[4]-x[1])/x[7],
                                       (x[5]-x[2])/x[8]};
        double i1, i2, j1, j2, w1, w2;
        
        i1 = y[0]*(1.0 - delta[2]) + y[1]*delta[2];
        i2 = y[2]*(1.0 - delta[2]) + y[3]*delta[2];
        j1 = y[4]*(1.0 - delta[2]) + y[5]*delta[2];
        j2 = y[6]*(1.0 - delta[2]) + y[7]*delta[2];
        
        w1 = i1*(1.0 - delta[1]) + i2*delta[1];
        w2 = j1*(1.0 - delta[1]) + j2*delta[1];
        
        return (w1*(1.0 - delta[0]) + w2*delta[0]);
    }
    
    /**
     * Return number of Y arguments required for this Interpolater
     *
     * @return number of Y arguments
     */
    @Override
    public int getNumberOfRequiredYArgs() {
        return 8;
    }
    
    /**
     * Return number of X arguments required for this Interpolater
     *
     * @return number of X arguments
     */
    @Override
    public int getNumberOfRequiredXArgs() {
        return 9;
    }
    
    /**
     * Overloaded toString()
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Tri linear interpolater";
    }
} // end of class TriLinearInterpolater
