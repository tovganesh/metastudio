/*
 * AtomGroupListImpl.java
 *
 * Created on May 26, 2004, 6:52 AM
 */

package org.meta.molecule.impl;

import java.util.*;
import org.meta.molecule.AtomGroup;
import org.meta.molecule.AtomGroupList;

/**
 * Implementation of atom group list.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AtomGroupListImpl implements AtomGroupList {
    
    /** the actual list */
    private ArrayList<AtomGroup> groupList;
    
    /** Creates a new instance of AtomGroupListImpl */
    public AtomGroupListImpl() {
        groupList = new ArrayList<AtomGroup>(10);
    }        
    
    /**
     * get the size of this list
     *
     * @return the size of this list
     */
    @Override
    public int getSize() {
        return groupList.size();
    }
    
    /**
     * Add a new atom group.
     *
     * @param atomGroup to be added to the group list
     */
    @Override
    public void addGroup(AtomGroup atomGroup) {
        if (!groupList.contains(atomGroup)) groupList.add(atomGroup);
    }
    
    /**
     * Removes an atom group from the current list.
     *
     * @param atomGroup to be removes from the group list
     */
    @Override
    public void removeGroup(AtomGroup atomGroup) {
        groupList.remove(atomGroup);
    }        
    
    /**
     * get the list of all group as an iterator object
     *
     * @return Iterator object containing instance of AtomGroup
     */
    @Override
    public Iterator<AtomGroup> getGroups() {
        return groupList.iterator();
    }        
    
    /**
     * Getter for property atomList.
     * @return Value of property atomList.
     */
    @Override
    public ArrayList<AtomGroup> getGroupList() {
        return groupList;
    }
    
    /**
     * overridden toString()
     */
    @Override
    public String toString() {
        return groupList.toString();
    }
} // end of class AtomGroupListImpl
