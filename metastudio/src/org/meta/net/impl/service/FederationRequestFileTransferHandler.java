/*
 * FederationRequestFileTransferHandler.java 
 *
 * Created on 12 Jul, 2008 
 */

package org.meta.net.impl.service;

import java.io.FileInputStream;
import java.io.OutputStream;
import org.meta.common.Utility;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;

/**
 * A service for handling file transfers.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestFileTransferHandler 
                       extends FederationRequestHandler {

    /** Creates a new instance of FederationRequestFileTransferHandler */
    public FederationRequestFileTransferHandler() {        
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
        handlerThread.setName("File transfer handler Thread");
        handlerThread.start();
    }

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        try {     
            // get the file name to send
            String fileToSend = federationRequest.receiveString();
            
            // then send the stream of bytes in the file
            OutputStream ostream = federationRequest.getFederationConnection()
                                                        .getOutputStream();
            FileInputStream fis = new FileInputStream(fileToSend);
            
            Utility.streamCopy(fis, ostream);                        
            
            fis.close();            
            federationRequest.closeIt();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } // end try ... catch block
    }
}
