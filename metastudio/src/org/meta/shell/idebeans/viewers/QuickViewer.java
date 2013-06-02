/*
 * QuickViewer.java
 *
 * Created on October 24, 2003, 9:09 PM
 */

package org.meta.shell.idebeans.viewers;

import java.io.*;

/**
 * An spelized type of Viewer .. for defining quick and fast viewing of
 * any type of supported file.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface QuickViewer extends Viewer {        
    
    /**
     * call to show up a file quick!
     * 
     * @param file : fully qualified file name
     * @throws IOException - if unable to read the file.
     */
    public void showFile(String file) throws IOException;
    
    /**
     * call to show up a file quick!
     * 
     * @param file : the file object
     * @throws IOException - if unable to read the file.
     */
    public void showFile(File file) throws IOException;
    
    /**
     * call to show up a string buffer quick!
     * 
     * @param data the string buffer to be displayed.
     */
    public void showStringBuffer(StringBuffer data);
    
    /**
     * call to show up an input stream quick!
     * Note: it is the responsibility of the caller to close the input stream
     * and this function should not be performed by the implementation of this
     * method.
     *
     * @param is : the input stream
     * @throws IOException - if unable to read the file.
     */
    public void showInputStream(InputStream is) throws IOException;
    
    /**
     * All readers specify an MIME type, most of them similar to that used
     * by most of the browsers and some are defined by the MeTA Studio.
     *
     * @return a string indicating the MIME type, for e.g. text/plain
     */
    public String getSupportedMIME();
    
} // end of interface QuickViewer
