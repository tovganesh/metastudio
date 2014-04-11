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
 * Helper class when defining reduce function as an external script.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ReduceFunctionScriptHelper extends ReduceFunction {

    private String bshScriptFileName;
    private String bshScript;

    /** Create instance of ReduceFunctionScriptHelper */
    public ReduceFunctionScriptHelper(String bshScriptFileName) 
                                      throws IOException {
        this.bshScriptFileName = bshScriptFileName;
        
        // read in the script
        ArrayList<String> lines
                = Utility.readLines(new BufferedReader(new InputStreamReader(
                            new FileInputStream(bshScriptFileName))));

        bshScript = "";
        for(String line : lines) {
            bshScript += line + '\n';
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
     * The master reduce operation will only run as an aggregator, producing
     * results in the form of one more data sets. In most cases, reduction
     * operation will just sum up intermediate results and produce a single
     * value.
     *
     * @param intmResults list of intermediate results
     * @return the reduced result list
     */
    @Override
    public ArrayList<Serializable> reduce(ArrayList<Serializable> intmResults) {
        ArrayList<Serializable> processedData = new ArrayList<Serializable>(0);

        try {
            // first start the interpreter
            bsh.Interpreter scriptInterpreter = new bsh.Interpreter();

            scriptInterpreter.eval("importCommands(\"org.meta.commands\")");

            // pass on the data to be processed
            scriptInterpreter.set("__data", intmResults);
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
