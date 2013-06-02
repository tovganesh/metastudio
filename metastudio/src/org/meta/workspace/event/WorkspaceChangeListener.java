/*
 * WorkspaceChangeListener.java
 *
 * Created on April 29, 2004, 6:39 AM
 */

package org.meta.workspace.event;

/**
 * For those who are interested in changes in a Workspace
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface WorkspaceChangeListener extends java.util.EventListener {
    
   /**
     * The method called when a workspace change occurs
     *
     * @param wce - the WorkspaceChangeEvent describing the event
     */
    public void workspaceChanged(WorkspaceChangeEvent wce);
    
} // end of interface WorkspaceChangeListener
