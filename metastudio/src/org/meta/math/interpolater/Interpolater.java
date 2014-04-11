/*
 * Interpolater.java
 *
 * Created on July 26, 2007, 10:24 PM
 * 
 */

package org.meta.math.interpolater;

/**
 * An interpolater abstract interface. 
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class Interpolater {
    
    /**
     * Interpolate value at X depending upon value at Y
     *
     * @param y the Y values (results of fuction evaluation)
     * @param x the X values at fuction evaluation is performed or is expected
     * @return the interpolated value depending upon the interpolation formula
     */
    public abstract double interpolate(double [] y, double [] x);
    
    /**
     * Return number of Y arguments required for this Interpolater
     *
     * @return number of Y arguments
     */
    public abstract int getNumberOfRequiredYArgs();
    
    /**
     * Return number of X arguments required for this Interpolater
     *
     * @return number of X arguments
     */
    public abstract int getNumberOfRequiredXArgs();

    /**
     * Holds value of property subInterpolater.
     */
    protected Interpolater subInterpolater;

    /**
     * Getter for property subInterpolater.
     * @return Value of property subInterpolater.
     */
    public Interpolater getSubInterpolater() {
        return this.subInterpolater;
    }

    /**
     * Setter for property subInterpolater.
     * @param subInterpolater New value of property subInterpolater.
     */
    public void setSubInterpolater(Interpolater subInterpolater) {
        this.subInterpolater = subInterpolater;
    }
} // end of interface Interpolater
