/**
 * Circle.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

/**
 * Represents a circle
 *
 * Note: reason for this being not inherited from Ellipse is saving space
 * for storing individual members.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Circle extends AbstractGeometricObject {

    /** Creates a new instance of Circle */
    public Circle() {
        center = new Point3D();
        radius = 0.0;
    }

    /** Creates a new instance of Circle */
    public Circle(Point3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    protected Point3D center;

    /**
     * Get the value of center
     *
     * @return the value of center
     */
    public Point3D getCenter() {
        return center;
    }

    /**
     * Set the value of center
     *
     * @param center new value of center
     */
    public void setCenter(Point3D center) {
        this.center = center;
    }

    protected double radius;

    /**
     * Get the value of radius
     *
     * @return the value of radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Set the value of radius
     *
     * @param radius new value of radius
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Returns the area of this circle
     *
     * @return the area of the circle in specified units
     */
    @Override
    public double totalSurfaceArea() {
        return Math.PI * radius * radius;
    }

    /**
     * Return the circumfrence of this circle
     *
     * @return the circumfrence of the circle in specified units
     */
    public double circumference() {
        return 2.0 * Math.PI * radius;
    }
}
