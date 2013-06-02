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
 * Head-Gordon/Pople scheme of evaluating two-electron integrals.
 * 
 * The code be based on
 * PyQuante (<a href="http://pyquante.sf.net"> http://pyquante.sf.net </a>).
 * See M. Head-Gordon and J. A. Pople, J. Chem. Phys. <b>89</b>, 5777 (1988)
 * for more details.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class HGPTwoElectronTerm extends TwoElectronTerm {

    /** Creates a new instance of HGPTwoElectronTerm */
    public HGPTwoElectronTerm() {
    }

    /**
     * 2E coulomb interactions between 4 contracted Gaussians
     */
    @Override
    public double coulomb(ContractedGaussian a, ContractedGaussian b,
                          ContractedGaussian c, ContractedGaussian d) {
         return (a.getNormalization() * b.getNormalization()
                 * c.getNormalization() * d.getNormalization()
                 * contrHrr(a.getOrigin(), a.getPowers(),
                            a.getCoefficients(), a.getExponents(), a.getPrimNorms(),
                            b.getOrigin(), b.getPowers(),
                            b.getCoefficients(), b.getExponents(), b.getPrimNorms(),
                            c.getOrigin(), b.getPowers(),
                            c.getCoefficients(), c.getExponents(), c.getPrimNorms(),
                            d.getOrigin(), d.getPowers(),
                            d.getCoefficients(), d.getExponents(), d.getPrimNorms()
                            ));
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
        return vrr(a, aNorm, aPower, aAlpha, b, bNorm, bAlpha,
                   c, cNorm, cPower, cAlpha, d, dNorm, dAlpha, 0);
    }
    
    /**
     * HRR (Horizontal Recurrance Relation)
     */
    protected double contrHrr(Point3D a, Power aPower, ArrayList<Double> aCoeff,
                              ArrayList<Double> aExps, ArrayList<Double> aNorms,
                              Point3D b, Power bPower, ArrayList<Double> bCoeff,
                              ArrayList<Double> bExps, ArrayList<Double> bNorms,
                              Point3D c, Power cPower, ArrayList<Double> cCoeff,
                              ArrayList<Double> cExps, ArrayList<Double> cNorms,
                              Point3D d, Power dPower, ArrayList<Double> dCoeff,
                              ArrayList<Double> dExps, ArrayList<Double> dNorms) {
        int la = aPower.getL(), ma = aPower.getM(), na = aPower.getN();
        int lb = bPower.getL(), mb = bPower.getM(), nb = bPower.getN();
        int lc = cPower.getL(), mc = cPower.getM(), nc = cPower.getN();
        int ld = dPower.getL(), md = dPower.getM(), nd = dPower.getN();

        if (lb > 0) {
            Power newBPower = new Power(lb-1,mb,nb);
            return (contrHrr(a, new Power(la+1,ma,na), aCoeff, aExps, aNorms, 
                             b, newBPower, bCoeff, bExps, bNorms,
                             c, cPower, cCoeff, cExps, cNorms,
                             d, dPower, dCoeff, dExps, dNorms)
                   + (a.getX()-b.getX())
                     * contrHrr(a, aPower, aCoeff, aExps, aNorms,
                                b, newBPower, bCoeff, bExps, bNorms,
                                c, cPower, cCoeff, cExps, cNorms,
                                d, dPower, dCoeff, dExps, dNorms));
        } else if (mb > 0) {
            Power newBPower = new Power(lb,mb-1,nb);
            return (contrHrr(a, new Power(la,ma+1,na), aCoeff, aExps, aNorms,
                             b, newBPower, bCoeff, bExps, bNorms,
                             c, cPower, cCoeff, cExps, cNorms,
                             d, dPower, dCoeff, dExps, dNorms)
                   + (a.getY()-b.getY())
                     * contrHrr(a, aPower, aCoeff, aExps, aNorms,
                                b, newBPower, bCoeff, bExps, bNorms,
                                c, cPower, cCoeff, cExps, cNorms,
                                d, dPower, dCoeff, dExps, dNorms));
        } else if (nb > 0) {
            Power newBPower = new Power(lb,mb,nb-1);
            return (contrHrr(a, new Power(la,ma,na+1), aCoeff, aExps, aNorms,
                             b, newBPower, bCoeff, bExps, bNorms,
                             c, cPower, cCoeff, cExps, cNorms,
                             d, dPower, dCoeff, dExps, dNorms)
                   + (a.getZ()-b.getZ())
                     * contrHrr(a, aPower, aCoeff, aExps, aNorms,
                                b, newBPower, bCoeff, bExps, bNorms,
                                c, cPower, cCoeff, cExps, cNorms,
                                d, dPower, dCoeff, dExps, dNorms));
        } else if (ld > 0) {
            Power newDPower = new Power(ld-1,md,nd);
            return (contrHrr(a, aPower, aCoeff, aExps, aNorms, 
                             b, bPower, bCoeff, bExps, bNorms,
                             c, new Power(lc+1,mc,nc), cCoeff, cExps, cNorms,
                             d, newDPower, dCoeff, dExps, dNorms)
                   + (c.getX()-d.getX())
                     * contrHrr(a, aPower, aCoeff, aExps, aNorms, 
                                b, bPower, bCoeff, bExps, bNorms,
                                c, cPower, cCoeff, cExps, cNorms,
                                d, newDPower, dCoeff, dExps, dNorms));
        } else if (md > 0) {
            Power newDPower = new Power(ld,md-1,nd);
            return (contrHrr(a, aPower, aCoeff, aExps, aNorms,
                             b, bPower, bCoeff, bExps, bNorms,
                             c, new Power(lc,mc+1,nc), cCoeff, cExps, cNorms,
                             d, newDPower, dCoeff, dExps, dNorms)
                   + (c.getY()-d.getY())
                     * contrHrr(a, aPower, aCoeff, aExps, aNorms,
                                b, bPower, bCoeff, bExps, bNorms,
                                c, cPower, cCoeff, cExps, cNorms,
                                d, newDPower, dCoeff, dExps, dNorms));
        } else if (nd > 0) {
            Power newDPower = new Power(ld,md,nd-1);
            return (contrHrr(a, aPower, aCoeff, aExps, aNorms,
                             b, bPower, bCoeff, bExps, bNorms,
                             c, new Power(lc,mc,nc+1), cCoeff, cExps, cNorms,
                             d, newDPower, dCoeff, dExps, dNorms)
                + (c.getZ()-d.getZ())
                    * contrHrr(a, aPower, aCoeff, aExps, aNorms,
                               b, bPower, bCoeff, bExps, bNorms,
                               c, cPower, cCoeff, cExps, cNorms,
                               d, newDPower, dCoeff, dExps, dNorms));
        } // end if
        
        return contrVrr(a, aPower, aCoeff, aExps, aNorms,
                        b, bCoeff, bExps, bNorms,
                        c, cPower, cCoeff, cExps, cNorms,
                        d, dCoeff, dExps, dNorms);
    }

    /**
     * VRR (Vertical Recurrance Relation) contribution
     */
    protected double contrVrr(Point3D a, Power aPower, ArrayList<Double> aCoeff,
                              ArrayList<Double> aExps, ArrayList<Double> aNorms,
                              Point3D b, ArrayList<Double> bCoeff,
                              ArrayList<Double> bExps, ArrayList<Double> bNorms,
                              Point3D c, Power cPower, ArrayList<Double> cCoeff,
                              ArrayList<Double> cExps, ArrayList<Double> cNorms,
                              Point3D d, ArrayList<Double> dCoeff,
                              ArrayList<Double> dExps, ArrayList<Double> dNorms) {
        double value = 0.0;

        int i, j, k, l;
        double iaExp, iaCoef, iaNorm,
               jbExp, jbCoef, jbNorm,
               kcExp, kcCoef, kcNorm;
        
        for (i = 0; i < aExps.size(); i++) {
            iaCoef = aCoeff.get(i);
            iaExp = aExps.get(i);
            iaNorm = aNorms.get(i);

            for (j = 0; j < bExps.size(); j++) {
                jbCoef = bCoeff.get(j);
                jbExp = bExps.get(j);
                jbNorm = bNorms.get(j);

                for (k = 0; k < cExps.size(); k++) {
                    kcCoef = cCoeff.get(k);
                    kcExp = cExps.get(k);
                    kcNorm = cNorms.get(k);

                    for(l=0; l < dExps.size(); l++) {
                        value += iaCoef * jbCoef * kcCoef * dCoeff.get(l)
                                 * vrrWrapper(a, iaNorm, aPower, iaExp,
                                       b, jbNorm, jbExp,
                                       c, kcNorm, cPower, kcExp,
                                       d, dNorms.get(l), dExps.get(l), 0);
                    } // end for
                } // end for
            } // end for
        } // end for
        
        return value;
    }

    private final double sqrt2PI = Math.sqrt(2.0) * Math.pow(Math.PI, 1.25);

    /**
     * VRR (Vertical Recurrance Relation)
     */
    protected double vrrWrapper(
                         Point3D a, double aNorm, Power aPower, double aAlpha,
                         Point3D b, double bNorm, double bAlpha,
                         Point3D c, double cNorm, Power cPower, double cAlpha,
                         Point3D d, double dNorm, double dAlpha, int m) {
        return vrrNonRecursive(a, aNorm, aPower, aAlpha, b, bNorm, bAlpha,
                               c, cNorm, cPower, cAlpha, d, dNorm, dAlpha, m);
    }

    /**
     * VRR (Vertical Recurrance Relation)
     */
    protected double vrr(Point3D a, double aNorm, Power aPower, double aAlpha,
                         Point3D b, double bNorm, double bAlpha,
                         Point3D c, double cNorm, Power cPower, double cAlpha,
                         Point3D d, double dNorm, double dAlpha, int m) {
        double val = 0.0;

        Point3D p = IntegralsUtil.gaussianProductCenter(aAlpha, a, bAlpha, b);
        Point3D q = IntegralsUtil.gaussianProductCenter(cAlpha, c, dAlpha, d);
        double zeta = aAlpha + bAlpha;
        double eta  = cAlpha + dAlpha;
        double zetaPlusEta = zeta + eta;
        double zetaByZetaPlusEta = zeta / zetaPlusEta;
        double etaByZetaPlusEta  = eta / zetaPlusEta;
        Point3D w = IntegralsUtil.gaussianProductCenter(zeta, p, eta, q);
        
        int la = aPower.getL();
        int ma = aPower.getM();
        int na = aPower.getN();
        int lc = cPower.getL();
        int mc = cPower.getM();
        int nc = cPower.getN();
        
        if (nc > 0) {
           Power newCPower = new Power(lc, mc, nc-1);
           val = (q.getZ()-c.getZ())*vrr(a, aNorm, aPower, aAlpha,
                                         b, bNorm, bAlpha,
                                         c, cNorm, newCPower, cAlpha,
                                         d, dNorm, dAlpha, m)
               + (w.getZ()-q.getZ())*vrr(a, aNorm, aPower, aAlpha,
                                         b, bNorm, bAlpha,
                                         c, cNorm, newCPower, cAlpha,
                                         d, dNorm, dAlpha, m+1);

           if (nc > 1) {
              Power newCPower1 = new Power(lc, mc, nc-2);
              val += 0.5*(nc-1) / eta*(vrr(a, aNorm, aPower, aAlpha,
                                           b, bNorm, bAlpha,
                                           c, cNorm, newCPower1, cAlpha,
                                           d, dNorm, dAlpha, m)
                    -zetaByZetaPlusEta*vrr(a, aNorm, aPower, aAlpha,
                                           b, bNorm, bAlpha,
                                           c, cNorm, newCPower1, cAlpha,
                                           d, dNorm, dAlpha, m+1));
           } // end if

           if (na > 0) {
              val += 0.5*na/zetaPlusEta*vrr(a, aNorm, new Power(la, ma, na-1),
                                            aAlpha,
                                            b, bNorm, bAlpha,
                                            c, cNorm, newCPower,
                                            cAlpha,
                                            d, dNorm, dAlpha, m+1);
           } // end if

           return val;
        } else if (mc > 0) {
            Power newCPower = new Power(lc, mc-1, nc);
            val = (q.getY()-c.getY())*vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower, cAlpha,
                                          d, dNorm, dAlpha, m)
                + (w.getY()-q.getY())*vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower, cAlpha,
                                          d, dNorm, dAlpha, m+1);

            if (mc > 1) {
               Power newCPower1 = new Power(lc, mc-2, nc);
               val += 0.5*(mc-1)/eta*(vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower1, cAlpha,
                                          d, dNorm, dAlpha, m)
                   -zetaByZetaPlusEta*vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower1, cAlpha,
                                          d, dNorm, dAlpha, m+1));
            } // end if

            if (ma > 0) {
                val += 0.5*ma/zetaPlusEta*vrr(a, aNorm, new Power(la, ma-1, na),
                                              aAlpha,
                                              b, bNorm, bAlpha,
                                              c, cNorm, newCPower,
                                              cAlpha,
                                              d, dNorm, dAlpha, m+1);
            } // end if
            
            return val;
        } else if (lc > 0) {
            Power newCPower = new Power(lc-1, mc, nc);
            val = (q.getX()-c.getX())*vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower, cAlpha,
                                          d, dNorm, dAlpha, m)
                + (w.getX()-q.getX())*vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower, cAlpha,
                                          d, dNorm, dAlpha, m+1);

            if (lc > 1) {
               Power newCPower1 = new Power(lc-2, mc, nc);
               val += 0.5*(lc-1)/eta*(vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower1, cAlpha,
                                          d, dNorm, dAlpha, m)
                   -zetaByZetaPlusEta*vrr(a, aNorm, aPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, newCPower1, cAlpha,
                                          d, dNorm, dAlpha, m+1));
            } // end if

            if (la > 0) {
                val += 0.5*la/zetaPlusEta*vrr(a, aNorm, new Power(la-1, ma, na),
                                              aAlpha,
                                              b, bNorm, bAlpha,
                                              c, cNorm, newCPower,
                                              cAlpha,
                                              d, dNorm, dAlpha, m+1);
            } // end if

            return val;
        } else if (na > 0) {
            Power newAPower = new Power(la, ma, na-1);
            val = (p.getZ()-a.getZ())*vrr(a, aNorm, newAPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, cPower, cAlpha,
                                          d, dNorm, dAlpha, m) 
                + (w.getZ()-p.getZ())*vrr(a, aNorm, newAPower, aAlpha,
                                          b, bNorm, bAlpha,
                                          c, cNorm, cPower, cAlpha,
                                          d, dNorm, dAlpha, m+1);

            if (na > 1) {
               Power newAPower1 = new Power(la, ma, na-2);
               val += 0.5*(na-1)/zeta*(vrr(a, aNorm, newAPower1, aAlpha,
                                           b, bNorm, bAlpha,
                                           c, cNorm, cPower, cAlpha,
                                           d, dNorm, dAlpha, m)
                     -etaByZetaPlusEta*vrr(a, aNorm, newAPower1, aAlpha,
                                           b, bNorm, bAlpha,
                                           c, cNorm, cPower, cAlpha,
                                           d, dNorm, dAlpha, m+1));
            } // end if

            return val;
        } else if (ma > 0) {
            Power newAPower = new Power(la, ma-1, na);
            val = (p.getY()-a.getY())*vrr(a, aNorm, newAPower, aAlpha,
                                          b, aNorm, aAlpha,
                                          c, aNorm, cPower, cAlpha,
                                          d, aNorm, dAlpha, m)
                + (w.getY()-p.getY())*vrr(a, aNorm, newAPower, aAlpha,
                                          b, aNorm, aAlpha,
                                          c, aNorm, cPower, cAlpha,
                                          d, aNorm, dAlpha, m+1);

            if (ma > 1) {
               Power newAPower1 = new Power(la, ma-2, na);
               val += 0.5*(ma-1)/zeta*(vrr(a, aNorm, newAPower1,
                                           aAlpha,
                                           b, aNorm, aAlpha,
                                           c, aNorm, cPower, cAlpha,
                                           d, aNorm, dAlpha, m)
                     -etaByZetaPlusEta*vrr(a, aNorm, newAPower1,
                                           aAlpha,
                                           b, aNorm, aAlpha,
                                           c, aNorm, cPower, cAlpha,
                                           d, aNorm, dAlpha, m+1));
            } // end if
            
            return val;
        } else if (la > 0) {
            Power newAPower = new Power(la-1, ma, na);
            val = (p.getX()-a.getX())*vrr(a, aNorm, newAPower, aAlpha,
                                          b, aNorm, aAlpha,
                                          c, aNorm, cPower, cAlpha,
                                          d, aNorm, dAlpha, m)
                + (w.getX()-p.getX())*vrr(a, aNorm, newAPower, aAlpha,
                                          b, aNorm, aAlpha,
                                          c, aNorm, cPower, cAlpha,
                                          d, aNorm, dAlpha, m+1);

            if (la > 1) {
                Power newAPower1 = new Power(la-2, ma, na);
                val += 0.5*(la-1)/zeta*(vrr(a, aNorm, newAPower1, aAlpha,
                                            b, aNorm, aAlpha,
                                            c, aNorm, cPower, cAlpha,
                                            d, aNorm, dAlpha, m)
                      -etaByZetaPlusEta*vrr(a, aNorm, newAPower1, aAlpha,
                                            b, aNorm, aAlpha,
                                            c, aNorm, cPower, cAlpha,
                                            d, aNorm, dAlpha, m+1));
            } // end if
            
            return val;
        } // end if

        double rab2 = a.distanceSquaredFrom(b);
        double Kab  = sqrt2PI / zeta * Math.exp(-aAlpha*bAlpha / zeta*rab2);
        double rcd2 = c.distanceSquaredFrom(d);
        double Kcd  = sqrt2PI / eta * Math.exp(-cAlpha*dAlpha / eta*rcd2);
        double rpq2 = p.distanceSquaredFrom(q);
        double T    = zeta*eta / zetaPlusEta*rpq2;

        val = aNorm*bNorm*cNorm*dNorm*Kab*Kcd/Math.sqrt(zetaPlusEta)
              * IntegralsUtil.computeFGamma(m, T);
        return val;
    }

    /**
     * VRR (Vertical Recurrance Relation)
     */
    protected double vrrNonRecursive(
                         Point3D a, double aNorm, Power aPower, double aAlpha,
                         Point3D b, double bNorm, double bAlpha,
                         Point3D c, double cNorm, Power cPower, double cAlpha,
                         Point3D d, double dNorm, double dAlpha, int m) {

        Point3D p = IntegralsUtil.gaussianProductCenter(aAlpha, a, bAlpha, b);
        Point3D q = IntegralsUtil.gaussianProductCenter(cAlpha, c, dAlpha, d);
        double zeta = aAlpha + bAlpha;
        double eta  = cAlpha + dAlpha;
        double zetaPlusEta = zeta + eta;
        double zetaByZetaPlusEta = zeta / zetaPlusEta;
        double etaByZetaPlusEta  = eta / zetaPlusEta;
        Point3D w = IntegralsUtil.gaussianProductCenter(zeta, p, eta, q);

        int la = aPower.getL();
        int ma = aPower.getM();
        int na = aPower.getN();
        int lc = cPower.getL();
        int mc = cPower.getM();
        int nc = cPower.getN();

        int mtot = la+ma+na+lc+mc+nc+m;  // total angular momentum

        double [] fGammaTerms = new double[mtot+1];

        int i, j, k, pp, qp, r, s, im;

        double px = p.getX(), py = p.getY(), pz = p.getZ();
        double qx = q.getX(), qy = q.getY(), qz = q.getZ();
        double wx = w.getX(), wy = w.getY(), wz = w.getZ();
        double xa = a.getX(), ya = a.getY(), za = a.getZ();
        double xc = c.getX(), yc = c.getY(), zc = c.getZ();

        double rab2 = a.distanceSquaredFrom(b);
        double Kab  = sqrt2PI / zeta * Math.exp(-aAlpha*bAlpha / zeta*rab2);
        double rcd2 = c.distanceSquaredFrom(d);
        double Kcd  = sqrt2PI / eta * Math.exp(-cAlpha*dAlpha / eta*rcd2);
        double rpq2 = p.distanceSquaredFrom(q);
        double T    = zeta*eta / zetaPlusEta*rpq2;
        
        // form [0]^m
        fGammaTerms[mtot] = IntegralsUtil.computeFGamma(mtot, T);
        for(im=mtot-1; im>=0; im--)
            fGammaTerms[im] = (2.0 * T * fGammaTerms[im+1] + Math.exp(-T))
                                  / (2.0 * im + 1);

        int maxam = 5; // la*ma*na*lc*mc*nc*mtot;
        double [] vrrTerms = new double[187500];

        for (im=0; im<mtot+1; im++)
            vrrTerms[iindex(0, 0, 0, 0, 0, 0, im, maxam)]
                    = aNorm*bNorm*cNorm*dNorm*Kab*Kcd
                               / Math.sqrt(zeta+eta)*fGammaTerms[im];

        // construct the other set of terms from the above [0]^m terms

        // the following code breaks the recursive steps in to a series of iterations
        for (i = 0; i < la; i++) {
            for (im = 0; im < mtot - i; im++) {
                vrrTerms[iindex(i+1, 0, 0, 0, 0, 0, im, maxam)] =
                      (px - xa)
                       * vrrTerms[iindex(i, 0, 0, 0, 0, 0, im, maxam)]
                      + (wx - px)
                       * vrrTerms[iindex(i, 0, 0, 0, 0, 0, im + 1, maxam)];

                if (i > 0) {
                    vrrTerms[iindex(i + 1, 0, 0, 0, 0, 0, im, maxam)] +=
                            i / 2.0 / zeta
                            * (vrrTerms[iindex(i - 1, 0, 0, 0, 0, 0, im, maxam)]
                            - etaByZetaPlusEta
                            * vrrTerms[iindex(i - 1, 0, 0, 0, 0, 0, im + 1, maxam)]);
                } // end if
            } // end for
        } // end for

        for (j = 0; j < ma; j++) {
            for (i = 0; i < la + 1; i++) {
                for (im = 0; im < mtot - i - j; im++) {
                    vrrTerms[iindex(i, j + 1, 0, 0, 0, 0, im, maxam)] =
                            (py - ya)
                             * vrrTerms[iindex(i, j, 0, 0, 0, 0, im, maxam)]
                            + (wy - py)
                             * vrrTerms[iindex(i, j, 0, 0, 0, 0, im + 1, maxam)];

                    if (j > 0) {
                        vrrTerms[iindex(i, j + 1, 0, 0, 0, 0, im, maxam)] +=
                                j / 2.0 / zeta
                                * (vrrTerms[iindex(i, j - 1, 0, 0, 0, 0, im, maxam)]
                                - etaByZetaPlusEta
                                * vrrTerms[iindex(i, j - 1, 0, 0, 0, 0, im + 1, maxam)]);
                    } // end if
                } // end for
            } // end for
        } // end for

        for (k = 0; k < na; k++) {
            for (j = 0; j < ma + 1; j++) {
                for (i = 0; i < la + 1; i++) {
                    for (im = 0; im < mtot - i - j - k; im++) {
                        vrrTerms[iindex(i, j, k + 1, 0, 0, 0, im, maxam)] =
                                (pz - za) 
                                * vrrTerms[iindex(i, j, k, 0, 0, 0, im, maxam)]
                                + (wz - pz)
                                * vrrTerms[iindex(i, j, k, 0, 0, 0, im + 1, maxam)];
                        if (k > 0) {
                            vrrTerms[iindex(i, j, k + 1, 0, 0, 0, im, maxam)] +=
                                    k / 2.0 / zeta
                                    * (vrrTerms[iindex(i, j, k - 1, 0, 0, 0, im, maxam)]
                                    - etaByZetaPlusEta
                                    * vrrTerms[iindex(i, j, k - 1, 0, 0, 0, im + 1, maxam)]);
                        } // end if
                    } // end for
                } // end for
            } // end for
        } // end for

        for (qp = 0; qp < lc; qp++) {
            for (k = 0; k < na + 1; k++) {
                for (j = 0; j < ma + 1; j++) {
                    for (i = 0; i < la + 1; i++) {
                        for (im = 0; im < mtot - i - j - k - qp; im++) {
                            vrrTerms[iindex(i, j, k, qp + 1, 0, 0, im, maxam)] =
                                    (qx - xc) 
                                    * vrrTerms[iindex(i, j, k, qp, 0, 0, im, maxam)]
                                    + (wx - qx)
                                    * vrrTerms[iindex(i, j, k, qp, 0, 0, im + 1, maxam)];
                            if (qp > 0) {
                                vrrTerms[iindex(i, j, k, qp + 1, 0, 0, im, maxam)] +=
                                        qp / 2.0 / eta
                                        * (vrrTerms[iindex(i, j, k, qp - 1, 0, 0, im, maxam)]
                                        - zetaByZetaPlusEta
                                        * vrrTerms[iindex(i, j, k, qp - 1, 0, 0, im + 1, maxam)]);
                            } // end if
                            if (i > 0) {
                                vrrTerms[iindex(i, j, k, qp + 1, 0, 0, im, maxam)] +=
                                        i / 2.0 / zetaPlusEta
                                        * vrrTerms[iindex(i - 1, j, k, qp, 0, 0, im + 1, maxam)];
                            } // end if
                        } // end for
                    } // end for
                } // end for
            } // end for
        } // end for

        for (r = 0; r < mc; r++) {
            for (qp = 0; qp < lc + 1; qp++) {
                for (k = 0; k < na + 1; k++) {
                    for (j = 0; j < ma + 1; j++) {
                        for (i = 0; i < la + 1; i++) {
                            for (im = 0; im < mtot - i - j - k - qp - r; im++) {
                                vrrTerms[iindex(i, j, k, qp, r + 1, 0, im, maxam)] =
                                        (qy - yc) 
                                        * vrrTerms[iindex(i, j, k, qp, r, 0, im, maxam)]
                                        + (wy - qy)
                                        * vrrTerms[iindex(i, j, k, qp, r, 0, im + 1, maxam)];
                                if (r > 0) {
                                    vrrTerms[iindex(i, j, k, qp, r + 1, 0, im, maxam)] +=
                                            r / 2.0 / eta
                                            * (vrrTerms[iindex(i, j, k, qp, r - 1, 0, im, maxam)]
                                            - zetaByZetaPlusEta
                                            * vrrTerms[iindex(i, j, k, qp, r - 1, 0, im + 1, maxam)]);
                                } // end if
                                if (j > 0) {
                                    vrrTerms[iindex(i, j, k, qp, r + 1, 0, im, maxam)] +=
                                            j / 2.0 / zetaPlusEta
                                            * vrrTerms[iindex(i, j - 1, k, qp, r, 0, im + 1, maxam)];
                                } // end if
                            } // end for
                        } // end for
                    } // end for
                } // end for
            } // end for
        } // end for

        for (s = 0; s < nc; s++) {
            for (r = 0; r < mc + 1; r++) {
                for (qp = 0; qp < lc + 1; qp++) {
                    for (k = 0; k < na + 1; k++) {
                        for (j = 0; j < ma + 1; j++) {
                            for (i = 0; i < la + 1; i++) {
                                for (im = 0; im < mtot-i-j-k-qp-r-s; im++) {
                                    vrrTerms[iindex(i, j, k, qp, r, s + 1, im, maxam)] =
                                            (qz - zc) 
                                            * vrrTerms[iindex(i, j, k, qp, r, s, im, maxam)]
                                            + (wz - qz)
                                            * vrrTerms[iindex(i, j, k, qp, r, s, im + 1, maxam)];
                                    if (s > 0) {
                                        vrrTerms[iindex(i, j, k, qp, r, s + 1, im, maxam)] +=
                                                s / 2.0 / eta
                                                * (vrrTerms[iindex(i, j, k, qp, r, s - 1, im, maxam)]
                                                - zetaByZetaPlusEta
                                                * vrrTerms[iindex(i, j, k, qp, r, s - 1, im + 1, maxam)]);
                                    } // end if
                                    if (k > 0) {
                                        vrrTerms[iindex(i, j, k, qp, r, s + 1, im, maxam)] +=
                                                k / 2.0 / zetaPlusEta
                                                * vrrTerms[iindex(i, j, k - 1, qp, r, s, im + 1, maxam)];
                                    } // end if
                                } // end for
                            } // end for
                        } // end for
                    } // end for
                } // end for
            } // end for
        } // end for

        return vrrTerms[iindex(la,ma,na,lc,mc,nc,m,maxam)];
    }

    /* Convert the 7-dimensional indices to a 1d iindex */
    private int iindex(int la, int ma, int na,
                       int lc, int mc, int nc, int m, int maxam) {
        return (la + ma * maxam + na * maxam * maxam
                + lc * maxam * maxam * maxam
                + nc * maxam * maxam * maxam * maxam
                + mc * maxam * maxam * maxam * maxam * maxam 
                + m * maxam * maxam * maxam * maxam * maxam * maxam);
    }
    
    @Override
    public double coulomb(ContractedGaussian a, ContractedGaussian b, 
                          ContractedGaussian c, ContractedGaussian d, 
                          Density density, Matrix jMat, Matrix kMat) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
