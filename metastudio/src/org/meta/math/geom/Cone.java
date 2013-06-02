/**
 * Cone.java
 *
 * Created on 29/04/2010
 */

package org.meta.math.geom;

/**
 * Represents a cone
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Cone extends AbstractGeometricObject {

    /** Creates a new instance of Cone */
    public Cone() {
        baseRadius = 0.0;
        height     = 0.0;
    }

    /** Creates a new instance of Cone */
    public Cone(double baseRadius, double height) {
        this.baseRadius = baseRadius;
        this.height     = height;
    }

    protected double baseRadius;

    /**
     * Get the value of baseRadius
     *
     * @return the value of baseRadius
     */
    public double getBaseRadius() {
        return baseRadius;
    }

    /**
     * Set the value of baseRadius
     *
     * @param baseRadius new value of baseRadius
     */
    public void setBaseRadius(double baseRadius) {
        this.baseRadius = baseRadius;
    }

    protected double height;

    /**
     * Get the value of height
     *
     * @return the value of height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Set the value of height
     *
     * @param height new value of height
     */
    public void setHeight(double height) {
        this.height = height;
    }
}
