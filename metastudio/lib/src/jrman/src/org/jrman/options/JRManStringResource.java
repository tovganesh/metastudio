/*
 * JRManStringResource.java
 *
 * Created on November 7, 2005, 8:48 PM
 *
 */

package org.jrman.options;

/**
 * Class providing strings for jrMan. Uses singleton pattern. 
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class JRManStringResource {
    
    private static JRManStringResource _stringResource;
    
    /**
     * Creates a new instance of JRManStringResource 
     */
    private JRManStringResource() {
        framebufferClass = "org.jrman.ui.FramebufferImpl";
    }
    
    public static JRManStringResource getInstance() {
        if (_stringResource == null) {
            _stringResource = new JRManStringResource();
        } // end if
        
        return _stringResource;
    }

    /**
     * Holds value of property framebufferClass.
     */
    private String framebufferClass;

    /**
     * Getter for property framebufferClass.
     * @return Value of property framebufferClass.
     */
    public String getFramebufferClass() {
        return this.framebufferClass;
    }

    /**
     * Setter for property framebufferClass.
     * @param framebufferClass New value of property framebufferClass.
     */
    public void setFramebufferClass(String framebufferClass) {
        this.framebufferClass = framebufferClass;
    }
} // end of class JRManStringResource
