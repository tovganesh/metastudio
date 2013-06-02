/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.mar.exception;

/**
 * Exception thrown by reader if an invalid .mar file is encountered.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class InvalidMarFileFormat extends Exception {

    /**
     * Creates a new instance of <code>InvalidMarFileFormat</code>
     * without detail message.
     */
    public InvalidMarFileFormat() {
    }

    /**
     * Constructs an instance of <code>InvalidMarFileFormat</code> with
     * the specified detail message.
     * @param msg the detail message.
     */
    public InvalidMarFileFormat(String msg) {
        super(msg);
    }
}
