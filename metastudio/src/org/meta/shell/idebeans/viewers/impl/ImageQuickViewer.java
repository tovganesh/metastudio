/*
 * ImageQuickViewer.java 
 *
 * Created on 12 Oct, 2008 
 */

package org.meta.shell.idebeans.viewers.impl;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.shell.idebeans.viewers.QuickViewer;

/**
 * A simple implementation of QuickViewer for displaying image.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ImageQuickViewer extends JDialog implements QuickViewer {

    private JLabel theImageLabel;    
    
    private static JFrame jf;
    
    static {
        jf = new JFrame();
        jf.setIconImage(ImageResource.getInstance().getWatch().getImage());
    } // static
    
    /** Creates a new instance of ImageQuickViewer */
    public ImageQuickViewer() {
        this(jf);
    }
    
    public ImageQuickViewer(JFrame parent) {
        super(parent, "ImageQuickViewer", true);
    }
    
    /**
     * call to show up a file quick!
     * 
     * @param file : fully qualified file name
     * @throws IOException - if unable to read the file.
     */
    @Override
    public void showFile(String file) throws IOException {
        showInputStream(new FileInputStream(file));
    }
    
    /**
     * call to show up a file quick!
     * 
     * @param file : the file object
     * @throws IOException - if unable to read the file.
     */
    @Override
    public void showFile(File file) throws IOException {
        showInputStream(new FileInputStream(file));    
    }
    
    /**
     * call to show up a string buffer quick!
     * 
     * @param data the string buffer to be displayed.
     */
    @Override
    public void showStringBuffer(StringBuffer data) {        
        try {
          showInputStream(new ByteArrayInputStream(data.toString().getBytes()));
        } catch(Exception e) {
          System.err.println("Error in reading: " + e.toString());
          e.printStackTrace();
        } // end of try ... catch block
    }
    
    /**
     * call to show up an input stream quick!
     * Note: it is the responsibility of the caller to close the input stream
     * and this function should not be performed by the implementation of this
     * method.
     *
     * @param is : the input stream
     * @throws IOException - if unable to read the file.
     */
    @Override
    public void showInputStream(InputStream is) throws IOException {        
        showImage(ImageIO.read(is));
    }
    
    /**
     * show image..
     * 
     * @param image the new image to show
     */
    public void showImage(Image image) {
        theImageLabel = new JLabel(new ImageIcon(image));
        
        // and prepare the UI and show the stuff
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(theImageLabel), 
                             BorderLayout.CENTER);
        setSize(MiscResource.getInstance().getQuickViewerDimension());        
        setVisible(true);
    }
    
    /**
     * All readers specify an MIME type, most of them similar to that used
     * by most of the browsers and some are defined by the MeTA Studio.
     *
     * @return a string indicating the MIME type, for e.g. text/plain
     */
    @Override
    public String getSupportedMIME(){
        return "image/any";
    }
}
