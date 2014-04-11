package org.meta.math.mm;

/**
 * A simple representation of a 1,3 connection between atoms I andK.
 * Simply contains the indices (in the molecule) of each of the three atoms.
 * @author J. Milthorpe
 */
public class OneThreePairing {
    public final int i;
    public final int j;
    public final int k;

    public OneThreePairing(int i, int j, int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }
}
