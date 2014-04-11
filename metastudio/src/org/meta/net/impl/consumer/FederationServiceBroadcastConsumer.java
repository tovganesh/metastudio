/*
 * FederationServiceBroadcastConsumer.java
 *
 * Created on July 16, 2006, 9:15 PM
 */

package org.meta.net.impl.consumer;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import org.meta.common.resource.StringResource;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceConsumer;
import org.meta.net.FederationServiceMessageCodes;
import org.meta.net.exception.FederationServiceConsumptionFailed;
import org.meta.net.exception.FederationServiceDiscoveryFailed;

/**
 * A simple "broadcast" service consumer.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceBroadcastConsumer 
                       extends FederationServiceConsumer {
    
    /** Creates a new instance of FederationServiceBroadcastConsumer */
    public FederationServiceBroadcastConsumer() {
        broadcastMessage = StringResource.getInstance().getVersion();
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
            writer.write(FederationRequestType.BROADCAST.toString() + '\n');
            writer.flush();
            
            String responce = reader.readLine();
            if (FederationServiceMessageCodes.valueOf(responce) 
                != FederationServiceMessageCodes.SERVICE_AVAILABLE) {
                throw new IOException("Unexpected responce: " + responce);
            } // end if
            
            // form the federation request object and send the stuff, if the
            // required service is found            
            return new FederationRequest(federatingSocket, reader, writer,
                                         FederationRequestType.BROADCAST);
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
            service.getWriter().write(broadcastMessage + '\n');
            service.getWriter().flush();
            
            service.closeIt();
        } catch(Exception e) {
            System.err.println("Service consumption failed: " + e);
            e.printStackTrace();
            
            throw new FederationServiceConsumptionFailed(e.toString());
        } // end of try .. catch block
    }

    /**
     * Holds value of property broadcastMessage.
     */
    private String broadcastMessage;

    /**
     * Getter for property broadcastMessage.
     * @return Value of property broadcastMessage.
     */
    public String getBroadcastMessage() {
        return this.broadcastMessage;
    }

    /**
     * Setter for property broadcastMessage.
     * @param broadcastMessage New value of property broadcastMessage.
     */
    public void setBroadcastMessage(String broadcastMessage) {
        this.broadcastMessage = broadcastMessage;
    }
} // end of class FederationServiceBroadcastConsumer
