/*
 * ItemData.java
 *
 * Created on November 9, 2003, 6:45 AM
 */

package org.meta.workspace;

/**
 * A wrapper class representing workspace item data.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ItemData {
    
    /** Holds value of property data. */
    private Object data;
    
    /** Creates a new instance of ItemData */
    public ItemData() {
        this(null);
    }
    
    public ItemData(Object data) {
        this.data = data;
    }
    
    /** Getter for property data.
     * @return Value of property data.
     *
     */
    public Object getData() {
        return this.data;
    }
    
    /** Setter for property data.
     * @param data New value of property data.
     *
     */
    public void setData(Object data) {
        this.data = data;
    }
    
} // end of class ItemData
