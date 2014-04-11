/*
 * ScreenCuboid.java
 *
 * Created on November 6, 2004, 7:53 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.graphics.AbstractGlyph;
import java.awt.*;
import java.util.*;

import org.meta.math.Matrix3D;
import org.meta.molecule.Atom;
import org.meta.math.geom.Point3D;
import org.meta.math.geom.BoundingBox;
import org.meta.common.resource.ColorResource;

/**
 * Represents a cuboid on the screen 
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenCuboid extends AbstractGlyph {
    
    /**
     * Holds value of property boundingBox.
     */
    protected BoundingBox boundingBox;
    
    /**
     * Holds value of property transform.
     */
    protected Matrix3D transform;
    
    // the list of cuboid points
    protected ArrayList<ScreenAtom> cuboidPoints;
    
    // list of edges 
    protected ArrayList<ScreenBond> edgeList;
    
    /** Creates a new instance of Cuboid */
    public ScreenCuboid(BoundingBox boundingBox) {
        super();
        
        this.boundingBox = boundingBox;        
        
        init();
    }
    
    /** initilise this glyph */
    protected void init() {
        // init the cuboidPoints
        cuboidPoints = new ArrayList<ScreenAtom>(9);
        
        Point3D ul = boundingBox.getUpperLeft();
        Point3D br = boundingBox.getBottomRight();
        
        // the cuboid points
        ScreenAtom [] points = new ScreenAtom[9];
        
        // the min point
        points[0] = new ScreenAtom(new Atom("X", 0.0, ul));
        
        // point on "front" face
        points[1] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(ul.getX() + boundingBox.getXWidth(), 
                                          ul.getY(), 
                                          ul.getZ())));
        points[2] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(ul.getX() + boundingBox.getXWidth(), 
                                          ul.getY() + boundingBox.getYWidth(), 
                                          ul.getZ())));
        points[3] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(ul.getX(), 
                                          ul.getY() + boundingBox.getYWidth(), 
                                          ul.getZ())));
        // the max point
        points[4] = new ScreenAtom(new Atom("X", 0.0, br));
        
        // point on "back" face
        points[5] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(br.getX() - boundingBox.getXWidth(), 
                                          br.getY(), 
                                          br.getZ())));
        points[6] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(br.getX() - boundingBox.getXWidth(), 
                                          br.getY() - boundingBox.getYWidth(), 
                                          br.getZ())));
        points[7] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(br.getX(), 
                                          br.getY() - boundingBox.getYWidth(), 
                                          br.getZ())));
        
        // and the center point
        points[8] = new ScreenAtom(new Atom("X", 0.0, boundingBox.center()));
        
        ColorResource colorResource = ColorResource.getInstance();
        
        // set color and radius for each, and add to list
        for(int i=0; i<4; i++) {
            points[i].setRadius(4.0);
            points[i].setColor(colorResource.getScreenCuboidColor()
                                                .darker().darker());
            cuboidPoints.add(points[i]);
        } // end for
        
        for(int i=4; i<8; i++) {
            points[i].setRadius(4.0);
            points[i].setColor(colorResource.getScreenCuboidColor());
            cuboidPoints.add(points[i]);
        } // end for
        
        points[8].setRadius(6.0);
        points[8].setColor(colorResource.getCentralDotColor());
        cuboidPoints.add(points[8]);
            
        // also construct the edge list
        edgeList = new ArrayList<ScreenBond>(12);
        
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(1)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(3)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(6)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(1), 
                                          (ScreenAtom) cuboidPoints.get(2)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(1), 
                                          (ScreenAtom) cuboidPoints.get(7)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(2), 
                                          (ScreenAtom) cuboidPoints.get(3)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(2), 
                                          (ScreenAtom) cuboidPoints.get(4)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(3), 
                                          (ScreenAtom) cuboidPoints.get(5)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(4), 
                                          (ScreenAtom) cuboidPoints.get(7)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(4), 
                                          (ScreenAtom) cuboidPoints.get(5)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(5), 
                                          (ScreenAtom) cuboidPoints.get(6)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(6), 
                                          (ScreenAtom) cuboidPoints.get(7)));
        
        // draw the center marker, by default
        drawCenterMarker = true;
        
        // default transform
        transform = new Matrix3D();        
    }
    
    /**
     * method to apply local transformations
     */
    public synchronized void applyTransforms() {
        if (transform != null) transform.transform(cuboidPoints);
    }
    
    /**
     * paints the cuboid on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {
        // draw a "cross" at the center point
        ScreenAtom center = (ScreenAtom) cuboidPoints.get(8);                
        
        if (drawCenterMarker) {            
            g2d.setColor(ColorResource.getInstance().getCentralDotColor());
            g2d.drawLine(center.getCurrentX(), 
                     (int) (center.getCurrentY() - (center.getRadius()+2)), 
                     center.getCurrentX(), 
                     (int) (center.getCurrentY() + (center.getRadius()+2)));
            g2d.drawLine((int) (center.getCurrentX() - (center.getRadius()+2)), 
                     center.getCurrentY(), 
                     (int) (center.getCurrentX() + (center.getRadius()+2)), 
                     center.getCurrentY());         
            
            // draw the center point if needed
            center.draw(g2d); 
        } // end if 
        
        int i = 0; 
        
        // draw each corner
        for(i=0; i<cuboidPoints.size(); i++) {
            if (i==8) continue; // the center point skip ... TODO: change this!
            
            ((ScreenAtom) cuboidPoints.get(i)).draw(g2d);            
        } // end for
        
        // and then the edges
        for(i=0; i<edgeList.size(); i++) {
            ((ScreenBond) edgeList.get(i)).draw(g2d);            
        } // end for                                
    }       
    
    /**
     * Getter for property transform.
     * @return Value of property transform.
     */
    public Matrix3D getTransform() {
        return this.transform;
    }
    
    /**
     * Setter for property transform.
     * @param transform New value of property transform.
     */
    public void setTransform(Matrix3D transform) {
        this.transform = transform;
    }

    /**
     * Holds value of property drawCenterMarker.
     */
    private boolean drawCenterMarker;

    /**
     * Getter for property drawCenterMarker.
     * @return Value of property drawCenterMarker.
     */
    public boolean isDrawCenterMarker() {
        return this.drawCenterMarker;
    }

    /**
     * Setter for property drawCenterMarker.
     * @param drawCenterMarker New value of property drawCenterMarker.
     */
    public void setDrawCenterMarker(boolean drawCenterMarker) {
        this.drawCenterMarker = drawCenterMarker;
    }

    /**
     * Get associated BoundingBox
     * 
     * @return return the associated BoundingBox
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }


} // end of class ScreenCuboid
