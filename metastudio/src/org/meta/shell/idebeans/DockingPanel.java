/*
 * DockingPanel.java 
 *
 * Created on 19 Oct, 2008 
 */

package org.meta.shell.idebeans;

import javax.swing.JPanel;

/**
 * A special panel that can dock!
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DockingPanel extends JPanel {

    /** Creates instance of DockingPanel */
    public DockingPanel() {
        
    }
    
    protected DockableFrame dockedFrame;

    /**
     * Get the value of dockedFrame
     *
     * @return the value of dockedFrame
     */
    public DockableFrame getDockedFrame() {
        return dockedFrame;
    }

    /**
     * Set the value of dockedFrame
     *
     * @param dockedFrame new value of dockedFrame
     */
    public void setDockedFrame(DockableFrame dockedFrame) {
        this.dockedFrame = dockedFrame;
    }    
}
