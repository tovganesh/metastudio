/*
 * PropertyFileReader.java
 *
 * Created on July 15, 2005, 3:16 PM
 *
 */

package org.meta.propertyreader;

import java.io.*;
import org.meta.molecule.property.electronic.GridProperty;

/**
 * This interface defines how an implementation that reads various property
 * file formats should behave. We use an interface driven approach so as to
 * reduce the amount of memory any unwanted class may take. Refer to class
 * PropertyFileReaderFactory for information on how this is acheived.
 * This interface only deals with grid based (or so called point based) 
 * properties.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface PropertyFileReader {
    
    /** 
     * This method returns the type or class of file formats being read.
     * For e.g. if the implementation class reads INDPROP files then this method
     * may return a string like 'INDPROP Property File format'.
     *
     * @return A string indicating the type or class of property file formats,
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
     * Reads the property file and returns GridProperty object.
     *
     * @param fileName The complete path of the file to be read.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    public GridProperty readPropertyFile(String fileName) throws IOException;
    
    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param file An instance of the file object specifing the 
     * file to be read.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    public GridProperty readPropertyFile(File file) throws IOException;
    
    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param inputStream General instance of input stream from where to 
     * read the property data.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    public GridProperty readProperty(InputStream inputStream)
                                     throws IOException;
    
    /**
     * Reads the property file and returns GridProperty object.
     *
     * @param reader instance of reader from where to read the property data.
     * @return instance of the GridProperty object.
     * @throws IOException Indicating an error in the input stream / file.
     */
    public GridProperty readProperty(BufferedReader reader)
                                     throws IOException;
    
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
    public boolean isMyType(String fileName) throws IOException;
} // end of interface PropertyFileReader
