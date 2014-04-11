/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.shell.idebeans.chemnotes;

import javax.swing.JPanel;
import org.meta.chemnotes.ChemNotebook;
import org.meta.chemnotes.NotebookGlyph;

/**
 * The UI interface for NotebookGlyphUI
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class NotebookGlyphUI extends NotebookGlyph {

    /**
     * Creates a new instance of NotebookGlyphUI
     * 
     * @param id and identifier for the glyph     
     * @param type type of this glyph
     * @param ownerBook the owner ChemBook
     */
    public NotebookGlyphUI(String id, GlyphType type, ChemNotebook ownerBook) {
        super(id, NotebookGlyphUI.class.getName(), type, ownerBook);
    }
    
    /**
     * Create a UI representation of this glyph
     * 
     * @return a JPanel representing the UI for this glyph
     */
    public abstract JPanel createUIRepresentation();
}
