/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

/**
 * Provides support for intantiating stock UI for providing
 * additional functionalities for the MoleculeViewer.
 * Follows a Singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeViewerStockUIFactory {

    private static MoleculeViewerStockUIFactory _theInstance;

    /** Create instance of MoleculeViewerStockUIFactory */
    private MoleculeViewerStockUIFactory() {
    }

    /**
     * Get an instance of this class
     *
     * @return instance of MoleculeViewerStockUIFactory
     */
    public static MoleculeViewerStockUIFactory getInstance() {
        if (_theInstance == null) {
            _theInstance = new MoleculeViewerStockUIFactory();
        } // end if

        return _theInstance;
    }

    /**
     * Return a the defaut implemenation for MoleculeViewerStockUIFactory
     * @return instance of MoleculeViewerAdditionalInfoPanel
     */
    public MoleculeViewerAdditionalInfoPanel getDefaultAdditionalInfoPanel() {
        String deafultAdditionalInfoPanel = "org.meta.shell.idebeans.viewers" +
                ".impl.moleculeviewer.DefaultMoleculeViewerAdditionalInfoPanel";

        try {
            return (MoleculeViewerAdditionalInfoPanel)
                      Class.forName(deafultAdditionalInfoPanel).newInstance();
        } catch (Exception ex) {
            System.err.println("Exception in " +
                "MoleculeViewerStockUIFactory.getDefaultAdditionalInfoPanel(): "
                + ex.toString());
            ex.printStackTrace();

            return null;
        }
    }
}
