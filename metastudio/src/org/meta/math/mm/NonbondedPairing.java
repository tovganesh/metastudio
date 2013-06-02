package org.meta.math.mm;

/**
 * A simple representation of a nonbonded pairing between atoms I and J.
 * @author J. Milthorpe
 */
public class NonbondedPairing {
    public final int i;
    public final int j;

    public NonbondedPairing(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
