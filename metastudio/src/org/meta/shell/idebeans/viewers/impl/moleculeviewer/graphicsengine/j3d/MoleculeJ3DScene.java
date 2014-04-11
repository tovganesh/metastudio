/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import javax.media.j3d.TransformGroup;
import org.meta.common.Utility;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.molecule.event.MoleculeStateChangeEvent;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.MoleculeSceneChangeEvent;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.MoleculeSceneChangeListener;

/**
 * Java3D implementation of MoleculeScene
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeJ3DScene extends MoleculeScene {
    
    private ArrayList<ScreenAtomJ3D> screenAtomsJ3D;
    private ArrayList<ScreenBondJ3D> screenBondsJ3D;
    
    private MoleculeScene parentScene;
    
    public MoleculeJ3DScene(MoleculeScene scene) {
        super(scene.getMolecule());
    
        this.parentScene = scene;
        
        if (propertyScenes == null)
            propertyScenes = new ArrayList<PropertyScene>();
        
        // then add all the attached property scenes 
        Iterator<PropertyScene> pslist = scene.getAllPropertyScene();
        if (pslist != null) {
            while(pslist.hasNext()) {
                propertyScenes.add(new PropertyJ3DScene(pslist.next()));
            } // end while
        } // end if
        
        scene.addMoleculeSceneChangeListener(new MoleculeSceneChangeListener() {
            @Override
            public void sceneChanged(MoleculeSceneChangeEvent msce) {
                if (msce.getType() == MoleculeSceneChangeEvent.PROPERTY_ADDED) {
                    addPropertyScene(msce.getChangedPropertyScene());
                } // end if
            }            
        });
        
        buildJava3DScene();        
    }
    
    public MoleculeJ3DScene(Molecule molecule) {
        super(molecule);
        
        buildJava3DScene();
    }
    
    /**
     * make the Java3D scene
     */
    private void buildJava3DScene() {
        if (transformGroup == null) return;
        
        int noOfAtoms = molecule.getNumberOfAtoms();
        
        // init the arrays
        screenAtomsJ3D = new ArrayList<ScreenAtomJ3D>(noOfAtoms);
        screenBondsJ3D = new ArrayList<ScreenBondJ3D>();
        
        // add the atoms first
        Iterator<Atom> atoms = molecule.getAtoms();
        
        while(atoms.hasNext()) {
            screenAtomsJ3D.add(new ScreenAtomJ3D(atoms.next()));
        } // end while
        
        // and the bonds between them!
        ScreenAtomJ3D  screenAtom;
        Hashtable<Integer, BondType> connList;
        Enumeration<Integer> connAtoms;
        Integer theIndex;
        int index;
        ScreenBondJ3D screenBond;
        
        for(int i=0; i<noOfAtoms; i++) {
            zBuffer[i] = i;
            screenAtom = screenAtomsJ3D.get(i);
            connList   = screenAtom.getAtom().getConnectedList();
            connAtoms  = connList.keys();
            
            while(connAtoms.hasMoreElements()) {
                theIndex = connAtoms.nextElement();
                index    = theIndex.intValue();
                
                if (i > index) continue;
                
                if (!connList.get(theIndex).equals(BondType.NO_BOND)) {
                    screenBond = new ScreenBondJ3D(
                      new SingleScreenBond(screenAtom, screenAtoms.get(index)));
                    
                    screenBondsJ3D.add(screenBond);
                    screenAtom.getScreenBondList().add(screenBond);
                } // end if
            } // end while
        } // end for
        
        TransformGroup objRot = transformGroup;        
        
        for(ScreenAtomJ3D atom : screenAtomsJ3D) {
            atom.buildJava3DScene(objRot);
        } // end for
        
        for(ScreenBondJ3D bond : screenBondsJ3D) {
            bond.buildJava3DScene(objRot);
        } // end for
        
        // the property scenes .. textured planes first
        if (propertyScenes != null) {
            for(PropertyScene scene : propertyScenes) {                
                try {
                    ((PropertyJ3DScene) scene).buildJava3DScene(objRot);
                } catch (Exception e) {
                    System.err.println("Exception in " +
                          "MoleculeJ3DScene.buildJava3DScene: " + e.toString());
                    e.printStackTrace();
                } // end try .. catch exception
            } // end if
        } // end if
    }
    
    /**
     * Attach a property scene to this molecule scene
     *
     * @param ps the property scene object to attach
     */
    @Override
    public void addPropertyScene(PropertyScene ps) {
        if (propertyScenes == null)
            propertyScenes = new ArrayList<PropertyScene>();
        
        PropertyJ3DScene ps3d = new PropertyJ3DScene(ps);
        propertyScenes.add(ps3d);
        ps3d.setName((++propertyCount) + " ");
        ps3d.addPropertySceneChangeListener(this);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.PROPERTY_ADDED);
        sceneChangeEvent.setChangedPropertyScene(ps3d);
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
        sceneChangeEvent.setChangedPropertyScene(null);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.GENERAL_SCENE_CHANGE);
    }
    
    /**
     * Holds value of property transformGroup.
     */
    private TransformGroup transformGroup;
    
    /**
     * Getter for property transformGroup.
     * @return Value of property transformGroup.
     */
    public TransformGroup getTransformGroup() {
        return this.transformGroup;
    }
    
    /**
     * Setter for property transformGroup.
     * @param transformGroup New value of property transformGroup.
     */
    public void setTransformGroup(TransformGroup transformGroup) {
        this.transformGroup = transformGroup;
        
        // rebuild the 3D scene
        buildJava3DScene();
    }
    
    /**
     * called if the concerned molecule is changed
     */
    @Override
    public void moleculeChanged(MoleculeStateChangeEvent msce) {
        if (!isVisible()) return;
        
        System.out.println("MoleculeScene => Molecule Changed : " + msce
                + "; from : " + msce.getSource());
        
        switch (msce.getEventType()) {
            case MoleculeStateChangeEvent.ATOM_ADDED:
            case MoleculeStateChangeEvent.ATOM_REMOVED:
            case MoleculeStateChangeEvent.MAJOR_MODIFICATION:
                try {
                    MoleculeBuilder mb = (MoleculeBuilder)
                    Utility.getDefaultImplFor(MoleculeBuilder.class)
                    .newInstance();
                    
                    mb.rebuildConnectivity(msce);
                } catch (Exception e) {
                    System.err.println("MoleculeScene => " +
                            "Error while refresing geometry : "
                            + e.toString());
                    e.printStackTrace();
                } // end try .. catch block
                break;
        } // end of switch .. case block
        
        // TODO : more effective
        buildJava3DScene();
        
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.MOLECULE_CHANGE);
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.GENERAL_SCENE_CHANGE);   
    }        
}
