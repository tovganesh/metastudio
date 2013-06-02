/*
 * ScreenBond.java
 *
 * Created on January 18, 2004, 10:06 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;


import org.meta.shell.idebeans.graphics.AbstractGlyph;
import org.meta.common.resource.ColorResource;

/**
 * Represents a bond between two atoms on screen.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class ScreenBond extends AbstractGlyph {
    
    /** Holds value of property atom1. */
    protected ScreenAtom atom1;
    
    /** Holds value of property atom2. */
    protected ScreenAtom atom2;
    
    /** Creates a new instance of ScreenBond */
    public ScreenBond(ScreenAtom atom1, ScreenAtom atom2) {
        super();
        
        this.atom1 = atom1;
        this.atom2 = atom2;
        
        selectionColor = ColorResource.getInstance().getSelectedBondColor();
    }
            
    /** Getter for property atom1.
     * @return Value of property atom1.
     *
     */
    public ScreenAtom getAtom1() {
        return this.atom1;
    }
    
    /** Setter for property atom1.
     * @param atom1 New value of property atom1.
     *
     */
    public void setAtom1(ScreenAtom atom1) {
        this.atom1 = atom1;
    }
    
    /** Getter for property atom2.
     * @return Value of property atom2.
     *
     */
    public ScreenAtom getAtom2() {
        return this.atom2;
    }
    
    /** Setter for property atom2.
     * @param atom2 New value of property atom2.
     *
     */
    public void setAtom2(ScreenAtom atom2) {
        this.atom2 = atom2;
    }     
    
} // end of class ScreenBond
