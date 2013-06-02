/*
 * MoleculeImpl.java
 *
 * Created on September 7, 2003, 8:07 PM
 */

package org.meta.molecule.impl;

import java.util.*;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;

import org.meta.config.impl.AtomInfo;
import org.meta.fragment.event.FragmentListChangeEvent;
import org.meta.fragment.event.FragmentListChangeListener;
import org.meta.fragmentor.FragmentGoodnessProbeFactory;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.fragmentor.FragmentationSchemeFactory;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.MolecularFormula;
import org.meta.molecule.Molecule;
import org.meta.molecule.SpecialStructureRecognizer;
import org.meta.molecule.event.MoleculeStateChangeEvent;

/**
 * The default implementation of Molecule interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeImpl extends Molecule {
    
    private String title;
    
    private ArrayList<Atom> atomList;
    
    private Set<SpecialStructureRecognizer> specialStructureRecognizerList;    
    
    private Set<FragmentationScheme> fragmentationSchemeList;
    
    private BoundingBox boundingBox;
    
    private boolean stateChanged;
    
    private MoleculeStateChangeEvent msce;
    
    private AtomInfo atomInfo;
    
    private MolecularFormula molecularFormula;
    
    private SpecialStructureRecognizer defaultSSR;
    
    private FragmentationScheme defaultFragmentationScheme;
    
    /** Creates a new instance of MoleculeImpl */
    public MoleculeImpl() {
        this("Molecule");               
    }
    
    public MoleculeImpl(String title) {
        this.zMatrixComputed = false;
        this.additionalInformationAvailable = false;
        this.title = title.trim();
        
        // init values
        atomList = new ArrayList<Atom>();
        
        specialStructureRecognizerList 
                                  = new HashSet<SpecialStructureRecognizer>(10);
        fragmentationSchemeList   = new HashSet<FragmentationScheme>(5);
        
        stateChanged = true;
        
        numberOfSingleBonds = numberOfMultipleBonds = numberOfWeakBonds = 0;
        numberOfElectrons = 0;
        molecularMass = 0.0;
        
        // event listener
        msce = new MoleculeStateChangeEvent(this);
        
        // atom info..
        atomInfo = AtomInfo.getInstance(); 
        
        // default special structure recognizer
        defaultSSR = new DefaultSpecialStructureRecognizer();
        specialStructureRecognizerList.add(defaultSSR);       
        
        // default value for center of mass
        centerOfMass = new Point3D(0.0, 0.0, 0.0);
    }
    
    /**
     * Returns the title.
     *
     * @return String identifying the title of the Molecule object
     */  
    @Override
    public String getTitle() {
        return title;
    }
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param atom the instance of atom class
     */    
    @Override
    public void addAtom(Atom atom) {
        atomList.add(atom);
        
        // adjust the molecular weight, and center of mass
        double mass = atomInfo.getAtomicWeight(atom.getSymbol());
        molecularMass += mass;
        centerOfMass.setX(centerOfMass.getX() + (mass * atom.getX()));
        centerOfMass.setY(centerOfMass.getY() + (mass * atom.getY()));
        centerOfMass.setZ(centerOfMass.getZ() + (mass * atom.getZ()));
        
        // and numberOfElectrons
        numberOfElectrons += atomInfo.getAtomicNumber(atom.getSymbol());
        
        // fire the change event 
        stateChanged = true;
        msce.setEventType(MoleculeStateChangeEvent.ATOM_ADDED);
        msce.setAtom1(atom);
        fireMoleculeStateChangeListenerMoleculeChanged(msce);
    }
    
    /** 
     * remove an atom from this Molecule at a given index
     * May throw runtime exception, if invalid index
     *
     * @param atomIndex the atom index to be removed
     */   
    @Override
    public void removeAtomAt(int atomIndex) {
        Atom atom = (Atom) atomList.get(atomIndex);
                
        // adjust the molecular weight and COM
        double mass = atomInfo.getAtomicWeight(atom.getSymbol());
        molecularMass -= mass;
        centerOfMass.setX(centerOfMass.getX() - (mass * atom.getX()));
        centerOfMass.setY(centerOfMass.getY() - (mass * atom.getY()));
        centerOfMass.setZ(centerOfMass.getZ() - (mass * atom.getZ()));
        
        // and numberOfElectrons
        numberOfElectrons -= atomInfo.getAtomicNumber(atom.getSymbol());
        
        // remove the atom
        atomList.remove(atomIndex);
        
        // then reindex the atoms
        Iterator atoms = atomList.iterator();
        int index = 0;
        Atom atom1;
        
        while(atoms.hasNext()) {
            atom1 = ((Atom) atoms.next());
            atom1.setIndex(index++);
            atom1.removeAllConnections();
        } // emd while
        
        // and fire the change event
        stateChanged = true;
        msce.setEventType(MoleculeStateChangeEvent.ATOM_REMOVED);
        msce.setAtom1(atom);        
        fireMoleculeStateChangeListenerMoleculeChanged(msce);
    }
    
    /** 
     * remove an atom from this Molecule    
     *
     * @param atom the instance of atom class
     */   
    @Override
    public void removeAtom(Atom atom) {
        removeAtomAt(atom.getIndex());
    }
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param symbol the atom symbol   
     * @param atomCenter the Cartesian coordinates of the atom stored as Point3D
     *        object
     */   
    @Override
    public void addAtom(String symbol, Point3D atomCenter) {
        addAtom(new Atom(symbol, 0.0, atomCenter));
    }
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param symbol the atom symbol
     * @param charge is the charge on the atom (the atomic number in many cases)
     * @param atomCenter the Cartesian coordinates of the atom stored as Point3D
     *        object
     */    
    @Override
    public void addAtom(String symbol, double charge, Point3D atomCenter) {
        addAtom(new Atom(symbol, charge, atomCenter));
    }
    
    /** 
     * Adds an atom to this molecule object.
     *
     * @param symbol the atom symbol
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     */   
    @Override
    public void addAtom(String symbol, double x, double y, double z) {
        addAtom(new Atom(symbol, 0.0, new Point3D(x, y, z)));
    }
    
    /** 
     * Adds an atom to this molecule object.
     *
     * @param symbol the atom symbol
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     * @param index the atom index of this atom
     */   
    @Override
    public void addAtom(String symbol, double x, double y, double z, 
                        int index) {
        addAtom(new Atom(symbol, 0.0, new Point3D(x, y, z), index));
    }
    
    /** 
     * Overloaded addAtom() method.
     *
     * @param symbol the atom symbol
     * @param charge is the charge on the atom (the atomic number in many cases)
     * @param x X coordinate of the atom 
     * @param y Y coordinate of the atom 
     * @param z Z coordinate of the atom 
     */   
    @Override
    public void addAtom(String symbol, 
                        double charge, double x, double y, double z) {
        addAtom(new Atom(symbol, charge, new Point3D(x, y, z)));
    }
    
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
    @Override
    public void addAtom(String symbol, double charge, 
                                 double x, double y, double z, int index) {
        addAtom(new Atom(symbol, charge, new Point3D(x, y, z), index));
    }
    
    /** 
     * Method to get a particular atom from the lists of atoms.
     *
     * @return Atom the instance of atom class
     * @throws runtime exception IndexOutOfBoundsException if not a
     *            valid atomIndex
     */
    @Override
    public Atom getAtom(int atomIndex) {
        return atomList.get(atomIndex);
    }
    
    /** 
     * Method to set a particular atom in the lists of atoms.
     *
     * @param atomIndex the index in the list, which is to be changed 
     * @param atom the instance of atom class
     * @throws runtime exception IndexOutOfBoundsException if not a
     *            valid atomIndex
     */
    @Override
    public void setAtom(int atomIndex, Atom atom) {
        atomList.set(atomIndex, atom);
    }
    
    /** 
     * Method to get a particular atom from the lists of atoms.
     *
     * @return an iterator object containing a linear list of atoms in the
     *         Molecule!
     */  
    @Override
    public Iterator<Atom> getAtoms() {
        return atomList.iterator();
    }

    /**
     * A way to indicate the implementation that all Atom objects are added
     * and now the Molecule object could be "cleansed" to minimize memory
     * usage.
     */
    @Override
    public void pack() {
        atomList.trimToSize();
    }

    /**
     * Return an atom index that seems similar to the one object passed.
     *
     * @param atom the Atom object to be searched for
     * @return the atoms index which is similar to the Atom object passed
     *         (-1 is no match found).
     */
    @Override
    public int soundsLike(Atom atom) {
        int index = -1;
        
        int i = 0;
        for(Atom atm : atomList) {
            if (atm.distanceFrom(atom) <= 0.0001) {
                if (atm.getSymbol().equals(atom.getSymbol())) {
                    index = i; break;
                } // end if
            } // end if
            i++;
        } // end for
        
        return index;
    }
    
    /**
     * The method defines an interface to add an implementation of
     * SpecialStructureRecognizer interface, so that certain special
     * structures like rings and special groups can be recognized.
     * <br>
     * <pre>
     * WARNING!: this method in no way gaurentees the non duplication of 
     *           recognizer objects, and is the responsibility of the callie
     *           to ensure non duplication.
     * </pre>
     *
     * @param ssr The implementation object of SpecialStructureRecognizer
     */ 
    @Override
    public void addSpecialStructureRecognizer(SpecialStructureRecognizer ssr) {        
        specialStructureRecognizerList.add(ssr);
        
        // no boolean value set as this doesn't affect the molecules bound
        
        msce.setEventType(MoleculeStateChangeEvent.STRUCTURE_ADDED);
        msce.setSpecialStructureRecognizer(ssr);
        fireMoleculeStateChangeListenerMoleculeChanged(msce);
    }
    
    /**
     * Removes an implementation of SpecialStructureRecognizer interface,
     * so that certain structures may not be identified.
     *
     * @param ssr The implementation object of SpecialStructureRecognizer
     */
    @Override
    public void removeSpecialStructureRecognizer(
                                              SpecialStructureRecognizer ssr) {
        specialStructureRecognizerList.remove(ssr);
        
        // no boolean value set as this doesn't affect the molecules bound
        
        msce.setEventType(MoleculeStateChangeEvent.STRUCTURE_REMOVED);
        msce.setSpecialStructureRecognizer(ssr);
        fireMoleculeStateChangeListenerMoleculeChanged(msce);
    }
   
    /**
     * Method to get list of all SpecialStructureRecognizer s
     *
     * @return Iterator object containing list of all in implementations of
     *         SpecialStructureRecognizer recorded with this instance of 
     *         Molecule object
     */ 
    @Override
    public Iterator<SpecialStructureRecognizer> 
                   getSpecialStructureRecognizers() {
        return specialStructureRecognizerList.iterator();
    }        
    
    /**
     * Add a fragmentation scheme to this molecule object
     *
     * @param fragmentationScheme the FragmentationScheme to be added to this 
     *        molecule object.
     */
    @Override
    public void addFragmentationScheme(
                                FragmentationScheme fragmentationScheme) {
        fragmentationSchemeList.add(fragmentationScheme);
        
        // TODO: not a very wise idea, but ok for now
        if (fragmentationScheme.getName().equals("defaultScheme")) {
            defaultFragmentationScheme = fragmentationScheme;
            
            System.out.println("default added");
        } // end if
        
        // listen to changes in the fragmentation scheme and notify
        // them as changes in the molecule
        fragmentationScheme.addFragmentListChangeListener(
          new FragmentListChangeListener() {
            @Override
            public void fragmentListChanged(FragmentListChangeEvent e) {
                // and fire the change event
                stateChanged = true;
                msce.setEventType(MoleculeStateChangeEvent.FRAGMENTS_MODIFIED);
                msce.setFragmentListChangeEvent(e);
                fireMoleculeStateChangeListenerMoleculeChanged(msce);
            }
          }
        );
    }
    
    /**
     * Remove a fragmentation scheme from this molecule object
     *
     * @param fragmentationScheme the FragmentationScheme to be removed from  
     *        this molecule object.
     */
    @Override
    public void removeFragmentationScheme(
                                    FragmentationScheme fragmentationScheme) {
        fragmentationSchemeList.remove(fragmentationScheme);
    }
    
    /**
     * Returns a list of all the fragmentation schemes connected to this 
     * molecule object.
     *
     * @return an Iterator object containing a list of FragmentationScheme
     *         objects.
     */
    @Override
    public Iterator<FragmentationScheme> getAllFragmentationSchemes() {
        return fragmentationSchemeList.iterator();
    }
    
    /**
     * This method returns true or false indicating whether an atom pair is 
     * bonded.
     *
     * @param atomIndex1 the reference atom 
     * @param atomIndex2 the atom index for which the bonding 
     *        information is queried.
     * @return true if there is a bond between atomIndex1 and atomIndex2, false
     *         otherwise.
     * @throws runtime exception IndexOutOfBoundsException if not a
     *            valid atomIndex1.
     */
    @Override
    public boolean isBonded(int atomIndex1, int atomIndex2) {
        return (((Atom) atomList.get(atomIndex1)).isConnected(atomIndex2));
    }
    
    /**
     * This method returns true or false indicating whether an atom pair is 
     * having a bond between them.
     *
     * @param atomIndex1 the reference atom 
     * @param atomIndex2 the atom index for which the bonding 
     *        information is queried.
     * @return true if there is a strong bond between atomIndex1 and 
     *         atomIndex2, false otherwise.
     * @throws runtime exception IndexOutOfBoundsException if not a
     *            valid atomIndex1.
     */
    @Override
    public boolean isStronglyBonded(int atomIndex1, int atomIndex2) {
        BondType bt = ((Atom) atomList.get(atomIndex1))
                                      .getConnectivity(atomIndex2);
        
        return bt.isStrongBond();
    }
   
    /**
     * Method to obtain the bonding type between an atom pair.
     *
     * @param atomIndex1 the reference atom 
     * @param atomIndex2 the atom index for which the bonding 
     *        information is queried.
     * @return instance of BondType indicating the type of bond.
     */ 
    @Override
    public BondType getBondType(int atomIndex1, int atomIndex2) {
        return (((Atom) atomList.get(atomIndex1)).getConnectivity(atomIndex2));
    }
    
    /**
     * Method to define the bonding type of an atom pair.
     *
     * @param atomIndex1 the reference atom
     * @param atomIndex2 the atom index between which the bonding
     *        is to be defined
     * @param bondType An instance of BondType defining the type of the bond
     * @throws runtime exception IndexOutOfBoundsException if not a
     *            valid atomIndex1 or atomIndex2.
     */
    @Override
    public void setBondType(int atomIndex1, int atomIndex2, BondType bondType) {
        ((Atom) atomList.get(atomIndex1)).addConnection(atomIndex2, bondType);
        ((Atom) atomList.get(atomIndex2)).addConnection(atomIndex1, bondType);
        
        // no boolean value set as this doesn't affect the molecules bound
        if (!bondType.equals(BondType.NO_BOND)) {
            msce.setEventType(MoleculeStateChangeEvent.BOND_MODIFIED);
        } else {
            msce.setEventType(MoleculeStateChangeEvent.BOND_REMOVED);
        } // end if
        
        msce.setAtom1((Atom) atomList.get(atomIndex1));
        msce.setAtom2((Atom) atomList.get(atomIndex2));
        fireMoleculeStateChangeListenerMoleculeChanged(msce);        
    }   
    
    /**
     * Method to remove the bond between an atom pair
     *
     * @param atomIndex1 the reference atom
     * @param atomIndex2 the atom index between which the bonding
     *        is to be removed
     */
    @Override
    public void removeBondBetween(int atomIndex1, int atomIndex2) {
        setBondType(atomIndex1, atomIndex2, BondType.NO_BOND);                     
    }
    
    /**
     * sets the title.
     *
     * @param title string identifying the title of the Molecule object
     */  
    @Override
    public void setTitle(String title) {
        this.title = title.trim();
        
        msce.setEventType(MoleculeStateChangeEvent.TITLE_MODIFIED);        
        fireMoleculeStateChangeListenerMoleculeChanged(msce);
    }
    
    /** 
     * Method to get total number of atoms in the molecule.
     *
     * @return number of atoms in the Molecule
     */    
    @Override
    public int getNumberOfAtoms() {
        return atomList.size();
    }
    
    /** 
     * Method to return the bonding box enclosing the molecule.
     *
     * @return BoundingBox inclosing the molecule.
     */    
    @Override
    public BoundingBox getBoundingBox() {
        if (boundingBox == null || stateChanged) {
            boundingBox = new BoundingBox();
            
            if (atomList.isEmpty()) return boundingBox;
            
            Atom atom = (Atom) atomList.get(0);
            
            double xmin = atom.getX(); 
            double xmax = xmin;
            double ymin = atom.getY(); 
            double ymax = ymin;
            double zmin = atom.getZ(); 
            double zmax = zmin;
            
            double x, y, z;
            
            for (int i=1; i<atomList.size(); i++) {
                atom = (Atom) atomList.get(i);
                
                x = atom.getX();
                
                if (x < xmin)
                    xmin = x;
                if (x > xmax)
                    xmax = x;
                
                y = atom.getY();
                
                if (y < ymin)
                    ymin = y;
                if (y > ymax)
                    ymax = y;
                
                z = atom.getZ();
                
                if (z < zmin)
                    zmin = z;
                if (z > zmax)
                    zmax = z;
            } // end for
            
            boundingBox.getUpperLeft().setX(xmin);
            boundingBox.getUpperLeft().setY(ymin);
            boundingBox.getUpperLeft().setZ(zmin);
            boundingBox.getBottomRight().setX(xmax);
            boundingBox.getBottomRight().setY(ymax);
            boundingBox.getBottomRight().setZ(zmax);            
        } // end if
        
        stateChanged = false;
        
        return boundingBox;
    }        
    
    /** 
     * Method to return the bonding box center on COM and enclosing the molecule
     *
     * @return BoundingBox inclosing the molecule and centered on COM.
     */     
    @Override
    public BoundingBox getMassCenteredBoundingBox() {        
        BoundingBox bb = getBoundingBox();
    
        if (atomList.isEmpty()) return bb;
       
        Point3D com = getCenterOfMass();
        
        BoundingBox bborg = getBoundingBox();
        
        double dist = bborg.center().distanceFrom(com);
        
        bb.getUpperLeft().setX(bb.getUpperLeft().getX() - dist);
        bb.getUpperLeft().setY(bb.getUpperLeft().getY() - dist);
        bb.getUpperLeft().setZ(bb.getUpperLeft().getZ() - dist);
        bb.getBottomRight().setX(bb.getBottomRight().getX() + dist);
        bb.getBottomRight().setY(bb.getBottomRight().getY() + dist);
        bb.getBottomRight().setZ(bb.getBottomRight().getZ() + dist);
        
        return bb;
    }        
    
    /**
     * Method to return the instance of RingRecognizer connected to this
     * object, if any.
     *
     * @return a valid subclass of SpecialStructureRecognizer which recognizes
     *        rings; a null if no such recognizer is attached to this molecule
     *        object.
     */    
    @Override
    public SpecialStructureRecognizer getRingRecognizer() {
        Iterator ssrList = specialStructureRecognizerList.iterator();
        Object ssr;
        
        while(ssrList.hasNext()) {
            ssr = ssrList.next();
            
            if (ssr instanceof RingRecognizer) {
                return (RingRecognizer) ssr; // there , we do know rings
            } // end if
        } // end while
        
        return null; // we did not find any relevent recognizer
    }
    
    /**
     * Method to return the instance of a default SpecialStructureRecognizer
     * which does not do any automatic recognition of structures.
     *
     * @return a valid subclass of SpecialStructureRecognizer which does no
     *         automatic recognition of structures.
     */    
    @Override
    public SpecialStructureRecognizer getDefaultSpecialStructureRecognizer() {
        return defaultSSR;
    }
    
    /**
     * Return an object of molecular formula, pertaining to this molecule
     *
     * @return MolecularFormula the molecular formula of this molecule
     */    
    @Override
    public MolecularFormula getFormula() {
        if (molecularFormula == null) {
            molecularFormula = new MolecularFormula(this);
        } // end if
        
        return molecularFormula;
    }

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
     * @param coords the coordinates with size 3N
     * @param updateConnectivity is the connectivity to be updated?
     * @throws IllegalArgumentException (a RuntimeException), if the array
     *         size does not match 3N.
     */
    @Override
    public void resetAtomCoordinates(double [] coords,
                  boolean updateConnectivity) throws IllegalArgumentException {
        // first ckeck if the size of coords is correct
        if (coords.length != getNumberOfAtoms()*3)
            throw new IllegalArgumentException("Size of coords array should " 
                    + "be exactly: " + getNumberOfAtoms()*3
                    + ". You provided an array with size: " + coords.length);

        // note that the state is changed, before its actually changed!
        stateChanged = true;

        // reset center of mass
        centerOfMass.setX(0.0);
        centerOfMass.setY(0.0);
        centerOfMass.setZ(0.0);

        // if its ok, we assume all is well
        double x, y, z;
        int idxCoord = 0;
        for(Atom atom : atomList) {
            x = coords[idxCoord];
            y = coords[idxCoord+1];
            z = coords[idxCoord+2];

            atom.setAtomCenter(new Point3D(x, y, z));

            double mass = atomInfo.getAtomicWeight(atom.getSymbol());
            centerOfMass.setX(centerOfMass.getX() + (mass * x));
            centerOfMass.setY(centerOfMass.getY() + (mass * y));
            centerOfMass.setZ(centerOfMass.getZ() + (mass * z));
            
            idxCoord += 3;
        } // end for

        if (updateConnectivity)
            msce.setEventType(MoleculeStateChangeEvent.MAJOR_MODIFICATION);
        else
            msce.setEventType(MoleculeStateChangeEvent.REFRESH_EVENT);

        fireMoleculeStateChangeListenerMoleculeChanged(msce);
    }
    
    /**
     * Do a breadth first traversal of the the molecular graph starting
     * with the specified atomIndex and return the traversed path as an Iterator
     * of atom indices.
     *
     * @param atomIndex the index from where to begin traversal
     * @return Iterator object containing atom indices visited in that order
     */
    @Override
    public java.util.Iterator<Integer> traversePath(int atomIndex) {        
        ArrayList<Integer> visited = new ArrayList<Integer>(atomList.size());                
        
        // push the current index
        visited.add(new Integer(atomIndex));
        
        // then traverse others and record
        traverseAndRecordBFS(atomIndex, visited);
        
        for(int i=0; i<atomList.size(); i++) {
            if (visited.size() == atomList.size()) break;
                        
            traverseAndRecordBFS(i, visited);
        } // end for
        
        return visited.iterator();
    }
    
    /**
     * Do a breadth first traversal of the the molecular graph starting
     * with the specified atomIndex and return the traversed path as an Iterator
     * of atom indices.
     *
     * @param atomIndex the index from where to begin traversal
     * @param noDepth do not go to higher depths
     * @return Iterator object containing atom indices visited in that order
     */
    @Override
    public java.util.Iterator<Integer> traversePath(int atomIndex,
                                                    boolean noDepth) {        
        if (!noDepth) return traversePath(atomIndex);
        
        ArrayList<Integer> visited = new ArrayList<Integer>(atomList.size());                
        
        // then traverse others and record
        traverseAndRecordDFSNoWeak(atomIndex, visited);
        
        return visited.iterator();
    }
    
    /**
     * traverse and record the path ... DFS
     */
    private void traverseAndRecordDFS(int atomIndex, 
                                      ArrayList<Integer> visited,
                                      int recursionLevel) {
        
        if (recursionLevel == 4) return;
                
        Enumeration<Integer> connectList = atomList.get(atomIndex)
                                          .getConnectedList().keys();
        
        while(connectList.hasMoreElements()) {
            Integer toVisit = connectList.nextElement();
            
            // visited already? then cycle
            if (visited.contains(toVisit)) continue;                        
            
            // push the current index
            visited.add(new Integer(toVisit));

            // avoid visit of "dead-end" atom
            if (atomList.get(toVisit).getDegree() < 2) continue;
   
            // recursive call
            traverseAndRecordDFS(toVisit.intValue(), visited, recursionLevel + 1);
        } // end while                
    }

    /**
     * traverse and record the path ... DFS ... without traversing paths
     * represented by a weak bond
     */
    private void traverseAndRecordDFSNoWeak(int atomIndex, 
                                            ArrayList<Integer> visited) {
          PriorityQueue<Integer> toVisitList = new PriorityQueue<Integer>();
          toVisitList.add(atomIndex);

          while(toVisitList.size() != 0) {
              Integer toVisit = toVisitList.remove();

              // visited already? then cycle
              if (visited.contains(toVisit)) continue;

              visited.add(toVisit);

              Hashtable<Integer, BondType> connedAtoms
                                  = atomList.get(toVisit).getConnectedList();
              Enumeration<Integer> connectList = connedAtoms.keys();

              while(connectList.hasMoreElements()) {
                Integer visitThis = connectList.nextElement();

                // if already visited dont worry much
                if (visited.contains(visitThis)) continue;

                // also do no traverse weak bond path
                if (connedAtoms.get(visitThis).equals(BondType.WEAK_BOND))
                    continue;

                // mark it to be visted
                toVisitList.add(visitThis);
              } // end while
          } // end while
    }
    
    /**
     * traverse and record the path .. BFS
     */
    private void traverseAndRecordBFS(int atomIndex, 
                                      ArrayList<Integer> visited) {
        LinkedList<Integer> queue = new LinkedList<Integer>();
        Enumeration<Integer> connectList;
        
        // do a BFS
        while(true) {
            // traverse through its branches
            connectList = atomList.get(atomIndex).getConnectedList().keys();

            while(connectList.hasMoreElements()) {
                Integer toVisit = connectList.nextElement();

                // visited already? then cycle
                if (visited.contains(toVisit)) continue;
                
                // push the visited index
                visited.add(toVisit);

                // avoid visit of "dead-end" atom
                if (atomList.get(toVisit).getDegree() < 2) continue;
                                 
                // go to the depth of 4 so as to track the dihedrals
                traverseAndRecordDFS(toVisit, visited, 0);
                
                // and add to the queue
                queue.add(toVisit);
            } // end while

            // if queue is empty, get out
            if (queue.size() == 0) break;

            // else get the next unexplored node
            atomIndex = queue.remove();
        } // end while                                     
        
        // finally visit atoms with degree 1
        int noOfAtoms = atomList.size();
        
        for(int i=0; i<noOfAtoms; i++) {
            connectList = atomList.get(i).getConnectedList().keys();

            while(connectList.hasMoreElements()) {
                Integer toVisit = connectList.nextElement();               
                
                // visited already? then cycle
                if (visited.contains(toVisit)) continue;
                
                // do avoid visit of an atom with degree 1
                if (atomList.get(toVisit).getDegree() > 1) continue;                
                
                // push the visited index
                visited.add(toVisit);
            } // end while
        } // end for
        
        // clean up
        connectList = null;
        queue       = null;        
    }
    
    /**
     * Returns the default implementation of FragmentationScheme
     *
     * @return an object containing default implementation of FragmentationScheme
     */
    @Override
    public FragmentationScheme getDefaultFragmentationScheme() {
        // default fragmentation scheme
        try {
            if (defaultFragmentationScheme == null) {
               FragmentationSchemeFactory schemes = (FragmentationSchemeFactory)
                    Utility.getDefaultImplFor(FragmentationSchemeFactory.class)
                           .newInstance();
            
               defaultFragmentationScheme = schemes.getDefaultScheme();
               
               // add this to our list of schemes
               if (!fragmentationSchemeList
                    .contains(defaultFragmentationScheme)) {
                  fragmentationSchemeList.add(defaultFragmentationScheme);
               } // end if
               
               // add a default goodness probe 
               FragmentGoodnessProbeFactory gProbes = 
                 (FragmentGoodnessProbeFactory)
                   Utility.getDefaultImplFor(FragmentGoodnessProbeFactory.class)
                          .newInstance();
               
               defaultFragmentationScheme.addFragmentGoodnessProbe(
                      gProbes.getDefaultFragmentGoodnessProbe(
                                defaultFragmentationScheme, MoleculeImpl.this));
               
               // listen to changes in the fragmentation scheme and notify
               // them as changes in the molecule
               defaultFragmentationScheme.addFragmentListChangeListener(
                 new FragmentListChangeListener() {
                    @Override
                    public void fragmentListChanged(FragmentListChangeEvent e) {
                        // and fire the change event
                        stateChanged = true;
                        
                        msce.setEventType(
                                MoleculeStateChangeEvent.FRAGMENTS_MODIFIED);
                        msce.setFragmentListChangeEvent(e);
                        fireMoleculeStateChangeListenerMoleculeChanged(msce);
                    }
                 }
               );
            } // end if
        } catch (Exception e) {
            defaultFragmentationScheme = null;
            
            System.err.println("Warning! Unable to create a default fragment " 
                                + "scheme : " + e);
            e.printStackTrace();
            
            throw new UnsupportedOperationException("Warning! Unable to create " 
                                          + "a default fragment scheme : " + e);
        } // end of try catch block
        
        return defaultFragmentationScheme;
    }

    /**
     * Get a canonical ordering of atoms as a new Molecule object. It is
     * up to the implementation as to how this ordering is done.
     * Note: for the new Molecule object, the connectivity must be
     * explicitely build by the caller.
     *
     * @return a new Molecule object with different ordering
     */
    @Override
    public Molecule getCanonicalOrdering() {
        Molecule mol  = new MoleculeImpl(this.getTitle());

        // make a copy first
        try {
            for(Atom atm : atomList) {
                Atom atmCpy = (Atom) atm.clone();
                
                mol.addAtom(atmCpy);
                atmCpy.removeAllConnections();
            } // end for
        } catch(Exception e) {
            System.err.println("Exception in getCanonicalOrdering(): " + e.toString());
            e.printStackTrace();
        } // end of try .. catch

        // get center of mass
        Point3D com = mol.getCenterOfMass();
        for(Atom atm : atomList) {
            atm.setIndex(mol.closestTo(com));
        } // end for
        
        return mol;
    }

    /**
     * Return an atom index that is closest to a given point
     *
     * @param point the reference point
     * @return the atom index that is closest to the point
     */
    @Override
    public int closestTo(Point3D point) {
        int index = -1;
        
        int i = 0;
        double dist = Double.MAX_VALUE;
        for(Atom atm : atomList) {
            if (atm.distanceFrom(point) <= dist) {
                index = i; break;
            } // end if
            i++;
        } // end for

        return index;
    }

    /**
     * Do a simple re-indexing of the atoms
     */
    @Override
    public void reIndexAtom() {
        int noOfAtoms = getNumberOfAtoms();

        for(int i=0; i<noOfAtoms; i++) {
            getAtom(i).setIndex(i);
        } // end for
    }
} // end of class MoleculeImpl
