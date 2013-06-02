/*
 * ScreenGrid.java
 *
 * Created on May 20, 2007, 11:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.Color;
import java.awt.Graphics2D;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;
import org.meta.molecule.property.electronic.GridProperty;

/**
 * The adjustable screen grid.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenGrid extends ScreenCuboid {                
    
    /** Creates a new instance of ScreenGrid */
    public ScreenGrid(BoundingBox boundingBox) {
        super(boundingBox);
        
        init();
    }
        
    /** Creates a new instance of ScreenGrid */
    public ScreenGrid(GridProperty gridProperty) {
        super(gridProperty.getBoundingBox());
        
        this.gridProperty = gridProperty;                
        init();        
    }
    
    /** initilize this glyph object */
    @Override
    protected void init() {
        super.init();
        
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(5)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(6), 
                                          (ScreenAtom) cuboidPoints.get(3)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(1), 
                                          (ScreenAtom) cuboidPoints.get(4)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(7), 
                                          (ScreenAtom) cuboidPoints.get(2)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(6), 
                                          (ScreenAtom) cuboidPoints.get(1)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(7), 
                                          (ScreenAtom) cuboidPoints.get(0)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(5), 
                                          (ScreenAtom) cuboidPoints.get(2)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(3), 
                                          (ScreenAtom) cuboidPoints.get(4)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(6), 
                                          (ScreenAtom) cuboidPoints.get(4)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(5), 
                                          (ScreenAtom) cuboidPoints.get(7)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(2)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(1), 
                                          (ScreenAtom) cuboidPoints.get(3)));
        
        // construct the gird holder
        holderLength = 0.1;  // default holder length;
        
        Point3D ul = boundingBox.getUpperLeft();
        Point3D br = boundingBox.getBottomRight();
        
        // the cuboid points
        ScreenAtom [] points = new ScreenAtom[9];
                
        points[0] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(ul.getX() + holderLength, 
                                          ul.getY(), 
                                          ul.getZ())));
        points[1] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(ul.getX(), 
                                          ul.getY() + holderLength, 
                                          ul.getZ())));
        points[2] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(ul.getX(), 
                                          ul.getY(), 
                                          ul.getZ() + holderLength)));        
        
        // point on "back" face
        points[3] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(br.getX() - holderLength, 
                                          br.getY(), 
                                          br.getZ())));
        points[4] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(br.getX(), 
                                          br.getY() - holderLength, 
                                          br.getZ())));
        points[5] = new ScreenAtom(new Atom("X", 0.0, 
                              new Point3D(br.getX(), 
                                          br.getY(), 
                                          br.getZ() - holderLength)));
        
        for(int i=0; i<=5; i++) {  // TODO: change?
            points[i].setRadius(4.0);
            points[i].setColor(Color.red);  // TODO : change this
            cuboidPoints.add(points[i]);
        } // end for
        
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(9)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(10)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(0), 
                                          (ScreenAtom) cuboidPoints.get(11)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(4), 
                                          (ScreenAtom) cuboidPoints.get(12)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(4), 
                                          (ScreenAtom) cuboidPoints.get(13)));
        edgeList.add(new SingleScreenBond((ScreenAtom) cuboidPoints.get(4), 
                                          (ScreenAtom) cuboidPoints.get(14)));  
        
        if (gridProperty == null) {
            gridProperty = new GridProperty(boundingBox, 0, 0, 0, 0, 0, 0,
                                            new double[1]);
        } // end if
    }
    
    /**
     * paints the cuboid on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
    }

    /**
     * Holds value of property holderLength.
     */
    private double holderLength;

    /**
     * Getter for property holderLength.
     * @return Value of property holderLength.
     */
    public double getHolderLength() {
        return this.holderLength;
    }

    /**
     * Setter for property holderLength.
     * @param holderLength New value of property holderLength.
     */
    public void setHolderLength(double holderLength) {
        this.holderLength = holderLength;
    }

    /**
     * Holds value of property gridProperty.
     */
    private GridProperty gridProperty;

    /**
     * Getter for property gridProperty.
     * @return Value of property gridProperty.
     */
    public GridProperty getGridProperty() {
        return this.gridProperty;
    }

    /**
     * Setter for property gridProperty.
     * @param gridProperty New value of property gridProperty.
     */
    public void setGridProperty(GridProperty gridProperty) {
        this.gridProperty = gridProperty;
        this.boundingBox = gridProperty.getBoundingBox();
        init();
    }
} // end of class ScreenGrid
