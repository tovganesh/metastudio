/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.mapreduce.impl;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.meta.net.FederationNode;
import org.meta.net.FederationRequest;
import org.meta.net.FederationServiceDiscovery;
import org.meta.net.exception.FederationServiceDiscoveryFailed;
import org.meta.net.impl.consumer.FederationServiceEchoConsumer;
import org.meta.net.impl.consumer.FederationServiceMapReduceConsumer;
import org.meta.net.mapreduce.MapReduceController;
import org.meta.net.mapreduce.MasterReduce;
import org.meta.net.mapreduce.WorkerMap;

/**
 * Implementation of MapReduceController over the Federation framework.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MapReduceControllerFederationImpl extends MapReduceController {
    
    /** Creates an instance of MapReduceControllerFederationImpl */
    public MapReduceControllerFederationImpl() {
    }

    private ArrayList<FederationNode> freeNodeList;

    /**
     * Execute a task based on the worker map and master reduce user
     * defined procedures.
     *
     * @param wm instance of WorkerMap, defining basic operation on data
     * @param mr instance of MasterReduce, defining reduction operation
     *           on map results
     * @param data Data items on which to operate the map function.
     */
    @Override
    public void executeTask(WorkerMap wm, MasterReduce mr,
                            ArrayList<Serializable> data) {
        this.workerMap    = wm;
        this.masterReduce = mr;
        this.taskData     = data;

        Thread mapReduceControllerThread = new Thread() {
          @Override
          public void run() {
            // get a list of federation nodes
            // step 1: use a pre scanned list
            ArrayList<FederationNode> fedNodes =
                    FederationServiceDiscovery.getInstance().listMeTA();

            // step 2: if nothing is found then start discovery process
            if ((fedNodes == null) || (fedNodes.size() == 0)) {
                fedNodes = FederationServiceDiscovery.getInstance().discoverMeTA();
            } // end if

            // step 3: is still nothing is found the use localhost
            if ((fedNodes == null) || (fedNodes.size() == 0)) {
                try {
                    fedNodes = new ArrayList<FederationNode>();
                    fedNodes.add(new FederationNode(
                                  InetAddress.getByName("localhost")));
                } catch (UnknownHostException ex) {
                    System.err.println("No nodes to run map-reduce! : " +
                                       ex.toString());
                    ex.printStackTrace();

                    return;
                } // end of try .. catch block
            } // end if

            // step 4: recreate the nodes list based on the number of processors
            // each node has
            ArrayList<FederationNode> nodes = new ArrayList<FederationNode>();
            for(FederationNode fn : fedNodes) {
                for(int i=0; i<fn.getNumberOfProcessors(); i++)
                    nodes.add(fn);
            } // end for

            // step 5: init the free node list
            freeNodeList = new ArrayList<FederationNode>();

            // and then start execution

            // Create thread group consisting of threads
            // for each node. Each thread then calls back
            // getNextTaskData() to process the data in the
            // taskData list

            ArrayList<WorkerThread> workerThreadList 
                      = new ArrayList<WorkerThread>();
            ThreadGroup workerThreads = new ThreadGroup(
                                              "MapReduce worker thread group");

            // start the threads
            for(int i=0; i<nodes.size(); i++) {
                WorkerThread wt = new WorkerThread(nodes.get(i), workerMap,
                                                   workerThreads);
                wt.start();
                workerThreadList.add(wt);
            } // end for

            // wait for all threads to finish
            for(int i=0; i<nodes.size(); i++) {
                try {
                    workerThreadList.get(i).join();
                } catch (InterruptedException ex) {
                    System.out.println("Unxpected exception" +
                            " in MapReduceControllerFederationImpl." +
                            "executeTask(): " + ex.toString());
                    ex.printStackTrace();

                    pushBackTaskData(workerThreadList.get(i)
                                                     .getCurrentTaskData());
                } // end of try .. catch block
            } // end for

            // TODO: node failures are taken care of, but whether it is
            // fool proof or not is to be checked thoroughly

            // collect worker results, do reduction,
            // distroy thread group

            ArrayList<Serializable> intmResults = new ArrayList<Serializable>();

            // aggragate all the results, then run reduction operation
            for(int i=0; i<nodes.size(); i++) {
                ArrayList<Serializable> res = workerThreadList.get(i)
                                                              .getResultList();
                for(int j=0; j<res.size(); j++) {
                    intmResults.add(res.get(j));
                } // end for
            } // end for
            
            // apply reduction, overwrite taskData with new results
            taskData = masterReduce.reduce(intmResults);
          }
        };

        mapReduceControllerThread.setName("MapReduce controller Thread");
        mapReduceControllerThread.start();
        try {
            mapReduceControllerThread.join();
        } catch (InterruptedException ex) {
            System.err.println("Exception in executeTask(): " + ex.toString());
            ex.printStackTrace();
        } // end of try .. catch block
    }

    /** Inner class handling a WorkerMIap executing on a task data item */
    public class WorkerThread extends Thread {
        private FederationNode node;
        private WorkerMap workerMap;
        private Serializable taskData;
        private ArrayList<Serializable> resultList;
        
        /**
         * Create a new instance of WorkerThread
         *
         * @param node the FederationNode on which the WorkerMap will be executed
         * @param wm the instance of WorkerMap to be executed
         * @param tg the ThreadGroup to which this thread is attached
         */
        public WorkerThread(FederationNode node, WorkerMap wm, ThreadGroup tg) {
            super(tg, "WorkerThread for: " + node.toString());
            this.node       = node;
            this.workerMap  = wm;

            this.resultList = new ArrayList<Serializable>();
        }

        /** Overridden run method */
        @Override
        public void run() {
          taskData = getNextTaskData();

          while(taskData != null) {
            try {
                // first initiate a federation request
                ArrayList<Serializable> data = new ArrayList<Serializable>();
                data.add(taskData);

                FederationServiceMapReduceConsumer fsmrc =
                        new FederationServiceMapReduceConsumer(workerMap, data);
                FederationRequest req = fsmrc.discover(node.getIpAddress());

                // then start a "heart-beat" thread
                startHeartBeatThread();

                // consume the service
                fsmrc.consume(req);

                // get back the processed data
                ArrayList<Serializable> results = fsmrc.getDataList();
                for(Serializable result : results) resultList.add(result);

                // stop the "heart-beat" thread
                stopHeartBeatThread();

                // and close the connections
                req.closeIt();
            } catch (FederationServiceDiscoveryFailed fe) {
                System.err.println("Exception in Worker thread: "
                                   + fe.toString() + ", for node: "
                                   + node.toString());
                fe.printStackTrace();
                System.err.println("Will now try to use other free nodes...");
                
                synchronized(MapReduceControllerFederationImpl.this.freeNodeList) {
                    // add to the end of the list
                    MapReduceControllerFederationImpl.this.freeNodeList.add(node);

                    System.out.println(MapReduceControllerFederationImpl.this.freeNodeList.size());
                    
                    // and change to a new node from beginning of the list
                    // that is not the same as the current node, if none is
                    // found keep trying the same node
                    FederationNode newNode = null;
                    for(FederationNode fn :
                         MapReduceControllerFederationImpl.this.freeNodeList) {
                         if (!fn.getIpAddress().equals(node.getIpAddress())) {
                             newNode = fn; break;
                         } // end if
                    } // end for

                    if (newNode == null) {
                       node = MapReduceControllerFederationImpl
                                       .this.freeNodeList.get(0);
                    } else {
                       node = newNode;
                    } // end if
                }

                System.err.println("Will be using: " + node);

                // there was some problem with processing
                // this data, so save it for other nodes
                pushBackTaskData(taskData);
            } catch (Exception ex) {
                System.err.println("Exception in Worker thread: "
                                   + ex.toString() + ", for node: "
                                   + node.toString());
                ex.printStackTrace();

                // there was some problem with processing
                // this data, so save it for other nodes
                pushBackTaskData(taskData);

                try {
                    // sleep for quite some time
                    sleep(10000);
                } catch (InterruptedException ignored) {}
            } // end of try .. catch block
            
            taskData = getNextTaskData();
          } // end while

          // if we are done we add our selfs in the free node list
          // so as to make us available at a later stage, if some node
          // breaks down
          synchronized(MapReduceControllerFederationImpl.this.freeNodeList) {
              MapReduceControllerFederationImpl.this.freeNodeList.add(node);
          }
        }

        /**
         * Get the current value of resultList.
         *
         * @return current value of resultList
         */
        public ArrayList<Serializable> getResultList() {
            return resultList;
        }

        /**
         * Set a new value for property resultList
         * 
         * @param resultList the new value of property resultList
         */
        public void setResultList(ArrayList<Serializable> resultList) {
            this.resultList = resultList;
        }

        /**
         * Return the current task data
         *
         * @return return the current task data
         */
        public synchronized Serializable getCurrentTaskData() {
           return taskData;
        }

        private boolean stopHeartBeatThread = false;

        private Thread heartBeatThread;

        private void startHeartBeatThread() {
            stopHeartBeatThread = false;

            heartBeatThread = new Thread() {
              @Override
              public void run() {
                  while(!stopHeartBeatThread) {
                      try {
                        // use the ECHO service to ensure that the
                        // federation node is up and running
                        FederationServiceEchoConsumer fsec
                              = new FederationServiceEchoConsumer();
                        FederationRequest freq 
                              = fsec.discover(node.getIpAddress());

                        fsec.setEchoMessage("HB");
                        fsec.consume(freq);

                        // sleep for some good amount of time
                        sleep(10000);

                        // close connections
                        freq.closeIt();
                      } catch (InterruptedException ignored) {
                      } catch (Exception e) {
                        System.err.println("Error in receiving heart-beat: " +
                                            e.toString());
                        e.printStackTrace();

                        // and interrupt the parent thread
                        WorkerThread.this.interrupt();
                        
                        // and exit from this thread too
                        return;
                      } // end of try .. catch block
                  } // end while
              }
            };

            heartBeatThread.setName("MapReduce heart-beat Thread");
            heartBeatThread.start();
        }

        private void stopHeartBeatThread() {
            stopHeartBeatThread = true;

            if (heartBeatThread != null) {
                heartBeatThread.interrupt();
                heartBeatThread = null;
            } // end if
        }
    }
}
