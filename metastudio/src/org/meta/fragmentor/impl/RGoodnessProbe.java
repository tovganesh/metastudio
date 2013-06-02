/*
 * RGoodnessProbe.java
 *
 * Created on March 26, 2005, 7:17 PM
 */

package org.meta.fragmentor.impl;

import java.util.*;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragmentor.FragmentAtomGoodness;
import org.meta.fragmentor.FragmentGoodnessProbe;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * The default "<i>r-goodness</i>" probe.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RGoodnessProbe extends FragmentGoodnessProbe {
    
    /** The goodness map DS */
    private ArrayList<HashMap<Integer, 
                              ArrayList<FragmentAtomGoodness>>> goodnessMap;
    
    /** Creates a new instance of RGoodnessProbe */
    public RGoodnessProbe(FragmentationScheme fragmentationScheme,
                          Molecule molecule) {
        super(fragmentationScheme, molecule);
        
        goodnessMap = new ArrayList<HashMap<Integer, 
                 ArrayList<FragmentAtomGoodness>>>(molecule.getNumberOfAtoms());
    }

    /**
     * Get the best fragment for given set of global atom indices.
     * 
     * @param atomIndex1 the first global atom index
     * @param atomIndex2 the second global atom index
     * @return Fragment the fragment in which this atom is best represented;
     *                  if it is not represented any where, a <code>null</code>
     *                  is returned.
     */
    @Override
    public Fragment getBestFragmentFor(int atomIndex1, int atomIndex2) {
        FragmentAtomGoodness fga = getBestGoodnessIndex(atomIndex1, atomIndex2);
        
        if (fga == null) return null;
        else return fga.getFragment();
    }

    /**
     * Get the best goodess index for given set of global atom indices.
     * 
     * @param atomIndex1 the first global atom index
     * @param atomIndex2 the second global atom index
     * @return FragmentAtomGoodness the best goodness of an atom pair
     */
    @Override
    public FragmentAtomGoodness getBestGoodnessIndex(int atomIndex1, 
                                                     int atomIndex2) {
        Iterator<FragmentAtomGoodness> fgas = getGoodnessIndices(atomIndex1, 
                                                                 atomIndex2);
        if (fgas == null) {
            return null;
        } else {
            FragmentAtomGoodness fga;
            
            while(fgas.hasNext()) {
                fga = fgas.next();
                
                if (fga.isBest()) return fga;
            } // end while
            
            // the control should probably never reach here
            return null;
        } // end if
    }

    /**
     * Get all the goodess indices for given set of global atom indices.
     * 
     * @param atomIndex1 the first global atom index
     * @param atomIndex2 the second global atom index
     * @return Iterator of FragmentAtomGoodness for all representations of this
     *                 atom pair in all the fragments in the said scheme
     */
    @Override
    public Iterator<FragmentAtomGoodness> getGoodnessIndices(int atomIndex1, 
                                                             int atomIndex2) { 
        if (goodnessMap.get(atomIndex1)==null) return null;
        else if (goodnessMap.get(atomIndex1).get(atomIndex2)==null) return null;
        else return goodnessMap.get(atomIndex1).get(atomIndex2).iterator();
    }

    /**
     * Run the goodness probe for the said fragmentation scheme.
     */
    @Override
    public void runGoodnessProbe() {
        Iterator<Fragment> fragments = fragmentationScheme.getFragmentList()
                                                           .getFragments();
        Fragment fragment;                
        double distance, minGoodness;
        
        overallMinimumGoodnessIndex = new FragmentAtomGoodness(null, 0.0);
        overallMaximumGoodnessIndex = new FragmentAtomGoodness(null, 0.0);
        
        overallMinimumGoodnessAtom = null;
        overallMaximumGoodnessAtom = null;
        
        goodnessMap = new ArrayList<HashMap<Integer, 
                 ArrayList<FragmentAtomGoodness>>>(molecule.getNumberOfAtoms());
        
        for(int i=0; i<molecule.getNumberOfAtoms(); i++) {            
            goodnessMap.add(new HashMap<Integer,
                                        ArrayList<FragmentAtomGoodness>>(10));
        } // end for
        
        // first form the (i, i) goodness        
        while(fragments.hasNext()) {
            fragment = fragments.next();
            
            Iterator<FragmentAtom> fAtoms = fragment.getFragmentAtoms();
            FragmentAtom fAtom;
            
            // make a list of all the atoms not in the fragment
            ArrayList<Atom> absentAtoms = new ArrayList<Atom>();
            Iterator<Atom> atoms = molecule.getAtoms();
            Atom atom;
            
            while(atoms.hasNext()) {
                atom = atoms.next();
                
                if (!fragment.contains(atom)) {
                    absentAtoms.add(atom);
                } // end if
            } // end while
                
            // for each fragment atom, check the goodness            
            while(fAtoms.hasNext()) {
                fAtom = fAtoms.next();
                
                // no goodness analysis for dummy atoms
                if (fAtom.isDummy()) continue;
                
                minGoodness = -1.0;
                for(Atom mAtom : absentAtoms) {
                    // this atom is not within this fragment,
                    // consider it for "goodness"
                    distance = mAtom.distanceFrom((Atom) fAtom);
                    
                    if (minGoodness == -1.0) {
                        minGoodness = distance;
                    } else {
                        minGoodness = Math.min(minGoodness, distance);
                    } // end if
                } // end for
                
                // we have the min goodness for this atom, add it to our list
                if (goodnessMap.get(fAtom.getAkaAtom().getIndex())
                                  .get(fAtom.getAkaAtom().getIndex()) == null) {
                    goodnessMap.get(fAtom.getAkaAtom().getIndex()).put(
                                      fAtom.getAkaAtom().getIndex(),
                                      new ArrayList<FragmentAtomGoodness>(5));
                } // end if
                
                goodnessMap.get(
                  fAtom.getAkaAtom().getIndex()).get(
                        fAtom.getAkaAtom().getIndex())
                          .add(new FragmentAtomGoodness(fragment, minGoodness));
            } // end while                        
        } // end while
        
        // find out the best of (i, i) goodness indices and mark them
        int noOfAtoms = molecule.getNumberOfAtoms();
        int maxGoodnessIndex, minGoodnessIndex, currentIndex;
        double maxGoodness;
        Integer idx;
                
        for(int i=0; i<noOfAtoms; i++) {
            maxGoodness = -1.0;
            minGoodness = -1.0;
            maxGoodnessIndex = -1;
            minGoodnessIndex = -1;
            currentIndex = 0;
            idx = new Integer(i);
            
            // search for the best representation
            if ((goodnessMap.get(idx) != null) 
                 && (goodnessMap.get(idx).get(idx) != null)) {
             for(FragmentAtomGoodness fag : goodnessMap.get(idx).get(idx)) {
                if (fag.getValue() > maxGoodness) {
                    maxGoodness      = fag.getValue();
                    maxGoodnessIndex = currentIndex;
                } // end if                
                
                currentIndex++;
             } // end for
            } // end if
            
            // and mark it
            if (maxGoodnessIndex != -1) {
              goodnessMap.get(idx).get(idx).get(maxGoodnessIndex).setBest(true);
              
              // set max and min goodness indices
              FragmentAtomGoodness fag =
                      goodnessMap.get(i).get(i).get(maxGoodnessIndex);

              if (fag.getValue()
                   > overallMaximumGoodnessIndex.getValue()) {
                  overallMaximumGoodnessIndex = fag;
                  overallMaximumGoodnessAtom  = molecule.getAtom(i);
              } // end if
              
              if (minGoodnessIndex == -1) {
                  minGoodness      = fag.getValue();
                  minGoodnessIndex = maxGoodnessIndex;
              } else {
                  if (fag.getValue() < minGoodness) {
                      minGoodness      = fag.getValue();
                      minGoodnessIndex = maxGoodnessIndex;
                  } // end if
              } // end if
            } // end if
            
            if (minGoodnessIndex != -1) {
                FragmentAtomGoodness fag =
                      goodnessMap.get(i).get(i).get(minGoodnessIndex);

                if (overallMinimumGoodnessIndex.getValue() == 0.0) {
                    overallMinimumGoodnessIndex = fag;
                    overallMinimumGoodnessAtom  = molecule.getAtom(i);
                } else if (fag.getValue() 
                    < overallMinimumGoodnessIndex.getValue()) {
                    overallMinimumGoodnessIndex = fag;
                    overallMinimumGoodnessAtom  = molecule.getAtom(i);
                } // end if
            } // end if
            
        } // end for
        
        // next define all (i, j) goodness        
        ArrayList<FragmentAtomGoodness> iGoodness, jGoodness;
        
        for(int i=1; i<noOfAtoms; i++) {
            iGoodness = goodnessMap.get(i).get(i);
            
            for(int j=i+1; j<noOfAtoms; j++) {
                jGoodness = goodnessMap.get(j).get(j);
                                
                maxGoodness = -1.0;
                maxGoodnessIndex = -1;
                currentIndex = 0;
            
                if ((jGoodness == null) || (iGoodness == null)) continue;
                
                for(FragmentAtomGoodness jGood : jGoodness) {
                    for(FragmentAtomGoodness iGood : iGoodness) {
                        // NOTE: this is an intentional object comparison
                        if (iGood.getFragment() == jGood.getFragment()) {
                            
                            if (goodnessMap.get(i).get(j) == null) {
                                goodnessMap.get(i).put(j, 
                                        new ArrayList<FragmentAtomGoodness>(5));
                            } // end if
                            
                            if (goodnessMap.get(j).get(i) == null) {
                                goodnessMap.get(j).put(i, 
                                        new ArrayList<FragmentAtomGoodness>(5));
                            } // end if
                            
                            FragmentAtomGoodness fag =
                                new FragmentAtomGoodness(iGood.getFragment(), 
                                  Math.min(iGood.getValue(), jGood.getValue()));
                            
                            // add the goodness
                            goodnessMap.get(i).get(j).add(fag);
                            goodnessMap.get(j).get(i).add(fag);
                            
                            // do the stat
                            if (fag.getValue() > maxGoodness) {
                                maxGoodness      = fag.getValue();
                                maxGoodnessIndex = currentIndex;
                            } // end if
                            
                            currentIndex++;
                        } // end if
                    } // end for iGood
                } // end for jGood
                
                // set the best stuff
                if (maxGoodnessIndex != -1) {
                  goodnessMap.get(i).get(j).get(maxGoodnessIndex).setBest(true);
                } // end if
            } // end for j
        } // end for i
    }
    
    /**
     * overridden toString()
     */
    @Override
    public String toString() {
        return "Default R-Goodness probe.";
    } 
} // end of class RGoodnessProbe
