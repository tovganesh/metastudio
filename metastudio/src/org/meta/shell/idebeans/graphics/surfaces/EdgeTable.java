/*
 * EdgeTable.java
 *
 * Created on November 25, 2005, 8:16 PM
 *
 */

package org.meta.shell.idebeans.graphics.surfaces;

import java.lang.ref.WeakReference;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.w3c.dom.*;

/**
 * Simple class for reading in edgetable, and storing in the form of an array!
 * Follows a singleton pattern.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class EdgeTable {
    
    private static WeakReference<EdgeTable> _edgeTable;
    
    /** Creates a new instance of EdgeTable */
    private EdgeTable() throws Exception {
        readInTable();
    }

    /**
     * readin the table from the resource
     */
    private void readInTable() throws Exception {
        StringResource strings = StringResource.getInstance();
        
        // read the internal XML config file
        Document edgeTableDoc = Utility.parseXML(
                getClass().getResourceAsStream(strings.getEdgeTableResource()));
        
        currentIndex = 0;
        
        saveTable(edgeTableDoc);
    }
    
    private int currentIndex;
    
    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveTable(Node n) {
        int type = n.getNodeType();   // get node type
        
        switch (type) {
        case Node.ATTRIBUTE_NODE:
            String nodeName = n.getNodeName();
            
            if (nodeName.equals("size")) { 
                edgeTable = new int[(new Integer(n.getNodeValue())).intValue()];
                currentIndex = 0;
            } else if (nodeName.equals("value")) { 
                edgeTable[currentIndex++] = Integer.decode(n.getNodeValue());
            } // end if
            
            break;
        case Node.ELEMENT_NODE:            
            NamedNodeMap atts = n.getAttributes();
            
            if (atts == null) return;
            
            // save the others
            for (int i = 0; i < atts.getLength(); i++) {
                Node att = atts.item(i);
                saveTable(att);
            } // end for
            
            break;
        default:
            break;
        } // end switch .. case
        
        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveTable(child);
        } // end for
    }
    
    /**
     * obtain a instance of this calss
     *
     * @return instance of EdgeTable
     */
    public static EdgeTable getInstance() throws Exception {
        if (_edgeTable == null) {
            _edgeTable = new WeakReference<EdgeTable>(new EdgeTable());
        } // end if
        
        EdgeTable edgeTable = _edgeTable.get();
        
        if (edgeTable == null) {
            edgeTable  = new EdgeTable();
            _edgeTable = new WeakReference<EdgeTable>(edgeTable);
        } // end if
        
        return edgeTable;
    }
    
    /**
     * Holds value of property edgeTable.
     */
    private int [] edgeTable;

    /**
     * Return the whole table as an array
     *
     * @return an array representing all elements in the table
     */
    public int [] getTableAsArray() {
        return edgeTable;
    }
    
    /**
     * Indexed getter for property edgeTable.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public int getEdgeTable(int index) {
        return this.edgeTable[index];
    }

    /**
     * Getter for property edgeTable.
     * @return Value of property edgeTable.
     */
    public int[] getEdgeTable() {
        return this.edgeTable;
    }

    /**
     * Indexed setter for property edgeTable.
     * @param index Index of the property.
     * @param edgeTable New value of the property at <CODE>index</CODE>.
     */
    public void setEdgeTable(int index, int edgeTable) {
        this.edgeTable[index] = edgeTable;
    }

    /**
     * Setter for property edgeTable.
     * @param edgeTable New value of property edgeTable.
     */
    public void setEdgeTable(int[] edgeTable) {
        this.edgeTable = edgeTable;
    }
    
} // end of class EdgeTable
