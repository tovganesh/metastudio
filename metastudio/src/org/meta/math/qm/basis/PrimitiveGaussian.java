/*
 * PrimitiveGaussian.java
 *
 * Created on July 22, 2004, 7:09 AM
 */

package org.meta.math.qm.basis;

import org.meta.math.MathUtil;
import org.meta.math.geom.Point3D;
import org.meta.math.Vector3D;
import org.meta.math.qm.integral.Integrals;

/**
 * The class defines a primitive Gaussian (PG) and the operations on it.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PrimitiveGaussian {
    
    /**
     * Holds value of property exponent.
     */
    private double exponent;
    
    /**
     * Holds value of property origin.
     */
    private Point3D origin;
    
    /**
     * Holds value of property powers.
     */
    private Power powers;
    
    /**
     * Holds value of property coefficient.
     */
    private double coefficient;
    
    /**
     * normalization factor
     */
    private double normalization;
    
    /** 
     * Creates a new instance of PrimitiveGaussian 
     *    
     * @param origin - the (x, y, z) on which this Gaussian is centered
     * @param powers - the powers of this Gaussian
     * @param exponent - the exponent for this PG
     * @param coefficient - the coefficient for this PG
     */    
    public PrimitiveGaussian(Point3D origin, Power powers, 
                             double exponent, double coefficient) {
        this.origin      = origin;
        this.powers      = powers;
        this.exponent    = exponent;
        this.coefficient = coefficient;
        
        this.normalization = 1;
        
        // normalise this PG
        normalize();
    }
        
    /**
     * Getter for property exponent.
     * @return Value of property exponent.
     */
    public double getExponent() {
        return this.exponent;
    }
    
    /**
     * Setter for property exponent.
     * @param exponent New value of property exponent.
     */
    public void setExponent(double exponent) {
        this.exponent = exponent;
    }
    
    /**
     * Getter for property origin.
     * @return Value of property origin.
     */
    public Point3D getOrigin() {
        return this.origin;
    }
    
    /**
     * Setter for property origin.
     * @param origin New value of property origin.
     */
    public void setOrigin(Point3D origin) {
        this.origin = origin;
    }
    
    /**
     * Getter for property powers.
     * @return Value of property powers.
     */
    public Power getPowers() {
        return this.powers;
    }
    
    /**
     * Setter for property powers.
     * @param powers New value of property powers.
     */
    public void setPowers(Power powers) {
        this.powers = powers;
    }
    
    /**
     * Getter for property coefficient.
     * @return Value of property coefficient.
     */
    public double getCoefficient() {
        return this.coefficient;
    }
    
    /**
     * Setter for property coefficient.
     * @param coefficient New value of property coefficient.
     */
    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    /**
     * Gaussian product center.
     * Return a new PG with product of this PG with a new PG. The powers of the
     * resulting PG is set to 0,0,0 and the coefficient to 0.0
     *
     * @param pg the PG with which to multiply
     * @return the product of the PG
     */
    public PrimitiveGaussian mul(PrimitiveGaussian pg) {
        double gamma = exponent + pg.exponent;
        Point3D newOrigin = new Point3D(
          (exponent * origin.getX() + pg.exponent * pg.origin.getX()) / gamma,
          (exponent * origin.getY() + pg.exponent * pg.origin.getY()) / gamma,
          (exponent * origin.getZ() + pg.exponent * pg.origin.getZ()) / gamma
        );

        return new PrimitiveGaussian(newOrigin, new Power(0,0,0), gamma, 0.0);
    }

    private final static double PI_RAISE_TO_1DOT5 = Math.pow(Math.PI, 1.5);
    
    /**
     * Normalize this primitive Gaussian.
     *
     * <br> <i>H. Phys. Soc. Japan,</i> <b>21</b>, 2313, 1966 <br>
     */
    public void normalize() {
        int l = powers.getL(), 
            m = powers.getM(),
            n = powers.getN();       
        
        normalization = Math.sqrt(Math.pow(2, 2 * (l + m + n) + 1.5) *
                                   Math.pow(exponent, l + m + n + 1.5) /
                                   MathUtil.factorial2(2 * l - 1) / 
                                   MathUtil.factorial2(2 * m - 1) /
                                   MathUtil.factorial2(2 * n - 1) /
                                   PI_RAISE_TO_1DOT5);
    }
    
    /**
     * Overlap matrix element with another PrimitiveGaussian
     *
     * @param pg the PrimitiveGaussian with which the overlap is to be
     *        be determined.
     * @return the overlap value
     */
    public double overlap(PrimitiveGaussian pg) {
        return (normalization * pg.normalization
                * Integrals.overlap(exponent, powers, origin, 
                                    pg.exponent, pg.powers, pg.origin)
               );
    }
    
    /**
     * Kinetic Energy (KE) matrix element with another PrimitiveGaussian
     *
     * @param pg the PrimitiveGaussian with which KE is to be determined.
     * @return the KE value
     */
    public double kinetic(PrimitiveGaussian pg) {
        return (normalization * pg.normalization
                * Integrals.kinetic(exponent, powers, origin, 
                                    pg.exponent, pg.powers, pg.origin)
               );
    }
    
    /**
     * Nuclear matrix element with another PrimitiveGaussian
     *
     * @param pg the PrimitiveGaussian with which nuclear interaction
     *        is to be determined.
     * @param center the center at which nuclear energy is to be computed
     * @return the nuclear value
     */
    public double nuclear(PrimitiveGaussian pg, Point3D center) {
        return (Integrals.nuclearAttraction(origin, normalization, powers,
                           exponent, pg.origin, pg.normalization, pg.powers,
                           pg.exponent, center)
               );
    }

    /**
     * Return the nuclear gradient of this PG w.r.t a center
     *
     * @param pg the other PG
     * @param center the reference center
     * @return partial derivatives w.r.t the center
     */
    public Vector3D nuclearAttractionGradient(PrimitiveGaussian pg, Point3D center) {
        Vector3D nder = new Vector3D();

        // TODO:
        
        return nder;
    }
    
    /**
     * The amplitude of this primitive Gaussian at a given point.
     *
     * @param point the reference point
     * @return the amplitude of this PG at the specified point
     */
    public double amplitude(Point3D point) {
       int l = powers.getL(),
           m = powers.getM(),
           n = powers.getN();
       double x = point.getX() - origin.getX(),
              y = point.getY() - origin.getY(),
              z = point.getZ() - origin.getZ();

       return (normalization * coefficient
               * Math.pow(x, l) * Math.pow(y, m) * Math.pow(z, n) 
               * Math.exp(-exponent * (x*x + y*y + z*z)));
    }

    /**
     * Calculate Laplacian at the specified point
     *
     * @param point the point where Laplacian is to be computed
     * @return the Laplacian at this point
     */
    public double laplacian(Point3D point) {
        double value = 0.0;
        double x = point.getX() - origin.getX(),
               y = point.getY() - origin.getY(),
               z = point.getZ() - origin.getZ();
        double x2 = x*x;
        double y2 = y*y;
        double z2 = z*z;
        int l = powers.getL(),
            m = powers.getM(),
            n = powers.getN();

        value = (l*(l-1)/x2 + m*(m-1)/y2 + n*(n-1)/z2) 
                + 4*exponent*exponent*(x2+y2+z2)
                - 2*exponent*(2*(l+m+n)+3);

        return value*normalization*coefficient*amplitude(point);
    }
    
    /**
     * Evaluate gradient of this function at a given point
     *
     * @param point the point where gradient is to be evaluated
     * @return partial derivatives with respect to x, y, z
     */
    public Vector3D gradient(Point3D point) {
        int l = powers.getL(),
            m = powers.getM(),
            n = powers.getN();
        double x = point.getX() - origin.getX(),
               y = point.getY() - origin.getY(),
               z = point.getZ() - origin.getZ();

        double fx = Math.pow(x, l) * Math.exp(-exponent*Math.pow(x, 2));
        double fy = Math.pow(y, m) * Math.exp(-exponent*Math.pow(y, 2));
        double fz = Math.pow(z, n) * Math.exp(-exponent*Math.pow(z, 2));
        double gx = -2.0 * exponent * x * fx;
        double gy = -2.0 * exponent * y * fy;
        double gz = -2.0 * exponent * z * fz;
        
        if (l > 0) gx += Math.pow(x, l-1) * Math.exp(-exponent*Math.pow(x, 2));
        if (m > 0) gy += Math.pow(y, m-1) * Math.exp(-exponent*Math.pow(y, 2));
        if (n > 0) gz += Math.pow(z, n-1) * Math.exp(-exponent*Math.pow(z, 2));

        double nc = normalization*coefficient;
        Vector3D grad = new Vector3D(gx*fy*fz*nc, fx*gy*fz*nc, fx*fy*gz*nc);

        return grad;
    }
    
    /**
     * Getter for property normalization.
     * @return Value of property normalization.
     */
    public double getNormalization() {
        return this.normalization;
    }
    
    /**
     * Setter for property normalization.
     * @param normalization New value of property normalization.
     */
    public void setNormalization(double normalization) {
        this.normalization = normalization;
    }

    /**
     * Return the maximum angular momentum for this primitive basis function
     *
     * @return the maximum angular momentum of this primitive basis function
     */
    public int getMaximumAngularMomentum() {
        return powers.getMaximumAngularMomentum();
    }

    /**
     * Return the minimum angular momentum for this primitive basis function
     *
     * @return the minimum angular momentum of this primitive basis function
     */
    public int getMinimumAngularMomentum() {
        return powers.getMinimumAngularMomentum();
    }

    /**
     * Return the total angular momentum of this primitive basis function
     *
     * @return the maximum of the primitive basis function
     */
    public int getTotalAngularMomentum() {
        return powers.getTotalAngularMomentum();
    }

    /**
     * overloaded toString()
     */
    @Override
    public String toString() {
        return "Origin : " + origin + " Powers : " + powers
               + " Normalization : " + normalization
               + " Coefficient : " + coefficient
               + " Exponent : " + exponent;
    }
} // end of class PrimitiveGaussian
