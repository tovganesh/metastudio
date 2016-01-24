/*
 * ScreenVector.java
 *
 * Created on January 1, 2006, 7:07 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.graphics.AbstractGlyph;
import java.awt.*;

import org.meta.math.geom.Point3DI;
import org.meta.math.Vector3D;
import org.meta.math.Matrix3D;

/**
 * Represents a Vector3D object on screen.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenVector extends AbstractGlyph {
    
    private Point3DI screenPointOrigin, screenPointEnd;
    
    private int [] x, y;
    private int x1, y1;
    private float m1, m2, m3, m4, vLength, th, ta;    
    
    /** Creates a new instance of ScreenVector */
    public ScreenVector(Vector3D vector) {
        this.vector = vector;
        this.screenPointOrigin = new Point3DI(vector.getI(), vector.getJ(),
                                              vector.getK());
        vector = vector.add(vector.normalize().mul(vector.magnitude()));
        this.screenPointEnd = new Point3DI(vector.getI(), vector.getJ(),
                                           vector.getK());
        
        this.color = Color.green;
        
        x = new int[3];
        y = new int[3];
        
        theta = 10; // 10 degrees
        arrowWidth = 10; // 10 pixels
    }
    
    /** Creates a new instance of ScreenVector */
    public ScreenVector(Vector3D vector, Point3DI base) {
        this.vector = vector;
        this.screenPointOrigin = base;        
        this.screenPointEnd = new Point3DI(base.add(vector.normalize().mul(
                                             vector.magnitude()).toPoint3D()));
        
        this.color = Color.green;
        
        x = new int[3];
        y = new int[3];
        
        theta = 10; // 10 degrees
        arrowWidth = 10; // 10 pixels
    }
    
    /**
     * paints the atom on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {   
        transform.transformPoint(screenPointOrigin);
        transform.transformPoint(screenPointEnd);
        
        double magnitude = vector.magnitude();
        
        Stroke currentStroke = g2d.getStroke();
        
        if (magnitude < 0.1) {
          arrowWidth = 2.0;  
          g2d.setStroke(new BasicStroke(1));
          g2d.setColor(color.darker().darker().darker().darker().darker());           
        } else if (magnitude >= 0.1 && magnitude < 0.5) {
          arrowWidth = 4.0;  
          g2d.setStroke(new BasicStroke(2));
          g2d.setColor(color.darker().darker().darker()); 
        } else if (magnitude >= 0.5 && magnitude < 1.0) {
          arrowWidth = 6.0;
          g2d.setStroke(new BasicStroke(3));
          g2d.setColor(color.darker());    
        } else if (magnitude >= 1.0 && magnitude < 5.0) {
          arrowWidth = 8.0;
          g2d.setStroke(new BasicStroke(4));  
          g2d.setColor(color.brighter());     
        } else {
          arrowWidth = 10.0;
          g2d.setStroke(new BasicStroke(5));
          g2d.setColor(color.brighter().brighter().brighter());   
        } 
        
        g2d.drawLine(x1=screenPointOrigin.getCurrentX(), 
                     y1=screenPointOrigin.getCurrentY(),
                     x[0]=screenPointEnd.getCurrentX(),
                     y[0]=screenPointEnd.getCurrentY());
    
        m1 = (float) (x[0]-x1); 
        m2 = (float) (y[0]-y1);
        
        m3 = -m2;
        m4 = m1;
        
        vLength = (float) Math.sqrt(m1*m1 + m2*m2);
        
        th = (float) (arrowWidth / (2.0f * vLength));
	ta = (float) (arrowWidth / (2.0f * (Math.tan(theta) / 2.0f) * vLength));
         
        x1 = (int) (x[0] + -ta*m1);
        y1 = (int) (y[0] + -ta*m2);         

        x[1] = (int) (x1 + th*m3);
        y[1] = (int) (y1 + th*m4);
        x[2] = (int) (x1 + -th*m3);
        y[2] = (int) (y1 + -th*m4);
                
        g2d.fillPolygon(x, y, 3);
        
        if (selected) {            
            g2d.setColor(selectionColor);
            
            g2d.drawLine(screenPointOrigin.getCurrentX(), 
                         screenPointOrigin.getCurrentY(),
                         screenPointEnd.getCurrentX(),
                         screenPointEnd.getCurrentY());            
            g2d.fillPolygon(x, y, 3);
        } // end if
        
        g2d.setStroke(currentStroke);
    }

    /**
     * Holds value of property vector.
     */
    private Vector3D vector;

    /**
     * Getter for property vector.
     * @return Value of property vector.
     */
    public Vector3D getVector() {
        return this.vector;
    }

    /**
     * Holds value of property transform.
     */
    private Matrix3D transform;

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
     * method to apply local transformations
     */
    public synchronized void applyTransforms() {
        // TODO: to be done
        if (transform == null) {
            transform = new Matrix3D();
            transform.unit();
        } // end if
    }

    /**
     * Holds value of property color.
     */
    private Color color;

    /**
     * Getter for property color.
     * @return Value of property color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Setter for property color.
     * @param color New value of property color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Holds value of property theta.
     */
    private double theta;

    /**
     * Getter for property theta.
     * @return Value of property theta.
     */
    public double getTheta() {
        return this.theta;
    }

    /**
     * Setter for property theta.
     * @param theta New value of property theta.
     */
    public void setTheta(double theta) {
        this.theta = theta;
    }

    /**
     * Holds value of property arrowWidth.
     */
    private double arrowWidth;

    /**
     * Getter for property arrowWidth.
     * @return Value of property arrowWidth.
     */
    public double getArrowWidth() {
        return this.arrowWidth;
    }

    /**
     * Setter for property arrowWidth.
     * @param arrowWidth New value of property arrowWidth.
     */
    public void setArrowWidth(double arrowWidth) {
        this.arrowWidth = arrowWidth;
    }
} // end of class ScreenVector
