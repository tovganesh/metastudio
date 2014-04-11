/*
 * MoleculeIDEFilePreviewer.java
 *
 * Created on August 15, 2005, 1:05 PM
 *
 */

package org.meta.shell.idebeans;

import java.io.*;        
import java.awt.*;
import org.meta.common.Utility;
import org.meta.common.resource.MiscResource;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.moleculereader.MoleculeFileReader;
import org.meta.moleculereader.MoleculeFileReaderFactory;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.SelectionState;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.TransformState;

/**
 * Generate a quick preview of a selected molecule in background.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeIDEFilePreviewer extends IDEFilePreviewer {
    
    /** the instance of MoleculeViewer */
    private MoleculeViewer moleculeViewer;
    
    private static final int LARGE_MOLECULE 
      = MiscResource.getInstance().getSimpleConnectivityMoleculeBuilderCutoff();
    
    /** Creates a new instance of MoleculeIDEFilePreviewer */
    public MoleculeIDEFilePreviewer() {
        super();
        
        // set initial state for molecular viewer
        moleculeViewer = new MoleculeViewer(null, null);
        moleculeViewer.setSelectionState(SelectionState.NO_STATE);
        moleculeViewer.setTransformState(TransformState.ROTATE_STATE);
        
        setLayout(new BorderLayout());
        add(moleculeViewer, BorderLayout.CENTER);         
    }
    
    /**
     * Set the current file to preview.
     */
    @Override
    public void setFileToPreview(File file) {                         
        try {
            MoleculeFileReaderFactory mfr = (MoleculeFileReaderFactory)
                    Utility.getDefaultImplFor(
                                 MoleculeFileReaderFactory.class).newInstance();
            
            String fileName = file.getAbsolutePath();
            String typ = fileName.substring(fileName.lastIndexOf(".")+1, 
                                            fileName.length());
    
            MoleculeFileReader rdr = mfr.getReader(typ);
    
            BufferedReader br = new BufferedReader(new InputStreamReader(
                                                    new FileInputStream(file)));
            
            Molecule mol = rdr.readMolecule(br);
            
            MoleculeBuilder mb = (MoleculeBuilder) Utility.getDefaultImplFor(
                                           MoleculeBuilder.class).newInstance();
            
            // skip connectivity for very large molecules
            if (mol.getNumberOfAtoms() <= LARGE_MOLECULE) 
                mb.makeSimpleConnectivity(mol);                        
                        
            if (moleculeViewer.getSceneList().size() != 0) {
                moleculeViewer.removeScene(
                                     moleculeViewer.getSceneList().get(0));
            } // end if            
            
            moleculeViewer.addScene(new MoleculeScene(mol));
                             
            updateUI();
            moleculeViewer.repaint();
        } catch (Exception ignored) {
            // just pass on...
            System.out.println("Error generating preview: " + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block                                
    }
} // end of class MoleculeIDEFilePreviewer
