/*
 * StatusBar.java
 *
 * Created on June 22, 2003, 7:36 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.meta.common.resource.FontResource;

/**
 * The status bar component of the IDE
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class StatusBar extends JPanel {
    
    private JLabel statusText;
    private JPanel notificationPanel;
    private MemoryMonitor memoryMonitor;    
    
    /** Creates a new instance of StatusBar */
    public StatusBar() {
        initComponents();
    }
    
    private Color defaultBackground;
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        
        statusText = new JLabel("Ready.", JLabel.LEFT);
        statusText.setFont(FontResource.getInstance().getStatusFont());
        add(statusText, BorderLayout.CENTER);
        
        notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        notificationPanel.setBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, Color.gray));        
        memoryMonitor = new MemoryMonitor();        
        memoryMonitor.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent me) {                
                setStatusText("Click on memory monitor to "
                              + "free up unused memory in JVM."
                              + " Right click to change memory configuration.");
            }
        });
        notificationPanel.add(memoryMonitor);
        notificationPanel.setToolTipText("Notification Panel");
        add(notificationPanel, BorderLayout.EAST);
        
        defaultBackground = statusText.getBackground();
    }
    
    /**
     * add to notification panel
     *
     * @param component - the component to be added to the panel
     */
    public void addToNotificationPanel(JComponent component) {
        notificationPanel.add(component);
    }
        
    /**
     * Method to set the status test displayed on this status bar
     */
    public void setStatusText(String newText) {
        if (newText.toLowerCase().indexOf("error") >= 0) {
            setBackground(Color.red);
            statusText.setBackground(Color.red);
        } else {
            setBackground(defaultBackground);
            statusText.setBackground(defaultBackground);
        } // end if
        
        statusText.setText(newText);        
    }
} // end of class StatusBar
