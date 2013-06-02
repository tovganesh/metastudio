/*
 * TexturedVdwSurfaceRenderer.java
 *
 * Created on September 29, 2005, 6:44 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces;

import org.meta.shell.idebeans.graphics.surfaces.GridPropertyRenderer;
import org.meta.shell.idebeans.graphics.surfaces.SphereTriSetGenerator;
import java.awt.*;
import java.util.*;

import java.io.IOException;
import org.meta.common.ColorToFunctionRangeMap;
import org.meta.molecule.Molecule;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.graphics.surfaces.TextureFunction;
import org.meta.shell.idebeans.graphics.surfaces.VdwGridPropertyGenerator;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenCuboid;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.PropertySceneChangeListener;

/**
 * A class of type (Glyph and GridPropertyRenderer) for rendering textured 
 * Vdw surface.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TexturedVdwSurfaceRenderer extends GridPropertyRenderer
                                        implements TextureFunction {
    
    protected Molecule molecule;
    protected MoleculeScene moleculeScene;
    
    protected HashMap<String, SphereTriSetGenerator> triSetTable;    
    
    protected static final int DEFAULT_LARGE_MOLECULE_SIZE = 200;
    
    protected ScreenCuboid boundingBox;
    
    private IsoSurfaceRenderer _renderer;
    
    /** Creates a new instance of TexturedVdwSurfaceRenderer */
    public TexturedVdwSurfaceRenderer(GridProperty gridProperty, 
                                      MoleculeScene moleculeScene) {
        super();
        
        setGridProperty(gridProperty);
        this.moleculeScene = moleculeScene;
        
        this.molecule = moleculeScene.getMolecule();                
        this.largeMoleculeSize = DEFAULT_LARGE_MOLECULE_SIZE;
        this.numberOfSphereDivisions = 40;
        
        this.triSetTable = new HashMap<String, SphereTriSetGenerator>(5);
        
        this.boundingBox  = new ScreenCuboid(gridProperty.getBoundingBox());
        this.boundingBox.setDrawCenterMarker(false);
        
        ArrayList<Color> colorRange = new ArrayList<Color>();
                
        colorRange.add(new Color(0.0f, 0.0f, 1.0f));
        colorRange.add(new Color(0.0f, 0.0f, 0.9f));
        colorRange.add(new Color(0.0f, 0.0f, 0.8f));
        colorRange.add(new Color(0.0f, 0.0f, 0.7f));
        colorRange.add(new Color(0.0f, 0.0f, 0.6f));
        colorRange.add(new Color(0.0f, 0.0f, 0.5f));
        colorRange.add(new Color(1.0f, 1.0f, 1.0f));
        colorRange.add(new Color(0.6f, 0.0f, 0.0f));
        colorRange.add(new Color(0.7f, 0.0f, 0.0f));
        colorRange.add(new Color(0.8f, 0.0f, 0.0f));
        colorRange.add(new Color(0.9f, 0.0f, 0.0f));        
        colorRange.add(new Color(1.0f, 0.0f, 0.0f));
                
        this.colorMap = new ColorToFunctionRangeMap(colorRange,
                                gridProperty.getMinFunctionValue(),
                                gridProperty.getMaxFunctionValue());
        
        this.fillPolygons = false;
        this.fillTransperency = 128;
        
        GridProperty vdwGrid = (new VdwGridPropertyGenerator(molecule)).getGridProperty();
        this._renderer = new IsoSurfaceRenderer(vdwGrid);
        this._renderer.setColorMap(colorMap);
        this._renderer.setCurrentFunctionValue(0.0);        
        this._renderer.setTextureFunction(this);
    }

    /**
     * The vdw surface texturing is done from here!
     */
    @Override
    public void draw(Graphics2D g2d) {                
        this._renderer.setTransform(transform);
        this._renderer.applyTransforms();
        this._renderer.draw(g2d);
        
        // draw the bounding box if needed
        if (showBoundingBox) {
            boundingBox.setTransform(transform);
            boundingBox.applyTransforms();
            boundingBox.draw(g2d);
        } // end if              
    }        
    
    /**
     * The vdw surface rendering for RIB is done from here!
     */
    @Override
    public void drawInRIBFile(java.io.FileWriter fos, String shader,
                              double scaleFactor) throws IOException {         
        this._renderer.setTransform(transform);
        this._renderer.applyTransforms();
        this._renderer.drawInRIBFile(fos, shader, scaleFactor);        
    }            
    
    /**
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // TODO:
    }   

    /**
     * Overridden Setter for property currentFunctionValue.
     * @param currentFunctionValue New value of property currentFunctionValue.
     */
    @Override
    public void setCurrentFunctionValue(double currentFunctionValue) {
        this.currentFunctionValue = currentFunctionValue;
        this._renderer.setCurrentFunctionValue(currentFunctionValue);
    } 
       
    /**
     * Holds value of property largeMoleculeSize.
     */
    protected int largeMoleculeSize;

    /**
     * Getter for property largeMoleculeSize.
     * @return Value of property largeMoleculeSize.
     */
    public int getLargeMoleculeSize() {
        return this.largeMoleculeSize;
    }

    /**
     * Setter for property largeMoleculeSize.
     * @param largeMoleculeSize New value of property largeMoleculeSize.
     */
    public void setLargeMoleculeSize(int largeMoleculeSize) {
        this.largeMoleculeSize = largeMoleculeSize;
    }

    /**
     * Holds value of property numberOfSphereDivisions.
     */
    protected int numberOfSphereDivisions;

    /**
     * Getter for property numberOfSphereDivisions.
     * @return Value of property numberOfSphereDivisions.
     */
    public int getNumberOfSphereDivisions() {
        return this.numberOfSphereDivisions;
    }

    /**
     * Setter for property numberOfSphereDivisions.
     * @param numberOfSphereDivisions New value of property 
     *        numberOfSphereDivisions.
     */
    public void setNumberOfSphereDivisions(int numberOfSphereDivisions) {
        this.numberOfSphereDivisions = numberOfSphereDivisions;
    }

    /**
     * Holds value of property fillPolygons.
     */
    protected boolean fillPolygons;

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
        return this._renderer.getClosestVisiblePoint(point);
    }

    /**
     * Get the color value at a particular index into a grid property
     * 
     * @param i index into x location
     * @param j index into y location
     * @param k index into z location
     * @return the interpolated color value
     */
    @Override
    public Color getColorValueAt(int i, int j, int k) {
        double val = gridProperty.getFunctionValueAt(i, j, k);               
        return colorMap.getInterpolatedColor(val);
    }

    /**
     * Get the color value at a particular point with in the grid property
     *
     * @param x
     * @param y
     * @param z
     * @return the interpolated color value
     */
    @Override
    public Color getColorValueAt(double x, double y, double z) {
        double val = gridProperty.getFunctionValueAt(x, y, z);
        return colorMap.getInterpolatedColor(val);
    }
} // end of class TexturedVdwSurfaceRenderer
