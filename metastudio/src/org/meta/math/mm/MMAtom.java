package org.meta.math.mm;

import java.util.ArrayList;
import org.meta.math.geom.Point3D;
import org.meta.math.Vector3D;
import org.meta.molecule.Atom;

/**
 * This class acts as a container for an atom in a molecular mechanics
 * simulation. It holds all necessary parameter data as well as current
 * position and force.
 * @author J. Milthorpe
 */
public abstract class MMAtom {
    /**
     * The list of atom types that are considered to be invertible (and
     * therefore candidates for Out-Of-Plane / Inversion forces.
     */
    private static final ArrayList<String> invertibleTypes =
            new ArrayList<String>();
    private final Atom atom;
    private final int atomicNumber;
    private final double atomicWeight;
    /**
     * The current center of the atom, used for minimization.
     * Importantly, this is not necessarily the same as atom.getCenter()
     */
    private Point3D atomCenter;
    private Vector3D force;
    private final boolean invertible;
    
    static {
        invertibleTypes.add("C");
        invertibleTypes.add("N");
        invertibleTypes.add("O");
        invertibleTypes.add("P");
        invertibleTypes.add("As");
        invertibleTypes.add("Sb");
        invertibleTypes.add("Bi");
    }

    public MMAtom(Atom atom, int atomicNumber, double atomicWeight,
            Point3D atomCenter) {
        this.atom = atom;
        this.atomicNumber = atomicNumber;
        this.atomicWeight = atomicWeight;
        this.atomCenter = atomCenter;
        force = new Vector3D();
        invertible = atom.getNumberOfStrongBonds() == 3 && invertibleTypes.
                contains(atom.getSymbol());
    }

    void addForceComponent(Vector3D direction, double magnitude) {
        addForceComponent(direction.mul(magnitude));
    }

    void addForceComponent(Vector3D forceComponent) {
        force = force.add(forceComponent);
    }

    public Atom getAtom() {
        return atom;
    }

    public Point3D getAtomCenter() {
        return atomCenter;
    }

    public int getAtomicNumber() {
        return atomicNumber;
    }

    public double getAtomicWeight() {
        return atomicWeight;
    }

    public Vector3D getForce() {
        return force;
    }

    public void setAtomCenter(Point3D atomCenter) {
        this.atomCenter = atomCenter;
    }

    public void setForce(Vector3D force) {
        this.force = force;
    }

    public boolean isInvertible() {
        return invertible;
    }
}
