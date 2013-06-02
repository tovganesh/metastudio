/*
 * ContourRenderer.java
 *
 * Created on July 20, 2005, 5:32 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces;

import org.meta.shell.idebeans.graphics.surfaces.GridPropertyRenderer;
import java.awt.*;

import java.io.IOException;

import org.meta.math.MathUtil;
import org.meta.math.Vector3D;
import org.meta.common.ColorToFunctionRangeMap;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenCuboid;

/**
 * A class of type (Glyph and GridPropertyRenderer) for rendering contours.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ContourRenderer extends GridPropertyRenderer {
    
    private double fixedPlaneStart, fixedPlaneEnd,
                   varPlane1Start, varPlane1End,
                   varPlane2Start, varPlane2End,
                   fixedPlaneIncrement, varPlane1Increment, 
                   varPlane2Increment;
    private int noOfPointsAlongFixedPlane,
                noOfPointsAlongVar1Plane,
                noOfPointsAlongVar2Plane;
    
    private ScreenCuboid boundingBox;    
    
    private ContourGenerator generator;
    
    /**
     * Creates a new instance of ContourRenderer 
     */
    public ContourRenderer(GridProperty gridProperty) {
        super();
        
        setGridProperty(gridProperty);
        this.contourPlane = ContourPlane.X_PLANE;
        this.generator    = new XContourGenerator();
        this.colorMap     = new ColorToFunctionRangeMap(Color.blue, Color.red,
                                gridProperty.getMinFunctionValue(),
                                gridProperty.getMaxFunctionValue());
        this.boundingBox  = new ScreenCuboid(gridProperty.getBoundingBox());
        this.boundingBox.setDrawCenterMarker(false);                
                        
        currentFunctionValue = 0.0;
    }  

    /**
     * The contur rendering is done from here!
     */
    @Override
    public void draw(Graphics2D g2d) {                
        // set up the plane variables
        setPlaneVariables();                
        
        // then set up the contur color
        g2d.setColor(colorMap.getInterpolatedColor(currentFunctionValue));
        
        // then do contour plotting        
        int a, b, c;
        double x, y, z;                        
        
        generator.init();
        
        for(a=0, z=fixedPlaneStart; a<noOfPointsAlongFixedPlane; a++, 
                             z=fixedPlaneStart + (a*fixedPlaneIncrement)) {
            for(b=0, x=varPlane1Start; b<noOfPointsAlongVar1Plane-1; b++, 
                             x=varPlane1Start + (b*varPlane1Increment)) {                
                for(c=0, y=varPlane2Start; c<noOfPointsAlongVar2Plane-1; c++, 
                             y=varPlane2Start + (c*varPlane2Increment)) {
                    
                    generator.generateNext(g2d, x, y, z);                    
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
     * The contur rendering for RIB is done from here!
     */
    @Override
    public void drawInRIBFile(java.io.FileWriter fos, String shader,
                              double scaleFactor) throws IOException {   
        // set up the plane variables
        setPlaneVariables();                
        
        // then set up the contur color
        Color theColor = colorMap.getInterpolatedColor(currentFunctionValue);
                        
        // then do contour plotting        
        int a, b, c;
        double x, y, z;                        
        
        generator.init();
        
        for(a=0, z=fixedPlaneStart; a<noOfPointsAlongFixedPlane; a++, 
                             z=fixedPlaneStart + (a*fixedPlaneIncrement)) {
            for(b=0, x=varPlane1Start; b<noOfPointsAlongVar1Plane-1; b++, 
                             x=varPlane1Start + (b*varPlane1Increment)) {                
                for(c=0, y=varPlane2Start; c<noOfPointsAlongVar2Plane-1; c++, 
                             y=varPlane2Start + (c*varPlane2Increment)) {
                    
                    generator.generateNextInRIB(fos, shader, scaleFactor,
                                                theColor,
                                                x, y, z);
                } // end for (y)
                                                              
                generator.incrementLoop2I();
            } // end for (x)
                        
            generator.incrementLoop3I();
        } // end for (z)                        
        
        // TODO: draw the bounding box if needed
        // if (showBoundingBox) {
        //    boundingBox.setTransform(transform);
        //    boundingBox.applyTransforms();
        //    boundingBox.draw(g2d);
        // } // end if                
    }
    
    /**
     * check if (a, b) is between the currentFunctionValue
     */
    private boolean between(double a, double b) {
        return (((a<currentFunctionValue) && (currentFunctionValue<b))
                || ((a>currentFunctionValue) && (currentFunctionValue>b)));
    }
    
    /**
     * setup variables defining the plane
     */
    private void setPlaneVariables() {
        switch(contourPlane) {
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
                
                this.generator = new XContourGenerator();
                
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
                
                this.generator = new YContourGenerator();
                
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
                
                this.generator = new ZContourGenerator();
                
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
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // TODO:
    }

    /**
     * The plane of the contour
     */
    public enum ContourPlane {
        X_PLANE, 
        Y_PLANE,
        Z_PLANE
    }

    /**
     * Holds value of property contourPlane.
     */
    private ContourPlane contourPlane;

    /**
     * Getter for property contourPlane.
     * @return Value of property contourPlane.
     */
    public ContourPlane getContourPlane() {
        return this.contourPlane;
    }

    /**
     * Setter for property contourPlane.
     * @param contourPlane New value of property contourPlane.
     */
    public void setContourPlane(ContourPlane contourPlane) {
        this.contourPlane = contourPlane;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }

    /**
     * inner interface that defines how a particular type of contour will
     * be generated.
     */
    private interface ContourGenerator {
        /** initilize the coutour generator */
        public void init();
        
        /** generate the next contour line */
        public void generateNext(Graphics2D g2d, double x, double y, double z);
        
        /** generate the next contour line in a RIB file */
        public void generateNextInRIB(java.io.FileWriter fos,
                                      String shader, double scaleFactor,
                                      Color theColor,
                                      double x, double y, double z)
                                        throws IOException;
        
        /** increment the function index access counter - for loop 2 */
        public void incrementLoop2I();
        
        /** increment the function index access counter - for loop 3 */
        public void incrementLoop3I();
    } // end of inner interface ContourGenerator

    /**
     * inner class to generate X contours
     */
    private class XContourGenerator implements ContourGenerator {
        
        private int i, j, k, i1, j1;        
        private double alpha, ijVal, ij1Val, i1jVal, i1j1Val;
        private double distance, angleWithZ;
        private double yIncrement, zIncrement;
        private double [] functionValue;
        private double [] xLine, yLine, zLine;
                  
        /** Creates a new instance of XContourGenerator */
        public XContourGenerator() { }
        
        /** initilize the coutour generator */
        @Override
        public void init() {
            functionValue = null;
            xLine = yLine = zLine = null;
            
            functionValue = gridProperty.getFunctionValues();
                        
            yIncrement = gridProperty.getYIncrement();
            zIncrement = gridProperty.getZIncrement();
            
            xLine = new double[] {0.0, 0.0, 0.0, 0.0};
            yLine = new double[] {0.0, 0.0, 0.0, 0.0};
            zLine = new double[] {0.0, 0.0, 0.0, 0.0};
                  
            i = j = i1 = j1 = 0;
        }
        
        /** generate the next contour line */
        @Override
        public void generateNext(Graphics2D g2d, double y, double z, double x) {
            // the following line is needed to ensure correct
            // coordinates.
            xLine[0] = xLine[1] = xLine[2] = xLine[3] = x;
            
            // get correct positions of function values
            j  = i + noOfPointsAlongVar2Plane;
            i1 = i + 1;
            j1 = j + 1;
            
            // decide the contour conditions
            k       = 0;
            ijVal   = functionValue[i];
            ij1Val  = functionValue[i1];
            i1jVal  = functionValue[j];
            i1j1Val = functionValue[j1];
            
            // north
            if (between(ijVal, i1jVal)) {
                alpha = (i1jVal - currentFunctionValue) / (i1jVal - ijVal);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                zLine[k] = z;
                k++;
            } // end if
            
            // east
            if (between(i1jVal, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - i1jVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                yLine[k] = (y+yIncrement); 
                k++;
            } // end if
            
            // south
            if (between(ij1Val, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - ij1Val);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                zLine[k] = (z+zIncrement);
                k++;
            } // end if
            
            // west
            if (between(ijVal, ij1Val)) {
                alpha = (ij1Val - currentFunctionValue) / (ij1Val - ijVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                yLine[k] = y;
                k++;
            } // end if
            
            if (k > 1) {
                transform.transform(xLine, yLine, zLine);
                g2d.drawLine((int) xLine[0], (int) yLine[0],
                             (int) xLine[1], (int) yLine[1]);
            } // end if
            
            if (k > 3) {
                g2d.drawLine((int) xLine[2], (int) yLine[2],
                             (int) xLine[3], (int) yLine[3]);
            } // end if
            
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
                                      Color theColor,
                                      double y, double z, double x) 
                                        throws IOException {
            // the following line is needed to ensure correct
            // coordinates.
            xLine[0] = xLine[1] = xLine[2] = xLine[3] = x;
            
            // get correct positions of function values
            j  = i + noOfPointsAlongVar2Plane;
            i1 = i + 1;
            j1 = j + 1;
            
            // decide the contour conditions
            k       = 0;
            ijVal   = functionValue[i];
            ij1Val  = functionValue[i1];
            i1jVal  = functionValue[j];
            i1j1Val = functionValue[j1];
            
            // north
            if (between(ijVal, i1jVal)) {
                alpha = (i1jVal - currentFunctionValue) / (i1jVal - ijVal);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                zLine[k] = z;
                k++;
            } // end if
            
            // east
            if (between(i1jVal, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - i1jVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                yLine[k] = (y+yIncrement); 
                k++;
            } // end if
            
            // south
            if (between(ij1Val, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - ij1Val);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                zLine[k] = (z+zIncrement);
                k++;
            } // end if
            
            // west
            if (between(ijVal, ij1Val)) {
                alpha = (ij1Val - currentFunctionValue) / (ij1Val - ijVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                yLine[k] = y;
                k++;
            } // end if
            
            if (k > 1) {
                transform.transform(xLine, yLine, zLine);
                fos.write("AttributeBegin\n");
        
                fos.write("Color " + theColor.getRed() / 255.0
                                   + " " + theColor.getGreen() / 255.0
                                   + " " + theColor.getBlue() / 255.0
                                   + "\n"
                          );
        
                // the surface shader
                fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n");  
        
                fos.write("Translate " + xLine[1] * scaleFactor
                                       + " " + yLine[1] * scaleFactor
                                       + " " + zLine[1] * scaleFactor
                                       + "\n"
                          );
                
                Vector3D vector = new Vector3D(xLine[1] - xLine[0],
                                               yLine[1] - yLine[0],
                                               zLine[1] - zLine[0]);
                distance = vector.magnitude();
                vector   = vector.normalize();                
                angleWithZ = MathUtil.toDegrees(Math.acos(vector.getK()));
                
                fos.write("Rotate " + (-angleWithZ)
                                    + " " + vector.getJ()
                                    + " " + (-vector.getI())
                                    + " 0.0 \n"
                          );
                                                
                fos.write("Cylinder 0.01 " + (-distance * scaleFactor) 
                                           + " 0.0 360.0 \n"
                          );
                
                fos.write("AttributeEnd\n");
            } // end if
            
            if (k > 3) {
                fos.write("AttributeBegin\n");
        
                fos.write("Color " + theColor.getRed() / 255.0
                                   + " " + theColor.getGreen() / 255.0 
                                   + " " + theColor.getBlue() / 255.0
                                   + "\n"
                          );
                
                // the surface shader
                fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n");  
        
                fos.write("Translate " + xLine[3] * scaleFactor
                                       + " " + yLine[3] * scaleFactor
                                       + " " + zLine[3] * scaleFactor
                                       + "\n"
                          );
                
                Vector3D vector = new Vector3D(xLine[3] - xLine[2],
                                               yLine[3] - yLine[2],
                                               zLine[3] - zLine[2]);
                distance = vector.magnitude();
                vector   = vector.normalize();                
                angleWithZ = MathUtil.toDegrees(Math.acos(vector.getK()));
                
                fos.write("Rotate " + (-angleWithZ)
                                    + " " + vector.getJ()
                                    + " " + (-vector.getI())
                                    + " 0.0 \n"
                          );
                                                
                fos.write("Cylinder 0.01 " + (-distance * scaleFactor) 
                                           + " 0.0 360.0 \n"
                          );
                fos.write("AttributeEnd\n");
            } // end if
            
            // get to the next point
            i++;
        }
    } // end of class XContourGenerator
        
    /**
     * inner class to generate Y contours
     */
    private class YContourGenerator implements ContourGenerator {
        
        private int i, j, k, i1, j1, l, m;        
        private double alpha, ijVal, ij1Val, i1jVal, i1j1Val;
        private double distance, angleWithZ;
        private double xIncrement, zIncrement;
        private double [] functionValue;
        private double [] xLine, yLine, zLine;
                  
        /** Creates a new instance of YContourGenerator */
        public YContourGenerator() { }
        
        /** initilize the coutour generator */
        @Override
        public void init() {
            functionValue = null;
            xLine = yLine = zLine = null;
            
            functionValue = gridProperty.getFunctionValues();
                        
            xIncrement = gridProperty.getXIncrement();
            zIncrement = gridProperty.getZIncrement();
            
            xLine = new double[] {0.0, 0.0, 0.0, 0.0};
            yLine = new double[] {0.0, 0.0, 0.0, 0.0};
            zLine = new double[] {0.0, 0.0, 0.0, 0.0};
                  
            i = j = i1 = j1 = l = m = 0;
        }
        
        /** generate the next contour line */
        @Override
        public void generateNext(Graphics2D g2d, double x, double z, double y) {
            // the following line is needed to ensure correct
            // coordinates.
            yLine[0] = yLine[1] = yLine[2] = yLine[3] = y;
            
            // get correct positions of function values
            j  = i + 1;
            i1 = i + (noOfPointsAlongVar2Plane * noOfPointsAlongFixedPlane);
            j1 = i1 + 1;
            
            // decide the contour conditions
            k       = 0;
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];
            
            // north
            if (between(ijVal, i1jVal)) {
                alpha = (i1jVal - currentFunctionValue) / (i1jVal - ijVal);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                zLine[k] = z;
                k++;
            } // end if
            
            // east
            if (between(i1jVal, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - i1jVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                xLine[k] = (x+xIncrement); 
                k++;
            } // end if
            
            // south
            if (between(ij1Val, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - ij1Val);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                zLine[k] = (z+zIncrement);
                k++;
            } // end if
            
            // west
            if (between(ijVal, ij1Val)) {
                alpha = (ij1Val - currentFunctionValue) / (ij1Val - ijVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                xLine[k] = x;
                k++;
            } // end if
            
            if (k > 1) {
                transform.transform(xLine, yLine, zLine);
                g2d.drawLine((int) xLine[0], (int) yLine[0],
                             (int) xLine[1], (int) yLine[1]);
            } // end if
            
            if (k > 3) {
                g2d.drawLine((int) xLine[2], (int) yLine[2],
                             (int) xLine[3], (int) yLine[3]);
            } // end if
            
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
                                      Color theColor,
                                      double x, double z, double y) 
                                        throws IOException {
            // the following line is needed to ensure correct
            // coordinates.
            yLine[0] = yLine[1] = yLine[2] = yLine[3] = y;
            
            // get correct positions of function values
            j  = i + 1;
            i1 = i + (noOfPointsAlongVar2Plane * noOfPointsAlongFixedPlane);
            j1 = i1 + 1;
            
            // decide the contour conditions
            k       = 0;
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];
            
            // north
            if (between(ijVal, i1jVal)) {
                alpha = (i1jVal - currentFunctionValue) / (i1jVal - ijVal);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                zLine[k] = z;
                k++;
            } // end if
            
            // east
            if (between(i1jVal, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - i1jVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                xLine[k] = (x+xIncrement); 
                k++;
            } // end if
            
            // south
            if (between(ij1Val, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - ij1Val);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                zLine[k] = (z+zIncrement);
                k++;
            } // end if
            
            // west
            if (between(ijVal, ij1Val)) {
                alpha = (ij1Val - currentFunctionValue) / (ij1Val - ijVal);
                
                zLine[k] = (z*alpha) + ((z+zIncrement)*(1.0-alpha));
                xLine[k] = x;
                k++;
            } // end if
            
            if (k > 1) {
                transform.transform(xLine, yLine, zLine);
                fos.write("AttributeBegin\n");
        
                fos.write("Color " + theColor.getRed() / 255.0
                                   + " " + theColor.getGreen() / 255.0
                                   + " " + theColor.getBlue() / 255.0
                                   + "\n"
                          );
        
                // the surface shader
                fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n");  
                        
                fos.write("Translate " + xLine[1] * scaleFactor
                                       + " " + yLine[1] * scaleFactor
                                       + " " + zLine[1] * scaleFactor
                                       + "\n"
                          );
                
                Vector3D vector = new Vector3D(xLine[1] - xLine[0],
                                               yLine[1] - yLine[0],
                                               zLine[1] - zLine[0]);
                distance = vector.magnitude();
                vector   = vector.normalize();                
                angleWithZ = MathUtil.toDegrees(Math.acos(vector.getK()));
                
                fos.write("Rotate " + (-angleWithZ)
                                    + " " + vector.getJ()
                                    + " " + (-vector.getI())
                                    + " 0.0 \n"
                          );
                                                
                fos.write("Cylinder 0.01 " + (-distance * scaleFactor) 
                                           + " 0.0 360.0 \n"
                          );
                
                fos.write("AttributeEnd\n");
            } // end if
            
            if (k > 3) {
                fos.write("AttributeBegin\n");
        
                fos.write("Color " + theColor.getRed() / 255.0
                                   + " " + theColor.getGreen() / 255.0 
                                   + " " + theColor.getBlue() / 255.0
                                   + "\n"
                          );
                
                // the surface shader
                fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n");  
                        
                fos.write("Translate " + xLine[3] * scaleFactor
                                       + " " + yLine[3] * scaleFactor
                                       + " " + zLine[3] * scaleFactor
                                       + "\n"
                          );
                
                Vector3D vector = new Vector3D(xLine[3] - xLine[2],
                                               yLine[3] - yLine[2],
                                               zLine[3] - zLine[2]);
                distance = vector.magnitude();
                vector   = vector.normalize();                
                angleWithZ = MathUtil.toDegrees(Math.acos(vector.getK()));
                
                fos.write("Rotate " + (-angleWithZ)
                                    + " " + vector.getJ()
                                    + " " + (-vector.getI())
                                    + " 0.0 \n"
                          );
                                                
                fos.write("Cylinder 0.01 " + (-distance * scaleFactor) 
                                           + " 0.0 360.0 \n"
                          );
                fos.write("AttributeEnd\n");
            } // end if
            
            // get to the next point
            i++;
        }
    } // end of class YContourGenerator
    
    /**
     * inner class to generate Z contours
     */
    private class ZContourGenerator implements ContourGenerator {
        
        private int i, j, k, i1, j1, l, m;        
        private double alpha, ijVal, ij1Val, i1jVal, i1j1Val;
        private double distance, angleWithZ;
        private double xIncrement, yIncrement;
        private double [] functionValue;
        private double [] xLine, yLine, zLine;
                  
        /** Creates a new instance of ZContourGenerator */
        public ZContourGenerator() { }
        
        /** initilize the coutour generator */
        @Override
        public void init() {
            functionValue = null;
            xLine = yLine = zLine = null;
            
            functionValue = gridProperty.getFunctionValues();
                        
            xIncrement = gridProperty.getXIncrement();
            yIncrement = gridProperty.getYIncrement();
            
            xLine = new double[] {0.0, 0.0, 0.0, 0.0};
            yLine = new double[] {0.0, 0.0, 0.0, 0.0};
            zLine = new double[] {0.0, 0.0, 0.0, 0.0};
                  
            i = j = i1 = j1 = l = m = 0;
        }
        
        /** generate the next contour line */
        @Override
        public void generateNext(Graphics2D g2d, double x, double y, double z) {
            // the following line is needed to ensure correct
            // coordinates.
            zLine[0] = zLine[1] = zLine[2] = zLine[3] = z;
            
            // get correct positions of function values
            j  = i + noOfPointsAlongFixedPlane;
            i1 = i + (noOfPointsAlongFixedPlane * noOfPointsAlongVar2Plane);
            j1 = i1 + noOfPointsAlongFixedPlane;
            
            // decide the contour conditions
            k       = 0;
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];
            
            // north
            if (between(ijVal, i1jVal)) {
                alpha = (i1jVal - currentFunctionValue) / (i1jVal - ijVal);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                yLine[k] = y;
                k++;
            } // end if
            
            // east
            if (between(i1jVal, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - i1jVal);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                xLine[k] = (x+xIncrement); 
                k++;
            } // end if
            
            // south
            if (between(ij1Val, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - ij1Val);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                yLine[k] = (y+yIncrement);
                k++;
            } // end if
            
            // west
            if (between(ijVal, ij1Val)) {
                alpha = (ij1Val - currentFunctionValue) / (ij1Val - ijVal);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                xLine[k] = x;
                k++;
            } // end if
            
            if (k > 1) {
                transform.transform(xLine, yLine, zLine);
                g2d.drawLine((int) xLine[0], (int) yLine[0],
                             (int) xLine[1], (int) yLine[1]);
            } // end if
            
            if (k > 3) {
                g2d.drawLine((int) xLine[2], (int) yLine[2],
                             (int) xLine[3], (int) yLine[3]);
            } // end if
            
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
                                      Color theColor,
                                      double x, double y, double z) 
                                        throws IOException {
            // the following line is needed to ensure correct
            // coordinates.
            zLine[0] = zLine[1] = zLine[2] = zLine[3] = z;
            
            // get correct positions of function values
            j  = i + noOfPointsAlongFixedPlane;
            i1 = i + (noOfPointsAlongFixedPlane * noOfPointsAlongVar2Plane);
            j1 = i1 + noOfPointsAlongFixedPlane;
            
            // decide the contour conditions
            k       = 0;
            ijVal   = functionValue[i];
            ij1Val  = functionValue[j];
            i1jVal  = functionValue[i1];
            i1j1Val = functionValue[j1];
            
            // north
            if (between(ijVal, i1jVal)) {
                alpha = (i1jVal - currentFunctionValue) / (i1jVal - ijVal);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                yLine[k] = y;
                k++;
            } // end if
            
            // east
            if (between(i1jVal, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - i1jVal);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                xLine[k] = (x+xIncrement); 
                k++;
            } // end if
            
            // south
            if (between(ij1Val, i1j1Val)) {
                alpha = (i1j1Val - currentFunctionValue) / (i1j1Val - ij1Val);
                
                xLine[k] = (x*alpha) + ((x+xIncrement)*(1.0-alpha));
                yLine[k] = (y+yIncrement);
                k++;
            } // end if
            
            // west
            if (between(ijVal, ij1Val)) {
                alpha = (ij1Val - currentFunctionValue) / (ij1Val - ijVal);
                
                yLine[k] = (y*alpha) + ((y+yIncrement)*(1.0-alpha));
                xLine[k] = x;
                k++;
            } // end if
            
            if (k > 1) {
                transform.transform(xLine, yLine, zLine);
                fos.write("AttributeBegin\n");
        
                fos.write("Color " + theColor.getRed() / 255.0
                                   + " " + theColor.getGreen() / 255.0
                                   + " " + theColor.getBlue() / 255.0
                                   + "\n"
                          );
        
                // the surface shader
                fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n");  
                       
                fos.write("Translate " + xLine[1] * scaleFactor
                                       + " " + yLine[1] * scaleFactor
                                       + " " + zLine[1] * scaleFactor
                                       + "\n"
                          );
                
                Vector3D vector = new Vector3D(xLine[1] - xLine[0],
                                               yLine[1] - yLine[0],
                                               zLine[1] - zLine[0]);
                distance = vector.magnitude();
                vector   = vector.normalize();                
                angleWithZ = MathUtil.toDegrees(Math.acos(vector.getK()));
                
                fos.write("Rotate " + (-angleWithZ)
                                    + " " + vector.getJ()
                                    + " " + (-vector.getI())
                                    + " 0.0 \n"
                          );
                                                
                fos.write("Cylinder 0.01 " + (-distance * scaleFactor) 
                                           + " 0.0 360.0 \n"
                          );
                
                fos.write("AttributeEnd\n");
            } // end if
            
            if (k > 3) {
                fos.write("AttributeBegin\n");
        
                fos.write("Color " + theColor.getRed() / 255.0
                                   + " " + theColor.getGreen() / 255.0 
                                   + " " + theColor.getBlue() / 255.0
                                   + "\n"
                          );
                
                // the surface shader
                fos.write("Surface \"" + shader + "\" \"Kd\" 1.0 \n");  
                       
                fos.write("Translate " + xLine[3] * scaleFactor
                                       + " " + yLine[3] * scaleFactor
                                       + " " + zLine[3] * scaleFactor
                                       + "\n"
                          );
                
                Vector3D vector = new Vector3D(xLine[3] - xLine[2],
                                               yLine[3] - yLine[2],
                                               zLine[3] - zLine[2]);
                distance = vector.magnitude();
                vector   = vector.normalize();                
                angleWithZ = MathUtil.toDegrees(Math.acos(vector.getK()));
                
                fos.write("Rotate " + (-angleWithZ)
                                    + " " + vector.getJ()
                                    + " " + (-vector.getI())
                                    + " 0.0 \n"
                          );
                                                
                fos.write("Cylinder 0.01 " + (-distance * scaleFactor) 
                                           + " 0.0 360.0 \n"
                          );
                fos.write("AttributeEnd\n");
            } // end if
            
            // get to the next point
            i += noOfPointsAlongFixedPlane;
        }
    } // end of class ZContourGenerator
    
} // end of class ContourRenderer
