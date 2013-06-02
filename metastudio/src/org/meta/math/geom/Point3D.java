/*
 * Point3D.java
 *
 * Created on May 6, 2003, 10:48 PM
 */

package org.meta.math.geom;

import java.io.Serializable;

/**
 * A class representing a point in 3D space.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Point3D extends AbstractGeometricObject
                     implements Cloneable, Serializable {
    
    /** Holds value of property x. */
    private double x;
    
    /** Holds value of property y. */
    private double y;
    
    /** Holds value of property z. */
    private double z;
    
    /** Creates a new instance of Point3D */
    public Point3D() {
        this(0.0, 0.0, 0.0);
    }
    
    /** Creates a new instance of Point3D */
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /** Getter for property x.
     * @return Value of property x.
     */
    public double getX() {
        return this.x;
    }
    
    /** Setter for property x.
     * @param x New value of property x.
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Add a shift to X 
     * 
     * @param addX the shift
     */
    public void addToX(double addX) {
        this.x += addX;
    }

    /** Getter for property y.
     * @return Value of property y.
     */
    public double getY() {
        return this.y;
    }
    
    /** Setter for property y.
     * @param y New value of property y.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Add a shift to Y
     *
     * @param addY the shift
     */
    public void addToY(double addY) {
        this.y += addY;
    }
    
    /** Getter for property z.
     * @return Value of property z.
     */
    public double getZ() {
        return this.z;
    }
    
    /** Setter for property z.
     * @param z New value of property z.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Add a shift to Z
     *
     * @param addZ the shift
     */
    public void addToZ(double addZ) {
        this.z += addZ;
    }

    /**
     * overloaded toString() method.
     */
    @Override
    public String toString() {        
        return x + " " + y + " " + z;
    }
    
    /**
     * overloaded equals() method
     */
    @Override
    public boolean equals(Object obj) { 
        if (this == obj) return true;
        
        if ((obj == null) || (!(obj instanceof Point3D))) {
            return false;
        } else {
            Point3D o = (Point3D) obj;
        
            return ((x == o.x) && (y == o.y) && (z == o.z));
        } // end if
    }
    
    /**
     * Check if this point is zero.
     *
     * @return true if all the components are zero, else false
     */
    public boolean isZero() {
        return (   (this.x == 0)
                && (this.y == 0)
                && (this.z == 0)
               );
    }
    
    /**
     * Simple method to find the distance between two points.
     * @param point - the point to which the distance is to be found
     * @return the distance between two points
     */    
    public double distanceFrom(Point3D point) {
        double xa = this.x - point.x;
        double ya = this.y - point.y;
        double za = this.z - point.z;
        
        return Math.sqrt((xa*xa) + (ya*ya) + (za*za));
    }

    /**
     * Simple method to find the distance<sup>2</sup> between two points.
     * @param point - the point to which the distance is to be found
     * @return the distance between two points
     */    
    public double distanceSquaredFrom(Point3D point) {
        double xa = this.x - point.x;
        double ya = this.y - point.y;
        double za = this.z - point.z;
        
        return ((xa*xa) + (ya*ya) + (za*za));
    }
    
    /**
     * addition of two points
     *
     * @param b the Point3D to be added to the current point
     * @return the addition of two points
     */
     public Point3D add(Point3D b) {                
        return new Point3D(this.x + b.x, this.y + b.y, this.z + b.z);
    }
    
     /**
     * addition of this point with a constant k
     *
     * @param k the constant to be added to this point
     * @return the result !
     */
    public Point3D add(double k) {        
        return new Point3D(this.x + k, this.y + k, this.z + k);
    }
    
    /**
     * multiplication of this point with a constant k
     *
     * @param k the constant to be multiplied to this point
     * @return the result !
     */
    public Point3D mul(double k) {        
        return new Point3D(this.x * k, this.y * k, this.z * k);
    }
    
    /**
     * substraction of two points (this - b)
     *
     * @param b the Point3D to be substracted from the current point
     * @return the substraction of two point
     */
    public Point3D sub(Point3D b) {                
        return new Point3D(this.x - b.x,this.y - b.y, this.z - b.z);
    }
    
    /**
     * i do some cloning business ;)
     */    
    @Override
    public Object clone() {        
        return new Point3D(this.x, this.y, this.z);
    }
    
} // end of class Point3D
