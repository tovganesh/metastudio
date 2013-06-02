/*
 * IDEPropertyFieldUIFacroty.java
 *
 * Created on July 4, 2004, 3:05 PM
 */

package org.meta.shell.idebeans.propertysheet;

import java.beans.*;

import javax.swing.*;

/**
 * A factory for generating appropriate UI for different classes of IDEProperty.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEPropertyFieldUIFacroty {
    
    /** Creates a new instance of IDEPropertyFieldUIFacroty */
    public IDEPropertyFieldUIFacroty() {
    }
    
    /**
     * returns an appropriate instance of JComponent for a given IDEProperty
     */
    public JComponent getPropertyFieldUI(IDEProperty property, 
                                       Object theInstance, 
                                       PropertyDescriptor pd) throws Exception {
        Class<?> propertyType = property.getPropertyType();
                
        if (propertyType.isAssignableFrom(String.class) 
            || propertyType.isAssignableFrom(int.class)
            || propertyType.isAssignableFrom(double.class)           
            || propertyType.isAssignableFrom(short.class)) {
            // text/ number type property
            if (propertyType.isAssignableFrom(int.class)
                && !(property.isDefaultUI())) {
                IDEIntegerPropertyUI theUI = new IDEIntegerPropertyUI(property, 
                                                               theInstance, pd);
                theUI.makeChangeUpdatable();
            
                return theUI;
            } if (propertyType.isAssignableFrom(double.class)
                && !(property.isDefaultUI())) {
                IDEDoublePropertyUI theUI = new IDEDoublePropertyUI(property, 
                                                               theInstance, pd);
                theUI.makeChangeUpdatable();
            
                return theUI;
            } else {
                IDETextPropertyUI theUI = new IDETextPropertyUI(property, 
                                                               theInstance, pd);
                theUI.makeChangeUpdatable();
            
                return theUI;
            } // end if             
        } else if (propertyType.isAssignableFrom(java.awt.Color.class)) {
            // color type property
            IDEColorPropertyUI theUI = new IDEColorPropertyUI(property, 
                                                            theInstance, pd);
            theUI.makeChangeUpdatable();
            
            return theUI;
        } else if (propertyType.isAssignableFrom(boolean.class)) {
            // boolean type property
            IDEBooleanPropertyUI theUI = new IDEBooleanPropertyUI(property, 
                                                            theInstance, pd);
            theUI.makeChangeUpdatable();
            
            return theUI;
        } else if (pd.getReadMethod().invoke(theInstance) instanceof Enum) {            
            // Enum type property ... Note the criterion to detect Enums
            // here is a bit different from all other types
            IDEEnumPropertyUI theUI = new IDEEnumPropertyUI(property, 
                                                            theInstance, pd);
            theUI.makeChangeUpdatable();
                        
            return theUI;
        } else { // we do not know, so we default to text!               
            IDETextPropertyUI theUI = new IDETextPropertyUI(property, 
                                                            theInstance, pd);
            
            return theUI;
        } // end if
                                                
    }
} // end of class IDEPropertyFieldUIFacroty
