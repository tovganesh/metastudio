/*
 * FederationServiceEchoConsumer.java
 *
 * Created on March 19, 2006, 2:50 PM
 *
 */

package org.meta.net.impl.consumer;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceConsumer;
import org.meta.net.FederationServiceMessageCodes;
import org.meta.net.exception.FederationServiceConsumptionFailed;
import org.meta.net.exception.FederationServiceDiscoveryFailed;

/**
 * A simple consumer for the "echo" service.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceEchoConsumer extends FederationServiceConsumer {
    
    /** Creates a new instance of FederationServiceEchoConsumer */
    public FederationServiceEchoConsumer() {
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
            writer.write(FederationRequestType.ECHO.toString() + '\n');
            writer.flush();
            
            String responce = reader.readLine();
            if (FederationServiceMessageCodes.valueOf(responce) 
                != FederationServiceMessageCodes.SERVICE_AVAILABLE) {
                throw new IOException("Unexpected responce: " + responce);
            } // end if
            
            // form the federation request object and send the stuff, if the
            // required service is found            
            return new FederationRequest(federatingSocket, reader, writer,
                                         FederationRequestType.ECHO);
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
            if (echoMessage == null) {
                echoMessage = "Echo this! "
                              + InetAddress.getLocalHost().getHostAddress();
                service.getWriter().write( + '\n');
            } // end if

            service.getWriter().write(echoMessage + '\n');
            
            service.getWriter().flush();
            if (!service.getReader().readLine().trim().equals(echoMessage)) {
                throw new FederationServiceConsumptionFailed(
                                    "Unsuccessful echo");
            } // end if
            
            service.closeIt();
        } catch(Exception e) {
            System.err.println("Service consumption failed: " + e);
            e.printStackTrace();
            
            throw new FederationServiceConsumptionFailed(e.toString());
        } // end of try .. catch block
    }

    /**
     * Holds value of property echoMessage.
     */
    private String echoMessage;

    /**
     * Getter for property echoMessage.
     * @return Value of property echoMessage.
     */
    public String getEchoMessage() {
        return this.echoMessage;
    }

    /**
     * Setter for property echoMessage.
     * @param echoMessage New value of property echoMessage.
     */
    public void setEchoMessage(String echoMessage) {
        this.echoMessage = echoMessage;
    }
    
} // end of class FederationServiceEchoConsumer
