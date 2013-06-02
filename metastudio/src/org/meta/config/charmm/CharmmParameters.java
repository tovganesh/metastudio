package org.meta.config.charmm;

import java.util.HashMap;
import org.meta.math.mm.LennardJonesParameters;

/**
 * This class represents the CHARMM parameters as read from a .prm file.
 * @author Josh Milthorpe
 */
public class CharmmParameters {
    public HashMap<String, BondParameters> bondParameters;
    public HashMap<String, AngleParameters> angleParameters;
    public HashMap<String, DihedralParameters> dihedralParameters;
    public HashMap<String, LennardJonesParameters> vanDerWaalsParameters;

    /**
     * Gets the dihedral parameters for the given atom types.
     * If no specific dihedral params can be found, try generic parameters
     * using the "wildcard" markers allowed in CHARMM param files.
     * @param atomIType
     * @param atomJType
     * @param atomKType
     * @param atomLType
     * @return dihedral parameters for the given atom types, or null if none could be found
     */
    public DihedralParameters getDihedralParameters(String atomIType,
            String atomJType, String atomKType, String atomLType) {
        String dihedralDesignation = DihedralParameters.getDihedralDesignation(
                atomIType,
                atomJType,
                atomKType,
                atomLType);
        //System.out.println(dihedralDesignation);
        DihedralParameters dihedralParams =
                dihedralParameters.get(dihedralDesignation);
        if (dihedralParams == null) {
            // try "X" wildcard designation
            String genericDesignation =
                    DihedralParameters.getDihedralDesignation("X",
                    atomJType, atomKType, "X");
            dihedralParams = dihedralParameters.get(genericDesignation);
            if (dihedralParams == null) {
                System.err.println("couldn't find dihedral params for " +
                        dihedralDesignation);
            }
        }

        return dihedralParams;
    }
}
