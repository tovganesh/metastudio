/*
 * SidePanel.java
 *
 * Created on October 20, 2004, 6:38 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import org.meta.common.resource.ImageResource;

/**
 * Used to construct a side panel, for additional on-line inretaction with 
 * the user, mostly in MoleculeViewerFrame.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SidePanel extends JPanel {
    
    private JPanel titleBar, mainPanel;
    
    private JLabel titleLabel;
    private JButton closeButton;
    
    /** used internally to enable/ disable call to addImpl() */
    private boolean checkAddImpl;
    
    /**
     * Holds value of property title.
     */
    private String title;
    
    /** Creates a new instance of SidePanel */
    public SidePanel() {
        this("");
    }
    
    /** Creates a new instance of SidePanel */
    public SidePanel(String title) {
        this.title = title;
        
        this.checkAddImpl = false; // do not perform add() check during init
        initComponents();
        this.checkAddImpl = true; // and then enable the check
        
        setBorder(BorderFactory.createLoweredBevelBorder());
    }
    
    /** Creates a new instance of SidePanel using instance of JPanel */
    public SidePanel(String title, JPanel panel) {
        this.title = title;
        
        this.checkAddImpl = false; // do not perform add() check during init
        initComponents();
        this.checkAddImpl = true; // and then enable the check
        
        setBorder(BorderFactory.createLoweredBevelBorder());
        
        mainPanel.add(panel, BorderLayout.CENTER);
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        
        // make the title 
        titleBar = new JPanel(new BorderLayout());
        // the title
        titleLabel = new JLabel(title);        
        titleBar.add(titleLabel, BorderLayout.CENTER);
        // and the close button
        ImageIcon cbImage = ImageResource.getInstance().getCloseWindow();
        closeButton = new JButton(cbImage);
        closeButton.setPreferredSize(
             new Dimension(cbImage.getIconWidth(), cbImage.getIconHeight()));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setToolTipText("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
            }
        });
        titleBar.add(closeButton, BorderLayout.EAST);
        titleBar.setBorder(BorderFactory.createLineBorder(Color.black));  
        
        super.setLayout(new BorderLayout());
        
        // make the title bar
        super.add(titleBar, BorderLayout.NORTH);
        
        // and the main panel
        super.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }
    
    /**
     * Add an action listener to the close button action
     * 
     * @param al the action listener object
     */
    public void addCloseButtonActionListener(ActionListener al) {
        if (closeButton != null) {
            closeButton.addActionListener(al);
        } // end if
    }
    
    /**
     * Removes existing action listener connected to the close button
     * 
     * @param al the action listener object
     */
    public void removeCloseButtonActionListener(ActionListener al) {
        if (closeButton != null) {
            closeButton.removeActionListener(al);
        } // end if
    }
    
    /**
     * Throws a runtime exception .. specifying use of 
     * <code> NotificationWindow.getContentPane().setLayout() </code> instead.
     */
    @Override
    public void setLayout(LayoutManager mgr) {
        if (!checkAddImpl) { 
            super.setLayout(mgr);
            return;
        } // end if
        
        getContentPane().setLayout(mgr);
    }
    
    /**
     * Throws a runtime exception .. specifying use of 
     * <code> NotificationWindow.getContentPane().getLayout() </code> instead.
     */
    @Override
    public LayoutManager getLayout() {
        if (!checkAddImpl) { 
           return super.getLayout();
        } // end if
        
        return getContentPane().getLayout();
    }
    
    /**
     * Throws a runtime exception .. specifying use of 
     * <code> NotificationWindow.getContentPane().add() </code> instead.
     */
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (!checkAddImpl) { 
            super.addImpl(comp, constraints, index);
            return;
        } // end if
        
        getContentPane().add(comp, constraints, index);
    }
    
    /**
     * Return the content pane...
     * The proper way of adding external componants is to use the ContentPane
     *
     * @return JComponent - the content pane to add additional componants
     */
    public JComponent getContentPane() {
        return mainPanel;
    }
    
    /**
     * Getter for property title.
     * @return Value of property title.
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
} // end of class SidePanel
