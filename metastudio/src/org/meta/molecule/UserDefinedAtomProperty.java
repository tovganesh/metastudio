/**
 * UserDefinedAtomProperty.java
 *
 * Created on Mar 11, 2009
 */
package org.meta.molecule;

import java.io.Serializable;

/**
 * A simple framework for adding arbitary user defined property to Atom instance.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class UserDefinedAtomProperty implements Serializable {

    /** Creates new instance of UserDefinedAtomProperty */
    public UserDefinedAtomProperty() {
    }

    /** Creates new instance of UserDefinedAtomProperty */
    public UserDefinedAtomProperty(String name, Serializable value) {
        this.name  = name;
        this.value = value;
    }
    
    protected String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    protected Serializable value;

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public Serializable getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(Serializable value) {
        this.value = value;
    }
}
