/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.parallel;

/**
 * A simple framework for execution of tasks in parallel on a shared
 * memory system.
 *
 * Interface declaring how a simple parallel task should be defined.
 * This parallel task as of now represents a thread (s) running in a
 * true shared meory environmnet, it future it may be extened to distributed
 * memory systems.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface SimpleParallelTask extends Runnable {

    /**
     * Initialize the task and return a new instance of this task.
     * It is required that a new instance is returned at every call as each of
     * these tasks will be run on a saparate thread.
     *
     * @param startItem index of starting item to be processed
     * @param endItem index of last item to be processed
     * @return a new instance of SimpleParallelTask representing the parallel
     *         task to be executed on a CPU
     */
    public SimpleParallelTask init(int startItem, int endItem);

    /**
     * Getter property for totalItems
     *
     * @return current value of totalItems
     */
    public int getTotalItems();

    /**
     * Setter property of totalItems
     * 
     * @param totalItems
     */
    public void setTotalItems(int totalItems);

    /**
     * Getter property for startItem
     *
     * @return current value of startItem
     */
    public int getStartItem();

    /**
     * Getter property for endItem
     *
     * @return current value of endItem
     */
    public int getEndItem();

    /**
     * Set a name for this task
     *
     * @param taskName the task name
     */
    public void setTaskName(String taskName);

    /**
     * Get the current task name
     *
     * @return the task name
     */
    public String getTaskName();
}
