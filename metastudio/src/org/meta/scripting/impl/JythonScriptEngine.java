/*
 * JythonScriptEngine.java
 *
 * Created on 8 May 2010
 */

package org.meta.scripting.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.meta.common.resource.StringResource;
import org.meta.scripting.ScriptEngine;

import org.python.util.PythonInterpreter;

/**
 * The Jython Script engine
 *
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class JythonScriptEngine implements ScriptEngine {

    protected PythonInterpreter pyInterpreter;

    private static final String PYTHON_LIB_PATH = "../lib/ext/meta-jython";
    
    /** Creates a new instance of JythonScriptEngine */
    public JythonScriptEngine() {

    }

    /**
     * Perform any initialization of the ScriptEngine
     */
    @Override
    public void init() {
        File pluginDir = new File(StringResource.getInstance().getPluginDir());

        Properties props = new Properties();
        props.setProperty("python.home", PYTHON_LIB_PATH);
        props.setProperty("python.cachedir", StringResource.getInstance().getRemoteAppDir());
        props.setProperty("python.path", PYTHON_LIB_PATH
                          + File.pathSeparatorChar + pluginDir.getName());

        PythonInterpreter.initialize(System.getProperties(), props, new String[]{""});
        pyInterpreter = new PythonInterpreter();
        pyInterpreter.setOut(System.out);
        pyInterpreter.setErr(System.err);
    }

    /**
     * Load built in modules for this script engine if any
     *
     * @throws Exception in case there is problem loading the built ins
     */
    @Override
    public void loadBuiltInModules() throws Exception {
        pyInterpreter.exec("import metastudio");
        pyInterpreter.exec("from metastudio import *");
    }

    @Override
    public Object execute(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);

        return execute(fis);
    }

    @Override
    public Object execute(String script) throws Exception {
        pyInterpreter.exec(script);

        return pyInterpreter.getLocals();
    }

    @Override
    public Object execute(FileInputStream fis) throws Exception {
        pyInterpreter.execfile(fis);

        return pyInterpreter.getLocals();
    }

    /**
     * Get the file type for this ScriptEngine (eg. py, bsh, etc)
     *
     * @return return tile type py, bsh etc.
     */
    @Override
    public String getFileType() {
        return "py";
    }

    /**
     * Get a new instance of this scripting engine
     * @return the new instance
     */
    @Override
    public ScriptEngine getNewInstance() {
        return new JythonScriptEngine();
    }
}
