/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.workflow.exception;

/**
 * Exception generated if two WorkUnit s cannot be connected in a WorkFlow
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IncompatibleWorkUnitConnection extends Exception {

    /**
     * Creates a new instance of <code>IncompatibleWorkUnitConnection</code> 
     * without detail message.
     */
    public IncompatibleWorkUnitConnection() {
    }

    /**
     * Constructs an instance of <code>IncompatibleWorkUnitConnection</code> 
     * with the specified detail message.
     * 
     * @param msg the detail message.
     */
    public IncompatibleWorkUnitConnection(String msg) {
        super(msg);
    }
}
