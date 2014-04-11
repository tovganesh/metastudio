/*
 * ContractedGaussian.java
 *
 * Created on July 22, 2004, 7:10 AM
 */

package org.meta.math.qm.basis;

import java.util.*;

import org.meta.math.geom.Point3D;
import org.meta.config.impl.AtomInfo;
import org.meta.math.Vector3D;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * The class defines a contracted gaussian and the operations on it.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ContractedGaussian {
    
    /**
     * Holds value of property origin.
     */
    private Point3D origin;
    
    /**
     * Holds value of property powers, whoes array size is 3.
     */
    private Power powers;
    
    /**
     * Holds value of property primitives.
     * The Primitive Gaussians (PGs) that make up this Contracted Gaussian.
     */
    private ArrayList<PrimitiveGaussian> primitives;
    
    /**
     * Holds value of property exponents.
     * The list of exponents for this gaussian.
     */
    private ArrayList<Double> exponents;
    
    /**
     * Holds value of property coefficients.
     */
    private ArrayList<Double> coefficients;
    
    /**
     * Holds value of property normalization.
     */
    private double normalization;
    
    /**
     * Holds value of property primNorms.
     *
     * normalization factors for PGs
     */
    private ArrayList<Double> primNorms;
    
    /** 
     * Creates a new instance of ContractedGaussian 
     *
     * @param origin - the (x, y, z) on which this gaussian is centered
     * @param powers - the powers of this gaussian
     */
    public ContractedGaussian(Point3D origin, Power powers) {
        this.origin = origin;
        this.powers = powers;
        
        primitives   = new ArrayList<PrimitiveGaussian>(10);
        exponents    = new ArrayList<Double>(10);
        coefficients = new ArrayList<Double>(10);
        primNorms    = new ArrayList<Double>(10);
        
        this.normalization = 1;
    }

    /**
     * Creates a new instance of ContractedGaussian
     *
     * @param centeredAtom - CG centered on an atom
     * @param powers - the powers of this gaussian
     */
    public ContractedGaussian(Atom centeredAtom, Power powers) {
        this(centeredAtom.getAtomCenterInAU(), powers);
        
        this.centeredAtom = centeredAtom;
    }

    protected Atom centeredAtom;

    /**
     * Get the value of centeredAtom
     *
     * @return the value of centeredAtom
     */
    public Atom getCenteredAtom() {
        return centeredAtom;
    }

    /**
     * Set the value of centeredAtom
     *
     * @param centeredAtom new value of centeredAtom
     */
    public void setCenteredAtom(Atom centeredAtom) {
        this.centeredAtom = centeredAtom;
    }

    /**
     * Adds a primitive gaussian (PG) to this contracted gaussian list
     *
     * @param exponent the exponent for this PG
     * @param coefficient the coefficient of this PG
     */
    public void addPrimitive(double exponent, double coefficient) {
        primitives.add(new PrimitiveGaussian(origin, powers, 
                                             exponent, coefficient));
        
        exponents.add(new Double(exponent));
        coefficients.add(new Double(coefficient));
    }
    
    /**
     * Getter for property origin.
     * @return Value of property origin.
     */
    public Point3D getOrigin() {
        return this.origin;
    }
    
    /**
     * Setter for property origin.
     * @param origin New value of property origin.
     */
    public void setOrigin(Point3D origin) {
        this.origin = origin;
    }
    
    /**
     * Getter for property powers.
     * @return Value of property powers.
     */
    public Power getPowers() {
        return this.powers;
    }
    
    /**
     * Setter for property powers.
     * @param powers New value of property powers.
     */
    public void setPowers(Power powers) {
        this.powers = powers;
    }
    
    /**
     * Getter for property primitives.
     * @return Value of property primitives.
     */
    public ArrayList<PrimitiveGaussian> getPrimitives() {
        return this.primitives;
    }
    
    /**
     * Setter for property primitives.
     * @param primitives New value of property primitives.
     */
    public void setPrimitives(ArrayList<PrimitiveGaussian> primitives) {
        this.primitives = primitives;
    }
    
    /**
     * Getter for property exponents.
     * @return Value of property exponents.
     */
    public ArrayList<Double> getExponents() {
        return this.exponents;
    }
    
    /**
     * Setter for property exponents.
     * @param exponents New value of property exponents.
     */
    public void setExponents(ArrayList<Double> exponents) {
        this.exponents = exponents;
    }
    
    /**
     * Getter for property coefficients.
     * @return Value of property coefficients.
     */
    public ArrayList<Double> getCoefficients() {
        return this.coefficients;
    }
    
    /**
     * Setter for property coefficients.
     * @param coefficients New value of property coefficients.
     */
    public void setCoefficients(ArrayList<Double> coefficients) {
        this.coefficients = coefficients;
    }        
    
    /**
     * Getter for property normalization.
     * @return Value of property normalization.
     */
    public double getNormalization() {
        return this.normalization;
    }
    
    /**
     * Setter for property normalization.
     * @param normalization New value of property normalization.
     */
    public void setNormalization(double normalization) {
        this.normalization = normalization;
    }
    
    /**
     * Normalize this basis function.
     *     
     */
    public void normalize() {       
        normalization = 1.0 / Math.sqrt(this.overlap(this));
        
        for(int i=0; i<primitives.size(); i++) {
            primNorms.add(new Double(((PrimitiveGaussian)
                                        primitives.get(i)).getNormalization()));
        } // end for
    }
    
    /**
     * Overlap matrix element with another ContractedGaussian
     *
     * @param cg the ContractedGaussian with which the overlap is to be
     *        be determined.
     * @return the overlap value
     */
    public double overlap(ContractedGaussian cg) {
        double sij = 0.0;
        int i, j;
        
        ArrayList<PrimitiveGaussian> cgPrimitives = cg.getPrimitives();
        
        for(i=0; i<primitives.size(); i++) {
            PrimitiveGaussian iPG = primitives.get(i);            
            for(j=0; j<cgPrimitives.size(); j++) {
                PrimitiveGaussian jPG = cgPrimitives.get(j);                
                
                sij += iPG.getCoefficient() * jPG.getCoefficient() 
                       * iPG.overlap(jPG);
            } // end for
        } // end for
        
        return normalization * cg.normalization * sij;
    }

    /**
     * Compute the overlap energy detivative term w.r.t the specified atom index
     *
     * @param atomIndex the reference atomIndex
     * @param cg the other cg
     * @return the derivative term as an instance of Vector3D
     */
    public Vector3D overlapDerivative(int atomIndex, ContractedGaussian cg) {
        Vector3D ovrDer = new Vector3D();

        // case 1: atomIndex is centered on this CG
        if (this.centeredAtom.getIndex() == atomIndex) {
            for(PrimitiveGaussian jPG : cg.primitives) {
                for(PrimitiveGaussian iPG : primitives) {
                    overlapDerivativeHelper(iPG, jPG, origin, ovrDer);
                } // end for
            } // end for
        } // end if

        // case 2: atomIndex is centered on the other CG
        if (cg.centeredAtom.getIndex() == atomIndex) {
            for(PrimitiveGaussian iPG : primitives) {
                for(PrimitiveGaussian jPG : cg.primitives) {
                    overlapDerivativeHelper(jPG, iPG, cg.origin, ovrDer);
                } // end for
            } // end for
        } // end if

        // case 3: is atomIndex is not centered on any of the CGs, then
        // then the result is zero.
        
        return ovrDer;
    }

    /** helper method for overlap derivative */
    private void overlapDerivativeHelper(PrimitiveGaussian iPG,
                                         PrimitiveGaussian jPG,
                                         Point3D pOrigin,
                                         Vector3D ovrDer) {
        int l = iPG.getPowers().getL(),
            m = iPG.getPowers().getM(),
            n = iPG.getPowers().getN();
        double coeff = iPG.getCoefficient() * jPG.getCoefficient();
        double alpha = iPG.getExponent();

        PrimitiveGaussian xPG = new PrimitiveGaussian(pOrigin,
                          new Power(l+1, m, n), coeff, alpha);
        xPG.normalize();

        double terma = Math.sqrt(alpha*(2.0*l+1.0)) * coeff
                           * xPG.overlap(jPG);
        double termb = 0.0;

        if (l>0) {
            xPG.setPowers(new Power(l-1, m, n));
            xPG.normalize();
            termb = -2.0*l*Math.sqrt(alpha/(2.0*l-1.0)) * coeff
                        * xPG.overlap(jPG);
        } // end if

        ovrDer.setI(ovrDer.getI() + terma + termb);

        xPG.setPowers(new Power(l, m+1, n));
        xPG.normalize();
        terma = Math.sqrt(alpha*(2.0*m+1.0)) * coeff
                  * xPG.overlap(jPG);

        if (m>0) {
            xPG.setPowers(new Power(l, m-1, n));
            xPG.normalize();
            termb = -2*m*Math.sqrt(alpha/(2.0*m-1.0)) * coeff
                        * xPG.overlap(jPG);
        } else termb = 0.0;

        ovrDer.setJ(ovrDer.getJ() + terma + termb);

        xPG.setPowers(new Power(l, m, n+1));
        xPG.normalize();
        terma = Math.sqrt(alpha*(2.0*n+1.0)) * coeff
                  * xPG.overlap(jPG);

        if (n>0) {
            xPG.setPowers(new Power(l, m, n-1));
            xPG.normalize();
            termb = -2.0 * n * Math.sqrt(alpha/(2.0*n-1.0)) * coeff
                         * xPG.overlap(jPG);
        } else termb = 0.0;

        ovrDer.setK(ovrDer.getK() + terma + termb);
    }

    /**
     * Kinetic Energy (KE) matrix element with another ContractedGaussian
     *
     * @param cg the ContractedGaussian with which KE is to be determined.
     * @return the KE value
     */
    public double kinetic(ContractedGaussian cg) {
        double tij = 0.0;
        int i, j;
        
        ArrayList<PrimitiveGaussian> cgPrimitives = cg.getPrimitives();
        
        for(i=0; i<primitives.size(); i++) {
            PrimitiveGaussian iPG = primitives.get(i);            
            for(j=0; j<cgPrimitives.size(); j++) {
                PrimitiveGaussian jPG = cgPrimitives.get(j);                
                
                tij += iPG.getCoefficient() * jPG.getCoefficient() 
                       * iPG.kinetic(jPG);
            } // end for
        } // end for
        
        return normalization * cg.normalization * tij;
    }
    
    /**
     * Compute the kinetic energy detivative term w.r.t the specified atom index
     *
     * @param atomIndex the reference atomIndex
     * @param cg the other cg
     * @return the derivative term as an instance of Vector3D
     */
    public Vector3D kineticDerivative(int atomIndex, ContractedGaussian cg) {
        Vector3D kder = new Vector3D();

        // case 1: atomIndex is centered on this CG
        if (this.centeredAtom.getIndex() == atomIndex) {
            for(PrimitiveGaussian jPG : cg.primitives) {
                for(PrimitiveGaussian iPG : primitives) {
                    kineticDerivativeHelper(iPG, jPG, origin, kder);
                } // end for
            } // end for
        } // end if

        // case 2: atomIndex is centered on the other CG
        if (cg.centeredAtom.getIndex() == atomIndex) {
            for(PrimitiveGaussian iPG : primitives) {
                for(PrimitiveGaussian jPG : cg.primitives) {
                    kineticDerivativeHelper(jPG, iPG, cg.origin, kder);
                } // end for
            } // end for
        } // end if

        // case 3: is atomIndex is not centered on any of the CGs, then
        // then the result is zero.
        
        return kder;
    }

    /** helper method for kinetic energy derivative */
    private void kineticDerivativeHelper(PrimitiveGaussian iPG,
                                         PrimitiveGaussian jPG,
                                         Point3D pOrigin,
                                         Vector3D kder) {
        int l = iPG.getPowers().getL(),
            m = iPG.getPowers().getM(),
            n = iPG.getPowers().getN();
        double coeff = iPG.getCoefficient() * jPG.getCoefficient();
        double alpha = iPG.getExponent();

        PrimitiveGaussian xPG = new PrimitiveGaussian(pOrigin,
                          new Power(l+1, m, n), coeff, alpha);
        xPG.normalize();

        double terma = Math.sqrt(alpha*(2.0*l+1.0)) * coeff
                           * xPG.kinetic(jPG);
        double termb = 0.0;

        if (l>0) {
            xPG.setPowers(new Power(l-1, m, n));
            xPG.normalize();
            termb = -2.0*l*Math.sqrt(alpha/(2.0*l-1.0)) * coeff
                        * xPG.kinetic(jPG);
        } // end if

        kder.setI(kder.getI() + terma + termb);

        xPG.setPowers(new Power(l, m+1, n));
        xPG.normalize();
        terma = Math.sqrt(alpha*(2.0*m+1.0)) * coeff
                  * xPG.kinetic(jPG);

        if (m>0) {
            xPG.setPowers(new Power(l, m-1, n));
            xPG.normalize();
            termb = -2*m*Math.sqrt(alpha/(2.0*m-1.0)) * coeff
                        * xPG.kinetic(jPG);
        } else termb = 0.0;

        kder.setJ(kder.getJ() + terma + termb);

        xPG.setPowers(new Power(l, m, n+1));
        xPG.normalize();
        terma = Math.sqrt(alpha*(2.0*n+1.0)) * coeff
                  * xPG.kinetic(jPG);

        if (n>0) {
            xPG.setPowers(new Power(l, m, n-1));
            xPG.normalize();
            termb = -2.0 * n * Math.sqrt(alpha/(2.0*n-1.0)) * coeff
                         * xPG.kinetic(jPG);
        } else termb = 0.0;

        kder.setK(kder.getK() + terma + termb);
    }

    /**
     * Nuclear matrix element with another ContractedGaussian and a center
     *
     * @param cg the ContractedGaussian with which nuclear interaction
     *        is to determined.
     * @param center the center at which nuclear energy is to be computed
     * @return the nuclear value
     */
    public double nuclear(ContractedGaussian cg, Point3D center) {
        double vij = 0.0;
        int i, j;
        
        ArrayList<PrimitiveGaussian> cgPrimitives = cg.getPrimitives();
        
        for(i=0; i<primitives.size(); i++) {
            PrimitiveGaussian iPG = primitives.get(i);            
            for(j=0; j<cgPrimitives.size(); j++) {
                PrimitiveGaussian jPG = cgPrimitives.get(j);                
                
                vij += iPG.getCoefficient() * jPG.getCoefficient() 
                       * iPG.nuclear(jPG, center);                                
            } // end for
        } // end for
        
        return normalization * cg.normalization * vij;
    }

    /**
     * Derivative of nuclear attraction term, with respect to an atom index
     * 
     * @param mol the reference Molecule object
     * @param atomIndex the reference atomIndex
     * @param cg the other ContractedGaussian
     * @return partial derivative of nuclear attraction integral term
     */
    public Vector3D nuclearAttractionDerivative(Molecule mol, int atomIndex,
                                                ContractedGaussian cg) {
        Vector3D nder = new Vector3D();

        // case 1: atom index is centered on this CG
        if (this.centeredAtom.getIndex() == atomIndex) {
            for(PrimitiveGaussian jPG : cg.primitives) {
                for(PrimitiveGaussian iPG : primitives) {
                    nuclearDerivativeHelper(mol, iPG, jPG, origin, nder);
                } // end for
            } // end for
        } // end if

        // case 2: atomIndex is centered on the other CG
        if (cg.centeredAtom.getIndex() == atomIndex) {
            for(PrimitiveGaussian iPG : primitives) {
                for(PrimitiveGaussian jPG : cg.primitives) {
                    nuclearDerivativeHelper(mol, jPG, iPG, cg.origin, nder);
                } // end for
            } // end for
        } // end if

        double factor = 0.0;
        double atno = AtomInfo.getInstance()
                              .getAtomicNumber(centeredAtom.getSymbol());
        for(PrimitiveGaussian iPG : primitives) {
            for(PrimitiveGaussian jPG : cg.primitives) {
                factor = iPG.getCoefficient() * jPG.getCoefficient() * atno;
                nder   = nder.add(jPG.nuclearAttractionGradient(iPG,
                           centeredAtom.getAtomCenter()).mul(factor));
            } // end for
        } // end for
        
        return nder;
    }

    /** helper method for kinetic energy derivative */
    private void nuclearDerivativeHelper(Molecule mol,
                                         PrimitiveGaussian iPG,
                                         PrimitiveGaussian jPG,
                                         Point3D pOrigin,
                                         Vector3D nder) {
        int l = iPG.getPowers().getL(),
            m = iPG.getPowers().getM(),
            n = iPG.getPowers().getN();
        double coeff = iPG.getCoefficient() * jPG.getCoefficient();
        double alpha = iPG.getExponent();

        PrimitiveGaussian xPG = new PrimitiveGaussian(pOrigin,
                                         new Power(l+1, m, n), coeff, alpha);
        xPG.normalize();

        double terma = 0.0;
        
        AtomInfo ai = AtomInfo.getInstance();
        for(int i=0; i<mol.getNumberOfAtoms(); i++)  {
            Atom atom = mol.getAtom(i);
            terma += ai.getAtomicNumber(atom.getSymbol())
                      * Math.sqrt(alpha*(2.0*l+1.0)) * coeff
                      * xPG.nuclear(jPG, atom.getAtomCenter());
        } // end for

        double termb = 0.0;

        if (l>0) {
            xPG.setPowers(new Power(l-1, m, n));
            xPG.normalize();
            
            for(int i=0; i<mol.getNumberOfAtoms(); i++)  {
                Atom atom = mol.getAtom(i);
                termb += -2.0*l*ai.getAtomicNumber(atom.getSymbol())
                          * Math.sqrt(alpha/(2.0*l-1.0)) * coeff
                          * xPG.nuclear(jPG, atom.getAtomCenter());
            } // end for
        } // end if

        nder.setI(nder.getI() + terma + termb);

        xPG.setPowers(new Power(l, m+1, n));
        xPG.normalize();
        terma = 0.0;
        for(int i=0; i<mol.getNumberOfAtoms(); i++)  {
            Atom atom = mol.getAtom(i);
            terma += ai.getAtomicNumber(atom.getSymbol())
                      * Math.sqrt(alpha*(2.0*m+1.0)) * coeff
                      * xPG.nuclear(jPG, atom.getAtomCenter());
        } // end for

        termb = 0.0;

        if (m>0) {
            xPG.setPowers(new Power(l, m-1, n));
            xPG.normalize();

            for(int i=0; i<mol.getNumberOfAtoms(); i++)  {
                Atom atom = mol.getAtom(i);
                termb += -2.0*m*ai.getAtomicNumber(atom.getSymbol())
                          * Math.sqrt(alpha/(2.0*m-1.0)) * coeff
                          * xPG.nuclear(jPG, atom.getAtomCenter());
            } // end for
        } // end if

        nder.setJ(nder.getJ() + terma + termb);

        xPG.setPowers(new Power(l, m, n+1));
        xPG.normalize();
        terma = 0.0;
        for(int i=0; i<mol.getNumberOfAtoms(); i++)  {
            Atom atom = mol.getAtom(i);
            terma += ai.getAtomicNumber(atom.getSymbol())
                      * Math.sqrt(alpha*(2.0*n+1.0)) * coeff
                      * xPG.nuclear(jPG, atom.getAtomCenter());
        } // end for

        termb = 0.0;

        if (n>0) {
            xPG.setPowers(new Power(l, m, n-1));
            xPG.normalize();

            for(int i=0; i<mol.getNumberOfAtoms(); i++)  {
                Atom atom = mol.getAtom(i);
                termb += -2.0*n*ai.getAtomicNumber(atom.getSymbol())
                          * Math.sqrt(alpha/(2.0*n-1.0)) * coeff
                          * xPG.nuclear(jPG, atom.getAtomCenter());
            } // end for
        } else termb = 0.0;

        nder.setK(nder.getK() + terma + termb);
    }
    
    /**
     * The aplitude of this primitive gaussian at a given point.
     *
     * @param point the reference point
     * @return the amplitude of this PG at the specified point
     */
    public double amplitude(Point3D point) {
        double value = 0.0;

        for(int i=0; i<primitives.size(); i++) {
            value += primitives.get(i).amplitude(point);
        } // end for

        return (normalization * value);
    }
    
    /**
     * Calculate laplacian at the specified point
     * 
     * @param point the point where Laplacian is to be computed
     * @return the laplacian at this point
     */
    public double laplacian(Point3D point) {
        double value = 0.0;

        for(PrimitiveGaussian pg : primitives)
            value += pg.laplacian(point);
        
        return normalization*value;
    }

    /**
     * Evaluate gradient of this function at a given point
     * 
     * @param point the point where gradient is to be evaluated
     * @return partial derivatives with respect to x, y, z
     */
    public Vector3D gradient(Point3D point) {
        Vector3D grad = new Vector3D();

        for(PrimitiveGaussian pg : primitives)
            grad.add(pg.gradient(point));

        return grad.mul(normalization);
    }

    /**
     * Getter for property primNorms.
     * @return Value of property primNorms.
     */
    public ArrayList<Double> getPrimNorms() {
        return this.primNorms;
    }
    
    /**
     * Setter for property primNorms.
     * @param primNorms New value of property primNorms.
     */
    public void setPrimNorms(ArrayList<Double> primNorms) {
        this.primNorms = primNorms;
    }

    /**
     * Return the maximum angular momentum for this contracted basis function
     * 
     * @return the maximum angular momentum of this contracted basis function
     */
    public int getMaximumAngularMomentum() {
        return powers.getMaximumAngularMomentum();
    }

    /**
     * Return the minimum angular momentum for this contracted basis function
     *
     * @return the minimum angular momentum of this contracted basis function
     */
    public int getMinimumAngularMomentum() {
        return powers.getMinimumAngularMomentum();
    }

    /**
     * Return the total angular momentum of this contracted basis function
     *
     * @return the maximum of the contracted basis function
     */
    public int getTotalAngularMomentum() {
        return powers.getTotalAngularMomentum();
    }

    protected int index;

    /**
     * Get the value of index
     *
     * @return the value of index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the value of index
     *
     * @param index new value of index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * overloaded toString()
     */
    @Override
    public String toString() {
        return "Origin : " + origin + " Powers : " + powers
               + " Normalization : " + normalization
               + " Primitives : " + primitives;
    }
} // end of class ContractedGaussian
