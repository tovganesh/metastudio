/*
 * FederationServiceTalkConsumer.java
 *
 * Created on July 19, 2006, 10:07 PM
 */

package org.meta.net.impl.consumer.talk;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import org.meta.common.Utility;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceConsumer;
import org.meta.net.FederationServiceMessageCodes;
import org.meta.net.exception.FederationServiceConsumptionFailed;
import org.meta.net.exception.FederationServiceDiscoveryFailed;
import org.meta.net.impl.service.talk.TalkBackend;
import org.meta.net.impl.service.talk.TalkCommand;
import org.meta.net.impl.service.talk.TalkObject;
import org.meta.net.impl.service.talk.TalkUISupport;
import org.meta.net.impl.service.talk.TalkUInitiator;

/**
 * An elaborate talk client, together with a clean mechanism to integrated 
 * into MeTA Studio UI.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceTalkConsumer extends FederationServiceConsumer
                                           implements TalkBackend {
    
    private FederationRequest service;
    
    /** Creates a new instance of FederationServiceTalkConsumer */
    public FederationServiceTalkConsumer() {
        // set display name
        displayName = System.getProperty("user.name");
    }
    
    /**
     * Discover if the desired service is available at a specified host.
     *
     * @param serviceProvider address of the service provider.
     * @throws FederationServiceDiscoveryFailed if the required service is 
     *         unavailable on the specified host
     * @return an valid instance of FederationRequest that can be consumed by
     *         a valid instance of this class
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
            writer.write(FederationRequestType.TALK.toString() + '\n');
            writer.flush();
            
            String responce = reader.readLine();
            if (FederationServiceMessageCodes.valueOf(responce) 
                != FederationServiceMessageCodes.SERVICE_AVAILABLE) {
                throw new IOException("Unexpected responce: " + responce);
            } // end if
            
            // form the federation request object and send the stuff, if the
            // required service is found            
            return new FederationRequest(federatingSocket, reader, writer,
                                         FederationRequestType.TALK);
        } catch (IOException ioe) {
            throw new FederationServiceDiscoveryFailed("Error while discovery: "
                    + ioe.toString());
        } // end of try .. catch block                
    }

    /**
     * Consume the service provided by the service provider.
     *
     * @param service the service that is to be consumed
     * @throws FederationServiceConsumptionFailed if an error occurs
     */
    @Override
    public void consume(FederationRequest service) 
                         throws FederationServiceConsumptionFailed {
        this.service = service;
        
        // initiate the UI        
        try {
            if (customTalkUIInitiator == null) {
             TalkUInitiator uiInitiator = 
               (TalkUInitiator) 
                (Utility.getDefaultImplFor(TalkUInitiator.class).newInstance());
            
             uiInitiator.initTalkUI(this);            
            } else {
             customTalkUIInitiator.initTalkUI(this);                   
            } // end if
        } catch (Exception e) {
            e.printStackTrace();
            throw new FederationServiceConsumptionFailed("Failed to initiate " +
                                                         "talk UI: " + e);            
        } // end of try .. catch block
        
        // then keep consuming till the session ends
        Thread consumeThread = new Thread() {
            @Override
            public void run() {
                ObjectInputStream ois = null;
                
                try {
                    ois = new ObjectInputStream(
                            FederationServiceTalkConsumer.this.service
                            .getFederationConnection().getInputStream());
                } catch (IOException ex) {
                    System.out.println("Exception in " +
                            "FederationServiceTalkConsumer>> consumeThread: " +
                            ex.toString());
                    ex.printStackTrace();
                    return;
                } // end
                
                while(true) {
                    TalkObject talkObj;
                    
                    try {
                        talkObj = (TalkObject) ois.readObject();
                        
                        switch(talkObj.getType()) {
                            case TALK_COMMAND:                                
                                TalkCommand tc = 
                                   (TalkCommand) talkObj.getTalkObjectContent();
                                
                                talkUIClient.messageReceived(talkObj);
                                
                                if (tc.getType() 
                                     == TalkCommand.CommandType.CLOSE_SESSION) {
                                    FederationServiceTalkConsumer.this.service
                                            .getFederationConnection().close();
                                    return;
                                } // end if
                                
                                break;
                            default:
                                talkUIClient.messageReceived(talkObj);
                                break;
                        } // end switch .. case
                    } catch (ClassCastException ex) {
                        ex.printStackTrace(); 
                    } catch(ClassNotFoundException ex) {
                        ex.printStackTrace(); 
                    } catch (IOException ex) {
                        ex.printStackTrace(); 
                        System.err.println("Unable to further continue talk " +
                                "session! " + ex.toString());
                        try {
                            FederationServiceTalkConsumer.this.service
                                            .getFederationConnection().close();
                            
                            TalkCommand tlkCmd = 
                                    new TalkCommand("Close Session",
                                         TalkCommand.CommandType.CLOSE_SESSION);
                            
                            TalkObject tlkObj = new TalkObject();
                            tlkObj.setType(
                                      TalkObject.TalkObjectType.TALK_COMMAND);
                            tlkObj.setTalkObjectContent(tlkCmd);
                            
                            talkUIClient.messageReceived(tlkObj);
                        } catch (Exception ignored) {
                            ignored.printStackTrace(); 
                            System.err.println("Unable to cleanely shutdown " +
                                "talk session! " + ignored.toString());
                        } // end try .. catch block
                        
                        break;
                    } // end try .. catch block
                } // end while
            }
        }; 
        
        consumeThread.setName("Talk consumer Thread");
        consumeThread.start();
    }
    
    /**
     * Return the associated FederationRequest object.
     * 
     * @return FederationRequest the associated FederationRequest object
     */
    @Override
    public FederationRequest getAssociatedFederationRequest() {
        return service;
    }
    
    private ObjectOutputStream oos;
    
    /**
     * Method called to send a message.
     *
     * @param talkObj the TalkObject to send
     * @throws IOException if send unsuccessful
     */
    @Override
    public void sendMessage(TalkObject talkObj) throws IOException {
        if (oos == null) {
            oos = new ObjectOutputStream(
                           service.getFederationConnection().getOutputStream());
        } // end if
        
        oos.writeObject(talkObj);
    }

    /**
     * Holds value of property talkUIClient.
     */
    private TalkUISupport talkUIClient;

    /**
     * Getter for property talkUIClient.
     * @return Value of property talkUIClient.
     */
    @Override
    public TalkUISupport getTalkUIClient() {
        return this.talkUIClient;
    }

    /**
     * Setter for property talkUIClient.
     * @param talkUIClient New value of property talkUIClient.
     */
    @Override
    public void setTalkUIClient(TalkUISupport talkUIClient) {
        this.talkUIClient = talkUIClient;
    } 
    
    private String displayName;
    
    /**
     * Getter for property displayName.
     * @return Value of property displayName.
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setter for property displayName.
     * @param displayName New value of property displayName.
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        
        TalkCommand tc = new TalkCommand(displayName, 
                                 TalkCommand.CommandType.DISPLAY_NAME_CHANGED);
        
        TalkObject talkObj = new TalkObject();
        talkObj.setType(TalkObject.TalkObjectType.TALK_COMMAND);
        talkObj.setTalkObjectContent(tc);
        
        try {            
            sendMessage(talkObj);
        } catch (IOException ex) {
            ex.printStackTrace();
        } // end of try .. catch block
    }
    
    protected TalkUInitiator customTalkUIInitiator;

    /**
     * Get the value of customTalkUIInitiator
     *
     * @return the value of customTalkUIInitiator
     */
    public TalkUInitiator getCustomTalkUIInitiator() {
        return customTalkUIInitiator;
    }

    /**
     * Set the value of customTalkUIInitiator
     *
     * @param customTalkUIInitiator new value of customTalkUIInitiator
     */
    public void setCustomTalkUIInitiator(TalkUInitiator customTalkUIInitiator) {
        this.customTalkUIInitiator = customTalkUIInitiator;
    }

} // end of class FederationServiceTalkConsumer
