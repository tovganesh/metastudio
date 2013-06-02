/*
 * SDFMoleculeFileReader.java 
 *
 * Created on 12 Sep, 2008 
 */
package org.meta.moleculereader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.meta.common.ScientificStreamTokenizer;
import org.meta.common.Utility;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.moleculereader.AbstractMoleculeFileReader;

/**
 * A simple implementation of MoleculeFileReader interface for reading SDF 
 * file format.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SDFMoleculeFileReader extends AbstractMoleculeFileReader {
    private Hashtable<String, String> supportedExtensions;

    /** Creates a new instance of MolMoleculeFileReader */
    public SDFMoleculeFileReader() {
        supportedExtensions = new Hashtable<String, String>(2);
        supportedExtensions.put("sdf", "SDF file format");
    }

    /**
     * Returns the default extension of the file format supported.
     *
     * @return Returns a string (case insensitive) indicating default extension
     * supported.
     */
    @Override
    public String getDefaultExtension() {
        return (String) supportedExtensions.keys().nextElement();
    }

    /**
     * This method returns an list of all the extensions suppoted by this
     * implementation.
     *
     * @return Returns an Iterator of string objects (case insensitive),
     * indicating the file extensions supported.
     */
    @Override
    public Iterator<String> getAllSupportedExtensions() {
        return supportedExtensions.keySet().iterator();
    }

    /** 
     * This method returns the type or class of file formats being read.
     * For e.g. if the implementation class reads XYZ files then this method
     * may return a string like 'XYZ File format'.
     *
     * @return A string indicating the type or class of molecule file formats,
     * whose reading is supported. The string returned should be treated as
     * case insensitive.
     */
    @Override
    public String getType() {
        return (String) supportedExtensions.elements().nextElement();
    }

    /**
     * This method returns a description of the file format supported.
     *
     * @return A string indicating description of the file format.
     */
    @Override
    public String getTypeDescription() {
        return "A simple, standard SDF file format";
    }    

    /**
     * This method reads the molecule from a buffered reader. This method should
     * ensure that the reader is never closed by this method.
     *
     * @param reader instance of reader from where to read the molecule data.
     * @param readDeep read deep into the file, including additional information
     * @return An instance of raw Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public Molecule readMolecule(BufferedReader reader, boolean readDeep) throws IOException {
        Molecule molecule = null;

        // get instance of a Molecule implementation
        try {
            molecule = (Molecule) ((Utility.getDefaultImplFor(Molecule.class)).
                    newInstance());
        } catch (ClassNotFoundException cnfe) {
            throw new IOException("Exception while loading class: " + cnfe);
        } catch (InstantiationException ie) {
            throw new IOException("Exception while initing class: " + ie);
        } catch (IllegalAccessException iae) {
            throw new IOException("Access denied while initing class: " + iae);
        } // end of try .. catch block

        // then start reading the file carefully and construct the 
        // Molecule object

        // the first line is the title
        String line = reader.readLine();
        int noOfAtoms, noOfBonds;

        molecule.setTitle(line);

        // skip next two lines        
        reader.readLine();
        reader.readLine();

        // next line contains information on number of atoms
        line = reader.readLine();

        // to read the number of atoms, we need to split the line into words
        // and then read the format in which the number of atoms are stored
        StringTokenizer stk = new StringTokenizer(line);
        String firstWord = stk.nextToken();
        String otherWords = "";

        for (int i = 0; i < 9; i++) {
            otherWords = stk.nextToken();
        }

        noOfAtoms = Integer.parseInt(line.substring(0, 3).trim());
        noOfBonds = Integer.parseInt(line.substring(3, 6).trim());

        // instantiate a tokenizer
        ScientificStreamTokenizer tokenizer =
                new ScientificStreamTokenizer(reader);

        // and now we are in a position to read all the atoms!
        String symbol;
        double x, y, z;

        try {
            for (int i = 0; i < noOfAtoms; i++) {
                // a set of four tokens constitute the atom                

                //  a) the x coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " +
                            tokenizer);
                } else {
                    x = tokenizer.nval;
                } // end if

                //  b) the y coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " +
                            tokenizer);
                } else {
                    y = tokenizer.nval;
                } // end if

                //  c) the z coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " +
                            tokenizer);
                } else {
                    z = tokenizer.nval;
                } // end if

                //  d) the symbol                
                if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new IOException("Could not understand format : " +
                            tokenizer);
                } else {
                    symbol = tokenizer.sval.toLowerCase();
                } // end if

                // and now we can safely add this atom to our list
                molecule.addAtom(symbol, x, y, z, i);

                // skip to next line
                reader.readLine();
                tokenizer = new ScientificStreamTokenizer(reader);
            } // end for

            for (int i = 0; i < noOfBonds; i++) {
                // the first three tokens describe the bond (for the time
                // being, ignore remaining columns)
                int atomIndex1, atomIndex2, bondType;

                //  a) the first atom index
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " +
                            tokenizer);
                } else {
                    atomIndex1 = (int) tokenizer.nval - 1;
                } // end if

                //  b) the second atom index
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " +
                            tokenizer);
                } else {
                    atomIndex2 = (int) tokenizer.nval - 1;
                } // end if

                //  c) the bondType
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " +
                            tokenizer);
                } else {
                    bondType = (int) tokenizer.nval;
                } // end if

                // and now we can safely add this atom to our list
                switch (bondType) {
                    // SDF only specifies single, double, triple and aromatic bonds
                    case 1:
                        molecule.setBondType(atomIndex1, atomIndex2,
                                BondType.SINGLE_BOND);
                        molecule.incrementNumberOfSingleBonds();
                        break;
                    case 2:
                        molecule.setBondType(atomIndex1, atomIndex2,
                                BondType.DOUBLE_BOND);
                        molecule.incrementNumberOfMultipleBonds();
                        break;
                    case 3:
                        molecule.setBondType(atomIndex1, atomIndex2,
                                BondType.TRIPLE_BOND);
                        molecule.incrementNumberOfMultipleBonds();
                        break;
                    case 4:
                        molecule.setBondType(atomIndex1, atomIndex2,
                                BondType.AROMATIC_BOND);
                        molecule.incrementNumberOfMultipleBonds();
                        break;
                    default:
                        System.err.println("Unknown bond order: " + bondType);
                }

                // skip to next line
                reader.readLine();
                tokenizer = new ScientificStreamTokenizer(reader);
            } // end for
            if (molecule.getAdditionalInformation() == null) {
                molecule.setAdditionalInformation(
                        new Molecule.AdditionalInformation());
            }
            molecule.getAdditionalInformation().setReadConnectivity(true);

            // now keep reading till end of record marker - "$$$$"
            while (true) {
                if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
                    if (tokenizer.sval.equals("$$$$")) {
                        break;
                    } else if (tokenizer.sval.equals(
                            "<PUBCHEM_MMFF94_PARTIAL_CHARGES>")) {
                        // extract partial charges for each atom
                        for (int i = 0; i < noOfAtoms; i++) {
                            // each line contains the charge for one atom
                            if (tokenizer.nextToken() !=
                                    StreamTokenizer.TT_NUMBER) {
                                throw new IOException("Could not understand format : " +
                                        tokenizer);
                            } else {
                                molecule.getAtom(i).setCharge(tokenizer.nval);
                            } // end if
                        }
                    }
                } // end if
            } // end while
        } catch (Exception e) {
            throw new IOException("Error reading file : " + tokenizer +
                    "\n Exception is : " + e.toString());
        } // end of try .. catch block                

        return molecule;
    }
}
