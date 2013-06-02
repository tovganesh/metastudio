/*
 * RingRecognizer.java
 *
 * Created on May 26, 2004, 6:38 AM
 */

package org.meta.molecule.impl;

import java.util.*;

import org.meta.math.Vector3D;
import org.meta.math.MathUtil;
import org.meta.molecule.AtomGroup;
import org.meta.molecule.AtomGroupList;
import org.meta.molecule.Molecule;
import org.meta.molecule.SpecialStructureRecognizer;


/**
 * Detects and records cycles in a molecular graph along with the type
 * of the cycle (planar / non-planar)
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RingRecognizer implements SpecialStructureRecognizer {
    
    /** we do not consider rings bigger than these, they may be cut ? */
    public static final int  DEFAULT_MAX_RING_SIZE = 15;
    
    /** unprocessed vertex */
    protected static final byte WHITE = (byte) 0; 
    
    /** the vertex and its children are being processed */
    protected static final byte GRAY  = (byte) 1; 
    
    /** the vertex is processed completely */
    protected static final byte BLACK = (byte) 2;                
    
    /** the list of atoms is stored here */
    private AtomGroupList theRings;
    
    /** torsion tolerance for defining planarity - 5<sup>0</sup> */
    protected static double TORSSIAN_ANGLE_TOLERANCE = MathUtil.toRadians(5.0);
    
    /**
     * Holds value of property maxRingSize.
     */
    private int maxRingSize;
    
    /** for handling graph traversal and cycle detection */
    private byte [] color;
    private int  [] parent;
    
    /** to make this available to all methods of this object */
    private Molecule theMolecule;
    
    /** Creates a new instance of RingRecognizer */
    public RingRecognizer() {
        maxRingSize = DEFAULT_MAX_RING_SIZE;
        theRings = new AtomGroupListImpl();
    }        
        
    /** 
     * This method detects presence of cycles in the molecular
     * graph and record this information in a object of AtomGroupList.
     *
     * @param molecule The molecule object reference.
     */    
    @Override
    public void recognizeAndRecord(Molecule molecule) {
        int i;
        int noOfAtoms = molecule.getNumberOfAtoms();
        
        // the molecule object .. to be seen by all methods
        this.theMolecule = molecule;
        
        // the ring list
        theRings = new AtomGroupListImpl();
        
        // init the algo. data structures
        color  = new byte[noOfAtoms];
        parent = new int[noOfAtoms];
        
        for(i=0; i<noOfAtoms; i++) {
            color[i]  = WHITE;
            parent[i] = -1;
        } // end for
        
        // now start the algo.
        for(i=0; i<noOfAtoms; i++) {
            if (color[i] == WHITE) {
                traverseAndRecordRing(i);
            } // end if
        } // end for
        
        // remove the subset rings
        removeSubsets(); 
        
        // and finally set the planarity of the rings detected
        for(i=0; i<theRings.getSize(); i++) {
            setPlanarity(i);
        } // end for                
    }
    
    /** 
     * traverseAndRecordRing() - do a dept first traversal, in the process
     * make a spanning tree and detect the occurance of a tree.
     *
     * @param v - the starting vector for traversal   
     */
    protected void traverseAndRecordRing(int v) {
        color[v] = GRAY;  // this vertex is to be processed now
        
        Hashtable connectedList = theMolecule.getAtom(v).getConnectedList();
        Enumeration atoms       = connectedList.keys();
        int atomIndex;
        
        // traverse the strongly connected ones only
        while(atoms.hasMoreElements()) {
            atomIndex = ((Integer) atoms.nextElement()).intValue();
            
            if (theMolecule.isStronglyBonded(atomIndex, v)) {
                if (color[atomIndex] == WHITE) {   // not yet traversed?
                    parent[atomIndex] = v;
                    traverseAndRecordRing(atomIndex); // recursive call
                } else if (color[atomIndex] == BLACK) { // found a ring!
                    Ring theRing = new Ring();
                    
                    theRing.addAtomIndex(atomIndex);
                    
                    int vertex = atomIndex;
                    
                    // record the cycle
                    while(true) {
                        vertex = parent[vertex];
                        
                        if (vertex == -1) { 
                            break;
                        } else if (vertex == v) {
                            theRing.addAtomIndex(vertex);
                            break;
                        } else {
                            theRing.addAtomIndex(vertex);
                        } // end if
                    } // end while  
                    
                    // record the ring
                    theRings.addGroup(theRing);
                } // end if
            } // end if
        } // end while
        
        // this node has been completely traversed, if we hit this node again
        // we have a cycle!
        color[v] = BLACK;
    }
    
    /**
     * remove subsets of rings
     */
    protected void removeSubsets() {
        ArrayList theList = theRings.getGroupList();        
        Ring r1;
        
        for(int i=0; i<theList.size(); i++) {
            r1 = (Ring) theList.get(i);
            
            for(int j=0; j<theList.size(); j++) {
                if (i == j) continue;
                
                // this does all the job!, no head ache
                if (r1.intersect((AtomGroup) theList.get(j))) break;
            } // end for
        } // end for
    }
    
    /**
     * Sets the planarity flag of the ring. <br>
     * To determine the planarity of a ring we make few assumptions: <br>
     * <ul>
     * <li> Three membered rings are always planar. </li>
     * <li> For rings more than 4 members we check the if torssian angle 
     *   with the constant TORSSIAN_ANGLE_TOLERANCE.
     *   If the angle is less than TORSSIAN_ANGLE_TOLERANCE then the ring 
     *   is planar else it is non-planar. </li>
     * </ul>
     *
     * A Note on torssian angle:
     * -------------------------
     * The torssian angle is computed as follows: <br>
     * Consider a ring fragment having atleast 4 atoms - <br>
     * <pre>
     *
     *      (2) +-------+ (3)
     *         +         +
     *    (1) +           + (4)
     * </pre> <br>
     * Where {(1), (2), (3), (4)} are atoms which are a part of the ring.
     * We consider the planes defined by atom points {(1), (2), (3)} and
     * {(2), (3), (4)}. The angle between these two planes is called the 
     * torssian angle. For a ring to be planar (n-2) torssian angles should
     * be less than TORSSIAN_ANGLE_TOLERANCE; where n is the number of 
     * atoms in the ring.
     *
     * @param ringNumber - the ring number whose planarity flag is to be set
     */
    protected void setPlanarity(int ringNumber) {        
        Ring theRing = (Ring) theRings.getGroupList().get(ringNumber);
        
        // we consider rings having less than 4 members are always planar
        if (theRing.getSize() < 4) { 
            theRing.setPlanar(true);
            return;
        } // end if
        
        int i, i2, i3;
        Vector3D a12, a23, a34;
        Vector3D n1, n2;        
        Vector3D [] points = new Vector3D[theRing.getSize()];
        
        // get all the atom centers of the ring
        for(i=0; i<points.length; i++) {
            points[i] = new Vector3D(theMolecule.getAtom(
                                    theRing.getAtomIndexAt(i)).getAtomCenter());
        } // end for
        
        for(i=0; i<points.length-2; i++) {
            // compute the vectors representing the plane
            a12 = points[i+1].sub(points[i]);
            
            i2 = ((i+2 >= points.length) ? 0 : (i + 2));
            
            a23 = points[i2].sub(points[i+1]);
            
            i3 = ((i+3 >= points.length) ? 0 : (i + 3));
            
            a34 = points[i3].sub(points[i2]);
            
            // compute normals to the plane
            n1 = a12.cross(a23);
            n2 = a23.cross(a34);
            
            // and check the angle between the planes
            if (n1.angleWith(n2) > TORSSIAN_ANGLE_TOLERANCE) {
                theRing.setPlanar(false); // not planar
                return;
            } // end if 
        } // end for
        
        // if we reached here, the ring is probably planar
        theRing.setPlanar(true);
        return;
    }
    
    /**
     * This method returns the list of all the identified structures as a
     * object of AtomGroupList
     * 
     * @return Instance of AtomGroupList
     */
    @Override
    public AtomGroupList getGroupList() {
        return theRings;
    }
    
    /**
     * Getter for property maxRingSize.
     * @return Value of property maxRingSize.
     */
    public int getMaxRingSize() {
        return this.maxRingSize;
    }
    
    /**
     * Setter for property maxRingSize.
     * @param maxRingSize New value of property maxRingSize.
     */
    public void setMaxRingSize(int maxRingSize) {
        this.maxRingSize = maxRingSize;
    }
    
    /**
     * a straight forward setGroupList() method, no checks are made
     * of the correctness of groupList and hence the correctness wholy
     * rests on the callie.
     *
     * @param groupList the groupList consisting of Ring objects.
     */
    @Override
    public void setGroupList(AtomGroupList groupList) {
        this.theRings = groupList;
    }
    
} // end of class RingRecognizer
