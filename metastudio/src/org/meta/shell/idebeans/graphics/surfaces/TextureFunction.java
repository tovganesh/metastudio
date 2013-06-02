/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.graphics.surfaces;

import java.awt.Color;

/**
 * A (3D) texture function can be used to color a surface property.
 *
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface TextureFunction {

    /**
     * Get the color value at a particular index into a grid property
     * 
     * @param i index into x location
     * @param j index into y location
     * @param k index into z location
     * @return the interpolated color value
     */
    public Color getColorValueAt(int i, int j, int k);

    /**
     * Get the color value at a particular point with in the grid property
     *
     * @param x
     * @param y
     * @param z
     * @return the interpolated color value
     */
    public Color getColorValueAt(double x, double y, double z);
}
