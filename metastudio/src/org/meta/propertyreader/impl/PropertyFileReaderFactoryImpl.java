/*
 * PropertyFileReaderFactoryImpl.java
 *
 * Created on July 17, 2005, 12:05 PM
 *
 */

package org.meta.propertyreader.impl;

import java.io.*;
import java.util.*;
import org.meta.common.resource.StringResource;
import org.meta.propertyreader.PropertyFileReader;
import org.meta.propertyreader.PropertyFileReaderFactory;

/**
 * The factory for PropertyFileReader objects.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PropertyFileReaderFactoryImpl 
             implements PropertyFileReaderFactory {
    
    private ResourceBundle resources;
    private Vector<String> theKeys;
    
    /** Creates a new instance of PropertyFileReaderFactoryImpl */
    public PropertyFileReaderFactoryImpl() {
        theKeys = new Vector<String>();
        
        // the initial parameters
        setDefaultParams();     
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() {
        StringResource strings   = StringResource.getInstance();
        resources = ResourceBundle.getBundle(
                                      strings.getPropertyFileReaderResource());
        
        Enumeration<String> implKeys = resources.getKeys();        
                
        while(implKeys.hasMoreElements()) {            
            theKeys.add(implKeys.nextElement());            
        }
    }

    /**
     * Returns a list of strings; of which each indicate an available instance 
     * of the PropertyFileReader interface.
     *
     * @return an Iterator of string objects, each indicating an available 
     *         implementation of PropertyFileReader interface.
     */
    @Override
    public Iterator<String> getAllSupportedTypes() {
        return theKeys.iterator();
    }

    /**
     * In most probable cases the implementation of this method should call
     * isMyType() method all implementors of PropertyFileReader class objects
     * to get information on the first such reader able to understand the 
     * format.
     * In case the implementation cannot determine the type of reader, then
     * an runtime exception of the type: java.lang.UnsupportedOperationException
     * should be thrown.
     *
     * @param file a File object pointing to property data
     * @return an appropriate object 
     */
    @Override
    public PropertyFileReader getMostProbableReaderFor(File file) {
        try {
            for(String aKey : theKeys) {
                PropertyFileReader reader = getReader(aKey);
                
                if (reader.isMyType(file.getAbsolutePath())) {
                    return reader;
                } // end if
            } // end for
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to determine " +
                    "reader for : " + file.getAbsolutePath() + ". The" +
                    "exception was: " + e.toString());
        } // end of try .. catch exception
        
        // if the control reaches here, that means probably we were unable
        // to determine the proper reader
        throw new UnsupportedOperationException("Unable to determine reader " +
                "for : " + file.getAbsolutePath());
    }

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
    @Override
    public PropertyFileReader getReader(String readerType) {
        try {
            return ((PropertyFileReader)
                  Class.forName(resources.getString(readerType)).newInstance());
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + readerType + "."
                      + " Exception is : " + e.toString());
        } // end try .. catch block
    }
} // end of class PropertyFileReaderFactoryImpl
