/*
 * MoleculePanel.java
 *
 * Created on January 7, 2007, 1:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import javax.swing.JPanel;
import org.meta.shell.ide.MeTA;

/**
 * Tree like info of the currently loaded molecule. Is displayed even if 
 * the molecule is not a part of a workspace.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculePanel extends JPanel {
    
    private MeTA ideInstance;
     
    /** Creates a new instance of MoleculePanel */
    public MoleculePanel(MeTA ideInstance) { 
        this.ideInstance = ideInstance;
    }
    
    // TODO:
} // end of class MoleculePanel
