/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.qm.property;

import java.util.ArrayList;
import org.meta.math.Vector;
import org.meta.math.geom.Point3D;
import org.meta.math.qm.BasisFunctions;
import org.meta.math.qm.MolecularOrbitals;
import org.meta.math.qm.SCFMethod;
import org.meta.math.qm.basis.ContractedGaussian;
import org.meta.molecule.Molecule;

/**
 * Computes MO density on specified points for a Molecule.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MODensity extends OneElectronProperty {

    private int monumber;

    private int nbf;
    private double [][] mos;    
    
    private ArrayList<ContractedGaussian> bfs;

    /** Creates a new instance of MODensity */
    public MODensity(SCFMethod scfMethod) {
        super(scfMethod);

        bfs = scfMethod.getOneEI().getBasisFunctions().getBasisFunctions();
        nbf = bfs.size();

        mos = scfMethod.getMos().getCoefficients();

        monumber = scfMethod.getMolecule().getNumberOfElectrons()/2;
    }

    /** Creates a new instance of MODensity */
    public MODensity(SCFMethod scfMethod, int monumber) {
        this(scfMethod);

        this.monumber = monumber;
    }

    /** Creates a new instance of MODensity */
    public MODensity(Molecule molecule, BasisFunctions bfs,
                     MolecularOrbitals mos) {

        this.bfs = bfs.getBasisFunctions();
        this.nbf = this.bfs.size();
        this.mos = mos.getCoefficients();
        this.monumber = molecule.getNumberOfElectrons()/2;
    }

    /** Creates a new instance of MODensity */
    public MODensity(Molecule molecule, BasisFunctions bfs,
                     MolecularOrbitals mos, int monumber) {

        this.bfs = bfs.getBasisFunctions();
        this.nbf = this.bfs.size();
        this.mos = mos.getCoefficients();
        this.monumber = monumber;
    }

    /**
     * Computes the one electron property on the specified point and returns its
     * value at the specified point. <br>
     * Note that the unit of Point3D object must be a.u. No attempt is made
     * to verify this.
     *
     * @param point the point of interest
     * @return the value of this property at this point
     */
    @Override
    public double compute(Point3D point) {
        Vector amplitides = new Vector(nbf);
        double [] amp  = amplitides.getVector();
        double moden = 0.0, amp_m, mos_m;
        int m, l;

        for(m=0; m<nbf; m++)
            amp[m] = bfs.get(m).amplitude(point);

        moden = 0.0;

        for (m = 0; m < nbf; m++) {
            amp_m = amp[m];
            mos_m = mos[monumber][m];

            for (l = 0; l < nbf; l++) {
                moden += mos_m*mos[monumber][l]*amp[l]*amp_m;
            } // end for
        } // end for
        
        return moden;
    }
}
