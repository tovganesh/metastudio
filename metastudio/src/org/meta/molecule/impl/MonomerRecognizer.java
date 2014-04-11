/*
 * MonomerRecognizer.java 
 *
 * Created on 27 Jul, 2008 
 */

package org.meta.molecule.impl;

import java.util.ArrayList;
import java.util.Iterator;
import org.meta.molecule.AtomGroupList;
import org.meta.molecule.Molecule;
import org.meta.molecule.SpecialStructureRecognizer;

/**
 * Recognize monomer units in a molecular cluster.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MonomerRecognizer implements SpecialStructureRecognizer {

    /** the list of atoms is stored here */
    private AtomGroupList theMonomers;
    
    /** Create instance of MonomerRecognizer */
    public MonomerRecognizer() {    
        theMonomers = new AtomGroupListImpl();
    }

    /** 
     * This method detects presence of cycles in the molecular
     * graph and record this information in a object of AtomGroupList.
     *
     * @param molecule The molecule object reference.
     */  
    @Override
    public void recognizeAndRecord(Molecule molecule) {
        ArrayList<Integer> visitedList = new ArrayList<Integer>();
        
        for(int i=0; i<molecule.getNumberOfAtoms(); i++) {
            if (!visitedList.contains(i)) {
                Iterator<Integer> monomerIdx = molecule.traversePath(i, true);

                DefaultAtomGroup monomer = new DefaultAtomGroup();
                
                while(monomerIdx.hasNext()) {
                    Integer atomIdx = monomerIdx.next();
                    
                    monomer.addAtomIndex(atomIdx);
                    visitedList.add(atomIdx);
                } // end while
                
                theMonomers.addGroup(monomer);               
            } // end if
        } // end for
    }

    /**
     * This method returns the list of all the identified structures as a
     * object of AtomGroupList
     * 
     * @return Instance of AtomGroupList
     */
    @Override
    public AtomGroupList getGroupList() {
        return theMonomers;
    }

    /**
     * a straight forward setGroupList() method, no checks are made
     * of the correctness of groupList and hence the correctness wholy
     * rests on the callie.
     *
     * @param groupList the groupList consisting of Ring objects.
     */
    @Override
    public void setGroupList(AtomGroupList groupList) {
        this.theMonomers = groupList;
    }

}
