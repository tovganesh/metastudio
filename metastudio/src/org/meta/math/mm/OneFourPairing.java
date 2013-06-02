package org.meta.math.mm;

/**
 * A simple representation of a 1,4 connection between atoms I and L.
 * Simply contains the indices (in the molecule) of each of the four atoms.
 * @author J. Milthorpe
 */
public class OneFourPairing {
    public final int i;
    public final int j;
    public final int k;
    public final int l;

    public OneFourPairing(int i, int j, int k, int l) {
        this.i = i;
        this.j = j;
        this.k = k;
        this.l = l;
    }
}
