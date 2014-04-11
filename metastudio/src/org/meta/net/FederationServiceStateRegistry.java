/*
 * FederationServiceStateRegistry.java 
 *
 * Created on 12 Oct, 2008 
 */

package org.meta.net;

import java.util.Date;
import java.util.HashMap;

/**
 * This class helps mainitains a list of currently active "service-consumer"
 * connections. Follows a singleton pattern.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceStateRegistry {

    private static FederationServiceStateRegistry _theRegistry;
    
    /** Creates an instance of FederationServiceStateRegistry */
    private FederationServiceStateRegistry() {
        
    }
        
    /**
     * Get instance of FederationServiceStateRegistry
     * 
     * @return instance of FederationServiceStateRegistry
     */
    public synchronized static FederationServiceStateRegistry getInstance() {
        if (_theRegistry == null) 
            _theRegistry = new FederationServiceStateRegistry();
        
        return _theRegistry;
    }
    
    private HashMap<FederationRequest, Date> serviceList;
    
    /**
     * Add a request state to registry.
     * 
     * @param request the FederationRequest to be added
     */
    public synchronized void addToRegistry(FederationRequest request) {
        if (serviceList == null) 
            serviceList = new HashMap<FederationRequest, Date>();
        
        serviceList.put(request, new Date());
    }
    
    /**
     * Remove request state from registery.
     * 
     * @param request
     */
    public synchronized void removeFromRegistry(FederationRequest request) {
        if (serviceList == null) return;
        
        serviceList.remove(request);
    }
    
    /**
     * Get the registry list
     * 
     * @return registry list as a mutable HashMap
     */
    public HashMap<FederationRequest, Date> getRegistryList() {
        return serviceList;
    }
}
