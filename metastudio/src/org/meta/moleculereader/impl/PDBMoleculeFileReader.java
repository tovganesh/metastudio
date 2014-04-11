/*
 * PDBMoleculeFileReader.java
 *
 * Created on June 11, 2004, 6:40 AM
 */

package org.meta.moleculereader.impl;

import java.io.*;
import java.util.*;
import org.meta.common.Utility;
import org.meta.molecule.Molecule;
import org.meta.moleculereader.AbstractMoleculeFileReader;

/**
 * Reads the standard PDB file format. For details check: <br>
 * <A HREF="http://pdb.org"> PDB Site and Repository </A>
 * 
 * This is a dead simple reader, just extracts symbol, x, y, z from the PDB file.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PDBMoleculeFileReader extends AbstractMoleculeFileReader {
    
    private Hashtable<String, String> supportedExtensions;
    
    private int currentLineNumber;
    
    protected static final int X_START = 31;
    protected static final int X_END   = 38;
    
    protected static final int Y_START = 39;
    protected static final int Y_END   = 46;
    
    protected static final int Z_START = 47;
    protected static final int Z_END   = 54;
    
    // v 2.2 PDB spec earlier didn't have specific field for
    // storing atomic symbols
    protected static final int SYMBOL_START_V1 = 12;
    protected static final int SYMBOL_END_V1   = 16;
    // v 2.2 PDB spec onwards this field is 76-78
    protected static final int SYMBOL_START_V2 = 76;
    protected static final int SYMBOL_END_V2   = 78;
    
    // Some relevent PDB keywords
    protected static final String ATOM    = "ATOM";
    protected static final String HETATM  = "HETATM";
    protected static final String REMARK  = "REMARK";
    protected static final String END_PDB = "END";
    
    /** Creates a new instance of PDBMoleculeFileReader */
    public PDBMoleculeFileReader() {
        supportedExtensions = new Hashtable<String, String>(2);
        supportedExtensions.put("pdb", "PDB file format");
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
     * This method returns an list of all the extensions supported by this
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
        return "A standard PDB file format";
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
        
        molecule.setTitle("Untitled");
        
        String line, symbol;
        int atomIndex = 0;
        currentLineNumber = 0;
        
        try {
            while(true) {
                line = reader.readLine();
                currentLineNumber++;
                
                if (line == null) break; // we are finished reading
                
                // found the "END" keyword
                if (line.indexOf(END_PDB) == 0) break;               
                
                if ((line.indexOf(ATOM) == 0) || (line.indexOf(HETATM) == 0)) {
                    try {
                        symbol = line.trim().substring(SYMBOL_START_V2, SYMBOL_END_V2);
                        
                        if (symbol.trim().isEmpty()) {
                            symbol = line.substring(SYMBOL_START_V1, SYMBOL_END_V1);
                            symbol = extractSymbol(symbol);
                        } else {
                            char [] sch = symbol.trim().toCharArray();
                            
                            for (int cidx = 0; cidx < sch.length; cidx++) {
                                if (!Character.isLetter(sch[cidx])) {
                                    symbol = line.substring(SYMBOL_START_V1, SYMBOL_END_V1);                                    
                                    symbol = extractSymbol(symbol);
                                    break;
                                } // end if
                            } // end for
                        } // end if
                    } catch (Exception ex) {
                        symbol = line.substring(SYMBOL_START_V1, SYMBOL_END_V1);
                        symbol = extractSymbol(symbol);                        
                    } // end if
                                      
                    molecule.addAtom(symbol.trim(),
                           Double.parseDouble(line.substring(X_START, X_END).trim()),
                           Double.parseDouble(line.substring(Y_START, Y_END).trim()),
                           Double.parseDouble(line.substring(Z_START, Z_END).trim()),
                           atomIndex);
                    atomIndex++;
                } else {
                    continue;
                } // end if
            } // end of end less while
        } catch (Exception e){
            e.printStackTrace();
            throw new IOException("Error reading file at line : " 
                                  + currentLineNumber
                                  + "\n Exception is : " + e.toString());
        } // end of try .. catch block
        return molecule;
    }
        
    private String extractSymbol(String symbol) {
        
        if (symbol.charAt(0) == ' ') {
            if (symbol.charAt(1) == ' ') {
                symbol = symbol.charAt(2) + "";
            } else {
                symbol = symbol.charAt(1) + "";
            } // end if
        } else {
            symbol = symbol.charAt(0) + "" + symbol.charAt(1);
        } // end if
        
        return symbol;
    }
} // end of class PDBMoleculeFileReader
