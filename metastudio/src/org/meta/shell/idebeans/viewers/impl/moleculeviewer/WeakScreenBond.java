/*
 * WeakScreenBond.java
 *
 * Created on February 7, 2004, 7:00 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;

import org.meta.config.impl.AtomInfo;

/**
 * Represents a weak bond on the screen.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WeakScreenBond extends ScreenBond {
    
    private static final BasicStroke weakStroke = new BasicStroke(1.0f, 
               BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, 
               new float [] {3.0f}, 0.0f);
    
    /** Creates a new instance of WeakScreenBond */
    public WeakScreenBond(ScreenAtom atom1, ScreenAtom atom2) {
        super(atom1, atom2);
    }
    
    public void draw(java.awt.Graphics2D g2d) {    
        // TODO : Needs refinement
        int mx = (atom1.getCurrentX() + atom2.getCurrentX()) / 2;
        int my = (atom1.getCurrentY() + atom2.getCurrentY()) / 2;
        
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(weakStroke);
                                    
        g2d.setColor(atom1.getColor());
        g2d.drawLine(atom1.getCurrentX(), atom1.getCurrentY(), mx, my);
        g2d.setColor(atom2.getColor());
        g2d.drawLine(atom2.getCurrentX(), atom2.getCurrentY(), mx, my);                
        
        if (selected || (atom1.isSelected() && atom2.isSelected())) {
            g2d.setColor(selectionColor);
            g2d.drawLine(atom1.getCurrentX(), atom1.getCurrentY(), mx, my);        
            g2d.drawLine(atom2.getCurrentX(), atom2.getCurrentY(), mx, my);
        } // end if
        
        g2d.setStroke(oldStroke);
    }
    
} // end of class WeakScreenBond
