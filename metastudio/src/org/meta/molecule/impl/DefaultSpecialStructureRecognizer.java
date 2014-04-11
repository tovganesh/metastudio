/*
 * DefaultSpecialStructureRecognizer.java
 *
 * Created on November 21, 2004, 9:06 AM
 */

package org.meta.molecule.impl;

import org.meta.molecule.AtomGroupList;
import org.meta.molecule.Molecule;
import org.meta.molecule.SpecialStructureRecognizer;

/**
 * Default structure recognizer, doesn't recognize any structures! <br>
 * Can be used to programatically define a group of atoms as a 
 * "functional group" or alike.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DefaultSpecialStructureRecognizer 
                           implements SpecialStructureRecognizer {
    
    /**
     * Holds value of property atomGroup.
     */
    private AtomGroupList atomGroup;
                               
    /** Creates a new instance of DefaultSpecialStructureRecognizer */
    public DefaultSpecialStructureRecognizer() {
        atomGroup = new AtomGroupListImpl();
    }
    
    /** 
     * This method does not do anything useful
     *
     * @param molecule The molecule object reference.
     */    
    @Override
    public void recognizeAndRecord(Molecule molecule) {
    }
    
    /**
     * Getter for property atomGroup.
     * @return Value of property atomGroup.
     */
    @Override
    public AtomGroupList getGroupList() {
        return this.atomGroup;
    }    
    
    /**
     * Setter for property atomGroup.
     * @param atomGroup New value of property atomGroup.
     */
    @Override
    public void setGroupList(AtomGroupList atomGroup) {
        this.atomGroup = atomGroup;
    }
    
} // end of class DefaultSpecialStructureRecognizer
