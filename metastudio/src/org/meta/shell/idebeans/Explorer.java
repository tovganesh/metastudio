/*
 * Explorer.java
 *
 * Created on June 22, 2003, 7:31 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;

import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;
import org.meta.workspace.Workspace;

/**
 * The workspace explorer control...
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Explorer extends JInternalFrame {
    
    private JTabbedPane explorerPane;
    
    private TaskPanel taskPanel;    
    private WorkspacePanel workspacePanel;
    private FilePanel filePanel;
    private RDFJobNotificationPanel rdfJobNotificationPanel;
    private IDEWidgetsPanel widgetsPanel;
            
    private Workspace currentWorkspace;
    
    private MeTA ideInstance;
    
    /** Creates a new instance of Explorer */
    public Explorer(MeTA ideInstance) {    
        super("Explorer");
        
        this.ideInstance = ideInstance;
        
        setFrameIcon(ImageResource.getInstance().getExplorer());
        
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        ImageResource images = ImageResource.getInstance();
        StringResource strings = StringResource.getInstance();
        
        getContentPane().setFont(FontResource.getInstance().getFrameFont());
        
        explorerPane = new JTabbedPane(JTabbedPane.LEFT);        
        
        taskPanel = new TaskPanel();
        
        // main group - common tasks
        TaskGroup mainGroup = new TaskGroup("Common Tasks");
            Task taskNewWorkspace = new Task("Create a new workspace");
            taskNewWorkspace.setIcon(images.getNewWorkspace());
            taskNewWorkspace.setToolTipText(strings.getNewWorkspaceTip());
            taskNewWorkspace.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                    .newWorkspaceMenuItemActionPerformed(evt);
                }
            });            
            mainGroup.add(taskNewWorkspace);
        
            Task taskOpenWorkspace = new Task("Open an existing workspace");
            taskOpenWorkspace.setIcon(images.getOpenWorkspace());
            taskOpenWorkspace.setToolTipText(strings.getOpenWorkspaceTip());
            taskOpenWorkspace.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                    .openWorkspaceMenuItemActionPerformed(evt);
                }
            });            
            mainGroup.add(taskOpenWorkspace);
        
            Task taskOpenMolecule = new Task("Open molecule file for viewing");            
            taskOpenMolecule.setIcon(images.getMoleculeViewer());
            taskOpenMolecule.setToolTipText(strings.getOpenMoleculeTip());
            taskOpenMolecule.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                          .openMoleculeTaskActionPerformed(evt);
                }
            });
            mainGroup.add(taskOpenMolecule);
        
            Task taskOpenBeanShell = new Task("Open BeanShell for scripting");
            taskOpenBeanShell.setIcon(images.getBeany());
            taskOpenBeanShell.setToolTipText(strings.getOpenBeanShellTip());
            taskOpenBeanShell.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                            .openBeanShellActionPerformed(evt);
                }
            });
            mainGroup.add(taskOpenBeanShell);
            
            Task taskOpenJRMan = new Task("Open JRMan for rendering");
            taskOpenJRMan.setIcon(images.getJrMan());
            taskOpenJRMan.setToolTipText(strings.getOpenJRManTip());
            taskOpenJRMan.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                            .openJRManActionPerformed(evt);
                }
            });
            mainGroup.add(taskOpenJRMan);                        
        taskPanel.add(mainGroup);
        
        // molecule tasks
        TaskGroup moleculeGroup = new TaskGroup("Molecule related Tasks");
            Task taskSaveMolecule = new Task("Save the current geometry");
            taskSaveMolecule.setIcon(images.getSaveWorkspace());
            taskSaveMolecule.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                            .saveMoleculeActionPerformed(evt);
                }
            });
            moleculeGroup.add(taskSaveMolecule);
            
            Task taskExportRIB = new Task("Export current geometry as "
                                           + "RIB file");
            taskExportRIB.setIcon(images.getRender());
            taskExportRIB.setToolTipText("<html><body>"
                            + "Exports the current geometry "
                            + "in Renderman format, <br> which can be rendered "
                            + "using renderman compliant rendering tools like " 
                            + "<i>JRMan</i>, <i>3Delight</i> etc.."
                            + "</html></body>");
            taskExportRIB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                         .exportToRIBActionPerformed(evt);
                }
            });
            moleculeGroup.add(taskExportRIB);
            
            Task taskSaveMoleculeImage = new Task("Save current scene as " +
                                                  "image file");
            taskSaveMoleculeImage.setIcon(images.getSaveWorkspace());
            taskSaveMoleculeImage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                         .saveMoleculeImageActionPerformed(evt);
                }
            });
            moleculeGroup.add(taskSaveMoleculeImage);
            
            Task taskAttachProperty = new Task("Attach a property file");
            taskAttachProperty.setIcon(images.getSurface());
            taskAttachProperty.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                         .attachPropertyActionPerformed(evt);
                }
            });
            moleculeGroup.add(taskAttachProperty);
        moleculeGroup.setVisible(false);
        taskPanel.add(moleculeGroup);
            
        // help group
        TaskGroup helpGroup = new TaskGroup("Help on using MeTA Studio");
            Task taskMeTADocs = new Task("MeTA Studio documentation");
            taskMeTADocs.setIcon(images.getHelp());
            taskMeTADocs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainMenuEventHandlers.getInstance(ideInstance)
                                        .contentsMenuItemActionPerformed(evt);
                }
            });
            helpGroup.add(taskMeTADocs);
        taskPanel.add(helpGroup);
                
        taskPanel.refreshUI();        
        explorerPane.addTab(null, 
        new CompositeIcon(images.getTasks(), new VerticalTextIcon(taskPanel, 
                         "MeTA Studio Tasks", 
                         VerticalTextIcon.ROTATE_LEFT), CompositeIcon.BOTTOM),
                         taskPanel);       
        explorerPane.setToolTipTextAt(0, "Central place to access all commonly"
                            + " used tasks with in the studio");                
        
        workspacePanel = new WorkspacePanel(ideInstance);
        explorerPane.addTab(null, 
        new CompositeIcon(images.getWorkspace(), 
                         new VerticalTextIcon(workspacePanel, 
                         "[No Workspace Loaded]", 
                         VerticalTextIcon.ROTATE_LEFT), CompositeIcon.BOTTOM), 
                         workspacePanel);                
        explorerPane.setToolTipTextAt(1, 
                            "Manage the current workspace using"
                            + " the hierarchial setup");
        
        // finally add the panel!
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(explorerPane);
    }
    
    /**
     * add aux panels
     */
    public void addAuxPanels() {
        ImageResource images = ImageResource.getInstance();
        StringResource strings = StringResource.getInstance();
        
        filePanel = new FilePanel(ideInstance);       
        explorerPane.addTab(null, 
        new CompositeIcon(images.getFileSystem(), 
                          new VerticalTextIcon(filePanel, 
                          "Filesystems", 
                          VerticalTextIcon.ROTATE_LEFT), CompositeIcon.BOTTOM),  
                          filePanel);                
        explorerPane.setToolTipTextAt(2, "Access to local file system");
        
        rdfJobNotificationPanel = new RDFJobNotificationPanel(ideInstance);
        explorerPane.addTab(null, 
        new CompositeIcon(images.getRss(), 
                          new VerticalTextIcon(rdfJobNotificationPanel, 
                          "Job notification and query", 
                          VerticalTextIcon.ROTATE_LEFT), CompositeIcon.BOTTOM),  
                          rdfJobNotificationPanel);                
        explorerPane.setToolTipTextAt(3, "Use RSS subscription to get " +
            "notification of Jobs running on remote grids or supercomputers " +
            "which support such notifications ");                
        
        widgetsPanel = new IDEWidgetsPanel(ideInstance);
        explorerPane.addTab(null, 
        new CompositeIcon(images.getBeany(), 
                         new VerticalTextIcon(widgetsPanel, 
                         "Widgets", 
                         VerticalTextIcon.ROTATE_LEFT), CompositeIcon.BOTTOM), 
                         widgetsPanel);                
        explorerPane.setToolTipTextAt(4, 
           "Expand the functionality of MeTA Studio with useful widgets.");        
    }
    
    /** Getter for property widgetsPanel.
     * @return Value of property widgetsPanel.
     *
     */
    public IDEWidgetsPanel getWidgetsPanel() {
        return this.widgetsPanel;
    }
    
    /** Getter for property currentWorkspace.
     * @return Value of property currentWorkspace.
     *
     */
    public Workspace getCurrentWorkspace() {
        return this.currentWorkspace;
    }
    
    /**
     * update the UI ro reflect this new workspace.
     *
     * @param workspace - instance of the new Workspace
     */
    public void update(Workspace workspace) {
        this.currentWorkspace = workspace;
        
        ImageResource images = ImageResource.getInstance();
        
        // update the panel
        if (workspace != null) {
            explorerPane.setIconAt(1, new CompositeIcon(images.getWorkspace(), 
                         new VerticalTextIcon(workspacePanel,  
                         "Workspace " + currentWorkspace.getInternalName(), 
                         VerticalTextIcon.ROTATE_LEFT), CompositeIcon.BOTTOM));
        } else {
            explorerPane.setIconAt(1,
            new CompositeIcon(images.getWorkspace(), 
                         new VerticalTextIcon(workspacePanel, 
                         "[No Workspace Loaded]", 
                         VerticalTextIcon.ROTATE_LEFT), CompositeIcon.BOTTOM));
        } // end if
        
        // now forward the update the UI call...
        workspacePanel.update(currentWorkspace);
    }
    
    /** Getter for property taskPanel.
     * @return Value of property taskPanel.
     *
     */
    public TaskPanel getTaskPanel() {
        return this.taskPanel;
    }
    
    /**
     * Getter for property workspacePanel.
     * @return Value of property workspacePanel.
     */
    public WorkspacePanel getWorkspacePanel() {
        return this.workspacePanel;
    }
    
    /**
     * Getter for property filePanel.
     * @return Value of property filePanel.
     */
    public FilePanel getFilePanel() {
        return this.filePanel;
    }
    
    /**
     * Getter for property rdfJobNotificationPanel.
     * @return Value of property rdfJobNotificationPanel.
     */
    public RDFJobNotificationPanel getRdfJobNotificationPanel() {
        return this.rdfJobNotificationPanel;
    }
    
} // end of class Explorer
