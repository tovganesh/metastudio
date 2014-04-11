/*
 * FragmentGoodnessProbe.java
 *
 * Created on August 29, 2004, 7:08 PM
 */

package org.meta.fragmentor;

import java.util.Iterator;
import org.meta.fragment.Fragment;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * Defines the interfaces for how a fragment goodness probe should function.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class FragmentGoodnessProbe {
    
    /**
     * Holds value of property fragmentationScheme.
     */
    protected FragmentationScheme fragmentationScheme;
    
    /**
     * Holds value of property molecule.
     */
    protected Molecule molecule;
    
    /** Creates a new instance of FragmentGoodnessProbe */
    public FragmentGoodnessProbe(FragmentationScheme fragmentationScheme,
                                 Molecule molecule) {
        this.fragmentationScheme = fragmentationScheme;
        this.molecule = molecule;
        
        overallMinimumGoodnessIndex = new FragmentAtomGoodness(null, 0.0);
        overallMaximumGoodnessIndex = new FragmentAtomGoodness(null, 0.0);
    }
    
    /**
     * Run the goodness probe for the said fragmentation scheme.
     */
    public abstract void runGoodnessProbe();
    
    /**
     * Get the best fragment for given set of global atom indices.
     * 
     * @param atomIndex1 the first global atom index
     * @param atomIndex2 the second global atom index
     * @return Fragment the fragment in which this atom is best represented;
     *                  if it is not represented any where, a <code>null</code>
     *                  is returned.
     */
    public abstract Fragment getBestFragmentFor(int atomIndex1, int atomIndex2);
    
    /**
     * Get the best goodess index for given set of global atom indices.
     * 
     * @param atomIndex1 the first global atom index
     * @param atomIndex2 the second global atom index
     * @return FragmentAtomGoodness the best goodness of an atom pair
     */
    public abstract FragmentAtomGoodness getBestGoodnessIndex(int atomIndex1, 
                                                              int atomIndex2);
    
    /**
     * Get all the goodess indices for given set of global atom indices.
     * 
     * @param atomIndex1 the first global atom index
     * @param atomIndex2 the second global atom index
     * @return Iterator of FragmentAtomGoodness for all representations of this
     *                 atom pair in all the fragments in the said scheme
     */
    public abstract Iterator<FragmentAtomGoodness> getGoodnessIndices(
                                                              int atomIndex1, 
                                                              int atomIndex2);
    
    /**
     * overridden toString()
     */
    public String toString() {
        return "Default goodness probe.";
    }        
    
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
    public void setFragmentationScheme(FragmentationScheme fragmentationScheme){
        this.fragmentationScheme = fragmentationScheme;
    }
    
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
     * Holds value of property overallMinimumGoodnessIndex.
     */
    protected FragmentAtomGoodness overallMinimumGoodnessIndex;

    /**
     * Getter for property overallMinimumGoodnessIndex.
     * @return Value of property overallMinimumGoodnessIndex.
     */
    public FragmentAtomGoodness getOverallMinimumGoodnessIndex() {
        return this.overallMinimumGoodnessIndex;
    }

    /**
     * Setter for property overallMinimumGoodnessIndex.
     * @param overallMinimumGoodnessIndex New value of property 
     *                                    overallMinimumGoodnessIndex.
     */
    public void setOverallMinimumGoodnessIndex(
                          FragmentAtomGoodness overallMinimumGoodnessIndex) {
        this.overallMinimumGoodnessIndex = overallMinimumGoodnessIndex;
    }

    /**
     * Holds value of property overallMaximumGoodnessIndex.
     */
    protected FragmentAtomGoodness overallMaximumGoodnessIndex;

    /**
     * Getter for property overallMaximumGoodnessIndex.
     * @return Value of property overallMaximumGoodnessIndex.
     */
    public FragmentAtomGoodness getOverallMaximumGoodnessIndex() {
        return this.overallMaximumGoodnessIndex;
    }

    /**
     * Setter for property overallMaximumGoodnessIndex.
     * @param overallMaximumGoodnessIndex New value of property 
     *                                    overallMaximumGoodnessIndex.
     */
    public void setOverallMaximumGoodnessIndex(
                          FragmentAtomGoodness overallMaximumGoodnessIndex) {
        this.overallMaximumGoodnessIndex = overallMaximumGoodnessIndex;
    }

    /**
     * Holds value of property overallMinimumGoodnessAtom.
     */
    protected Atom overallMinimumGoodnessAtom;

    /**
     * Getter for property overallMinimumGoodnessAtom.
     * @return Value of property overallMinimumGoodnessAtom.
     */
    public Atom getOverallMinimumGoodnessAtom() {
        return this.overallMinimumGoodnessAtom;
    }

    /**
     * Setter for property overallMinimumGoodnessAtom.
     * @param overallMinimumGoodnessAtom New value of property 
     *        overallMinimumGoodnessAtom.
     */
    public void setOverallMinimumGoodnessAtom(Atom overallMinimumGoodnessAtom) {
        this.overallMinimumGoodnessAtom = overallMinimumGoodnessAtom;
    }

    /**
     * Holds value of property overallMaximumGoodnessAtom.
     */
    protected Atom overallMaximumGoodnessAtom;

    /**
     * Getter for property overallMaximumGoodnessAtom.
     * @return Value of property overallMaximumGoodnessAtom.
     */
    public Atom getOverallMaximumGoodnessAtom() {
        return this.overallMaximumGoodnessAtom;
    }

    /**
     * Setter for property overallMaximumGoodnessAtom.
     * @param overallMaximumGoodnessAtom New value of property 
     *        overallMaximumGoodnessAtom.
     */
    public void setOverallMaximumGoodnessAtom(Atom overallMaximumGoodnessAtom) {
        this.overallMaximumGoodnessAtom = overallMaximumGoodnessAtom;
    }
} // end of class FragmentGoodnessProbe
