/*
 * IDEColorPropertyUI.java
 *
 * Created on September 18, 2004, 6:31 PM
 */

package org.meta.shell.idebeans.propertysheet;

import java.awt.*;
import java.util.*;
import java.beans.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * A class representing UI for java.awt.Color type.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEColorPropertyUI extends JButton 
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
                                    
    /** Creates a new instance of IDEColorPropertyUI */
    public IDEColorPropertyUI() {
        super();        
    }
    
    /** Creates a new instance of IDEColorPropertyUI */
    public IDEColorPropertyUI(String text) {
        super(text);        
    }
    
    /** 
     * Creates a new instance of IDEColorPropertyUI
     * 
     * @param property - the property that is represented by this field
     * @param theInstance - The instance of class that contains this property
     */
    public IDEColorPropertyUI(IDEProperty property, Object theInstance,
                             PropertyDescriptor pd) throws Exception {         
        this.property           = property;
        this.theInstance        = theInstance;
        this.propertyDescriptor = pd;
        
        setPropertyColor();
    }
    
    /**
     * set the color property
     */
    private void setPropertyColor() throws Exception {
        if (!property.getPropertyType().isAssignableFrom(Color.class)) {
            throw new Exception("Invalid property type : " 
                                + property.getPropertyType());
        } // end if
        
        setBackground((Color) propertyDescriptor.getReadMethod().invoke(
                                                                  theInstance));
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
               
               // get the old color
               Color oldColor = getBackground();
               
               // show a color dialog
               JColorChooser cc = new JColorChooser();               
               
               Color newColor = cc.showDialog(IDEColorPropertyUI.this,
                         "Choose a " + property.getDisplayName(), oldColor);
               
               if (newColor != null) {
                   // set the new property (color) value
                   try {
                       propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                                  newColor); 
                       setBackground(newColor);
                   } catch (Exception e) {
                       System.err.println("Unable to set value : " 
                                           + e.toString());
                       e.printStackTrace();
                       
                       JOptionPane.showMessageDialog(IDEColorPropertyUI.this,
                          "Unable to set value : " + e.toString(),
                          "Error setting value", JOptionPane.ERROR_MESSAGE);
                   }  // end of try .. catch block               
               } // end if
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
    
} // end of class IDEColorPropertyUI
