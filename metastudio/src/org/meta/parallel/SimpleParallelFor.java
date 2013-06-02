/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.parallel;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Simple parallel for pattern.
 * Note the loop dependencies have to be taken care of by the programming.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SimpleParallelFor<T> {

    /** Creates new instance of SimpleParallelFor */
    public SimpleParallelFor(int startIndex, int endIndex, ForTask<T> forTask) {
        this(startIndex, endIndex, forTask, ReduceOperation.USER_DEFINED);
    }
    
    /** Creates new instance of SimpleParallelFor */
    public SimpleParallelFor(int startIndex, int endIndex, ForTask<T> forTask,
                             ReduceOperation reop) {
        this.startIndex = startIndex;
        this.endIndex   = endIndex;
        this.forTask    = forTask;
        this.reduceOperation = reop;
    }
    
    protected ReduceOperation reduceOperation;

    /**
     * Get the value of reduceOperation
     *
     * @return the value of reduceOperation
     */
    public ReduceOperation getReduceOperation() {
        return reduceOperation;
    }

    /**
     * Set the value of reduceOperation
     *
     * @param reduceOperation new value of reduceOperation
     */
    public void setReduceOperation(ReduceOperation reduceOperation) {
        this.reduceOperation = reduceOperation;
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
    
    protected ForTask<T> forTask;

    /**
     * Get the value of forTask
     *
     * @return the value of forTask
     */
    public ForTask<T> getForTask() {
        return forTask;
    }

    /**
     * Set the value of forTask
     *
     * @param forTask new value of forTask
     */
    public void setForTask(ForTask<T> forTask) {
        this.forTask = forTask;
    }

    /**
     * Start the parallel loops and return the result of execution, if any
     * 
     * @return the result of parallel loop execution
     */
    public T start() {
        // first execute
        SimpleParallelTaskExecuter pTaskExecuter
                                   = new SimpleParallelTaskExecuter();                
        pTaskExecuter.execute(new ParallelForTask<T>(startIndex, endIndex));

        // then reduce the results (TODO: Note this is sequential, and a 
        // potential bottleneck, if large number of cores are used)
        Iterator<SimpleParallelTask> tasks = pTaskExecuter.getTaskList();

        ArrayList<T> results = new ArrayList<T>();

        while(tasks.hasNext()) {
            results.add(((ParallelForTask<T>) tasks.next()).getTask().getResult());
        } // end while
 
        return forTask.reduceResult(results, reduceOperation);
    } 

    /** Private class for managing the parallel task */
    private class ParallelForTask<T> extends AbstractSimpleParallelTask {

        private ForTask<T> _forTask;
        
        public ParallelForTask(int startItem, int endItem) {
            super.startItem  = startItem;
            super.endItem    = endItem;
            super.totalItems = Math.abs(startItem-endItem);

            _forTask = (ForTask<T>) SimpleParallelFor.this.forTask;
        }

        private ParallelForTask(int startItem, int endItem, ForTask<T> forTask) {
            super.startItem  = startItem;
            super.endItem    = endItem;
            super.totalItems = Math.abs(startItem-endItem);

            this._forTask = forTask;
        }

        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new ParallelForTask<T>(startItem, endItem,
                                 _forTask.getNewInstance(startItem, endItem));
        }

        @Override
        public void run() {
            _forTask.run();
        }

        public ForTask<T> getTask() {
            return _forTask;
        }
    }
}
