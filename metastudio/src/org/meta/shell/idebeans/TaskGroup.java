/*
 * TaskGroup.java
 *
 * Created on March 28, 2004, 7:14 AM
 */

package org.meta.shell.idebeans;

import java.util.Vector;

/**
 * The TaskGroup represents a set of Task s to be performed via. TaskPanel
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TaskGroup implements TaskType {
        
    /** Holds value of property taskList. */
    private Vector<TaskType> taskList;
    
    /** Holds value of property enabled. */
    private boolean enabled;
    
    /** Holds value of property description. */
    private String description;
    
    /** Holds value of property visible. */
    private boolean visible;
    
    /** Holds value of property toolTipText. */
    private String toolTipText;        
    
    /** Creates a new instance of TaskGroup */
    public TaskGroup(String description) {
        taskList = new Vector<TaskType>(5);
        
        this.description = description;
        this.toolTipText = description;
        
        enabled = visible = true;                
    }
    
    /**
     * Add a sub task group to this TaskGroup
     *
     * @param taskGroup - the task group to be added
     */
    public void add(TaskGroup taskGroup) {
        taskList.add(taskGroup);
    }
    
    /**
     * Add a sub task to this TaskGroup
     *
     * @param task - the task to be added
     */
    public void add(Task task) {
       taskList.add(task);
    }
    
    /** Getter for property taskList.
     * @return Value of property taskList.
     *
     */
    public Vector getTaskList() {
        return this.taskList;
    }
    
    /** Setter for property taskList.
     * @param taskList New value of property taskList.
     *
     */
    public void setTaskList(Vector<TaskType> taskList) {
        this.taskList = taskList;
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
    
} // end of class TaskGroup
