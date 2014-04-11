/*
 * SCFType.java
 *
 * Created on August 5, 2004, 10:58 PM
 */

package org.meta.math.qm;

/**
 * SCF Type enumeration.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SCFType {

    /**
     * Holds value of property type.
     */
    private int type;
    
    /**
     * The Hartree Fock method
     */
    public static final SCFType HARTREE_FOCK = new SCFType(1);
    
    /**
     * The Hartree Fock method (Direct SCF)
     */
    public static final SCFType HARTREE_FOCK_DIRECT = new SCFType(2);
    
    /**
     * The Moller Plesset method
     */
    public static final SCFType MOLLER_PLESSET = new SCFType(3);

    /** Creates a new instance of SCFType */
    private SCFType(int type) {
        this.type = type;
    }

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return this.type;
    }

    /**
     * Returns a description of the SCF type
     *
     * @return a string indicating SCF type
     */
    @Override
    public String toString() {
        String description = "";

        if (this.equals(HARTREE_FOCK)) {
            description = "Hartree Fock Method (in core)";
        } else if (this.equals(HARTREE_FOCK_DIRECT)) {
            description = "Hartree Fock Method (direct SCF)";
        } else if (this.equals(MOLLER_PLESSET)) {
            description = "Moller Plesset Method (in core)";
        } else {
            description = "No description available";
        } // end if

        return description;
    }

    /**
     * The method to check the equality of two objects of SCFType class.
     *
     * @return true/ false specifying the equality or inequality
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return ((obj != null)
                && (obj instanceof SCFType)
                && (this.type == ((SCFType) obj).type));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.type;
        return hash;
    }
} // end of class SCFType
