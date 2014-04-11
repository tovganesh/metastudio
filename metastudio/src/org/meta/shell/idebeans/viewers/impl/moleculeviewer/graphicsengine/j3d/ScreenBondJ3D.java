/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import com.sun.j3d.utils.geometry.Cylinder;
import java.awt.Graphics2D;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import org.meta.math.geom.Point3D;
import org.meta.config.impl.AtomInfo;

/**
 * Java3D implementation of ScreenBond
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenBondJ3D extends ScreenBond implements GlyphJ3D {
    /** for cylinder drawing */
    private double radius;
    private float theta, phi;
    private Point3D midPoint;      
    
    private ScreenBond screenBond;           
    
    public ScreenBondJ3D(ScreenBond screenBond) {
        super(screenBond.getAtom1(), screenBond.getAtom2());
        
        this.screenBond = screenBond;
    }
    
    @Override
    public void draw(Graphics2D g2d) {   
        // just empty
    }
    
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    @Override
    public void buildJava3DScene(TransformGroup objRot) {   
        radius = AtomInfo.getInstance().getCovalentRadius("H");
    
        if (this.screenBond instanceof WeakScreenBond) radius = 0.01;
        
        midPoint = atom2.getAtom().getAtomCenter()
                       .add(atom1.getAtom().getAtomCenter()).mul(0.5);        
        
        makeCylinder(atom1, midPoint, objRot);        
        makeCylinder(atom2, midPoint, objRot);
    }    
    
    /**
     * make the cylinder
     */
    private void makeCylinder(ScreenAtom atom, Point3D endPoint,
                              TransformGroup objRot) {        
        Point3D lengthAxis = endPoint.sub(atom.getAtom().getAtomCenter());        
        
        float RXY1 = (float) (lengthAxis.getX()*lengthAxis.getX() 
                               + lengthAxis.getY()*lengthAxis.getY());
        float R = RXY1 + (float) (lengthAxis.getZ()*lengthAxis.getZ());
        if (R <= 0.0)
            return;
        
        R = (float) Math.sqrt((double) R);

        phi = (float) Math.acos((double) (lengthAxis.getZ() / R));
        
        float RXY = (float) Math.sqrt((double) RXY1);
        
        if (RXY <= 0.0) {
            theta = 0.0f;
        } else {
            theta = (float) Math.acos((double) (lengthAxis.getX() / RXY));
            if (lengthAxis.getY() < 0.0)
                theta = (float) (2.0f * Math.PI) - theta;
        } // end if
        
        Transform3D t = new Transform3D();        
        t.rotZ(theta + (float) (Math.PI * 0.5f));
        
        Transform3D tmp = new Transform3D();
        tmp.rotX(phi + (float) (Math.PI * 0.5f));
        t.mul(tmp);

        Vector3f pos = new Vector3f(
                             (float) (atom.getX()+endPoint.getX())*0.5f, 
                             (float) (atom.getY()+endPoint.getY())*0.5f,
                             (float) (atom.getZ()+endPoint.getZ())*0.5f);        
        t.setTranslation(pos);
        TransformGroup bndScene = new TransformGroup(t);
        
        Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f objColor  = new Color3f(atom.getColor());

        Appearance app = new Appearance();
        Material mm = new Material(objColor, eColor, objColor, sColor, 100.0f);
	mm.setLightingEnable(true);
	app.setMaterial(mm);
        
        Cylinder c1 = new Cylinder((float) radius*0.2f, 
                                   (float) R, Cylinder.GENERATE_NORMALS,
                                   80, 80, app);
                
        bndScene.addChild(c1);
        objRot.addChild(bndScene);
    }
}
