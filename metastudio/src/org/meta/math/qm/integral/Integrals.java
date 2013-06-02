/*
 * Integrals.java
 *
 * Created on July 28, 2004, 7:03 AM
 */

package org.meta.math.qm.integral;

import org.meta.math.Vector3D;
import org.meta.math.geom.Point3D;
import org.meta.math.qm.basis.ContractedGaussian;
import org.meta.math.qm.basis.Power;
import org.meta.math.qm.basis.PrimitiveGaussian;

/**
 * Integral package dispatch class for MeTA Studio. <br>
 *
 * The purpose of this class has substantially changed from the earlier code,
 * in that it no longer provides an implementation, but dispatches it to the
 * implementation classes that are actually handling the computation work.
 * This in turn gives an easy way to dynamically change the method used
 * to actually evaluate the integrals.
 * 
 * The static initialization is done as a way to have good performance as well
 * as allow flexibility of using multiple integral packages.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public final class Integrals {

    private static NuclearTerm nuclearTerm;
    private static OneElectronTerm oneElectronTerm;
    private static TwoElectronTerm twoElectronTerm;

    static {
        nuclearTerm = IntegralsPackageFactory.getInstance().getNuclearTerm();
        oneElectronTerm
                   = IntegralsPackageFactory.getInstance().getOneElectronTerm();
        twoElectronTerm
                   = IntegralsPackageFactory.getInstance().getTwoElectronTerm();
    }

    /**
     * Re-initialize package references in case there are any changes..
     */
    public static void reInitPackageReference() {
        nuclearTerm = IntegralsPackageFactory.getInstance().getNuclearTerm();
        oneElectronTerm
                   = IntegralsPackageFactory.getInstance().getOneElectronTerm();
        twoElectronTerm
                   = IntegralsPackageFactory.getInstance().getTwoElectronTerm();
    }

    /**
     * No instantiation possible
     */
    private Integrals() {
    }
       
    /**
     * Overlap matrix element taken form
     * <i> Taken from THO eq. 2.12 <i>
     */
    public static double overlap(double alpha1, Power power, Point3D a, 
                                 double alpha2, Power power2, Point3D b) {
        return oneElectronTerm.overlap(alpha1, power, a, alpha2, power2, b);
    }
       
    /**
     * The Kinetic Energy (KE) component
     *
     * <i> Taken from THO eq. 2.12 <i>
     */
    public static double kinetic(double alpha1, Power power1, Point3D a, 
                                 double alpha2, Power power2, Point3D b) {
        return oneElectronTerm.kinetic(alpha1, power1, a, alpha2, power2, b);
    }
    
    /**
     * The nuclear attraction term.
     *
     * <i> Taken from THO eq. 2.12 <i>
     */
    public static double nuclearAttraction(Point3D a, double norm1, 
                                Power power1, double alpha1, Point3D b, 
                                double norm2, Power power2, double alpha2, 
                                Point3D c) {
        return nuclearTerm.nuclearAttraction(a, norm1, power1, alpha1, b,
                                             norm2, power2, alpha2, c);
                                    
    }

    /**
     * The nuclear attraction gradient term
     */
    public static Vector3D nuclearAttractionGradient(Point3D a,
                                Power power1, double alpha1, Point3D b,
                                Power power2, double alpha2, Point3D c) {
        return nuclearTerm.nuclearAttractionGradient(a, power1, alpha1, b,
                                                     power2, alpha2, c);

    }
    
    /**
     * 2E coulomb interactions between 4 contracted Gaussians
     */
    public static double coulomb(ContractedGaussian a, ContractedGaussian b,
                                 ContractedGaussian c, ContractedGaussian d) {
        return twoElectronTerm.coulomb(a, b, c, d);
    }

    /**
     * 2E coulomb interactions between 4 primitive Gaussians
     */
    public static double coulomb(PrimitiveGaussian a, PrimitiveGaussian b,
                                 PrimitiveGaussian c, PrimitiveGaussian d) {
        return twoElectronTerm.coulombRepulsion(
            a.getOrigin(), a.getNormalization(), a.getPowers(), a.getExponent(),
            b.getOrigin(), b.getNormalization(), b.getPowers(), b.getExponent(),
            c.getOrigin(), c.getNormalization(), c.getPowers(), c.getExponent(),
            d.getOrigin(), d.getNormalization(), d.getPowers(), d.getExponent());
    }
} // end of class Integrals
