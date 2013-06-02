/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.impl.consumer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.meta.common.Utility;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceConsumer;
import org.meta.net.FederationServiceMessageCodes;
import org.meta.net.exception.FederationServiceConsumptionFailed;
import org.meta.net.exception.FederationServiceDiscoveryFailed;

/**
 * A consumer for service handling user loadable remote applications.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceUserScriptConsumer 
                       extends FederationServiceConsumer {

    private String scriptFile;
    
    /**
     * Create a new instance of FederationServiceUserScriptConsumer using
     * a user defined "boot" script.
     * 
     * @param scriptFile the instance of script file
     */
    public FederationServiceUserScriptConsumer(String scriptFile) {  
        this.scriptFile = scriptFile;
    }
    
    /**
     * Discover if the desired service is available at a specified host.
     *
     * @param serviceProvider address of the service provider.
     * @throws FederationServiceDiscoveryFailed if the required service is 
     *         unavailable on the specified host
     * @return an valid instance of FederationRequest that can be consumed by
     *         a valid instance of this calss
     */
    @Override
    public FederationRequest discover(InetAddress serviceProvider) 
                                      throws FederationServiceDiscoveryFailed {
        try {
            // first establish a connection
            int port = FederationService.getInstance().getFederatingPort();
        
            Socket sock = new Socket(serviceProvider, port);
            SSLSocketFactory sockFactory = 
                 (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket federatingSocket = (SSLSocket) sockFactory.createSocket(
                            sock, serviceProvider.getHostAddress(), port, true);
            SSLServerSocketFactory serverFactory = 
                   (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            federatingSocket.setEnabledCipherSuites(
                    serverFactory.getSupportedCipherSuites());
            
            // next check if the required service is provided
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(federatingSocket.getOutputStream()));
            BufferedReader reader = new BufferedReader(
                      new InputStreamReader(federatingSocket.getInputStream()));
            writer.write(FederationRequestType.USER_SCRIPT.toString() + '\n');
            writer.flush();
            
            String responce = reader.readLine();
            if (FederationServiceMessageCodes.valueOf(responce) 
                != FederationServiceMessageCodes.SERVICE_AVAILABLE) {
                throw new IOException("Unexpected responce: " + responce);
            } // end if
            
            // form the federation request object and send the stuff, if the
            // required service is found            
            return new FederationRequest(federatingSocket, reader, writer,
                                         FederationRequestType.USER_SCRIPT);
        } catch (IOException ioe) {
            throw new FederationServiceDiscoveryFailed("Error while discovery: "
                    + ioe.toString());
        } // end of try .. catch block   
    }

    /**
     * Consume the serive provided by the service provider.
     *
     * @param service the service that is to be consumed
     * @throws FederationServiceConsumptionFailed if an error occurs
     */
    @Override
    public void consume(FederationRequest service) 
                                     throws FederationServiceConsumptionFailed {
        try {
            // first start the interpreter
            bsh.Interpreter scriptInterpreter = new bsh.Interpreter();
            
            scriptInterpreter.eval("importCommands(\"org.meta.commands\")");
            
            // then set the FederationRequest object
            scriptInterpreter.set("__federationRequest", service);
            scriptInterpreter.eval("getFederationRequest() " +
                                       "{ return __federationRequest; }");
            scriptInterpreter.eval("isHandler() { return false; }");
            
            // push the script
            BufferedReader br = new BufferedReader(new InputStreamReader(
                                       new FileInputStream(this.scriptFile)));
            ArrayList<String> lines = Utility.readLines(br);
            br.close();
            
            String script = "";
            service.sendInt(lines.size());
            for(String line : lines) {
                service.sendString(line);
                script += line;
            } // end for
            
            // and then start execution
            scriptInterpreter.eval(script);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            throw new FederationServiceConsumptionFailed();
        } // end of try catch block
    }

    /**
     * Return the value of scriptFile field
     * 
     * @return value of scriptFile field
     */
    public String getScriptFile() {
        return scriptFile;
    }

    /**
     * set the value of script file field
     * 
     * @param scriptFile the new value for scriptFile field
     */
    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

} // end of class FederationServiceUserScriptConsumer
