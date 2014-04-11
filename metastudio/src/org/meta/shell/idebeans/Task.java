/*
 * Task.java
 *
 * Created on March 28, 2004, 7:12 AM
 */

package org.meta.shell.idebeans;

import java.awt.event.*;

import javax.swing.*;

/**
 * The Task represents a task to be performed through TaskPanel
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Task implements TaskType {
    
    /** Holds value of property enabled. */
    private boolean enabled;
    
    /** Holds value of property description. */
    private String description;
    
    /** Utility field used by event firing mechanism. */
    private javax.swing.event.EventListenerList listenerList =  null;
    
    /** Holds value of property visible. */
    private boolean visible;
    
    /** Holds value of property icon. */
    private ImageIcon icon;
    
    /** Holds value of property toolTipText. */
    private String toolTipText;
    
    /** Creates a new instance of Task */
    public Task(String description) {
        this(description, null);
    }
    
    public Task(String description, ImageIcon icon) {
        this.description = description;
        this.toolTipText = description;
        
        enabled = visible = true;
        
        this.icon = icon;
    }
    
    /** Getter for property enabled.
     * @return Value of property enabled.
     *
     */
    public boolean isEnabled() {
        return this.enabled;
    }
    
    /** Setter for property enabled.
     * @param enabled New value of property enabled.
     *
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /** Getter for property description.
     * @return Value of property description.
     *
     */
    public String getDescription() {
        return this.description;
    }
    
    /** Setter for property description.
     * @param description New value of property description.
     *
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /** Registers ActionListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addActionListener(ActionListener listener) {
        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(java.awt.event.ActionListener.class, listener);
    }
    
    /** Removes ActionListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     *
     */
    public void fireActionListenerActionPerformed(ActionEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==java.awt.event.ActionListener.class) {
                ((ActionListener)listeners[i+1]).actionPerformed(event);
            }
        }
    }
    
    /**
     * overloaded toString()
     */
    public String toString() {
        return description;
    } 
    
    /** Getter for property visible.
     * @return Value of property visible.
     *
     */
    public boolean isVisible() {
        return this.visible;
    }
    
    /** Setter for property visible.
     * @param visible New value of property visible.
     *
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /** Getter for property icon.
     * @return Value of property icon.
     *
     */
    public ImageIcon getIcon() {
        return this.icon;
    }
    
    /** Setter for property icon.
     * @param icon New value of property icon.
     *
     */
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    
    /** Getter for property toolTipText.
     * @return Value of property toolTipText.
     *
     */
    public String getToolTipText() {
        return this.toolTipText;
    }
    
    /** Setter for property toolTipText.
     * @param toolTipText New value of property toolTipText.
     *
     */
    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }
    
} // end of class Task
