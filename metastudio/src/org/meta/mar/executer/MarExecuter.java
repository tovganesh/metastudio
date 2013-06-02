/**
 * MarExecuter.java
 *
 * Created on 17/04/2010
 */

package org.meta.mar.executer;

import org.meta.mar.MarManifest;
import org.meta.mar.MarObject;
import org.meta.scripting.ScriptEngine;
import org.meta.scripting.ScriptInterpreter;
import org.meta.scripting.ScriptLanguage;

/**
 * The MeTA Studio Archive executer.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MarExecuter {
    
    /** Create a new instance of MarExecuter */
    public MarExecuter() {
    }

    /**
     * Start execution of a MarObject, if executable
     * 
     * @param marObject the MarObject to execute
     * @throws a general exception, if unable to execute, or there is error
     *         in execution
     */
    public void execute(MarObject marObject) throws Exception {
        // get the manifest information for MarObject
        MarManifest manifest = marObject.getMarManifest();

        // get lang and implementing script engine
        ScriptLanguage lang  = manifest.getScriptLanguage();
        ScriptEngine engine  =
                 ScriptInterpreter.getInstance().findScriptEngineFor(lang);

        // load defaults into the engine
        engine.init();
        engine.loadBuiltInModules();
        
        // finally execute the code
        engine.execute(marObject.getMarItem(manifest.getMainScript()).getContent());
    }
}
