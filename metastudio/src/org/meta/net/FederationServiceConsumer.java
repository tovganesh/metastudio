/*
 * FederationServiceConsumer.java
 *
 * Created on February 26, 2006, 9:59 PM
 *
 */

package org.meta.net;

import java.net.*;
import org.meta.net.exception.FederationServiceConsumptionFailed;
import org.meta.net.exception.FederationServiceDiscoveryFailed;

/**
 * An abstract class describing the functionality of a federation 
 * service consumer.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class FederationServiceConsumer {
    
    /** Creates a new instance of FederationServiceConsumer */
    public FederationServiceConsumer() {
    }

    /**
     * Discover if the desired service is available at a specified host.
     *
     * @param serviceProvider address of the service provider.
     * @throws FederationServiceDiscoveryFailed if the required service is 
     *         unavailable on the specified host
     * @return an valid instance of FederationRequest that can be consumed by
     *         a valid instance of this calss
     */
    public abstract FederationRequest discover(InetAddress serviceProvider) 
                                        throws FederationServiceDiscoveryFailed;

    /**
     * Consume the serive provided by the service provider.
     *
     * @param service the service that is to be consumed
     * @throws FederationServiceConsumptionFailed if an error occurs
     */
    public abstract void consume(FederationRequest service)
                                      throws FederationServiceConsumptionFailed;
    
} // end of class FederationServiceConsumer
