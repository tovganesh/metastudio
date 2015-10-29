/*
 * ScreenAtom.java
 *
 * Created on January 18, 2004, 10:04 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.graphics.AbstractGlyph;
import java.awt.*;

import java.util.ArrayList;
import org.meta.math.geom.Point3D;
import org.meta.math.geom.Point3DI;
import org.meta.math.Matrix3D;
import org.meta.math.Vector3D;
import org.meta.config.impl.AtomInfo;
import org.meta.common.resource.ColorResource;
import org.meta.fragment.FragmentAtom;
import org.meta.molecule.Atom;
import org.meta.molecule.UserDefinedAtomProperty;
import org.meta.shell.idebeans.graphics.PaintGlyphObject;

/**
 * Represents an atom on screen.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenAtom extends AbstractGlyph {
    
    /** Holds value of property atom. */
    protected Atom atom;
    
    /** Holds value of property currentX. */
    protected int currentX;
    
    /** Holds value of property currentY. */
    protected int currentY;
    
    /** Holds value of property currentZ. */
    protected int currentZ;            
    
    /** Holds value of property screenLabel. */
    protected ScreenLabel screenLabel;
    
    protected boolean symbolLabel, idLabel, centerLabel;
    protected String labelString;
    
    /**
     * Holds value of property color.
     */
    protected Color color;

    /**
     * Holds value of property fragmentAtom.
     */
    protected boolean fragmentAtom;

    /**
     * Holds value of property transparentColor.
     */
    protected Color transparentColor;

    /**
     * Holds value of property screenBondList.
     */
    private ArrayList<ScreenBond> screenBondList;
    
    /**
     * Holds value of property radius.
     */
    protected double radius;
    
    protected double covalentRadius;
    
    /**
     * Is a vector component?
     */
    protected ScreenVector vector;
    
    protected ScreenSphere selectionSphere;
    
    /** Creates a new instance of ScreenAtom */
    public ScreenAtom(Atom atom) {
        super();
        
        this.atom = atom;
        
        currentX = (int) atom.getX();
        currentY = (int) atom.getY();
        currentZ = (int) atom.getZ();                
        
        selectionColor = ColorResource.getInstance().getSelectedAtomColor();
        
        symbolLabel = idLabel = centerLabel = false;
        
        color = AtomInfo.getInstance().getColor(atom.getSymbol()); 
        
        radius = 2.0;        
        
        fragmentAtom = false;
        
        screenBondList = new ArrayList<ScreenBond>(4);
        
        transform = new Matrix3D();
        transform.unit();
        
        if (atom.getSymbol().equals("Vec")) {
            UserDefinedAtomProperty ua = atom.getUserDefinedAtomProperty("baseCenter");
            
            if (ua == null) {
                vector = new ScreenVector(new Vector3D(atom.getAtomCenter()));
                vector.setSelectionColor(selectionColor);
            } else {
                Point3D baseCenter = (Point3D) ua.getValue();    
                vector = new ScreenVector(new Vector3D(atom.getAtomCenter()), new Point3DI(baseCenter));
                vector.setSelectionColor(selectionColor);
            }
        } // end if      
        
        covalentRadius = AtomInfo.getInstance()
                                 .getCovalentRadius(atom.getSymbol());
    }
    
    /** Creates a new instance of ScreenAtom representing a fragment atom */
    public ScreenAtom(FragmentAtom fAtom) {
        this((Atom) fAtom);
        
        fragmentAtom = true;        
        radius = 8.0;
    }
    
    /**
     * paints the atom on the screen
     */
    @Override
    public void draw(Graphics2D g2d) {     
        if (!visible) return;
        
        // TODO : Needs refinement
        if (vector != null) { // is it a vector component?
            vector.setTransform(transform);
            vector.setColor(color);
            vector.setSelected(selected);
            vector.draw(g2d);           
        } else {        
            // normal atom, draw it!
            g2d.setColor(color);
            g2d.fillOval((int) (currentX-(radius/2.0)), 
                         (int) (currentY-(radius/2.0)), 
                         (int) radius, (int) radius);        

            if (selected) {
                if (selectionSphere == null) {
                  selectionSphere = new ScreenSphere(atom.getAtomCenter(), 7.0);
                  selectionSphere.setScaleFactor(1.0);
                } // end if
                
                selectionSphere.setTransform(transform);
                selectionSphere.applyTransforms();
                selectionSphere.draw(g2d);
            } // end if
        } //end if
        
        if (symbolLabel || idLabel || centerLabel) {
            // TODO : Needs refinement
            screenLabel.setColor(color);
            screenLabel.setX(currentX);
            screenLabel.setY(currentY);
            screenLabel.draw(g2d);
        } // end if       
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
        if (vector != null) { // is it a vector component?
            vector.setTransform(transform);
            vector.setColor(color);
            vector.setSelected(selected);
            vector.paintGlyph(pgo);           
        } else {        
            // normal atom, draw it!
            pgo.setDrawingColor(color);
            pgo.fillOval(new Point3D((currentX-(radius/2.0)), 
                                     (currentY-(radius/2.0)), 
                                     (currentZ-(radius/2.0))), 
                         (int) radius, (int) radius);        

            if (selected) {
                if (selectionSphere == null) {
                  selectionSphere = new ScreenSphere(atom.getAtomCenter(), 7.0);
                  selectionSphere.setScaleFactor(1.0);
                } // end if
                
                selectionSphere.setTransform(transform);
                selectionSphere.applyTransforms();
                selectionSphere.paintGlyph(pgo);
            } // end if
        } //end if
        
        if (symbolLabel || idLabel || centerLabel) {
            // TODO : Needs refinement
            screenLabel.setColor(color);
            screenLabel.setX(currentX);
            screenLabel.setY(currentY);
            screenLabel.paintGlyph(pgo);
        } // end if 
    }
    
    /**
     * method to test whether the specified point is near by this atom
     *
     * @param point - the point to be tested
     * @return boolean - true is the point is near by, else false.
     */
    public boolean contains(Point point) {
        return contains(point.x, point.y);
    }
    
    /**
     * method to test whether the specified point is near by this atom
     *
     * @param x the X coordinate of the point to be tested
     * @param y the Y coordinate of the point to be tested
     * @return boolean - true is the point is near by, else false.
     */
    public boolean contains(int x, int y) {
        return ((x > currentX-4 && x < currentX+4)
                && (y > currentY-4 && y < currentY+4));
    }
    
    /**
     * the distance in 2D from the other ScreenAtom
     *
     * @param atom - the screen atom from which the 2d distance is to be
     *               computed
     * @return the distance
     */
    public int distanceFrom(ScreenAtom atom) {
        int x = atom.currentX - currentX;
        int y = atom.currentY - currentY;
        
        return (int) Math.sqrt(x*x + y*y);
    }
    
    /** Getter for property atom.
     * @return Value of property atom.
     *
     */
    public Atom getAtom() {
        return this.atom;
    }
    
    /** Setter for property atom.
     * @param atom New value of property atom.
     *
     */
    public void setAtom(Atom atom) {
        this.atom = atom;
    }
    
    /** Getter for property currentX.
     * @return Value of property currentX.
     *
     */
    public int getCurrentX() {
        return this.currentX;
    }
    
    /** Setter for property currentX.
     * @param currentX New value of property currentX.
     *
     */
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }
    
    /** Getter for property currentY.
     * @return Value of property currentY.
     *
     */
    public int getCurrentY() {
        return this.currentY;
    }
    
    /** Setter for property currentY.
     * @param currentY New value of property currentY.
     *
     */
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }
    
    /** Getter for property currentZ.
     * @return Value of property currentZ.
     *
     */
    public int getCurrentZ() {
        return this.currentZ;
    }
    
    /** Setter for property currentZ.
     * @param currentZ New value of property currentZ.
     *
     */
    public void setCurrentZ(int currentZ) {
        this.currentZ = currentZ;
    }
    
    /**
     * Return the current position
     * 
     * @return the current position
     */
    public Point3D getCurrentPosition() {
        return new Point3D(currentX, currentY, currentZ);        
    }
    
    /** Getter for property x.
     * @return Value of property x.
     *
     */
    public double getX() {
        return atom.getX();
    }
    
    /** Getter for property y.
     * @return Value of property y.
     *
     */
    public double getY() {
        return atom.getY();
    }
    
    /** Getter for property z.
     * @return Value of property z.
     *
     */
    public double getZ() {
        return atom.getZ();
    }
        
    /**
     * method to apply local transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // TODO: to be done
        if (transform == null) {
            transform = new Matrix3D();
            transform.unit();
        } // end if
        
        transform.transform(this);
    }
    
    /**
     * overridden toString()
     */
    @Override
    public String toString() {
        return (atom.toString() + "( " + currentX + ", " 
                                       + currentY + ", " 
                                       + currentZ + " )");
    }
    
    /** Getter for property screenLabel.
     * @return Value of property screenLabel.
     *
     */
    public ScreenLabel getScreenLabel() {
        return this.screenLabel;
    }
    
    /** Setter for property screenLabel.
     * @param screenLabel New value of property screenLabel.
     *
     */
    public void setScreenLabel(ScreenLabel screenLabel) {
        this.screenLabel = screenLabel;
    }
    
    /**
     * show/ hide the symbol label or not
     */
    public void showSymbolLabel(boolean symbolLabel) {
        this.symbolLabel = symbolLabel;        
        makeLabelString();
    }
    
    /**
     * current status of symbol label
     */
    public boolean isShowingSymbolLabel() {
        return this.symbolLabel;
    }
    
    /**
     * show/ hide the ID label or not
     */
    public void showIDLabel(boolean idLabel) {
        this.idLabel = idLabel;
        makeLabelString();
    }
    
    /**
     * current status of id label
     */
    public boolean isShowingIDLabel() {
        return this.idLabel;
    }
    
    /**
     * show/ hide the center label or not
     */
    public void showCenterLabel(boolean centerLabel) {
        this.centerLabel = centerLabel;
        makeLabelString();
    }
    
    /**
     * current status of atom center label
     */
    public boolean isShowingCenterLabel() {
        return this.centerLabel;
    }
    
    /**
     * make the label string
     */
    protected void makeLabelString() {
        labelString = " ";
        
        if (symbolLabel) labelString += atom.getSymbol();
        if (idLabel)     labelString += "(" + atom.getIndex()      + ")";
        if (centerLabel) labelString += " {" + atom.getX() + ", "
                                             + atom.getY() + ", "
                                             + atom.getZ() + "}";
        
        if (screenLabel == null) {            
            screenLabel = new ScreenLabel(labelString);
        } else { 
            screenLabel.setLabel(labelString);
        } // end if
    }
    
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
     * Getter for property radius.
     * @return Value of property radius.
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Getter for property fragmentAtom.
     * @return Value of property fragmentAtom.
     */
    public boolean isFragmentAtom() {
        return this.fragmentAtom;
    }

    /**
     * Getter for property transparentColor.
     * @return Value of property transparentColor.
     */
    public Color getTransparentColor(int alpha) {
        if ((transparentColor == null) 
            || (transparentColor.getAlpha() != alpha)) {            
            transparentColor = new Color(color.getRed(),
                                         color.getGreen(),
                                         color.getBlue(),
                                         alpha);
        } // end if
        
        return transparentColor;
    }

    /**
     * Getter for property screenBondList.
     * @return Value of property screenBondList.
     */
    public ArrayList<ScreenBond> getScreenBondList() {
        return this.screenBondList;
    }

    /**
     * Setter for property screenBondList.
     * @param screenBondList New value of property screenBondList.
     */
    public void setScreenBondList(ArrayList<ScreenBond> screenBondList) {
        this.screenBondList = screenBondList;
    }
    
    /**
     * Setter for property radius.
     * @param radius New value of property radius.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Holds value of property vectorBase.
     */
    private Point3D vectorBase;

    /**
     * Getter for property vectorBase.
     * @return Value of property vectorBase.
     */
    public Point3D getVectorBase() {
        return this.vectorBase;
    }

    /**
     * Setter for property vectorBase.
     * @param vectorBase New value of property vectorBase.
     */
    public void setVectorBase(Point3D vectorBase) {
        this.vectorBase = vectorBase;
        
        if (atom.getSymbol().equals("Vec")) {
            vector = new ScreenVector(new Vector3D(atom.getAtomCenter()),
                                      new Point3DI(vectorBase));
            vector.setSelectionColor(selectionColor);
        } // end if  
    }
    
} // end of class ScreenAtom
