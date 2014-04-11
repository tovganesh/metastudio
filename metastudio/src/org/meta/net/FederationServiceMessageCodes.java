/*
 * FederationServiceMessageCodes.java
 *
 * Created on March 26, 2006, 12:20 PM
 */

package org.meta.net;

/**
 * Federation service message codes that may be used during the establishment
 * of a connection or during the service activity.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum FederationServiceMessageCodes {
    UNSUPPORTED_SERVICE, /* requested service is unavaible */
    ACCESS_DENIED, /* request to any service is denied */
    SERVICE_AVAILABLE; /* requested service available */
    
    /**
     * Convert the string to FederationServiceMessageCodes.
     *
     * @param codeString the string representing a message code
     * @return valid FederationServiceMessageCodes if available, 
     *         else returns null
     */
    public static FederationServiceMessageCodes getType(String codeString) {
        FederationServiceMessageCodes [] vals 
                = FederationServiceMessageCodes.values();
        
        for(FederationServiceMessageCodes v : vals) {
            if (v.toString().equals(codeString)) return v;
        } // end for
        
        return null;
    }
} // end of FederationServiceMessageCodes
