/*
 * FederationRequest.java
 *
 * Created on February 6, 2006, 9:28 PM
 */

package org.meta.net;

import java.io.*;

import javax.net.ssl.*;

/**
 * Encapsulates a FederationRequest from a client / service provider.
 * 
 * <br> Note on performance: The sendXXX() and receiveXXX() functions are not 
 * optimized and they may be very expensive in certain situations. In case 
 * performance is a great issue, you may directly use the reader/ writer
 * streams to do customized IO.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequest {
    
    /** Creates empty instance of FederationRequest */
    protected FederationRequest() {
    }
    
    /** Creates a new instance of FederationRequest */
    public FederationRequest(SSLSocket federationConnection, 
                             BufferedReader reader, BufferedWriter writer, 
                             FederationRequestType type) {
        this.federationConnection = federationConnection;
        this.reader = reader;
        this.writer = writer;
        this.type = type;
    }

    /**
     * Holds value of property federationConnection.
     */
    private SSLSocket federationConnection;

    /**
     * Getter for property federationConnection.
     * @return Value of property federationConnection.
     */
    public SSLSocket getFederationConnection() {
        return this.federationConnection;
    }

    /**
     * Setter for property federationConnection.
     * @param federationConnection New value of property federationConnection.
     */
    public void setFederationConnection(SSLSocket federationConnection) {
        this.federationConnection = federationConnection;
    }

    /**
     * Holds value of property type.
     */
    private FederationRequestType type;

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public FederationRequestType getType() {
        return this.type;
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(FederationRequestType type) {
        this.type = type;
    }

    /**
     * The overridden finalize method
     * @throws java.lang.Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        closeIt();
    }

    /**
     * Close this request object
     * 
     * @throws java.io.IOException
     */
    public void closeIt() throws IOException {
        reader.close();
        writer.close();
        federationConnection.close();
    }
    
    /**
     * Holds value of property reader.
     */
    private BufferedReader reader;

    /**
     * Getter for property reader.
     * @return Value of property reader.
     */
    public BufferedReader getReader() {
        return this.reader;
    }

    /**
     * Holds value of property writer.
     */
    private BufferedWriter writer;

    /**
     * Getter for property writer.
     * @return Value of property writer.
     */
    public BufferedWriter getWriter() {
        return this.writer;
    }
    
    // send and reveive functions 
    // TODO: improve performance of these methods
    
    /**
     * Send an integer across
     * 
     * @param value the integer value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendInt(Integer value) throws IOException {
        writer.write(value.toString() + "\n");
        writer.flush();
    }
    
    /**
     * Receive an integer data
     * 
     * @return the integer value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Integer receiveInt() throws IOException {
        return Integer.parseInt(reader.readLine().trim());
    }
    
    /**
     * Send a Integer array across
     * 
     * @param values the Integer values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendIntArray(Integer [] values, int offset, int len) 
                            throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendInt(values[i]);
        } // end for
    }
    
    /**
     * Send a int array across
     * 
     * @param values the int values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendIntArray(int [] values, int offset, int len) 
                            throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendInt(values[i]);
        } // end for
    }
    
    /**
     * Receive a integer data array
     * 
     * @param len the number items to be received
     * @return the integer array that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Integer [] receiveIntArray(int len) throws IOException {
        Integer [] values = new Integer[len];
        
        for(int i=0; i<len; i++) {
            values[i] = receiveInt();
        } // end for
        
        return values;
    }
    
    /**
     * Send a String across
     * 
     * @param value the String value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendString(String value) throws IOException {
        writer.write(value + "\n");
        writer.flush();
    }
    
    /**
     * Receive a String data
     * 
     * @return the String value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public String receiveString() throws IOException {
        return reader.readLine().trim();
    }
    
    /**
     * Send a float across
     * 
     * @param value the float value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendFloat(Float value) throws IOException {
        writer.write(value.toString() + "\n");
        writer.flush();
    }
    
    /**
     * Receive a float data
     * 
     * @return the float value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Float receiveFloat() throws IOException {
        return Float.parseFloat(reader.readLine().trim());
    }
    
    /**
     * Send a Float array across
     * 
     * @param values the Float values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendFloatArray(Float [] values, int offset, int len) 
                               throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendFloat(values[i]);
        } // end for
    }
    
    /**
     * Send a float array across
     * 
     * @param values the float values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendFloatArray(float [] values, int offset, int len) 
                               throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendFloat(values[i]);
        } // end for
    }
    
    /**
     * Receive a Float data array
     * 
     * @param len the number items to be received
     * @return the Float array that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Float [] receiveFloatArray(int len) throws IOException {
        Float [] values = new Float[len];
        
        for(int i=0; i<len; i++) {
            values[i] = receiveFloat();
        } // end for
        
        return values;
    }
    
    /**
     * Send a double across
     * 
     * @param value the double value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendDouble(Double value) throws IOException {
        writer.write(value.toString() + "\n");
        writer.flush();
    }
    
    /**
     * Receive a double data
     * 
     * @return the double value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Double receiveDouble() throws IOException {
        return Double.parseDouble(reader.readLine().trim());
    }
    
    /**
     * Send a Double array across
     * 
     * @param values the Double values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendDoubleArray(Double [] values, int offset, int len) 
                               throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendDouble(values[i]);
        } // end for
    }
    
    /**
     * Send a double array across
     * 
     * @param values the double values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendDoubleArray(double [] values, int offset, int len) 
                               throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendDouble(values[i]);
        } // end for
    }
    
    /**
     * Receive a Double data array
     * 
     * @param len the number items to be received
     * @return the Double array that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Double [] receiveDoubleArray(int len) throws IOException {
        Double [] values = new Double[len];
        
        for(int i=0; i<len; i++) {
            values[i] = receiveDouble();
        } // end for
        
        return values;
    }
    
    /**
     * Send a boolean across
     * 
     * @param value the boolean value to be sent
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendBoolean(Boolean value) throws IOException {
        writer.write(value.toString() + "\n");
        writer.flush();
    }
    
    /**
     * Receive a boolean data
     * 
     * @return the boolean value that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Boolean receiveBoolean() throws IOException {
        return Boolean.parseBoolean(reader.readLine().toString());
    }
    
    /**
     * Send a Boolean array across
     * 
     * @param values the Boolean values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendBooleanArray(Boolean [] values, int offset, int len) 
                                 throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendBoolean(values[i]);
        } // end for
    }
    
    /**
     * Send a boolean array across
     * 
     * @param values the boolean values to be sent
     * @param offset position from where the data is to be sent
     * @param len the number items to be sent from position
     * @throws java.io.IOException thrown if unable to send data
     */
    public void sendBooleanArray(boolean [] values, int offset, int len) 
                                 throws IOException {
        for(int i=offset; i<offset+len; i++) {
            sendBoolean(values[i]);
        } // end for
    }
    
    /**
     * Receive a Double data array
     * 
     * @param len the number items to be received
     * @return the Double array that is read
     * @throws java.io.IOException thrown if unable to receive data
     */
    public Boolean [] receiveBooleanArray(int len) throws IOException {
        Boolean [] values = new Boolean[len];
        
        for(int i=0; i<len; i++) {
            values[i] = receiveBoolean();
        } // end for
        
        return values;
    }        
    
    /**
     * overridden toString() method
     *
     * @return the string representation!
     */
    @Override
    public String toString() {
        return "Request " + type 
                + " from: " + federationConnection.getInetAddress();
    }
} // end of class FederationRequest
