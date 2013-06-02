/**
 * Intensity.java
 *
 * Created on Feb 18, 2009
 */
package org.meta.molecule.property.vibrational;

/**
 * Represents a Intensity value corresponding to a FrequencyItem
 * (i.e. a frequency number).
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Intensity {

    public Intensity(double value, IntensityType type) {
        this.value = value;
        this.type  = type;
    }

    protected double value;

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public double getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(double value) {
        this.value = value;
    }

    protected IntensityType type;

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public IntensityType getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(IntensityType type) {
        this.type = type;
    }

}
