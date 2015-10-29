/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.graphics.j2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import org.meta.math.geom.Point3D;
import org.meta.math.Vector3D;
import org.meta.shell.idebeans.graphics.GlyphPaintingException;
import org.meta.shell.idebeans.graphics.PaintGlyphObject;

/**
 * Java 2D implementation of PaintGlyphObject interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PaintGlyphObjectJ2DImpl implements PaintGlyphObject {

    private Graphics2D graphics2d;

    /** Creates a new instance of Graphics2D */
    public PaintGlyphObjectJ2DImpl() {
        this.graphics2d = null;
    }

    /** 
     * Creates a new instance of Graphics2D
     * 
     * @param graphics2d the instance of graphics 2d context
     */
    public PaintGlyphObjectJ2DImpl(Graphics2D graphics2d) {
        this.graphics2d = graphics2d;
    }

    /**
     * Set the current drawing color for this paint object
     *
     * @param color the required color
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void setDrawingColor(Color color) throws GlyphPaintingException {
        graphics2d.setColor(color);
    }

    /**
     * Get the current drawing color for this paint object
     *
     * @return the current drawing color
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public Color getDrawingColor() throws GlyphPaintingException {
        return graphics2d.getColor();
    }

    /**
     * Set the current text font
     *
     * @param font the new text rendering font object
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void setTextFont(Font font) throws GlyphPaintingException {
        graphics2d.setFont(font);
    }

    /**
     * Get the current text font
     *
     * @return the current text rendering font
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public Font getTextFont() throws GlyphPaintingException {
        return graphics2d.getFont();
    }

    /**
     * Draw a line between two points a and b
     *
     * @param a the starting point of line
     * @param b the ending point of line
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawLine(Point3D a, Point3D b) throws GlyphPaintingException {
        graphics2d.drawLine((int) a.getX(), (int) a.getY(),
                            (int) b.getX(), (int) b.getY());
    }

    /**
     * Draw a Vector
     *
     * @param vec the vector object
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawVector(Vector3D vec) throws GlyphPaintingException {
        // TODO: 
    }

    /**
     * Draw a Vector with a specified base point
     *
     * @param vec the vector object
     * @param basePoint the base point from where the vector is to be drawn
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawVector(Vector3D vec, Point3D basePoint) throws GlyphPaintingException {
        // TODO:
    }

    /**
     * Draw a text object at a given position
     *
     * @param text the text the be drawn
     * @param pos the position of the text in space
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawText(String text, Point3D pos) throws GlyphPaintingException {
        graphics2d.drawString(text, (float) pos.getX(), (float) pos.getY());
    }

    /**
     * Draw a circle with a given center and radius
     *
     * @param center the center the circle
     * @param radius the radius of the this circle
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawCircle(Point3D center, double radius) throws GlyphPaintingException {        
        int presentRadius = (int) radius;

        graphics2d.drawOval((int) (center.getX()-presentRadius),
                            (int) (center.getY()-presentRadius),
                            2*presentRadius, 2*presentRadius);
    }

    /**
     * Draw an Oval
     *
     * @param startingPoint the starting point    
     * @param width the width of this Oval
     * @param height the height of this Oval
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawOval(Point3D startingPoint,
                         double width, double height) throws GlyphPaintingException {
        graphics2d.drawOval((int) startingPoint.getX(),
                            (int) startingPoint.getY(),
                            (int) width, (int) height);
    }

    /**
     * Draw a rectangle
     *
     * @param rect the Rectangle2D object to be drawn
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawRectangle(Rectangle2D rect) throws GlyphPaintingException {
        graphics2d.draw(rect);
    }

    /**
     * Draw a polygon
     *
     * @param polygon the polygon object
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void drawPolygon(Polygon polygon) throws GlyphPaintingException {
        graphics2d.draw(polygon);
    }

    /**
     * Draw a circle with a given center and radius,
     * and fill it with current drawing color
     *
     * @param center the center the circle
     * @param radius the radius of the this circle
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void fillCircle(Point3D center, double radius) throws GlyphPaintingException {
        int presentRadius = (int) radius;

        graphics2d.fillOval((int) (center.getX()-presentRadius),
                            (int) (center.getY()-presentRadius),
                            2*presentRadius, 2*presentRadius);
    }

    /**
     * Draw an Oval, and fill it with current drawing color
     *
     * @param startingPoint the starting point
     * @param height the height of this Oval
     * @param width the width of this Oval
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void fillOval(Point3D startingPoint,
                         double height, double width) throws GlyphPaintingException {
         graphics2d.fillOval((int) startingPoint.getX(),
                            (int) startingPoint.getY(),
                            (int) width, (int) height);
    }

    /**
     * Draw a rectangle, and fill it with current drawing color
     *
     * @param rect the Rectangle2D object to be drawn
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void fillRectangle(Rectangle2D rect) throws GlyphPaintingException {
        graphics2d.fill(rect);
    }

    /**
     * Draw a polygon, and fill it with current drawing color
     *
     * @param polygon the polygon object
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void fillPolygon(Polygon polygon) throws GlyphPaintingException {
        graphics2d.fill(polygon);
    }

    /**
     * Draw a sphere with given radius, and shade it with
     * current drawing color. <br>
     * Note that the sphere will be centered at (0,0,0) unless transformations
     * change its position.
     *
     * @param radius the radius of the sphere
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void fillShpere(double radius) throws GlyphPaintingException {
        // TODO:
    }

    /**
     * Draw a cylinder with a given center, height, a base and a top radius,
     * and shade it with current drawing color. <br>
     * Note that the cylinder will be centered at (0,0,0) unless transformations
     * change its position.
     *
     * @param height
     * @param baseRadius
     * @param topRadius
     * @throws org.meta.shell.idebeans.graphics.GlyphPaintingException
     */
    @Override
    public void fillCylinder(double height, double baseRadius, double topRadius)
                              throws GlyphPaintingException {
        // TODO:
    }

    protected HashMap<String, Object> additonalProperties;

    /**
     * Set additional property for this paint object
     *
     * @param propertyName the name of the property
     * @param propertyValue the value of this property
     */
    @Override
    public void setAdditionalProperty(String propertyName, Object propertyValue) {
        if (additonalProperties == null)
            additonalProperties = new HashMap<>();

        additonalProperties.put(propertyName, propertyValue);

        if (propertyName.equals("_graphics2d"))
            graphics2d = (Graphics2D) propertyValue;

        if (propertyName.equals("_smoothDrawing")) {
            if (graphics2d != null) {
                if (propertyValue.equals(Boolean.TRUE))
                    graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON);
                else
                    graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_OFF);
            } // end if
        } // end if
        
        if (propertyName.equals("_stroke")) {
            if (graphics2d != null) {
                graphics2d.setStroke((Stroke) propertyValue);
            } // end if
        } // end if
    }

    /**
     * Get an additional property associated with this paint object.
     *
     * @param propertyName the name of the property
     * @return the value of this property
     */
    @Override
    public Object getAdditionalProperty(String propertyName) {
        if (additonalProperties == null) { 
            if (propertyName.equals("_stroke")) {
                if (graphics2d != null) return graphics2d.getStroke();
            } // end if
            
            return null;
        } // end if
        
        return additonalProperties.get(propertyName);
    }

    /**
     * Get all the properties associated with this paint object
     *
     * @return A HashMap of all the properties associated with this paint object
     */
    @Override
    public HashMap<String, Object> getAllAdditionalProperties() {
        return additonalProperties;
    }
}
