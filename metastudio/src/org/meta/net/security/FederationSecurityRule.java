/*
 * FederationSecurityRule.java
 *
 * Created on February 6, 2006, 9:49 PM
 *
 */

package org.meta.net.security;

import java.net.*;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.IPExpression;

/**
 * Defines a prototype of a configurable security rule for locally 
 * controlling the FederationSerivice/ FederationRequest.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationSecurityRule {        
    
    /** Creates a new instance of FederationSecurityRule */
    public FederationSecurityRule() {
    }

    /** Creates a new instance of FederationSecurityRule */
    public FederationSecurityRule(String host, FederationRequestType type, 
                                  FederationSecurityAction action) {
        this.host   = host;
        this.type   = type;
        this.action = action;
    }
    
    /**
     * Holds value of property host.
     */
    private String host;

    /**
     * Getter for property host.
     * @return Value of property host, an IP or IP expression.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Setter for property host.
     * @param host New value of property host, an IP or IP expression.
     */
    public void setHost(String host) {
        this.host = host;                
    }

    /**
     * Holds value of property type.
     */
    private FederationRequestType type;

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public FederationRequestType getType() {
        return this.type;
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(FederationRequestType type) {
        this.type = type;
    }

    /**
     * Holds value of property action.
     */
    private FederationSecurityAction action;

    /**
     * Getter for property action.
     * @return Value of property action.
     */
    public FederationSecurityAction getAction() {
        return this.action;
    }

    /**
     * Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(FederationSecurityAction action) {
        this.action = action;
    }

    /**
     * Check for violation of this rule.
     *
     * @param req the FederationRequest object that needs to be scrutenized
     * @return true / false indicating violation or non-violation of this
     *         rule by the specified request.
     *
     * Note: After the host criteria is matched, if a action is anything other
     *       than ACCEPT, a true is returned.
     */
    public FederationSecurityAction getAction(FederationRequest req) {
        InetAddress requestIP = req.getFederationConnection().getInetAddress();
        boolean hostsMatch = false;

        // first check if "host" is equal to '*'
        if (host.equals("*")) hostsMatch = true;
        else {
            // then first check if we can make simple string comparisons
            if (requestIP.getHostAddress().equals(host)) hostsMatch = true;

            if (!hostsMatch) {
                try {
                    IPExpression ipe = new IPExpression(host);

                    hostsMatch = ipe.matches(requestIP);
                } catch (UnsupportedOperationException ignored) { }
            } // end if

            if (!hostsMatch) return FederationSecurityAction.UNDEFINED;
        } // end if

        // if we reach here, that means the host criteria is satisfied
        // and now we need to check the type and action to be taken
        if (req.getType() != type) {
            if (type == FederationRequestType._ANY)
                return action;
            else
                return FederationSecurityAction.UNDEFINED;
        } // end if

        // if it reaches here, then we just pass on the correct action
        return action;
    }

    /**
     * Check for violation of this rule.
     *
     * @param req the FederationRequest object that needs to be scrutenized
     * @return true / false indicating violation or non-violation of this 
     *         rule by the specified request.
     *
     * Note: After the host criteria is matched, if a action is anything other 
     *       than ACCEPT, a true is returned.
     */
    public boolean checkViolation(FederationRequest req) {
        InetAddress requestIP = req.getFederationConnection().getInetAddress();
        boolean hostsMatch = false;
        
        // first check if "host" is equal to '*'
        if (host.equals("*")) hostsMatch = true;
        else {
            // then first check if we can make simple string comparisons
            if (requestIP.getHostAddress().equals(host)) hostsMatch = true;
        
            if (!hostsMatch) {
                try {
                    IPExpression ipe = new IPExpression(host);
                
                    hostsMatch = ipe.matches(requestIP);
                } catch (UnsupportedOperationException ignored) { }
            } // end if
                
            if (!hostsMatch) return false;
        } // end if
        
        // if we reach here, that means the host criteria is satisfied
        // and now we need to check the type and action to be taken
        if (req.getType() != type) {
            if (type == FederationRequestType._ANY) 
                return (action != FederationSecurityAction.ACCEPT); 
            else
                return false;
        } // end if
        
        // if action is != ACCEPT then return true, else false
        return (action != FederationSecurityAction.ACCEPT);        
    }
    
    /**
     * overridden toString() method
     *
     * @return the string representation!
     */
    @Override
    public String toString() {
       return host + ":" + type + " action:> " + action;
    }
} // end of class FederationSecurityRule
