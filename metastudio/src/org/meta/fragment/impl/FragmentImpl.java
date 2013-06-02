/*
 * FragmentImpl.java
 *
 * Created on October 11, 2004, 8:23 PM
 */

package org.meta.fragment.impl;

import java.util.*;
import org.meta.config.impl.AtomInfo;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.math.Vector3D;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;

/**
 * The default implementation of Fragment abstract class.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentImpl extends Fragment {
    
    // list of actual fragment atoms
    private ArrayList<FragmentAtom> fragmentAtomList;
    
    // list of dummy fragment atoms
    private ArrayList<FragmentAtom> dummyAtomList;
    
    // list of boundary fragment atoms
    private ArrayList<FragmentAtom> boundaryAtomList;
    
    /** Creates a new instance of FragmentImpl */
    public FragmentImpl() {
        super();
        
        // init the list
        fragmentAtomList = new ArrayList<FragmentAtom>(30);
        dummyAtomList    = new ArrayList<FragmentAtom>(10);
        boundaryAtomList = new ArrayList<FragmentAtom>(10);
        
        // by default i am main fragment, not an overlap
        overlapFragment = false;
        
        // default cardinality sign is +1
        cardinalitySign = 1;
        
        // the fragment by default is charge neutral
        totalCharge = 0;
    }
    
    /**
     * Add a fragment atom to this fragment 
     *
     * @param fragmentAtom the fragment atom to be added to this fragment
     */
    @Override
    public void addFragmentAtom(FragmentAtom fragmentAtom) {
        if (fragmentAtom.isBoundaryAtom()) {
            boundaryAtomList.add(fragmentAtom);
        } else if (fragmentAtom.isDummy()) {
            dummyAtomList.add(fragmentAtom);
        } // end if
        
        // all kinds of atoms should be referenced here
        if (!fragmentAtomList.contains(fragmentAtom))
            fragmentAtomList.add(fragmentAtom);        
    }
    
    /**
     * get degree of connectivity of a FragmentAtom in this fragment
     *
     * @param fa fragment atom whose degree is to be found
     */
    protected int getDegreeOfFragmentAtom(FragmentAtom fa) {
        int degree = 0;
        
        Set<Integer> atoms = fa.getAkaAtom().getConnectedList().keySet();
        
        for(Integer atm : atoms) {
            if (contains(parentMolecule.getAtom(atm))) {
              if ((fa.getAkaAtom().getConnectivity(atm) != BondType.NO_BOND)
               && (fa.getAkaAtom().getConnectivity(atm) != BondType.WEAK_BOND)){
                  degree++;
              } // end if
            } // end if
        } // end for
        
        return degree;
    }
    
    /**
     * Add dummy atoms to each fragment for this scheme. 
     *
     * @param dummyAtom The type of dummy atoms to be added to the fragments          
     * @param position The position at which the dummy atom is to be placed
     */
    @Override
    public void addDummyAtoms(Atom dummyAtom, 
                              FragmentationScheme.DummyAtomPosition position) {
        
        dummyAtomList.clear();
        
        for(FragmentAtom ba : boundaryAtomList) {
            Set<Integer> atoms = ba.getAkaAtom().getConnectedList().keySet();
            
            for(Integer atm : atoms) {
                
                Atom akaAtom = ba.getAkaAtom();
                
                if ((akaAtom.getConnectivity(atm).equals(BondType.NO_BOND)) ||
                    (akaAtom.getConnectivity(atm).equals(BondType.WEAK_BOND))) {
                  continue;
                } // end if
                
                akaAtom = parentMolecule.getAtom(atm);
                                
                if (!contains(akaAtom)) {
                    FragmentAtom newDummyAtom;
                    
                    switch(position) {
                        case AT_CUT_POSITION:
                            newDummyAtom = new FragmentAtom(
                                  dummyAtom.getSymbol(), dummyAtom.getCharge(),
                                  akaAtom.getAtomCenter());
                            
                            newDummyAtom.setDummy(true);                            
                            addFragmentAtom(newDummyAtom);
                            break;
                        case STD_BOND_DISTANCE:
                            Vector3D vec = new Vector3D(
                                akaAtom.getAtomCenter().sub(
                                     ba.getAkaAtom().getAtomCenter()));
                            
                            vec = vec.normalize();
                            
                            AtomInfo ai = AtomInfo.getInstance();
                            double bondDistance = 
                                    ai.getCovalentRadius(dummyAtom.getSymbol())
                                  + ai.getCovalentRadius(ba.getSymbol());
                            
                            vec = vec.mul(bondDistance)
                                     .add(ba.getAkaAtom().getAtomCenter());
                            
                            newDummyAtom = new FragmentAtom(
                                  dummyAtom.getSymbol(), dummyAtom.getCharge(),
                                  vec.toPoint3D());
                            
                            newDummyAtom.setDummy(true);                            
                            addFragmentAtom(newDummyAtom);
                            break;
                        default:
                            System.err.println("WARNING: " +
                                    "No dummy atoms added! Invalid position!");
                            break;
                    } // end of switch case
                } // end if
            } // end for
        } // end for
    }
    
    /**
     * Identify boundary atoms and add them to the fragment list
     */
    @Override
    public void identfyBoundaryAtoms() {
        boundaryAtomList.clear();
        
        int degreeOfAka, degreeOfFa;
        
        for(FragmentAtom fa : fragmentAtomList) {
            if (fa.isDummy()) continue;
            
            degreeOfAka = 0;
            
            for(Integer atm : fa.getAkaAtom().getConnectedList().keySet()) {
              if ((fa.getAkaAtom().getConnectivity(atm) != BondType.NO_BOND)
               && (fa.getAkaAtom().getConnectivity(atm) != BondType.WEAK_BOND)){
                  degreeOfAka++;
              } // end if
            } // end for
            
            degreeOfFa  = getDegreeOfFragmentAtom(fa);
            
            if (degreeOfFa != degreeOfAka) {
                fa.setBoundaryAtom(true);
                boundaryAtomList.add(fa);
            } // end if
        } // end for
    }
    
   /**
     * Check for the presence of fragmentAtom in this fragment.
     *
     * @param fragmentAtom the fragmentAtom whose presence is to be checked
     * @return true if the fragmentAtom is present in this fragment else false
     */
    @Override
    public boolean contains(FragmentAtom fragmentAtom) {        
        return fragmentAtomList.contains(fragmentAtom);        
    }
    
    /**
     * Check for the presence of atom in this fragment.
     *
     * @param atom the atom whose presence is to be checked
     * @return true if the atom is present in this fragment else false
     */
    @Override
    public boolean contains(Atom atom) {     
        Iterator<FragmentAtom> fragmentAtoms = fragmentAtomList.iterator();
        
        while(fragmentAtoms.hasNext()) {
          FragmentAtom fa = fragmentAtoms.next();
          
          if (fa.isDummy()) continue;
          
          if (fa.getAkaAtomIndex() == atom.getIndex()) {
              return true;
          } // end if
        } // end while
        
        return false;
    }
        
    /**
     * Get a list of all the boundary atoms in this fragment.
     *
     * @return Iterator of all the boundary atoms 
     */
    @Override
    public Iterator<FragmentAtom> getBoundaryAtoms() {
        return boundaryAtomList.iterator();
    }
    
    /**
     * Get a list of all the dummy atoms in this fragment.
     *
     * @return Iterator of all the dummy atoms 
     */
    @Override
    public Iterator<FragmentAtom> getDummyAtoms() {        
        return dummyAtomList.iterator();
    }
    
    /**
     * Returns a Fragment object that is intersection of this and 
     * parameter fragment. <br>
     * Note: Intersection only applies to the main fragment atoms, and not the
     *       boundary and dummy atoms. Thus the result of intersection will
     *
     * @param fragment the Fragment object with which the intersection operation
     *         is to be performed.
     * @return a new Fragment object object instance, which is intersection of 
     *         both the fragments
     */
    @Override
    public Fragment intersection(Fragment fragment) {
        FragmentImpl newFragment = new FragmentImpl();
        
        newFragment.setParentMolecule(parentMolecule);
        newFragment.setOverlapFragment(true);
        
        Iterator<FragmentAtom> fragmentAtoms = fragmentAtomList.iterator();
        FragmentAtom fragmentAtom;
        
        while(fragmentAtoms.hasNext()) {
            fragmentAtom = fragmentAtoms.next();
            
            if (fragmentAtom.isDummy()) continue;
            
            if (fragment.contains(fragmentAtom)) {
                newFragment.addFragmentAtom(
                               new FragmentAtom(fragmentAtom.getAkaAtom()));
            } // end if
        } // end while
        
        return newFragment;
    }
    
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
    @Override
    public Fragment union(Fragment fragment) {
        FragmentImpl newFragment = new FragmentImpl();
        
        newFragment.setParentMolecule(parentMolecule);
        newFragment.setOverlapFragment(true);
        
        Iterator<FragmentAtom> fragmentAtoms = fragmentAtomList.iterator();
        FragmentAtom fragmentAtom;
        
        while(fragmentAtoms.hasNext()) {
            fragmentAtom = fragmentAtoms.next();
            
            if (fragmentAtom.isDummy()) continue;
            
            newFragment.addFragmentAtom(
                    new FragmentAtom(fragmentAtom.getAkaAtom()));            
        } // end while
        
        fragmentAtoms = fragment.getFragmentAtoms();
        
        while(fragmentAtoms.hasNext()) {
            fragmentAtom = fragmentAtoms.next();
            
            if (fragmentAtom.isDummy()) continue;
            
            if (!newFragment.contains(fragmentAtom)) {
                newFragment.addFragmentAtom(
                        new FragmentAtom(fragmentAtom.getAkaAtom()));
            } // end if
        } // end while
        
        return newFragment;
    }
    
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
    @Override
    public Fragment substract(Fragment fragment) {
        FragmentImpl newFragment = new FragmentImpl();
        
        newFragment.setParentMolecule(parentMolecule);
        newFragment.setOverlapFragment(true);
        
        Iterator<FragmentAtom> fragmentAtoms = fragmentAtomList.iterator();
        FragmentAtom fragmentAtom;
                
        while(fragmentAtoms.hasNext()) {
            fragmentAtom = fragmentAtoms.next();
            
            if (fragmentAtom.isDummy()) continue;
            
            if (!fragment.contains(fragmentAtom)) {
                newFragment.addFragmentAtom(
                        new FragmentAtom(fragmentAtom.getAkaAtom()));
            } // end if
        } // end while                
        
        return newFragment;
    }
    
    /**
     * Check if a fragment is a subset of this Fragment object
     *
     * @param fragment the Fragment to be checked for subset relation
     * @return a boolean true if it is a subset or else false.
     */
    @Override
    public boolean isSubset(Fragment fragment) {
        Iterator<FragmentAtom> fragmentAtoms = fragment.getFragmentAtoms();
        FragmentAtom fragmentAtom;
                
        while(fragmentAtoms.hasNext()) {
            fragmentAtom = fragmentAtoms.next();
            
            if (fragmentAtom.isDummy()) continue;
            
            if (!contains(fragmentAtom)) {
                // atleast one atom is missing so no subset relation!
                return false;  
            } // end if
        } // end while      
        
        // all the atoms seems to be preset, subset!!
        return true;
    }
    
    /**
     * Returns the number of boundary atoms in this fragment.
     *
     * @return number of boundary atoms
     */
    @Override
    public int getNumberOfBoundaryAtoms() {        
        return boundaryAtomList.size();
    }
    
    /**
     * Returns the number of common atoms between this and the fragment 
     * represented by the paramenter. Dummy atoms should not be counted for
     * this purpose.
     *
     * @param fragment the other fragment with which this fragment need to be 
     *        compared.
     * @return int the number of atoms common in both the fragments.
     */
    @Override
    public int numberOfCommonAtoms(Fragment fragment) {
        int noOfCommonAtoms = 0;
        
        Iterator<FragmentAtom> fragmentAtoms = fragmentAtomList.iterator();
        FragmentAtom fragmentAtom;
        
        while(fragmentAtoms.hasNext()) {
            fragmentAtom = fragmentAtoms.next();
            
            if (fragment.contains(fragmentAtom)) {
                noOfCommonAtoms++;
            } // end if
        } // end while
        
        return noOfCommonAtoms;        
    }
    
    /**
     * Returns the number of dummy atoms in this fragment.
     *
     * @return number of dummy atoms
     */
    @Override
    public int getNumberOfDummyAtoms() {        
        return dummyAtomList.size();
    }
    
    /**
     * Remove a fragment atom to this fragment 
     *
     * @param fragmentAtom the fragment atom to be removed from this fragment
     */
    @Override
    public void removeFragmentAtom(FragmentAtom fragmentAtom) {
        fragmentAtomList.remove(fragmentAtom);
    }        
    
    /** 
     * remove a fragment atom from this Fragment at a given index
     *
     * @param atomIndex the atom index to be removed
     */    
    @Override
    public void removeFragmentAtomAt(int atomIndex) {
        fragmentAtomList.remove(atomIndex);
    }
    
    /** 
     * Method to get a particular fragment atom from the lists of fragment atoms
     *
     * @return FragmentAtom the instance of FragmentAtom class
     */    
    @Override
    public FragmentAtom getFragmentAtom(int atomIndex) {
        return fragmentAtomList.get(atomIndex);
    }
    
    /** 
     * Method to get list of fragment atoms in this fragment.
     *
     * @return an iterator object containing a linear list of fragment atoms in 
     *         the Fragment!
     */    
    @Override
    public Iterator<FragmentAtom> getFragmentAtoms() {
        return fragmentAtomList.iterator();
    }
    
    /**
     * Returns the total number of atoms in this fragment 
     * (including dummy atoms).
     *
     * @return total number of atoms in this fragment
     */
    @Override
    public int getTotalNumberOfAtoms() {
        return fragmentAtomList.size();
    }

    /** 
     * Method to get index of a particular atom in this fragment
     *
     * @return index of the atom in this fragment, else -1
     */   
    @Override
    public int getIndexOfAtom(Atom atom) {
        Iterator<FragmentAtom> fragmentAtoms = fragmentAtomList.iterator();
        FragmentAtom fragmentAtom;        
        int i = 0;
        
        while(fragmentAtoms.hasNext()) {
          fragmentAtom = fragmentAtoms.next();
          
          if (fragmentAtom.isDummy()) continue;
          
          if (fragmentAtom.getAkaAtom().equals(atom)) {
              return i;
          } // end if
          i++;
        } // end while
        
        return -1;
    }
    
    /**
     * Compute Charges on the fragments      
     */
    @Override
    public void computeCharges() {
        AtomInfo ai = AtomInfo.getInstance();
        
        totalCharge = 0;
        
        for(FragmentAtom fa : fragmentAtomList) {
            
            if (fa.isDummy()) continue;
                
            Atom akaAtom = fa.getAkaAtom();
            
            int nBonds = 0;
            
            if (ai.getAtomicNumber(akaAtom.getSymbol()) <= 6) {
                nBonds = ai.getDefaultValency(akaAtom.getSymbol())
                            - akaAtom.getConnectedList().size();
            } else {                
                nBonds = akaAtom.getConnectedList().size()
                            - ai.getDefaultValency(akaAtom.getSymbol());
            } // end if
            
            totalCharge += nBonds;
        } // end for
    }
    
    /**
     * Overloaded equals method.
     * 
     * @param obj the object to be tested for equality
     * @return true: they match!, else false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if ((obj == null) || !(obj instanceof Fragment)) return false;                
        
        Fragment o = (Fragment) obj;
        
        if (getTotalNumberOfAtoms() != o.getTotalNumberOfAtoms()) return false;
        
        for(FragmentAtom atm : fragmentAtomList) {
            if (atm.isDummy()) continue;
            
            if (!o.contains(atm)) return false;
        } // end for
        
        return true;
    }
} // end of class FragmentImpl
