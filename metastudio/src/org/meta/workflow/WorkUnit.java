/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.workflow;

import org.meta.workflow.exception.IncompatibleWorkUnitConnection;

/**
 * Represents the basic work unit in a workflow
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface WorkUnit<TyIn, TyOut> {    
    /**
     * Set input for WorkUnit
     * 
     * @param workInput input for this workunit
     */
    void setInput(TyIn workInput);
    
    /**
     * Get current input for WorkUnit
     * 
     * @return the workunit input
     */
    TyIn getInput();
    
    /**
     * Get the current output for WorkUnit
     * 
     * @return the workunit output
     */
    TyOut getOutput();
    
    /**
     * Get the next workunit in this chain
     * 
     * @return next workunit, null if none
     */
    WorkUnit<TyOut, ?> getNextWorkUnit();
    
    /**
     * Set the next workunit in this chain
     * 
     * @param workUnit a compatible next workunit
     * @throws IncompatibleWorkUnitConnection if the connection is not compatible
     */
    void setNextWorkUnit(WorkFlow<TyOut, ?> workUnit) throws IncompatibleWorkUnitConnection;
    
    /**
     * Get the previous workunit in this chain
     * 
     * @return previous workunit, null if none
     */
    WorkFlow<?, TyIn> getPreviousWorkUnit(); 
    
    /**
     * Set the previous workunit in this chain
     * 
     * @param workUnit a compatible previous workunit
     * @throws IncompatibleWorkUnitConnection if the connection is not compatible
     */
    void setPreviousWorkUnit(WorkFlow<?, TyIn> workUnit) throws IncompatibleWorkUnitConnection;
}
