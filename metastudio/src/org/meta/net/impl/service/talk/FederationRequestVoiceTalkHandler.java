/*
 * FederationRequestVoiceTalkHandler.java 
 *
 * Created on 31 Oct, 2008 
 */

package org.meta.net.impl.service.talk;

import org.meta.common.SoundUtility;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * Handler for simple voice based service.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestVoiceTalkHandler extends FederationRequestHandler {

    /** Creates a new instance of FederationRequestVoiceTalkHandler */
    public FederationRequestVoiceTalkHandler() {
    }
    
    /**
     * Method where the processing of FederationRequest starts, this may 
     * inturn lead to passing the call to other FederationHandlers on the
     * local or remote machine, which may allow "routing" of FederationRequests
     * in future.
     * 
     * @param federationRequest the FederationRequest to be handled
     */
    @Override
    public void handleRequest(FederationRequest federationRequest) {
        this.federationRequest = federationRequest;
        
        Thread handlerThread = new Thread(this);   
        handlerThread.setName("Voice talk handler Thread");
        handlerThread.start();
    }

    private SoundUtility.CaptureThread captureThread;
    private SoundUtility.SoundOutputThread soundOutputThread;
    
    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        try {
          captureThread 
             = SoundUtility.createCaptureThread(
                 federationRequest.getFederationConnection().getOutputStream());
        
          soundOutputThread 
             = SoundUtility.createSoundOutputThread(
                 federationRequest.getFederationConnection().getInputStream());                    
        } catch(Exception e) {
          System.err.println("Error in Voice talk handler: " + e.toString());
          e.printStackTrace();
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
