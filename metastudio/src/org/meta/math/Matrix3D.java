/*
 * Matrix3D.java
 *
 * Created on December 18, 2003, 7:20 AM
 */

package org.meta.math;

import java.util.*;
import org.meta.math.geom.Point3D;
import org.meta.math.geom.Point3DI;
import org.meta.common.Utility;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * A simple matrix class optimized to perform 3D transformations on point(s).
 *
 * Note: Most of the operations defined in the class perform the operations on 
 * the current instance of the class.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Matrix3D implements Cloneable {
    
    /** the matrix elements, here the elements a03, a13 and a23 represent
        origin */
    private double a00, a01, a02, a03,
                   a10, a11, a12, a13,
                   a20, a21, a22, a23;    

    /** Creates a new instance of Matrix3D */
    public Matrix3D() {
        // initilize matrix
        a00 = a01 = a02 = a03 = 0.0;
        a10 = a11 = a12 = a13 = 0.0; 
        a20 = a21 = a22 = a23 = 0.0;        
    }
    
    /**
     * Uniform scaling along all axis
     *
     * @param scaleFactor - the scale factor
     */
    public void scale(double scaleFactor) {
        a00 *= scaleFactor; 
        a01 *= scaleFactor; 
        a02 *= scaleFactor;
        a03 *= scaleFactor;
        a10 *= scaleFactor; 
        a11 *= scaleFactor; 
        a12 *= scaleFactor; 
        a13 *= scaleFactor; 
        a20 *= scaleFactor; 
        a21 *= scaleFactor; 
        a22 *= scaleFactor;
        a23 *= scaleFactor;
    }
    
    /**
     * Scale along each axis independently
     *
     * @param scaleX - scalefactor along X axis
     * @param scaleY - scalefactor along Y axis
     * @param scaleZ - scalefactor along Z axis
     */
    public void scale(double scaleX, double scaleY, double scaleZ) {
        a00 *= scaleX; 
        a01 *= scaleX; 
        a02 *= scaleX;
        a03 *= scaleX;
        a10 *= scaleY; 
        a11 *= scaleY; 
        a12 *= scaleY; 
        a13 *= scaleY; 
        a20 *= scaleZ; 
        a21 *= scaleZ; 
        a22 *= scaleZ;
        a23 *= scaleZ;
    }
    
    /**
     * Translate the origin
     *
     * @param xUnits - translate along X - axis by xUnits
     * @param yUnits - translate along Y - axis by yUnits
     * @param zUnits - translate along Z - axis by zUnits
     */
    public void translate(double xUnits, double yUnits, double zUnits) {
        a03 += xUnits;
        a13 += yUnits;
        a23 += zUnits;
    }
    
    /**
     * Rotate theta degrees along X axis
     *
     * @param theta - the angle in degrees with which to rotate along X axis
     */
    public void rotateAlongX(double theta) {
        theta = MathUtil.toRadians(theta);
        
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        
        double Na10 = (a10 * cosTheta + a20 * sinTheta);
        double Na11 = (a11 * cosTheta + a21 * sinTheta);
        double Na12 = (a12 * cosTheta + a22 * sinTheta);
        double Na13 = (a13 * cosTheta + a23 * sinTheta);

        double Na20 = (a20 * cosTheta - a10 * sinTheta);
        double Na21 = (a21 * cosTheta - a11 * sinTheta);
        double Na22 = (a22 * cosTheta - a12 * sinTheta);
        double Na23 = (a23 * cosTheta - a13 * sinTheta);
        
        a13 = Na13;
        a10 = Na10;
        a11 = Na11;
        a12 = Na12;
        a23 = Na23;
        a20 = Na20;
        a21 = Na21;
        a22 = Na22;
    }
    
    /**
     * Rotate theta degrees along Y axis
     *
     * @param theta - the angle in degrees with which to rotate along Y axis
     */
    public void rotateAlongY(double theta) {
        theta = MathUtil.toRadians(theta);
        
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        
        double Na00 = (a00 * cosTheta + a20 * sinTheta);
        double Na01 = (a01 * cosTheta + a21 * sinTheta);
        double Na02 = (a02 * cosTheta + a22 * sinTheta);
        double Na03 = (a03 * cosTheta + a23 * sinTheta);

        double Na20 = (a20 * cosTheta - a00 * sinTheta);
        double Na21 = (a21 * cosTheta - a01 * sinTheta);
        double Na22 = (a22 * cosTheta - a02 * sinTheta);
        double Na23 = (a23 * cosTheta - a03 * sinTheta);
        
        a03 = Na03;
        a00 = Na00;
        a01 = Na01;
        a02 = Na02;
        a23 = Na23;
        a20 = Na20;
        a21 = Na21;
        a22 = Na22;
    }        
    
    /**
     * Rotate theta degrees along Z axis
     *
     * @param theta - the angle in degrees with which to rotate along Z axis
     */
    public void rotateAlongZ(double theta) {
        theta = MathUtil.toRadians(theta);
        
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        
        double Na10 = (a10 * cosTheta + a00 * sinTheta);
        double Na11 = (a11 * cosTheta + a01 * sinTheta);
        double Na12 = (a12 * cosTheta + a02 * sinTheta);
        double Na13 = (a13 * cosTheta + a03 * sinTheta);
        
        double Na00 = (a00 * cosTheta - a10 * sinTheta);
        double Na01 = (a01 * cosTheta - a11 * sinTheta);
        double Na02 = (a02 * cosTheta - a12 * sinTheta);
        double Na03 = (a03 * cosTheta - a13 * sinTheta);
        
        a13 = Na13;
        a10 = Na10;
        a11 = Na11;
        a12 = Na12;
        a03 = Na03;
        a00 = Na00;
        a01 = Na01;
        a02 = Na02;
    }

    /**
     * Rotate a point along an arbitrary axis
     * 
     * @param point the point
     * @param axis axis of rotation
     * @param theta the angle of rotation
     * @return the rotated point
     */
    public Point3D rotate(Point3D point, Vector3D axis, double theta) {
        Quaternion quaternion = new Quaternion(axis, theta);
        
        return quaternion.rotate(point);
    }

    /**
     * Rotate an Atom along an arbitrary axis
     *
     * @param atom the Atom
     * @param axis axis of rotation
     * @param theta the angle of rotation
     * @return the rotated Molecule
     * @throws CloneNotSupportedException
     */
    public Atom rotate(Atom atom, Vector3D axis, double theta)
                       throws CloneNotSupportedException {
        theta = MathUtil.toRadians(theta);
        Quaternion quaternion = new Quaternion(axis, theta);

        Atom newAtom = (Atom) atom.clone();

        newAtom.setAtomCenter(quaternion.rotate(newAtom.getAtomCenter()));

        return newAtom;
    }

    /**
     * Rotate an Atom along an arbitrary axis
     *
     * @param atom the Atom
     * @param xAngle the angle of rotation in X direction
     * @param yAngle the angle of rotation in Y direction
     * @param zAngle the angle of rotation in Z direction
     * @return the rotated Molecule
     * @throws CloneNotSupportedException
     */
    public Atom rotate(Atom atom,
                       double xAngle, double yAngle, double zAngle)
                       throws CloneNotSupportedException {
        xAngle = MathUtil.toRadians(xAngle);
        yAngle = MathUtil.toRadians(yAngle);
        zAngle = MathUtil.toRadians(zAngle);

        Quaternion quaternion = new Quaternion(xAngle, yAngle, zAngle);

        Atom newAtom = (Atom) atom.clone();

        newAtom.setAtomCenter(quaternion.rotate(newAtom.getAtomCenter()));

        return newAtom;
    }

    /**
     * Transform an Atom using Quaternion
     *
     * @param atom the Atom
     * @param quat the Quaternion object used for transform
     * @return the rotated Molecule
     * @throws CloneNotSupportedException
     */
    public Atom transform(Atom atom, Quaternion quat) throws CloneNotSupportedException {

        Quaternion quaternion = quat;

        Atom newAtom = (Atom) atom.clone();

        newAtom.setAtomCenter(quaternion.rotate(newAtom.getAtomCenter()));

        return newAtom;
    }


    /**
     * Rotate a Molecule along an arbitrary axis
     *
     * @param molecule the Molecule
     * @param axis axis of rotation
     * @param theta the angle of rotation
     * @return the rotated Molecule
     * @throws Exception - may throw InstantiationException, if unable to
     *  create instance of Molecule class.
     */
    public Molecule rotate(Molecule molecule, Vector3D axis, double theta)
                                                          throws Exception {        
        Molecule newMolecule =
         (Molecule) ((Utility.getDefaultImplFor(Molecule.class)).newInstance());

        newMolecule.setTitle(molecule.getTitle());

        Iterator<Atom> atoms = molecule.getAtoms();

        while(atoms.hasNext()) {
            newMolecule.addAtom(rotate(atoms.next(), axis, theta));
        } // end while

        return newMolecule;
    }

    /**
     * Transform a Molecule using a Quaternion
     *
     * @param molecule the Molecule
     * @param quat the Quaternion
     * @return the transformed Molecule
     * @throws Exception - may throw InstantiationException, if unable to
     *  create instance of Molecule class.
     */
    public Molecule transform(Molecule molecule, Quaternion quat) throws Exception {
        Molecule newMolecule =
         (Molecule) ((Utility.getDefaultImplFor(Molecule.class)).newInstance());

        newMolecule.setTitle(molecule.getTitle());

        Iterator<Atom> atoms = molecule.getAtoms();

        while(atoms.hasNext()) {
            newMolecule.addAtom(transform(atoms.next(), quat));
        } // end while

        return newMolecule;
    }


    /**
     * Rotate a Molecule along an arbitrary axis
     *
     * @param molecule the Molecule     
     * @param xAngle the angle of rotation in X direction
     * @param yAngle the angle of rotation in Y direction
     * @param zAngle the angle of rotation in Z direction
     * @return the rotated Molecule
     * @throws Exception - may throw InstantiationException, if unable to
     *  create instance of Molecule class.
     */
    public Molecule rotate(Molecule molecule, 
                           double xAngle, double yAngle, double zAngle)
                                                          throws Exception {
        Molecule newMolecule =
         (Molecule) ((Utility.getDefaultImplFor(Molecule.class)).newInstance());

        newMolecule.setTitle(molecule.getTitle());

        Iterator<Atom> atoms = molecule.getAtoms();

        while(atoms.hasNext()) {
            newMolecule.addAtom(rotate(atoms.next(), xAngle, yAngle, zAngle));
        } // end while

        return newMolecule;
    }

    /**
     * A special method to transform ScreenAtoms .. only relevant
     * when used with MoleculeViewer
     *
     * @param screenAtoms - an array list of ScreenAtom s which will be
     *        transformed according to current transformation matrix.
     */
    public void rotate(ArrayList<ScreenAtom> screenAtoms, Vector3D axis, double theta) {
        ScreenAtom screenAtom;        
        Iterator<ScreenAtom> atoms = screenAtoms.iterator();

        while(atoms.hasNext()) {
            screenAtom = atoms.next();

            Point3D tPoint = rotate(screenAtom.getAtom().getAtomCenter(),
                                    axis, theta);
            
            screenAtom.setCurrentX((int) tPoint.getX());
            screenAtom.setCurrentY((int) tPoint.getY());
            screenAtom.setCurrentZ((int) tPoint.getZ());
        } // end for
    }
    
    /** 
     * multiply the current matrix with a new matrix, useful for
     * concatenated transformations.
     *
     * @param rhs :: this = this * rhs
     */
    public void mul(Matrix3D rhs) {
        double l00 = a00 * rhs.a00 + a10 * rhs.a01 + a20 * rhs.a02;
        double l01 = a01 * rhs.a00 + a11 * rhs.a01 + a21 * rhs.a02;
        double l02 = a02 * rhs.a00 + a12 * rhs.a01 + a22 * rhs.a02;
        double l03 = a03 * rhs.a00 + a13 * rhs.a01 + a23 * rhs.a02 + rhs.a03;
        
        double l10 = a00 * rhs.a10 + a10 * rhs.a11 + a20 * rhs.a12;
        double l11 = a01 * rhs.a10 + a11 * rhs.a11 + a21 * rhs.a12;
        double l12 = a02 * rhs.a10 + a12 * rhs.a11 + a22 * rhs.a12;
        double l13 = a03 * rhs.a10 + a13 * rhs.a11 + a23 * rhs.a12 + rhs.a13;
        
        double l20 = a00 * rhs.a20 + a10 * rhs.a21 + a20 * rhs.a22;
        double l21 = a01 * rhs.a20 + a11 * rhs.a21 + a21 * rhs.a22;
        double l22 = a02 * rhs.a20 + a12 * rhs.a21 + a22 * rhs.a22;
        double l23 = a03 * rhs.a20 + a13 * rhs.a21 + a23 * rhs.a22 + rhs.a23;
        
        a00 = l00;
        a01 = l01;
        a02 = l02;
        a03 = l03;
        
        a10 = l10;
        a11 = l11;
        a12 = l12;
        a13 = l13;
        
        a20 = l20;
        a21 = l21;
        a22 = l22;
        a23 = l23;
    }
    
    /**
     * reinitialize this matrix to be a unit matrix.
     */
    public void unit() {
        a00 = 1.0; a01 =      a02 =      a03 = 0.0;
        a10 = 0.0; a11 = 1.0; a12 =      a13 = 0.0; 
        a20 =      a21 = 0.0; a22 = 1.0; a23 = 0.0;
    }
    
    /**
     * Get inverse of this matrix
     * 
     * @return the inverse of this matrix
     */
    public Matrix3D inverse() {
        Matrix3D inv = new Matrix3D();
        
        inv.unit();
        
        double det = a00*a11*a22 + a10*a21*a02 + a20*a01*a12 
                     - a00*a21*a12 - a20*a11*a02 - a10*a01*a22;                
        
        inv.a00 = (a11*a22 - a12*a21) / det;
        inv.a01 = (a02*a21 - a01*a22) / det;
        inv.a02 = (a01*a12 - a02*a11) / det;
        inv.a03 = (a01*a23*a22 + a02*a11*a23 + a03*a12*a21
                   - a01*a12*a23-a02*a13*a21-a03*a11*a22) / det;
                
        inv.a10 = (a12*a20 - a10*a22) / det;
        inv.a11 = (a00*a22 - a02*a20) / det;
        inv.a12 = (a02*a10 - a00*a12) / det;
        inv.a13 = (a00*a12*a23 + a02*a13*a20 + a03*a10*a22
                   - a00*a13*a22-a02*a10*a23-a03*a12*a20) / det;
        
        inv.a20 = (a10*a21 - a11*a20) / det;
        inv.a21 = (a01*a20 - a00*a21) / det;
        inv.a22 = (a00*a11 - a01*a10) / det;
        inv.a23 = (a00*a13*a21 + a01*a10*a23 + a03*a11*a20
                   - a00*a11*a23-a01*a13*a20-a03*a10*a21) / det;
        
        return inv;
    }
    
    /**
     * A special method to transform ScreenAtoms .. only relevant
     * when used with MoleculeViewer
     *
     * @param screenAtoms - an array list of ScreenAtom s which will be
     *        transformed according to current transformation matrix.
     */
    public void transform(ArrayList<ScreenAtom> screenAtoms) {
        ScreenAtom screenAtom;        
        double x, y, z;
            
        Iterator<ScreenAtom> atoms = screenAtoms.iterator();
        
        while(atoms.hasNext()) {
            screenAtom = atoms.next();
            
            screenAtom.setTransform(this);
            
            x = screenAtom.getX();
            y = screenAtom.getY();
            z = screenAtom.getZ();                        
            
            screenAtom.setCurrentX((int) (x * a00 + y * a01 + z * a02 + a03));
            screenAtom.setCurrentY((int) (x * a10 + y * a11 + z * a12 + a13));
            screenAtom.setCurrentZ((int) (x * a20 + y * a21 + z * a22 + a23));
        } // end for
    }            
    
    /**
     * A special method to transform ScreenAtoms .. only relevant
     * when used with MoleculeViewer
     *
     * @param screenAtom - an ScreenAtom which will be transformed according 
     *                     to current transformation matrix.
     */
    public void transform(ScreenAtom screenAtom) {        
        double x, y, z;                    
                    
        screenAtom.setTransform(this);
        
        x = screenAtom.getX();
        y = screenAtom.getY();
        z = screenAtom.getZ();
            
        screenAtom.setCurrentX((int) (x * a00 + y * a01 + z * a02 + a03));
        screenAtom.setCurrentY((int) (x * a10 + y * a11 + z * a12 + a13));
        screenAtom.setCurrentZ((int) (x * a20 + y * a21 + z * a22 + a23));        
    }    
    
    /**
     * A special method to transform Molecule object
     *
     * @param molecule - the molecule to be transformed
     * @return Molecule - the transformed molecule
     * @throws Exception - may throw InstantiationException, if unable to
     *  create instance of Molecule class.
     */
    public Molecule transform(Molecule molecule) throws Exception {
        Molecule newMolecule = 
         (Molecule) ((Utility.getDefaultImplFor(Molecule.class)).newInstance());
        
        newMolecule.setTitle(molecule.getTitle());
        
        Iterator<Atom> atoms = molecule.getAtoms();
        
        while(atoms.hasNext()) {
            newMolecule.addAtom(transform(atoms.next()));
        } // end while
        
        return newMolecule;
    } 
       
    /**
     * A special method to transform Atom object
     *
     * @param atom - the atom to be transformed
     * @return Atom - the transformed atom
     * @throws CloneNotSupportedException - may throw this exception if
     * the atom info like bonding info etc. can't be cloned.
     */
    public Atom transform(Atom atom) throws CloneNotSupportedException {
        Atom newAtom = (Atom) atom.clone();       
        double x, y, z;
        
        x = atom.getX();
        y = atom.getY();
        z = atom.getZ();
         
        Point3D atomCenter = newAtom.getAtomCenter();
        
        atomCenter.setX(x * a00 + y * a01 + z * a02 + a03);
        atomCenter.setY(x * a10 + y * a11 + z * a12 + a13);
        atomCenter.setZ(x * a20 + y * a21 + z * a22 + a23);
        
        return newAtom;
    }
    
    /**
     * A special method to transform Point3D object
     *
     * @param point - the point to be transformed
     * @return Point3D - the transformed point     
     */
    public Point3D transform(Point3D point) {
        Point3D newPoint = new Point3D(); 
        double x, y, z;
        
        x = point.getX();
        y = point.getY();
        z = point.getZ();                 
        
        newPoint.setX(x * a00 + y * a01 + z * a02 + a03);
        newPoint.setY(x * a10 + y * a11 + z * a12 + a13);
        newPoint.setZ(x * a20 + y * a21 + z * a22 + a23);
        
        return newPoint;
    }
        
    /**
     * A special method to transform Point3DI object
     *
     * @param point - the point to be transformed
     * @return Point3DI - the transformed point  
     */
    public Point3DI transform(Point3DI point) {        
        double x, y, z;
        
        x = point.getX();
        y = point.getY();
        z = point.getZ();                 
        
        return new Point3DI((x * a00 + y * a01 + z * a02 + a03),
                            (x * a10 + y * a11 + z * a12 + a13),
                            (x * a20 + y * a21 + z * a22 + a23));        
    }
    
    /**
     * A special method to transform Vector3D object
     *
     * @param vec - the vector to be transformed
     * @return Vector3D - the transformed vector  
     */
    public Vector3D transform(Vector3D vec) {        
        double x, y, z;
        
        x = vec.getI();
        y = vec.getJ();
        z = vec.getK();                 
        
        return new Vector3D((x * a00 + y * a01 + z * a02 + a03),
                            (x * a10 + y * a11 + z * a12 + a13),
                            (x * a20 + y * a21 + z * a22 + a23));        
    }
    
    /**
     * A special method to transform a set of points stored in array.
     * Each component is stored in a different array of equal length.
     *
     * @param xp - the x component of the point
     * @param yp - the y component of the point
     * @param zp - the z component of the point
     */
    public void transform(double [] xp, double [] yp, double [] zp) {        
        double x, y, z;
        
        for(int i=0; i<xp.length; i++) {
            x = xp[i];
            y = yp[i];
            z = zp[i];                 
        
            xp[i] = x * a00 + y * a01 + z * a02 + a03;
            yp[i] = x * a10 + y * a11 + z * a12 + a13;
            zp[i] = x * a20 + y * a21 + z * a22 + a23;
        } // end if                
    }        
    
    /**
     * A special method to transform Point3DI objects 
     *
     * @param thePoints - an array list of Point3DI s which will be
     *        transformed according to current transformation matrix.
     */
    public void transformPoints(ArrayList<Point3DI> thePoints) {
        transformPoints(thePoints.iterator());                
    }            
    
    /**
     * A special method to transform Point3DI objects 
     *
     * @param points - an Iterator of Point3DI s which will be
     *        transformed according to current transformation matrix.
     */
    public void transformPoints(Iterator<Point3DI> points) {
        Point3DI point;        
        double x, y, z;
        
        while(points.hasNext()) {
            point = points.next();
            
            x = point.getX();
            y = point.getY();
            z = point.getZ();
            
            point.setCurrentX((int) (x * a00 + y * a01 + z * a02 + a03));
            point.setCurrentY((int) (x * a10 + y * a11 + z * a12 + a13));
            point.setCurrentZ((int) (x * a20 + y * a21 + z * a22 + a23));
        } // end for
    }      
    
    /**
     * A special method to transform Point3DI object 
     *
     * @param point - the point (I) component that need to be transformed
     */
    public void transformPoint(Point3DI point) {        
        double x, y, z;
        
        x = point.getX();
        y = point.getY();
        z = point.getZ();

        point.setCurrentX((int) (x * a00 + y * a01 + z * a02 + a03));
        point.setCurrentY((int) (x * a10 + y * a11 + z * a12 + a13));
        point.setCurrentZ((int) (x * a20 + y * a21 + z * a22 + a23));        
    }      
    
    /**
     * clonable!! i am a cloner
     */
    @Override
    public Object clone() {
        Matrix3D newMat = new Matrix3D();
        
        newMat.a00 = this.a00;
        newMat.a01 = this.a01;
        newMat.a02 = this.a02;
        newMat.a03 = this.a03;
        newMat.a10 = this.a10;
        newMat.a11 = this.a11;
        newMat.a12 = this.a12;
        newMat.a13 = this.a13;
        newMat.a20 = this.a20;
        newMat.a21 = this.a21;
        newMat.a22 = this.a22;
        newMat.a23 = this.a23;
        
        return newMat;
    }
    
    /**
     * Overridden toString()
     */
    @Override
    public String toString() {
            return ("[" + a03 + "," + a00 + "," + a01 + "," + a02 + ";"
                	+ a13 + "," + a10 + "," + a11 + "," + a12 + ";"
                        + a23 + "," + a20 + "," + a12 + "," + a22 + "]");
    }

    /**
     * Holds value of property scaleFactor.
     */
    private double scaleFactor;

    /**
     * Getter for property scaleFactor.
     * It is just a place holder of a factor, which may be used in the
     * transformations. It is not internally used to update the matrix.
     *
     * @return Value of property scaleFactor.
     */
    public double getScaleFactor() {
        return this.scaleFactor;
    }

    /**
     * Setter for property scaleFactor.     
     * It is just a place holder of a factor, which may be used in the
     * transformations. It is not internally used to update the matrix.
     *
     * @param scaleFactor New value of property scaleFactor.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    /**
     * Convert this matrix into a 1D array.
     *
     * return a double[] of this matrix
     */
    public double[] toArray() {
        return new double[]{a00, a01, a02, a03,
                            a10, a11, a12, a13,
                            a20, a21, a22, a23,
                            0.0, 0.0, 0.0, 1.0};
    }
} // end of class Matrix3D
