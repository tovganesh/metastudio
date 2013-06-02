/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.qm.integral;

import org.meta.math.geom.Point3D;
import org.meta.math.MathUtil;
import org.meta.math.qm.basis.Power;

/**
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class OneElectronTerm implements IntegralsPackage {

    /** Creates a new instance of OneElectronTerm */
    public OneElectronTerm() {
    }

    /**
     * Overlap matrix element taken form
     * <i> Taken from THO eq. 2.12 <i>
     */
    public double overlap(double alpha1, Power power, Point3D a,
                                 double alpha2, Power power2, Point3D b) {
        double radiusABSquared = a.distanceSquaredFrom(b);
        double gamma = alpha1 + alpha2;
        Point3D product = IntegralsUtil.gaussianProductCenter(alpha1, a,
                                                              alpha2, b);

        double wx = overlap1D(power.getL(), power2.getL(),
                              product.getX() - a.getX(),
                              product.getX() - b.getX(), gamma);
        double wy = overlap1D(power.getM(), power2.getM(),
                              product.getY() - a.getY(),
                              product.getY() - b.getY(), gamma);
        double wz = overlap1D(power.getN(), power2.getN(),
                              product.getZ() - a.getZ(),
                              product.getZ() - b.getZ(), gamma);

        return (Math.pow(Math.PI / gamma, 1.5)
                * Math.exp((-alpha1 * alpha2 * radiusABSquared) / gamma)
                * wx * wy * wz);
    }

    /**
     * 1D overlap.
     *
     * <i> Taken from THO eq. 2.12 <i>
     */
    public double overlap1D(int l1, int l2, double pax, double pbx,
                                   double gamma) {
        double sum = 0.0;
        int k = 1 + (int) Math.floor(0.5 * (l1 + l2));

        for(int i = 0; i < k; i++) {
            sum += (MathUtil.binomialPrefactor(2 * i, l1, l2, pax, pbx)
                    * MathUtil.factorial2(2*i - 1)) / Math.pow(2D * gamma, i);
        } // end for

        return sum;
    }

    /**
     * The Kinetic Energy (KE) componant
     *
     * <i> Taken from THO eq. 2.12 <i>
     */
    public double kinetic(double alpha1, Power power1, Point3D a,
                                 double alpha2, Power power2, Point3D b) {
        int l1 = power1.getL();
        int m1 = power1.getM();
        int n1 = power1.getN();
        int l2 = power2.getL();
        int m2 = power2.getM();
        int n2 = power2.getN();

        double term = alpha2 * (2 * (l2+ m2 + n2) + 3)
                                * overlap(alpha1, power1, a, alpha2, power2, b);

        term += -2.0 * Math.pow(alpha2, 2.0)
                                * (overlap(alpha1, power1, a, alpha2,
                                           new Power(l2 + 2, m2, n2), b)
                                   + overlap(alpha1, power1, a, alpha2,
                                           new Power(l2, m2 + 2, n2), b)
                                   + overlap(alpha1, power1, a, alpha2,
                                           new Power(l2, m2, n2 + 2), b));

        term += -0.5 * ((l2 * (l2 - 1)) * overlap(alpha1, power1, a, alpha2,
                                         new Power(l2 - 2, m2, n2), b)
                        + (m2 * (m2 - 1)) * overlap(alpha1, power1, a, alpha2,
                                         new Power(l2, m2 - 2, n2), b)
                        + (n2 * (n2 - 1)) * overlap(alpha1, power1, a, alpha2,
                                         new Power(l2, m2, n2 - 2), b));

        return term;
    }
}
