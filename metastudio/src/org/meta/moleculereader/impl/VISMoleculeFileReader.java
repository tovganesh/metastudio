/*
 * VISMoleculeFileReader.java
 *
 * Created on October 12, 2003, 7:20 PM
 */

package org.meta.moleculereader.impl;

import java.io.*;
import java.util.*;
import org.meta.common.ScientificStreamTokenizer;
import org.meta.common.Utility;
import org.meta.molecule.Molecule;
import org.meta.moleculereader.AbstractMoleculeFileReader;

/**
 * A simple implementation of MoleculeFileReader interface for reading 
 * standard VIS format files as defined by UNIVIS 2K package:
 * (http://chem.unipune.ernet.in/univis/univis-2k.html)
 * An simple VIS file will contain:
 * <PRE>
 * {$angstrom | $au | $molecule | $atom}
 * [$molecule | $atom]
 * symbol x y z
 * ...
 * {$end}
 * </PRE>
 * 
 * By default the file is considered to be in A.U. i.e. if no $angstrom
 * is specified. In any case the coordinates are stored as angstrom units
 * in the Molecule object.
 *
 * This implementation doesn't read VIS files having multiple molecule
 * definitions stored in it. In cases where multiple molecules are defined
 * only the first such molecule is read (as it occurs in the file).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class VISMoleculeFileReader extends AbstractMoleculeFileReader {
    
    private Hashtable<String, String> supportedExtensions;
    
    /** Creates a new instance of VISMoleculeFileReader */
    public VISMoleculeFileReader() {
        supportedExtensions = new Hashtable<String, String>(2);
        supportedExtensions.put("vis", "UNIVIS-2K file format");
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
        return "UNIVIS-2K file format";
    }       
    
    private static boolean convertToAngstromState = false;
    private static BufferedReader readerState = null;
    
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
        
        boolean convertToAngstrom; // flag to check if conversion to angstrom
                                   // unit is necessary
        
        // the molecule title is not defined in VIS file - so give it
        // an arbitary name
        molecule.setTitle("Untitled");
        
        String line = reader.readLine().trim();
        
        if (line.compareToIgnoreCase("$angstrom") == 0) {
            convertToAngstrom = false;
            line = reader.readLine().trim();
            if (line.compareToIgnoreCase("$molecule") != 0
                && line.compareToIgnoreCase("$atom") != 0) {
                throw new IOException("Could not understand format : " + line);
            } // end if
        } else if (line.compareToIgnoreCase("$au") == 0) {
            convertToAngstrom = true; // ;) 
            line = reader.readLine().trim();
            if (line.compareToIgnoreCase("$molecule") != 0
                && line.compareToIgnoreCase("$atom") != 0) {
                throw new IOException("Could not understand format : " + line);
            } // end if
        } else if (line.compareToIgnoreCase("$molecule") == 0
                   || line.compareToIgnoreCase("$atom") == 0) {
            convertToAngstrom = true; // ;)
        } else {
            throw new IOException("Could not understand format : " + line);
        } // end if

        // save/retrive the reader state
        if ((readerState != null) && (readerState == reader)) {
            convertToAngstrom = convertToAngstromState;
        } // end if
        if (reader != readerState) { 
            readerState = reader;
            convertToAngstromState = convertToAngstrom;
        } // end if
        
        // and now we are in a position to read all the atoms!
        String symbol;
        double x, y, z;
        double xi, yj, zk;
        String [] words;
        int atomIndex = 0;
        
        try {
            while(true) {   
                line = reader.readLine().trim();
                if (line == null) break;
                if (line.compareTo("") == 0) break;
                if (line.compareToIgnoreCase("$end") == 0) break;
                
                words = line.split("\\s+");

                symbol = words[0];

                x = Double.parseDouble(words[1]);
                y = Double.parseDouble(words[2]);
                z = Double.parseDouble(words[3]);

                if (words.length >= 7) {
                    System.out.println("vec detected" + line);
                    xi = Double.parseDouble(words[4]);
                    yj = Double.parseDouble(words[5]);
                    zk = Double.parseDouble(words[6]);

                    // and now we can safely add this atom to our list  
                    if (convertToAngstrom) {
                        molecule.addAtom(symbol, x * Utility.AU_TO_ANGSTROM_FACTOR, 
                                                 y * Utility.AU_TO_ANGSTROM_FACTOR, 
                                                 z * Utility.AU_TO_ANGSTROM_FACTOR,
                                                 xi * Utility.AU_TO_ANGSTROM_FACTOR, 
                                                 yj * Utility.AU_TO_ANGSTROM_FACTOR,
                                                 zk * Utility.AU_TO_ANGSTROM_FACTOR,
                                                 atomIndex);
                        atomIndex++;
                    } else {
                        molecule.addAtom(symbol, x, y, z, xi, yj, zk, atomIndex);
                        atomIndex++;
                    } // end if
                } else {
                    // and now we can safely add this atom to our list  
                    if (convertToAngstrom) {
                        molecule.addAtom(symbol, x * Utility.AU_TO_ANGSTROM_FACTOR, 
                                                 y * Utility.AU_TO_ANGSTROM_FACTOR, 
                                                 z * Utility.AU_TO_ANGSTROM_FACTOR,
                                                 atomIndex);
                        atomIndex++;
                    } else {
                        molecule.addAtom(symbol, x, y, z, atomIndex);
                        atomIndex++;
                    } // end if
                } // end if
            } // end while
        } catch (Exception e) {
            throw new IOException("Error reading file : "
                                  + "\n Exception is : " + e.toString());
        } // end of try .. catch block
        
        // return the "raw" molecule
        return molecule;
    }
    
} // end of class VISMoleculeFileReader
