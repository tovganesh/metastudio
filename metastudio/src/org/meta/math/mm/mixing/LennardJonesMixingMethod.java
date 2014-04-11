package org.meta.math.mm.mixing;

import org.meta.math.mm.LennardJonesParameters;

/**
 * Mixing method for Lennard-Jones collision diameter and well-depth parameters.
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface LennardJonesMixingMethod {
    /**
     * Combines Lennard-Jones parameters for different species.
     * @param a the Lennard-Jones parameters for the first species
     * @param b the Lennard-Jones parameters for the second species
     * @return the combined Lennard-Jones parameters
     */
    public LennardJonesParameters getCombinedParameters(LennardJonesParameters a,
            LennardJonesParameters b);

    /**
     * Combines Lennard-Jones collision diameters for different species.
     * @param a the Lennard-Jones collision diameter for the first species
     * @param b the Lennard-Jones collision diameter for the second species
     * @return the combined collision diameter
     */
    public abstract double getCombinedBondRadius(double a, double b);

    /**
     * Combines Lennard-Jones well depths for different species.
     * @param a the Lennard-Jones well depth for the first species
     * @param b the Lennard-Jones well depth for the second species
     * @return the combined well depth
     */
    public abstract double getCombinedWellDepth(double a, double b);
}
