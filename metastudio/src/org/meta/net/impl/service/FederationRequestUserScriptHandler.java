/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.net.impl.service;

import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * A service for handling user loadable remote applications.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestUserScriptHandler
                       extends FederationRequestHandler {

    public FederationRequestUserScriptHandler() {        
    }
    
    /**
     * Method where the processing of FederationRequest starts, this may 
     * inturn lead to passing the call to other FederationHandlers on the
     * local or remote machine, which may allow "routing" of FederationRequests
     * in future.
     * 
     * @param federationRequest the FederationRequest to be handled
     */
    @Override
    public void handleRequest(FederationRequest federationRequest) {
        this.federationRequest = federationRequest;
        
        Thread handlerThread = new Thread(this);        
        handlerThread.setName("User script handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        try {
            // first start the interpreter
            bsh.Interpreter scriptInterpreter = new bsh.Interpreter();
            
            scriptInterpreter.eval("importCommands(\"org.meta.commands\")");
            
            // then set the FederationRequest object
            scriptInterpreter.set("__federationRequest", this.federationRequest);
            scriptInterpreter.eval("getFederationRequest() " +
                                       "{ return __federationRequest; }");
            scriptInterpreter.eval("isHandler() { return true; }");
            
            // pull the script
            int noOfLines = federationRequest.receiveInt();
            String script = "";
            
            for(int i=0; i<noOfLines; i++) 
                script += federationRequest.receiveString();
            
            // and then start execution
            scriptInterpreter.eval(script);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } // end of try catch block
    }
} // end of class FederationRequestUserScriptHandler

