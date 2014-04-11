/*
 * BeanShellScriptEngineGUI.java
 *
 * Created on September 28, 2003, 7:46 PM
 */

package org.meta.shell.idebeans;

import java.io.*;

import javax.swing.JScrollPane;

import org.meta.scripting.ScriptEngine;

import bsh.Interpreter;
import bsh.BshClassManager;
import bsh.util.JConsole;
import bsh.util.NameCompletionTable;
import bsh.classpath.ClassManagerImpl;
import org.meta.common.resource.ColorResource;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;
import org.meta.scripting.PluginLoader;
import org.meta.scripting.WidgetLoader;
import org.meta.scripting.impl.BeanShellScriptEngine;

/**
 * A very simple interface to Bean Shell scripting engine.
 * Check http://www.beanshell.org for details of the scripting 
 * capabilities.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BeanShellScriptEngineGUI extends BeanShellScriptEngine
                                      implements ScriptEngineGUIAdapter {
    
    protected JConsole console;
    
    /** Creates a new instance of BeanShellScriptEngineGUI */
    public BeanShellScriptEngineGUI() {
        super();                
    }
    
    /**
     * Since bean shell has its own UI we use the same in form 
     * of a JScrollPane .. to be directly used by the MeTA Studio.
     *
     * @return JScrollPane - a UI supposed to act as a shell for scripting
     */
    @Override
    public synchronized JScrollPane getShellUI() {
        initInterpreter();
        
        // start the interpreter
        interpreterThread = new Thread() {
            @Override
            public void run() {
                try {
                    interpreter.run();
                } catch (Exception e) {
                    System.out.println("Interpreter error : " + e.toString());
                    System.err.println("Interpreter killed!");
                    console.error("Interpreter killed, " +
                                       "This shell is no longer functional.\n");
                } // end of try .. catch block
            }
        };
        interpreterThread.setName("Beanshell thread");
        interpreterThread.start();        
        
        return console;
    }       

    /**
     * init the interpreter
     */
    private synchronized void initInterpreter() {
        console = new JConsole() {
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                BeanShellScriptEngineGUI.this.finalize();
            }
        };

        init(); // init the interpter

        NameCompletionTable nct = new NameCompletionTable();
        BshClassManager bcm = interpreter.getClassManager();

        // import our commands!
        try {
            loadBuiltInModules(); // load built ins

            (new PluginLoader(this)).load();

            if (!isWidgetLoaded) {
                // init all the widgets
                (new WidgetLoader(this)).load();

                isWidgetLoaded = true;
            } // end if
        } catch (Exception ignored) {
            System.err.println("Warning! Unable to import commands : " + ignored);
        } // end try .. catch block

        nct.add(interpreter.getNameSpace());

        try {
            nct.add(((ClassManagerImpl) bcm).getClassPath());
        } catch (Exception ignored) { }

        console.setNameCompletion(nct);
        console.print(ImageResource.getInstance().getIcon());
        console.print(" BeanShell configured for : "
                      + StringResource.getInstance().getVersion(),
                      FontResource.getInstance().getDescriptionFont(),
                      ColorResource.getInstance().getBannerColor());
        console.println("\n");
        console.print(" Type help(); for a list of ChemLets",
                      FontResource.getInstance().getDescriptionFont(), 
                      ColorResource.getInstance().getBannerColor());
        console.println("\n");
        console.setWaitFeedback(true);
    }

    /**
     * Source the widget...
     * 
     * @param file the .bsh file in which the widget is defined
     */
    @Override
    public synchronized void sourceWidget(String file) throws Exception {
        (new WidgetLoader(this)).load(file);
    }
    
    /**
     * Source the plugin... for default instance of interpreter
     * 
     * @param file the .bsh file in which the plugin is defined
     */
    @Override
    public synchronized void sourcePlugin(String file) throws Exception {
        (new PluginLoader(this)).load(file);
    }
    
    /**
     * stop the interpreter, if that is running
     */
    @Override
    public synchronized void stopInterpreter() {
        try {
            if (interpreterThread != null)
                interpreterThread.interrupt();

            if (interpreter != null) {
                interpreter.getErr().close();
                interpreter.getIn().close();
                interpreter.getOut().close();
                interpreter = null;
            } // end if
            
            if (console != null) {
                console.getInputStream().close();
                console.getErr().close();
                console.getIn().close();
                console.getOut().close();
                console.setWaitFeedback(false);
                console.interruptConsole();
                console = null;
            } // end if

            System.gc();
        } catch (Exception e) {
            System.err.println("Error closing input stream of interpreter " +
                               " : " + e.toString());
            e.printStackTrace();
        }
    }    

    /**
     * Perform any initialization of the ScriptEngine
     */
    @Override
    public void init() {
        if (console != null) interpreter = new Interpreter(console);
        else                 interpreter = new Interpreter();

        interpreter.setExitOnEOF(false);
    }
    
    /**
     * Get a new instance of this scripting engine
     * @return the new instance
     */
    @Override
    public ScriptEngine getNewInstance() {
        return new BeanShellScriptEngineGUI();
    }

    /**
     * final bye .. bye!
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        
        stopInterpreter();
    }            
} // end of class BeanShellScriptEngineGUI
