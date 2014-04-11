/*
 * DefaultFragmentationScheme.java
 *
 * Created on January 16, 2005, 6:25 PM
 */

package org.meta.fragmentor.impl;

import java.util.*;
import org.meta.fragment.Fragment;
import org.meta.fragmentor.FragmentCorrector;
import org.meta.fragmentor.FragmentGoodnessProbe;
import org.meta.fragmentor.FragmentationException;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.fragmentor.GoodnessProbeException;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * Defines a very simple manual fragmentation scheme, with no attempt 
 * being made to automatically generate fragments of any kind.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DefaultFragmentationScheme extends FragmentationScheme {
    
    private ArrayList<FragmentCorrector> fragmentCorrectors;
    private ArrayList<FragmentGoodnessProbe> fragmentGoodnessProbes;
    
    /** Creates a new instance of DefaultFragmentationScheme */
    public DefaultFragmentationScheme() throws ClassNotFoundException,
                                        InstantiationException,
                                        IllegalAccessException {
        super();                
        
        fragmentCorrectors = new ArrayList<FragmentCorrector>();
        fragmentGoodnessProbes = new ArrayList<FragmentGoodnessProbe>();
        
        this.name = "defaultScheme";
    }

    /**
     * Add a goodness probe to this scheme
     *
     * @param fragmentGoodnessProbe the new goodness probe
     */    
    @Override
    public void addFragmentGoodnessProbe(
                           FragmentGoodnessProbe fragmentGoodnessProbe) {
        fragmentGoodnessProbes.add(fragmentGoodnessProbe);
        
        System.out.println("Added probe");
        System.out.println(fragmentGoodnessProbes.iterator().next());
    }
    
    /**
     * Remove a goodness probe from this scheme
     *
     * @param fragmentGoodnessProbe the goodness probe to be removed
     */ 
    @Override
    public void removeFragmentGoodnessProbe(
                               FragmentGoodnessProbe fragmentGoodnessProbe) {
        fragmentGoodnessProbes.remove(fragmentGoodnessProbe);
    }
    
    /**
     * Get a list of all fragment goodness probes.
     *
     * @return Iterator of fragmentGoodnessProbe 's 
     */
    @Override
    public Iterator<FragmentGoodnessProbe> getFragmentGoodnessProbes() {
        return fragmentGoodnessProbes.iterator();
    }
    
    /**
     * Add a fragment corrector to this scheme
     *
     * @param fragmentCorrector the new corrector to be added to this scheme
     */
    @Override
    public void addFragmentCorrector(
                           FragmentCorrector fragmentCorrector) {
        fragmentCorrectors.add(fragmentCorrector);
    }
    
    /**
     * Remove a fragment corrector from this scheme
     *
     * @param fragmentCorrector the corrector to be removed from this scheme
     */
    @Override
    public void removeFragmentCorrector(
                              FragmentCorrector fragmentCorrector) {
        fragmentCorrectors.remove(fragmentCorrector);
    }
    
    /**
     * Get a list of all fragment correctors.
     *
     * @return Iterator of fragmentCorrector 's 
     */
    @Override
    public Iterator<FragmentCorrector> getFragmentCorrectors() {
        return fragmentCorrectors.iterator();
    }
    
    /**
     * A automated fragmenation scheme should implement this method to generate
     * the fragments automatically, else should quitely return (and should not
     * throw an exception).
     *
     * @param molecule the molecule which is to be fragmented
     * @throws FragmentationException if unable to fragment.
     */
    @Override
    public void doAutomatedFragmentation(Molecule molecule)
                                    throws FragmentationException {
        throw new FragmentationException("This class does not support " +
                                         "automatic fragmentation!");
    }
    
    /**
     * All schemes, automated or otherwise should run the goodness probes
     * associated with this scheme for each fragment present in the fragment
     * list of this scheme.
     *
     * @throws GoodnessProbeException if unable to run all the goodness probes
     */
    @Override
    public void runGoodnessProbes() throws GoodnessProbeException {              
        for(FragmentGoodnessProbe probe : fragmentGoodnessProbes) {
            probe.setFragmentationScheme(this);
            probe.runGoodnessProbe();
        } // end for
    }

    /**
     * Identify boundary atoms and add them to the fragment list
     */
    @Override
    public void identfyBoundaryAtoms() {
        Iterator<Fragment> fragments = fragmentList.getFragments();
        
        while(fragments.hasNext()) {
            fragments.next().identfyBoundaryAtoms();             
        } // end while
    }
    
    /**
     * Add dummy atoms to each fragment for this scheme. 
     *
     * @param dummyAtom The type of dummy atoms to be added to the fragments          
     * @param position The position at which the dummy atom is to be placed
     */
    @Override
    public void addDummyAtoms(Atom dummyAtom, 
                              DummyAtomPosition position) {
        // first identify boundary atoms
        identfyBoundaryAtoms();
        
        Iterator<Fragment> fragments = fragmentList.getFragments();
       
        while(fragments.hasNext()) {
            fragments.next().addDummyAtoms(dummyAtom, position);            
        } // end while
    }
    
    /**
     * Compute Charges on the fragments      
     */
    @Override
    public void computeCharges() {
        Iterator<Fragment> fragments = fragmentList.getFragments();
        
        while(fragments.hasNext()) {
            fragments.next().computeCharges();            
        } // end while
    }
    
    /**
     * overloaded toString()
     */
    @Override
    public String toString() {
        return "The Default manual fragmentation scheme";
    }        
} // end of class DefaultFragmentationScheme
