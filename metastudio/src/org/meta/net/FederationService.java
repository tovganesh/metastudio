/*
 * FederationService.java
 *
 * Created on January 15, 2006, 4:10 PM
 *
 */

package org.meta.net;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.net.ssl.*;
        
import org.meta.common.resource.StringResource;
import org.meta.net.exception.FederationRequestAccessDeniedException;
import org.meta.net.exception.NoPromptHandlerDefinedException;
import org.meta.net.security.FederationSecurityShield;

/**
 * The main class defining Federation and Networking services allowing
 * various distributed versions of MeTA Studio instances to coordinate
 * and collabrate in accomplishing various tasks. <br>
 * All the Federation and Networking in MeTA Studio will be handled using
 * SSL defined in <code>javax.net.ssl</code> package. <br>
 * 
 * This class follows a Singleton pattern.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationService {

    /** the one and only one instance of federation service */
    private static FederationService _federationService;
    
    /** the server socket connection of the federation service */
    private SSLServerSocket federationServerSocket;
    
    /** flag to identify whether the server socket thread is running */
    private boolean serverSocketThreadRunning = false;
    
    /** the federation server listner (socket server) thread */
    private Thread federationServerSocketThread;
    
    /**
     * The default MeTA Studio federating port has an interesting story.
     * I wanted to give a uniquie number to this port which somehow also
     * reflected the 'MeTA' name. After lots of combinations and trials I
     * ended up with the following set of bits: <br>
     * <pre> (('M' &lt&lt 8) | 'T') </pre>  <br>
     * The above code translates into port number 19796, which seems to
     * have some intersting properties.
     */
    public static final int DEFAULT_STUDIO_FEDERATING_PORT = (('M' << 8) | 'T');
    
    /** Creates a new instance of FederationService */
    private FederationService() throws UnknownHostException, IOException {
        // initial settings
        federatingPort = DEFAULT_STUDIO_FEDERATING_PORT;
        name = StringResource.getInstance().getVersion() + " @ " 
                + InetAddress.getLocalHost().getHostName()
                + " on port " + federatingPort
                + " with time stamp " + System.currentTimeMillis(); 
        
        initFederationServiceThread();
    }

    /**
     * the method initilizes the federation service listener thread 
     * if it is not already inited.
     */
    private void initFederationServiceThread() throws IOException {
        // if already running do nothing
        if (serverSocketThreadRunning) return;
        
        // init the server socket and start listining
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) 
                                         SSLServerSocketFactory.getDefault();
        federationServerSocket = (SSLServerSocket) 
                                         ssf.createServerSocket(federatingPort);
        
        // setup encryption suites 
        federationServerSocket.setEnabledCipherSuites(
                                         ssf.getSupportedCipherSuites());
        
        federationServerSocketThread = new Thread() {
          @Override
          public void run() {
              while(serverSocketThreadRunning) {
                  try {
                    // 1. listen
                    SSLSocket federationClient = 
                                    (SSLSocket) federationServerSocket.accept();
                    
                    // 2. accept a federation client request 
                    BufferedReader reader = new BufferedReader(
                      new InputStreamReader(federationClient.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(federationClient.getOutputStream()));
                    
                    FederationRequestType requestType = 
                            FederationRequestType.getType(reader.readLine());
                    
                    FederationRequest federationRequest = 
                          new FederationRequest(federationClient, 
                                                reader, writer, 
                                                requestType);
                    
                    try {
                        // 3. check security rules                    
                        FederationSecurityShield.getInstance()
                                    .verifyRequestPermission(federationRequest);
                    } catch (NoPromptHandlerDefinedException ex) {
                        writer.write(FederationServiceMessageCodes
                                        .ACCESS_DENIED.toString() + '\n');
                        writer.flush();
                        reader.close(); writer.close();
                        federationClient.close();
                        federationRequest = null;
                        
                        throw ex;
                    } catch (FederationRequestAccessDeniedException ex) {
                        writer.write(FederationServiceMessageCodes
                                        .ACCESS_DENIED.toString() + '\n');
                        writer.flush();
                        reader.close(); writer.close();
                        federationClient.close();
                        federationRequest = null;
                        
                        throw ex;
                    } // end of try .. catch
                    
                    // 4. appropriately route it to a proper federation 
                    //    client handler
                    FederationRequestHandlerFactory handlerFactory =
                            FederationRequestHandlerFactory.getInstance();
                    FederationRequestHandler requestHandler = 
                        handlerFactory.getFederationRequestHandler(requestType);
                    
                    if (requestHandler == null) {
                        writer.write(FederationServiceMessageCodes
                                        .UNSUPPORTED_SERVICE.toString() + '\n');
                        writer.flush();
                        reader.close(); writer.close();
                        federationClient.close();
                        federationRequest = null;
                        
                        throw new IOException("No handler defined for request: "
                                              + requestType);
                    } // end if
                    
                    writer.write(FederationServiceMessageCodes
                                        .SERVICE_AVAILABLE.toString() + '\n');
                    writer.flush();
                    
                    // add this to registery
                    FederationServiceStateRegistry.getInstance()
                                          .addToRegistry(federationRequest);

                    // see if we have any UI notifier, if so add them
                    // to the request handler before processing the request
                    if (uiNotifiers != null) {
                       for(FederationRequestHandlerUINotifier uin : uiNotifiers)
                           requestHandler.addUINotifier(uin);
                    } // end if
                    
                    // and then delegate the request
                    requestHandler.handleRequest(federationRequest);
                  } catch (IOException ioe) {
                      System.err.println("IOException in " +
                            "federationServerSocketThread: " + ioe);  
                      ioe.printStackTrace();
                  } catch (FederationRequestAccessDeniedException fex) {
                      System.err.println(
                            "FederationRequestAccessDeniedException in " +
                            "federationServerSocketThread: " + fex);  
                      fex.printStackTrace();
                  } catch (NoPromptHandlerDefinedException nex) {
                      System.err.println(
                            "NoPromptHandlerDefinedException in " +
                            "federationServerSocketThread: " + nex);  
                      nex.printStackTrace();
                  } // end of try .. catch block
              } // end while                            
          }
        };
        
        // start the listening service
        federationServerSocketThread.setName("Federation server Thread");
        federationServerSocketThread.start();        
        serverSocketThreadRunning = true;
    }
    
    /**
     * Get an instance of this Federation service.
     *
     * @return an instance of FederationService
     * @throws UnknownHostException if unable execute any IP related taks that
     *         are encountered during creation of a federation service.
     */
    public static FederationService getInstance() throws UnknownHostException,
                                                         IOException {
        if (_federationService == null) {
            _federationService = new FederationService();
        } // end if
        
        return _federationService;
    }
    
    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Holds value of property federatingPort.
     */
    private int federatingPort;

    /**
     * Getter for property federatingPort.
     * @return Value of property federatingPort.
     */
    public int getFederatingPort() {
        return this.federatingPort;
    }

    /**
     * Setter for property federatingPort.
     * @param federatingPort New value of property federatingPort.
     */
    public void setFederatingPort(int federatingPort) 
                                   throws UnknownHostException {
        this.federatingPort = federatingPort;
        
        // the name also change!
        name = StringResource.getInstance().getVersion() + " @ " 
                + InetAddress.getLocalHost().getHostName()
                + " on port " + federatingPort
                + " with time stamp " + System.currentTimeMillis();
    }

    protected ArrayList<FederationRequestHandlerUINotifier> uiNotifiers;

    /**
     * Add an UI notifier, one who wants to be notified of events being
     * processed by the handler. Note that registering for notification
     * does not always mean that a notification will be sent. It is
     * entirely dependent on a handler implementation if it needs to
     * really send any notifications at all.
     *
     * @param uin the notifier instance
     */
    public void addUINotifier(FederationRequestHandlerUINotifier uin) {
        if (uiNotifiers == null)
            uiNotifiers = new ArrayList<FederationRequestHandlerUINotifier>();

        uiNotifiers.add(uin);
    }

    /**
     * Remove an UI notifier to stop receiving any notifications from this
     * handler.
     *
     * @param uin the notifier instance
     */
    public void removeUINotifier(FederationRequestHandlerUINotifier uin) {
        if (uiNotifiers == null) return;

        uiNotifiers.add(uin);
    }
    
    /**
     * the overridden toString method
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * The finalize method
     */
    @Override
    protected void finalize() throws java.lang.Throwable {
        // attempt to stop the federation service thread
        serverSocketThreadRunning = false;
        
        // TODO: for proper closing a self initiated shutdown of accept()
        // call is necessary!
    }
} // end of class FederationService
