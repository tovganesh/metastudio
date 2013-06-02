/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import org.meta.math.geom.BoundingBox;
import org.meta.common.resource.ColorResource;

/**
 * Java3D implementation of ScreenCuboid
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenCuboidJ3D extends ScreenCuboid implements GlyphJ3D {
    
    /** Creates a new instance of Cuboid */
    public ScreenCuboidJ3D(ScreenCuboid cuboid) {
        super(cuboid.getBoundingBox());
    }
    
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    @Override
    public void buildJava3DScene(TransformGroup objRot) {
        if (objRot == null) return;
        
        ColorResource colorResource = ColorResource.getInstance();
        
        Transform3D t = new Transform3D();
        Vector3d pos = new Vector3d((float) 
                                      (boundingBox.getUpperLeft().getX()
                                       + (boundingBox.getXWidth() / 2.0)), 
                                    (float) 
                                      (boundingBox.getUpperLeft().getY()
                                       + (boundingBox.getYWidth() / 2.0)),
                                    (float) 
                                      (boundingBox.getUpperLeft().getZ()
                                       + (boundingBox.getZWidth() / 2.0)));
        t.set(pos);
        TransformGroup boxScene = new TransformGroup(t);
        
        Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f objColor  = new Color3f(colorResource.getScreenCuboidColor());

        Appearance app = new Appearance();
        Material mm = new Material(objColor, eColor, objColor, sColor, 100.0f);
	mm.setLightingEnable(true);
	app.setMaterial(mm);
        app.setPolygonAttributes(new PolygonAttributes(
                                       PolygonAttributes.POLYGON_LINE,
                                       PolygonAttributes.CULL_NONE,
                                       0.0f));
        Box box = new Box((float) boundingBox.getXWidth(), 
                         (float) boundingBox.getYWidth(), 
                         (float) boundingBox.getZWidth(), 
                         app);
        boxScene.addChild(box);
        objRot.addChild(boxScene);
    }
}
