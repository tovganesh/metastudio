package org.meta.config.event;

import java.util.EventListener;

/**
 * The lisner interface, defining a mechanism for the observer to "listen"
 * to changes in LennardJonesParameters object.
 *
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface UffParametersChangeListener extends EventListener {
    
    /**
     * The call back method for indicating the listeners of the said change.
     * 
     * @param event - the event containing the required information.
     */
    public void uffParametersChanged(UffParametersChangeEvent event);
    
}
