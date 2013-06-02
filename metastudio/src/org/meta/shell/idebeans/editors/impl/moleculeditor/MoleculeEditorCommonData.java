/*
 * MoleculeEditorCommonData.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import org.meta.math.geom.Point3D;
import org.meta.math.Matrix3D;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * A singleton class that stores data common to MoleculeEditor instance(s).
 * However, this "singleton" has a bit of different behaviour, in that 
 * internally there would be multiple instance of the same class!
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeEditorCommonData {

    private MoleculeEditorCommonData _transformPoint, _nearestAtom;    
    
    /** Creates instance of MoleculeEditorCommonData */
    private MoleculeEditorCommonData() {                
    }
    
    private static MoleculeEditorCommonData _moleculeEditorCommonData;
    
    /**
     * Get instance of this class.
     * 
     * @return the instance of this class
     */
    public static MoleculeEditorCommonData getInstance() {
        if (_moleculeEditorCommonData == null) {
            _moleculeEditorCommonData = new MoleculeEditorCommonData();
            
            _moleculeEditorCommonData._transformPoint 
                                      = new MoleculeEditorCommonData();
            _moleculeEditorCommonData._nearestAtom    
                                      = new MoleculeEditorCommonData();
        } // end if
        
        return _moleculeEditorCommonData;
    }
    
    protected MoleculeScene moleculeScene;

    /**
     * Get the value of moleculeScene
     *
     * @return the value of moleculeScene
     */
    public MoleculeScene getMoleculeScene() {
        return moleculeScene;
    }

    /**
     * Set the value of moleculeScene
     *
     * @param moleculeScene new value of moleculeScene
     */
    public void setMoleculeScene(MoleculeScene moleculeScene) {
        this.moleculeScene = moleculeScene;
    }

    protected int referenceX;

    /**
     * Get the value of referenceX
     *
     * @return the value of referenceX
     */
    public int getReferenceX() {
        return referenceX;
    }

    /**
     * Set the value of referenceX
     *
     * @param referenceX new value of referenceX
     */
    public void setReferenceX(int referenceX) {
        this.referenceX = referenceX;
    }

    protected int referenceY;

    /**
     * Get the value of referenceY
     *
     * @return the value of referenceY
     */
    public int getReferenceY() {
        return referenceY;
    }

    /**
     * Set the value of referenceY
     *
     * @param referenceY new value of referenceY
     */
    public void setReferenceY(int referenceY) {
        this.referenceY = referenceY;
    }    
    
    /**
     * Do we need to update?
     * 
     * @param scene the MoleculeScene object under consideration.
     * @param referenceX current reference X point
     * @param referenceY current reference Y point
     * @return true / false
     */
    private boolean isUpdateNeeded(MoleculeScene scene, 
                                  int referenceX, int referenceY) {           
        if (this.referenceX == referenceX) {
            if (this.referenceY == referenceY) {
                if (this.moleculeScene == scene) { // Object comparison, intended!
                    return false;
                } else {
                    return true;
                } // end if
            } else {
                return true;
            } // end if
        } else {
            return true;
        } // end if
    }
    
    private ScreenAtom currentNearestScreenAtom;    
    
    /**    
     * Get the nearest screen atom instance
     * 
     * @param scene the MoleculeScene object under consideration.
     * @param referenceX current reference X point
     * @param referenceY current reference Y point
     * @return the nearest ScreenAtom instance
     */
    public ScreenAtom getNearestScreenAtom(MoleculeScene scene, 
                                           int referenceX, int referenceY) {        
        if (_nearestAtom.isUpdateNeeded(scene, referenceX, referenceY)) {
            _nearestAtom.currentNearestScreenAtom 
                          = scene.getClosestScreenAtom(referenceX, referenceY);
            
            _nearestAtom.moleculeScene = scene;
            _nearestAtom.referenceX    = referenceX;
            _nearestAtom.referenceY    = referenceY;            
        } // end if
        
        return _nearestAtom.currentNearestScreenAtom;
    }
    
    private Point3D transformedPoint;
    
    /**
     * Get the transformed instance of point (referenceX, referenceY)
     * 
     * @param scene the MoleculeScene object under consideration.
     * @param referenceX current reference X point
     * @param referenceY current reference Y point
     * @return the transformed instance of point (referenceX, referenceY)
     */
    public Point3D getTransformedPoint(MoleculeScene scene, 
                                        int referenceX, int referenceY) {                   
        if (_transformPoint.isUpdateNeeded(scene, referenceX, referenceY)) {
            _transformPoint.moleculeScene = scene;
            _transformPoint.referenceX    = referenceX;
            _transformPoint.referenceY    = referenceY;
            
            _transformPoint.currentNearestScreenAtom 
                 = getNearestScreenAtom(_transformPoint.moleculeScene, 
                                        referenceX, referenceY);
        
            if (_transformPoint.currentNearestScreenAtom == null) {
                _transformPoint.transformedPoint = 
                     _transformPoint.moleculeScene.getTransform().inverse()
                           .transform(new Point3D(referenceX, referenceY, 0.0));
            } else {          
                Matrix3D tmat = _transformPoint.moleculeScene.getTransform();
                Point3D tpoint = tmat.transform(
                      _transformPoint.currentNearestScreenAtom.getAtom()
                                                              .getAtomCenter());                        
                _transformPoint.transformedPoint = 
                    tmat.inverse().transform(new Point3D(referenceX, referenceY,
                                                         tpoint.getZ()));
            } // end if    
        } // end if
                
        return _transformPoint.transformedPoint;
    }
}
