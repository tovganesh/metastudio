/*
 * IDEPropertySheet.java
 *
 * Created on June 20, 2004, 6:18 PM
 */

package org.meta.shell.idebeans.propertysheet;

import java.util.*;

/**
 * Represents the property sheet of a class.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEPropertySheet {
    
    /**
     * List of property groups that make up this property sheet
     */    
    protected ArrayList<IDEPropertyGroup> propertyGroups;
    
    /**
     * Holds value of property title.
     */
    private String title;
    
    /** Creates a new instance of IDEPropertySheet */
    public IDEPropertySheet() {
        propertyGroups = new ArrayList<IDEPropertyGroup>(10);
    }
    
    /**
     * Adds a property group
     * @param propertyGroup a new property group
     */    
    public void addPropertyGroup(IDEPropertyGroup propertyGroup) {
        propertyGroups.add(propertyGroup);
    }
    
    /**
     * Removes a property group
     * @param propertyGroup property group to be removed
     */    
    public void removePropertyGroup(IDEPropertyGroup propertyGroup) {
        propertyGroups.remove(propertyGroup);
    }
    
    /**
     * Returns the list of property groups associacted with
     * this property sheet.
     * @return An Iterator of property groups
     */    
    public Iterator getPropertyGroups() {
        return propertyGroups.iterator();
    }
    
    /**
     * Getter for property title.
     * @return Value of property title.
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
} // end of class IDEPropertySheet
