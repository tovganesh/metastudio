/*
 * Fragment.java
 *
 * Created on May 15, 2004, 6:43 AM
 */

package org.meta.fragment;

import java.util.Iterator;
import org.meta.fragmentor.FragmentationScheme;

import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * Represents the molecular fragment, as a list of FragmentAtom objects.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class Fragment {

    protected Molecule parentMolecule;
    
    /** Creates a new instance of Fragment */
    public Fragment() {
    }
    
    /**
     * Add a fragment atom to this fragment 
     *
     * @param fragmentAtom the fragment atom to be added to this fragment
     */
    public abstract void addFragmentAtom(FragmentAtom fragmentAtom);
    
    /**
     * Remove a fragment atom to this fragment 
     *
     * @param fragmentAtom the fragment atom to be removed from this fragment
     */
    public abstract void removeFragmentAtom(FragmentAtom fragmentAtom);
    
    /** 
     * remove a fragment atom from this Fragment at a given index
     *
     * @param atomIndex the atom index to be removed
     */    
    public abstract void removeFragmentAtomAt(int atomIndex);
    
    /** 
     * Method to get index of a particular atom in this fragment
     *
     * @return index of the atom in this fragment, else -1
     */    
    public abstract int getIndexOfAtom(Atom atom);
    
    /** 
     * Method to get a particular fragment atom from the lists of fragment atoms
     *
     * @return FragmentAtom the instance of FragmentAtom class
     */    
    public abstract FragmentAtom getFragmentAtom(int atomIndex);
    
    /** 
     * Method to get the list of fragment atoms in this fragment.
     *
     * @return an iterator object containing a linear list of fragment atoms in 
     *         the Fragment!
     */    
    public abstract Iterator<FragmentAtom> getFragmentAtoms();
    
    /**
     * Check for the presence of fragmentAtom in this fragment.
     *
     * @param fragmentAtom the fragmentAtom whose presence is to be checked
     * @return true if the fragmentAtom is present in this fragment else false
     */
    public abstract boolean contains(FragmentAtom fragmentAtom);
    
    /**
     * Check for the presence of atom in this fragment.
     *
     * @param atom the atom whose presence is to be checked
     * @return true if the atom is present in this fragment else false
     */
    public abstract boolean contains(Atom atom);
    
    /**
     * Returns the total number of atoms in this fragment 
     * (including dummy atoms).
     *
     * @return total number of atoms in this fragment
     */
    public abstract int getTotalNumberOfAtoms();
    
    /**
     * Returns the number of dummy atoms in this fragment.
     *
     * @return number of dummy atoms
     */
    public abstract int getNumberOfDummyAtoms();
    
    /**
     * Get a list of all the dummy atoms in this fragment.
     *
     * @return Iterator of all the dummy atoms 
     */
    public abstract Iterator<FragmentAtom> getDummyAtoms();
    
    /**
     * Returns the number of boundary atoms in this fragment.
     *
     * @return number of boundary atoms
     */
    public abstract int getNumberOfBoundaryAtoms();
    
    /**
     * Get a list of all the boundary atoms in this fragment.
     *
     * @return Iterator of all the boundary atoms 
     */
    public abstract Iterator<FragmentAtom> getBoundaryAtoms();
    
    /**
     * Returns the number of common atoms between this and the fragment 
     * represented by the paramenter. Dummy atoms should not be counted for
     * this purpose.
     *
     * @param fragment the other fragment with which this fragment need to be 
     *        compared.
     * @return int the number of atoms common in both the fragments.
     */
    public abstract int numberOfCommonAtoms(Fragment fragment);
    
    /**
     * Returns a Fragment object that is union of this and parameter fragment.
     * <br>
     * Note: Union only applies to the main fragment atoms, and not the
     *       boundary and dummy atoms. Thus the result of union will
     *       be a Fragment object that has empty boundary and dummy atom list.     
     *
     * @param fragment the Fragment object with which the union operation is 
     *        to be performed.
     * @return a new Fragment object object instance, which is union of both the
     *         fragments
     */
    public abstract Fragment union(Fragment fragment);
    
    /**
     * Returns a Fragment object that is intersection of this and 
     * parameter fragment.
     * <br>
     * Note: Intersection only applies to the main fragment atoms, and not the
     *       boundary and dummy atoms. Thus the result of intersection will
     *       be a Fragment object that has empty boundary and dummy atom list.
     *
     * @param fragment the Fragment object with which the intersection operation
     *         is to be performed.
     * @return a new Fragment object object instance, which is intersection of 
     *         both the fragments
     */
    public abstract Fragment intersection(Fragment fragment);

    /**
     * Returns a Fragment object that is this '-' parameter fragment.
     * <br>
     * Note: Substraction only applies to the main fragment atoms, and not the
     *       boundary and dummy atoms. Thus the result of intersection will
     *       be a Fragment object that has empty boundary and dummy atom list.
     *
     * @param fragment the Fragment object with which the substraction operation
     *         is to be performed.
     * @return a new Fragment object object instance, which is this '-' 
     *         parameter fragment.
     */
    public abstract Fragment substract(Fragment fragment);
    
    /**
     * Check if a fragment is a subset of this Fragment object
     *
     * @param fragment the Fragment to be checked for subset relation
     * @return a boolean true if it is a subset or else false.
     */
    public abstract boolean isSubset(Fragment fragment);
    
    /**
     * Identify boundary atoms and add them to the fragment list
     */
    public abstract void identfyBoundaryAtoms();
    
    /**
     * Add dummy atoms to each fragment for this scheme. 
     *
     * @param dummyAtom The type of dummy atoms to be added to the fragments          
     * @param position The position at which the dummy atom is to be placed
     */
    public abstract void addDummyAtoms(Atom dummyAtom, 
                              FragmentationScheme.DummyAtomPosition position);
    
    /**
     * Compute Charges on the fragments      
     */
    public abstract void computeCharges();
    
    /**
     * Sets the parent molecule of this fragment
     *
     * @param parentMolecule the parent molecule of this fragment
     */
    public void setParentMolecule(Molecule parentMolecule) {
        this.parentMolecule = parentMolecule;
    }
    
    /**
     * Returns the parent molecule of this fragment
     *
     * @return parent molecule of this fragment
     */
    public Molecule getParentMolecule() {
        return this.parentMolecule;
    }

    /**
     * Holds value of property overlapFragment.
     */
    protected boolean overlapFragment;

    /**
     * Getter for property overlapFragment.
     * @return Value of property overlapFragment.
     */
    public boolean isOverlapFragment() {
        return this.overlapFragment;
    }

    /**
     * Setter for property overlapFragment.
     * @param overlapFragment New value of property overlapFragment.
     */
    public void setOverlapFragment(boolean overlapFragment) {
        this.overlapFragment = overlapFragment;
    }

    /**
     * Holds value of property cardinalitySign.
     */
    protected int cardinalitySign;

    /**
     * Getter for property cardinalitySign.
     * @return Value of property cardinalitySign.
     */
    public int getCardinalitySign() {
        return this.cardinalitySign;
    }

    /**
     * Setter for property cardinalitySign.
     * @param cardinalitySign New value of property cardinalitySign.
     */
    public void setCardinalitySign(int cardinalitySign) {
        this.cardinalitySign = cardinalitySign;
    }

    /**
     * Holds value of property totalCharge.
     */
    protected int totalCharge;

    /**
     * Getter for property totalCharge.
     * @return Value of property totalCharge.
     */
    public int getTotalCharge() {
        return this.totalCharge;
    }

    /**
     * Setter for property totalCharge.
     * @param totalCharge New value of property totalCharge.
     */
    public void setTotalCharge(int totalCharge) {
        this.totalCharge = totalCharge;
    }
} // end of class Fragment
