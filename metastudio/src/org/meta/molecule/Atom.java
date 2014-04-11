/*
 * Atom.java
 *
 * Created on April 27, 2003, 2:47 PM
 */
package org.meta.molecule;

import java.util.*;
import org.meta.common.Units;
import org.meta.common.Utility;
import org.meta.math.geom.Point3D;

/**
 * This class define the structure of an Atom (programmatically of course! ;) ).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Atom implements Cloneable {
    /** Holds value of property symbol. */
    private String symbol;

    /** Holds value of property charge. */
    private double charge;

    /** Holds value of property atomCenter. */
    private Point3D atomCenter;

    /** Holds information on all the connected atoms and the cardinalities, 
     * the atomIndices to which the present atom is connected serves 
     * as the <I>key</I> of the table and the value contains object 
     * of class BondType.
     */
    private Hashtable<Integer, BondType> connectedList;

    /**
     * Holds information on the ZMatrix representation of this atom in the
     * molecule. This can either contain zero, one, two or three entries 
     * indicating bond length, angle or dihedral connectivity with other atoms
     * in the molecular graph.
     */
    private ArrayList<ZMatrixItem> zMatrixElement;

    /** Holds value of property index. */
    private int index;

    /** Creates a new instance of Atom
     * @param symbol The atom symbol
     * @param charge The charge on the atom
     * @param atomCenter The nuclear center of the atom in Cartesian 
     *        coordinates
     */
    public Atom(String symbol, double charge, Point3D atomCenter) {
        this(symbol, charge, atomCenter,
                new Hashtable<Integer, BondType>(1),
                null, 0);
    }

    /** Creates a new instance of Atom
     * @param symbol The atom symbol
     * @param charge The charge on the atom
     * @param atomCenter The nuclear center of the atom in Cartesian 
     *        coordinates
     * @param atomIndex The atom index of this atom
     */
    public Atom(String symbol, double charge, Point3D atomCenter,
               int atomIndex) {
        this(symbol, charge, atomCenter,
                new Hashtable<Integer, BondType>(1),
                null,
                atomIndex);
    }

    /** Creates a new instance of Atom
     * @param symbol The atom symbol
     * @param charge The charge on the atom
     * @param atomCenter The nuclear center of the atom in Cartesian 
     *        coordinates
     * @param connectedList The connected list
     * @param zMatrixElement ZMatrix elements
     * @param atomIndex the atom index of this atom.
     */
    private Atom(String symbol, double charge, Point3D atomCenter,
            Hashtable<Integer, BondType> connectedList,
            ArrayList<ZMatrixItem> zMatrixElement, int atomIndex) {
        this.symbol = Utility.capitalise(symbol.toLowerCase());
        this.charge = charge;
        this.index = atomIndex;
        this.atomCenter = atomCenter;

        this.connectedList = connectedList;
        this.zMatrixElement = zMatrixElement;

        // by default all atom centers are in angstrom units
        atomCenterUnits = Units.ANGSTROM;
    }

    /** initialize the ZMatrix Items */
    private void initZMatrixItems() {
        if (this.zMatrixElement == null) {
            this.zMatrixElement = new ArrayList<ZMatrixItem>(3);

            this.zMatrixElement.add(new ZMatrixItem(null, 0.0));
            this.zMatrixElement.add(new ZMatrixItem(null, 0.0));
            this.zMatrixElement.add(new ZMatrixItem(null, 0.0));
        } // end if
    }

    /** Getter for property symbol.
     * @return Value of property symbol.
     */
    public String getSymbol() {
        return this.symbol;
    }

    /** Setter for property symbol.
     * @param symbol New value of property symbol.
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /** Getter for property charge.
     * @return Value of property charge.
     */
    public double getCharge() {
        return this.charge;
    }

    /** Setter for property charge.
     * @param charge New value of property charge.
     */
    public void setCharge(double charge) {
        this.charge = charge;
    }

    /** Getter for property atomCenter.
     * @return Value of property atomCenter.
     */
    public Point3D getAtomCenter() {
        return this.atomCenter;
    }

    /** Getter for property atomCenterInAU.
     * @return Value of property atomCenterInAU.
     */
    public Point3D getAtomCenterInAU() {
        return this.atomCenter.mul((atomCenterUnits == Units.AU) ? 1.0
                                      : 1.0/Utility.AU_TO_ANGSTROM_FACTOR);
    }

    protected Units atomCenterUnits;

    /**
     * Get the value of atomCenterUnits
     *
     * @return the value of atomCenterUnits
     */
    public Units getAtomCenterUnits() {
        return atomCenterUnits;
    }

    /**
     * Set the value of atomCenterUnits
     *
     * @param atomCenterUnits new value of atomCenterUnits
     */
    public void setAtomCenterUnits(Units atomCenterUnits) {
        this.atomCenterUnits = atomCenterUnits;
    }

    /** Setter for property atomCenter.
     * @param atomCenter New value of property atomCenter.
     */
    public void setAtomCenter(Point3D atomCenter) {
        this.atomCenter = atomCenter;
    }

    /** Getter for property x.
     * @return Value of property x.
     */
    public double getX() {
        return atomCenter.getX();
    }

    /** Getter for property y.
     * @return Value of property y.
     */
    public double getY() {
        return atomCenter.getY();
    }

    /** Getter for property z.
     * @return Value of property z.
     */
    public double getZ() {
        return atomCenter.getZ();
    }

    /** overloaded toString() method.
     * @return A description of Atom object
     */
    @Override
    public String toString() {
        return symbol + " " + charge + " " + atomCenter.toString();
    }

    /** for elaborate information
     * @return An extended description!
     */
    public String toExtendedString() {
        return symbol + " " + charge + " " + atomCenter.toString()
                + " " + connectedList.toString();
    }

    /** overloaded equals() method
     * @param obj The object to be compared with
     * @return true : they are same else not
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (!(obj instanceof Atom))) {
            return false;
        } else {
            Atom o = (Atom) obj;

            return ((o.symbol.equals(symbol)) && (o.charge == charge)
                    && (o.atomCenter.equals(atomCenter)) && (o.index == index));
        } // end if
    }

    /**
     * Simple method to find the distance between two atom centers.
     * @param atom - the atom from which the distance is to be found
     * @return the distance between the two atom centers
     */
    public double distanceFrom(Atom atom) {
        return distanceFrom(atom.atomCenter);
    }

    /**
     * Simple method to find the distance between atom center and an point.
     * @param point - the point to which the distance is to be found
     * @return the distance between the point and the atom center
     */
    public double distanceFrom(Point3D point) {
        return atomCenter.distanceFrom(point);
    }

    /** i do some cloning business ;)
     * @throws CloneNotSupportedException If that isn't possible
     * @return A copy of the present object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Hashtable<Integer, BondType> theClonedConnection
                                     = new Hashtable<Integer, BondType>();

        Enumeration<Integer> keys = connectedList.keys();
        Integer key;

        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            theClonedConnection.put(new Integer(key.intValue()),
                    connectedList.get(key));
        } // end while

        ArrayList<ZMatrixItem> theColonedZMatrixElement
                                 = new ArrayList<ZMatrixItem>();

        try {
            for (ZMatrixItem item : zMatrixElement) {
                theColonedZMatrixElement.add((ZMatrixItem) item.clone());
            } // end for
        } catch(NullPointerException npe) {
            theColonedZMatrixElement = null;
        } // end of try .. catch block

        return new Atom(this.symbol, this.charge,
                (Point3D) this.atomCenter.clone(), theClonedConnection,
                theColonedZMatrixElement, index);
    }

    /** Method to add a connection between the present atom and the atom
     * specified by atomIndex as specified by the implimentation of
     * Molecule interface.
     * @param atomIndex the integer index of atom to be connected
     * @param bondType the bond type
     */
    public void addConnection(int atomIndex, BondType bondType) {
        Integer key = new Integer(atomIndex);

        if (bondType.equals(BondType.NO_BOND) && connectedList.containsKey(key)) {
            connectedList.remove(key);
            return;
        } // end if

        connectedList.put(key, bondType);
    }

    /** Method to remove connection between the present atom and the atom
     * specified by atomIndex as specified by the implementation of
     * Molecule interface.
     * @param atomIndex the index of atom to be disconnected
     */
    public void removeConnection(int atomIndex) {
        connectedList.remove(new Integer(atomIndex));
    }

    /**
     * remove all the connections to this atom
     */
    public void removeAllConnections() {
        connectedList = new Hashtable<Integer, BondType>();
    }

    /** Getter for property connectedList.
     * @return Value of property connectedList.
     *
     */
    public Hashtable<Integer, BondType> getConnectedList() {
        return this.connectedList;
    }

    /** Setter for property connectedList.
     * @param connectedList New value of property connectedList.
     *
     */
    public void setConnectedList(Hashtable<Integer, BondType> connectedList) {
        this.connectedList = connectedList;
    }

    /**
     * check if an atom index is in the connected list.
     *
     * @param atomIndex the index to be checked
     * @return boolean - true / false indicating the present connectivity 
     *         status.
     */
    public boolean isConnected(int atomIndex) {
        return (connectedList.containsKey(new Integer(atomIndex)));
    }

    /**
     * get the bond type of the current atom with the specified atom index.
     *
     * @param atomIndex the index to be checked
     * @return BondType - type of bonding.
     */
    public BondType getConnectivity(int atomIndex) {
        if (isConnected(atomIndex)) {
            return (BondType) connectedList.get(new Integer(atomIndex));
        } else {
            return BondType.NO_BOND;
        } // end if
    }

    /**
     * @return the number of atoms that are strongly bonded to this atom
     */
    public int getNumberOfStrongBonds() {
        int bonds = 0;
        Iterator<BondType> connectedListIter = connectedList.values().iterator();
        while (connectedListIter.hasNext()) {
            BondType bond = connectedListIter.next();
            if (bond.isStrongBond()) {
                bonds++;
            } // end if
        } // end while

        return bonds;
    }

    /**
     * @return the sum of the bond orders of this atom
     */
    public double getSumOfBondOrders() {
        double sumBondOrders = 0;
        Iterator<BondType> connectedListIter = connectedList.values().iterator();
        while (connectedListIter.hasNext()) {
            BondType bond = connectedListIter.next();
            sumBondOrders += bond.getBondOrder();
        } // end while

        return sumBondOrders;
    }

    /**
     * @return the number of atoms that are "double bonded" to this atom
     */
    public int getNumberOfDoubleBonds() {
        int doubleBonds = 0;
        Iterator<BondType> connectedListIter = connectedList.values().iterator();
        while (connectedListIter.hasNext()) {
            BondType bond = connectedListIter.next();
            if (bond == BondType.DOUBLE_BOND) {
                doubleBonds++;
            } // end if
        } // end while

        return doubleBonds;
    }

    /**
     * @return true if this atom participates in a resonant bond structure
     */
    public boolean hasResonantBonds() {
        Iterator<Integer> connectedAtomsIter = connectedList.keySet().iterator();
        while (connectedAtomsIter.hasNext()) {
            Integer connectedIndex = connectedAtomsIter.next();
            BondType bond = connectedList.get(connectedIndex);
            if (bond == BondType.AROMATIC_BOND || bond == BondType.AMIDE_BOND) {
                return true;
            }
        }
        return false;
    }

    /**
     * return the degree of connectivity of this atom.
     * 
     * @return the degree of connectivity of this atom
     */
    public int getDegree() {
        return connectedList.size();
    }

    /** Getter for property index.
     * @return Value of property index.
     *
     */
    public int getIndex() {
        return this.index;
    }

    /** Setter for property index.
     * @param index New value of property index.
     *
     */
    public void setIndex(int index) {
        this.index = index;
    }
    /** Position of length reference in the ZMatrixElement */
    private final static int LENGTH_REFERENCE_POS = 0;

    /**
     * Getter for property lengthReference.
     * @return Value of property lengthReference.
     */
    public ZMatrixItem getLengthReference() {
        initZMatrixItems();
        return zMatrixElement.get(LENGTH_REFERENCE_POS);
    }

    /**
     * Setter for property lengthReference.
     * @param lengthReference New value of property lengthReference.
     */
    public void setLengthReference(ZMatrixItem lengthReference) {
        initZMatrixItems();
        zMatrixElement.set(LENGTH_REFERENCE_POS, lengthReference);
    }
    
    /** Position of angle reference in the ZMatrixElement */
    private final static int ANGLE_REFERENCE_POS = 1;

    /**
     * Getter for property angleReference.
     * @return Value of property angleReference.
     */
    public ZMatrixItem getAngleReference() {
        initZMatrixItems();
        return zMatrixElement.get(ANGLE_REFERENCE_POS);
    }

    /**
     * Setter for property angleReference.
     * @param angleReference New value of property angleReference.
     */
    public void setAngleReference(ZMatrixItem angleReference) {
        initZMatrixItems();
        zMatrixElement.set(ANGLE_REFERENCE_POS, angleReference);
    }

    /** Position of dihedral reference in the ZMatrixElement */
    private final static int DIHEDRAL_REFERENCE_POS = 2;

    /**
     * Getter for property dihedralReference.
     * @return Value of property dihedralReference.
     */
    public ZMatrixItem getDihedralReference() {
        initZMatrixItems();
        return zMatrixElement.get(DIHEDRAL_REFERENCE_POS);
    }

    /**
     * Setter for property dihedralReference.
     * @param dihedralReference New value of property dihedralReference.
     */
    public void setDihedralReference(ZMatrixItem dihedralReference) {
        initZMatrixItems();
        zMatrixElement.set(DIHEDRAL_REFERENCE_POS, dihedralReference);
    }

    protected ArrayList<UserDefinedAtomProperty> userProperties;

    /**
     * Add a user defined atom property.
     *
     * @param uProp the new instance of property to be added
     * @throws UnsupportedOperationException if a property with the same
     *         name already exists
     */
    public void addUserDefinedAtomProperty(UserDefinedAtomProperty uProp) {
        if (userProperties == null)
            userProperties = new ArrayList<UserDefinedAtomProperty>();

        // first check if this property already exists
        String name = uProp.getName();
        for (UserDefinedAtomProperty uprop : userProperties) {
            if (uprop.getName().equals(name)) {
                throw new UnsupportedOperationException("Property with name" +
                        " '" + name + "' is already defined for this object!");
            } // end if
        } // end for

        userProperties.add(uProp);
    }

    /**
     * Remove a user defined property.
     *
     * @param uProp instance of UserDefinedAtomProperty to be removed
     */
    public void removeUserDefinedAtomProperty(UserDefinedAtomProperty uProp) {
       if (userProperties == null) return;

       userProperties.remove(uProp);
    }

    /**
     * Get a list of all the user defined atom properties, null if
     * no user defined properties were ever added to this Atom object.
     *
     * @return an Iterator object of list of instances of
     *              UserDefinedAtomProperty
     */
    public Iterator<UserDefinedAtomProperty> getUserDefinedAtomProperty() {
        if (userProperties == null) return null;

        return userProperties.iterator();
    }

    /**
     * Get a user defined atom property with the specified name, null if
     * no user defined property with specified name exists.
     *
     * @param name the name of the property want a reference to
     * @return an Iterator object of list of instances of
     *              UserDefinedAtomProperty
     */
    public UserDefinedAtomProperty getUserDefinedAtomProperty(String name) {
        if (userProperties == null) return null;

        for (UserDefinedAtomProperty uprop : userProperties) 
            if (uprop.getName().equals(name)) return uprop;

        return null;
    }
} // end of class Atom
