/*
 * INDPROPropertyFileReader.java
 *
 * Created on July 15, 2005, 8:04 PM
 *
 */

package org.meta.propertyreader.impl;

import java.io.*;
import java.util.StringTokenizer;
import org.meta.math.geom.BoundingBox;
import org.meta.common.ScientificStreamTokenizer;
import org.meta.common.Utility;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.propertyreader.PropertyFileReader;

/**
 * This reads property files (MESP or MED) created from INDPROP. For more
 * information on INDPROP or obtaining a copy of it please mail to its
 * developers at: 
 * <a href="mail:tcg@chem.unipune.ernet.in">tcg@chem.unipune.ernet.in</a>
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class INDPROPropertyFileReader implements PropertyFileReader {
    
    /** Creates a new instance of INDPROPropertyFileReader */
    public INDPROPropertyFileReader() {
    }

    /** 
     * This method returns the type or class of file formats being read.
     * For e.g. if the implementation class reads INDPROP files then this method
     * may return a string like 'INDPROP Property File format'.
     *
     * @return A string indicating the type or class of property file formats,
     * whose reading is supported. The string returned should be treated as
     * case insensitive.
     */   
    @Override
    public String getType() {
        return "INDPROP Property File format";
    }

    /**
     * This method returns a description of the file format supported.
     *
     * @return A string indicating description of the file format.
     */ 
    @Override
    public String getTypeDescription() {
        return "Reads MESP and MED property files generated over a grid" +
                " using INDPROP or compatible code.";
    }

    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param file An instance of the file object specifying the
     * file to be read.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public GridProperty readPropertyFile(File file) throws IOException {
        return readPropertyFile(file.getAbsolutePath());
    }

    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param fileName The complete path of the file to be read.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public GridProperty readPropertyFile(String fileName) throws IOException {
        FileInputStream fis  = new FileInputStream(fileName);        
        GridProperty theProperty = readProperty(fis);
        fis.close();
        
        return theProperty;
    }
    
    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param inputStream General instance of input stream from where to 
     * read the property data.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public GridProperty readProperty(InputStream inputStream) 
                                     throws IOException {
        // convert to a buffered reader
        BufferedReader br = new BufferedReader(
                                new InputStreamReader(inputStream));
        
        return readProperty(br);
    }   
    
    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param reader instance of reader from where to read the property data.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public GridProperty readProperty(BufferedReader reader) throws IOException {
        GridProperty property = new GridProperty();
        StringTokenizer tokenizer;
        
        // varify whether we have correct file and read the ranges
        if (reader.readLine()
                  .indexOf(PropertyFileKeywords.INDPROP_RANGE_KEYWORD) == -1) {
            throw new IOException("The first line of INDPROP should be: "
                                  + PropertyFileKeywords.INDPROP_RANGE_KEYWORD);
        } // end if
        
        // read in the range:
        try {
            BoundingBox bb = new BoundingBox();
            
            // All specification generted by INDPROP are in a.u. which needs
            // to be appropriately converted into angstroms.
            
            // the X component
            tokenizer = new StringTokenizer(reader.readLine());
            
            bb.getUpperLeft().setX(Double.parseDouble(tokenizer.nextToken())
                                   * Utility.AU_TO_ANGSTROM_FACTOR);
            property.setXIncrement(Double.parseDouble(tokenizer.nextToken())
                                   * Utility.AU_TO_ANGSTROM_FACTOR);
            property.setNoOfPointsAlongX(
                                   Integer.parseInt(tokenizer.nextToken()));
            bb.getBottomRight().setX(Double.parseDouble(tokenizer.nextToken())
                                     * Utility.AU_TO_ANGSTROM_FACTOR);
            
            // the Y component
            tokenizer = new StringTokenizer(reader.readLine());
            
            bb.getUpperLeft().setY(Double.parseDouble(tokenizer.nextToken())
                                   * Utility.AU_TO_ANGSTROM_FACTOR);
            property.setYIncrement(Double.parseDouble(tokenizer.nextToken())
                                   * Utility.AU_TO_ANGSTROM_FACTOR);
            property.setNoOfPointsAlongY(
                                   Integer.parseInt(tokenizer.nextToken()));
            bb.getBottomRight().setY(Double.parseDouble(tokenizer.nextToken())
                                     * Utility.AU_TO_ANGSTROM_FACTOR);
            
            // the Z component
            tokenizer = new StringTokenizer(reader.readLine());
            
            bb.getUpperLeft().setZ(Double.parseDouble(tokenizer.nextToken())
                                   * Utility.AU_TO_ANGSTROM_FACTOR);
            property.setZIncrement(Double.parseDouble(tokenizer.nextToken())
                                   * Utility.AU_TO_ANGSTROM_FACTOR);
            property.setNoOfPointsAlongZ(
                                   Integer.parseInt(tokenizer.nextToken()));
            bb.getBottomRight().setZ(Double.parseDouble(tokenizer.nextToken())
                                     * Utility.AU_TO_ANGSTROM_FACTOR);
            
            property.setBoundingBox(bb);
            
            // enable GC!
            tokenizer = null;
        } catch(Exception e) {
            throw new IOException("Could not read range information from "
                                  + " the provided file." );
        } // end of try catch ... block
        
        // next check if another keyword is available
        String line = reader.readLine(); 
        if ((line.indexOf(PropertyFileKeywords.INDPROP_FUNCTION_KEYWORD) == -1)
        &&(line.indexOf(PropertyFileKeywords.INDPROP_FUNCTION_KEYWORD_2) == -1))
        {
            throw new IOException("The INDPROP property file should have: '"
                            + PropertyFileKeywords.INDPROP_FUNCTION_KEYWORD
                            + "' or '"
                            + PropertyFileKeywords.INDPROP_FUNCTION_KEYWORD_2
                            + "' keyword.");
        } // end if
        
        // set up the grid array to copy the function values
        int noOfGridPoints = property.getNumberOfGridPoints();
        
        double [] functionValues = new double[noOfGridPoints];
        
        // start a tokeniser to obtain all the values in the grid
        ScientificStreamTokenizer sTokenizer = 
                                  new ScientificStreamTokenizer(reader); 
        
        try {
            for(int i=0; i<noOfGridPoints; i++) {
                if (sTokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("Could not understand format : " 
                                           + sTokenizer);
                } else {
                    functionValues[i] = sTokenizer.nval;
                } // end if
            } // end for
        } catch (Exception e) {
            throw new IOException("Error reading file : " + sTokenizer
                                  + "\n Exception is : " + e.toString());
        } // end of try ... catch block
        
        // setup the function values in property object
        property.setFunctionValues(functionValues);
        
        return property;
    }   

    /**
     * The implementation of this method should make a fast guess as to 
     * whether this file could be read properly by the intended reader.
     * This is usually done by checking for some keywords in the file.
     *
     * @param fileName the file name to be tested for
     * @return a boolean value indicating the file format could be understood 
     *         or not
     * @throws IOException Indicating an error in the input stream / file.
     */
    @Override
    public boolean isMyType(String fileName) throws IOException {
        FileInputStream fis  = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(
                                new InputStreamReader(fis));        
        boolean myType = true;
        
        // verify whether we have correct file and read the ranges
        if (br.readLine()
              .indexOf(PropertyFileKeywords.INDPROP_RANGE_KEYWORD) == -1) {
            myType = false;
        } // end if       
        
        br.close();
        fis.close();
        
        return myType;
    }
    
} // end of class INDPROPropertyFileReader
