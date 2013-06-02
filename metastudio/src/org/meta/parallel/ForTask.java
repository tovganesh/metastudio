/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.parallel;

import java.util.ArrayList;

/**
 * Represents a code body in a For loop
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class ForTask<T> {

    /**
     * Creates a new instance of ForTask
     */
    public ForTask() {
    }
    
    /**
     * Creates a new instance of ForTask
     */
    public ForTask(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex   = endIndex;
    }

    protected int startIndex;

    /**
     * Get the value of startIndex
     *
     * @return the value of startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Set the value of startIndex
     *
     * @param startIndex new value of startIndex
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
    protected int endIndex;

    /**
     * Get the value of endIndex
     *
     * @return the value of endIndex
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Set the value of endIndex
     *
     * @param endIndex new value of endIndex
     */
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
    
    /**
     * Get a new instance of this task
     * 
     * @return a new instance of this task
     */
    public abstract ForTask<T> getNewInstance(int startIndex, int endIndex);

    /**
     * Run the for task
     */
    public abstract void run();

    /**
     * Get the partial result
     *
     * @return partial result of this for loop
     */
    public abstract T getResult();

    /**
     * Reduce the results from partial result, using default reduction operation
     * <code>ReduceOperation.PLUS</code>
     *
     * @param partResults reduce the results
     * @return the final reduced result
     */
    public T reduceResult(ArrayList<T> partResults) {
        return reduceResult(partResults, ReduceOperation.PLUS);
    }

    /**
     * Reduce the results from partial results.
     *
     * @param partResults reduce the results
     * @param op the reduction operation to be applied
     * @return the final reduced result
     */
    public abstract T reduceResult(ArrayList<T> partResults, ReduceOperation op);
}
