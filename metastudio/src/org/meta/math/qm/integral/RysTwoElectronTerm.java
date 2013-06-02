/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.qm.integral;

import java.util.ArrayList;
import org.meta.math.Matrix;
import org.meta.math.geom.Point3D;
import org.meta.math.qm.Density;
import org.meta.math.qm.basis.ContractedGaussian;
import org.meta.math.qm.basis.Power;

/**
 * Two electron integrals using RYS qudrature.
 * The code be based on
 * PyQuante (<a href="http://pyquante.sf.net"> http://pyquante.sf.net </a>).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RysTwoElectronTerm extends TwoElectronTerm {

    /** Creates an instance of RysTwoElectronTerm */
    public RysTwoElectronTerm() {
    }
 
    /**
     * 2E coulomb interactions between 4 contracted Gaussians
     */
    @Override
    public double coulomb(ContractedGaussian a, ContractedGaussian b,
                          ContractedGaussian c, ContractedGaussian d) {
         ArrayList<Double> aExps, aCoefs, aNorms,
                              bExps, bCoefs, bNorms,
                              cExps, cCoefs, cNorms,
                              dExps, dCoefs, dNorms;
         
         aExps  = a.getExponents();
         aCoefs = a.getCoefficients();
         aNorms = a.getPrimNorms();
         Point3D aOrigin  = a.getOrigin();
         Power   aPower   = a.getPowers();

         bExps  = b.getExponents();
         bCoefs = b.getCoefficients();
         bNorms = b.getPrimNorms();
         Point3D bOrigin  = b.getOrigin();
         Power   bPower   = b.getPowers();

         cExps  = c.getExponents();
         cCoefs = c.getCoefficients();
         cNorms = c.getPrimNorms();
         Point3D cOrigin  = c.getOrigin();
         Power   cPower   = c.getPowers();

         dExps  = d.getExponents();
         dCoefs = d.getCoefficients();
         dNorms = d.getPrimNorms();
         Point3D dOrigin  = d.getOrigin();
         Power   dPower   = d.getPowers();

         // TODO: 
         return (a.getNormalization() * b.getNormalization()
                 * c.getNormalization() * d.getNormalization()
                 );
    }

    /**
     * coulomb repulsion term
     */
    @Override
    public double coulombRepulsion(
                    Point3D a, double aNorm, Power aPower, double aAlpha,
                    Point3D b, double bNorm, Power bPower, double bAlpha,
                    Point3D c, double cNorm, Power cPower, double cAlpha,
                    Point3D d, double dNorm, Power dPower, double dAlpha) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double coulomb(ContractedGaussian a, ContractedGaussian b, 
                          ContractedGaussian c, ContractedGaussian d, 
                          Density density, Matrix jMat, Matrix kMat) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
