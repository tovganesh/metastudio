/*
 * FragmentAtomEditEvent.java
 *
 * Created on August 18, 2007, 10:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.molecule.Molecule;

/**
 * An edit even for a fragment atom.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentAtomEditEvent extends java.util.EventObject {
    
    /** Creates a new instance of FragmentAtomEditEvent */
    public FragmentAtomEditEvent(Object source) {
        super(source);
    }

    /**
     * Holds value of property molecule.
     */
    private Molecule molecule;

    /**
     * Getter for property molecule.
     * @return Value of property molecule.
     */
    public Molecule getMolecule() {
        return this.molecule;
    }

    /**
     * Setter for property molecule.
     * @param molecule New value of property molecule.
     */
    public void setMolecule(Molecule molecule) {
        this.molecule = molecule;
    }

    /**
     * Holds value of property fragmentationScheme.
     */
    private FragmentationScheme fragmentationScheme;

    /**
     * Getter for property fragmentationScheme.
     * @return Value of property fragmentationScheme.
     */
    public FragmentationScheme getFragmentationScheme() {
        return this.fragmentationScheme;
    }

    /**
     * Setter for property fragmentationScheme.
     * @param fragmentationScheme New value of property fragmentationScheme.
     */
    public void setFragmentationScheme(
                                FragmentationScheme fragmentationScheme) {
        this.fragmentationScheme = fragmentationScheme;
    }

    /**
     * Holds value of property fragment.
     */
    private Fragment fragment;

    /**
     * Getter for property fragment.
     * @return Value of property fragment.
     */
    public Fragment getFragment() {
        return this.fragment;
    }

    /**
     * Setter for property fragment.
     * @param fragment New value of property fragment.
     */
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Holds value of property fragmentAtom.
     */
    private FragmentAtom fragmentAtom;

    /**
     * Getter for property fragmentAtom.
     * @return Value of property fragmentAtom.
     */
    public FragmentAtom getFragmentAtom() {
        return this.fragmentAtom;
    }

    /**
     * Setter for property fragmentAtom.
     * @param fragmentAtom New value of property fragmentAtom.
     */
    public void setFragmentAtom(FragmentAtom fragmentAtom) {
        this.fragmentAtom = fragmentAtom;
    }

    /** Type of fragment atom edit event */
    public enum FragmentAtomEditEventType {
        ATOM_ADDED, ATOM_REMOVED
    }

    /**
     * Holds value of property eventType.
     */
    private FragmentAtomEditEventType eventType;

    /**
     * Getter for property eventType.
     * @return Value of property eventType.
     */
    public FragmentAtomEditEventType getEventType() {
        return this.eventType;
    }

    /**
     * Setter for property eventType.
     * @param eventType New value of property eventType.
     */
    public void setEventType(FragmentAtomEditEventType eventType) {
        this.eventType = eventType;
    }
    
} // end of class FragmentAtomEditEvent
