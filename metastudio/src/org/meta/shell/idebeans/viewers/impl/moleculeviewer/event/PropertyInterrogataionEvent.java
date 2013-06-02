/*
 * PropertyInterrogataionEvent.java
 *
 * Created on October 12, 2007, 8:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

import org.meta.molecule.property.electronic.PointProperty;

/**
 * The property interrogation event. 
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PropertyInterrogataionEvent extends java.util.EventObject {
    
    /** Creates a new instance of PropertyInterrogataionEvent */
    public PropertyInterrogataionEvent(Object source) {
        super(source);
    }

    /**
     * Holds value of property pointProperty.
     */
    private PointProperty pointProperty;

    /**
     * Getter for property pointProperty.
     * @return Value of property pointProperty.
     */
    public PointProperty getPointProperty() {
        return this.pointProperty;
    }

    /**
     * Setter for property pointProperty.
     * @param pointProperty New value of property pointProperty.
     */
    public void setPointProperty(PointProperty pointProperty) {
        this.pointProperty = pointProperty;
    }
    
} // end of class PropertyInterrogataionEvent
