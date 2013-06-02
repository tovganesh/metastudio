/*
 * IDEProperty.java
 *
 * Created on June 20, 2004, 6:23 PM
 */

package org.meta.shell.idebeans.propertysheet;

/**
 * Represents an IDE property in a property group.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEProperty {
    
    /**
     * Holds value of property name.
     */
    private String name;
    
    /**
     * Holds value of property displayName.
     */
    private String displayName;
    
    /**
     * Holds value of property tooltipText.
     */
    private String tooltipText;
    
    /**
     * Holds value of property getMethod.
     */
    private boolean getMethod;
    
    /**
     * Holds value of property setMethod.
     */
    private boolean setMethod;
    
    /**
     * Holds value of property propertyType.
     */
    private Class<?> propertyType;
    
    /**
     * Holds value of property defaultUI.
     */
    private boolean defaultUI;
    
    /**
     * Holds value of property mnemonicChar.
     */
    private char mnemonicChar;
    
    /** Creates a new instance of IDEProperty */
    public IDEProperty() {
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
    
    /**
     * Getter for property displayName.
     * @return Value of property displayName.
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * Setter for property displayName.
     * @param displayName New value of property displayName.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Getter for property tooltipText.
     * @return Value of property tooltipText.
     */
    public String getTooltipText() {
        return this.tooltipText;
    }
    
    /**
     * Setter for property tooltipText.
     * @param tooltipText New value of property tooltipText.
     */
    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }
    
    /**
     * Getter for property getMethod.
     * @return Value of property getMethod.
     */
    public boolean isGetMethod() {
        return this.getMethod;
    }
    
    /**
     * Setter for property getMethod.
     * @param getMethod New value of property getMethod.
     */
    public void setGetMethod(boolean getMethod) {
        this.getMethod = getMethod;
    }
    
    /**
     * Getter for property setMethod.
     * @return Value of property setMethod.
     */
    public boolean isSetMethod() {
        return this.setMethod;
    }
    
    /**
     * Setter for property setMethod.
     * @param setMethod New value of property setMethod.
     */
    public void setSetMethod(boolean setMethod) {
        this.setMethod = setMethod;
    }
    
    /**
     * Getter for property propertyType.
     * @return Value of property propertyType.
     */
    public Class<?> getPropertyType() {
        return this.propertyType;
    }
    
    /**
     * Setter for property propertyType.
     * @param propertyType New value of property propertyType.
     */
    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }
    
    /**
     * Getter for property defaultUI.
     * @return Value of property defaultUI.
     */
    public boolean isDefaultUI() {
        return this.defaultUI;
    }
    
    /**
     * Setter for property defaultUI.
     * @param defaultUI New value of property defaultUI.
     */
    public void setDefaultUI(boolean defaultUI) {
        this.defaultUI = defaultUI;
    }
    
    /**
     * Getter for property mnemonicChar.
     * @return Value of property mnemonicChar.
     */
    public char getMnemonicChar() {
        return this.mnemonicChar;
    }
    
    /**
     * Setter for property mnemonicChar.
     * @param mnemonicChar New value of property mnemonicChar.
     */
    public void setMnemonicChar(char mnemonicChar) {
        this.mnemonicChar = mnemonicChar;
    }
    
} // end of class IDEProperty
