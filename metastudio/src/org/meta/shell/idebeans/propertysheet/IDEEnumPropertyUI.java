/*
 * IDEEnumPropertyUI.java
 *
 * Created on September 18, 2005, 4:54 PM
 *
 */

package org.meta.shell.idebeans.propertysheet;

import java.awt.*;
import java.util.*;
import java.beans.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;

/**
 * A class representing UI for Enum type.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEEnumPropertyUI extends JComboBox  
                                  implements IDEPropertyUI {
    
    /**
     * Holds value of property property.
     */
    private IDEProperty property;

    /**
     * Holds value of property theInstance.
     */
    private Object theInstance;

    /**
     * Holds value of property propertyDescriptor.
     */
    private PropertyDescriptor propertyDescriptor;
    
    /** Creates a new instance of IDEEnumPropertyUI */
    public IDEEnumPropertyUI() {
        super();
    }
    
    /** 
     * Creates a new instance of IDEEnumPropertyUI
     * 
     * @param property - the property that is represented by this field
     * @param theInstance - The instance of class that contains this property
     */
    public IDEEnumPropertyUI(IDEProperty property, Object theInstance,
                             PropertyDescriptor pd) throws Exception {         
        this.property           = property;
        this.theInstance        = theInstance;
        this.propertyDescriptor = pd;
        
        // add the item s to the combobox and then select appropriate item
        Enum e = (Enum) propertyDescriptor.getReadMethod().invoke(theInstance);                
        Method m = e.getClass().getDeclaredMethod("values", new Class[0]);
        Object [] values = (Object []) m.invoke(e, new Object[0]);
        
        for(Object value : values) {
            addItem(value);
        } // end for
                        
        setPropertyEnum();
        
        setEnabled(property.isSetMethod());
    }
    
    /**
     * sets the initial property value
     */
    private void setPropertyEnum() throws Exception {
        if (!(propertyDescriptor.getReadMethod().invoke(theInstance) 
               instanceof Enum)) {
            throw new Exception("Invalid property type : " 
                                + property.getPropertyType());
        } // end if
        
        setSelectedItem(propertyDescriptor.getReadMethod().invoke(theInstance));
    }
    
    /**
     * Add change listener for this property, if property is writable
     */
    public void makeChangeUpdatable() {
        if (!property.isSetMethod()) return; // no action !
        
        // well there might be some
        addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ae) {
               if (!property.isSetMethod()) return; // just to make sure ...
               
               // set the new property (color) value
               try {                   
                   propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                             getSelectedItem());
               } catch (Exception e) {
                   System.err.println("Unable to set value : " 
                                       + e.toString());
                   e.printStackTrace();

                   JOptionPane.showMessageDialog(IDEEnumPropertyUI.this,
                      "Unable to set value : " + e.toString(),
                      "Error setting value", JOptionPane.ERROR_MESSAGE);
               }  // end of try .. catch block 
           }
        });
    }
    
    /**
     * Getter for property property.
     * @return Value of property property.
     */
    public IDEProperty getProperty() {
        return this.property;
    }
    
    /**
     * Getter for property theInstance.
     * @return Value of property theInstance.
     */
    public Object getTheInstance() {
        return this.theInstance;
    }
    
    /**
     * Getter for property propertyDescriptor.
     * @return Value of property propertyDescriptor.
     */
    public PropertyDescriptor getPropertyDescriptor() {
        return this.propertyDescriptor;
    }
    
} // end of class IDEEnumPropertyUI
