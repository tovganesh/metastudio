/*
 * MathUtil.java
 *
 * Created on November 16, 2003, 2:44 PM
 */

package org.meta.math;

import org.meta.math.geom.Point3D;

/**
 * A collection of few misc. utility math functions.
 * All methods are static and the class cannot be instantiated.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public final class MathUtil {
    
    /** Creates a new instance of MathUtil */
    private MathUtil() {
    }
    
    /**
     * Method to convert radians to degrees
     *
     * @param radians - the value
     * @return the equivalent in degrees
     */
    public static double toDegrees(double radians) {
        return (radians * 180.0 / Math.PI);
    }
    
    /**
     * Method to convert degrees to radians
     *
     * @param degrees - the value
     * @return the equivalent in radians
     */
    public static double toRadians(double degrees) {
        return (degrees * Math.PI / 180.0);
    }

    /**
     * @param val a double value
     * @return true if val is within 2e-6 of zero
     */
    public static boolean isNearZero(double val) {
        return isNearZero(val, 2e-6);
    }

    /**
     *
     * @param val a double value
     * @param epsilon the "near" distance from zero
     * @return true if val is within a distance epsilon of zero
     */
    public static boolean isNearZero(double val, double epsilon) {
        return (Math.abs(val) < epsilon);
    }
    
    /**
     * Method to find the angle in radians defined by three points
     * v1-v2-v3.
     *
     * @param v1 the first point
     * @param v2 the second point (central angle)
     * @param v3 the third point
     * @return the angle defined
     */
    public static double findAngle(Point3D v1, Point3D v2, Point3D v3) {
        Vector3D v12 = new Vector3D(v2.sub(v1));
        Vector3D v32 = new Vector3D(v2.sub(v3));
        
        return v12.angleWith(v32);
    }
    
    /**
     * Method to find the dihedral angle defined by planes v1-v2-v3 and
     * v2-v3-v4.
     *
     * @param v1 first point
     * @param v2 second point
     * @param v3 third point (2nd and 3rd point define the angle)
     * @param v4 the fourth angle
     * @return the dihedral angle defined
     */
    public static double findDihedral(Point3D v1, Point3D v2, 
                                      Point3D v3, Point3D v4) {
        // normal of plane 1
        Vector3D v12 = new Vector3D(v2.sub(v1));
        Vector3D v32 = new Vector3D(v2.sub(v3));
        Vector3D n123 = v12.cross(v32).normalize();
        
        // normal of plane 2
        Vector3D v23  = new Vector3D(v3.sub(v2));
        Vector3D v43  = new Vector3D(v3.sub(v4));
        Vector3D n234 = v23.cross(v43).normalize();
    
        // sign of the dihedral
        double sign = v32.mixedProduct(n123, n234);
        
        if (sign >= 0.0) sign = -1.0;
        else             sign = 1.0;
        
        // and find the angle between the two planes    
        return n123.angleWith(n234) * sign;
    }
    
    /**
     * compute N!
     *
     * @param n the n, whose factorial is to be found
     * @return the factorial
     */
    public static long factorial(int n) {
        long value = 1;
        
        while(n > 1) {
            value = value * n;
            n--;
        } // end while
        
        return value;
    }
    
    /**
     * compute double N! ... (1*3*5*...*n)
     *
     * @param n the n, whose factorial is to be found
     * @return the factorial
     */
    public static long factorial2(int n) {
        long value = 1;
        
        while(n > 0) {
            value = value * n;
            n-=2;
        } // end while
        
        return value;
    }
    
    /**
     * Does ( a! / b! / (a-2*b)! )
     * 
     * @param a the first term
     * @param b the second term
     * @return ( a! / b! / (a-2*b)! )
     */
    public static double factorialRatioSquared(int a, int b) {
        return factorial(a) / factorial(b) / factorial(a-2*b);
    }

    /**
     * Pre-factor of binomial expansion.
     *
     * From Augspurger and Dykstra
     */
    public static double binomialPrefactor(int s, int ia, int ib,
                                           double xpa, double xpb) {
        double sum = 0.0;

        for(int t=0; t<(s+1); t++) {
            if(((s-ia) <= t) && (t <= ib)) {
                sum += binomial(ia, s-t) * binomial(ib, t)
                      * Math.pow(xpa, ia-s+t) * Math.pow(xpb, ib-t);
            } // end if
        } // end for

        return sum;
    }

    /**
     * the binomial coefficient
     */
    public static double binomial(int i, int j) {
        return (MathUtil.factorial(i) / MathUtil.factorial(j)
                        / MathUtil.factorial(i - j));
    }

    /**
     * Relative difference between two floating point numbers, as described at:
     * <a href="http://c-faq.com/fp/fpequal.html"/>
     *  http://c-faq.com/fp/fpequal.html</a>
     *
     * @param a first number
     * @param b second number
     * @return the relative difference
     */
    public static double relativeDifference(double a, double b) {
	double c = Math.abs(a);
	double d = Math.abs(b);

	d = Math.max(c, d);

	return d == 0.0 ? 0.0 : Math.abs(a - b) / d;
    }
} // end of class MathUtil
