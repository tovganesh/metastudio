/*
 * GridPropertyRendererChangeListener.java
 *
 * Created on July 20, 2005, 7:52 PM
 *
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

/**
 * The listener interface for change event of GridPropertyRenderer.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface GridPropertyRendererChangeListener 
                                     extends java.util.EventListener {
    
    /**
     * method called when event is fired.
     *
     * @param ce the change event related to GridPropertyRenderer
     */
    public void gridPropertyRendererChanged(GridPropertyRendererChangeEvent ce);
    
} // end of interface GridPropertyRendererChangeListener
