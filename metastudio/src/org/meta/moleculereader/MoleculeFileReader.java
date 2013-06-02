/*
 * MoleculeFileReader.java
 *
 * Created on April 4, 2003, 6:37 AM
 */

package org.meta.moleculereader;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;

import java.util.Iterator;

import org.meta.molecule.Molecule;

/**
 * This interface defines how an implementation that reads various molecule
 * file formats should behave. We use an interface driven approach so as to
 * reduce the amount of memory any unwanted class may take. Refer to class
 * MoleculeFileReaderFactory for information on how this is acheived.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MoleculeFileReader {
    
    /** 
     * This method returns the type or class of file formats being read.
     * For e.g. if the implementation class reads XYZ files then this method
     * may return a string like 'XYZ File format'.
     *
     * @return A string indicating the type or class of molecule file formats,
     * whose reading is supported. The string returned should be treated as
     * case insensitive.
     */    
    public String getType();
    
    /**
     * This method returns a description of the file format supported.
     *
     * @return A string indicating description of the file format.
     */    
    public String getTypeDescription();
    
    /**
     * Returns the default extension of the file format supported.
     *
     * @return Returns a string (case insensitive) indicating default extension
     * supported.
     */    
    public String getDefaultExtension();
    
    /**
     * This method returns an list of all the extensions suppoted by this
     * implementation.
     *
     * @return Returns an Iterator of string objects (case insensitive),
     * indicating the file extensions supported.
     */    
    public Iterator getAllSupportedExtensions();
    
    /**
     * Reads the molecule file and returns an raw Molecule object.
     *
     * @param fileName The complete path of the file to be read.
     * @return raw instance of the Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */    
    public Molecule readMoleculeFile(String fileName) throws IOException;
    
    /**
     * Reads the molecule file and returns an raw Molecule object.
     *
     * @param file An instance of the file object specifing the 
     * file to be read.
     * @return raw instance of the Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */    
    public Molecule readMoleculeFile(File file) throws IOException;
    
    /**
     * This method reads the molecule from an input stream.
     *
     * @param inputStream General instance of input stream from where to 
     * read the molecule data.
     * @return An instance of raw Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */    
    public Molecule readMolecule(InputStream inputStream) throws IOException;
    
    /**
     * This method reads the molecule from a buffered reader. This method should
     * ensure that the reader is never closed by this method.
     *
     * @param reader instance of reader from where to read the molecule data.
     * @return An instance of raw Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */    
    public Molecule readMolecule(BufferedReader reader) throws IOException;

    /**
     * This method reads the molecule from a buffered reader. This method should
     * ensure that the reader is never closed by this method.
     *
     * @param reader instance of reader from where to read the molecule data.
     * @param readDeep read deep into the file, including additional information
     * @return An instance of raw Molecule object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    public Molecule readMolecule(BufferedReader reader, boolean readDeep) throws IOException;
} // end of interface MoleculeFileReader
