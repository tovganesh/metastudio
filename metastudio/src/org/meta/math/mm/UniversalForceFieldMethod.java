package org.meta.math.mm;

import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.config.impl.UffParameters;
import org.meta.config.impl.UffParameters.UffCoordinationParameters;
import org.meta.config.impl.UffParametersConfiguration;
import org.meta.math.MathUtil;
import org.meta.math.Vector3D;
import org.meta.math.mm.mixing.GeometricMixingMethod;
import org.meta.molecule.Atom;
import org.meta.molecule.Coordination;
import org.meta.molecule.Hybridisation;
import org.meta.molecule.Molecule;

/**
 * Implements calculation of energy according to Universal Force Field
 * (see Rappé et al. J. Am. Chem. Soc. 1992)
 * includes terms for the following interactions.
 * <ul>
 * <li>bond stretching</li>
 * <li>bond angle bending</li>
 * <li>dihedral angle torsion</li>
 * <li>inversion</li>
 * <li>nonbonded (van der Waals)</li>
 * <li>electrostatic</li>
 * </ul>
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class UniversalForceFieldMethod extends MMEnergyMethod {
    /** Bond order proportionality constant Lambda in eq. 3 of UFF. */
    private static final double BOND_ORDER_PROPORTIONALITY_CONSTANT = -0.1332;
    private UffParametersConfiguration uffConfig;
    private final UffAtom[] atoms;

    /**
     * @param molecule the molecule for which to calculate the energy
     */
    public UniversalForceFieldMethod(final Molecule molecule,
            final boolean calculateForces) {
        super(molecule, new GeometricMixingMethod(), calculateForces);
        this.uffConfig = UffParametersConfiguration.getInstance();
        this.atoms = new UffAtom[molecule.getNumberOfAtoms()];
        for (int i = 0; i < atoms.length; i++) {
            atoms[i] = createUffAtom(i);
        }
        determineConnectivity(atoms);
    }

    @Override
    public UffAtom[] getAtoms() {
        return atoms;
    }

    /**
     * Evaluate energy function for the new set of variables
     *
     * @param variables new set of variables
     * @return the energy
     */
    @Override
    public double evaluate(double[] variables) {
        // set UffAtom centers according to variables
        for (int i = 0;
                i < atoms.length; i++) {
            atoms[i].setAtomCenter(new Point3D(variables[i * 3],
                    variables[i * 3 + 1], variables[i * 3 + 2]));
            atoms[i].setForce(new Vector3D());
        }

        double energy = 0.0;

        for (int i = 0; i < bondedPairings.length; i++) {
            BondedPairing pair = bondedPairings[i];
            double bondStretchTerm = getBondStretchTerm(atoms[pair.i],
                    atoms[pair.j]);
            //System.out.println("bond stretching interaction = " + bondStretchTerm);
            energy += bondStretchTerm;
        }

        for (int i = 0; i < oneThreePairings.length; i++) {
            OneThreePairing oneThree = oneThreePairings[i];
            double angleBendTerm = getAngleBendTerm(atoms[oneThree.i],
                    atoms[oneThree.j],
                    atoms[oneThree.k]);

            //System.out.println("angleIJK bend interaction = " + angleBendTerm);

            energy += angleBendTerm;
        }

        for (int i = 0; i < oneFourPairings.length; i++) {
            OneFourPairing oneFour = oneFourPairings[i];
            double torsionTerm = getTorsionTerm(atoms[oneFour.i],
                    atoms[oneFour.j],
                    atoms[oneFour.k], atoms[oneFour.l]);

            //System.out.println("torsion interaction = " + torsionTerm);

            energy += torsionTerm;
        }

        for (int i = 0; i < nonbondedPairings.length; i++) {
            NonbondedPairing nonbonded = nonbondedPairings[i];
            UffAtom atomI = atoms[nonbonded.i];
            UffAtom atomJ = atoms[nonbonded.j];
            double vdwTerm = getVanDerWaalsTerm(atomI, atomJ);

            //System.out.println("van der Waals interaction = " + vdwTerm);

            energy += vdwTerm;

        // TODO disabled electrostatics - neither Towhee nor UFF appear to use it!
                        /*
        final double electrostaticTerm = getElectrostaticTerm(r, atomI, atomJ);
        System.out.println("electrostatic interaction = " + electrostaticTerm);
        energy += electrostaticTerm;
         */
        }

        for (int i = 0; i < invertibleAtoms.length; i++) {
            UffAtom atomI = (UffAtom) invertibleAtoms[i];
            double inversionTerm = getInversionTerm(atomI);
            if (inversionTerm > 0) {
                //System.out.println("inversion term = " + inversionTerm);
                }
            energy += inversionTerm;
        }

        return energy;
    }

    private UffAtom createUffAtom(int i) {
        Atom atomI = (Atom) molecule.getAtom(i);
        final UffParameters paramsI = uffConfig.getUffParameters(
                atomI.getSymbol());
        final Coordination coordination = determineCoordinationGeometry(atomI);
        final UffCoordinationParameters coordinationParamsI =
                paramsI.getCoordinationParams(coordination.
                getUffGeometrySpecification());
        final int atomicNumber = atomInfo.getAtomicNumber(atomI.getSymbol());
        final double atomicWeight = atomInfo.getAtomicWeight(atomI.getSymbol());
        final UffAtom uffAtomI = new UffAtom(atomI, atomI.getAtomCenter(),
                paramsI, coordination,
                coordinationParamsI, atomicNumber, atomicWeight);
        return uffAtomI;
    }

    /**
     * @param distance distance between atoms I and J
     * @param bondOrder the bond order e.g. 1.0 = single, 2.0 = double, 1.41 = amide
     * @param atomI
     * @param atom2
     * @return the bond stretch contribution (in Hartrees)
     */
    private double getBondStretchTerm(UffAtom atomI, UffAtom atomJ) {
        double distance = Math.sqrt(atomI.getAtomCenter().distanceSquaredFrom(
                atomJ.getAtomCenter()));
        double bondOrder = atomI.getAtom().getConnectivity(atomJ.getAtom().
                getIndex()).getBondOrder();
        double naturalDistance = getNaturalBondRadius(bondOrder, atomI, atomJ);
        //System.out.println("naturalDistance = " + naturalDistance);

        // eq. 6 of UFF
        double forceConstant = Utility.AU_TO_ANGSTROM_FACTOR * atomI.getParams().
                getEffectiveCharge() * atomJ.getParams().getEffectiveCharge() /
                (naturalDistance * naturalDistance * naturalDistance);
        //System.out.println("bond stretching force: " + forceConstant);
        final double stretch = distance - naturalDistance;
        if (calculateForces) {
            Vector3D direction = getVector(atomJ, atomI);
            Vector3D force = direction.normalize().mul(2 * forceConstant *
                    stretch);
            //System.out.println("bond stretching force: " + force);
            atomI.addForceComponent(force);
            atomJ.addForceComponent(force.negate());
        }

        // eq. 1a of UFF
        return forceConstant * stretch * stretch;
    }

    private double getAngleBendTerm(UffAtom atomI, UffAtom atomJ, UffAtom atomK) {
        final double theta0 = Math.toRadians(atomJ.getCoordinationParams().
                getBondAngle());
        double cosTheta0 = Math.cos(theta0);
        double sinTheta0 = Math.sin(theta0);

        double theta;
        Vector3D[] forces = new Vector3D[3];
        if (calculateForces) {
            theta = getBondAngleAndForces(atomI, atomJ, atomK, forces);
        } else {
            theta = getBondAngle(atomI, atomJ, atomK);
        }
        double cosTheta = Math.cos(theta);

        //System.out.println("bond angle = " + theta + " (" + Math.toDegrees(theta) + "°)");
        //System.out.println("natural bond angle = " + theta0 + " (" + Math.toDegrees(theta0) + "°)");

        double charge1 = atomI.getParams().getEffectiveCharge();
        double charge3 = atomK.getParams().getEffectiveCharge();
        double rIJ = getNaturalBondRadius(atomI.getAtom().getConnectivity(atomJ.
                getAtom().getIndex()).getBondOrder(), atomI, atomJ);
        double rJK = getNaturalBondRadius(atomJ.getAtom().getConnectivity(atomK.
                getAtom().getIndex()).getBondOrder(), atomJ, atomK);
        double rIK = Math.sqrt(rIJ * rIJ + rJK * rJK - 2.0 * rIJ * rJK *
                cosTheta0);
        //System.out.println("r12 = " + r12 + ", r23 = " + r23 + ", r13 = " + r13);
        double ka = Utility.AU_TO_ANGSTROM_FACTOR * 2.0 * (charge1 * charge3 /
                (Math.pow(rIK, 5.0))) * (3.0 * rIJ * rJK *
                (1.0 - cosTheta0 * cosTheta0) -
                rIK * rIK * cosTheta0);

        Coordination coordination = atomJ.getCoordination();
        /*
        System.out.println("coordination {" + atomI.getAtom().getSymbol() + "," + atom2.getAtom().
        getSymbol() + "," + atom3.getAtom().getSymbol() + "} = " +
        coordination);
         */
        double c0, c1, c2;
        switch (coordination) {
            case Linear:
            case TrigonalPlanar:
            case SquarePlanar:
            case Octahedral:
                // eq. 10 in UFF
                c0 = c1 = c2 = 0;
                break;
            default:
                // eq. 12 in UFF
                c2 = 1.0 / (4.0 * sinTheta0 * sinTheta0);
                c1 = -4.0 * c2 * cosTheta0;
                c0 = c2 * (2.0 * cosTheta0 * cosTheta0 + 1.0);
        }

        double angleBendTerm = 0.0;
        if ((coordination == Coordination.SquarePlanar || coordination ==
                Coordination.Octahedral) &&
                (theta > (Math.PI / 4.0 * 3.0) || theta < Math.PI / 4.0)) {
            // from JMol - use linear coordination to allow for straight angleIJK
            //bonds in square planar or octahedral coordination
            coordination = Coordination.Linear;
        }
        switch (coordination) {
            case None: // constraint
            case Linear: //sp
                angleBendTerm = ka * (1.0 + cosTheta) * (1.0 + cosTheta) / 4.0;
                break;
            case TrigonalPlanar: //sp2
                //(1 + 4cos(theta) + 4cos(theta)^2)/9
                angleBendTerm =
                        ka * (1.0 + (4.0 * cosTheta) * (1.0 + cosTheta)) / 9.0;
                break;
            case SquarePlanar: //dsp2
            case Octahedral: //d2sp3
                angleBendTerm = ka * cosTheta * cosTheta;
                break;
            default:
                // E_theta = K_ijk [C_0 + C_1 cos(theta) + C_2 cos(2*theta)]
                // NOTE: Towhee uses K_ijk [(C_0-C_2) + C_1 cos(theta) + (2*C_2 cos(2*theta))] - don't understand why yet
                angleBendTerm = ka * (c0 - c2 + c1 * cosTheta + 2 * c2 *
                        cosTheta * cosTheta);
        }

        if (calculateForces) {
            double forceConstant;
            double sinTheta = Math.sin(theta);

            // da = dTheta/dx * dE/dTheta
            switch (coordination) {
                case None: // constraint
                case Single: //sp
                    forceConstant = 0.5 * ka * sinTheta * (1 + cosTheta);
                    break;
                case Linear: //sp2
                    forceConstant = 4.0 * sinTheta * ka *
                            (1.0 - 2.0 * cosTheta) / 9.0;
                    break;
                case Tetrahedral: //dsp2F
                case Octahedral: //d2sp3
                    forceConstant = ka * sinTheta * cosTheta;
                    break;
                default:
                    forceConstant = ka * (c1 * sinTheta - 4.0 * c2 * cosTheta *
                            sinTheta);
            }
            //System.out.println("angle bending forces: constant: " + forceConstant + " i: " + forces[0] + " j: " + forces[1] + " k:" + forces[2]);
            atomI.addForceComponent(forces[0], forceConstant);
            atomJ.addForceComponent(forces[1], forceConstant);
            atomK.addForceComponent(forces[2], forceConstant);
        }

        return angleBendTerm;
    }

    private double getTorsionTerm(UffAtom atomI, UffAtom atomJ, UffAtom atomK,
            UffAtom atomL) {
        double torsionalBarrier = 0.0;
        double cosNPhi0 = -1.0; // n * phi0 = PI; max at 0
        double n = 2.0;
        if (atomJ.getCoordination().getHybridisation() == Hybridisation.sp3 && atomK.getCoordination().
                getHybridisation() == Hybridisation.sp3) {
            // sp3-sp3, eq. 16 of UFF
            torsionalBarrier = Math.sqrt(atomJ.getCoordinationParams().
                    getSp3TorsionalBarrier() * atomK.getCoordinationParams().
                    getSp3TorsionalBarrier());
            n = 3.0;
            cosNPhi0 = -1.0; // phi0 = PI; cos(3 * PI) = -1.0
        //System.out.println("sp3-sp3, torsionalBarrier = " + torsionalBarrier);
        } else if (atomJ.getCoordination().getHybridisation() ==
                Hybridisation.sp3 &&
                atomK.getCoordination().getHybridisation() == Hybridisation.sp2 || atomJ.getCoordination().
                getHybridisation() == Hybridisation.sp2 && atomK.getCoordination().
                getHybridisation() == Hybridisation.sp3) {
            // sp3-sp2
            torsionalBarrier = 2.0;
            n = 6.0;
            cosNPhi0 = 1.0; // phi0 = 0; cos(6 * 0) = 1.0
        //System.out.println("sp3-sp2, torsionalBarrier = " + torsionalBarrier);
        } else if (atomJ.getCoordination().getHybridisation() ==
                Hybridisation.sp2 &&
                atomK.getCoordination().getHybridisation() == Hybridisation.sp2) {
            // sp2-sp2, eq. 17 of UFF
            double bondOrder = atomJ.getAtom().getConnectivity(atomK.getAtom().
                    getIndex()).getBondOrder();
            torsionalBarrier = 5.0 * Math.sqrt(atomJ.getCoordinationParams().
                    getSp2TorsionalBarrier() * atomK.getCoordinationParams().
                    getSp2TorsionalBarrier()) * (1.0 + 4.18 *
                    Math.log(bondOrder));
            n = 2.0;
            cosNPhi0 = 1.0; // phi0 = PI; cos(2 * PI) = 1.0
        //System.out.println("sp2-sp2, torsionalBarrier = " + torsionalBarrier);
        }

        double phi = 0.0;
        if (calculateForces) {
            Vector3D[] forces = new Vector3D[4];
            phi = getTorsionAngleAndForces(atomI, atomJ, atomK, atomL, forces);
            double forceConstant = 0.5 * Utility.KCAL_TO_AU_FACTOR *
                    torsionalBarrier * n * cosNPhi0 * Math.sin(n * phi);
            if (!MathUtil.isNearZero(forceConstant)) {
                //System.out.println("torsion forces: phi: " + phi + " constant: " + forceConstant + " i: " + forces[0] + " j: " + forces[1] + " k:" + forces[2] + " l:" + forces[3]);
                atomI.addForceComponent(forces[0], forceConstant);
                atomJ.addForceComponent(forces[1], forceConstant);
                atomK.addForceComponent(forces[2], forceConstant);
                atomL.addForceComponent(forces[3], forceConstant);
            }
        } else {
            phi = getTorsionAngle(atomI, atomJ, atomK, atomL);
        }

        double cosNPhi = 1.0; // default if near zero torsion angle
        if (!MathUtil.isNearZero(phi)) {
            cosNPhi = Math.cos(n * phi);
        }
        // eq. 15 of UFF
        return 0.5 * Utility.KCAL_TO_AU_FACTOR * torsionalBarrier * (1 - cosNPhi0 *
                cosNPhi);
    }

    /**
     * @param atomI an atom that may be invertible
     * @return the sum of inversion terms around atomI, if it is invertible
     */
    private double getInversionTerm(UffAtom atomI) {
        double inversionTerm = 0.0;
        if (atomI.isInvertible()) {
            Integer[] bondedAtoms = new Integer[3];
            bondedAtoms = atomI.getAtom().getConnectedList().keySet().toArray(
                    bondedAtoms);
            UffAtom atomJ = atoms[bondedAtoms[0].intValue()];
            UffAtom atomK = atoms[bondedAtoms[1].intValue()];
            UffAtom atomL = atoms[bondedAtoms[2].intValue()];

            double a0 = 1.0;
            double a1 = -1.0;
            double a2 = 0.0;
            double forceConstant; // K_IJKL in eg. 18 of UFF
            switch (atomI.getAtomicNumber()) {
                case 6: // C
                    if (isTrigonalOxygen(atomJ) || isTrigonalOxygen(atomK) ||
                            isTrigonalOxygen(atomL)) {
                        // this is a carbonyl group, which has a stronger barrier to inversion
                        forceConstant = 50.0 * Utility.KCAL_TO_AU_FACTOR;
                        break;
                    }
                // TODO force flat aromatic rings?
                case 7: // N
                case 8: // O
                    forceConstant = 6.0 * Utility.KCAL_TO_AU_FACTOR;
                    break;
                default:
                    forceConstant = 22.0 * Utility.KCAL_TO_AU_FACTOR;
                    double phi = 0.0;
                    switch (atomI.getAtomicNumber()) {
                        case 15: // P
                            phi = 1.47365;
                            break;
                        case 33: // As
                            phi = 1.51797;
                            break;
                        case 51: // Sb
                            phi = 1.53074;
                            break;
                        case 83: // Bi
                            phi = Math.PI / 2.0;
                            break;
                        default:
                            System.err.println("Trying to get inversion term for an unknown atomic number: " +
                                    atomI.getAtomicNumber());
                    }
                    double cosPhi = Math.cos(phi);
                    a0 = cosPhi * cosPhi;
                    a1 = -2.0 * cosPhi;
                    a2 = 1.0;
                //
                // same as:
                //
                // E = K [ cos(theta) - cos(phi)]^2
                //
                //phi ~ 90, so c0 ~ 0, c1 ~ 0.5, and E(0) ~ K
            }
            forceConstant /= 3.0;

            //System.out.println("forceConstant = " + forceConstant);

            Point3D[] atomLocations = new Point3D[4];
            Vector3D[] forces = new Vector3D[4];
            atomLocations[0] = atomI.getAtomCenter();
            for (int i = 0; i < 3; i++) {
                // consider each unique axis IL, IJ, IK
                atomLocations[i % 3 + 1] = atomJ.getAtomCenter();
                atomLocations[(i + 1) % 3 + 1] = atomK.getAtomCenter();
                atomLocations[(i + 2) % 3 + 1] = atomL.getAtomCenter();
                double omega = getInversionAngleAndForces(atomLocations, forces);
                double cosOmega = Math.cos(omega);
                //System.out.println("inversion angle = " + omega);
                double axisEnergy = forceConstant * (a0 + a1 * cosOmega + a2 *
                        cosOmega * cosOmega);
                //System.out.println("axisEnergy = " + axisEnergy);
                inversionTerm += axisEnergy;
                if (calculateForces) {
                    atomI.addForceComponent(forces[0], forceConstant);
                    atomJ.addForceComponent(forces[i % 3 + 1], forceConstant);
                    atomK.addForceComponent(forces[(i + 1) % 3 + 1],
                            forceConstant);
                    atomL.addForceComponent(forces[(i + 2) % 3 + 1],
                            forceConstant);
                }
            }

        }
        return inversionTerm;
    }

    /**
     * @param atom an atom
     * @return true if the atom is oxygen in a trigonal planar coordination
     */
    private boolean isTrigonalOxygen(UffAtom atom) {
        return (atom.getAtomicNumber() == 8 && atom.getCoordination() ==
                Coordination.TrigonalPlanar);
    }

    /**
     * @param distance internuclear separation
     * @param ljParamsI Lennard-Jones parameters for atom I
     * @param ljParamsJ Lennard-Jones parameters for atom J
     * @return van der Waals interaction between I, J (in Hartrees) according to Lennard-Jones potential
     */
    private double getVanDerWaalsTerm(UffAtom atomI, UffAtom atomJ) {
        Vector3D separation = getVector(atomI, atomJ);
        double distance = separation.magnitude();
        final LennardJonesParameters combinedParams = mixingMethod.
                getCombinedParameters(
                atomI.getParams().getVdwParams(),
                atomJ.getParams().getVdwParams());
        final double term = combinedParams.getBondRadius() / distance;
        double term6 = term * term * term;
        term6 = term6 * term6;
        // eq. 20 in UFF
        // Evdw = Dij{-2(x_ij/x)^6 + (x_ij/x)^12}
        //      = Dij{(x_ij/x)^6 * [(x_ij/x)^6 - 2.0]}
        final double pairEnergy = combinedParams.getWellDepth() * term6 *
                (term6 - 2.0);

        if (calculateForces) {
            double forceConstant = combinedParams.getWellDepth() * 12.0 *
                    (1.0 - term6) * term6 * term /
                    combinedParams.getBondRadius();
            Vector3D i = separation.mul(forceConstant);
            Vector3D j = i.negate();
            /* System.out.println("vdW force: " + forceConstant + " i = " + i +
            " j = " + j);*/
            atomI.addForceComponent(i);
            atomJ.addForceComponent(j);
        }
        return pairEnergy;
    }

    /**
     * Note: this isn't used in either UFF or Towhee
     * (and consequently, not in this implementation).
     * @param distance separation between chargeI and chargeJ (in Angstroms)
     * @param chargeI effective partial charge (in atomic units)
     * @param chargeJ effective partial charge (in atomic units)
     * @return electrostatic interaction (in Hartrees)
     */
    private double getElectrostaticTerm(double distance, UffAtom atomI,
            UffAtom atomJ) {
        return Utility.AU_TO_ANGSTROM_FACTOR * (atomI.getParams().
                getEffectiveCharge() * atomJ.getParams().getEffectiveCharge() /
                distance);
    }

    /**
     * Determines coordination geometry for the given atom within the molecule.
     * @param atom the atom of interest
     * @return the coordination geometry in the molecule
     */
    private Coordination determineCoordinationGeometry(Atom atom) {
        final String symbol = atom.getSymbol();
        if (symbol.equals("C") || symbol.equals("O") || symbol.equals("N") ||
                symbol.equals("S")) {
            // UFF only allows C, N, O, S resonant bonds
            if (atom.hasResonantBonds()) {
                return Coordination.Resonant;
            }
        }

        int numBonds = atom.getNumberOfStrongBonds();

        // HACK at least get this right for carbon
        if (symbol.equals("C")) {
            switch (numBonds) {
                case 4:
                    return Coordination.Tetrahedral;
                case 3:
                    return Coordination.TrigonalPlanar;
                case 2:
                    return Coordination.Linear;
            }
        }

        // TODO: need to consider non-regular coodinations e.g. bent -  probably need to consider steric number
        switch (numBonds) {
            case 0:
                return Coordination.None;
            case 1:
                return Coordination.Single;
            case 2:
                Integer[] bonded2 = new Integer[2];
                bonded2 = atom.getConnectedList().keySet().toArray(
                        bonded2);
                Atom atomA = molecule.getAtom(bonded2[0].intValue());
                Atom atomB = molecule.getAtom(bonded2[1].intValue());
                double bondAngle2 = getCoordinationBondAngle(atom, atomA, atomB);
                if (MathUtil.isNearZero(bondAngle2 - Math.PI, 2.0e-2)) {
                    return Coordination.Linear;
                } else if (MathUtil.isNearZero(bondAngle2 - Math.PI / 3.0 * 2.0,
                        2.0e-2)) {
                    return Coordination.BentTrigonal;
                } else {
                    return Coordination.BentTetrahedral;
                }
            case 3:
                Integer[] bonded3 = new Integer[3];
                bonded3 = atom.getConnectedList().keySet().toArray(
                        bonded3);
                Atom atomC = molecule.getAtom(bonded3[0].intValue());
                Atom atomD = molecule.getAtom(bonded3[1].intValue());
                double bondAngle3 = getCoordinationBondAngle(atom, atomC, atomD);
                if (MathUtil.isNearZero(bondAngle3 - (Math.PI / 3.0 * 2.0),
                        2.0e-2)) {
                    return Coordination.TrigonalPlanar;
                } else if (MathUtil.isNearZero(bondAngle3 - Math.PI / 2.0,
                        2.0e-2) ||
                        MathUtil.isNearZero(bondAngle3 - Math.PI, 2.0e-2)) {
                    return Coordination.TShape;
                } else {
                    return Coordination.TrigonalPyramidal;
                }
            case 4:
                Integer[] bonded4 = new Integer[4];
                bonded4 = atom.getConnectedList().keySet().toArray(
                        bonded4);
                Atom atomE = molecule.getAtom(bonded4[0].intValue());
                Atom atomF = molecule.getAtom(bonded4[1].intValue());
                double bondAngle4 = getCoordinationBondAngle(atom, atomE, atomF);
                if (MathUtil.isNearZero(bondAngle4 - (Math.PI / 2.0)) ||
                        MathUtil.isNearZero(bondAngle4 - Math.PI)) {
                    return Coordination.SquarePlanar;
                } else {
                    return Coordination.Tetrahedral;
                }
            case 6:
                return Coordination.Octahedral;
        }

        return Coordination.Tetrahedral;
    }

    /**
     * Get the natural bond radius for bonded atoms, which is the sum of the
     * bond radii of the two atoms, plus a bond order correction, minus an
     * electronegativity correction.  See eq. 2 of UFF.
     * NOTE: there is a typo in the UFF paper, it suggests the electronegativity
     * correction should be added.
     * see http://towhee.sourceforge.net/forcefields/uff.html
     * @param bondOrder the bond order e.g. 1.0 = single, 2.0 = double, 1.41 = amide
     * @param atomI first atom
     * @param atomJ second atom
     * @return the natural bond radius between the atoms
     */
    private double getNaturalBondRadius(double bondOrder, UffAtom atomI,
            UffAtom atomJ) {
        final double radiusI = atomI.getCoordinationParams().getBondRadius();
        final double radiusJ = atomJ.getCoordinationParams().getBondRadius();
        final double naturalRadius = radiusI + radiusJ; // default value for C_3-H_ is 1.111
        //System.out.println("naturalRadius = " + naturalRadius);
        // eq. 3 of UFF
        double bondOrderCorrection = BOND_ORDER_PROPORTIONALITY_CONSTANT *
                naturalRadius * Math.log(bondOrder);
        //System.out.println("bondOrderCorrection = " + bondOrderCorrection);
        final double chiI = atomI.getParams().getElectronegativity();
        final double chiJ = atomJ.getParams().getElectronegativity();
        double dSqrtChi = Math.sqrt(chiI) - Math.sqrt(chiJ);
        // eq. 4 of UFF
        double electronegativityCorrection = radiusI * radiusJ * (dSqrtChi *
                dSqrtChi) / (chiI * radiusI + chiJ * radiusJ);

        return (naturalRadius + bondOrderCorrection -
                electronegativityCorrection);
    }
}
