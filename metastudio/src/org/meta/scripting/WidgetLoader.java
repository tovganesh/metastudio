/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.scripting;

import java.io.File;
import java.io.FilenameFilter;
import org.meta.common.resource.StringResource;

/**
 * The generic widgets loader for MeTA Studio
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WidgetLoader {

    private File widgetDir;
    private ScriptEngine theEngine;

    /**
     * Create a widget loader for the specified script engine
     *
     * @param theEngine the instance of engine
     */
    public WidgetLoader(ScriptEngine theEngine) {
        widgetDir = new File(StringResource.getInstance().getWidgetsDir());        

        this.theEngine = theEngine;

        if (!widgetDir.exists())
            widgetDir.mkdir();
    }

    public void load() throws Exception {        
        // init all the widgets
        for (File file : widgetDir.listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name.endsWith("bsh");
                                }})) {
           try {
               load(file.toString());
           } catch (Exception e) {
               System.err.println("Warning! Unable to import widget " +
                        "commands : " + e);
           } // end of try .. catch block
        } // end for
    }

    /**
     * Source the widget...
     *
     * @param file the .bsh file in which the widget is defined
     */
    public synchronized void load(String file) throws Exception {
        try {
            final String wFile = file;

            Thread widgetThread = new Thread() {
                @Override
                public void run() {
                    try {
                        ScriptEngine scriptEngine = theEngine.getNewInstance();
                        scriptEngine.init();

                        // each widget needs to be loaded in a new instance of the
                        // engine ..
                        (new PluginLoader(scriptEngine)).load();
                        
                        scriptEngine.execute("source(\""
                            + wFile.toString().replace('\\', '/') + "\")");
                    } catch(Exception e) {
                        System.out.println("Error in : " + getName());
                        e.printStackTrace();
                    } // end of try .. catch block
                }
            };

            widgetThread.setName("Widget thread for: " + file);
            widgetThread.start();
        } catch (Exception e) {
            System.err.println("Warning! Unable to import widget " +
                               "commands : " + e);

            throw new RuntimeException("Warning! Unable to import widget " +
                                       "commands : " + e);
        } // end of try .. catch block
    }
}
