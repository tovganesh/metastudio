/*
 * Basis.java
 *
 * Created on July 25, 2004, 10:36 AM
 */

package org.meta.math.qm.basis;

import java.util.*;

/**
 * Represents an entire basis set (say sto-3g ... etc.)
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BasisSet {
    
    /**
     * Holds value of property name.
     */
    private String name;
    
    /** 
     * Holds the collection of atomic basis, with the symbol as the key
     */
    private HashMap<String, AtomicBasis> basisSet;
    
    /** 
     * Creates a new instance of Basis 
     *
     * @param name - the name of this basis set (say "sto-3g")
     */
    public BasisSet(String name) {
        this.name = name;
        
        basisSet = new HashMap<String, AtomicBasis>(80);
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Add a relevent atomic basis to this basis set
     *
     * @param basis the instance of AtomicBasis to be added to this basis set
     */
    public void addAtomicBasis(AtomicBasis basis) {
        basisSet.put(basis.getSymbol(), basis);
    }
    
    /**
     * Returns appropriate basis for a given atomic symbol. Will throw 
     * <code>BasisNotFoundException</code> if the basis set does not contain
     * atomic basis for the requested atomic symbol.
     *
     * @param symbol for which the basis is requested
     * @return instance of AtomicBasis
     */
    public AtomicBasis getAtomicBasis(String symbol) {
        AtomicBasis basis = basisSet.get(symbol);
        
        if (basis == null) {
            throw new BasisNotFoundException("Basis for atom '" + 
                           symbol + "' is not defined in : " + name);
        } // end if
        
        return basis;
    }
} // end of class BasisSet
