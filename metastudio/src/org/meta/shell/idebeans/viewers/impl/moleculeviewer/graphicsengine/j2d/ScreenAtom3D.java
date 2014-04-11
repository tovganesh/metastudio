/*
 * ScreenAtom3D.java
 *
 * Created on April 23, 2005, 9:55 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j2d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import java.awt.*;
import org.meta.config.impl.AtomInfo;
import org.meta.molecule.Atom;

/**
 * Represents an atom on screen (3D).
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenAtom3D extends ScreenAtom {
    
    private double covalentRadius;            
    
    /** Creates a new instance of ScreenAtom3D */
    public ScreenAtom3D(Atom atom) {
        super(atom);
        
        covalentRadius = AtomInfo.getInstance()
                                 .getCovalentRadius(atom.getSymbol());        
    }        
    
    /**
     * paints the atom on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {     
        if (vector != null) { // is it a vector component?
            vector.setTransform(transform);
            vector.setColor(color);
            vector.setSelected(selected);
            vector.draw(g2d);            
        } else {        
            // if normal atom draw it            
            radius = (SphereFactory.MAX_RADIUS/4 + covalentRadius * currentZ 
                                               * transform.getScaleFactor());
            
            int size = (int) radius;
            if (size < 10) size = 10;

            g2d.drawImage(SphereFactory.getInstance()
                                       .getSphereImage(atom.getSymbol()), 
                          currentX - (size >> 1), currentY - (size >> 1), 
                          size, size, null);

            if (selected) {
                size += 2;
                g2d.setColor(selectionColor);            
                g2d.fillOval(currentX-size/2, currentY-size/2, size, size);
            } // end if
        } // end if
        
        if (symbolLabel || idLabel || centerLabel) {
            // TODO : Needs refinement
            screenLabel.setColor(color);
            screenLabel.setX(currentX);
            screenLabel.setY(currentY);
            screenLabel.draw(g2d);
        } // end if       
    }
    
    /**
     * method to test whether the specified point is near by this atom
     *
     * @param x the X coordinate of the point to be tested
     * @param y the Y coordinate of the point to be tested
     * @return boolean - true is the point is near by, else false.
     */
    @Override
    public boolean contains(int x, int y) {
        double r = radius/2.0;
        return ((x > currentX-r && x < currentX+r)
                && (y > currentY-r && y < currentY+r));
    }
} // end of class ScreenAtom3D
