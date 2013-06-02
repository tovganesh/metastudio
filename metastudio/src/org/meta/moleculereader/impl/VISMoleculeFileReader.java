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
        
        // instantiate a tokenizer
        ScientificStreamTokenizer tokenizer = 
                                  new ScientificStreamTokenizer(reader);
        
        int tokenID, noOfAtoms;
        boolean convertToAngstrom; // flag to check if conversion to angstrom
                                   // unit is necessary
        
        // the molecule title is not defined in VIS file - so give it
        // an arbitary name
        molecule.setTitle("Untitled");
            
        // the first two tokens have to be read carefully
        // they determine how to read the rest of the file
        tokenID = tokenizer.nextToken();
        
        convertToAngstrom = false;
        
        if (tokenID == StreamTokenizer.TT_WORD) {
            // the first token should be either of the following:
            // {$angstrom | $au | $molecule | $atom}
            if (tokenizer.sval.compareToIgnoreCase("$angstrom") == 0) {
                convertToAngstrom = false;
                
                // the next token has to be $molecule or $atom in this case
                tokenID = tokenizer.nextToken();
                
                if (tokenID != StreamTokenizer.TT_WORD) {
                    throw new IOException("Could not understand format : "
                    + tokenizer);
                } else {
                    if ((tokenizer.sval.compareToIgnoreCase("$molecule") != 0)
                       && (tokenizer.sval.compareToIgnoreCase("$atom") != 0)) {
                        throw new IOException("Could not understand format : "
                                               + tokenizer);
                    } // end if
                } // end if
            } else if (tokenizer.sval.compareToIgnoreCase("$au") == 0) {                   
                convertToAngstrom = true; // ;)  
                
                // the next token has to be $molecule or $atom in this case
                tokenID = tokenizer.nextToken();
                
                if (tokenID != StreamTokenizer.TT_WORD) {
                    throw new IOException("Could not understand format : "
                    + tokenizer);
                } else {
                    if ((tokenizer.sval.compareToIgnoreCase("$molecule") != 0)
                       && (tokenizer.sval.compareToIgnoreCase("$atom") != 0)) {
                        throw new IOException("Could not understand format : "
                                               + tokenizer);
                    } // end if
                } // end if
            } else if ((tokenizer.sval.compareToIgnoreCase("$molecule") == 0)
                        || (tokenizer.sval.compareToIgnoreCase("$atom") == 0)) {
                convertToAngstrom = true; // ;)                         
            } else {               
                throw new IOException("Could not understand format : " 
                                      + tokenizer);
            } // end if                                    
        } else {
            // error in format!
            throw new IOException("Could not understand format : " + tokenizer);
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
        int atomIndex = 0;
        
        try {
            while(true) {                
                // a set of four tokens constitute the atom
                //  a) the symbol                
                if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    if (tokenizer.sval.compareToIgnoreCase("$end") == 0) {
                        // we have reached end of file!
                        break;
                    } // end if
                    
                    symbol = tokenizer.sval;
                } // end if
                
                //  b) the x coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    x = tokenizer.nval;
                } // end if
                
                //  c) the y coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    y = tokenizer.nval;
                } // end if
                
                //  d) the z coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    z = tokenizer.nval;
                } // end if
                
                // and now we can safely add this atom to our list
                if (convertToAngstrom) {
                    molecule.addAtom(symbol, x * Utility.AU_TO_ANGSTROM_FACTOR, 
                                             y * Utility.AU_TO_ANGSTROM_FACTOR, 
                                             z * Utility.AU_TO_ANGSTROM_FACTOR,
                                             atomIndex);                    
                } else {
                    molecule.addAtom(symbol, x, y, z, atomIndex);
                } // end if 
                
                atomIndex++;
            } // end while
        } catch (Exception e) {
            throw new IOException("Error reading file : " + tokenizer
                                  + "\n Exception is : " + e.toString());
        } // end of try .. catch block
        
        // return the "raw" molecule
        return molecule;
    }
    
} // end of class VISMoleculeFileReader
