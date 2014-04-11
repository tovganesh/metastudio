/*
 * DefaultAtomGroup.java
 *
 * Created on November 21, 2004, 11:30 AM
 */

package org.meta.molecule.impl;

import java.util.*;
import org.meta.molecule.AtomGroup;

/**
 * An implementaion of a default atom group.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DefaultAtomGroup implements AtomGroup {
    
    protected LinkedHashSet<Integer> groupAtoms;        
    
    /** Creates a new instance of DefaultAtomGroup */
    public DefaultAtomGroup() {
        groupAtoms = new LinkedHashSet<Integer>();        
    }        
    
    /**
     * adds an unique atom index to this ring
     *
     * @param atomIndex is an atom index to be added to the current ring
     */
    @Override
    public void addAtomIndex(int atomIndex) {
        groupAtoms.add(new Integer(atomIndex));
    }
    
    /**
     * removes a atom index from the ring
     *
     * @param atomIndex is an atom index to be removed from current ring
     */
    @Override
    public void removeAtomIndex(int atomIndex) {
        groupAtoms.remove(new Integer(atomIndex));
    }
    
    /**
     * remove the last atom index added to the ring
     *
     * @return int the atomIndex that is removed from the ring
     */
    @Override
    public int popAtomIndex() {
       Integer toRemove = ((Integer) groupAtoms.toArray()[groupAtoms.size()-1]);
        
       groupAtoms.remove(toRemove);
       return toRemove.intValue();
    }
    
    /** 
     * retrive an atom index at a given location in the ring list
     *
     * @param index of in the list
     * @return the atom index at the specified location
     * @throws runtime exception IndexOutOfBoundsException if not a
     *         valid index.
     */
    @Override
    public int getAtomIndexAt(int index) {
        return ((Integer) groupAtoms.toArray()[index]).intValue();
    }
    
    /**
     * get a list of all atom indices in this ring
     *
     * @return Iterator - the list of all atom indices
     */
    @Override
    public Iterator<Integer> getAtomIndices() {
        return groupAtoms.iterator();
    }
    
    /**
     * get the number of atom indices in this ring
     *
     * @return int - number of atom indices in this ring
     */
    @Override
    public int getSize() {
        return groupAtoms.size();
    }
    
    /**
     * overridden toString() method
     */
    @Override
    public String toString() {
        return groupAtoms.toString();
    }
    
    /**
     * Getter for property atomList.
     * @return Value of property atomList.
     */
    @Override
    public ArrayList<Integer> getAtomList() {
        return new ArrayList<Integer>(groupAtoms);
    }
    
    /**
     * Intersection (a set operation) of one atom group and other.
     * The following procedure is followed: <br>
     * <ol>
     * <li> Check if AtomGroup is of DefaultAtomGroup type </li>
     * <li> Check size, ensure i am bigger ! </li>
     * <li> Apply intersection </li>
     * <li> Add back first and last elements of <code>atomGroup</code> </li>
     * </ol>
     *
     * @param atomGroup - with what we should intersect
     * @return true, if operation was a success else false
     */
    @Override
    public boolean intersect(AtomGroup atomGroup) {
        if (atomGroup instanceof DefaultAtomGroup) {
            DefaultAtomGroup r2 = (DefaultAtomGroup) atomGroup;
            
            if (groupAtoms.size() > r2.groupAtoms.size()) {
                if (groupAtoms.containsAll(r2.groupAtoms)) {
                    groupAtoms.removeAll(r2.groupAtoms);
                    
                    Object [] atoms = r2.groupAtoms.toArray();
                    groupAtoms.add((Integer) atoms[0]);
                    groupAtoms.add((Integer) atoms[atoms.length - 1]);
                    
                    return true;
                } // end if
            } // end if
        } // end if
        
        return false;
    }
    
    /**
     * Union (a set operation) of one ring and other
     *
     * @param atomGroup - with what we should union
     * @return true, if operation was a success else false
     */
    @Override
    public boolean union(AtomGroup atomGroup) {
        if (atomGroup instanceof DefaultAtomGroup) {
            groupAtoms.addAll(((DefaultAtomGroup) atomGroup).groupAtoms);
            
            return true;
        } // end if
        
        return false;
    }
    
    /**
     * overridden finalize
     */
    @Override
    protected void finalize() throws Throwable {
        groupAtoms = null;
        
        super.finalize();
    }
    
    /**
     * checks if two atom groups are equal
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof DefaultAtomGroup))) return false;
        
        return (groupAtoms.equals(((DefaultAtomGroup) obj).groupAtoms));
    }

    /**
     * Overridden hashcode..
     * @return the hashcode for comparison
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.groupAtoms != null 
                            ? this.groupAtoms.hashCode() : 0);
        return hash;
    }
} // end of class DefaultAtomGroup
