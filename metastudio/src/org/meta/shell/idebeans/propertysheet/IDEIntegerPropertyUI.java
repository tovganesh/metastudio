/*
 * IDEIntegerPropertyUI.java
 *
 * Created on September 18, 2005, 7:57 PM
 *
 */

package org.meta.shell.idebeans.propertysheet;

import java.beans.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.*;
import org.meta.common.Utility;

/**
 * Special UI for handling Integers with a JSpinner control.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEIntegerPropertyUI extends JSpinner  
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
    
    private SpinnerNumberModel integerModel;
            
    /** Creates a new instance of IDEIntegerPropertyUI */
    public IDEIntegerPropertyUI() {
        super();
    }
    
    /** 
     * Creates a new instance of IDEIntegerPropertyUI
     * 
     * @param property - the property that is represented by this field
     * @param theInstance - The instance of class that contains this property
     */
    public IDEIntegerPropertyUI(IDEProperty property, Object theInstance,
                                PropertyDescriptor pd) throws Exception {         
        this.property           = property;
        this.theInstance        = theInstance;
        this.propertyDescriptor = pd;
        
        // add the item s to the combobox and then select appropriate item
        Method m = theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Max", 
                                                  new Class[0]);
        Integer maxVal = (Integer) m.invoke(theInstance, new Object[0]);
        
        m =  theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Min", 
                                                  new Class[0]);
        Integer minVal = (Integer) m.invoke(theInstance, new Object[0]); 
        
        integerModel = new SpinnerNumberModel((Number)
                                              propertyDescriptor.getReadMethod()
                                                .invoke(theInstance), 
                                              minVal.intValue(), 
                                              maxVal.intValue(), 1);
        setModel(integerModel);
        setEnabled(property.isSetMethod());
    }        
    
    /**
     * Add change listener for this property, if property is writable
     */
    public void makeChangeUpdatable() {
        if (!property.isSetMethod()) return; // no action !
        
        // well there might be some
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
               if (!property.isSetMethod()) return; // just to make sure ...
               
               // set the new property (integer) value
               try {                           
                   propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                          (Integer) getValue());
                   
                   // update the min and max
                   Method m = theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Max", 
                                                  new Class[0]);
                   Integer maxVal = (Integer) m.invoke(theInstance, 
                                                       new Object[0]);
        
                   m =  theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Min", 
                                                  new Class[0]);
                   Integer minVal = (Integer) m.invoke(theInstance, 
                                                       new Object[0]);
                   
                   integerModel.setMaximum(maxVal);
                   integerModel.setMinimum(minVal);
               } catch (Exception e) {
                   System.err.println("Unable to set value : " 
                                       + e.toString());
                   e.printStackTrace();

                   JOptionPane.showMessageDialog(IDEIntegerPropertyUI.this,
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
    
} // end of class IDEIntegerPropertyUI
