/*
 * WorkspaceItem.java
 *
 * Created on May 11, 2003, 11:07 AM
 */

package org.meta.workspace;

import java.util.Vector;
import org.meta.common.EventListenerList;
import org.meta.common.Utility;
import org.meta.workspace.event.WorkspaceItemChangeEvent;
import org.meta.workspace.event.WorkspaceItemChangeListener;

/**
 * Generic interface for representing an item of Workspace object.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class WorkspaceItem {
    
    /** Holds value of property workspaceItemFile. */
    protected String workspaceItemFile;
    
    /** Holds value of property name. */
    protected String name;
    
    /** Holds value of property ID. */
    protected String ID;
    
    /** Holds value of property implClass. */
    protected String implClass;
    
    /** Holds value of property description. */
    protected String description;
    
    /** Holds value of property type. */
    protected String type;
    
    /** Holds value of property itemData. */
    protected ItemData itemData;
    
    /** Holds value of property workspaceItemProperties. */
    private Vector<WorkspaceItemProperty> workspaceItemProperties;
    
    /**
     * Utility field used by event firing mechanism.
     */
    private EventListenerList<WorkspaceItemChangeListener> listenerList =  null;
    
    /** Creates a new instance of WorkspaceItem */
    public WorkspaceItem(String worspaceItemFile) {
        this.workspaceItemFile = worspaceItemFile;
        
        this.workspaceItemProperties = new Vector<WorkspaceItemProperty>();
        
        // set the default values
        this.name        = "New workspace item";
        this.ID          = Utility.generateWorkspaceItemID();
        this.description = "No description available";
        this.implClass   = "org.meta.workspace.WorkspaceItem";  
        this.type        = "workspace/item";
        this.itemData    = new ItemData();
        
        this.baseDirectory = "";
    }

    /**
     * Method to add an WorkspaceItemProperty object.
     *
     * @param workspaceItemProperty the instance of WorkspaceItemProperty to be 
     * associated with this WorkspaceItem
     */    
    public void addWorkspaceItemProperty(
                                  WorkspaceItemProperty workspaceItemProperty) {
        workspaceItemProperties.add(workspaceItemProperty);
    }
    
    /**
     * Method to remove an WorkspaceItemProperty object.
     *
     * @param workspaceItemProperty the instance of WorkspaceItemProperty to be 
     * associated with this WorkspaceItem.
     *
     * @return true/ false indicating whether operation was successful
     */    
    public boolean removeWorkspaceItemProperty(
                                  WorkspaceItemProperty workspaceItemProperty) {
        return workspaceItemProperties.remove(workspaceItemProperty);
    }
    
    /**
     * method's implementation should open the relevant workspace item with all 
     * the saved preferences.
     *
     * @throws WorkspaceIOException in case an error occurs.
     */
    public abstract void open() throws WorkspaceIOException;
    
    /**
     * method's implementation should close the relevant workspace item with all 
     * the preferences saved on to the nonvolatile store for further retrieval.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    public abstract void close() throws WorkspaceIOException;
    
    /**
     * method's implementation should save the relevant workspace item with all 
     * its preferences in its current state to a nonvolatile store for further 
     * retrieval.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    public abstract void save() throws WorkspaceIOException;
        
    /** Getter for property worspaceItemFile.
     * @return Value of property worspaceItemFile.
     */
    public String getWorkspaceItemFile() {
        return this.workspaceItemFile;
    }
    
    /** Setter for property worspaceItemFile.
     * @param workspaceItemFile New value of property worspaceItemFile.
     */
    public void setWorkspaceItemFile(String workspaceItemFile) {
        this.workspaceItemFile = workspaceItemFile;
    }
    
    /**
     * Overrinden toString() method
     */
    @Override
    public String toString() {        
        return this.name;
    }
    
    /**
     * An extended information on the workspace item
     */
    public String toExtendedString() {
        StringBuilder about = new StringBuilder();
                
        about.append("Name:          ").append(this.name).append("\n");
        about.append("ID:            ").append(this.ID).append("\n");        
        about.append("Impl Class:    ").append(this.implClass).append("\n");
        about.append("Description:   ").append(this.description).append("\n");
        
        return about.toString();
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Getter for property ID.
     * @return Value of property ID.
     */
    public String getID() {
        return this.ID;
    }
    
    /** Setter for property ID.
     * @param ID New value of property ID.
     */
    public void setID(String ID) {
        this.ID = ID;
    }
    
    /** Getter for property implClass.
     * @return Value of property implClass.
     */
    public String getImplClass() {
        return this.implClass;
    }
       
    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
        
    /** Getter for property workspaceItemProperties.
     * @return Value of property workspaceItemProperties.
     *
     */
    public Vector getWorkspaceItemProperties() {
        return this.workspaceItemProperties;
    }
    
    /** Getter for property type.
     * @return Value of property type.
     *
     */
    public String getType() {
        return this.type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     *
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /** Getter for property itemData.
     * @return Value of property itemData.
     *
     */
    public ItemData getItemData() {
        return this.itemData;
    }
    
    /** Setter for property itemData.
     * @param itemData New value of property itemData.
     *
     */
    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }
    
    /**
     * Registers WorkspaceItemChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addWorkspaceItemChangeListener(
                                         WorkspaceItemChangeListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<WorkspaceItemChangeListener>();
        }
        listenerList.add(WorkspaceItemChangeListener.class, listener);
    }
    
    /**
     * Removes WorkspaceItemChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeWorkspaceItemChangeListener(
                                         WorkspaceItemChangeListener listener) {
        listenerList.remove(WorkspaceItemChangeListener.class, listener);
    }
    
    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    protected void fireWorkspaceItemChangeListenerWorkspaceItemChanged(
                                               WorkspaceItemChangeEvent event) {
        if (listenerList == null) return;
        
        for(Object listener : listenerList.getListenerList()) {
            ((WorkspaceItemChangeListener)listener).workspaceItemChanged(event);
        } // end for        
    }
    
    protected String baseDirectory;

    /**
     * Get the value of baseDirectory
     *
     * @return the value of baseDirectory
     */
    public String getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Set the value of baseDirectory
     *
     * @param baseDirectory new value of baseDirectory
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

} // end of class WorkspaceItem
