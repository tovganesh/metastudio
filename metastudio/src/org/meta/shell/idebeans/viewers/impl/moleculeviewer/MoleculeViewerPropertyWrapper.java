/*
 * MoleculeViewerPropertyWrapper.java
 *
 * Created on September 18, 2004, 8:51 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;

/**
 * A wrapper for MoleculeViewer properties.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeViewerPropertyWrapper {
    
    private MoleculeViewer moleculeViewer;
    
    /** Creates a new instance of MoleculeViewerPropertyWrapper */
    public MoleculeViewerPropertyWrapper(MoleculeViewer moleculeViewer) {
        this.moleculeViewer = moleculeViewer;
    }
    
    /**
     * Getter for property backgroundColor.
     * @return Value of property backgroundColor.
     */
    public Color getBackgroundColor() {
        return moleculeViewer.getBackgroundColor();
    }
    
    /**
     * Setter for property backgroundColor.
     * @param backgroundColor New value of property backgroundColor.
     */
    public void setBackgroundColor(Color backgroundColor) {
        moleculeViewer.setBackgroundColor(backgroundColor);
    }
    
    /**
     * Getter for property smoothDrawing.
     * @return Value of property smoothDrawing.
     */
    public boolean isSmoothDrawing() {
        return moleculeViewer.isSmoothDrawing();
    }
    
    /**
     * Setter for property smoothDrawing.
     * @param smoothDrawing New value of property smoothDrawing.
     */
    public void setSmoothDrawing(boolean smoothDrawing) {
        moleculeViewer.setSmoothDrawing(smoothDrawing);
    }
    
    /**
     * overriden toString()
     */
    public String toString() {
        return moleculeViewer.toString();
    }
    
    /**
     * Getter for property axisDrawn.
     * @return Value of property axisDrawn.
     */
    public boolean isAxisDrawn() {
        return moleculeViewer.isAxisDrawn();
    }
    
    /**
     * Setter for property axisDrawn.
     * @param axisDrawn New value of property axisDrawn.
     */
    public void setAxisDrawn(boolean axisDrawn) {
        moleculeViewer.setAxisDrawn(axisDrawn);
    }

    /**
     * Getter for property enable3D.
     * @return Value of property enable3D.
     */
    public boolean isEnable3D() {
        return moleculeViewer.isEnable3D();
    }

    /**
     * Setter for property enable3D.
     * @param enable3D New value of property enable3D.
     */
    public void setEnable3D(boolean enable3D) {
        moleculeViewer.setEnable3D(enable3D);
    }

    /**
     * Holds value of property enableJava3D.
     */
    private boolean enableJava3D;

    /**
     * Getter for property enableJava3D.
     * @return Value of property enableJava3D.
     */
    public boolean isEnableJava3D() {
        return moleculeViewer.isEnableJava3D();
    }

    /**
     * Setter for property enableJava3D.
     * @param enableJava3D New value of property enableJava3D.
     */
    public void setEnableJava3D(boolean enableJava3D) {
        moleculeViewer.setEnableJava3D(enableJava3D);
    }
    
} // end of class MoleculeViewerPropertyWrapper
