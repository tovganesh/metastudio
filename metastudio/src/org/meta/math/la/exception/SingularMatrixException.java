/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.la.exception;

/**
 * Exception thrown when a matrix is singular
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SingularMatrixException extends Exception {

    /**
     * Creates a new instance of <code>SingularMatrixException</code>
     * without detail message.
     */
    public SingularMatrixException() {
    }


    /**
     * Constructs an instance of <code>SingularMatrixException</code> 
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public SingularMatrixException(String msg) {
        super(msg);
    }
}
