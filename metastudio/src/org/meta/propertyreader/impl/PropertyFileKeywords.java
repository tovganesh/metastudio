/*
 * PropertyFileKeywords.java
 *
 * Created on July 15, 2005, 8:45 PM
 *
 */

package org.meta.propertyreader.impl;

/**
 * A class that defines kewords related to various property file formats.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public final class PropertyFileKeywords {
    
    /** Creates a new instance of PropertyFileKeywords */
    private PropertyFileKeywords() {
    }
    
    // INDPROP related keywords:
    public static final String INDPROP_RANGE_KEYWORD = "RANGE :";
    
    public static final String INDPROP_FUNCTION_KEYWORD 
                                                     = "FUNCTION VALUES ARE :";
    
    public static final String INDPROP_FUNCTION_KEYWORD_2 
                                                     = "FUNCTION VALUES :";
} // end of class PropertyFileKeywords
