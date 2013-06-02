/**
 * MoleculeDisplayModel.java
 *
 * Created on May 20, 2009
 */
package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine;

/**
 * Supported display models for molecules.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum MoleculeDisplayModel {
    LINE {
        @Override
        public String toString() { return "Line"; }
    },
    STICK {
        @Override
        public String toString() { return "Stick"; }
    },
    BALL_AND_STICK {
        @Override
        public String toString() { return "Ball and Stick"; }
    },
    VDW {
        @Override
        public String toString() { return "Space fill"; }
    },
    CPK {
        @Override
        public String toString() { return "CPK"; }
    }        
}
