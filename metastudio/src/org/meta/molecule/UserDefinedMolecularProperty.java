/**
 * UserDefinedMolecularProperty.java
 *
 * Created on Feb 23, 2009
 */
package org.meta.molecule;

import java.io.Serializable;

/**
 * A simple framework for adding arbitary user defined molecular property.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class UserDefinedMolecularProperty implements Serializable {

    /** Create a new instance of UserDefinedMolecularProperty */
    public UserDefinedMolecularProperty() {
    }

    /** Creates new instance of UserDefinedAtomProperty */
    public UserDefinedMolecularProperty(String name, Serializable value) {
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

    protected boolean originDependent;

    /**
     * Get the value of originDependent
     *
     * @return the value of originDependent
     */
    public boolean isOriginDependent() {
        return originDependent;
    }

    /**
     * Set the value of originDependent.
     * (Note: origin dependent property is the one that is depended on the
     *  machine where Molecule object was created, an example is
     *  CommonUserDefinedMolecularProperty.SOURCE_FILE_NAME)
     *
     * @param originDependent new value of originDependent
     */
    public void setOriginDependent(boolean originDependent) {
        this.originDependent = originDependent;
    }
}
