/*
 * IDEToolBar.java
 *
 * Created on July 20, 2003, 5:51 PM
 */

package org.meta.shell.idebeans;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;

/**
 * Handles all the toolbars for the IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEToolBar extends JToolBar {
    
    /** Holds a reference to the IDE object */
    private MeTA ideInstance;
    
    // the tool bars
    private JToolBar workspaceToolbar;    
    
    // the tool buttons - workspace related
    private JButton newWorkspaceButton;
    private JButton openWorkspaceButton;
    private JButton saveWorkspaceButton;        
    
    /** Creates a new instance of IDEToolBar */
    public IDEToolBar(MeTA ideInstance) {
        this.ideInstance = ideInstance;
        
        setRollover(true); // looks cool
        setFloatable(true);
        
        // make the tool bar and the associated event handlers
        makeToolBar();
    }
    
    /**
     * the method to setup the tool bar and the event handlers.
     */
    protected void makeToolBar() {
        ImageResource images   = ImageResource.getInstance();
        StringResource strings = StringResource.getInstance();
        
        // make the workspaceToolbar first
        workspaceToolbar = new JToolBar("workspaceToolbar");
        workspaceToolbar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        workspaceToolbar.setRollover(true);        
        
        newWorkspaceButton = new JButton(images.getNewWorkspace());
        newWorkspaceButton.setToolTipText(strings.getNewWorkspaceTip());
        newWorkspaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .newWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspaceToolbar.add(newWorkspaceButton);
        
        openWorkspaceButton = new JButton(images.getOpenWorkspace());
        openWorkspaceButton.setToolTipText(strings.getOpenWorkspaceTip());
        openWorkspaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .openWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspaceToolbar.add(openWorkspaceButton);
        
        saveWorkspaceButton = new JButton(images.getSaveWorkspace());
        saveWorkspaceButton.setToolTipText(strings.getSaveWorkspaceTip());        
        saveWorkspaceButton.setEnabled(false);
        saveWorkspaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .saveWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspaceToolbar.add(saveWorkspaceButton);                
        
        // add all the toolbars to the main toolbar
        add(workspaceToolbar);
    }
    
    /**
     * return the list of toolbars
     */
    public JToolBar[] getToolBarList() {
        JToolBar [] toolBars = new JToolBar[1];
        
        toolBars[0] = workspaceToolbar;        
        
        return toolBars;
    }
    
    /**
     * update the toolbar
     */
    public void updateToolBar() {
        // enable / disable menues according to various situations
        
        if (ideInstance.getCurrentWorkspace() != null) {
            saveWorkspaceButton.setEnabled(true);                        
        } else {
            saveWorkspaceButton.setEnabled(false);            
        } // end if                                
    }
} // end of class IDEToolBar
