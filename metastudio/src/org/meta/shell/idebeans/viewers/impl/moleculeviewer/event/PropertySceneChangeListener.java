/*
 * PropertySceneChangeListener.java
 *
 * Created on July 20, 2005, 8:02 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

/**
 * Event listener interface for PropertySceneChangeEvent.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface PropertySceneChangeListener extends java.util.EventListener {
    
    /**
     * fired for a change event associated with PropertyScene
     *
     * @param ce the change event object
     */
    public void propertySceneChanged(PropertySceneChangeEvent ce);
} // end of interface PropertySceneChangeListener
