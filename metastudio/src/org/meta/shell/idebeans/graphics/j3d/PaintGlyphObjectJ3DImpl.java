/**
 * PaintGlyphObjectJ3DImpl.java
 *
 * Created on Jun 26, 2009
 */
package org.meta.shell.idebeans.graphics.j3d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import org.meta.math.geom.Point3D;
import org.meta.math.Vector3D;
import org.meta.shell.idebeans.graphics.GlyphPaintingException;
import org.meta.shell.idebeans.graphics.PaintGlyphObject;

/**
 * Java 3D implementation of PaintGlyphObject interface.
 * TODO: incomplete
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PaintGlyphObjectJ3DImpl implements PaintGlyphObject {

    public PaintGlyphObjectJ3DImpl() {
    }
    
    @Override
    public void setDrawingColor(Color color) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color getDrawingColor() throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTextFont(Font font) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Font getTextFont() throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawLine(Point3D a, Point3D b) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawVector(Vector3D vec) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawVector(Vector3D vec, Point3D basePoint)
                           throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawText(String text, Point3D pos)
                         throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawCircle(Point3D center, double radius)
                           throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawOval(Point3D startingPoint, double width, double height)
                         throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawRectangle(Rectangle2D rect) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawPolygon(Polygon polygon) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillCircle(Point3D center, double radius)
                           throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillOval(Point3D startingPoint, double height, double width)
                         throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillRectangle(Rectangle2D rect) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillPolygon(Polygon polygon) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillShpere(double radius) throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillCylinder(double height, double baseRadius, double topRadius)
                             throws GlyphPaintingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAdditionalProperty(String propertyName, Object propertyValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAdditionalProperty(String propertyName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HashMap<String, Object> getAllAdditionalProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
