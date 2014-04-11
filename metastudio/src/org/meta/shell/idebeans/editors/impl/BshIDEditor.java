/*
 * BshIDEditor.java
 *
 * Created on October 10, 2004, 10:33 AM
 */

package org.meta.shell.idebeans.editors.impl;

import bsh.Interpreter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.FindTextPanel;
import org.meta.shell.idebeans.IDEFileChooser;
import org.meta.shell.idebeans.IDEFileFilter;
import org.meta.shell.idebeans.IDEFileView;
import org.meta.shell.idebeans.editors.IDEEditor;
import org.syntax.jedit.*;
import org.syntax.jedit.event.*;
import org.syntax.jedit.tokenmarker.*;

/**
 * Bean shell editor.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BshIDEditor extends JInternalFrame 
             implements UndoableEditListener, UIChangeListener, IDEEditor {
                 
    protected String fileName;
    
    protected JEditTextArea editorPane;

    protected JToolBar toolbar;
    protected JButton newFile, openFile, saveFile, savePlugin, saveWidget, 
              cut, copy, paste, undo, redo,
              run;

    protected final static String EDITOR_TITLE = "Beanshell IDEditor";

    protected JPopupMenu popupMenu;
    protected JMenuItem cutMenu, copyMenu, pasteMenu, undoMenu, redoMenu;

    protected Interpreter theInterpreter;        
    
    private MeTA ideInstance;
    
    /**
     * Holds value of property dirty.
     */
    protected boolean dirty;
    
    public BshIDEditor(MeTA ideInstance) {
        super(EDITOR_TITLE, true, true, true, true);

        this.ideInstance = ideInstance;
        
        fileName = null;
        initComponents();
        setTitle(EDITOR_TITLE + " - [Untitled]");
        dirty = false;  
        
        startInterpreter();
    }

    public BshIDEditor(MeTA ideInstance, String fileName) {        
        super(EDITOR_TITLE, true, true, true, true);

        this.ideInstance = ideInstance;
        
        initComponents();
        open(fileName);
        dirty = false;
        
        startInterpreter();
    }

    /**
     * start the interpreter, import common commands
     */
    private void startInterpreter() {
        if (theInterpreter != null) theInterpreter = null;
        System.gc();
        
        try {
            theInterpreter = new Interpreter();
        } catch (Exception e) {
            System.out.println("Unable to correctly start interpreter : " 
                               + e.toString());
            e.printStackTrace();
        }
    }
    
    /**
     * initialize the components
     */
    protected void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        
        editorPane = new JEditTextArea();                
        editorPane.setTokenMarker(new BeanShellTokenMarker());        
        getContentPane().add(editorPane, BorderLayout.CENTER);

        setFrameIcon(ImageResource.getInstance().getEditor());    
   
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
                    int option = JOptionPane.showConfirmDialog(BshIDEditor.this,
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
        newFile.setToolTipText("Opens a new BeanShell file");
        toolbar.add(newFile);

        openFile = new JButton("Open");
        openFile.setMnemonic('O');
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {  
                if (dirty) {
                    int option = JOptionPane.showConfirmDialog(BshIDEditor.this,
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

                fileChooser.setDialogTitle("Open a BeanShell script file...");

                IDEFileFilter fileFilter = new IDEFileFilter("bsh", 
                                                      "BeanShell script files");
                
                // and add the file filter
                fileChooser.addChoosableFileFilter(fileFilter);
                // and add the iconic stuff
                fileChooser.setFileView(new IDEFileView());              

                if (fileChooser.showOpenDialog(BshIDEditor.this)
                     == IDEFileChooser.APPROVE_OPTION) {
                   if (!fileChooser.getSelectedFile().exists()) return;

                   open(fileChooser.getSelectedFile().getAbsolutePath());
                } // end if

                editorPane.initUndoManager();
                editorPane.repaint();  
                undoableEditHappened(null);
            }
        });
        openFile.setToolTipText("Opens an existing BeanShell file");
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
        
                fileChooser.setDialogTitle("Save the BeanShell script file...");

                IDEFileFilter fileFilter = new IDEFileFilter("bsh", 
                                                      "BeanShell script files");
               
                // and add the file filter
                fileChooser.addChoosableFileFilter(fileFilter);
                // and add the iconic stuff
                fileChooser.setFileView(new IDEFileView());

                if (fileChooser.showSaveDialog(BshIDEditor.this)
                     == IDEFileChooser.APPROVE_OPTION) {
                  String theFileName = fileChooser.getSelectedFile()
                                                  .getAbsolutePath();

                  if (!theFileName.endsWith(".bsh")) theFileName += ".bsh";

                  save(theFileName);
                } // end if

                editorPane.initUndoManager();
                editorPane.repaint();
                updateBshEditorUI();
            }
        });
        saveFile.setToolTipText("Saves the current BeanShell file");
        dirty = false;
        saveFile.setEnabled(false);
        toolbar.add(saveFile);

        savePlugin = new JButton("Make Plugin");
        savePlugin.setMnemonic('M');
        savePlugin.setToolTipText("Saves this script in the default plugins " +
                                 "location, and make it available at startup");
        savePlugin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if ((fileName == null) || dirty) {
                    JOptionPane.showMessageDialog(BshIDEditor.this, 
                            "You need to save the script first!", "Need saving",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                } // end if
                                    
                File pluginFile = new File(fileName);
                String pluginDir = StringResource.getInstance().getPluginDir();
                
                savePluginFile(pluginDir + File.separatorChar 
                                         + pluginFile.getName());
                
                // try to activate the plugin!
                try {
                    ideInstance.getStartupScriptEngine().sourcePlugin(
                                pluginDir + File.separatorChar
                                          + pluginFile.getName());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BshIDEditor.this,
                            "Unable to load your plugin! Error is: "
                            + e.toString(),
                            "Error!",
                            JOptionPane.ERROR_MESSAGE);
                } // end of try .. catch block
            }
        });
        toolbar.add(savePlugin);
        
        saveWidget = new JButton("Make Widget");
        saveWidget.setMnemonic('W');
        saveWidget.setToolTipText("Saves this script in the default widgets " +
                                 "location, and make it available at startup");
        saveWidget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if ((fileName == null) || dirty) {
                    JOptionPane.showMessageDialog(BshIDEditor.this, 
                            "You need to save the script first!", "Need saving",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                } // end if
                                    
                File widgetFile = new File(fileName);
                String widgetDir = StringResource.getInstance().getWidgetsDir();
                
                savePluginFile(widgetDir + File.separatorChar 
                                         + widgetFile.getName());
                
                // try to activate the widget
                try {
                    ideInstance.getStartupScriptEngine().sourceWidget(
                                widgetDir + File.separatorChar
                                          + widgetFile.getName());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BshIDEditor.this,
                            "Unable to load your great widget! Error is: "
                            + e.toString(),
                            "Error!",
                            JOptionPane.ERROR_MESSAGE);
                } // end of try .. catch block
            }
        });
        toolbar.add(saveWidget);
        
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
        
        run = new JButton("Run");
        ActionListener actionRun = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (run.getText().equals("Run")) {
                    runScript();
                } else {
                    if (runScriptThread != null) {
                        try {
                            runScriptThread.wait();
                        } catch(InterruptedException ignored) { } 
                        runScriptThread.interrupt();                        
                    } // end if
                    
                    run.setText("Run");
                    run.setToolTipText("Execute the current script in Bsh interpreter");
                } // end if
            }
        };
        run.setMnemonic('R');
        run.addActionListener(actionRun);
        run.setToolTipText("Execute the current script in Bsh interpreter");
        
        KeyStroke keyStrokeRun = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 
                                                     InputEvent.CTRL_MASK);
        run.registerKeyboardAction(actionRun, "run", keyStrokeRun, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        toolbar.add(run);
        toolbar.addSeparator();
        
        // then make the "find tool"
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
        updateBshEditorUI();
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

    /** The thread in which the current script is executed */
    private Thread runScriptThread;
    
    /**
     * run the script...
     */
    private void runScript() {
        if (runScriptThread != null) runScriptThread = null;
        System.gc();
        
        runScriptThread = new Thread() {
            @Override
            public void run() {
                try {
                    run.setText("Interrupt");
                    run.setToolTipText("Interrupt the currently running script");
                    
                    startInterpreter();
                    theInterpreter.eval("clear();");
                    theInterpreter.eval(
                          "importCommands(\"org.meta.commands\")");

                    File pluginDir = new File(
                                   StringResource.getInstance().getPluginDir());

                    if (pluginDir.exists()) {
                        for (File file : pluginDir.listFiles()) {
                            try {
                                theInterpreter.eval("source(\""
                                        + file.toString().replace('\\', '/')
                                        + "\")");
                            } catch (Exception e) {
                                System.err.println("Warning! " +
                                        "Unable to import commands : " + e);
                            } // end of try .. catch block
                        } // end for
                    } // end if

                    theInterpreter.eval(editorPane.getText());               
                    
                    run.setText("Run");
                    run.setToolTipText("Execute the current script in Bsh interpreter");
                } catch (Exception e) {
                    System.out.println("Error while execution: " 
                                       + e.toString());
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(BshIDEditor.this, 
                        "Error while execution : " + e.toString(),
                        "Error!", JOptionPane.ERROR_MESSAGE);
                    
                    run.setText("Run");
                    run.setToolTipText("Execute the current script in Bsh interpreter");
                } // end of try .. catch block
            }
        };        
        
        runScriptThread.setName("Run script Thread");
        runScriptThread.start();
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
                          new InputStreamReader(new FileInputStream(fileName),
                                                    "UTF-8"));
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
            OutputStreamWriter fos = new OutputStreamWriter(
                                      new FileOutputStream(fileName), "UTF-8");

            fos.write(editorPane.getText());

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
     * Saves the currently loaded file, as plugin
     */
    public void savePluginFile(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);

            fos.write(editorPane.getText().getBytes());

            fos.close();
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

        updateBshEditorUI();
    }

    /**
     * update the UI
     */
    public void updateBshEditorUI() {
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
                    int option = JOptionPane.showConfirmDialog(BshIDEditor.this,
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
        theInterpreter = null;
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
    
} // end of class BshIDEditor
