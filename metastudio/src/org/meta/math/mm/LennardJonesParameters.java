package org.meta.math.mm;

/**
 * Represents the parameters for a Lennard-Jones potential, e.g. to model the
 * van der Waals interaction between non-bonded particles.
 * TODO implement scaled vdW params as per eq. 23 of UFF
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LennardJonesParameters {
    private static final double DEFAULT_SCALE = 12.0;
    private final double scale; // note: not currently used
    private final double bondRadius;
    private final double wellDepth;

    /**
     * Creates a new LennardJonesParameters with a default scale of 12.
     * @param bondRadius the van der Waals bond radius or equilibrium separation distance
     * @param wellDepth the van der Waals well depth or minimum energy in hartrees in ångströms
     */
    public LennardJonesParameters(final double bondRadius, final double wellDepth) {
        this.scale = DEFAULT_SCALE;
        this.bondRadius = bondRadius;
        this.wellDepth = wellDepth;
    }

    /**
     * Creates a new LennardJonesParameters.
     * @param scale the scale parameter i.e. the exponent of the repulsion term
     * @param vdwBondRadius the van der Waals bond radius or equilibrium separation distance
     * @param vdwWellDepth the van der Waals well depth or minimum energy in hartrees in ångströms
     */
    public LennardJonesParameters(final double scale, final double vdwBondRadius,
            final double vdwWellDepth) {
        this.scale = scale;
        this.bondRadius = vdwBondRadius;
        this.wellDepth = vdwWellDepth;
    }

    /**
     * @return the van der Waals bond radius or equilibrium separation distance in ångströms
     */
    public double getBondRadius() {
        return bondRadius;
    }

    /**
     * @return the van der Waals well depth or minimum energy in hartrees
     */
    public double getWellDepth() {
        return wellDepth;
    }

    /**
     * @return Lennard-Jones parameter A (repulsive part, varies inversely as r^12).
     */
    public double getParamA() {
        return wellDepth * Math.pow(bondRadius, 12);
    }

    /**
     * @return Lennard-Jones parameter B (attractive part, varies inversely as r^6).
     */
    public double getParamB() {
        return 2 * wellDepth * Math.pow(bondRadius, 6);
    }
}
