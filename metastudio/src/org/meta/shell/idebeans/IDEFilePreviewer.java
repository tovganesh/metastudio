/*
 * IDEFilePreviewer.java
 *
 * Created on August 15, 2005, 11:41 AM
 *
 */

package org.meta.shell.idebeans;

import java.io.*;        
import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * A simple "wrap" class for defining "file preview" accessory.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class IDEFilePreviewer extends JPanel {
    
    /** Creates a new instance of IDEFilePreviewer */
    public IDEFilePreviewer() {
        super();
        
        setPreferredSize(new Dimension(100, 50));
        setBorder(new BevelBorder(BevelBorder.LOWERED));
    }
    
    /**
     * Set the current file to preview.
     */
    public abstract void setFileToPreview(File file);
    
} // end of class IDEFilePreviewer
