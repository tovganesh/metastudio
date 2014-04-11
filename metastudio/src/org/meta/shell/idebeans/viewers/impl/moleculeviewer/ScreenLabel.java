/*
 * ScreenLabel.java
 *
 * Created on January 18, 2004, 10:07 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.graphics.AbstractGlyph;
import java.awt.*;

import org.meta.common.resource.FontResource;
import org.meta.common.resource.ColorResource;

/**
 * Represents a label in the MoleculeScene.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenLabel extends AbstractGlyph {
    
    /** Holds value of property font. */
    private Font font;
    
    /** Holds value of property position. */
    private Point position;
    
    /** Holds value of property label. */
    private String label;
    
    /** Holds value of property color. */
    private Color color;
    
    /** Creates a new instance of ScreenLabel */
    public ScreenLabel(String label) {
        super();
        
        this.label = label;
        
        position = new Point(0, 0);
        color    = ColorResource.getInstance().getScreenLabelColor();
        font     = FontResource.getInstance().getScreenLabelFont();
    }
    
    /**
     * draw the lable on the screen
     */
    public void draw(Graphics2D g2d) {
        Font oldFont = g2d.getFont();
        
        g2d.setColor(color);
        g2d.setFont(font);
        
        g2d.drawString(label, position.x, position.y);
        
        g2d.setFont(oldFont);
    }
    
    /** Getter for property font.
     * @return Value of property font.
     *
     */
    public Font getFont() {
        return this.font;
    }
    
    /** Setter for property font.
     * @param font New value of property font.
     *
     */
    public void setFont(Font font) {
        this.font = font;
    }
    
    /** Getter for property position.
     * @return Value of property position.
     *
     */
    public Point getPosition() {
        return this.position;
    }
    
    /** Setter for property position.
     * @param position New value of property position.
     *
     */
    public void setPosition(Point position) {
        this.position = position;
    }
    
    /** Getter for property label.
     * @return Value of property label.
     *
     */
    public String getLabel() {
        return this.label;
    }
    
    /** Setter for property label.
     * @param label New value of property label.
     *
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /** Getter for property color.
     * @return Value of property color.
     *
     */
    public Color getColor() {
        return this.color;
    }
    
    /** Setter for property color.
     * @param color New value of property color.
     *
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /** Getter for property x.
     * @return Value of property x.
     *
     */
    public int getX() {
        return position.x;
    }
    
    /** Setter for property x.
     * @param x New value of property x.
     *
     */
    public void setX(int x) {
        position.x = x;
    }
    
    /** Getter for property y.
     * @return Value of property y.
     *
     */
    public int getY() {
        return position.y;
    }
    
    /** Setter for property y.
     * @param y New value of property y.
     *
     */
    public void setY(int y) {
        position.y = y;
    }
    
} // end of class ScreenLabel
