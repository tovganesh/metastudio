/*
 * Triangle.java
 *
 * Created on September 29, 2005, 9:59 PM
 *
 */

package org.meta.math.geom;

/**
 * A structure to hold a set of three vertices, that represent a triangle.
 * Used in construction of 3D objects and surfaces.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Triangle extends AbstractGeometricObject {
    
    /** Creates a new instance of Triangle */
    public Triangle() {
    }

    /** Creates a new instance of Triangle */
    public Triangle(Point3DI p1, Point3DI p2, Point3DI p3) {
        point1 = p1;
        point2 = p2;
        point3 = p3;
    }
    
    /**
     * Holds value of property point1.
     */
    private Point3DI point1;

    /**
     * Getter for property point1.
     * @return Value of property point1.
     */
    public Point3DI getPoint1() {
        return this.point1;
    }

    /**
     * Setter for property point1.
     * @param point1 New value of property point1.
     */
    public void setPoint1(Point3DI point1) {
        this.point1 = point1;
    }

    /**
     * Holds value of property point2.
     */
    private Point3DI point2;

    /**
     * Getter for property point2.
     * @return Value of property point2.
     */
    public Point3DI getPoint2() {
        return this.point2;
    }

    /**
     * Setter for property point2.
     * @param point2 New value of property point2.
     */
    public void setPoint2(Point3DI point2) {
        this.point2 = point2;
    }

    /**
     * Holds value of property point3.
     */
    private Point3DI point3;

    /**
     * Getter for property point3.
     * @return Value of property point3.
     */
    public Point3DI getPoint3() {
        return this.point3;
    }

    /**
     * Setter for property point3.
     * @param point3 New value of property point3.
     */
    public void setPoint3(Point3DI point3) {
        this.point3 = point3;
    }
    
    /**
     * Get the X points
     *
     * @return an array of X points
     */
    public double [] getXPoints() {
        return new double[] {point1.getX(), point2.getX(), point3.getX()};
    }
    
    /**
     * Get the Y points
     *
     * @return an array of Y points
     */
    public double [] getYPoints() {
        return new double[] {point1.getY(), point2.getY(), point3.getY()};
    }
    
    /**
     * Get the Z points
     *
     * @return an array of Z points
     */
    public double [] getZPoints() {
        return new double[] {point1.getZ(), point2.getZ(), point3.getZ()};
    }
    
    /**
     * Get the X points (as integers)
     *
     * @return an array of X points
     */
    public int [] getXPointsI() {
        return new int[] {point1.getCurrentX(), 
                          point2.getCurrentX(), 
                          point3.getCurrentX()};
    }
    
    /**
     * Get the Y points (as integers)
     *
     * @return an array of Y points
     */
    public int [] getYPointsI() {
        return new int[] {point1.getCurrentY(), 
                          point2.getCurrentY(), 
                          point3.getCurrentY()};
    }
    
    /**
     * Get the Z points (as integers)
     *
     * @return an array of Z points
     */
    public int [] getZPointsI() {
        return new int[] {point1.getCurrentZ(), 
                          point2.getCurrentZ(), 
                          point3.getCurrentZ()};
    }
    
    /**
     * return string representation
     */
    @Override
    public String toString() {
        return "P1: <" + point1 + ">, P2: <" + point2 
                + ">, P3: <" + point3 + ">";
    }
    
} // end of class Triangle
