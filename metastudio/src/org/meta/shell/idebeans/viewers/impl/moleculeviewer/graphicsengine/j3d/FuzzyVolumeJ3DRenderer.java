/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import javax.media.j3d.TransformGroup;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.FuzzyVolumeRenderer;

/**
 * Java 3D implementation of FuzzyVolumeRenderer
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FuzzyVolumeJ3DRenderer extends FuzzyVolumeRenderer 
                                    implements GlyphJ3D {
    
    public FuzzyVolumeJ3DRenderer(GridProperty gridProperty) {
        super(gridProperty);
    }
    
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    @Override
    public void buildJava3DScene(TransformGroup objRot) {
        if (objRot == null) return;
        
        // TODO:
    }
}
