/*
 * FederationRequestTalkHandler.java
 *
 * Created on July 19, 2006, 10:17 PM
 */

package org.meta.net.impl.service.talk;

import java.io.*;
import org.meta.common.Utility;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * An elaborate talk server, along with clean mechanism to integrate into the
 * MeTA Studio UI.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestTalkHandler extends FederationRequestHandler
                                          implements TalkBackend {
    
    /** Creates a new instance of FederationRequestTalkHandler */
    public FederationRequestTalkHandler() {
        // set display name
        displayName = System.getProperty("user.name");
    }

    /**
     * Method where the processing of FederationRequest starts, this may 
     * in-turn lead to passing the call to other FederationHandlers on the
     * local or remote machine, which may allow "routing" of FederationRequests
     * in future.
     * 
     * @param federationRequest the FederationRequest to be handled
     */
    @Override
    public void handleRequest(FederationRequest federationRequest) {
        this.federationRequest = federationRequest;
        
        Thread handlerThread = new Thread(this);   
        handlerThread.setName("Talk handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        fireRequestReceivedNotification();
        
        // ensure the display name reflects on the other side
        setDisplayName(displayName);

        ObjectInputStream ois = null;
                
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
            System.out.println("Failed to initiate talk UI: " + e);
            e.printStackTrace();            
        } // end of try .. catch block
        
        try {
            ois = new ObjectInputStream(federationRequest
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
                            federationRequest.getFederationConnection().close();
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
                break;
            } // end try .. catch block
        } // end while
        
        fireRequestProcessedNotification();
    }    
    
    /**
     * Return the associated FederationRequest object.
     * 
     * @return FederationRequest the associated FederationRequest object
     */
    @Override
    public FederationRequest getAssociatedFederationRequest() {
        return federationRequest;
    }
    
    private ObjectOutputStream oos;
    
    /**
     * Method called to send a message.
     *
     * @param talkObj - the TalkObject to send
     * @throws IOException if send unsuccessful
     */
    @Override
    public void sendMessage(TalkObject talkObj) throws IOException {
        if (oos == null) {
            oos = new ObjectOutputStream(
                 federationRequest.getFederationConnection().getOutputStream());
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
} // end of class FederationRequestTalkHandler
