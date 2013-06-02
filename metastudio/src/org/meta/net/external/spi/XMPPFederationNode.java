/**
 * XMPPFederationNode.java
 *
 * Created on 08/10/2009
 */

package org.meta.net.external.spi;

import java.net.InetAddress;
import org.jivesoftware.smack.XMPPConnection;
import org.meta.net.FederationNode;

/**
 * Simple extenstion for FederationNode in an XMPP setup
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class XMPPFederationNode extends FederationNode {

    /** Creates a new instance of XMPPFederationNode */
    public XMPPFederationNode(XMPPConnection conn) {
        this.xmppConnection = conn;
    }

    protected XMPPConnection xmppConnection;

    /**
     * Get the value of xmppConnection
     *
     * @return the value of xmppConnection
     */
    public XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    /**
     * Set the value of xmppConnection
     *
     * @param xmppConnection new value of xmppConnection
     */
    public void setXmppConnection(XMPPConnection xmppConnection) {
        this.xmppConnection = xmppConnection;
    }

    /**
     * Getter for property ipAddress.
     * @return Value of property ipAddress.
     */
    @Override
    public InetAddress getIpAddress() {
        try {
            return InetAddress.getByName(xmppConnection.getHost());
        } catch(Exception e) {
            return super.getIpAddress();
        } // end of try .. catch block
    }

    /**
     * Setter for property ipAddress.
     * @param ipAddress New value of property ipAddress.
     */
    @Override
    public void setIpAddress(InetAddress ipAddress) {
        throw new UnsupportedOperationException("Setting IP address for a " +
                                     "XMPPFederationNode is not possible!");
    }
}
