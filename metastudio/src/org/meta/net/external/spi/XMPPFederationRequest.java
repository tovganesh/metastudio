/**
 * XMPPFederationRequest.java
 *
 * Created on 08/10/2009
 */

package org.meta.net.external.spi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import javax.net.ssl.SSLSocket;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.meta.common.resource.StringResource;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;

/**
 * Simple extenstion for FederationService in an XMPP setup
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class XMPPFederationRequest extends FederationRequest {

    private ArrayDeque<Message> msgQueue;
    
    /** Creates a new instance of XMPPFederationRequest */
    public XMPPFederationRequest(XMPPConnection conn, Chat chat) {
        this.xmppConnection = conn;
        this.xmppChat = chat;

        MessageListener ml = new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if (message.getBody().trim().equals("")) return;

                msgQueue.add(message);
            }
        };

        xmppChat.addMessageListener(ml);
    }

    protected Chat xmppChat;

    /**
     * Get the value of xmppChat
     *
     * @return the value of xmppChat
     */
    public Chat getXmppChat() {
        return xmppChat;
    }

    /**
     * Set the value of xmppChat
     *
     * @param xmppChat new value of xmppChat
     */
    public void setXmppChat(Chat xmppChat) {
        this.xmppChat = xmppChat;
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
     * Getter for property federationConnection.
     * @return Value of property federationConnection.
     */
    @Override
    public SSLSocket getFederationConnection() {
        return new XMPPSSLSocket(this.xmppConnection);
    }

    /**
     * Getter for property reader.
     * @return Value of property reader.
     */
    @Override
    public BufferedReader getReader() {
        return super.getReader();
    }

    /**
     * Getter for property writer.
     * @return Value of property writer.
     */
    @Override
    public BufferedWriter getWriter() {
        return super.getWriter();
    }

    /**
     * Receive a boolean data
     *
     * @return the boolean value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    @Override
    public Boolean receiveBoolean() throws IOException {
        return (Boolean) receiveData();
    }

    /**
     * Receive a double data
     *
     * @return the double value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    @Override
    public Double receiveDouble() throws IOException {
        return (Double) receiveData();
    }

    /**
     * Receive a float data
     *
     * @return the float value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    @Override
    public Float receiveFloat() throws IOException {
        return (Float) receiveData();
    }

    /**
     * Receive an integer data
     *
     * @return the integer value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    @Override
    public Integer receiveInt() throws IOException {
        return (Integer) receiveData();
    }

    /**
     * Receive a String data
     *
     * @return the String value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    @Override
    public String receiveString() throws IOException {
        return (String) receiveData();
    }

    /**
     * Send a boolean across
     *
     * @param value the boolean value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    @Override
    public void sendBoolean(Boolean value) throws IOException {
        sendData(value);
    }

    /**
     * Send a double across
     *
     * @param value the double value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    @Override
    public void sendDouble(Double value) throws IOException {
        sendData(value);
    }

    /**
     * Send a float across
     *
     * @param value the float value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    @Override
    public void sendFloat(Float value) throws IOException {
        sendData(value);
    }

    /**
     * Send an integer across
     *
     * @param value the integer value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    @Override
    public void sendInt(Integer value) throws IOException {
        sendData(value);
    }

    /**
     * Send a String across
     *
     * @param value the String value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    @Override
    public void sendString(String value) throws IOException {
        sendData(value);
    }

    /**
     * Setter for property federationConnection.
     * @param federationConnection New value of property federationConnection.
     */
    @Override
    public void setFederationConnection(SSLSocket federationConnection) {
        throw new UnsupportedOperationException("Unsupported operation " +
                                                "for XMPPFederationRequest");
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    @Override
    public void setType(FederationRequestType type) {
        super.setType(type);
    }

    private static final String XMPP_DATA = "__data";
    private static final String XMPP_DATA_BODY = "Data packet from "
                                  + StringResource.getInstance().getVersion();

    /** send a data packet */
    private void sendData(Object value) throws IOException {
        Message msg = new Message();
        msg.setType(Message.Type.chat);
        msg.setProperty(XMPP_DATA, value);
        msg.setBody(XMPP_DATA_BODY);

        try {
            xmppChat.sendMessage(msg);
        } catch(Exception e) {
            throw new IOException(e.toString());
        } // end of try .. catch block
    }

    /** receive a data packet */
    private Object receiveData() throws IOException {
        Object msg = msgQueue.removeFirst().getProperty(XMPP_DATA);

        if (msg == null) throw new IOException("Null message!");

        return msg;
    }
}
