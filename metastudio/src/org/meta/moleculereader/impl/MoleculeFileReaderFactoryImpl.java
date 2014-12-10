/*
 * MoleculeFileReaderFactoryImpl.java
 *
 * Created on September 21, 2003, 5:51 PM
 */

package org.meta.moleculereader.impl;

import java.util.*;
import org.meta.common.resource.StringResource;
import org.meta.moleculereader.MoleculeFileReader;
import org.meta.moleculereader.MoleculeFileReaderFactory;

/**
 * The factory of MoleculeFileReader objects!
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeFileReaderFactoryImpl implements MoleculeFileReaderFactory {
    
    private ResourceBundle resources;
    private Vector<String> theKeys;
    
    /** Creates a new instance of MoleculeFileReaderFactoryImpl */
    public MoleculeFileReaderFactoryImpl() {
        theKeys = new Vector<String>();
        
        // the initial parameters
        setDefaultParams();                
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() {
        StringResource strings   = StringResource.getInstance();
        resources = ResourceBundle.getBundle(strings.getFileReaderResource());
        
        Enumeration<String> implKeys = resources.getKeys();        
                
        while(implKeys.hasMoreElements()) {            
            theKeys.add(implKeys.nextElement());            
        }
    }
        
   /**
     * Returns a list of strings; of which each indicate an available instance 
     * of the MoleculeFileReader interface.
     *
     * @return an Iterator of string objects, each indicating an available 
     *         implementation of MoleculeFileReader interface.
     */
    public Iterator<String> getAllSupportedTypes() {
        return theKeys.iterator();
    }

    /**
     * this method returns appropriate instance of MoleculeFileReader or 
     * will throw an runtime exception of the type:
     * java.lang.UnsupportedOperationException
     * 
     * @param readerType an string object that should match (case insensitive)
     *        to the one returned by MoleculeFileReader.getType() method
     * @return an object implementation of MoleculeFileReader that match the 
     *         above criterion. Preferably there should be single instance of 
     *         any reader implementations and the class should be loaded only
     *         when they are required.
     */
    public MoleculeFileReader getReader(String readerType) {        
        try {
            return ((MoleculeFileReader)
                  Class.forName(resources.getString(readerType)).newInstance());
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + readerType + "."
                      + " Exception is : " + e.toString());
        } // end try .. catch block
    }    
} // end of class MoleculeFileReaderFactoryImpl
