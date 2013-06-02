/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.impl.service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestHandler;
import org.meta.net.mapreduce.WorkerMap;
import org.meta.net.mapreduce.impl.MapFunctionScriptHelper;

/**
 * The map reduce handler service.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestMapReduceHandler 
             extends FederationRequestHandler {

    /** Creates instance of FederationRequestMapReduceHandler */
    public FederationRequestMapReduceHandler() {
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
        handlerThread.setName("MapReduce request handler Thread");
        handlerThread.start();
    }

    private ArrayList<Serializable> result;

    /**
     * The request should always handled in a thread
     */
    @Override
    public void run() {
        try {
            // get the object streams
            ObjectOutputStream oos = new ObjectOutputStream(
                federationRequest.getFederationConnection().getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(
                federationRequest.getFederationConnection().getInputStream());
            
            // get the fully qualified name of worker map class
            String workerMapName = (String) ois.readObject();
 
            // then read the data set one by one
            int numberOfItems = ois.readInt();
            ArrayList<Serializable> data = new ArrayList<Serializable>();

            for(int i=0; i<numberOfItems; i++) {
                data.add((Serializable) ois.readObject());
            } // end for

            // then initialise the workerMap class .. ensure that it works
            WorkerMap wm =
                    (WorkerMap) Class.forName(workerMapName).newInstance();
            
            // handle some post initialization specific to some
            // WorkerMap implimentations like script based worker map
            handlePostInitilization(wm, ois, oos);

            // init the results array
            result = wm.processData(data);
          
            // first send the recalculatd number of items for the result
            numberOfItems = result.size();
            oos.writeInt(numberOfItems);

            // then push on the actual data
            for(int i=0; i<numberOfItems; i++) {
                oos.writeObject(result.get(0));
            } // end for

            // and close the connections
            federationRequest.closeIt();
        } catch(Exception e) {
            System.err.println("Error in handling request: " + e.toString());
            e.printStackTrace();
        } // end of try .. catch block
    }

    /**
     * Handle post initialization of WorkerMap which require additional
     * "payload" like in the case of script based WorkerMap implementations
     *
     * @param wm the instance of the WorkerMap to be further inited
     * @param ois the ObjectInputStream associated with federation connection
     * @param oos the ObjectOutputStream associated with federation connection
     */
    private void handlePostInitilization(WorkerMap wm,
                       ObjectInputStream ois, ObjectOutputStream oos)
                 throws Exception {
        if (wm instanceof MapFunctionScriptHelper) {
            // then read in the script
            ((MapFunctionScriptHelper) wm).setBshScript(
                                              (String) ois.readObject());
        } // end if
    }
}
