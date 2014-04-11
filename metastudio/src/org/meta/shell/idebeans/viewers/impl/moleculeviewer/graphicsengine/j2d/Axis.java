/*
 * Axis.java
 *
 * Created on October 3, 2004, 8:19 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j2d;

import org.meta.shell.idebeans.graphics.AbstractGlyph;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import java.awt.*;
import java.util.*;

import org.meta.molecule.Atom;
import org.meta.math.geom.Point3D;
import org.meta.common.resource.FontResource;
import org.meta.math.Matrix3D;

/**
 * Represents the axis on the screen 
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Axis extends AbstractGlyph {    
    
    // list of 3 points that form the axis
    private ArrayList<ScreenAtom> xyzPoints;
    
    // the pseudo center of this axis
    private int centerX, centerY;
    
    /**
     * Holds value of property labelColor.
     */
    private Color labelColor;
    
    /**
     * Holds value of property xAxisColor.
     */
    private Color xAxisColor;
    
    /**
     * Holds value of property yAxisColor.
     */
    private Color yAxisColor;
    
    /**
     * Holds value of property zAxisColor.
     */
    private Color zAxisColor;        
    
    /** Creates a new instance of Axis */
    public Axis(int x, int y) {
        super();
        
        // initilize
        centerX = x;
        centerY = y;
        
        xyzPoints = new ArrayList<ScreenAtom>(3);
        
        xyzPoints.add(new ScreenAtom(
                           new Atom("X", 0.0, new Point3D(20.0, 0.0, 0.0))));
        xyzPoints.add(new ScreenAtom(
                           new Atom("Y", 0.0, new Point3D(0.0, 20.0, 0.0))));
        xyzPoints.add(new ScreenAtom(
                           new Atom("Z", 0.0, new Point3D(0.0, 0.0, 20.0))));                
        
        // colors and lables...
        labelColor = Color.lightGray;
        xAxisColor = Color.red;
        yAxisColor = Color.green;
        zAxisColor = Color.blue;
        
        refreshPoints();
    }
    
    /**
     * refresh the color representing the points
     */
    private void refreshPoints() {        
        for(ScreenAtom thePoint : xyzPoints) {            
            thePoint.showSymbolLabel(true);
            thePoint.setColor(labelColor);
            thePoint.getScreenLabel()
                    .setFont(FontResource.getInstance().getAxisFont());
        } // end for
    }
    
    /**
     * paints the axis on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {
        ScreenAtom thePoint;
        
        // X axis
        thePoint = xyzPoints.get(0);
        g2d.setColor(xAxisColor);
        g2d.drawLine(centerX, centerY, 
                     thePoint.getCurrentX(), thePoint.getCurrentY());
        thePoint.draw(g2d);
        
        // Y axis
        thePoint = xyzPoints.get(1);
        g2d.setColor(yAxisColor);
        g2d.drawLine(centerX, centerY, 
                     thePoint.getCurrentX(), thePoint.getCurrentY());
        thePoint.draw(g2d);
        
        // Z axis
        thePoint = xyzPoints.get(2);
        g2d.setColor(zAxisColor);
        g2d.drawLine(centerX, centerY, 
                     thePoint.getCurrentX(), thePoint.getCurrentY());
        thePoint.draw(g2d);                
    }

    /** Setter for property transform.
     * @param transform New value of property transform.
     *
     */
    @Override
    public void setTransform(Matrix3D transform) {
        this.transform = (Matrix3D) transform.clone();
    }

    /**
     * method to apply local transformations
     */
    @Override
    public synchronized void applyTransforms() {
        transform.translate(centerX, centerY, 0.0);
        transform.transform(xyzPoints);                
    }    
    
    /**
     * Getter for property labelColor.
     * @return Value of property labelColor.
     */
    public Color getLabelColor() {
        return this.labelColor;
    }
    
    /**
     * Setter for property labelColor.
     * @param labelColor New value of property labelColor.
     */
    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        
        refreshPoints();
    }
    
    /**
     * Getter for property xAxisColor.
     * @return Value of property xAxisColor.
     */
    public Color getXAxisColor() {
        return this.xAxisColor;
    }
    
    /**
     * Setter for property xAxisColor.
     * @param xAxisColor New value of property xAxisColor.
     */
    public void setXAxisColor(Color xAxisColor) {
        this.xAxisColor = xAxisColor;
        
        refreshPoints();
    }
    
    /**
     * Getter for property yAxisColor.
     * @return Value of property yAxisColor.
     */
    public Color getYAxisColor() {
        return this.yAxisColor;
    }
    
    /**
     * Setter for property yAxisColor.
     * @param yAxisColor New value of property yAxisColor.
     */
    public void setYAxisColor(Color yAxisColor) {
        this.yAxisColor = yAxisColor;
        
        refreshPoints();
    }
    
    /**
     * Getter for property zAxisColor.
     * @return Value of property zAxisColor.
     */
    public Color getZAxisColor() {
        return this.zAxisColor;
    }
    
    /**
     * Setter for property zAxisColor.
     * @param zAxisColor New value of property zAxisColor.
     */
    public void setZAxisColor(Color zAxisColor) {
        this.zAxisColor = zAxisColor;
        
        refreshPoints();
    }
    
} // end of class Axis
