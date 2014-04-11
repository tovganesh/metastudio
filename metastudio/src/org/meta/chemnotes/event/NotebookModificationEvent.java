/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes.event;

/**
 * Any modification to the ChemNotebook
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotebookModificationEvent extends java.util.EventObject {
    
    private boolean glyphModifedEvent;
    
    private NotebookGlyphModifiedEvent glyphModificationEvent;
    
    /** Creates a new instance of NotebookModificationEvent 
     * @param source is the instance of ChemNotebook being modified
     */    
    public NotebookModificationEvent(Object source) {
        super(source);
        
        glyphModifedEvent = false;
    }

    /**
     * Is this a glyph modification event?
     * 
     * @return true if yes, false if no
     */
    public boolean isGlyphModifedEvent() {
        return glyphModifedEvent;
    }

    /**
     * Set if this is a glyph modification event
     * 
     * @param glyphModifedEvent a flag vale to true / false
     */
    public void setGlyphModifedEvent(boolean glyphModifedEvent) {
        this.glyphModifedEvent = glyphModifedEvent;
    }    

    /**
     * Get the glyph modification event object if this is a glyph modification event
     * 
     * @return glyph modification event object
     */
    public NotebookGlyphModifiedEvent getGlyphModificationEvent() {
        return glyphModificationEvent;
    }

    /**
     * Set the glyph modification object 
     * 
     * @param glyphModificationEvent the new glyph modification object
     */
    public void setGlyphModificationEvent(NotebookGlyphModifiedEvent glyphModificationEvent) {
        this.glyphModificationEvent = glyphModificationEvent;
    }    
}
