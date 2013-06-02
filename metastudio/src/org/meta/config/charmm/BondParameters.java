package org.meta.config.charmm;

/**
 * This class represents the CHARMM bond parameters for a given bond type.
 * @author Josh Milthorpe
 */
public class BondParameters {
    /** The first atom type e.g. "CT1" = aliphatic sp3 C for CH */
    public final String atom1;
    /** The second atom type e.g. "NH1" = peptide nitrogen */
    public final String atom2;
    /** The bond stretch force k_B in kcal/mol Å^2 */
    public final double bondStretchForce;
    /** The natural bond length r0 in Å */
    public final double naturalBondLength;

    public BondParameters(String atomName1, String atomName2,
            double bondStretchForce, double bondDistance) {
        this.atom1 = atomName1;
        this.atom2 = atomName2;
        this.bondStretchForce = bondStretchForce;
        this.naturalBondLength = bondDistance;
    }

    public String getBondDesignation() {
        return getBondDesignation(atom1, atom2);
    }

    public static String getBondDesignation(String atom1, String atom2) {
        if (atom1.compareTo(atom2) < 0) {
            // put atom designations in alphabetical order
            String temp = atom1;
            atom1 = atom2;
            atom2 = temp;
        }

        return atom1 + "-" + atom2;
    }
}
