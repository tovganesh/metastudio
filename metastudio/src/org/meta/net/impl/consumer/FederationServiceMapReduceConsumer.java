/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.impl.consumer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceConsumer;
import org.meta.net.FederationServiceMessageCodes;
import org.meta.net.exception.FederationServiceConsumptionFailed;
import org.meta.net.exception.FederationServiceDiscoveryFailed;
import org.meta.net.mapreduce.WorkerMap;
import org.meta.net.mapreduce.impl.MapFunctionScriptHelper;

/**
 * Consumer of a MapReduce federation request.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceMapReduceConsumer 
             extends FederationServiceConsumer {

    /** Create new instance of FederationServiceMapReduceConsumer */
    public FederationServiceMapReduceConsumer(WorkerMap wm,
                                              ArrayList<Serializable> data) {
        this.workerMap = wm;
        this.dataList  = data;
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
            writer.write(FederationRequestType.MAP_REDUCE.toString() + '\n');
            writer.flush();

            String responce = reader.readLine();
            if (FederationServiceMessageCodes.valueOf(responce)
                != FederationServiceMessageCodes.SERVICE_AVAILABLE) {
                throw new IOException("Unexpected responce: " + responce);
            } // end if

            // form the federation request object and send the stuff, if the
            // required service is found
            return new FederationRequest(federatingSocket, reader, writer,
                                         FederationRequestType.MAP_REDUCE);
        } catch (IOException ioe) {
            throw new FederationServiceDiscoveryFailed("Error while discovery: "
                    + ioe.toString());
        } // end of try .. catch block
    }

    protected WorkerMap workerMap;

    /**
     * Get the value of workerMap
     *
     * @return the value of workerMap
     */
    public WorkerMap getWorkerMap() {
        return workerMap;
    }

    /**
     * Set the value of workerMap
     *
     * @param workerMap new value of workerMap
     */
    public void setWorkerMap(WorkerMap workerMap) {
        this.workerMap = workerMap;
    }

    protected ArrayList<Serializable> dataList;

    /**
     * Get the value of dataList
     *
     * @return the value of dataList
     */
    public ArrayList<Serializable> getDataList() {
        return dataList;
    }

    /**
     * Set the value of dataList
     *
     * @param dataList new value of dataList
     */
    public void setDataList(ArrayList<Serializable> dataList) {
        this.dataList = dataList;
    }

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
            // first get the object streams
            ObjectInputStream ois = new ObjectInputStream(
                service.getFederationConnection().getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(
                service.getFederationConnection().getOutputStream());

            // first send the class name
            oos.writeObject(workerMap.getClass().getName());
        
            // then send the size of the data items array
            oos.writeInt(dataList.size());

            // then send the data items to be processed
            for(int i=0; i<dataList.size(); i++) {
                oos.writeObject(dataList.get(i));
            } // end if

            // handle some post initialization specific to some
            // WorkerMap implimentations like script based worker map
            handlePostInitilization(workerMap, ois, oos);

            // wait ... for the procesing to get over
            // and then collect the number of items in the results array
            int noOfItems = ois.readInt();
            ArrayList<Serializable> results = new ArrayList<Serializable>();

            // then read the results
            for(int i=0; i<noOfItems; i++) {
                results.add((Serializable) ois.readObject());
            } // end for

            // update the data list with the new results
            dataList = results;
        } catch (Exception ex) {
            System.err.println("Error in FederationServiceMapReduceConsumer:" +
                                ex.toString());
            ex.printStackTrace();

            throw new FederationServiceConsumptionFailed(
               "Error in FederationServiceMapReduceConsumer:" + ex.toString());
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
            // send the script
            oos.writeObject(((MapFunctionScriptHelper) wm).getBshScript());
        } // end if
    }
}
