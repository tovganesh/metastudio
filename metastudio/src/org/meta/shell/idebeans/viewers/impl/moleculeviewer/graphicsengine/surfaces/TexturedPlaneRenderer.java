/*
 * TexturedPlaneRenderer.java
 *
 * Created on September 17, 2005, 7:56 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces;

import org.meta.shell.idebeans.graphics.surfaces.GridPropertyRenderer;
import java.awt.*;
import java.util.*;

import java.io.IOException;
import javax.media.j3d.TransformGroup;

import org.meta.common.ColorToFunctionRangeMap;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenCuboid;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.PropertySceneChangeListener;

/**
 * A class of type (Glyph and GridPropertyRenderer) for rendering textured 
 * planes.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TexturedPlaneRenderer extends GridPropertyRenderer {
    
    private double fixedPlaneStart, fixedPlaneEnd,
                   varPlane1Start, varPlane1End,
                   varPlane2Start, varPlane2End,
                   fixedPlaneIncrement, varPlane1Increment, 
                   varPlane2Increment;
    private int noOfPointsAlongFixedPlane,
                noOfPointsAlongVar1Plane,
                noOfPointsAlongVar2Plane;
    
    private ScreenCuboid boundingBox;
    private TriSetGenerator generator;
    
    /** Creates a new instance of TexturedPlaneRenderer */
    public TexturedPlaneRenderer(GridProperty gridProperty) {
        super();
        
        setGridProperty(gridProperty);
        this.texturePlane = TexturePlane.Z_PLANE;
        this.generator    = new ZTriSetGenerator();        
        this.boundingBox  = new ScreenCuboid(gridProperty.getBoundingBox());
        this.boundingBox.setDrawCenterMarker(false);                
                        
        currentFunctionValue = 0.0;
        
        displayPlane = 0;
        
        ArrayList<Color> colorRange = new ArrayList<Color>();
                
        colorRange.add(new Color(0.0f, 0.0f, 1.0f));
        colorRange.add(new Color(0.0f, 0.1f, 0.9f));
        colorRange.add(new Color(0.0f, 0.2f, 0.8f));
        colorRange.add(new Color(0.0f, 0.3f, 0.7f));
        colorRange.add(new Color(0.0f, 0.4f, 0.6f));
        colorRange.add(new Color(0.0f, 0.5f, 0.5f));
        colorRange.add(new Color(0.0f, 0.6f, 0.4f));
        colorRange.add(new Color(0.0f, 0.7f, 0.3f));
        colorRange.add(new Color(0.0f, 0.8f, 0.2f));
        colorRange.add(new Color(0.0f, 0.9f, 0.1f));
        colorRange.add(new Color(1.0f, 1.0f, 1.0f));
        colorRange.add(new Color(0.9f, 0.1f, 0.0f));
        colorRange.add(new Color(0.8f, 0.2f, 0.0f));
        colorRange.add(new Color(0.7f, 0.2f, 0.0f));
        colorRange.add(new Color(0.7f, 0.3f, 0.0f));
        colorRange.add(new Color(0.6f, 0.4f, 0.0f));
        colorRange.add(new Color(0.5f, 0.5f, 0.0f));
        colorRange.add(new Color(0.6f, 0.4f, 0.0f));
        colorRange.add(new Color(0.7f, 0.3f, 0.0f));
        colorRange.add(new Color(0.8f, 0.2f, 0.0f));
        colorRange.add(new Color(0.9f, 0.1f, 0.0f));
        colorRange.add(new Color(1.0f, 0.0f, 0.0f));
                
        this.colorMap = new ColorToFunctionRangeMap(colorRange,
                                gridProperty.getMinFunctionValue(),
                                gridProperty.getMaxFunctionValue());
    }    

    /**
     * The plane texturing is done from here!
     */
    @Override
    public void draw(Graphics2D g2d) {
        // set up the plane variables
        setPlaneVariables(); 
        
        int a, b, c;
        double x, y, z;                  
        
        generator.init();
                
        for(a=0, z=fixedPlaneStart; a<noOfPointsAlongFixedPlane; a++, 
                             z=fixedPlaneStart + (a*fixedPlaneIncrement)) {
            for(b=0, x=varPlane1Start; b<noOfPointsAlongVar1Plane-1; b++, 
                             x=varPlane1Start + (b*varPlane1Increment)) {                
                for(c=0, y=varPlane2Start; c<noOfPointsAlongVar2Plane-1; c++, 
                             y=varPlane2Start + (c*varPlane2Increment)) {
                    
                    generator.generateNext(g2d, x, y, z, a); 
                } // end for (y)   
                
                generator.incrementLoop2I();
            } // end for (x) 
            
            generator.incrementLoop3I();
        } // end for (z)      
        
        // draw the bounding box if needed
        if (showBoundingBox) {
            boundingBox.setTransform(transform);
            boundingBox.applyTransforms();
            boundingBox.draw(g2d);
        } // end if
    }   
    
    /**
     * The textured plane rendering for RIB is done from here!
     */
    @Override
    public void drawInRIBFile(java.io.FileWriter fos, String shader,
                              double scaleFactor) throws IOException { 
        // set up the plane variables
        setPlaneVariables(); 
        
        int a, b, c;
        double x, y, z;                  
        
        generator.init();
                
        fos.write("AttributeBegin\n");
        fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n");
        
        for(a=0, z=fixedPlaneStart; a<noOfPointsAlongFixedPlane; a++, 
                             z=fixedPlaneStart + (a*fixedPlaneIncrement)) {
            for(b=0, x=varPlane1Start; b<noOfPointsAlongVar1Plane-1; b++, 
                             x=varPlane1Start + (b*varPlane1Increment)) {                
                for(c=0, y=varPlane2Start; c<noOfPointsAlongVar2Plane-1; c++, 
                             y=varPlane2Start + (c*varPlane2Increment)) {
                    
                    generator.generateNextInRIB(fos, shader, scaleFactor,
                                                x, y, z, a);
                } // end for (y)   
                
                generator.incrementLoop2I();
            } // end for (x) 
            
            generator.incrementLoop3I();
        } // end for (z)      
        
        fos.write("AttributeEnd\n");
        
        // TODO: draw the bounding box if needed
        // if (showBoundingBox) {
        //    boundingBox.setTransform(transform);
        //    boundingBox.applyTransforms();
        //    boundingBox.draw(g2d);
        // } // end if
    }
    
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    public void buildJava3DScene(TransformGroup objRot) {
        if (objRot == null) return;
        
        // TODO:
    }
    
    /**
     * setup variables defining the plane
     */
    private void setPlaneVariables() {
        switch(texturePlane) {
            case X_PLANE:
                fixedPlaneStart = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getX();
                fixedPlaneEnd   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getX();
                fixedPlaneIncrement       = gridProperty.getXIncrement();
                noOfPointsAlongFixedPlane = gridProperty.getNoOfPointsAlongX();
                
                varPlane1Start = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getY();
                varPlane1End   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getY();
                varPlane1Increment       = gridProperty.getYIncrement();
                noOfPointsAlongVar1Plane = gridProperty.getNoOfPointsAlongY();
                
                
                varPlane2Start = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getZ();
                varPlane2End   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getZ();
                varPlane2Increment       = gridProperty.getZIncrement();
                noOfPointsAlongVar2Plane = gridProperty.getNoOfPointsAlongZ();
                                
                displayPlaneMin = 0;
                displayPlaneMax = noOfPointsAlongFixedPlane;
                
                generator = new XTriSetGenerator();
                
                break;
            case Y_PLANE:
                fixedPlaneStart = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getY();
                fixedPlaneEnd   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getY();
                fixedPlaneIncrement       = gridProperty.getYIncrement();
                noOfPointsAlongFixedPlane = gridProperty.getNoOfPointsAlongY();
                
                varPlane1Start = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getX();
                varPlane1End   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getX();
                varPlane1Increment       = gridProperty.getXIncrement();
                noOfPointsAlongVar1Plane = gridProperty.getNoOfPointsAlongX();
                
                
                varPlane2Start = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getZ();
                varPlane2End   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getZ();
                varPlane2Increment       = gridProperty.getZIncrement();
                noOfPointsAlongVar2Plane = gridProperty.getNoOfPointsAlongZ();
                                
                displayPlaneMin = 0;
                displayPlaneMax = noOfPointsAlongFixedPlane;
                
                generator = new YTriSetGenerator();
                
                break;
            case Z_PLANE:
                fixedPlaneStart = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getZ();
                fixedPlaneEnd   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getZ();
                fixedPlaneIncrement       = gridProperty.getZIncrement();
                noOfPointsAlongFixedPlane = gridProperty.getNoOfPointsAlongZ();
                
                varPlane1Start = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getX();
                varPlane1End   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getX();
                varPlane1Increment       = gridProperty.getXIncrement();
                noOfPointsAlongVar1Plane = gridProperty.getNoOfPointsAlongX();
                
                
                varPlane2Start = gridProperty.getBoundingBox()
                                                  .getUpperLeft().getY();
                varPlane2End   = gridProperty.getBoundingBox()
                                                  .getBottomRight().getY();
                varPlane2Increment       = gridProperty.getYIncrement();
                noOfPointsAlongVar2Plane = gridProperty.getNoOfPointsAlongY();
                                
                displayPlaneMin = 0;
                displayPlaneMax = noOfPointsAlongFixedPlane;
                        
                generator = new ZTriSetGenerator();
                
                break;
        } // end of switch case
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
        // TODO:
        
        // if the control reaches here then we throw an exception;
        throw new PropertyNotDefinedException();
    }
    
    /**
     * Overidden Setter for property currentFunctionValue.
     * @param currentFunctionValue New value of property currentFunctionValue.
     */
    @Override
    public void setCurrentFunctionValue(double currentFunctionValue) {
        this.currentFunctionValue = currentFunctionValue;
    } 
    
    /**
     * Holds value of property displayPlane.
     */
    private int displayPlane;
    
    /**
     * Getter for property displayPlane.
     * @return Value of property displayPlane.
     */
    public int getDisplayPlane() {
        return this.displayPlane;
    }
    
    /**
     * Setter for property displayPlane.
     * @param displayPlane New value of property displayPlane.
     */
    public void setDisplayPlane(int displayPlane) {
        this.displayPlane = displayPlane;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }
    
    /**
     * Holds value of property displayPlaneMax.
     */
    private int displayPlaneMax;
    
    /**
     * Getter for property displayPlaneMax.
     * @return Value of property displayPlaneMax.
     */
    public int getDisplayPlaneMax() {
        return displayPlaneMax;
    }
    
    /**
     * Holds value of property displayPlaneMax.
     */
    private int displayPlaneMin;
    
    /**
     * Getter for property displayPlaneMin.
     * @return Value of property displayPlaneMin.
     */
    public int getDisplayPlaneMin() {
        return displayPlaneMin;
    }
    
    /**
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // TODO:
    }
        
    /**
     * The texture plane to be drawn
     */
    public enum TexturePlane {
        X_PLANE, 
        Y_PLANE,
        Z_PLANE
    }

    /**
     * Holds value of property texturePlane.
     */
    private TexturePlane texturePlane;

    /**
     * Getter for property texturePlane.
     * @return Value of property texturePlane.
     */
    public TexturePlane getTexturePlane() {
        return this.texturePlane;
    }

    /**
     * Setter for property texturePlane.
     * @param texturePlane New value of property texturePlane.
     */
    public void setTexturePlane(TexturePlane texturePlane) {
        this.texturePlane = texturePlane;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }
    
    /**
     * inner interface that defines how a particular tri set will
     * be generated.
     */
    private interface TriSetGenerator {
        /** initilize the tri set generator */
        public void init();
        
        /** generate the next tri set */
        public void generateNext(Graphics2D g2d, double x, double y, double z,
                                 int currentPlane);
        
        /** generate the next contour line in a RIB file */
        public void generateNextInRIB(java.io.FileWriter fos,
                                      String shader, double scaleFactor,
                                      double x, double y, double z,
                                      int currentPlane)
                                        throws IOException;
        
        /** increment the function index access counter - for loop 2 */
        public void incrementLoop2I();
        
        /** increment the function index access counter - for loop 3 */
        public void incrementLoop3I();
    } // end of inner interface TriSetGenerator
    
    /**
     * Generate X-plane trisets
     */
    private class XTriSetGenerator implements TriSetGenerator {
        
        private int i, j, i1, j1, l, m;
        private double ijVal, ij1Val, i1jVal, i1j1Val;
                
        private double [] xLine, yLine, zLine;
        private int [] xLineI, yLineI;
        
        private double [] functionValue;
        
        public XTriSetGenerator() { }
        
        /** initilize the tri set generator */
        @Override
        public void init() {
            functionValue = gridProperty.getFunctionValues();
            
            i = l = m = 0;
            
            xLine = new double[] {0.0, 0.0, 0.0, 0.0};
            yLine = new double[] {0.0, 0.0, 0.0, 0.0};
            zLine = new double[] {0.0, 0.0, 0.0, 0.0};
            
            xLineI   = new int [] {0, 0, 0};
            yLineI   = new int [] {0, 0, 0}; 
        }
        
        /** generate the next tri set */
        @Override
        public void generateNext(Graphics2D g2d, double y, double z, double x,
                                 int currentPlane) {
            
            if (currentPlane != displayPlane) {
                // get to the next point
                i++;
                
                return;
            } // end if
            
            // get correct positions of function values
            j  = i + noOfPointsAlongVar2Plane;
            i1 = i + 1;
            j1 = j + 1;

            // decide the triangles and their fill colors
            ijVal   = functionValue[i];
            ij1Val  = functionValue[i1];
            i1jVal  = functionValue[j];
            i1j1Val = functionValue[j1];

            // first set of triangles
            currentFunctionValue = (ijVal+i1jVal+ij1Val)/3.0;
            g2d.setColor(colorMap.getInterpolatedColor(
                                     currentFunctionValue));

            xLine[0] = xLine[1] = xLine[2] = xLine[3] = x;

            yLine[0] = y; zLine[0] = z; 
            yLine[1] = y+varPlane1Increment; zLine[1] = z;
            yLine[2] = y; zLine[2] = z+varPlane2Increment;
            yLine[3] = y+varPlane1Increment; zLine[3] = z+varPlane2Increment; 

            transform.transform(xLine, yLine, zLine);
                        
            xLineI[0] = (int) Math.round(xLine[0]);            
            yLineI[0] = (int) Math.round(yLine[0]);
            
            xLineI[1] = (int) Math.round(xLine[1]);            
            yLineI[1] = (int) Math.round(yLine[1]);
            
            xLineI[2] = (int) Math.round(xLine[2]);            
            yLineI[2] = (int) Math.round(yLine[2]);
            
            g2d.fillPolygon(xLineI, yLineI, 3);

            // the second one
            currentFunctionValue = (i1j1Val+i1jVal+ij1Val)/3.0;
            g2d.setColor(colorMap.getInterpolatedColor(
                                     currentFunctionValue));
            
            xLineI[0] = (int) Math.round(xLine[3]);            
            yLineI[0] = (int) Math.round(yLine[3]);
            g2d.fillPolygon(xLineI, yLineI, 3);

            // get to the next point
            i++;
        }
        
        /** increment the function index access counter - for loop 2 */
        @Override
        public void incrementLoop2I() {
            // get to the next point
            i++;
        }
        
        /** increment the function index access counter - for loop 3 */
        @Override
        public void incrementLoop3I() {
            // get to the next point
            i+=noOfPointsAlongVar2Plane;
        }
        
        /** generate the next contour line in a RIB file */
        @Override
        public void generateNextInRIB(java.io.FileWriter fos,
                                      String shader, double scaleFactor,
                                      double y, double z, double x,
                                      int currentPlane)
                                        throws IOException {
            if (currentPlane != displayPlane) {
                // get to the next point
                i++;
                
                return;
            } // end if
            
            // get correct positions of function values
            j  = i + noOfPointsAlongVar2Plane;
            i1 = i + 1;
            j1 = j + 1;

            // decide the triangles and their fill colors
            ijVal   = functionValue[i];
            ij1Val  = functionValue[i1];
            i1jVal  = functionValue[j];
            i1j1Val = functionValue[j1];                                  

            xLine[0] = xLine[1] = xLine[2] = xLine[3] = x;

            yLine[0] = y; zLine[0] = z; 
            yLine[1] = y+varPlane1Increment; zLine[1] = z;
            yLine[2] = y; zLine[2] = z+varPlane2Increment;
            yLine[3] = y+varPlane1Increment; zLine[3] = z+varPlane2Increment; 

            transform.transform(xLine, yLine, zLine);
            
            // first set of triangles
            currentFunctionValue = (ijVal+i1jVal+ij1Val)/3.0;            
            Color theColor = colorMap.getInterpolatedColor(
                                       currentFunctionValue); 
            
            fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                     );
            
            fos.write("Polygon \"P\" [ "
                      + xLine[0] + " " + yLine[0] + " " + zLine[0] + " "
                      + xLine[1] + " " + yLine[1] + " " + zLine[1] + " "
                      + xLine[2] + " " + yLine[2] + " " + zLine[2]
                      + " ]\n");

            // the second one
            currentFunctionValue = (i1j1Val+i1jVal+ij1Val)/3.0;
            theColor = colorMap.getInterpolatedColor(
                                       currentFunctionValue);
            
            fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                     );
                        
            fos.write("Polygon \"P\" [ "
                      + xLine[3] + " " + yLine[3] + " " + zLine[3] + " "
                      + xLine[1] + " " + yLine[1] + " " + zLine[1] + " "
                      + xLine[2] + " " + yLine[2] + " " + zLine[2]
                      + " ]\n");
            
            // get to the next point
            i++;
        }
    } // end of inner class XTriSetGenerator
    
    /**
     * Generate Y-plane trisets
     */
    private class YTriSetGenerator implements TriSetGenerator {
        
        private int i, j, i1, j1, l, m;
        private double ijVal, ij1Val, i1jVal, i1j1Val;
                
        private double [] xLine, yLine, zLine;
        private int [] xLineI, yLineI;
        
        private double [] functionValue;
        
        public YTriSetGenerator() { }
        
        /** initilize the tri set generator */
        @Override
        public void init() {
            functionValue = gridProperty.getFunctionValues();
            
            i = l = m = 0;
            
            xLine = new double[] {0.0, 0.0, 0.0, 0.0};
            yLine = new double[] {0.0, 0.0, 0.0, 0.0};
            zLine = new double[] {0.0, 0.0, 0.0, 0.0};
            
            xLineI   = new int [] {0, 0, 0};
            yLineI   = new int [] {0, 0, 0}; 
        }
        
        /** generate the next tri set */
        @Override
        public void generateNext(Graphics2D g2d, double x, double z, double y,
                                 int currentPlane) {
            
            if (currentPlane != displayPlane) {
                // get to the next point
                i++;
                
                return;
            } // end if
            
            // get correct positions of function values
            j  = i + 1;
            i1 = i + (noOfPointsAlongVar2Plane * noOfPointsAlongFixedPlane);
            j1 = i1 + 1;

            // decide the triangles and their fill colors
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];

            // first set of triangles
            currentFunctionValue = (ijVal+i1jVal+ij1Val)/3.0;
            g2d.setColor(colorMap.getInterpolatedColor(
                                     currentFunctionValue));

            yLine[0] = yLine[1] = yLine[2] = yLine[3] = y;

            xLine[0] = x; zLine[0] = z; 
            xLine[1] = x+varPlane1Increment; zLine[1] = z;
            xLine[2] = x; zLine[2] = z+varPlane2Increment;
            xLine[3] = x+varPlane1Increment; zLine[3] = z+varPlane2Increment; 

            transform.transform(xLine, yLine, zLine);
                        
            xLineI[0] = (int) Math.round(xLine[0]);            
            yLineI[0] = (int) Math.round(yLine[0]);
            
            xLineI[1] = (int) Math.round(xLine[1]);            
            yLineI[1] = (int) Math.round(yLine[1]);
            
            xLineI[2] = (int) Math.round(xLine[2]);            
            yLineI[2] = (int) Math.round(yLine[2]);
            
            g2d.fillPolygon(xLineI, yLineI, 3);

            // the second one
            currentFunctionValue = (i1j1Val+i1jVal+ij1Val)/3.0;
            g2d.setColor(colorMap.getInterpolatedColor(
                                     currentFunctionValue));
            
            xLineI[0] = (int) Math.round(xLine[3]);            
            yLineI[0] = (int) Math.round(yLine[3]);
            g2d.fillPolygon(xLineI, yLineI, 3);

            // get to the next point
            i++;
        }
        
        /** increment the function index access counter - for loop 2 */
        @Override
        public void incrementLoop2I() {
            // get to the next point
            i = (m * noOfPointsAlongVar2Plane)
                   + (++l * noOfPointsAlongVar2Plane*noOfPointsAlongFixedPlane);
        }
        
        /** increment the function index access counter - for loop 3 */
        @Override
        public void incrementLoop3I() {
            // get to the next point
            i = (++m * noOfPointsAlongVar2Plane);
            l = 0;
        }
        
        /** generate the next contour line in a RIB file */
        @Override
        public void generateNextInRIB(java.io.FileWriter fos,
                                      String shader, double scaleFactor,
                                      double x, double z, double y,
                                      int currentPlane)
                                        throws IOException {
            if (currentPlane != displayPlane) {
                // get to the next point
                i++;
                
                return;
            } // end if
            
            // get correct positions of function values
            j  = i + 1;
            i1 = i + (noOfPointsAlongVar2Plane * noOfPointsAlongFixedPlane);
            j1 = i1 + 1;

            // decide the triangles and their fill colors
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];
            
            yLine[0] = yLine[1] = yLine[2] = yLine[3] = y;

            xLine[0] = x; zLine[0] = z; 
            xLine[1] = x+varPlane1Increment; zLine[1] = z;
            xLine[2] = x; zLine[2] = z+varPlane2Increment;
            xLine[3] = x+varPlane1Increment; zLine[3] = z+varPlane2Increment; 

            transform.transform(xLine, yLine, zLine);                                    

            // first set of triangles
            currentFunctionValue = (ijVal+i1jVal+ij1Val)/3.0;
            Color theColor = colorMap.getInterpolatedColor(
                                       currentFunctionValue); 
            
            fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                     );
            
            fos.write("Polygon \"P\" [ "
                      + xLine[0] + " " + yLine[0] + " " + zLine[0] + " "
                      + xLine[1] + " " + yLine[1] + " " + zLine[1] + " "
                      + xLine[2] + " " + yLine[2] + " " + zLine[2]
                      + " ]\n");

            // the second one
            currentFunctionValue = (i1j1Val+i1jVal+ij1Val)/3.0;
            theColor = colorMap.getInterpolatedColor(
                                       currentFunctionValue);
            
            fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                     );
                        
            fos.write("Polygon \"P\" [ "
                      + xLine[3] + " " + yLine[3] + " " + zLine[3] + " "
                      + xLine[1] + " " + yLine[1] + " " + zLine[1] + " "
                      + xLine[2] + " " + yLine[2] + " " + zLine[2]
                      + " ]\n");
            
            // get to the next point
            i++;
        }
    } // end of inner class YTriSetGenerator
    
    /**
     * Generate Z-plane trisets
     */
    private class ZTriSetGenerator implements TriSetGenerator {
        
        private int i, j, i1, j1, l, m;
        private double ijVal, ij1Val, i1jVal, i1j1Val;
                
        private double [] xLine, yLine, zLine;
        private int [] xLineI, yLineI;
        
        private double [] functionValue;
        
        public ZTriSetGenerator() { }
        
        /** initilize the tri set generator */
        @Override
        public void init() {
            functionValue = gridProperty.getFunctionValues();
            
            i = l = m = 0;
            
            xLine = new double[] {0.0, 0.0, 0.0, 0.0};
            yLine = new double[] {0.0, 0.0, 0.0, 0.0};
            zLine = new double[] {0.0, 0.0, 0.0, 0.0};
            
            xLineI   = new int [] {0, 0, 0};
            yLineI   = new int [] {0, 0, 0}; 
        }
        
        /** generate the next tri set */
        @Override
        public void generateNext(Graphics2D g2d, double x, double y, double z,
                                 int currentPlane) {
            
            if (currentPlane != displayPlane) {
                // get to the next point
                i += noOfPointsAlongFixedPlane;
                
                return;
            } // end if
            
            // get correct positions of function values
            j  = i + noOfPointsAlongFixedPlane;
            i1 = i + (noOfPointsAlongFixedPlane 
                      * noOfPointsAlongVar2Plane);
            j1 = i1 + noOfPointsAlongFixedPlane;

            // decide the triangles and their fill colors
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];

            // first set of triangles
            currentFunctionValue = (ijVal+i1jVal+ij1Val)/3.0;
            g2d.setColor(colorMap.getInterpolatedColor(
                                     currentFunctionValue));

            zLine[0] = zLine[1] = zLine[2] = zLine[3] = z;

            xLine[0] = x; yLine[0] = y; 
            xLine[1] = x+varPlane1Increment; yLine[1] = y;
            xLine[2] = x; yLine[2] = y+varPlane2Increment;
            xLine[3] = x+varPlane1Increment; yLine[3] = y+varPlane2Increment; 

            transform.transform(xLine, yLine, zLine);
                        
            xLineI[0] = (int) Math.round(xLine[0]);            
            yLineI[0] = (int) Math.round(yLine[0]);
            
            xLineI[1] = (int) Math.round(xLine[1]);            
            yLineI[1] = (int) Math.round(yLine[1]);
            
            xLineI[2] = (int) Math.round(xLine[2]);            
            yLineI[2] = (int) Math.round(yLine[2]);
            
            g2d.fillPolygon(xLineI, yLineI, 3);

            // the second one
            currentFunctionValue = (i1j1Val+i1jVal+ij1Val)/3.0;
            g2d.setColor(colorMap.getInterpolatedColor(
                                     currentFunctionValue));
            
            xLineI[0] = (int) Math.round(xLine[3]);            
            yLineI[0] = (int) Math.round(yLine[3]);
            g2d.fillPolygon(xLineI, yLineI, 3);

            // get to the next point
            i += noOfPointsAlongFixedPlane;
        }
        
        /** increment the function index access counter - for loop 2 */
        @Override
        public void incrementLoop2I() {
            // get to the next point
            i = m + (++l * noOfPointsAlongFixedPlane*noOfPointsAlongVar2Plane);
        }
        
        /** increment the function index access counter - for loop 3 */
        @Override
        public void incrementLoop3I() {
            // get to the next point            
            i = (++m);
            l = 0;
        }
        
        /** generate the next contour line in a RIB file */
        @Override
        public void generateNextInRIB(java.io.FileWriter fos,
                                      String shader, double scaleFactor,
                                      double x, double y, double z,
                                      int currentPlane)
                                        throws IOException {
            if (currentPlane != displayPlane) {
                // get to the next point
                i += noOfPointsAlongFixedPlane;
                
                return;
            } // end if
            
            // get correct positions of function values
            j  = i + noOfPointsAlongFixedPlane;
            i1 = i + (noOfPointsAlongFixedPlane 
                      * noOfPointsAlongVar2Plane);
            j1 = i1 + noOfPointsAlongFixedPlane;

            // decide the triangles and their fill colors
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];
            
            zLine[0] = zLine[1] = zLine[2] = zLine[3] = z;

            xLine[0] = x; yLine[0] = y; 
            xLine[1] = x+varPlane1Increment; yLine[1] = y;
            xLine[2] = x; yLine[2] = y+varPlane2Increment;
            xLine[3] = x+varPlane1Increment; yLine[3] = y+varPlane2Increment; 

            transform.transform(xLine, yLine, zLine);                                    

            // first set of triangles
            currentFunctionValue = (ijVal+i1jVal+ij1Val)/3.0;
            Color theColor = colorMap.getInterpolatedColor(
                                       currentFunctionValue); 
            
            fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                     );
            
            fos.write("Polygon \"P\" [ "
                      + xLine[0] + " " + yLine[0] + " " + zLine[0] + " "
                      + xLine[1] + " " + yLine[1] + " " + zLine[1] + " "
                      + xLine[2] + " " + yLine[2] + " " + zLine[2]
                      + " ]\n");

            // the second one
            currentFunctionValue = (i1j1Val+i1jVal+ij1Val)/3.0;
            theColor = colorMap.getInterpolatedColor(
                                       currentFunctionValue);
            
            fos.write("Color " + theColor.getRed() / 255.0
                               + " " + theColor.getGreen() / 255.0
                               + " " + theColor.getBlue() / 255.0
                               + "\n"
                     );
                        
            fos.write("Polygon \"P\" [ "
                      + xLine[3] + " " + yLine[3] + " " + zLine[3] + " "
                      + xLine[1] + " " + yLine[1] + " " + zLine[1] + " "
                      + xLine[2] + " " + yLine[2] + " " + zLine[2]
                      + " ]\n");
            
            // get to the next point
            i += noOfPointsAlongFixedPlane;
        }
    } // end of inner class ZTriSetGenerator
    
} // end of class TexturedPlaneRenderer
