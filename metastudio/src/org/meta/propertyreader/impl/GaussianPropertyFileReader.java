/*
 * GaussianPropertyFileReader.java
 *
 * Created on April 30, 2006, 3:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
 * Reads volumetric data from Gaussian "cube" format. For more information in 
 * Gaussian visit: <a href="http://www.gaussian.com">www.gaussian.com</a>.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GaussianPropertyFileReader implements PropertyFileReader {
    
    /** Creates a new instance of GaussianPropertyFileReader */
    public GaussianPropertyFileReader() {
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
        return "Gaussian Cube File format";
    }

    /**
     * This method returns a description of the file format supported.
     *
     * @return A string indicating description of the file format.
     */ 
    @Override
    public String getTypeDescription() {
        return "Reads volumetric data from Gaussian cube file.";
    }

    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param file An instance of the file object specifing the 
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
        String line;
        
        // skip first two lines
        reader.readLine(); reader.readLine(); 
        
        // read in the range:
        try {
            BoundingBox bb = new BoundingBox();
            
            line = reader.readLine();
            tokenizer = new StringTokenizer(line);
    
            int noOfAtoms = Integer.parseInt(tokenizer.nextToken());
            double divFactor = 1.0;
            
            if (noOfAtoms > 0) divFactor = Utility.AU_TO_ANGSTROM_FACTOR;
            
            noOfAtoms = Math.abs(noOfAtoms);
            
            bb.getUpperLeft().setX(
                    Double.parseDouble(tokenizer.nextToken()) * divFactor);
            bb.getUpperLeft().setY(
                    Double.parseDouble(tokenizer.nextToken()) * divFactor);
            bb.getUpperLeft().setZ(
                    Double.parseDouble(tokenizer.nextToken()) * divFactor);      

            // read NX, NY, NZ
            line = reader.readLine();
            tokenizer = new StringTokenizer(line);

            property.setNoOfPointsAlongX(
                                  Integer.parseInt(tokenizer.nextToken()));
            property.setXIncrement(
                         Double.parseDouble(tokenizer.nextToken()) * divFactor);

            // NY
            line = reader.readLine();
            tokenizer = new StringTokenizer(line);

            property.setNoOfPointsAlongY(
                                  Integer.parseInt(tokenizer.nextToken()));
            tokenizer.nextToken();
            property.setYIncrement(
                         Double.parseDouble(tokenizer.nextToken()) * divFactor);

            // NZ
            line = reader.readLine();
            tokenizer = new StringTokenizer(line);

            property.setNoOfPointsAlongZ(
                                  Integer.parseInt(tokenizer.nextToken()));
            tokenizer.nextToken(); tokenizer.nextToken();
            property.setZIncrement(
                        Double.parseDouble(tokenizer.nextToken()) * divFactor);

            // bottom right
            bb.getBottomRight().setX(bb.getUpperLeft().getX() 
                           + ((property.getNoOfPointsAlongX()+1)
                                       *property.getXIncrement()));
            bb.getBottomRight().setY(bb.getUpperLeft().getY()
                           + ((property.getNoOfPointsAlongY()+1)
                                       *property.getYIncrement()));
            bb.getBottomRight().setZ(bb.getUpperLeft().getZ()
                           + ((property.getNoOfPointsAlongZ()+1)
                                       *property.getZIncrement()));

            property.setBoundingBox(bb);
            
            // from the next line the Molecule entry starts
            for(int i=0; i<noOfAtoms; i++) reader.readLine();
            
            // next line may be an no of mos, or will contain data
            // check whats it
            int mos = 0;
            boolean hasMos = true;
            reader.mark(10000);
            line = reader.readLine();
            tokenizer = new StringTokenizer(line);
            try {
                mos = Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) { hasMos = false; }
            if (!hasMos) reader.reset();
            
            // the way to read data if Mos are available is a bit different!
            if (hasMos) {
                // set up the grid array to copy the function values
                int noOfGridPoints = property.getNumberOfGridPoints();
                
                double [] fVals = new double[noOfGridPoints*mos];
                double [] functionValues = new double[noOfGridPoints];
                
                // start a tokeniser to obtain all the values in the grid
                ScientificStreamTokenizer sTokenizer =
                        new ScientificStreamTokenizer(reader);
                
                try {
                    int ijkl = 0;
                    int N1 = property.getNoOfPointsAlongX();
                    int N2 = property.getNoOfPointsAlongY();
                    int N3 = property.getNoOfPointsAlongZ();
                    for(int i=0; i<N1; i++) {
                        for(int j=0; j<N2; j++) {
                            for(int k=0; k<N3; k++) {
                                for(int l=0; l<mos; l++) {
                                    ijkl = i + N1 * j + N1 * N2 * k 
                                             + N1 * N2 * N3 * l;
                                    if (sTokenizer.nextToken() 
                                            != StreamTokenizer.TT_NUMBER) {
                                        throw new IOException(
                                                "Could not understand format : "
                                                + sTokenizer);
                                    } else {
                                        fVals[ijkl] = sTokenizer.nval;
                                    } // end  if
                                } // end for
                            } // end for
                        } // end for
                    } // end for
                    
                    // now write the stuff
                    int ijk=0;
                    for(int i=0; i<N1; i++) {
                        for(int j=0; j<N2; j++) {
                            for(int k=0; k<N3; k++) {
                                ijkl = i + N1 * j + N1 * N2 * k 
                                         + N1 * N2 * N3 * 0;
                                functionValues[ijk] = fVals[ijkl];
                                ijk++;
                            } // end for
                        } // end for
                    } // end for
                    fVals = null;
                } catch (Exception e) {
                    throw new IOException("Error reading file : " + sTokenizer
                            + "\n Exception is : " + e.toString());
                } // end of try ... catch block
                
                // setup the function values in property object
                property.setFunctionValues(functionValues);
            } else {
                // set up the grid array to copy the function values
                int noOfGridPoints = property.getNumberOfGridPoints();
                
                double [] functionValues = new double[noOfGridPoints];
                
                // start a tokeniser to obtain all the values in the grid
                ScientificStreamTokenizer sTokenizer =
                        new ScientificStreamTokenizer(reader);
        
                // else we read the data in standard way
                try {
                    for(int i=0; i<noOfGridPoints; i++) {
                       if (sTokenizer.nextToken() != StreamTokenizer.TT_NUMBER){
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
            } // end if
            
            // enable GC!
            tokenizer = null;
        } catch(Exception e) {
            throw new IOException("Could not read range information from "
                                  + " the provided file. The Error is: " 
                                  + e.toString());
        } // end of try catch ... block                
        
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
        boolean myType = false;
        
        // varify whether we have correct file and read the ranges
        for(int i=0; i<4; i++) {
            String line = br.readLine();
            
            if (i == 2) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                try {
                    Integer.parseInt(tokenizer.nextToken());
                    myType = true;
                    break;
                } catch(Exception ignored) { }
            } // end if
        } // end for
                
        br.close();
        fis.close();
        
        return myType;
    }        
} // end of class GaussianPropertyFileReader
