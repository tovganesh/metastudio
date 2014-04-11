package org.meta.math.mm;

import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;

/**
 * This class acts as a container for an atom in a CHARMM force field calculation.
 * It holds all necessary parameter data as well as current
 * position and force.
 * @author J. Milthorpe
 */
public class CharmmAtom extends MMAtom {
    private final String charmmAtomType;

    public CharmmAtom(Atom atom, int atomicNumber, double atomicWeight,
            Point3D atomCenter, String charmmAtomType) {
        super(atom, atomicNumber, atomicWeight, atomCenter);
        this.charmmAtomType = charmmAtomType;
    }

    public String getCharmmAtomType() {
        return charmmAtomType;
    }

    @Override
    public String toString() {
        return "atom [" + getAtom().getIndex() + "] {" + charmmAtomType +
                "} centre = " + getAtomCenter();
    }
}
