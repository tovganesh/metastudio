/*
 * URLImplParameter.java
 *
 * Created on December 5, 2004, 8:21 PM
 */

package org.meta.config.impl;

import java.net.*;
import org.meta.config.Parameter;

/**
 * A simple implementation of Parameter interface for URLs (search urls mostly)
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class URLImplParameter implements Parameter {
    
    private URL theURL;
    
    /** Creates a new instance of URLImplParameter */
    public URLImplParameter(URL theURL) {
        this.theURL = theURL;
    }
    
    /** Getter for property value.
     * @return Value of property value.
     */
    public Object getValue() {
        return theURL;
    }
    
    /** Setter for property value.
     * @param value New value of property value.
     * @throws may throw java.lang.ClassCastException
     */
    public void setValue(Object value) {
        this.theURL = (URL) value;
    }    
} // end of class URLImplParameter
