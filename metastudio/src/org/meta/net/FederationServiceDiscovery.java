/*
 * FederationServiceDiscovery.java
 *
 * Created on July 3, 2006, 12:19 PM 
 */

package org.meta.net;

import java.io.*;
import java.net.*;
import java.util.*;
import org.meta.common.EventListenerList;
import org.meta.common.resource.StringResource;
import org.meta.net.event.FederationDiscoveryEvent;
import org.meta.net.event.FederationDiscoveryListener;

/**
 * The discovery service for automatically discovering "peer" MeTA Studio 
 * services in your local network. Only one instance exists in a single
 * MeTA Studio instance.
 *
 * Note that the discovery mechanism is *not* carried over a secure medium,
 * as opposed to the actual "federating service provider" communication, which
 * is always carried over a secure channel.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationServiceDiscovery {
    
    private static FederationServiceDiscovery _federationServiceDiscovery;

    private static final int DEFAULT_TIMEOUT = 5000;
    
    private boolean stopDiscoveryThread = false;
    
    /**
     * The default MeTA Studio discover port is generated the reverse way in 
     * which the federating port number is generated. The port number is 
     * generated using bit operation as follows: <br>
     * <pre> (('T' << 8) | 'M') </pre>  <br>
     * The above code translates into port number 21581, which also seems to
     * have some intersting properties.
     */
    public static final int DEFAULT_STUDIO_DISCOVERY_PORT = (('T' << 8) | 'M');
    
    /** Creates a new instance of FederationServiceDiscovery */
    private FederationServiceDiscovery() {
        showLogMessages = true;
        recordSelf = false; // don't discover onself!
        
        discoveryPort = DEFAULT_STUDIO_DISCOVERY_PORT;        
        timeout = DEFAULT_TIMEOUT;        
        
        register();
    }
    
    /**
     * Get instance, singleton interface.
     * 
     * @return instance of FederationServiceDiscovery
     */
    public static FederationServiceDiscovery getInstance() {
        if (_federationServiceDiscovery == null) {
            _federationServiceDiscovery = new FederationServiceDiscovery();
        }
        
        return _federationServiceDiscovery;
    }
    
    /**
     * The discovery algorithm works like this:
     * <ol>
     *    <li>Obtain self IP address, A:B:C:X </li>
     *    <li> <code> Iterate over A:B:C:1 to A:B:C:254 as ip
     *                if (currentIP != ip):
     *                   <ul>
     *                      <li> Check access (ping) </li>
     *                      <li> Check MeTA (send discovery packet) </li>
     *                      <li> Obtain discovery list </li>
     *                      <li> Update local list </li>
     *                      <li> Send back difference list </li>
     *                      <li> Add the new node to the local list </li>
     *                   </ul>
     *         </code>
     *    </li>
     * </ol>
     * Limitations: In its current form, the algorithm only works for a local 
     * IPV4 based network. Externals address need to be mapped manually (or
     * programatically) via. MeTA Studio interface (when and where available).
     *
     * @return a list of nodes offering MeTA Studio federation service.
     */
    public ArrayList<FederationNode> discoverMeTA() {
        try {
            // Obtain the local address:
            Enumeration<NetworkInterface> nics 
                                     = NetworkInterface.getNetworkInterfaces();
            ArrayList<InetAddress> metaSearchAddress 
                                     = new ArrayList<InetAddress>();                        
            
            // Iterate through each interface
            // check its reachability, and use the device address
            // to perform an discovery of MeTA services            
            while(nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                
                // Obtain all address bound to this interface
                Enumeration<InetAddress> addrs = nic.getInetAddresses();
                                
                // check to see the availability and whether it is a loop
                // back address, if it not then we start the discovery 
                // on the network associated with this address
                while(addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                
                    if (showLogMessages)
                        System.out.println("Probing NIC address: " + addr);
                    
                    if ((!addr.isLoopbackAddress()) 
                        && (!(addr instanceof Inet6Address))) {
                        metaSearchAddress.add(addr);                        
                    } else if (addr.isLoopbackAddress() 
                               && (!(addr instanceof Inet6Address))
                               && recordSelf) { 
                        // honor any user requests to record self
                        pingAndRecord(addr);
                    } // end if
                } // end while
            } // end while
                                    
            // next iterate through each search address
            for(InetAddress addr : metaSearchAddress) {
                byte [] bAddr = addr.getAddress();
                
                for(int i=1; i<=Byte.MAX_VALUE*2; i++) {
                    bAddr[bAddr.length-1] = (byte) i;
                    
                    InetAddress newAddress = InetAddress.getByAddress(bAddr);
                    
                    // Don't add self: get to know about yourself
                    // from others!! so the discovery service 
                    // will require atleast two nodes to function 
                    // properly. Obviously! as if we have only one
                    // node there is no need for discovery and
                    // federation services can be directly queried 
                    // and used. 
                    if (newAddress.equals(addr)) continue;
                    
                    pingAndRecord(newAddress);
                } // end for
            } // end for         
            
            // return the discovered nodes
            return discoveredNodes;                        
        } catch (Exception e) {
            return null;
        } // end of try .. catch block                
    }
    
    /**
     * The discovery algorithm works like this:
     * <ol>
     *    <li>Obtain IP regular expression, say A:B:C:* </li>
     *    <li> <code> Iterate over A:B:C:1 to A:B:C:254 as ip
     *                if (currentIP != ip):
     *                   <ul>
     *                      <li> Check access (ping) </li>
     *                      <li> Check MeTA (send discovery packet) </li>
     *                      <li> Obtain discovery list </li>
     *                      <li> Update local list </li>
     *                      <li> Send back difference list </li>
     *                      <li> Add the new node to the local list </li>
     *                   </ul>
     *         </code>
     *    </li>
     * </ol>
     * Limitations: a) In its current form, the algorithm only works for a local 
     * IPV4 based network. Externals address need to be mapped manually (or
     * programatically) via. MeTA Studio interface (when and where available).
     * b) You can only provide one wild-card search. Two stars are not allowed.
     *    eg. A:B:*:* is not allowed.
     * c) However, you may provide a part of the IP with two '?'s as in 
     *    A:B:C:1??, but A:2?:C:1? is illegal.
     * d) You can not include both '?' and '*' at the same time in the ip 
     *    expression
     *
     * Note: a) You need to provide IP addresses with ':' instead of '.' (IPV4). 
     * IPV6 is not currently accepted.
     * b) The discovery list is not re-initialized in this routine, but is done
     * in the default <code>discoverMeTA()</code> method.
     *
     * @param ipExpression the IP regular expression 
     * @return a list of nodes offering MeTA Studio federation service.
     * @throws UnsupportedOperationException in case an illegal IP expression
     *         is passed to this method.
     */
    public ArrayList<FederationNode> discoverMeTA(String ipExpression) {        
        IPExpression ipe = new IPExpression(ipExpression);
        
        while(true) {
            InetAddress ip = ipe.getNextAddress();
            
            if (ip == null) break;
                    
            pingAndRecord(ip);
        } // end while
        
        // GCed !
        ipe = null;
        
        // return the discovered nodes
        return discoveredNodes; 
    }
    
    /**
     * Ping an IP for MeTA service and record it if active.
     *
     * @param newAddress the address to ping
     */
    public void pingAndRecord(InetAddress newAddress) {
        String msg = "Probing address " + newAddress.toString() + "...";
        
        FederationDiscoveryEvent fde = new FederationDiscoveryEvent(this);
        
        fde.setMessage(msg);
        fde.setDiscoveryType(FederationDiscoveryEvent.DiscoveryType.YAYEEN);
        fireFederationDiscoveryListenerFederated(fde);
            
        try {
            if (!newAddress.isReachable(timeout))
                throw new Exception("Not Reachable");           
            
            // check if MeTA service is running?                        
            Socket discoveredSock = new Socket(newAddress, discoveryPort);            
            
            // open up reader streams
            BufferedReader br
              = new BufferedReader(new InputStreamReader(
                            discoveredSock.getInputStream()));
            BufferedWriter bw 
              = new BufferedWriter(new OutputStreamWriter(
                            discoveredSock.getOutputStream()));

            // set up discovered node information
            FederationNode discoveredNode 
                                  = new FederationNode();
            discoveredNode.setIpAddress(newAddress);            
            discoveredNode.setMetaVersion(br.readLine().trim());
            discoveredNode.setUserName(br.readLine().trim());
            discoveredNode.setNumberOfProcessors(Integer.parseInt(
                                                      br.readLine().trim()));
            
            // send in self version
            String ver 
                    = StringResource.getInstance().getVersion();
            bw.write(ver + '\n');
            bw.flush();        
                        
            // and user name of the current node
            String usr = "Annonymous Coward!"; // slashdoted! ;)
            
            try {
                usr = System.getProperty("user.name");
            } catch (Exception ignored) {}
            
            bw.write(usr + '\n');
            bw.flush();
            
            String procs = Runtime.getRuntime().availableProcessors() + "\n";
            bw.write(procs);
            bw.flush();

            // read in the discovered nodes list from the
            // currently discovered node
            while(true) {
                String line = br.readLine().trim();
                
                // break if we receive the version information
                // back
                if (
                   line.equals(discoveredNode.getMetaVersion())
                   ) break;

                // else record the discovered node
                String [] words = line.split(";");

                FederationNode newNode = new FederationNode();
                newNode.setIpAddress(
                           InetAddress.getByName(words[0]));
                newNode.setMetaVersion(words[1]);
                newNode.setUserName(words[2]);
                newNode.setNumberOfProcessors(Integer.parseInt(words[3].trim()));

                synchronized (discoveredNode) {
                    if (!discoveredNodes.contains(newNode)) {
                        discoveredNodes.add(newNode);
                    }
                } // end : synchronized
            } // end while                                                        

            // then send in own list!
            // next send the discovered nodes on this
            // this node
            for(FederationNode fn : discoveredNodes) {
                bw.write(fn.getIpAddress().getHostAddress() 
                         + ";" +
                         fn.getMetaVersion() 
                         + ";" +
                         fn.getUserName() 
                         + ";" +
                         fn.getNumberOfProcessors() + "\n");
                bw.flush();
            } // end for
            // sending of version string again indicates
            // end of the list
            bw.write(StringResource.getInstance()
                                   .getVersion() + '\n');
            
            // add the discovered node information into
            // our local list if that is not already present
            synchronized (discoveredNode) {
                if (!discoveredNodes.contains(discoveredNode)) {
                    discoveredNodes.add(discoveredNode);
                }
            } // end : synchronized

            // close connections
            discoveredSock.close();

            msg += "Success";

            fde.setMessage(msg);
            fde.setDiscoveryType(
                   FederationDiscoveryEvent.DiscoveryType.SUCCESS);
            fde.setRelatedNode(discoveredNode);
            fireFederationDiscoveryListenerFederated(fde);            
        } catch (Exception ignored) {                         
            msg += "Failed: " + ignored.toString();
            if (showLogMessages) {
               System.err.println("Error traceback: ");
               ignored.printStackTrace();
            } // end if

            fde.setMessage(msg);
            fde.setDiscoveryType(
                   FederationDiscoveryEvent.DiscoveryType.FAILURE);
            fireFederationDiscoveryListenerFederated(fde);
        } // end of try catch .. block     
    }
    
    /**
     * Returns a list of already discovered MeTA nodes.
     *
     * @return a list of nodes offering MeTA Studio federation service.
     */
    public ArrayList<FederationNode> listMeTA() {
        if (discoveredNodes.size() == 0) {
            discoverMeTA();
        } // end if
        
        return discoveredNodes;
    }
    
    /**
     * Register the discovery service!
     */
    private void register() {
        Thread discoveryThread = new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket discoveryServer 
                                      = new ServerSocket(discoveryPort);
                    String msg = "";
                    
                    FederationDiscoveryEvent fde = new FederationDiscoveryEvent(
                                                FederationServiceDiscovery.this);
                    
                    while(!stopDiscoveryThread) {
                        try {                            
                            // accept a discovery query
                            Socket discoveryClient = discoveryServer.accept();
                            
                            FederationNode queryingNode = new FederationNode();
                            
                            queryingNode.setIpAddress(
                                    discoveryClient.getInetAddress());
                            
                            msg = "Probing address " + 
                            discoveryClient.getInetAddress().toString() + "...";
        
                            fde.setMessage(msg);
                            fde.setDiscoveryType(
                                 FederationDiscoveryEvent.DiscoveryType.YAYEEN);
                            fireFederationDiscoveryListenerFederated(fde);
                            
                            // open up reader streams
                            BufferedWriter bw 
                              = new BufferedWriter(new OutputStreamWriter(
                                            discoveryClient.getOutputStream()));
                            BufferedReader br
                              = new BufferedReader(new InputStreamReader(
                                            discoveryClient.getInputStream()));
                            
                            // send the version information
                            bw.write(StringResource.getInstance()
                                                   .getVersion() + '\n');
                            bw.flush();                                                        
                            
                            // and user name of the current node
                            String usr = "Annonymous Coward!"; // slashdoted! ;)

                            try {
                                usr = System.getProperty("user.name");
                            } catch (Exception ignored) {}

                            bw.write(usr + '\n');
                            bw.flush();

                            String procs =
                              Runtime.getRuntime().availableProcessors() + "\n";
                            bw.write(procs);
                            bw.flush();
                            
                            // and get back the client's version and user info.
                            queryingNode.setMetaVersion(br.readLine().trim());
                            queryingNode.setUserName(br.readLine().trim());
                            queryingNode.setNumberOfProcessors(
                                    Integer.parseInt(br.readLine().trim()));
                            
                            // next send the discovered nodes on this
                            // this node
                            for(FederationNode fn : discoveredNodes) {
                                bw.write(fn.getIpAddress().getHostAddress() 
                                         + ";" +
                                         fn.getMetaVersion() 
                                         + ";" +
                                         fn.getUserName() 
                                         + ";"+
                                         fn.getNumberOfProcessors() + "\n");
                                bw.flush();
                            } // end for
                            // sending of version string again indicates
                            // end of the list
                            bw.write(StringResource.getInstance()
                                                   .getVersion() + '\n');
                            bw.flush();
                            
                            // then get the difference list from the 
                            // connected node, and update the local list
                            while(true) {
                                String line = br.readLine();
                                
                                if (line == null) break;
                                
                                line = line.trim();
                                if (line.equals("")) break;

                                // break if we receive the version information
                                // back
                                if (line.equals(queryingNode.getMetaVersion()))
                                    break;
                                
                                // else record the discovered node
                                String [] words = line.split(";");
                                
                                FederationNode newNode = new FederationNode();
                                newNode.setIpAddress(
                                           InetAddress.getByName(words[0]));
                                newNode.setMetaVersion(words[1]);
                                newNode.setUserName(words[2]);
                                newNode.setNumberOfProcessors(
                                        Integer.parseInt(words[3].trim()));
                                
                                synchronized (discoveredNodes) {
                                    if (!discoveredNodes.contains(newNode)) {
                                        discoveredNodes.add(newNode);
                                    }
                                }
                            } // end while
                            
                            // add the querying node information into
                            // our local list if that is not already present
                            synchronized (discoveredNodes) {
                                if (!discoveredNodes.contains(queryingNode)) {
                                    discoveredNodes.add(queryingNode);
                                }
                            }
                            
                            // close connections
                            discoveryClient.close();
                            
                            msg += "Success";

                            fde.setMessage(msg);
                            fde.setDiscoveryType(
                                FederationDiscoveryEvent.DiscoveryType.SUCCESS);
                            fireFederationDiscoveryListenerFederated(fde);
                        } catch (Exception e) {
                            if (showLogMessages) {
                                System.err.println("Warning! exception in " +
                                 "FederationServiceDiscovery.register(): " + e);
                                e.printStackTrace();
                            } // end if
                            
                            msg += "Failed";

                            fde.setMessage(msg);
                            fde.setDiscoveryType(
                                FederationDiscoveryEvent.DiscoveryType.FAILURE);
                            fireFederationDiscoveryListenerFederated(fde);
                        } // end try .. catch block
                    } // end while
                    
                    // close connections!
                    discoveryServer.close();
                } catch (Exception e) {
                    // The following message is critical and must be put out
                    System.err.println("Unhandled exception occured in " +
                            "FederationServiceDiscovery.register(): " + e);
                    System.err.println("Discovery service may not " +
                            "run properly!");
                    e.printStackTrace();
                } // end of try .. catch block
            }
        };
        
        discoveryThread.setName("Federation Discovery Thread");
        discoveryThread.start();
    }
    
    /**
     * The finalizer()
     */
    @Override
    protected void finalize() throws Throwable {
        stopDiscoveryThread = true;        
    }

    /**
     * Holds value of property discoveryPort.
     */
    private int discoveryPort;

    /**
     * Getter for property discoveryPort.
     * @return Value of property discoveryPort.
     */
    public synchronized int getDiscoveryPort() {
        return this.discoveryPort;
    }

    /**
     * Setter for property discoveryPort.
     * @param discoveryPort New value of property discoveryPort.
     */
    public synchronized void setDiscoveryPort(int discoveryPort) {
        this.discoveryPort = discoveryPort;
    }

    /**
     * Holds value of property discoveredNodes.
     */
    private final ArrayList<FederationNode> discoveredNodes 
                                              = new ArrayList<FederationNode>();

    /**
     * Getter for property discoveredNodes.
     * @return Value of property discoveredNodes.
     */
    public synchronized ArrayList<FederationNode> getDiscoveredNodes() {
        return this.discoveredNodes;
    }

    /**
     * Holds value of property timeout.
     */
    private int timeout;

    /**
     * Getter for property timeout.
     * @return Value of property timeout.
     */
    public synchronized int getTimeout() {
        return this.timeout;
    }

    /**
     * Setter for property timeout.
     * @param timeout New value of property timeout.
     */
    public synchronized void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected boolean showLogMessages;

    /**
     * Get the value of showLogMessages
     *
     * @return the value of showLogMessages
     */
    public synchronized boolean isShowLogMessages() {
        return showLogMessages;
    }

    /**
     * Set the value of showLogMessages
     *
     * @param showLogMessages new value of showLogMessages
     */
    public synchronized void setShowLogMessages(boolean showLogMessages) {
        this.showLogMessages = showLogMessages;
    }

    /**
     * Utility field used by event firing mechanism.
     */
    private EventListenerList<FederationDiscoveryListener> listenerList =  null;

    /**
     * Registers FederationDiscoveryListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addFederationDiscoveryListener(
                                FederationDiscoveryListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<FederationDiscoveryListener>();
        }
        listenerList.add(FederationDiscoveryListener.class, listener);
    }

    /**
     * Removes FederationDiscoveryListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeFederationDiscoveryListener(
                                   FederationDiscoveryListener listener) {
        listenerList.remove(FederationDiscoveryListener.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event The event to be fired
     */
    private synchronized void fireFederationDiscoveryListenerFederated(
                                        FederationDiscoveryEvent event) {
        if (listenerList == null) return;        
        
        for(Object listener : listenerList.getListenerList()) {
            ((FederationDiscoveryListener)listener).federated (event);
        } // end for
    }
    
    protected boolean recordSelf;

    /**
     * Get the value of recordSelf
     *
     * @return the value of recordSelf
     */
    public boolean isRecordSelf() {
        return recordSelf;
    }

    /**
     * Set the value of recordSelf
     *
     * @param recordSelf new value of recordSelf
     */
    public void setRecordSelf(boolean recordSelf) {
        this.recordSelf = recordSelf;
    }

} // end of class FederationServiceDiscovery
