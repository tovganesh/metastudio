/*
 * CursorResource.java
 *
 * Created on February 15, 2004, 1:53 PM
 */

package org.meta.common.resource;

import java.awt.*;

/**
 * Repository of all custom cursor resources.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CursorResource implements Resource {
    
    private static CursorResource _cursorResource;
    
    private String imagePath;
    
    /** Holds value of property zoomCursor. */
    private Cursor zoomCursor;
    
    /** Holds value of property penCursor. */
    private Cursor penCursor;
    
    /** Holds value of property pencilCursor. */
    private Cursor pencilCursor;
    
    /** Creates a new instance of CursorResource */
    private CursorResource() {
        imagePath = ImageResource.getInstance().getImagePath();
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        try {
            zoomCursor = toolkit.createCustomCursor(toolkit.createImage(
                          getClass().getResource(imagePath + "zoomCursor.png")),
                          new Point(11, 11), "zoomCursor");            
            penCursor = toolkit.createCustomCursor(toolkit.createImage(
                          getClass().getResource(imagePath + "penCursor.png")),
                          new Point(8, 22), "penCursor"); 
            pencilCursor = toolkit.createCustomCursor(toolkit.createImage(
                          getClass().getResource(imagePath + "pencilCursor.png")),
                          new Point(8, 22), "pencilCursor"); 
            openingCursor = toolkit.createCustomCursor(toolkit.createImage(
                       getClass().getResource(imagePath + "openingCursor.png")),
                       new Point(0, 0), "openingCursor"); 
        } catch (Exception e) {
            System.out.println("Cannot load cursors : " + e);
            e.printStackTrace();
        } // end try
    }
    
    /**
     * method to return instance of this object.
     *
     * @return ColorResource a single global instance of this class
     */
    public static CursorResource getInstance() {
        if (_cursorResource == null) {
            _cursorResource = new CursorResource();            
        } // end if
        
        return _cursorResource;
    }
    
    /** Getter for property version.
     * @return Value of property version.
     */
    @Override
    public String getVersion() {
        return StringResource.getInstance().getVersion();
    }
    
    /** Getter for property zoomCursor.
     * @return Value of property zoomCursor.
     *
     */
    public Cursor getZoomCursor() {
        return this.zoomCursor;
    }
    
    /** Setter for property zoomCursor.
     * @param zoomCursor New value of property zoomCursor.
     *
     */
    public void setZoomCursor(Cursor zoomCursor) {
        this.zoomCursor = zoomCursor;
    }
    
    /** Getter for property penCursor.
     * @return Value of property penCursor.
     *
     */
    public Cursor getPenCursor() {
        return this.penCursor;
    }
    
    /** Setter for property penCursor.
     * @param penCursor New value of property penCursor.
     *
     */
    public void setPenCursor(Cursor penCursor) {
        this.penCursor = penCursor;
    }

    /** Getter for property pencilCursor.
     * @return Value of property pencilCursor.
     *
     */
    public Cursor getPencilCursor() {
        return this.pencilCursor;
    }
    
    /** Setter for property pencilCursor.
     * @param pencilCursor New value of property pencilCursor.
     *
     */
    public void setPencilCursor(Cursor pencilCursor) {
        this.pencilCursor = pencilCursor;
    }
    
    /**
     * Holds value of property openingCursor.
     */
    private Cursor openingCursor;

    /**
     * Getter for property openingCursor.
     * @return Value of property openingCursor.
     */
    public Cursor getOpeningCursor() {
        return this.openingCursor;
    }

    /**
     * Setter for property openingCursor.
     * @param openingCursor New value of property openingCursor.
     */
    public void setOpeningCursor(Cursor openingCursor) {
        this.openingCursor = openingCursor;
    }
    
} // end of class CursorResource
