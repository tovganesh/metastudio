/*
 * Molecule.java
 *
 * Created on April 6, 2003, 7:00 AM
 */

package org.meta.molecule;

import java.io.Serializable;
import java.util.Iterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.meta.common.EventListenerList;

import org.meta.math.geom.Point3D;
import org.meta.math.geom.BoundingBox;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.math.Vector3D;
import org.meta.molecule.event.MoleculeStateChangeEvent;
import org.meta.molecule.event.MoleculeStateChangeListener;
import org.meta.molecule.property.electronic.MolecularVolumetricProperty;
import org.meta.molecule.property.vibrational.MolecularVibrationalProperty;

/**
 * Defines an interface for Molecule object. 
 * Any implementation should define the appropriate data structures for storage
 * of atoms, their connectivity and any special structures.
 * Note that the atomIndices are to be maintained in a consistent form. i.e.
 * the atom added first using any form of addAtom()  method should have an 
 * atomIndex which is numerically lower as compared to an atom added at a later
 * stage. Also negative atom indices should never be used.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class Molecule {
    
    /** Utility field used by event firing mechanism. */
    protected transient EventListenerList<MoleculeStateChangeListener> listenerList =  null;
    
    /** Utility field for enabling / disabling event listeners */
    protected transient boolean enableListeners = true;
    
    /**
     * Holds value of property numberOfSingleBonds.
     */
    protected int numberOfSingleBonds;
    
    /**
     * Holds value of property numberOfMultipleBonds.
     */
    protected int numberOfMultipleBonds;
    
    /**
     * Holds value of property numberOfWeakBonds.
     */
    protected int numberOfWeakBonds;
    
    /**
     * Holds value of property molecularMass.
     */
    protected double molecularMass;
    
    /**
     * Holds value of property numberOfElectrons.
     */
    protected int numberOfElectrons;
    
    /**
     * Returns the title.
     *
     * @return String identifying the title of the Molecule object
     */    
    public abstract String getTitle();
    
    /**
     * sets the title.
     *
     * @param title string identifying the title of the Molecule object
     */    
    public abstract void setTitle(String title);
    
    /**
     * Return an object of moelcular formula, pertaining to this molecule
     *
     * @return MolecularFormula the molecular formula of this molecule
     */    
    public abstract MolecularFormula getFormula();
    
    /** 
     * Adds an atom to this molecule object.
     *
     * @param symbol the atom symbol
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     */    
    public abstract void addAtom(String symbol, double x, double y, double z);
    
    /** 
     * Adds an atom to this molecule object.
     *
     * @param symbol the atom symbol
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     * @param index the atom index
     */    
    public abstract void addAtom(String symbol, double x, double y, double z, 
                                 int index);    
    
    /** 
     * Adds an atom to this molecule object.
     *
     * @param symbol the atom symbol
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     * @param xi X coordinate of the atom base (vector)
     * @param yj Y coordinate of the atom base (vector)
     * @param zk Z coordinate of the atom base (vector)
     * @param index the atom index
     */    
    public abstract void addAtom(String symbol, double x, double y, double z, 
                                 double xi, double yj, double zk, 
                                 int index);  
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param symbol the atom symbol   
     * @param atomCenter the Cartesian coordinates of the atom stored as Point3D
     *        object
     */    
    public abstract void addAtom(String symbol, Point3D atomCenter);
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param symbol the atom symbol
     * @param charge is the charge on the atom (the atomic number in many cases)
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     */    
    public abstract void addAtom(String symbol, double charge, 
                                 double x, double y, double z);
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param symbol the atom symbol
     * @param charge is the charge on the atom (the atomic number in many cases)
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     * @param index the atom index
     */    
    public abstract void addAtom(String symbol, double charge, 
                                 double x, double y, double z, int index);
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param symbol the atom symbol
     * @param charge is the charge on the atom (the atomic number in many cases)
     * @param atomCenter the Cartesian coordinates of the atom stored as Point3D
     *        object
     */    
    public abstract void addAtom(String symbol, double charge, 
                                 Point3D atomCenter);
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param atom the instance of atom class
     */    
    public abstract void addAtom(Atom atom);
    
    /** 
     * remove an atom from this Molecule
     *
     * @param atom the instance of atom class
     */    
    public abstract void removeAtom(Atom atom);
    
    /** 
     * remove an atom from this Molecule at a given index
     *
     * @param atomIndex the atom index to be removed
     */    
    public abstract void removeAtomAt(int atomIndex);
    
    /** 
     * Method to get a particular atom from the lists of atoms.
     *
     * @return Atom the instance of atom class
     */    
    public abstract Atom getAtom(int atomIndex);
    
    /**
     * Return an atom index that seems similar to the one object passed.
     *
     * @param atom the Atom object to be searched for
     * @return the atoms index which is similar to the Atom object passed
     *         (-1 is no match found).
     */
    public abstract int soundsLike(Atom atom);
    
    /**
     * Return an atom index that is closest to a given point
     * 
     * @param point the reference point
     * @return the atom index that is closest to the point
     */
    public abstract int closestTo(Point3D point);

    /** 
     * Method to set a particular atom in the lists of atoms.
     *
     * @param atomIndex the index in the list, which is to be changed 
     * @param atom the instance of atom class
     * @throws runtime exception IndexOutOfBoundsException if not a
     *            valid atomIndex
     */
    public abstract void setAtom(int atomIndex, Atom atom);
    
    /** 
     * Method to get a particular atom from the lists of atoms.
     *
     * @return an iterator object containing a linear list of atoms in the
     *         Molecule!
     */    
    public abstract Iterator<Atom> getAtoms();
    
    /** 
     * Method to get total number of atoms in the molecule.
     *
     * @return number of atoms in the Molecule
     */    
    public abstract int getNumberOfAtoms();
    
    /** 
     * Method to return the bonding box enclosing the molecule.
     *
     * @return BoundingBox inclosing the molecule.
     */    
    public abstract BoundingBox getBoundingBox();
    
    /** 
     * Method to return the bonding box center on COM and enclosing the molecule
     *
     * @return BoundingBox inclosing the molecule and centered on COM.
     */    
    public abstract BoundingBox getMassCenteredBoundingBox();
    
    /**
     * Method to define the bonding type of an atom pair.
     *
     * @param atomIndex1 the reference atom
     * @param atomIndex2 the atom index between which the bonding
     *        is to be defined
     * @param bondType An instance of BondType defining the type of the bond
     */    
    public abstract void setBondType(int atomIndex1, int atomIndex2, 
                                     BondType bondType);
    
    /**
     * Method to remove the bond between an atom pair
     *
     * @param atomIndex1 the reference atom
     * @param atomIndex2 the atom index between which the bonding
     *        is to be removed
     */
    public abstract void removeBondBetween(int atomIndex1, int atomIndex2);
    
    /**
     * Method to obtain the bonding type between an atom pair.
     *
     * @param atomIndex1 the reference atom 
     * @param atomIndex2 the atom index for which the bonding 
     *        information is queried.
     * @return instance of BondType indicating the type of bond.
     */    
    public abstract BondType getBondType(int atomIndex1, int atomIndex2);
    
    /**
     * This method returns true or false indicating whether an atom pair is 
     * bonded.
     *
     * @param atomIndex1 the reference atom 
     * @param atomIndex2 the atom index for which the bonding 
     *        information is queried.
     * @return true if there is a bond between atomIndex1 and atomIndex2, false
     *         otherwise.
     */
    public abstract boolean isBonded(int atomIndex1, int atomIndex2);
    
    /**
     * This method returns true or false indicating whether an atom pair is 
     * having a bond between them.
     *
     * @param atomIndex1 the reference atom 
     * @param atomIndex2 the atom index for which the bonding 
     *        information is queried.
     * @return true if there is a strong bond between atomIndex1 and 
     *         atomIndex2, false otherwise.
     */
    public abstract boolean isStronglyBonded(int atomIndex1, int atomIndex2);
    
    /**
     * Do a breadth first traversal of the the molecular graph starting
     * with the specified atomIndex and return the traversed path as an Iterator
     * of atom indices.
     *
     * @param atomIndex the index from where to begin traversal
     * @return Iterator object containing atom indices visited in that order
     */
    public abstract Iterator<Integer> traversePath(int atomIndex);
     
    /**
     * Do a breadth first traversal of the the molecular graph starting
     * with the specified atomIndex and return the traversed path as an Iterator
     * of atom indices.
     *
     * @param atomIndex the index from where to begin traversal
     * @param noDepth do not got to higher depths
     * @return Iterator object containing atom indices visited in that order
     */
    public abstract Iterator<Integer> traversePath(int atomIndex, 
                                                   boolean noDepth);
    
    /**
     * The method defines an interface to add an implementation of
     * SpecialStructureRecognizer interface, so that certain special
     * structures like rings and special groups can be recognized.
     *
     * @param ssr The implementation object of SpecialStructureRecognizer
     */    
    public abstract void addSpecialStructureRecognizer(
                                            SpecialStructureRecognizer ssr);
    
    /**
     * Removes an implemntation of SpecialStructureRecognizer interface, 
     * so that certain structures may not be identified.
     *
     * @param ssr The implementation object of SpecialStructureRecognizer
     */    
    public abstract void removeSpecialStructureRecognizer(
                                               SpecialStructureRecognizer ssr);
    
    /**
     * Method to get list of all SpecialStructureRecognizer s
     *
     * @return Iterator object contatinig list of all in implementations of
     *         SpecialStructureRecognizer recorded with this instance of 
     *         Molecule object
     */ 
    public abstract Iterator<SpecialStructureRecognizer> 
                            getSpecialStructureRecognizers();
    
    /**
     * Method to return the instance of RingRecognizer connected to this
     * object, if any.
     *
     * @return a valid subclass of SpecialStructureRecognizer which recognizes
     *        rings; a null if no such recognizer is attached to this molecule
     *        object.
     */    
    public abstract SpecialStructureRecognizer getRingRecognizer();
    
    /**
     * Method to return the instance of a default SpecialStructureRecognizer
     * which does not do any automatic recognision of structures.
     *
     * @return a valid subclass of SpecialStructureRecognizer which does no
     *         automatic recognision of structures.
     */    
    public abstract 
            SpecialStructureRecognizer getDefaultSpecialStructureRecognizer();
    
    /**
     * Add a fragmentation scheme to this molecule object
     *
     * @param fragmentationScheme the FragmentationScheme to be added to this 
     *        molecule object.
     */
    public abstract void addFragmentationScheme(
                                    FragmentationScheme fragmentationScheme);
    
    /**
     * Remove a fragmentation scheme from this molecule object
     *
     * @param fragmentationScheme the FragmentationScheme to be removed from  
     *        this molecule object.
     */
    public abstract void removeFragmentationScheme(
                                    FragmentationScheme fragmentationScheme);
    
    /**
     * Returns a list of all the fragmentation schemes connected to this 
     * molecule object.
     *
     * @return an Iterator object contating a list of FragmentationScheme
     *         objects.
     */
    public abstract Iterator<FragmentationScheme> getAllFragmentationSchemes();
    
    /**
     * Returns the default implementation of FragmentationScheme
     *
     * @return an object contating default implementation of FragmentationScheme    
     */
    public abstract FragmentationScheme getDefaultFragmentationScheme();

    /**
     * The implementation of this method should update the coordinates
     * of exixting Atom objects which for this molecule. The new coordinates
     * are provided in a linear array, of size 3N, with the first three
     * indices representing coordinates for first atom as (x,y,z) and so on.
     * In case <code>updateConnectivity</code> is set to true, an appropriate
     * event should be fired, with the listener taking care of updating
     * the connectivity information.
     * The implementation of this method should also update fields
     * dependent on the atomic center such as center of
     * mass and bounding box etc.
     * 
     * @param coords the corrds with size 3N
     * @param updateConnectivity is the connectivity to be updated?
     * @throws IllegalArgumentException (a RuntimeException), if the array
     *         size does not match 3N.
     */
    public abstract void resetAtomCoordinates(double [] coords,
                  boolean updateConnectivity) throws IllegalArgumentException;

    /**
     * A way to indicate the implementation that all Atom objects are added
     * and now the Molecule object could be "cleansed" to minimize memory
     * usage.
     */
    public abstract void pack();

    /**
     * Get a canonical ordering of atoms as a new Molecule object. It is 
     * up to the implementation as to how this ordering is done.
     * Note: for the new Molecule object, the connectivity must be
     * explicitely build by the caller. 
     * 
     * @return a new Molecule object with different ordering
     */
    public abstract Molecule getCanonicalOrdering();

    /**
     * Do a simple reindexing of the atoms
     */
    public abstract void reIndexAtom();

    /**
     * method to enable all suppressed event listeners
     */
    public void enableListeners() {
        enableListeners = true;        
    }
    
    /**
     * method to suppress all event listeners
     */
    public void disableListeners() {
        enableListeners = false;
    }
    
    /** Registers MoleculeStateChangeListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addMoleculeStateChangeListener(
                                MoleculeStateChangeListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<MoleculeStateChangeListener>();
        }
        listenerList.add(MoleculeStateChangeListener.class, listener);
    }
    
    /** Removes MoleculeStateChangeListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeMoleculeStateChangeListener(
                                    MoleculeStateChangeListener listener) {
        listenerList.remove(MoleculeStateChangeListener.class, listener);
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     *
     */
    protected void fireMoleculeStateChangeListenerMoleculeChanged(
                                    MoleculeStateChangeEvent event) {
        if (!enableListeners) return;
        if (listenerList == null) return;        
        
        for(Object listener : listenerList.getListenerList()) {
            ((MoleculeStateChangeListener)listener).moleculeChanged(event);
        } // end for        
    }
    
    /**
     * overridden toString()
     */
    @Override
    public String toString() {
        return "Molecule " + getTitle();
    }
    
    /**
     * extended description of Molecule class
     */
    public String toExtendedString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Molecule Name           : ").append(getTitle()).append("\n");
        sb.append("Number of atoms         : ").append(getNumberOfAtoms())
                .append("\n");
        sb.append("Molecular formula       : ").append(getFormula().toString())
                .append("\n");
        sb.append("Number of electrons     : ").append(getNumberOfElectrons())
                .append("\n");
        sb.append("Molecular mass          : ").append(new DecimalFormat("#.###")
                .format(getMolecularMass())).append(" a.m.u \n");
        sb.append("Number of single bonds  : ").append(getNumberOfSingleBonds())
                .append("\n");
        sb.append("Number of multiple bonds: ").append(getNumberOfMultipleBonds())
                .append("\n");
        sb.append("Number of weak bonds    : ").append(getNumberOfWeakBonds())
                .append("\n");
        sb.append("Center of mass          : ").append(getCenterOfMass())
                .append("\n");
        sb.append("Bounding box            : ").append(getBoundingBox())
                .append("\n");
        
        if (isAdditionalInformationAvailable()) {
            AdditionalInformation ai = getAdditionalInformation();
            
            if (ai != null) { // just to be extra sure!
                if (ai.isEnergyAvailable()) {
                  sb.append("Energy                  : ").append(ai.getEnergy())
                        .append("\n");
                }
                
                if (ai.isZpEnergyAvailable()) { 
                  sb.append("ZP Correction           : ").append(ai.getZpEnergy())
                        .append("\n");
                }
                
                if (ai.isDipoleAvailable()) {
                  sb.append("Dipole Moment           : ").append(ai.getDipole().toString())
                        .append("\n");                
                  sb.append("Total Dipole Moment     : ").append(ai.getDipole().magnitude())
                        .append("\n");                
                }
            } // end if
        } // end if
        
        return sb.toString();
    }
    
    /**
     * Initialize the bond related metrics.
     */
    public void initBondMetrics() {
        numberOfSingleBonds = numberOfMultipleBonds = numberOfWeakBonds = 0;
    }
    
    /**
     * Getter for property numberOfSingleBonds.
     * @return Value of property numberOfSingleBonds.
     */
    public int getNumberOfSingleBonds() {
        return this.numberOfSingleBonds;
    }

    /**
     * Setter for property numberOfSingleBonds.
     * @param numberOfSingleBonds New value of property numberOfSingleBonds.
     */
    public void setNumberOfSingleBonds(int numberOfSingleBonds) {
        this.numberOfSingleBonds = numberOfSingleBonds;
    }
    
    /**
     * increment the metrics <code> numberOfSingleBonds </code>
     */
    public void incrementNumberOfSingleBonds() {
        this.numberOfSingleBonds++;
    }
    
    /**
     * decrement the metrics <code> numberOfSingleBonds </code>
     */
    public void decrementNumberOfSingleBonds() {
        if (this.numberOfSingleBonds > 0) {
           this.numberOfSingleBonds--;
        } // end if
    }
    
    /**
     * Getter for property numberOfMultipleBonds.
     * @return Value of property numberOfMultipleBonds.
     */
    public int getNumberOfMultipleBonds() {
        return this.numberOfMultipleBonds;
    }
    
    /**
     * Setter for property numberOfMultipleBonds.
     * @param numberOfMultipleBonds New value of property numberOfMultipleBonds.
     */
    public void setNumberOfMultipleBonds(int numberOfMultipleBonds) {
        this.numberOfMultipleBonds = numberOfMultipleBonds;
    }
    
    /**
     * increment the metrics <code> numberOfMultipleBonds </code>
     */
    public void incrementNumberOfMultipleBonds() {
        this.numberOfMultipleBonds++;
    }
    
    /**
     * decrement the metrics <code> numberOfMultipleBonds </code>
     */
    public void decrementNumberOfMultipleBonds() {
        if (this.numberOfMultipleBonds > 0) {
           this.numberOfMultipleBonds--;
        } // end if
    }
    
    /**
     * Getter for property numberOfWeakBonds.
     * @return Value of property numberOfWeakBonds.
     */
    public int getNumberOfWeakBonds() {
        return this.numberOfWeakBonds;
    }
    
    /**
     * Setter for property numberOfWeakBonds.
     * @param numberOfWeakBonds New value of property numberOfWeakBonds.
     */
    public void setNumberOfWeakBonds(int numberOfWeakBonds) {
        this.numberOfWeakBonds = numberOfWeakBonds;
    }
    
    /**
     * increment the metrics <code> numberOfWeakBonds </code>
     */
    public void incrementNumberOfWeakBonds() {
        this.numberOfWeakBonds++;
    }
    
    /**
     * decrement the metrics <code> numberOfWeakBonds </code>
     */
    public void decrementNumberOfWeakBonds() {
        if (this.numberOfWeakBonds > 0) {
           this.numberOfWeakBonds--;
        } // end if
    }
    
    /**
     * Getter for property molecularMass.
     * @return Value of property molecularMass.
     */
    public double getMolecularMass() {
        return this.molecularMass;
    }
       
    /**
     * Getter for property numberOfStrongBonds.
     * @return Value of property numberOfStrongBonds.
     */
    public int getNumberOfStrongBonds() {
        return (numberOfSingleBonds + numberOfMultipleBonds);
    }   
    
    /**
     * Getter for property numberOfElectrons.
     * @return Value of property numberOfElectrons.
     */
    public int getNumberOfElectrons() {
        return this.numberOfElectrons;
    }

    /**
     * Holds value of property zMatrixComputed.
     */
    protected boolean zMatrixComputed;

    /**
     * Getter for property zMatrixComputed.
     * @return Value of property zMatrixComputed.
     */
    public boolean isZMatrixComputed() {
        return this.zMatrixComputed;
    }

    /**
     * Setter for property zMatrixComputed.
     * @param zMatrixComputed New value of property zMatrixComputed.
     */
    public void setZMatrixComputed(boolean zMatrixComputed) {
        this.zMatrixComputed = zMatrixComputed;
    }    

    /**
     * Sets the value for a common user defined property
     *
     * @param molProp the property
     * @param value and its value
     */
    public void setCommonUserDefinedProperty(
          CommonUserDefinedMolecularPropertyNames molProp, Serializable value) {
        if (additionalInformation == null) {
            additionalInformation = new AdditionalInformation();
            additionalInformationAvailable = true;
        } // end if

        UserDefinedMolecularProperty udmp = 
          additionalInformation.getUserDefinedMolecularProperty(molProp.toString());

        if (udmp == null) {
            udmp = new UserDefinedMolecularProperty(molProp.toString(), value);
            additionalInformation.addUserDefinedMolecularProperty(udmp);
        } // end if

        udmp.setValue(value);
    }

    /**
     * Get the value for a common user defined property
     *
     * @param molProp the property
     * @return the value of the this property, null if non exists
     */
    public Object getCommonUserDefinedProperty(
            CommonUserDefinedMolecularPropertyNames molProp) {
        if (!additionalInformationAvailable) return false;

        try {
            return additionalInformation.getUserDefinedMolecularProperty(
                                                 molProp.toString()).getValue();
        } catch(Exception err) { return false; }
    }
    
    /**
     * information additional to a Molecule object
     */
    public static final class AdditionalInformation {
        public AdditionalInformation() { 
            energy = 0.0;
            zpEnergy = 0.0;
            rmsGradient = 0.0;
            maxGradient = 0.0;

            readConnectivity = false;
            pdb = false;
            energyAvailable   = false;
            zpEnergyAvailable = false;
            gradientAvailable = false;
            dipoleAvailable   = false;
            frequencyDataAvailable  = false;
            volumetricDataAvailable = false;
            
            level = "none";
            basis = "none";

            energyUnit    = "a.u.";
            gradientUnit  = "a.u.";
            dipoleUnit    = "Debye";
            frequencyUnit = "cm-1";
        }

        /**
         * Holds value of property energy.
         */
        private double energy;

        /**
         * Getter for property energy.
         * @return Value of property energy.
         */
        public double getEnergy() {
            return this.energy;
        }

        /**
         * Setter for property energy.
         * @param energy New value of property energy.
         */
        public void setEnergy(double energy) {
            this.energy = energy;
        }

        /**
         * Holds value of property energy.
         */
        private double zpEnergy;

        /**
         * Getter for property zpEnergy.
         * @return Value of property zpEnergy.
         */
        public double getZpEnergy() {
            return this.zpEnergy;
        }

        /**
         * Setter for property zpEnergy.
         * @param zpEnergy New value of property zpEnergy.
         */
        public void setZpEnergy(double zpEnergy) {
            this.zpEnergy = zpEnergy;
        }
        
        /**
         * Holds value of property rmsGradient.
         */
        private double rmsGradient;

        /**
         * Getter for property rmsGradient.
         * @return Value of property rmsGradient.
         */
        public double getRmsGradient() {
            return this.rmsGradient;
        }

        /**
         * Setter for property rmsGradient.
         * @param rmsGradient New value of property rmsGradient.
         */
        public void setRmsGradient(double rmsGradient) {
            this.rmsGradient = rmsGradient;
        }

        /**
         * Holds value of property maxGradient.
         */
        private double maxGradient;

        /**
         * Getter for property maxGradient.
         * @return Value of property maxGradient.
         */
        public double getMaxGradient() {
            return this.maxGradient;
        }

        /**
         * Setter for property maxGradient.
         * @param maxGradient New value of property maxGradient.
         */
        public void setMaxGradient(double maxGradient) {
            this.maxGradient = maxGradient;
        }

        /**
         * true if the connectivity information was read from the molecule file
         */
        private boolean readConnectivity;

        /**
         * @return true if the connectivity information was read from the molecule file
         */
        public boolean isReadConnectivity() {
            return readConnectivity;
        }

        public void setReadConnectivity(boolean readConnectivity) {
            this.readConnectivity = readConnectivity;
        }

        /**
         * Holds value of property pdb.
         */
        private boolean pdb;

        /**
         * Getter for property pdb.
         * @return Value of property pdb.
         */
        public boolean isPdb() {
            return this.pdb;
        }

        /**
         * Setter for property pdb.
         * @param pdb New value of property pdb.
         */
        public void setPdb(boolean pdb) {
            this.pdb = pdb;
        }

        /**
         * Holds value of property level.
         */
        private String level;

        /**
         * Getter for property level.
         * @return Value of property level.
         */
        public String getLevel() {
            return this.level;
        }

        /**
         * Setter for property level.
         * @param level New value of property level.
         */
        public void setLevel(String level) {
            this.level = level;
        }

        /**
         * Holds value of property basis.
         */
        private String basis;

        /**
         * Getter for property basis.
         * @return Value of property basis.
         */
        public String getBasis() {
            return this.basis;
        }

        /**
         * Setter for property basis.
         * @param basis New value of property basis.
         */
        public void setBasis(String basis) {
            this.basis = basis;
        }

        /**
         * Holds value of property frequencyDataAvailable.
         */
        private boolean frequencyDataAvailable;

        /**
         * Getter for property frequencyDataAvailable.
         * @return Value of property frequencyDataAvailable.
         */
        public boolean isFrequencyDataAvailable() {
            return this.frequencyDataAvailable;
        }

        /**
         * Setter for property frequencyDataAvailable.
         * @param frequencyDataAvailable New value of property 
         *                               frequencyDataAvailable.
         */
        public void setFrequencyDataAvailable(boolean frequencyDataAvailable) {
            this.frequencyDataAvailable = frequencyDataAvailable;
        }

        protected MolecularVibrationalProperty molecularVibrationalProperty;

        /**
         * Get the value of molecularVibrationalProperty
         *
         * @return the value of molecularVibrationalProperty
         */
        public MolecularVibrationalProperty getMolecularVibrationalProperty() {
            return molecularVibrationalProperty;
        }

        /**
         * Set the value of molecularVibrationalProperty
         *
         * @param molecularVibrationalProperty new value of
         *                          molecularVibrationalProperty
         */
        public void setMolecularVibrationalProperty(
                    MolecularVibrationalProperty molecularVibrationalProperty) {
            this.molecularVibrationalProperty = molecularVibrationalProperty;
        }

        protected MolecularVolumetricProperty molecularVolumetricProperty;

        /**
         * Get the value of molecularVolumetricProperty
         *
         * @return the value of molecularVolumetricProperty
         */
        public MolecularVolumetricProperty getMolecularVolumetricProperty() {
            return molecularVolumetricProperty;
        }

        /**
         * Set the value of molecularVolumetricProperty
         *
         * @param molecularVolumetricProperty new value of
         *            molecularVolumetricProperty
         */
        public void setMolecularVolumetricProperty(
                      MolecularVolumetricProperty molecularVolumetricProperty) {
            this.molecularVolumetricProperty = molecularVolumetricProperty;
        }

        /**
         * Holds value of property volumetricDataAvailable.
         */
        private boolean volumetricDataAvailable;

        /**
         * Getter for property volumetricDataAvailable.
         * @return Value of property volumetricDataAvailable.
         */
        public boolean isVolumetricDataAvailable() {
            return this.volumetricDataAvailable;
        }

        /**
         * Setter for property volumetricDataAvailable.
         * @param volumetricDataAvailable New value of property 
         *                                volumetricDataAvailable.
         */
        public void setVolumetricDataAvailable(boolean volumetricDataAvailable){
            this.volumetricDataAvailable = volumetricDataAvailable;
        }

        /**
         * Holds value of property dipole.
         */
        private Vector3D dipole;

        /**
         * Getter for property dipole.
         * @return Value of property dipole.
         */
        public Vector3D getDipole() {
            return this.dipole;
        }

        /**
         * Setter for property dipole.
         * @param dipole New value of property dipole.
         */
        public void setDipole(Vector3D dipole) {
            this.dipole = dipole;
        }


        /**
         * Holds the property energyAvailable
         */
        private boolean energyAvailable;

        /**
         * Get the value of energyAvailable
         *
         * @return the value of energyAvailable
         */
        public boolean isEnergyAvailable() {
            return energyAvailable;
        }

        /**
         * Set the value of energyAvailable
         *
         * @param energyAvailable new value of energyAvailable
         */
        public void setEnergyAvailable(boolean energyAvailable) {
            this.energyAvailable = energyAvailable;
        }

        /**
         * Holds the property energyAvailable
         */
        private boolean zpEnergyAvailable;

        /**
         * Get the value of zpEnergyAvailable
         *
         * @return the value of zpEnergyAvailable
         */
        public boolean isZpEnergyAvailable() {
            return zpEnergyAvailable;
        }

        /**
         * Set the value of zpEnergyAvailable
         *
         * @param zpEnergyAvailable new value of zpEnergyAvailable
         */
        public void setZpEnergyAvailable(boolean zpEnergyAvailable) {
            this.zpEnergyAvailable = zpEnergyAvailable;
        }
        
        /**
         * Holds the property gradientAvailable
         */
        private boolean gradientAvailable;

        /**
         * Get the value of gradientAvailable
         *
         * @return the value of gradientAvailable
         */
        public boolean isGradientAvailable() {
            return gradientAvailable;
        }

        /**
         * Set the value of gradientAvailable
         *
         * @param gradientAvailable new value of gradientAvailable
         */
        public void setGradientAvailable(boolean gradientAvailable) {
            this.gradientAvailable = gradientAvailable;
        }

        /**
         * Holds the property dipoleAvailable
         */
        private boolean dipoleAvailable;

        /**
         * Get the value of dipoleAvailable
         *
         * @return the value of dipoleAvailable
         */
        public boolean isDipoleAvailable() {
            return dipoleAvailable;
        }

        /**
         * Set the value of dipoleAvailable
         *
         * @param dipoleAvailable new value of dipoleAvailable
         */
        public void setDipoleAvailable(boolean dipoleAvailable) {
            this.dipoleAvailable = dipoleAvailable;
        }

        
        /**
         * Holds the property energyUnit
         */
        private String energyUnit;

        /**
         * Get the value of energyUnit
         *
         * @return the value of energyUnit
         */
        public String getEnergyUnit() {
            return energyUnit;
        }

        /**
         * Set the value of energyUnit
         *
         * @param energyUnit new value of energyUnit
         */
        public void setEnergyUnit(String energyUnit) {
            this.energyUnit = energyUnit;
        }

        /**
         * Holds the property gradientUnit
         */
        private String gradientUnit;

        /**
         * Get the value of gradientUnit
         *
         * @return the value of gradientUnit
         */
        public String getGradientUnit() {
            return gradientUnit;
        }

        /**
         * Set the value of gradientUnit
         *
         * @param gradientUnit new value of gradientUnit
         */
        public void setGradientUnit(String gradientUnit) {
            this.gradientUnit = gradientUnit;
        }

        /**
         * Holds the property dipoleUnit
         */
        private String dipoleUnit;

        /**
         * Get the value of dipoleUnit
         *
         * @return the value of dipoleUnit
         */
        public String getDipoleUnit() {
            return dipoleUnit;
        }

        /**
         * Set the value of dipoleUnit
         *
         * @param dipoleUnit new value of dipoleUnit
         */
        public void setDipoleUnit(String dipoleUnit) {
            this.dipoleUnit = dipoleUnit;
        }

        /**
         * Holds the property frequencyUnit
         */
        private String frequencyUnit;

        /**
         * Get the value of frequencyUnit
         *
         * @return the value of frequencyUnit
         */
        public String getFrequencyUnit() {
            return frequencyUnit;
        }

        /**
         * Set the value of frequencyUnit
         *
         * @param frequencyUnit new value of frequencyUnit
         */
        public void setFrequencyUnit(String frequencyUnit) {
            this.frequencyUnit = frequencyUnit;
        }

        private ArrayList<UserDefinedMolecularProperty> userProperties;

        /**
         * Add a user defined molecular property.
         *
         * @param uProp the new instance of property to be added
         * @throws UnsupportedOperationException if a property with the same
         *         name already exists
         */
        public void addUserDefinedMolecularProperty(
                           UserDefinedMolecularProperty uProp) {
            if (userProperties == null)
                userProperties = new ArrayList<UserDefinedMolecularProperty>();

            // first check if this property already exists
            String name = uProp.getName();
            for (UserDefinedMolecularProperty uprop : userProperties) {
                if (uprop.getName().equals(name)) {
                    throw new UnsupportedOperationException("Property with name" 
                       + " '" + name + "' is already defined for this object!");
                } // end if
            } // end for

            userProperties.add(uProp);
        }

        /**
         * Remove a user defined property.
         *
         * @param uProp instance of UserDefinedMolecularProperty to be removed
         */
        public void removeUserDefinedMolecularProperty(
                              UserDefinedMolecularProperty uProp) {
           if (userProperties == null) return;

           userProperties.remove(uProp);
        }

        /**
         * Get a list of all the user defined molecular properties, null if
         * no user defined properties were ever added to this Molecule object.
         *
         * @return an Iterator object of list of instances of
         *              UserDefinedMolecularProperty
         */
        public Iterator<UserDefinedMolecularProperty>
                            getUserDefinedMolecularProperty() {
            if (userProperties == null) return null;

            return userProperties.iterator();
        }

        /**
         * Get a user defined molecular properties with the specified name, null
         * if no user defined properties with the specified name exists.
         *
         * @param name the name of the requested UserDefinedMolecularProperty
         * @return an instances of UserDefinedMolecularProperty
         */
        public UserDefinedMolecularProperty
                            getUserDefinedMolecularProperty(String name) {
            if (userProperties == null) return null;

            for(UserDefinedMolecularProperty uprop : userProperties) 
                if (uprop.getName().equals(name)) return uprop;

            return null;
        }
    } // end of inner class AdditionalInformation
    /**
     * Holds value of property additionalInformationAvailable.
     */
    protected boolean additionalInformationAvailable;

    /**
     * Getter for property additionalInformationAvailable.
     * @return Value of property additionalInformationAvailable.
     */
    public boolean isAdditionalInformationAvailable() {
        return this.additionalInformationAvailable;
    }

    /**
     * Setter for property additionalInformationAvailable.
     * @param additionalInformationAvailable New value of property 
     *        additionalInformationAvailable.
     */
    public void setAdditionalInformationAvailable(
                                       boolean additionalInformationAvailable) {
        this.additionalInformationAvailable = additionalInformationAvailable;
    }

    /**
     * Holds value of property additionalInformation.
     */
    protected AdditionalInformation additionalInformation;

    /**
     * Getter for property additionalInformation.
     * @return Value of property additionalInformation.
     */
    public AdditionalInformation getAdditionalInformation() {
        return this.additionalInformation;
    }

    /**
     * Setter for property additionalInformation.
     * @param additionalInformation New value of property additionalInformation.
     */
    public void setAdditionalInformation(
                                  AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        this.additionalInformationAvailable = true;
    }

    /**
     * Holds value of property centerOfMass.
     */
    protected Point3D centerOfMass;

    /**
     * Getter for property centerOfMass.
     * @return Value of property centerOfMass.
     */
    public Point3D getCenterOfMass() {
        return this.centerOfMass.mul(1.0/getMolecularMass());
    }

    /**
     * Setter for property centerOfMass.
     * @param centerOfMass New value of property centerOfMass.
     */
    public void setCenterOfMass(Point3D centerOfMass) {
        this.centerOfMass = centerOfMass;
    }
    
} // end of interface Molecule
