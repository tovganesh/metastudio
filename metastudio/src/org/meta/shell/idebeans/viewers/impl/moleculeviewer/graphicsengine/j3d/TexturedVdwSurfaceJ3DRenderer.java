/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.TexturedVdwSurfaceRenderer;
import org.meta.shell.idebeans.graphics.surfaces.SphereTriSetGenerator;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import org.meta.math.geom.Point3DI;
import org.meta.math.geom.Triangle;
import org.meta.config.impl.AtomInfo;
import org.meta.math.Matrix3D;
import org.meta.molecule.Atom;
import org.meta.molecule.property.electronic.GridProperty;

/**
 * Java 3D implementation of TexturedVdwSurfaceRenderer
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TexturedVdwSurfaceJ3DRenderer extends TexturedVdwSurfaceRenderer 
                                           implements GlyphJ3D {
    
    public TexturedVdwSurfaceJ3DRenderer(GridProperty gridProperty, 
                                         MoleculeScene moleculeScene) {
        super(gridProperty, moleculeScene);
    }
    
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    @Override
    public void buildJava3DScene(TransformGroup objRot) {
        if (objRot == null) return;

        AtomInfo ai = AtomInfo.getInstance();
        double xMin = gridProperty.getBoundingBox().getUpperLeft().getX(),
               yMin = gridProperty.getBoundingBox().getUpperLeft().getY(),
               zMin = gridProperty.getBoundingBox().getUpperLeft().getZ();        
        double f1, f2, f3;
        double [] functionValues = gridProperty.getFunctionValues();
        double incInX = gridProperty.getXIncrement(),
               incInY = gridProperty.getYIncrement(),
               incInZ = gridProperty.getZIncrement();
        int pointsAlongX = gridProperty.getNoOfPointsAlongX(),
            pointsAlongY = gridProperty.getNoOfPointsAlongY(),
            pointsAlongZ = gridProperty.getNoOfPointsAlongZ();
        
        int i, j, k;                
        
        Atom atom;
        String symbol ;
        SphereTriSetGenerator stsg;        
        Point3DI point1, point2, point3;  
        
        int noOfAtoms  = molecule.getNumberOfAtoms();
        int pointsAlongSlice = pointsAlongY*pointsAlongZ;        
        HashMap<String, SphereTriSetGenerator> triSetTableTmp = 
                new HashMap<String, SphereTriSetGenerator>(5);
        
        if (noOfAtoms > largeMoleculeSize) triSetTableTmp = triSetTable;
                
        Matrix3D tmat = new Matrix3D();
        tmat.unit();
        
        TransformGroup surfaceScene = new TransformGroup();
                                        
        for(int atomIndex=0; atomIndex<noOfAtoms; atomIndex++) {
            atom = molecule.getAtom(atomIndex);
            
            // check to see if we are with in the bounding box?
            if (!gridProperty.getBoundingBox().contains(atom.getAtomCenter()))
                continue;
            
            symbol = atom.getSymbol();                        
            
            if ((stsg = triSetTableTmp.get(symbol)) == null) {
                stsg = new SphereTriSetGenerator(atom.getAtomCenter(), 
                                                 ai.getVdwRadius(symbol),
                                                 numberOfSphereDivisions);
                triSetTableTmp.put(symbol, stsg);           
            } // end if
        
            // setup color
            Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
            Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
            Color3f objColor  = new Color3f(
                    colorMap.getInterpolatedColor(currentFunctionValue));
            
            Appearance app = new Appearance();
            Material mm = new Material(objColor, eColor, objColor, sColor,
                                       100.0f);
            mm.setLightingEnable(true);
            app.setMaterial(mm);
            
            if (fillTransperency > 0) {
             app.setTransparencyAttributes(new TransparencyAttributes(
              TransparencyAttributes.NICEST, (float) (fillTransperency/255.0)));
            } // end if
                            
            int idx = 0;
            
            stsg.setTransform(tmat);
            stsg.setCenter(atom.getAtomCenter()); 
                        
            ArrayList<Point3d> validPoints = new ArrayList<Point3d>();
            ArrayList<Color3f> validColors = new ArrayList<Color3f>();
            
            while(true) {
                Triangle t = stsg.nextVisibleTriSet();
                                
                if (t == null) break;
                
                // first point
                point1 = t.getPoint1();
                i = (int) Math.round(Math.abs((point1.getX()-xMin)/incInX));
                j = (int) Math.round(Math.abs((point1.getY()-yMin)/incInY));
                k = (int) Math.round(Math.abs((point1.getZ()-zMin)/incInZ));
                     
                if (i > pointsAlongX-1) i = pointsAlongX-1;
                if (j > pointsAlongY-1) j = pointsAlongY-1;
                if (k > pointsAlongZ-1) k = pointsAlongZ-1;
                
                f1 = functionValues[(i*pointsAlongSlice)
                                    + (j*pointsAlongZ) + k];
                
                // second point
                point2 = t.getPoint2();
                i = (int) Math.round(Math.abs((point2.getX()-xMin)/incInX));
                j = (int) Math.round(Math.abs((point2.getY()-yMin)/incInY));
                k = (int) Math.round(Math.abs((point2.getZ()-zMin)/incInZ));
                
                if (i > pointsAlongX-1) i = pointsAlongX-1;
                if (j > pointsAlongY-1) j = pointsAlongY-1;
                if (k > pointsAlongZ-1) k = pointsAlongZ-1;
                
                f2 = functionValues[(i*pointsAlongSlice)
                                    + (j*pointsAlongZ) + k];
                
                // third point
                point3 = t.getPoint3();
                i = (int) Math.round(Math.abs((point3.getX()-xMin)/incInX));
                j = (int) Math.round(Math.abs((point3.getY()-yMin)/incInY));
                k = (int) Math.round(Math.abs((point3.getZ()-zMin)/incInZ));
                
                if (i > pointsAlongX-1) i = pointsAlongX-1;
                if (j > pointsAlongY-1) j = pointsAlongY-1;
                if (k > pointsAlongZ-1) k = pointsAlongZ-1;
                
                f3 = functionValues[(i*pointsAlongSlice)
                                    + (j*pointsAlongZ) + k]; 
                                          
                validPoints.add(
                      new Point3d(point1.getX(), point1.getY(), point1.getZ()));        
                validColors.add(
                            new Color3f(colorMap.getInterpolatedColor(f1)));
                                
                validPoints.add(
                      new Point3d(point2.getX(), point2.getY(), point2.getZ()));
                validColors.add(
                            new Color3f(colorMap.getInterpolatedColor(f2)));
                                
                validPoints.add(
                      new Point3d(point3.getX(), point3.getY(), point3.getZ()));
                validColors.add(
                            new Color3f(colorMap.getInterpolatedColor(f3)));
            } // end while
                        
            TriangleArray ta = new TriangleArray(validPoints.size(),
                               GeometryArray.COORDINATES|GeometryArray.COLOR_3);
                    
            for(idx=0; idx<validPoints.size(); idx++) {
                ta.setCoordinate(idx, validPoints.get(idx));
                ta.setColor(idx, validColors.get(idx));
            }
            
            validColors = null; validPoints = null;
            
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
        } // end for          
        
        if (showBoundingBox) {
            boundingBox.setTransform(transform);
            boundingBox.applyTransforms();
            (new ScreenCuboidJ3D(boundingBox)).buildJava3DScene(surfaceScene);
        } // end if
        
        objRot.addChild(surfaceScene);
    }
}
