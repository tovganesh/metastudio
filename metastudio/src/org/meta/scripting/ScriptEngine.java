/*
 * ScriptEngine.java
 *
 * Created on September 28, 2003, 7:26 PM
 */

package org.meta.scripting;

import java.io.File;
import java.io.FileInputStream;

/**
 * Top level interface for incorporating any scripting engine into 
 * the MeTA studio.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface ScriptEngine {

    /**
     * Get a new instance of this scripting engine
     * @return the new instance
     */
    public ScriptEngine getNewInstance();
    
    /**
     * Perform any initialization of the ScriptEngine
     */
    public void init();
    
    /**
     * Load built in modules for this script engine if any
     * 
     * @throws Exception in case there is problem loading the built ins
     */
    public void loadBuiltInModules() throws Exception;
    
    /**
     * Get the file type for this ScriptEngine (eg. py, bsh, etc)
     * 
     * @return return tile type py, bsh etc.
     */
    public String getFileType();

    /**
     * Execute the script contained in this file
     *
     * @param file the file whose script needs to be executed
     * @return any return value from the script
     * @throws Exception, if the script could not be executed for some
     *         reason
     */
    public Object execute(File file) throws Exception;

    /**
     * Execute the script.
     *
     * @param script the script to be executed
     * @return any return value from the script
     * @throws Exception, if the script could not be executed for some
     *         reason
     */
    public Object execute(String script) throws Exception;

    /**
     * Execute the script contained in this file stream
     *
     * @param fis the file stream whose script needs to be executed
     * @return any return value from the script
     * @throws Exception, if the script could not be executed for some
     *         reason
     */
    public Object execute(FileInputStream fis) throws Exception;
} // end of interface ScriptEngine
