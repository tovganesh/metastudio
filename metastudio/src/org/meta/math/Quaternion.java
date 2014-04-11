/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math;

import org.meta.math.geom.Point3D;

/**
 * Quaternion operations for MeTA Studio .. for rotation. <br>
 * This code is based on the following:
 * <a href="http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/code/index.htm">
 * http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/code/index.htm </a> <br>
 *
 * The main algebra involved is give here:
 * <a href="http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/index.htm">
 * http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/index.htm </a>
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Quaternion {

    private enum Coding {AXIS_ANGLE, QUATERNION};

    private Coding codingType;

    /** Creates a new instance of Quaternion */
    public Quaternion(Vector3D rotationAxis, double rotationAngle) {
        this.rotationAxis  = rotationAxis;
        this.rotationAngle = rotationAngle;

        codingType = Coding.AXIS_ANGLE;
    }

    /** Creates a new instance of Quaternion */
    public Quaternion(double zRotationAngle, double yRotationAngle, double xRotationAngle) {
        double c1 = Math.cos(zRotationAngle / 2);
        double s1 = Math.sin(zRotationAngle / 2);
        double c2 = Math.cos(yRotationAngle / 2);
        double s2 = Math.sin(yRotationAngle / 2);
        double c3 = Math.cos(xRotationAngle / 2);
        double s3 = Math.sin(xRotationAngle / 2);
        double c1c2 = c1 * c2;
        double s1s2 = s1 * s2;
        this.rotationAngle = c1c2 * c3 + s1s2 * s3;
        this.rotationAxis = new Vector3D(c1c2 * s3 - s1s2 * c3,
                                         c1 * s2 * c3 + s1 * c2 * s3,
                                         s1 * c2 * c3 - c1 * s2 * s3);

        codingType = Coding.QUATERNION;
    }

    protected Vector3D rotationAxis;

    /**
     * Get the value of rotationAxis
     *
     * @return the value of rotationAxis
     */
    public Vector3D getRotationAxis() {
        return rotationAxis;
    }

    /**
     * Set the value of rotationAxis
     *
     * @param rotationAxis new value of rotationAxis
     */
    public void setRotationAxis(Vector3D rotationAxis) {
        this.rotationAxis = rotationAxis;
    }

    protected double rotationAngle;

    /**
     * Get the value of rotationAngle
     *
     * @return the value of rotationAngle
     */
    public double getRotationAngle() {
        return rotationAngle;
    }

    /**
     * Set the value of rotationAngle
     *
     * @param rotationAngle new value of rotationAngle
     */
    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    /**
     * Rotate this point using this Quaternion
     * 
     * @param point the point to be rotated
     * @return the new rotated point
     */
    public Point3D rotate(Point3D point) {
        Point3D newPoint = new Point3D();

        double wh = rotationAngle;
        double xh = rotationAxis.getI();
        double yh = rotationAxis.getJ();
        double zh = rotationAxis.getK();

        if (codingType == Coding.AXIS_ANGLE) {
            double s  = Math.sin(rotationAngle/2);
            xh = xh * s;
            yh = yh * s;
            zh = zh * s;
            wh = Math.cos(rotationAngle/2);
        } // end if

        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();

        newPoint.setX(wh*wh*x + 2*yh*wh*z - 2*zh*wh*y + xh*xh*x + 2*yh*xh*y
                      + 2*zh*xh*z - zh*zh*x - yh*yh*x);
        newPoint.setY(2*xh*yh*x + yh*yh*y + 2*zh*yh*z + 2*wh*zh*x - zh*zh*y
                      + wh*wh*y - 2*xh*wh*z - xh*xh*y);
        newPoint.setZ(2*xh*zh*x + 2*yh*zh*y + zh*zh*z - 2*wh*yh*x - yh*yh*z
                      + 2*wh*xh*y - xh*xh*z + wh*wh*z);

        return newPoint;
    }

    /**
     * Multiply this Quaternion with the argument, and return the result
     * as a new Quaternion object. This operation essentially combines
     * effect of two Quaternion objects.
     * 
     * @param q the Quaternion to which this Quaternion is to be multiplied
     * @return the result of this multiplication
     */
    public Quaternion mul(Quaternion q) {
        double s = Math.sin(q.rotationAngle / 2);
        double qax = rotationAxis.getI() * s;
        double qay = rotationAxis.getJ() * s;
        double qaz = rotationAxis.getK() * s;
        double qaw = Math.cos(rotationAngle / 2);

        s = Math.sin(q.rotationAngle / 2);
        double qbx = q.rotationAxis.getI() * s;
        double qby = q.rotationAxis.getJ() * s;
        double qbz = q.rotationAxis.getK() * s;
        double qbw = Math.cos(q.rotationAngle / 2);

        // now multiply the quaternions
        double angle = qaw*qbw - qax*qbx - qay*qby - qaz*qbz ;
        double x = qax*qbw + qaw*qbx + qay*qbz - qaz*qby;
        double y = qaw*qby - qax*qbz + qay*qbw + qaz*qbx;
        double z = qaw*qbz + qax*qby - qay*qbx + qaz*qbw;
  
        return new Quaternion(new Vector3D(x, y, z), angle);
    }
}

