package org.meta.math.mm;

import java.io.IOException;
import java.util.Hashtable;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.config.charmm.AngleParameters;
import org.meta.config.charmm.BondParameters;
import org.meta.config.charmm.CharmmParameterFileReader;
import org.meta.config.charmm.CharmmParameters;
import org.meta.config.charmm.DihedralParameters;
import org.meta.math.MathUtil;
import org.meta.math.Vector3D;
import org.meta.math.mm.mixing.LorentzBerthelotMixingMethod;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;

/**
 * Implements calculation of energy according to CHARMM force field
 * (see Brooks et al. J. Comp. Chem. 1983)
 * includes terms for the following interactions.
 * <ul>
 * <li>bond length</li>
 * <li>bond angle</li>
 * <li>dihedral angle torsion</li>
 * <li>improper torsions</li>
 * <li>nonbonded (van der Waals)</li>
 * <li>electrostatic</li>
 * <li>hydrogen bonding</li>
 * <li>atom harmonics</li>
 * <li>dihedral constraints</li>
 * </ul>
 * Note that CHARMM parameter files use kcal/mol for energy and degrees for
 * angles. Conversions are made on the fly during calculation.
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CharmmForceFieldMethod extends MMEnergyMethod {
    private final CharmmAtom[] atoms;
    private final CharmmParameters params;
    /** The dielectric constant: default is 1.0 = explicit treatment of solvent. */
    private double dielectricConstant = 1.0;

    public CharmmForceFieldMethod(String paramFile, Molecule molecule,
            boolean calculateForces) {
        super(molecule, new LorentzBerthelotMixingMethod(), calculateForces);
        atoms = new CharmmAtom[molecule.getNumberOfAtoms()];
        for (int i = 0; i < atoms.length; i++) {
            atoms[i] = createCharmmAtom(i);
        }
        determineConnectivity(atoms);
        CharmmParameterFileReader paramReader = new CharmmParameterFileReader();
        CharmmParameters params;
        try {
            params = paramReader.readParameterFile(paramFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("failed to read CHARMM parameter file: " + e.
                    getMessage());
            params = null;
        }
        this.params = params;
    }

    public void setDielectricConstant(double dielectricConstant) {
        // TODO read this from param file instead
        this.dielectricConstant = dielectricConstant;
    }

    @Override
    public MMAtom[] getAtoms() {
        return atoms;
    }

    /**
     * Evaluate energy function for the new set of variables
     *
     * @param variables new set of variables
     * @return the energy
     */
    @Override
    public double evaluate(
            double[] variables) {
        // set CharmmAtom centers according to variables
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

            //System.out.println("angle bend interaction = " + angleBendTerm);

            energy += angleBendTerm;
        }

        for (int i = 0; i < oneFourPairings.length; i++) {
            OneFourPairing oneFour = oneFourPairings[i];
            double dihedralTerm = getDihedralTerm(atoms[oneFour.i],
                    atoms[oneFour.j],
                    atoms[oneFour.k],
                    atoms[oneFour.l]);

            //System.out.println("dihedral interaction = " + dihedralTerm);
            energy += dihedralTerm;

            double vdwTerm = getVanDerWaalsTerm(atoms[oneFour.i],
                    atoms[oneFour.l], true);
            // TODO when to include 1,4 van der Waals ?
            //System.out.println("1,4 van der Waals interaction = " + vdwTerm);
            energy += vdwTerm;
        }

        for (int i = 0; i < invertibleAtoms.length; i++) {
            CharmmAtom atomI = (CharmmAtom) invertibleAtoms[i];
            double inversionTerm = getInversionTerm(atomI);
            if (inversionTerm > 0) {
                //System.out.println("inversion term = " + inversionTerm);
                }
            energy += inversionTerm;
        }

        for (int i = 0; i < nonbondedPairings.length; i++) {
            NonbondedPairing pair = nonbondedPairings[i];
            double electrostaticTerm = getElectrostaticTerm(atoms[pair.i],
                    atoms[pair.j]);
            //System.out.println("electrostatic interaction = " + electrostaticTerm);
            energy += electrostaticTerm;
            double vdwTerm = getVanDerWaalsTerm(atoms[pair.i],
                    atoms[pair.j], false);
            //System.out.println("van der Waals interaction = " + vdwTerm);
            energy += vdwTerm;
        }

        return Utility.KCAL_TO_AU_FACTOR * energy;
    }

    /**
     * @param atomI
     * @param atom2
     * @return the bond stretch contribution (in Hartrees)
     */
    private double getBondStretchTerm(CharmmAtom atomI, CharmmAtom atomJ) {
        String bondDesignation = BondParameters.getBondDesignation(atomI.
                getCharmmAtomType(), atomJ.getCharmmAtomType());
        //System.out.println(bondDesignation);
        BondParameters bondParams = params.bondParameters.get(bondDesignation);
        if (bondParams == null) {
            System.err.println("couldn't find bond params for " +
                    bondDesignation);
            return 0.0;
        }
        double kB = bondParams.bondStretchForce; // bond stretch constant
        double r0 = bondParams.naturalBondLength; // default bond length

        double r = getVector(atomI, atomJ).magnitude();
        double displacement = r - r0;
        //System.out.println("r = " + r + " r0 = " + r0);
        // equation 2 in CHARMM
        return kB * displacement * displacement;
    }

    private double getAngleBendTerm(CharmmAtom atomI, CharmmAtom atomJ,
            CharmmAtom atomK) {
        String angleDesignation = AngleParameters.getAngleDesignation(
                atomI.getCharmmAtomType(),
                atomJ.getCharmmAtomType(),
                atomK.getCharmmAtomType());
        //System.out.println(angleDesignation);
        AngleParameters angleParams = params.angleParameters.get(
                angleDesignation);
        if (angleParams == null) {
            System.err.println("couldn't find angle params for " +
                    angleDesignation);
            return 0.0;
        }

        double theta = MathUtil.toDegrees(getBondAngle(atomI, atomJ, atomK));

        //System.out.println("theta = " + theta + " theta0 = " + angleParams.naturalAngle);
        double angleBend = theta - angleParams.naturalAngle;
        //System.out.println("angleBend = " + angleBend + " kTheta = " + angleParams.angleBendForce);

        return angleParams.angleBendForce * angleBend * angleBend;
    }

    private double getDihedralTerm(CharmmAtom atomI, CharmmAtom atomJ,
            CharmmAtom atomK, CharmmAtom atomL) {
        DihedralParameters dihedralParams = params.getDihedralParameters(
                atomI.getCharmmAtomType(),
                atomJ.getCharmmAtomType(),
                atomK.getCharmmAtomType(),
                atomL.getCharmmAtomType());
        if (dihedralParams == null) {
            return 0.0;
        }

        double chi = Math.toDegrees(getTorsionAngle(atomI, atomJ, atomK, atomL));
        //System.out.println("chi = " + chi + " phase = " + dihedralParams.phase);

        return dihedralParams.dihedralForce * (1 + Math.cos(
                dihedralParams.multiplicity * chi - dihedralParams.phase));
    }

    /**
     * @param atomI an atom that may be invertible
     * @return the sum of inversion terms around atomI, if it is invertible
     */
    private double getInversionTerm(CharmmAtom atomI) {
        double inversionTerm = 0.0;
        if (atomI.isInvertible()) {
            Integer[] bondedAtoms = new Integer[3];
            bondedAtoms = atomI.getAtom().getConnectedList().keySet().toArray(
                    bondedAtoms);
            CharmmAtom atomJ = atoms[bondedAtoms[0].intValue()];
            CharmmAtom atomK = atoms[bondedAtoms[1].intValue()];
            CharmmAtom atomL = atoms[bondedAtoms[2].intValue()];


            // TODO values for anything other than NH2
            double forceConstant = 0.0717;
            double omega0 = 0.0;

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
                double diff = omega - omega0;
                //System.out.println("inversion angle = " + omega);
                double axisEnergy = forceConstant * diff * diff;
                //System.out.println("axisEnergy = " + axisEnergy);
                inversionTerm += axisEnergy;
            }

        }
        return inversionTerm;
    }

    /**
     * Gets the van der Waals interaction between two atoms I and J.
     * @param atomI
     * @param atomJ
     * @param is14Interaction true if atoms are 1,4 connected
     * @return the van der Waals potential between atomI and atomJ
     */
    public double getVanDerWaalsTerm(CharmmAtom atomI, CharmmAtom atomJ,
            boolean is14Interaction) {
        String atomTypeI = atomI.getCharmmAtomType();
        String atomTypeJ = atomJ.getCharmmAtomType();
        if (is14Interaction) {
            atomTypeI = atomTypeI + ",14";
            atomTypeJ = atomTypeJ + ",14";
        }
        //System.out.println("van der Waals: " + atomTypeI + " - " + atomTypeJ);
        LennardJonesParameters vdwParamsI = params.vanDerWaalsParameters.get(
                atomTypeI);
        if (vdwParamsI == null) {
            if (!is14Interaction) {
                System.err.println("couldn't find vdW params for " +
                        atomTypeI);
            }
            return 0.0;
        }
        LennardJonesParameters vdwParamsJ = params.vanDerWaalsParameters.get(
                atomTypeJ);
        if (vdwParamsJ == null) {
            if (!is14Interaction) {
                System.err.println("couldn't find vdW params for " +
                        atomTypeJ);
            }
            return 0.0;
        }

        LennardJonesParameters combinedParams = vdwParamsI;
        if (vdwParamsI != vdwParamsJ) {
            combinedParams = mixingMethod.getCombinedParameters(vdwParamsI,
                    vdwParamsJ);
        }
        Vector3D separation = getVector(atomI, atomJ);
        double distance = separation.magnitude();
        final double term = combinedParams.getBondRadius() / distance;
        double term6 = term * term * term;
        term6 = term6 * term6;
        // eq. 20 in UFF
        // Evdw = Dij{-2(x_ij/x)^6 + (x_ij/x)^12}
        //      = Dij{(x_ij/x)^6 * [(x_ij/x)^6 - 2.0]}
        return combinedParams.getWellDepth() * term6 *
                (term6 - 2.0);
    }

    /**
     * Gets the electrostatic interaction between two atoms I and J.
     * TODO anything faster than explicit calculation of all nonbonded pairs
     * @param atomI
     * @param atomJ
     * @return the electrostatic potential of atomI and atomJ
     */
    public double getElectrostaticTerm(CharmmAtom atomI, CharmmAtom atomJ) {
        double qi = atomI.getAtom().getCharge();
        double qj = atomJ.getAtom().getCharge();
        double rij = getVector(atomI, atomJ).magnitude();

        return qi * qj / dielectricConstant * rij;
    }

    private CharmmAtom createCharmmAtom(int i) {
        Atom atomI = (Atom) molecule.getAtom(i);
        final int atomicNumber = atomInfo.getAtomicNumber(atomI.getSymbol());
        final double atomicWeight = atomInfo.getAtomicWeight(atomI.getSymbol());
        return new CharmmAtom(atomI, atomicNumber, atomicWeight, atomI.
                getAtomCenter(), getCharmmAtomType(atomI, atomicNumber));
    }

    /**
     * Determines the CHARMM atom type based on the parameters in
     * par_all27_prot_lipid.prm.
     * TODO this is hopelessly inadequate for any real protein or lipid structure
     * @param atom an atom
     * @param atomicNumber the atomic number of the given atom
     * @return the CHARMM atom type from par_all27_prot_lipid.prm
     */
    private String getCharmmAtomType(Atom atom, int atomicNumber) {
        String charmmAtomType = atom.getSymbol();
        switch (atomicNumber) {
            case 1: // H
                if (getNumberConnected(atom, "C", null) > 0) {
                    charmmAtomType = "HA"; // alphatic hydrogen
                } else if (getNumberConnected(atom, "N", null) > 0) {
                    charmmAtomType = "HC"; // N-ter hydrogen
                } else if (getNumberConnected(atom, "S", null) > 0) {
                    charmmAtomType = "HS"; // thiol hydrogen
                }
                break;
            case 6: // C
                if (atom.hasResonantBonds()) {
                    charmmAtomType = "CA"; // aromatic carbon
                } else if (getNumberConnected(atom, "O", BondType.DOUBLE_BOND) >
                        0) {
                    charmmAtomType = "CD"; // carbonyl C, pres aspp,glup,ct1
                } else {
                    int numHydrogens = getNumberConnected(atom, "H", null);
                    if (numHydrogens > 0) {
                        charmmAtomType = "CT" + numHydrogens; // aliphatic carbon
                    }
                }
                break;
            case 7: // N
                int numHydrogens = getNumberConnected(atom, "H", null);
                if (numHydrogens > 0) {
                    int numCarbons = getNumberConnected(atom, "C", null);
                    if (numCarbons > 0) {
                        charmmAtomType = "NH" + numHydrogens;
                    } else {
                        // amide / peptide / ammonium nitrogen
                        charmmAtomType = "NH" + (numHydrogens - 1);
                    }
                }
                break;
            case 8: // O
                if (getNumberConnected(atom, "H", null) > 0) {
                    charmmAtomType = "OH1"; // hydroxyl oxygen
                }

            default:

        }

        return charmmAtomType;
    }

    /**
     * Gets the number of atoms of a given type connected to the given atom
     * through bonds of the given bondType.
     * @param atom an atom
     * @param atomType the atom symbol e.g. "S" = Sulfur
     * @param bondType a bondType e.g. BondType.SINGLE_BOND, null signifies any bond type
     * @return the number of atoms of the given type connected by the given bondType
     */
    private int getNumberConnected(Atom atom, String atomType, BondType bondType) {
        int numConnected = 0;
        Hashtable<Integer, BondType> connectedList = atom.getConnectedList();
        for (Integer atomIndex : connectedList.keySet()) {
            Atom atomJ = molecule.getAtom(atomIndex);
            if (atomJ.getSymbol().equals(atomType) &&
                    (bondType == null || bondType ==
                    connectedList.get(atomIndex))) {
                numConnected++;
            }
        }
        return numConnected;
    }
}
