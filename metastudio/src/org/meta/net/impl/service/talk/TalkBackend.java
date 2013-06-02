/*
 * TalkBackend.java
 *
 * Created on July 23, 2006, 1:47 PM
 */

package org.meta.net.impl.service.talk;

import org.meta.net.FederationRequest;

/**
 * The backend call back for the UI.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface TalkBackend {
    /**
     * Method called to send a message.
     *
     * @param talkObj the TalkObject to send
     * @throws IOException if send unsuccessful
     */
    public void sendMessage(TalkObject talkObj) throws java.io.IOException;

    /**
     * Getter for property talkUIClient.
     * @return Value of property talkUIClient.
     */
    public TalkUISupport getTalkUIClient();
    
    /**
     * Regieter UI client
     * 
     * @param ui the UI client for Talk
     */
    public void setTalkUIClient(TalkUISupport ui);

    /**
     * Getter for property displayName.
     * @return Value of property displayName.
     */
    public String getDisplayName();

    /**
     * Setter for property displayName.
     * @param displayName New value of property displayName.
     */
    public void setDisplayName(String displayName);
    
    /**
     * Return the associated FederationRequest object.
     * 
     * @return FederationRequest the associated FederationRequest object
     */
    public FederationRequest getAssociatedFederationRequest();
} // end of interface TalkBackend
