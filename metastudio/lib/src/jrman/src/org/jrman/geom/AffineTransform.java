/*
 AffineTransform.java
 Copyright (C) 2003 Gerardo Horvilleur Martinez

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package org.jrman.geom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

public class AffineTransform implements Transform {

    public static final AffineTransform IDENTITY;

    static {
        Matrix4f identity = new Matrix4f();
        identity.setIdentity();
        IDENTITY = new AffineTransform(identity);
    }

    private Matrix4f matrix;

    private Matrix4f inverseMatrix;

    private Matrix4f normalsMatrix;

    private Matrix4f inverseNormalsMatrix;

    public static Transform createOrthographic(float near, float far) {
            Matrix4f m =
                new Matrix4f(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1, //1f / (far - near),
        0f, //-near / (far - near),
    0f, 0f, 0f, 1f);
        return new AffineTransform(m);
    }

    public static Transform createRaster(
        Bounds2f screenWindow,
        float frameAspectRatio,
        int horizontalResolution,
        int verticalResolution,
        float pixelAspectRatio) {
        float imageAspectRatio =
            (horizontalResolution * pixelAspectRatio) / verticalResolution;
        if (imageAspectRatio < frameAspectRatio)
            verticalResolution =
                (int) ((horizontalResolution * pixelAspectRatio) / frameAspectRatio);
        else if (imageAspectRatio > frameAspectRatio)
            horizontalResolution =
                (int) ((verticalResolution * frameAspectRatio) / pixelAspectRatio);
        // Doesn't handle "flipped" screen window
        Point2f min = screenWindow.getMin();
        Point2f max = screenWindow.getMax();
        Matrix4f m =
            new Matrix4f(
                horizontalResolution / (max.x - min.x),
                0f,
                0f,
                0f,
                0f,
                verticalResolution / (max.y - min.y),
                0f,
                0f,
                0f,
                0f,
                1f,
                0f,
                0f,
                0f,
                0f,
                1f);
        Matrix4f tmp = new Matrix4f();
        tmp.setIdentity();
        tmp.setTranslation(new Vector3f(0f, max.y - min.y, 0f));
        m.mul(tmp);
        tmp = new Matrix4f(1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f);
        m.mul(tmp);
        tmp.setIdentity();
        tmp.setTranslation(new Vector3f(-min.x, -min.y, 0f));
        m.mul(tmp);
        return new AffineTransform(m);
    }

    public static Transform createNDC(int horizontalResolution, int verticalResolution) {
        Matrix4f m =
            new Matrix4f(
                1f / horizontalResolution,
                0f,
                0f,
                0f,
                0f,
                1f / verticalResolution,
                0f,
                0f,
                0f,
                0f,
                1f,
                0f,
                0f,
                0f,
                0f,
                1f);
        return new AffineTransform(m);
    }

    public static Transform readFromFile(DataInputStream dis) throws IOException {
        float a = dis.readFloat();
        float b = dis.readFloat();
        float c = dis.readFloat();
        float d = dis.readFloat();
        float e = dis.readFloat();
        float f = dis.readFloat();
        float g = dis.readFloat();
        float h = dis.readFloat();
        float i = dis.readFloat();
        float j = dis.readFloat();
        float k = dis.readFloat();
        float l = dis.readFloat();
        float m = dis.readFloat();
        float n = dis.readFloat();
        float o = dis.readFloat();
        float p = dis.readFloat();
        Matrix4f mat = new Matrix4f(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
        return new AffineTransform(mat);
    }

    protected AffineTransform(Matrix4f matrix) {
        this.matrix = matrix;
    }

    protected AffineTransform(Transform transform) {
        this(transform.getMatrix());
    }

    public Transform getCopy() {
        return new AffineTransform(this);
    }

    public Transform concat(Transform transform) {
        Matrix4f matrix = getMatrix();
        matrix.mul(transform.getMatrix());
        return new AffineTransform(matrix);
    }

    public Transform concat(Matrix4f m) {
        Matrix4f matrix = getMatrix();
        matrix.mul(m);
        return new AffineTransform(matrix);
    }

    public void transformPoint(Point2f point) {
        Point3f p = new Point3f(point.x, point.y, 1f);
        matrix.transform(p);
        point.x = p.x;
        point.y = p.y;
    }

    public void transformPoint(Point2f point, Point2f out) {
        Point3f p = new Point3f(point.x, point.y, 1f);
        matrix.transform(p);
        out.x = p.x;
        out.y = p.y;
    }

    public void transformPoint(Point3f point) {
        matrix.transform(point);
    }

    public void transformPoint(Point3f point, Point3f out) {
        matrix.transform(point, out);
    }

    public void transformHPoint(Point4f hpoint) {
        matrix.transform(hpoint);
    }

    public void transformHPoint(Point4f hpoint, Point4f out) {
        matrix.transform(hpoint, out);
    }

    public void transformVector(Vector3f vector) {
        matrix.transform(vector);
    }

    public void transformVector(Vector3f vector, Vector3f out) {
        matrix.transform(vector, out);
    }

    public void transformNormal(Vector3f normal) {
        getNormalsMatrix().transform(normal);
    }

    public void transformNormal(Vector3f normal, Vector3f out) {
        getNormalsMatrix().transform(normal, out);
    }

    public void untransformPoint(Point2f point) {
        Point3f p = new Point3f(point.x, point.y, 1f);
        getInverseMatrix().transform(p);
        point.x = p.x;
        point.y = p.y;
    }

    public void untransformPoint(Point2f point, Point3f out) {
        Point3f p = new Point3f(point.x, point.y, 1f);
        getInverseMatrix().transform(p);
        out.x = p.x;
        out.y = p.y;
    }

    public void untransformPoint(Point3f point) {
        getInverseMatrix().transform(point);
    }

    public void untransformPoint(Point3f point, Point3f out) {
        getInverseMatrix().transform(point, out);
    }

    public void untransformHPoint(Point4f hpoint) {
        getInverseMatrix().transform(hpoint);
    }

    public void untransformHPoint(Point4f hpoint, Point4f out) {
        getInverseMatrix().transform(hpoint, out);
    }

    public void untransformVector(Vector3f vector) {
        getInverseMatrix().transform(vector);
    }

    public void untransformVector(Vector3f vector, Vector3f out) {
        getInverseMatrix().transform(vector, out);
    }

    public void untransformNormal(Vector3f normal) {
        getInverseNormalsMatrix().transform(normal);
    }

    public void untransformNormal(Vector3f normal, Vector3f out) {
        getInverseNormalsMatrix().transform(normal, out);
    }

    public Matrix4f getMatrix() {
        return new Matrix4f(matrix);
    }

    protected Matrix4f getInverseMatrix() {
        if (inverseMatrix == null) {
            inverseMatrix = new Matrix4f(matrix);
            inverseMatrix.invert();
        }
        return inverseMatrix;
    }

    protected Matrix4f getNormalsMatrix() {
        if (normalsMatrix == null) {
            normalsMatrix = new Matrix4f(getInverseMatrix());
            normalsMatrix.transpose();
        }
        return normalsMatrix;
    }

    protected Matrix4f getInverseNormalsMatrix() {
        if (inverseNormalsMatrix == null) {
            inverseNormalsMatrix = new Matrix4f(getNormalsMatrix());
            inverseNormalsMatrix.invert();
        }
        return inverseNormalsMatrix;
    }

    public Transform getInverse() {
        return new AffineTransform(getInverseMatrix());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AffineTransform:\n").append(matrix);
        return sb.toString();
    }

    public boolean isPerspective() {
        return false;
    }

    public void writeToFile(DataOutputStream dos) throws IOException {
        dos.writeFloat(matrix.m00);
        dos.writeFloat(matrix.m01);
        dos.writeFloat(matrix.m02);
        dos.writeFloat(matrix.m03);
        dos.writeFloat(matrix.m10);
        dos.writeFloat(matrix.m11);
        dos.writeFloat(matrix.m12);
        dos.writeFloat(matrix.m13);
        dos.writeFloat(matrix.m20);
        dos.writeFloat(matrix.m21);
        dos.writeFloat(matrix.m22);
        dos.writeFloat(matrix.m23);
        dos.writeFloat(matrix.m30);
        dos.writeFloat(matrix.m31);
        dos.writeFloat(matrix.m32);
        dos.writeFloat(matrix.m33);
    }

}
