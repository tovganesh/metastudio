/*
 * Orbital.java
 *
 * Created on July 25, 2004, 11:14 AM
 */

package org.meta.math.qm.basis;

import java.util.ArrayList;

/**
 * Represents an orbital type and its coefficients and exponents in 
 * <code>AtomicBasis</code>.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Orbital {
    
    /**
     * Holds value of property type.
     */
    private String type;
    
    /**
     * Holds value of property coefficients.
     */
    private ArrayList<Double> coefficients;
    
    /**
     * Holds value of property exponents.
     */
    private ArrayList<Double> exponents;
    
    /** 
     * Creates a new instance of Orbital 
     *
     * @param type the type of this orbital (e.g. 'S', 'P', 'D' etc...)
     */
    public Orbital(String type) {
        this.type = type;
        
        coefficients = new ArrayList<Double>(10);
        exponents    = new ArrayList<Double>(10);
    }
    
    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public String getType() {
        return this.type;
    }
    
    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Getter for property coefficients.
     * @return Value of property coefficients.
     */
    public ArrayList<Double> getCoefficients() {
        return this.coefficients;
    }
    
    /**
     * Setter for property coefficients.
     * @param coefficients New value of property coefficients.
     */
    public void setCoefficients(ArrayList<Double> coefficients) {
        this.coefficients = coefficients;
    }
    
    /**
     * Getter for property exponents.
     * @return Value of property exponents.
     */
    public ArrayList<Double> getExponents() {
        return this.exponents;
    }
    
    /**
     * Setter for property exponents.
     * @param exponents New value of property exponents.
     */
    public void setExponents(ArrayList<Double> exponents) {
        this.exponents = exponents;
    }
    
    /**
     * adds a coefficient to this Orbital entry
     * 
     * @param coeff the coefficient to be added
     */
    public void addCoefficient(double coeff) {
        coefficients.add(new Double(coeff));
    }
    
    /**
     * adds a exponent to this Orbital entry
     * 
     * @param exp the exponent to be added
     */
    public void addExponent(double exp) {
        exponents.add(new Double(exp));
    }
    
    /**
     * adds a (coefficient, exponent) pair to this Orbital entry
     * 
     * @param coeff the coefficient to be added
     * @param exp the exponent to be added
     */
    public void addEntry(double coeff, double exp) {
        addCoefficient(coeff);
        addExponent(exp);
    }
} // end of class Orbital
