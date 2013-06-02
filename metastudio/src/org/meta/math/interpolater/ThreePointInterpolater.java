/*
 * ThreePointInterpolater.java
 *
 * Created on August 5, 2007, 9:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.interpolater;

/**
 * Same as @class TriLinearInterpolater but use a specified 
 * sub interpolater to perform individual point interpolation.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ThreePointInterpolater extends Interpolater {
    
    /** Creates a new instance of ThreePointInterpolater */
    public ThreePointInterpolater() {
        subInterpolater = new LinearInterpolater();
    }

    private double i1, i2, j1, j2, w1, w2;
    private double [] x1 = new double[9], y1 = new double[8];
    private double [] delta = new double[3];
    
    /**
     * Interpolate value at X depending upon value at Y.
     * Same as @class TriLinearInterpolater but use a specified 
     * sub interpolater to perform individual point interpolation.
     * Default sub-interpolater is Linear interpolater.
     *
     * Note: It is the reposibility of the caller to ensure that only one point
     * interpolater(s) are set as sub-interpolater. Else this routine will fail
     * to operate.
     *
     * @param y the Y values (results of fuction evaluation)
     * @param x the X values at fuction evaluation is performed or is expected
     * @return the interpolated value depending upon the interpolation formula
     */
    @Override
    public double interpolate(double [] y, double [] x) {        
        delta[0] = (x[3]-x[0])/x[6];
        delta[1] = (x[4]-x[1])/x[7];
        delta[2] = (x[5]-x[2])/x[8];

        y1[0]=y[0]; y1[1]=y[1]; y1[2]=y[2]; y1[3]=y[3]; x1[0]=delta[2];
        i1 = subInterpolater.interpolate(y1, x1);
        
        y1[0]=y[2]; y1[1]=y[3]; y1[2]=y[4]; y1[3]=y[5];
        i2 = subInterpolater.interpolate(y1, x1);
        
        y1[0]=y[4]; y1[1]=y[5]; y1[2]=y[6]; y1[3]=y[7];
        j1 = subInterpolater.interpolate(y1, x1);
        
        y1[0]=y[6]; y1[1]=y[7]; y1[2]=y[4]; y1[3]=y[5];
        j2 = subInterpolater.interpolate(y1, x1);
        
        y1[0]=i1; y1[1]=i2; y1[2]=j1; y1[3]=j2; x1[0]=delta[1];
        w1 = subInterpolater.interpolate(y1, x1);
        
        y1[0]=j1; y1[1]=j2; y1[2]=i1; y1[3]=i2;
        w2 = subInterpolater.interpolate(y1, x1);
        
        y1[0]=w1; y1[1]=w2; y1[2]=w1; y1[3]=w2; x1[0]=delta[0];
        
        return (subInterpolater.interpolate(y1, x1));
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
} // end of class ThreePointInterpolater
