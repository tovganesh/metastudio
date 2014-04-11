/*
 * ScreenSphere.java
 *
 * Created on November 6, 2004, 10:53 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.graphics.AbstractGlyph;
import java.awt.*;

import org.meta.math.Matrix3D;
import org.meta.molecule.Atom;
import org.meta.math.geom.Point3D;
import org.meta.common.resource.ColorResource;

/**
 * A "flat" sphere on the screen!
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenSphere extends AbstractGlyph {
    
    /**
     * Holds value of property center.
     */
    private Point3D center;
    
    /**
     * Holds value of property radius.
     */
    private double radius;
    
    // the screen atom .. center
    private ScreenAtom centerPoint;
    
    // radius point
    private ScreenAtom radiusPoint;
        
    /**
     * Holds value of property transform.
     */
    private Matrix3D transform;
    
    /**
     * Holds value of property scaleFactor.
     */
    private double scaleFactor;
    
    /** Creates a new instance of ScreenSphere */
    public ScreenSphere(Point3D center, double radius) {
        super();
        
        this.radius = radius;
        this.center = center;
        
        centerPoint = new ScreenAtom(new Atom("X", 0.0, center));
        centerPoint.setRadius(6.0);
        centerPoint.setColor(ColorResource.getInstance().getCentralDotColor());
        
        radiusPoint = new ScreenAtom(new Atom("X", 0.0,  
                             new Point3D(center.getX() + radius, 
                                         center.getY() + radius, 
                                         center.getZ() + radius)));                
    }
    
    /**
     * method to apply local transformations
     */
    @Override
    public synchronized void applyTransforms() {
        transform.transform(centerPoint);
        transform.transform(radiusPoint);
    }
    
    /**
     * Getter for property center.
     * @return Value of property center.
     */
    public Point3D getCenter() {
        return this.center;
    }
    
    /**
     * Getter for property radius.
     * @return Value of property radius.
     */
    public double getRadius() {
        return this.radius;
    }
    
    /**
     * Getter for property transform.
     * @return Value of property transform.
     */
    @Override
    public Matrix3D getTransform() {
        return this.transform;
    }
    
    /**
     * Setter for property transform.
     * @param transform New value of property transform.
     */
    @Override
    public void setTransform(Matrix3D transform) {
        this.transform = transform;
    }
    
    /**
     * paints the "flat" sphere on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {          
        centerPoint.draw(g2d);
        
        g2d.setColor(ColorResource.getInstance().getScreenSphereColor());
        g2d.drawLine(
                centerPoint.getCurrentX(), 
                (int) (centerPoint.getCurrentY() - (centerPoint.getRadius()+2)), 
                centerPoint.getCurrentX(), 
                (int) (centerPoint.getCurrentY() + (centerPoint.getRadius()+2))
        );
        g2d.drawLine(
                (int) (centerPoint.getCurrentX() - (centerPoint.getRadius()+2)), 
                centerPoint.getCurrentY(), 
                (int) (centerPoint.getCurrentX() + (centerPoint.getRadius()+2)), 
                centerPoint.getCurrentY()
        );       
        
        // X-Y the circles                
        int presentRadius = (int) (radius * scaleFactor);

        g2d.drawOval(centerPoint.getCurrentX()-presentRadius, 
                     centerPoint.getCurrentY()-presentRadius,
                     2*presentRadius, 
                     2*presentRadius);         
    }
    
    /** 
     * Draw the contents in a RIB file
     *
     * @param fos - the file stream connected to RIB file
     * @param shader - a string representing RIB shader
     * @param scaleFactor - scaling applied to inidividual objects
     */
    @Override
    public void drawInRIBFile(java.io.FileWriter fos, String shader,
                              double scaleFactor) throws java.io.IOException {
        // default implementation does nothing
    }
    
    /**
     * Setter for property scaleFactor.
     * @param scaleFactor New value of property scaleFactor.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
} // end of class ScreenSphere
