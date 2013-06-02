/*
 * PropertyInterrogataionListener.java
 *
 * Created on October 12, 2007, 8:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.event;

/**
 * The listherner interface for property interrogation event.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface PropertyInterrogataionListener 
                 extends java.util.EventListener {
    /**
     * the interface for call back when an interrogation event occurs
     *
     * @param pie the interrogation even object
     */
    public void interrogatedValue(PropertyInterrogataionEvent pie);
} // end of interface PropertyInterrogataionListener
