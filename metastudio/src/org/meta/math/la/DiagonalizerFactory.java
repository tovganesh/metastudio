/*
 * DiagonalizerFactory.java
 *
 * Created on August 5, 2004, 7:20 AM
 */

package org.meta.math.la;

import java.lang.ref.WeakReference;

/**
 * Supplying Diagonalizers! Follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DiagonalizerFactory {
    
    private static WeakReference<DiagonalizerFactory> _diagonalizerFactory;
    
    /**
     * Holds value of property defaultDiagonalizer.
     */
    private Diagonalizer defaultDiagonalizer;
    
    /** Creates a new instance of DiagonalizerFactory */
    private DiagonalizerFactory() {
        defaultDiagonalizer = new JacobiDiagonalizer();                
    }
    
    /**
     * Get an instance (and the only one) of DiagonalizerFactory
     *
     * @return DiagonalizerFactory instance
     */
    public static DiagonalizerFactory getInstance() {
        if (_diagonalizerFactory == null) {
            _diagonalizerFactory = 
              new WeakReference<DiagonalizerFactory>(new DiagonalizerFactory());
        } // end if
        
        DiagonalizerFactory diagonalizerFactory = _diagonalizerFactory.get();
        
        if (diagonalizerFactory == null) {
            diagonalizerFactory  = new DiagonalizerFactory();
            _diagonalizerFactory = 
                    new WeakReference<DiagonalizerFactory>(diagonalizerFactory);
        } // end if
        
        return diagonalizerFactory;
    }
    
    /** 
     * Return an appropriate instance of Diagonalizer
     *
     * @param dt the diagonalizer type
     * @return appropriate Diagonalizer instance 
     */
    public Diagonalizer getDiagonalizer(DiagonalizerType dt) {
        if (dt.equals(DiagonalizerType.JACOBI)) {
            return new JacobiDiagonalizer();
        } else {
            throw new UnsupportedOperationException("Diagonalizer not " +
                                                "supported : " + dt.toString());
        } // end if
    }
    
    /**
     * Getter for property defaultDiagonalizer.
     * @return Value of property defaultDiagonalizer.
     */
    public Diagonalizer getDefaultDiagonalizer() {
        return this.defaultDiagonalizer;
    }
    
    /**
     * Setter for property defaultDiagonalizer.
     * @param defaultDiagonalizer New value of property defaultDiagonalizer.
     */
    public void setDefaultDiagonalizer(Diagonalizer defaultDiagonalizer) {
        this.defaultDiagonalizer = defaultDiagonalizer;
    }
    
} // end of class DiagonalizerFactory
