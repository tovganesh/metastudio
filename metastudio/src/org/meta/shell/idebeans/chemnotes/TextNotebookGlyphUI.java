/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.shell.idebeans.chemnotes;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.meta.chemnotes.ChemNotebook;

/**
 * The UI interface for cnb/text type of NotebookGlyphUI
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TextNotebookGlyphUI extends NotebookGlyphUI {
    
    /**
     * Creates a new instance of TextNotebookGlyphUI
     * 
     * @param id and identifier for the glyph     
     * @param type type of this glyph
     * @param ownerBook the owner ChemBook
     */
    public TextNotebookGlyphUI(String id, ChemNotebook ownerBook) {
        super(id, GlyphType.cnb_text, ownerBook);
        
        setUiClassName(TextNotebookGlyphUI.class.getName());
    }

    /**
     * Create a UI representation of this glyph
     * 
     * @return a JPanel representing the UI for this glyph
     */
    @Override
    public JPanel createUIRepresentation() {
        // TODO:
        JPanel textPanel = new JPanel(new BorderLayout());
        
        textPanel.add(new JTextArea(id), BorderLayout.CENTER);
        
        return textPanel;
    }
}
