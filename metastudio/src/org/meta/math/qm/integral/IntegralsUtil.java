/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.qm.integral;

import org.meta.math.geom.Point3D;

/**
 * Utility class for IntegralsPackage.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public final class IntegralsUtil {

    /** No instantiation: private constructor */
    private IntegralsUtil() { }

    /**
     * the gaussian product theorem
     *
     * @param alpha1 exponent of first gaussian
     * @param a center of first gaussian
     * @param alpha2 exponent of first gaussian
     * @param b center of first gaussian
     */
    public static Point3D gaussianProductCenter(double alpha1, Point3D a,
                                                double alpha2, Point3D b) {
        double gamma = alpha1 + alpha2;
        return new Point3D(
                            (alpha1 * a.getX() + alpha2 * b.getX()) / gamma,
                            (alpha1 * a.getY() + alpha2 * b.getY()) / gamma,
                            (alpha1 * a.getZ() + alpha2 * b.getZ()) / gamma
                          );
    }

    /**
     * Indexing (i,j,k,l) into long array.
     */
    public static int ijkl2intindex(int i, int j, int k, int l) {
        int temp;

        if (i<j) {
            temp = i;
            i    = j;
            j    = temp;
        } // end if
        if (k<l) {
            temp = k;
            k    = l;
            l    = temp;
        } // end if

        int ij = i * (i+1) / 2+j;
        int kl = k * (k+1) / 2+l;

        if (ij < kl) {
            temp = ij;
            ij   = kl;
            kl   = temp;
        } // end id

        return (ij * (ij+1) / 2+kl);
    }

    /**
     * Incomplete gamma function
     */
    public static double computeFGamma(int m, double x) {
        x = Math.max(Math.abs(x), SMALL);

        return (0.5 * Math.pow(x, -m - 0.5)
                     * gammaIncomplete(m + 0.5, x));
    }

    /**
     * Incomple gamma function gamma() computed from Numerical Recipes
     * routine gammp.
     */
    public static double gammaIncomplete(double a, double x) {
        double gammap = 0.0;
        double gln    = 0.0;

        gln = gammln(a);

        if(x < (a + 1.0)) {
            // Series representation of Gamma. NumRec sect 6.1.
            if(x != 0.0) {
                double ap = a;
                double sum;
                double delta = sum = 1.0 / a;

                for(int i=0; i<MAX_ITERATION; i++) {
                    ap++;
                    delta *= x / ap;
                    sum += delta;
                    if(Math.abs(delta) < Math.abs(sum) * EPS) break;
                } // end for

                gammap = sum * Math.exp(-x + a*Math.log(x) - gln);
            } else {
                gammap = 0.0;
            } // end if
        } else {
            // Continued fraction representation of Gamma. NumRec sect 6.1
            double b = (x + 1.0) - a;
            double c = 1.0 / FPMIN;
            double d = 1.0 / b;
            double h = d;
            double an, delta;

            for(int i=1; i<(MAX_ITERATION+1); i++) {
                an = -i * (i-a);
                b += 2.0;
                d = an * d + b;

                if(Math.abs(d) < FPMIN) d = FPMIN;

                c = b + an / c;

                if(Math.abs(c) < FPMIN) c = FPMIN;

                d = 1.0 / d;
                delta = d * c;
                h *= delta;

                if(Math.abs(delta - 1.0) < EPS) break;
            } // end for

            gammap = 1.0 - (Math.exp(-x + a*Math.log(x) - gln) * h);
        } // end if

        return (Math.exp(gln) * gammap);
    }

    /**
     * Numerical recipes, section 6.1
     */
    public static double gammln(double x) {
        double y = x;
        double tmp = x + 5.5;

        tmp -= (x + 0.5) * Math.log(tmp);

        double ser = 1.000000000190015;

            y++;
            ser +=  76.18009172947146 / y;
            y++;
            ser += -86.50532032941677 / y;
            y++;
            ser +=  24.01409824083091 / y;
            y++;
            ser += -1.231739572450155 / y;
            y++;
            ser +=  0.1208650973866179e-2 / y;
            y++;
            ser += -0.5395239384953e-5 / y;
            
        return (-tmp + Math.log((2.5066282746310005 * ser) / x));
    }

    // for gammp mathod
    private static double SMALL = 0.00000001;
    private static double EPS   = 3.0e-7;
    private static double FPMIN = 1.0e-30;

    private static int MAX_ITERATION = 100;
}
