/*
 * FragmentGoodnessProbeFactory.java
 *
 * Created on April 3, 2005, 7:12 PM
 */

package org.meta.fragmentor;

import org.meta.molecule.Molecule;

/**
 * Defines the methods applicable to a factory for "goodness probes".
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FragmentGoodnessProbeFactory {
    /**
     * Get an implementation of FragmentGoodnessProbe with the specified name.
     *
     * @param fragmentationScheme the scheme to which the goodness probe will
     *                            be attached
     * @param molecule the molecule to which the fragmentationScheme is attached
     * @param goodnessProbeName a name with which the goodness probe is 
     *                          identified
     * @return FragmentGoodnessProbe an appropriate instance of goodness probe,
     *                               null otherwise.
     */
    public FragmentGoodnessProbe getFragmentGoodnessProbe(
                                       FragmentationScheme fragmentationScheme, 
                                       Molecule molecule,
                                       String goodnessProbeName
                                 );
    
    /**
     * Get default implementation of FragmentGoodnessProbe.
     *
     * @param fragmentationScheme the scheme to which the goodness probe will
     *                            be attached
     * @param molecule the molecule to which the fragmentationScheme is attached    
     * @return FragmentGoodnessProbe an appropriate instance of goodness probe,
     *                               null otherwise.
     */
    public FragmentGoodnessProbe getDefaultFragmentGoodnessProbe(
                                       FragmentationScheme fragmentationScheme, 
                                       Molecule molecule
                                 );
} // end of interface FragmentGoodnessProbeFactory
