/*
 * Workspace.java
 *
 * Created on May 11, 2003, 11:02 AM
 */

package org.meta.workspace;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import org.meta.common.EventListenerList;
import org.meta.common.Utility;
import org.meta.workspace.event.WorkspaceChangeEvent;
import org.meta.workspace.event.WorkspaceChangeListener;
import org.meta.workspace.event.WorkspaceItemChangeEvent;
import org.meta.workspace.event.WorkspaceItemChangeListener;

/**
 * A generic Workspace interface for workspace management in MeTA IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class Workspace implements WorkspaceItemChangeListener {
    
    /** Holds value of property workspaceItems. */
    protected Vector<WorkspaceItem> workspaceItems;
    
    /** Holds value of property workspaceConfigurationFile. */
    protected String workspaceConfigurationFile;
    
    /** Holds value of property ID. */
    protected String ID;
    
    /** Holds value of property version. */
    protected String version;
    
    /** Holds value of property creationDate. */
    protected Date creationDate;
    
    /** Holds value of property lastModifiedDate. */
    protected Date lastModifiedDate;
    
    /** Holds value of property author. */
    protected String author;
    
    /** Holds value of property description. */
    protected String description;
    
    /** Holds value of property implClass. */
    protected String implClass;
    
    /** Holds value of property internalName. */
    protected String internalName;
    
    /** Holds value of property workspaceProperties. */
    protected Vector<WorkspaceProperty> workspaceProperties;
    
    /**
     * Holds value of property dirty.
     */
    protected boolean dirty;
    
    /**
     * Utility field used by event firing mechanism.
     */
    private EventListenerList<WorkspaceChangeListener> listenerList =  null;
    
    private WorkspaceChangeEvent wce;        
    
    /**
     * Holds value of property workspaceDirectory.
     */
    protected String workspaceDirectory;
    
    /** Creates a new instance of Workspace */
    public Workspace(String workspaceConfigurationFile) {
        this.workspaceConfigurationFile = workspaceConfigurationFile;
        
        // set all the default values:                
        this.workspaceItems      = new Vector<WorkspaceItem>();
        this.workspaceProperties = new Vector<WorkspaceProperty>();
                       
        this.ID = Utility.generateWorkspaceID();
        
        // get the current user name
        this.author = System.getProperty("user.name");
        
        // and fit in other information
        this.version          = "1.0";
        this.creationDate     = new Date();
        this.lastModifiedDate = (Date) this.creationDate.clone();             
        this.description      = "No description available";
        this.implClass        = "org.meta.workspace.Workspace";
        this.internalName     = "New Workspace";        
        
        // the default workspace directory
        this.workspaceDirectory = System.getProperty("user.home")
                               + java.io.File.separatorChar + this.internalName;
        
        // the events
        this.wce = new WorkspaceChangeEvent(this);
        
        // state of object on disk .. memory
        this.dirty = false;
    }
    
    /** Getter for property workspaceItems.
     * @return Value of property workspaceItems.
     */
    public Vector<WorkspaceItem> getWorkspaceItems() {
        return workspaceItems;
    }
    
    /**
     * Method to add an WorkspaceItem object.
     *
     * @param workspaceItem the instance of WorkspaceItem to be associated with
     * this workspace
     */    
    public void addWorkspaceItem(WorkspaceItem workspaceItem) {
        workspaceItem.addWorkspaceItemChangeListener(this);
        workspaceItems.add(workspaceItem);        
    }
    
    /**
     * Method to remove an WorkspaceItem object.
     *
     * @param workspaceItem the instance of WorkspaceItem to be associated with
     * this workspace
     * @return true/ false indicating whether operation was successful
     */    
    public boolean removeWorkspaceItem(WorkspaceItem workspaceItem) {
        // delete the file and then remove from memory
        java.io.File wsFile = new java.io.File(
                                       workspaceItem.getWorkspaceItemFile());
        wsFile.delete();
        wsFile.deleteOnExit();
        
        return workspaceItems.remove(workspaceItem);
    }
    
    /**
     * Method to remove an WorkspaceItem object, represented by a path.
     *
     * @param path to workspace item
     * @return true/ false indicating whether operation was successful
     */    
    public boolean removeWorkspaceItem(String path) {
        // delete the workspace item if present
        
        WorkspaceItem workspaceItem;
        java.util.Iterator items = workspaceItems.iterator();
        
        while(items.hasNext()) {
            workspaceItem = ((WorkspaceItem) items.next());
            
            if (workspaceItem.getWorkspaceItemFile().equals(path)) { 
                // we have found it! ... try to remove it
                return workspaceItems.remove(workspaceItem);
            } // end if
        } // end while
        
        return false;
    }
    
    /**
     * Method to add an WorkspaceProperty object.
     *
     * @param workspaceProperty the instance of WorkspaceProperty to be 
     * associated with this workspace
     */    
    public void addWorkspaceProperty(WorkspaceProperty workspaceProperty) {
        workspaceProperties.add(workspaceProperty);
    }
    
    /**
     * Method to remove an WorkspaceProperty object.
     *
     * @param workspaceProperty the instance of WorkspaceProperty to be 
     * associated with this workspace.
     *
     * @return true/ false indicating whether operation was successful
     */    
    public boolean removeWorkspaceProperty(
                                          WorkspaceProperty workspaceProperty) {
        return workspaceProperties.remove(workspaceProperty);
    }
    
    /**
     * method's implementation should open the relevant workspace with all 
     * the saved preferences.
     *
     * @throws WorkspaceIOException in case an error occurs.
     */
    public abstract void open() throws WorkspaceIOException;
    
    /**
     * method's implementation should close the relevant workspace with all 
     * the preferences saved on to the nonvolatile store for further retrieval.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    public abstract void close() throws WorkspaceIOException;
    
    /**
     * method's implementation should save the relevant workspace with all 
     * its preferences in its current state a nonvolatile store for further 
     * retrieval.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    public abstract void save() throws WorkspaceIOException;
    
    /** Getter for property workspaceConfigurationFile.
     * @return Value of property workspaceConfigurationFile.
     */
    public String getWorkspaceConfigurationFile() {
        return this.workspaceConfigurationFile;
    }
    
    /** Setter for property workspaceConfigurationFile.
     * @param workspaceConfigurationFile New value of property 
     *        workspaceConfigurationFile.
     */
    public void setWorkspaceConfigurationFile(
                                         String workspaceConfigurationFile) {
        this.workspaceConfigurationFile = workspaceConfigurationFile;
    }
    
    /**
     * Overrinden toString() method
     */
    @Override
    public String toString() {                
        return "Workspace " + this.internalName;
    }
    
    /**
     * An extended description of this workspace
     */
    public String toExtendedString() {
        StringBuffer about = new StringBuffer();
        
        about.append("Internal Name: " + this.internalName + "\n");
        about.append("ID:            " + this.ID           + "\n");
        about.append("Author:        " + this.author       + "\n");
        about.append("Creation Date: " + this.creationDate.toString() + "\n");
        about.append("Last modified: " + this.lastModifiedDate.toString() 
                                       + "\n");
        about.append("Version:       " + this.version      + "\n");
        about.append("Impl Class:    " + this.implClass    + "\n");
        about.append("Description:   " + this.description  + "\n");
        
        return about.toString();
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
    
    /** Getter for property version.
     * @return Value of property version.
     */
    public String getVersion() {
        return this.version;
    }
    
    /** Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(String version) {
        this.version = version;
        
        this.dirty = true;
        wce.setMessage("Version changed");  
        fireWorkspaceChangeListenerWorkspaceChanged(wce);
    }
    
    /** Getter for property creationDate.
     * @return Value of property creationDate.
     */
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    /** Setter for property creationDate.
     * @param creationDate New value of property creationDate.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    /** Getter for property lastModifiedDate.
     * @return Value of property lastModifiedDate.
     */
    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }
    
    /** Setter for property lastModifiedDate.
     * @param lastModifiedDate New value of property lastModifiedDate.
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    /** Getter for property author.
     * @return Value of property author.
     */
    public String getAuthor() {
        return this.author;
    }
    
    /** Setter for property author.
     * @param author New value of property author.
     */
    public void setAuthor(String author) {
        this.author = author;
        
        this.dirty = true;
        wce.setMessage("Author changed");  
        fireWorkspaceChangeListenerWorkspaceChanged(wce);
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
        
        this.dirty = true;
        wce.setMessage("Description changed");  
        fireWorkspaceChangeListenerWorkspaceChanged(wce);
    }
    
    /** Getter for property implClass.
     * @return Value of property implClass.
     */
    public String getImplClass() {
        return this.implClass;
    }
    
    /** Getter for property internalName.
     * @return Value of property internalName.
     */
    public String getInternalName() {
        return this.internalName;
    }
    
    /** Setter for property internalName.
     * @param internalName New value of property internalName.
     */
    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }
    
    /** Getter for property workspaceProperties.
     * @return Value of property workspaceProperties.
     *
     */
    public Vector<WorkspaceProperty> getWorkspaceProperties() {
        return this.workspaceProperties;
    }
    
    /**
     * Registers WorkspaceChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addWorkspaceChangeListener(
                                             WorkspaceChangeListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<WorkspaceChangeListener>();
        }
        listenerList.add(WorkspaceChangeListener.class, listener);
    }
    
    /**
     * Removes WorkspaceChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeWorkspaceChangeListener(
                                             WorkspaceChangeListener listener) {
        listenerList.remove(WorkspaceChangeListener.class, listener);
    }
    
    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    protected void fireWorkspaceChangeListenerWorkspaceChanged(
                                                   WorkspaceChangeEvent event) {
        if (listenerList == null) return;
        
        for(Object listener : listenerList.getListenerList()) {
            ((WorkspaceChangeListener)listener).workspaceChanged(event);
        } // end for
    }
      
    /**
     * The method called when a workspace item change occurs
     *
     * @param wice - the WorkspaceItemChangeEvent describing the event
     */
    @Override
    public void workspaceItemChanged(WorkspaceItemChangeEvent wice) {
        this.dirty = true;
        wce.setMessage(wice.toString());  
        fireWorkspaceChangeListenerWorkspaceChanged(wce);
    }
    
    /**
     * clean up!!
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    @Override
    public void finalize() throws Throwable {
        Enumeration wi = workspaceItems.elements();
        
        while(wi.hasMoreElements()) {
            ((WorkspaceItem) wi.nextElement())
                                    .removeWorkspaceItemChangeListener(this);
        }
    }
    
    /**
     * Getter for property dirty.
     * @return Value of property dirty.
     */
    public boolean isDirty() {
        return this.dirty;
    }
    
    /**
     * Setter for property dirty.
     * @param dirty New value of property dirty.
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        
        wce.setMessage("Dirty bit changed!");  
        fireWorkspaceChangeListenerWorkspaceChanged(wce);
    }
    
    /**
     * Getter for property workspaceDirectory.
     * @return Value of property workspaceDirectory.
     */
    public String getWorkspaceDirectory() {
        return this.workspaceDirectory;
    }
    
    /**
     * Setter for property workspaceDirectory.
     * @param workspaceDirectory New value of property workspaceDirectory.
     */
    public void setWorkspaceDirectory(String workspaceDirectory) {
        this.workspaceDirectory = workspaceDirectory;
    }
    
} // end of class Workspace
