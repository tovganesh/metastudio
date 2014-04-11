/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.IsoSurfaceRenderer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d.GlyphJ3D;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import org.meta.math.geom.Point3DI;
import org.meta.math.geom.Triangle;
import org.meta.math.Matrix3D;
import org.meta.molecule.property.electronic.GridProperty;

/**
 * Java 3D implementation of IsoSurfaceRenderer
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IsoSurfaceJ3DRenderer extends IsoSurfaceRenderer 
                                   implements GlyphJ3D {
    
    public IsoSurfaceJ3DRenderer(GridProperty gridProperty) {
        super(gridProperty);
    }
        
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    @Override
    public void buildJava3DScene(TransformGroup objRot) {
        if (objRot == null) return;
                
        // transform
        Matrix3D tmat = new Matrix3D(); 
        tmat.unit();
        triSetGenerator.setTransform(tmat);
        triSetGenerator.applyTransforms();                
        
        TransformGroup surfaceScene = new TransformGroup();
        
        // setup isosurface color
        Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f objColor  = new Color3f(
                           colorMap.getInterpolatedColor(currentFunctionValue));

        Appearance app = new Appearance();
        Material mm = new Material(objColor, eColor, objColor, sColor, 100.0f);
	mm.setLightingEnable(true);
	app.setMaterial(mm);
        
        if (fillTransperency > 0) {
           app.setTransparencyAttributes(new TransparencyAttributes(
              TransparencyAttributes.NICEST, (float) (fillTransperency/255.0)));
        } // end if
                
        TriangleArray ta = new TriangleArray(triSetGenerator.size()*3,
                                             GeometryArray.COORDINATES);
        Point3DI p1, p2, p3;
        
        // and draw it!
        int i=0;        
        while(true) {
            Triangle t = triSetGenerator.nextVisibleTriSet();
                
            if (t == null) break;
            
            p1 = t.getPoint1();
            p2 = t.getPoint2();
            p3 = t.getPoint3();
                
            ta.setCoordinate(i++, new Point3d(p1.getX(), p1.getY(), p1.getZ()));
            ta.setCoordinate(i++, new Point3d(p2.getX(), p2.getY(), p2.getZ()));
            ta.setCoordinate(i++, new Point3d(p3.getX(), p3.getY(), p3.getZ()));
        } // end while
        
        GeometryInfo gi = new GeometryInfo(ta);        
        gi.compact();        
        gi.recomputeIndices();
        
        // generate normals
        NormalGenerator ng = new NormalGenerator();        
        ng.generateNormals(gi);
        
        // stripify
        Stripifier st = new Stripifier();
        st.stripify(gi);        
         
        surfaceScene.addChild(new Shape3D(gi.getGeometryArray(), app));
        
        if (showBoundingBox) {
            boundingBox.setTransform(transform);
            boundingBox.applyTransforms();
            (new ScreenCuboidJ3D(boundingBox)).buildJava3DScene(surfaceScene);
        } // end if
        
        objRot.addChild(surfaceScene);               
    }
}
