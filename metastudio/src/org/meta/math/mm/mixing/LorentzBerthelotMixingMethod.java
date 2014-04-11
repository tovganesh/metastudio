package org.meta.math.mm.mixing;

import org.meta.math.mm.LennardJonesParameters;

/**
 * Implements mixing of Lennard-Jones parameters using Lorentz-Berthelot
 * combining rules.
 * TODO allow non-Lorentz-Berthelot mixing e.g. Tang-Toennies
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LorentzBerthelotMixingMethod implements LennardJonesMixingMethod {
    /**
     * Combines Lennard-Jones parameters for different species using
     * the Lorentz-Berthelot mixing rules (i.e. geometric and arithmetic means).
     * @param a the Lennard-Jones parameters for the first species
     * @param b the Lennard-Jones parameters for the second species
     * @return the combined Lennard-Jones parameters
     */
    @Override
    public LennardJonesParameters getCombinedParameters(LennardJonesParameters a,
            LennardJonesParameters b) {
        double combinedBondRadius = (a.getBondRadius() + b.getBondRadius()) / 2;
        double combinedWellDepth =
                Math.sqrt(a.getWellDepth() * b.getWellDepth());
        return new LennardJonesParameters(combinedBondRadius, combinedWellDepth);
    }

    /**
     * 
     * Combines Lennard-Jones collision diameters for different species using
     * the Lorentz-Berthelot rule, i.e. the arithmetic mean.
     * @param a the Lennard-Jones collision diameter for the first species
     * @param b the Lennard-Jones collision diameter for the second species
     * @return the combined collision diameter
     */
    @Override
    public double getCombinedBondRadius(double a, double b) {
        return (a + b) / 2;
    }

    /**
     * 
     * Combines Lennard-Jones well depths for different species using
     * the Lorentz-Berthelot rule, i.e. the geometric mean.
     * @param a the Lennard-Jones well depth for the first species
     * @param b the Lennard-Jones well depth for the second species
     * @return the combined well depth
     */
    @Override
    public double getCombinedWellDepth(double a, double b) {
        return Math.sqrt(a * b);
    }
}
