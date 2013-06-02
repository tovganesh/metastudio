/*
 * FederationDiscoveryEvent.java
 *
 * Created on July 11, 2006, 5:02 PM
 */

package org.meta.net.event;

import org.meta.net.FederationNode;

/**
 * Defines federation service discovery event.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationDiscoveryEvent extends java.util.EventObject {
    
    /** Creates a new instance of FederationDiscoveryEvent */
    public FederationDiscoveryEvent(Object source) {
        super(source);
        
        message = "Discovery Event";
        discoveryType = DiscoveryType.YAYEEN;
        relatedNode = null;
    }
    
    /**
     * Holds value of property message.
     */
    private String message;

    /**
     * Getter for property message.
     * @return Value of property message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Setter for property message.
     * @param message New value of property message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public enum DiscoveryType {
        SUCCESS, FAILURE, YAYEEN
    }

    /**
     * Holds value of property discoveryType.
     */
    private DiscoveryType discoveryType;

    /**
     * Getter for property discoveryType.
     * @return Value of property discoveryType.
     */
    public DiscoveryType getDiscoveryType() {
        return this.discoveryType;
    }

    /**
     * Setter for property discoveryType.
     * @param discoveryType New value of property discoveryType.
     */
    public void setDiscoveryType(DiscoveryType discoveryType) {
        this.discoveryType = discoveryType;
    }


    protected FederationNode relatedNode;

    /**
     * Get the value of relatedNode
     *
     * @return the value of relatedNode
     */
    public FederationNode getRelatedNode() {
        return relatedNode;
    }

    /**
     * Set the value of relatedNode
     *
     * @param relatedNode new value of relatedNode
     */
    public void setRelatedNode(FederationNode relatedNode) {
        this.relatedNode = relatedNode;
    }

} // end of class FederationDiscoveryEvent
