/*
 * BuildState.java 
 *
 * Created on 14 Jul, 2008 
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ViewerState;

/**
 * The build state of the molecule editor.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum BuildState implements ViewerState {
    PENCIL_TOOL,
    STRAIGHT_CHAIN,
    PLANAR_RING,
    NON_PLANAR_RING,
    ORIENTATION_CHANGE
}
