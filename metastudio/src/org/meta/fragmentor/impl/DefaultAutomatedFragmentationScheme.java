/**
 * DefaultAutomatedFragmentationScheme.java
 *
 * Created on 09/01/2010
 */

package org.meta.fragmentor.impl;

import org.meta.common.Utility;
import org.meta.fragmentor.FragmentationException;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;

/**
 * Implements the default fragmentation scheme. This is a modified and improved
 * version of the algorithm described in:
 * J. Chem. Phys., Volume 125, Issue 10, pp. 104109 (2006).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DefaultAutomatedFragmentationScheme
             extends DefaultFragmentationScheme {

    private static double DEFAULT_RADIUS_CUTOFF = 3.5;
    private static int DEFAULT_MAX_ATOMS_PER_FRAGMENT = 30;

    /** Creates a new instance of DefaultAutomatedFragmentationScheme */
    public DefaultAutomatedFragmentationScheme() throws ClassNotFoundException,
                                                        InstantiationException,
                                                        IllegalAccessException {
        super();

        this.name = "defaultAutoScheme";

        this.radiusCutoff = DEFAULT_RADIUS_CUTOFF;
        this.maxAtomsPerFragment = DEFAULT_MAX_ATOMS_PER_FRAGMENT;
    }

    /**
     * A automated fragmenation scheme should implement this method to generate
     * the fragments automatically, else should quitely return (and should not
     * throw an exception).
     *
     * @param molecule the molecule which is to be fragmented
     * @throws FragmentationException if unable to fragment.
     */
    @Override
    public void doAutomatedFragmentation(Molecule molecule) throws FragmentationException {
        // TODO:
        try {
            // Step 0: 'reindex' atoms to be always consistant irrespective of
            //         the input order
            Molecule reIndexedMolecule = molecule.getCanonicalOrdering();

            MoleculeBuilder mb = (MoleculeBuilder)
                    Utility.getDefaultImplFor(MoleculeBuilder.class).newInstance();

            mb.makeConnectivity(reIndexedMolecule);
            
            // Step 1: form spheres with radius 'r' around each atom
            //         and extract initial fragments
            

            // Step 2: merge connected fragments based on size constraint

            // Step 3: follow a general merge based on size constraint
        } catch (Exception ex) {
            System.err.println("Exception in doAutomatedFragmentation(): " + ex.toString());
            ex.printStackTrace();
        } // end of try .. catch
    }

    protected double radiusCutoff;

    /**
     * Get the value of radiusCutoff
     *
     * @return the value of radiusCutoff
     */
    public double getRadiusCutoff() {
        return radiusCutoff;
    }

    /**
     * Set the value of radiusCutoff
     *
     * @param radiusCutoff new value of radiusCutoff
     */
    public void setRadiusCutoff(double radiusCutoff) {
        this.radiusCutoff = radiusCutoff;
    }

    protected int maxAtomsPerFragment;

    /**
     * Get the value of maxAtomsPerFragment
     *
     * @return the value of maxAtomsPerFragment
     */
    public int getMaxAtomsPerFragment() {
        return maxAtomsPerFragment;
    }

    /**
     * Set the value of maxAtomsPerFragment
     *
     * @param maxAtomsPerFragment new value of maxAtomsPerFragment
     */
    public void setMaxAtomsPerFragment(int maxAtomsPerFragment) {
        this.maxAtomsPerFragment = maxAtomsPerFragment;
    }
    
    /**
     * overloaded toString()
     */
    @Override
    public String toString() {
        return "The Default auto fragmentation scheme";
    }
}
