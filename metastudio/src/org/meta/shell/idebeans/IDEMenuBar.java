/*
 * IDEMenuBar.java
 *
 * Created on July 18, 2003, 8:11 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;

import org.meta.config.impl.RecentFilesConfiguration;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;

/**
 * The main menu bar for the IDE and all the handlers.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEMenuBar extends JMenuBar {
    
    /** Holds a reference to the IDE object */
    private MeTA ideInstance;
    
    // the menues
    // file menu
    private JMenu fileMenu;       
    private JMenuItem newWorkspaceMenuItem, openWorkspaceMenuItem,
                      openMoleculeMenuItem;
    private JMenuItem saveWorkspaceMenuItem, saveWorkspaceAsMenuItem;
    private JMenu rescentWorkspaceMenu;
    private JMenuItem closeWorkspaceMenuItem, closeWindowMenuItem;
    private JMenuItem exitMenuItem; 
    
    // view menu
    private JMenu viewMenu;
    private JMenu toolBar;
    private JCheckBoxMenuItem statusBar;   
    
    // tools menu
    private JMenu toolsMenu;
    private JMenu toolsMenuScripting, toolsMenuOptions;
    private JMenuItem openBeanShell, atomInfoSettings;
    
    // help menu
    private JMenu helpMenu;
    private JMenuItem contentMenuItem;
    private JMenuItem aboutMenuItem;    
    
    /** Creates a new instance of IDEMenuBar */
    public IDEMenuBar(MeTA ideInstance) {
        this.ideInstance = ideInstance;
        
        // make the menu bar and the associated event handlers
        makeMenuBar();
    }
    
    /**
     * the method to setup the menu bar and the event handlers.
     */
    protected void makeMenuBar() {
        ImageResource images   = ImageResource.getInstance();
        StringResource strings = StringResource.getInstance();
        
        fileMenu = new JMenu();
            newWorkspaceMenuItem    = new JMenuItem(images.getNewWorkspace());
            openWorkspaceMenuItem   = new JMenuItem(images.getOpenWorkspace());
            openMoleculeMenuItem    = new JMenuItem();
            saveWorkspaceMenuItem   = new JMenuItem(images.getSaveWorkspace());
            saveWorkspaceAsMenuItem = new JMenuItem();
            rescentWorkspaceMenu    = new JMenu();
            closeWorkspaceMenuItem  = new JMenuItem();
            closeWindowMenuItem     = new JMenuItem();
            exitMenuItem            = new JMenuItem();
        viewMenu  = new JMenu();
            toolBar   = new JMenu();
            statusBar = new JCheckBoxMenuItem("Status Bar" , true);            
        toolsMenu = new JMenu();
            toolsMenuScripting   = new JMenu();
                openBeanShell    = new JMenuItem();
            toolsMenuOptions     = new JMenu();
                atomInfoSettings = new JMenuItem();                
        helpMenu = new JMenu();
            contentMenuItem = new JMenuItem();
            aboutMenuItem   = new JMenuItem(images.getAbout());

        // the menu font, properties and the event handlers
        Font menuFont = FontResource.getInstance().getMenuFont();
        
        setFont(menuFont);
        
        // file menu
        fileMenu.setText("File");
        fileMenu.setMnemonic('F');
        fileMenu.setFont(menuFont);
        newWorkspaceMenuItem.setFont(menuFont);
        newWorkspaceMenuItem.setText("New Workspace...");
        newWorkspaceMenuItem.setMnemonic('N');
        newWorkspaceMenuItem.setToolTipText(strings.getNewWorkspaceTip());
        newWorkspaceMenuItem.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        newWorkspaceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .newWorkspaceMenuItemActionPerformed(evt);                
            }
        });
        fileMenu.add(newWorkspaceMenuItem);        
        
        openWorkspaceMenuItem.setFont(menuFont);
        openWorkspaceMenuItem.setText("Open Workspace...");
        openWorkspaceMenuItem.setMnemonic('O');
        openWorkspaceMenuItem.setToolTipText(strings.getOpenWorkspaceTip());
        openWorkspaceMenuItem.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        openWorkspaceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .openWorkspaceMenuItemActionPerformed(evt);                
            }
        });
        fileMenu.add(openWorkspaceMenuItem);
        
        openMoleculeMenuItem.setFont(menuFont);
        openMoleculeMenuItem.setText("Open Molecule File...");
        openMoleculeMenuItem.setMnemonic('M');
        openMoleculeMenuItem.setToolTipText("Opens a molecule file");
        openMoleculeMenuItem.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_MASK));
        openMoleculeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .openMoleculeTaskActionPerformed(evt);                
            }
        });
        fileMenu.add(openMoleculeMenuItem);
        fileMenu.addSeparator();
        
        saveWorkspaceMenuItem.setFont(menuFont);
        saveWorkspaceMenuItem.setText("Save Workspace");
        saveWorkspaceMenuItem.setMnemonic('S');
        saveWorkspaceMenuItem.setToolTipText(strings.getSaveWorkspaceTip());
        saveWorkspaceMenuItem.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        saveWorkspaceMenuItem.setEnabled(false);
        saveWorkspaceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .saveWorkspaceMenuItemActionPerformed(evt);                
            }
        });
        fileMenu.add(saveWorkspaceMenuItem);
        
        saveWorkspaceAsMenuItem.setFont(menuFont);
        saveWorkspaceAsMenuItem.setText("Save As Workspace...");
        saveWorkspaceAsMenuItem.setMnemonic('A');               
        saveWorkspaceAsMenuItem.setToolTipText(strings.getSaveAsWorkspaceTip());
        saveWorkspaceAsMenuItem.setEnabled(false);
        fileMenu.add(saveWorkspaceAsMenuItem);
        fileMenu.addSeparator();
        
        rescentWorkspaceMenu.setFont(menuFont);
        rescentWorkspaceMenu.setText("Rescent Workspace");
        rescentWorkspaceMenu.setMnemonic('R');
        makeRecentMenu();
        fileMenu.add(rescentWorkspaceMenu);
        fileMenu.addSeparator();
        
        closeWorkspaceMenuItem.setFont(menuFont);
        closeWorkspaceMenuItem.setText("Close Workspace");
        closeWorkspaceMenuItem.setMnemonic('C');
        closeWorkspaceMenuItem.setEnabled(false);
        closeWorkspaceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                    .closeWorkspaceMenuItemActionPerformed(evt);                
            }
        });
        fileMenu.add(closeWorkspaceMenuItem);
        
        closeWindowMenuItem.setFont(menuFont);
        closeWindowMenuItem.setText("Close Window");
        closeWindowMenuItem.setMnemonic('W');         
        closeWindowMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ideInstance.getWorkspaceDesktop().closeActiveFrame();
            }
        });
        fileMenu.add(closeWindowMenuItem);   
        fileMenu.addSeparator();
        
        exitMenuItem.setFont(menuFont);
        exitMenuItem.setText("Exit");
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setToolTipText(strings.getExitTip());
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);                                    
        add(fileMenu);
        
        // view menu
        viewMenu.setText("View");
        viewMenu.setMnemonic('V');
        viewMenu.setFont(menuFont);
//        toolBar.setText("Toolbars");
//        toolBar.setMnemonic('T');
//        toolBar.setFont(menuFont);
//        // remember this is crutial, we have to be damn sure that the toolbar
//        // is already initilized otherwise this is completely wrong!
//        JToolBar [] toolBars = ideInstance.getIDEToolBar().getToolBarList();
//        for(int i=0; i<toolBars.length; i++) {
//            JCheckBoxMenuItem theTool 
//              = new JCheckBoxMenuItem(toolBars[i].getName(), true);
//            
//            theTool.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent evt) {
//                    // get the tool having this name
//                    JToolBar [] toolBars = 
//                             ideInstance.getIDEToolBar().getToolBarList();
//                    JCheckBoxMenuItem theMenu = 
//                             ((JCheckBoxMenuItem) evt.getSource());
//                    String toolName = theMenu.getText();
//                    
//                    for(int i=0; i<toolBars.length; i++) {
//                        if (toolBars[i].getName().equals(toolName)) {
//                            toolBars[i].setVisible(theMenu.isSelected());
//                            return; // done
//                        } // end if
//                    } // end for
//                }
//            });
//            
//            theTool.setFont(menuFont);
//            toolBar.add(theTool);
//        } // end for                
//        viewMenu.add(toolBar);
        statusBar.setMnemonic('S');
        statusBar.setFont(menuFont);
        statusBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ideInstance.getStatusBar().setVisible(statusBar.isSelected());
            }
        });        
        viewMenu.add(statusBar);
        add(viewMenu);
        
        // tools menu
        toolsMenu.setText("Tools");
        toolsMenu.setMnemonic('T');
        toolsMenu.setFont(menuFont);
        toolsMenuScripting.setText("Scripting");
        toolsMenuScripting.setMnemonic('S');
        toolsMenuScripting.setFont(menuFont);
        openBeanShell.setText("Open BeanShell");
        openBeanShell.setMnemonic('B');
        openBeanShell.setFont(menuFont);
        openBeanShell.setToolTipText(strings.getOpenBeanShellTip());
        openBeanShell.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .openBeanShellActionPerformed(evt);
            }
        });
        toolsMenuScripting.add(openBeanShell);
        toolsMenu.add(toolsMenuScripting);    
        
        toolsMenuOptions.setText("Options");
        toolsMenuOptions.setMnemonic('O');
        toolsMenuOptions.setFont(menuFont);
        atomInfoSettings.setText("AtomInfo Settings...");
        atomInfoSettings.setMnemonic('I');
        atomInfoSettings.setFont(menuFont);
        atomInfoSettings.setToolTipText(strings.getAtomInfoSettingsTip());
        atomInfoSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .openAtomInfoSettings(evt);
            }
        });
        toolsMenuOptions.add(atomInfoSettings);
        toolsMenu.add(toolsMenuOptions);
        add(toolsMenu);
        
        // help menu
        helpMenu.setText("Help");
        helpMenu.setMnemonic('H');
        helpMenu.setFont(menuFont);
        contentMenuItem.setFont(menuFont);        
        contentMenuItem.setText("Contents");
        contentMenuItem.setMnemonic('C');
        contentMenuItem.setAccelerator(
                           KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        contentMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .contentsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(contentMenuItem);
        helpMenu.addSeparator();
        aboutMenuItem.setFont(menuFont);
        aboutMenuItem.setText("About...");
        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);
        add(helpMenu);
    }        
    
    /**
     * update the menues
     */
    public void updateMenues() {
        // enable / disable menues according to various situations
        
        if (ideInstance.getCurrentWorkspace() != null) {
            saveWorkspaceMenuItem.setEnabled(true);
            saveWorkspaceAsMenuItem.setEnabled(true);            
            closeWorkspaceMenuItem.setEnabled(true);
        } else {
            saveWorkspaceMenuItem.setEnabled(false);
            saveWorkspaceAsMenuItem.setEnabled(false);            
            closeWorkspaceMenuItem.setEnabled(false);
        } // end if                
        
        makeRecentMenu();
    }
    
    /**
     * make up the recent workspace menu
     */
    private void makeRecentMenu() {
        RecentFilesConfiguration rfc = RecentFilesConfiguration.getInstance();
        StringResource strings = StringResource.getInstance();
        
        Iterator fileList = rfc.getFileList();
        String filePath;
        int number = 1;
        
        // remove all the items
        rescentWorkspaceMenu.removeAll();
        
        while(fileList.hasNext()) {
            filePath = (String) fileList.next();
            
            // is it a proper workspace extension?
            if (filePath.endsWith(strings.getWorkspaceFileExtension())) {
                // if yes we add it to our menu
                JMenuItem fileItem = new JMenuItem(number + " " + filePath);
                
                fileItem.setName(filePath);
                fileItem.setMnemonic((new String(number + "").charAt(0)));
                fileItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        MainMenuEventHandlers.getInstance(ideInstance)
                         .openWorkspace(((JMenuItem)evt.getSource()).getName(),
                                        false);
                    }
                });
                rescentWorkspaceMenu.add(fileItem);
                
                number++;
            } // end if
        } // end while
    }
    
} // end of class IDEMenuBar
