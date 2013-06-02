package org.meta.config.charmm;

/**
 * This class represents the CHARMM angle parameters for a given bond angle
 * configuration.
 * CHARMM angle terms are calculated as
 * k_theta (theta - theta_0) where
 *   k_theta is the valence angle force constant;
 *   theta is the bond angle; and
 *   theta_0 is the equilibrium bond angle.
 * @author Josh Milthorpe
 */
public class AngleParameters {
    /** The first atom type e.g. "CT1" = aliphatic sp3 C for CH */
    public final String atom1;
    /** The second atom type e.g. "CT2" = aliphatic sp3 C for CH2 */
    public final String atom2;
    /** The third atom type e.g. "CC" = carbonyl C for sidechains */
    public final String atom3;
    /** The angle bending force k_theta in kcal/mol */
    public final double angleBendForce;
    /** The natural bond angle theta0 in degrees */
    public final double naturalAngle;

    public AngleParameters(String atom1, String atom2, String atom3,
            double angleBendForce, double naturalAngle) {
        this.atom1 = atom1;
        this.atom2 = atom2;
        this.atom3 = atom3;
        this.angleBendForce = angleBendForce;
        this.naturalAngle = naturalAngle;
    }

    public String getAngleDesignation() {
        return getAngleDesignation(atom1, atom2, atom3);
    }

    public static String getAngleDesignation(String atom1, String atom2,
            String atom3) {
        if (atom1.compareTo(atom3) < 0) {
            // order 1,3 angle designations in alphabetical order
            String temp = atom1;
            atom1 = atom3;
            atom3 = temp;
        }
        return atom1 + "-" + atom2 + "-" + atom3;
    }
}
