/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.workflow.event;

/**
 * Listener mechanism to notify progress during WorkFlow execution
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface WorkFlowProgressListener extends java.util.EventListener {
    
    /**
     * Workflow progress even occurred
     * 
     * @param wfpe the WorkFlowProgressEven describing the progress
     */
    public void workFlowExecutionProgressed(WorkFlowProgressEvent wfpe);
}
