/*
 * DihedralTracker.java
 *
 * Created on January 18, 2004, 10:14 PM
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

/**
 * Represents the dihedral / torssian angle defined by planes formed by
 * four atom centers.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DihedralTracker extends AbstractTracker {
    
    /** Holds value of property atom1. */
    private ScreenAtom atom1;
    
    /** Holds value of property atom2. */
    private ScreenAtom atom2;
    
    /** Holds value of property atom3. */
    private ScreenAtom atom3;
    
    /** Holds value of property atom4. */
    private ScreenAtom atom4;
    
    private String angleString;
    
    private QuadCurve2D theArc;   
    
    /** Creates a new instance of DihedralTracker */
    public DihedralTracker(ScreenAtom atom1, ScreenAtom atom2, 
                           ScreenAtom atom3, ScreenAtom atom4) {
        this.atom1 = atom1;
        this.atom2 = atom2;
        this.atom3 = atom3;
        this.atom4 = atom4;               
    
        // and find the dihedral angle
        double angle = MathUtil.toDegrees(
                           MathUtil.findDihedral(
                                    atom1.getAtom().getAtomCenter(),
                                    atom2.getAtom().getAtomCenter(),
                                    atom3.getAtom().getAtomCenter(), 
                                    atom4.getAtom().getAtomCenter()
                           )
                       );
        
        angleString = (new DecimalFormat("#.###").format(angle))
                      + " " + Utility.DEGREE_SYMBOL;
        
        // and prepare the screen label
        screenLabel = new ScreenLabel(angleString);  
        
        theArc = new QuadCurve2D.Double();
    }
    
    /**
     * draws the dihedral angle atom1-atom2-atom3 and atom2-atom3-atom4
     */
    public void draw(Graphics2D g2d) {
        Stroke drawingStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                                               BasicStroke.JOIN_BEVEL, 
                                               0, new float[]{1, 3}, 0);
        
        Stroke currentStroke = g2d.getStroke();
        
        g2d.setStroke(drawingStroke);
        
        // and then draw the stuff
        // and the arc        
        double midX12 = (atom1.getCurrentX() + atom2.getCurrentX()) / 2.0;
        double midY12 = (atom1.getCurrentY() + atom2.getCurrentY()) / 2.0;
        double midX34 = (atom3.getCurrentX() + atom4.getCurrentX()) / 2.0;
        double midY34 = (atom3.getCurrentY() + atom4.getCurrentY()) / 2.0;
        double midX14 = (atom1.getCurrentX() + atom4.getCurrentX()) / 2.0;
        double midY14 = (atom1.getCurrentY() + atom4.getCurrentY()) / 2.0;
        
        // use the same color as the screen lable
        g2d.setColor(screenLabel.getColor());         
               
        theArc.setCurve(new Point2D.Double(midX12, midY12),
                        new Point2D.Double(midX14, midY14),
                        new Point2D.Double(midX34, midY34));
        g2d.draw(theArc);
        
        screenLabel.setX((atom1.getCurrentX() + atom4.getCurrentX()) / 2);
        screenLabel.setY((atom1.getCurrentY() + atom4.getCurrentY()) / 2);
        screenLabel.draw(g2d);
        
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
    
    /** Getter for property atom4.
     * @return Value of property atom4.
     *
     */
    public ScreenAtom getAtom4() {
        return this.atom4;
    }
    
    /** Setter for property atom4.
     * @param atom4 New value of property atom4.
     *
     */
    public void setAtom4(ScreenAtom atom4) {
        this.atom4 = atom4;
    }
    
} // end of class DihedralTracker
