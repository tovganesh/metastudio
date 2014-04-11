/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.IsoSurfaceRenderer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.TexturedVdwSurfaceRenderer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import javax.media.j3d.TransformGroup;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.PropertySceneChangeEvent;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.PropertySceneChangeListener;

/**
 * Java3D implementaion of PropertyScene
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PropertyJ3DScene extends PropertyScene {
    
    private PropertyScene parentPropertyScene;
    
    public PropertyJ3DScene(PropertyScene ps) {
        this(ps.getMoleculeScene(), ps.getGridProperty());
        
        ps.addPropertySceneChangeListener(new PropertySceneChangeListener() {
            @Override
            public void propertySceneChanged(PropertySceneChangeEvent ce) {
                PropertyScene ps = (PropertyScene) ce.getSource();
                
                syncWithParent(ps);
            }
        });
        
        syncWithParent(ps);
        
        this.parentPropertyScene = ps;
    }
    
    public PropertyJ3DScene(MoleculeScene moleculeScene, 
                            GridProperty gridProperty) {
        super(moleculeScene, gridProperty);                
    }
    
    private void syncWithParent(PropertyScene ps) {
        setType(ps.getType());   
        
        getGridPropertyRenderer().setCurrentFunctionValue(
           ps.getGridPropertyRenderer().getCurrentFunctionValue()
        );

        getGridPropertyRenderer().setColorMap(
            ps.getGridPropertyRenderer().getColorMap()
        );

        getGridPropertyRenderer().setShowBoundingBox(
            ps.getGridPropertyRenderer().isShowBoundingBox()
        );

        getGridPropertyRenderer().setVisible(
            ps.getGridPropertyRenderer().isVisible()
        );       
        
        if (parentPropertyScene != null) {
            try {
                if (gridPropertyRenderer instanceof IsoSurfaceJ3DRenderer) {
                    ((IsoSurfaceJ3DRenderer) gridPropertyRenderer)
                        .setFillTransperency(((IsoSurfaceRenderer) 
                            parentPropertyScene.getGridPropertyRenderer())
                                .getFillTransperency());
                } else if (gridPropertyRenderer 
                           instanceof TexturedVdwSurfaceJ3DRenderer) {
                    ((TexturedVdwSurfaceJ3DRenderer) gridPropertyRenderer)
                            .setFillTransperency(((TexturedVdwSurfaceRenderer) 
                                parentPropertyScene.getGridPropertyRenderer())
                                    .getFillTransperency());
                } // end if 
            } catch (Exception ignored) {
                 ignored.printStackTrace();
            } // end try .. catch block
        } // end if
    }
    
    /**
     * Build the 3D scene on a Java3D enabled draw.
     *
     * @param objRot the transform object
     */
    public void buildJava3DScene(TransformGroup objRot) {
        if (gridPropertyRenderer == null || !visible) return;
        
        // pass it on pass it on!
        ((GlyphJ3D) gridPropertyRenderer).buildJava3DScene(objRot);        
    }
    
    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    @Override
    public void setType(PropertySceneType type) {        
        if (this.type.equals(type)) return;
        
        this.type = type;
        
        if (gridPropertyRenderer != null) {
            gridPropertyRenderer.removeGridPropertyRendererChangeListener(this);
        } // end if
        
        switch(type) {
            case CONTOUR:
                // TODO:
                // gridPropertyRenderer = new ContourRenderer(gridProperty);
                // break;            
            case TEXTURED_PLANAR_CUT:
                // TODO:
                // gridPropertyRenderer = new TexturedPlaneRenderer(gridProperty);
                // break;            
            case OBLIQUE_CUT:
                // TODO:
                // gridPropertyRenderer = new TexturedObliquePlaneRenderer(gridProperty);
                // break;
            case FUZZY_VOLUME:
                // TODO:
                // gridPropertyRenderer = new FuzzyVolumeRenderer(gridProperty);
                // break;   
            case ISO_SURFACE:
                gridPropertyRenderer = new IsoSurfaceJ3DRenderer(gridProperty);
                
                if (parentPropertyScene != null) {
                    try {
                        ((IsoSurfaceJ3DRenderer) gridPropertyRenderer)
                            .setFillTransperency(((IsoSurfaceRenderer) 
                                parentPropertyScene.getGridPropertyRenderer())
                                .getFillTransperency());
                    } catch (Exception ignored) {
                         ignored.printStackTrace();
                    } // end try .. catch block
                } // end if
                break;            
            case TEXTURED_VDW_SURFACE:
                gridPropertyRenderer 
                = new TexturedVdwSurfaceJ3DRenderer(gridProperty, moleculeScene);
                
                if (parentPropertyScene != null) {
                    try {
                        ((TexturedVdwSurfaceJ3DRenderer) gridPropertyRenderer)
                            .setFillTransperency(((TexturedVdwSurfaceRenderer) 
                                parentPropertyScene.getGridPropertyRenderer())
                                .getFillTransperency());
                    } catch (Exception ignored) {
                         ignored.printStackTrace();
                    } // end try .. catch block
                } // end if
                
                break;
        } // end of switch .. case
        
        gridPropertyRenderer.addGridPropertyRendererChangeListener(this);
        
        firePropertySceneChangeListenerPropertySceneChanged(evt);
    }
}
