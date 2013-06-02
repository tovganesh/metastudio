/*
 * RibIDEditor.java
 *
 * Created on November 20, 2005, 10:20 PM
 *
 */

package org.meta.shell.idebeans.editors.impl;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import org.meta.common.Utility;
import org.meta.common.resource.ImageResource;
import org.meta.shell.idebeans.FindTextPanel;
import org.meta.shell.idebeans.IDEFileChooser;
import org.meta.shell.idebeans.IDEFileFilter;
import org.meta.shell.idebeans.IDEFileView;
import org.meta.shell.idebeans.editors.IDEEditor;

import org.syntax.jedit.*;
import org.syntax.jedit.event.*;

/**
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RibIDEditor extends JInternalFrame 
             implements UndoableEditListener, UIChangeListener, IDEEditor {
    
    protected String fileName;
    
    protected JEditTextArea editorPane;

    protected JToolBar toolbar;
    protected JButton newFile, openFile, saveFile,
              cut, copy, paste, undo, redo, render;

    protected final static String EDITOR_TITLE = "RIB IDEditor";

    protected JPopupMenu popupMenu;
    protected JMenuItem cutMenu, copyMenu, pasteMenu, undoMenu, redoMenu;
    
    /**
     * Holds value of property dirty.
     */
    protected boolean dirty;
    
    /** Creates a new instance of RibIDEditor */
    public RibIDEditor() {
        super(EDITOR_TITLE, true, true, true, true);

        fileName = null;
        initComponents();
        setTitle(EDITOR_TITLE + " - [Untitled]");
        dirty = false;  
    }
    
    public RibIDEditor(String fileName) {        
        super(EDITOR_TITLE, true, true, true, true);

        initComponents();
        open(fileName);
        dirty = false;                
    } 
    
    /**
     * initilize the components
     */
    protected void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        
        editorPane = new JEditTextArea();
        editorPane.setTokenMarker(new RIBTokenMarker());        
        getContentPane().add(editorPane, BorderLayout.CENTER);

        setFrameIcon(ImageResource.getInstance().getRender());
   
        // make the toolbar
        toolbar = new JToolBar();
        toolbar.setRollover(true);
        toolbar.setFloatable(true);

        ImageResource images = ImageResource.getInstance();

        // add the buttons
        newFile = new JButton("New");
        newFile.setMnemonic('N');
        newFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {     
                if (dirty) {
                    int option = JOptionPane.showConfirmDialog(RibIDEditor.this,
                    "Do you want to save changes in the file ["
                    + ((fileName == null) ? "Untitled " : fileName) + "] ?",
                    "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);
                    
                    switch(option) {
                        case JOptionPane.CANCEL_OPTION:                            
                            return;
                        case JOptionPane.YES_OPTION:
                            saveFile.doClick();                            
                            break;
                        case JOptionPane.NO_OPTION:                            
                            break;
                    }
                } // end if

                editorPane.setText("");
                fileName = null;
                editorPane.initUndoManager();
                editorPane.repaint();
                undoableEditHappened(null);
            }
        });
        newFile.setToolTipText("Opens a new RIB file");
        toolbar.add(newFile);

        openFile = new JButton("Open");
        openFile.setMnemonic('O');
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {  
                if (dirty) {
                    int option = JOptionPane.showConfirmDialog(RibIDEditor.this,
                    "Do you want to save changes in the file ["
                    + ((fileName == null) ? "Untitled " : fileName) + "] ?",
                    "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);
                    
                    switch(option) {
                        case JOptionPane.CANCEL_OPTION:                            
                            return;
                        case JOptionPane.YES_OPTION:
                            saveFile.doClick();                            
                            break;
                        case JOptionPane.NO_OPTION:                            
                            break;
                    }
                } // end if

                IDEFileChooser fileChooser = new IDEFileChooser();                        

                fileChooser.setDialogTitle("Open a RIB file...");               

                IDEFileFilter fileFilter = new IDEFileFilter("rib", 
                                                      "RIB files");
                
                // and add the file filter
                fileChooser.addChoosableFileFilter(fileFilter);
                // and add the iconic stuff
                fileChooser.setFileView(new IDEFileView());              

                if (fileChooser.showOpenDialog(RibIDEditor.this)
                     == IDEFileChooser.APPROVE_OPTION) {
                   if (!fileChooser.getSelectedFile().exists()) return;

                   open(fileChooser.getSelectedFile().getAbsolutePath());
                } // end if

                editorPane.initUndoManager();
                editorPane.repaint();  
                undoableEditHappened(null);
            }
        });
        openFile.setToolTipText("Opens an existing RIB file");
        toolbar.add(openFile);

        saveFile = new JButton("Save");
        saveFile.setMnemonic('S');
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) { 
                if (fileName != null) {
                   save(fileName);
                   return;
                } // end if

                IDEFileChooser fileChooser = new IDEFileChooser();
        
                fileChooser.setDialogTitle("Save the RIB script file...");

                IDEFileFilter fileFilter = new IDEFileFilter("rib", 
                                                      "RIB script files");
               
                // and add the file filter
                fileChooser.addChoosableFileFilter(fileFilter);
                // and add the iconic stuff
                fileChooser.setFileView(new IDEFileView());

                if (fileChooser.showSaveDialog(RibIDEditor.this)
                     == IDEFileChooser.APPROVE_OPTION) {
                  String theFileName = fileChooser.getSelectedFile()
                                                  .getAbsolutePath();

                  if (!theFileName.endsWith(".rib")) theFileName += ".rib";

                  save(theFileName);
                } // end if

                editorPane.initUndoManager();
                editorPane.repaint();
                updateRibEditorUI();
            }
        });
        saveFile.setToolTipText("Saves the current RIB file");
        dirty = false;
        saveFile.setEnabled(false);
        toolbar.add(saveFile);       
        
        toolbar.addSeparator();

        cut = new JButton(images.getCut());
        ActionListener actionCut = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {               
                editorPane.cut();
            }
        };
        cut.addActionListener(actionCut);
        cut.setToolTipText("Cut");
        toolbar.add(cut);

        copy = new JButton(images.getCopy());
        ActionListener actionCopy = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                editorPane.copy();
            }
        };
        copy.addActionListener(actionCopy);
        copy.setToolTipText("Copy");
        toolbar.add(copy);

        paste = new JButton(images.getPaste());
        ActionListener actionPaste = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {               
                editorPane.paste();
            }
        };
        paste.addActionListener(actionPaste);
        paste.setToolTipText("Paste");
        toolbar.add(paste);

        toolbar.addSeparator();

        undo = new JButton(images.getUndo());
        ActionListener actionUndo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                editorPane.undo();    
                undoableEditHappened(null);
            }
        };
        undo.addActionListener(actionUndo);        
        undo.setToolTipText("Undo");
        undo.setEnabled(false);        
        toolbar.add(undo);

        redo = new JButton(images.getRedo());
        ActionListener actionRedo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                editorPane.redo();         
                undoableEditHappened(null);
            }
        };
        redo.addActionListener(actionRedo);        
        redo.setToolTipText("Redo");
        redo.setEnabled(false);                
        toolbar.add(redo);       

        toolbar.addSeparator();
        
        render = new JButton("Render");
        ActionListener actionRun = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    Utility.executeBeanShellScript("render(\""
                                   + fileName.replace('\\', '/') + "\")");
                } catch (Exception e) {
                    System.out.println("Error while execution: " 
                                       + e.toString());
                    e.printStackTrace();
                    
                    JOptionPane.showMessageDialog(RibIDEditor.this, 
                        "Error while execution : " + e.toString(),
                        "Error!", JOptionPane.ERROR_MESSAGE);
                } // end of try .. catch block
            }
        };
        render.setMnemonic('R');
        render.addActionListener(actionRun);
        render.setToolTipText("Render the Rib file");
        
        KeyStroke keyStrokeRun = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 
                                                     InputEvent.CTRL_MASK);
        render.registerKeyboardAction(actionRun, "render", keyStrokeRun, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        toolbar.add(render);
        
        toolbar.add(new FindTextPanel(editorPane));
        
        // add the toolbar to the UI
        getContentPane().add(toolbar, BorderLayout.NORTH);
                
        // setup the popup menu
        popupMenu = new JPopupMenu("EditPopupMenu");

        cutMenu = new JMenuItem("Cut");
        cutMenu.addActionListener(actionCut);
        popupMenu.add(cutMenu);

        copyMenu = new JMenuItem("Copy");
        copyMenu.addActionListener(actionCopy);
        popupMenu.add(copyMenu);

        pasteMenu = new JMenuItem("Paste");
        pasteMenu.addActionListener(actionPaste);
        popupMenu.add(pasteMenu);

        popupMenu.addSeparator();

        undoMenu = new JMenuItem("Undo");
        undoMenu.addActionListener(actionUndo);
        popupMenu.add(undoMenu);

        redoMenu = new JMenuItem("Redo");
        redoMenu.addActionListener(actionRedo);
        popupMenu.add(redoMenu);

        editorPane.setRightClickPopup(popupMenu);

        // update UI before preceeding
        updateRibEditorUI();
        dirty = false;
        editorPane.addUIChangeListener(this);
        // add undo listener
        editorPane.removeUndoableEditListener(editorPane);
        editorPane.addUndoableEditListener(this);
        editorPane.addUndoableEditListener(editorPane);
        // set visible
        setVisible(true);
        grabFocus();       
    }

    /**
     * Opens up the specified file in editor.
     *
     * @param fileName the file name (absolute path) to be opened
     */
    @Override
    public void open(String fileName) {
        this.fileName = fileName;

        try {
            BufferedReader br = new BufferedReader(
                          new InputStreamReader(new FileInputStream(fileName)));
            String theLine;
            StringBuffer theLines = new StringBuffer();

            while(true) {
                theLine = br.readLine();

                if (theLine == null) break;

                theLines.append(theLine + "\n");
            } // end while

            br.close();

            editorPane.setText(theLines.toString());

            setTitle(EDITOR_TITLE + " - [" + fileName + "]");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, 
                        "Error opening file : " + e.toString(),
                        "Error!", JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }

    /**
     * Saves the currently loaded file
     *
     * @param fileName the file name (absolute path) to which to save
     */
    @Override
    public void save(String fileName) {
        this.fileName = fileName;

        try {
            FileOutputStream fos = new FileOutputStream(fileName);

            fos.write(editorPane.getText().getBytes());

            fos.close();         
            
            // update undo manager, and UI
            editorPane.initUndoManager();
            setTitle(EDITOR_TITLE + " - [" + fileName + "]");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, 
                        "Error saving file : " + e.toString(),
                        "Error!", JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }
   
    /**
     * listening to undo events
     */
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {        
        boolean canUndo = editorPane.canUndo();
        boolean canRedo = editorPane.canRedo();
        
        undo.setEnabled(canUndo);        
        redo.setEnabled(canRedo);
        undoMenu.setEnabled(canUndo);        
        redoMenu.setEnabled(canRedo);

        dirty = canUndo;

        saveFile.setEnabled(dirty);

        if (dirty) {
            setTitle(EDITOR_TITLE + " - [" + 
                     ((fileName == null) ? "Untitled " : fileName)
                     + " *]");
        } else {
            setTitle(EDITOR_TITLE + " - [" + 
                     ((fileName == null) ? "Untitled" : fileName)
                     + "]");
        } // end if
    }

    /**
     * UI updates form text componenet needed
     */
    @Override
    public void uiChanged(UIChangeEvent uice) {
        if (!editorPane.canUndo()) undoableEditHappened(null);

        updateRibEditorUI();
    }

    /**
     * update the UI
     */
    public void updateRibEditorUI() {
        if (editorPane.getSelectedText() == null) {
           copy.setEnabled(false);
           cut.setEnabled(false);
           copyMenu.setEnabled(false);
           cutMenu.setEnabled(false);
        } else {
           copy.setEnabled(true);
           cut.setEnabled(true);
           copyMenu.setEnabled(true);
           cutMenu.setEnabled(true);
        } // end if       
    }

    /**
     * add frame close listener, in the form of vetoable change listener
     */
    public void addCloseListener() {
        addInternalFrameListener(new InternalFrameAdapter() {           
            @Override
            public void internalFrameClosing(InternalFrameEvent e) { 
                if (dirty) {
                    int option = JOptionPane.showConfirmDialog(RibIDEditor.this,
                    "Do you want to save changes in the file ["
                    + ((fileName == null) ? "Untitled" : fileName) + "] ?",
                    "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);

                    switch(option) {
                        case JOptionPane.CANCEL_OPTION:                            
                            return;                            
                        case JOptionPane.YES_OPTION:
                            saveFile.doClick();  
                            cleanUp();
                            return;
                        case JOptionPane.NO_OPTION: 
                            cleanUp();
                            return;
                    } // end switch .. case
                } else {
                    cleanUp();
                } // end if
            }
        });
    }
    
    /**
     * clean up and dispose frame
     */
    private void cleanUp() {        
        dispose();
    }
    
    /**
     * Getter for property dirty.
     * @return Value of property dirty.
     */
    @Override
    public boolean isDirty() {
        return this.dirty;
    }
    
} // end of class RibIDEditor
