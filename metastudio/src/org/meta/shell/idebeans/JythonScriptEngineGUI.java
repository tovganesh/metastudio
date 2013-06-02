/*
 * JythonScriptEngineGUI.java
 *
 * Created on 8 May 2010
 */

package org.meta.shell.idebeans;

import javax.swing.JScrollPane;
import org.meta.scripting.ScriptEngine;
import org.meta.scripting.impl.JythonScriptEngine;

/**
 * The Jython Script engine
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class JythonScriptEngineGUI extends JythonScriptEngine
                                   implements ScriptEngineGUIAdapter  {

    /** Creates a new instance of JythonScriptEngineGUI */
    public JythonScriptEngineGUI() {
        super();
    }
    
    /**
     * Get a new instance of this scripting engine
     * @return the new instance
     */
    @Override
    public ScriptEngine getNewInstance() {
        return new JythonScriptEngineGUI();
    }

    @Override
    public JScrollPane getShellUI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopInterpreter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sourceWidget(String file) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sourcePlugin(String file) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
