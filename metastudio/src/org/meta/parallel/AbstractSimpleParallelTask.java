/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.parallel;

/**
 * An abstract implementation of SimpleParallelTask interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractSimpleParallelTask implements SimpleParallelTask {

    protected int totalItems, startItem, endItem;

    protected String taskName;

    /** Creates instance of AbstractSimpleParallelTask */
    public AbstractSimpleParallelTask() {
        this.totalItems = 0;
        this.startItem  = 0;
        this.endItem    = 0;

        this.taskName   = "GenericParallelTask";
    }

    /**
     * Getter property for totalItems
     *
     * @return current value of totalItems
     */
    @Override
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * Setter property of totalItems
     *
     * @param totalItems
     */
    @Override
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Getter property for startItem
     *
     * @return current value of startItem
     */
    @Override
    public int getStartItem() {
        return startItem;
    }

    /**
     * Getter property for endItem
     *
     * @return current value of endItem
     */
    @Override
    public int getEndItem() {
        return endItem;
    }

    /**
     * Set a name for this task
     *
     * @param taskName the task name
     */
    @Override
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Get the current task name
     *
     * @return the task name
     */
    @Override
    public String getTaskName() {
        return this.taskName;
    }
}
