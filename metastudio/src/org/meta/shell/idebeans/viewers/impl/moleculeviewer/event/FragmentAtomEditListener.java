/*
 * FragmentAtomEditListener.java
 *
 * Created on August 18, 2007, 10:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

/**
 * Listener event for an edit of fragment atom.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FragmentAtomEditListener {
    
    /** 
     * callback on fragment atom edit event
     *
     * @param faee is the object describing the event
     */
    public void fragmentAtomEdited(FragmentAtomEditEvent faee);
    
} // end of interface FragmentAtomEditListener
