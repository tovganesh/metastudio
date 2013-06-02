/**
 * XMPPConfig.java
 *
 * Created on 27/07/2009
 */

package org.meta.shell.idebeans.talk;

/**
 * Class representing XMPP configuration data
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class XMPPConfig {

    /** Creates a new instance of XMPPConfig */
    public XMPPConfig() {
    }

    /** Creates a new instance of XMPPConfig */
    public XMPPConfig(String name, String server, String domain, int port) {
        this.name   = name;
        this.server = server;
        this.domain = domain;
        this.port   = port;
    }

    protected String server;

    /**
     * Get the value of server
     *
     * @return the value of server
     */
    public String getServer() {
        return server;
    }

    /**
     * Set the value of server
     *
     * @param server new value of server
     */
    public void setServer(String server) {
        this.server = server;
    }

    protected String domain;

    /**
     * Get the value of domain
     *
     * @return the value of domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set the value of domain
     *
     * @param domain new value of domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    protected int port;

    /**
     * Get the value of port
     *
     * @return the value of port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the value of port
     *
     * @param port new value of port
     */
    public void setPort(int port) {
        this.port = port;
    }

    protected String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }
}
