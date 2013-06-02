/*
 * FilePanel.java
 *
 * Created on November 11, 2003, 6:57 AM
 */

package org.meta.shell.idebeans;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import org.meta.common.Utility;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;
import org.meta.moleculereader.MoleculeFileReaderFactory;

import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;

/**
 * The file panel UI ... handling file system in the MeTA Studio.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FilePanel extends JPanel {
    
    private JTree fileSystems;
    private DefaultMutableTreeNode dirTree;
    
    /** File extension to JPopupMenu association */
    private Hashtable<String, FilePopup> popupAssociation;
    
    private FilePopup moleculeMenu, workspaceMenu, scriptMenu;
    
    private JMenuItem openMolecule, openWorkspace, openScript;
    
    private MeTA ideInstance;
    
    /** Creates a new instance of FilePanel */
    public FilePanel(MeTA ideInstance) {
        this.ideInstance = ideInstance;
        
        initComponents();
        
        // set up the default popup associations
        popupAssociation = new Hashtable<String, FilePopup>(10);
        makePopups();     
        
        fileSystems.setDragEnabled(true);
    }
    
    /**
     * method to make the popups
     */
    private void makePopups() {
	StringResource strings = StringResource.getInstance();

        // the workspaceMenu
        workspaceMenu = new FilePopup("Workspace Menu");
        
        openWorkspace = new JMenuItem("Open workspace");
        openWorkspace.setMnemonic('w');
        openWorkspace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                    .openWorkspace(
                         workspaceMenu.getSourceFileItem().getAbsolutePath());
            }
        });
        workspaceMenu.add(openWorkspace);
        popupAssociation.put(strings.getWorkspaceFileExtension(), 
                             workspaceMenu);
        
        // the moleculeMenu
        moleculeMenu = new FilePopup("Molecule Menu");
        
        openMolecule = new JMenuItem("Open molecule");
        openMolecule.setMnemonic('m');
        openMolecule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                    .openMoleculeFile(
                         moleculeMenu.getSourceFileItem().getAbsolutePath());
            }
        });
        moleculeMenu.add(openMolecule);
        
        // add this menu to all the supported molecule file formats
        // construct the file filter
        try {
            MoleculeFileReaderFactory mfrm =
                        (MoleculeFileReaderFactory) Utility.getDefaultImplFor(
                                MoleculeFileReaderFactory.class).newInstance();
            
            Iterator<String> supportedFileFormats = mfrm.getAllSupportedTypes();
            
            while(supportedFileFormats.hasNext()) {
                popupAssociation.put(supportedFileFormats.next(), 
                                     moleculeMenu);
            } // end while
        } catch (Exception ignored) {
            // should never come here
            System.err.println(
                      "Warning! Unable to make menu associations : " + ignored);
            ignored.printStackTrace();
        } // end try .. catch block
        
        // script menu
        scriptMenu = new FilePopup("Beanshell Script Menu");
        
        openScript = new JMenuItem("Open beanshell script");
        openScript.setMnemonic('b');
        openScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // we use the bean shell command here!
                // for a great simplicity
                try {                                        
                    ideInstance.setCursor(
                       Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    Utility.executeBeanShellScript("edit(\""
                      + scriptMenu.getSourceFileItem()
                                    .getAbsolutePath().replace('\\', '/') 
                      + "\", \"false\")");
                    
                    ideInstance.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ignored) {
                    ideInstance.setCursor(Cursor.getDefaultCursor());
                    System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                    ignored.printStackTrace();
                    
                    JOptionPane.showMessageDialog(ideInstance,
                          "Error while opening file. "
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while opening file!",
                          JOptionPane.ERROR_MESSAGE);
                } // end try .. catch block
            }
        });
        scriptMenu.add(openScript);        
        popupAssociation.put(strings.getBeanShellScriptExtension(), scriptMenu);
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // make the initial tree ... as a separate thread
        dirTree = new DefaultMutableTreeNode("Filesystems");
        Thread treeMaker = new Thread() {
          @Override
          public void run() {            
            DefaultMutableTreeNode loading = 
                                   new DefaultMutableTreeNode("Loading...");
            
            DefaultMutableTreeNode userTree = 
                                   new DefaultMutableTreeNode("User home");
            
            dirTree.add(loading); // indicate user that some thing is on ..
            fileSystems = new JTree(dirTree);
            fileSystems.setCellRenderer(new TreeCellRenderer());        
        
            add(new JScrollPane(fileSystems), BorderLayout.CENTER);
            
            // make the directory structure            
            createNodes(userTree, System.getProperty("user.home"));
            dirTree.remove(loading);
            dirTree.add(userTree);            
            
            // over ! we can display the stuff
            fileSystems.setModel(new DefaultTreeModel(dirTree));
            
            fileSystems.addTreeWillExpandListener(new TreeWillExpandListener() {
                @Override
                public void treeWillExpand(TreeExpansionEvent event) 
                                           throws ExpandVetoException {
                    TreePath treePath = event.getPath();
                    
                    final DefaultMutableTreeNode node = 
                       (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    
                    if (node.getUserObject() instanceof TreeNodeModal) {
                        final TreeNodeModal theFile = 
                                  (TreeNodeModal) node.getUserObject();
                            
                        if (theFile.isDirectory()) {
                            Thread nodeThread = new Thread() {
                                @Override
                                public void run() {
                                    fileSystems.setCursor(
                                            Cursor.getPredefinedCursor(
                                                      Cursor.WAIT_CURSOR));
                                    node.removeAllChildren();
                                    createNodes(node, 
                                        theFile.getFile().getAbsolutePath());
                                    fileSystems.setCursor(
                                            Cursor.getDefaultCursor());
                                }
                            };
                            
                            nodeThread.setPriority(Thread.MIN_PRIORITY);
                            nodeThread.setName("File panel Thread");
                            nodeThread.start();
                            
                            try {
                                nodeThread.join();
                            } catch (InterruptedException ignored) {}
                        } // end if
                    } // end if
                }

                @Override
                public void treeWillCollapse(TreeExpansionEvent event) 
                                           throws ExpandVetoException {
                    return;
                }
            });
            
            // and make the pop - up
            fileSystems.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {                    
                    if (me.isPopupTrigger()) {
                        showPopup(me);
                        me.consume();
                    } // end if                    
                }
                
                @Override
                public void mouseReleased(MouseEvent me) {
                    if (me.isPopupTrigger()) {
                        showPopup(me);
                        me.consume();
                    } // end if                    
                }
                
                /**
                 * method to show the popup
                 */
                public void showPopup(MouseEvent me) {       
                    int row = fileSystems.getRowForLocation(me.getX(), 
                                                            me.getY());
                    
                    if (row < 0) { 
                        fileSystems.setSelectionRow(-1);
                        return;  // no selection
                    } // end if
                    
                    // do the selection if required
                    fileSystems.setSelectionRow(row);
                    
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                     fileSystems.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                    
                    Enumeration theInvokers = popupAssociation.keys();
                    
                    if (!(node.getUserObject() instanceof TreeNodeModal)) 
                        return;
                    
                    TreeNodeModal userObject = 
                                      (TreeNodeModal) node.getUserObject();
                    String theInvoker;
                    
                    // for each invoker, check the type and its association
                    // and then invoke the appropriate menu
                    while(theInvokers.hasMoreElements()) {
                        theInvoker = (String) theInvokers.nextElement();
                                                
                        if (userObject.hasExtension(theInvoker)) {                            
                            // set up the invoker
                            ((FilePopup) popupAssociation.get(theInvoker))
                                  .setSourceFileItem(userObject.getFile());
                            
                            // and then show the popup
                            ((FilePopup) popupAssociation.get(theInvoker))
                                    .show(fileSystems, me.getX(), me.getY());
                            break;
                        } // end if
                    } // end while
                }
            });
          }
        };
        treeMaker.setPriority(Thread.MIN_PRIORITY);
        treeMaker.setName("File panel tree maker Thread");
        treeMaker.start(); // start making the tree, which may take long time!
    }
    
    /**
     * method to create nodes of the directory tree
     */
    private void createNodes(DefaultMutableTreeNode top, String path) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode theFile = null;
        
        // PGL = pretty good logic
        try {
            File fileSystem = new File(path);
            File[] theFiles = sortIt(fileSystem.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File directory, String fileName) {
                    boolean showIt = true;
                    
                    // don't show hidden or setting files
                    showIt = showIt && !(fileName.startsWith("."));
                    showIt = showIt && !(directory.isHidden());
                    showIt = showIt && directory.canRead();
                    
                    return showIt;
                }
            }));
            
            // if there are no files
            if (theFiles.length == 0) {
                theFile = new DefaultMutableTreeNode("<< No Files >>");
                
                if (category == null) { // in the root!
                    top.add(theFile);
                } else {
                    category.add(theFile);
                } // end if
                
                return;
            } // end if
            
            // if there are files
            for(int i=0; i<theFiles.length; i++) {
                if (theFiles[i].isDirectory()) { // if directory
                    category = new DefaultMutableTreeNode(
                                          new TreeNodeModal(theFiles[i]));                    
                    top.add(category);
                    
                    // TODO: the following is commented for performance reasons!
                    // recursively handle the stuff
                    // createNodes(category, theFiles[i].getAbsolutePath());                    
                    category.add(new DefaultMutableTreeNode("Loading..."));
                    
                    // resort to original state of the JTree
                    top = (DefaultMutableTreeNode) category.getParent();
                    // make category null, as we are physically up
                    // but logically down..., let us come up!
                    category = null;
                } else { // the file business is much easier!
                    theFile = new DefaultMutableTreeNode(
                                         new TreeNodeModal(theFiles[i]));
                    
                    if (category == null) { // in the root!
                        top.add(theFile);
                    } else {
                        category.add(theFile);
                    } // end if
                } // end if
            } // end for
            
            fileSystem = null;
            theFiles   = null;
        } catch(NullPointerException noe) {
            System.err.println("Error while building user home: " + noe);
            noe.printStackTrace();
        } // end of try .. catch block
    } // end of method createNodes()
    
    /**
     * method to sort the file array in such a way that the directories come
     * before the files
     */
    private File[] sortIt(File[] files) {      
        if (files == null) return files;
        
        for(int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) continue;
            
            for(int j=i+1; j<files.length; j++) {
                if (files[j].isDirectory()) {
                    
                    // need a swap
                    File temp = files[i];
                    files[i]  = files[j];
                    files[j]  = temp;
                } // end if
            } // end for
        } // end for
        
        return files;
    }       
    
    /**
     * Inner class to represent the tree node data.
     */
    private class TreeNodeModal {
        private File node;
        
        public TreeNodeModal(File f) {
            node = f;
        } // end constructor
        
        public String getAbsolutePath() {
            return node.getAbsolutePath();
        } // end of method getAbsolutePath()
        
        public boolean isDirectory() {
            return node.isDirectory();
        } // end of method isDirectory()
        
        public boolean hasExtension(String extn) {
            return ((!isDirectory()) 
                     && (getAbsolutePath().endsWith("." + extn)));
        } // end of method hasExtension()
        
        public File getFile() {
            return node;
        } // end of method getFile()
        
        @Override
        public String toString() {
            return node.getAbsolutePath();
        } // end of overridden method toString()
    } // end of inner class TreeNodeModal
    
    /**
     * Inner class to represent cell rendering.
     */
    public class TreeCellRenderer extends DefaultTreeCellRenderer {
        private ImageIcon nodeIcon;
        
        public TreeCellRenderer() {
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
                        
            super.getTreeCellRendererComponent(tree, value, sel,
                                     expanded, leaf, row, hasFocus);
            
            // set the icons accordingly!
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            value = node.getUserObject();
            
            if (value instanceof String) {
                if (((String) value).equals("<< No Files >>")) {
                    setIcon(null);
                } else if (((String) value).equals("Filesystems")) {  
                    setIcon(ImageResource.getInstance().getFileSystem());
                } else if (((String) value).equals("Loading...")) {  
                    setIcon(null);
                } // end if// end if                                
                
                return this;
            } // end if
           
            if (! (value instanceof TreeNodeModal)) {
                return this;
            }
                
            
            setText(((TreeNodeModal) value).getFile().getName());
            
            nodeIcon = ImageResource.getInstance().getIconFor(
                                             ((TreeNodeModal) value).getFile());
            
            if (leaf && (nodeIcon != null)) {
                setIcon(nodeIcon);                
            } // end if
            
            return this;
        }

    } // end of inner class TreeCellRendering
    
    /**
     * A specialized popup menu for the FilePanel
     */
    public class FilePopup extends JPopupMenu {
        
        /** Holds value of property sourceFileItem. */
        private File sourceFileItem;
        
        /** Creates new instance of FilePopup */
        public FilePopup() {
            super();
        }
        
        /** Creates new instance of FilePopup 
         *
         * @param label - the label for this popup menu
         */
        public FilePopup(String label) {
            super(label);
        }        
        
        /** Getter for property sourceFileItem.
         * @return Value of property sourceWorkspaceItem.
         *
         */
        public File getSourceFileItem() {
            return this.sourceFileItem;
        }
        
        /** Setter for property sourceFileItem.
         * @param sourceFileItem New value of property sourceFileItem.
         *
         */
        public void setSourceFileItem(File sourceFileItem) {
            this.sourceFileItem = sourceFileItem;
        }
        
    } // end of inner class FilePopup
    
} // end of class FilePanel
