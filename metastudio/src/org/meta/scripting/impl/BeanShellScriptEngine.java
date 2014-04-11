/*
 * BeanShellScriptEngine.java
 *
 * Created on September 28, 2003, 7:46 PM
 */

package org.meta.scripting.impl;

import java.io.*;

import org.meta.scripting.ScriptEngine;

import bsh.Interpreter;
import org.meta.common.resource.StringResource;

/**
 * A very simple interface to Bean Shell scripting engine.
 * Check http://www.beanshell.org for details of the scripting
 * capabilities.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BeanShellScriptEngine implements ScriptEngine {

    protected Interpreter interpreter;

    protected Thread interpreterThread;

    protected static boolean isWidgetLoaded = false;

    /** Creates a new instance of BeanShellScriptEngine */
    public BeanShellScriptEngine() {
        // also make the remote apps directory if it doesnot exist
        File remoteAppsDir = new File(StringResource.getInstance()
                                                    .getRemoteAppDir());
        if (!remoteAppsDir.exists()) {
            if (!remoteAppsDir.mkdir()) {
                System.out.println("Could not make remoteapps directory: " +
                                    remoteAppsDir.toString());
            } // end if
        } // end if
    }

    /**
     * Execute the script contained in this file
     *
     * @param file the file whose script needs to be executed
     * @return any return value from the script
     * @throws Exception, if the script could not be executed for some
     *         reason
     */
    @Override
    public Object execute(File file) throws Exception {
        return execute(new FileInputStream(file));
    }

    /**
     * Execute the script.
     *
     * @param script the script to be executed
     * @return any return value from the script
     * @throws Exception, if the script could not be executed for some
     *         reason
     */
    @Override
    public Object execute(String script) throws Exception {
        return interpreter.eval(script);
    }

    /**
     * Execute the script contained in this file stream
     *
     * @param fis the file stream whose script needs to be executed
     * @return any return value from the script
     * @throws Exception, if the script could not be executed for some
     *         reason
     */
    @Override
    public Object execute(FileInputStream fis) throws Exception {
        return interpreter.eval(new InputStreamReader(fis));
    }

    /**
     * Perform any initialization of the ScriptEngine
     */
    @Override
    public void init() {        
        interpreter = new Interpreter(new InputStreamReader(System.in),
                                      System.out, System.err, false);

        interpreter.setExitOnEOF(false);
    }

    /**
     * Load built in modules for this script engine if any
     *
     * @throws Exception in case there is problem loading the built ins
     */
    @Override
    public void loadBuiltInModules() throws Exception {
        interpreter.eval("importCommands(\"org.meta.commands\")");
    }

    /**
     * Get the file type for this ScriptEngine (eg. py, bsh, etc)
     *
     * @return return tile type py, bsh etc.
     */
    @Override
    public String getFileType() {
        return "bsh";
    }

    /**
     * Get a new instance of this scripting engine
     * @return the new instance
     */
    @Override
    public ScriptEngine getNewInstance() {
        return new BeanShellScriptEngine();
    }

    /**
     * The finalize method
     * 
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (interpreter != null)
            interpreter = null;
    }

} // end of class BeanShellScriptEngine
