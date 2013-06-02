/*
 * IDEPropertySheetReader.java
 *
 * Created on June 20, 2004, 6:06 PM
 */

package org.meta.shell.idebeans.propertysheet;

import java.io.*;
import java.util.*;
import java.beans.*;
import org.meta.common.Utility;
import org.w3c.dom.*;

/**
 * Reads in the Property Sheet information of a specified fully qualified 
 * class name and converts the info. into an IDEPropertySheet object.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEPropertySheetReader {        
    
    /** the resource name of the property sheet */
    protected String propertySheetName;        
    
    /** the suffix of the property sheet, which is appended to the 
     *  the fully qualified name of the class
     */
    protected static final String PROP_FILE_SUFIX = "PropertySheet.xml";
    
    /**
     * Holds value of property theClass.
     */
    private Class theClass;
    
    private String element = new String("element");
    
    private IDEPropertySheet propertySheet;    
    private IDEPropertyGroup propertyGroup;
    private IDEProperty property;    
    
    /**
     * Holds value of property propertyDescriptors.
     */
    private HashMap<String, PropertyDescriptor> propertyDescriptors;    
    
    /**
     * Creates a new instance of IDEPropertySheetReader
     * @param clazz The class for which we need to read the property sheet
     */
    public IDEPropertySheetReader(Class clazz) {
        theClass = clazz;
        propertySheetName = "/" + theClass.getName();
        propertySheetName = propertySheetName.replace('.', '/');
        propertySheetName += PROP_FILE_SUFIX;       
    }
    
    /**
     * Creates a new instance of IDEPropertySheetReader
     * @param clazzName The class name for which we need to read the 
     *        property sheet
     * @throws ClassNotFoundException Thrown if we cannot get access to the 
     *        specified class name
     */    
    public IDEPropertySheetReader(String clazzName) 
                                  throws ClassNotFoundException {
        this(Class.forName(clazzName));
    }
    
    /**
     * Reads the property sheet info of the specified class.
     * @throws IOException thrown if unable to read property sheet
     * @return the fully build object of IDEPropertySheet read from the 
     *         property sheet file
     */    
    public IDEPropertySheet readIt() throws IOException {
        try {
           propertySheet = new IDEPropertySheet();
           
           // parse XML
           Document propertySheetDoc = Utility.parseXML(
                            getClass().getResourceAsStream(propertySheetName));
           
           // get property info           
           BeanInfo beanInfo = Introspector.getBeanInfo(theClass);
           PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
           propertyDescriptors = new HashMap<String, PropertyDescriptor>(10);
           
           for(int i=0; i<pds.length; i++) {
               propertyDescriptors.put(pds[i].getName(), pds[i]);
           } // end for
           
           // save XML
           saveIt(propertySheetDoc);
           
           return propertySheet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("An XML parse error/ ioerror / javabeans " +
                      "error has occured: " + e.toString());
        } // end try .. catch 
    }
    
    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveIt(Node n) throws Exception {
        int type = n.getNodeType();   // get node type
        
        switch (type) {
        case Node.ATTRIBUTE_NODE:
            String nodeName = n.getNodeName();
            
            if (nodeName.equals("title")) 
                propertySheet.setTitle(n.getNodeValue());
            
            break;
        case Node.ELEMENT_NODE:
            element = n.getNodeName();
            
            NamedNodeMap atts = n.getAttributes();
            
            if (element.equals("propertygroup")) {
                propertyGroup = new IDEPropertyGroup();
                
                propertyGroup.setName(atts.getNamedItem("name").getNodeValue());                
                propertySheet.addPropertyGroup(propertyGroup);
            } else if (element.equals("property")) {
                property = new IDEProperty();
                
                property.setName(atts.getNamedItem("name").getNodeValue());
                property.setDisplayName(atts.getNamedItem(
                                         "displayName").getNodeValue());
                property.setMnemonicChar(
                 atts.getNamedItem("mnemonicChar").getNodeValue().charAt(0));
                property.setTooltipText(
                 atts.getNamedItem("tooltipText").getNodeValue());
                property.setGetMethod(Boolean.valueOf(
                 atts.getNamedItem("get").getNodeValue()).booleanValue());
                property.setSetMethod(Boolean.valueOf(
                 atts.getNamedItem("set").getNodeValue()).booleanValue());                
                property.setDefaultUI(Boolean.valueOf(
                 atts.getNamedItem("defaultUI").getNodeValue()).booleanValue());
                
                // finally set the class type, of this property, using
                // the javabeans framework                
                property.setPropertyType(
                      ((PropertyDescriptor) propertyDescriptors.get(
                                 property.getName())).getPropertyType());
                
                // .. add the stuff to property group
                if (propertyGroup == null) {
                    throw new NullPointerException("You can't have an orphan " +
                                         "property, without a property group.");
                } // end if
                
                propertyGroup.addProperty(property);
            } else {
                if (atts == null) return;
                
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveIt(att);
                } // end for
            } // end if
            
            break;
        default:
            break;
        } // end of switch .. case
        
        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveIt(child);
        } // end for
    }
    
    /**
     * Getter for property theClass.
     * @return Value of property theClass.
     */
    public Class getTheClass() {
        return this.theClass;
    }
    
    /**
     * Setter for property theClass.
     * @param theClass New value of property theClass.
     */
    public void setTheClass(Class theClass) {
        this.theClass = theClass;
    }
    
    /**
     * Getter for property propertyDescriptors.
     * @return Value of property propertyDescriptors.
     */
    public HashMap getPropertyDescriptors() {
        return this.propertyDescriptors;
    }
    
} // end of class IDEPropertySheetReader
