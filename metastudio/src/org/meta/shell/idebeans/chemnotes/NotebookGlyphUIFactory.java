/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.shell.idebeans.chemnotes;

import org.meta.chemnotes.ChemNotebook;
import org.meta.chemnotes.NotebookGlyph.GlyphType;

/**
 * Factory class to generate instances of UI class handling a particular type
 * of NotebookGlyph. Follows a singleton pattern.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotebookGlyphUIFactory {
    private static NotebookGlyphUIFactory _theInstance;
    
    /** Create a new instance of NotebookGlyphUIFactory */
    private NotebookGlyphUIFactory() {
        
    }
    
    /**
     * The instance of this factory
     * 
     * @return the instance of this factory
     */
    public static NotebookGlyphUIFactory getInstance() {
        if (_theInstance == null)
            _theInstance = new NotebookGlyphUIFactory();
        
        return _theInstance;                
    }
        
    /**
     * The UI instance of a glyph type is returned
     * 
     * @param id an id attached to the glyph
     * @param type the type of the glyph
     * @param ownerBook the owner of the glyph object
     * @param x the x position of new glyph
     * @param y the y position of new glyph
     * @param width the width of the glyph
     * @param height the height of the glyph     
     * @return the instance of NotebookGlyphUI
     */
    public NotebookGlyphUI createInstance(String id, GlyphType type, 
                                          ChemNotebook ownerBook, 
                                          int x, int y, int height, int width) {
        // TODO: 
        switch(type) {
            case cnb_text:
            default:
            {                
                NotebookGlyphUI nbui = new TextNotebookGlyphUI(id, ownerBook);
                                
                nbui.setX(x);
                nbui.setY(y);
                nbui.setHeight(width);
                nbui.setWidth(height);        
                
                ownerBook.addNotebookGlyph(nbui);
                return nbui;
            }
        } // end of switch .. case
    }
}
