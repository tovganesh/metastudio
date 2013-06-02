/*
 * InvalidFileFormatException.java
 *
 * Created on September 7, 2003, 7:04 PM
 */

package org.meta.moleculereader;

/**
 * The exception thrown by an implimentor of MoleculeFileReader 
 * if the current file being read is in an inconsistant format.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class InvalidFileFormatException extends java.io.IOException {
    
    /**
     * Creates a new instance of <code>InvalidFileFormatException</code> 
     * without detail message.
     */
    public InvalidFileFormatException() {
    }
    
    
    /**
     * Constructs an instance of <code>InvalidFileFormatException</code> 
     * with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidFileFormatException(String msg) {
        super(msg);
    }
} // end of exception class InvalidFileFormatException
