/*
 * DefaultIDEFilePreviewer.java
 *
 * Created on August 15, 2005, 11:59 AM
 *
 */

package org.meta.shell.idebeans;

import java.io.*;        
import java.awt.*;

import javax.swing.*;

/**
 * The default previewer just displayes the first few bytes of the file.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DefaultIDEFilePreviewer extends IDEFilePreviewer {
    
    /** Creates a new instance of DefaultIDEFilePreviewer */
    public DefaultIDEFilePreviewer() {
        super();
    }        
    
    /**
     * Set the current file to preview.
     */
    public void setFileToPreview(File file) {
        removeAll();
        setLayout(new BorderLayout());
        
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        
        try {
            BufferedReader br = new BufferedReader(
                              new InputStreamReader(new FileInputStream(file)));
            String line;
            
            for(int i=0; i<5; i++) {
                line = br.readLine();
                
                if (line == null || line.equals("")) break;
                
                ta.append(line + '\n');
            } // end if
        } catch(Exception e) {
            ta.setText("No preview \navailable.");
        } // end try .. catch 
        
        add(ta, BorderLayout.CENTER);
        updateUI();
    }
} // end of class DefaultIDEFilePreviewer
