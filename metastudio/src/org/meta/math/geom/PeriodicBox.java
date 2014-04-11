/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.geom;

/**
 * Represents a periodic box
 * 
 * @author ritwik
 */
public class PeriodicBox extends AbstractGeometricObject {

    private double tx, ty, tz;
    private double alpha, beta, gamma;
    private Point3D origin;
    private int nxLattice, nyLattice, nzLattice;

    /** Creates a new instance of PeriodicBox */
    public PeriodicBox(double tx, double ty, double tz,
                       double alpha, double beta, double gamma) {

        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.origin = new Point3D();
        this.nxLattice = 1;
        this.nyLattice = 1;
        this.nzLattice = 1;
    }

    /**
     * get number of lattice in z
     *
     * @return number of lattice in z
     */
    public int getNzLattice() {
        return nzLattice;
    }

    /**
     * set number of lattice in z
     */
    public void setNzLattice(int nzLattice) {
        this.nzLattice = nzLattice;
    }

    /**
     * get number of lattice in x
     *
     * @return number of lattice in x
     */
    public int getNxLattice() {
        return nxLattice;
    }

    /**
     * set number of lattice in x
     */
    public void setNxLattice(int nxLattice) {
        this.nxLattice = nxLattice;
    }

    /**
     * get number of lattice in y
     *
     * @return number of lattice in y
     */
    public int getNyLattice() {
        return nyLattice;
    }

    /**
     * set number of lattice in x
     */
    public void setNyLattice(int nyLattice) {
        this.nyLattice = nyLattice;
    }

    /**
     * set property alpha
     *
     * @return alpha the angle
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * set property alpha
     *
     * @param alpha the angle
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * get property beta
     *
     * @return beta the angle
     */
    public double getBeta() {
        return beta;
    }

    /**
     * set property beta
     *
     * @param beta the angle
     */
    public void setBeta(double beta) {
        this.beta = beta;
    }

    /**
     * get property gamma
     *
     * @return gamma the angle
     */
    public double getGamma() {
        return gamma;
    }

    /**
     * set property gamma
     *
     * @param gamma the angle
     */
    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    /**
     * get property Tx
     *
     * @return tx the translation along x
     */
    public double getTx() {
        return tx;
    }

    /**
     * set property Tx
     *
     * @param tx the translation along x
     */
    public void setTx(double tx) {
        this.tx = tx;
    }

    /**
     * get property Ty
     *
     * @return ty the translation along y
     */
    public double getTy() {
        return ty;
    }

    /**
     * set property Ty
     *
     * @param ty the translation along y
     */
    public void setTy(double ty) {
        this.ty = ty;
    }

    /**
     * get property Tz
     *
     * @return tz the translation along z
     */
    public double getTz() {
        return tz;
    }

    /**
     * set property Tz
     *
     * @param tz the translation along z
     */
    public void setTz(double tz) {
        this.tz = tz;
    }

    /**
     * Get the current origin
     *
     * @return return the current origin
     */
    public Point3D getOrigin() {
        return origin;
    }

    /**
     * Set a new origin
     *
     * @param origin the origin
     */
    public void setOrigin(Point3D origin) {
        this.origin = origin;
    }

    /**
     * Overridden toString() method
     * 
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "Periodic Box: ";
    }

}