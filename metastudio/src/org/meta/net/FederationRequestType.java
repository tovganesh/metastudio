/*
 * FederationRequestType.java
 *
 * Created on February 6, 2006, 9:20 PM
 *
 */

package org.meta.net;

/**
 * Enumerates the set of FederationRequests available with MeTA Studio.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum FederationRequestType {
    SHUTDOWN,      /* Priviledged internal request to shutdown the 
                      federation service */
    ECHO,          /* An "echo" service */
    BROADCAST,     /* A "broadcast" service */
    OBJECT_PUSH,   /* An object push service */
    TALK,          /* A basic talk service */ 
    USER_SCRIPT,   /* A user written script service */
    FILE_TRANSFER, /* A file transfer service */
    VOICE_TALK,    /* A voice talk service */
    MAP_REDUCE,    /* A map-reduce service helper */
    OBJECT_SYNC,   /* A way to synhronize objects, in a way providing
                      functionality akin to RMI */
    _ANY;          /* Special internal word representing *any* service */
    
    /**
     * Convert the string to FederationRequestType.
     *
     * @param typeString the string representing a request type
     * @return valid FederationRequestType if available, else returns null
     */
    public static FederationRequestType getType(String typeString) {
        FederationRequestType [] vals = FederationRequestType.values();
        
        typeString = typeString.toUpperCase();
        for(FederationRequestType v : vals) {
            if (v.toString().equals(typeString)) return v;
        } // end for
        
        return null;
    }
} // end of enum FederationRequestType
