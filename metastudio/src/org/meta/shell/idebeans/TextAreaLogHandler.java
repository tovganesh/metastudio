/*
 * TextAreaLogHandler.java
 *
 * Created on August 31, 2003, 7:36 AM
 */

package org.meta.shell.idebeans;

import java.util.logging.*;

import javax.swing.*;

/**
 * A simple log handler for logging in to a text area.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TextAreaLogHandler extends Handler {
    
    private JTextArea textArea;
    
    /** Creates a new instance of TextAreaLogHandler */
    public TextAreaLogHandler(JTextArea textArea) {
        this.textArea = textArea;         
        
        setFormatter(new SimpleFormatter());
    }
    
    /**
     * no implimentations - ignoring!
     */
    public void close() throws SecurityException {
    }
    
    /**
     * no implimentations - ignoring!
     */
    public void flush() {
    }
    
    /**
     * method to publish the record on to the text area
     */
    public void publish(LogRecord record) {
        textArea.append(getFormatter().format(record));
    }
    
} // end of class TextAreaLogHandler
