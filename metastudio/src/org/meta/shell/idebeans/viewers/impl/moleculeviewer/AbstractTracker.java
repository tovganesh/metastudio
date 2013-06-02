/*
 * AbstractTracker.java
 *
 * Created on January 22, 2004, 7:26 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.graphics.AbstractGlyph;

/**
 * Abstract implementation of Tracker interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractTracker extends AbstractGlyph implements Tracker {       
    
    /** Holds value of property screenLabel. */
    protected ScreenLabel screenLabel;
    
    protected boolean isolated;
    
    /** Creates a new instance of AbstractTracker */
    public AbstractTracker() {
        super();
        
        // by default a tracker is not isolated
        isolated = false;
    }
     
    /**
     * Is this tracker isolated, in such a case a tranformation needs
     * to be separately applied to it.
     * 
     * @return true / false
     */
    @Override
    public boolean isIsolated() {
        return isolated;
    }
    
    /**
     * Sets the current status of this tracker.
     * 
     * @param isolated is it?
     */
    @Override
    public void setIsolated(boolean isolated) {
        isolated = true;
    }
    
    /** Getter for property selected.
     * @return Value of property selected.
     *
     */
    @Override
    public boolean isSelected() {
        return this.selected;
    }
    
    /** Setter for property selected.
     * @param selected New value of property selected.
     *
     */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /** Getter for property visible.
     * @return Value of property visible.
     *
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    /** Setter for property visible.
     * @param visible New value of property visible.
     *
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
       
    /** Getter for property screenLabel.
     * @return Value of property screenLabel.
     *
     */
    @Override
    public ScreenLabel getScreenLabel() {
        return this.screenLabel;
    }
    
    /** Setter for property screenLabel.
     * @param screenLabel New value of property screenLabel.
     *
     */
    @Override
    public void setScreenLabel(ScreenLabel screenLabel) {
        this.screenLabel = screenLabel;
    }
    
} // end of class AbstractTracker
