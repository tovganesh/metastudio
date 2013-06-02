/*
 * SCFMethod.java
 *
 * Created on August 5, 2004, 10:55 PM
 */

package org.meta.math.qm;

import org.meta.common.EventListenerList;
import org.meta.config.impl.AtomInfo;
import org.meta.math.optimizer.OptimizerFunction;
import org.meta.math.qm.event.SCFEvent;
import org.meta.math.qm.event.SCFEventListener;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * An abstract class representing the Self Consistent Field (SCF) method 
 * like Hartree-Fock, MP2 etc.
 * 
 * @author  V.Ganesh 
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class SCFMethod implements OptimizerFunction {
    
    /**
     * Holds value of property energyTolerance.
     */
    protected double energyTolerance;
    
    /**
     * Holds value of property densityTolerance.
     */
    protected double densityTolerance;
    
    /**
     * Holds value of property maxIteration.
     */
    protected int maxIteration;
    
    /**
     * The molecule under consideration
     */
    protected Molecule molecule;
    
    /**
     * The one electron integrals of the system
     */
    protected OneElectronIntegrals oneEI;
    
    /**
     * The two electron integrals of the system
     */
    protected TwoElectronIntegrals twoEI;        
    
    /**
     * Holds value of property density - the Density Matrix.
     */
    protected Density density;
    
    /**
     * Holds value of property mos - Molecular Orbitals.
     */
    protected MolecularOrbitals mos;
    
    /**
     * Holds value of property scfIteration.
     */
    protected int scfIteration;    
    
    /**
     * Holds value of property densityGuesser.
     */
    protected DensityGuesser densityGuesser;
    
    /**
     * Holds value of property guessInitialDM.
     */
    protected boolean guessInitialDM;
    
    /**
     * Holds value of property fock - the Fock matrix.
     */
    protected Fock fock;
    
    // default constants
    private static final int MAX_ITERATION = 20;
    private static final double ENERGY_TOLERANCE  = 1.0e-4;
    private static final double DENSITY_TOLERANCE = 1.0e-4;                
        
    /**
     * Utility field used by event firing mechanism.
     */
    private EventListenerList<SCFEventListener> listenerList =  null;    

    /**
     * Holds value of property energy.
     */
    protected double energy;
    
    /** Creates a new instance of SCFMethod */
    public SCFMethod(Molecule molecule,
                     OneElectronIntegrals oneEI,
                     TwoElectronIntegrals twoEI) {
        maxIteration = MAX_ITERATION;
        
        energyTolerance  = ENERGY_TOLERANCE;
        densityTolerance = DENSITY_TOLERANCE;
        
        this.molecule = molecule;
        this.oneEI    = oneEI;
        this.twoEI    = twoEI;
        
        guessInitialDM = false;        
    }
    
    /**
     * Perform the SCF
     */
    public abstract void scf();
    
    /**
     * compute nuclear repulsion energy
     *
     * @return the nuclear repulsion energy
     */
    public double nuclearEnergy() {
        double eNuke = 0.0;
        int i, j;
        int noOfAtoms = molecule.getNumberOfAtoms();
        
        Atom atomI, atomJ;
        
        // read in the atomic numbers
        int [] atomicNumbers = new int[noOfAtoms];
        AtomInfo ai = AtomInfo.getInstance();
        
        for(i=0; i<noOfAtoms; i++) {
            atomicNumbers[i] = ai.getAtomicNumber(
                                     molecule.getAtom(i).getSymbol());
        } // end for
        
        // and compute nuclear energy
        for(i=0; i<noOfAtoms; i++) {
            atomI = (Atom) molecule.getAtom(i);
            for(j=0; j<i; j++) {
                atomJ = (Atom) molecule.getAtom(j);
                
                eNuke += atomicNumbers[i] * atomicNumbers[j] 
                         / atomI.getAtomCenterInAU().distanceFrom(
                                atomJ.getAtomCenterInAU());
            } // end for
        } // end for
        
        return eNuke;
    }
    
    /**
     * Getter for property energyTolerance.
     * @return Value of property energyTolerance.
     */
    public double getEnergyTolerance() {
        return this.energyTolerance;
    }
    
    /**
     * Setter for property energyTolerance.
     * @param energyTolerance New value of property energyTolerance.
     */
    public void setEnergyTolerance(double energyTolerance) {
        this.energyTolerance = energyTolerance;
    }
    
    /**
     * Getter for property densityTolerance.
     * @return Value of property densityTolerance.
     */
    public double getDensityTolerance() {
        return this.densityTolerance;
    }
    
    /**
     * Setter for property densityTolerance.
     * @param densityTolerance New value of property densityTolerance.
     */
    public void setDensityTolerance(double densityTolerance) {
        this.densityTolerance = densityTolerance;
    }
    
    /**
     * Getter for property maxIteration.
     * @return Value of property maxIteration.
     */
    public int getMaxIteration() {
        return this.maxIteration;
    }
    
    /**
     * Setter for property maxIteration.
     * @param maxIteration New value of property maxIteration.
     */
    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }
    
    /**
     * Getter for property density.
     * @return Value of property density.
     */
    public Density getDensity() {
        return this.density;
    }
    
    /**
     * Getter for property mos.
     * @return Value of property mos.
     */
    public MolecularOrbitals getMos() {
        return this.mos;
    }
    
    /**
     * Getter for property orbE.
     * @return Value of property orbE.
     */
    public double[] getOrbE() {
        return this.mos.getOrbitalEnergies();
    }
    
    /**
     * Getter for property scfIteration.
     * @return Value of property scfIteration.
     */
    public int getScfIteration() {
        return this.scfIteration;
    }
    
    /**
     * Getter for property molecule.
     * @return Value of property molecule.
     */
    public Molecule getMolecule() {
        return this.molecule;
    }
    
    /**
     * Getter for property oneEI.
     * @return Value of property oneEI.
     */
    public OneElectronIntegrals getOneEI() {
        return this.oneEI;
    }
    
    /**
     * Getter for property twoEI.
     * @return Value of property twoEI.
     */
    public TwoElectronIntegrals getTwoEI() {
        return this.twoEI;
    }
    
    /**
     * Getter for property densityGuesser.
     * @return Value of property densityGuesser.
     */
    public DensityGuesser getDensityGuesser() {
        return this.densityGuesser;
    }
    
    /**
     * Setter for property densityGuesser.
     * @param densityGuesser New value of property densityGuesser.
     */
    public void setDensityGuesser(DensityGuesser densityGuesser) {
        this.densityGuesser = densityGuesser;
    }
    
    /**
     * Getter for property guessInitialDM.
     * @return Value of property guessInitialDM.
     */
    public boolean isGuessInitialDM() {
        return this.guessInitialDM;
    }
    
    /**
     * Setter for property guessInitialDM.
     * @param guessInitialDM New value of property guessInitialDM.
     */
    public void setGuessInitialDM(boolean guessInitialDM) {
        this.guessInitialDM = guessInitialDM;
    }
    
    /**
     * Getter for property fock.
     * @return Value of property fock.
     */
    public Fock getFock() {
        return this.fock;
    }

    protected GMatrix gMatrix;

    /**
     * Get the value of gMatrix
     *
     * @return the value of gMatrix
     */
    public GMatrix getGMatrix() {
        return gMatrix;
    }

    /**
     * Registers SCFEventListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addSCFEventListener(SCFEventListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<SCFEventListener>();
        }
        listenerList.add(SCFEventListener.class, listener);
    }
    
    /**
     * Removes SCFEventListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeSCFEventListener(SCFEventListener listener) {
        listenerList.remove(SCFEventListener.class, listener);
    }
    
    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    protected void fireSCFEventListenerScfEventOccured(SCFEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        
        for(Object listener : listeners) {
            ((SCFEventListener)listener).scfEventOccured(event);
        } // end for        
    }

    /**
     * Getter for property energy.
     * @return Value of property energy.
     */
    public double getEnergy() {
        return this.energy;
    }

    /**
     * A string representation return the current computed energy
     *
     * @return the String representation of this object
     */
    @Override
    public String toString() {
        return getEnergy() + "";
    }
} // end of class SCFMethod
