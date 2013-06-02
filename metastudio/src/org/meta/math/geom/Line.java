/**
 * Line.java
 *
 * Created on 29/04/2010
 */

package org.meta.math.geom;

/**
 * Represents a Line
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Line extends AbstractGeometricObject {

    /** Creates a new instance of Line */
    public Line() {
        startPoint = new Point3D();
        endPoint = new Point3D();
    }

    /** Creates a new instance of Line */
    public Line(Point3D stp, Point3D edp) {
        startPoint = stp;
        endPoint = edp;
    }

    protected Point3D startPoint;

    /**
     * Get the value of startPoint
     *
     * @return the value of startPoint
     */
    public Point3D getStartPoint() {
        return startPoint;
    }

    /**
     * Set the value of startPoint
     *
     * @param startPoint new value of startPoint
     */
    public void setStartPoint(Point3D startPoint) {
        this.startPoint = startPoint;
    }

    protected Point3D endPoint;

    /**
     * Get the value of endPoint
     *
     * @return the value of endPoint
     */
    public Point3D getEndPoint() {
        return endPoint;
    }

    /**
     * Set the value of endPoint
     *
     * @param endPoint new value of endPoint
     */
    public void setEndPoint(Point3D endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * The length of the line in appropriate units
     * 
     * @return the length of the current line
     */
    public double length() {
        return startPoint.distanceFrom(endPoint);
    }

    /**
     * The midpoint of this line
     * 
     * @return the midpoint of the line segment
     */
    public Point3D midPoint() {
       return startPoint.add(endPoint).mul(0.5);
    }
}
