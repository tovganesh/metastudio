/*
 * FuzzyVolumeRenderer.java
 *
 * Created on July 16, 2007, 5:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces;

import org.meta.shell.idebeans.graphics.surfaces.GridPropertyRenderer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import org.meta.common.ColorToFunctionRangeMap;
import org.meta.math.geom.Point3DI;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenCuboid;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.PropertySceneChangeListener;

/**
 * Contours and Isosurfaces allow to track a single value within the entire 
 * range of values for a given data set. One can employ multiple of values 
 * for contours or isosurfaces to compare and contrast more than one subset 
 * of data values. However, this technique can be used with two or atmost 
 * three such values as the visuals become too crowded to infer any valuable 
 * comparisons when a range of these values need to be compared. In such cases 
 * fuzzy volume visualization provides with a continuous subset of values which 
 * are shown as a set of colored dots. These dots are plotted where ever a value
 * within the data set is found to lie in the given range of values. The points
 * are appropriately colored according currently defined color mapping function 
 * to bring out differentiating features of a range of values in a dataset. 
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FuzzyVolumeRenderer extends GridPropertyRenderer {
    
    private ScreenCuboid boundingBox;
    
    /** Creates a new instance of FuzzyVolumeRenderer */
    public FuzzyVolumeRenderer(GridProperty gridProperty) {
        super();

        setGridProperty(gridProperty);
        
        this.boundingBox  = new ScreenCuboid(gridProperty.getBoundingBox());
        this.boundingBox.setDrawCenterMarker(false);
        
        ArrayList<Color> colorRange = new ArrayList<Color>();
                
        colorRange.add(new Color(0.0f, 0.0f, 1.0f));
        colorRange.add(new Color(0.0f, 0.0f, 0.9f));
        colorRange.add(new Color(0.0f, 0.0f, 0.8f));
        colorRange.add(new Color(0.0f, 0.0f, 0.7f));
        colorRange.add(new Color(0.0f, 0.0f, 0.6f));
        colorRange.add(new Color(0.4f, 0.4f, 0.5f));
        colorRange.add(new Color(1.0f, 1.0f, 1.0f));
        colorRange.add(new Color(0.6f, 0.0f, 0.0f));
        colorRange.add(new Color(0.7f, 0.0f, 0.0f));
        colorRange.add(new Color(0.8f, 0.0f, 0.0f));
        colorRange.add(new Color(0.9f, 0.0f, 0.0f));        
        colorRange.add(new Color(1.0f, 0.0f, 0.0f));
                
        this.colorMap = new ColorToFunctionRangeMap(colorRange,
                                gridProperty.getMinFunctionValue(),
                                gridProperty.getMaxFunctionValue());
      
        minFuzzyValue = gridProperty.getMinFunctionValue();
        maxFuzzyValue = -gridProperty.getMinFunctionValue();
    }   

    /**
     * The fuzzy volume rendering is done from here!
     */
    @Override
    public void draw(Graphics2D g2d) {        
        int index = 0;
        int nx = gridProperty.getNoOfPointsAlongX(),
            ny = gridProperty.getNoOfPointsAlongY(),
            nz = gridProperty.getNoOfPointsAlongZ();
        
        double [] fVals = gridProperty.getFunctionValues();
        Point3DI pointToPlot;
        
        for(int i=0; i<nx; i++) {
            for(int j=0; j<ny; j++) {
                for(int k=0; k<nz; k++) {
                    if ((fVals[index] >= minFuzzyValue) 
                        && (fVals[index] <= maxFuzzyValue)) {
                        pointToPlot = new Point3DI(
                                 gridProperty.getGridPointNear(i, j, k));
                        transform.transformPoint(pointToPlot);                        
                        
                        g2d.setColor(
                               colorMap.getInterpolatedColor(fVals[index]));
                        g2d.drawLine(pointToPlot.getCurrentX(),
                                     pointToPlot.getCurrentY(),
                                     pointToPlot.getCurrentX(),
                                     pointToPlot.getCurrentY()
                                    );
                    } // end if
                    
                    index++;
                } // end for
            } // end for
        } // end for     
        
        // draw the bounding box if needed
        if (showBoundingBox) {
            boundingBox.setTransform(transform);
            boundingBox.applyTransforms();
            boundingBox.draw(g2d);
        } // end if
    }   
    
    /**
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // TODO:
    }   

    /**
     * Holds value of property minFuzzyValue.
     */
    private double minFuzzyValue;

    /**
     * Getter for property minFuzzyValue.
     * @return Value of property minFuzzyValue.
     */
    public double getMinFuzzyValue() {
        return this.minFuzzyValue;
    }

    /**
     * Getter for property minFuzzyValue.
     * @return Value of property minFuzzyValue.
     */
    public double getMinFuzzyValueMin() {
        return gridProperty.getMinFunctionValue();
    }
    
    /**
     * Getter for property minFuzzyValue.
     * @return Value of property minFuzzyValue.
     */
    public double getMinFuzzyValueMax() {
        return this.maxFuzzyValue;
    }

    /**
     * Getter for property minFuzzyValue.
     * @return Value of property minFuzzyValue.
     */
    public double getMinFuzzyValueIncrement() {
        return 0.000001;
    }
    
    /**
     * Setter for property minFuzzyValue.
     * @param minFuzzyValue New value of property minFuzzyValue.
     */
    public void setMinFuzzyValue(double minFuzzyValue) {
        this.minFuzzyValue = minFuzzyValue;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }

    /**
     * Holds value of property maxFuzzyValue.
     */
    private double maxFuzzyValue;

    /**
     * Getter for property maxFuzzyValue.
     * @return Value of property maxFuzzyValue.
     */
    public double getMaxFuzzyValue() {
        return this.maxFuzzyValue;
    }

    /**
     * Getter for property maxFuzzyValue.
     * @return Value of property maxFuzzyValue.
     */
    public double getMaxFuzzyValueMin() {
        return this.minFuzzyValue;
    }

    /**
     * Getter for property maxFuzzyValue.
     * @return Value of property maxFuzzyValue.
     */
    public double getMaxFuzzyValueMax() {
        return gridProperty.getMaxFunctionValue();
    }
    
    /**
     * Getter for property maxFuzzyValue.
     * @return Value of property maxFuzzyValue.
     */
    public double getMaxFuzzyValueIncrement() {
        return 0.000001;
    }
    
    /**
     * Setter for property maxFuzzyValue.
     * @param maxFuzzyValue New value of property maxFuzzyValue.
     */
    public void setMaxFuzzyValue(double maxFuzzyValue) {
        this.maxFuzzyValue = maxFuzzyValue;
        
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
        // TODO:
        
        // if the control reaches here then we throw an exception;
        throw new PropertyNotDefinedException();
    }
    
} // end of class FuzzyVolumeRenderer
