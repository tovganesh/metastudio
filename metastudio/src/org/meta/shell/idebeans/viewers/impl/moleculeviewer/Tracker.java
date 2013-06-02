/*
 * Tracker.java
 *
 * Created on January 18, 2004, 10:09 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.graphics.Glyph;

/**
 * Represents a generic tracker.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Tracker extends Glyph {
    
    /** Getter for property screenLabel.
     * @return Value of property screenLabel.
     *
     */
    public ScreenLabel getScreenLabel();
    
    /** Setter for property screenLabel.
     * @param screenLabel New value of property screenLabel.
     *
     */
    public void setScreenLabel(ScreenLabel screenLabel);
    
    /**
     * Is this tracker isolated, in such a case a transformation needs
     * to be separately applied to it.
     * 
     * @return true / false
     */
    public boolean isIsolated();
    
    /**
     * Sets the current status of this tracker.
     * 
     * @param isolated is it?
     */
    public void setIsolated(boolean isolated);
} // end of interface Tracker
