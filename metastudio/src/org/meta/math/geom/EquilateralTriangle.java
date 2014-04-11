/**
 * EquilateralTriangle.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

/**
 * Represents a triangle with all equal sides
 *
 * Note: reason for this being not inherited from Triangle is saving space
 * for storing individua memmbers.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class EquilateralTriangle extends AbstractGeometricObject {

    /** Creates a new instance of EquilateralTriangle */
    public EquilateralTriangle() {
        origin = new Point3D();
        sideLength = 0.0;
    }

    /** Creates a new instance of EquilateralTriangle */
    public EquilateralTriangle(Point3D origin, double sideLength) {
        this.origin = origin;
        this.sideLength = sideLength;
    }

    protected Point3D origin;

    /**
     * Get the value of origin
     *
     * @return the value of origin
     */
    public Point3D getOrigin() {
        return origin;
    }

    /**
     * Set the value of origin
     *
     * @param origin new value of origin
     */
    public void setOrigin(Point3D origin) {
        this.origin = origin;
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
}
