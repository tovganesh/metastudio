/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import org.meta.math.geom.Point3D;
import org.meta.math.Vector3D;

/**
 * Abstract Paint object for rendering a Glyph. Provides all the
 * primitives needed to draw a Glyph.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface PaintGlyphObject {

    /**
     * Set the current drawing color for this paint object
     *
     * @param color the required color
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void setDrawingColor(Color color) throws GlyphPaintingException;

    /**
     * Get the current drawing color for this paint object
     *
     * @return the current drawing color
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public Color getDrawingColor() throws GlyphPaintingException;

    /**
     * Set the current text font
     *
     * @param font the new text rendering font object
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void setTextFont(Font font) throws GlyphPaintingException;

    /**
     * Get the current text font
     * 
     * @return the current text rendering font
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public Font getTextFont() throws GlyphPaintingException;
    
    /**
     * Draw a line between two points a and b
     *
     * @param a the starting point of line
     * @param b the ending point of line
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawLine(Point3D a, Point3D b) throws GlyphPaintingException;

    /**
     * Draw a Vector
     *
     * @param vec the vector object
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawVector(Vector3D vec) throws GlyphPaintingException;

    /**
     * Draw a Vector with a specified base point
     *
     * @param vec the vector object
     * @param basePoint the base point from where the vector is to be drawn
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawVector(Vector3D vec, Point3D basePoint) throws GlyphPaintingException;

    /**
     * Draw a text object at a given position
     *
     * @param text the text the be drawn
     * @param pos the position of the text in space
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawText(String text, Point3D pos) throws GlyphPaintingException;

    /**
     * Draw a circle with a given center and radius
     *
     * @param center the center the circle
     * @param radius the radius of the this circle
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawCircle(Point3D center, double radius) throws GlyphPaintingException;

    /**
     * Draw an Oval
     *
     * @param startingPoint the starting point    
     * @param width the width of this Oval
     * @param height the height of this Oval
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawOval(Point3D startingPoint,
                         double width, double height) throws GlyphPaintingException;

    /**
     * Draw a rectangle
     *
     * @param rect the Rectangle2D object to be drawn
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawRectangle(Rectangle2D rect) throws GlyphPaintingException;

    /**
     * Draw a polygon
     * 
     * @param polygon the polygon object
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void drawPolygon(Polygon polygon) throws GlyphPaintingException;

    /**
     * Draw a circle with a given center and radius,
     * and fill it with current drawing color
     *
     * @param center the center the circle
     * @param radius the radius of the this circle
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void fillCircle(Point3D center, double radius) throws GlyphPaintingException;

    /**
     * Draw an Oval, and fill it with current drawing color
     *
     * @param startingPoint the starting point
     * @param height the height of this Oval
     * @param width the width of this Oval
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void fillOval(Point3D startingPoint,
                         double height, double width) throws GlyphPaintingException;

    /**
     * Draw a rectangle, and fill it with current drawing color
     *
     * @param rect the Rectangle2D object to be drawn
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void fillRectangle(Rectangle2D rect) throws GlyphPaintingException;

    /**
     * Draw a polygon, and fill it with current drawing color
     *
     * @param polygon the polygon object
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void fillPolygon(Polygon polygon) throws GlyphPaintingException;
    
    /**
     * Draw a sphere with given radius, and shade it with
     * current drawing color. <br>
     * Note that the sphere will be centered at (0,0,0) unless transformations
     * change its position.
     *
     * @param radius the radius of the sphere
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void fillShpere(double radius) throws GlyphPaintingException;

    /**
     * Draw a cylinder with a given center, height, a base and a top radius,
     * and shade it with current drawing color. <br>
     * Note that the cylinder will be centered at (0,0,0) unless transformations
     * change its position.
     *   
     * @param height
     * @param baseRadius
     * @param topRadius
     * @throws org.meta.shell.idebeans.viewers.impl.moleculeviewer.GlyphPaintingException
     */
    public void fillCylinder(double height, double baseRadius, double topRadius)
                             throws GlyphPaintingException;

    /**
     * Set additional property for this paint object
     *
     * @param propertyName the name of the property
     * @param propertyValue the value of this property
     */
    public void setAdditionalProperty(String propertyName, Object propertyValue);

    /**
     * Get an additional property associated with this paint object.
     *
     * @param propertyName the name of the property
     * @return the value of this property
     */
    public Object getAdditionalProperty(String propertyName);

    /**
     * Get all the properties associated with this paint object
     *
     * @return A hashmap of all the properties associated with this paint object
     */
    public HashMap<String, Object> getAllAdditionalProperties();
}
