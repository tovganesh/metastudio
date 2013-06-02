/**
 * Square.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

/**
 * A Square.
 *
 * Note: reason for this being not inherited from Rectangle is saving space
 * for storing individua memmbers.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Square extends AbstractGeometricObject {

    /** Creates a new instance of Square */
    public Square() {
        sideLength = 0.0;
    }

    /** Creates a new instance of Square */
    public Square(double sideLength) {
        this.sideLength = sideLength;
    }

    protected double sideLength;

    /**
     * Get the value of sideLength
     *
     * @return the value of sideLength
     */
    public double getSideLength() {
        return sideLength;
    }

    /**
     * Set the value of sideLength
     *
     * @param sideLength new value of sideLength
     */
    public void setSideLength(double sideLength) {
        this.sideLength = sideLength;
    }

    /**
     * Return area of the square
     * 
     * @return area of square in specified units
     */
    @Override
    public double totalSurfaceArea() {
        return sideLength*sideLength;
    }
}
