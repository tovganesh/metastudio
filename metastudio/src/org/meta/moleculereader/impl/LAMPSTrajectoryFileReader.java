/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.moleculereader.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import org.meta.common.Utility;
import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.moleculereader.AbstractMoleculeFileReader;
import org.meta.moleculereader.impl.GAMESSGaussianFileReader.ExternalFileType;

/**
 * This is a very simple LAMPS trajectory file reader. It assumes un-wrapped
 * scaled coordinates in a plain ASCII text file.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LAMPSTrajectoryFileReader extends AbstractMoleculeFileReader {

    private Hashtable<String, String> supportedExtensions;
    
    private GAMESSGaussianFileReader.ExternalFileType previousType;
    
    /** Creates a new instance of GAMESSGaussianFileReader */
    public LAMPSTrajectoryFileReader() {
        supportedExtensions = new Hashtable<String, String>(6);
        supportedExtensions.put("lammpstrj", "LAMPS trajectory file");        
        
        previousType = ExternalFileType.UNKNOWN_FORMAT;
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

    @Override
    public String getTypeDescription() {
        return "LAMPS trajectory file reader";
    }

    @Override
    public String getDefaultExtension() {
        return (String) supportedExtensions.keys().nextElement();
    }
    
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
        
        String line = reader.readLine();
        
        if (line == null || line == "") 
            throw new IOException("Nothing to read.");
        
        if (!line.startsWith("ITEM: TIMESTEP")) 
            throw new IOException("Could not understand format.");
        
        line = reader.readLine();
        line = reader.readLine();
        
        if (!line.startsWith("ITEM: NUMBER OF ATOMS"))
            throw new IOException("Could not understand format.");
        
        int nAtoms = Integer.parseInt(reader.readLine().trim());
        
        line = reader.readLine();
        
        if (!line.startsWith("ITEM: BOX BOUNDS"))
            throw new IOException("Could not understand format.");
        
        line = reader.readLine();
        line = reader.readLine();
        line = reader.readLine();
        
        line = reader.readLine();
        
        // TODO: need to read the format here
        if (!line.startsWith("ITEM: ATOMS"))
            throw new IOException("Could not understand format.");
        
        // assuming: id type xu yu zu fx fy fz 
        for(int i=0; i<nAtoms; i++) {
            line = reader.readLine().trim();
            
            String [] words = line.split("\\s+");
            
            molecule.addAtom(words[1].trim(), Double.parseDouble(words[2].trim()),
                                              Double.parseDouble(words[3].trim()),
                                              Double.parseDouble(words[4].trim()),
                                              Integer.parseInt(words[0].trim())-1);
        } // end for
        
        return molecule;
    }        
}
