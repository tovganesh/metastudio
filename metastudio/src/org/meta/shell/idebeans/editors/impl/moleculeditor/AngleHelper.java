/*
 * AngleHelper.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import java.awt.Color;
import java.util.Hashtable;
import org.meta.math.geom.Point3D;
import org.meta.molecule.BondType;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.AngleTracker;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * Show angles!
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AngleHelper extends MoleculeEditorCommandHelper {

    private AngleTracker angleTracker;
    
    /** Create instance of AngleHelper */
    public AngleHelper() {        
    }
    
    /**
     * Update the helper based on the current state of MoleculeScene 
     * and X and Y reference points.
     */
    @Override
    public void update() {
        clear();
        
        MoleculeEditorCommonData mecd = MoleculeEditorCommonData.getInstance();
        ScreenAtom nearestAtom = 
               mecd.getNearestScreenAtom(moleculeScene, referenceX, referenceY);
            
        if (nearestAtom == null) return;
        
        Hashtable<Integer, BondType> connList 
                                    = nearestAtom.getAtom().getConnectedList();            
        Point3D p1 
              = mecd.getTransformedPoint(moleculeScene, referenceX, referenceY);
        
        if (connList.size() != 0) {
            if (angleTracker != null)
                moleculeScene.removeTracker(angleTracker);

            angleTracker = new AngleTracker(
                 moleculeScene.getMolecule().getAtom(
                            connList.keys().nextElement()).getAtomCenter(),
                 nearestAtom.getAtom().getAtomCenter(), p1);

            angleTracker.setTransform(moleculeScene.getTransform());
            moleculeScene.addTracker(angleTracker);
        } // end if
    }
    
    /**
     * Set the value of helperColor
     *
     * @param helperColor new value of helperColor
     */
    @Override
    public void setHelperColor(Color helperColor) {
        this.helperColor = helperColor;
        
        if (angleTracker != null) 
            angleTracker.getScreenLabel().setColor(helperColor);
    } 
    
    /**
     * Clear this helper!
     */
    @Override
    public void clear() {
        if (angleTracker != null)
            moleculeScene.removeTracker(angleTracker);
    }
}
