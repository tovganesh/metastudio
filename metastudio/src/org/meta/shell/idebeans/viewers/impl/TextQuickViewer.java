/*
 * TextQuickViewer.java
 *
 * Created on October 25, 2003, 8:01 AM
 */

package org.meta.shell.idebeans.viewers.impl;

import java.io.*;
import java.awt.*;

import javax.swing.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.shell.idebeans.viewers.QuickViewer;

/**
 * A simple implementation of QuickViewer for displaying text.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TextQuickViewer extends JDialog implements QuickViewer {
    
    private JTextArea theText;    
    
    private static JFrame jf;
    
    static {
        jf = new JFrame();
        jf.setIconImage(ImageResource.getInstance().getWatch().getImage());
    } // static 
        
    /** Creates a new instance of TextQuickViewer */
    public TextQuickViewer() {                
        this(jf);
    }
    
    public TextQuickViewer(JFrame parent) {
        super(parent, "TextQuickViewer - (Read Only)", true);
    }
    
    /**
     * All readers specify an MIME type, most of them similar to that used
     * by most of the browsers and some are defined by the MeTA Studio.
     *
     * @return a string indicating the MIME type, for e.g. text/plain
     */
    @Override
    public String getSupportedMIME() {
        return "text/plain";
    }
    
    /**
     * call to show up a file quick!
     * 
     * @param file : fully qualified file name
     * @throws IOException - if unable to read the file.
     */
    @Override
    public void showFile(String file) throws IOException {
        showFile(new File(file));
    }
    
    /**
     * call to show up a file quick!
     * 
     * @param file : the file object
     * @throws IOException - if unable to read the file.
     */
    @Override
    public void showFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        showInputStream(fis);
        fis.close();
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
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer data = new StringBuffer();
        String line;
        
        // first read in the data
        while (true) {
            line = br.readLine();
            
            if ((line == null) || (line.equals(""))) break;
                
            data.append(line);
            data.append('\n');
        } // end while
        
        showStringBuffer(data);
    }

    /**
     * call to show up a string buffer quick!
     * 
     * @param data the string buffer to be displayed.
     */
    @Override
    public void showStringBuffer(StringBuffer data) {
        // the text area
        theText = new JTextArea(data.toString());
        theText.setFont(FontResource.getInstance().getCodeFont());
        theText.setEditable(false);
        
        // and prepare the UI and show the stuff
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(theText), BorderLayout.CENTER);
        setSize(MiscResource.getInstance().getQuickViewerDimension());        
        setVisible(true);
    }
    
} // end of class TextQuickViewer
