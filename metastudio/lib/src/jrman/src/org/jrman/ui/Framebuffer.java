/*
 Framebuffer.java
 Copyright (C) 2003 Alessandro Falappa

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.jrman.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.falappa.imageio.ImageViewerPanelSaveAction;
import net.falappa.swing.widgets.JImageViewerPanel;

/**
 * The window of the framebuffer display.
 * It shows the image being rendered, when finished allows to save the image to
 * a file. It allows to scroll and zoom the image.
 * 
 * @author Alessandro Falappa
 * @modified V.Ganesh
 */
public interface Framebuffer {    

    /**
     * Signal that a certain rectangular region has changed
     * @param x top-left x coordinate
     * @param y top-left y coordinate
     * @param w rectangle width
     * @param h rectangle height
     */
    public void refresh(int x, int y, int w, int h);

    /**
     * Signal image is completed
     */
    public void completed();
    
    /**
     * visibility ??
     */
    public void setVisible(boolean v);
} // end of interface Framebuffer
