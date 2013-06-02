/**
 * XMPPConfigReader.java
 *
 * Created on 27/07/2009
 */

package org.meta.shell.idebeans.talk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Reads XMPP config from XMPPConfig.xml file.
 * Follows a singleton pattern.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class XMPPConfigReader {
    
    /** Creates instance of XMPPConfigReader */
    private XMPPConfigReader() { 
        init();
    }

    private static XMPPConfigReader _theInstance;

    /**
     * Return the instance of this class
     *
     * @return instance of this class
     */
    public static XMPPConfigReader getInstance() {
        if (_theInstance == null)
            _theInstance = new XMPPConfigReader();

        return _theInstance;
    }

    /** Read in the configuration data */
    private void init() {
        xmppConfigList = new ArrayList<XMPPConfig>();

        try {
            setDefaultParameters();
        } catch(Exception e) {
            System.err.println("Unable to read in XMPP config parameters, " +
                    "defaulting to standard values.");
            initDefault();
        } // end of try .. catch block
    }

    /** init the default XMPP config */
    private void initDefault() {
        defaultXMPPConfig = new XMPPConfig("Gtalk", "talk.google.com",
                                           "google.com", 5222);
        xmppConfigList.add(defaultXMPPConfig);
    }

    /** read the default parameters or create them if run for the first time */
    private void setDefaultParameters() throws IOException {
        StringResource strings = StringResource.getInstance();

        if ((new File(strings.getXmppConfig())).exists()) {
            // this config file already exists, read in the default parameters
            // read the internal XML config file
            try {
                Document configDoc = Utility.parseXML(strings.getXmppConfig());

                saveConfig(configDoc);
            } catch (Exception e) {
                throw new IOException("Parsing error, can't start launcher.");
            } // end of try .. catch block
        } else {
            // no config file, probably running first time; create with default
            // set of parameters
            initDefault();
            saveDefaultXmppConfig();
        } // end if
    }

    private XMPPConfig currentConfig;
    
    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveConfig(Node n) {
        int type = n.getNodeType();   // get node type

        switch (type) {
        case Node.ATTRIBUTE_NODE:
            String nodeName = n.getNodeName();

            if (nodeName.equals("name")) {
                currentConfig.setName(n.getNodeValue());
            } else if (nodeName.equals("server")) {
                currentConfig.setServer(n.getNodeValue());
            } else if (nodeName.equals("domain")) {
                currentConfig.setDomain(n.getNodeValue());
            } else if (nodeName.equals("port")) {
                currentConfig.setPort(Integer.parseInt(n.getNodeValue()));
            } // end if

            break;
        case Node.ELEMENT_NODE:
            String element = n.getNodeName();

            if (element.equals("xmppserver")) {
                NamedNodeMap atts = n.getAttributes();

                currentConfig = new XMPPConfig();

                // save the others
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveConfig(att);
                } // end for

                xmppConfigList.add(currentConfig);
            } else if (element.equals("defaultserver")) {
                // set the default server
                NamedNodeMap atts = n.getAttributes();

                currentConfig = new XMPPConfig();

                // save the others
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveConfig(att);
                } // end for
                
                defaultXMPPConfig = currentConfig;
            } // end if
            
            break;
        default:
            break;
        } // end of switch .. case block

        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveConfig(child);
        } // end for
    }

    /**
     * Saves the launcher config file to a default MeTA Studio config folder.
     */
    public void saveDefaultXmppConfig() throws IOException {
        StringResource strings = StringResource.getInstance();
        FileWriter fw = new FileWriter(strings.getXmppConfig());

        fw.write(strings.getXmlHeader());
        fw.write("<xmppconfig> \n");
        fw.write("  <xmppserver name=\"Gtalk\" server=\"talk.google.com\" " +
                 "domain=\"gmail.com\" port=\"5222\" />");
        fw.write("  <defaultserver name=\"GTalk\" server=\"talk.google.com\" " +
                 "domain=\"gmail.com\" port=\"5222\" />");
        fw.write("</xmppconfig> \n");

        fw.close();
    }
    
    private ArrayList<XMPPConfig> xmppConfigList;

    /**
     * Return a list of all the XMPPConfig items
     *
     * @return all XMPPConfig items
     */
    public ArrayList<XMPPConfig> getXMPPConfigList() {
        return xmppConfigList;
    }

    private XMPPConfig defaultXMPPConfig;

    /**
     * Return instance of default XMPP config item
     *
     * @return the default XMPPConfig item
     */
    public XMPPConfig getDefaultXMPPConfig() {
        return defaultXMPPConfig;
    }
}
