/**
 * ShellList.java
 *
 * Created on Jul 9, 2009
 */
package org.meta.math.qm.basis;

import java.util.HashMap;

/**
 * A place holder of various Shell objects
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ShellList {

    protected HashMap<Integer, Shell> shellList;

    /** Creates a new instance of ShellList */
    public ShellList() {
        shellList = new HashMap<Integer, Shell>();
    }

    /**
     * Add a new shell primitive, automatically creates a Shell object, if one
     * is not already present.
     *
     * @param cg the shell primitive to be added
     */
    public void addShellPrimitive(ContractedGaussian cg) {
        Shell theShell = shellList.get(cg.getTotalAngularMomentum());

        if (theShell == null) {
            theShell = new Shell(cg.getTotalAngularMomentum());
            shellList.put(cg.getTotalAngularMomentum(), theShell);
        } // end if

        theShell.addShellPrimitive(cg);
    }

    /**
     * Remove a shell primitive.
     *
     * @param cg the shell primitive to be removed
     */
    public void removeShellPrimitive(ContractedGaussian cg) {
        Shell theShell = shellList.get(cg.getTotalAngularMomentum());

        if (theShell == null) return;

        theShell.removeShellPrimitive(cg);

        // if there are no primitives left, we remove it from the list too
        if (theShell.getNumberOfShellPrimitives() == 0)
            shellList.remove(theShell.getAngularMomentum());
    }

    /**
     * Get a shell instance with the specified angular momentum number.
     *
     * @param shellAngularMomentum the desired shell anugular momentum number
     * @return the instanstance of Shell for the specified angular momentum
     *         number. If none is present, null is returned.
     */
    public Shell getShell(int shellAngularMomentum) {
        return shellList.get(shellAngularMomentum);
    }

    /**
     * Returns the total number of Shell instances recoreded by this list
     *
     * @return number of Shell instances
     */
    public int getNumberOfShells() {
        return shellList.size();
    }
}
