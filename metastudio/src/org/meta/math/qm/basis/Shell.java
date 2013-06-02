/**
 * Shell.java
 *
 * Created on Jul 8, 2009
 */
package org.meta.math.qm.basis;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Defines a Gaussian Shell, a collection of ContractedGaussian functions
 * with the same total angular momentum value.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Shell {

    protected ArrayList<ContractedGaussian> shellPrimitives;

    /** Creates a new instance of Shell */
    public Shell(int angularMomentum) {
        this.angularMomentum = angularMomentum;

        shellPrimitives = new ArrayList<ContractedGaussian>();
    }

    /**
     * Add a shell primitive to this Shell, note that there is no check
     * made here to see if the CG is really part of this shell
     *
     * @param cg the shell primitive to be added to this shell
     */
    public void addShellPrimitive(ContractedGaussian cg) {
        // not checking to see if this CG is really in this shell!
        shellPrimitives.add(cg);
    }

    /**
     * Remove a shell primitive from this Shell object
     *
     * @param cg the shell primitive to be removed
     */
    public void removeShellPrimitive(ContractedGaussian cg) {
        shellPrimitives.remove(cg);
    }

    /**
     * Return the list of all shell primitives
     *
     * @return Iterator object with list of shell primitives
     */
    public Iterator<ContractedGaussian> getAllShellPrimitives() {
        return shellPrimitives.iterator();
    }

    /**
     * Get the requested shell primitive
     *
     * @param primitiveIndex the requested index
     * @return instance of the required shell primitive
     */
    public ContractedGaussian getShellPrimitive(int primitiveIndex) {
        return shellPrimitives.get(primitiveIndex);
    }

    /**
     * Get total number of shell primitives of this Shell
     *
     * @return number of shell primitives in this shell
     */
    public int getNumberOfShellPrimitives() {
        return shellPrimitives.size();
    }

    protected int angularMomentum;

    /**
     * Get the value of angularMomentum
     *
     * @return the value of angularMomentum
     */
    public int getAngularMomentum() {
        return angularMomentum;
    }

    /**
     * Set the value of angularMomentum
     *
     * @param angularMomentum new value of angularMomentum
     */
    public void setAngularMomentum(int angularMomentum) {
        this.angularMomentum = angularMomentum;
    }
}
