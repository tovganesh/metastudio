/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import javax.swing.JScrollPane;

/**
 * GUI adapter for the script engine
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface ScriptEngineGUIAdapter {

    /**
     * The only method required to be implemented is a shell UI in the form
     * of a JScrollPane .. to be directly used by the MeTA Studio.
     *
     * @return JScrollPane - a UI supposed to act as a shell for scripting
     */
    public JScrollPane getShellUI();

    /**
     * stop the interpreter, if that is running
     */
    public void stopInterpreter();

    /**
     * Source the widget...
     *
     * @param file the script file in which the widget is defined
     */
    public void sourceWidget(String file) throws Exception;

    /**
     * Source the plugin... for default instance of interpreter
     *
     * @param file the script file in which the plugin is defined
     */
    public void sourcePlugin(String file) throws Exception;
}
