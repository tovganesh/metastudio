/*
 * FragmentScreenBond.java
 *
 * Created on February 2, 2005, 9:40 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;

import org.meta.config.impl.AtomInfo;

/**
 * Represents a fragment bond on screen.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentScreenBond extends ScreenBond {
    
    /** Creates a new instance of FragmentScreenBond */
    public FragmentScreenBond(ScreenAtom atom1, ScreenAtom atom2) {
        super(atom1, atom2);
    }
    
    public void draw(Graphics2D g2d) {        
        // TODO : Needs refinement
        int mx = (atom1.getCurrentX() + atom2.getCurrentX()) / 2;
        int my = (atom1.getCurrentY() + atom2.getCurrentY()) / 2;
        
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER, 1.0f));
  
        g2d.setColor(atom1.getTransparentColor(100));        
        g2d.drawLine(atom1.getCurrentX(), atom1.getCurrentY(), mx, my);        
        g2d.setColor(atom2.getTransparentColor(100));        
        g2d.drawLine(atom2.getCurrentX(), atom2.getCurrentY(), mx, my);
                
        if (selected || (atom1.isSelected() && atom2.isSelected())) {
            g2d.setColor(selectionColor);
            g2d.drawLine(atom1.getCurrentX(), atom1.getCurrentY(), mx, my);        
            g2d.drawLine(atom2.getCurrentX(), atom2.getCurrentY(), mx, my);
        } // end if
        
        g2d.setStroke(oldStroke);
    }
    
} // end of class FragmentScreenBond
