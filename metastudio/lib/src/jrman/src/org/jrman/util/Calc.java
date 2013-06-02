/*
 Calc.java
 Copyright (C) 2003, 2004 Gerardo Horvilleur Martinez

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

package org.jrman.util;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;

public class Calc {

    private static Tuple3f tmpP0 = new Point3f();

    private static Tuple3f tmpP1 = new Point3f();

    private static Tuple3f tmpP2 = new Point3f();

    private static Tuple3f tmpP3 = new Point3f();

    private static Tuple4f tmpHP0 = new Point4f();

    private static Tuple4f tmpHP1 = new Point4f();

    private static Tuple4f tmpHP2 = new Point4f();

    private static Tuple4f tmpHP3 = new Point4f();

    private Calc() {
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static int clamp(int v, int min, int max) {
        return min(max(v, min), max);
    }

    public static float log(float x, float base) {
        return (float) (Math.log(x) / Math.log(base));
    }
    
    public static int floor(float x) {
        return (int) x - ((x < 0 && x != (int) x) ? 1 : 0);
    }
    
    public static int ceil(float x) {
        return (int) x + ((x > 0 && x != (int) x) ? 1 : 0);
    }

    public static float sign(float x) {
        if (x < 0)
            return -1f;
        else if (x > 0)
            return 1f;
        return 0f;
    }

    public static float step(float min, float value) {
        if (value < min)
            return 0f;
        return 1f;
    }

    public static float smoothstep(float min, float max, float value) {
        if (value < min)
            return 0f;
        if (value >= max)
            return 1f;
        value = (value - min) / (max - min);
        return value * value * (3 - 2 * value);
    }

    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static float clamp(float v, float min, float max) {
        return min(max(v, min), max);
    }

    public static float depth(Point3f p, float near, float far) {
        return (p.z - near) / (far - near);
    }

    public static void perp(Tuple2f t, Tuple2f out) {
        out.set(-t.y, t.x);
    }

    public static float interpolate(float a, float b, float alpha) {
        return a + (b - a) * alpha;
    }

    public static float interpolate(
        float a00,
        float a10,
        float a01,
        float a11,
        float alphaU,
        float alphaV) {
        return interpolate(
            interpolate(a00, a10, alphaU),
            interpolate(a01, a11, alphaU),
            alphaV);
    }

    public static void interpolate(Tuple3f a, Tuple3f b, float alpha, Tuple3f result) {
        result.interpolate(a, b, alpha);
    }

    public static Matrix4f interpolate(Matrix4f a, Matrix4f b, float alpha) {
        return new Matrix4f(
            interpolate(a.m00, b.m00, alpha),
            interpolate(a.m01, b.m01, alpha),
            interpolate(a.m02, b.m02, alpha),
            interpolate(a.m03, b.m03, alpha),
            interpolate(a.m10, b.m10, alpha),
            interpolate(a.m11, b.m11, alpha),
            interpolate(a.m12, b.m12, alpha),
            interpolate(a.m13, b.m13, alpha),
            interpolate(a.m20, b.m20, alpha),
            interpolate(a.m21, b.m21, alpha),
            interpolate(a.m22, b.m22, alpha),
            interpolate(a.m23, b.m23, alpha),
            interpolate(a.m30, b.m30, alpha),
            interpolate(a.m31, b.m31, alpha),
            interpolate(a.m32, b.m32, alpha),
            interpolate(a.m33, b.m33, alpha));
    }

    public static Matrix4f interpolate(
        Matrix4f a00,
        Matrix4f a10,
        Matrix4f a01,
        Matrix4f a11,
        float alphaU,
        float alphaV) {
        return interpolate(
            interpolate(a00, a10, alphaU),
            interpolate(a01, a11, alphaU),
            alphaV);
    }

    public static float bezierInterpolate(float p1, float p2, float p3, float p4, float u) {
        float oneMinusU = 1f - u;
        return oneMinusU * oneMinusU * oneMinusU * p1
            + 3f * u * oneMinusU * oneMinusU * p2
            + 3f * u * u * oneMinusU * p3
            + u * u * u * p4;
    }

    public static float bezierInterpolate(
        float p00,
        float p10,
        float p20,
        float p30,
        float p01,
        float p11,
        float p21,
        float p31,
        float p02,
        float p12,
        float p22,
        float p32,
        float p03,
        float p13,
        float p23,
        float p33,
        float u,
        float v) {
        return bezierInterpolate(
            bezierInterpolate(p00, p10, p20, p30, u),
            bezierInterpolate(p01, p11, p21, p31, u),
            bezierInterpolate(p02, p12, p22, p32, u),
            bezierInterpolate(p03, p13, p23, p33, u),
            v);
    }

    public static void bezierInterpolate(
        Tuple3f p1,
        Tuple3f p2,
        Tuple3f p3,
        Tuple3f p4,
        float u,
        Tuple3f result) {
        result.x = bezierInterpolate(p1.x, p2.x, p3.x, p4.x, u);
        result.y = bezierInterpolate(p1.y, p2.y, p3.y, p4.y, u);
        result.z = bezierInterpolate(p1.z, p2.z, p3.z, p4.z, u);
    }

    public static void bezierInterpolate(
        Tuple4f p1,
        Tuple4f p2,
        Tuple4f p3,
        Tuple4f p4,
        float u,
        Tuple4f result) {
        result.x = bezierInterpolate(p1.x, p2.x, p3.x, p4.x, u);
        result.y = bezierInterpolate(p1.y, p2.y, p3.y, p4.y, u);
        result.z = bezierInterpolate(p1.z, p2.z, p3.z, p4.z, u);
        result.w = bezierInterpolate(p1.w, p2.w, p3.w, p4.w, u);
    }

    public static void bezierInterpolate(
        Tuple3f p00,
        Tuple3f p10,
        Tuple3f p20,
        Tuple3f p30,
        Tuple3f p01,
        Tuple3f p11,
        Tuple3f p21,
        Tuple3f p31,
        Tuple3f p02,
        Tuple3f p12,
        Tuple3f p22,
        Tuple3f p32,
        Tuple3f p03,
        Tuple3f p13,
        Tuple3f p23,
        Tuple3f p33,
        float u,
        float v,
        Tuple3f result) {
        bezierInterpolate(p00, p10, p20, p30, u, tmpP0);
        bezierInterpolate(p01, p11, p21, p31, u, tmpP1);
        bezierInterpolate(p02, p12, p22, p32, u, tmpP2);
        bezierInterpolate(p03, p13, p23, p33, u, tmpP3);
        bezierInterpolate(tmpP0, tmpP1, tmpP2, tmpP3, v, result);
    }

    
    public static void bezierInterpolate(
        Tuple4f p00,
        Tuple4f p10,
        Tuple4f p20,
        Tuple4f p30,
        Tuple4f p01,
        Tuple4f p11,
        Tuple4f p21,
        Tuple4f p31,
        Tuple4f p02,
        Tuple4f p12,
        Tuple4f p22,
        Tuple4f p32,
        Tuple4f p03,
        Tuple4f p13,
        Tuple4f p23,
        Tuple4f p33,
        float u,
        float v,
        Tuple4f result) {
        bezierInterpolate(p00, p10, p20, p30, u, tmpHP0);
        bezierInterpolate(p01, p11, p21, p31, u, tmpHP1);
        bezierInterpolate(p02, p12, p22, p32, u, tmpHP2);
        bezierInterpolate(p03, p13, p23, p33, u, tmpHP3);
        bezierInterpolate(tmpHP0, tmpHP1, tmpHP2, tmpHP3, v, result);
    }

    public static float fovToFocalLength(float fov) {
        return (float) (1 / Math.tan(Math.toRadians(fov / 2)));
    }

}
