/*
 * MultipleScreenBond.java
 *
 * Created on February 7, 2004, 6:59 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;
import org.meta.math.geom.Point3D;
import org.meta.shell.idebeans.graphics.PaintGlyphObject;


/**
 * Represents a multiple bond on the screen.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MultipleScreenBond extends ScreenBond {
    
    /** Creates a new instance of MultipleScreenBond */
    public MultipleScreenBond(ScreenAtom atom1, ScreenAtom atom2) {
        super(atom1, atom2);
    }

    /**
     * Draw double / triple bond
     *
     * @param g2d the instance of Graphics2D object
     */
    @Override
    public void draw(Graphics2D g2d) {   
        if (!visible) return;
        
        // TODO : Needs refinement
        int mx = (atom1.getCurrentX() + atom2.getCurrentX()) / 2;
        int my = (atom1.getCurrentY() + atom2.getCurrentY()) / 2;
        
        Stroke oldStroke = g2d.getStroke();        
        g2d.setStroke(new BasicStroke(2.0f));
        
        if (atom1.isVisible()) {        
            g2d.setColor(atom1.getColor());
            g2d.drawLine(atom1.getCurrentX(), atom1.getCurrentY(), mx, my);
        } // end if
        
        if (atom2.isVisible()) {            
            g2d.setColor(atom2.getColor());
            g2d.drawLine(atom2.getCurrentX(), atom2.getCurrentY(), mx, my);            
        } // end if
        
        if (selected || (atom1.isSelected() && atom2.isSelected())) {
            g2d.setColor(selectionColor);
            g2d.drawLine(atom1.getCurrentX(), atom1.getCurrentY(), 
                         atom2.getCurrentX(), atom2.getCurrentY());
        } // end if
        
        g2d.setStroke(oldStroke);
    }

    /**
     * Generic paint method for this glyph.
     *
     * @param pgo The instance of PaintGlyphObject that will actually provide
     *            primitives to render this glyph.
     */
    @Override
    public void paintGlyph(PaintGlyphObject pgo) {
        if (!visible) return;
        
        // TODO : Needs refinement
        int mx = (atom1.getCurrentX() + atom2.getCurrentX()) / 2;
        int my = (atom1.getCurrentY() + atom2.getCurrentY()) / 2;
        int mz = (atom1.getCurrentZ() + atom2.getCurrentZ()) / 2;
        
        Point3D midPoint = new Point3D(mx, my, mz);
        
        Stroke oldStroke = (Stroke) pgo.getAdditionalProperty("_stroke");        
        pgo.setAdditionalProperty("_stroke", new BasicStroke(2.0f));
        
        if (atom1.isVisible()) {
            pgo.setDrawingColor(atom1.getColor());
            pgo.drawLine(atom1.getCurrentPosition(), midPoint);
        } // end if
        
        if (atom2.isVisible()) {
            pgo.setDrawingColor(atom2.getColor());
            pgo.drawLine(atom2.getCurrentPosition(), midPoint);
        } // end if
         
        if (selected || (atom1.isSelected() && atom2.isSelected())) {
            pgo.setDrawingColor(selectionColor);
            pgo.drawLine(atom1.getCurrentPosition(), atom2.getCurrentPosition());
        } // end if
        
        pgo.setAdditionalProperty("_stroke", oldStroke);
    }
} // end of class MultipleScreenBond
