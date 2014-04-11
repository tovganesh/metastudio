/**
 * Tetrahedron.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

/**
 * Represents a regular tetrahedron
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Tetrahedron extends AbstractGeometricObject {

    /** Creates a new instance of Tetrahedron */
    public Tetrahedron(Point3D origin, double sideLength) {
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

    /**
     * Return the points represting the vertices of this tetrahedron
     *
     * @return points representing the vertices of this tetrahedron
     */
    public Point3D [] getVertices() {
        Point3D [] edges = new Point3D[4];

        double factor = sideLength / (2.0 * Math.sqrt(2));

        edges[0] = new Point3D(factor, factor, factor).add(origin);
        edges[1] = new Point3D(-factor, -factor, factor).add(origin);
        edges[2] = new Point3D(-factor, factor, -factor).add(origin);
        edges[3] = new Point3D(factor, -factor, -factor).add(origin);

        return edges;
    }
}
