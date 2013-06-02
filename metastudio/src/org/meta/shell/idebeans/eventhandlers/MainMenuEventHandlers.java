/*
 * MainMenuEventHandlers.java
 *
 * Created on August 3, 2003, 8:09 AM
 */

package org.meta.shell.idebeans.eventhandlers;


import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;

import javax.help.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.meta.common.Utility;
import org.meta.common.resource.CursorResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.common.resource.StringResource;
import org.meta.config.impl.FileImplParameter;
import org.meta.config.impl.RecentFilesConfiguration;
import org.meta.molecule.Molecule;
import org.meta.moleculereader.MoleculeFileReaderFactory;
import org.meta.scripting.ScriptLanguage;
import org.meta.shell.ide.AboutMeTA;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.AtomInfoSettingsDialog;
import org.meta.shell.idebeans.BeanShellScriptEngineGUI;
import org.meta.shell.idebeans.IDEFileChooser;
import org.meta.shell.idebeans.IDEFileFilter;
import org.meta.shell.idebeans.IDEFileView;
import org.meta.shell.idebeans.IDEJRManFrame;
import org.meta.shell.idebeans.MoleculeEditorFrame;
import org.meta.shell.idebeans.MoleculeViewerFrame;
import org.meta.shell.idebeans.NewWorkspaceWizard;
import org.meta.shell.idebeans.ScriptEngineGUIAdapter;
import org.meta.shell.idebeans.ScriptInterpreterGUI;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.workspace.Workspace;
import org.meta.workspace.impl.MoleculeItem;

/**
 * Handlers for all main menu/ task events.
 * Follows Singleton desing pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MainMenuEventHandlers {
    
    private static MainMenuEventHandlers _menuEventHandler;
    
    /** Holds value of property ideInstance. */
    private MeTA ideInstance;
    
    /** a bean shell count - always increases. we hope the 
      user never open bean shells greater than the integer limit!! >2 billion */
    private int beanShellCount;
    
    /** Creates a new instance of MainMenuEventHandlers */
    private MainMenuEventHandlers(MeTA ideInstance) {
        this.ideInstance = ideInstance;
        beanShellCount   = 1;
    }
    
    /**
     * method to return instance of this object.
     *
     * @param ideInstance - instance of the IDE
     * @return MainMenuEventHandlers a single global instance of this class
     */
    public static MainMenuEventHandlers getInstance(MeTA ideInstance) {
        if (_menuEventHandler == null) {
            _menuEventHandler = new MainMenuEventHandlers(ideInstance);            
        } // end if
        
        return _menuEventHandler;
    }
    
    /**
     * for handling newWorkspace menu events
     *
     * @param evt - the ActionEvent object!
     */
    public void newWorkspaceMenuItemActionPerformed(ActionEvent evt) {
        NewWorkspaceWizard wizard = new NewWorkspaceWizard(ideInstance);
        
        wizard.setLocationRelativeTo(ideInstance);        
        wizard.setModal(true);
        wizard.setVisible(true);        
    }
    
    /**
     * for handling openWorkspace menu events
     *
     * @param evt - the ActionEvent object!
     */
    public void openWorkspaceMenuItemActionPerformed(ActionEvent evt) {
        IDEFileChooser workspaceChooser = new IDEFileChooser();
                
        workspaceChooser.setDialogTitle("Choose a workspace...");
        
        IDEFileFilter fileFilter = new IDEFileFilter(
         new String[]{StringResource.getInstance().getWorkspaceFileExtension()}, 
         "MeTA Studio workspace");         
        workspaceChooser.addChoosableFileFilter(fileFilter);
        
        // and add the iconic stuff
        workspaceChooser.setFileView(new IDEFileView());
        
        if (workspaceChooser.showOpenDialog(ideInstance)
                                == IDEFileChooser.APPROVE_OPTION) {
           if (!workspaceChooser.getSelectedFile().exists()) return;
           
           openWorkspace(workspaceChooser.getSelectedFile()
                                             .getAbsolutePath());
        } // end if
    }
    
     /**
     * opens up a workspace file, a public utility only to be used by
     * event handlers
     *
     * @param fileName the file name of the workspace file
     * @param addToRecentList a flag to indicate whether this file sohuld be
     *        added to recently used list
     */
    public void openWorkspace(String fileName, boolean addToRecentList) {
       if (addToRecentList) {
           RecentFilesConfiguration.getInstance().setParameter(null, 
                 new FileImplParameter(new File(fileName)));
           ideInstance.getWorkflowBar().makeRecentMenu();
       } // end if
                 
       // try to open the workspace 
       try {                              
           ideInstance.setCursor(
                       Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

           // init workspace
           Class implClz = Utility.getDefaultImplFor(Workspace.class);
           Constructor theConstructor = implClz.getDeclaredConstructor(
                                                           String.class);

           Workspace ws = (Workspace) theConstructor.newInstance(fileName);
           ws.open();

           // update the UI
           ideInstance.setCurrentWorkspace(ws);

           ideInstance.setCursor(Cursor.getPredefinedCursor(
                                                  Cursor.DEFAULT_CURSOR));

           // open the MoleculeViewerFrame and related molecules in it
           MoleculeViewerFrame mvf = new MoleculeViewerFrame(ideInstance);
           ideInstance.getWorkspaceDesktop().addInternalFrame(mvf, true);
           
           mvf.getMoleculeViewer().disableUndo();
           for(int i=0; i<ws.getWorkspaceItems().size(); i++) {
               if (ws.getWorkspaceItems().get(i) instanceof MoleculeItem) {
                    mvf.addScene(new MoleculeScene((Molecule)
                         ((MoleculeItem) ws.getWorkspaceItems().get(i))
                            .getItemData().getData()));
               } // end if
           } // end for    
           mvf.getMoleculeViewer().enableUndo();
           
           ideInstance.getWorkflowBar().getCloseWorkSpace()
                                            .setEnabled(true);
       } catch (Exception e) {
           // should never happen
           ideInstance.setCursor(Cursor.getPredefinedCursor(
                                                  Cursor.DEFAULT_CURSOR));
           System.err.println("ERROR: Workspace open action failed!");
           e.printStackTrace();
           JOptionPane.showMessageDialog(ideInstance,
                "ERROR: Workspace open action failed! \n"
                + StringResource.getInstance().getStdErrorMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
           return;
       } // end try .. catch block   
    }
    
    /**
     * opens up a workspace file, a public utility only to be used by
     * event handlers
     *
     * @param fileName the file name of the workspace file
     */
    public void openWorkspace(String fileName) {
        openWorkspace(fileName, true);        
    }
    
    /**
     * for handling saveWorkspace menu events
     *
     * @param evt - the ActionEvent object!
     */
    public void saveWorkspaceMenuItemActionPerformed(ActionEvent evt) {
        if (ideInstance.getCurrentWorkspace() != null) {
            try {
              ideInstance.getCurrentWorkspace().setLastModifiedDate(new Date());
              ideInstance.getCurrentWorkspace().save();
              
              // this is a bit ugly .. but required
              ideInstance.setTitle(StringResource.getInstance().getVersion() 
                   + " - ["
                     + ideInstance.getCurrentWorkspace().getInternalName() 
                   + "]");
              ideInstance.getWorkflowBar().getSaveWorkspaceButton()
                                            .setEnabled(false);
            } catch (Exception e) {                
              System.err.println("ERROR: Workspace save operation failed !");
              e.printStackTrace();
              JOptionPane.showMessageDialog(ideInstance, 
                        "ERROR: Workspace save operation failed! \n"
                        + StringResource.getInstance().getStdErrorMessage(), 
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
              return;
            } // end try .. catch block
        } // end if
    }

    /**
     * for handling closeWorkspace menu events
     *
     * @param evt - the ActionEvent object!
     */
    public void closeWorkspaceMenuItemActionPerformed(ActionEvent evt) {
        if (ideInstance.getCurrentWorkspace() != null) {
            try {
                // set the current workspace to null, effectively closing
                // the current workspace
                ideInstance.setCurrentWorkspace(null);
                ideInstance.getWorkflowBar().getCloseWorkSpace()
                                            .setEnabled(false);
                ideInstance.getWorkflowBar().getSaveWorkspaceButton()
                                            .setEnabled(false);
            } catch (Exception e) {                
                System.err.println("ERROR: Workspace close operation failed !");
                e.printStackTrace();
                JOptionPane.showMessageDialog(ideInstance, 
                        "ERROR: Workspace close operation failed! \n"
                        + StringResource.getInstance().getStdErrorMessage(), 
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end try .. catch block
        } // end if
    }
    
    /**
     * for handling exit menu events
     *
     * @param evt - the ActionEvent object!
     */
    public void exitMenuItemActionPerformed(ActionEvent evt) {
        ideInstance.exitIDE(0);
    }
    
    /**
     * for handling about menu events
     *
     * @param evt - the ActionEvent object!
     */
    public void aboutMenuItemActionPerformed(ActionEvent evt) {        
        new AboutMeTA(ideInstance);
    }
    
    /**
     * for handling contents menu events
     *
     * @param evt - the ActionEvent object!
     */
    public void contentsMenuItemActionPerformed(ActionEvent evt) {        
        StringResource strings = StringResource.getInstance();
        ClassLoader cl = MeTA.class.getClassLoader();
        HelpSet hs = null;
        
        ideInstance.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {            
            URL hsURL = HelpSet.findHelpSet(cl, strings.getIdeHelpSet());            
            hs = new HelpSet(cl, hsURL);
        } catch (Exception ee) {
            ideInstance.setCursor(Cursor.getPredefinedCursor(
                                                      Cursor.DEFAULT_CURSOR));
            // Say what the exception really is
            System.out.println("HelpSet " + ee.getMessage());
            System.out.println("HelpSet " + strings.getIdeHelpSet() 
                               + " not found");
            
            ee.printStackTrace();
            
            JOptionPane.showMessageDialog(ideInstance,
					      "HelpSet not found", 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
            return;
        } // end try .. catch block
        
        // Create a HelpBroker object:
        JHelp helpComponent = new JHelp(hs);
        
        // trigger the help viewer:     
        JInternalFrame helpFrame = new JInternalFrame(
                                        strings.getVersion() + " - Help",
                                        true, true, true, true);
        helpFrame.getContentPane().setLayout(new BorderLayout());
        helpFrame.getContentPane().add(helpComponent);        
        helpFrame.setSize(MiscResource.getInstance().getHelpFrameDimension());
        helpFrame.setFrameIcon(ImageResource.getInstance().getHelp());
        ideInstance.getWorkspaceDesktop().addInternalFrame(helpFrame);
        helpFrame.setVisible(true);
        ideInstance.setCursor(Cursor.getPredefinedCursor(
                                                  Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * for handling open bean shell event
     * 
     * @param evt - the ActionEvent object!
     */
    public void openBeanShellActionPerformed(ActionEvent evt) {     
        try {
            ideInstance.setCursor(
                    CursorResource.getInstance().getOpeningCursor());

            JInternalFrame beanShellFrame = new JInternalFrame(
                    "Bean Shell - [" + beanShellCount + "]",
                    true, true, true, true);
            
            final ScriptEngineGUIAdapter bsEngine = ScriptInterpreterGUI.getInstance()
                                    .findScriptEngineGUIFor(ScriptLanguage.BEANSHELL);
            
            beanShellFrame.setFrameIcon(
                    ImageResource.getInstance().getBeany());
            beanShellFrame.getContentPane().setLayout(new BorderLayout());
            beanShellFrame.getContentPane().add(bsEngine.getShellUI());
            beanShellFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    Thread closeThread = new Thread() {
                        @Override
                        public void run() {
                            System.out.println("Stopping Interpreter...");
                            bsEngine.stopInterpreter();
                            System.out.println("Interpreter stopped.");
                        }
                    }; 
                    closeThread.start();
                }
            });

            ideInstance.getWorkspaceDesktop().addInternalFrame(beanShellFrame);
            beanShellFrame.setVisible(true);

            ideInstance.setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            beanShellCount++;
        } catch (Exception e) {
            System.out.println("Unable to start BeanShell: " + e.toString());
            e.printStackTrace();
        } // end of try .. catch block            
    }
    
    /**
     * for handling open jrman event
     * 
     * @param evt - the ActionEvent object!
     */
    public void openJRManActionPerformed(ActionEvent evt) {
        ideInstance.getWorkspaceDesktop().addInternalFrame(
                                   new IDEJRManFrame(ideInstance), true);
    }

    /**
     * for handling newMolecule task events
     *
     * @param evt - the ActionEvent object!
     */
    public void newMoleculeTaskActionPerformed(ActionEvent evt) {
        MoleculeEditorFrame mef = new MoleculeEditorFrame(ideInstance);

        // add a dummy molecule scene as a starter
        mef.getMoleculeEditor().disableUndo();
        try {
            mef.addScene(new MoleculeScene((Molecule)
                Utility.getDefaultImplFor(Molecule.class).newInstance()));
        } catch (Exception ignored) {
            System.err.println("Exception in newMoleculeTaskActionPerformed: "
                               + ignored.toString());
            ignored.printStackTrace();
        } // end of try .. catch block
        mef.getMoleculeEditor().enableUndo();

        // add the frame to studio desktop
        ideInstance.getWorkspaceDesktop().addInternalFrame(mef);
    }
    
    /**
     * for handling openMolecule task events
     *
     * @param evt - the ActionEvent object!
     */
    public void openMoleculeTaskActionPerformed(ActionEvent evt) {
        final IDEFileChooser fileChooser = new IDEFileChooser();
        final JDialog fileDialog = new JDialog(ideInstance, 
                                         "Open molecule file as...", true);
        final JCheckBox makeZMatrix = new JCheckBox("Make Z-Matrix");
        final JCheckBox openInJmol = new JCheckBox("Open in Jmol viewer");
        final JCheckBox copyConnectivity = new JCheckBox("Copy connectivity");
        
        fileChooser.setShowPreview(true);
        fileChooser.setDialogTitle("Choose a molecule file...");
        
        // construct the file filter
        try {
            MoleculeFileReaderFactory mfrm =
                        (MoleculeFileReaderFactory) Utility.getDefaultImplFor(
                                MoleculeFileReaderFactory.class).newInstance();
            
            Iterator<String> supportedFileFormats 
                                      = mfrm.getAllSupportedTypes();
            Vector<String> fileFormatList = new Vector<String>();
            
            while(supportedFileFormats.hasNext()) {
                fileFormatList.add(supportedFileFormats.next());
            } // end while
            
            IDEFileFilter fileFilter =
                              new IDEFileFilter(fileFormatList.toArray(),
                                         "All supported formats");
            
            // and add the file filter            
            fileChooser.addChoosableFileFilter(fileFilter);
            // and add the iconic stuff
            fileChooser.setFileView(new IDEFileView());
        } catch(Exception e) {
            System.err.println("Exception : " + e.toString());
            System.err.println("Could not make proper file filters.");
            e.printStackTrace();
        } // end of try .. catch block
        
        fileChooser.setApproveButtonText("Open molecule");
        fileChooser.addActionListener(new ActionListener() {        
            @Override
            public void actionPerformed(ActionEvent ae) {        
                // close the dialog 
                fileDialog.dispose();
                
                if (ae.getActionCommand().equals("CancelSelection")) {                    
                    return;
                } // end if
                
                // check for the validity of this file
                // and then proceed
                if (!fileChooser.getSelectedFile().exists()) return;
            
                // update last visited folder
                fileChooser.setLastVisitedFolder(
                           fileChooser.getCurrentDirectory().getAbsolutePath());
        
                if (!openInJmol.isSelected()) {
                    openMoleculeFile(fileChooser.getSelectedFile()
                                                .getAbsolutePath(),
                                     makeZMatrix.isSelected(),
                                     copyConnectivity.isSelected());
                } else {
                    // unix style separators;
                    String fileName = fileChooser.getSelectedFile()
                                      .getAbsolutePath().replace('\\', '/'); 
                    try {                        
                        Utility.executeBeanShellScript("openInJmol(\"" 
                                                          + fileName + "\")");            
                    } catch (Exception ignored) {
                        ideInstance.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                        ignored.printStackTrace();
            
                        JOptionPane.showMessageDialog(ideInstance,
                                "Error opening file : "
                                + fileName
                                + ". \n Please look into Runtime log for more "
                                + "information.",
                                "Error while showing content!",
                                JOptionPane.ERROR_MESSAGE);
                    } // end try .. catch block
                } // end if
                
                RecentFilesConfiguration.getInstance().setParameter(null, 
                 new FileImplParameter(new File(fileChooser.getSelectedFile()
                                                         .getAbsolutePath())));
                ideInstance.getWorkflowBar().makeRecentMenu();
            }
        });
        
        makeZMatrix.setSelected(false);
        makeZMatrix.setMnemonic('Z');        
        makeZMatrix.setToolTipText("<html><body>" +
          "Generate Z-Matrix representation when opening the molecule file." +
          "<br>Tip: Unselect this option if the molecule is very large" +
          " like a PDB file so as to speed up opening of files.</body></html>");
        
        openInJmol.setSelected(false);
        openInJmol.setMnemonic('J');
        openInJmol.setToolTipText("<html><body>" + 
          "Open in Jmol viewer. <br>Note that " +
          "no Z-Matrix is generated if this option is selected. <br>" +
          "Also, some file formats may be unsupported in JMol.</body></html>");
        
        copyConnectivity.setSelected(true);
        copyConnectivity.setMnemonic('t');
        copyConnectivity.setToolTipText("<html><body>" + 
          "Use this in case a file containing multiple geometries is opened." +
          "<br>Warning: Use this with caution, as the geometries read from " +
          "the file may have different connectivities.");
        
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.add(fileChooser, BorderLayout.CENTER);
        
        JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionPanel.add(makeZMatrix); 
        optionPanel.add(openInJmol);
        optionPanel.add(copyConnectivity);
        
        filePanel.add(optionPanel, BorderLayout.SOUTH);
                
        fileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        fileDialog.getContentPane().add(filePanel, BorderLayout.CENTER);            
        fileDialog.pack();    
        fileDialog.setLocationRelativeTo(ideInstance);
        
        fileDialog.setVisible(true);               
    }
        
    /**
     * opens up a molecule file, a public utility only to be used by
     * event handlers
     *
     * @param fileName the file name of the molecule file
     */
    public void openMoleculeFile(String fileName) {
        openMoleculeFile(fileName, true, false);
    }
    
    /**
     * opens up a molecule file, a public utility only to be used by
     * event handlers
     *
     * @param fileName the file name of the molecule file
     * @param makeZMatrix flag indicating creation of Z-Matrix    
     */
    public void openMoleculeFile(String fileName, boolean makeZMatrix) {
        openMoleculeFile(fileName, makeZMatrix, false);
    }
    
    /**
     * opens up a molecule file, a public utility only to be used by
     * event handlers
     *
     * @param fileName the file name of the molecule file
     * @param makeZMatrix flag indicating creation of Z-Matrix
     * @param copyConnectivity flag indicating coping of connectivity matrix
     *        for the geometry
     */
    public void openMoleculeFile(String fileName, boolean makeZMatrix,
                                 boolean copyConnectivity) {
        fileName = fileName.replace('\\', '/'); // unix style separators
        
        // we use the bean shell command here!
        // for a great simplicity                
        try {   
            Utility.executeBeanShellScript("showMolecule(\"" + fileName 
                                            + "\", \"" + makeZMatrix 
                                            + "\", \"" + copyConnectivity
                                            + "\")");
        } catch (Exception ignored) {
            ideInstance.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            System.err.println(
                            "Warning! Unable to import commands : " + ignored);
            ignored.printStackTrace();
            
            JOptionPane.showMessageDialog(ideInstance,
                                "Error opening file : "
                                + fileName
                                + ". \n Please look into Runtime log for more "
                                + "information.",
                                "Error while showing content!",
                                JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }
    
    /**
     * open up an interface for modifying/ viewing atom info settings.
     *
     * @param ae the event object
     */
    public void openAtomInfoSettings(ActionEvent ae) {
        AtomInfoSettingsDialog aisDialog = 
                AtomInfoSettingsDialog.getInstance(ideInstance);
        
        aisDialog.setLocationRelativeTo(ideInstance);
        aisDialog.setModal(true);
        aisDialog.setVisible(true);
    }
    
    /**
     * for handling save molecule event
     * 
     * @param evt - the ActionEvent object!
     */
    public void saveMoleculeActionPerformed(ActionEvent evt) {
        // we use the bean shell command here!
        // for a great simplicity                                        
        try {                                                
            Utility.executeBeanShellScript(
                                    "saveCurrentMoleculeScene()"); 
            ideInstance.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ignored) {                        
            ideInstance.setCursor(Cursor.getDefaultCursor());
            System.err.println(
                "Warning! Unable to import commands : " + ignored);
                ignored.printStackTrace();

            JOptionPane.showMessageDialog(ideInstance,
                "Error while saving file. "                           
                + ". \n Please look into Runtime log for more "
                + "information.",
                "Error while saving content!",
                JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }
    
    /**
     * for handling save molecule image event
     * 
     * @param evt - the ActionEvent object!
     */
    public void saveMoleculeImageActionPerformed(ActionEvent evt) {
        // we use the bean shell command here!
        // for a great simplicity 
        try {                                                
            Utility.executeBeanShellScript(
                                    "saveCurrentSceneAsImage()"); 
            ideInstance.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ignored) {                        
            ideInstance.setCursor(Cursor.getDefaultCursor());
            System.err.println(
                "Warning! Unable to import commands : " + ignored);
                ignored.printStackTrace();

            JOptionPane.showMessageDialog(ideInstance,
                "Error while saving file. "                           
                + ". \n Please look into Runtime log for more "
                + "information.",
                "Error while saving content!",
                JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }
    
    /**
     * for handling export to RIB event
     * 
     * @param evt - the ActionEvent object!
     */
    public void exportToRIBActionPerformed(ActionEvent evt) {
        // we use the bean shell command here!
        // for a great simplicity                                       
        try {                                                
            Utility.executeBeanShellScript(
                               "exportCurrentMoleculeSceneAsRIB()");  
            ideInstance.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ignored) {                        
            ideInstance.setCursor(Cursor.getDefaultCursor());
            System.err.println(
                "Warning! Unable to import commands : " + ignored);
                ignored.printStackTrace();

            JOptionPane.showMessageDialog(ideInstance,
                "Error while saving file. "                           
                + ". \n Please look into Runtime log for more "
                + "information.",
                "Error while saving content!",
                JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }
    
    /**
     * for handling attach property event
     * 
     * @param evt - the ActionEvent object!
     */
    public void attachPropertyActionPerformed(ActionEvent evt) {
        // we use the bean shell command here!
        // for a great simplicity 
        try {                                                
            Utility.executeBeanShellScript(
                                    "attachProperty()");
        } catch (Exception ignored) {                        
            ideInstance.setCursor(Cursor.getDefaultCursor());
            System.err.println(
                "Warning! Unable to import commands : " + ignored);
                ignored.printStackTrace();

            JOptionPane.showMessageDialog(ideInstance,
                "Error while attaching property file. " 
                + ". \n Please look into Runtime log for more "
                + "information.",
                "Error while attaching property!",
                JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }
    
    /** Getter for property ideInstance.
     * @return Value of property ideInstance.
     *
     */
    public MeTA getIdeInstance() {
        return this.ideInstance;
    }
    
    /** Setter for property ideInstance.
     * @param ideInstance New value of property ideInstance.
     *
     */
    public void setIdeInstance(MeTA ideInstance) {
        this.ideInstance = ideInstance;
    }
    
} // end of class MainMenuEventHandlers
