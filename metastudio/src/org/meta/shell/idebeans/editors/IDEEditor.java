/*
 * Editor.java
 *
 * Created on October 10, 2004, 10:32 AM
 */

package org.meta.shell.idebeans.editors;

/**
 * All editors in MeTA Studio should implement this interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface IDEEditor {
    
    /**
     * Opens up the specified file in editor.
     *
     * @param fileName the file name (absolute path) to be opened
     */
    public void open(String fileName);
    
    /**
     * Saves the currently loaded file
     *
     * @param fileName the file name (absolute path) to which to save
     */
    public void save(String fileName);
    
    /**
     * Getter for property dirty.
     * @return Value of property dirty.
     */
    public boolean isDirty();
    
} // end of interface IDEEditor
