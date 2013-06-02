/*
 * GridPropertyRenderer.java
 *
 * Created on July 19, 2005, 7:31 PM
 *
 */

package org.meta.shell.idebeans.graphics.surfaces;


import org.meta.shell.idebeans.graphics.AbstractGlyph;
import java.awt.Point;
import org.meta.common.ColorToFunctionRangeMap;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.*;
        
/**
 * Generic class for defining how a grid property renderer should behave.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class GridPropertyRenderer extends AbstractGlyph {  
    
    protected transient GridPropertyRendererChangeEvent evt;
    
    /** creates a new instance of GridPropertyRenderer */
    public GridPropertyRenderer() {
        showBoundingBox = true;        
        
        evt = new GridPropertyRendererChangeEvent(this);
        
        textureFunction = null;
    }        

    /**
     * Holds value of property gridProperty.
     */
    protected GridProperty gridProperty;

    /**
     * Getter for property gridProperty.
     * @return Value of property gridProperty.
     */
    public GridProperty getGridProperty() {
        return this.gridProperty;
    }

    /**
     * Setter for property gridProperty.
     * @param gridProperty New value of property gridProperty.
     */
    public void setGridProperty(GridProperty gridProperty) {
        this.gridProperty = gridProperty;
        
        if (gridProperty.getMinFunctionValue() > 0.0) {
            currentFunctionValue = gridProperty.getMinFunctionValue();
        } else if (gridProperty.getMaxFunctionValue() < 0.0) {
            currentFunctionValue = gridProperty.getMaxFunctionValue();
        } else {
            currentFunctionValue = 0.0;
        } // end if
    }

    /**
     * Holds value of property colorMap.
     */
    protected ColorToFunctionRangeMap colorMap;

    /**
     * Getter for property colorMap.
     * @return Value of property colorMap.
     */
    public ColorToFunctionRangeMap getColorMap() {
        return this.colorMap;
    }

    /**
     * Setter for property colorMap.
     * @param colorMap New value of property colorMap.
     */
    public void setColorMap(ColorToFunctionRangeMap colorMap) {
        this.colorMap = colorMap;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }

    /**
     * Holds value of property currentFunctionValue.
     */
    protected double currentFunctionValue;

    /**
     * Getter for property currentFunctionValue.
     * @return Value of property currentFunctionValue.
     */
    public double getCurrentFunctionValue() {
        return this.currentFunctionValue;
    }

    /**
     * Setter for property currentFunctionValue.
     * @param currentFunctionValue New value of property currentFunctionValue.
     */
    public void setCurrentFunctionValue(double currentFunctionValue) {
        if (currentFunctionValue < gridProperty.getMinFunctionValue()) {
            this.currentFunctionValue = gridProperty.getMinFunctionValue();
        } else if (currentFunctionValue > gridProperty.getMaxFunctionValue()) {
            this.currentFunctionValue = gridProperty.getMaxFunctionValue();
        } else {
            this.currentFunctionValue = currentFunctionValue;
        } // end if

        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }  

    /**
     * Utility field used by event firing mechanism.
     */
    private javax.swing.event.EventListenerList listenerList =  null;

    /**
     * Registers GridPropertyRendererChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addGridPropertyRendererChangeListener(
                                GridPropertyRendererChangeListener listener) {

        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(GridPropertyRendererChangeListener.class, listener);
    }

    /**
     * Removes GridPropertyRendererChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeGridPropertyRendererChangeListener(
                                  GridPropertyRendererChangeListener listener) {

        listenerList.remove(GridPropertyRendererChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event The event to be fired
     */
    protected 
       void fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(
                                        GridPropertyRendererChangeEvent event) {

        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i]==GridPropertyRendererChangeListener.class) {
                ((GridPropertyRendererChangeListener)listeners[i+1])
                                      .gridPropertyRendererChanged(event);
            }
        }
    }
    
    /**
     * method to apply scene transformations
     */
    @Override
    public abstract void applyTransforms();

    /**
     * Holds value of property showBoundingBox.
     */
    protected boolean showBoundingBox;
    
    /**
     * Getter for property showBoundingBox.
     * @return Value of property showBoundingBox.
     */
    public boolean isShowBoundingBox() {
        return this.showBoundingBox;
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
    public abstract PointProperty getClosestVisiblePoint(Point point) 
                            throws PropertyNotDefinedException;

    /**
     * Setter for property showBoundingBox.
     * @param showBoundingBox New value of property showBoundingBox.
     */
    public void setShowBoundingBox(boolean showBoundingBox) {
        this.showBoundingBox = showBoundingBox;
        
        fireGridPropertyRendererChangeListenerGridPropertyRendererChanged(evt);
    }

    protected TextureFunction textureFunction;

    /**
     * Get the value of textureFunction
     *
     * @return the value of textureFunction
     */
    public TextureFunction getTextureFunction() {
        return textureFunction;
    }

    /**
     * Set the value of textureFunction
     *
     * @param textureFunction new value of textureFunction
     */
    public void setTextureFunction(TextureFunction textureFunction) {
        this.textureFunction = textureFunction;
    }

} // end of class GridPropertyRenderer
