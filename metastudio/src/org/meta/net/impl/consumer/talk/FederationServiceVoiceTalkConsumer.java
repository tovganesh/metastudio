/*
 * FederationServiceVoiceTalkConsumer.java 
 *
 * Created on 31 Oct, 2008 
 */

package org.meta.net.impl.consumer.talk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.meta.common.SoundUtility;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceConsumer;
import org.meta.net.FederationServiceMessageCodes;
import org.meta.net.exception.FederationServiceConsumptionFailed;
import org.meta.net.exception.FederationServiceDiscoveryFailed;

/**
 * Support basic voice communication.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceVoiceTalkConsumer 
             extends FederationServiceConsumer {

    /** Creates a new instance of FederationServiceVoiceTalkConsumer */
    public FederationServiceVoiceTalkConsumer() {
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
            writer.write(FederationRequestType.VOICE_TALK.toString() + '\n');
            writer.flush();
            
            String responce = reader.readLine();
            if (FederationServiceMessageCodes.valueOf(responce) 
                != FederationServiceMessageCodes.SERVICE_AVAILABLE) {
                throw new IOException("Unexpected responce: " + responce);
            } // end if
            
            // form the federation request object and send the stuff, if the
            // required service is found            
            return new FederationRequest(federatingSocket, reader, writer,
                                         FederationRequestType.VOICE_TALK);
        } catch (IOException ioe) {
            throw new FederationServiceDiscoveryFailed("Error while discovery: "
                    + ioe.toString());
        } // end of try .. catch block                
    }

    private SoundUtility.CaptureThread captureThread;
    private SoundUtility.SoundOutputThread soundOutputThread;
    
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
          captureThread 
             = SoundUtility.createCaptureThread(
                 service.getFederationConnection().getOutputStream());
        
          soundOutputThread 
             = SoundUtility.createSoundOutputThread(
                 service.getFederationConnection().getInputStream());                    
        } catch(Exception e) {
          System.err.println("Error in Voice talk handler: " + e.toString());
          e.printStackTrace();
          
          throw new FederationServiceConsumptionFailed(
                      "Error in Voice talk handler: " + e.toString());
        } // end of try .. catch block
    }
    
    /**
     * Request to stop the voice chat
     */
    public void stopVoiceChat() {
        captureThread.setStopCapture(true);
        soundOutputThread.setStopSoundOutput(true);
    }
}
