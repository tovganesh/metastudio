/*
 PerspectiveTransform.java
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
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

import org.jrman.util.Calc;

public class PerspectiveTransform extends AffineTransform {
    
    private static Point4f hpoint = new Point4f();
    
    private float near;
    
    private float far;

    public static PerspectiveTransform createWithFov(float fov, float near, float far) {
        return createWithFocalLength(Calc.fovToFocalLength(fov), near, far);
    }

    public static PerspectiveTransform createWithFocalLength(float focalLength, float near, float far) {
        return new PerspectiveTransform(focalLength, near, far);
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
        Matrix4f mat = new Matrix4f(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o ,p);
        float near = dis.readFloat();
        float far = dis.readFloat();
        return new PerspectiveTransform(mat, near, far);
    }

    private PerspectiveTransform(Matrix4f matrix, float near, float far) {
        super(matrix);
        this.near = near;
        this.far = far;
    }

    private PerspectiveTransform(float fl, float near, float far) {
        super(
            new Matrix4f(
                fl, 0f, 0f, 0f,
                0f, fl, 0f, 0f,
                // 0f, 0f, far / (far - near), (far * near) / (near - far),
                0f, 0f, 1f, -1f,
                0f, 0f, 1f, 0f));
         this.near = near;
         this.far = far;
    }

    public Transform getCopy() {
        return new PerspectiveTransform(getMatrix(), near, far);
    }

    public Transform concat(Matrix4f m) {
        Matrix4f matrix = getMatrix();
        matrix.mul(m);
        return new PerspectiveTransform(matrix, near, far);
    }

    public Transform concat(Transform transform) {
        Matrix4f matrix = getMatrix();
        matrix.mul(transform.getMatrix());
        return new PerspectiveTransform(matrix, near, far);
    }

    public Transform preConcat(Transform transform) {
        Transform perspective = new PerspectiveTransform(transform.getMatrix(), near, far);
        return perspective.concat(this);
    }

    public void transformPoint(Point3f point) {
        hpoint.set(point);
        transformHPoint(hpoint);
        point.project(hpoint);
        point.z = hpoint.w;
        // point.z = (hpoint. w - near) / (far - near);
    }

    public void transformPoint(Point3f point, Point3f out) {
        hpoint.set(point);
        transformHPoint(hpoint);
        out.project(hpoint);
        out.z = hpoint.w;
        // out.z = (hpoint.w - near) / (far -near);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AffineTransform:\n").append(getMatrix());
        return sb.toString();
    }
    
    public boolean isPerspective() {
        return true;
    }
    
    public void writeToFile(DataOutputStream dos) throws IOException {
        super.writeToFile(dos);
        dos.writeFloat(near);
        dos.writeFloat(far);
    }

}
