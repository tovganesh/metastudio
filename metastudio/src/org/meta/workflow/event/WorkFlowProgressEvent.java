/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.workflow.event;

/**
 * Event fired during the course of a WorkFlow execution
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WorkFlowProgressEvent extends java.util.EventObject {
    
    /** Creates a new instance of WorkFlowProgressEvent */
    public WorkFlowProgressEvent(Object source) {
        super(source);
    }
}
