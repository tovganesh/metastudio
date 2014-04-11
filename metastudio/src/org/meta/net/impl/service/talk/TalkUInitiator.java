/*
 * TalkUInitiator.java
 *
 * Created on July 23, 2006, 1:40 PM
 */

package org.meta.net.impl.service.talk;

/**
 * Interface for UI initiator.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface TalkUInitiator {
    /**
     * Initialize the talk UI.
     *
     * @param backend the backend object (consumer / service provider)
     */
    public void initTalkUI(TalkBackend backend);
} // end of interface TalkUInitiator
