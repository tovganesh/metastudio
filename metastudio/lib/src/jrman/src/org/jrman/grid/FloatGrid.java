/*
 * FloatGrid.java Copyright (C) 2003 Gerardo Horvilleur Martinez
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jrman.grid;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jrman.maps.MipMap;
import org.jrman.maps.ShadowMap;
import org.jrman.util.Calc;
import org.jrman.util.PerlinNoise;

public class FloatGrid extends Grid {

    private static Point3fGrid pg1 = new Point3fGrid();

    private static Point3f p1 = new Point3f();

    private static Point3f p2 = new Point3f();

    private static float[] mmData = new float[4];

    public float[] data;

    private static float getFloat(MipMap texture, float s, float t, 
                                  float area, int band) {
        texture.getData(s, t, area, mmData);
        return mmData[band];
    }

    public static FloatGrid getInstance() {
        return new FloatGrid();
    }

    public FloatGrid() {
        data = new float[maxSize];
    }

    public float get(int i) {
        return data[i];
    }

    public void set(int i, float in) {
        data[i] = in;
    }

    public float get(int u, int v) {
        return get(v * uSize + u);
    }

    public void set(int u, int v, float in) {
        set(v * uSize + u, in);
    }

    /*
     * set
     */

    public void set(float f) {
        for (int i = 0; i < size; i++)
            data[i] = f;
    }

    public void set(float f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f;
    }

    public void set(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = f.data[i];
    }

    public void set(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f.data[i];
    }

    /*
     * negate
     */

    public void negate(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = -f.data[i];
    }

    public void negate(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = -f.data[i];
    }

    /*
     * add
     */

    public void add(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] + f2;
    }

    public void add(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] + f2;
    }

    public void add(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] + f2.data[i];
    }

    public void add(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] + f2.data[i];
    }

    /*
     * sub
     */

    public void sub(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] - f2;
    }

    public void sub(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] - f2;
    }

    public void sub(float f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1 - f2.data[i];
    }

    public void sub(float f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1 - f2.data[i];
    }

    public void sub(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] - f2.data[i];
    }

    public void sub(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] - f2.data[i];
    }

    /*
     * mul
     */

    public void mul(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] * f2;
    }

    public void mul(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] * f2;
    }

    public void mul(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] * f2.data[i];
    }

    public void mul(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] * f2.data[i];
    }

    /*
     * div
     */

    public void div(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] / f2;
    }

    public void div(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] / f2;
        ;
    }

    public void div(float f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1 / f2.data[i];
    }

    public void div(float f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1 / f2.data[i];
    }

    public void div(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = f1.data[i] / f2.data[i];
    }

    public void div(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = f1.data[i] / f2.data[i];
    }

    /*
     * toRadians
     */

    public void toRadians(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.toRadians(f.data[i]);
    }

    public void toRadians(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.toRadians(f.data[i]);
    }

    /*
     * toDegrees
     */

    public void toDegrees(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.toDegrees(f.data[i]);
    }

    public void toDegrees(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.toDegrees(f.data[i]);
    }

    /*
     * sin
     */

    public void sin(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.sin(f.data[i]);
    }

    public void sin(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.sin(f.data[i]);
    }

    /*
     * asin
     */

    public void asin(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.asin(f.data[i]);
    }

    public void asin(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.asin(f.data[i]);
    }

    /*
     * cos
     */

    public void cos(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.cos(f.data[i]);
    }

    public void cos(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.cos(f.data[i]);
    }

    /*
     * acos
     */

    public void acos(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.acos(f.data[i]);
    }

    public void acos(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.acos(f.data[i]);
    }

    /*
     * tan
     */

    public void tan(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.tan(f.data[i]);
    }

    public void tan(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.tan(f.data[i]);
    }

    /*
     * atan
     */

    public void atan(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.atan(f.data[i]);
    }

    public void atan(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.atan(f.data[i]);
    }

    /*
     * atan (2 args)
     */

    public void atan(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.atan2(f1.data[i], f2);
    }

    public void atan(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.atan2(f1.data[i], f2);
    }

    public void atan(float f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.atan2(f1, f2.data[i]);
    }

    public void atan(float f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.atan2(f1, f2.data[i]);
    }

    public void atan(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.atan2(f1.data[i], f2.data[i]);
    }

    public void atan(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.atan2(f1.data[i], f2.data[i]);
    }

    /*
     * pow
     */

    public void pow(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.pow(f1.data[i], f2);
    }

    public void pow(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.pow(f1.data[i], f2);
    }

    public void pow(float f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.pow(f1, f2.data[i]);
    }

    public void pow(float f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.pow(f1, f2.data[i]);
    }

    public void pow(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.pow(f1.data[i], f2.data[i]);
    }

    public void pow(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.pow(f1.data[i], f2.data[i]);
    }

    /*
     * exp
     */

    public void exp(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.exp(f.data[i]);
    }

    public void exp(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.exp(f.data[i]);
    }

    /*
     * sqrt
     */

    public void sqrt(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.sqrt(f.data[i]);
    }

    public void sqrt(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.sqrt(f.data[i]);
    }

    /*
     * isqrt
     */

    public void isqrt(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = 1f / (float) Math.sqrt(f.data[i]);
    }

    public void isqrt(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = 1f / (float) Math.sqrt(f.data[i]);
    }

    /*
     * log
     */

    public void log(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.log(f.data[i]);
    }

    public void log(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.log(f.data[i]);
    }

    /*
     * log base
     */

    public void log(FloatGrid f, float base) {
        double baseLog = Math.log(base);
        for (int i = 0; i < size; i++)
            data[i] = (float) (Math.log(f.data[i]) / baseLog);
    }

    public void log(FloatGrid f, float base, BooleanGrid cond) {
        double baseLog = Math.log(base);
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) (Math.log(f.data[i]) / baseLog);
    }

    /*
     * mod
     */

    public void mod(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.IEEEremainder(f1.data[i], f2);
    }

    public void mod(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.IEEEremainder(f1.data[i], f2);
    }

    public void mod(float f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.IEEEremainder(f1, f2.data[i]);
    }

    public void mod(float f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.IEEEremainder(f1, f2.data[i]);
    }

    public void mod(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.IEEEremainder(f1.data[i], f2.data[i]);
    }

    public void mod(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.IEEEremainder(f1.data[i], f2.data[i]);
    }

    /*
     * abs
     */

    public void abs(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.abs(f.data[i]);
    }

    public void abs(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.abs(f.data[i]);
    }

    /*
     * sign
     */

    public void sign(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Calc.sign(f.data[i]);
    }

    public void sign(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Calc.sign(f.data[i]);
    }

    /*
     * min
     */

    public void min(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.min(f1.data[i], f2);
    }

    public void min(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.min(f1.data[i], f2);
    }

    public void min(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.min(f1.data[i], f2.data[i]);
    }

    public void min(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.min(f1.data[i], f2.data[i]);
    }

    /*
     * max
     */

    public void max(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.max(f1.data[i], f2);
    }

    public void max(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.max(f1.data[i], f2);
    }

    public void max(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.max(f1.data[i], f2.data[i]);
    }

    public void max(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.max(f1.data[i], f2.data[i]);
    }

    /*
     * clamp
     */

    public void clamp(FloatGrid f, float min, float max) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.clamp(f.data[i], min, max);
    }

    public void clamp(FloatGrid f, float min, float max, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.clamp(f.data[i], min, max);
    }

    /*
     * mix
     */

    public void mix(float f1, float f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.interpolate(f1, f2, alpha.data[i]);
    }

    public void mix(float f1, float f2, FloatGrid alpha, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.interpolate(f1, f2, alpha.data[i]);
    }

    public void mix(float f1, FloatGrid f2, float alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.interpolate(f1, f2.data[i], alpha);
    }

    public void mix(float f1, FloatGrid f2, float alpha, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.interpolate(f1, f2.data[i], alpha);
    }

    public void mix(float f1, FloatGrid f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.interpolate(f1, f2.data[i], alpha.data[i]);
    }

    public void mix(float f1, FloatGrid f2, FloatGrid alpha, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.interpolate(f1, f2.data[i], alpha.data[i]);
    }

    public void mix(FloatGrid f1, float f2, float alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.interpolate(f1.data[i], f2, alpha);
    }

    public void mix(FloatGrid f1, float f2, float alpha, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.interpolate(f1.data[i], f2, alpha);
    }

    public void mix(FloatGrid f1, float f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.interpolate(f1.data[i], f2, alpha.data[i]);
    }

    public void mix(FloatGrid f1, float f2, FloatGrid alpha, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.interpolate(f1.data[i], f2, alpha.data[i]);
    }

    public void mix(FloatGrid f1, FloatGrid f2, float alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.interpolate(f1.data[i], f2.data[i], alpha);
    }

    public void mix(FloatGrid f1, FloatGrid f2, float alpha, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.interpolate(f1.data[i], f2.data[i], alpha);
    }

    public void mix(FloatGrid f1, FloatGrid f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.interpolate(f1.data[i], f2.data[i], alpha.data[i]);
    }

    public void mix(FloatGrid f1, FloatGrid f2, FloatGrid alpha,
                    BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] =
                    Calc.interpolate(f1.data[i], f2.data[i], alpha.data[i]);
    }

    /*
     * floor
     */

    public void floor(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Calc.floor(f.data[i]);
    }

    public void floor(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Calc.floor(f.data[i]);
    }

    /*
     * ceil
     */

    public void ceil(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = (float) Calc.ceil(f.data[i]);
    }

    public void ceil(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Calc.ceil(f.data[i]);
    }

    /*
     * round
     */

    public void round(FloatGrid f) {
        for (int i = 0; i < size; i++)
            data[i] = Math.round(f.data[i]);
    }

    public void round(FloatGrid f, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Math.round(f.data[i]);
    }

    /*
     * step
     */

    public void step(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.step(f1.data[i], f2);
    }

    public void step(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.step(f1.data[i], f2);
    }

    public void step(float f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.step(f1, f2.data[i]);
    }

    public void step(float f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.step(f1, f2.data[i]);
    }

    public void step(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.step(f1.data[i], f2.data[i]);
    }

    public void step(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.step(f1.data[i], f2.data[i]);
    }

    /*
     * smoothstep
     */

    public void smoothstep(float f1, float f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.smoothstep(f1, f2, alpha.data[i]);
    }

    public void smoothstep(float f1, float f2, FloatGrid alpha, 
                           BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.smoothstep(f1, f2, alpha.data[i]);
    }

    public void smoothstep(float f1, FloatGrid f2, float alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.smoothstep(f1, f2.data[i], alpha);
    }

    public void smoothstep(float f1, FloatGrid f2, float alpha,
                           BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.smoothstep(f1, f2.data[i], alpha);
    }

    public void smoothstep(float f1, FloatGrid f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.smoothstep(f1, f2.data[i], alpha.data[i]);
    }

    public void smoothstep(float f1, FloatGrid f2, FloatGrid alpha, 
                           BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.smoothstep(f1, f2.data[i], alpha.data[i]);
    }

    public void smoothstep(FloatGrid f1, float f2, float alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.smoothstep(f1.data[i], f2, alpha);
    }

    public void smoothstep(FloatGrid f1, float f2, float alpha,
                           BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.smoothstep(f1.data[i], f2, alpha);
    }

    public void smoothstep(FloatGrid f1, float f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.smoothstep(f1.data[i], f2, alpha.data[i]);
    }

    public void smoothstep(FloatGrid f1, float f2, FloatGrid alpha,
                           BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.smoothstep(f1.data[i], f2, alpha.data[i]);
    }

    public void smoothstep(FloatGrid f1, FloatGrid f2, float alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.smoothstep(f1.data[i], f2.data[i], alpha);
    }

    public void smoothstep(FloatGrid f1, FloatGrid f2, float alpha,
                           BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.smoothstep(f1.data[i], f2.data[i], alpha);
    }

    public void smoothstep(FloatGrid f1, FloatGrid f2, FloatGrid alpha) {
        for (int i = 0; i < size; i++)
            data[i] = Calc.smoothstep(f1.data[i], f2.data[i], alpha.data[i]);
    }

    public void smoothstep(FloatGrid f1, FloatGrid f2, FloatGrid alpha, 
                           BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] =
                    Calc.smoothstep(f1.data[i], f2.data[i], alpha.data[i]);
    }

    /*
     * filterstep
     */

    // TODO implement float filterstep

    /*
     * spline
     */

    // TODO implement float spline

    /*
     * Du
     */

    public void du(FloatGrid u) {
        for (int i = 0; i < size; i += uSize) {
            for (int j = 0; j < uSize - 1; j++)
                data[i + j] = u.data[i + j + 1] - u.data[i + j];
            data[i + uSize - 1] = u.data[i + uSize - 1] - u.data[i + uSize - 2];
        }
    }

    public void Du(FloatGrid f1, FloatGrid du) {
        for (int i = 0; i < size; i += uSize) {
            for (int j = 0; j < uSize - 1; j++)
                data[i + j] = (f1.data[i + j + 1] - f1.data[i + j]) / 
                    du.data[i + j];
            data[i + uSize - 1] =
                (f1.data[i + uSize - 1] - f1.data[i + uSize - 2]) / 
                du.data[i + uSize - 1];
        }
    }

    public void Du(FloatGrid f1, FloatGrid du, BooleanGrid cond) {
        for (int i = 0; i < size; i += uSize) {
            for (int j = 0; j < uSize - 1; j++)
                if (cond.data[i + j])
                    data[i + j] = (f1.data[i + j + 1] - f1.data[i + j]) / 
                        du.data[i + j];
            if (cond.data[i + uSize - 1])
                data[i + uSize - 1] =
                    (f1.data[i + uSize - 1] - f1.data[i + uSize - 2]) / 
                    du.data[i + uSize - 1];
        }
    }

    /*
     * Dv
     */

    public void dv(FloatGrid v) {
        for (int i = 0; i < size - uSize; i += uSize)
            for (int j = 0; j < uSize; j++)
                data[i + j] = v.data[i + j + uSize] - v.data[i + j];
        for (int i = size - uSize; i < size; i++)
            data[i] = v.data[i] - v.data[i - uSize];
    }

    public void Dv(FloatGrid f1, FloatGrid dv) {
        for (int i = 0; i < size - uSize; i += uSize)
            for (int j = 0; j < uSize; j++)
                data[i + j] = (f1.data[i + j + uSize] - f1.data[i + j]) / 
                    dv.data[i + j];
        for (int i = size - uSize; i < size; i++)
            data[i] = (f1.data[i] - f1.data[i - uSize]) / dv.data[i];
    }

    public void Dv(FloatGrid f1, FloatGrid dv, BooleanGrid cond) {
        for (int i = 0; i < size - uSize; i += uSize)
            for (int j = 0; j < uSize; j++)
                if (cond.data[i + j])
                    data[i + j] = (f1.data[i + j + uSize] - f1.data[i + j]) / 
                        dv.data[i + j];
        for (int i = size - uSize; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] - f1.data[i - uSize]) / dv.data[i];
    }

    /*
     * random
     */

    public void random() {
        for (int i = 0; i < size; i++)
            data[i] = (float) Math.random();
    }

    public void random(BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (float) Math.random();
    }

    /*
     * noise
     */

    public void noise(FloatGrid x) {
        for (int i = 0; i < size; i++)
            data[i] = 0.5f * PerlinNoise.noise1d(x.data[i]) + 0.5f;
    }

    public void noise(FloatGrid x, FloatGrid y) {
        for (int i = 0; i < size; i++)
            data[i] = 0.5f * PerlinNoise.noise2d(x.data[i], y.data[i]) + 0.5f;
    }

    public void noise(FloatGrid x, FloatGrid y, FloatGrid z) {
        for (int i = 0; i < size; i++)
            data[i] = 0.5f * PerlinNoise.noise3d(x.data[i], y.data[i], 
                                                 z.data[i]) + 0.5f;
    }

    public void noise(FloatGrid x, FloatGrid y, FloatGrid z, float w) {
        for (int i = 0; i < size; i++)
            data[i] = 0.5f * PerlinNoise.noise4d(x.data[i], y.data[i], 
                                                 z.data[i], w) + 0.5f;
    }

    public void noise(Tuple3fGrid t) {
        for (int i = 0; i < size; i++)
            data[i] = 0.5f * PerlinNoise.noise3d(t.data[i].x, t.data[i].y,
                                                 t.data[i].z) + 0.5f;
    }

    public void noise(Tuple3fGrid t, float w) {
        for (int i = 0; i < size; i++)
            data[i] =
                0.5f * PerlinNoise.noise4d(t.data[i].x, t.data[i].y, 
                                           t.data[i].z, w) + 0.5f;

    }

    /*
     * snoise
     */

    public void snoise(FloatGrid x) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.noise1d(x.data[i]);
    }

    public void snoise(FloatGrid x, FloatGrid y) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.noise2d(x.data[i], y.data[i]);
    }

    public void snoise(FloatGrid x, FloatGrid y, FloatGrid z) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.noise3d(x.data[i], y.data[i], z.data[i]);
    }

    public void snoise(FloatGrid x, FloatGrid y, FloatGrid z, float w) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.noise4d(x.data[i], y.data[i], z.data[i], w);
    }

    public void snoise(Tuple3fGrid t) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.noise3d(t.data[i].x, t.data[i].y, 
                                          t.data[i].z);
    }

    public void snoise(Tuple3fGrid t, float w) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.noise4d(t.data[i].x, t.data[i].y, 
                                          t.data[i].z, w);
    }

    /*
     * pnoise
     */

    public void pnoise(FloatGrid x, float px) {
        assert px > 0f;
        for (int i = 0; i < size; i++) {
            float nx = x.data[i] > 0 ? x.data[i] % px : (-x.data[i]) % px;
            float pmx = px - nx;
            data[i] = (PerlinNoise.noise1d(nx) * pmx + 
                       PerlinNoise.noise1d(-pmx) * nx);
            data[i] /= px;
            data[i] = .5f + data[i] * .5f;
        }
    }

    public void pnoise(FloatGrid x, FloatGrid y, float px, float py) {
        assert px > 0f && py > 0f;
        for (int i = 0; i < size; i++) {
            float nx = x.data[i] > 0 ? x.data[i] % px : (-x.data[i]) % px;
            float ny = y.data[i] > 0 ? y.data[i] % py : (-y.data[i]) % py;
            float pmx = px - nx;
            float pmy = py - ny;
            data[i] = (PerlinNoise.noise2d(nx, ny) * pmx + 
                       PerlinNoise.noise2d(-pmx, ny) * nx) * pmy;
            data[i] += (PerlinNoise.noise2d(nx, -pmy) * pmx + 
                        PerlinNoise.noise2d(-pmx, -pmy) * nx)
                * ny;
            data[i] /= px * py;
            data[i] = .5f + data[i] * .5f;
        }
    }

    public void pnoise(FloatGrid x, FloatGrid y, FloatGrid z, float px, 
                       float py, float pz) {
        assert px > 0f && py > 0f && pz > 0f;
        for (int i = 0; i < size; i++) {
            float nx = x.data[i] > 0 ? x.data[i] % px : (-x.data[i]) % px;
            float ny = y.data[i] > 0 ? y.data[i] % py : (-y.data[i]) % py;
            float nz = z.data[i] > 0 ? z.data[i] % pz : (-z.data[i]) % pz;
            float pmx = px - nx;
            float pmy = py - ny;
            float pmz = pz - nz;
            data[i] =
                ((PerlinNoise.noise3d(nx, ny, nz) * pmx
                    + PerlinNoise.noise3d(-pmx, ny, nz) * nx)
                    * pmy
                    + (PerlinNoise.noise3d(nx, -pmy, nz) * pmx
                        + PerlinNoise.noise3d(-pmx, -pmy, nz) * nx)
                        * ny)
                    * pmz;
            data[i]
                += ((PerlinNoise.noise3d(nx, ny, -pmz) * pmx
                    + PerlinNoise.noise3d(-pmx, ny, -pmz) * nx)
                    * pmy
                    + (PerlinNoise.noise3d(nx, -pmy, -pmz) * pmx
                        + PerlinNoise.noise3d(-pmx, -pmy, -pmz) * nx)
                        * ny)
                * nz;
            data[i] /= px * py * pz;
            data[i] = .5f + data[i] * .5f;
        }
    }

    public void pnoise(
        FloatGrid x,
        FloatGrid y,
        FloatGrid z,
        float w,
        float px,
        float py,
        float pz,
        float pw) {
        assert px > 0f && py > 0f && pz > 0f && pw > 0f;
        float nw = w > 0 ? w % pw : (-w) % pw;
        float pmw = pw - nw;
        for (int i = 0; i < size; i++) {
            float nx = x.data[i] > 0 ? x.data[i] % px : (-x.data[i]) % px;
            float ny = y.data[i] > 0 ? y.data[i] % py : (-y.data[i]) % py;
            float nz = z.data[i] > 0 ? z.data[i] % pz : (-z.data[i]) % pz;
            float pmx = px - nx;
            float pmy = py - ny;
            float pmz = pz - nz;
            data[i] =
                (((PerlinNoise.noise4d(nx, ny, nz, nw) * pmx
                    + PerlinNoise.noise4d(-pmx, ny, nz, nw) * nx)
                    * pmy
                    + (PerlinNoise.noise4d(nx, -pmy, nz, nw) * pmx
                        + PerlinNoise.noise4d(-pmx, -pmy, nz, nw) * nx)
                        * ny)
                    * pmz)
                    * pmw
                    + (((PerlinNoise.noise4d(nx, ny, nz, nw) * pmx
                        + PerlinNoise.noise4d(-pmx, ny, nz, nw) * nx)
                        * pmy
                        + (PerlinNoise.noise4d(nx, -pmy, nz, nw) * pmx
                            + PerlinNoise.noise4d(-pmx, -pmy, nz, nw) * nx)
                            * ny)
                        * pmz)
                        * pmw;
            data[i]
                += (((PerlinNoise.noise4d(nx, ny, -pmz, -pmw) * pmx
                    + PerlinNoise.noise4d(-pmx, ny, -pmz, -pmw) * nx)
                    * pmy
                    + (PerlinNoise.noise4d(nx, -pmy, -pmz, -pmw) * pmx
                        + PerlinNoise.noise4d(-pmx, -pmy, -pmz, -pmw) * nx)
                        * ny)
                    * nz)
                * nw
                + (((PerlinNoise.noise4d(nx, ny, -pmz, -pmw) * pmx
                    + PerlinNoise.noise4d(-pmx, ny, -pmz, -pmw) * nx)
                    * pmy
                    + (PerlinNoise.noise4d(nx, -pmy, -pmz, -pmw) * pmx
                        + PerlinNoise.noise4d(-pmx, -pmy, -pmz, -pmw) * nx)
                        * ny)
                    * nz)
                    * nw;
            data[i] /= px * py * pz * pw;
            data[i] = .5f + data[i] * .5f;
        }
    }

    public void pnoise(Tuple3fGrid t, float px, float py, float pz) {
        assert px > 0f && py > 0f && pz > 0f;
        for (int i = 0; i < size; i++) {
            Tuple3f tup = t.data[i];
            float nx = tup.x > 0 ? tup.x % px : (-tup.x) % px;
            float ny = tup.y > 0 ? tup.y % py : (-tup.y) % py;
            float nz = tup.z > 0 ? tup.z % pz : (-tup.z) % pz;
            float pmx = px - nx;
            float pmy = py - ny;
            float pmz = pz - nz;
            data[i] =
                ((PerlinNoise.noise3d(nx, ny, nz) * pmx
                    + PerlinNoise.noise3d(-pmx, ny, nz) * nx)
                    * pmy
                    + (PerlinNoise.noise3d(nx, -pmy, nz) * pmx
                        + PerlinNoise.noise3d(-pmx, -pmy, nz) * nx)
                        * ny)
                    * pmz;
            data[i]
                += ((PerlinNoise.noise3d(nx, ny, -pmz) * pmx
                    + PerlinNoise.noise3d(-pmx, ny, -pmz) * nx)
                    * pmy
                    + (PerlinNoise.noise3d(nx, -pmy, -pmz) * pmx
                        + PerlinNoise.noise3d(-pmx, -pmy, -pmz) * nx)
                        * ny)
                * nz;
            data[i] /= px * py * pz;
            data[i] = .5f + data[i] * .5f;
        }
    }

    public void pnoise(Tuple3fGrid t, float w, float px, float py, float pz,
                       float pw) {
        assert px > 0f && py > 0f && pz > 0f && pw > 0f;
        float nw = w > 0 ? w % pw : (-w) % pw;
        float pmw = pw - nw;
        for (int i = 0; i < size; i++) {
            Tuple3f tup = t.data[i];
            float nx = tup.x > 0 ? tup.x % px : (-tup.x) % px;
            float ny = tup.y > 0 ? tup.y % py : (-tup.y) % py;
            float nz = tup.z > 0 ? tup.z % pz : (-tup.z) % pz;
            float pmx = px - nx;
            float pmy = py - ny;
            float pmz = pz - nz;
            data[i] =
                (((PerlinNoise.noise4d(nx, ny, nz, nw) * pmx
                    + PerlinNoise.noise4d(-pmx, ny, nz, nw) * nx)
                    * pmy
                    + (PerlinNoise.noise4d(nx, -pmy, nz, nw) * pmx
                        + PerlinNoise.noise4d(-pmx, -pmy, nz, nw) * nx)
                        * ny)
                    * pmz)
                    * pmw
                    + (((PerlinNoise.noise4d(nx, ny, nz, nw) * pmx
                        + PerlinNoise.noise4d(-pmx, ny, nz, nw) * nx)
                        * pmy
                        + (PerlinNoise.noise4d(nx, -pmy, nz, nw) * pmx
                            + PerlinNoise.noise4d(-pmx, -pmy, nz, nw) * nx)
                            * ny)
                        * pmz)
                        * pmw;
            data[i]
                += (((PerlinNoise.noise4d(nx, ny, -pmz, -pmw) * pmx
                    + PerlinNoise.noise4d(-pmx, ny, -pmz, -pmw) * nx)
                    * pmy
                    + (PerlinNoise.noise4d(nx, -pmy, -pmz, -pmw) * pmx
                        + PerlinNoise.noise4d(-pmx, -pmy, -pmz, -pmw) * nx)
                        * ny)
                    * nz)
                * nw
                + (((PerlinNoise.noise4d(nx, ny, -pmz, -pmw) * pmx
                    + PerlinNoise.noise4d(-pmx, ny, -pmz, -pmw) * nx)
                    * pmy
                    + (PerlinNoise.noise4d(nx, -pmy, -pmz, -pmw) * pmx
                        + PerlinNoise.noise4d(-pmx, -pmy, -pmz, -pmw) * nx)
                        * ny)
                    * nz)
                    * nw;
            data[i] /= px * py * pz * pw;
            data[i] = .5f + data[i] * .5f;
        }
    }

    /*
    public static float pnoise(float x, float y, float px, float py) {
        assert px > 0f && py > 0f;
        x= x > 0 ? x % px : (-x) % px;
        y= y > 0 ? y % py : (-y) % py;
        float pmx= px - x;
        float pmy= py - y;
        float tmp= (PerlinNoise.noise2d(x, y) * pmx + PerlinNoise.noise2d(-pmx, y) * x) * pmy;
        tmp += (PerlinNoise.noise2d(x, -pmy) * pmx + PerlinNoise.noise2d(-pmx, -pmy) * x) * y;
        tmp /= px * py;
        return .5f + tmp * .5f;
    }
    
    public static void main(String[] args) {
        float x, y, l= 4f, w= 4f, n, max= -Float.MAX_VALUE, min= Float.MAX_VALUE;
        int i, j, il= 500, iw= 500;
        float dx= w / iw, dy= l / il;
        BufferedImage bi= new BufferedImage(iw, il, BufferedImage.TYPE_INT_ARGB);
        long before= System.currentTimeMillis();
        for (i= 0, x= -2f; i < iw; i++, x += dx) {
            for (j= 0, y= 2f; j < il; j++, y += dy) {
                n= FloatGrid.pnoise(x, y, 2.5f, 1.7f);
                bi.setRGB(i, j, Color.getHSBColor(0f, 0.f, n).getRGB());
                if (n > max)
                    max= n;
                if (n < min)
                    min= n;
            }
        }
        System.out.println("millis " + (System.currentTimeMillis() - before));
        System.out.println("max=" + max);
        System.out.println("min=" + min);
        JFrame f= new JFrame("periodic noise");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new JLabel(new ImageIcon(bi)));
        f.pack();
        f.setVisible(true);
    }
    */
    /*
     * cellnoise
     */

    public void cellnoise(FloatGrid x) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.cellnoise1d(x.data[i]);
    }

    public void cellnoise(FloatGrid x, FloatGrid y) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.cellnoise2d(x.data[i], y.data[i]);
    }

    public void cellnoise(FloatGrid x, FloatGrid y, FloatGrid z) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.cellnoise3d(x.data[i], y.data[i], z.data[i]);
    }

    public void cellnoise(FloatGrid x, FloatGrid y, FloatGrid z, float w) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.cellnoise4d
                (x.data[i], y.data[i], z.data[i], w);
    }

    public void cellnoise(Tuple3fGrid t) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.cellnoise3d
                (t.data[i].x, t.data[i].y, t.data[i].z);
    }

    public void cellnoise(Tuple3fGrid t, float w) {
        for (int i = 0; i < size; i++)
            data[i] = PerlinNoise.cellnoise4d
                (t.data[i].x, t.data[i].y, t.data[i].z, w);
    }

    /*
     * xcomp
     */

    public void xcomp(Tuple3fGrid t) {
        for (int i = 0; i < size; i++)
            data[i] = t.data[i].x;
    }

    public void xcomp(Tuple3fGrid t, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = t.data[i].x;
    }

    /*
     * ycomp
     */

    public void ycomp(Tuple3fGrid t) {
        for (int i = 0; i < size; i++)
            data[i] = t.data[i].y;
    }

    public void ycomp(Tuple3fGrid t, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = t.data[i].y;
    }

    /*
     * zcomp
     */

    public void zcomp(Tuple3fGrid t) {
        for (int i = 0; i < size; i++)
            data[i] = t.data[i].z;
    }

    public void zcomp(Tuple3fGrid t, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = t.data[i].z;
    }

    /*
     * length
     */

    public void length(Vector3fGrid v) {
        Vector3f[] vdata = (Vector3f[]) v.data;
        for (int i = 0; i < size; i++)
            data[i] = vdata[i].length();
    }

    public void length(Vector3fGrid v, BooleanGrid cond) {
        Vector3f[] vdata = (Vector3f[]) v.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = vdata[i].length();
    }

    /*
     * distance
     */

    public void distance(Point3fGrid p1, Point3f p2) {
        Point3f[] p1data = (Point3f[]) p1.data;
        for (int i = 0; i < size; i++)
            data[i] = p1data[i].distance(p2);
    }

    public void distance(Point3fGrid p1, Point3f p2, BooleanGrid cond) {
        Point3f[] p1data = (Point3f[]) p1.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = p1data[i].distance(p2);
    }

    public void distance(Point3fGrid p1, Point3fGrid p2) {
        Point3f[] p1data = (Point3f[]) p1.data;
        Point3f[] p2data = (Point3f[]) p2.data;
        for (int i = 0; i < size; i++)
            data[i] = p1data[i].distance(p2data[i]);
    }

    public void distance(Point3fGrid p1, Point3fGrid p2, BooleanGrid cond) {
        Point3f[] p1data = (Point3f[]) p1.data;
        Point3f[] p2data = (Point3f[]) p2.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = p1data[i].distance(p2data[i]);
    }

    /*
     * dot
     */

    public void dot(Vector3fGrid p1, Vector3f p2) {
        Vector3f[] p1data = (Vector3f[]) p1.data;
        for (int i = 0; i < size; i++)
            data[i] = p1data[i].dot(p2);
    }

    public void dot(Vector3fGrid p1, Vector3f p2, BooleanGrid cond) {
        Vector3f[] p1data = (Vector3f[]) p1.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = p1data[i].dot(p2);
    }

    public void dot(Vector3fGrid p1, Vector3fGrid p2) {
        Vector3f[] p1data = (Vector3f[]) p1.data;
        Vector3f[] p2data = (Vector3f[]) p2.data;
        for (int i = 0; i < size; i++)
            data[i] = p1data[i].dot(p2data[i]);
    }

    public void dot(Vector3fGrid p1, Vector3fGrid p2, BooleanGrid cond) {
        Vector3f[] p1data = (Vector3f[]) p1.data;
        Vector3f[] p2data = (Vector3f[]) p2.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = p1data[i].dot(p2data[i]);
    }

    /*
     * ptlined
     */

    // TODO implement ptlined

    /*
     * area
     */

    public void area(Point3fGrid p) {
        Point3f[] pdata = (Point3f[]) p.data;
        Vector3f uv = new Vector3f();
        Vector3f vv = new Vector3f();
        Vector3f ucrossv = new Vector3f();
        for (int i = 0; i < size - uSize; i += uSize) {
            for (int j = 0; j < uSize - 1; j++) {
                uv.sub(pdata[i + j + 1], pdata[i + j]);
                vv.sub(pdata[i + j + uSize], pdata[i + j]);
                ucrossv.cross(uv, vv);
                data[i + j] = ucrossv.length();
            }
            data[i + uSize - 1] = data[i + uSize - 2];
        }
        for (int i = size - uSize; i < size; i++)
            data[i] = data[i - uSize];
    }

    public void area(Point3fGrid p, BooleanGrid cond) {
        Point3f[] pdata = (Point3f[]) p.data;
        Vector3f uv = new Vector3f();
        Vector3f vv = new Vector3f();
        Vector3f ucrossv = new Vector3f();
        for (int i = 0; i < size - uSize; i += uSize) {
            for (int j = 0; j < uSize - 1; j++)
                if (cond.data[i + j]) {
                    uv.sub(pdata[i + j + 1], pdata[i + j]);
                    vv.sub(pdata[i + j + uSize], pdata[i + j]);
                    ucrossv.cross(uv, vv);
                    data[i + j] = ucrossv.length();
                }
            if (cond.data[i + uSize - 1]) {
                uv.sub(pdata[i + uSize - 1], pdata[i + uSize - 2]);
                vv.sub(pdata[i + uSize - 2 + uSize], pdata[i + uSize - 2]);
                ucrossv.cross(uv, vv);
                data[i + uSize - 1] = ucrossv.length();
            }
        }
        for (int i = size - uSize; i < size - 1; i++)
            if (cond.data[i]) {
                uv.sub(pdata[i - uSize + 1], pdata[i - uSize]);
                vv.sub(pdata[i], pdata[i - uSize]);
                ucrossv.cross(uv, vv);
                data[i] = ucrossv.length();
            }
        if (cond.data[size - 1]) {
            uv.sub(pdata[size - uSize - 1], pdata[size - uSize - 2]);
            vv.sub(pdata[size - 2], pdata[size - uSize - 2]);
            ucrossv.cross(uv, vv);
            data[size - 1] = ucrossv.length();
        }
    }

    /*
     * depth
     */

    public void depth(Point3fGrid p, float near, float far) {
        Point3f[] pdata = (Point3f[]) p.data;
        for (int i = 0; i < size; i++)
            data[i] = Calc.depth(pdata[i], near, far);
    }

    public void depth(Point3fGrid p, float near, float far, BooleanGrid cond) {
        Point3f[] pdata = (Point3f[]) p.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = Calc.depth(pdata[i], near, far);
    }

    /*
     * texture
     */

    public void texture(String textureName, FloatGrid s, FloatGrid t, 
                        float blur, int band) {
        MipMap texture = MipMap.getMipMap(textureName);
        for (int v = 0; v < vSize; v++)
            for (int u = 0; u < uSize; u++) {
                int u1 = u + 1;
                if (u1 == uSize)
                    u1 = u - 1;
                int v1 = v + 1;
                if (v1 == vSize)
                    v1 = v - 1;
                float area =
                    ((s.get(u, v) + s.get(u1, v)) * 
                     (t.get(u, v) - t.get(u1, v))
                        + (s.get(u1, v) + s.get(u1, v1)) * 
                     (t.get(u1, v) - t.get(u1, v1))
                        + (s.get(u1, v1) + s.get(u, v1)) * 
                     (t.get(u1, v1) - t.get(u, v1))
                        + (s.get(u, v1) + s.get(u, v)) * 
                     (t.get(u, v1) - t.get(u, v)))
                        / 2f;
                area = Math.abs(area) + blur;
                float avgS = (s.get(u, v) + s.get(u1, v) + 
                              s.get(u1, v1) + s.get(u, v1)) / 4f;
                float avgT = (t.get(u, v) + t.get(u1, v) + 
                              t.get(u1, v1) + t.get(u, v1)) / 4f;
                set(u, v, getFloat(texture, avgS, avgT, area, band));
            }
    }

    /*
     * shadow
     */

    public void shadow(
        String shadowName,
        Point3fGrid p,
        float bias,
        float samples,
        float blur) {
        int isamples = (int) samples;
        float oneOverSamples = 1f / samples;
        float halfBlur = blur * .5f;
        ShadowMap shadowMap = ShadowMap.getShadowMap(shadowName);
        pg1.transform(p, shadowMap.getWorldToRaster());
        for (int v = 0; v < vSize; v++)
            for (int u = 0; u < uSize; u++) {
                int u1 = u + 1;
                if (u1 == uSize)
                    u1 = u - 1;
                int v1 = v + 1;
                if (v1 == vSize)
                    v1 = v - 1;
                pg1.get(u, v, p1);
                pg1.get(u1, v1, p2);
                set(
                    u,
                    v,
                    shadowMap.get
                    (p1, p2, bias, isamples, oneOverSamples, blur, halfBlur));
            }
    }

    /*
     * simulPow
     */

    public void simulPow(FloatGrid f1, float n) {
        for (int i = 0; i < size; i++) {
            float t = f1.data[i];
            data[i] = t / (n - n * t + t);
        }
    }

    /*
     * toString
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int v = 0; v < vSize; v++) {
            for (int u = 0; u < uSize; u++)
                sb.append(get(u, v)).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }

}
