/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.scripting;

import java.io.File;
import java.io.FilenameFilter;
import org.meta.common.resource.StringResource;

/**
 * Generic plugin loader framework
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PluginLoader {

    private File pluginDir;
    private ScriptEngine theEngine;

    /**
     * Plugin loader for a particular scripting engine
     * @param theEngine instance of the engine
     */
    public PluginLoader(ScriptEngine theEngine) {
        pluginDir = new File(StringResource.getInstance().getPluginDir());        

        this.theEngine = theEngine;

        if (!pluginDir.exists())
            pluginDir.mkdir();
    }

    /**
     * Load all the plugins from the default location
     *
     * @throws Exception if there is problem loading plugins.
     * Note: errors while loading individual exceptions are not thrown, but
     * merely printed on the screen.
     */
    public void load() throws Exception {
        theEngine.loadBuiltInModules(); // ensure default modules are loaded

        for (File file : pluginDir.listFiles(new FilenameFilter() {
                              @Override
                              public boolean accept(File dir, String name) {
                                  return name.endsWith(theEngine.getFileType());
                              }})) {
            try {
                theEngine.execute(file);
            } catch (Exception e) {
                System.err.println("Warning! Unable to import plugin " +
                                   "commands : " + e);
            } // end of try .. catch block
        } // end for
    }

    /**
     * Load a particular script file as a plugin
     * 
     * @param fileName the plugin file name
     * @throws Exception if there is problem loading the plugin
     */
    public void load(String fileName) throws Exception {
        load(new File(fileName));
    }

    /**
     * Load a particular script file as a plugin
     *
     * @param file the plugin file object
     * @throws Exception if there is problem loading the plugin
     */
    public void load(File file) throws Exception {
        theEngine.execute(file);
    }
}
