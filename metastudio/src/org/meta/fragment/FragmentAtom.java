/*
 * FragmentAtom.java
 *
 * Created on April 18, 2004, 11:19 PM
 */

package org.meta.fragment;

import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;

/**
 * Defines an atom in a fragment (programatically of course!) <br>
 * There can be two types of FragmentAtom s <br>
 * 1. As a link to the original "aka" atom of the parent molecule <br>
 * 2. As a dummy or pseudo atom in a fragment <br>
 * The two types are created using different initialization procedures .i.e. 
 * call to constructors, look docs for more details.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentAtom extends Atom {
    
    /**
     * Holds value of property dummy.
     */
    private boolean dummy;
    
    /**
     * Holds value of property boundaryAtom.
     */
    private boolean boundaryAtom;
    
    /**
     * Holds value of property akaAtomIndex.
     */
    private int akaAtomIndex;
    
    /**
     * Holds value of property akaAtom.
     */
    private Atom akaAtom;
    
    /** hash code cached here */
    private volatile int hashCode = 0;
    
    /** Creates a new instance of FragmentAtom <br>
     *  .. as a link to the original "aka" atom.
     *    
     * @param akaAtom the original atom of parent molecule to which this atom
     * will be linked
     */
    public FragmentAtom(Atom akaAtom) {
        this(akaAtom.getSymbol(), akaAtom.getCharge(), 
             akaAtom.getAtomCenter(), akaAtom.getIndex());
        
        // and set the other defaults
        this.dummy        = false;
        this.akaAtom      = akaAtom;
        this.akaAtomIndex = akaAtom.getIndex();
        setConnectedList(akaAtom.getConnectedList());
    }
    
    /** Creates a new instance of FragmentAtom <br>
     *  .. as a dummy atom.
     *
     * @param symbol The atom symbol
     * @param charge The charge on the atom
     * @param atomCenter The nuclear center of the atom in cartesian 
     *        coordinates
     */
    public FragmentAtom(String symbol, double charge, Point3D atomCenter) {
        this(symbol, charge, atomCenter, 0);                
    }
    
    /** Creates a new instance of FragmentAtom <br>
     *  .. as a dummy atom.
     *
     * @param symbol The atom symbol
     * @param charge The charge on the atom
     * @param atomCenter The nuclear center of the atom in cartesian 
     *        coordinates
     * @param atomIndex The atom index of this atom
     */
    public FragmentAtom(String symbol, double charge, Point3D atomCenter, 
                        int atomIndex) {               
        super(symbol, charge, atomCenter, atomIndex);
        
        // and set the other defaults
        dummy        = true;
        akaAtom      = null;
        akaAtomIndex = -1;
    }
    
    /** private constructor for cloning purpose */
    private FragmentAtom(Atom akaAtom, boolean dummy, 
                         boolean boundaryAtom, int akaAtomIndex) {
        this(akaAtom);
        
        // and set the other stuff
        this.dummy        = dummy;
        this.akaAtom      = akaAtom;
        this.akaAtomIndex = akaAtomIndex;
        this.boundaryAtom = boundaryAtom;
    }
    
    /**
     * Getter for property dummy.
     * @return Value of property dummy.
     */
    public boolean isDummy() {
        return this.dummy;
    }
    
    /**
     * Setter for property dummy.
     * @param dummy New value of property dummy.
     */
    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
    
    /**
     * Getter for property boundaryAtom.
     * @return Value of property boundaryAtom.
     */
    public boolean isBoundaryAtom() {
        return this.boundaryAtom;
    }
    
    /**
     * Setter for property boundaryAtom.
     * @param boundaryAtom New value of property boundaryAtom.
     */
    public void setBoundaryAtom(boolean boundaryAtom) {
        this.boundaryAtom = boundaryAtom;
    }
    
    /**
     * Getter for property akaAtomIndex.
     * @return Value of property akaAtomIndex.
     */
    public int getAkaAtomIndex() {
        return this.akaAtomIndex;
    }
    
    /**
     * Setter for property akaAtom.
     * @param akaAtom New value of property akaAtom.
     */
    public void setAkaAtom(Atom akaAtom) {
        this.akaAtom = akaAtom;
        
        // and set the other defaults
        this.dummy        = false;        
        this.akaAtomIndex = akaAtom.getIndex();
    }
    
    /**
     * Getter for property akaAtom.
     * @return Value of property akaAtom.
     */
    public Atom getAkaAtom() {
        return akaAtom;
    }
    
    /** i do some cloning business ;)
     * @throws CloneNotSupportedException If that isn't possible
     * @return A copy of the present object
     */  
    public Object clone() throws CloneNotSupportedException {                        
        return new FragmentAtom((Atom) this.akaAtom.clone(), dummy, 
                                boundaryAtom, akaAtomIndex);
    }        
    
    /**
     * overriden hashCode() method
     *    
     * @return int - the hashCode
     */
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17; // prime number!
            long c;
                
            if (akaAtom != null)
                result = 37 * result + akaAtom.hashCode();                        
            result = 37 * result + akaAtomIndex;            
            result = 37 * result + super.hashCode();            
            result = 37 * result + (dummy ? 1 : 0);
            
            hashCode = result;
        } // end if
        
        return hashCode;
    }
    
    /** shallowEquals() method
     * @param obj The object to be compared with
     * @return true : they are same else not
     */
    public boolean shallowEquals(Object obj) {
        if (this == obj) return true;
        
        // pretty expensive ... but at present can't help...
        
        if ((obj == null) || (!(obj instanceof FragmentAtom))) {
            return false;
        } else {
            FragmentAtom o = (FragmentAtom) obj;                        
            
            return (o.akaAtomIndex == akaAtomIndex);
        } // end if
    }        
    
    /** equals() method, do a deep equals()
     *
     * @param obj The object to be compared with
     * @return true : they are same else not
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        // pretty expensive (?) ... but at present can't help...
        
        if ((obj == null) || (!(obj instanceof FragmentAtom))) {
            return false;
        } else {
            FragmentAtom o = (FragmentAtom) obj;
            
            boolean akaEqualFlag = false;
            
            if (akaAtom == o.akaAtom) {
                akaEqualFlag = true;
            } else if ((akaAtom == null) && (o.akaAtom == null)) {
                akaEqualFlag = true;
            } else if ((akaAtom != null) && (o.akaAtom != null)) {
	        akaEqualFlag = akaAtom.equals(o.akaAtom);
            } // end if
            
            return (akaEqualFlag && (dummy == o.dummy) 
                    && super.equals(o));
        } // end if
    }        
} // end of class FragmentAtom
