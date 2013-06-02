/**
 * ObjectArrivalEvent.java
 *
 * Created on 03/11/2009
 */

package org.meta.net.impl.event;

import java.io.Serializable;

/**
 * The object arrival enven sent by object sync handler
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ObjectArrivalEvent extends java.util.EventObject {

    /** Creates a new instance of ObjectArrivalEvent */
    public ObjectArrivalEvent(Object source) {
        super(source);
    }

    protected Serializable object;

    /**
     * Get the value of object
     *
     * @return the value of object
     */
    public Serializable getObject() {
        return object;
    }

    /**
     * Set the value of object
     *
     * @param object new value of object
     */
    public void setObject(Serializable object) {
        this.object = object;
    }
}
