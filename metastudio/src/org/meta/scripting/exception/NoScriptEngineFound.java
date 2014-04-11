/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.scripting.exception;

/**
 * Exception thrown if there is not handler script engine found
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NoScriptEngineFound extends Exception {

    /**
     * Creates a new instance of <code>NoScriptEngineFound</code> without
     * detail message.
     */
    public NoScriptEngineFound() {
    }


    /**
     * Constructs an instance of <code>NoScriptEngineFound</code> with the
     * specified detail message.
     * @param msg the detail message.
     */
    public NoScriptEngineFound(String msg) {
        super(msg);
    }
}
