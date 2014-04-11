/*
 * XYZMoleculeFileReader.java
 *
 * Created on September 7, 2003, 6:27 PM
 */

package org.meta.moleculereader.impl;

import java.io.*;
import java.util.*;
import org.meta.common.Utility;
import org.meta.molecule.Molecule;
import org.meta.moleculereader.AbstractMoleculeFileReader;

/**
 * A simple implementation of MoleculeFileReader interface for reading 
 * standard XYZ format files.
 * Though specified *standard*, this class also reads files in the following
 * forms:
 * <PRE>
 * a)  title
 *     no of atoms
 *     symbol x y z
 *     ...
 * b)  no of atoms
 *     title
 *     symbol x y z
 *     ... 
 * </PRE>
 *
 * This implementation doesn't read XYZ files having multiple molecule
 * definitions stored in it. In cases where multiple molecules are defined
 * only the first such molecule is read (as it occurs in the file).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class XYZMoleculeFileReader extends AbstractMoleculeFileReader {
    
    private Hashtable<String, String> supportedExtensions;
    
    /** Creates a new instance of XYZMoleculeFileReader */
    public XYZMoleculeFileReader() {
        supportedExtensions = new Hashtable<String, String>(2);
        supportedExtensions.put("xyz", "XYZ file format");
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
        return "A simple, standard XYZ file format";
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
        Molecule molecule;
        
        // get instance of a Molecule implementation
        try {
            molecule = (Molecule) ((Utility.getDefaultImplFor(Molecule.class))
                                           .newInstance());
        } catch(ClassNotFoundException cnfe) {
            throw new IOException("Exception while loading class: " + cnfe);
        } catch (InstantiationException ie) {
            throw new IOException("Exception while initing class: " + ie);
        } catch (IllegalAccessException iae) {
            throw new IOException("Access denied while initing class: " + iae);
        } // end of try .. catch block
        
        // then start reading the file carefully and construct the 
        // Molecule object
        
        // the first two tokens have to be read carefully
        // they determine how to read the rest of the file        
        String line = reader.readLine();
        int noOfAtoms;

        // first check, it that is number of atoms?
        try {
            noOfAtoms = Integer.parseInt(line.trim());
            // if we reached here, the next line is the title line
            molecule.setTitle(reader.readLine());
        } catch (Exception e) {
            // if we came here, the order is reversed!
            molecule.setTitle(line);
            
            // and the next line should be number of atoms
            try {
                line = reader.readLine();
                noOfAtoms = Integer.parseInt(line.trim());
            } catch(Exception e1) {
                throw new IOException("Could not understand format : "
                          + "Line number 2, token : " + line);
            } // end of try .. catch block
        } // end try .. catch block
        
        // and now we are in a position to read all the atoms!
        String symbol;
        double x, y, z;
        String [] words;

        try {
            for(int i=0; i<noOfAtoms; i++) {
                words = reader.readLine().trim().split("\\s+");

                symbol = words[0];

                x = Double.parseDouble(words[1]);
                y = Double.parseDouble(words[2]);
                z = Double.parseDouble(words[3]);

                // and now we can safely add this atom to our list
                molecule.addAtom(symbol, x, y, z, i);
            } // end for
        } catch (Exception e) {
            e.printStackTrace();

            throw new IOException("Error reading file." +
                    " Exception is : " + e.toString());
        } // end of try .. catch block
        
        // return the "raw" molecule
        return molecule;
    }
    
} // end of class XYZMoleculeFileReader
