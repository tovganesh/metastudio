package org.meta.molecule;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * This class defines an enum type called BondType, used to differentiate
 * among different classes of chemical bond.
 *
 * @author  V.Ganesh, J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum BondType implements Serializable {

    WEAK_BOND("Weak bond", 0.0),
    NO_BOND("No bond", 0.0),
    SINGLE_BOND("Single bond", 1.0),
    DOUBLE_BOND("Double bond", 2.0),
    TRIPLE_BOND("Triple bond", 3.0),
    QUADRUPLE_BOND("Quadruple bond", 4.0),
    AROMATIC_BOND("Aromatic bond", 1.5),
    AMIDE_BOND("Amide bond", 1.41),
    IONIC_BOND("Ionic bond", 0.0);

    private final String description;
    private final double bondOrder;

    private static final EnumSet<BondType> vals = EnumSet.allOf(BondType.class);

    /**
     * Returns a description of the bond type
     *
     * @return a string indicating bond type
     */
    private BondType(String description, double bondOrder) {
        this.description = description;
        this.bondOrder = bondOrder;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * @return the bond order, which in a covalent bond is typically the number
     * of valence electrons this atom contributes to the bond.
     */
    public double getBondOrder() {
        return bondOrder;
    }
    /**
     * complimentary to toString()
     *
     * @param description a string indicating bond type
     * @return the equivalent BondType object
     */
    public static BondType getBondTypeFor(String description) {
        Iterator<BondType> iter = vals.iterator();
        while (iter.hasNext()) {
            BondType bondType = iter.next();
            if (bondType.description.equals(description)) {
                return bondType;
            }
        }
        return BondType.NO_BOND;
    }

    /**
     * @return true if this is a strong bond type
     */
    public boolean isStrongBond() {
        return (this != BondType.NO_BOND && this != BondType.WEAK_BOND);
    }
} 
