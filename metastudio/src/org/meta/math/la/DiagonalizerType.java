/*
 * DiagonalizerType.java
 *
 * Created on August 4, 2004, 7:20 AM
 */

package org.meta.math.la;

/**
 * Specifies the diagonalizer types, like <code>JACOBI</code> etc.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public final class DiagonalizerType {
    
    /**
     * Holds value of property type.
     */
    private int type;
    
    /**
     * The Jacobi method
     */
    public static final DiagonalizerType JACOBI = new DiagonalizerType(1);
    
    /** Creates a new instance of DiagonalizerType */
    private DiagonalizerType(int type) {
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
     * Returns a description of the diagonalizer type
     *
     * @return a string indicating diagonalizer type
     */
    public String toString() {
        String description = "";
        
        if (this.equals(JACOBI)) {
            description = "Jacobi Diagonalization Method";
        } else {
            description = "No description available";
        } // end if
        
        return description;
    }
    
    /**
     * The method to check the equality of two objects of DiagonalizerType class.
     *
     * @return true/ false specifying the equality or inequality
     */
    public boolean equals(Object obj) {       
        if (this == obj) return true;
        
        return ((obj != null) 
                && (obj instanceof DiagonalizerType) 
                && (this.type == ((DiagonalizerType) obj).type));
    }
} // end of class DiagonalizerType
