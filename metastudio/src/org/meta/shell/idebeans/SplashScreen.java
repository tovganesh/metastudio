/*
 * SplashScreen.java
 *
 * Created on June 29, 2003, 7:09 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;

import javax.swing.*;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;

/**
 * Splash screen for the MeTA IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SplashScreen extends JWindow {       
    
    private JProgressBar progressBar; // the indeterminate prgress bar
    private JPanel imagePanel;
     
    /** Creates a new instance of SplashScreen */
    public SplashScreen() {        
        initComponents();
        
        MiscResource misc = MiscResource.getInstance();
        
        // set size and location   
        setSize(misc.getSplashScreenDimension());
        setLocationRelativeTo(null);   
        
        // add a indeterminate progress bar
        getContentPane().setLayout(new BorderLayout());
        
        progressBar = new JProgressBar();
        progressBar.setString("Please wait, loading...");
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);           
        getContentPane().add(progressBar, BorderLayout.SOUTH);
        
        imagePanel = new JPanel();
        imagePanel.setPreferredSize(misc.getSplashImageDimension());
        getContentPane().add(imagePanel, BorderLayout.CENTER);        
    }
    
    /**
     * sets the current progress bar string..
     *
     * @param newString - the new progress bar string
     */
    public void setProgressBarString(String newString) {
        progressBar.setString(newString);
    }
    
    /**
     * method to initialize the look of this splash screen
     */
    public void initComponents() {  
        setBackground(Color.blue);
    }
    
    /**
     * overridden paint method
     */
    public void paint(Graphics g) {                
        imagePanel.getGraphics().drawImage(
             ImageResource.getInstance().getIdeSplash(), 0, 0, this);    
    }
    
} // end of class SplashScreen
