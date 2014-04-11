/*
 * ColorResource.java
 *
 * Created on August 15, 2003, 9:10 AM
 */

package org.meta.common.resource;

import java.awt.Color;

/**
 * Resource of the colors supplied to the IDE.
 * Follows a singleton design pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ColorResource implements Resource {
    
    private static ColorResource _colorResource;
    
    /** Holds value of property activeFrameColor. */
    private Color activeFrameColor;
    
    /** Holds value of property deactiveFrameColor. */
    private Color deactiveFrameColor;
    
    /** Holds value of property bannerColor. */
    private Color bannerColor;
    
    /** Holds value of property selectedAtomColor. */
    private Color selectedAtomColor;
    
    /** Holds value of property selectedBondColor. */
    private Color selectedBondColor;
    
    /** Holds value of property screenLabelColor. */
    private Color screenLabelColor;
    
    /** Holds value of property defaultSelectionColor. */
    private Color defaultSelectionColor;
    
    private final static float BRIGHTNESS_FACTOR = 0.7f;
    private final static float THRICE_BRIGHTNESS_FACTOR = 0.7f*0.7f*0.7f;
    private final static int I_FACTOR  = (int) (1.0/1.0-BRIGHTNESS_FACTOR);
    private final static Color I_COLOR = new Color(I_FACTOR, 
                                                   I_FACTOR, I_FACTOR);
    
    /** Creates a new instance of ColorResource */
    private ColorResource() {
        activeFrameColor   = new Color(204, 204, 255);
        deactiveFrameColor = new Color(204, 204, 204);  
        bannerColor        = new Color(20 , 100, 20 );
        selectedAtomColor  = new Color(0.9f, 0.9f, 0.0f, 0.5f);
        selectedBondColor  = new Color(0.9f, 0.9f, 0.0f, 0.3f);
        deactiveFrameColor = Color.yellow;
        screenLabelColor   = Color.green;
        centralDotColor    = Color.orange;
        screenCuboidColor  = Color.green;
        screenSphereColor  = Color.green;
    }
    
    /**
     * method to return instance of this object.
     *
     * @return ColorResource a single global instance of this class
     */
    public static ColorResource getInstance() {
        if (_colorResource == null) {
            _colorResource = new ColorResource();            
        } // end if
        
        return _colorResource;
    }
    
    /**
     * method to get fast a brighter version of the current color
     *
     * @param color the source color
     * @return the brightened color
     */
    public Color brighter(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        if (r==0 && g==0 && b==0) {
            return I_COLOR;
        } // end if
        
        if (r<I_FACTOR) r = I_FACTOR;
        if (g<I_FACTOR) g = I_FACTOR;
        if (b<I_FACTOR) b = I_FACTOR;
        
        r = (int) (r / BRIGHTNESS_FACTOR);
        g = (int) (g / BRIGHTNESS_FACTOR);
        b = (int) (b / BRIGHTNESS_FACTOR);
        
        return new Color((r < 255) ? r : 255,
                         (g < 255) ? g : 255,
                         (b < 255) ? b : 255);
    }
    
    /**
     * method to get fast a darker version of the current color
     *
     * @param color the source color
     * @return the darkened color
     */
    public Color darker(Color color) {        
        return new Color((int) (color.getRed()   * BRIGHTNESS_FACTOR),
                         (int) (color.getGreen() * BRIGHTNESS_FACTOR),
                         (int) (color.getBlue()  * BRIGHTNESS_FACTOR));
    }
    
    /** Getter for property version.
     * @return Value of property version.
     */
    public String getVersion() {
        return StringResource.getInstance().getVersion();
    }
    
    /** Getter for property activeFrameColor.
     * @return Value of property activeFrameColor.
     *
     */
    public Color getActiveFrameColor() {
        return this.activeFrameColor;
    }
    
    /** Setter for property activeFrameColor.
     * @param activeFrameColor New value of property activeFrameColor.
     *
     */
    public void setActiveFrameColor(Color activeFrameColor) {
        this.activeFrameColor = activeFrameColor;
    }
    
    /** Getter for property deactiveFrameColor.
     * @return Value of property deactiveFrameColor.
     *
     */
    public Color getDeactiveFrameColor() {
        return this.deactiveFrameColor;
    }
    
    /** Setter for property deactiveFrameColor.
     * @param deactiveFrameColor New value of property deactiveFrameColor.
     *
     */
    public void setDeactiveFrameColor(Color deactiveFrameColor) {
        this.deactiveFrameColor = deactiveFrameColor;
    }
    
    /** Getter for property bannerColor.
     * @return Value of property bannerColor.
     *
     */
    public Color getBannerColor() {
        return this.bannerColor;
    }
    
    /** Setter for property bannerColor.
     * @param bannerColor New value of property bannerColor.
     *
     */
    public void setBannerColor(Color bannerColor) {
        this.bannerColor = bannerColor;
    }
    
    /** Getter for property selectedAtomColor.
     * @return Value of property selectedAtomColor.
     *
     */
    public Color getSelectedAtomColor() {
        return this.selectedAtomColor;
    }
    
    /** Setter for property selectedAtomColor.
     * @param selectedAtomColor New value of property selectedAtomColor.
     *
     */
    public void setSelectedAtomColor(Color selectedAtomColor) {
        this.selectedAtomColor = selectedAtomColor;
    }
    
    /** Getter for property selectedBondColor.
     * @return Value of property selectedBondColor.
     *
     */
    public Color getSelectedBondColor() {
        return this.selectedBondColor;
    }
    
    /** Setter for property selectedBondColor.
     * @param selectedBondColor New value of property selectedBondColor.
     *
     */
    public void setSelectedBondColor(Color selectedBondColor) {
        this.selectedBondColor = selectedBondColor;
    }
    
    /** Getter for property screenLabelColor.
     * @return Value of property screenLabelColor.
     *
     */
    public Color getScreenLabelColor() {
        return this.screenLabelColor;
    }
    
    /** Setter for property screenLabelColor.
     * @param screenLabelColor New value of property screenLabelColor.
     *
     */
    public void setScreenLabelColor(Color screenLabelColor) {
        this.screenLabelColor = screenLabelColor;
    }
    
    /** Getter for property defaultSelectionColor.
     * @return Value of property defaultSelectionColor.
     *
     */
    public Color getDefaultSelectionColor() {
        return this.defaultSelectionColor;
    }
    
    /** Setter for property defaultSelectionColor.
     * @param defaultSelectionColor New value of property defaultSelectionColor.
     *
     */
    public void setDefaultSelectionColor(Color defaultSelectionColor) {
        this.defaultSelectionColor = defaultSelectionColor;
    }

    /**
     * Holds value of property centralDotColor.
     */
    private Color centralDotColor;

    /**
     * Getter for property centralDotColor.
     * @return Value of property centralDotColor.
     */
    public Color getCentralDotColor() {
        return this.centralDotColor;
    }

    /**
     * Setter for property centralDotColor.
     * @param centralDotColor New value of property centralDotColor.
     */
    public void setCentralDotColor(Color centralDotColor) {
        this.centralDotColor = centralDotColor;
    }

    /**
     * Holds value of property screenCuboidColor.
     */
    private Color screenCuboidColor;

    /**
     * Getter for property screenCuboidColor.
     * @return Value of property screenCuboidColor.
     */
    public Color getScreenCuboidColor() {
        return this.screenCuboidColor;
    }

    /**
     * Setter for property screenCuboidColor.
     * @param screenCuboidColor New value of property screenCuboidColor.
     */
    public void setScreenCuboidColor(Color screenCuboidColor) {
        this.screenCuboidColor = screenCuboidColor;
    }

    /**
     * Holds value of property screenSphereColor.
     */
    private Color screenSphereColor;

    /**
     * Getter for property screenSphereColor.
     * @return Value of property screenSphereColor.
     */
    public Color getScreenSphereColor() {

        return this.screenSphereColor;
    }

    /**
     * Setter for property screenSphereColor.
     * @param screenSphereColor New value of property screenSphereColor.
     */
    public void setScreenSphereColor(Color screenSphereColor) {

        this.screenSphereColor = screenSphereColor;
    }
    
} // end of class ColorResource
