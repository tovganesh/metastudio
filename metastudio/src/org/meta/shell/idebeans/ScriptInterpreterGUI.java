/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import org.meta.scripting.ScriptInterpreter;
import org.meta.scripting.ScriptLanguage;
import org.meta.scripting.exception.NoScriptEngineFound;

/**
 * Extended class to support loading of script interpreter GUI
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScriptInterpreterGUI extends ScriptInterpreter {

    private static ScriptInterpreterGUI _scriptInterpreter;

    protected ScriptInterpreterGUI() throws ClassNotFoundException {
        super();
    }

    /**
     * Get an instance of this class
     *
     * @return instance of ScriptInterpreter
     * @throws ClassNotFoundException if interpreter classes could not be loaded
     */
    public static ScriptInterpreterGUI getInstance() throws ClassNotFoundException {
        if (_scriptInterpreter == null)
            _scriptInterpreter = new ScriptInterpreterGUI();

        return _scriptInterpreter;
    }

    /**
     * Return appropriate instance of the interpreter if available
     *
     * @param scriptLanguage the script language for which interpreter is required
     * @return the script engine instance that can handle the scriptLanguage
     * @throws NoScriptEngineFound is no script engine is found
     */
    public ScriptEngineGUIAdapter findScriptEngineGUIFor(ScriptLanguage scriptLanguage)
                                                         throws NoScriptEngineFound {
        try {
            return (ScriptEngineGUIAdapter) scriptEngines.get(scriptLanguage.toString()+"_GUI")
                                                         .newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            throw new NoScriptEngineFound("Cannot find approriate "
                    + "script engine: " + scriptLanguage.toString());
        } // end try .. catch
    }
}
