/*
 * AutoSelectionHelper.java 
 *
 * Created on 6 Oct, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

/**
 * Just do an atom selection, automatically!
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AutoSelectionHelper extends MoleculeEditorCommandHelper {

    /** Creates an instance of AutoSelectionHelper */
    public AutoSelectionHelper() {        
    }
    
    /**
     * Update the helper based on the current state of MoleculeScene 
     * and X and Y reference points.
     */
    @Override
    public void update() {
        moleculeScene.selectOnlyThisAtomIndex(
                MoleculeEditorCommonData.getInstance()
                 .getNearestScreenAtom(moleculeScene, referenceX, referenceY)
                .getAtom().getIndex());
    }
    
    /**
     * Clear this helper!
     */
    @Override
    public void clear() {
        // again select, in the process deselect
        moleculeScene.selectAtomIndex(
                MoleculeEditorCommonData.getInstance()
                 .getNearestScreenAtom(moleculeScene, referenceX, referenceY)
                .getAtom().getIndex());
    }
}
