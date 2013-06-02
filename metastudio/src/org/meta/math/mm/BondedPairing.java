package org.meta.math.mm;

import org.meta.molecule.BondType;

/**
 * A simple representation of a bonded pairing between atoms I and J.
 * @author J. Milthorpe
 */
public class BondedPairing {
    public final int i;
    public final int j;
    public final BondType connection;

    public BondedPairing(int i, int j, BondType connection) {
        this.i = i;
        this.j = j;
        this.connection = connection;
    }
}
