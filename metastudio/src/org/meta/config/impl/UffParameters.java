package org.meta.config.impl;

import java.util.Iterator;
import java.util.List;
import org.meta.config.Parameter;
import org.meta.math.mm.LennardJonesParameters;

/**
 * Represents the Universal Force Field parameters for a single species.
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class UffParameters implements Parameter {
    private String symbol;
    private double effectiveCharge;
    private double electronegativity;
    private List<UffCoordinationParameters> coordinationParams;
    private LennardJonesParameters vdwParams;

    /**
     * Creates a new UffParameters holder for a species.
     * @param symbol the symbol for the species e.g. H_, Be3+2
     * @param vdwParams the Lennard-Jones parameters for van der Waals interactions
     * @param effectiveCharge the effective partial charge for electrostatic interactions
     * @param electronegativity the GMP electronegativity for bond length calculation
     */
    public UffParameters(String symbol, LennardJonesParameters vdwParams,
            double effectiveCharge,
            double electronegativity) {
        this.symbol = symbol;
        this.vdwParams = vdwParams;
        this.effectiveCharge = effectiveCharge;
        this.electronegativity = electronegativity;
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof UffParameters) {
            UffParameters lj = (UffParameters) value;
            this.symbol = lj.symbol;
            this.vdwParams = lj.vdwParams;
            this.effectiveCharge = lj.effectiveCharge;
            this.electronegativity = lj.electronegativity;
            this.coordinationParams = lj.coordinationParams;

        } else {
            throw new IllegalArgumentException(value.getClass().getName());
        }
    }

    /**
     * @return the species symbol e.g. H, N
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @return the van der Waals (Lennard-Jones) parameter for this species
     */
    public LennardJonesParameters getVdwParams() {
        return vdwParams;
    }

    /**
     * @return the effective partial charge for electrostatic interactions
     */
    public double getEffectiveCharge() {
        return effectiveCharge;
    }

    /**
     * @return the electronegativity constant chi_i as used in eq. 4 of UFF
     */
    public double getElectronegativity() {
        return electronegativity;
    }

    public List<UffCoordinationParameters> getCoordinationParams() {
        return coordinationParams;
    }

    public UffCoordinationParameters getCoordinationParams(String id) {
        Iterator<UffCoordinationParameters> iter = coordinationParams.iterator();
        while (iter.hasNext()) {
            UffCoordinationParameters coord = iter.next();
            if (coord.id.startsWith(id)) {
                return coord;
            }
        }
        return getDefaultCoordinationParams();
    }

    public UffCoordinationParameters getCoordinationParams(char id) {
        Iterator<UffCoordinationParameters> iter = coordinationParams.iterator();
        while (iter.hasNext()) {
            UffCoordinationParameters coord = iter.next();
            final String currentId = coord.id;
            if (currentId != null && !currentId.isEmpty() &&
                    currentId.charAt(0) == id) {
                return coord;
            }
        }
        return getDefaultCoordinationParams();
    }

    /**
     * @return the first coordination
     */
    public UffCoordinationParameters getDefaultCoordinationParams() {
        return coordinationParams.get(0);
    }

    public void setCoordinationParams(
            List<UffCoordinationParameters> coordinations) {
        this.coordinationParams = coordinations;
    }

    public class UffCoordinationParameters {
        protected String id;
        private double bondRadius;
        private double bondAngle;
        private double sp2TorsionalBarrier;
        private double sp3TorsionalBarrier;

        public UffCoordinationParameters(String id, double bondRadius,
                double bondAngle, double sp2TorsionalBarrier,
                double sp3TorsionalBarrier) {
            this.id = id;
            this.bondRadius = bondRadius;
            this.bondAngle = bondAngle;
            this.sp2TorsionalBarrier = sp2TorsionalBarrier;
            this.sp3TorsionalBarrier = sp3TorsionalBarrier;
        }

        public double getBondAngle() {
            return bondAngle;
        }

        public double getBondRadius() {
            return bondRadius;
        }

        public String getId() {
            return id;
        }

        public double getSp2TorsionalBarrier() {
            return sp2TorsionalBarrier;
        }

        public double getSp3TorsionalBarrier() {
            return sp3TorsionalBarrier;
        }
    }
}
