/*
 * AtomGroup.java
 *
 * Created on September 9, 2003, 9:21 AM
 */

package org.meta.molecule;

import java.util.*;

/**
 * Represents a generic atom group for representing special structures
 * like rings or functional groups.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface AtomGroup {
    
    /**
     * adds an unique atom index to the group
     *
     * @param atomIndex is an atom index to be added to the current group
     */
    public void addAtomIndex(int atomIndex);
    
    /**
     * removes a atom index from the group
     *
     * @param atomIndex is an atom index to be removed from current group
     */
    public void removeAtomIndex(int atomIndex);       
    
    /**
     * remove the last atom index added to the group
     *
     * @return int the atomIndex that is removed from the group
     */
    public int popAtomIndex();
    
    /**
     * get a list of all atom indices in this group
     *
     * @return Iterator - the list of all atom indices
     */
    public Iterator<Integer> getAtomIndices();
    
    /**
     * get the number of atom indices in this group
     *
     * @return int - number of atom indices in this group
     */
    public int getSize();
    
    /**
     * retrive an atom index at a given location in the list
     * 
     * @param index of in the list 
     * @return the atom index at the specified location
     */
    public int getAtomIndexAt(int index);
    
    /**
     * Getter for property atomList.
     * @return Value of property atomList.
     */
    public ArrayList<Integer> getAtomList();
    
    /**
     * Intersection (a set operation) of one atom group and other
     *
     * @param atomGroup - with what we should intersect
     * @return true, if operation was a success else false
     */
    public boolean intersect(AtomGroup atomGroup);
    
    /**
     * Union (a set operation) of one atom group and other
     *
     * @param atomGroup - with what we should union
     * @return true, if operation was a success else false
     */
    public boolean union(AtomGroup atomGroup);
    
} // end of interface AtomGroup
