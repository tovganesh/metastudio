/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.mapreduce;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Interface that defines how a Map functions should be written.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface WorkerMap {

    /**
     * The worker map process a list and returs the result of these in a list.
     * The size of input list is usually 1 or more. The output list is usually
     * 1 or equal to the size of the input list.
     *
     * @param data the input data list
     * @return the processed data list
     */
    public ArrayList<Serializable> processData(ArrayList<Serializable> data);
}
