package org.meta.math.mm.mixing;

import org.meta.math.mm.LennardJonesParameters;

/**
 * Implements straight geometric combination of Lennard-Jones parameters.
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GeometricMixingMethod implements LennardJonesMixingMethod {
    /**
     * Combines Lennard-Jones parameters for different species using
     * the geometric mean.
     * @param a the Lennard-Jones parameters for the first species
     * @param b the Lennard-Jones parameters for the second species
     * @return the combined Lennard-Jones parameters
     */
    @Override
    public LennardJonesParameters getCombinedParameters(LennardJonesParameters a,
            LennardJonesParameters b) {
        double combinedBondRadius = Math.sqrt(a.getBondRadius() * b.
                getBondRadius());
        double combinedWellDepth =
                Math.sqrt(a.getWellDepth() * b.getWellDepth());
        return new LennardJonesParameters(combinedBondRadius,
                combinedWellDepth);
    }

    /**
     * Combines Lennard-Jones collision diameters as the geometric mean.
     * @param a the Lennard-Jones collision diameter for the first species
     * @param b the Lennard-Jones collision diameter for the second species
     * @return the combined collision diameter
     */
    @Override
    public double getCombinedBondRadius(double a, double b) {
        return Math.sqrt(a * b);
    }

    /**
     * Combines Lennard-Jones well depths as the geometric mean.
     * @param a the Lennard-Jones well depth for the first species
     * @param b the Lennard-Jones well depth for the second species
     * @return the combined well depth
     */
    @Override
    public double getCombinedWellDepth(double a, double b) {
        return Math.sqrt(a * b);
    }
}
