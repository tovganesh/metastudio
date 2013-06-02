/*
 * IDETextPropertyUI.java
 *
 * Created on July 4, 2004, 3:05 PM
 */

package org.meta.shell.idebeans.propertysheet;

import java.awt.*;
import java.util.*;
import java.beans.*;
import java.awt.event.*;

import javax.swing.*;

import java.text.DecimalFormat;

/**
 * A class representing UI for text type.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDETextPropertyUI extends JTextField 
                               implements IDEPropertyUI {
    
    /**
     * Holds value of property property.
     */
    private IDEProperty property;
    
    /**
     * Holds value of property theInstance.
     */
    private Object theInstance;
    
    private Class<?> propertyType;
    
    /**
     * Holds value of property propertyDescriptor.
     */
    private PropertyDescriptor propertyDescriptor;
    
    /** Creates a new instance of IDETextPropertyUI */
    public IDETextPropertyUI() {
        super();        
    }
    
    /** Creates a new instance of IDETextPropertyUI */
    public IDETextPropertyUI(String text) {
        super(text);        
    }
    
    /** 
     * Creates a new instance of IDETextPropertyUI 
     * 
     * @param property - the property that is represented by this field
     * @param theInstance - The instance of class that contains this property
     */
    public IDETextPropertyUI(IDEProperty property, Object theInstance,
                             PropertyDescriptor pd) throws Exception {         
        this.property           = property;
        this.theInstance        = theInstance;
        this.propertyDescriptor = pd;            
        
        setEditable(property.isSetMethod());
        setPropertyText();
    }
    
    /**
     * sets the property text
     */
    private void setPropertyText() throws Exception {        
        if (!property.getPropertyType().isAssignableFrom(double.class)) {
            setText(propertyDescriptor.getReadMethod().invoke(theInstance) 
                                                             .toString()); 
        } else { // doubles are displayed differently!            
            setText(new DecimalFormat("#.#####").format(
               ((Double) propertyDescriptor.getReadMethod().invoke(theInstance)) 
                                                           .doubleValue()));
        } // end if
    }
    
    /**
     * Add change listener for this property, if property is writable
     */
    public void makeChangeUpdatable() {
        if (!property.isSetMethod()) return; // no action !
        
        propertyType = property.getPropertyType();
        
        // well there might be some
        addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ae) {
               if (!property.isSetMethod()) return; // just to make sure ...
               
               try {
                   if (propertyType.isAssignableFrom(String.class)) {
                       propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                                  getText()); 
                   } else if (propertyType.isAssignableFrom(short.class)) {                       
                       propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                          new Short(getText())); 
                   } else if (propertyType.isAssignableFrom(int.class)) {                       
                       propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                        new Integer(getText())); 
                   } else if (propertyType.isAssignableFrom(double.class)) {                       
                       propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                         new Double(getText())); 
                   } // end if
               } catch (Exception e) {
                   System.err.println("Unable to set value : " + e.toString());
                   e.printStackTrace();
                   
                   JOptionPane.showMessageDialog(IDETextPropertyUI.this, 
                       "Unable to set value : " + e.toString(), 
                       "Error setting value", JOptionPane.ERROR_MESSAGE);
               } // end of try .. catch block
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
     * Getter for property value.
     * @return Value of property value.
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
    
} // end of class IDETextPropertyUI
