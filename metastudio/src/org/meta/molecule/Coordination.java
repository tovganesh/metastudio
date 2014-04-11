package org.meta.molecule;

/**
 * Defines coordination geometry around a single atom.
 * TODO more exotic geometries e.g. trigonal prismatic, pentagonal bipyramidal
 * @author J. Milthorpe
 */
public enum Coordination {
    None(0, null, '0'),
    Single(1, null, '\0'),
    Linear(2, Hybridisation.sp, '1'),
    BentTrigonal(2, Hybridisation.sp2, '2'),
    Resonant(2, null, 'R'),
    TrigonalPlanar(3, Hybridisation.sp2, '2'),
    BentTetrahedral(2, Hybridisation.sp3, '3'),
    TrigonalPyramidal(3, Hybridisation.sp3, '3'),
    Tetrahedral(4, Hybridisation.sp3, '3'),
    SquarePlanar(4, null, '4'),
    // TODO how to tell linear with steric number 5?
    TShape(3, Hybridisation.sp3d, '5'),
    Seesaw(4, Hybridisation.sp3d, '5'),
    TrigonalBipyramidal(5, Hybridisation.sp3d, '5'),
    SquarePyramidal(5, Hybridisation.sp3d2, '6'),
    Octahedral(6, Hybridisation.sp3d2, '6');
    private final int coordinationNumber;
    private final Hybridisation hybridisation;
    private final char uffGeometrySpecification;

    private Coordination(int coordinationNumber, Hybridisation hybridisation,
            char uffGeometrySpecification) {
        this.coordinationNumber = coordinationNumber;
        this.hybridisation = hybridisation;
        this.uffGeometrySpecification = uffGeometrySpecification;
    }

    /**
     * @return the number of atoms strongly bonded to an atom with this coordination geometry
     */
    public int getCoordinationNumber() {
        return coordinationNumber;
    }

    /**
     * @return the orbital hybridisation associated with this coordination geometry (may be null)
     */
    public Hybridisation getHybridisation() {
        return hybridisation;
    }

    /**
     * UFF and Towhee specify coordination geometry using a one-character code.
     * Note: not all possible geometries have a matching code.
     * @return the UFF coordination geometry specification character e.g. 1 = linear, 3 = tetrahedral, R = resonant
     */
    public char getUffGeometrySpecification() {
        return uffGeometrySpecification;
    }
}
