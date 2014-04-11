/*
 * IDEPropertyUI.java
 *
 * Created on September 18, 2004, 6:24 PM
 */

package org.meta.shell.idebeans.propertysheet;

/**
 * All the property UI componants should implement this interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface IDEPropertyUI {
    
    /**
     * Add change listener for this property, if property is writable
     */
    public void makeChangeUpdatable();
    
} // end of interface IDEPropertyUI
