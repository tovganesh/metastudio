/*
 * TriTable.java
 *
 * Created on November 25, 2005, 8:32 PM
 *
 */

package org.meta.shell.idebeans.graphics.surfaces;

import java.lang.ref.WeakReference;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.w3c.dom.*;

/**
 * Simple class for reading in tritable, and storing in the form of an array!
 * Follows a singleton pattern.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TriTable {
    
    private static WeakReference<TriTable> _triTable;
    
    /** Creates a new instance of TriTable */
    private TriTable() throws Exception {
        readInTable();
    }

    /**
     * readin the table from the resource
     */
    private void readInTable() throws Exception {
        StringResource strings = StringResource.getInstance();
        
        // read the internal XML config file
        Document triTableDoc = Utility.parseXML(
                getClass().getResourceAsStream(strings.getTriTableResource()));
        
        currentRowIndex = -1;
        currentIndex    = 0;
        
        saveTable(triTableDoc);
    }

    private int currentRowIndex, currentIndex;
    
    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveTable(Node n) {
        int type = n.getNodeType();   // get node type
        
        switch (type) {
        case Node.ATTRIBUTE_NODE:
            String nodeName = n.getNodeName();
            
            if (nodeName.equals("value")) { 
                triTable[currentRowIndex][currentIndex++] =
                        (new Integer(n.getNodeValue())).intValue();
            } // end if
            
            break;
        case Node.ELEMENT_NODE:            
            NamedNodeMap atts = n.getAttributes();
            
            if (n.getNodeName().equals("triTable")) {
                triTable = new int[new Integer(atts.getNamedItem("size")
                                    .getNodeValue()).intValue()][new Integer(
                                      atts.getNamedItem("entriesInEachRow")
                                          .getNodeValue()).intValue()];
                currentRowIndex = -1;
                currentIndex    = 0;                                
            } else if (n.getNodeName().equals("triset")) {
                currentRowIndex++;
                currentIndex = 0;                                
            } // end if
            
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
     * @return instance of TriTable
     */
    public static TriTable getInstance() throws Exception {
        if (_triTable == null) {
            _triTable = new WeakReference<TriTable>(new TriTable());
        } // end if
        
        TriTable triTable = _triTable.get();
        
        if (triTable == null) {
            triTable  = new TriTable();
            _triTable = new WeakReference<TriTable>(triTable);
        } // end if
        
        return triTable;
    }
    
    /**
     * Holds value of property triTable.
     */
    private int[][] triTable;

    /**
     * Return the whole table as an array
     *
     * @return an array representing all elements in the table
     */
    public int [][] getTableAsArray() {
        return triTable;
    }
    
    /**
     * Indexed getter for property triTable.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public int[] getTriTable(int index) {
        return this.triTable[index];
    }

    /**
     * Getter for property triTable.
     * @return Value of property triTable.
     */
    public int[][] getTriTable() {
        return this.triTable;
    }

    /**
     * Indexed setter for property triTable.
     * @param index Index of the property.
     * @param triTable New value of the property at <CODE>index</CODE>.
     */
    public void setTriTable(int index, int[] triTable) {
        this.triTable[index] = triTable;
    }

    /**
     * Setter for property triTable.
     * @param triTable New value of property triTable.
     */
    public void setTriTable(int[][] triTable) {
        this.triTable = triTable;
    }
    
} // end of class TriTable
