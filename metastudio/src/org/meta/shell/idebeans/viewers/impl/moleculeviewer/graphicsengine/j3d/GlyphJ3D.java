/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.graphics.Glyph;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import javax.media.j3d.TransformGroup;

/**
 * A simple interface for Glyph s that use Java 3D
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface GlyphJ3D extends Glyph {
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    public void buildJava3DScene(TransformGroup objRot);
}
