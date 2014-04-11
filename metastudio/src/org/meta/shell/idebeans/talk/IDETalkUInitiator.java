/*
 * IDETalkUInitiator.java
 *
 * Created on July 23, 2006, 1:44 PM
 */

package org.meta.shell.idebeans.talk;

import org.meta.net.impl.service.talk.TalkBackend;
import org.meta.net.impl.service.talk.TalkUInitiator;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;

/**
 * Talk UI initiator.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDETalkUInitiator implements TalkUInitiator {
    
    /** Creates a new instance of IDETalkUInitiator */
    public IDETalkUInitiator() {
    }

    /**
     * Initilize the talk UI.
     *
     * @param backend the backend object (consumer / service provider)
     */
    @Override
    public void initTalkUI(TalkBackend backend) {
        // initilise UI        
        IDETalkFrame talkFrame = new IDETalkFrame(
            MainMenuEventHandlers.getInstance(null).getIdeInstance(), backend);
    }
    
} // end of class IDETalkUInitiator
