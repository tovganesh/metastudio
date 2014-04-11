/*
 * NewWorkspaceWizard.java
 *
 * Created on July 28, 2003, 10:38 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.meta.common.Utility;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.common.resource.StringResource;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.molecule.impl.MonomerRecognizer;
import org.meta.molecule.impl.RingRecognizer;
import org.meta.moleculereader.MoleculeFileReader;
import org.meta.moleculereader.MoleculeFileReaderFactory;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.viewers.QuickViewer;
import org.meta.shell.idebeans.viewers.QuickViewerFactory;
import org.meta.workspace.ItemData;
import org.meta.workspace.Workspace;
import org.meta.workspace.WorkspaceItem;
import org.meta.workspace.WorkspaceItemFactory;

/**
 * The wizard to guide you through creation of a new workspace for the IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NewWorkspaceWizard extends GenericIDEWizard {
    
    /** the UI components for page 1 */
    private JLabel workspaceNameLabel, locationLabel;
    private JLabel descriptionLabel1;
    private JTextField workspaceName, location;
    private JButton browse;    
    private String defaultLocation;
    
    /** the UI components for page 2 */
    private JLabel workspaceAuthorLabel, workspaceVersionLabel,
                   workspaceDescriptionLabel;
    private JTextField workspaceAuthor, workspaceVersion;
    private JTextArea workspaceDescription;
    private JLabel descriptionLabel2;
    
    /** the UI components for page 3 */
    private JLabel descriptionLabel3;
    private JTable workspaceFiles;
    private JScrollPane workspaceFilesScroll;
    private JPanel stepTwoButtonPanel;
    private JButton addButton, removeButton, removeAllButton, showContent;
    private JCheckBox makeZMatrix;
    
    /** the UI components for page 4 */
    private JLabel descriptionLabel4;
    private JEditorPane workspaceSummary;
    
    /** the IDE instance */
    private MeTA ideInstance;
    
    /** Creates a new instance of NewWorkspaceWizard */
    public NewWorkspaceWizard(JFrame parent) {
        super(parent, "New workspace wizard", 4, 
             (new String[] {"Create", "Describe", "Import", "Confirm"}),
             (new String[] {"Create a new workspace. Specify name, path etc.", 
                            "Give name and describe the purpose of this "
                            + "workspace.", 
                            "Import molecule or any related file(s) etc.",
                            "Check and confirm creation of this workspace."}));
                                    
        setSize(MiscResource.getInstance().getNewWorkspaceWizardDimension());
        
        // the following line is very crutial .. if the following code fails
        // the other methods may not work correctly
        try {
            ideInstance = (MeTA) parent;
        } catch (Exception e) {
            System.err.println("Warning! : NewWorkspaceWizard is not passed "
                               + "with a proper instance of MeTA : " + e);
            e.printStackTrace();
        } // end try .. catch block
    }
    
    /**
     * set up all the wizard pages one by one
     */
    @Override
    protected void initWizardPages() {
        super.initWizardPages();
                
        makePageOne();
        makePageTwo();
        makePageThree();
        
        // finally set the focus on workspaceName of Page1
        workspaceName.grabFocus();
    }
    
    /**
     * setup page1 of the wizard
     */
    protected void makePageOne() {
        FontResource fonts = FontResource.getInstance();
        defaultLocation    = System.getProperty("user.home");
        
        // grid bag constraints
        GridBagConstraints gbc = new GridBagConstraints();        
        gbc.insets = new Insets(2, 2, 2, 0);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill   = GridBagConstraints.BOTH;
        
        // >>>>> Page 1
        wizardPages[0].setLayout(new GridBagLayout());        
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
        
        // the following two buttons are disabled in this page at the 
        // beginning:
        next.setEnabled(false);
        finish.setEnabled(false);

        //    workspace ...
        workspaceNameLabel = new JLabel("Workspace Name:", JLabel.RIGHT);
        workspaceNameLabel.setFont(fonts.getFrameFont());
        workspaceNameLabel.setDisplayedMnemonic('W');
        gbc.gridx = 0;
        gbc.gridy = 0;        
        wizardPages[0].add(workspaceNameLabel, gbc);
        
        workspaceName = new JTextField(30);     
        workspaceName.setFont(fonts.getFrameFont());
        // local event handler                  
        workspaceName.getDocument().addDocumentListener(new DocumentListener() {
            // the change update
            @Override
            public void changedUpdate(DocumentEvent event) { 
                updateLocationField(event);
            }
            
            // the insert event
            @Override
            public void insertUpdate(DocumentEvent event) { 
                updateLocationField(event);
            }
            
            // the remove event
            @Override
            public void removeUpdate(DocumentEvent event) { 
                updateLocationField(event);
            }
            
            // what ever happens update the location field.
            public void updateLocationField(DocumentEvent event) {
                try {                   
                    String path = defaultLocation;
                    
                    if (!path.endsWith(File.separator)) {
                        path += File.separator;
                    } // end if
                    
                    Document doc = workspaceName.getDocument();
                    File wsd = new File(path + doc.getText(0, doc.getLength()));
                    boolean validName = wsd.mkdir();
                    
                    // set the correct status of the navigation buttons
                    if ((doc.getLength() == 0) || (!validName)) {
                       next.setEnabled(false);                        
                       finish.setEnabled(false);
                       // show the error
                       descriptionLabel1.setForeground(Color.red);
                       descriptionLabel1.setText("Invalid name or path."
                                                 + " Please Correct!");
                    } else {
                       next.setEnabled(true);                        
                       finish.setEnabled(true);
                       // update location field
                       location.setText(path + doc.getText(0, doc.getLength()));
                       wsd.delete(); // delete temporaty file object
                       // update display .. the normal one
                       descriptionLabel1.setForeground(Color.black);
                       descriptionLabel1.setText(stepDescriptions[0]);
                    } // end if                                                            
                } catch (BadLocationException ignored) { } // shd never occur
            }
        }); // documentListener
        gbc.gridx = 1;
        wizardPages[0].add(workspaceName, gbc);
        workspaceNameLabel.setLabelFor(workspaceName);
        
        //    location ...
        locationLabel = new JLabel("Location:", JLabel.RIGHT);
        locationLabel.setFont(fonts.getFrameFont());
        locationLabel.setDisplayedMnemonic('L');
        gbc.gridx  = 0;
        gbc.gridy  = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        wizardPages[0].add(locationLabel, gbc);
        
        gbc.anchor = GridBagConstraints.SOUTH;
        location   = new JTextField(30); 
        location.setFont(fonts.getFrameFont());
        location.setText(defaultLocation);    
        location.setEditable(false);
        gbc.gridx = 1;
        wizardPages[0].add(location, gbc);
        locationLabel.setLabelFor(location);
        
        browse = new JButton("Change Location...");
        browse.setFont(fonts.getFrameFont());
        browse.setMnemonic('g');     
        // local event handler for browse button
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                IDEFileChooser locationChooser = 
                                     new IDEFileChooser(defaultLocation);
                locationChooser.setFileSelectionMode(
                                     IDEFileChooser.DIRECTORIES_ONLY);
                locationChooser.setDialogTitle("Choose a location...");                
                
                if (locationChooser.showOpenDialog(NewWorkspaceWizard.this)
                        == IDEFileChooser.APPROVE_OPTION) {
                    // check for the validity of this directory 
                    // and then proceed                    
                    if (!locationChooser.getSelectedFile().exists()) return;
                    
                    // update the default location string
                    defaultLocation = locationChooser.getSelectedFile().
                                                         getAbsolutePath();                    
                    String path = defaultLocation;
                    
                    if (!path.endsWith(File.separator)) {
                        path += File.separator;
                    } // end if
                    
                    // and up date the location textbox
                    location.setText(path + workspaceName.getText());
                } // end if
            }
        });        
        gbc.gridx  = 1;
        gbc.gridy  = 2;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        wizardPages[0].add(browse, gbc);
        
        //      and finally add a label for description
        descriptionLabel1 = new JLabel(stepDescriptions[0], JLabel.LEFT);
        descriptionLabel1.setFont(fonts.getDescriptionFont());
        gbc.gridx      = 0;
        gbc.gridy      = 3;
        gbc.gridwidth  = 2;
        gbc.gridheight = 2;
        gbc.fill       = GridBagConstraints.BOTH;
        gbc.anchor     = GridBagConstraints.SOUTH;
        wizardPages[0].add(descriptionLabel1, gbc);
    }
    
    /**
     * setup page3 of the wizard
     */
    protected void makePageTwo() {
        FontResource fonts = FontResource.getInstance();
        
        // >>>>> Page 2
        GridBagConstraints gbc = new GridBagConstraints();        
        gbc.insets = new Insets(2, 2, 2, 0);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill   = GridBagConstraints.BOTH;
        
        wizardPages[1].setLayout(new GridBagLayout());        
        wizardPages[1].setFont(fonts.getFrameFont());
        wizardPages[1].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[1].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel) e.getSource()).scrollRectToVisible(r);
            }
        });
        
        // add the UI
        //      description label
        descriptionLabel2 = new JLabel(stepDescriptions[1], JLabel.LEFT);
        descriptionLabel2.setFont(fonts.getDescriptionFont());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth  = 2;        
        gbc.gridheight = 2;  
        wizardPages[1].add(descriptionLabel2, gbc);
        gbc.gridwidth  = 1;  
        gbc.gridheight = 1;  

        //    author
        workspaceAuthorLabel = new JLabel("Author : ", JLabel.RIGHT);
        workspaceAuthorLabel.setDisplayedMnemonic('A');
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        wizardPages[1].add(workspaceAuthorLabel, gbc);
        workspaceAuthor = new JTextField(30);        
        workspaceAuthor.setText(System.getProperty("user.name"));
        gbc.gridx = 1;        
        gbc.anchor = GridBagConstraints.SOUTH;
        wizardPages[1].add(workspaceAuthor, gbc);
        workspaceAuthorLabel.setLabelFor(workspaceAuthor);
        
        //    version
        workspaceVersionLabel = new JLabel("Version : ", JLabel.RIGHT);
        workspaceVersionLabel.setDisplayedMnemonic('V');
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        wizardPages[1].add(workspaceVersionLabel, gbc);
        workspaceVersion = new JTextField(30);        
        workspaceVersion.setText("1.0");
        gbc.gridx = 1;        
        gbc.anchor = GridBagConstraints.SOUTH;
        wizardPages[1].add(workspaceVersion, gbc);
        workspaceVersionLabel.setLabelFor(workspaceVersion);
        
        //   description
        workspaceDescriptionLabel = new JLabel("Description : ", JLabel.RIGHT);
        workspaceDescriptionLabel.setDisplayedMnemonic('D');
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        wizardPages[1].add(workspaceDescriptionLabel, gbc);
        workspaceDescription = new JTextArea(5, 30);
        workspaceDescription.setText("No description available");
        gbc.gridx = 1;        
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill   = GridBagConstraints.BOTH;
        wizardPages[1].add(new JScrollPane(workspaceDescription), gbc);
        workspaceDescriptionLabel.setLabelFor(workspaceDescription);
    }
    
    /**
     * setup page3 of the wizard
     */
    protected void makePageThree() {
        FontResource fonts = FontResource.getInstance();
        
        // >>>>> Page 3
        wizardPages[2].setLayout(new BorderLayout());        
        wizardPages[2].setFont(fonts.getFrameFont());
        wizardPages[2].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[2].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });
        
        //     the description label
        descriptionLabel3 = new JLabel(stepDescriptions[2], JLabel.LEFT);
        descriptionLabel3.setFont(fonts.getDescriptionFont());
        wizardPages[2].add(descriptionLabel3, BorderLayout.NORTH);
        
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
                
        workspaceFilesScroll = new JScrollPane(workspaceFiles);
        wizardPages[2].add(workspaceFilesScroll, BorderLayout.CENTER);
        
        //     make the button panel
        stepTwoButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add...");
        addButton.setMnemonic('A');
        addButton.setFont(fonts.getFrameFont());        
        // local event handler
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                IDEFileChooser fileChooser = new IDEFileChooser(defaultLocation);
                
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
                                
                if (fileChooser.showOpenDialog(NewWorkspaceWizard.this)
                                == IDEFileChooser.APPROVE_OPTION) {
                    // check for the validity of this file 
                    // and then proceed   
                    File [] files = fileChooser.getSelectedFiles();     
                    
                    if ((files == null) || (files.length == 0)) return;
                    
                    // update the default location string
                    defaultLocation = fileChooser.getSelectedFile().
                                                     getAbsolutePath();

                    DefaultTableModel dtm = (DefaultTableModel)
                                                    workspaceFiles.getModel();
                     
                    for(int i=0; i<files.length; i++) {
                        if (!files[i].exists()) return;
                       
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
        stepTwoButtonPanel.add(addButton);
        
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
        stepTwoButtonPanel.add(removeButton);
        
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
        stepTwoButtonPanel.add(removeAllButton);
        
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
                        
                        JOptionPane.showMessageDialog(NewWorkspaceWizard.this, 
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
        stepTwoButtonPanel.add(showContent);
        
        makeZMatrix = new JCheckBox("Make Z-Matrix");
        makeZMatrix.setSelected(true);
        makeZMatrix.setToolTipText("Select to make Z-Matrix for all the " +
                                   "imported molecule files files.");
        stepTwoButtonPanel.add(makeZMatrix);
        wizardPages[2].add(stepTwoButtonPanel, BorderLayout.SOUTH);
    }        
    
    /**
     * setup page3 of the wizard
     */
    protected void makePageFour() {
        // need to update the UI here
        FontResource fonts = FontResource.getInstance();
        
        // >>>>> Page 4
        wizardPages[3].setLayout(new BorderLayout());        
        wizardPages[3].setFont(fonts.getFrameFont());
        wizardPages[3].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[3].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });
        
        //     the description label
        descriptionLabel4 = new JLabel(stepDescriptions[3], JLabel.LEFT);
        descriptionLabel4.setFont(fonts.getDescriptionFont());
        wizardPages[3].add(descriptionLabel4, BorderLayout.NORTH);
        
        //     the summary
        workspaceSummary = new JEditorPane("text/html", "");
        workspaceSummary.setEditable(false);
        workspaceSummary.setText(getSummary());
        wizardPages[3].add(new JScrollPane(workspaceSummary), 
                           BorderLayout.CENTER);
    }
    
    /**
     * overridden nextActionPerformed()
     */
    @Override
    protected void nextActionPerformed(ActionEvent event) {
        if (currentStep == 2) {
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           wizardPages[3].removeAll();
           makePageFour();  // refresh the next page always! it is summary 
           setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } // end if 
        
        super.nextActionPerformed(event);
    }
    
    /**
     * private method for summarizing the stuff
     */
    private String getSummary() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<u><b>Workspace Summary:</b></u>");
        sb.append("<table border=0>");
        sb.append("<tr><td align=right><i>Name :</i></td><td>" 
                  + workspaceName.getText() + "</td>");
        sb.append("<tr><td align=right><i>Author :</i></td><td>" 
                  + workspaceAuthor.getText() + "</td>");
        sb.append("<tr><td align=right><i>Version :</i></td><td>" 
                  + workspaceVersion.getText() + "</td>");
        sb.append("<tr><td align=right><i>Description :</i></td><td>" 
                  + workspaceDescription.getText() + "</td>");
        sb.append("<tr><td align=right><i>Location :</i></td><td>" 
                  + location.getText() + "</td>");
        sb.append("</table>");
        
        sb.append("<br><u><b>Files to be imported into Workspace:</b></u>");
        
        DefaultTableModel tm = (DefaultTableModel) workspaceFiles.getModel();
        
        if (tm.getRowCount() == 0) {
            sb.append("<br><i>None.</i>");
        } else {
            sb.append("<ol>");
            for(int i=0; i<tm.getRowCount(); i++) {
                sb.append("<li>" + tm.getValueAt(i, 1) 
                          + " (" + tm.getValueAt(i, 2) + ") </li>" );
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
        
        // make the workspace and log every step
        System.out.println("Creating workspace...");
        
        File wsDir = new File(location.getText());
        StringResource strings = StringResource.getInstance();                
        
        // now create the workspace
        Workspace newWS;
        
        try  {
            Class implClz = Utility.getDefaultImplFor(Workspace.class);
            Constructor theConstructor = implClz.getDeclaredConstructor(
                                                 new Class[]{String.class});
            
            newWS = (Workspace) theConstructor.newInstance(
                                (wsDir.getAbsolutePath()
                                + File.separatorChar + workspaceName.getText()
                                + "." + strings.getWorkspaceFileExtension()));
        } catch (Exception e) {
            System.err.println("ERROR: Workspace creation failed!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "ERROR: Workspace creation failed! \n"
                    + strings.getStdErrorMessage(),
                    "Error",
                JOptionPane.ERROR_MESSAGE);
            
            dispose();
            return;
        } // end try .. catch bloxk
        
        // configure the workspace
        newWS.setAuthor(workspaceAuthor.getText());
        newWS.setDescription(workspaceDescription.getText());
        newWS.setVersion(workspaceVersion.getText());
        newWS.setInternalName(workspaceName.getText());
        newWS.setWorkspaceDirectory(wsDir.getAbsolutePath());
        
        // add any workspace items requested
        DefaultTableModel tm = (DefaultTableModel) workspaceFiles.getModel();
        
        if (tm.getRowCount() != 0) {
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
                        
                        // make sure that a default fragmentation 
                        // scheme is ready
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
                          theConstructor.newInstance(itemName
                              + "." + strings.getWorkspaceItemFileExtension());
                        wi.setItemData(new ItemData(molecule));
                        wi.setBaseDirectory(wsDir.getAbsolutePath());
                        
                        // add workspace item to the workspace
                        newWS.addWorkspaceItem(wi);

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
                System.err.println("ERROR: Workspace creation failed!");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                           "ERROR: Workspace creation failed! \n"
                           + strings.getStdErrorMessage(), 
                           "Error",
                           JOptionPane.ERROR_MESSAGE);                            
                dispose();
                return;    
            } // end try .. catch bloxk                     
        } // end if
                
        // make the directory for the new workspace
        if (!wsDir.mkdir()) {
            System.err.println("ERROR: Workspace creation failed!");
            JOptionPane.showMessageDialog(this, 
                        "ERROR: Workspace creation failed! \n"
                        + strings.getStdErrorMessage(), 
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        } // end if
        
        // and finally save the workspace
        try {            
            newWS.save();
        } catch (Exception e) {
            System.err.println("ERROR: Workspace creation failed!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                        "ERROR: Workspace creation failed! \n"
                        + strings.getStdErrorMessage(), 
                        "Error",
                        JOptionPane.ERROR_MESSAGE);                        
            dispose();
            return;
        } // end try .. catch block
        
        System.out.println("... done creating workspace.");
        
        // initiate UI updtions...
        System.out.println("Updating the UI...");
        
        if (ideInstance != null) {
            ideInstance.setCurrentWorkspace(newWS);
        } // end if
        
        // finally dispose this dialog
        dispose();
    }
    
} // end of class NewWorkspaceWizard
