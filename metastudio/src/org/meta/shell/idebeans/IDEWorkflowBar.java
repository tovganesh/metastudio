/*
 * IDEWorkflowBar.java
 *
 * Created on February 26, 2006, 10:59 AM
 *
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import org.meta.common.Utility;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;

import org.meta.shell.ide.MeTA;
import org.meta.config.impl.RecentFilesConfiguration;
import org.meta.shell.ide.MeTAUpdateManager;
import org.meta.shell.idebeans.appbuilder.IDEAppBuilder;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;
import org.meta.shell.idebeans.propertysheet.IDEPropertySheetUI;
import org.meta.shell.idebeans.talk.IntegratedIDETalkUI;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewerPropertyWrapper;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo.SelectionUndoableEdit;
import org.meta.shell.idebeans.workbook.IDEWorkbook;

/**
 * A new Windows Vista<sup>TM</sup> inspired workflow UI for MeTA Studio,  
 * allowing lesser screen clutter, and clarity in workflow within the IDE.
 *
 * Currently it is implemented as an extension of a standard tool bar, and uses
 * the existing Task APIs within the IDE.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEWorkflowBar extends JToolBar 
                            implements UndoableEditListener {
    
    /** Holds a reference to the IDE object */
    private MeTA ideInstance;
    
    /** The place where a menu, toolbar and task pane are amalgamated. */
    private JTabbedPane taskPane;
    
    private JButton newWorkspaceButton, openWorkspaceButton, 
                    saveWorkspaceButton, extraOptions;
    private JPopupMenu extraOptionsPopup, extraShellPopup;
    private JMenuItem closeWorkSpace, exitStudio;
    private JMenu recentWorkspace, recentFiles;
    private JToggleButton extraMolOptions;
    
    private JPanel extraMoleculeWorkflow;
    
    /** Creates a new instance of IDEWorkflowBar */
    public IDEWorkflowBar(MeTA ideInstance) {
        this.ideInstance = ideInstance;
        
        setRollover(true); // looks cool
        setFloatable(true);
        
        taskPane = new JTabbedPane(JTabbedPane.TOP, 
                                   JTabbedPane.SCROLL_TAB_LAYOUT);        
        taskPane.setBorder(BorderFactory.createEmptyBorder());        
        
        add(taskPane);
        
        initWorkflowPanels();
    }
    
    
    /**
     * Add a workflow panel to this bar.
     *
     * @param panelName the name of the panel
     * @param icon icon if any
     * @param panel the instance of JPanel containing workflow UI elements
     * @param tip general information about this workflow panel
     */
    public void addWorkflowPanel(String panelName, Icon icon, JPanel panel, 
                                 String tip, int mnemonic) {
        panel.setBorder(BorderFactory.createEmptyBorder());
        taskPane.addTab(panelName, icon, panel, tip);
        taskPane.setMnemonicAt(taskPane.getTabCount()-1, mnemonic);
    } 
    
    /**
     * Removes a particular workflow panel.
     *
     * @param indexOfPanel the index of the workflow panel to be removed.
     */
    public void removeWorkflowPanel(int indexOfPanel) {
        taskPane.removeTabAt(indexOfPanel);
    }
    
    /** some special button whose state may be dinamic */
    private JButton saveGeometryButton, saveImageButton, exportToRIButton,
                    attachPropertyButton;
    
    
    /**
     * sets the visiblity of some of the dinamic button in molecule workflow.
     *
     * @param visible true / false
     */
    public void setMoleculeWorkflowDinamicVisibility(boolean visible) {
        saveGeometryButton.setVisible(visible);
        saveImageButton.setVisible(visible);
        exportToRIButton.setVisible(visible);
        attachPropertyButton.setVisible(visible);
        extraMolOptions.setVisible(visible);
        
        if (extraMolOptions.isSelected()) {
            extraMoleculeWorkflow.setVisible(false);
            extraMolOptions.doClick();
        } // end if
    }

    /**
     * sets the visiblity of some of the dinamic button in molecule workflow
     * related to the molecule editor.
     *
     * @param visible true / false
     */
    public void setMoleculeEditorWorkflowDinamicVisibility(boolean visible) {
        saveGeometryButton.setVisible(visible);
        saveImageButton.setVisible(visible);
        exportToRIButton.setVisible(visible);
        attachPropertyButton.setVisible(false);
        extraMolOptions.setVisible(false);
    }

    /**
     * initialize the standard workflow panels
     */
    private void initWorkflowPanels() {
        final ImageResource images   = ImageResource.getInstance();
        StringResource strings = StringResource.getInstance();
        
        // File workflow
        JPanel fileWorkflow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        fileWorkflow.setBorder(BorderFactory.createEmptyBorder());        
        
        newWorkspaceButton = new JButton("New workspace", 
                                         images.getNewWorkspace());
        newWorkspaceButton.setBorderPainted(false);
        newWorkspaceButton.setRolloverEnabled(true);
        newWorkspaceButton.setToolTipText(strings.getNewWorkspaceTip());
        newWorkspaceButton.setMnemonic('N');
        newWorkspaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .newWorkspaceMenuItemActionPerformed(evt);
                
                if (ideInstance.getCurrentWorkspace() != null) {
                    closeWorkSpace.setEnabled(true);
                } // end if
            }
        });
        fileWorkflow.add(newWorkspaceButton);
        
        openWorkspaceButton = new JButton("Open workspace", 
                                          images.getOpenWorkspace());
        openWorkspaceButton.setBorderPainted(false);
        openWorkspaceButton.setRolloverEnabled(true);
        openWorkspaceButton.setToolTipText(strings.getOpenWorkspaceTip());
        openWorkspaceButton.setMnemonic('O');
        openWorkspaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .openWorkspaceMenuItemActionPerformed(evt);
            }
        });
        fileWorkflow.add(openWorkspaceButton);                
        
        saveWorkspaceButton = new JButton("Save workspace",
                                          images.getSaveWorkspace());
        saveWorkspaceButton.setBorderPainted(false);
        saveWorkspaceButton.setRolloverEnabled(true);
        saveWorkspaceButton.setToolTipText(strings.getSaveWorkspaceTip()); 
        saveWorkspaceButton.setMnemonic('S');
        saveWorkspaceButton.setEnabled(false);
        saveWorkspaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .saveWorkspaceMenuItemActionPerformed(evt);
            }
        });
        fileWorkflow.add(saveWorkspaceButton);
        
        makeExtraOptionsPopup();
        extraOptions = new JButton("More options", images.getExpand());
        extraOptions.setHorizontalTextPosition(SwingConstants.LEFT);
        extraOptions.setBorderPainted(false);
        extraOptions.setRolloverEnabled(true);
        extraOptions.setToolTipText("Click here for more options");
        extraOptions.setMnemonic('p');
        extraOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                extraOptionsPopup.show(extraOptions, 
                                       0, extraOptions.getHeight());
            }
        });
        fileWorkflow.add(extraOptions);
        
        addWorkflowPanel("File", null, fileWorkflow, "File workflow", 
                         KeyEvent.VK_F);
        
        // Molecule workflow
        JPanel topLevelMoleculePanel = new JPanel(new GridBagLayout());
        JPanel moleculeWorkflow = new JPanel(new FlowLayout(FlowLayout.LEFT, 
                                                            0, 5));
        extraMoleculeWorkflow = new JPanel(new GridBagLayout());
        
        moleculeWorkflow.setBorder(BorderFactory.createEmptyBorder());
        JButton openMoleculeButton, newMoleculeButton;

        newMoleculeButton = new JButton("New molecule",
                                         images.getMoleculeEditor());
        newMoleculeButton.setBorderPainted(false);
        newMoleculeButton.setRolloverEnabled(true);
        newMoleculeButton.setToolTipText(strings.getNewMoleculeTip());
        newMoleculeButton.setMnemonic('N');
        newMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .newMoleculeTaskActionPerformed(evt);
            }
        });
        moleculeWorkflow.add(newMoleculeButton);

        openMoleculeButton = new JButton("Open molecule", 
                                         images.getMoleculeViewer());
        openMoleculeButton.setBorderPainted(false);
        openMoleculeButton.setRolloverEnabled(true);
        openMoleculeButton.setToolTipText(strings.getOpenMoleculeTip());
        openMoleculeButton.setMnemonic('O');
        openMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .openMoleculeTaskActionPerformed(evt);
            }
        });
        moleculeWorkflow.add(openMoleculeButton);
        
        saveGeometryButton = new JButton("Save geometry", 
                                         images.getSaveWorkspace());
        saveGeometryButton.setBorderPainted(false);
        saveGeometryButton.setRolloverEnabled(true);
        saveGeometryButton.setToolTipText(strings.getSaveMoleculeTip());
        saveGeometryButton.setMnemonic('g');
        saveGeometryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {   
                MainMenuEventHandlers.getInstance(ideInstance)
                                            .saveMoleculeActionPerformed(evt);
            }
        });
        saveGeometryButton.setVisible(false);
        moleculeWorkflow.add(saveGeometryButton);        
        
        saveImageButton = new JButton("Save image", images.getSaveWorkspace());
        saveImageButton.setBorderPainted(false);
        saveImageButton.setRolloverEnabled(true);
        saveImageButton.setToolTipText(strings.getSaveMoleculeImageTip());
        saveImageButton.setMnemonic('i');
        saveImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {   
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .saveMoleculeImageActionPerformed(evt);
            }
        });
        saveImageButton.setVisible(false);
        moleculeWorkflow.add(saveImageButton);        
        
        exportToRIButton = new JButton("Export RIB", images.getRender());
        exportToRIButton.setBorderPainted(false);
        exportToRIButton.setRolloverEnabled(true);
        exportToRIButton.setToolTipText(strings.getExportRIBTip());
        exportToRIButton.setMnemonic('R');
        exportToRIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {   
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .exportToRIBActionPerformed(evt);
            }
        });
        exportToRIButton.setVisible(false);
        moleculeWorkflow.add(exportToRIButton);        
        
        attachPropertyButton = new JButton("Attach property",
                                           images.getSurface());
        attachPropertyButton.setBorderPainted(false);
        attachPropertyButton.setRolloverEnabled(true);
        attachPropertyButton.setToolTipText(strings.getAttachPropertyTip());
        attachPropertyButton.setMnemonic('A');
        attachPropertyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {   
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .attachPropertyActionPerformed(evt);
            }
        });
        attachPropertyButton.setVisible(false);
        moleculeWorkflow.add(attachPropertyButton); 
        
        extraMolOptions = new JToggleButton("More options", 
                                            images.getExpand());
        extraMolOptions.setHorizontalTextPosition(SwingConstants.LEFT);
        extraMolOptions.setBorderPainted(false);
        extraMolOptions.setRolloverEnabled(true);
        extraMolOptions.setToolTipText("Click here for more/less options");
        extraMolOptions.setMnemonic('p');
        extraMolOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                extraMoleculeWorkflow.setVisible(extraMolOptions.isSelected());
                if (extraMolOptions.isSelected()) {
                    extraMolOptions.setText("Hide options");
                    extraMolOptions.setIcon(images.getShrink());
                    makeExtraOptions(extraMoleculeWorkflow);
                } else {
                    extraMolOptions.setText("More options");
                    extraMolOptions.setIcon(images.getExpand());
                } // end if
            }
        });
        extraMolOptions.setVisible(false);
        extraMolOptions.setSelected(false);
        moleculeWorkflow.add(extraMolOptions);
        
        extraMoleculeWorkflow.setVisible(false);
       
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.fill  = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        
        // TODO: the following is not really a correct way to do things!
        gbc.insets = new Insets(0, 0, 0, 5000);
        topLevelMoleculePanel.add(moleculeWorkflow, gbc);
        gbc.gridy = 1; 
        topLevelMoleculePanel.add(extraMoleculeWorkflow, gbc);
        addWorkflowPanel("Molecule", null, topLevelMoleculePanel, 
                         "Molecule workflow", KeyEvent.VK_M);
        
        // View workflow
        JPanel viewWorkflow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        viewWorkflow.setBorder(BorderFactory.createEmptyBorder());
        JCheckBox showStatusBar, showExplorerWindow, showLogWindow;
        
        showStatusBar = new JCheckBox("Show status bar");
        showStatusBar.setMnemonic('b');
        showStatusBar.setSelected(true);
        showStatusBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {   
                ideInstance.getStatusBar().setVisible(
                        ((JCheckBox) evt.getSource()).isSelected());
            }
        });        
        viewWorkflow.add(showStatusBar);
        
        showExplorerWindow = new JCheckBox("Show explorer window");
        showExplorerWindow.setMnemonic('e');
        showExplorerWindow.setSelected(false);
        showExplorerWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {   
                ideInstance.setExplorerPaneVisibility(
                        ((JCheckBox) evt.getSource()).isSelected());
            }
        });        
        viewWorkflow.add(showExplorerWindow);
        
        showLogWindow = new JCheckBox("Show log window");
        showLogWindow.setMnemonic('l');
        showLogWindow.setSelected(false);
        showLogWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {   
                ideInstance.setLogPaneVisibility(
                        ((JCheckBox) evt.getSource()).isSelected());
            }
        });        
        viewWorkflow.add(showLogWindow);
        
        addWorkflowPanel("View", null, viewWorkflow, 
                         "View workflow", KeyEvent.VK_V);

        // Workbook workflow
        JPanel workbookWorkFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        JButton openNewWorkBook;

        openNewWorkBook = new JButton("New workbook",
                                      images.getNewWorkspace());
        openNewWorkBook.setToolTipText("Opens up a workbook (" +
                "CAUTION: This is experimetal feature!)");
        openNewWorkBook.setBorderPainted(false);
        openNewWorkBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JInternalFrame jif = new JInternalFrame("Workbook",
                                                        true, true, true, true);

                jif.setLayout(new BorderLayout());
                jif.add(new IDEWorkbook(false)); // use work book interpreter
                ideInstance.getWorkspaceDesktop().addInternalFrame(jif);
                jif.setVisible(true);
            }
        });
        workbookWorkFlow.add(openNewWorkBook);
        addWorkflowPanel("Workbook", null, workbookWorkFlow,
                         "Workbook workflow", KeyEvent.VK_W);

        // App builder workflow
        // Workbook workflow
        JPanel appBuildeWorkFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        JButton openNewAppBuilder;

        openNewAppBuilder = new JButton("New App builder workspace",
                                        images.getNewWorkspace());
        openNewAppBuilder.setToolTipText("Opens up a App builder workspace (" +
                            "CAUTION: This is experimetal feature!)");
        openNewAppBuilder.setBorderPainted(false);
        openNewAppBuilder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JInternalFrame jif = new JInternalFrame("App builder",
                                                        true, true, true, true);

                jif.setLayout(new BorderLayout());
                jif.add(new IDEAppBuilder()); 
                ideInstance.getWorkspaceDesktop().addInternalFrame(jif);
                jif.setVisible(true);
            }
        });
        appBuildeWorkFlow.add(openNewAppBuilder);
        addWorkflowPanel("App builder", null, appBuildeWorkFlow,
                         "App builder workflow", KeyEvent.VK_B);

        // Tools workflow
        JPanel toolsWorkflow = new JPanel(new FlowLayout(FlowLayout.LEFT, 
                                                         0, 5));    
        toolsWorkflow.setBorder(BorderFactory.createEmptyBorder());
        
        final JButton openScriptShellButton;
        JButton openCodeEditor, openJRManButton, 
                atomInfoSettingsButton;
        
        openScriptShellButton = new JButton("Open Script Shell", 
                                          images.getExpand());
        openScriptShellButton.setHorizontalTextPosition(SwingConstants.LEFT);
        openScriptShellButton.setBorderPainted(false);
        openScriptShellButton.setRolloverEnabled(true);
        openScriptShellButton.setToolTipText(strings.getOpenBeanShellTip());
        openScriptShellButton.setMnemonic('h');
        openScriptShellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                extraShellPopup.show(openScriptShellButton, 
                                     0, openScriptShellButton.getHeight());
            }
        });
        toolsWorkflow.add(openScriptShellButton);
        
        openCodeEditor = new JButton("Open Code Editor", images.getEditor());
        openCodeEditor.setBorderPainted(false);
        openCodeEditor.setRolloverEnabled(true);
        openCodeEditor.setToolTipText("Open a code editor to edit BeanShell " +
                "or other codes.");
        openCodeEditor.setMnemonic('C');
        openCodeEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    Utility.executeBeanShellScript("edit();");
                } catch (Exception ex) {
                    System.err.println("Unable to upen editor: "  
                                       + ex.toString());
                    ex.printStackTrace();
                } // end of try .. catch block
            }
        });
        toolsWorkflow.add(openCodeEditor);
        
        openJRManButton = new JButton("Open JRMan", images.getJrMan());
        openJRManButton.setBorderPainted(false);
        openJRManButton.setRolloverEnabled(true);
        openJRManButton.setToolTipText(strings.getOpenJRManTip());
        openJRManButton.setMnemonic('J');
        openJRManButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                            .openJRManActionPerformed(evt);
            }
        });
        toolsWorkflow.add(openJRManButton);                
        
        toolsWorkflow.add(new IntegratedIDETalkUI(ideInstance));
        addWorkflowPanel("Tools", null, toolsWorkflow, 
                         "Tools workflow", KeyEvent.VK_T);                
        
        // Preferences workflow
        JPanel prefWorkflow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        prefWorkflow.setBorder(BorderFactory.createEmptyBorder());
        
        atomInfoSettingsButton = new JButton("Modify atomic information", 
                                             images.getOptions());        
        atomInfoSettingsButton.setBorderPainted(false);
        atomInfoSettingsButton.setRolloverEnabled(true);
        atomInfoSettingsButton.setMnemonic('A');
        atomInfoSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                            .openAtomInfoSettings(evt);
            }
        });
        prefWorkflow.add(atomInfoSettingsButton);
        
        JButton fedSecurityRules;
        
        fedSecurityRules = new JButton("Federation security rules", 
                                        images.getOptions());
        fedSecurityRules.setBorderPainted(false);
        fedSecurityRules.setRolloverEnabled(true);
        fedSecurityRules.setMnemonic('S');
        fedSecurityRules.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new IDEFederationRuleEditor(ideInstance);
            }
        });
        prefWorkflow.add(fedSecurityRules);
        
        addWorkflowPanel("Preferences", null, prefWorkflow, 
                         "Preferences workflow", KeyEvent.VK_P);
        
        // Help workflow
        JPanel helpWorkflow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        helpWorkflow.setBorder(BorderFactory.createEmptyBorder());
        
        JButton aboutButton, ideHelpButton;
        
        aboutButton = new JButton("About MeTA Studio", images.getAbout());
        aboutButton.setBorderPainted(false);
        aboutButton.setRolloverEnabled(true);
        aboutButton.setMnemonic('A');
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                            .aboutMenuItemActionPerformed(evt);
            }
        });
        helpWorkflow.add(aboutButton);
        
        ideHelpButton = new JButton("MeTA Studio Help", images.getAbout());
        ideHelpButton.setBorderPainted(false);
        ideHelpButton.setRolloverEnabled(true);
        ideHelpButton.setMnemonic('l');
        ideHelpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                          .contentsMenuItemActionPerformed(evt);
            }
        });
        helpWorkflow.add(ideHelpButton);
        
        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        updatePanel.setBorder(BorderFactory.createLineBorder(Color.red, 1));
        
        final JLabel updateStatus = new JLabel(images.getWarn(), JLabel.LEFT);  
        updateStatus.setText("Not yet checked for updates.");
        updatePanel.add(updateStatus);
        
        final JButton updateButton = new JButton("Check for updates",
                                           images.getRefresh());
        updateButton.setToolTipText("Get the latest version of MeTA Studio." +
                " [CAUTION: This is an experimental feature!]");
        updateButton.setBorderPainted(false);
        updateButton.setRolloverEnabled(true);
        updateButton.setMnemonic('u');        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Thread updateThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            MeTAUpdateManager updater 
                                    = MeTAUpdateManager.getInstance();
                            
                            if (updateButton.getText().startsWith("Check")) {
                              // check for updates
                              updater.check();
                              System.out.println("Is new version available? : "
                                             + updater.isNewVersionAvailable());
                              System.out.println("Is it an upgrade? : "
                                             + updater.isUpgradeRequired());

                              if (updater.isNewVersionAvailable()) {
                                 if (!updater.isUpgradeRequired()) {
                                   updateStatus.setIcon(images.getInform());
                                   updateStatus.setText("New updates available!");
                                   updateButton.setText("Update now!");
                                 } else {
                                   updateStatus.setIcon(images.getInform());
                                   updateStatus.setText("Visit "
                                       + "http://code.google.com/p/metastudio/"
                                       + " for instructions on upgrading"
                                       + " by installing a full binary"
                                       + " distribution.");
                                 } // end if
                              } else {
                                 updateStatus.setText("No new updates yet."); 
                              } // end if
                            } else {
                              // actually do the updates
                              updater.update();
                              
                              updateStatus.setIcon(images.getError());
                              updateStatus.setText("New updates installed!");
                              updateButton.setText("Restart the IDE!");
                              updateButton.setToolTipText("Restart the IDE!");
                              updateButton.setBackground(Color.red);
                              updateButton.setForeground(Color.green);
                              updateButton.setEnabled(false);
                            } // end if
                        } catch (Exception e) {
                            System.err.println("Error while updating: " 
                                               + e.toString());
                            e.printStackTrace();
                        } // end of try .. catch block
                    }
                };
                
                updateThread.setPriority(Thread.MIN_PRIORITY);
                updateThread.setName("MeTA Studio update thread");
                updateThread.start();
            }
        });
        updatePanel.add(updateButton);
        
        helpWorkflow.add(updatePanel);
        
        addWorkflowPanel("Help", null, helpWorkflow, 
                         "Help workflow", KeyEvent.VK_H);
    }

    /**
     * make extra options popup
     */
    private void makeExtraOptionsPopup() {                
        extraOptionsPopup = new JPopupMenu();        
        
        closeWorkSpace = new JMenuItem("Close workspace");        
        closeWorkSpace.setMnemonic('l');
        closeWorkSpace.setEnabled(false);
        closeWorkSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                 MainMenuEventHandlers.getInstance(ideInstance)
                                    .closeWorkspaceMenuItemActionPerformed(evt);
            }
        });
        closeWorkSpace.setEnabled(false);
        extraOptionsPopup.add(closeWorkSpace);        
        extraOptionsPopup.addSeparator();
        
        recentWorkspace = new JMenu("Recent workspaces");
        recentWorkspace.setMnemonic('q');
        extraOptionsPopup.add(recentWorkspace);
        
        recentFiles = new JMenu("Recent files");
        recentFiles.setMnemonic('f');
        extraOptionsPopup.add(recentFiles);
        extraOptionsPopup.addSeparator();
        makeRecentMenu();
        
        exitStudio = new JMenuItem("Exit MeTA Studio");
        exitStudio.setMnemonic('x');
        exitStudio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                         .exitMenuItemActionPerformed(evt);
            }
        });
        extraOptionsPopup.add(exitStudio);
                
        extraShellPopup = new JPopupMenu();          
            JMenuItem openBeanShell = new JMenuItem("BeanShell script shell");
            openBeanShell.setMnemonic('B');
            openBeanShell.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                            .openBeanShellActionPerformed(e);
                }
            });
                    
        extraShellPopup.add(openBeanShell);
            JMenuItem openJythonShell = new JMenuItem("Jython script shell");
            openJythonShell.setMnemonic('J');
            openJythonShell.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO: this is a workaround and needs to be resolved
                    // via the ScriptGUIAdapter interface
                    try {
                        Utility.executeBeanShellScript("openJythonShell();");
                    } catch (Exception ex) {
                        System.err.println("Unable to upen editor: "
                                + ex.toString());
                        ex.printStackTrace();
                    } // end of try .. catch block
                }
            });
        extraShellPopup.add(openJythonShell);
    }

    private JButton dist, ang, dihed, removeAllMonitors;
    private JButton removeAllCaptions;
    private JCheckBox symb, indx, center;
    private JButton addAtoms, addAtomsAsFrag, removeAtoms;
    
    /**
     * Make extra options for the molecule workflow panel
     * 
     * @param extraMoleculeWorkflow
     */
    private void makeExtraOptions(JPanel extraMoleculeWorkflow) {
        final MoleculeViewer moleculeViewer;
        
        try {
            moleculeViewer = 
                    ((MoleculeViewerFrame) ideInstance.getWorkspaceDesktop()
                     .getActiveFrame()).getMoleculeViewer();
            moleculeViewer.removeUndoableEditListener(this);
            moleculeViewer.addUndoableEditListener(this);
        } catch(ClassCastException cce) {
            // ignored
            System.err.println("Unable to create extra workflow options: "
                               + cce.toString());
            cce.printStackTrace();
            return;
        }

        IDEPropertySheetUI pui = new IDEPropertySheetUI(
                              MoleculeViewerPropertyWrapper.class, 
                              new MoleculeViewerPropertyWrapper(moleculeViewer), 
                              ideInstance);
        pui.setPropertyOptionsInsects(new Insets(0, 0, 0, 0));
        pui.setDockable(true);
        
        JPanel puiPanel = new JPanel();
        pui.show(puiPanel);
        
        extraMoleculeWorkflow.removeAll();
        extraMoleculeWorkflow.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill  = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        extraMoleculeWorkflow.add(puiPanel, gbc);
        
            // monitor panel 
            JPanel monitorPanel = new JPanel(new IDEVerticalFlowLayout(0));
            monitorPanel.setBorder(BorderFactory.createTitledBorder(
                                                    "Monitor"));
            dist = new JButton("Distance");
            dist.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addTrackers();
                }
            });
            dist.setEnabled(false);
            monitorPanel.add(dist);
            
            ang = new JButton("Angle");
            ang.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addTrackers();
                }
            });
            ang.setEnabled(false);
            monitorPanel.add(ang);
            
            dihed = new JButton("Dihedral");
            dihed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addTrackers();
                }
            });
            dihed.setEnabled(false);
            monitorPanel.add(dihed);
            
            removeAllMonitors = new JButton("Remove All");
            removeAllMonitors.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().removeTrackers();
                }
            });
            monitorPanel.add(removeAllMonitors);
            
       gbc.gridx = 1;
       extraMoleculeWorkflow.add(monitorPanel, gbc);   
       
            // caption panel 
            JPanel captionPanel = new JPanel(new IDEVerticalFlowLayout(0));
            captionPanel.setBorder(BorderFactory.createTitledBorder(
                                                    "Captions"));             
            symb = new JCheckBox("Symbol");
            symb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addAtomSymbolCaptions(
                                                     symb.isSelected());
                }
            });
            symb.setEnabled(false);
            symb.setSelected(false);
            captionPanel.add(symb);
            
            indx = new JCheckBox("Index");
            indx.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addAtomIndexCaptions(
                                                     indx.isSelected());
                }
            });
            indx.setEnabled(false);
            indx.setSelected(false);
            captionPanel.add(indx);
            
            center = new JCheckBox("Center");
            center.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addAtomCenterCaptions(
                                                     center.isSelected());
                }
            });
            center.setEnabled(false);
            center.setSelected(false);
            captionPanel.add(center);
            
            removeAllCaptions = new JButton("Remove All");
            removeAllCaptions.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().removeAtomCaptions();
                }
            });
            captionPanel.add(removeAllCaptions);
            
       gbc.gridx = 2;
       extraMoleculeWorkflow.add(captionPanel, gbc); 
       
            // fragment panel
            JPanel fragmentPanel = new JPanel(new IDEVerticalFlowLayout(0));
            fragmentPanel.setBorder(BorderFactory.createTitledBorder(
                                                    "Fragmentation"));
            JCheckBox fragmentationMode;
            final JCheckBox showGoodnessPanel;            

            fragmentationMode = new JCheckBox("Fragmentation Mode");
            fragmentationMode.setSelected(moleculeViewer.getPopup()
                                                        .isFragmentationMode());
            fragmentationMode.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().setFragmentationMode(
                            !moleculeViewer.getPopup().isFragmentationMode());
                }
            });
            fragmentPanel.add(fragmentationMode);
            
            showGoodnessPanel = new JCheckBox("Show goodness panel");
            showGoodnessPanel.setEnabled(moleculeViewer.isPartOfWorkspace());
            showGoodnessPanel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                      MoleculeViewerFrame mvf = 
                          (MoleculeViewerFrame) moleculeViewer.getParentFrame();
                     
                        if (showGoodnessPanel.isSelected()) {
                            mvf.showGoodnessSidePanel();
                        } else {
                            mvf.hideSidePanel();
                        } // end if
                    } catch(Exception ignored) {
                        System.err.println("Error in (de)-activating" +
                                " goodness panel: " + ignored.toString());
                        ignored.printStackTrace();
                    } // end of try .. catch block
                }
            });
            fragmentPanel.add(showGoodnessPanel);
            
            JPanel addRemoveAtomsPanel = new JPanel(new GridLayout(1, 2));
            
            addAtoms = new JButton("Add atom(s)");
            addAtoms.setToolTipText("Add selected atom(s) to " +
                                    "the visible fragment(s)");
            addAtoms.setBorderPainted(false);
            addAtoms.setRolloverEnabled(true);
            ActionListener actionAddAtoms = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addAtomToFragment();
                }
            };
            addAtoms.setEnabled(false);            
            addAtoms.addActionListener(actionAddAtoms);
            // and add the accelerator
            KeyStroke keyStrokeAddAtoms = KeyStroke.getKeyStroke(
                                           KeyEvent.VK_A, 
                                           0);
            addAtoms.registerKeyboardAction(actionAddAtoms, 
                                    "addAtomsToFrag", keyStrokeAddAtoms, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
            addRemoveAtomsPanel.add(addAtoms);
            
            removeAtoms = new JButton("Remove atom(s)");
            removeAtoms.setToolTipText("Remove selected atom(s) " +
                                       "from the visible fragment(s)");
            removeAtoms.setBorderPainted(false);
            removeAtoms.setRolloverEnabled(true);   
            ActionListener actionRemoveAtoms = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().removeAtomFromFragment();
                }
            };
            removeAtoms.addActionListener(actionRemoveAtoms);
            removeAtoms.setEnabled(false);
            // and add the accelerator
            KeyStroke keyStrokeRemoveAtoms = KeyStroke.getKeyStroke(
                                           KeyEvent.VK_DELETE, 0);
            removeAtoms.registerKeyboardAction(actionRemoveAtoms, 
                                    "removeAtoms", keyStrokeRemoveAtoms, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
            addRemoveAtomsPanel.add(removeAtoms);
            fragmentPanel.add(addRemoveAtomsPanel);
            
            addAtomsAsFrag = new JButton("Add atom(s) as fragment");
            addAtomsAsFrag.setToolTipText("Add selected atom(s) as " +
                                          "a new fragment");
            addAtomsAsFrag.setBorderPainted(false);
            addAtomsAsFrag.setRolloverEnabled(true);
            ActionListener actionAddAtomsAsFrag = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addAtomAsFragment();
                }
            };
            addAtomsAsFrag.addActionListener(actionAddAtomsAsFrag);
            addAtomsAsFrag.setEnabled(false);            
            // and add the accelerator
            KeyStroke keyStrokeAddAtomsAsFrag = KeyStroke.getKeyStroke(
                                           KeyEvent.VK_CONTROL|KeyEvent.VK_A, 
                                           0);
            addAtomsAsFrag.registerKeyboardAction(actionAddAtomsAsFrag, 
                                    "addAtomsAsFrag", keyStrokeAddAtomsAsFrag, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
            fragmentPanel.add(addAtomsAsFrag);                        
            
            fragmentPanel.setEnabled(moleculeViewer.getSceneList().size() == 1);
            
        gbc.gridx = 3;
        extraMoleculeWorkflow.add(fragmentPanel, gbc);
        
            // tools panel 
            JPanel toolsPanel = new JPanel(new IDEVerticalFlowLayout(0));
            toolsPanel.setBorder(BorderFactory.createTitledBorder("Tools"));
            
            JCheckBox gridGenerator = new JCheckBox("Grid generator");
            gridGenerator.setToolTipText("Tool to help generate a grid " +
                              "around the molecule to calculate properties.");
            gridGenerator.setSelected(moleculeViewer.isGridDrawn());
            gridGenerator.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                      MoleculeViewerFrame mvf = 
                          (MoleculeViewerFrame) moleculeViewer.getParentFrame();
                      
                      moleculeViewer.setGridDrawn(
                              !moleculeViewer.isGridDrawn());
                      
                      if (moleculeViewer.isGridDrawn()) {
                          mvf.showGridGeneratorSidePanel();
                      } else {
                          mvf.hideSidePanel();
                      } // end if
                    } catch(Exception ignored) {
                        System.err.println("Error in (de)-activating" +
                                " grid generator: " + ignored.toString());
                        ignored.printStackTrace();
                    } // end of try .. catch block
                }
            });
            gridGenerator.setSelected(moleculeViewer.isGridDrawn());
            toolsPanel.add(gridGenerator);
            
            JButton addMol, editMol;
            
            addMol = new JButton("Add molecule");
            addMol.setToolTipText("Add a molecule object to this frame.");            
            addMol.setBorderPainted(false);
            addMol.setRolloverEnabled(true);
            addMol.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().addMolecule();
                }
            });
            toolsPanel.add(addMol);
                    
            editMol = new JButton("Edit molecule");
            editMol.setToolTipText("Open the current molecule object" +
                                   " in a internal molecule editor.");
            editMol.setBorderPainted(false);
            editMol.setRolloverEnabled(true);
            editMol.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.getPopup().editMolecule();
                }
            });
            toolsPanel.add(editMol);
            
        gbc.gridx = 4;
        extraMoleculeWorkflow.add(toolsPanel, gbc);    
        
            // multi view panel
            JPanel multiViewPanel = new JPanel(new IDEVerticalFlowLayout());
            multiViewPanel.setBorder(BorderFactory.createTitledBorder(
                                                   "Multi view"));
            JCheckBox multiView = new JCheckBox("Enable multi view");
            multiView.setToolTipText(
                    "Control multiple views of the molecule scene");
            
            final JButton addNewView = new JButton("Add new view");
            
            multiView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                      MoleculeViewerFrame mvf = 
                          (MoleculeViewerFrame) moleculeViewer.getParentFrame();
                      
                      mvf.setMultiViewEnabled(!mvf.isMultiViewEnabled());
                      addNewView.setEnabled(mvf.isMultiViewEnabled());
                    } catch(Exception ignored) {
                        System.err.println("Error in (de)-activating" +
                                " multi view: " + ignored.toString());
                        ignored.printStackTrace();
                    } // end of try .. catch block 
                }
            });
            try {
                MoleculeViewerFrame mvf =
                        (MoleculeViewerFrame) moleculeViewer.getParentFrame();

                multiView.setSelected(mvf.isMultiViewEnabled());
                addNewView.setEnabled(mvf.isMultiViewEnabled());
            } catch (Exception ignored) {
                System.err.println("Error in (de)-activating" +
                        " multi view: " + ignored.toString());
                ignored.printStackTrace();
            } // end of try .. catch block
            multiViewPanel.add(multiView);

            addNewView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                   try {
                      MoleculeViewerFrame mvf = 
                          (MoleculeViewerFrame) moleculeViewer.getParentFrame();
                      
                      mvf.addNewMoleculeView();
                   } catch(Exception ignored) {
                        System.err.println("Error add a new view: " 
                                           + ignored.toString());
                        ignored.printStackTrace();
                   } // end of try .. catch block                  
                }
            });
            multiViewPanel.add(addNewView);
            
        gbc.gridx = 5;
        // TODO: the following is not really a correct way to do things!
        gbc.insets = new Insets(0, 0, 0, 5000);
        extraMoleculeWorkflow.add(multiViewPanel, gbc);     
    }
    
    /**
     * make up the recent workspace menu
     */
    public void makeRecentMenu() {
        RecentFilesConfiguration rfc = RecentFilesConfiguration.getInstance();
        StringResource strings = StringResource.getInstance();
        
        Iterator fileList = rfc.getFileList();
        String filePath;
        int numberW = 1, numberF = 1;
        
        // remove all the items
        recentWorkspace.removeAll(); recentFiles.removeAll();
        
        while(fileList.hasNext()) {
            filePath = (String) fileList.next();
            
            // is it a proper workspace extension?
            if (filePath.endsWith(strings.getWorkspaceFileExtension())) {
                // if yes we add it to our menu
                JMenuItem fileItem = new JMenuItem(numberW + " " + filePath);
                
                fileItem.setName(filePath);
                fileItem.setMnemonic((new String(numberW + "").charAt(0)));
                fileItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        MainMenuEventHandlers.getInstance(ideInstance)
                         .openWorkspace(((JMenuItem)evt.getSource()).getName(),
                                        false);
                    }
                });
                recentWorkspace.add(fileItem);
                
                numberW++;
            } else {
                // if yes we add it to our menu
                JMenuItem fileItem = new JMenuItem(numberF + " " + filePath);
                
                fileItem.setName(filePath);
                fileItem.setMnemonic((new String(numberF + "").charAt(0)));
                fileItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        int option = JOptionPane.showConfirmDialog(ideInstance, 
                               "Generate Z-Matrix?", "Query", 
                                JOptionPane.YES_NO_OPTION);
                        
                        MainMenuEventHandlers.getInstance(ideInstance)
                         .openMoleculeFile(
                              ((JMenuItem)evt.getSource()).getName(), 
                              option==JOptionPane.YES_OPTION);
                    }
                });
                recentFiles.add(fileItem);
                
                numberF++;
            } // end if            
        } // end while
    }
    
    /**
     * Getter for property saveWorkspaceButton.
     * @return Value of property saveWorkspaceButton.
     */
    public JButton getSaveWorkspaceButton() {
        return this.saveWorkspaceButton;
    }

    /**
     * Getter for property closeWorkSpace.
     * @return Value of property closeWorkSpace.
     */
    public JMenuItem getCloseWorkSpace() {
        return this.closeWorkSpace;
    }

    /**
     * undoable edit action
     * 
     * @param e the event object
     */
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        if (e.getEdit() instanceof SelectionUndoableEdit) {
            SelectionUndoableEdit se = (SelectionUndoableEdit) e.getEdit();
            
            Stack<ScreenAtom> ss = se.getFinalSelectionStack();

            dist.setEnabled(false);
            ang.setEnabled(false);
            dihed.setEnabled(false);            
            
            symb.setEnabled(false);
            indx.setEnabled(false);
            center.setEnabled(false); 
            
            symb.setSelected(false);
            indx.setSelected(false);
            center.setSelected(false);            
            
            addAtoms.setEnabled(false);
            addAtomsAsFrag.setEnabled(false);
            removeAtoms.setEnabled(false);
            
            if (ss.size() == 0) return;
            
            switch(ss.size()) {
                case 2:
                    dist.setEnabled(true); break;
                case 3:
                    ang.setEnabled(true); break;
                case 4:
                    dihed.setEnabled(true); break;
            } // end of switch ... case
                        
            symb.setEnabled(true);
            indx.setEnabled(true);
            center.setEnabled(true);
            
            addAtoms.setEnabled(true);
            addAtomsAsFrag.setEnabled(true);
            removeAtoms.setEnabled(true);
            
            Iterator<ScreenAtom> sa = ss.iterator();
            ScreenAtom atom;
            
            atom = sa.next();
            boolean sym, idx, cen;
            
            sym = atom.isShowingSymbolLabel();
            idx = atom.isShowingIDLabel();
            cen = atom.isShowingCenterLabel();
            
            while(sa.hasNext()) {
                atom = sa.next();
                
                sym = sym & atom.isShowingSymbolLabel();
                idx = idx & atom.isShowingIDLabel();
                cen = cen & atom.isShowingCenterLabel();
            } // end while
            
            symb.setSelected(sym);
            indx.setSelected(idx);
            center.setSelected(cen);  
        } // end if
    }
} // end of class IDEWorkflowBar
