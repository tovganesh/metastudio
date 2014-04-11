/*
 * TriSetGenerator.java
 *
 * Created on September 29, 2005, 10:06 PM
 *
 */

package org.meta.shell.idebeans.graphics.surfaces;

import org.meta.math.geom.Triangle;
import org.meta.math.Matrix3D;

/**
 * Abstract class defining the mechanism of tri-set generators.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class TriSetGenerator {
    
    /** Creates a new instance of TriSetGenerator */
    public TriSetGenerator() {        
    }
    
    /**
     * Returns true or false indicating availablity of any further tri sets
     * for a 3D objcet or surface.
     *
     * @return a boolean indicative of availability of a tri-set for the object
     * under conctruction.
     */
    public abstract boolean isOver();
    
    /**
     * Returns the next visible tri set, after performing object based, self
     * backface culling.
     *
     * @return a valid Triangle object if something is available, else returns
     * a null.
     */
    public abstract Triangle nextVisibleTriSet();
    
    /**
     * method to apply scene transformations
     */
    public abstract void applyTransforms();        
    
    /**
     * resets the Triangle iterator
     */
    public abstract void resetTriSetIterator();
    
    /**
     * Holds value of property transform.
     */
    protected Matrix3D transform;

    /**
     * Getter for property transform.
     * @return Value of property transform.
     */
    public Matrix3D getTransform() {
        return this.transform;
    }

    /**
     * Setter for property transform.
     * @param transform New value of property transform.
     */
    public void setTransform(Matrix3D transform) {
        this.transform = transform;
    }
       
    /**
     * Number of elements generated.
     *
     * @return the size of the generated trisets.
     */
    public abstract int size();
} // end of class TriSetGenerator
