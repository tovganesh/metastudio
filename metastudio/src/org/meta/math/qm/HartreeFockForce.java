/**
 * HartreeFockForce.java
 *
 * Created on Apr 17, 2009
 */
package org.meta.math.qm;

import java.util.ArrayList;
import org.meta.config.impl.AtomInfo;
import org.meta.math.Matrix;
import org.meta.math.Vector;
import org.meta.math.Vector3D;
import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * Force calculations for the Hartree-Fock method.
 * This gradient (or Force) calculation is based on Appendix C of
 * Modern Quantum Chemistry by Szabo and Ostland, which describes
 * computing analytic gradients and geometry optimization.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class HartreeFockForce implements Force {

    /** Creates a new instance of HartreeFockForce */
    public HartreeFockForce() {
    }

    private int atomIndex;
    private SCFMethod scfMethod;

    /**
     * Compute the total force on the specified atom and return the results
     * as a Vector3D object. Note that the SCF calculations should have been
     * over before calling this method. This method in no way guarantees
     * to check if the SCF was performed prior to calling this method.
     *
     * @param atomIndex the atom index for which the force is to be computed
     * @param scfMethod the instance of the SCF method
     * @return the computed force
     */
    @Override
    public Vector3D computeForce(int atomIndex, SCFMethod scfMethod) {
        this.atomIndex = atomIndex;
        this.scfMethod = scfMethod;
        
        Vector3D force = computeNuclearDerivative();
        System.out.println("Nuclear /dr: " + force);
        force = force.add(computeDensityMatrixDerivative().mul(2.0));
        System.out.println("Density /dr: " + force);
        force = force.add(computeOneElectronDerivative().mul(2.0));
        System.out.println("1E /dr: " + force);
        force = force.add(computeTwoElectronDerivative());
        System.out.println("2E /dr: " + force);

        return force.mul(-1);
    }

    /** Compute the nuclear derivative contribution */
    private Vector3D computeNuclearDerivative() {
        Molecule mol = scfMethod.getMolecule();
        double nDer  = 0.0;

        AtomInfo ai = AtomInfo.getInstance();

        Atom a = mol.getAtom(atomIndex), b;
        double aCharge  = ai.getAtomicNumber(a.getSymbol());
        Point3D aCenter = a.getAtomCenterInAU();
        double ndx = 0.0, ndy = 0.0, ndz = 0.0;

        for(int i=0; i<mol.getNumberOfAtoms(); i++) {
            if (i != atomIndex) {
                b = mol.getAtom(i);
                Point3D bCenter = b.getAtomCenterInAU();
                
                nDer = aCharge * ai.getAtomicNumber(b.getSymbol()) /
                       Math.pow(bCenter.distanceSquaredFrom(aCenter), 1.5);

                ndx += (nDer * (bCenter.getX()-aCenter.getX()));
                ndy += (nDer * (bCenter.getY()-aCenter.getY()));
                ndz += (nDer * (bCenter.getZ()-aCenter.getZ()));
            } // end if
        } // end for

        return new Vector3D(ndx, ndy, ndz);
    }

    /** Compute the one electron derivative contribution */
    private Vector3D computeOneElectronDerivative() {
        Vector3D oneEDer = new Vector3D();
        HCore hCore      = scfMethod.getOneEI().getHCore();
        Density dens     = scfMethod.getDensity();
        ArrayList<HCore> hCoreDer = hCore.computeDerivative(atomIndex, scfMethod);

        oneEDer.setI(dens.mul(hCoreDer.get(0)).trace());
        oneEDer.setJ(dens.mul(hCoreDer.get(1)).trace());
        oneEDer.setK(dens.mul(hCoreDer.get(2)).trace());

        return oneEDer;
    }

    /** Compute the derivative contribution from density matrix*/
    private Vector3D computeDensityMatrixDerivative() {
        Vector3D denDer = new Vector3D();
        Overlap overlap = scfMethod.getOneEI().getOverlap();
        Density dens    = scfMethod.getDensity();
        ArrayList<Overlap> overlapDer = overlap.computeDerivative(atomIndex, scfMethod);
        Matrix eMat = new Matrix(new Vector(scfMethod.getOrbE()));
        Matrix qMat = dens.mul(eMat.mul(dens.transpose()));

        denDer.setI(qMat.mul(overlapDer.get(0)).trace());
        denDer.setJ(qMat.mul(overlapDer.get(1)).trace());
        denDer.setK(qMat.mul(overlapDer.get(2)).trace());

        return denDer;
    }

    /** Compute the two electron derivative contribution */
    private Vector3D computeTwoElectronDerivative() {
        Vector3D twoEDer = new Vector3D();

        ArrayList<GMatrix> gDer 
               = scfMethod.getGMatrix().computeDerivative(atomIndex, scfMethod);
        Density density = scfMethod.getDensity();

        twoEDer.setI(density.mul(gDer.get(0)).trace());
        twoEDer.setJ(density.mul(gDer.get(1)).trace());
        twoEDer.setK(density.mul(gDer.get(2)).trace());

        return twoEDer;
    }
}
