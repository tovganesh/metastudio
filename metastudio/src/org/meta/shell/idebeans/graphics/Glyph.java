/*
 * Glyph.java
 *
 * Created on January 18, 2004, 10:03 PM
 */

package org.meta.shell.idebeans.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import org.meta.math.Matrix3D;

/**
 * Represents a graphics/text object.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Glyph {
    
    /**
     * This method is called on individual Glyphs to draw themselves on the
     * canvas. It the responsibility of each Glyph to record its current 
     * position on the canvas.
     *
     * @param g2d - instance of Graphics2D for drawing on the concerned canvas
     */
    public void draw(Graphics2D g2d);        
    
    /** 
     * Draw the contents in a RIB file
     *
     * @param fos - the file stream connected to RIB file
     * @param shader - a string representing RIB shader
     * @param scaleFactor - scaling applied to individual objects
     */
    public void drawInRIBFile(java.io.FileWriter fos, String shader, 
                              double scaleFactor) throws java.io.IOException;

    /**
     * Generic paint method for this glyph.
     *
     * @param pgo The instance of PaintGlyphObject that will actually provide
     *            primitives to render this glyph.
     */
    public void paintGlyph(PaintGlyphObject pgo);

    /** Getter for property hidden.
     * @return Value of property hidden.
     *
     */
    public boolean isSelected();
    
    /** Setter for property hidden.
     * @param hidden New value of property hidden.
     *
     */
    public void setSelected(boolean hidden);
    
    /** Getter for property visible.
     * @return Value of property visible.
     *
     */
    public boolean isVisible();
    
    /** Setter for property visible.
     * @param visible New value of property visible.
     *
     */
    public void setVisible(boolean visible);
    
    /** Getter for property selectionColor.
     * @return Value of property selectionColor.
     *
     */
    public Color getSelectionColor();
    
    /** Setter for property selectionColor.
     * @param selectionColor New value of property selectionColor.
     *
     */
    public void setSelectionColor(Color selectionColor);
    
    /**
     * Getter for property highlight.
     * @return Value of property highlight.
     */
    public boolean isHighlight();
    
    /**
     * Setter for property highlight.
     * @param highlight New value of property highlight.
     */
    public void setHighlight(boolean highlight);
    
    /** Getter for property transform.
     * @return Value of property transform.
     *
     */
    public Matrix3D getTransform();
    
    /** Setter for property transform.
     * @param transform New value of property transform.
     *
     */
    public void setTransform(Matrix3D transform);
    
    /**
     * method to apply local transformations
     */
    public void applyTransforms();
} // end of interface Glyph
