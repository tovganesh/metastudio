/*
 * DistanceHelper.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import java.awt.Color;
import org.meta.math.geom.Point3D;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.DistanceTracker;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * Show distances!
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DistanceHelper extends MoleculeEditorCommandHelper {

    private DistanceTracker distanceTracker;
    
    /** Creates new instance if DistanceHelper */
    public DistanceHelper() {        
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
           
        Point3D p1 
              = mecd.getTransformedPoint(moleculeScene, referenceX, referenceY);
        
        distanceTracker = new DistanceTracker(p1, 
                                         nearestAtom.getAtom().getAtomCenter());
        distanceTracker.setTransform(moleculeScene.getTransform());
        moleculeScene.addTracker(distanceTracker);
    }
    
    /**
     * Set the value of helperColor
     *
     * @param helperColor new value of helperColor
     */
    @Override
    public void setHelperColor(Color helperColor) {
        this.helperColor = helperColor;
        
        if (distanceTracker != null)
            distanceTracker.getScreenLabel().setColor(helperColor);
    } 
    
    /**
     * Clear this helper!
     */
    @Override
    public void clear() {
        if (distanceTracker != null)
            moleculeScene.removeTracker(distanceTracker);
    }
}
