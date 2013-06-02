/*
 * IDEPropertySheetUI.java
 *
 * Created on June 20, 2004, 6:24 PM
 */

package org.meta.shell.idebeans.propertysheet;

import java.awt.*;
import java.util.*;
import java.beans.*;
import java.awt.event.*;

import javax.swing.*;

import org.meta.shell.ide.MeTA;

/**
 * All the UI pertaining to property sheets is managed by this class.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEPropertySheetUI {        
    
    /**
     * Holds value of property theClass.
     */
    private Class theClass;
    
    /**
     * Holds value of property theObject.
     */
    private Object theObject;
    
    /**
     * Holds value of property dockable.
     */
    private boolean dockable;
    
    private HashMap propertyDescriptors;  
    
    private MeTA ideInstance;
    
    private JDialog propertyDialog;
    
    /** 
     * Creates a new instance of IDEPropertySheetUI 
     *
     * @param clazz - the Class whose properties need to displayed
     */
    public IDEPropertySheetUI(Class clazz, Object obj, MeTA ideInstance) {
        theClass  = clazz;
        theObject = obj;
        this.ideInstance = ideInstance;
        
        dockable = false; 
        
        propertyOptionsInsects = new Insets(5, 5, 5, 5);
    }
    
    
    /**
     * Show the UI for property sheet of the concerned class
     * @param parent the parent componant, can be null if 
     *        <code> isDockable() </code> returns false.
     */    
    public void show(JComponent parent) {
        // Step 0: varify 
        if (!theClass.isInstance(theObject)) {
            throw new UnsupportedOperationException("The object is not the " +
                                            "instance of the specified class.");            
        } // end if
        if (dockable) {
            if (parent == null) {
                throw new NullPointerException("parent cannot be null if " +
                                                       "isDockable() is true.");
            } // end if
        } // end if                
        
        try {
           // Step 1: read in the property sheet
           IDEPropertySheetReader reader = new IDEPropertySheetReader(theClass);
           IDEPropertySheet propertySheet = reader.readIt();
           
           propertyDescriptors = reader.getPropertyDescriptors();
           
           // Step 2: render the UI
           renderUI(propertySheet, parent);
        } catch (Exception e) { 
           System.out.println("Unexpected exception occured in " +
                               "IDEPropertySheetUI.show() : " + e.toString());
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Unexpected exception " +
                     "occured in IDEPropertySheetUI.show() : " + e.toString(),
                     "Error", JOptionPane.ERROR_MESSAGE);
        } // end try .. catch 
    }
    
    /**
     * renderUI() - render property sheet UI
     */
    protected void renderUI(IDEPropertySheet propertySheet, 
                            JComponent parent) throws Exception {        
        propertyDialog = new JDialog(ideInstance);
        
        if (!dockable) {            
            propertyDialog.setModal(true); // it is a modal dialog
            propertyDialog.setTitle(propertySheet.getTitle() + " ("
                                    + theObject.toString() + ")"); // set title
        } // end if
        
        JTabbedPane propertyPane = new JTabbedPane(JTabbedPane.TOP, 
                                       JTabbedPane.SCROLL_TAB_LAYOUT);                        
        
        Iterator propertyGroups = propertySheet.getPropertyGroups();
        
        IDEPropertyGroup propertyGroup;
        
        IDEPropertyFieldUIFacroty uiFactory = new IDEPropertyFieldUIFacroty();
        
        // now render each group as a "tab"
        while(propertyGroups.hasNext()) { // level 1 nesting
             propertyGroup = (IDEPropertyGroup) propertyGroups.next();
             
             Iterator properties = propertyGroup.getProperties();
             IDEProperty property;
             
             JPanel propertyGroupPanel = new JPanel(new GridBagLayout());
                          
             GridBagConstraints gbc = new GridBagConstraints();
             
             gbc.gridx = 0; gbc.gridy = 0;
             gbc.fill = GridBagConstraints.BOTH;
             gbc.anchor = GridBagConstraints.NORTHWEST;
             gbc.insets = propertyOptionsInsects;
             
             // and render the individual properties
             while(properties.hasNext()) { // level 2 nesting
                 property = (IDEProperty) properties.next();
                 
                 JLabel propertyLabel = new JLabel(property.getDisplayName(), 
                                                   JLabel.RIGHT);
                 JComponent propertyField = uiFactory.getPropertyFieldUI(
                  property, theObject, (PropertyDescriptor) 
                            propertyDescriptors.get(property.getName()));
                                  
                 propertyField.setToolTipText(property.getTooltipText());
                 propertyLabel.setDisplayedMnemonic(property.getMnemonicChar());
                 propertyLabel.setLabelFor(propertyField);
                 propertyLabel.setToolTipText(property.getTooltipText());
                 
                 // add the stuff to the UI
                 gbc.gridwidth = GridBagConstraints.RELATIVE;
                 propertyGroupPanel.add(propertyLabel, gbc);
                 gbc.gridx++;
                 gbc.gridwidth = GridBagConstraints.REMAINDER;
                 propertyGroupPanel.add(propertyField, gbc);
                 gbc.gridy++; gbc.gridx = 0;
             } // end while
             
             // and finally add this tab
             propertyPane.addTab(propertyGroup.getName(), propertyGroupPanel);
        } // end while                
        
        // add the stuff to the UI and show it
        if (!dockable) {
            // and add the accelerator
            ActionListener actionCancel = new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    propertyDialog.setVisible(false);
                    propertyDialog.dispose();
                }
            };
            
            KeyStroke keyStrokeCancel = KeyStroke.getKeyStroke(
                                                  KeyEvent.VK_ESCAPE, 0);
            propertyPane.registerKeyboardAction(actionCancel, "cancel",
                                         keyStrokeCancel,
                                         JComponent.WHEN_IN_FOCUSED_WINDOW);
        
            propertyDialog.getContentPane().setLayout(new BorderLayout());
            propertyDialog.getContentPane().add(propertyPane, 
                                                BorderLayout.CENTER);
            propertyDialog.pack();
            propertyDialog.setLocationRelativeTo(ideInstance);
            
            propertyDialog.setSize(propertyDialog.getWidth(),
                                   propertyDialog.getHeight() + 20);
            propertyDialog.setVisible(true);
        } else {
            parent.removeAll();
            parent.setLayout(new BorderLayout());
            parent.add(propertyPane, BorderLayout.CENTER);
            parent.setVisible(true);
        } // end if
    }       
    
    private Insets propertyOptionsInsects;

    /**
     * Getter for property propertyOptionsInsects.
     * @return Value of property propertyOptionsInsects.
     */
    public Insets getPropertyOptionsInsects() {
        return propertyOptionsInsects;
    }

    /**
     * Setter for property propertyOptionsInsects.
     * @param propertyOptionsInsects
     *        New value of property propertyOptionsInsects.
     */
    public void setPropertyOptionsInsects(Insets propertyOptionsInsects) {
        this.propertyOptionsInsects = propertyOptionsInsects;
    }
    
    /**
     * Getter for property theClass.
     * @return Value of property theClass.
     */
    public Class getTheClass() {
        return this.theClass;
    }
    
    /**
     * Setter for property theClass.
     * @param theClass New value of property theClass.
     */
    public void setTheClass(Class theClass) {
        this.theClass = theClass;
    }
    
    /**
     * Getter for property theObject.
     * @return Value of property theObject.
     */
    public Object getTheObject() {
        return this.theObject;
    }
    
    /**
     * Setter for property theObject.
     * @param theObject New value of property theObject.
     */
    public void setTheObject(Object theObject) {
        this.theObject = theObject;
    }
    
    /**
     * Getter for property dockable.
     * @return Value of property dockable.
     */
    public boolean isDockable() {
        return this.dockable;
    }
    
    /**
     * Setter for property dockable.
     * @param dockable New value of property dockable.
     */
    public void setDockable(boolean dockable) {
        this.dockable = dockable;
    }
    
} // end of class IDEPropertySheetUI
