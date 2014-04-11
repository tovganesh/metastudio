/**
 * ScriptInterpreter.java
 *
 * Created on 24/04/2010
 */

package org.meta.scripting;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.scripting.exception.NoScriptEngineFound;

/**
 * Top level interface to get an instance of ScriptEngine that is integrated
 * into MeTA Studio. Follows a Singleton pattern
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScriptInterpreter {

    private static ScriptInterpreter _scriptInterpreter;

    protected HashMap<String, Class> scriptEngines;

    protected ScriptInterpreter() throws ClassNotFoundException {
        StringResource strings   = StringResource.getInstance();
        ResourceBundle resources = ResourceBundle.getBundle(
                                              strings.getScriptengines());

        Enumeration implKeys = resources.getKeys();
        String theKey;

        scriptEngines = new HashMap<String, Class>();

        // read in the script engine classes 
        while(implKeys.hasMoreElements()) {
            theKey = (String) implKeys.nextElement();
            scriptEngines.put(theKey, Class.forName(resources.getString(theKey)));
        } // end while

        // make sure that a few required directories are present for
        // the intepreters to work with MeTA
        initDir();
    }

    /**
     * make required directories, if not present
     */
    private void initDir() {
        // the plugins dir
        File pluginDir = new File(StringResource.getInstance().getPluginDir());
        if (!pluginDir.exists()) {
            if (!pluginDir.mkdir()) {
                System.out.println("Could not make plugin directory: " +
                                    pluginDir.toString());
            } // end if
        } // end if
        
        // the widgets dir
        File widgetDir = new File(StringResource.getInstance().getWidgetsDir());

        if (!widgetDir.exists()) {
            if (!widgetDir.mkdir()) {
                System.out.println("Could not make widget directory: " +
                                    widgetDir.toString());
            } // end if
        } // end if

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
     * Get an instance of this class
     *
     * @return instance of ScriptInterpreter
     * @throws ClassNotFoundException if interpreter classes could not be loaded
     */
    public static ScriptInterpreter getInstance() throws ClassNotFoundException {
        if (_scriptInterpreter == null)
            _scriptInterpreter = new ScriptInterpreter();

        return _scriptInterpreter;
    }

    /**
     * Return appropriate instance of the interpreter if available
     * 
     * @param scriptLanguage the script language for which interpreter is required
     * @return the script engine instance that can handle the scriptLanguage
     * @throws NoScriptEngineFound is no script engine is found
     */
    public ScriptEngine findScriptEngineFor(ScriptLanguage scriptLanguage) 
                                            throws NoScriptEngineFound {
        try {
            return (ScriptEngine) scriptEngines.get(scriptLanguage.toString())
                                               .newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            throw new NoScriptEngineFound("Cannot find approriate "
                    + "script engine: " + scriptLanguage.toString());
        } // end try .. catch
    }

    /**
     * Return the most probable interpreter available based on file extension
     *
     * @param scriptFile the script file for which interpreter is required
     * @return the script engine instance that can handle the scriptFile
     * @throws NoScriptEngineFound is no script engine is found
     */
    public ScriptEngine findProbableScriptEngineFor(File scriptFile)
                                                    throws NoScriptEngineFound {
        String fileExt = Utility.getFileExtension(scriptFile);

        try {
            return (ScriptEngine) scriptEngines.get(fileExt)
                                               .newInstance();
        } catch(Exception e) {
            e.printStackTrace();
            throw new NoScriptEngineFound("Cannot find approriate "
                    + "script engine: " + fileExt);
        } // end try .. catch
    }
}
