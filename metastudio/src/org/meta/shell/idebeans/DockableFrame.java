/*
 * DockableFrame.java 
 *
 * Created on 12 Oct, 2008 
 */

package org.meta.shell.idebeans;

/**
 * Interface indicating that a Frame allows for docking a panel on its side.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface DockableFrame {

    /**
     * Dock a JPanel instance to this Frame.
     * 
     * @param title
     * @param dockPanel
     */
    public void dockIt(String title, DockingPanel dockPanel);
    
    /**
     * Undock a previously docked JPanel
     */
    public void unDock();
}
