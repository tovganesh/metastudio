/**
 * Sphere.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

/**
 * Represents a sphere
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Sphere extends AbstractGeometricObject {

    /** Creates a new instance of Sphere */
    public Sphere() {
        center = new Point3D();
        radius = 0.0;
    }

    /** Creates a new instance of Sphere */
    public Sphere(Point3D center, double radius) {
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
     * Return the total surface area of the sphere
     *
     * @return area of the sphere in specifed units
     */
    @Override
    public double totalSurfaceArea() {
        return 4.0 * Math.PI * radius * radius;
    }

    /**
     * Return the volume of the sphere
     * 
     * @return the volume of the sphere in specified units
     */
    @Override
    public double volume() {
        return 4.0/3.0 * Math.PI * radius * radius * radius;
    }
}
