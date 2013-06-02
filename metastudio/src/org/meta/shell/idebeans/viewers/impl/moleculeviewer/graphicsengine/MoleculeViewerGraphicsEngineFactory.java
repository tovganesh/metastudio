/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine;

import java.util.HashMap;

/**
 * Factory interface for generating instance of actual 
 * MoleculeViewerGraphicsEngine implementations.
 * 
 * This class follows a singleton pattern.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeViewerGraphicsEngineFactory {
    
    public enum MoleculeViewerGraphicsEngineType {DEFAULT, JAVA3D, OPENGL, G3D};
            
    protected HashMap<MoleculeViewerGraphicsEngineType, String> classMap;
    
    private static MoleculeViewerGraphicsEngineFactory _theInstance;
    
    private MoleculeViewerGraphicsEngineFactory() {
        initClassMap();
    }
   
    private void initClassMap() {
        classMap = new HashMap<MoleculeViewerGraphicsEngineType, String>();
        
        classMap.put(MoleculeViewerGraphicsEngineType.JAVA3D, 
                      "org.meta.shell.idebeans.viewers.impl." +
                           "moleculeviewer.graphicsengine.j3d." + 
                           "MoleculeViewerJ3DGraphicsEngine");
    }
    
    /**
     * Return an appropriate instance of the graphics engine. 
     * 
     * @param type the type of the graphics engine required
     * @return an appropriate instance, null if no implementation is found.
     */
    public MoleculeViewerGraphicsEngine getGraphicsEngine(
                                       MoleculeViewerGraphicsEngineType type) {
        try {
            return (MoleculeViewerGraphicsEngine) 
                      Class.forName(classMap.get(type)).newInstance();
        } catch (Exception ex) {
            System.err.println("Exception in " +
                    "MoleculeViewerGraphicsEngineFactory.getGraphicsEngine(): "
                    + ex.toString());
            ex.printStackTrace();
            
            return null;
        }
    }
    
    /**
     * Return the instance of this class
     * 
     * @return the one and only one instance of this class
     */
    public static MoleculeViewerGraphicsEngineFactory getInstance() {
        if (_theInstance == null) {
            _theInstance = new MoleculeViewerGraphicsEngineFactory();
        }
        
        return _theInstance;
    }
}
