/*
 * CubicInterpolater.java
 *
 * Created on July 26, 2007, 11:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.interpolater;

/**
 * Cubic interpolater (for smoother interpolation). 
 * Uses the following formula: <br>
 * <code>
 *  mu2 = mu * mu <br>
 *  a0  = y3 - y2 - y0 + y1 <br>
 *  a1  = y0 - y1 - a0 <br>
 *  a2  = y2 - y0 <br>
 *  a3  = y1 <br>
 *  a0*mu*m2 + a1*m2 + a2*mu + a3
 * </code>
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CubicInterpolater extends Interpolater {
    
    /** Creates a new instance of CubicInterpolater */
    public CubicInterpolater() {
    }
 
   /**
     * Interpolate value at X depending upon value at Y
     * Uses the formula: <br>
     * <code>
     *  mu2 = mu * mu <br>
     *  a0  = y3 - y2 - y0 + y1 <br>
     *  a1  = y0 - y1 - a0 <br>
     *  a2  = y2 - y0 <br>
     *  a3  = y1 <br>
     *  a0*mu*m2 + a1*m2 + a2*mu + a3 <br>
     * </code>
     * Where, <br>
     *  y0, y1, y2, y3  =>  y[0], y[1], y[2] and y[3] respectively <br>
     *  mu              =>  x[0]
     *
     * @param y the Y values (results of fuction evaluation)
     * @param x the X values at fuction evaluation is performed or is expected
     * @return the interpolated value depending upon the interpolation formula
     */
    @Override
    public double interpolate(double [] y, double [] x) {
        double mu2 = x[0]*x[0];
        double a0  = y[3] - y[2] - y[0] + y[1];
        double a1  = y[0] - y[1] - a0;
        double a2  = y[2] - y[0];        
        
        return (a0*x[0]*mu2 + a1*mu2 + a2*x[0] + y[1]);
    }    
    
    /**
     * Return number of Y arguments required for this Interpolater
     *
     * @return number of Y arguments
     */
    @Override
    public int getNumberOfRequiredYArgs() {
        return 4;
    }
    
    /**
     * Return number of X arguments required for this Interpolater
     *
     * @return number of X arguments
     */
    @Override
    public int getNumberOfRequiredXArgs() {
        return 1;
    }
    
    /**
     * Overloaded toString()
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Cubic interpolater";
    }
} // end of class CubicInterpolater
