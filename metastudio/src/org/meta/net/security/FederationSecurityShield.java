/*
 * FederationSecurityShield.java
 *
 * Created on February 6, 2006, 9:41 PM
 *
 */

package org.meta.net.security;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.net.FederationRequest;
import org.meta.net.FederationRequestType;
import org.meta.net.exception.FederationRequestAccessDeniedException;
import org.meta.net.exception.NoPromptHandlerDefinedException;

/**
 * A singleton interface to enforcing a security shield at various 
 * federation events.
 * <br>
 * Caution: This class provides methods to dynamically change the security rules
 * (only as far as the federation service is concerned) which may be potentially
 * dangerous if executed remotely. Though in most visualised situations this 
 * seems not possible.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationSecurityShield {
    
    private static FederationSecurityShield _federationSecurityShield;
    
    private ArrayList<FederationSecurityRule> ruleList;
    
    private String element = new String("element");
    
    /** Creates a new instance of FederationSecurityShield */
    private FederationSecurityShield() {
        ruleList = new ArrayList<FederationSecurityRule>();
        
        initRuleList();
    }
    
    /**
     * Initilize the rule list!
     */
    private void initRuleList() {
        StringResource strings = StringResource.getInstance();
        
        try {
            if ((new File(strings.getFederationSecurityResource())).exists()) {
               // if a user configuration file exists, then we copy 
               // its contents 
               Document configDoc = Utility.parseXML(
                                       strings.getFederationSecurityResource());
            
               saveUserNode(configDoc);
            } else {
               FederationSecurityRule rule = new FederationSecurityRule() ;
               rule.setHost("*");
               rule.setType(FederationRequestType.valueOf("_ANY"));
               rule.setAction(FederationSecurityAction.valueOf("PROMPT"));
               
               ArrayList<FederationSecurityRule> rl = getRuleList();
               rl.add(rule);
               setRuleList(rl);
            } // end if
        } catch (Exception e) {
            System.err.println("Unable to read security rules for federation" +
                    "service! : " + e);
            System.err.println("Security system may not work propery!");
            e.printStackTrace();
        } // end if
    }
    
    /**
     * Saves the rule list.
     */
    public void saveRuleList() {
        try {
            StringResource strings = StringResource.getInstance();
            
            File appDir = new File(strings.getAppDir());
        
            if (!appDir.exists()) {
                appDir.mkdir(); // make the directory if not already present
            } // end if
        
            FileOutputStream fos = new FileOutputStream(
                                       strings.getFederationSecurityResource());
            
            fos.write(strings.getXmlHeader().getBytes());
            
            fos.write("<securityRules>\n".getBytes());
            for(FederationSecurityRule rule : ruleList) {
                fos.write(("\t<rule host=\"" + rule.getHost() + "\" type=\""
                            + rule.getType().toString() + "\" action=\""
                         + rule.getAction().toString() + "\" />\n").getBytes());
            } // end for
            fos.write("</securityRules>\n".getBytes());
            
            fos.close();
        } catch (Exception e) {
            System.err.println("Error saving rule list! " + e.toString());
            e.printStackTrace();
        } // end if try .. catch block
    }
    
    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveUserNode(Node n) {
        int type = n.getNodeType();   // get node type
        
        switch (type) {        
        case Node.ELEMENT_NODE:
            element = n.getNodeName();
            
            NamedNodeMap atts = n.getAttributes();
            if (element.equals("rule")) {
                FederationSecurityRule rule = new FederationSecurityRule();
                
                rule.setHost(atts.getNamedItem("host").getNodeValue());
                rule.setType(FederationRequestType.valueOf(
                               atts.getNamedItem("type").getNodeValue()));
                rule.setAction(FederationSecurityAction.valueOf(
                                atts.getNamedItem("action").getNodeValue()));
                
                ruleList.add(rule);
                
                // save the others
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveUserNode(att);
                } // end for
            } else {
                if (atts == null) return;
                
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveUserNode(att);
                } // end for
            } // end if
            
            break;
        default:
            break;
        } // end switch..case

        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveUserNode(child);
        } // end for
    } // end of method saveUserNode()
    
    /**
     * Get the instance of FederationSecurityShield
     */
    public static FederationSecurityShield getInstance() {
        if (_federationSecurityShield == null) {
            _federationSecurityShield = new FederationSecurityShield();
        } // end if
        
        return _federationSecurityShield;
    }
    
    /**
     * Verify permission for a request.
     *
     * @param federationRequest the request to be validated for sufficient 
     *        permissions
     * @throws NoPromptHandlerDefinedException if no prompt handler is defined
     * @throws FederationRequestAccessDeniedException is this request is 
     *         disallowed by local security settings
     */
    public void verifyRequestPermission(FederationRequest federationRequest) 
                                 throws NoPromptHandlerDefinedException, 
                                        FederationRequestAccessDeniedException { 
        // all requests are regected by default if 
        // action in UNDEFINED
        boolean fallbackAcceptRule = false;

        for(FederationSecurityRule rule : ruleList) {
            if (rule.checkViolation(federationRequest)) {
                if (rule.getAction() == FederationSecurityAction.PROMPT) {
                    if (promptHandler == null) {
                        throw new NoPromptHandlerDefinedException(
                                "No prompt handler is defined!");
                    } else {
                        if (!promptHandler.promptRequest(
                                 federationRequest.getType(),
                                 federationRequest.getFederationConnection())) {
                            throw new FederationRequestAccessDeniedException(
                                      federationRequest + " access rejected!");
                        } else {
                            fallbackAcceptRule = true;
                        } // end if
                    } // end if
                } else {
                    throw new FederationRequestAccessDeniedException(
                                      federationRequest + " violates " + rule);
                } // end if
            } else {
                FederationSecurityAction action
                        = rule.getAction(federationRequest);

                if (action == FederationSecurityAction.ACCEPT)
                    fallbackAcceptRule = true;
            } // end if
        } // end for

        // If this request is not accepted any where
        // and yet there were no violations, we need to regect this
        if (!fallbackAcceptRule) {
            throw new FederationRequestAccessDeniedException(
                                      federationRequest + " access is denied." +
                                      " No rules defined for this " +
                                      "request.");
        } // end if
    }

    /**
     * Holds value of property promptHandler.
     */
    private FederationSecurityShieldPromptHandler promptHandler;

    /**
     * Getter for property promptHandler.
     * @return Value of property promptHandler.
     */
    public FederationSecurityShieldPromptHandler getPromptHandler() {
        return this.promptHandler;
    }

    /**
     * Setter for property promptHandler.
     * @param promptHandler New value of property promptHandler.
     */
    public void setPromptHandler(
                         FederationSecurityShieldPromptHandler promptHandler) {
        this.promptHandler = promptHandler;
    }

    /**
     * Getter for property ruleList.
     * @return Value of property ruleList.
     */
    public ArrayList<FederationSecurityRule> getRuleList() {
        return this.ruleList;
    }

    /**
     * Setter for property ruleList.
     * @param ruleList New value of property ruleList.
     */
    public void setRuleList(ArrayList<FederationSecurityRule> ruleList) {
        this.ruleList = ruleList;
        
        saveRuleList();
    }
} // end of class FederationSecurityShield
