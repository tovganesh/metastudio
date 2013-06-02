/**
 * Force.java
 *
 * Created on Apr 17, 2009
 */
package org.meta.math.qm;

import org.meta.math.Vector3D;

/**
 * This interface defines the interfaces required for Force calculations on a
 * specific atomic position.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Force {

    /**
     * Compute the total foce on the specified atom and return the results
     * as a Vector3D object. Note that the SCF calculaions should have been
     * over before calling this method. This method in no way gaurenties
     * to check if the SCF was performend prior to calling this method.
     * 
     * @param atomIndex the atom index for which the force is to be computed
     * @param scfMethod the instance of the SCF method
     * @return the computed force
     */
    public Vector3D computeForce(int atomIndex, SCFMethod scfMethod);
}
