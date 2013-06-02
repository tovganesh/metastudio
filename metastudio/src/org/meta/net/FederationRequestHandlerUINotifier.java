/**
 * FederationRequestHandlerUINotifier.java
 *
 * Created on 27/08/2009
 */

package org.meta.net;

/**
 * A simple notification mechanism for the handlers to notify UI of incomming
 * or processed federation requests. Note that it is not required that
 * these events be fired at all by the handlers. It is entirely upon the
 * handlers if they wish to actually notify of such events to external source.
 * Further, handlers may choose to send only some notification, say only
 * <code>requestProcessed();</code> to indicate that some data is available
 * with the handler and can be obtained by the UI. <br>
 * 
 * UI here actually referes to an external entity who would need to be notified
 * of these events, this need not necessarly be interacting directly with a
 * user.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FederationRequestHandlerUINotifier {

    /**
     * Called when request is received by the handler.
     * 
     * @param sourceHandler the handler for this notification
     */
    public void requestReceived(FederationRequestHandler sourceHandler);

    /**
     * Called when a request is processed by the handler.
     *
     * @param sourceHandler the handler for this notification
     */
    public void requestProcessed(FederationRequestHandler sourceHandler);
}
