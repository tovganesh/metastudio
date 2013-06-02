/*
 * TalkUISupport.java
 *
 * Created on July 23, 2006, 10:50 AM
 */

package org.meta.net.impl.service.talk;

/**
 * An interface to route received messages to the UI.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface TalkUISupport {
    /**
     * call back to UI when a message is received.
     *
     * @param talkObj reference to the message object
     */
    public void messageReceived(TalkObject talkObj);
} // end of interface TalkUISupport
