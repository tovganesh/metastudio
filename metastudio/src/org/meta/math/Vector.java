/*
 * Vector.java
 *
 * Created on November 16, 2003, 10:58 AM
 */

package org.meta.math;

/**
 * Defines an R^n space vector.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Vector implements Cloneable {        
  
    /** Holds value of property vector. */
    protected double[] vector;
    
    /** 
     * Creates a new instance of Vector 
     *
     * @param size the vector.length of this vector
     */
    public Vector(int size) {
        vector = new double[size];
    }        
    
    /** 
     * Creates a new instance of Vector from an double array 
     *
     * @param array the array from which this vector will be made
     */
    public Vector(double[] array) {
        this.vector = array;
    }
    
    /** 
     * Creates a new instance of Vector from a square Matrix 
     *
     * @param a the square matrix from which this vector will be made
     */
    public Vector(Matrix a) {
        double [][] matrix = a.getMatrix();
        
        this.vector = new double[matrix.length*matrix.length];
        
        int ii = 0, i, j;
        for(i=0; i<matrix.length; i++) {
            for(j=0; j<matrix.length; j++) {
                this.vector[ii] = matrix[i][j];
                ii++;
            } // end for
        } // end for
    }
    
    /** The size of this vector
     * @return the size of this vector
     *
     */
    public int getSize() {
        return this.vector.length;
    }
    
    /** Indexed getter for property vector.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     *
     */
    public double getVector(int index) {
        return this.vector[index];
    }
    
    /** Getter for property vector.
     * @return Value of property vector.
     *
     */
    public double[] getVector() {
        return this.vector;
    }
    
    /** Indexed setter for property vector.
     * @param index Index of the property.
     * @param vector New value of the property at <CODE>index</CODE>.
     *
     */
    public void setVector(int index, double vector) {
        this.vector[index] = vector;
    }
    
    /** Setter for property vector.
     * @param vector New value of property vector.
     *
     */
    public void setVector(double[] vector) {
        this.vector = vector;
    }
    
    /**
     * Make this vector a Null vector
     */
    public void makeZero() {
        for(int i=0; i<vector.length; i++) vector[i] = 0.0;
    }
    
    /**
     * addition of two vectors
     *
     * @param b Vector to be added to the current vector
     * @return the addition of two vector or null if that is not possible
     */
    public Vector add(Vector b) {
        if (this.vector.length != b.vector.length) {
            return null;
        } // end if
        
        Vector result = new Vector(vector.length);
        
        for(int i=0; i<vector.length; i++) {
            result.vector[i] = this.vector[i] + b.vector[i];
        } // end for
        
        return result;
    }
    
    /**
     * substraction of two vectors (this - b)
     *
     * @param b the vector to be substracted from the current vector
     * @return the addition of two vector or null if that is not possible
     */
    public Vector sub(Vector b) {
        if (this.vector.length != b.vector.length) {
            return null;
        } // end if
        
        Vector result = new Vector(vector.length);
        
        for(int i=0; i<vector.length; i++) {
            result.vector[i] = this.vector[i] - b.vector[i];
        } // end for
        
        return result;
    }
    
    /**
     * multiplication of this vector with a scalar k
     *
     * @param k the scalar to be multiplied to this vector
     * @return the result !
     */
    public Vector mul(double k) {        
        Vector result = new Vector(vector.length);
        
        for(int i=0; i<vector.length; i++) {
            result.vector[i] = this.vector[i] * k;
        } // end for
        
        return result;
    }
    
    /**
     * The dot product of two vectors (a.b)
     *
     * @param b the vector with which the dot product is to be evaluated
     * @return a double value which is the result of the dot product
     */    
    public double dot(Vector b) {
        if (this.vector.length != b.vector.length) {
            return Double.NaN;
        } // end if
        
        double result = 0.0;
        
        for(int i=0; i<vector.length; i++) {
            result += this.vector[i] * b.vector[i];
        } // end for
        
        return result;
    }        
    
    /**
     * The magnitude of this vector
     *
     * @return the magnitude (length) of this vector.      
     */
    public double magnitude() {
        double length;
        
        length = 0.0;
        
        for(int i=0; i<vector.length; i++) {
            length += vector[i] * vector[i];
        } // end for
        
        return Math.sqrt(length);
    }
    
    /**
     * get the normalized form of this vector
     *
     * @return the normalized form of this vector
     */
    public Vector normalize() {
        double magnitude = magnitude();
        
        Vector n = new Vector(vector.length);
        
        for(int i=0; i<vector.length; i++) {
            n.vector[i] = vector[i] / magnitude;
        } // end for
        
        return n;
    }
    
    /**
     * negate this vector
     *
     * @return the reverse vector
     */
    public Vector negate() {      
        Vector n = new Vector(vector.length);
        
        for(int i=0; i<vector.length; i++) {
            n.vector[i] = -vector[i];
        } // end for
        
        return n;
    }
    
    /**
     * clone this vector ;)
     * Cloning is getting interesting! :)
     *
     * @return the clone
     */
    @Override
    public Object clone() {
        Vector theCopy = new Vector(vector.length);
        
        for(int i=0; i<vector.length; i++) {
            theCopy.vector[i] = vector[i];
        } // end for
               
        return theCopy;
    }
    
    /**
     * the overridden toString() method
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for(int i=0; i<vector.length; i++) {
            sb.append(vector[i]);
            sb.append("\n");
        } // end for
        
        return sb.toString();
    }

    /**
     * Returns the max norm of this matrix
     *
     * @return max(abs(matrix))
     */
    public double maxNorm() {
        double mx = Math.abs(vector[0]);

        for(int i=0; i<vector.length; i++)
            if (mx < Math.abs(vector[i]))
                 mx = Math.abs(vector[i]);

        return mx;
    }
} // end of class Vector
