/*
 * MoleculeFileReaderFactory.java
 *
 * Created on April 4, 2003, 6:46 AM
 */

package org.meta.moleculereader;

import java.util.Iterator;

/**
 * As the name suggests the interface follows a factory design pattern so that
 * its clients can obtain instances of various implementations of 
 * MoleculeFileReader interface in a consistant and effitient manner.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MoleculeFileReaderFactory {   
    
    /**
     * implementation of this method should return appropriate instance of 
     * MoleculeFileReader or will throw an runtime exception of the type:
     * java.lang.UnsupportedOperationException
     * 
     * @param readerType an string object that should match (case insensitive)
     *        to the one returned by MoleculeFileReader.getType() method
     * @return an object implementation of MoleculeFileReader that match the 
     *         above criterion. Preferably there should be single instance of 
     *         any reader implementations and the class should be loaded only
     *         when they are required.
     */
    public MoleculeFileReader getReader(String readerType);
    
    /**
     * Returns a list of strings; of which each indicate an available instance 
     * of the MoleculeFileReader interface.
     *
     * @return an Iterator of string objects, each indicating an available 
     *         implimentaion of MoleculeFileReader interface.
     */
    public Iterator<String> getAllSupportedTypes();
    
} // end of interface MoleculeFileReaderFactory
