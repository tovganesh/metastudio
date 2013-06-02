/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.workflow.exception;

/**
 * Exception thrown if one tries to execute an incomplete WorkFlow
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IncompleteWorkFlow extends Exception {

    /**
     * Creates a new instance of <code>IncompleteWorkFlow</code> without 
     * detail message.
     */
    public IncompleteWorkFlow() {
    }

    /**
     * Constructs an instance of <code>IncompleteWorkFlow</code> with the 
     * specified detail message.
     * @param msg the detail message.
     */
    public IncompleteWorkFlow(String msg) {
        super(msg);
    }
}
