/*
 * MoleculeBuilder.java
 *
 * Created on May 10, 2003, 11:10 PM
 */

package org.meta.molecule;

import java.util.List;
import org.meta.common.EventListenerList;
import org.meta.molecule.event.MoleculeBuildEvent;
import org.meta.molecule.event.MoleculeBuildListener;
import org.meta.molecule.event.MoleculeStateChangeEvent;

/**
 * This abstract class defines the methods that an implementation of 
 * MoleculeBuilder should support.
 * The interface provides the basic factory based framework for processing
 * the Molecule object with bond info. and additional special structures 
 * (like rings, functional groups, user defined groups etc).
 *
 * Note: An implementation of this class can also use 
 * CommonUserDefinedMolecularProperty or Molecule.AdditionalMolecularProperty
 * to make custom runtime decisions.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class MoleculeBuilder {
    
    /** Utility field used by event firing mechanism. */
    private EventListenerList<MoleculeBuildListener> listenerList =  null;
    
    /** Creates a new instance of MoleculeBuilder */
    public MoleculeBuilder() {
    }
    
    /**
     * The implementation of this method should write the best possible
     * algorithms for identifying the required type of bonds. Also the
     * implementation should *not* worry about how the connectivity is
     * to be stored, this is taken care of by the implementors of
     * Molecule class.
     *
     * @param molecule The instance of the Molecule class which needs to be 
     *        processed for bonding information.
     */    
    public abstract void makeConnectivity(Molecule molecule);

    /**
     * The implementation of this method should write the best possible
     * algorithms for identifying the required type of bonds. Also the
     * implementation should *not* worry about how the connectivity is
     * to be stored, this is taken care of by the implementors of
     * Molecule class. <br>
     *
     * This method, behaves similar to simple <code>makeConnectivity(molecule)
     * </code> except that the attempt to build connectivity is only made for
     * the atoms indices supplied. If the atomIndices provided in this list
     * do not match the once in the Molecule object, they should be
     * ignored silently.
     *
     * @param molecule The instance of the Molecule class which needs to be
     *        processed for bonding information.
     */
    public abstract void makeConnectivity(Molecule molecule,
                                          List<Integer> atomIndices);
    
    /**
     * The implementation of this method should write the best possible
     * algorithms for identifying the required simple bonds as fast as possible. 
     * Also the implementation should *not* worry about how the connectivity is
     * to be stored, this is taken care of by the implementors of
     * Molecule class.
     *
     * @param molecule The instance of the Molecule class which needs to be 
     *        processed for bonding information.
     */    
    public abstract void makeSimpleConnectivity(Molecule molecule);
    
    /**
     * The implementation of this method should generate a ZMatrix for the
     * molecule object, in such a way that allows geometry optimization of
     * the molecule.
     *
     * @param molecule The instance of the Molecule class which needs to be 
     *        processed for building the ZMatrix.
     */
    public abstract void makeZMatrix(Molecule molecule);
    
    /**
     * The implementation of this method should write the best possible 
     * algorithm to "refresh" the connectivity of a Molecule object, i.e.
     * the source of MoleculeStateChangeEvent.
     *
     * @param msce - the MoleculeStateChangeEvent and hence the Molecule
     *        object that has changed as a result.
     */
    public abstract void rebuildConnectivity(MoleculeStateChangeEvent msce);
    
    /**
     * The implementation of this structure should preferablly use the
     * list of SpecialStructureRecognizer's and delagte the job to these
     * implementations.
     *
     * @param molecule The molecule object instance in which the special 
     * structures are to be recognized.
     */    
    public abstract void identifySpecialStructures(Molecule molecule);        

    /**
     * The implementation of this method should try and detect all weakly
     * bonded interactions.
     *
     * @param molecule The molecule object instance in which the weak
     * interactions are to be identified.
     */
    public abstract void identifyWeakBonds(Molecule molecule);

    /**
     * The implementation of this method should try and detect all multiply
     * bonded interaction.
     *
     * @param molecule The molecule object instance in which presence of
     * multiple bonds are to be detected.
     */
    public abstract void identifyMultipleBonds(Molecule molecule);

    /**
     * A method that calculates if a bond can between two atoms. 
     * Additional checks may be made, such as check default valency etc.
     * 
     * @param a1 first atom
     * @param a2 second atom
     * @param bondtype the expected bondtype
     * @return a boolean indiacting if a bond can be formed
     */
    public abstract boolean canFormBond(Atom a1, Atom a2, BondType bondtype);
    
    /**
     * Some times it may be needed to add hydrogens to a molecule automatically,
     * typically the case with PDB files. This simple interface defines a way to
     * add hydrogens to an existing Molecule object 
     * 
     * @param molecule the Molecule object to which hydrogen atoms is
     *                 to be added
     */
    public abstract void addHydrogens(Molecule molecule);

    /** 
     * Registers MoleculeBuildListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addMoleculeBuildListener(
                                            MoleculeBuildListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<MoleculeBuildListener>();
        }
        listenerList.add(MoleculeBuildListener.class, listener);
    }
    
    /** 
     * Removes MoleculeBuildListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeMoleculeBuildListener(
                                            MoleculeBuildListener listener) {
        listenerList.remove(MoleculeBuildListener.class, listener);
    }
    
    /** 
     * Notifies all registered listeners about the event.
     *
     * @param evt The event to be fired
     */
    protected void fireMoleculeBuildListenerBuildEvent(MoleculeBuildEvent evt) {
        if (listenerList == null) return;
        
        for(Object listener : listenerList.getListenerList()) {
            ((MoleculeBuildListener)listener).buildEvent(evt);
        }
    }
    
} // end of class MoleculeBuilder
