/*
 * FederationNode.java
 *
 * Created on July 3, 2006, 12:29 PM
 */

package org.meta.net;

import java.net.*;

import org.meta.common.resource.StringResource;

/**
 * A "wrapper" for storing IP addresses of the Federating service providers.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationNode {
    
    /** Creates a new instance of FederationNode */
    public FederationNode() {
        metaVersion = StringResource.getInstance().getVersion();

        numberOfProcessors = 1;
    }

    /** Creates a new instance of FederationNode */
    public FederationNode(InetAddress ipAddress) {
        metaVersion = StringResource.getInstance().getVersion();

        this.ipAddress = ipAddress;
        numberOfProcessors = 1;
    }

    /**
     * Holds value of property ipAddress.
     */
    private InetAddress ipAddress;

    /**
     * Getter for property ipAddress.
     * @return Value of property ipAddress.
     */
    public InetAddress getIpAddress() {
        return this.ipAddress;
    }

    /**
     * Setter for property ipAddress.
     * @param ipAddress New value of property ipAddress.
     */
    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Holds value of property metaVersion.
     */
    private String metaVersion;

    /**
     * Getter for property metaVersion.
     * @return Value of property metaVersion.
     */
    public String getMetaVersion() {
        return this.metaVersion;
    }

    /**
     * Setter for property metaVersion.
     * @param metaVersion New value of property metaVersion.
     */
    public void setMetaVersion(String metaVersion) {
        this.metaVersion = metaVersion;
    }
    
    /**
     * Holds value of property userName.
     */
    private String userName;

    /**
     * Getter for property userName.
     * @return Value of property userName.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Setter for property userName.
     * @param userName New value of property userName.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    private int numberOfProcessors;

    /**
     * Get the value of numberOfProcessors
     *
     * @return the value of numberOfProcessors
     */
    public int getNumberOfProcessors() {
        return numberOfProcessors;
    }

    /**
     * Set the value of numberOfProcessors
     *
     * @param noOfProcessors new value of numberOfProcessors
     */
    public void setNumberOfProcessors(int noOfProcessors) {
        this.numberOfProcessors = noOfProcessors;
    }

    /**
     * The overridden equals() method
     *
     * @param o the object to compare
     * @return true/ false 
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof FederationNode)) return false;
        
        FederationNode co = (FederationNode) o;

        if (co.getIpAddress() == null)   return false;
        if (co.getMetaVersion() == null) return false;
        if (co.getUserName() == null)    return false;

        return ((co.getIpAddress().equals(ipAddress)) 
                && (co.getMetaVersion().equals(metaVersion))
                && (co.getUserName().equals(userName))
                && (co.getNumberOfProcessors() == numberOfProcessors));
    }

    /**
     * Overridden hashCode()
     *
     * @return the hash code!
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash
                + (this.ipAddress != null ? this.ipAddress.hashCode() : 0);
        hash = 47 * hash
                + (this.metaVersion != null ? this.metaVersion.hashCode() : 0);
        hash = 47 * hash
                + (this.userName != null ? this.userName.hashCode() : 0);
        hash = 47 * hash + numberOfProcessors;
        
        return hash;
    }
    
    /**
     * overridden toString() method
     *
     * @return the string representation of this object
     */
    @Override
    public String toString() {
        return (ipAddress.getHostAddress() 
                + ";" + metaVersion + ";" + userName
                + ";" + numberOfProcessors);
    }
} // end of class FederationNode
