/*
 * FragmentScene.java
 *
 * Created on February 4, 2005, 7:06 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;
import java.util.*;

import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragment.event.FragmentListChangeEvent;
import org.meta.fragment.event.FragmentListChangeListener;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.config.event.AtomInfoChangeEvent;
import org.meta.config.event.AtomInfoChangeListener;
import org.meta.config.impl.AtomInfo;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.shell.idebeans.graphics.AbstractScene;

/**
 * A simple scene graph for Molecular Fragment!
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentScene extends AbstractScene {
    
    private Fragment fragment;
    private Molecule molecule;
    private FragmentationScheme scheme;    

    private ArrayList<ScreenAtom> screenAtoms;
    private ArrayList<FragmentScreenBond> fragmentScreenBonds;
            
    private AtomInfoChangeListener atomInfoChangeListener;
    
    /** Creates a new instance of FragmentScene */
    public FragmentScene(FragmentationScheme scheme, Fragment fragment, 
                         Molecule molecule) {
        this.fragment = fragment;
        this.molecule = molecule;
        this.scheme   = scheme;        
        
        buildFragmentScene();
        
        visible = true;
        
        // listen to changes in fragmentation scheme
        scheme.addFragmentListChangeListener(new FragmentListChangeListener() {
            @Override
            public void fragmentListChanged(FragmentListChangeEvent flce) {
                buildFragmentScene();
            }
        });
        
        // better way out?
        atomInfoChangeListener = new AtomInfoChangeListener() {
            @Override
            public void atomInfoChanged(AtomInfoChangeEvent aice) {
                // TODO: what about other events?
                switch(aice.getChangeType()) {
                    case AtomInfoChangeEvent.COLOR_VALUE:                        
                        buildFragmentScene();
                        break;
                }
            }
        };
        
        // add a listener
        AtomInfo.getInstance()
        .addAtomInfoChangeListener(atomInfoChangeListener);
    }

    /**
     * private method to build the scene
     */
    private void buildFragmentScene() {
        int noOfFragmentAtoms = fragment.getTotalNumberOfAtoms();
        
        // TODO : PRIMITIVE STATE : MAY NEED OPTIMIZATION
        
        screenAtoms = new ArrayList<ScreenAtom>(noOfFragmentAtoms);
        fragmentScreenBonds = new ArrayList<FragmentScreenBond>();
        
        Iterator<FragmentAtom> fragmentAtoms = fragment.getFragmentAtoms();
        Hashtable<Integer, BondType> connList;
        FragmentAtom fragmentAtom;
        int idx, i=0;
        
        while(fragmentAtoms.hasNext()) {
            fragmentAtom = fragmentAtoms.next();
            
            if (fragmentAtom.isDummy()) continue;
            screenAtoms.add(new ScreenAtom(fragmentAtom));
        } // end while
                
        for(ScreenAtom screenAtom : screenAtoms) {
            connList = screenAtom.getAtom().getConnectedList();

            for(int connectedIndex : connList.keySet()) {
                idx = fragment.getIndexOfAtom(
                                         molecule.getAtom(connectedIndex));
                
                if ((idx == -1) || (i > idx)) continue;
                
                fragmentScreenBonds.add(new FragmentScreenBond(screenAtom, 
                                                    screenAtoms.get(idx)));
            } // end for
            
            i++;
        } // end for
        
        // remove all the references
        fragmentAtoms = null;
        connList = null;
    }
    
    /**
     * Method to appropriately draw the fragment on to the canvas.
     * 
     * @param g2d - the graphics object conneced to the canvas
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (!visible) return;                 
        
        // the atoms
        for(ScreenAtom item : screenAtoms) {
            item.draw(g2d);
        } // end for
        
        // the bonds
        for(FragmentScreenBond item : fragmentScreenBonds) {
            item.draw(g2d);
        } // end for
    }
    
    /**
     * Getter for property molecule.
     * @return Value of property molecule.
     */
    public Molecule getMolecule() {
        return this.molecule;
    }

    /**
     * Getter for property fragment.
     * @return Value of property fragment.
     */
    public Fragment getFragment() {
        return this.fragment;
    }

    /**
     * Getter for property scheme.
     * @return Value of property scheme.
     */
    public FragmentationScheme getScheme() {
        return this.scheme;
    }
    
    /**
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        transform.transform(screenAtoms);
    }
} // end of class FragmentScene
