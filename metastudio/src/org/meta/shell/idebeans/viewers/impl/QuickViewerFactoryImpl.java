/*
 * QuickViewerFactoryImpl.java
 *
 * Created on October 25, 2003, 7:35 AM
 */

package org.meta.shell.idebeans.viewers.impl;

import java.util.*;
import org.meta.common.resource.StringResource;
import org.meta.shell.idebeans.viewers.QuickViewer;
import org.meta.shell.idebeans.viewers.QuickViewerFactory;

/**
 * A simple implementation of QuickViewerFactory interface ...
 * producing the instances of QuickViewers as required.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class QuickViewerFactoryImpl implements QuickViewerFactory {
    
    private ResourceBundle resources;
    private Vector<String> theKeys;
    
    /** Creates a new instance of QuickViewerFactoryImpl */
    public QuickViewerFactoryImpl() {
        theKeys = new Vector<String>();
        
        // the initial parameters
        setDefaultParams();                
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() {
        StringResource strings   = StringResource.getInstance();
        resources = ResourceBundle.getBundle(strings.getViewerResource());
        
        Enumeration<String> implKeys = resources.getKeys();        
                
        while(implKeys.hasMoreElements()) {            
            theKeys.add(implKeys.nextElement());            
        }
    }
    
    /**
     * Return an instance of QuickViewer for the "mime" string supplied 
     * or else throw an UnsupportedOperationException.
     *
     * @return appropriate instance of QuickViewer for "mime"
     */
    @Override
    public Iterator getAllSupportedViewers() {
        return theKeys.iterator();
    }
    
    /**
     * Returns a list of all available viewers as a list of "mime" strings
     *
     * @return Iterator : list of "mime" strings
     */
    @Override
    public QuickViewer getViewer(String mime) {
        try {
            return ((QuickViewer)
                     Class.forName(resources.getString(mime)).newInstance());
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + mime + "."
                      + " Exception is : " + e.toString());
        } // end try .. catch block
    }   
} // end of class QuickViewerFactoryImpl
