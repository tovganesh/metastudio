/*
 * MoleculeBuilderMCImpl.java
 *
 * Created on July 9, 2007, 11:11 AM
 */

package org.meta.molecule.impl;

import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.CommonUserDefinedMolecularPropertyNames;
import org.meta.molecule.Molecule;
import org.meta.parallel.AbstractSimpleParallelTask;
import org.meta.parallel.SimpleParallelTask;
import org.meta.parallel.SimpleParallelTaskExecuter;

/**
 * This is a special implementation of MoleculeBuilder which is specifically
 * targeted towards multi core processors (so the attached MC). If a multi
 * core version of the required algorithm is not present, this class 
 * automatically uses the serial version provided by its super class.
 *
 * Note: The methods in this class are not thread safe, and hence it is best
 * to use individual objects of this class in separate threads. This, however,
 * does not apply to the fact that methods in this class are coded specifically
 * to take advantage of multiple cores.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeBuilderMCImpl extends MoleculeBuilderImpl {
    
    /**
     * We care about parallel version only if we have more than 
     * <code>PARALLEL_THRESHOLD</code> atoms to handle.
     */
    public static final int PARALLEL_THRESHOLD = 3;
    
    /** Creates a new instance of MoleculeBuilderMCImpl */
    public MoleculeBuilderMCImpl() {
    }
    
    /**
     * The implementation of this method should write the best possible
     * algorithms for identifying the required type of bonds. Also the
     * implementation should *not* worry about how the connectivity is
     * to be stored, this is taken care of by the implementors of
     * Molecule class.
     *
     * This is the multi core processor version of the <code>makeConnectivity()
     * </code> interface that aims to use available cores to substantially 
     * fasten up the process of identifying bonds (strong and weak) and 
     * any special structures.
     *
     * @param molecule The instance of the Molecule class which needs to be 
     *        processed for bonding information.
     */    
    @Override
    public void makeConnectivity(Molecule molecule) {
        // and also check if we have sufficient atoms to really "reap" benifit        
        if (molecule.getNumberOfAtoms() <= PARALLEL_THRESHOLD) {
            super.makeConnectivity(molecule);
            
            return;
        } // end if

        boolean readConnectivity = molecule.isAdditionalInformationAvailable() 
                && molecule.getAdditionalInformation().isReadConnectivity();
        
        if (readConnectivity) {
            // already read connectivity information from file, no nead to build
            return;
        } // end if
        
        // else we can try doing some juggling ...
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
        
        int i;        
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
        
        SimpleParallelTaskExecuter pTaskExecuter
                                   = new SimpleParallelTaskExecuter();

        MoleculeBuilderThread tThread
                   = new MoleculeBuilderThread(molecule);
        tThread.setTaskName("MoleculeBuilder Thread");
        tThread.setTotalItems(molecule.getNumberOfAtoms());

        pTaskExecuter.execute(tThread);
        
        // and then do the rest of the stuff in the usual way ...
        mbEvent.setEventDescription("Connectivity build.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.75);
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        
        // try to free up some memory
        covalentRadius = vdwRadius = dblBndOverlaps = null;        
        System.gc();

        // put a marker on what is detected
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.SIMPLE_BOND_DETECTED, true);
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.MULTIPLE_BOND_DETECTED, true);

        // confirm the presence of weak bonds        
        confirmWeakBonds();
        // again try to grab some space        
        weakBondAngle = null;
        System.gc();
        
        molecule.enableListeners(); // enable the listeners
        
        mbEvent.setEventDescription("Varified connectivity. "
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
        // and also check if we have sufficient atoms to really "reap" benifit
        if (molecule.getNumberOfAtoms() <= PARALLEL_THRESHOLD) {
            super.makeConnectivity(molecule);

            return;
        } // end if

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

        int i;
        String symbol;

        for(i=0; i<noOfAtoms; i++) {
            symbol = molecule.getAtom(i).getSymbol();

            covalentRadius[i] = atomInfo.getCovalentRadius(symbol);
        } // end for

        SimpleParallelTaskExecuter pTaskExecuter
                                   = new SimpleParallelTaskExecuter();

        SimpleMoleculeBuilderThread tThread
                   = new SimpleMoleculeBuilderThread(molecule);
        tThread.setTaskName("SimpleMoleculeBuilder Thread");
        tThread.setTotalItems(molecule.getNumberOfAtoms());

        pTaskExecuter.execute(tThread);

        // and then do the rest of the stuff in the usual way ...
        mbEvent.setEventDescription("Connectivity build.");
        fireMoleculeBuildListenerBuildEvent(mbEvent);
        mbEvent.setPercentCompletion(0.75);
        fireMoleculeBuildListenerBuildEvent(mbEvent);

        // try to free up some memory
        covalentRadius = null;
        System.gc();

        // put a marker on what is detected
        molecule.setCommonUserDefinedProperty(
           CommonUserDefinedMolecularPropertyNames.SIMPLE_BOND_DETECTED, true);
        
        molecule.enableListeners(); // enable the listeners
    }
    
    /**
     * The molecule builder Thread ... that does the actual job of
     * partially building the connectivity of Molecule (for makeConnectivity())
     */
    protected class MoleculeBuilderThread extends AbstractSimpleParallelTask {
        private Molecule molecule;
        private int startAtomIndex, endAtomIndex, i, j;
        private double vdwRadiusSum, covalentRadiusSum, doubleBondOverlap,
                       distance, x, y, z;

        public MoleculeBuilderThread(Molecule molecule) {
            this.molecule = molecule;
        }
        
        public MoleculeBuilderThread(Molecule molecule, 
                                     int startAtomIndex, int endAtomIndex) {
           this.molecule       = molecule;
           this.startAtomIndex = startAtomIndex;
           this.endAtomIndex   = endAtomIndex;
           
           setTaskName("MoleculeBuilder Thread");
        }

        /** overridden run() */
        @Override
        public void run() {
            Point3D atomCenter1, atomCenter2;
            Atom a1, a2;

            for(i=startAtomIndex; i<endAtomIndex; i++) {
                a1 = molecule.getAtom(i);
                if (a1.isVector()) continue;
                atomCenter1 = a1.getAtomCenter();            

                // >>> This is the place where defaultValency needs to be checked
                // >>> for a1
                for(j=0; j<i; j++) {
                    a2 = molecule.getAtom(j);
                    if (a2.isVector()) continue;
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
        }

        /** Overridden init() */
        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new MoleculeBuilderThread(molecule, startItem, endItem);
        }

        // the following methods are duplicated for thread security reasons.
        
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
    } // end of class MoleculeBuilderThread

    /**
     * The molecule builder Thread ... that does the actual job of
     * partially building the connectivity of Molecule (for makeSimpleConnectivity())
     */
    protected class SimpleMoleculeBuilderThread extends AbstractSimpleParallelTask {
        private Molecule molecule;
        private int startAtomIndex, endAtomIndex, i, j;
        private double covalentRadiusSum, distance, x, y, z;

        public SimpleMoleculeBuilderThread(Molecule molecule) {
            this.molecule = molecule;
        }

        public SimpleMoleculeBuilderThread(Molecule molecule,
                                     int startAtomIndex, int endAtomIndex) {
           this.molecule       = molecule;
           this.startAtomIndex = startAtomIndex;
           this.endAtomIndex   = endAtomIndex;
           
           setTaskName("MoleculeBuilder Thread");
        }

        /** overridden run() */
        @Override
        public void run() {
            Point3D atomCenter1, atomCenter2;
            Atom a1, a2;

            for(i=startAtomIndex; i<endAtomIndex; i++) {
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
                }
            }
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
        
        /** Overridden init() */
        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new SimpleMoleculeBuilderThread(molecule, startItem, endItem);
        }
    } // end of class SimpleMoleculeBuilderThread

} // end of class MoleculeBuilderMCImpl
