/*
 * FragmentationScheme.java
 *
 * Created on August 29, 2004, 7:14 PM
 */

package org.meta.fragmentor;


import org.meta.common.Utility;
import org.meta.fragment.FragmentList;

import java.util.Iterator;
import org.meta.fragment.Fragment;
import org.meta.fragment.event.FragmentListChangeListener;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * Defines the interfaces and encapsulates the functionality of a fragmentation
 * scheme for a given Molecule object.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class FragmentationScheme {
    
    /**
     * Holds value of property fragmentList.
     */
    protected FragmentList fragmentList;
    
    /**
     * Holds value of property name.
     */
    protected String name;
    
    /** Creates a new instance of FragmentationScheme */
    public FragmentationScheme() throws ClassNotFoundException,
                                        InstantiationException,
                                        IllegalAccessException { 
        fragmentList = (FragmentList) 
                Utility.getDefaultImplFor(FragmentList.class).newInstance();
        fragmentList.setParentFragmentationScheme(this);
        
        name = "default";
    }
    
    /**
     * Add a goodness probe to this scheme
     *
     * @param fragmentGoodnessProbe the new goodness probe
     */    
    public abstract void addFragmentGoodnessProbe(
                               FragmentGoodnessProbe fragmentGoodnessProbe);
    
    /**
     * Remove a goodness probe from this scheme
     *
     * @param fragmentGoodnessProbe the goodness probe to be removed
     */ 
    public abstract void removeFragmentGoodnessProbe(
                               FragmentGoodnessProbe fragmentGoodnessProbe);
    
    /**
     * Get a list of all fragment goodness probes.
     *
     * @return Iterator of fragmentGoodnessProbe 's 
     */
    public abstract Iterator<FragmentGoodnessProbe> getFragmentGoodnessProbes(); 
    
    /**
     * Add a fragment corrector to this scheme
     *
     * @param fragmentCorrector the new corrector to be added to this scheme
     */
    public abstract void addFragmentCorrector(
                                    FragmentCorrector fragmentCorrector);
    
    /**
     * Remove a fragment corrector from this scheme
     *
     * @param fragmentCorrector the corrector to be removed from this scheme
     */
    public abstract void removeFragmentCorrector(
                                    FragmentCorrector fragmentCorrector);
    
    /**
     * Get a list of all fragment correctors.
     *
     * @return Iterator of fragmentCorrector 's 
     */
    public abstract Iterator<FragmentCorrector> getFragmentCorrectors(); 
    
    /**
     * A automated fragmenation scheme should implement this method to generate
     * the fragments automatically, else should quitely return (and should not
     * throw an exception).
     *
     * @param molecule the molecule which is to be fragmented
     * @throws FragmentationException if unable to fragment.
     */
    public abstract void doAutomatedFragmentation(Molecule molecule)
                                    throws FragmentationException;
    
    /**
     * All schemes, automated or otherwise should run the goodness probes
     * associated with this scheme for each fragment present in the fragment
     * list of this scheme.
     *
     * @throws GoodnessProbeException if unable to run all the goodness probes
     */
    public abstract void runGoodnessProbes() throws GoodnessProbeException;
    
    /**
     * overloaded toString()
     */
    @Override
    public String toString() {
        return "The Default fragmentation scheme";
    }
    
    /**
     * Getter for property fragmentList.
     * @return Value of property fragmentList.
     */
    public FragmentList getFragmentList() {
        return this.fragmentList;
    }
    
    /**
     * Setter for property fragmentList.
     * @param fragmentList New value of property fragmentList.
     */
    public void setFragmentList(FragmentList fragmentList) {
        this.fragmentList = fragmentList;
    }   
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }   
    
    /**
     * Registers FragmentListChangeListener to receive events. 
     * (forwards call to FragmentList object)
     * @param listener The listener to register.
     */
    public synchronized void addFragmentListChangeListener(
                                        FragmentListChangeListener listener) {
        fragmentList.addFragmentListChangeListener(listener);
    }

    /**
     * Removes FragmentListChangeListener from the list of listeners.
     * (forwards call to FragmentList object)
     * @param listener The listener to remove.
     */
    public synchronized void removeFragmentListChangeListener(
                                          FragmentListChangeListener listener) {
        fragmentList.removeFragmentListChangeListener(listener);
    }

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
                                       DummyAtomPosition position);   
    
    /**
     * Compute Charges on the fragments      
     */
    public abstract void computeCharges();
    
    /**
     * Try to merge this ovelap fragment into previous overlap fragments
     */
    protected boolean mergeIntoPreviousExpression(Fragment f, int sign, int n) {
        int nn = fragmentList.size();
        
        for(int i=0; i<nn; i++) {
            Fragment a = fragmentList.getFragment(i);
            
            if (!a.isOverlapFragment()) continue;
            
            if (a.equals(f)) {
               a.setCardinalitySign(a.getCardinalitySign() + sign);
               return true;
            } // end if
        } // end for
        
        return false;
    }
    
    /**
     * Compute Intersection of 2 or more fragments and return true if
     * the intersection was non zero, else false.
     * 
     * @param combs a list of fragments to check for intersection
     * @param len the length of actual list of fragments to check for
     * @param sign the sign of the current term
     * @param n the number of main fragments
     * @return true: non zero intersection, else false
     */
    protected boolean computeIntersectionsAndAdd(int[] combs, int len, 
                                                 int sign, int n) {
        Fragment a = fragmentList.getFragment(combs[0]);
        
        for(int i=1; i<=len; i++) {
            a = a.intersection(fragmentList.getFragment(combs[i]));
            
            if (a.getTotalNumberOfAtoms() == 0) return false;
        } // end for
        
        if (!mergeIntoPreviousExpression(a, sign, n)) {
            a.setOverlapFragment(true);
            a.setCardinalitySign(sign);
            
            fragmentList.addFragment(a);            
        } // end if
        
        return true;
    }
    
    /**
     * Return the sign of the term in a cardinality expression.
     *
     * @param m the number of terms (-1) in the expression
     * @return the sign of the this term
     */
    protected int getSignOfTerm(int m) {
        m++;
        
        if (m%2 == 0) return -1;
        else          return  1;
    }
    
    /**
     * Generate cardinality fragments based on the following reference:
     * 
     * V.Ganesh <i>et al. </i> J. Chem. Phys., <b>125</b>, 104109 (2006).
     */
    public void generateCardinalityFragments() {      
        Thread cardinalityThread = new Thread() {
            @Override
            public void run() {
                fragmentList.disableListiners();
                
                // first remove all overlap fragments
                fragmentList.removeOverlapFragments();
                
                // then proceed with all combinations, lexicographical generator
                int [] combs = new int[fragmentList.size()];
                int i, j, k, l, pos, m, n;

                n = combs.length;

                for(i=0; i<n; i++) {
                    combs[i] = -1;
                } // end for

                for(i=0; i<n; i++) {
                    for(j=i+1; j<n; j++) {
                        combs[0] = i; combs[1] = j;

                        //////
                        if (!computeIntersectionsAndAdd(combs, 1, 
                                                 getSignOfTerm(1), n)) continue;
                        //////

                        if ((n <= 2) || (combs[1] == n-1)) continue;

                        l=1; pos=1; m=pos+1;

                        while(true) {
                            for(k=combs[pos]+1; k<n; k++) {
                                combs[m] = k;
                                
                                if (m > 0) {
                                    if (combs[m] <= combs[m-1]) break;
                                } // end if

                                //////                                           
                                if (!computeIntersectionsAndAdd(combs, m,
                                                         getSignOfTerm(m), n)) {
                                    if (combs[m] == n-1) break;
                                    m--;
                                } // end  if
                                //////

                                m++;
                            } // end for

                            pos=n-l; m=pos;

                            if (combs[pos] == n-1) l++;
                            else                   l=1;                            

                            if (pos==1) break;
                            
                        } // end while
                    } // end for
                } // end for  
                
                fragmentList.enableListiners();
                
                // finally remove the overlap fragments that have 'zero' sign
                fragmentList.purnZeroFragments();
            }
        };
        
        // start cardinality generation
        cardinalityThread.setName("Cardinality Thread");
        cardinalityThread.start();
    }

    
    /**
     * Constants to specify dummy atom position
     */
    public enum DummyAtomPosition {
        STD_BOND_DISTANCE, AT_CUT_POSITION
    }
    
} // end of class FragmentationScheme
