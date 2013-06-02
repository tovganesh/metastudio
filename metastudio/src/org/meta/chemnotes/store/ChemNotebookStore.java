/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes.store;

import java.io.IOException;
import org.meta.chemnotes.ChemNotebook;
import org.meta.chemnotes.event.NotebookModificationListener;

/**
 * The storage interface for ChemNotebook
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class ChemNotebookStore 
                      implements NotebookModificationListener {

    protected ChemNotebook chemNotebook;
    
    /** Creates new instance of ChemNotebookStore
     * @param chemNotebook the object of ChemNotebook to be associated
     * @throws IOException if error encountered while associating with storage
     */
    public ChemNotebookStore(ChemNotebook chemNotebook) throws IOException {
        this.chemNotebook = chemNotebook;
        
        load();
        
        this.chemNotebook.addNotebookModificationListener(this);
    }

    /**
     * Get the associated chemnotebook
     * 
     * @return the instance of associated chemnotebook
     */
    public ChemNotebook getChemNotebook() {
        return chemNotebook;
    }
    
    /**
     * Load the chembook into memory representation object     
     * @throws IOException if error encountered while associating with storage
     */
    public abstract void load() throws IOException;
}
