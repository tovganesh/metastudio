/*
 * IDEJRManFrame.java
 *
 * Created on November 8, 2005, 5:39 PM
 *
 */

package org.meta.shell.idebeans;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.commons.cli.*;
import org.jrman.parser.Parser;
import org.jrman.options.JRManStringResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;

import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.editors.impl.RibIDEditor;

/**
 * An interface to JRMan for MeTA Studio. Mainly a copy of
 * <code>org.jrman.main.JRMan</code> and
 * <code>org.jrman.ui.MainFrame</code>.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEJRManFrame extends JInternalFrame {
    
    private MeTA ideInstance;
    
    private CommandLine commandLine;
    private JButton bExit = new JButton("Exit");
    private DefaultListModel dlm = new DefaultListModel();
    private JList fileList = new JList(dlm);
    
    private class RenderRunnable implements Runnable {
        private DefaultListModel queue;
        private CommandLine commandLine;
        
        @Override
        public void run() {
            if (queue != null && IDEJRManFrame.this.commandLine != null) {
                ProgressMonitor monitor =
                        new ProgressMonitor(
                        IDEJRManFrame.this,
                        "Rendering:",
                        "filename",
                        0,
                        queue.size() + 1);
                monitor.setMillisToDecideToPopup(0);
                monitor.setMillisToPopup(0);
                monitor.setProgress(0);
                for (int i = 0; i < queue.size(); i++) {
                    File f = (File) queue.elementAt(i);
                    monitor.setNote(f.getName());
                    monitor.setProgress(i + 1);
                    try {
                        Parser parser = new Parser(commandLine);
                        parser.begin(f.getAbsolutePath());
                        parser.parse(f.getAbsolutePath());
                        parser.end();
                    } catch (ParseException pe) {
                        System.err.println(pe.getLocalizedMessage());
                    } catch (IOException ioe) {
                        System.err.println(
                                "Error on file " + ioe.getLocalizedMessage());
                    } catch (Exception e) {
                        System.err.println(e.getLocalizedMessage());
                    }
                    if (monitor.isCanceled())
                        break;
                }
                monitor.close();
                renderAction.setEnabled(true);
                addAction.setEnabled(true);
                removeAction.setEnabled(true);
            }
        }
    }
    
    private class AddAction extends AbstractAction {
        private IDEFileChooser chooser;
        
        public AddAction() {
            putValue(Action.NAME, "Add...");
            putValue(
                    Action.SHORT_DESCRIPTION,
                    "Add a rib file to the render queue");
            putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (chooser == null) {
                chooser = new IDEFileChooser();
                chooser.setDialogTitle("Select a RIB file to render...");  
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.removeChoosableFileFilter(
                        chooser.getAcceptAllFileFilter());
                
                chooser.addChoosableFileFilter(new IDEFileFilter("rib",
                        "Renderman file format"));
                
                // and add the iconic stuff
                chooser.setFileView(new IDEFileView());
            }
            if (JFileChooser.APPROVE_OPTION
                    == chooser.showOpenDialog(IDEJRManFrame.this)) {
                File f = chooser.getSelectedFile();
                if (f != null) {
                    dlm.addElement(f);
                    fileList.setSelectedIndex(dlm.getSize() - 1);
                }
            }
        }
    }
    
    private class RemoveAction extends AbstractAction {
        public RemoveAction() {
            putValue(Action.NAME, "Delete");
            putValue(
                    Action.SHORT_DESCRIPTION,
                    "Removes the selected rib file from the render queue");
            putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int idx = fileList.getSelectedIndex();
            if (idx >= 0) {
                dlm.remove(idx--);
                fileList.setSelectedIndex(idx < 0 ? 0 : idx);
            }
        }
    }
    
    private class RenderAction extends AbstractAction {
        
        public RenderAction() {
            putValue(Action.NAME, "Render");
            putValue(
                    Action.SHORT_DESCRIPTION,
                    "Starts rendering of the queued rib files");
            putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            this.setEnabled(false);
            addAction.setEnabled(false);
            removeAction.setEnabled(false);
            RenderRunnable runnable = new RenderRunnable();
            runnable.queue = dlm;
            runnable.commandLine = commandLine;
            Thread t = new Thread(runnable, "JRMan Render Thread");
            t.start();
        }
    }
    
    private class EditAction extends AbstractAction {
        
        public EditAction() {
            putValue(Action.NAME, "Edit");
            putValue(
                    Action.SHORT_DESCRIPTION,
                    "Open up and editor for modifying rib files");
            putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {          
            if (fileList.getSelectedValue() == null) return;
            
            RibIDEditor editor 
                    = new RibIDEditor(fileList.getSelectedValue().toString());
            
            ideInstance.getWorkspaceDesktop().addInternalFrame(editor, 
                                                               false, false);
            // add action listener for closing
            editor.addCloseListener();
            editor.grabFocus();
        }
    }
    
    private AddAction addAction = new AddAction();
    private RemoveAction removeAction = new RemoveAction();
    private EditAction editAction = new EditAction();
    private RenderAction renderAction = new RenderAction();
    private JButton bAdd = new JButton(addAction);
    private JButton bRemove = new JButton(removeAction);
    private JButton bEdit = new JButton(editAction);
    private JButton bRender = new JButton(renderAction);
    
    public static final char OPTION_QUALITY = 'q';
    
    /** Creates a new instance of IDEJRManFrame */
    public IDEJRManFrame(MeTA ideInstance) {
        super("JRMan console configured for: " 
                + StringResource.getInstance().getVersion(), 
                true, true, true, true);
        
        // set JRMan framebuffer class
        JRManStringResource.getInstance().setFramebufferClass(
             "org.meta.shell.idebeans.IDEJRManFramebufferImpl");
        
        this.ideInstance = ideInstance;
        
        Options opts = prepareOptions();
        
        // parse command line
        try {
            commandLine = new BasicParser().parse(opts, new String[]{"-q"});
        } catch (Exception e) {
            // ignored
            e.printStackTrace();
        } // end of try .. catch block
            
        initComponents();
        
        setVisible(true);
    }
    
    private Options prepareOptions() {
        Options options = new Options();                
        
        OptionBuilder.withLongOpt("quality");
        OptionBuilder.withDescription("higher rendering " +
                                      "quality (slightly slower)");
        options.addOption(OptionBuilder.create(OPTION_QUALITY));
        
        return options;
    }
    
    /** This method is called from within the constructor to
     * initialize the UI.
     */
    private void initComponents() {
        ImageResource images = ImageResource.getInstance();
        
        removeAction.setEnabled(false);
        renderAction.setEnabled(false);
        bExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateActions();
                }
            }
        });
        
        fileList.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                updateActions();
            }
            
            @Override
            public void intervalRemoved(ListDataEvent e) {
                updateActions();
            }
            
            @Override
            public void contentsChanged(ListDataEvent e) {
                updateActions();
            }
            
        });
        
        JPanel contentPane = new JPanel(new GridBagLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.gridx = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add(new JLabel("Select files to render"), gbc);
        gbc.gridheight = 6;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(new JScrollPane(fileList), gbc);
        gbc.gridheight = 1;
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 2, 0, 0);
        contentPane.add(bAdd, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 2, 0, 0);
        contentPane.add(bRemove, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 2, 0, 0);
        contentPane.add(bEdit, gbc);
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(Box.createVerticalGlue(), gbc);
        gbc.gridy = 5;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(bRender, gbc);
        gbc.gridy = 6;
        contentPane.add(bExit, gbc);
        setContentPane(contentPane);
        
        // set the frame icon
        setFrameIcon(images.getJrMan());
    }
    
    private void updateActions() {
        renderAction.setEnabled(fileList.getModel().getSize() > 0);
        removeAction.setEnabled(
                fileList.getModel().getSize() > 0
                && fileList.getSelectedIndex() >= 0);
    }
} // end of class IDEJRManFrame
