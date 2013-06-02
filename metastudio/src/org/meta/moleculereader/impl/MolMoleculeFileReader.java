/*
 * MolMoleculeFileReader.java
 *
 * Created on November 5, 2005, 10:37 PM
 *
 */

package org.meta.moleculereader.impl;

import java.io.*;
import java.util.*;
import org.meta.common.ScientificStreamTokenizer;
import org.meta.common.Utility;
import org.meta.molecule.Molecule;
import org.meta.moleculereader.AbstractMoleculeFileReader;

/**
 * A simple implementation of MoleculeFileReader interface for reading Sybyl's
 * MOL file format.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MolMoleculeFileReader extends AbstractMoleculeFileReader {
    
    private Hashtable<String, String> supportedExtensions;
    
    /** Creates a new instance of MolMoleculeFileReader */
    public MolMoleculeFileReader() {
        supportedExtensions = new Hashtable<String, String>(1);
        supportedExtensions.put("mol", "MOL file format");
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
        return "A simple, standard MOL file format";
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
        
        // the first line is the title
        String line = reader.readLine();
        int noOfAtoms;
                
        molecule.setTitle(line);
            
        // skip next two lines        
        reader.readLine(); reader.readLine();
        
        // next line contains information on number of atoms
        line = reader.readLine();
        
        // to read the number of atoms
        noOfAtoms = Integer.parseInt(line.trim().split("\\s+")[0].trim());
        
        // instantiate a tokenizer
        ScientificStreamTokenizer tokenizer = 
                                  new ScientificStreamTokenizer(reader);
                        
        // and now we are in a position to read all the atoms!
        String symbol;
        double x, y, z;
        
        try {
            for(int i=0; i<noOfAtoms; i++) {                
                // a set of four tokens constitute the atom                
                
                //  a) the x coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    x = tokenizer.nval;
                } // end if
                
                //  b) the y coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    y = tokenizer.nval;
                } // end if
                
                //  c) the z coordinate               
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    z = tokenizer.nval;
                } // end if
                
                //  d) the symbol                
                if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new IOException("Could not understand format : " 
                                           + tokenizer);
                } else {
                    symbol = tokenizer.sval.toLowerCase();
                } // end if
                
                // and now we can safely add this atom to our list
                molecule.addAtom(symbol, x, y, z, i);
                
                // skip to next line
                reader.readLine();
                tokenizer = new ScientificStreamTokenizer(reader);
            } // end for
        } catch (Exception e) {
            throw new IOException("Error reading file : " + tokenizer
                                  + "\n Exception is : " + e.toString());
        } // end of try .. catch block
        
        // return the "raw" molecule
        return molecule;
    }
} // end of class MolMoleculeFileReader
