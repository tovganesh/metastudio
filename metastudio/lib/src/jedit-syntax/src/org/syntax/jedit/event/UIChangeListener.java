/*
 * UIChangeListener.java
 *
 * Created on June 10, 2004, 6:19 AM
 */

package org.syntax.jedit.event;

/**
 * The UI change listener
 *
 * @author  V.Ganesh
 * @version 1.0, jedit-syntax 10th June 2004
 */
public interface UIChangeListener extends java.util.EventListener {
    /**
     * the UI change event!
     */
    public void uiChanged(UIChangeEvent uice);
} // end of interface UIChangeListener
