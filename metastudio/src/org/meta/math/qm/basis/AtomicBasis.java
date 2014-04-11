/*
 * AtomicBasis.java
 *
 * Created on July 25, 2004, 10:40 AM
 */

package org.meta.math.qm.basis;

import java.util.*;

/**
 * Represents a single entity in a BasisSet
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AtomicBasis {
    
    /**
     * Holds value of property symbol.
     */
    private String symbol;
    
    /**
     * Holds value of property atomicNumber.
     */
    private int atomicNumber;
    
    /**
     * Holds value of property orbitals.
     */
    private ArrayList<Orbital> orbitals;
    
    /** 
     * Creates a new instance of AtomicBasis 
     *
     * @param symbol the atomic symbol, of whose this is basis
     * @param atomicNumber its atomic number
     */
    public AtomicBasis(String symbol, int atomicNumber) {
        this.symbol = symbol;
        this.atomicNumber = atomicNumber;
        
        orbitals = new ArrayList<Orbital>(6);
    }
    
    /**
     * Getter for property symbol.
     * @return Value of property symbol.
     */
    public String getSymbol() {
        return this.symbol;
    }
    
    /**
     * Setter for property symbol.
     * @param symbol New value of property symbol.
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    /**
     * Getter for property atomicNumber.
     * @return Value of property atomicNumber.
     */
    public int getAtomicNumber() {
        return this.atomicNumber;
    }
    
    /**
     * Setter for property atomicNumber.
     * @param atomicNumber New value of property atomicNumber.
     */
    public void setAtomicNumber(int atomicNumber) {
        this.atomicNumber = atomicNumber;
    }
    
    /**
     * Getter for property orbitals.
     * @return Value of property orbitals.
     */
    public ArrayList<Orbital> getOrbitals() {
        return this.orbitals;
    }
    
    /**
     * Setter for property orbitals.
     * @param orbitals New value of property orbitals.
     */
    public void setOrbitals(ArrayList<Orbital> orbitals) {
        this.orbitals = orbitals;
    }
    
    /**
     * Add an orbital object to this atomic basis
     * 
     * @param orbital the Orbital object to be added to this atomic basis
     */
    public void addOrbital(Orbital orbital) {
        orbitals.add(orbital);
    }
} // end of class AtomicBasis
