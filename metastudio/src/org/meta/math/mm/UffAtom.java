package org.meta.math.mm;

import org.meta.math.geom.Point3D;
import org.meta.config.impl.UffParameters;
import org.meta.molecule.Atom;
import org.meta.molecule.Coordination;

/**
 * This class acts as a container for an atom in a UFF calculation.
 * It holds all necessary parameter data as well as force and energy
 * interactions as required.
 * @author J. Milthorpe
 */
public class UffAtom extends MMAtom {
    private final UffParameters params;
    private final Coordination coordination;
    private final UffParameters.UffCoordinationParameters coordinationParams;

    public UffAtom(Atom atom, Point3D atomCenter, UffParameters params,
            Coordination coordination,
            UffParameters.UffCoordinationParameters coordinationParams,
            int atomicNumber, double atomicWeight) {
        super(atom, atomicNumber, atomicWeight, atomCenter);
        this.params = params;
        this.coordination = coordination;
        this.coordinationParams = coordinationParams;

    }

    public UffParameters getParams() {
        return params;
    }

    public Coordination getCoordination() {
        return coordination;
    }

    public UffParameters.UffCoordinationParameters getCoordinationParams() {
        return coordinationParams;
    }

    @Override
    public String toString() {
        return "atom [" + getAtom().getIndex() + "] {" + getAtom().getSymbol() +
                coordination.getUffGeometrySpecification() + "} centre = " +
                getAtomCenter();
    }
}
