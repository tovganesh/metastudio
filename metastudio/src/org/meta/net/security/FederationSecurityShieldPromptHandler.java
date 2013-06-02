/*
 * FederationSecurityShieldPromptHandler.java
 *
 * Created on February 7, 2006, 9:32 PM
 */

package org.meta.net.security;


import javax.net.ssl.*;
import org.meta.net.FederationRequestType;

/**
 * Interface defining how a prompt action should be handled by an implimenting
 * federation client.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FederationSecurityShieldPromptHandler {
    
    /**
     * Method acting as an interface to the external manual user to
     * ask and obtain and acceptance/ denial action for the request from
     * a federating remote client.
     *
     * @param requestType the enumerated federation request
     * @param federatingClient the ssl socket connected the requesting client
     * @return true/false indicative of whether the request was 
     *         accepted or not by the manual user
     */
    public boolean promptRequest(FederationRequestType requestType, 
                                 SSLSocket federatingClient);    
} // end of interface FederationSecurityShieldPromptHandler
