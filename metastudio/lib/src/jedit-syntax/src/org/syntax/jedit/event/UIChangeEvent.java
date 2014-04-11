/*
 * UIChangeEvent.java
 *
 * Created on June 10, 2004, 6:17 AM
 */

package org.syntax.jedit.event;

/**
 * The UI change event
 *
 * @author  V.Ganesh
 * @version 1.0, jedit-syntax 10th June 2004
 */
public class UIChangeEvent extends java.util.EventObject {
    
    /** Creates a new instance of UIChangeEvent */
    public UIChangeEvent(Object source) {
        super(source);
    }
    
} // end of class UIChangeEvent
