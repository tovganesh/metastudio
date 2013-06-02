/**
 * FockExtrapolator.java
 *
 * Created on 18/01/2010
 */

package org.meta.math.qm;

/**
 * Generic interface for Fock matrix extrapolation
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FockExtrapolator {

    /** Initialize this interpolator */
    public void init();

    /** Get the next exrapolated fock matrix
     *
     * @param currentFock the current fock
     * @param overlap the overlap matrix
     * @param density the current density matrix
     * @return exrapolated fock
     */
    public Fock next(Fock currentFock, Overlap overlap, Density density);
}
