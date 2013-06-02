/*
 * WorkspaceIOException.java
 *
 * Created on May 18, 2003, 11:39 AM
 */

package org.meta.workspace;

/**
 * The general exception thrown by the implementation classes of the 
 * Workspace and WorkspaceItem interfaces incase of in i/o error.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WorkspaceIOException extends java.io.IOException {
    
    /**
     * Creates a new instance of <code>WorkspaceIOException</code> 
     * without detail message.
     */
    public WorkspaceIOException() {
    }
        
    /**
     * Constructs an instance of <code>WorkspaceIOException</code> with 
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public WorkspaceIOException(String msg) {
        super(msg);
    }
    
    public String toString() {
        return super.toString();
    }
    
} // end of class WorkspaceIOException
