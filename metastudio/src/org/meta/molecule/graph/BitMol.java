/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.molecule.graph;

import java.util.BitSet;
import java.util.Set;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;

/**
 * A bit representation of Molecule object.
 * 
 * @author V. Ganesh
 */
public class BitMol {
   private final static int MOL_BITS = 64;   
   private BitSet bitMol;
   
   public BitMol(Molecule mol) {
       bitMol = new BitSet(MOL_BITS);
       
       makeBitMol(mol);
   }
   
   private void makeBitMol(Molecule mol) {
       int nAtms = mol.getNumberOfAtoms();
       for(int idx=0; idx<nAtms; idx++) {
           Atom atm = mol.getAtom(idx);
           Set<Integer> conn = atm.getConnectedList().keySet();
           for(Integer connAtom : conn) {
               BondType bond = atm.getConnectivity(connAtom);
           } // end for
       } // end for
   }
   
   public BitSet getBitMol() {
       return bitMol;
   }
}
