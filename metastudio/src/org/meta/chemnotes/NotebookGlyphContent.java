/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes;

import java.io.Serializable;

/**
 * Content of NotebookGlyph
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotebookGlyphContent {
    
    private Serializable content;
    private Object originalSource;
    
    /** Creates a new instance of NotebookGlyphContent */
    public NotebookGlyphContent() {        
    }

    /**
     * Get the content of notebook glyph
     * 
     * @return the content
     */
    public Serializable getContent() {
        return content;
    }

    /**
     * Set the content of notebook glyph
     * 
     * @param content the new content
     */
    public void setContent(Serializable content) {
        this.content = content;
    }

    /**
     * Get original source, if any
     * 
     * @return original source
     */
    public Object getOriginalSource() {
        return originalSource;
    }

    /**
     * Set the original source
     * 
     * @param originalSource the new original source
     */
    public void setOriginalSource(Object originalSource) {
        this.originalSource = originalSource;
    }        
}
