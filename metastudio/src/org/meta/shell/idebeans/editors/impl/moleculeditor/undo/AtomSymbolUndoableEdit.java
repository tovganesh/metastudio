/*
 * AtomSymbolUndoableEdit.java 
 *
 * Created on 30 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor.undo;

import javax.swing.undo.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * Class to keep track of changes in atom symbol.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AtomSymbolUndoableEdit extends AbstractUndoableEdit {
        
    private ScreenAtom sAtom;
    private String oldSymbol, newSymbol;
    private MoleculeScene ms;

    /** Creates a new instance of AtomSymbolUndoableEdit */
    public AtomSymbolUndoableEdit(MoleculeScene ms, ScreenAtom sAtom,
                                  String oldSymbol, String newSymbol) {
        this.ms = ms;
        this.sAtom = sAtom;
        this.oldSymbol = oldSymbol;
        this.newSymbol = newSymbol;
    }
    
    /**
     * the redo method
     */
    @Override
    public void redo() {
        super.redo();        
        
        sAtom.getAtom().setSymbol(newSymbol);
        ms.moleculeChanged(null);
    }
    
    /**
     * the undo method
     */
    @Override
    public void undo() {
        super.undo();        
           
        sAtom.getAtom().setSymbol(oldSymbol);
        ms.moleculeChanged(null);
    }    
}
