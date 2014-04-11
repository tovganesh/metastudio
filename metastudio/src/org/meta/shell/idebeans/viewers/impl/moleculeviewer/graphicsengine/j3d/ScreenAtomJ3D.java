/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import org.meta.molecule.Atom;

/**
 * Java3D implementation of ScreenAtom
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenAtomJ3D extends ScreenAtom implements GlyphJ3D {
    
    private ScreenAtom screenAtom;
    
    public ScreenAtomJ3D(Atom a) {
        this(new ScreenAtom(a));
    }
    
    public ScreenAtomJ3D(ScreenAtom a) {
        super(a.getAtom());
        
        this.screenAtom = a;
    }
    
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    @Override
    public void buildJava3DScene(TransformGroup objRot) {        
        Transform3D t = new Transform3D();
        Vector3d pos = new Vector3d((float) atom.getX(), 
                                    (float) atom.getY(),
                                    (float) atom.getZ());
        t.set(pos);
        TransformGroup atmScene = new TransformGroup(t);
        
        Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f objColor  = new Color3f(color);

        Appearance app = new Appearance();
        Material mm = new Material(objColor, eColor, objColor, sColor, 100.0f);
        mm.setLightingEnable(true);
        app.setMaterial(mm);
        Sphere sp = new Sphere((float) covalentRadius/4.0f, 
                               Sphere.GENERATE_NORMALS, 
                               80, app);        
        
        atmScene.addChild(sp);
        objRot.addChild(atmScene);
    }
}
