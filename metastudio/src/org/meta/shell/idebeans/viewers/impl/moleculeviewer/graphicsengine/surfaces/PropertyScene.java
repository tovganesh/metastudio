/*
 * PropertyScene.java
 *
 * Created on July 18, 2005, 5:12 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces;

import org.meta.shell.idebeans.graphics.surfaces.GridPropertyRenderer;
import java.awt.*;

import org.meta.molecule.Molecule;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.graphics.AbstractScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.*;

/**
 * A scene graph for showing grid-based molecular properties.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PropertyScene extends AbstractScene
                           implements GridPropertyRendererChangeListener {
    
    protected MoleculeScene moleculeScene;
    protected GridProperty gridProperty;

    protected transient PropertySceneChangeEvent evt;
    
    /** Creates a new instance of PropertyScene */
    public PropertyScene(MoleculeScene moleculeScene, 
                         GridProperty gridProperty) {
        this.moleculeScene = moleculeScene;
        this.gridProperty  = gridProperty;
        
        this.type = PropertySceneType.CONTOUR; // draw contours by default
        this.gridPropertyRenderer = new ContourRenderer(gridProperty);
        this.gridPropertyRenderer.addGridPropertyRendererChangeListener(this);
        
        this.visible = true;
        this.name    = gridProperty.toString();
        
        this.evt = new PropertySceneChangeEvent(this);
    }

    /**
     * The property rendering is done from here!
     */
    @Override
    public void draw(Graphics2D g2d) {        
        if (gridPropertyRenderer == null || !visible) return;
        
        // pass it on pass it on!
        gridPropertyRenderer.draw(g2d);
    }        
    
    /**
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // pass it on pass it on!
        if (gridPropertyRenderer != null) {
            gridPropertyRenderer.setTransform(transform);
            gridPropertyRenderer.applyTransforms();
        } // end if
    }

    /** The type of property to be shown: iso-surface or contour */
    public enum PropertySceneType {
        CONTOUR { 
            @Override
            public String toString() { return "Countours"; }
        },
        TEXTURED_PLANAR_CUT { 
            @Override
            public String toString() { return "Textured plane cut"; }
        },
        TEXTURED_VDW_SURFACE { 
            @Override
            public String toString() { return "Textured Vdw surface"; }
        },
        ISO_SURFACE { 
            @Override
            public String toString() { return "Iso-surface"; }
        },
        OBLIQUE_CUT { 
            @Override
            public String toString() { return "Oblique plane cut"; }
        },
        FUZZY_VOLUME { 
            @Override
            public String toString() { return "Fuzzy volume"; }
        }
    } // end of enum PropertySceneType   

    /**
     * Holds value of property type.
     */
    protected PropertySceneType type;

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public PropertySceneType getType() {
        return this.type;
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(PropertySceneType type) {
        if (this.type.equals(type)) return;
        
        this.type = type;
        
        if (gridPropertyRenderer != null) {
            gridPropertyRenderer.removeGridPropertyRendererChangeListener(this);
        } // end if
        
        switch(type) {
            case CONTOUR:
                gridPropertyRenderer = new ContourRenderer(gridProperty);
                break;            
            case TEXTURED_PLANAR_CUT:
                gridPropertyRenderer = new TexturedPlaneRenderer(gridProperty);
                break;
            case TEXTURED_VDW_SURFACE:
                gridPropertyRenderer 
                  = new TexturedVdwSurfaceRenderer(gridProperty, moleculeScene);
                break;
            case ISO_SURFACE:
                gridPropertyRenderer = new IsoSurfaceRenderer(gridProperty);
                break;
            case OBLIQUE_CUT:
                gridPropertyRenderer = new TexturedObliquePlaneRenderer(gridProperty);
                break;
            case FUZZY_VOLUME:
                gridPropertyRenderer = new FuzzyVolumeRenderer(gridProperty);
                break;    
        } // end of switch .. case
        
        gridPropertyRenderer.addGridPropertyRendererChangeListener(this);
        
        firePropertySceneChangeListenerPropertySceneChanged(evt);
    }

    /**
     * Utility field used by event firing mechanism.
     */
    private javax.swing.event.EventListenerList listenerList =  null;

    /**
     * Registers PropertySceneChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addPropertySceneChangeListener(
                                        PropertySceneChangeListener listener) {

        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(PropertySceneChangeListener.class, listener);
    }

    /**
     * Removes PropertySceneChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removePropertySceneChangeListener(
                                        PropertySceneChangeListener listener) {

        listenerList.remove(PropertySceneChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event The event to be fired
     */
    protected void firePropertySceneChangeListenerPropertySceneChanged(
                                              PropertySceneChangeEvent event) {

        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i]==PropertySceneChangeListener.class) {
                ((PropertySceneChangeListener)listeners[i+1])
                                               .propertySceneChanged(event);
            }
        }
    }

    /**
     * Getter for property molecule.
     * @return Value of property molecule.
     */
    public Molecule getMolecule() {
        return this.moleculeScene.getMolecule();
    }

    /**
     * Getter for property gridProperty.
     * @return Value of property gridProperty.
     */
    public GridProperty getGridProperty() {
        return this.gridProperty;
    }

    /**
     * Holds value of property gridPropertyRenderer.
     */
    protected GridPropertyRenderer gridPropertyRenderer;

    /**
     * Getter for property gridPropertyRenderer.
     * @return Value of property gridPropertyRenderer.
     */
    public GridPropertyRenderer getGridPropertyRenderer() {
        return this.gridPropertyRenderer;
    }

    /**
     * Setter for property visible.
     * @param visible New value of property visible.
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        firePropertySceneChangeListenerPropertySceneChanged(evt);
    }

    /**
     * get the string conversion!
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Holds value of property name.
     */
    protected String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
        
        // just initiate a fire, so that we get a "refresh"
        firePropertySceneChangeListenerPropertySceneChanged(evt);
    }

    /**
     * Listener for GridProperty change.
     */
    @Override
    public void gridPropertyRendererChanged(
                                    GridPropertyRendererChangeEvent ce) {
        // just initiate a fire, so that we get a "refresh"
        firePropertySceneChangeListenerPropertySceneChanged(evt);
    }
    
    /**
     * Get the closest visible point from the currently displaying property.
     *
     * @param point the point object (probably from a mouse click)
     * @return PointProperty object, giving the nearest point and the value at
     *          that point
     * @throws PropertySceneChangeListener if the point object queried for
     *         doesn't lie near any of the visible grid points.
     */
    public PointProperty getClosestVisiblePoint(Point point) 
                            throws PropertyNotDefinedException {
        if (gridPropertyRenderer != null)
            return gridPropertyRenderer.getClosestVisiblePoint(point);
        
        throw new PropertyNotDefinedException();
    }

    /**
     * Get connected MoleculeScene instance
     *
     * @return the instance of MoleculeScene to which this scene is connected
     */
    public MoleculeScene getMoleculeScene() {
        return moleculeScene;
    }
    
    /**
     * overridden finalize() method
     */
    @Override
    public void finalize() throws Throwable {        
        super.finalize();
        
        gridPropertyRenderer.removeGridPropertyRendererChangeListener(this);
    }
} // end of class PropertyScene
