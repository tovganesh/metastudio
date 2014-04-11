/*
 * IDEFileChooser.java
 *
 * Created on June 15, 2004, 9:51 PM
 */

package org.meta.shell.idebeans;


import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import javax.swing.JFileChooser;
import org.meta.common.resource.StringResource;

/**
 * A derived JFileChooser type, with facility of remembering previous
 * browsed directory.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEFileChooser extends JFileChooser 
                            implements PropertyChangeListener {
    
    private static String lastVisitedFolder = System.getProperty("user.home");
    
    static {
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                         StringResource.getInstance().getLastVisitedFolder()));
            lastVisitedFolder = br.readLine();
            br.close();
        } catch (Exception ignored) { }
    } // end of static block
    
    /** Creates a new instance of IDEFileChooser */
    public IDEFileChooser() {
        this(lastVisitedFolder);            
    }
    
    /** Creates a new instance of IDEFileChooser */
    public IDEFileChooser(String initialFolder) {
        this(initialFolder, false);
    }        
    
    /** Creates a new instance of IDEFileChooser */
    public IDEFileChooser(String initialFolder, boolean showPreview) {
        super(initialFolder);
        
        this.showPreview = showPreview;
    }  
    
    /**
     * Set last visited folder name
     *
     * @param newFolder path to a valid folder name
     */
    public void setLastVisitedFolder(String newFolder) {
        lastVisitedFolder = newFolder;
        
        try {
            FileOutputStream fos = new FileOutputStream(
                    StringResource.getInstance().getLastVisitedFolder());
            fos.write(lastVisitedFolder.getBytes());
            fos.close();
        } catch (Exception ignored) { }
    }
    
    /**
     * Pops up a "Save File" file chooser dialog. Note that the
     * text that appears in the approve button is determined by
     * the L&F.
     *
     * @param    parent  the parent component of the dialog,
     * 			can be <code>null</code>;
     *                  see <code>showDialog</code> for details
     * @return   the return state of the file chooser on pop-down:
     * <ul>
     * <li>JFileChooser.CANCEL_OPTION
     * <li>JFileChooser.APPROVE_OPTION
     * <li>JFileCHooser.ERROR_OPTION if an error occurs or the
     * 			dialog is dismissed
     * </ul>
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #showDialog
     */
    @Override
    public int showSaveDialog(Component parent) throws HeadlessException {
        int retValue;
        
        retValue = super.showSaveDialog(parent);
        
        if (retValue == APPROVE_OPTION) {
            lastVisitedFolder = getCurrentDirectory().getAbsolutePath();
            
            try {
                FileOutputStream fos = new FileOutputStream(
                           StringResource.getInstance().getLastVisitedFolder());
                fos.write(lastVisitedFolder.getBytes());
                fos.close();
            } catch (Exception ignored) { }
        } // end if
        
        return retValue;
    }
    
    /**
     * Pops up an "Open File" file chooser dialog. Note that the
     * text that appears in the approve button is determined by
     * the L&F.
     *
     * @param    parent  the parent component of the dialog,
     * 			can be <code>null</code>;
     *                  see <code>showDialog</code> for details
     * @return   the return state of the file chooser on pop-down:
     * <ul>
     * <li>JFileChooser.CANCEL_OPTION
     * <li>JFileChooser.APPROVE_OPTION
     * <li>JFileCHooser.ERROR_OPTION if an error occurs or the
     * 			dialog is dismissed
     * </ul>
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #showDialog
     */
    @Override
    public int showOpenDialog(Component parent) throws HeadlessException {
        int retValue;
        
        retValue = super.showOpenDialog(parent);
        
        if (retValue == APPROVE_OPTION) {
            lastVisitedFolder = getCurrentDirectory().getAbsolutePath();
            
            try {
                FileOutputStream fos = new FileOutputStream(
                           StringResource.getInstance().getLastVisitedFolder());
                fos.write(lastVisitedFolder.getBytes());
                fos.close();
            } catch (Exception ignored) { }
        } // end if
        
        return retValue;
    }

    /**
     * Holds value of property showPreview.
     */
    private boolean showPreview;

    /**
     * Getter for property showPreview.
     * @return Value of property showPreview.
     */
    public boolean isShowPreview() {
        return this.showPreview;
    }

    /**
     * Setter for property showPreview.
     * @param showPreview New value of property showPreview.
     */
    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
        
        if (showPreview) {
            addPropertyChangeListener(this);
            setAccessory(IDEFilePreviewerFactory.getInstance()
                                .getAccessoryFor(new File(".default")));            
            getAccessory().updateUI();            
        } else {
            removePropertyChangeListener(this);
        } // end if
    }

    /**
     * Property change listener for showing previews
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
	String prop = e.getPropertyName();
        
	if(prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
	    if(showPreview && isShowing() && (e.getNewValue() != null)) {
                setAccessory(IDEFilePreviewerFactory.getInstance()
                                .getAccessoryFor((File) e.getNewValue()));                
                getAccessory().updateUI();
	    } // end if
	} // end if
    }
} // end of class IDEFileChooser
