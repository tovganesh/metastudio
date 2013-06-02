package org.meta.config.charmm;

/**
 * This class represents the CHARMM dihedral (torsion) parameters for a given
 * dihedral type.
 * CHARMM dihedral angle terms are calculated as
 *   k_chi (1 + cos(n * chi - sigma) ) where
 *   k_chi is the dihedral angle force constant;
 *   chi is the dihedral torsion angle;
 *   n is the multiplicity; and
 *   sigma is the phase.
 * @author Josh Milthorpe
 */
public class DihedralParameters {
    /** The first atom type e.g. "NH2" = amide nitrogen */
    public final String atom1;
    /** The second atom type e.g. "CT1" = aliphatic sp3 C for CH */
    public final String atom2;
    /** The third atom type e.g. "C" = polar C */
    public final String atom3;
    /** The fourth atom type e.g. "O" = carbonyl oxygen */
    public final String atom4;
    /** The angle bending force k_chi in kcal/mol */
    public final double dihedralForce;
    /** The multiplicity n */
    public final int multiplicity;
    /** The phase sigma in degrees. */
    public final double phase;

    public DihedralParameters(String atom1, String atom2, String atom3,
            String atom4, double dihedralForce, int multiplicity, double phase) {
        this.atom1 = atom1;
        this.atom2 = atom2;
        this.atom3 = atom3;
        this.atom4 = atom4;
        this.dihedralForce = dihedralForce;
        this.multiplicity = multiplicity;
        this.phase = phase;
    }

    public String getDihedralDesignation() {
        return getDihedralDesignation(atom1, atom2, atom3, atom4);
    }

    public static String getDihedralDesignation(String atom1, String atom2,
            String atom3, String atom4) {
        if (atom1.compareTo(atom4) < 0 ||
                (atom1.compareTo(atom4) == 0 && atom2.compareTo(atom3) < 0)) {
            // order 1,4 dihedral designations in alphabetical order
            // or, if 1,4 are the same, sort 2,3 in alphabetical order
            String temp = atom1;
            atom1 = atom4;
            atom4 = temp;

            temp = atom2;
            atom2 = atom3;
            atom3 = temp;
        }
        return atom1 + "-" + atom2 + "-" + atom3 + "-" + atom4;
    }
}
