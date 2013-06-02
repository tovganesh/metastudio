/*
 * FederationServiceFileTransferConsumer.java 
 *
 * Created on 10 Jul, 2008 
 */

package org.meta.net.impl.consumer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
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
 * A consumer for a "file transfer" service.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceFileTransferConsumer 
                              extends FederationServiceConsumer {
    
    private String fileToReceive, fileToSave;
    
    /** Creates a new instance of FederationServiceFileTransferConsumer */
    public FederationServiceFileTransferConsumer(String fileToReceive,
                                                 String fileToSave) {
        this.fileToReceive = fileToReceive;
        this.fileToSave    = fileToSave;
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
            writer.write(FederationRequestType.FILE_TRANSFER.toString() + '\n');
            writer.flush();
            
            String responce = reader.readLine();
            if (FederationServiceMessageCodes.valueOf(responce) 
                != FederationServiceMessageCodes.SERVICE_AVAILABLE) {
                throw new IOException("Unexpected responce: " + responce);
            } // end if
            
            // form the federation request object and send the stuff, if the
            // required service is found            
            return new FederationRequest(federatingSocket, reader, writer,
                                         FederationRequestType.FILE_TRANSFER);
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
            // send in the request file name (preferably absolute path)
            service.sendString(fileToReceive);
            
            // then receive the stream of bytes in the file
            InputStream istream 
                           = service.getFederationConnection().getInputStream();
            FileOutputStream fos = new FileOutputStream(fileToSave);
            
            Utility.streamCopy(istream, fos);                        
            
            fos.close();            
            service.closeIt();
        } catch (IOException ioe) {
            throw new FederationServiceConsumptionFailed(
                           "Error while consuming service: " + ioe.toString());
        } // end try ... catch block
    }
}
