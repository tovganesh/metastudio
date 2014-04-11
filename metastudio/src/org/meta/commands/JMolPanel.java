/*
 * JMolPanel.java
 *
 * Created on April 23, 2006, 12:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.commands;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
        
import org.jmol.api.*;
import org.jmol.adapter.smarter.*;
import org.jmol.viewer.Viewer;

/**
 * A simple interface to JMol adapter panel.
 *
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class JMolPanel extends JPanel {
    
    /** Creates a new instance of JMolPanel */
    public JMolPanel() {
        this.adapter = new SmarterJmolAdapter();
        this.viewer = Viewer.allocateViewer(this, adapter);
        this.viewer.setColorBackground("black");       
    }

    /** 
     * overridden paint() method
     */
    @Override
    public void paint(Graphics g) {                
        this.viewer.renderScreenImage(g, getSize(), g.getClipBounds());
    }
        
    /**
     * method for saving images to disk in specified format
     *
     * @param fileName the file to which the image is to be dumped
     * @param format the format of the image, typically "png", "jpg" etc.
     * @throws IOException if the operation is unsuccessful
     */
    public void writeImageToFile(String fileName, String format)
                                                    throws IOException {
        if (this.viewer == null) {
            throw new IOException("Couldn't save image to file : " + fileName);
        } // end if
        
        FileOutputStream fos = new FileOutputStream(fileName);
        
       
        if (!ImageIO.write((RenderedImage) this.viewer.getScreenImage(), 
                           format, fos)) {
            fos.close();
                
            throw new IOException("Couldn't save image to file : " + fileName);
        } // end if       
        
        fos.close();
    }
    
    /**
     * Holds value of property adapter.
     */
    private SmarterJmolAdapter adapter;

    /**
     * Getter for property adapter.
     * @return Value of property adapter.
     */
    public SmarterJmolAdapter getAdapter() {
        return this.adapter;
    }

    /**
     * Holds value of property viewer.
     */
    private JmolViewer viewer;

    /**
     * Getter for property viewer.
     * @return Value of property viewer.
     */
    public JmolViewer getViewer() {
        return this.viewer;
    }
    
} // end of class JMolPanel

