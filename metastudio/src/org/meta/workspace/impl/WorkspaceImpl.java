/*
 * WorkspaceImpl.java
 *
 * Created on July 8, 2003, 8:49 PM
 */

package org.meta.workspace.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.workspace.Workspace;
import org.meta.workspace.WorkspaceIOException;
import org.meta.workspace.WorkspaceItem;
import org.meta.workspace.WorkspaceProperty;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The default implementation of the Workspace interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WorkspaceImpl extends Workspace {
    
    /** Creates a new instance of WorkspaceImpl */
    public WorkspaceImpl(String workspaceConfigurationFile) {
        super(workspaceConfigurationFile);
        
        this.implClass = "org.meta.workspace.impl.WorkspaceImpl";
    }
    
    /**
     * The methods implements of how a workspace is opened.
     *
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void open() throws WorkspaceIOException {
        try {            
            // read the configuration file
            Document wcf = Utility.parseXML(workspaceConfigurationFile);
            
            // set default lists
            this.workspaceItems      = new Vector<WorkspaceItem>();
            this.workspaceProperties = new Vector<WorkspaceProperty>();
        
            // update the object based on the configuration file
            updateIt(wcf);
            this.dirty = false;
            wcf = null;
        } catch (Exception e) {
            System.err.println("Error reading workspace configuration file: "
                               + e.toString());
            e.printStackTrace();
            
            throw new WorkspaceIOException(
                              "Error reading workspace configuration file: "
                               + e.toString());
        } // end of try .. catch block
    }
    
    /**
     * Initiates a close operation on the workspace if currently active.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void close() throws WorkspaceIOException {
    }        
    
    /**
     * Initiates a save operation of the workspace on a non-volatile store..
     * .. possibly on a disk, which may in-turn invoke other save methods.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void save() throws WorkspaceIOException {
        // a save on Workspace sould first ensure save on 
        // the WorkspaceItem s .
        Enumeration items = workspaceItems.elements();
        WorkspaceItem item;
                
        while (items.hasMoreElements()) {
            item = (WorkspaceItem) items.nextElement();
            // initiate save on the items
            item.save();
        } // end while
        
        // next write the workspace config file.
        try {
            FileOutputStream fis = 
                             new FileOutputStream(workspaceConfigurationFile);
        
            // the header
            fis.write(StringResource.getInstance().getXmlHeader().getBytes());
            fis.write("\n".getBytes());
            
            if (!(new File(workspaceDirectory)).exists()) {
                workspaceDirectory = (new File(workspaceConfigurationFile)).getPath();
            } // end if

            // the rest of the file
            fis.write(("<workspace version=\""+ version + "\" " 
                       + "ID=\"" + ID + "\" "
                       + "creationDate=\"" 
                         + creationDate.getTime()
                         + "\" "
                       + "lastModifiedDate=\"" 
                         + lastModifiedDate.getTime()
                         + "\" "
                       + "author=\"" + author + "\" "
                       + "description=\"" + description + "\" "
                       + "internalName=\"" + internalName + "\" "
                       + "implClass=\"" + implClass + "\" "
                       + "workspaceDirectory=\"" + workspaceDirectory + "\" "
                       + "> \n").getBytes());
            
            items = workspaceItems.elements();           
            
            // write info about workspace items
            while (items.hasMoreElements()) {
                item = (WorkspaceItem) items.nextElement();
                
                item.setBaseDirectory(workspaceDirectory);
                
                fis.write(("\t<workspaceItem name=\"" + item.getName() + "\" "
                           + "ID=\""       + item.getID() + "\" "
                           + "file=\""     + item.getWorkspaceItemFile() + "\" "
                           + "description=\"" + item.getDescription() + "\" "
                           + "implClass=\""   + item.getImplClass() + "\" "
                           + "type=\""        + item.getType() + "\" "
                           + " > \n").getBytes());
                
                // workspace properties goes here
                
                fis.write(("\t</workspaceItem>\n").getBytes());                
            } // end while
        
            fis.write(("</workspace> \n").getBytes());
        
            fis.close();
            
            this.dirty = false;
        } catch (IOException ioe) {
            throw new WorkspaceIOException("I/O Exception while saving "
                      + "workspace : " + ioe.toString());
        } // end try .. catch block
    }
    
    /**
     * method to update this object based on the XML configuration data.
     * Warning: this is a recursive procedure
     */
    private void updateIt(Node n) throws WorkspaceIOException {
        int type = n.getNodeType();   // get node type
                
        switch (type) {
            case Node.ATTRIBUTE_NODE:
                 String nodeName = n.getNodeName();
                
                 if (nodeName.equals("version")) {
                    version = n.getNodeValue();
                 } else if (nodeName.equals("ID")) {
                    ID = n.getNodeValue();
                 } else if (nodeName.equals("creationDate")) {
                    try {
                      creationDate = new Date(Long.parseLong(n.getNodeValue()));
                    } catch(Exception e) {
                      // ignore error silently
                      creationDate = new Date();
                    } // end of try .. catch block
                 } else if (nodeName.equals("lastModifiedDate")) {
                    try {
                      lastModifiedDate = new Date(Long.parseLong(n.getNodeValue()));
                    } catch(Exception e) {
                      // ignore error silently
                      lastModifiedDate = new Date();
                    } // end of try .. catch block
                 } else if (nodeName.equals("author")) {
                    author = n.getNodeValue();
                 } else if (nodeName.equals("description")) {
                    description = n.getNodeValue();
                 } else if (nodeName.equals("internalName")) {
                    internalName = n.getNodeValue();
                 } else if (nodeName.equals("implClass")) {
                    // well?? it has to be this!!
                    if (!implClass.equals(n.getNodeValue())) {
                        throw new WorkspaceIOException("Implementation class "
                                  + "different!! can't read - confusion!");
                    } // end if
                 } else if (nodeName.equals("workspaceDirectory")) {
                    workspaceDirectory = n.getNodeValue();
                    
                    if (!(new File(workspaceDirectory)).exists()) {
                        workspaceDirectory = (new File(workspaceConfigurationFile)).getPath();
                    } // end if
                 } // end if
                 
                break;
            case Node.ELEMENT_NODE:
                String element = n.getNodeName();
            
                NamedNodeMap atts = n.getAttributes();
                
                // TODO: add support for workspaceProperty
                
                if (element.equals("workspaceItem")) {
                    // now we need to be a bit careful here:
                    // steps involved:
                    // 1. Get the proper instance of the WorkspaceItem impl.
                    // 2. Make the class read the file using open() method
                    // 3. Add this item to the workspace class
                    // Note that we are not using services of 
                    // WorkspaceItemFactory so as to avoid versining or 
                    // incompatibility in the XML schema produced by an
                    // item type supported by two different implementations
                    try {
                        String impClz = atts.getNamedItem("implClass")
                                            .getNodeValue();
                        String wif    = atts.getNamedItem("file").getNodeValue();
                        
                        // get the class instance
                        Class wic = Class.forName(impClz);
                        
                        // get the constuctor
                        Constructor wicConstructor = 
                          wic.getDeclaredConstructor(new Class[]{String.class});
                        
                        // and instantiate it! ... classic stunt ;)
                        WorkspaceItem wi = (WorkspaceItem) wicConstructor
                                                              .newInstance(wif);
                        
                        wi.setBaseDirectory(workspaceDirectory);
                        
                        // then open the file ?? should we at this stage??
                        wi.open();
                        
                        // updates other properties of this item
                        wi.setName(atts.getNamedItem("name").getNodeValue());
                        wi.setID(atts.getNamedItem("ID").getNodeValue());
                        wi.setDescription(atts.getNamedItem("description")
                                              .getNodeValue());
                        wi.setType(atts.getNamedItem("type").getNodeValue());
                        
                        // and finally add it to workspace
                        addWorkspaceItem(wi);
                    } catch (Exception e){
                        System.out.println("Unexpected error : " + e
                                                 + " can't read - confusion!");
                        e.printStackTrace();
                        
                        throw new WorkspaceIOException("Unexpected error : " + e
                                                 + " can't read - confusion!");
                    } // end of try .. catch block
                } else {
                    if (atts == null) return;
                    
                    for (int i = 0; i < atts.getLength(); i++) {
                        Node att = atts.item(i);
                        updateIt(att);
                    } // end for
                } // end if
                
                break;
            default:                
                break;
        } // end switch .. case block
        
        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             updateIt(child);
        } // end for
    }
} // end of class WorkspaceImpl
