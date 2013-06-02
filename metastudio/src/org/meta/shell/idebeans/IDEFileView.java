/*
 * IDEFileView.java
 *
 * Created on October 24, 2003, 11:27 AM
 */

package org.meta.shell.idebeans;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import org.meta.common.resource.ImageResource;

/**
 * A very simple implimentation for mapping icons with the known file
 * types in the IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEFileView extends FileView {
    
    /** Creates a new instance of IDEFileView */
    public IDEFileView() {
    }
    
    /**
     * The icon that represents this file in the JFileChooser. 
     */
    public Icon getIcon(File file) {
        return ImageResource.getInstance().getIconFor(file);
    }
} // end of class IDEFileView
