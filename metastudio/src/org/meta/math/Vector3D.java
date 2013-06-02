/*
 * Vector3D.java
 *
 * Created on November 16, 2003, 1:50 PM
 */

package org.meta.math;

import java.io.Serializable;
import org.meta.math.geom.Point3D;

/**
 * A special class representing R^3 vector.
 * This class is purposely not derived from Vector class, use toVector()
 * method get an instance compliant with Vector.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Vector3D implements Serializable {
    
    /** Null vector in 3 dimensional space */
    public final static Vector3D NULL_VECTOR = new Vector3D(0.0, 0.0, 0.0);
    
    /** Unit vector along X in 3 dimensional space */
    public final static Vector3D UX = new Vector3D(1.0, 0.0, 0.0);
    
    /** Unit vector along Y in 3 dimensional space */
    public final static Vector3D UY = new Vector3D(0.0, 1.0, 0.0);
    
    /** Unit vector along Z in 3 dimensional space */
    public final static Vector3D UZ = new Vector3D(0.0, 0.0, 1.0);
    
    /** the componanats of this vector */
    private double i, j, k;
    
    /** hash code cached here */
    private volatile int hashCode = 0;
    
    /** Creates a new instance of Vector3D */
    public Vector3D() {
        i = 0.0;
        j = 0.0;
        k = 0.0;
    }
    
    public Vector3D(double i, double j, double k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }
    
    public Vector3D(Point3D point) {
        this.i = point.getX();
        this.j = point.getY();
        this.k = point.getZ();
    }                
    
    /**
     * addition of two vectors
     *
     * @param b the Vector3D to be added to the current vector
     * @return the addition of two vector 
     */
    public Vector3D add(Vector3D b) {
        return new Vector3D(this.i + b.i,
                this.j + b.j,
                this.k + b.k);
    }
    
    /**
     * addition of a point as a vector
     *
     * @param b the Point3D to be added to the current vector
     * @return the addition of point (as vector) and this vector 
     */
    public Vector3D add(Point3D b) {
        return add(new Vector3D(b));
    }
    
    /**
     * multiplication of this vector with a scalar k
     *
     * @param k the scalar to be multiplied to this vector
     * @return the result !
     */
    public Vector3D mul(double k) {
        return new Vector3D(this.i * k, this.j * k, this.k * k);
    }
    
    /**
     * substraction of two vectors (this - b)
     *
     * @param b the Vector3D to be substracted from the current vector
     * @return the substraction of two vector 
     */
    public Vector3D sub(Vector3D b) {
        return new Vector3D(this.i - b.i, this.j - b.j, this.k - b.k);
    }
    
    /**
     * substraction of a point as a vector
     *
     * @param b the Point3D to be substracted to the current vector
     * @return the substraction of point (as vector) and this vector 
     */
    public Vector3D sub(Point3D b) {
        return sub(new Vector3D(b));
    }
    
    /**
     * The dot product of two vectors (a.b)
     *
     * @param b the vector with which the dot product is to be evaluated
     * @return a double value which is the result of the dot product
     */    
    public double dot(Vector3D b) {        
        
        double result = 0.0;
                
        result += this.i * b.i;
        result += this.j * b.j;
        result += this.k * b.k;
        
        return result;
    }        
    
    /**
     * The dot product of two vectors (a.b), point considered as vector.
     *
     * @param b the vector (Point3D) with which the dot product is to be evaluated
     * @return a double value which is the result of the dot product
     */
    public double dot(Point3D b) {
        return dot(new Vector3D(b));
    }
    
    /**
     * The cross product of two vectors (a.b). The corss product is only valid
     * if both the vectors are in R^3 space.
     *
     * @param b the vector with which the cross product is to be evaluated
     * @return the result of the cross product 
     */    
    public Vector3D cross(Vector3D b) {
        return new Vector3D(j * b.k - k * b.j,
                k * b.i - i * b.k,
                i * b.j - j * b.i);
    }
    
    /**
     * The cross product of two vectors (a.b), point considered as vector.
     *
     * @param b the vector (Point3D) with which the cross product is 
     *        to be evaluated
     * @return the result of the cross product
     */
    public Vector3D cross(Point3D b) {
        return cross(new Vector3D(b));
    }
    
    /**
     * Mixed product of there vectors:
     * v1 . ( v2 x v3 )
     *  
     * @param v2 the second vector
     * @param v3 the third vector
     *
     * @return the value of mixed product
     */
    public double mixedProduct(Vector3D v2, Vector3D v3) {
        return this.dot(v2.cross(v3));
    }
    
    /**
     * The magnitude of this vector
     *
     * @return the magnitude (length) of this vector.      
     */
    public double magnitude() {
        double length;
        
        length = 0.0;
                
        length += i * i;
        length += j * j;
        length += k * k;
        
        return Math.sqrt(length);
    }
    
    /**
     * Find the angle made with the vector.
     *
     * @return the angle in radians
     */
    public double angleWith(Vector3D b) {
        double aDotb = this.dot(b);
        double ab    = magnitude() * b.magnitude();
        
        return Math.acos(aDotb / ab);
    }
    
    /**
     * get the normalized form of this vector
     *
     * @return the normalized form of this vector
     */
    public Vector3D normalize() {
        double magnitude = magnitude();

        return new Vector3D(i / magnitude, j / magnitude, k / magnitude);
    }
    
    /**
     * negate this vector
     *
     * @return the reverse vector
     */
    public Vector3D negate() {
        return new Vector3D(-i, -j, -k);
    }

    /**
     * clone this vector ;)
     * Cloning is getting interesting! :)
     *
     * @return the clone
     */
    @Override
    public Object clone() {
        return new Vector3D(i, j, k);
    }
    
    /**
     * method to convert this object to one representing a general Vector
     *
     * @return Vector the converted form
     */
    public Vector toVector() {
        return new Vector(new double [] { i, j, k } );
    }
    
    /**
     * method to convert this object to one representing a general Point3D
     *
     * @return Point3D the converted form
     */
    public Point3D toPoint3D() {
        return new Point3D(i, j, k);
    }
    
    /**
     * the overridden toString() method
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
                
        sb.append(i + " i + ");
        sb.append(j + " j + ");
        sb.append(k + " k ");             
        
        return sb.toString();
    }
    
    /**
     * overloaded equals() method
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if ((obj == null) || (!(obj instanceof Vector3D))) {
            return false;
        } else {
            Vector3D o = (Vector3D) obj;
        
            return ((i == o.i) && (j == o.j) && (k == o.k));
        } // end if
    }
    
    /**
     * overriden hashCode() method
     * 
     * @return int - the hashCode
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17; // prime number!
            long c;
            
            c = Double.doubleToLongBits(i);
            result = 37 * result + (int)(c ^ (c >>> 32));
            
            c = Double.doubleToLongBits(j);
            result = 37 * result + (int)(c ^ (c >>> 32));
            
            c = Double.doubleToLongBits(k);
            result = 37 * result + (int)(c ^ (c >>> 32));
            
            hashCode = result;
        } // end if
        
        return hashCode;
    }
    
    /** Getter for property i.
     * @return Value of property i.
     *
     */
    public double getI() {
        return this.i;
    }
    
    /** Setter for property i.
     * @param i New value of property i.
     *
     */
    public void setI(double i) {
        this.i = i;
    }
    
    /** Getter for property j.
     * @return Value of property j.
     *
     */
    public double getJ() {
        return this.j;
    }
    
    /** Setter for property j.
     * @param j New value of property j.
     *
     */
    public void setJ(double j) {
        this.j = j;
    }
    
    /** Getter for property k.
     * @return Value of property k.
     *
     */
    public double getK() {
        return this.k;
    }
    
    /** Setter for property k.
     * @param k New value of property k.
     *
     */
    public void setK(double k) {
        this.k = k;
    }
    
} // end of class Vector3D
