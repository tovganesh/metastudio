/*
 * DistanceTracker.java
 *
 * Created on January 18, 2004, 10:10 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.text.DecimalFormat;
import org.meta.common.Utility;
import org.meta.math.Matrix3D;
import org.meta.math.geom.Point3D;
import org.meta.math.geom.Point3DI;

/**
 * Represents a distance tracker (between two atom centers).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DistanceTracker extends AbstractTracker {
    
    /** Holds value of property atom1. */
    private ScreenAtom atom1;
    
    /** Holds value of property atom2. */
    private ScreenAtom atom2;
    
    /** Holds value of property point1. */
    private Point3DI point1;
    
    /** Holds value of property point2. */
    private Point3DI point2;
    
    
    private String distanceString;
    
    /** Creates a new instance of DistanceTracker */    
    public DistanceTracker(ScreenAtom atom1, ScreenAtom atom2) {
        this.atom1 = atom1;
        this.atom2 = atom2;
                
        // compute the distance ..
        distanceString = (new DecimalFormat("#.###").format(
                              atom1.getAtom().distanceFrom(atom2.getAtom())))
                        + " " + Utility.ANGSTROM_SYMBOL;
        
        // and prepare the screen label
        screenLabel = new ScreenLabel(distanceString);
        
        this.isolated = false;
    }    
    
    /** Creates a new instance of DistanceTracker */
    public DistanceTracker(Point3D point1, Point3D point2) {
        this.point1 = new Point3DI(point1);
        this.point2 = new Point3DI(point2);
                
        // compute the distance ..
        distanceString = (new DecimalFormat("#.###").format(
                              point1.distanceFrom(point2)))
                        + " " + Utility.ANGSTROM_SYMBOL;
        
        // and prepare the screen label
        screenLabel = new ScreenLabel(distanceString);
        
        this.isolated = true;
    }    
    
    /**
     * method to apply local transformations
     */
    @Override
    public synchronized void applyTransforms() {
        if (isIsolated()) {
            if (transform == null) {
                transform = new Matrix3D();
                transform.unit();
            } // end if
            
            transform.transformPoint(point1);
            transform.transformPoint(point2);
        } // end if
    }
    
    /**
     * draw a distance tracker between two atom centers
     */
    @Override
    public void draw(Graphics2D g2d) {
        // use the same color as the screen lable
        g2d.setColor(screenLabel.getColor());
        
        Stroke drawingStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                               BasicStroke.JOIN_BEVEL, 
                                               0, new float[]{1, 3}, 0);
        
        Stroke currentStroke = g2d.getStroke();
        
        g2d.setStroke(drawingStroke);
        
        // and then draw the stuff
        if (!isIsolated()) {
            g2d.drawLine(atom1.getCurrentX(), atom1.getCurrentY(),
                         atom2.getCurrentX(), atom2.getCurrentY());
            screenLabel.setX((atom1.getCurrentX() + atom2.getCurrentX()) / 2);
            screenLabel.setY((atom1.getCurrentY() + atom2.getCurrentY()) / 2);
        } else {
            applyTransforms();
            g2d.drawLine(point1.getCurrentX(), point1.getCurrentY(),
                         point2.getCurrentX(), point2.getCurrentY());
            screenLabel.setX((point1.getCurrentX() + point1.getCurrentX()) / 2);
            screenLabel.setY((point2.getCurrentY() + point2.getCurrentY()) / 2);
        } // end if
        
        g2d.setStroke(currentStroke);
        
        screenLabel.draw(g2d);
    }
    
    /** Getter for property atom1.
     * @return Value of property atom1.
     *
     */
    public ScreenAtom getAtom1() {
        return this.atom1;
    }
    
    /** Setter for property atom1.
     * @param atom1 New value of property atom1.
     *
     */
    public void setAtom1(ScreenAtom atom1) {
        this.atom1 = atom1;
    }
    
    /** Getter for property atom2.
     * @return Value of property atom2.
     *
     */
    public ScreenAtom getAtom2() {
        return this.atom2;
    }
    
    /** Setter for property atom2.
     * @param atom2 New value of property atom2.
     *
     */
    public void setAtom2(ScreenAtom atom2) {
        this.atom2 = atom2;
    }
    
} // end of class DistanceTracker
