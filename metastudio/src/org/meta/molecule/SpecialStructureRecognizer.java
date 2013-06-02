/*
 * SpecialStructureRecognizer.java
 *
 * Created on May 5, 2003, 6:31 AM
 */

package org.meta.molecule;

/**
 * Interface defination of how the SpecialStructureRecognizer should be 
 * implimented.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface SpecialStructureRecognizer {
    
   /** 
    * This methods' implementation should recognize special structure 
    * (like a ring or a functional group) and record this information 
    * in a object of AtomGroupList.
    *
    * @param molecule The molecule object reference.
    */    
    public void recognizeAndRecord(Molecule molecule);

    /**
     * This method returns the list of all the identified structures as a
     * object of AtomGroupList
     * 
     * @return Instance of AtomGroupList
     */
    public AtomGroupList getGroupList();
    
    /**
     * Setter for property groupList.
     * @param groupList New value of property groupList.
     */
    public void setGroupList(AtomGroupList groupList);
    
} // end of interface SpecialStructureRecognizer
