/*
 * PropertyFileReaderfactory.java
 *
 * Created on July 15, 2005, 7:19 PM
 *
 */

package org.meta.propertyreader;

import java.io.File;
import java.util.Iterator;

/**
 * A interface defining factory pattern for accessing appropriate instances
 * of PropertyFileReader instances.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface PropertyFileReaderFactory {
    
    /**
     * implementation of this method should return appropriate instance of 
     * PropertyFileReader or will throw an runtime exception of the type:
     * java.lang.UnsupportedOperationException
     * 
     * @param readerType an string object that should match (case insensitive)
     *        to the one returned by PropertyFileReader.getType() method
     * @return an object implementation of PropertyFileReader that match the 
     *         above criterion. Preferably there should be single instance of 
     *         any reader implementations and the class should be loaded only
     *         when they are required.
     */
    public PropertyFileReader getReader(String readerType);
    
    /**
     * Returns a list of strings; of which each indicate an available instance 
     * of the PropertyFileReader interface.
     *
     * @return an Iterator of string objects, each indicating an available 
     *         implimentaion of PropertyFileReader interface.
     */
    public Iterator<String> getAllSupportedTypes();
    
    /**
     * In most probable cases the implementation of this method should call
     * isMyType() method all implementors of PropertyFileReader class objects
     * to get information on the first such reader able to understand the 
     * format.
     * In case the implimentation cannot determine the type of reader, then
     * an runtime exception of the type: java.lang.UnsupportedOperationException
     * should be thrown.
     *
     * @param file a File object pointing to property data
     * @return an appropriate object 
     */
    public PropertyFileReader getMostProbableReaderFor(File file);
} // end of interface PropertyFileReaderfactory
