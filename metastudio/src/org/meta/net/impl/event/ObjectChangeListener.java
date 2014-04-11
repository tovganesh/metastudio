/**
 * ObjectChangeListener.java
 *
 * Created on 03/11/2009
 */

package org.meta.net.impl.event;

/**
 * Used by object sync request to notify of changes to the object
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface ObjectChangeListener extends java.util.EventListener {

    /**
     * fired when an object change occurs
     * 
     * @param oce the instance of ObjectChange event
     */
    public void objectChanged(ObjectChangeEvent oce);
}
