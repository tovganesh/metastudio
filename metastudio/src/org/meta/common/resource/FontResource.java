/*
 * FontResource.java
 *
 * Created on June 15, 2003, 8:05 PM
 */

package org.meta.common.resource;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UIDefaults;

/**
 * Resource of different types of fonts used in the application.
 * Uses singleton pattern. The first instance (and the only instance)
 * also changes some swing default fonts.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FontResource implements Resource {
    
    private static FontResource _fontResource;
    
    /** Holds value of property menuFont. */
    private Font menuFont;
    
    /** Holds value of property frameFont. */
    private Font frameFont;
    
    /** Holds value of property statusFont. */
    private Font statusFont;
    
    /** Holds value of property descriptionFont. */
    private Font descriptionFont;
    
    /** Holds value of property codeFont. */
    private Font codeFont;
    
    /** Holds value of property screenLabelFont. */
    private Font screenLabelFont;
    
    /** Holds value of property taskGroupFont. */
    private Font taskGroupFont;
    
    /** Holds value of property taskFont. */
    private Font taskFont;
    
    /**
     * Holds value of property axisFont.
     */
    private Font axisFont;

    /**
     * Holds value of property smallFont.
     */
    private Font smallFont;
    
    /** Creates a new instance of FontResource -- private! */
    private FontResource() {
        // default initilizations :
        menuFont        = new Font("sansserif",  Font.PLAIN, 11);
        frameFont       = new Font("sansserif",  Font.PLAIN, 11);
        statusFont      = new Font("sansserif",  Font.PLAIN, 11);
        descriptionFont = new Font("sansserif",  Font.BOLD,  14);
        codeFont        = new Font("monospaced", Font.PLAIN, 11);
        screenLabelFont = new Font("sansserif",  Font.PLAIN, 12);
        taskGroupFont   = new Font("sansserif",  Font.BOLD,  12);
        taskFont        = new Font("sansserif",  Font.PLAIN, 12);
        axisFont        = new Font("sansserif",  Font.PLAIN, 10);
        smallFont       = new Font("sansserif",  Font.PLAIN, 10);
        
        // set some swing defaults..
        UIDefaults defaults = UIManager.getDefaults();
        defaults.put("Button.font", frameFont);
        defaults.put("Label.font", frameFont);
        defaults.put("TextArea.font", frameFont);
        defaults.put("Panel.font", frameFont);
        defaults.put("Menu.font", frameFont);
        defaults.put("MenuItem.font", frameFont); 
        defaults.put("CheckBoxMenuItem.font", frameFont);
        defaults.put("RadioButtonMenuItem.font", frameFont);
        defaults.put("OptionPane.font", frameFont);
        defaults.put("PopupMenu.font", frameFont);
        defaults.put("ProgressBar.font", frameFont);
        defaults.put("TabbedPane.font", frameFont);
        defaults.put("RadioButton.font", frameFont);
        defaults.put("CheckBox.font", frameFont);
        defaults.put("Table.font", frameFont);
        defaults.put("Tree.font", frameFont);
        defaults.put("TextField.font", frameFont);        
        defaults.put("ToggleButton.font", frameFont);
        defaults.put("ToolBar.font", frameFont);
        defaults.put("ToolTip.font", frameFont);
        defaults.put("ComboBox.font", frameFont);
        defaults.put("List.font", frameFont);
        defaults.put("EditorPane.font", frameFont);
        defaults.put("InternalFrame.titleFont", frameFont);
        defaults.put("ColorChooser.font", frameFont);
        defaults.put("Spinner.font", frameFont);
        defaults.put("TitledBorder.font", frameFont);        
    }
    
    /**
     * method to return instance of this object.
     *
     * @return FontResource a single global instance of this class
     */
    public static FontResource getInstance() {
        if (_fontResource == null) {
            _fontResource = new FontResource();            
        } // end if
        
        return _fontResource;
    }
    
    /** Getter for property version.
     * @return Value of property version.
     */
    @Override
    public String getVersion() {
        return StringResource.getInstance().getVersion();
    }
    
    /** Getter for property menuFont.
     * @return Value of property menuFont.
     */
    public Font getMenuFont() {
        return this.menuFont;
    }
    
    /** Setter for property menuFont.
     * @param menuFont New value of property menuFont.
     */
    public void setMenuFont(Font menuFont) {
        this.menuFont = menuFont;
    }
    
    /** Getter for property frameFont.
     * @return Value of property frameFont.
     */
    public Font getFrameFont() {
        return this.frameFont;
    }
    
    /** Setter for property frameFont.
     * @param frameFont New value of property frameFont.
     */
    public void setFrameFont(Font frameFont) {
        this.frameFont = frameFont;
    }
    
    /** Getter for property statusFont.
     * @return Value of property statusFont.
     */
    public Font getStatusFont() {
        return this.statusFont;
    }
    
    /** Setter for property statusFont.
     * @param statusFont New value of property statusFont.
     */
    public void setStatusFont(Font statusFont) {
        this.statusFont = statusFont;
    }
    
    /** Getter for property descriptionFont.
     * @return Value of property descriptionFont.
     *
     */
    public Font getDescriptionFont() {
        return this.descriptionFont;
    }
    
    /** Setter for property descriptionFont.
     * @param descriptionFont New value of property descriptionFont.
     *
     */
    public void setDescriptionFont(Font descriptionFont) {
        this.descriptionFont = descriptionFont;
    }
    
    /** Getter for property codeFont.
     * @return Value of property codeFont.
     *
     */
    public Font getCodeFont() {
        return this.codeFont;
    }
    
    /** Setter for property codeFont.
     * @param codeFont New value of property codeFont.
     *
     */
    public void setCodeFont(Font codeFont) {
        this.codeFont = codeFont;
    }
    
    /** Getter for property screenLabelFont.
     * @return Value of property screenLabelFont.
     *
     */
    public Font getScreenLabelFont() {
        return this.screenLabelFont;
    }
    
    /** Setter for property screenLabelFont.
     * @param screenLabelFont New value of property screenLabelFont.
     *
     */
    public void setScreenLabelFont(Font screenLabelFont) {
        this.screenLabelFont = screenLabelFont;
    }
    
    /** Getter for property taskGroupFont.
     * @return Value of property taskGroupFont.
     *
     */
    public Font getTaskGroupFont() {
        return this.taskGroupFont;
    }
    
    /** Setter for property taskGroupFont.
     * @param taskGroupFont New value of property taskGroupFont.
     *
     */
    public void setTaskGroupFont(Font taskGroupFont) {
        this.taskGroupFont = taskGroupFont;
    }
    
    /** Getter for property taskFont.
     * @return Value of property taskFont.
     *
     */
    public Font getTaskFont() {
        return this.taskFont;
    }
    
    /** Setter for property taskFont.
     * @param taskFont New value of property taskFont.
     *
     */
    public void setTaskFont(Font taskFont) {
        this.taskFont = taskFont;
    }
    
    /**
     * Getter for property axisFont.
     * @return Value of property axisFont.
     */
    public Font getAxisFont() {
        return this.axisFont;
    }
    
    /**
     * Setter for property axisFont.
     * @param axisFont New value of property axisFont.
     */
    public void setAxisFont(Font axisFont) {
        this.axisFont = axisFont;
    }

    /**
     * Getter for property smallFont.
     * @return Value of property smallFont.
     */
    public Font getSmallFont() {

        return this.smallFont;
    }

    /**
     * Setter for property smallFont.
     * @param smallFont New value of property smallFont.
     */
    public void setSmallFont(Font smallFont) {

        this.smallFont = smallFont;
    }
    
} // end of class FontResource
