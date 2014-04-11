/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.mapreduce;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

/**
 * The main controller interface for MapReduce applications
 * written in MeTA Studio. 
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class MapReduceController {

    protected WorkerMap workerMap;
    protected MasterReduce masterReduce;
    protected ArrayList<Serializable> taskData;
    protected Stack<Serializable> taskDataStack;
    
    /** Default constructor for MapReduceController */
    public MapReduceController() {
    }

    /**
     * Get the property masterReduce
     *
     * @return current value of masterReduce
     */
    public MasterReduce getMasterReduce() {
        return masterReduce;
    }

    /**
     * Set the property of masterReduce
     *
     * @param masterReduce the new value of masterReduce
     */
    public void setMasterReduce(MasterReduce masterReduce) {
        this.masterReduce = masterReduce;
    }

    /**
     * Get the property taskData
     *
     * @return current value of taskData
     */
    public ArrayList<Serializable> getTaskData() {
        return taskData;
    }

    /**
     * Set the property of taskData
     *
     * @param taskData the new value of taskData
     */
    public void setTaskData(ArrayList<Serializable> taskData) {
        this.taskData = taskData;
    }

    /**
     * Get the property workerMap
     *
     * @return current value of workerMap
     */
    public WorkerMap getWorkerMap() {
        return workerMap;
    }

    /**
     * Set the property of workerMap
     *
     * @param workerMap the new value of workerMap
     */
    public void setWorkerMap(WorkerMap workerMap) {
        this.workerMap = workerMap;
    }

    /**
     * Return the next available taskData item.
     * 
     * @return the instance next available taskData, null otherwise
     */
    public synchronized Serializable getNextTaskData() {
        if (taskDataStack == null) {
            taskDataStack = new Stack<Serializable>();
            
            for(Serializable data : taskData) {
                taskDataStack.push(data);
            } // end for
        } // end if

        if (!taskDataStack.empty()) return taskDataStack.pop();
        else                        return null;
    }

    /**
     * Push back data on to the task data stack
     * 
     * @param data the data to be pushed back
     */
    protected synchronized void pushBackTaskData(Serializable data) {
        if (taskData == null) return;
        if (taskDataStack == null) return;

        taskDataStack.push(data);
    }

    /**  
     * Execute a task based on the worker map and master reduce user
     * defined procedures.
     * 
     * @param wm instance of WorkerMap, defining basic operation on data
     * @param mr instance of MasterReduce, defining reduction operation
     *           on map results
     * @param data Data items on which to operate the map function.
     */
    public abstract void executeTask(WorkerMap wm, MasterReduce mr,
                                     ArrayList<Serializable> data);
}
