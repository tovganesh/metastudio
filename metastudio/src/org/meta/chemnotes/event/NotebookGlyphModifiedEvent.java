/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes.event;

import org.meta.chemnotes.NotebookGlyph;

/**
 * Event fired when a NotebookGlyph is added to ChemNotebook
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotebookGlyphModifiedEvent extends java.util.EventObject {
    
    /**
     * the type of the modification event
     */
    public enum ModificationType {

        /**
         * glyph was added
         */
        ADDED,
        
        /**
         * glyph was removed
         */
        REMOVED,
        
        /**
         * glyph was edited
         */
        EDITED
    };
    
    private NotebookGlyph notebookGlyph;
    private ModificationType modificationType;
    
    /** Creates a new instance of NotebookGlyphModifiedEvent 
     * @param source the source NotebookGlyph
     */    
    public NotebookGlyphModifiedEvent(Object source) {
        super(source);
        
        notebookGlyph = (NotebookGlyph) getSource();
    }

    /**
     * The modified NotebookGlyph
     * 
     * @return the instance of NotebookGlyph 
     */
    public NotebookGlyph getNotebookGlyph() {
        return notebookGlyph;
    }
    
    /**
     * The type of the modification event
     * 
     * @return the modification type
     */
    public ModificationType getModificationType() {
        return modificationType;
    }

    /**
     * Set the modification type
     * 
     * @param modificationType
     */
    public void setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
    }        
}
