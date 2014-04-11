/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes.event;

/**
 * Listener interface for NotebookGlyphModifiedEvent
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface NotebookGlyphModifiedListener extends java.util.EventListener {
    /**
     * Callback on a notebook glyph being added
     * 
     * @param ngme the associated event object
     */
    public void notebookGlyphModified(NotebookGlyphModifiedEvent ngme);
}
