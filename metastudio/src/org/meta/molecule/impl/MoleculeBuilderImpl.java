/*
 * MoleculeBuilderImpl.java
 *
 * Created on November 30, 2003, 1:57 PM
 */

package org.meta.molecule.impl;

import java.util.*;
import org.meta.math.geom.Point3D;

import org.meta.config.impl.AtomInfo;
import org.meta.math.MathUtil;
import org.meta.math.Vector3D;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.CommonUserDefinedMolecularPropertyNames;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.molecule.SpecialStructureRecognizer;
import org.meta.molecule.ZMatrixItem;
import org.meta.molecule.event.MoleculeBuildEvent;
import org.meta.molecule.event.MoleculeStateChangeEvent;

/**
 * The default implementation of class MoleculeBuilder.
 * 
 * Note: The methods in this class are not thread safe, and hence it is best
 * to use individual objects of this class in separate threads.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeBuilderImpl extends MoleculeBuilder {        
    
    /** default tolerance percentage for presence of a double bond */
    public static final double DOUBLE_BOND_OVERLAP_PERCENTAGE = 0.92; // 92 %
    
    /** tolerance percentage for presence of a triple bond */
    public static final double TRIPLE_BOND_OVERLAP_PERCENTAGE = 0.70; // 70 %        
    
    /** tolerance for checking for presence of covalent bonds */
    public static final double COVALENT_BOND_TOLERANCE = 0.4; // angstroms
    
    /** the upper tolerance factor for presence of a weak bond */
    public static final double WEAK_BOND_TOLERANCE_UPPER = 1.05; // angstroms
    
    /** the lower tolerance factor for presence of a weak bond */
    public static final double WEAK_BOND_TOLERANCE_LOWER = 0.1; // angstroms
    
    /** weak bond axis should be having value greater than this */
    public static final double WEAK_BOND_AXIS_REJECTION_FACTOR = 0.1;     
    
    /** The bond formation tolerance */
    public static final double BOND_RADIUS_CHECK = 4.0; // angstroms
    
    /** other private variables */
    
    protected AtomInfo atomInfo;
    protected Molecule molecule; // temporay cache!
    
    protected double [] covalentRadius, vdwRadius, 
                        weakBondAngle, dblBndOverlaps;
    
    private double x, y, z;
    private double distance;
    private double vdwRadiusSum, covalentRadiusSum, doubleBondOverlap;
            
    // needed for isOrientedProperly()
    private double angle1, angle2;
    private Atom atom1, atom2;  
    private Vector3D axis1, axis2, vectorFrom1To2, vectorFrom2To1;
    
    protected MoleculeBuildEvent mbEvent;        
    
    /** Creates a new instance of MoleculeBuilderImpl */
    public MoleculeBuilderImpl() {
        atomInfo = AtomInfo.getInstance();
        
        mbEvent  = new MoleculeBuildEvent(this, 0.0);
    }        
    
    /**
     * The implementation of this structure should preferably use the
     * list of SpecialStructureRecognizer's and delegate the job to these
     * implementations.
     *
     * @param molecule The molecule object instance in which the special 
     * structures are to be recognized.
     */    
    @Override
    public void identifySpecialStructures(Molecule molecule) {        
       Iterator ssrs = molecule.getSpecialStructureRecognizers();
       SpecialStructureRecognizer ssr;
       
       while(ssrs.hasNext()) {
           ssr = (SpecialStructureRecognizer) ssrs.next();
           
           ssr.recognizeAndRecord(molecule);
       } // end while

       // marker of what was identified
       molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.SPECIAL_STRUCTURE_DETECTED,
           true);
    }
    
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
    @Override
    public void makeSimpleConnectivity(Molecule molecule) {
        boolean readConnectivity = molecule.isAdditionalInformationAvailable()
                 && molecule.getAdditionalInformation().isReadConnectivity();
        
        if (readConnectivity) {
            // already read connectivity information from file, no nead to build
            return;
        } // end if
        
        this.molecule = molecule;
        
        int noOfAtoms = molecule.getNumberOfAtoms();
        
        molecule.disableListeners(); // disable listeners
        
        // init bond metrices
        molecule.initBondMetrics();
        
        // first cache the covalent and vdw radius, along with the info
        // on default valency and weakbond angles, needed for optimizing 
        // bond detection and finding presence of weak bonds respectively.  
        covalentRadius = new double[noOfAtoms];
        
        int i, j;        
        String symbol;
        
        for(i=0; i<noOfAtoms; i++) {
            symbol = molecule.getAtom(i).getSymbol();
            
            covalentRadius[i] = atomInfo.getCovalentRadius(symbol);            
        } // end for
        
        // now since we have cached the entries, we run the loop over
        // the atoms to identify their bonding info
        Point3D atomCenter1, atomCenter2;
        Atom a1, a2;
        
        for(i=0; i<noOfAtoms; i++) {
            a1 = molecule.getAtom(i);
            if (a1.isVector()) continue;
            atomCenter1 = a1.getAtomCenter();            
            
            // >>> This is the place where defaultValency needs to be checked
            // >>> for a1
            for(j=0; j<i; j++) {
                a2 = molecule.getAtom(j);
                if (a2.isVector()) continue;
                atomCenter2 = a2.getAtomCenter();                
                
                // we only identify single bonds here
                if (canFormBond(atomCenter1, atomCenter2)) {
                    covalentRadiusSum = covalentRadius[i] + covalentRadius[j];
                    
                    if (isSingleBondPresent()) {
                        molecule.setBondType(i, j, BondType.SINGLE_BOND);
                        molecule.incrementNumberOfSingleBonds();
                    } // end if
                } // end if
            } // end for
        } // end for                
        
        // try to free up some memory
        covalentRadius = null;        
        System.gc();

        // put a marker on what is detected
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.SIMPLE_BOND_DETECTED, true);
        
        molecule.enableListeners(); // enable the listeners
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
    @Override
    public void makeConnectivity(Molecule molecule) {  
        boolean readConnectivity = molecule.isAdditionalInformationAvailable()
                && molecule.getAdditionalInformation().isReadConnectivity();
        
        if (readConnectivity) {
            // already read connectivity information from file, no nead to build
            return;
        } // end if

        this.molecule = molecule;

        // see if the molecule object is presented with any
        // processing directives, if so process them instead
        if (preProcessMolecule(molecule)) return;
        
        int noOfAtoms = molecule.getNumberOfAtoms();
        
        molecule.disableListeners(); // disable listeners
        
        // init bond metrices
        molecule.initBondMetrics();
        
        // first cache the covalent and vdw radius, along with the info
        // on default valency and weakbond angles, needed for optimizing 
        // bond detection and finding presence of weak bonds respectively.  
        covalentRadius = new double[noOfAtoms];
        vdwRadius      = new double[noOfAtoms];
        weakBondAngle  = new double[noOfAtoms];
        dblBndOverlaps = new double[noOfAtoms];        
        
        int i, j;        
        String symbol;
        
        for(i=0; i<noOfAtoms; i++) {
            symbol = molecule.getAtom(i).getSymbol();
            
            covalentRadius[i] = atomInfo.getCovalentRadius(symbol);
            vdwRadius[i]      = atomInfo.getVdwRadius(symbol);
            weakBondAngle[i]  = atomInfo.getWeakBondAngle(symbol);
            dblBndOverlaps[i] = atomInfo.getDoubleBondOverlap(symbol);
        } // end for
        
        mbEvent.setEventDescription("Done with initial setup.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.05);
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        
        // now since we have cached the entries, we run the loop over
        // the atoms to identify their bonding info
        Point3D atomCenter1, atomCenter2;
        Atom a1, a2;
        
        for(i=0; i<noOfAtoms; i++) {
            a1 = molecule.getAtom(i);
            if (a1.isVector() || a1.isDummy()) continue;
            atomCenter1 = a1.getAtomCenter();            
            
            // >>> This is the place where defaultValency needs to be checked
            // >>> for a1
            for(j=0; j<i; j++) {
                a2 = molecule.getAtom(j);
                if (a2.isVector() || a2.isDummy()) continue;
                atomCenter2 = a2.getAtomCenter();                
                
                // the first level of defence for checking the existance 
                // of bond between two atom centers
                if (canFormBond(atomCenter1, atomCenter2)) {
                    // if so then classify the bonds..
                    // first check for weak interactions
                    vdwRadiusSum = vdwRadius[i] + vdwRadius[j];
                    covalentRadiusSum = covalentRadius[i] + covalentRadius[j];
                    if (isWeekBondPresent() && (!isSingleBondPresent())) { // weak bond?
                      molecule.setBondType(i, j, BondType.WEAK_BOND);
                    } else {
                      if (isSingleBondPresent()) { 
                          doubleBondOverlap = 
                            ((dblBndOverlaps[i] == 0.0) ? 
                                 (dblBndOverlaps[j] == 0.0 ? 
                                     DOUBLE_BOND_OVERLAP_PERCENTAGE
                                     : dblBndOverlaps[j])
                                 : dblBndOverlaps[i]);
                                 
                          if (!(a1.getSymbol().equals("H") 
                                 || a2.getSymbol().equals("H")) 
                              && isDoubleBondPresent()) {
                             if (isTripleBondPresent()) { // triple bond
                               molecule.setBondType(i, j, BondType.TRIPLE_BOND);
                               molecule.incrementNumberOfMultipleBonds();
                             } else { // double bond
                               molecule.setBondType(i, j, BondType.DOUBLE_BOND);
                               molecule.incrementNumberOfMultipleBonds();
                             } // end if
                          } else { // single bond
                              molecule.setBondType(i, j, BondType.SINGLE_BOND);
                              molecule.incrementNumberOfSingleBonds();
                          } // end if
                      } // end if
                    } // end if
                } // end if
            } // end for
        } // end for
        
        mbEvent.setEventDescription("Connectivity build.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.75);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // put a marker on what is detected
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.SIMPLE_BOND_DETECTED, true);
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.MULTIPLE_BOND_DETECTED, true);
        
        // try to free up some memory
        covalentRadius = vdwRadius = dblBndOverlaps = null;        
        System.gc();
        
        // confirm the presence of weak bonds        
        confirmWeakBonds();
        // again try to grab some space        
        weakBondAngle = null;
        System.gc();
        
        molecule.enableListeners(); // enable the listeners
        
        mbEvent.setEventDescription("Verified connectivity. "
                                     + "Building connectivity over.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(1.0);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // indicate the connectivity is fully formed
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.PARTIAL_CONNECTIVITY_FORMED,
           false);
    }        

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
    @Override
    public void makeConnectivity(Molecule molecule, List<Integer> atomIndices) {
        this.molecule = molecule;

        int noOfAtoms = molecule.getNumberOfAtoms();
        int noOfAtomsToFormConnectivity = atomIndices.size();
        
        molecule.disableListeners(); // disable listeners

        // first cache the covalent and vdw radius, along with the info
        // on default valency and weakbond angles, needed for optimizing
        // bond detection and finding presence of weak bonds respectively.
        covalentRadius = new double[noOfAtomsToFormConnectivity];
        vdwRadius      = new double[noOfAtomsToFormConnectivity];
        weakBondAngle  = new double[noOfAtomsToFormConnectivity];
        dblBndOverlaps = new double[noOfAtomsToFormConnectivity];
        
        String symbol;

        for(Integer atomIndex : atomIndices) {
            try {
                symbol = molecule.getAtom(atomIndex).getSymbol();

                covalentRadius[atomIndex] = atomInfo.getCovalentRadius(symbol);
                vdwRadius[atomIndex]      = atomInfo.getVdwRadius(symbol);
                weakBondAngle[atomIndex]  = atomInfo.getWeakBondAngle(symbol);
                dblBndOverlaps[atomIndex] = atomInfo.getDoubleBondOverlap(symbol);
            } catch(Exception ignored) { }
        } // end for

        mbEvent.setEventDescription("Done with initial setup.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.05);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // now since we have cached the entries, we run the loop over
        // the atoms to identify their bonding info
        Point3D atomCenter1, atomCenter2;
        Atom a1, a2;
        int j;

        for(Integer i : atomIndices) {
            try {
                a1 = molecule.getAtom(i);
                if (a1.isVector() || a1.isDummy()) continue;
                atomCenter1 = a1.getAtomCenter();

                // >>> This is the place where defaultValency needs to be checked
                // >>> for a1
                for(j=0; j<noOfAtoms; j++) {
                    a2 = molecule.getAtom(j);
                    if (a2.isVector() || a2.isDummy()) continue;
                    atomCenter2 = a2.getAtomCenter();

                    // the first level of defence for checking the existance
                    // of bond between two atom centers
                    if (canFormBond(atomCenter1, atomCenter2)) {
                        // if so then classify the bonds..
                        // first check for weak interactions
                        vdwRadiusSum = vdwRadius[i] + vdwRadius[j];
                        covalentRadiusSum = covalentRadius[i] + covalentRadius[j];
                        if (isWeekBondPresent() && (!isSingleBondPresent())) { // weak bond?
                          molecule.setBondType(i, j, BondType.WEAK_BOND);
                        } else {
                          if (isSingleBondPresent()) {
                              doubleBondOverlap =
                                ((dblBndOverlaps[i] == 0.0) ?
                                     (dblBndOverlaps[j] == 0.0 ?
                                         DOUBLE_BOND_OVERLAP_PERCENTAGE
                                         : dblBndOverlaps[j])
                                     : dblBndOverlaps[i]);

                              if (!(a1.getSymbol().equals("H")
                                     || a2.getSymbol().equals("H"))
                                  && isDoubleBondPresent()) {
                                 if (isTripleBondPresent()) { // triple bond
                                   molecule.setBondType(i, j, BondType.TRIPLE_BOND);
                                   molecule.incrementNumberOfMultipleBonds();
                                 } else { // double bond
                                   molecule.setBondType(i, j, BondType.DOUBLE_BOND);
                                   molecule.incrementNumberOfMultipleBonds();
                                 } // end if
                              } else { // single bond
                                  molecule.setBondType(i, j, BondType.SINGLE_BOND);
                                  molecule.incrementNumberOfSingleBonds();
                              } // end if
                          } // end if
                        } // end if
                    } // end if
                } // end for
            } catch(Exception ignored) { }
        } // end for

        mbEvent.setEventDescription("Connectivity build.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.75);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // try to free up some memory
        covalentRadius = vdwRadius = dblBndOverlaps = null;
        System.gc();

        // confirm the presence of weak bonds
        confirmWeakBonds();
        // again try to grab some space
        weakBondAngle = null;
        System.gc();

        molecule.enableListeners(); // enable the listeners

        mbEvent.setEventDescription("Verified connectivity. "
                                     + "Building connectivity over.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(1.0);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // indicate the the connectivity is only partially formed
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.PARTIAL_CONNECTIVITY_FORMED,
           true);
    }

    /**
     * The implementation of this method should try and detect all weakly
     * bonded interactions.
     *
     * @param molecule The molecule object instance in which the weak
     * interactions are to be identified.
     */
    @Override
    public void identifyWeakBonds(Molecule molecule) {
        this.molecule = molecule;
        int noOfAtoms = molecule.getNumberOfAtoms();

        molecule.disableListeners(); // disable listeners

        // first cache the covalent and vdw radius, along with the info
        // on default valency and weakbond angles, needed for optimizing
        // bond detection and finding presence of weak bonds respectively.
        covalentRadius = new double[noOfAtoms];
        vdwRadius      = new double[noOfAtoms];
        weakBondAngle  = new double[noOfAtoms];

        int i, j;
        String symbol;

        for(i=0; i<noOfAtoms; i++) {
            symbol = molecule.getAtom(i).getSymbol();

            covalentRadius[i] = atomInfo.getCovalentRadius(symbol);
            vdwRadius[i]      = atomInfo.getVdwRadius(symbol);
            weakBondAngle[i]  = atomInfo.getWeakBondAngle(symbol);
        } // end for

        mbEvent.setEventDescription("Done with initial setup.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.05);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // now since we have cached the entries, we run the loop over
        // the atoms to identify their bonding info
        Point3D atomCenter1, atomCenter2;
        Atom a1, a2;

        for(i=0; i<noOfAtoms; i++) {
            a1 = molecule.getAtom(i);
            if (a1.isVector() || a1.isDummy()) continue;
            atomCenter1 = a1.getAtomCenter();

            // >>> This is the place where defaultValency needs to be checked
            // >>> for a1
            for(j=0; j<i; j++) {
                a2 = molecule.getAtom(j);
                if (a2.isVector() || a2.isDummy()) continue;
                atomCenter2 = a2.getAtomCenter();

                // the first level of defence for checking the existance
                // of bond between two atom centers
                if (canFormBond(atomCenter1, atomCenter2)) {
                    // if so then classify the bonds..
                    // first check for weak interactions
                    vdwRadiusSum = vdwRadius[i] + vdwRadius[j];
                    covalentRadiusSum = covalentRadius[i] + covalentRadius[j];
                    if (isWeekBondPresent() && (!isSingleBondPresent())) { // weak bond?
                      molecule.setBondType(i, j, BondType.WEAK_BOND);
                    } // end if
                } // end if
            } // end for
        } // end for

        mbEvent.setEventDescription("Connectivity build.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.75);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // try to free up some memory
        vdwRadius = null;
        System.gc();

        // confirm the presence of weak bonds
        confirmWeakBonds();
        // again try to grab some space
        weakBondAngle = null;
        System.gc();

        molecule.enableListeners(); // enable the listeners

        mbEvent.setEventDescription("Verified connectivity. "
                                     + "Building connectivity over.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(1.0);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // marker of what was identified
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.WEAK_BOND_DETECTED,
           true);
    }

    /**
     * The implementation of this method should try and detect all multiply
     * bonded interaction.
     *
     * @param molecule The molecule object instance in which presence of
     * multiple bonds are to be detected.
     */
    @Override
    public void identifyMultipleBonds(Molecule molecule) {
        this.molecule = molecule;

        int noOfAtoms = molecule.getNumberOfAtoms();

        molecule.disableListeners(); // disable listeners

        // first cache some std info.
        covalentRadius = new double[noOfAtoms];
        dblBndOverlaps = new double[noOfAtoms];

        int i, j;
        String symbol;

        for(i=0; i<noOfAtoms; i++) {
            symbol = molecule.getAtom(i).getSymbol();

            covalentRadius[i] = atomInfo.getCovalentRadius(symbol);
            dblBndOverlaps[i] = atomInfo.getDoubleBondOverlap(symbol);
        } // end for

        mbEvent.setEventDescription("Done with initial setup.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.05);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // now since we have cached the entries, we run the loop over
        // the atoms to identify their bonding info
        Point3D atomCenter1, atomCenter2;
        Atom a1, a2;

        for(i=0; i<noOfAtoms; i++) {
            a1 = molecule.getAtom(i);
            if (a1.isVector() || a1.isDummy()) continue;
            atomCenter1 = a1.getAtomCenter();

            // >>> This is the place where defaultValency needs to be checked
            // >>> for a1
            for(j=0; j<i; j++) {
                a2 = molecule.getAtom(j);
                if (a2.isVector() || a2.isDummy()) continue;
                atomCenter2 = a2.getAtomCenter();

                // the first level of defence for checking the existance
                // of bond between two atom centers
                if (canFormBond(atomCenter1, atomCenter2)) {
                    covalentRadiusSum = covalentRadius[i] + covalentRadius[j];
                    if (isSingleBondPresent()) {
                      doubleBondOverlap =
                        ((dblBndOverlaps[i] == 0.0) ?
                             (dblBndOverlaps[j] == 0.0 ?
                                 DOUBLE_BOND_OVERLAP_PERCENTAGE
                                 : dblBndOverlaps[j])
                             : dblBndOverlaps[i]);

                      if (!(a1.getSymbol().equals("H")
                             || a2.getSymbol().equals("H"))
                          && isDoubleBondPresent()) {
                         if (isTripleBondPresent()) { // triple bond
                           molecule.setBondType(i, j, BondType.TRIPLE_BOND);
                           molecule.incrementNumberOfMultipleBonds();
                         } else { // double bond
                           molecule.setBondType(i, j, BondType.DOUBLE_BOND);
                           molecule.incrementNumberOfMultipleBonds();
                         } // end if
                      } // end if
                    } // end if
                } // end if
            } // end for
        } // end for

        mbEvent.setEventDescription("Connectivity build.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.75);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // try to free up some memory
        covalentRadius = dblBndOverlaps = null;
        System.gc();

        molecule.enableListeners(); // enable the listeners

        mbEvent.setEventDescription("Verified connectivity. "
                                     + "Building connectivity over.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(1.0);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // marker of what was identified
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.MULTIPLE_BOND_DETECTED,
           true);
    }
    
    /**
     * A method that calculates if a bond can between two atoms.
     * Additional checks may be made, such as check default valency etc.
     * 
     * @param a1 first atom
     * @param a2 second atom
     * @param bondtype the expected bondtype
     * @return true / false indicating if a bond is present
     */
    @Override
    public boolean canFormBond(Atom a1, Atom a2, BondType bondtype) {
        String a1Symbol = a1.getSymbol();
        String a2Symbol = a2.getSymbol();
        
        if (a1.getDegree() + 1 > atomInfo.getDefaultValency(a1Symbol)) 
            return false;
        
        if (!canFormBond(a1.getAtomCenter(), a2.getAtomCenter())) return false;                
        
        covalentRadiusSum = atomInfo.getCovalentRadius(a1Symbol) 
                            + atomInfo.getCovalentRadius(a2Symbol);
        vdwRadiusSum      = atomInfo.getVdwRadius(a1Symbol)
                            + atomInfo.getVdwRadius(a2Symbol);
        
        if (bondtype.equals(BondType.SINGLE_BOND)) return isSingleBondPresent();
        if (bondtype.equals(BondType.DOUBLE_BOND)) { 
            doubleBondOverlap = 
                        ((atomInfo.getDoubleBondOverlap(a1Symbol) == 0.0) ? 
                             (atomInfo.getDoubleBondOverlap(a2Symbol) == 0.0 ? 
                                 DOUBLE_BOND_OVERLAP_PERCENTAGE
                                 : atomInfo.getDoubleBondOverlap(a2Symbol))
                             : atomInfo.getDoubleBondOverlap(a1Symbol));
            return (isSingleBondPresent() && isDoubleBondPresent());
        } // end if
        if (bondtype.equals(BondType.TRIPLE_BOND))
            return (isSingleBondPresent() && isTripleBondPresent());
        if (bondtype.equals(BondType.WEAK_BOND)) return isWeekBondPresent();
        
        return false;
    }
    
    /**
     * A method that smartly calculates the distance between two points
     * by taking in to cognizance the separation along X, Y and Z coordinates.
     *
     * @param p1 - the first point, representing an atom center
     * @param p2 - the second point, representing another atom center
     */    
    protected boolean canFormBond(Point3D p1, Point3D p2) {
        x = Math.abs(p2.getX() - p1.getX());
        if (x > BOND_RADIUS_CHECK) return false;
        
        y = Math.abs(p2.getY() - p1.getY());
        if (y > BOND_RADIUS_CHECK) return false;
        
        z = Math.abs(p2.getZ() - p1.getZ());
        if (z > BOND_RADIUS_CHECK) return false;
        
        distance = Math.sqrt(x*x + y*y + z*z);
        
        return true;
    }
    
    /**
     * method to check the presence of weak bond, using distance criterion
     */
    protected boolean isWeekBondPresent() {
        // this checking is very simple, at present no care is taken of the
        // orientation of the interacting atoms.
        // >>>> NOTE : This criteria is taken from 
        //             Cambridge Cluster Data base site
        return ((distance < (vdwRadiusSum - WEAK_BOND_TOLERANCE_LOWER)
             && (vdwRadiusSum - WEAK_BOND_TOLERANCE_UPPER) < distance));
        // >>>>       
    } 
    
    /**
     * method to check the presence of single bond, using distance criterion
     */
    protected boolean isSingleBondPresent() {
        // >>>> NOTE : This criteria is taken from 
        //             Cambridge Cluster Data base site
        return (((covalentRadiusSum - COVALENT_BOND_TOLERANCE) < distance) 
                && (distance < (covalentRadiusSum + COVALENT_BOND_TOLERANCE)));
        // >>>>        
    } 
    
    /**
     * method to check the presence of double bond, using distance criterion
     */
    protected boolean isDoubleBondPresent() {
        return (distance < (doubleBondOverlap * covalentRadiusSum));                
    } // end of method isWeekBondPresent()
    
    /**
     * method to check the presence of triple bond, using distance criterion
     */
    protected boolean isTripleBondPresent() {
        return (distance < (TRIPLE_BOND_OVERLAP_PERCENTAGE 
                            * covalentRadiusSum));     
    } // end of method isWeekBondPresent()
    
    /**
     * confirm the presence of weak bonds     
     */
    protected void confirmWeakBonds() {
        int noOfAtoms = molecule.getNumberOfAtoms();
        
        int i, j;
        Object connectedAtom;
        Hashtable connectedList;
        Enumeration connectedAtoms;
                
        // check for each weak bond, and verify its correctness by
        // checking for proper orientation
        for(i=0; i<noOfAtoms; i++) {
            connectedList  = molecule.getAtom(i).getConnectedList();
            connectedAtoms = connectedList.keys();
            
            while(connectedAtoms.hasMoreElements()) {
              connectedAtom = connectedAtoms.nextElement();
               
              // if weak bond is present ... need to evaluate it!
              if (connectedList.get(connectedAtom).equals(BondType.WEAK_BOND)) {
                  j = ((Integer) connectedAtom).intValue();
                                   
                  if (!isOrientedProperly(i, j)) {
                      // well .. no bonding verifiable, so remove it!
                      // i hope this does not cause some inconsistency with
                      // the DS !!??
                      molecule.removeBondBetween(i, j);                      
                  } else {
                      // yes, we have a weak bond here
                      molecule.incrementNumberOfWeakBonds();
                  } // end if
              }
            } // end while
        } // end for

        // put a marker on what is detected
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.WEAK_BOND_DETECTED, true);
    }
    
    /**
     * Given that an atom pair satisfies the van der Waals' criterion provided
     * by the method isWeakBondPresent(); this method checks whether the
     * atom pairs are properly oriented to form weak interaction.
     * <pre>
     * To check for proper orientation, the following procedure is used:
     * a) Consider the following situation:
     *    
     *          C
     *            +
     *              > O.......H - C
     *            +
     *          C  
     *    
     * b) Since the OH pair satisfies the van der Waals' distance criteria;
     *    therefore there is a possibility of weak interaction between O and H
     *    atoms.
     * c) To confirm the existance of such an interaction, we draw a
     *    characteristic cone (immaginary) as follows:
     *      i) Find out all the adjacent atoms of O which are strongly bonded.
     *     ii) Find the resultant of the vectors that represent the bondings.
     *    iii) Similarly find the resultant vector of atom H.
     *     iv) Compute the angles made by the two resultants vectors computed 
     *         above.
     *      v) If this angle lies with in the standard cone angles specified 
     *         then there exists weak interaction between two atom pairs.
     * </pre>
     *
     * @param atomIndex1 - the first atom index
     * @param atomIndex2 - the second atom index
     * @return boolean - true / false depending upon above specified condition
     */
    protected boolean isOrientedProperly(int atomIndex1, int atomIndex2) {        
        // first check to see if there is no connection at all
        atom1 = molecule.getAtom(atomIndex1);
        atom2 = molecule.getAtom(atomIndex2);
        
        if ((atom1.getConnectedList().size() == 0)
            || (atom2.getConnectedList().size() == 0)) {
            return true;
        } // end if
        
        // we do not consider these interactions
        if ((weakBondAngle[atomIndex1] == 0.0) 
            || (weakBondAngle[atomIndex2] == 0.0)) {
            return false;
        } // end if
                   
        // compute axis
        axis1 = computeAxis(atom1, atomIndex1);
        axis2 = computeAxis(atom2, atomIndex2);
        
        // check for null axis
        if (axis1.equals(Vector3D.NULL_VECTOR) 
            || axis2.equals(Vector3D.NULL_VECTOR)) {
            return false;
        } // end if
        
        // discard near zero vector interaction
        if ((axis1.magnitude() < WEAK_BOND_AXIS_REJECTION_FACTOR)
            || (axis2.magnitude() < WEAK_BOND_AXIS_REJECTION_FACTOR)) {
            return false;
        } // end if
        
        // compute the vector connecting atoms
        vectorFrom1To2 = new Vector3D(
                              atom2.getAtomCenter().sub(atom1.getAtomCenter()));
        vectorFrom2To1 = new Vector3D(
                              atom1.getAtomCenter().sub(atom2.getAtomCenter()));
               
        // find the two angles
        angle1 = axis1.angleWith(vectorFrom1To2);
        angle2 = axis2.angleWith(vectorFrom2To1);
               
        // and then check the correctness of the weak bonds
        if ((angle1 < weakBondAngle[atomIndex1]) 
            && (angle2 < weakBondAngle[atomIndex2])) {
            return true;
        } else {
            return false;
        } // end if
    }
    
    /**
     * method to compute the resultant axis w.r.t an atom center, based on its
     * connectivity with the neighboring atoms
     *
     * @param atom - the atom to be used for defining the axis
     * @param atomIndex - and the related atom index
     * @return Vector3D - representing the axis !
     */
    protected Vector3D computeAxis(Atom atom, int atomIndex) {
        Hashtable connectedList    = atom.getConnectedList();
        Enumeration connectedAtoms = connectedList.keys();
        int connectedAtom;
        
        Vector3D axis = Vector3D.NULL_VECTOR;
        
        while(connectedAtoms.hasMoreElements()) {
            connectedAtom = ((Integer) connectedAtoms.nextElement()).intValue();
            
            if (molecule.isStronglyBonded(atomIndex, connectedAtom)) {                
                axis = axis.add(atom.getAtomCenter().sub(
                              molecule.getAtom(connectedAtom).getAtomCenter()));
            } // end if
        } // end while               
        
        return axis.normalize();
    }
    
    /**
     * The implementation of this method should write the best possible 
     * algorithm to "refresh" the connectivity of a Molecule object, i.e.
     * the source of MoleculeStateChangeEvent.
     *
     * @param msce - the MoleculeStateChangeEvent and hence the Molecule
     *        object that has changed as a result.
     */
    @Override
    public void rebuildConnectivity(MoleculeStateChangeEvent msce) {
        Molecule mol = (Molecule) msce.getSource();
        
        // TODO: Incomplete .. special structures
        switch (msce.getEventType()) {
            case MoleculeStateChangeEvent.ATOM_ADDED:               
            case MoleculeStateChangeEvent.ATOM_REMOVED:
            case MoleculeStateChangeEvent.MAJOR_MODIFICATION:
                if (mol.isAdditionalInformationAvailable()) {
                    // connectivity information that was previously
                    // read may now be incorrect
                    mol.getAdditionalInformation().setReadConnectivity(false);
                } // end if

                mol.setCommonUserDefinedProperty(
                       CommonUserDefinedMolecularPropertyNames.DETECT_ALL, true);
                
                makeConnectivity(mol); // need to remake every thing!
                                       // indices have changed
                identifySpecialStructures(mol);
                break;
        } // end of switch .. case block
    }

    /**
     * The implementation of this method should generate a ZMatrix for the
     * molecule object, in such a way that allows geometry optimization of
     * the molecule.
     *
     * @param molecule The instance of the Molecule class which needs to be 
     *        processed for building the ZMatrix.
     */    
    @Override
    public void makeZMatrix(Molecule molecule) {   
        molecule.disableListeners(); // disable listeners
        
        // ensure we have initial connectivity
        makeSimpleConnectivity(molecule);
        
        // then reindex the molecule using backbone centered 
        // connectivity
        Iterator<Integer> traversedPath = molecule.traversePath(0);        
        int newAtomIndex = 0;
        
        while(traversedPath.hasNext()) {
            Atom atom = molecule.getAtom(traversedPath.next());
            
            atom.setIndex(newAtomIndex++);
            atom.removeAllConnections();
        } // end while
        
        for(int i=0; i<molecule.getNumberOfAtoms(); i++) {            
            int j;
            for(j=i; j<molecule.getNumberOfAtoms(); j++) {
                if (molecule.getAtom(j).getIndex() == i) break;
            } // end for
            
            Atom atomJ = molecule.getAtom(j);
            Atom atomI = molecule.getAtom(i);
            
            molecule.setAtom(i, atomJ);
            molecule.setAtom(j, atomI);
        } // end for
        
        // rebuild the molecule
        molecule.setCommonUserDefinedProperty(
                CommonUserDefinedMolecularPropertyNames.DETECT_ALL, true);
        makeConnectivity(molecule);
        identifySpecialStructures(molecule);
        
        // and then build the Z-Matrix
        double sum, dist;
        Atom referenceAtom;        
        Iterator<Atom> atoms = molecule.getAtoms();        
        
        for(;atoms.hasNext();) {
            Atom atom = atoms.next();                       
            
            if (atom.getIndex() == 0) {
                atom.getLengthReference().setReferenceAtom(null);
                atom.getAngleReference().setReferenceAtom(null);
                atom.getDihedralReference().setReferenceAtom(null);
                                
                continue;
            } if (atom.getIndex() == 1) {               
                referenceAtom = molecule.getAtom(0);               
                atom.setLengthReference(new ZMatrixItem(referenceAtom, 0.0));
                atom.getAngleReference().setReferenceAtom(null);
                atom.getDihedralReference().setReferenceAtom(null);
                                
                continue;
            } else if (atom.getIndex() == 2) {                
                referenceAtom = molecule.getAtom(1);
                if (molecule.isBonded(atom.getIndex(), 
                                       referenceAtom.getIndex())) {
                   atom.setLengthReference(new ZMatrixItem(referenceAtom, 0.0));
                   atom.setAngleReference(new ZMatrixItem(
                   referenceAtom.getLengthReference().getReferenceAtom(), 0.0));
                   atom.getDihedralReference().setReferenceAtom(null);
                } else {
                   referenceAtom = molecule.getAtom(0);
                   atom.setLengthReference(new ZMatrixItem(referenceAtom, 0.0));
                   atom.setAngleReference(new ZMatrixItem(
                                                     molecule.getAtom(1), 0.0));
                   atom.getDihedralReference().setReferenceAtom(null);
                } // end if
                            
                continue;
            } // end if
            
            // need to evaluate for nearness
            sum = 1.0E10;
            referenceAtom = molecule.getAtom(0);
            
            Iterator<Atom> atomsN = molecule.getAtoms();
            Atom atomN;            
            
            while(atomsN.hasNext()) {
                atomN = atomsN.next();
                
                if ((atom.getIndex() < 2) && (atomN.getIndex() < 2)) continue;
                    
                if (atom.getIndex() == atomN.getIndex()) break; 
                
                dist = atom.distanceFrom(atomN);                                
                                
                if ((dist < sum)
                   && (atomN.getLengthReference().getReferenceAtom() != atomN) 
                   && (atomN.getAngleReference().getReferenceAtom() != atomN)) {
                    sum = dist;
                    referenceAtom = atomN;
                } // end if                                
            } // end while
            
            atom.setLengthReference(new ZMatrixItem(referenceAtom, 0.0));
            
            if (referenceAtom.getIndex() == 0) {
                atom.setAngleReference(new ZMatrixItem(
                                           molecule.getAtom(1), 0.0));
            } else {
                atom.setAngleReference(new ZMatrixItem(
                   referenceAtom.getLengthReference().getReferenceAtom(), 0.0));
            } // end if
            
            if (referenceAtom.getIndex() <= 1) {
                atom.setDihedralReference(new ZMatrixItem(
                                           molecule.getAtom(2), 0.0));
            } else {
                atom.setDihedralReference(new ZMatrixItem(
                   referenceAtom.getAngleReference().getReferenceAtom(), 0.0));
            } // end if
        } // end for                
        
        // now fill in the values of ZMatrix elements
        Atom atom, a, b, c;
        Vector3D v1, v2;
        
        for(int k=1; k<molecule.getNumberOfAtoms(); k++) {
            atom = molecule.getAtom(k);            
            a = atom.getLengthReference().getReferenceAtom();
            b = atom.getAngleReference().getReferenceAtom();
            c = atom.getDihedralReference().getReferenceAtom(); 
                        
            // length
            if (k == 1) {
                atom.getLengthReference().setValue(atom.distanceFrom(a));
                
                continue;
            } // end if
            
            atom.getLengthReference().setValue(atom.distanceFrom(a));
            
            // angle
            v1 = new Vector3D(atom.getAtomCenter().sub(a.getAtomCenter()));
            v2 = new Vector3D(b.getAtomCenter().sub(a.getAtomCenter()));            
            atom.getAngleReference().setValue(
                                        MathUtil.toDegrees(v1.angleWith(v2)));
            
            if (k == 2) continue;
            
            // dihedral
            atom.getDihedralReference().setValue(MathUtil.toDegrees(
                           MathUtil.findDihedral(
                                    atom.getAtomCenter(),
                                    a.getAtomCenter(),
                                    b.getAtomCenter(), 
                                    c.getAtomCenter()
                           )
                       ));            
        } // end for
        
        // check for linear geometries and try to correct if possible
        boolean done;
        double angle;
        
        for(int k=1; k<molecule.getNumberOfAtoms(); k++) {            
            atom = molecule.getAtom(k);
            angle = Math.abs(atom.getAngleReference().getValue());
            
            if ((angle > 5.0) && (angle < 175.0)) continue;
            
            done = false;            
            atoms = molecule.getAtoms();
            
            while (atoms.hasNext()) {
                a = atoms.next();
                
                if ((a.getIndex() >= k) || done) break;
                
                Iterator<Atom> atoms2 = molecule.getAtoms();
                
                while(atoms2.hasNext()) {
                    b = atoms2.next();
                    
                    if ((b.getIndex() >= a.getIndex()) || done) break;
                    
                    v1 = new Vector3D(atom.getAtomCenter().sub(
                                                            a.getAtomCenter()));
                    v2 = new Vector3D(b.getAtomCenter().sub(a.getAtomCenter()));
                   
                    angle = Math.abs(MathUtil.toDegrees(v1.angleWith(v2)));
                   
                    if ((angle < 5.0) && (angle > 175.0)) continue;
                    
                    Iterator<Atom> atoms3 = molecule.getAtoms();
                    c = null;
                    
                    while(atoms3.hasNext()) {
                        c = atoms3.next();
                        
                        if (c.getIndex() >= atom.getIndex()) break;
                        
                        if ((c != atom) && (c != a) && (c != b)) break;
                    } // end while
                    
                    if (c == null) continue;
                    
                    // recalculate the coordinates of this atom                    
                    atom.getLengthReference().setReferenceAtom(a);
                    atom.getLengthReference().setValue(v1.magnitude());
                    
                    atom.getAngleReference().setReferenceAtom(b);
                    atom.getAngleReference().setValue(MathUtil.toDegrees(
                                                       v1.angleWith(v2)));
                    
                    atom.getDihedralReference().setReferenceAtom(c);
                    atom.getDihedralReference().setValue(MathUtil.toDegrees(
                           MathUtil.findDihedral(
                                    atom.getAtomCenter(),
                                    a.getAtomCenter(),
                                    b.getAtomCenter(), 
                                    c.getAtomCenter()
                           )
                       ));
                    
                    done = true;
                } // end while
            } // end while
        } // end for
        
        molecule.setZMatrixComputed(true);
        molecule.enableListeners(); // enable the listeners
        
        mbEvent.setEventDescription("Z-Matrix construction over.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
    }

    /**
     * Some times it may be needed to add hydrogens to a molecule automatically,
     * typically the case with PDB files. This simple interface defines a way to
     * add hydrogens to an existing Molecule object
     *
     * @param molecule the Molecule object to which hydrogen atoms is
     *                 to be added
     */
    @Override
    public void addHydrogens(Molecule molecule) {
        // TODO:
    }

    /**
     * Pre process the Molecule object
     * 
     * @param molecule the
     */
    protected boolean preProcessMolecule(Molecule molecule) {
        try {
          // detect every thing
          if ((Boolean) molecule.getCommonUserDefinedProperty(
             CommonUserDefinedMolecularPropertyNames.DETECT_ALL)
              == true) {
              return false;
          } // end if
        } catch(Exception ignored) {}

        try {
          // detect only single bonds
          if ((Boolean) molecule.getCommonUserDefinedProperty(
             CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_SINGLE_BONDS)
              == true) {
              makeSimpleConnectivity(molecule);

              molecule.setCommonUserDefinedProperty(
               CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_SINGLE_BONDS,
               false);

              return true;
          } // end if
        } catch(Exception ignored) {}

        try {
          // detect special structures
          if ((Boolean) molecule.getCommonUserDefinedProperty(
             CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_SPECIAL_STRUCTURE)
              == true) {
              identifySpecialStructures(molecule);

              molecule.setCommonUserDefinedProperty(
               CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_SPECIAL_STRUCTURE,
               false);
              
              return true;
          } // end if
        } catch(Exception ignored) {}

        try {
          // detect weak bonds
          if ((Boolean) molecule.getCommonUserDefinedProperty(
             CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_WEAK_BONDS)
              == true) {
              identifyWeakBonds(molecule);

              molecule.setCommonUserDefinedProperty(
               CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_WEAK_BONDS,
               false);

              return true;
          } // end if
        } catch(Exception ignored) {}

        try {
          // detect multiple bonds
          if ((Boolean) molecule.getCommonUserDefinedProperty(
             CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_MULTIPLE_BONDS)
              == true) {
              identifyMultipleBonds(molecule);

              molecule.setCommonUserDefinedProperty(
               CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_MULTIPLE_BONDS,
               false);

              return true;
          } // end if
        } catch(Exception ignored) { }

        // if control reaches here that means it is better to try the
        // normal way of connectivity
        
        return false;
    }
} // end of class MoleculeBuilderImpl
