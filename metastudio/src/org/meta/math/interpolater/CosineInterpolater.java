/*
 * CosineInterpolater.java
 *
 * Created on July 26, 2007, 11:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.interpolater;

/**
 * Cosine interpolater (for smoother interpolation). 
 * Uses the following formula: <br>
 * <code>
 *  mu2 = (1 - cos(mu * PI)) / 2 <br>
 *  y1 * (1 - mu2) + y2 * mu2  
 * </code>
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CosineInterpolater extends Interpolater {
    
    /** Creates a new instance of CosineInterpolater */
    public CosineInterpolater() {
    }
    
    /**
     * Interpolate value at X depending upon value at Y
     * Uses the formula: <br>
     * <code>
     *  mu2 = (1 - cos(mu * PI)) / 2 <br>
     *  y1 * (1 - mu2) + y2 * mu2 <br>
     * </code>
     * Where, <br>
     *  y1, y2  =>  y[0] and y[1] respectively <br>
     *  mu      =>  x[0]
     *
     * @param y the Y values (results of fuction evaluation)
     * @param x the X values at fuction evaluation is performed or is expected
     * @return the interpolated value depending upon the interpolation formula
     */
    @Override
    public double interpolate(double [] y, double [] x) {
        double mu2 = (1.0 - Math.cos(x[0] * Math.PI)) / 2.0;        
        return (y[0] * (1.0 - mu2) + y[1] * mu2);
    }
    
    /**
     * Return number of Y arguments required for this Interpolater
     *
     * @return number of Y arguments
     */
    @Override
    public int getNumberOfRequiredYArgs() {
        return 2;
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
        return "Cosine interpolater";
    }
} // end of class CosineInterpolater
