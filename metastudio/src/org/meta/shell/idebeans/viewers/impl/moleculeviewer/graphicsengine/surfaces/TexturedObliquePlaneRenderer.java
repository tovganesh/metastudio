/*
 * TexturedObliquePlaneRenderer.java
 *
 * Created on July 16, 2007, 5:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces;

import org.meta.shell.idebeans.graphics.surfaces.GridPropertyRenderer;
import java.awt.Graphics2D;
import java.awt.Point;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenCuboid;

/**
 * This class implements the oblique plane texture renderer. As opposed to plain
 * textured plane renderer this allows for non standard planar cuts for viewing
 * the molecular property.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TexturedObliquePlaneRenderer extends GridPropertyRenderer {
    
    private ScreenCuboid boundingBox;
    
    /**
     * Creates a new instance of TexturedObliquePlaneRenderer
     */
    public TexturedObliquePlaneRenderer(GridProperty gridProperty) {
        super();

        setGridProperty(gridProperty);
        this.boundingBox  = new ScreenCuboid(gridProperty.getBoundingBox());
        this.boundingBox.setDrawCenterMarker(false);
    }   

    /**
     * The fuzzy volume rendering is done from here!
     */
    @Override
    public void draw(Graphics2D g2d) {
        // TODO:
        
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
        // if the control reaches here then we throw an exception;
        throw new PropertyNotDefinedException();
    }
    
} // end of class TexturedObliquePlaneRenderer
