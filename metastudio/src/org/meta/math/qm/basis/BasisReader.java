/*
 * BasisReader.java
 *
 * Created on July 22, 2004, 7:06 AM
 */

package org.meta.math.qm.basis;


import org.w3c.dom.*;

// others
import java.lang.ref.WeakReference;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;

/**
 * This class provides the means to read a basis set stored in XML format
 * and convert it into appropriate datastructure representation.
 * 
 * Follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BasisReader {
    
    private static WeakReference<BasisReader> _basisReader = null;
    
    private BasisSet basisSet;
    private AtomicBasis atomicBasis;
    private Orbital orbital;
    
    private String element = new String("element");
    
    /** Creates a new instance of BasisReader */
    private BasisReader() {
    }
    
    /**
     * Get an instance (and the only one) of BasisReader
     *
     * @return BasisReader instance
     */
    public static BasisReader getInstance() {
        if (_basisReader == null) {
            _basisReader = new WeakReference<BasisReader>(new BasisReader());
        } // end if
        
        BasisReader basisReader = _basisReader.get();
        
        if (basisReader == null) {
            basisReader  = new BasisReader();
            _basisReader = new WeakReference<BasisReader>(basisReader);
        } // end if
        
        return basisReader;
    }
    
    /**
     * Read a particular basis for the basis library (XML)
     *
     * @param basisName the name of basis
     * @return BasisSet object, representing the requested basis set
     */
    public BasisSet readBasis(String basisName) throws Exception {
        StringResource strings = StringResource.getInstance();
        
        // read the XML config file
        Document basisDoc = Utility.parseXML(
            getClass().getResourceAsStream(strings.getBasisLibraryPath() 
                                           + basisName + ".xml"));

        // and save the basis info. properly
        saveIt(basisDoc);
        
        return basisSet;
    }

    /**
     * Read a particular basis for the basis library (XML)
     *
     * @param basisFileName the name of external basis file name
     * @return BasisSet object, representing the requested basis set
     */
    public BasisSet readExternalBasis(String basisFileName) throws Exception {        
        // read the XML config file
        Document basisDoc = Utility.parseXML(getClass().getResourceAsStream(basisFileName));

        // and save the basis info. properly
        saveIt(basisDoc);

        return basisSet;
    }

    /**
     * Recursive routine save DOM tree nodes 
     * ... and hoping that it reads the basis properly
     */
    private void saveIt(Node n) {
        int type = n.getNodeType();   // get node type
        
        switch (type) {
        case Node.ATTRIBUTE_NODE:
            String nodeName = n.getNodeName();
                        
            if (nodeName.equals("name")) {
                // instance of a new basis set
                basisSet = new BasisSet(nodeName);
            } // end if
            
            break;
        case Node.ELEMENT_NODE:
            element = n.getNodeName();
            
            NamedNodeMap atts = n.getAttributes();
            
            if (element.equals("atom")) {    
                // a new atomic basis
                atomicBasis = new AtomicBasis(
                      atts.getNamedItem("symbol").getNodeValue(), 
                      Integer.parseInt(
                             atts.getNamedItem("atomicNumber").getNodeValue()));
                basisSet.addAtomicBasis(atomicBasis);
            } else if (element.equals("orbital")) {
                // a orbital entry for atomic basis
                orbital = new Orbital(atts.getNamedItem("type").getNodeValue());
                atomicBasis.addOrbital(orbital);
            } else if (element.equals("entry")) {
                // a orbital (coefficient, exponent) entry
                orbital.addEntry(
                 Double.parseDouble(atts.getNamedItem("coeff").getNodeValue()), 
                 Double.parseDouble(atts.getNamedItem("exp").getNodeValue()));
            } else {
                if (atts == null) return;
                
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveIt(att);
                } // end for
            } // end if
            
            break;
        default:
            break;
        } // end switch..case

        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveIt(child);
        } // end for
    }
} // end of class BasisReader
