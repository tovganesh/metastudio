/*
 * IDEWidgetsPanel.java
 *
 * Created on February 19, 2007, 10:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.meta.common.resource.StringResource;
import org.meta.shell.ide.MeTA;

/**
 * The IDE Widgets panel, a placeholder for custom IDE widgets.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEWidgetsPanel extends JPanel {
    
    private ArrayList<IDEWidget> widgetsList;
    
    private JScrollPane scrollPanel;
            
    private JPanel thePanel;
    
    private JButton addRemove;
    
    private MeTA ideInstance;
    
    /** Creates a new instance of IDEWidgetsPanel */
    public IDEWidgetsPanel(MeTA ideInstance) {
        this.ideInstance = ideInstance;
        
        thePanel = new JPanel() {

            Insets insets = new Insets(0, 1, 0, 0);

            @Override
            public Insets getInsets() {
                return insets;
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, 50);
            }
        };
                                        
        thePanel.setLayout(new IDEVerticalFlowLayout());
        
        scrollPanel = new JScrollPane(thePanel, 
                              JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                
        setLayout(new BorderLayout());
        add(scrollPanel, BorderLayout.CENTER);
        
        addRemove = new JButton("Add/ Remove Widgets...");
        addRemove.setToolTipText("Add or remove widgets from the widget panel");
        addRemove.setFont(addRemove.getFont().deriveFont(Font.BOLD));
        addRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showAddRemoveWidgetsDialog();
            }
        });
        add(addRemove, BorderLayout.NORTH);
        
        widgetsList = new ArrayList<IDEWidget>();
    }
        
    /**
     * show the add remove widgets dialog
     */
    private void showAddRemoveWidgetsDialog() {
        JDialog addRemoveDialog = new JDialog(ideInstance);        
        Container contentPane = addRemoveDialog.getContentPane();
        
        contentPane.setLayout(new BorderLayout());
        
        final JList widgetsListUI = new JList(widgetsList.toArray());
        contentPane.add(new JScrollPane(widgetsListUI, 
                                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), 
                        BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new IDEVerticalFlowLayout());
        JButton addWidget, removeWidget;
        
        addWidget = new JButton("Add Widget...");
        addWidget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                IDEFileChooser fileChooser = new IDEFileChooser();

                fileChooser.setDialogTitle("Open a BeanShell script file...");

                IDEFileFilter fileFilter = new IDEFileFilter("bsh", 
                                                      "BeanShell script files");
                
                // and add the file filter
                fileChooser.addChoosableFileFilter(fileFilter);
                // and add the iconic stuff
                fileChooser.setFileView(new IDEFileView());              

                if (fileChooser.showOpenDialog(IDEWidgetsPanel.this)
                     == IDEFileChooser.APPROVE_OPTION) {
                    if (!fileChooser.getSelectedFile().exists()) {
                        return;
                    }

                            
                    try {
                        // try to start this widget
                        ideInstance.getStartupScriptEngine().sourceWidget(
                            fileChooser.getSelectedFile().getAbsolutePath());

                        // then try to copy to standard widgets dir
                        // so that they automatically startup during the
                        // next start up
                        String widgetDir = StringResource.getInstance()
                                                     .getWidgetsDir();
                        String fileName = widgetDir + File.separatorChar
                                      + fileChooser.getSelectedFile().getName();

                        BufferedReader br = new BufferedReader(
                            new InputStreamReader(new FileInputStream(
                               fileChooser.getSelectedFile().getAbsolutePath()),
                            "UTF-8"));
                        String theLine;
                        StringBuilder theLines = new StringBuilder();

                        while (true) {
                            theLine = br.readLine();

                            if (theLine == null) break;

                            theLines.append(theLine).append("\n");
                        } // end while

                        br.close();
                        
                        FileOutputStream fos = new FileOutputStream(fileName);

                        fos.write(theLines.toString().getBytes());
                        fos.close();
                    } catch(Exception e) {
                        System.err.println("Error while adding new widget: " 
                                           + e.toString());
                        e.printStackTrace();
                    } // end of try .. catch block 
                    
                    widgetsListUI.setListData(widgetsList.toArray());
                    updateUI();
                } // end if
            }
        });
        buttonPanel.add(addWidget);                
        
        removeWidget = new JButton("Remove Widget...");
        removeWidget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                IDEWidget widget = (IDEWidget) widgetsListUI.getSelectedValue();
                
                String widgetDir = StringResource.getInstance().getWidgetsDir();
                String fileName = widgetDir + File.separatorChar
                                      + widget.getWidgetID() + ".bsh";
                    
                System.out.println("Removing widget: " + fileName);
                
                // try to delete the widget file name if 
                // there in the start up list
                if (!(new File(fileName)).delete()) {
                    (new File(fileName)).deleteOnExit();
                } // end if
                
                // and then remove from the UI
                removeWidget(widget);
                widgetsListUI.setListData(widgetsList.toArray());
                updateUI();
            }
        });
        buttonPanel.add(removeWidget);
        
        contentPane.add(buttonPanel, BorderLayout.EAST);
                
        addRemoveDialog.setLocationRelativeTo(this.ideInstance);
        addRemoveDialog.setModal(true);
        addRemoveDialog.setTitle("Add/ Remove widgets");
        addRemoveDialog.pack();
        addRemoveDialog.setVisible(true);
    }
    
    /**
     * Add a widget to this panel
     *
     * @param widget the IDEWidget to be added to this panel
     */
    public void addWidget(IDEWidget widget) {
        if (!(widget instanceof IDEWidget)) {
            throw new UnsupportedOperationException("Only instance of " +
                    "IDEWidget can be added to IDEWidgetsPanel");
        } // end if
        
        if (widget.getWidgetID() == null) {
             throw new UnsupportedOperationException("WidgetID can not " +
                     "be null! Please provide a valid WidgetID for the " +
                     "widget to be added.");
        } // end if
        
        if (widgetsList.contains(widget)) {
            throw new UnsupportedOperationException("A widget with WidgetID '" +
                    widget.getWidgetID() + "' is already present! This" +
                    " widget is not added.");
        } // end if
        
        // else add to the panel, and the list 
        widget.setWidgetsPanel(this);
        thePanel.add(widget);        
        widgetsList.add(widget);
        updateUI();
    }
    
    /**
     * Remove a widget from this panel
     *
     * @param widget the IDEWidget to be added to this panel
     */
    public void removeWidget(IDEWidget widget) {
        for (Component c : thePanel.getComponents()) {
            if (c instanceof IDEWidget) {
                if (((IDEWidget) c).equals(widget)) {
                    thePanel.remove(c);
                } // end if
            } // end if            
        } // end for
        
        widgetsList.remove(widget);
        
        validate();
    }

    /**
     * overriden removeAll() method
     */
    @Override
    public void removeAll() {
        thePanel.removeAll();        
        widgetsList = new ArrayList<IDEWidget>();
        
        validate();
    }    
    
} // end of class IDEWidgetsPanel
