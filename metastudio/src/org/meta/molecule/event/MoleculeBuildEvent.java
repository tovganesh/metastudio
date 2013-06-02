/*
 * MoleculeBuildEvent.java
 *
 * Created on May 11, 2003, 9:19 AM
 */

package org.meta.molecule.event;

/**
 * Defines the event object for MoleculeBuildEvent to be used by 
 * implementations of MoleculeBuilder class for notifying various events 
 * such as various stages in building the molecule cinnectivity.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeBuildEvent extends java.util.EventObject {
    
    /** event types */
    public static final int DESCRIPTION_EVENT        = 0;
    public static final int PERCENT_COMPLETION_EVENT = 1;
    
    /** Holds value of property eventDesctiption. */
    private String eventDescription;            
    
    /** Holds value of property percentCompletion. */
    private double percentCompletion;
            
    /** Holds value of property eventType. */
    private int eventType;
    
    /** Creates a new instance of MoleculeBuildEvent */    
    public MoleculeBuildEvent(Object source, String eventDescription) {
        super(source);
        this.eventType        = MoleculeBuildEvent.DESCRIPTION_EVENT;
        this.eventDescription = eventDescription;
    }
    
    /** Creates a new instance of MoleculeBuildEvent */    
    public MoleculeBuildEvent(Object source, double percentCompletion) {
        super(source);
        this.eventType         = MoleculeBuildEvent.PERCENT_COMPLETION_EVENT;
        this.percentCompletion = percentCompletion;
    }
    
    /**
     * overridden toString() method.
     * 
     * @return String object describing the event
     */
    public String toString() {        
        return eventDescription;
    }
    
    /** Getter for property eventDesctiption.
     * @return Value of property eventDesctiption.
     */
    public String getEventDescription() {
        return this.eventDescription;
    }
    
    /** Setter for property eventDesctiption.
     * @param eventDescription New value of property eventDesctiption.
     */
    public void setEventDescription(String eventDescription) {
        this.eventType        = MoleculeBuildEvent.DESCRIPTION_EVENT;
        this.eventDescription = eventDescription;
    }
    
    /** Getter for property percentCompletion.
     * @return Value of property percentCompletion.
     *
     */
    public double getPercentCompletion() {
        return this.percentCompletion;
    }
    
    /** Setter for property percentCompletion.
     * @param percentCompletion New value of property percentCompletion.
     *
     */
    public void setPercentCompletion(double percentCompletion) {
        this.eventType         = MoleculeBuildEvent.PERCENT_COMPLETION_EVENT;
        this.percentCompletion = percentCompletion;
    }
    
    /** Getter for property eventType.
     * @return Value of property eventType.
     *
     */
    public int getEventType() {
        return this.eventType;
    }
    
    /** Setter for property eventType.
     * @param eventType New value of property eventType.
     *
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
    
} // end of class MoleculeBuildEvent
