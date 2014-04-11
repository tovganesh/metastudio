/*
 * IDEPropertyGroup.java
 *
 * Created on June 20, 2004, 6:21 PM
 */

package org.meta.shell.idebeans.propertysheet;

import java.util.*;

/**
 * Represents a property group contained in a property sheet.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEPropertyGroup {
    
    /**
     * Holds value of property name.
     */
    private String name;
    
    /**
     * List of properties that make up this property group
     */    
    protected ArrayList<IDEProperty> properties;
    
    /** Creates a new instance of IDEPropertyGroup */
    public IDEPropertyGroup() {
        properties = new ArrayList<IDEProperty>(10);
    }
    
    /**
     * Adds a property
     * @param property a new property for this group
     */    
    public void addProperty(IDEProperty property) {
        properties.add(property);
    }
    
    /**
     * Removes a property
     * @param property property to be removed from this group
     */    
    public void removeProperty(IDEProperty property) {
        properties.remove(property);
    }
    
    /**
     * Returns the list of properties associacted with
     * this property group.
     * @return An Iterator of properties
     */    
    public Iterator getProperties() {
        return properties.iterator();
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
} // end of class IDEPropertyGroup
