/**
 * JSchemeScriptEngine.java
 *
 * Created on May 8, 2010
 */

package org.meta.scripting.impl;

import java.io.File;
import java.io.FileInputStream;
import org.meta.scripting.ScriptEngine;


/**
 * JScheme script engine
 *
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class JSchemeScriptEngine implements ScriptEngine {

    /** Creates a new instance of JSchemeScriptEngine */
    public JSchemeScriptEngine() {

    }

    /**
     * Perform any initialization of the ScriptEngine
     */
    @Override
    public void init() {
        // TODO:
    }

    /**
     * Load built in modules for this script engine if any
     *
     * @throws Exception in case there is problem loading the built ins
     */
    @Override
    public void loadBuiltInModules() throws Exception {
        // TODO:
    }
    
    @Override
    public Object execute(File file) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object execute(String script) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object execute(FileInputStream fis) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the file type for this ScriptEngine (eg. py, bsh, etc)
     *
     * @return return tile type py, bsh etc.
     */
    @Override
    public String getFileType() {
        return "scheme";
    }

    /**
     * Get a new instance of this scripting engine
     * @return the new instance
     */
    @Override
    public ScriptEngine getNewInstance() {
        return new JSchemeScriptEngine();
    }
}
