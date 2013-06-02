/*
 * IDEDoublePropertyUI.java
 *
 * Created on July 16, 2007, 7:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.propertysheet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.meta.common.Utility;

/**
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEDoublePropertyUI extends JSpinner  
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
    
    private SpinnerNumberModel doubleModel;
    
    /** Creates a new instance of IDEDoublePropertyUI */
    public IDEDoublePropertyUI() {
        super();
    }
    
    /** 
     * Creates a new instance of IDEDoublePropertyUI
     * 
     * @param property - the property that is represented by this field
     * @param theInstance - The instance of class that contains this property
     */
    public IDEDoublePropertyUI(IDEProperty property, Object theInstance,
                                PropertyDescriptor pd) throws Exception {         
        this.property           = property;
        this.theInstance        = theInstance;
        this.propertyDescriptor = pd;
        
        // add the item s to the combobox and then select appropriate item
        Method m = theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Max", 
                                                  new Class[0]);
        Double maxVal = (Double) m.invoke(theInstance, new Object[0]);
        
        m =  theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Min", 
                                                  new Class[0]);
        Double minVal = (Double) m.invoke(theInstance, new Object[0]); 
        
        m =  theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) 
                                + "Increment", new Class[0]);
        Double increment = (Double) m.invoke(theInstance, new Object[0]); 
        
        doubleModel = new SpinnerNumberModel((Number)
                                              propertyDescriptor.getReadMethod()
                                                 .invoke(theInstance), 
                                             minVal.doubleValue(), 
                                             maxVal.doubleValue(), 
                                             increment.doubleValue());        
        
        setModel(doubleModel);
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
               
               // set the new property (double) value
               try {                           
                   propertyDescriptor.getWriteMethod().invoke(theInstance, 
                                                          (Double) getValue());
                   
                   // update the min and max
                   Method m = theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Max", 
                                                  new Class[0]);
                   Double maxVal = (Double) m.invoke(theInstance, 
                                                       new Object[0]);
        
                   m =  theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) + "Min", 
                                                  new Class[0]);
                   Double minVal = (Double) m.invoke(theInstance, 
                                                       new Object[0]);
                   
                   m =  theInstance.getClass().getDeclaredMethod("get" + 
                                Utility.capitalise(property.getName()) 
                                + "Increment", new Class[0]);
                   Double increment = (Double) m.invoke(theInstance, 
                                                        new Object[0]); 
                   
                   doubleModel.setMaximum(maxVal);
                   doubleModel.setMinimum(minVal);
                   doubleModel.setStepSize(increment);
               } catch (Exception e) {
                   System.err.println("Unable to set value : " 
                                       + e.toString());
                   e.printStackTrace();

                   JOptionPane.showMessageDialog(IDEDoublePropertyUI.this,
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
} // end of class IDEDoublePropertyUI
