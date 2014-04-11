package org.meta.math.mm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import org.meta.math.geom.Point3D;
import org.meta.config.impl.AtomInfo;
import org.meta.math.MathUtil;
import org.meta.math.Matrix;
import org.meta.math.Vector3D;
import org.meta.math.mm.mixing.LennardJonesMixingMethod;
import org.meta.math.optimizer.OptimizerFunction;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;

/**
 * An abstract class representing a Molecular Mechanics Energy method
 *
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class MMEnergyMethod implements OptimizerFunction {
    /**
     * The molecule under consideration
     */
    protected final Molecule molecule;
    protected final AtomInfo atomInfo;
    protected final boolean calculateForces;
    protected BondedPairing[] bondedPairings;
    protected MMAtom[] invertibleAtoms;
    protected NonbondedPairing[] nonbondedPairings;
    protected OneFourPairing[] oneFourPairings;
    protected OneThreePairing[] oneThreePairings;
    /**
     * The mixing method to use for Lennard-Jones vdW potential parameters.
     * UFF allows either Lorentz-Berthelot (arithmetic/geometric) or straight
     * geometric mixing, suggesting the later where summation of van der Waals
     * terms is required (Eq. 21b). Towhee/JMol allows only geometric mixing.
     */
    protected final LennardJonesMixingMethod mixingMethod;

    /** Creates a new instance of MMEnergyMethod */
    public MMEnergyMethod(final Molecule molecule,
            final LennardJonesMixingMethod mixingMethod, boolean calculateForces) {
        this.molecule = molecule;
        this.mixingMethod = mixingMethod;
        this.calculateForces = calculateForces;
        this.atomInfo = AtomInfo.getInstance();
    }

    /**
     * @return the atoms in the molecule under consideration
     */
    public abstract MMAtom[] getAtoms();

    /**
     * Get the energy of the current atom coordinates
     * @return the energy
     */
    public double getEnergy() {
        return evaluate(getAtomCoords());
    }

    /**
     * @return current atom coordinates, in a flat array [...,i,j,k,...]
     */
    public double[] getAtomCoords() {
        MMAtom[] atoms = getAtoms();
        double coords[] = new double[atoms.length * 3];
        for (int i = 0; i < atoms.length; i++) {
            final Point3D atomCenter = atoms[i].getAtom().getAtomCenter();
            coords[i * 3] = atomCenter.getX();
            coords[i * 3 + 1] = atomCenter.getY();
            coords[i * 3 + 2] = atomCenter.getZ();
        }
        return coords;
    }

    @Override
    public boolean isDerivativeAvailable() {
        return true;
    }

    /**
     * Puts the forces into a flat array with the I,J,K component of
     * the force for each atom in order.
     * @return the force array
     */
    public double[] getForces() {
        MMAtom[] atoms = getAtoms();
        double[] gradients = new double[atoms.length * 3];
        for (int i = 0; i < atoms.length; i++) {
            Vector3D force = atoms[i].getForce();
            gradients[i * 3] = -force.getI();
            gradients[i * 3 + 1] = -force.getJ();
            gradients[i * 3 + 2] = -force.getK();
        //System.out.println("force on atom[" + i + "]: " + force);
        }
        return gradients;
    }

    /**
     * Puts the gradients into a flat array with the I,J,K component of
     * the gradient for each atom in order.
     * NOTE in theory the gradient/derivatives should equal the forces on each
     * atom.  However with some force fields like UFF this is not the case.
     * This method calculates a "numerical derivative" by perturbing the energy
     * a small amount in each direction and taking delta(energy)/delta(distance)
     * @return the gradient array
     */
    @Override
    public double[] getDerivatives() {
        //return getForces();
        final double DELTA = 1.0E-4;
        double[] atomCoords = getAtomCoords();
        double[] gradients = new double[atomCoords.length];
        double energy = getEnergy();
        for (int i = 0; i < atomCoords.length; i++) {
            atomCoords[i] += DELTA;
            double newEnergy = evaluate(atomCoords);
            atomCoords[i] -= DELTA;
            gradients[i] = (newEnergy - energy) / DELTA;
        }
        return gradients;
    }

    public Molecule getMolecule() {
        return molecule;
    }

    /**
     * Updates all atoms to new positions.  Used for display of new minimized structure.
     * @param variables the new positions of all atoms, in a flat array [...,i,j,k,...]
     */
    @Override
    public void resetVariables(double[] variables) {
        molecule.resetAtomCoordinates(variables, false);
    }

    @Override
    public boolean isHessianAvailable() {
        return false;
    }

    @Override
    public Matrix getHessian() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the bond angle between bonds I-J and I-K
     * @param atomI
     * @param atomJ
     * @param atomK
     * @return the bond angle between bonds I-J and I-K
     */
    protected double getCoordinationBondAngle(Atom atomI, Atom atomJ, Atom atomK) {
        Vector3D ij = new Vector3D(atomJ.getAtomCenter()).sub(atomI.
                getAtomCenter());
        Vector3D ik = new Vector3D(atomK.getAtomCenter()).sub(atomI.
                getAtomCenter());
        return ij.angleWith(ik);
    }

    /**
     * Determines all pairings for bonded, 1-3, 1-4 and non-bonded atoms.
     */
    public void determineConnectivity(MMAtom[] atoms) {
        ArrayList<BondedPairing> bondedPairings =
                new ArrayList<BondedPairing>();
        ArrayList<OneThreePairing> oneThreePairings =
                new ArrayList<OneThreePairing>();
        ArrayList<OneFourPairing> oneFourPairings =
                new ArrayList<OneFourPairing>();
        ArrayList<NonbondedPairing> nonbondedPairings =
                new ArrayList<NonbondedPairing>();
        ArrayList<MMAtom> invertibleAtoms =
                new ArrayList<MMAtom>();
        for (int i = 0; i < atoms.length;
                i++) {
            MMAtom atomI = atoms[i];
            for (int j = 0; j < i;
                    j++) {
                MMAtom atomJ = atoms[j];

                //System.out.println(atomI + ", " + atomJ);

                BondType connectivity =
                        atomI.getAtom().
                        getConnectivity(atomJ.getAtom().getIndex());
                if (connectivity.isStrongBond()) {
                    bondedPairings.add(new BondedPairing(i, j, connectivity));
                } else {
                    OneThreePairing oneThreeConnection = get13Connection(i, j);
                    if (oneThreeConnection != null) {
                        oneThreePairings.add(oneThreeConnection);
                    } else {
                        OneFourPairing oneFourConnection =
                                get14Connection(i, j);
                        if (oneFourConnection != null) {
                            // 1,4 connection: I-2-3-J
/*
                            System.out.println("1,4 connected through:" +
                            oneFourConnection.j + " and " +
                            oneFourConnection.k);
                             */
                            oneFourPairings.add(oneFourConnection);
                        }
                        // atoms i, j are not connected.  calculate non-bonding interactions
                        nonbondedPairings.add(new NonbondedPairing(i, j));
                    }
                }
            }
            if (atomI.isInvertible()) {
                invertibleAtoms.add(atomI);
            }
        }
        this.bondedPairings =
                bondedPairings.toArray(new BondedPairing[bondedPairings.size()]);
        System.out.println("bonded: " + bondedPairings.size());
        this.oneThreePairings =
                oneThreePairings.toArray(new OneThreePairing[oneThreePairings.
                size()]);
        System.out.println("1,3: " + oneThreePairings.size());
        this.oneFourPairings =
                oneFourPairings.toArray(
                new OneFourPairing[oneFourPairings.size()]);
        System.out.println("1,4: " + oneFourPairings.size());
        this.nonbondedPairings =
                nonbondedPairings.toArray(
                new NonbondedPairing[nonbondedPairings.size()]);
        System.out.println("nonbonded: " + nonbondedPairings.size());
        this.invertibleAtoms =
                invertibleAtoms.toArray(new MMAtom[invertibleAtoms.size()]);
        System.out.println("invertible: " + invertibleAtoms.size());
    }

    /**
     * If atoms I and K are 1,3-connected via atom K return the 1,3 pairing.
     * @param i index of one atom in the molecule
     * @param k index of another atom in the molecule
     * @return a OneThreePairing if the atoms are 1,3 connected; otherwise null
     */
    private OneThreePairing get13Connection(int i, int k) {
        Atom atomI = molecule.getAtom(i);
        Hashtable<Integer, BondType> connectedI = atomI.getConnectedList();
        Iterator<Integer> iter = connectedI.keySet().iterator();
        while (iter.hasNext()) {
            Integer atomIndex = iter.next();
            if (connectedI.get(atomIndex).isStrongBond() &&
                    molecule.isStronglyBonded(atomIndex, k)) {
                return new OneThreePairing(i, atomIndex, k);
            }
        }
        return null;
    }

    /**
     * If atoms I and L are 1,4-connected, find connecting atoms J and K
     * @param i index of first atom in the molecule
     * @param l index of second atom in the molecule
     * @return the 1,4 connection between the atoms, or null if none exists
     */
    private OneFourPairing get14Connection(int i, int l) {
        Atom atomI = molecule.getAtom(i);
        Hashtable<Integer, BondType> connectedI = atomI.getConnectedList();
        Iterator<Integer> iterI = connectedI.keySet().iterator();
        while (iterI.hasNext()) {
            Integer j = iterI.next();
            if (connectedI.get(j).isStrongBond()) {
                Atom atomJ = molecule.getAtom(j);
                Hashtable<Integer, BondType> connectedJ =
                        atomJ.getConnectedList();
                Iterator<Integer> iterJ = connectedJ.keySet().iterator();
                while (iterJ.hasNext()) {
                    Integer k = iterJ.next();
                    if (connectedJ.get(k).isStrongBond() &&
                            molecule.isStronglyBonded(k, l)) {
                        return new OneFourPairing(i, j, k, l);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public double getMaxNormOfDerivatives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getRMSOfDerivatives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @param atom1 the start atom
     * @param atom2 the end atom
     * @return the vector from atom 1 in the direction of atom 2
     */
    protected Vector3D getVector(MMAtom atom1, MMAtom atom2) {
        Vector3D direction =
                new Vector3D(atom1.getAtomCenter().sub(atom2.getAtomCenter()));
        return direction;
    }

    /**
     * Gets the angle between two bonds IJ and JK.
     * @param atomI
     * @param atomJ
     * @param atomK
     * @return the bond angle IJK
     */
    protected double getBondAngle(MMAtom atomI, MMAtom atomJ, MMAtom atomK) {
        Vector3D vectorIJ = getVector(atomI, atomJ);
        Vector3D vectorKJ = getVector(atomK, atomJ);
        return vectorIJ.angleWith(vectorKJ);
    }

    /**
     * Gets the angle between two bonds IJ and JK.  Also returns the force
     * vectors to restore the angle towards zero.
     * @param atomI
     * @param atomJ
     * @param atomK
     * @param forces the force vectors to restore to a zero bond angle
     * @return the bond angle IJK
     */
    protected double getBondAngleAndForces(MMAtom atomI, MMAtom atomJ,
            MMAtom atomK, Vector3D[] forces) {
        Vector3D vectorIJ = getVector(atomI, atomJ);
        Vector3D vectorKJ = getVector(atomK, atomJ);
        double theta = vectorIJ.angleWith(vectorKJ);

        double magI = vectorIJ.magnitude();
        double magK = vectorKJ.magnitude();

        if (!(MathUtil.isNearZero(magI) || MathUtil.isNearZero(magK))) {
            double invMagI = 1.0 / magI;
            double invMagK = 1.0 / magK;
            Vector3D forceI = vectorIJ.mul(invMagI);
            Vector3D forceK = vectorKJ.mul(invMagK);

            Vector3D crossProduct = forceI.cross(forceK);
            if (!MathUtil.isNearZero(crossProduct.magnitude())) {
                crossProduct = crossProduct.normalize();
                forceI = forceI.cross(crossProduct).normalize();
                forceK = forceK.cross(crossProduct).normalize();

                forceI = forceI.mul(-invMagI);
                forceK = forceK.mul(invMagK);
                Vector3D forceJ = forceK.add(forceI).negate();
                /*
                System.out.println("angle bend: forceI = " + forceI +
                " forceJ = " + forceJ +
                " forceK = " + forceK);
                 */
                forces[0] = forceI;
                forces[1] = forceJ;
                forces[2] = forceK;

                return theta;
            }
        }
        // forces are zero, still return angle
        forces[0] = new Vector3D();
        forces[1] = new Vector3D();
        forces[2] = new Vector3D();

        return theta;
    }

    /**
     * Determines the torsion angle between the two bond angle planes
     * IJK and JKL.
     * @param atomI
     * @param atomJ
     * @param atomK
     * @param atomL
     * @return the torsion angle IJK-JKL
     */
    protected double getTorsionAngle(MMAtom atomI, MMAtom atomJ, MMAtom atomK,
            MMAtom atomL) {
        double phi = 0.0;

        Vector3D i = getVector(atomJ, atomI);
        Vector3D j = getVector(atomK, atomJ);
        Vector3D k = getVector(atomL, atomK);

        double magIJ = i.magnitude();
        double magJK = j.magnitude();
        double magKL = k.magnitude();

        if (!(MathUtil.isNearZero(magIJ) || MathUtil.isNearZero(magJK) ||
                MathUtil.isNearZero(magKL))) {
            i = i.normalize();
            j = j.normalize();
            k = k.normalize();

            Vector3D a = i.cross(j);
            Vector3D b = j.cross(k);
            Vector3D c = a.cross(b);

            phi = -Math.atan2(
                    c.dot(j), // ((ij x jk) x (jk x kl)) . jk
                    a.dot(b)); //  (ij x jk) . (jk x kl)
        }
        //System.out.println("torsion angle = " + phi);
        return phi;
    }

    /**
     * Determines the torsion angle between the two bond angle planes
     * IJK and JKL.  Also returns the gradient vectors for each atom to restore
     * the torsion angle towards zero.
     * @param atomI
     * @param atomJ
     * @param atomK
     * @param atomL
     * @param forces the restorative vector for each atom towards a zero torsion
     * @return the torsion angle IJK-JKL
     */
    protected double getTorsionAngleAndForces(MMAtom atomI, MMAtom atomJ,
            MMAtom atomK, MMAtom atomL, Vector3D[] forces) {
        double phi;
        Vector3D i = getVector(atomJ, atomI);
        Vector3D j = getVector(atomK, atomJ);
        Vector3D k = getVector(atomL, atomK);

        double magIJ = i.magnitude();
        double magJK = j.magnitude();
        double magKL = k.magnitude();

        if (MathUtil.isNearZero(magIJ) || MathUtil.isNearZero(magJK) ||
                MathUtil.isNearZero(magKL)) {
            phi = 0.0;
            forces[0] = new Vector3D();
            forces[1] = new Vector3D();
            forces[2] = new Vector3D();
            forces[3] = new Vector3D();
        } else {
            double angleIJK = i.angleWith(j);
            double sinIJK = Math.sin(angleIJK);
            double cosIJK = Math.cos(angleIJK);

            double angleJKL = j.angleWith(k);
            double sinJKL = Math.sin(angleJKL);
            double cosJKL = Math.cos(angleJKL);

            i = i.normalize();
            j = j.normalize();
            k = k.normalize();

            i = i.cross(j);
            Vector3D l = j.cross(k);
            k = i.cross(l);

            phi = -Math.atan2(
                    k.dot(j), // ((ij x jk) x (jk x kl)) . jk
                    i.dot(l)); //  (ij x jk) . (jk x kl)

            i = i.mul(1.0 / magIJ / sinIJK / sinIJK);
            l = l.mul(-1.0 / magKL / sinJKL / sinJKL);
            k = l.mul(-magKL / magJK * cosJKL);
            j = i.mul(-magIJ / magJK * cosIJK - 1.0).sub(k);
            k = i.add(j).add(l).negate();

            forces[0] = i;
            forces[1] = j;
            forces[2] = k;
            forces[3] = l;
        /*
        System.out.println("torsional force: i = " + i + " j = " + j +
        " k = " + k + " l = " + l);
         */
        }
        //System.out.println("torsion angle = " + phi);
        return phi;
    }

    public static double getInversionAngleAndForces(
            Point3D[] atomLocations, Vector3D[] forces) {
        // This is adapted from http://scidok.sulb.uni-saarland.de/volltexte/2007/1325/pdf/Dissertation_1544_Moll_Andr_2007.pdf
        // via JMol, thanks to JMol developers and
        // Many thanks to Andreas Moll and the BALLView developers for this

        /*  ported to Java by Bob Hanson
         *
         *      (theta)
         *       \an /
         *     j---i---k
         *      cn | bn
         *         l
         *
         */
        Vector3D i = new Vector3D(atomLocations[0]); // the central atom
        Vector3D ij = new Vector3D(atomLocations[1]).sub(i);
        Vector3D ik = new Vector3D(atomLocations[2]).sub(i);
        Vector3D il = new Vector3D(atomLocations[3]).sub(i);

        // bond distances:
        double magIJ = ij.magnitude();
        double magIK = ik.magnitude();
        double magIL = il.magnitude();
        if (MathUtil.isNearZero(magIJ) || MathUtil.isNearZero(magIK) ||
                MathUtil.isNearZero(magIL)) {
            forces[0] = new Vector3D();
            forces[1] = new Vector3D();
            forces[2] = new Vector3D();
            forces[3] = new Vector3D();
            return 0.0;
        }

        ij = ij.normalize();
        ik = ik.normalize();
        il = il.normalize();

        // theta is the j-i-k bond angle:
        double cosTheta = ij.dot(ik);
        double theta = Math.acos(cosTheta);

        // If theta equals 180 degree or 0 degree
        if (MathUtil.isNearZero(theta) || (MathUtil.isNearZero(Math.abs(theta -
                Math.PI)))) {
            forces[0] = new Vector3D();
            forces[1] = new Vector3D();
            forces[2] = new Vector3D();
            forces[3] = new Vector3D();
            return 0.0;
        }
        double cosecTheta = 1.0 / Math.sin(theta);

        // normals to planes:
        Vector3D normalJIK = ij.cross(ik);
        Vector3D normalKIL = ik.cross(il);
        Vector3D normalLIJ = il.cross(ij);

        // the angle from l to the j-i-k plane (Wilson angle):
        double sinOmega = normalJIK.dot(il) * cosecTheta;
        double omega = Math.asin(sinOmega);
        double cosOmega = Math.cos(omega);

        // In case: wilson angle equals 0 or 180 degree: do nothing
        // if wilson angle equal 90 degree: abort
        if (cosOmega < 0.0001 || MathUtil.isNearZero(omega) || MathUtil.
                isNearZero(Math.abs(omega -
                Math.PI))) {
            forces[0] = new Vector3D();
            forces[1] = new Vector3D();
            forces[2] = new Vector3D();
            forces[3] = new Vector3D();
            return omega;
        }

        Vector3D forceL = il.mul(-sinOmega / cosecTheta).add(normalJIK);
        forceL = forceL.mul(cosecTheta / magIL);

        Vector3D forceJ = ik.mul(-cosTheta).add(ij);
        forceJ = forceJ.mul(-sinOmega * cosecTheta).add(normalKIL);
        forceJ = forceJ.mul(cosecTheta / magIJ);

        Vector3D forceK = ij.mul(-cosTheta).add(ik);
        forceK = forceK.mul(-sinOmega * cosecTheta).add(normalLIJ);
        forceK = forceK.mul(cosecTheta / magIK);

        //    i = -(j + k + l);
        Vector3D forceI = forceJ.add(forceK).add(forceL).mul(-1.0);

        forces[0] = forceI;
        forces[1] = forceJ;
        forces[2] = forceK;
        forces[3] = forceL;

        return omega;
    }
}
