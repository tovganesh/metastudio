/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.mapreduce.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import org.meta.common.Utility;

/**
 * Helper for defining the Map function as an external script.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MapFunctionScriptHelper extends MapFunction {

    private String bshScriptFileName;
    private String bshScript;

    /** Creates empty instance of MapFunctionScriptHelper */
    public MapFunctionScriptHelper() {
    }

    /** Create instance of MapFunctionScriptHelper */
    public MapFunctionScriptHelper(String bshScriptFileName) 
                                   throws IOException {
        this.bshScriptFileName = bshScriptFileName;

        // read in the script
        ArrayList<String> lines
                = Utility.readLines(new BufferedReader(new InputStreamReader(
                            new FileInputStream(bshScriptFileName))));

        bshScript = "";
        for(String line : lines) {
            bshScript += line.trim() + '\n';
        } // end for
    }

    /**
     * Getter property for bshScript
     *
     * @return the complete source script of this map function
     */
    public String getBshScript() {
        return bshScript;
    }

    /**
     * Setter property for bshScript
     * @param bshScript the new valuw of bshScript
     */
    public void setBshScript(String bshScript) {
        this.bshScript = bshScript;
    }

    /**
     * The worker map processess a list and returs the result of these in a list.
     * The size of input list is usually 1 or more. The output list is usually
     * 1 or equal to the size of the input list.
     *
     * @param data the input data list
     * @return the processed data list
     */
    @Override
    public ArrayList<Serializable> processData(ArrayList<Serializable> data) {
        ArrayList<Serializable> processedData = new ArrayList<Serializable>(0);

        try {
            // first start the interpreter
            bsh.Interpreter scriptInterpreter = new bsh.Interpreter();

            scriptInterpreter.eval("importCommands(\"org.meta.commands\")");

            // pass on the data to be processed
            scriptInterpreter.set("__data", data);
            scriptInterpreter.eval("getData() " +
                                       "{ return __data; }");
            scriptInterpreter.eval("setData(data) " +
                                       "{ __data = data; }");

            // and then start execution
            scriptInterpreter.eval(bshScript);

            // get back the results
            processedData =
                    (ArrayList<Serializable>) scriptInterpreter.get("__data");
        } catch (Exception ignored) {
            System.out.println("Error in " +
              "MapFunctionScriptHelper.processData(): " + ignored.toString());
            ignored.printStackTrace();
        } // end of try catch block

        return processedData;
    }
}
