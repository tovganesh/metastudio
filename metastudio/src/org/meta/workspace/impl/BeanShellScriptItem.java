/*
 * BeanShellScriptItem.java
 *
 * Created on October 8, 2004, 7:25 AM
 */

package org.meta.workspace.impl;

import java.io.*;
import org.meta.workspace.WorkspaceIOException;
import org.meta.workspace.WorkspaceItem;

/**
 * A implementation of Bean shell script item object.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BeanShellScriptItem extends WorkspaceItem {
    
    /** Creates a new instance of BeanShellScriptItem */
    public BeanShellScriptItem(String scriptItemFile) {
        super(scriptItemFile);
        
        this.implClass   = 
             "org.meta.workspace.impl.BeanShellScriptItem";
        this.type        = "workspace/beanshell";
        this.name        = scriptItemFile.substring(
                             scriptItemFile.lastIndexOf(File.separatorChar)+1,
                             scriptItemFile.lastIndexOf('.'));
        
        this.itemData.setData(new StringBuffer(""));
    }
    
    /** Setter for property worspaceItemFile.
     * @param workspaceItemFile New value of property worspaceItemFile.
     */
    @Override
    public void setWorkspaceItemFile(String workspaceItemFile) {
        super.setWorkspaceItemFile(workspaceItemFile);
        
        this.name = workspaceItemFile.substring(
                       workspaceItemFile.lastIndexOf(File.separatorChar)+1,
                       workspaceItemFile.lastIndexOf('.'));
    }
    
    /**
     * method's implementation should close the relevant workspace item with all 
     * the preferences saved on to the nonvolatile store for further retrieval.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void close() throws WorkspaceIOException {
        // TODO: not implemented 
    }
    
    /**
     * method's implementation should open the relevant workspace item with all 
     * the saved preferences.
     *
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void open() throws WorkspaceIOException {
        // TODO: not implemented, is this needed?
    }
    
    /**
     * method's implementation should save the relevant workspace item with all 
     * its preferences in its current state to a nonvolatile store for further 
     * retrieval. 
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void save() throws WorkspaceIOException {
        // TODO: not implemented, is this needed?
    }
    
} // end of class BeanShellScriptItem
