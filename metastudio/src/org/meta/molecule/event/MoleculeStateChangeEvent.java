/*
 * MoleculeStateChangeEvent.java
 *
 * Created on February 1, 2004, 6:56 AM
 */

package org.meta.molecule.event;

import org.meta.fragment.event.FragmentListChangeEvent;
import org.meta.molecule.Atom;
import org.meta.molecule.SpecialStructureRecognizer;

/**
 * An event used to indicate a change in molecule state.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeStateChangeEvent extends java.util.EventObject {
    
    /** event types ... */
    
    /** atom added event */
    public static final int ATOM_ADDED    = 1;
    
    /** atom removed event */
    public static final int ATOM_REMOVED  = 2;
    
    /** bond removed event */
    public static final int BOND_REMOVED  = 3;
    
    /** bond modified event */
    public static final int BOND_MODIFIED = 4;
    
    /** special structure addition event */
    public static final int STRUCTURE_ADDED = 5;
    
    /** special structure deletion event */
    public static final int STRUCTURE_REMOVED = 6;
    
    /** title of molecule was modified */
    public static final int TITLE_MODIFIED = 7;
    
    /** event related to fragment modifications */
    public static final int FRAGMENTS_MODIFIED = 8;

    /** event to just say that refresh with the current state of molecule */
    public static final int REFRESH_EVENT = 9;
    
    /** there was a major modification in the molecule structure */
    public static final int MAJOR_MODIFICATION = 100;
    
    /** Holds value of property eventType. */
    private int eventType;
    
    /** Holds value of property atom1. */
    private Atom atom1;
    
    /** Holds value of property atom2. */
    private Atom atom2;
    
    /** Holds value of property specialStructureRecognizer. */
    private SpecialStructureRecognizer specialStructureRecognizer;
    
    /** Creates a new instance of MoleculeStateChangeEvent */
    public MoleculeStateChangeEvent(Object source) {
        super(source);
        
        eventType = MAJOR_MODIFICATION;
        
        fragmentListChangeEvent = new FragmentListChangeEvent(source);
    }
    
    /** Getter for property eventType.
     * @return Value of property eventType.
     *
     */
    public int getEventType() {
        return this.eventType;
    }
    
    /** Setter for property eventType.
     * @param eventType New value of property eventType.
     *
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
    
    /** Getter for property atom1.
     * @return Value of property atom1.
     *
     */
    public Atom getAtom1() {
        return this.atom1;
    }
    
    /** Setter for property atom1.
     * @param atom1 New value of property atom1.
     *
     */
    public void setAtom1(Atom atom1) {
        this.atom1 = atom1;
    }
    
    /** Getter for property atom2. Relevent only when a bond is modified or
     * removed.
     * @return Value of property atom2.
     *
     */
    public Atom getAtom2() {
        return this.atom2;
    }
    
    /** Setter for property atom2. Relevent only when a bond is modified or
     * removed.
     * @param atom2 New value of property atom2.
     *
     */
    public void setAtom2(Atom atom2) {
        this.atom2 = atom2;
    }
    
    /** Getter for property specialStructureRecognizer. 
     * Only relevent if event of type STRUCTURE_REMOVED or STRUCTURE_ADDED.
     * @return Value of property specialStructureRecognizer. 
     * 
     */
    public SpecialStructureRecognizer getSpecialStructureRecognizer() {
        return this.specialStructureRecognizer;
    }
    
    /** Setter for property specialStructureRecognizer.
     * Only relevent if event of type STRUCTURE_REMOVED or STRUCTURE_ADDED.
     * @param specialStructureRecognizer New value of property 
     *        specialStructureRecognizer. 
     */
    public void setSpecialStructureRecognizer(SpecialStructureRecognizer 
                                              specialStructureRecognizer) {
        this.specialStructureRecognizer = specialStructureRecognizer;
    }
    
    /**
     * overridden toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        switch(eventType) {
            case ATOM_ADDED:
                sb.append("Atom added : " + atom1);
                break;
            case ATOM_REMOVED:
                sb.append("Atom removed : " + atom1);
                break;
            case BOND_REMOVED:
                sb.append("Bond between : (" + atom1 + ", " + atom2 
                          + ") removed");
                break;
            case BOND_MODIFIED:
                sb.append("Bond between : (" + atom1 + ", " + atom2 
                          + ") modified");
                break;
            case FRAGMENTS_MODIFIED:
                sb.append("Fragment changed: " 
                          + fragmentListChangeEvent.toString());
                break;
            default:
                sb.append("General molecule state modification event");
                break;
        } // end switch..case
        
        return sb.toString();
    }

    /**
     * Holds value of property fragmentListChangeEvent.
     */
    private FragmentListChangeEvent fragmentListChangeEvent;

    /**
     * Getter for property fragmentListChangeEvent.
     * @return Value of property fragmentListChangeEvent.
     */
    public FragmentListChangeEvent getFragmentListChangeEvent() {
        return this.fragmentListChangeEvent;
    }

    /**
     * Setter for property fragmentListChangeEvent.
     * @param fragmentListChangeEvent New value of property 
     *        fragmentListChangeEvent.
     */
    public void setFragmentListChangeEvent(
                   FragmentListChangeEvent fragmentListChangeEvent) {
        this.fragmentListChangeEvent = fragmentListChangeEvent;
    }
    
} // end of class MoleculeStateChangeEvent
