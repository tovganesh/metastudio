/**
 * ObjectChangeEvent.java
 *
 * Created on 03/11/2009
 */

package org.meta.net.impl.event;

import java.io.Serializable;

/**
 * Object change even sent by object sysc handler
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ObjectChangeEvent extends java.util.EventObject {

    /** Creates a new instance of ObjectChangeEvent */
    public ObjectChangeEvent(Object source) {
        super(source);
    }

    protected Serializable targetObject;

    /**
     * Get the value of targetObject
     *
     * @return the value of targetObject
     */
    public Serializable getTargetObject() {
        return targetObject;
    }

    /**
     * Set the value of targetObject
     *
     * @param targetObject new value of targetObject
     */
    public void setTargetObject(Serializable targetObject) {
        this.targetObject = targetObject;
    }

    protected String propertyName;

    /**
     * Get the value of propertyName
     *
     * @return the value of propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Set the value of propertyName
     *
     * @param propertyName new value of propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

}
