/*
 * GAMESSGaussianFileReader.java
 *
 * Created on April 24, 2006, 10:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.moleculereader.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.meta.common.ScientificStreamTokenizer;
import org.meta.common.Utility;
import org.meta.config.impl.AtomInfo;
import org.meta.math.Vector3D;
import org.meta.molecule.CommonUserDefinedMolecularPropertyNames;
import org.meta.molecule.Molecule;
import org.meta.molecule.property.vibrational.FrequencyItem;
import org.meta.molecule.property.vibrational.Intensity;
import org.meta.molecule.property.vibrational.IntensityType;
import org.meta.molecule.property.vibrational.MolecularVibrationalProperty;
import org.meta.moleculereader.AbstractMoleculeFileReader;

/**
 * Read GAMESS/Gaussian input/output file.
 * This is pretty complicated reader, in that it supports variety of GAMESS and
 * Gaussian file formats. Some of these may contain volumetric data, like the
 * Gaussian cube file, and these will not be completely read by the routines 
 * here. It is the responsibility of the programmer to identify such files and
 * appropriately use other APIs of MeTA Studio to extract the remaining 
 * information. Currently input files in Cartesian coordinates are the only
 * one accepted and correctly read. 
 * Certain additional information like Frequency is not currently read by this
 * class but may be supported in future.
 *
 * Note that the codes in this reader is fragile, and will break down
 * with slightest change in the file formats. Alternatively it is best to write
 * these readers as a script, which can be modified easily to suit your needs.
 * A few such example scripts are provided in 
 * <code>[meta-studio-install-dir]/scripts</code> directory.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GAMESSGaussianFileReader extends AbstractMoleculeFileReader {
    
    private Hashtable<String, String> supportedExtensions;
    
    private ExternalFileType previousType;
    
    /** Creates a new instance of GAMESSGaussianFileReader */
    public GAMESSGaussianFileReader() {
        supportedExtensions = new Hashtable<String, String>(6);
        supportedExtensions.put("inp", "GAMESS input file");
        supportedExtensions.put("out", "GAMESS output file");
        supportedExtensions.put("com", "Gaussian input file");
        supportedExtensions.put("gjf", "Gaussian input file");
        supportedExtensions.put("log", "Gaussian output file");
        supportedExtensions.put("cub", "Gaussian cube file");
        supportedExtensions.put("cube", "Gaussian cube file");
        
        previousType = ExternalFileType.UNKNOWN_FORMAT;
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
        return "GAMESS/Gaussian input/output file reader";
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
    public Molecule readMolecule(BufferedReader reader, boolean readDeep)
                                throws IOException {
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

        // first identify type, else use the previously identified type
        previousType = identifyProbableType(reader);

        if (previousType == ExternalFileType.UNKNOWN_FORMAT) {
            throw new IOException("Couldn't identify filetype!");
        } // end if

        // read and convert into Molecule object
        if (previousType == ExternalFileType.GAMESS_INPUT) {
            readGAMESSInput(reader, molecule, readDeep);
        } else if (previousType == ExternalFileType.GAMESS_RUN) {
            readGAMESSRun(reader, molecule, readDeep);
        } else if (previousType == ExternalFileType.GAUSSIAN_INPUT) {
            readGaussianInput(reader, molecule, readDeep);
        } else if (previousType == ExternalFileType.GAUSSIAN_RUN) {
            readGaussianRun(reader, molecule, readDeep);
        } else if (previousType == ExternalFileType.GAUSSIAN_CUBE) {
            readGaussianCube(reader, molecule, readDeep);
        } // end if

        // put a mark that for deep reading
        molecule.setCommonUserDefinedProperty(
                CommonUserDefinedMolecularPropertyNames.READ_DEEP, readDeep);
        
        // return the "raw" molecule
        return molecule;
    }

    /**
     * reader Gaussian cube file
     */
     private void readGaussianCube(BufferedReader reader, Molecule molecule,
                                   boolean readDeep) throws IOException {
        // skip first two lines
        reader.readLine(); reader.readLine();                
        
        // read number of atoms
        String line = reader.readLine();
        
        StringTokenizer tok = new StringTokenizer(line);
        int noOfAtoms = Math.abs(Integer.parseInt(tok.nextToken()));
        
        // skip next THREE lines
        for(int i=0; i<3; i++, reader.readLine());
        
        String symbol;
        double x, y, z, charge;
        int atomIndex = 0;
        
        // not read coordinate till the we again see a $
        // instantiate a tokenizer
        ScientificStreamTokenizer tokenizer = 
                                  new ScientificStreamTokenizer(reader);
        AtomInfo ai = AtomInfo.getInstance();
        boolean convertToAnstrom = true;
        
        for(atomIndex=0; atomIndex<noOfAtoms; atomIndex++) {                        
            // a set of four tokens constitute the atom
            //  a) the symbol                
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {                
                symbol = ai.getSymbol((int) tokenizer.nval);            
            } // end if           
            
            //  b) the charge
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                charge = tokenizer.nval;
            } // end if
            
            //  c) the x coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                x = tokenizer.nval;
            } // end if

            //  d) the y coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                y = tokenizer.nval;
            } // end if

            //  e) the z coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                z = tokenizer.nval;
            } // end if

            // and now we can safely add this atom to our list            
            if (convertToAnstrom) {
                molecule.addAtom(symbol, charge, 
                                     x * Utility.AU_TO_ANGSTROM_FACTOR, 
                                     y * Utility.AU_TO_ANGSTROM_FACTOR, 
                                     z * Utility.AU_TO_ANGSTROM_FACTOR,
                                     atomIndex);                 
            } else {
                molecule.addAtom(symbol, charge, x, y, z, atomIndex);
            } // end if                   
        } // end for

        Molecule.AdditionalInformation adi 
                                       = new Molecule.AdditionalInformation();
        adi.setVolumetricDataAvailable(true);
        molecule.setAdditionalInformation(adi);
        molecule.setAdditionalInformationAvailable(true);
     }
     
    /**
     * Read the Gaussian "run" file
     */
    private void readGaussianRun(BufferedReader reader, Molecule molecule,
                                 boolean readDeep) throws IOException {
        // keep reading the file till "Coordinates (:" is found
        // TODO: modify later to read and fill MetaInformation
        String line = "";
        while (true) {
            if ((line = reader.readLine()).indexOf("Coordinates (") >= 0) break;
        } // end if
        
        // check if there is Atom Type specification
        line = reader.readLine();
        boolean hasAtomType = line.contains("Type");
        // skip next line
        reader.readLine();
        
        String symbol;
        double x, y, z;
        int atomIndex = 0;
        
        ScientificStreamTokenizer tokenizer = 
                                  new ScientificStreamTokenizer(reader);
        AtomInfo ai = AtomInfo.getInstance();
        
        while (true) {
            // a set of four tokens constitute the atom
            // skip token this is serial number
            if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) break;
            
            //  a) the symbol                
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {                
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                if (tokenizer.nval != 0)
                    symbol = ai.getSymbol((int) tokenizer.nval);
                else
                    symbol = "X"; // ghost atom
            } // end if           
            
            if (hasAtomType) tokenizer.nextToken(); // skip token this is atom type
            
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
            molecule.addAtom(symbol, x, y, z, atomIndex);            
                
            atomIndex++;         
        } // end while

        // do we need to read deep?
        if (!readDeep) return;

        // then try to read any additional information
        // that may be available: energy, gradients, frequency etc..
        Molecule.AdditionalInformation adi
                                       = new Molecule.AdditionalInformation();
        molecule.setAdditionalInformation(adi);

        try {
          while (true) {
            line = reader.readLine();
            if (line == null) break;

            if (line.indexOf("Standard orientation:") >= 0) break;
            if (line.indexOf("Input orientation:") >= 0) break;
            if (line.indexOf("Z-Matrix orientation:") >= 0) break;

            line = line.trim();
            // extract energy
            if (line.indexOf("Done") >= 0) {
                molecule.setAdditionalInformationAvailable(true);

                adi.setEnergyAvailable(true);
                adi.setEnergy(Double.parseDouble(line.split("\\s+")[4]));
            } // end if

            // extract gradient
            if (line.indexOf("Cartesian Forces:") >= 0) {
                molecule.setAdditionalInformationAvailable(true);

                adi.setGradientAvailable(true);

                String [] words = line.split("\\s+");
                adi.setMaxGradient(Double.parseDouble(words[3]));
                adi.setRmsGradient(Double.parseDouble(words[5]));
            } // end if
            
            // extract ZP corrected energy
            if (line.indexOf("Sum of electronic and zero-point Energies=") >= 0) {
                molecule.setAdditionalInformationAvailable(true);

                adi.setZpEnergyAvailable(true);

                String [] words = line.split("\\s+");
                adi.setZpEnergy(Double.parseDouble(words[1]));
            }

            // extract Dipole moment
            if (line.indexOf("Dipole moment (") >= 0) {
                molecule.setAdditionalInformationAvailable(true);

                adi.setDipoleAvailable(true);

                // Dipole moments in next line
                String [] words = reader.readLine().trim().split("\\s+");
                adi.setDipole(new Vector3D(Double.parseDouble(words[1]),
                                           Double.parseDouble(words[3]), 
                                           Double.parseDouble(words[5])));
            } // end if

            // extract frequency
            if (line.indexOf("Frequencies --") >= 0) {
                adi.setFrequencyDataAvailable(true);

                MolecularVibrationalProperty vibProperty
                        = adi.getMolecularVibrationalProperty();

                if (vibProperty == null) {
                    vibProperty = new MolecularVibrationalProperty(molecule);
                    adi.setMolecularVibrationalProperty(vibProperty);
                } // end if

                String [] fq = line.trim().split("--")[1].trim().split("\\s+");

                ArrayList<FrequencyItem> freqs = new ArrayList<FrequencyItem>();
                
                for(String f : fq) {
                    FrequencyItem fi = new FrequencyItem(Double.parseDouble(f),
                                                         molecule);
                    if (fi.getFrequency() < 0) {
                        fi.setImaginary(true);
                        fi.setFrequency((-1) * fi.getFrequency());
                    } // end if
      
                    freqs.add(fi);                    
                } // end for

                // now read in additional items
                while(true) {
                  // then check to see if we have more data?
                  line = reader.readLine().trim();

                  if (line.indexOf("Atom") >= 0) break;

                  // reduced mass
                  if (line.indexOf("Red. masses --") >= 0) {
                    String [] rm = line.trim().split("--")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.setReducedMass(Double.parseDouble(rm[i++]));
                    } // end for
                  } // end if

                  // IR intensity
                  if (line.indexOf("IR Inten    --") >= 0) {
                    String [] ir = line.trim().split("--")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.addIntensity(new Intensity(
                         Double.parseDouble(ir[i++]), IntensityType.INFRA_RED));
                    } // end for
                  } // end if

                  // Raman intensity
                  if (line.indexOf("Raman Activ --") >= 0) {
                    String [] ir = line.trim().split("--")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.addIntensity(new Intensity(
                         Double.parseDouble(ir[i++]), IntensityType.RAMAN));
                    } // end for
                  } // end if

                  // depolarization
                  if (line.indexOf("Depolar (P) --") >= 0) {
                    String [] rm = line.trim().split("--")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.setDepolarization(Double.parseDouble(rm[i++]));
                    } // end for
                  } // end if
                } // end while

                // read in the displacements
                for(int i=0; i<molecule.getNumberOfAtoms(); i++) {
                    ArrayList<Vector3D> disp = new ArrayList<Vector3D>();

                    String [] dp = reader.readLine().trim().split("\\s+");

                    for(int j=0; j<freqs.size(); j++) {
                        Vector3D vec = new Vector3D(
                                              Double.parseDouble(dp[(j*3)+2]),
                                              Double.parseDouble(dp[(j*3)+3]),
                                              Double.parseDouble(dp[(j*3)+4]));

                        disp.add(vec);
                        freqs.get(j).addDisplacement(vec);
                    } // end for
                } // end for

                // and then save these frequency numbers
                for(FrequencyItem fi : freqs) {
                    vibProperty.addFrequencyItem(fi);
                } // end for
            } // end if
          } // end while
        } catch(Exception ignored) { }
    }
    
    /**
     * Read the Gaussian input file
     */
    private void readGaussianInput(BufferedReader reader, Molecule molecule,
                                   boolean readDeep) throws IOException {
        // keep reading the file till no "#" is found
        // TODO: modify later to read and fill MetaInformation
        String line = "";
        while (true) {
            line = reader.readLine();            
            if (line.indexOf("%") < 0) break;
        } // end if
                        
        while (true) {
            line = reader.readLine();
            if (line.indexOf("#") < 0) break;            
        } // end if

        // skip a line
        reader.readLine();
        
        // read title
        molecule.setTitle(reader.readLine());
        
        // again skip a line: contains charge and multiplicity
        line = reader.readLine();

        // if this was a blank line, then read the next line too
        if (line.trim().equals("")) reader.readLine();
                
        String symbol;
        double x, y, z;
        int atomIndex = 0;
        
        // not read coordinates
        
        AtomInfo ai = AtomInfo.getInstance();

        String [] words;
        
        while (true) {                        
            line = reader.readLine().trim();

            if (line.equals("")) break;

            words = line.split("\\s+");

            try {
                symbol = ai.getSymbol(Integer.parseInt(words[0].trim()));
            } catch(Exception e) {
                symbol = words[0];
            } // end try .. catch

            x = Double.parseDouble(words[1]);
            y = Double.parseDouble(words[2]);
            z = Double.parseDouble(words[3]);

            // and now we can safely add this atom to our list            
            molecule.addAtom(symbol, x, y, z, atomIndex);            
            
            atomIndex++;            
        } // end while        
    }
    
    /**
     * Read the GAMESS input file
     */
    private void readGAMESSInput(BufferedReader reader, Molecule molecule,
                                 boolean readDeep) throws IOException {
        // keep reading the file till no "$" is found
        // TODO: modify later to read and fill MetaInformation
        String line = "";
        while (true) {            
            line = reader.readLine();
            
            if (line == null) 
                throw new IOException("Incomplete file! No $data found!");
            
            if (line.toLowerCase().indexOf("$data") >= 0) 
                break;
        } // end while
        
        // read title
        molecule.setTitle(reader.readLine());
        
        // skip the next line, is symmetry information, may be read into
        // MetaInformation??
        reader.readLine();
        
        String symbol;
        double x, y, z, charge;
        int atomIndex = 0;
        
        // now read coordinate till the we again see a $
        // instantiate a tokenizer
        ScientificStreamTokenizer tokenizer = 
                                  new ScientificStreamTokenizer(reader);
        while (true) {
            // a set of four tokens constitute the atom
            //  a) the symbol                
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                if (tokenizer.sval.compareToIgnoreCase("$end") == 0) {
                    // we have reached end of $data section!
                    break;
                } // end if

                // WARNING: this code may not work properly
                // for "long" atom names like in CALCIUM for Ca
                symbol = tokenizer.sval;
                if (symbol.length() > 2) {
                    symbol = symbol.substring(0, 1);
                } // end if
            } // end if
            
            //  b) charge
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                charge = tokenizer.nval;
            } // end if
            
            //  c) the x coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                x = tokenizer.nval;
            } // end if

            //  d) the y coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                y = tokenizer.nval;
            } // end if

            //  e) the z coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                z = tokenizer.nval;
            } // end if

            // and now we can safely add this atom to our list            
            molecule.addAtom(symbol, charge, x, y, z, atomIndex);            
                
            atomIndex++;            
        } // end while     
        
        // Do not read further, if we are not reading deep into the file
        if (!readDeep) return;

        reader.mark(100000);
        try {
            // check if we have $mat group
            while (true) {
                if ((line = reader.readLine()).toLowerCase()
                                              .indexOf("$mat") >= 0) break;
            } // end while
            
            int noOfAtoms = molecule.getNumberOfAtoms();            
            int matValues = noOfAtoms * (noOfAtoms+1) / 2;
            int noOfValuesPerLine = 80;
            int noOfLines = matValues/noOfValuesPerLine;
            int noOfValuesInLastLine = matValues%noOfValuesPerLine;
            
            
            if (noOfValuesInLastLine != 0) noOfLines++;
            
            char [] mat = new char[matValues];
            int ii = 0;
            for(int i=0; i<noOfLines; i++) {
                int lim=(i==noOfLines-1)?noOfValuesInLastLine:noOfValuesPerLine;
                
                for (int j=0; j<lim; j++) {
                    mat[ii] = (char) reader.read();            
                    ii++;
                } // end for
                
                // the last character in this line should be a new line
                reader.read();
            } // end for
            
            // setup fragment information so that the selected atoms 
            // can be displayed (hiligheted) .. TODO:
            
        } catch (Exception ignored) { 
            System.err.println("No $mat group found.");            
        } // end of try .. catch block
        
        reader.reset();
        reader.mark(100000);
        try {
            // also check if $efrag is provided
            // so that we can display point charges as well
            while (true) {
                if ((line = reader.readLine()).toLowerCase()
                                              .indexOf("$efrag") >= 0) break;
            } // end while
        
            // read the next line
            line = reader.readLine();
        
            // this line should contain "cart" for us to procced
            if (line.toLowerCase().indexOf("cart") < 0) return;
        
            // else proceed to read the next line
            String cline = (reader.readLine().split("=")[1]).trim();
        
            // now search for the line containing $'cline'
            while (true) {
                if ((line = reader.readLine()).indexOf("$" + cline) >= 0) break;
            } // end while
            
            // skip five lines
            for(int i=0; i<5; i++) reader.readLine();
            
            // now read coordinate till the we again see a "stop"
            // instantiate a tokenizer
            tokenizer = new ScientificStreamTokenizer(reader);
            // ... read in the charge coordinates which are in a.u.
            while (true) {
                // a set of four tokens constitute the atom
                //  a) the symbol
                if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new IOException("Could not understand format : "
                            + tokenizer);
                } else {
                    if (tokenizer.sval.compareToIgnoreCase("stop") == 0) {
                        // we have reached end of $'cline' section!
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
                molecule.addAtom(symbol, 0.0, 
                                 x * Utility.AU_TO_ANGSTROM_FACTOR, 
                                 y * Utility.AU_TO_ANGSTROM_FACTOR, 
                                 z * Utility.AU_TO_ANGSTROM_FACTOR, atomIndex);
                
                atomIndex++;
                
                // skip next two tokens
                tokenizer.nextToken(); tokenizer.nextToken();                
            } // end while
            
            reader.reset();
        } catch (Exception ignored) { 
            System.err.println("No $efrag group found.");
        } // end of try .. catch block
    }
    
    /**
     * Read the GAMESS "run" or output file
     */
    private void readGAMESSRun(BufferedReader reader, Molecule molecule,
                               boolean readDeep) throws IOException {
        // read till we find something interesting: "RUN TITLE"
        boolean convertToAnstrom = false;
        String line = "";        
        while (true) {
            line = reader.readLine();
            
            if (line.indexOf("COORDINATES (BOHR)") >= 0) {                 
                convertToAnstrom = true;
                break;
            } // end if
            if (line.indexOf("COORDINATES OF ALL ATOMS ARE (ANGS)") >= 0) {                
                break;
            }
        } // end while
        
        // skip the next line
        reader.readLine();                
        if (!convertToAnstrom) reader.readLine();
        
        String symbol;
        double x, y, z, charge;
        int atomIndex = 0;
        
        // not read coordinate till the we again see a $
        // instantiate a tokenizer
        ScientificStreamTokenizer tokenizer = 
                                  new ScientificStreamTokenizer(reader);
        while (true) {
            // a set of four tokens constitute the atom
            //  a) the symbol                
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                if ((tokenizer.sval==null) 
                    || tokenizer.sval.equals("")
                    || (tokenizer.sval.indexOf("INTERNUCLEAR") >= 0)
                    || (tokenizer.sval.indexOf("BY") >= 0)
                    || (tokenizer.sval.indexOf("**") >= 0)) break;

                // WARNING: this code may not work properly
                // for "long" atom names like in CALCIUM for Ca
                symbol = tokenizer.sval;
                if (symbol.length() > 2) {
                    symbol = symbol.substring(0, 1);
                } // end if
            } // end if

            //  b) charge
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                charge = tokenizer.nval;
            } // end if
            
            //  c) the x coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                x = tokenizer.nval;
            } // end if

            //  d) the y coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                y = tokenizer.nval;
            } // end if

            //  e) the z coordinate               
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Could not understand format : " 
                                       + tokenizer);
            } else {
                z = tokenizer.nval;
            } // end if

            // and now we can safely add this atom to our list            
            // the coordinates are in BHOR (a.u.) unit, so they need to 
            // be converted!
            if (convertToAnstrom) {
                molecule.addAtom(symbol, charge, 
                                     x * Utility.AU_TO_ANGSTROM_FACTOR, 
                                     y * Utility.AU_TO_ANGSTROM_FACTOR, 
                                     z * Utility.AU_TO_ANGSTROM_FACTOR,
                                     atomIndex);                 
            } else {
                molecule.addAtom(symbol, charge, x, y, z, atomIndex);
            } // end if 
            
            atomIndex++;            
        } // end while

        // are we reading deep?
        if (!readDeep) return;
        
        // then try to read any additional information
        // that may be available: energy, gradients, frequency etc..
        Molecule.AdditionalInformation adi
                                       = new Molecule.AdditionalInformation();
        molecule.setAdditionalInformation(adi);

        try {
          while (true) {
            line = reader.readLine();
            if (line == null) break;
            line = line.trim();

            if (line.indexOf("1NSERCH") >= 0) break;
            if (line.indexOf("BEGINNING GEOMETRY SEARCH") >= 0) break;

            // extract energy
            if (line.indexOf("FINAL") >=0 && line.indexOf("ENERGY") >= 0) {
                adi.setEnergy(Double.parseDouble(line.split("\\s+")[4]));
                adi.setEnergyAvailable(true);
            } // end if

            // if MP2 energy?
            if (line.indexOf("E(MP2)") >=0) {
                adi.setEnergy(Double.parseDouble(line.split("\\s+")[1]));
                adi.setEnergyAvailable(true);
            } // end if

            // cardinality / MTA energy
            if (line.indexOf("CARDINALITY BASED ENERGY EVAL OVER") >= 0) {
                adi.setEnergy(Double.parseDouble(line.split("\\s+")[5]));
                adi.setEnergyAvailable(true);
            } // end if
            
            // gradients
            if (line.indexOf("MAXIMUM GRADIENT") >= 0) {
                adi.setGradientAvailable(true);
                
                String [] words = line.split("\\s+");
                adi.setMaxGradient(Double.parseDouble(words[3]));
                adi.setMaxGradient(Double.parseDouble(words[7]));
            } // end if

            // frequency
            if (line.indexOf("FREQUENCY:") >= 0) {
                adi.setFrequencyDataAvailable(true);

                MolecularVibrationalProperty vibProperty
                        = adi.getMolecularVibrationalProperty();

                if (vibProperty == null) {
                    vibProperty = new MolecularVibrationalProperty(molecule);
                    adi.setMolecularVibrationalProperty(vibProperty);
                } // end if
                
                // read the frequency line
                String [] fq = line.trim().split("[:]")[1].trim().split("\\s+");
                
                ArrayList<FrequencyItem> freqs = new ArrayList<FrequencyItem>();
                
                for(String f : fq) {
                    if (f.indexOf("I") < 0) {
                        FrequencyItem fi = new FrequencyItem(
                                Double.parseDouble(f), molecule);
                        freqs.add(fi);
                    } else {
                        // get the last frequency number and change
                        // its property to imaginary
                        freqs.get(freqs.size()-1).setImaginary(true);
                    } // end if
                } // end for

                while(true) {
                  // then check to see if we have more data?
                  line = reader.readLine().trim();

                  if (line.equals("")) break;

                  // reduced mass
                  if (line.indexOf("REDUCED MASS:") >= 0) {
                    String [] rm = line.trim().split("[:]")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.setReducedMass(Double.parseDouble(rm[i++]));
                    } // end for
                  } // end if

                  // IR intensity
                  if (line.indexOf("IR INTENSITY:") >= 0) {
                    String [] ir = line.trim().split("[:]")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.addIntensity(new Intensity(
                         Double.parseDouble(ir[i++]), IntensityType.INFRA_RED));
                    } // end for
                  } // end if

                  // Raman intensity
                  if (line.indexOf("RAMAN INTENSITY:") >= 0) {
                    String [] ir = line.trim().split("[:]")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.addIntensity(new Intensity(
                         Double.parseDouble(ir[i++]), IntensityType.RAMAN));
                    } // end for
                  } // end if

                  // depolarization
                  if (line.indexOf("DEPOLARIZATION:") >= 0) {
                    String [] rm = line.trim().split("[:]")[1]
                                       .trim().split("\\s+");

                    int i = 0;
                    for(FrequencyItem fi : freqs) {
                        fi.setDepolarization(Double.parseDouble(rm[i++]));
                    } // end for
                  } // end if
                } // end while

                // read in the displacements
                for(int i=0; i<molecule.getNumberOfAtoms(); i++) {
                    ArrayList<Vector3D> disp = new ArrayList<Vector3D>();

                    for(int j=0; j<freqs.size(); j++) {
                        Vector3D vec = new Vector3D();

                        disp.add(vec);
                        freqs.get(j).addDisplacement(vec);
                    } // end for

                    for(int j=0; j<3; j++) {
                        line = reader.readLine().trim();

                        if (line.indexOf("X") >= 0) {
                            String [] xs = line.split("X")[1].trim().split("\\s+");

                            for(int k=0; k<freqs.size(); k++) {
                                disp.get(k).setI(Double.parseDouble(xs[k]));
                            } // end for
                        } // end if

                        if (line.indexOf("Y") >= 0) {
                            String [] ys = line.split("Y")[1].trim().split("\\s+");

                            for(int k=0; k<freqs.size(); k++) {
                                disp.get(k).setJ(Double.parseDouble(ys[k]));
                            } // end for
                        } // end if

                        if (line.indexOf("Z") >= 0) {
                            String [] zs = line.split("Z")[1].trim().split("\\s+");

                            for(int k=0; k<freqs.size(); k++) {
                                disp.get(k).setK(Double.parseDouble(zs[k]));
                            } // end for
                        } // end if
                    } // end for
                } // end for
                
                // and then save these frequency numbers
                for(FrequencyItem fi : freqs) {
                    vibProperty.addFrequencyItem(fi);
                } // end for
            } // end if
          } // end while
        } catch(Exception ignored) { }
    }
    
    /**
     * identify type of the file
     */
    private ExternalFileType identifyProbableType(BufferedReader reader) 
                                                         throws IOException {
        // put a large number and assume that we can determine 
        // file type before reading so many characters and the
        // buffer size if no less than this!
        reader.mark(10000); 
        
        // if we cannot identify the file type after reading
        // first 50 lines, then we give up!
        for(int i=0; i<50; i++) {
            String line = reader.readLine();
            
            if (line.indexOf("$") == 1) {
                reader.reset();
                return ExternalFileType.GAMESS_INPUT;
            } else if (line.indexOf("#") == 0) {
                reader.reset();
                return ExternalFileType.GAUSSIAN_INPUT;
            } else if (line.indexOf("GAMESS") >=0) {
                reader.reset();
                return ExternalFileType.GAMESS_RUN;
            } else if (line.indexOf("Gaussian") >=0) {
                reader.reset();
                return ExternalFileType.GAUSSIAN_RUN;
            } else if (i == 2) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                try {
                    Integer.parseInt(tokenizer.nextToken());
                    reader.reset();
                    return ExternalFileType.GAUSSIAN_CUBE;
                } catch(Exception ignored) { }
            } // end if
        } // end if
        
        // fallback, unknown format
        reader.reset();
        
        // the following return can be potentially dangerous, but extremely
        // useful to read geometries in an optimization run
        return previousType;
    }
    
    /**
     * Defines the file type, to aid in parsing..
     */
    public enum ExternalFileType {
        GAMESS_INPUT, GAUSSIAN_INPUT, GAMESS_RUN, GAUSSIAN_RUN, GAUSSIAN_CUBE,
        UNKNOWN_FORMAT
    }

} // end of class GAMESSGaussianFileReader
