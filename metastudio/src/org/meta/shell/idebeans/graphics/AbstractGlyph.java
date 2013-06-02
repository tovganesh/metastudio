/*
 * AbstractGlyph.java
 *
 * Created on January 21, 2004, 7:23 AM
 */

package org.meta.shell.idebeans.graphics;

import java.awt.Color;

import org.meta.common.resource.ColorResource;
import org.meta.math.Matrix3D;

/**
 * An abstract implementation of Glyph interface. 
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractGlyph implements Glyph {
    
    /** Holds value of property selected. */
    protected boolean selected;
    
    /** Holds value of property visible. */
    protected boolean visible;
    
    /** Holds value of property selectionColor. */
    protected Color selectionColor;
    
    /** Holds value of property highlight. */
    protected boolean highlight;
    
    /** Creates a new instance of AbstractGlyph */
    public AbstractGlyph() {
        selectionColor = ColorResource.getInstance().getDefaultSelectionColor();
        selected = false;
        visible  = true;
    }                        
    
    /** Getter for property hidden.
     * @return Value of property hidden.
     *
     */
    @Override
    public boolean isSelected() {
        return this.selected;
    }
    
    /** Setter for property hidden.
     * @param selected New value of property selected.
     *
     */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /** Getter for property visible.
     * @return Value of property visible.
     *
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    /** Setter for property visible.
     * @param visible New value of property visible.
     *
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /** Getter for property selectionColor.
     * @return Value of property selectionColor.
     *
     */
    @Override
    public Color getSelectionColor() {
        return this.selectionColor;
    }
    
    /** Setter for property selectionColor.
     * @param selectionColor New value of property selectionColor.
     *
     */
    @Override
    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
    }
    
    /**
     * Getter for property highlight.
     * @return Value of property highlight.
     */
    @Override
    public boolean isHighlight() {
        return highlight;
    }
    
    /**
     * Setter for property highlight.
     * @param highlight New value of property highlight.
     */
    @Override
    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
    
    /** Holds value of property transform. */
    protected Matrix3D transform;
    
    /** Getter for property transform.
     * @return Value of property transform.
     *
     */
    @Override
    public Matrix3D getTransform() {
        return this.transform;
    }
    
    /** Setter for property transform.
     * @param transform New value of property transform.
     *
     */
    @Override
    public void setTransform(Matrix3D transform) {
        this.transform = transform;
    }
    
    /**
     * method to apply local transformations, nothing done here, default 
     * implimentation
     */
    @Override
    public synchronized void applyTransforms() {
        // do nothing here .. detault impl
    }
    
    /** 
     * Draw the contents in a RIB file
     *
     * @param fos - the file stream connected to RIB file
     * @param shader - a string representing RIB shader
     * @param scaleFactor - scaling applied to inidividual objects
     */
    @Override
    public void drawInRIBFile(java.io.FileWriter fos, String shader,
                              double scaleFactor) throws java.io.IOException {
        // default implementation does nothing
    }

    /**
     * Generic paint method for this glyph.
     *
     * @param pgo The instance of PaintGlyphObject that will actually provide
     *            primitives to render this glyph.
     */
    @Override
    public void paintGlyph(PaintGlyphObject pgo) {
        // default implementation does nothing
    }
} // end of class AbstractGlyph
