/*
 * Ring.java
 *
 * Created on September 9, 2003, 2:43 PM
 */

package org.meta.molecule.impl;

import java.util.*;
import org.meta.molecule.AtomGroup;

/**
 * An implementaion of AtomGroup representing a ring.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Ring implements AtomGroup {
    
    private LinkedHashSet<Integer> ringAtoms;
    
    private static final String PLANAR = "Planar";
    private static final String NON_PLANAR = "Non Planar";
    
    /**
     * Holds value of property planar.
     */
    private boolean planar;
    
    /** Creates a new instance of Ring */
    public Ring() {
        ringAtoms = new LinkedHashSet<Integer>();
        planar    = false;
    }
    
    /** Creates a new instance of Ring 
     * @param type a string representing the type for ring
     */
    public Ring(String type) {
        ringAtoms = new LinkedHashSet<Integer>();
        setRingType(type);
    }
    
    /**
     * adds an unique atom index to this ring
     *
     * @param atomIndex is an atom index to be added to the current ring
     */
    public void addAtomIndex(int atomIndex) {
        ringAtoms.add(new Integer(atomIndex));
    }
    
    /**
     * removes a atom index from the ring
     *
     * @param atomIndex is an atom index to be removed from current ring
     */
    public void removeAtomIndex(int atomIndex) {
        ringAtoms.remove(new Integer(atomIndex));
    }
    
    /**
     * remove the last atom index added to the ring
     *
     * @return int the atomIndex that is removed from the ring
     */
    public int popAtomIndex() {
        Integer toRemove = ((Integer) ringAtoms.toArray()[ringAtoms.size()-1]);
        
        ringAtoms.remove(toRemove);
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
    public int getAtomIndexAt(int index) {
        return ((Integer) ringAtoms.toArray()[index]).intValue();
    }
    
    /**
     * get a list of all atom indices in this ring
     *
     * @return Iterator - the list of all atom indices
     */
    public Iterator<Integer> getAtomIndices() {
        return ringAtoms.iterator();
    }
    
    /**
     * get the number of atom indices in this ring
     *
     * @return int - number of atom indices in this ring
     */
    public int getSize() {
        return ringAtoms.size();
    }
    
    /**
     * overridden toString() method
     */
    public String toString() {
        return ringAtoms.toString() + ((planar) ? 
                                          ("- " + PLANAR) 
                                        : ("- " + NON_PLANAR));
    }
    
    /**
     * Getter for property atomList.
     * @return Value of property atomList.
     */
    public ArrayList<Integer> getAtomList() {
        return new ArrayList<Integer>(ringAtoms);
    }
    
    /**
     * Intersection (a set operation) of one ring and other.
     * The following procedure is followed: <br>
     * <ol>
     * <li> Check if AtomGroup is of Ring type </li>
     * <li> Check size, ensure i am bigger ! </li>
     * <li> Apply intersection </li>
     * <li> Add back first and last elements of <code>atomGroup</code> </li>
     * </ol>
     *
     * @param atomGroup - with what we should intersect
     * @return true, if operation was a success else false
     */
    public boolean intersect(AtomGroup atomGroup) {
        if (atomGroup instanceof Ring) {
            Ring r2 = (Ring) atomGroup;
            
            if (ringAtoms.size() > r2.ringAtoms.size()) {
                if (ringAtoms.containsAll(r2.ringAtoms)) {
                    ringAtoms.removeAll(r2.ringAtoms);
                    
                    Object [] atoms = r2.ringAtoms.toArray();
                    ringAtoms.add((Integer) atoms[0]);
                    ringAtoms.add((Integer) atoms[atoms.length - 1]);
                    
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
    public boolean union(AtomGroup atomGroup) {
        if (atomGroup instanceof Ring) {
            ringAtoms.addAll(((Ring) atomGroup).ringAtoms);
            
            return true;
        } // end if
        
        return false;
    }
    
    /**
     * Getter for property planar.
     * @return Value of property planar.
     */
    public boolean isPlanar() {
        return this.planar;
    }
    
    /**
     * Setter for property planar.
     * @param planar New value of property planar.
     */
    public void setPlanar(boolean planar) {
        this.planar = planar;
    }
    
    /**
     * get a string description of this ring type
     *
     * @return a string representing the type for ring
     */
    public String getRingType() {
        return (isPlanar() ? PLANAR : NON_PLANAR);
    }
    
    /**
     * get a string description of this ring type
     *
     * @param type a string representing the type for ring
     */
    public void setRingType(String type) {
        if (type.equalsIgnoreCase(PLANAR)) {
           setPlanar(true);
        } else if (type.equalsIgnoreCase(NON_PLANAR)) {
           setPlanar(false);
        } else {
           throw new UnsupportedOperationException("Unknown ring type " + type);
        } // end if
    }
    
    /**
     * checks if two rings are equal
     */
    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof Ring))) return false;
        
        return ((ringAtoms.equals(((Ring) obj).ringAtoms))
                && (planar == ((Ring) obj).planar));
    }
    
    /**
     * overridden finalize
     */
    protected void finalize() throws Throwable {
        ringAtoms = null;
        
        super.finalize();
    }
    
} // end of class Ring
