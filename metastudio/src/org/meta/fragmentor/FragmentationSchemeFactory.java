/*
 * FragmentationSchemeFactory.java
 *
 * Created on January 16, 2005, 8:56 PM
 */

package org.meta.fragmentor;

import java.util.Iterator;

/**
 * Defines interfaces as to how the factory interface for getting
 * instances of FragmentationScheme should function.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FragmentationSchemeFactory {        
    /**
     * Getter for property defaultScheme.
     * @return Value of property defaultScheme.
     */
    public FragmentationScheme getDefaultScheme();
    
    /**
     * Get a FragmentationScheme by its name.
     *
     * @param name name of the desired fragmentation scheme
     * @return instance of FragmentationScheme.
     */
    public FragmentationScheme getScheme(String name);
    
    /**
     * Getter for property allSchemes.
     * @return Value of property allSchemes.
     */
    public Iterator<String> getAllSchemes();
    
} // end of interface FragmentationSchemeFactory
