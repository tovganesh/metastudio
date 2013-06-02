/*
 * WorkspaceItemChangeListener.java
 *
 * Created on April 29, 2004, 6:26 AM
 */

package org.meta.workspace.event;

/**
 * For those who are interested in changes in WorkspaceItem s
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface WorkspaceItemChangeListener extends java.util.EventListener {
    
    /**
     * The method called when a workspace item change occurs
     *
     * @param wice - the WorkspaceItemChangeEvent describing the event
     */
    public void workspaceItemChanged(WorkspaceItemChangeEvent wice);
    
} // end of interface WorkspaceItemChangeListener
