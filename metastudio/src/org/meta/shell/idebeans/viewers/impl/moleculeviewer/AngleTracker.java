/*
 * AngleTracker.java
 *
 * Created on January 18, 2004, 10:11 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.text.DecimalFormat;
import org.meta.common.Utility;
import org.meta.math.MathUtil;
import org.meta.math.Matrix3D;
import org.meta.math.geom.Point3D;
import org.meta.math.geom.Point3DI;

/**
 * Represents a angle tracker (between vectors defined by three atom centers).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AngleTracker extends AbstractTracker {
    
    /** Holds value of property atom1. */
    private ScreenAtom atom1;
    
    /** Holds value of property atom2. */
    private ScreenAtom atom2;
    
    /** Holds value of property atom3. */
    private ScreenAtom atom3;
    
    private String angleString;
    
    private QuadCurve2D theArc;    
    
    private Point3DI p1, p2, p3;
    
    /** Creates a new instance of AngleTracker */
    public AngleTracker(ScreenAtom atom1, ScreenAtom atom2, ScreenAtom atom3) {
        this.atom1 = atom1;
        this.atom2 = atom2;
        this.atom3 = atom3;
        
        // find out the angle                
        double angle = MathUtil.toDegrees(
                           MathUtil.findAngle(
                                    atom1.getAtom().getAtomCenter(),
                                    atom2.getAtom().getAtomCenter(),
                                    atom3.getAtom().getAtomCenter()
                           )
                       );
        
        angleString = (new DecimalFormat("#.###").format(angle))
                      + " " + Utility.DEGREE_SYMBOL;
        
        // and prepare the screen label
        screenLabel = new ScreenLabel(angleString);  
        
        theArc = new QuadCurve2D.Double();
        
        isolated = false;
    }
    
    /** Creates a new instance of AngleTracker */
    public AngleTracker(Point3D p1, Point3D p2, Point3D p3) {
        this.p1 = new Point3DI(p1);
        this.p2 = new Point3DI(p2);
        this.p3 = new Point3DI(p3);
        
        // find out the angle                
        double angle = MathUtil.toDegrees(MathUtil.findAngle(p1, p2, p3));
        
        angleString = (new DecimalFormat("#.###").format(angle))
                      + " " + Utility.DEGREE_SYMBOL;
        
        // and prepare the screen label
        screenLabel = new ScreenLabel(angleString);  
        
        theArc = new QuadCurve2D.Double();
        
        isolated = true;
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
            
            transform.transformPoint(p1);
            transform.transformPoint(p2);
            transform.transformPoint(p3);            
        } // end if
    }
    
    /**
     * draw the angle tracker between the three atoms specified
     */
    @Override
    public void draw(Graphics2D g2d) {  
        Stroke drawingStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                               BasicStroke.JOIN_BEVEL, 
                                               0, new float[]{1, 3}, 0);
        
        Stroke currentStroke = g2d.getStroke();
        
        g2d.setStroke(drawingStroke);
        
        // and then draw the stuff
        if (!isolated) {
            // and the arc        
            double midX12 = (atom1.getCurrentX() + atom2.getCurrentX()) / 2.0;
            double midY12 = (atom1.getCurrentY() + atom2.getCurrentY()) / 2.0;
            double midX23 = (atom2.getCurrentX() + atom3.getCurrentX()) / 2.0;
            double midY23 = (atom2.getCurrentY() + atom3.getCurrentY()) / 2.0;
            double midX13 = (atom1.getCurrentX() + atom3.getCurrentX()) / 2.0;
            double midY13 = (atom1.getCurrentY() + atom3.getCurrentY()) / 2.0;

            // use the same color as the screen lable
            g2d.setColor(screenLabel.getColor());         

            theArc.setCurve(new Point2D.Double(midX12, midY12),
                            new Point2D.Double(midX13, midY13),
                            new Point2D.Double(midX23, midY23));
            g2d.draw(theArc);

            screenLabel.setX((atom1.getCurrentX() + atom3.getCurrentX()) / 2);
            screenLabel.setY((atom1.getCurrentY() + atom3.getCurrentY()) / 2);
            screenLabel.draw(g2d);
        } else {
            applyTransforms();
            // and the arc        
            double midX12 = (p1.getCurrentX() + p2.getCurrentX()) / 2.0;
            double midY12 = (p1.getCurrentY() + p2.getCurrentY()) / 2.0;
            double midX23 = (p2.getCurrentX() + p3.getCurrentX()) / 2.0;
            double midY23 = (p2.getCurrentY() + p3.getCurrentY()) / 2.0;
            double midX13 = (p1.getCurrentX() + p3.getCurrentX()) / 2.0;
            double midY13 = (p1.getCurrentY() + p3.getCurrentY()) / 2.0;

            // use the same color as the screen lable
            g2d.setColor(screenLabel.getColor());         

            theArc.setCurve(new Point2D.Double(midX12, midY12),
                            new Point2D.Double(midX13, midY13),
                            new Point2D.Double(midX23, midY23));
            g2d.draw(theArc);

            screenLabel.setX((p1.getCurrentX() + p3.getCurrentX()) / 2);
            screenLabel.setY((p1.getCurrentY() + p3.getCurrentY()) / 2);
            screenLabel.draw(g2d);
        } // end if
        
        g2d.setStroke(currentStroke);
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
    
    /** Getter for property atom3.
     * @return Value of property atom3.
     *
     */
    public ScreenAtom getAtom3() {
        return this.atom3;
    }
    
    /** Setter for property atom3.
     * @param atom3 New value of property atom3.
     *
     */
    public void setAtom3(ScreenAtom atom3) {
        this.atom3 = atom3;
    }
    
} // end of class AngleTracker
