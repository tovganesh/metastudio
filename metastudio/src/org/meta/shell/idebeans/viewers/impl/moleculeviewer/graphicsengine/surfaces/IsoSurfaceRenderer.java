/*
 * IsoSurfaceRenderer.java
 *
 * Created on November 25, 2005, 9:49 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces;

import org.meta.shell.idebeans.graphics.surfaces.GridPropertyRenderer;
import org.meta.shell.idebeans.graphics.surfaces.MarchingCubesTriSetGenerator;
import java.awt.*;
import java.util.*;

import java.io.IOException;

import org.meta.math.Vector3D;
import org.meta.math.geom.Point3DI;
import org.meta.math.geom.Triangle;
import org.meta.common.ColorToFunctionRangeMap;
import org.meta.math.geom.Point3D;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenCuboid;

/**
 * A class of type (Glyph and GridPropertyRenderer) for rendering an isosurface.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IsoSurfaceRenderer extends GridPropertyRenderer {
    
    protected MarchingCubesTriSetGenerator triSetGenerator;
    
    protected ScreenCuboid boundingBox;
    
    /** Creates a new instance of IsoSurfaceRenderer */
    public IsoSurfaceRenderer(GridProperty gridProperty) {
        super();
        
        setGridProperty(gridProperty);
        
        this.colorMap = new ColorToFunctionRangeMap(Color.blue, Color.red,
                                gridProperty.getMinFunctionValue(),
                                gridProperty.getMaxFunctionValue());
        
        currentFunctionValue = 0.0;
        
        triSetGenerator = new MarchingCubesTriSetGenerator(gridProperty,  
                                                          currentFunctionValue);
        
        this.boundingBox  = new ScreenCuboid(gridProperty.getBoundingBox());
        this.boundingBox.setDrawCenterMarker(false); 
        
        this.generateRIBVertexNormals = true;
        
        this.fillPolygons = false;
        this.fillTransperency = 128;
    }
    
    /**
     * The isosurface is rendered from here!
     */
    @Override
    public void draw(Graphics2D g2d) {        
        // setup isosurface color
        g2d.setColor(colorMap.getInterpolatedColor(currentFunctionValue));
        
        // transform
        triSetGenerator.setTransform(transform);
        triSetGenerator.applyTransforms();
        
        // and draw it!
        if (!fillPolygons) {
            while(true) {
                Triangle t = triSetGenerator.nextVisibleTriSet();
                
                if (t == null) break;
                if (textureFunction != null) {
                    Point3DI point = t.getPoint1();
                    Color color = textureFunction.getColorValueAt(point.getX(), 
                                                                  point.getY(),
                                                                  point.getZ());
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), 
                                   color.getBlue(), fillTransperency));
                } // end if
                
                g2d.drawPolyline(t.getXPointsI(), t.getYPointsI(), 3);
            } // end while
        } else {
            Color color = colorMap.getInterpolatedColor(currentFunctionValue);
            g2d.setColor(new Color(color.getRed(), color.getGreen(), 
                                   color.getBlue(), fillTransperency));
            
            while(true) {
                Triangle t = triSetGenerator.nextVisibleTriSet();
                
                if (t == null) break;
                if (textureFunction != null) {
                    Point3DI point = t.getPoint1();
                    color = textureFunction.getColorValueAt(point.getX(), 
                                                            point.getY(),
                                                            point.getZ());
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), 
                                           color.getBlue(), fillTransperency));
                } // end if
                
                g2d.fillPolygon(t.getXPointsI(), t.getYPointsI(), 3);            
            } // end while
        } // end if
        
        // draw the bounding box if needed
        if (showBoundingBox) {
            boundingBox.setTransform(transform);
            boundingBox.applyTransforms();
            boundingBox.draw(g2d);
        } // end if
    }
        
    /**
     * The isosurface rendering for RIB is done from here!
     */
    @Override
    public void drawInRIBFile(java.io.FileWriter fos, String shader,
                              double scaleFactor) throws IOException { 
        fos.write("AttributeBegin\n");        
        fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n"); 
        
        Color theColor = colorMap.getInterpolatedColor(currentFunctionValue);
        
        fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                         );                
        
        double trasnp = 1.0 - (fillTransperency / 255.0);
        fos.write("Opacity " + trasnp
                               + " " + trasnp
                               + " " + trasnp
                               + "\n"
                         );
        
        // transform
        triSetGenerator.setTransform(transform);
        triSetGenerator.applyTransforms();
        
        Point3DI point1, point2, point3;
        
        if (generateRIBVertexNormals) {
            Vector3D n1, n2, n3, n4;
            ArrayList<Vector3D> vertexNormals 
                                      = triSetGenerator.getVertexNormals();

            // and draw it!        
            while(true) {
                Triangle t = triSetGenerator.nextVisibleTriSet();

                if (t == null) break;

                point1 = t.getPoint1();
                point2 = t.getPoint2();
                point3 = t.getPoint3();
                
                if (textureFunction != null) {                    
                    theColor = textureFunction.getColorValueAt(point1.getX(), 
                                                               point1.getY(),
                                                               point1.getZ());
                    fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                         );        
                    fos.write("Opacity " + trasnp
                               + " " + trasnp
                               + " " + trasnp
                               + "\n"
                         );
                } // end if
                

                n1 = transform.transform(vertexNormals.get(point1.getIndex()));
                n2 = transform.transform(vertexNormals.get(point2.getIndex()));
                n3 = transform.transform(vertexNormals.get(point3.getIndex()));            
                n4 = (n1.add(n2).add(n3).mul(1.0/3.0)).normalize();

                point1 = transform.transform(point1);
                point2 = transform.transform(point2);
                point3 = transform.transform(point3);                            
                
                fos.write("Polygon \"P\" [ "
                     + point1.getX() + " " + point1.getY() + " " + point1.getZ() 
                     + " "
                     + point2.getX() + " " + point2.getY() + " " + point2.getZ()
                     + " "
                     + point3.getX() + " " + point3.getY() + " " + point3.getZ()
                     + " ] \"N\" [ "
                     + n1.getI() + " " + n1.getJ() + " " + n1.getK() + " "
                     + n2.getI() + " " + n2.getJ() + " " + n2.getK() + " "
                     + n3.getI() + " " + n3.getJ() + " " + n3.getK() + " "
                     + n4.getI() + " " + n4.getJ() + " " + n4.getK()                       
                     + " ]\n");   
            } // end while             
        } else {
            // and draw it!        
            while(true) {
                Triangle t = triSetGenerator.nextVisibleTriSet();

                if (t == null) break;

                point1 = t.getPoint1();
                point2 = t.getPoint2();
                point3 = t.getPoint3();
                
                if (textureFunction != null) {                    
                    theColor = textureFunction.getColorValueAt(point1.getX(), 
                                                               point1.getY(),
                                                               point1.getZ());
                    fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                         );        
                    fos.write("Opacity " + trasnp
                               + " " + trasnp
                               + " " + trasnp
                               + "\n"
                         );
                } // end if
                
                point1 = transform.transform(point1);
                point2 = transform.transform(point2);
                point3 = transform.transform(point3);                            

                fos.write("Polygon \"P\" [ "
                     + point1.getX() + " " + point1.getY() + " " + point1.getZ() 
                     + " "
                     + point2.getX() + " " + point2.getY() + " " + point2.getZ()
                     + " "
                     + point3.getX() + " " + point3.getY() + " " + point3.getZ()
                     + " ]\n");   
            } // end while
        } // end if
        
        fos.write("AttributeEnd\n");                
    }                    
            
    
    
    /**
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // TODO:
    }   
    
    /**
     * Setter for property currentFunctionValue.
     * @param currentFunctionValue New value of property currentFunctionValue.
     */
    @Override
    public void setCurrentFunctionValue(double currentFunctionValue) {                
        triSetGenerator.setCurrentFunctionValue(currentFunctionValue);                
        
        super.setCurrentFunctionValue(currentFunctionValue);
    }

    /**
     * Holds value of property generateRIBVertexNormals.
     */
    private boolean generateRIBVertexNormals;

    /**
     * Getter for property generateRIBVertexNormals.
     * @return Value of property generateRIBVertexNormals.
     */
    public boolean isGenerateRIBVertexNormals() {
        return this.generateRIBVertexNormals;
    }

    /**
     * Setter for property generateRIBVertexNormals.
     * @param generateRIBVertexNormals New value of property 
     *        generateRIBVertexNormals.
     */
    public void setGenerateRIBVertexNormals(boolean generateRIBVertexNormals) {
        this.generateRIBVertexNormals = generateRIBVertexNormals;
    }

    /**
     * Holds value of property fillPolygons.
     */
    private boolean fillPolygons;

    /**
     * Getter for property fillPolygons.
     * @return Value of property fillPolygons.
     */
    public boolean isFillPolygons() {
        return this.fillPolygons;
    }

    /**
     * Setter for property fillPolygons.
     * @param fillPolygons New value of property fillPolygons.
     */
    public void setFillPolygons(boolean fillPolygons) {
        this.fillPolygons = fillPolygons;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }

    /**
     * Holds value of property fillTransperency.
     */
    protected int fillTransperency;

    /**
     * Getter for property fillTransperency.
     * @return Value of property fillTransperency.
     */
    public int getFillTransperency() {
        return this.fillTransperency;
    }

    /**
     * Getter for property fillTransperency.
     * @return Value of property fillTransperency.
     */
    public int getFillTransperencyMax() {
        return 255;
    }
    
    /**
     * Getter for property fillTransperency.
     * @return Value of property fillTransperency.
     */
    public int getFillTransperencyMin() {
        return 0;
    }
    
    /**
     * Setter for property fillTransperency.
     * @param fillTransperency New value of property fillTransperency.
     */
    public void setFillTransperency(int fillTransperency) {
        this.fillTransperency = fillTransperency;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }
    
    /**
     * Get the closest visible point from the currently displaying property.
     *
     * @param point the point object (probably from a mouse click)
     * @return PointProperty object, giving the nearest point and the value at
     *          that point
     * @throws PropertySceneChangeListener if the point object queried for
     *         doesn't lie near any of the visible grid points.
     */
    @Override
    public PointProperty getClosestVisiblePoint(Point point) 
                            throws PropertyNotDefinedException {
      
        PointProperty pp = new PointProperty();
        
        // transform
        triSetGenerator.setTransform(transform);
        triSetGenerator.applyTransforms();
        
        int [] x, y;
        int i;
        
        // TODO: optimize this 
        
        while(true) {
            Triangle t = triSetGenerator.nextVisibleTriSet();
            
            if (t == null) break;
            
            x = t.getXPointsI();
            y = t.getYPointsI();
            
            for(i=0; i<3; i++) {
                if (point.x >= x[i]-5 && point.x <= x[i]+5 
                    && point.y >= y[i]-5 && point.y <= y[i]+5) {
                    pp.setPoint(new Point3D(t.getXPoints()[i],
                                            t.getYPoints()[i],
                                            t.getZPoints()[i]));
                    pp.setValue(gridProperty.getFunctionValueAt(pp.getPoint()));
                    
                    return pp;
                } // end if
            } // end for
        } // end while
        
        // if the control reaches here then we throw an exception;
        throw new PropertyNotDefinedException();
    }
    
} // end of class IsoSurfaceRenderer
