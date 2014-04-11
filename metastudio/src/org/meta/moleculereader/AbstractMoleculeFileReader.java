/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.moleculereader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.meta.molecule.CommonUserDefinedMolecularPropertyNames;
import org.meta.molecule.Molecule;

/**
 * Abstract MoleculeFileReader class 
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractMoleculeFileReader implements MoleculeFileReader {

    /** Creates a new instance of class AbstractMoleculeFileReader */
    public AbstractMoleculeFileReader() {}

    /**
     * Reads the molecule file and returns an raw Molecule object.
     *
     * @param fileName The complete path of the file to be read.
     * @return raw instance of the Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public Molecule readMoleculeFile(String fileName) throws IOException {
        FileInputStream fis  = new FileInputStream(fileName);
        Molecule theMolecule = readMolecule(fis);
        fis.close();

        return theMolecule;
    }

    /**
     * Reads the molecule file and returns an raw Molecule object.
     *
     * @param file An instance of the file object specifing the
     * file to be read.
     * @return raw instance of the Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public Molecule readMoleculeFile(File file) throws IOException {
        return readMoleculeFile(file.getAbsolutePath());
    }

    /**
     * This method reads the molecule from an input stream.
     * The resposibility of closing / freeing the input stream lies on the
     * caller.
     *
     * @param inputStream General instance of input stream from where to
     * read the molecule data.
     * @return An instance of raw Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public Molecule readMolecule(InputStream inputStream) throws IOException {
        // convert to a buffered reader
        BufferedReader br = new BufferedReader(
                                new InputStreamReader(inputStream));

        return readMolecule(br);
    }

    /**
     * This method reads the molecule from a buffered reader. This method should
     * ensure that the reader is never closed by this method. By default do not
     * read deep into the file.
     * 
     * @param reader instance of reader from where to read the molecule data.
     * @return An instance of raw Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public Molecule readMolecule(BufferedReader reader) throws IOException {
        Molecule molecule = readMolecule(reader, false);

        // put a mark that "deep reading" was not done
        molecule.setCommonUserDefinedProperty(
                CommonUserDefinedMolecularPropertyNames.READ_DEEP, false);

        return molecule;
    }
}
