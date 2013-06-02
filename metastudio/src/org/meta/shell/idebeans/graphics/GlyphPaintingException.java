/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.graphics;

/**
 * An exception thrown at runtime if an error occurs during a paint event of
 * a Glyph; used extensively by PaintGlyphObject.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GlyphPaintingException extends RuntimeException {

    /** Create a new instance of GlyphPaintingException */
    public GlyphPaintingException(String msg) {
        super(msg);
    }
}
