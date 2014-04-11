/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.net.mapreduce;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Ineterface defining how a reduce operation will be written.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MasterReduce {

    /**
     * The master reduce operation will only run as an aggregator, producing
     * results in the form of one more data sets. In most cases, reduction
     * operation will just sum up intermediate results and produce a single
     * value.
     * 
     * @param intmResults list of intermediate results
     * @return the reduced result list
     */
    public ArrayList<Serializable> reduce(ArrayList<Serializable> intmResults);

}
