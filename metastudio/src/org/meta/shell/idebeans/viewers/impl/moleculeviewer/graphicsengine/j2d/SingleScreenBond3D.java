/*
 * SingleScreenBond3D.java
 *
 * Created on May 2, 2005, 3:33 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j2d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import java.awt.*;
import java.awt.geom.*;

import org.meta.config.impl.AtomInfo;

/**
 * Represents a single bond on the screen (as 3D object).
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SingleScreenBond3D extends SingleScreenBond {
    
    private double covalentRadius;    
    
    private static final AffineTransform identityTransform 
                                         = new AffineTransform();
    
    private static final float PI_BY_2       = (float) (Math.PI/2.0);
    private static final float THREE_PI_BY_2 = (float) (PI_BY_2 + Math.PI);
    
    private boolean sameAtom = false;
    
    /** Creates a new instance of SingleScreenBond3D */
    public SingleScreenBond3D(ScreenAtom atom1, ScreenAtom atom2) {
        super(atom1, atom2);
        
        AtomInfo ai = AtomInfo.getInstance();
        
        covalentRadius = Math.min(
                            ai.getCovalentRadius(atom1.getAtom().getSymbol()),
                            ai.getCovalentRadius(atom2.getAtom().getSymbol()));
        
        sameAtom = atom1.getAtom().getSymbol().equals(
                                               atom2.getAtom().getSymbol());
    }
    
    /**
     * paints the bond on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {         
        int currentZ = Math.min(atom1.getCurrentZ(), atom2.getCurrentZ());
        int width = (int) (CylinderFactory.MAX_RADIUS/8 
                      + covalentRadius * currentZ
                      * atom1.getTransform().getScaleFactor()); 
        int radius = (int) (SphereFactory.MAX_RADIUS/4 
                      + covalentRadius * currentZ 
                      * atom1.getTransform().getScaleFactor());
        
        if (width < 4) width = 4;        
        if (width >= radius) width = radius/2;
            
        // TODO : Needs refinement 
        float x1 = atom1.getCurrentX();
        float y1 = atom1.getCurrentY();
        float x2 = atom2.getCurrentX();
        float y2 = atom2.getCurrentY();
        float mx = (x1+x2)/2;
        float my = (y1+y2)/2;
        float vx1 = (x1-mx);
        float vy1 = (y1-my);
        float vx2 = (x2-mx);
        float vy2 = (y2-my);        
        float theta = (float) Math.atan(vy1/vx1);                
        
        if (vx1 < 0.0) theta = THREE_PI_BY_2 + theta;
        else           theta = PI_BY_2 + theta;
                
        
        // TODO: this is a gamble to delibrately not set the transform 
        // matrix to identity and save a function call, and rely on the 
        // correctness of the sequence of transforms being applied
        if (sameAtom) {
            vx1 = (x1-x2);
            vy1 = (y1-y2);
            
            g2d.translate(x1, y1);
            g2d.rotate(theta);
            g2d.drawImage(CylinderFactory.getInstance()
                              .getCylinderImage(atom1.getAtom().getSymbol()), 
                      -(width>>1), -(width>>1), 
                      width, (int) Math.sqrt(vx1*vx1 + vy1*vy1), null);        
            g2d.rotate(-theta);    
            g2d.translate(-x1, -y1);
        } else {
            g2d.translate(x1, y1);
            g2d.rotate(theta);
            g2d.drawImage(CylinderFactory.getInstance()
                              .getCylinderImage(atom1.getAtom().getSymbol()), 
                      -(width>>1), -(width>>1), 
                      width, (int) Math.sqrt(vx1*vx1 + vy1*vy1)+2, null);        
            g2d.rotate(-theta);    
            g2d.translate(-x1, -y1);
                
            g2d.translate(mx, my);
            g2d.rotate(theta);
            g2d.drawImage(CylinderFactory.getInstance()
                              .getCylinderImage(atom2.getAtom().getSymbol()), 
                      -(width>>1), -(width>>1), 
                      width, (int) Math.sqrt(vx2*vx2 + vy2*vy2), null);        
            g2d.rotate(-theta);
            g2d.translate(-mx, -my);
            g2d.setTransform(identityTransform);        
        } // end if
        
        if (selected || (atom1.isSelected() && atom2.isSelected())) {
            Stroke oldStroke = g2d.getStroke();      
            g2d.setStroke(new BasicStroke((float) (width+2.0), 
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_ROUND, 1.0f));
            g2d.setColor(selectionColor);
            g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            g2d.setStroke(oldStroke);  
        } // end if        
    }
} // end of class SingleScreenBond3D
