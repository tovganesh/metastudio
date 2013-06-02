/**
 * IDETalkUser.java
 *
 * Created on Jun 26, 2009
 */
package org.meta.shell.idebeans.talk;

/**
 * Holder class for a talk user info.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDETalkUser {

    /** Constructor for IDETalkUser */
    public IDETalkUser(String userName, String hostID,
                       String clientInfo, TalkUserDomain domain) {
        this.userName   = userName;
        this.hostID     = hostID;
        this.clientInfo = clientInfo;
        this.userDomain = domain;
    }

    protected String userName;

    /**
     * Get the value of userName
     *
     * @return the value of userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the value of userName
     *
     * @param userName new value of userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    protected String clientInfo;

    /**
     * Get the value of clientInfo
     *
     * @return the value of clientInfo
     */
    public String getClientInfo() {
        return clientInfo;
    }

    /**
     * Set the value of clientInfo
     *
     * @param clientInfo new value of clientInfo
     */
    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    protected TalkUserDomain userDomain;

    /**
     * Get the value of userDomain
     *
     * @return the value of userDomain
     */
    public TalkUserDomain getUserDomain() {
        return userDomain;
    }

    /**
     * Set the value of userDomain
     *
     * @param userDomain new value of userDomain
     */
    public void setUserDomain(TalkUserDomain userDomain) {
        this.userDomain = userDomain;

        if (userDomain == TalkUserDomain.GTalk)
            hostID = "gmail.com";
    }

    protected String hostID;

    /**
     * Get the value of hostID
     *
     * @return the value of hostID
     */
    public String getHostID() {
        return hostID;
    }

    /**
     * Set the value of hostID
     *
     * @param hostID new value of hostID
     */
    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    /**
     * Overridden toString() method
     *
     * @return a String representation of this object
     */
    @Override
    public String toString() {
        return userName + "@" + hostID + " on " + userDomain;
    }

    /**
     * Overriden equals()
     *
     * @param obj the IDETalkUser object to compare
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IDETalkUser)) return false;

        IDETalkUser usr = (IDETalkUser) obj;
        
        if (!userName.equals(usr.userName)) return false;
        if (!hostID.equals(usr.hostID)) return false;
        if (!userDomain.equals(usr.userDomain)) return false;

        return true;
    }

    /**
     * Overriden hashCode()
     * 
     * @return the hash code!
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.userName != null ? this.userName.hashCode() : 0);
        hash = 37 * hash + (this.clientInfo != null ? this.clientInfo.hashCode() : 0);
        hash = 37 * hash + this.userDomain.hashCode();
        hash = 37 * hash + (this.hostID != null ? this.hostID.hashCode() : 0);
        hash = 37 * hash + (this.additionalInfo != null ? this.additionalInfo.hashCode() : 0);
        return hash;
    }
    /**
     * HTML representation of above string
     *
     * @return a String representation of this object
     */
    public String toHTMLString() {
        return "<html><body><b>" + userName + "</b>@<i>" + hostID
                + "</i> on <b>" + userDomain
                + "</b></body></html>";
    }

    protected Object additionalInfo;

    /**
     * Get the value of additionalInfo
     *
     * @return the value of additionalInfo
     */
    public Object getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Set the value of additionalInfo
     *
     * @param additionalInfo new value of additionalInfo
     */
    public void setAdditionalInfo(Object additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /** Constants depecting the domain of this Talk user: peer
        MeTA Studio user or using thrid party (currently only GTalk) */
    public enum TalkUserDomain {
        MeTA, GTalk
    }
}
