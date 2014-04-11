package org.meta.config.charmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.meta.math.mm.LennardJonesParameters;

/**
 *
 * @author josh
 */
public class CharmmParameterFileReader {
    public CharmmParameters readParameterFile(String fileName) throws IOException {
        CharmmParameters params = new CharmmParameters();

        File paramFile = new File(fileName);
        FileInputStream inputStream = new FileInputStream(paramFile);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream));

        String line = null;
        try {
            line = reader.readLine();
            while (line.indexOf("BOND") < 0) {
                line = reader.readLine();
            }
            ArrayList<String> bondLines = new ArrayList<String>();
            line = reader.readLine();
            while (line.indexOf("ANGL") < 0) {
                if (!(line.startsWith("!") || line.isEmpty())) {
                    // ignore comments and blank lines
                    bondLines.add(line);
                }
                line = reader.readLine().trim();
            }
            params.bondParameters =
                    getBondParams(bondLines);
            System.out.println("found " + params.bondParameters.size() +
                    " bond parameters");
            ArrayList<String> angleLines = new ArrayList<String>();
            line = reader.readLine();
            while (line.indexOf("DIHE") < 0) {
                if (!(line.startsWith("!") || line.isEmpty())) {
                    // ignore comments and blank lines
                    angleLines.add(line);
                }
                line = reader.readLine().trim();
            }
            params.angleParameters = getAngleParams(angleLines);
            System.out.println("found " + params.angleParameters.size() +
                    " angle parameters");
            ArrayList<String> dihedralLines = new ArrayList<String>();
            line = reader.readLine();
            while (line.indexOf("IMPR") < 0) {
                if (!(line.startsWith("!") || line.isEmpty())) {
                    // ignore comments and blank lines
                    dihedralLines.add(line);
                }
                line = reader.readLine().trim();
            }
            params.dihedralParameters = getDihedralParams(dihedralLines);
            System.out.println("found " + params.dihedralParameters.size() +
                    " dihedral parameters");
            // TODO other params
            while (line.indexOf("NONB") < 0) {
                // skip everything up to vdW params
                line = reader.readLine().trim();
            }
            ArrayList<String> nonbondLines = new ArrayList<String>();
            line = reader.readLine();
            if (line.indexOf("cutnb") >= 0) {
                line = reader.readLine().trim();
            }
            while (line.indexOf("NBFIX") != 0 && line.indexOf("HBOND") != 0 &&
                    line.indexOf("END") != 0) {
                if (!(line.startsWith("!") || line.isEmpty())) {
                    // ignore comments and blank lines
                    if (line.indexOf("!") >= 0) {
                        line = line.substring(0, line.indexOf("!"));
                    }
                    nonbondLines.add(line);
                }
                line = reader.readLine().trim();
            }
            params.vanDerWaalsParameters =
                    getVanDerWaalsParameters(nonbondLines);
            System.out.println("found " + params.vanDerWaalsParameters.size() +
                    " van der Waals parameters");


        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error reading file, current line:\n" +
                    line + "\n Exception is : " + e.toString());
        } // end of try .. catch block

        inputStream.close();

        return params;
    }

    private HashMap<String, BondParameters> getBondParams(
            ArrayList<String> bondLines) {

        HashMap<String, BondParameters> bondParams =
                new HashMap<String, BondParameters>(bondLines.size());
        for (String bondLine : bondLines) {
            String[] tokens = bondLine.split("\\s+");
            String firstAtom = tokens[0];
            String secondAtom = tokens[1];
            double bondStretchForce = Double.parseDouble(tokens[2]);
            double bondDistance = Double.parseDouble(tokens[3]);
            BondParameters params = new BondParameters(firstAtom, secondAtom,
                    bondStretchForce, bondDistance);
            bondParams.put(params.getBondDesignation(), params);
        }
        return bondParams;
    }

    private HashMap<String, AngleParameters> getAngleParams(
            ArrayList<String> angleLines) {

        HashMap<String, AngleParameters> angleParameters =
                new HashMap<String, AngleParameters>(angleLines.size());
        for (String bondLine : angleLines) {
            String[] tokens = bondLine.split("\\s+");
            String firstAtom = tokens[0];
            String secondAtom = tokens[1];
            String thirdAtom = tokens[2];
            double bondStretchForce = Double.parseDouble(tokens[3]);
            double bondDistance = Double.parseDouble(tokens[4]);
            AngleParameters params = new AngleParameters(firstAtom, secondAtom,
                    thirdAtom, bondStretchForce, bondDistance);
            angleParameters.put(params.getAngleDesignation(), params);
        }
        return angleParameters;
    }

    private HashMap<String, DihedralParameters> getDihedralParams(
            ArrayList<String> dihedralLines) {

        HashMap<String, DihedralParameters> dihedralParameters =
                new HashMap<String, DihedralParameters>(dihedralLines.size());
        for (String bondLine : dihedralLines) {
            String[] tokens = bondLine.split("\\s+");
            String firstAtom = tokens[0];
            String secondAtom = tokens[1];
            String thirdAtom = tokens[2];
            String fourthAtom = tokens[3];
            double dihedralForce = Double.parseDouble(tokens[4]);
            int multiplicity = Integer.parseInt(tokens[5]);
            double phase = Double.parseDouble(tokens[6]);
            DihedralParameters params = new DihedralParameters(firstAtom,
                    secondAtom,
                    thirdAtom, fourthAtom, dihedralForce, multiplicity, phase);
            dihedralParameters.put(params.getDihedralDesignation(), params);
        }
        return dihedralParameters;
    }

    private HashMap<String, LennardJonesParameters> getVanDerWaalsParameters(
            ArrayList<String> nonbondLines) {

        HashMap<String, LennardJonesParameters> vanDerWaalsParameters =
                new HashMap<String, LennardJonesParameters>(nonbondLines.size());
        for (String bondLine : nonbondLines) {
            String[] tokens = bondLine.split("\\s+");
            String atomType = tokens[0];
            // field 1 ignored
            double epsilon = Double.parseDouble(tokens[2]);
            double rmin = Double.parseDouble(tokens[3]);
            double epsilon14 = 0.0;
            double rmin14 = 0.0;
            LennardJonesParameters params = new LennardJonesParameters(
                    epsilon,
                    rmin);
            vanDerWaalsParameters.put(atomType, params);

            if (tokens.length >= 7) {
                // 1,4 params present
                // field 4 ignored
                epsilon14 = Double.parseDouble(tokens[5]);
                rmin14 = Double.parseDouble(tokens[6]);
                LennardJonesParameters params14 = new LennardJonesParameters(
                        epsilon14,
                        rmin14);
                vanDerWaalsParameters.put(atomType + ",14", params14);
            }
        }
        return vanDerWaalsParameters;
    }
}
