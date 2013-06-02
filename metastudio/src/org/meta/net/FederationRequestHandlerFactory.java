/*
 * FederationRequestHandlerFactory.java
 *
 * Created on February 9, 2006, 10:02 AM
 *
 */

package org.meta.net;

import java.util.*;

import java.beans.PropertyVetoException;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.w3c.dom.*;

/**
 * Singleton class to route appropriate, varified to requests available, 
 * reqestered request handlers.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationRequestHandlerFactory {
    
    private static 
            FederationRequestHandlerFactory _federationRequestHandlerFactory;
    
    private HashMap<FederationRequestType, Class> requestMap;
    
    /** Creates a new instance of FederationRequestHandlerFactory */
    private FederationRequestHandlerFactory() {
        requestMap = new HashMap<FederationRequestType, Class>();
        
        try {
            setDefaultParams();
        } catch (Exception ignored) {
            System.err.println("Warning! FederationRequestHandlerFactory " +
                               "could not start normally: " + ignored);
            System.err.println("Service discovery may not work properly!");
            ignored.printStackTrace();
        } // end try .. catch
    }
    
    /**
     * Get instance of FederationRequestHandlerFactory
     *
     * @return instance of FederationRequestHandlerFactory
     */
    public static FederationRequestHandlerFactory getInstance() {
        if (_federationRequestHandlerFactory == null) {
            _federationRequestHandlerFactory 
                                     = new FederationRequestHandlerFactory();            
        } // end if
        
        return _federationRequestHandlerFactory;
    }
        
    /**
     * Get a request handler for a given type.
     *
     * @return Appropriate FederationRequestHandler or null, if none found
     */
    public FederationRequestHandler getFederationRequestHandler(
                                       FederationRequestType requestType) {
       Class handlerClz = requestMap.get(requestType);
        
       // if we do not have any handlers, return null
       if (handlerClz == null) return null;
       
       // else try to return an appropriate handler instance
       try {
           return (FederationRequestHandler) handlerClz.newInstance();
       } catch (Exception e) {
           System.err.println("Unable to instantiate handler: " + e.toString());
           e.printStackTrace();
           
           return null;
       } // end of try .. catch block              
    }
    
    /**
     * Add a request handler.
     * 
     * @param type instance of FederationRequestType
     * @param handler request handler of type FederationRequestHandler
     */
    public void addRequestHandler(FederationRequestType type, 
                                  Class<FederationRequestHandler> handler) {
        requestMap.put(type, handler);
    }
    
    /**
     * Remove a request handler.
     * 
     * @param type instance of FederationRequestType     
     */
    public void removeRequestHandler(FederationRequestType type) {
        requestMap.remove(type);
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() throws PropertyVetoException {
        StringResource strings   = StringResource.getInstance();
        try {                        
            // read the internal XML config file
            Document configDoc = Utility.parseXML(
                                  getClass().getResourceAsStream(
                                     strings.getFederationRequestHandlers()));
            
            // and save the info. properly
            saveHandlerMaps(configDoc);
                        
        } catch (Exception e) {
            throw new PropertyVetoException("Exception in "
                              + "FederationRequestHandlerFactory.setParameter()" 
                              + e.toString(), null);
        } // end of try .. catch block
    }

    /**
     * Recursively save the DOM tree
     */
    private void saveHandlerMaps(Node n) throws ClassNotFoundException {        
        int type = n.getNodeType();   // get node type
        
        switch (type) {                    
        case Node.ELEMENT_NODE:            
            NamedNodeMap atts = n.getAttributes();
            String element = n.getNodeName();
            
            if (element.equals("handle")) {
                if (atts == null) return;
                
                FederationRequestType reqType = FederationRequestType.getType(
                                      atts.getNamedItem("type").getNodeValue());
                
                Class handler = 
                       FederationRequestType.class.forName(
                                   atts.getNamedItem("handler").getNodeValue());
                
                requestMap.put(reqType, handler);
            } else {
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveHandlerMaps(att);
                } // end for
            } // end if
            
            break;
        default:
            break;
        } // end of switch .. case block
        
        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveHandlerMaps(child);
        } // end for
    }
} // end of class FederationRequestHandlerFactory
