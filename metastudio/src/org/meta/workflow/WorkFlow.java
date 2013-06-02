/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.workflow;

import java.io.IOException;
import org.meta.workflow.exception.IncompatibleWorkUnitConnection;
import org.meta.workflow.exception.IncompleteWorkFlow;

/**
 * Represents the API for WorkFlow in MeTA Studio
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class WorkFlow<TyIn, TyOut> {
    
    protected TyIn workflowInput;
    protected TyOut workflowOutput; 
    
    /** Create a new instance of WorkFlow */
    public WorkFlow(TyIn workflowInput, TyOut workflowOutput) {        
        this.workflowInput  = workflowInput;
        this.workflowOutput = workflowOutput;
    }
    
    /** Load a saved workflow from a file */
    public WorkFlow(String workflowFile) throws IOException {
        // TODO: 
    }
    
    /**
     * Add a workunit to this workflow
     * 
     * Note: it is up to the implementation to appropriately reconnect based on
     * addition of this workunit
     * 
     * @param workUnit the new workunit
     */
    abstract void addWorkUnit(WorkUnit<?, ?> workUnit);
    
    /**
     * Set the starting workunit for this WorkFlow
     * 
     * @param workUnit the start workunit
     */
    abstract void setStartWorkUnit(WorkUnit<TyIn, ?> workUnit);
    
    /**
     * Get the current starting workunit for this WorkFlow
     * 
     * @return the current starting workunit
     */
    abstract WorkUnit<TyIn, ?> getStartWorkUnit();
    
    /**
     * Set the final workunit for this WorkFlow
     * 
     * @param workUnit the end workunit
     */
    abstract void setEndWorkUnit(WorkUnit<?, TyOut> workUnit);
    
    /**
     * Set the final workunit for this WorkFlow
     * 
     * @param workUnit the end workunit
     */
    abstract WorkUnit<?, TyOut> getEndWorkUnit();
    
    /**
     * Remove a workunit from this WorkFlow.
     * 
     * Note: it is up to the implementation to appropriately reconnect based on
     * removal of this workunit
     * 
     * @param workUnit 
     */
    abstract void removeWorkUnit(WorkUnit<?, ?> workUnit);
    
    /**
     * Connect two workunits. Note that both the workunits should have been 
     * added to this WorkFlow before a connection can be made.
     * 
     * @param workUnit1 the first workunit
     * @param workUnit2 the second workunit
     * @throws IncompatibleWorkUnitConnection if the input and output types of 
     *         the workunits do not match
     */
    abstract void connectWorkUnit(WorkUnit<?, ?> workUnit1, 
                                  WorkUnit<?, ?> workUnit2) 
                                  throws IncompatibleWorkUnitConnection;
    
    /**
     * Disconnect workunit connection. Note that both the workunits should have 
     * been added to this WorkFlow before.
     * 
     * @param workUnit1 the first WorkUnit
     * @param workUnit2 the second WorkUnit
     */
    abstract void diconnectWorkUnit(WorkUnit<?, ?> workUnit1, 
                                    WorkUnit<?, ?> workUnit2);
    
    /**
     * Execute this workflow
     * 
     * @throws IncompleteWorkFlow if the workflow is not properly connected or
     *         if the start and end workunits are not appropriately set
     */
    abstract void execute() throws IncompleteWorkFlow;
    
    /**
     * Get current workflow input
     * 
     * @return current workflow input
     */
    TyIn getWorkflowInput() {
        return workflowInput;
    }
    
    /**
     * Set current workflow input 
     * 
     * @param workflowInput the new workflow input
     */
    void setWorkflowInput(TyIn workflowInput) {
        this.workflowInput = workflowInput;
    }
    
    /**
     * Get workflow output
     * 
     * @return the workflow output
     */
    TyOut getWorkflowOutput() {
        return workflowOutput;
    }    
    
    /**
     * Save the current workflow in a file to be retrived later 
     * 
     * @param workflowFileName the name of workflow file
     * @throws IOException thrown in case there is a problem saving the file
     */
    void saveWorkflow(String workflowFileName) throws IOException {
        // TODO: 
    }
}
