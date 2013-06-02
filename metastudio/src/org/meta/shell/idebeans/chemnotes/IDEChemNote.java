/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.shell.idebeans.chemnotes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.meta.chemnotes.ChemNotebook;
import org.meta.chemnotes.store.ChemNotebookFileStore;
import org.meta.chemnotes.store.ChemNotebookStore;
import org.meta.common.resource.StringResource;

/**
 * Fast way to note and record all kinds of activities in MeTA Studio
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEChemNote extends JPanel {
    private JPanel titlePanel;
    private JLabel title;
    
    private NotebookArea notebookArea;
    
    private ChemNotebook chemNotebook;
    private ChemNotebookStore chemNoteStore;
            
    /** Create new instance of IDEChemNote */
    public IDEChemNote() {        
        init();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            title = new JLabel("ChemNote - note everything!");
            titlePanel.add(title);
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.black));
            
        add(titlePanel, BorderLayout.NORTH);
        
        chemNotebook = new ChemNotebook("Unfiled");
        
        StringResource strings = StringResource.getInstance();
                
        try {
            chemNoteStore = new ChemNotebookFileStore(new File(strings.getAppDir() + "unfiledchemnotes.mcn"), 
                                                      chemNotebook);
            chemNoteStore.load();
        } catch(Exception e) {
            System.err.println("Unable to load chemnotes from file store! : " + e.toString());
            e.printStackTrace();
        } // end try .. catch block
        
        notebookArea = new NotebookArea(chemNotebook);
        add(notebookArea, BorderLayout.CENTER);
    }
}
