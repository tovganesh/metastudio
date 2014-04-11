/*
 * MoleculeSceneChangeEvent.java
 *
 * Created on February 2, 2004, 6:27 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;

/**
 * Event fired when a molecule scne changes.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeSceneChangeEvent extends java.util.EventObject {
    
    /**
     * Holds value of property type.
     */
    private int type;
    
    /**
     * Fired when a general scene change event occured
     */
    public static final int GENERAL_SCENE_CHANGE = 1;
    
    /**
     * Fired when a molecule releted change occurs 
     */
    public static final int MOLECULE_CHANGE      = 2;
    
    /**
     * Fired when a property releted change occurs 
     */
    public static final int PROPERTY_CHANGE      = 3;
    
    /**
     * Fired when a property is added
     */
    public static final int PROPERTY_ADDED       = 4;
    
    /**
     * Fired when a property is removed
     */
    public static final int PROPERTY_REMOVED     = 5;
    
    
    /** Creates a new instance of MoleculeSceneChangeEvent */
    public MoleculeSceneChangeEvent(Object source) {
        super(source);
        
        type = GENERAL_SCENE_CHANGE;
        
        changedPropertyScene = null;
    }        
    
    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return this.type;
    }
    
    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Holds value of property changedPropertyScene.
     */
    private PropertyScene changedPropertyScene;

    /**
     * Getter for property changedPropertyScene.
     * @return Value of property changedPropertyScene.
     */
    public PropertyScene getChangedPropertyScene() {
        return this.changedPropertyScene;
    }

    /**
     * Setter for property changedPropertyScene.
     * @param changedPropertyScene New value of property changedPropertyScene.
     */
    public void setChangedPropertyScene(PropertyScene changedPropertyScene) {
        this.changedPropertyScene = changedPropertyScene;
    }
    
} // end of class MoleculeSceneChangeEvent
