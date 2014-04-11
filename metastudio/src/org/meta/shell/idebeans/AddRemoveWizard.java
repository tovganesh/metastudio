/*
 * AddRemoveWizard.java
 *
 * Created on July 11, 2004, 8:38 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.*;

import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import org.meta.common.Utility;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.common.resource.StringResource;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;

import org.meta.molecule.impl.MonomerRecognizer;
import org.meta.shell.ide.MeTA;
import org.meta.molecule.impl.RingRecognizer;
import org.meta.moleculereader.MoleculeFileReader;
import org.meta.moleculereader.MoleculeFileReaderFactory;
import org.meta.shell.idebeans.viewers.QuickViewer;
import org.meta.shell.idebeans.viewers.QuickViewerFactory;
import org.meta.workspace.ItemData;
import org.meta.workspace.Workspace;
import org.meta.workspace.WorkspaceItem;
import org.meta.workspace.WorkspaceItemFactory;

/**
 * Add / Remove wizard for workspace items
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AddRemoveWizard extends GenericIDEWizard {
    
    /** the instance of workspace editing that is handled here */
    private Workspace workspace;
    
    /** the IDE instance */
    private MeTA ideInstance;
    
    /** the UI components for page 1 */
    private JLabel descriptionLabel1;
    private JTable workspaceFiles;
    private JScrollPane workspaceFilesScroll;
    private JPanel stepOneButtonPanel;
    private JButton addButton, removeButton, removeAllButton, showContent;
    private JCheckBox makeZMatrix;
    
    /** the UI components for page 2 */
    private JLabel descriptionLabel2;
    private JEditorPane workspaceSummary;
    
    /** following for a list of new imports into workspace */
    private Vector<Object> newImportList;
    
    /** following for deleting items from workspace */
    private Vector<Object> removeList;
    
    /** Creates a new instance of AddRemoveWizard */
    public AddRemoveWizard(JFrame parent, Workspace theWorkspace) {                
        super(parent, "Add / Remove wizard", 2, 
             (new String[] {"Add / Remove", "Confirm"}),
             (new String[] {"Add or remove components of this workspace",  
                          "Check and confirm the changes to this workspace."}));
        
        this.workspace = theWorkspace;
        
        // similar sized?
        setSize(MiscResource.getInstance().getNewWorkspaceWizardDimension());                
        
        // init the lists
        newImportList = new Vector<Object>(20);
        removeList    = new Vector<Object>(20);
        
        // the following line is very crutial .. if the following code fails
        // the other methods may not work correctly
        try {
            ideInstance = (MeTA) parent;
        } catch (Exception e) {
            System.err.println("Warning! : NewWorkspaceWizard is not passed "
                               + "with a proper instance of MeTA : " + e);
            e.printStackTrace();
        } // end try .. catch block
        
        // init the pages
        makePageOne();
    }
    
    /**
     * set up all the wizard pages one by one
     */
    @Override
    protected void initWizardPages() {                
        super.initWizardPages();
                
        if (workspace == null) return;
        
        makePageOne();        
    }
    
    /**
     * overridden nextActionPerformed()
     */
    @Override
    protected void nextActionPerformed(ActionEvent event) {
        if (currentStep == 0) {
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           wizardPages[1].removeAll();
           makePageTwo();  // refresh the next page always! it is summary 
           setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } // end if 
        
        super.nextActionPerformed(event);
    }
    
    /**
     * setup page1 of the wizard
     */
    protected void makePageOne() {
        FontResource fonts = FontResource.getInstance();
        
        // >>>>> Page 1
        wizardPages[0].setLayout(new BorderLayout());        
        wizardPages[0].setFont(fonts.getFrameFont());
        wizardPages[0].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[0].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });
        
        //     the description label
        descriptionLabel1 = new JLabel(stepDescriptions[0], JLabel.LEFT);
        descriptionLabel1.setFont(fonts.getDescriptionFont());
        wizardPages[0].add(descriptionLabel1, BorderLayout.NORTH);       
        
        //     make the button panel
        stepOneButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add...");
        addButton.setMnemonic('A');
        addButton.setFont(fonts.getFrameFont());        
        // local event handler
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                IDEFileChooser fileChooser = new IDEFileChooser();
                
                fileChooser.setDialogTitle("Choose a file...");   
                fileChooser.setMultiSelectionEnabled(true);
                                
                // construct the file filter                
                try {
                  MoleculeFileReaderFactory mfrm = (MoleculeFileReaderFactory)
                    (Utility.getDefaultImplFor(MoleculeFileReaderFactory.class))
                            .newInstance();
                  
                  Iterator<String> supportedFileFormats 
                                            = mfrm.getAllSupportedTypes();
                  Vector<String> fileFormatList = new Vector<String>();
                  
                  while(supportedFileFormats.hasNext()) {
                      fileFormatList.add(supportedFileFormats.next());
                  } // end while
                  
                  IDEFileFilter fileFilter = 
                         new IDEFileFilter(fileFormatList.toArray(), 
                                    "All supported molecule file formats");
                  
                  // and add the file filter
                  fileChooser.addChoosableFileFilter(fileFilter);
                  // and add the iconic stuff
                  fileChooser.setFileView(new IDEFileView());
                } catch(Exception e) {
                  System.err.println("Exception : " + e.toString());
                  System.err.println("Could not make proper file filters.");
                  e.printStackTrace();
                } // end of try .. catch block
                
                if (fileChooser.showOpenDialog(AddRemoveWizard.this)
                                == IDEFileChooser.APPROVE_OPTION) {
                    // check for the validity of this file 
                    // and then proceed   
                    File [] files = fileChooser.getSelectedFiles();     
                    
                    if ((files == null) || (files.length == 0)) return;
                    
                    for(int i=0; i<files.length; i++) {
                        if (!files[i].exists()) return;

                        DefaultTableModel dtm = (DefaultTableModel)
                                                     workspaceFiles.getModel();
                        dtm.addRow(new Object[] {
                            ImageResource.getInstance()
                                     .getIconFor(files[i]),
                            files[i].getName(),
                            files[i].getAbsolutePath()
                        });
                    } // end for
                } // end if
            }
        });
        stepOneButtonPanel.add(addButton);
        
        removeButton = new JButton("Remove");
        removeButton.setMnemonic('R');
        removeButton.setFont(fonts.getFrameFont());
        removeButton.setEnabled(false);
        //    local listeners
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (workspaceFiles.getSelectedRow() >= 0) {
                    DefaultTableModel tm = 
                                (DefaultTableModel) workspaceFiles.getModel();
                    
                    tm.removeRow(workspaceFiles.getSelectedRow());
                    
                    if (tm.getRowCount() == 0) removeButton.setEnabled(false);
                } // end if
            } 
        });
        stepOneButtonPanel.add(removeButton);
        
        removeAllButton = new JButton("Remove all");
        removeAllButton.setMnemonic('l');
        removeAllButton.setFont(fonts.getFrameFont());
        //    local listeners
        removeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DefaultTableModel tm = 
                                 (DefaultTableModel) workspaceFiles.getModel();
                int noOfRows = tm.getRowCount();
                
                // remove all data!
                for(int i=0; i<noOfRows; i++) tm.removeRow(0);                
            }
        });
        stepOneButtonPanel.add(removeAllButton);
        
        showContent = new JButton("Show content...");
        showContent.setMnemonic('S');
        showContent.setFont(fonts.getFrameFont());
        showContent.setEnabled(false);
        //    local listeners
        showContent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (workspaceFiles.getSelectedRow() >= 0) {
                  try {
                    QuickViewerFactory qvf = (QuickViewerFactory)
                          (Utility.getDefaultImplFor(QuickViewerFactory.class))
                                  .newInstance();
                    QuickViewer qv = qvf.getViewer("text/plain");
                    
                    DefaultTableModel dtm = (DefaultTableModel)
                                                    workspaceFiles.getModel();
                    
                    try {
                        setCursor(
                           Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        
                        // show the stuff
                        qv.showFile((String)
                           dtm.getValueAt(workspaceFiles.getSelectedRow(), 2));
                        
                        setCursor(
                           Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    } catch (IOException ioe) {
                        setCursor(
                           Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        
                        System.err.println("Error opening file"
                                           + " : " + ioe.toString());
                        ioe.printStackTrace();
                        
                        JOptionPane.showMessageDialog(AddRemoveWizard.this, 
                          "Error opening file : " + ioe.toString(), 
                          "Error while showing content!",
                          JOptionPane.ERROR_MESSAGE);                        
                    } // end of try .. catch block
                  } catch(Exception e) {
                    System.err.println("Exception : " + e.toString());
                    System.err.println("Could not initilize quick viewer.");
                    e.printStackTrace();
                  } // end of try .. catch block
                }
            }
        });
        stepOneButtonPanel.add(showContent);
        
        makeZMatrix = new JCheckBox("Make Z-Matrix");
        makeZMatrix.setSelected(true);
        makeZMatrix.setToolTipText("Select to make Z-Matrix for all the " +
                                   "imported molecule files files.");
        stepOneButtonPanel.add(makeZMatrix);        
        
        wizardPages[0].add(stepOneButtonPanel, BorderLayout.SOUTH);
        
        //     next setup the table
        workspaceFiles = new JTable(new DefaultTableModel(
                new Object [][] { },
                new String [] {
                    "@", "File Name", "Absolute Path"
                }
            ) {
            Class[] types = new Class [] {
                ImageIcon.class, String.class, String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        workspaceFiles.setShowGrid(false);  // do not show the grid
        
        // set column width
        workspaceFiles.getColumn("@").setMaxWidth(20);
        workspaceFiles.sizeColumnsToFit(0);
        //   local listener
        workspaceFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (workspaceFiles.getSelectedRow() >= 0) {
                    removeButton.setEnabled(true);
                    showContent.setEnabled(true);
                } else {
                    removeButton.setEnabled(false);
                    showContent.setEnabled(false);
                } // end if
            }
        });
        workspaceFiles.getModel().addTableModelListener(
                                     new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {                                
                if ((workspaceFiles.getSelectedRow() >= 0) 
                    && (workspaceFiles.getRowCount() != 0)) {
                    removeButton.setEnabled(true);
                    showContent.setEnabled(true);
                } else {
                    removeButton.setEnabled(false);
                    showContent.setEnabled(false);
                } // end if
            }
        });                       
        
        // fill up the workspace
        fillWorkspaceFiles();
        
        workspaceFilesScroll = new JScrollPane(workspaceFiles);
        wizardPages[0].add(workspaceFilesScroll, BorderLayout.CENTER);                
    }
    
    /**
     *  fill up the workspace files area, with the components already 
     *  present
     */
    private void fillWorkspaceFiles() {
        DefaultTableModel dtm = (DefaultTableModel) workspaceFiles.getModel();
        
        Iterator workspaceItems = workspace.getWorkspaceItems().iterator();
        WorkspaceItem workspaceItem;
        File workspaceFile;
        
        while(workspaceItems.hasNext()) {
            workspaceItem = (WorkspaceItem) workspaceItems.next();
            
            workspaceFile = new File(workspaceItem.getWorkspaceItemFile());
            
            dtm.addRow(new Object[] {
                ImageResource.getInstance().getIconFor(workspaceFile),
                             workspaceFile.getName(),
                             workspaceFile.getAbsolutePath()
            });
        } // end while
    }
    
    /**
     * setup page1 of the wizard
     */
    protected void makePageTwo() {
        // need to update the UI here
        FontResource fonts = FontResource.getInstance();
        
        // >>>>> Page 2
        wizardPages[1].setLayout(new BorderLayout());        
        wizardPages[1].setFont(fonts.getFrameFont());
        wizardPages[1].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[1].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });
        
        //     the description label
        descriptionLabel2 = new JLabel(stepDescriptions[1], JLabel.LEFT);
        descriptionLabel2.setFont(fonts.getDescriptionFont());
        wizardPages[1].add(descriptionLabel2, BorderLayout.NORTH);
       
        //     the summary
        workspaceSummary = new JEditorPane("text/html", "");
        workspaceSummary.setEditable(false);
        workspaceSummary.setText(getSummary());
        wizardPages[1].add(new JScrollPane(workspaceSummary), 
                           BorderLayout.CENTER);
    }
    
    /**
     * private method for summarizing the stuff
     */
    private String getSummary() {
        StringBuffer sb = new StringBuffer();
        StringResource strings = StringResource.getInstance();
        
        sb.append("<u><b>Files objects already in workspace:</b></u>");
        Iterator workspaceItems = workspace.getWorkspaceItems().iterator();
        WorkspaceItem workspaceItem;
        File workspaceFile;
                
        DefaultTableModel tm = (DefaultTableModel) workspaceFiles.getModel();
        
        if (!workspaceItems.hasNext()) { 
            sb.append("<br><i>None.</i>");
        } else {
            sb.append("<ol>");
            
            Vector newList = tm.getDataVector();           
            theWhileLoop: while(workspaceItems.hasNext()) {
                workspaceItem = (WorkspaceItem) workspaceItems.next();
                workspaceFile = new File(workspaceItem.getWorkspaceItemFile());
                
                sb.append("<li>" + workspaceFile.getName() 
                         + " (" + workspaceFile.getAbsolutePath() + ") </li>" );
                
                for(int k=0; k<newList.size(); k++) {
                    if (((String) ((Vector) newList.get(k)).get(2))
                                .equals(workspaceItem.getWorkspaceItemFile())) {
                       continue theWhileLoop;
                    } // end if
                } // end for
                
                // only if we reach here, we need to remove the stuff
                removeList.add(workspaceItem.getWorkspaceItemFile());        
            } // end while
            sb.append("</ol>");
        } // end if         
        
        sb.append("<br><u><b>Files to be imported into Workspace:</b></u>");
        
        if (tm.getRowCount() == 0) {
            sb.append("<br><i>None.</i>");
        } else {
            sb.append("<ol>");
            for(int i=0; i<tm.getRowCount(); i++) {     
                // workspace item files are not supposed to be added!
                // thus there is rt. now no provision to import other
                // workspace component into the current workspace
                if (!((String) tm.getValueAt(i, 1)).endsWith(
                              strings.getWorkspaceItemFileExtension())) {
                    sb.append("<li>" + tm.getValueAt(i, 1) 
                              + " (" + tm.getValueAt(i, 2) + ") </li>" );
                    
                    newImportList.add(tm.getValueAt(i, 2));
                } // end if
            } // end for
            sb.append("</ol>");
        } // end if
        
        if (newImportList.size() == 0) {
            sb.append("<br><i>None.</i>");
        } // end if
        
        sb.append("<br><u><b>Files to be removed from Workspace:</b></u>");
        
        if (removeList.size() == 0) {
            sb.append("<br><i>None.</i>");
        } else {
            sb.append("<ol>");
            for(int i=0; i<removeList.size(); i++) {
                sb.append("<li>" + removeList.get(i) + " </li>" );
            } // end for
            sb.append("</ol>");
        } // end if
        
        return sb.toString();
    }
    
    /**
     * overridden finishActionPerformed()
     */
    @Override
    protected void finishActionPerformed(ActionEvent event) {        
        super.finishActionPerformed(event);                               
        
        // if in initial step, ensure that we have consistant data!
        if (currentStep == 0) {
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           wizardPages[1].removeAll();
           makePageTwo();  // refresh the next page always! it is summary 
           setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } // end if 
        
        StringResource strings = StringResource.getInstance();
        
        // add any workspace items requested
        DefaultTableModel tm = (DefaultTableModel) workspaceFiles.getModel();
        
        if (newImportList.size() != 0) {
            // we only support molecule files at present
            // .. all other files will be just copied .. no processing
            // performed on these.
            try {
                if (ideInstance != null) {
                    ideInstance.setCursor(
                       Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                } // end if
                
                for(int i=0; i<tm.getRowCount(); i++) {                
                    String fileName = (String) tm.getValueAt(i, 2);
                
                    // is not in new list, skip
                    if (!newImportList.contains(fileName)) {
                        continue;
                    } // end if

                    if (Utility.isSupportedType(fileName)) {
                        // get the reader
                        System.out.println("Adding to project : " + fileName);
                        MoleculeFileReader mr = Utility.getReaderFor(fileName);
                        Molecule molecule     = mr.readMoleculeFile(fileName);
                        
                        // add special structure recognizers
                        molecule.addSpecialStructureRecognizer(
                                                    new RingRecognizer());
                        molecule.addSpecialStructureRecognizer(
                                                    new MonomerRecognizer());
                        
                        // get builder
                        MoleculeBuilder mb = (MoleculeBuilder) 
                          Utility.getDefaultImplFor(
                                     MoleculeBuilder.class).newInstance();
                                                
                        // make the connectivity
                        if (makeZMatrix.isSelected()) mb.makeZMatrix(molecule);
                        else mb.makeConnectivity(molecule);
                        
                        // identify special structures
                        mb.identifySpecialStructures(molecule);
                        
                        // generate a default fragmentation scheme 
                        molecule.getDefaultFragmentationScheme();
                        
                        // make the workspace item
                        String itemName = (String) tm.getValueAt(i, 1);
                        
                        itemName = itemName.substring(0, 
                                             itemName.lastIndexOf('.'));
                                                
                        WorkspaceItemFactory wif = (WorkspaceItemFactory)
                           Utility.getDefaultImplFor(
                                     WorkspaceItemFactory.class).newInstance();
                        Class implClz = wif.getItemClass("workspace/molecule");
                        Constructor theConstructor = 
                         implClz.getDeclaredConstructor(
                                    new Class[]{String.class});
                                    
                        WorkspaceItem wi = (WorkspaceItem) 
                          theConstructor.newInstance(
                              workspace.getWorkspaceDirectory() 
                              + File.separatorChar + itemName
                              + "." + strings.getWorkspaceItemFileExtension());
                        wi.setItemData(new ItemData(molecule));
                        
                        // add workspace item to the workspace
                        workspace.addWorkspaceItem(wi);

                        System.out.println("... done with adding.");
                    } else {
                        // TODO: files not supported by IDE
                    } // end if
                } // end for   
                
                if (ideInstance != null) {
                    ideInstance.setCursor(
                       Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } // end if
            } catch (Exception e) {
                System.err.println("ERROR: Workspace modification failed!");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                           "ERROR: Workspace modification failed! \n"
                           + strings.getStdErrorMessage(), 
                           "Error",
                           JOptionPane.ERROR_MESSAGE);                            
                dispose();
                return;    
            } // end try .. catch bloxk                     
        } // end if                        
        
        // and remove the items that are supposed to be removed
        if (removeList.size() != 0) {
            try {
                Iterator removeItem = removeList.iterator();
                
                while(removeItem.hasNext()) {
                    String path = (String) removeItem.next();
                    
                    if (!workspace.removeWorkspaceItem(path)) {
                        throw new Exception("Unable to remove item : " + path);
                    } // end if
                } // end while
            } catch (Exception e) {
                System.err.println("ERROR: Workspace modification failed!");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                                "ERROR: Workspace modification failed! \n"
                                + strings.getStdErrorMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            } // end try .. catch block
        } // end if
        
        // and finally save the workspace
        try {            
            workspace.save();
        } catch (Exception e) {
            System.err.println("ERROR: Workspace modification failed!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                        "ERROR: Workspace modification failed! \n"
                        + strings.getStdErrorMessage(), 
                        "Error",
                        JOptionPane.ERROR_MESSAGE);                        
            dispose();
            return;
        } // end try .. catch block
        
        System.out.println("... done modifying workspace.");
        
        // initiate UI updtions...
        // TODO: this is a hack, and need to write a better mechanism
        System.out.println("Updating the UI...");
        ideInstance.setCurrentWorkspace(workspace);
        
        // finally dispose this dialog
        dispose();
    }
} // end of class AddRemoveWizard
