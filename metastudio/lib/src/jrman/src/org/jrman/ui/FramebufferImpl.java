/*
 * FramebufferImpl.java
 *
 * Created on November 7, 2005, 8:54 PM
 *
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
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FramebufferImpl extends JFrame implements Framebuffer {
    private JImageViewerPanel imagePanel = new JImageViewerPanel();
	private ImageViewerPanelSaveAction save=
    new ImageViewerPanelSaveAction(imagePanel,BufferedImage.TYPE_INT_ARGB);
    private String name;

    /**
     * Constructs a new framebuffer window
     * @param name string used as window title
     * @param image <code>BufferedImage</code> object to show
     */
    public FramebufferImpl(String name, BufferedImage image) {
        super(name);
        this.name = name;
        save.setEnabled(false);
        imagePanel.setImage(image);
        imagePanel.addToolbarAction(save);        
        if (image.getType() == BufferedImage.TYPE_INT_ARGB
            || image.getType() == BufferedImage.TYPE_INT_ARGB_PRE) {
            imagePanel.setShowTransparencyPattern(true);
        }
        setIconImage(
            new ImageIcon(getClass().getResource("images/framebuffer_icon.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getRootPane().setDoubleBuffered(false);
        getContentPane().add(imagePanel);
        pack();
    }

    /**
     * Signal that a certain rectangular region has changed
     * @param x top-left x coordinate
     * @param y top-left y coordinate
     * @param w rectangle width
     * @param h rectangle height
     */
    public void refresh(int x, int y, int w, int h) {
        imagePanel.repaintImage(x, y, w, h);
    }

    /**
     * Signal image is completed
     */
    public void completed() {
        save.setEnabled(true);
    }
    
} // end of class FramebufferImpl
