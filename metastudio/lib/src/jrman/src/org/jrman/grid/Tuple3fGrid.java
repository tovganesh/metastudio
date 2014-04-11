/*
 Tuple3fGrid.java
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

package org.jrman.grid;

import javax.vecmath.Tuple3f;

import org.jrman.util.Calc;
import org.jrman.util.PerlinNoise;

public class Tuple3fGrid extends Grid {

    public Tuple3f[] data;

    protected Tuple3fGrid() {
    }

    public void get(int i, Tuple3f out) {
        out.set(data[i]);
    }

    public void set(int i, Tuple3f in) {
        data[i].set(in);
    }

    public void get(int u, int v, Tuple3f out) {
        get(v * uSize + u, out);
    }

    public void set(int u, int v, Tuple3f in) {
        set(v * uSize + u, in);
    }

    /*
     * set
     */

    public void set(float in) {
        for (int i= 0; i < size; i++) {
            data[i].x = in;
            data[i].y = in;
            data[i].z = in;
        }
    }

    public void set(float in, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = in;
                data[i].y = in;
                data[i].z = in;
            }
    }

    public void set(FloatGrid in) {
        for (int i= 0; i < size; i++) {
            data[i].x = in.data[i];
            data[i].y = in.data[i];
            data[i].z = in.data[i];
        }
    }

    public void set(FloatGrid in, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = in.data[i];
                data[i].y = in.data[i];
                data[i].z = in.data[i];
            }
    }

    public void set(Tuple3f in) {
        for (int i= 0; i < size; i++) {
            data[i].set(in);
        }
    }

    public void set(Tuple3f in, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].set(in);
    }

    public void set(Tuple3fGrid t) {
        for (int i= 0; i < size; i++)
            data[i].set(t.data[i]);
    }

    public void set(Tuple3fGrid t, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].set(t.data[i]);
    }

    /*
     * negate
     */

    public void negate(Tuple3fGrid t) {
        for (int i= 0; i < size; i++)
            data[i].negate(t.data[i]);
    }

    public void negate(Tuple3fGrid t, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].negate(t.data[i]);
    }

    /*
     * add
     */

    public void add(Tuple3fGrid t1, Tuple3f t2) {
        for (int i= 0; i < size; i++)
            data[i].add(t1.data[i], t2);
    }

    public void add(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].add(t1.data[i], t2);
    }

    public void add(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++)
            data[i].add(t1.data[i], t2.data[i]);
    }

    public void add(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].add(t1.data[i], t2.data[i]);
    }

    /*
     * sub
     */

    public void sub(Tuple3fGrid t1, Tuple3f t2) {
        for (int i= 0; i < size; i++)
            data[i].sub(t1.data[i], t2);
    }

    public void sub(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].sub(t1.data[i], t2);
    }

    public void sub(Tuple3f t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++)
            data[i].sub(t1, t2.data[i]);
    }

    public void sub(Tuple3f t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].sub(t1, t2.data[i]);
    }

    public void sub(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++)
            data[i].sub(t1.data[i], t2.data[i]);
    }

    public void sub(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].sub(t1.data[i], t2.data[i]);
    }

    /*
     * mul
     */

    public void mul(Tuple3fGrid t1, float f) {
        for (int i= 0; i < size; i++) {
            data[i].x = t1.data[i].x * f;
            data[i].y = t1.data[i].y * f;
            data[i].z = t1.data[i].z * f;
        }
    }

    public void mul(Tuple3fGrid t1, Tuple3f t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = t1.data[i].x * t2.x;
            data[i].y = t1.data[i].y * t2.y;
            data[i].z = t1.data[i].z * t2.z;
        }
    }

    public void mul(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = t1.data[i].x * t2.x;
                data[i].y = t1.data[i].y * t2.y;
                data[i].z = t1.data[i].z * t2.z;
            }
    }

    public void mul(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = t1.data[i].x * t2.data[i].x;
            data[i].y = t1.data[i].y * t2.data[i].y;
            data[i].z = t1.data[i].z * t2.data[i].z;
        }
    }

    public void mul(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = t1.data[i].x * t2.data[i].x;
                data[i].y = t1.data[i].y * t2.data[i].y;
                data[i].z = t1.data[i].z * t2.data[i].z;
            }
    }

    /*
     * div
     */

    public void div(Tuple3fGrid t1, Tuple3f t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = t1.data[i].x / t2.x;
            data[i].y = t1.data[i].y / t2.y;
            data[i].z = t1.data[i].z / t2.z;
        }
    }

    public void div(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = t1.data[i].x / t2.x;
                data[i].y = t1.data[i].y / t2.y;
                data[i].z = t1.data[i].z / t2.z;
            }
    }

    public void div(Tuple3f t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = t1.x / t2.data[i].x;
            data[i].y = t1.y / t2.data[i].y;
            data[i].z = t1.z / t2.data[i].z;
        }
    }

    public void div(Tuple3f t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = t1.x / t2.data[i].x;
                data[i].y = t1.y / t2.data[i].y;
                data[i].z = t1.z / t2.data[i].z;
            }
    }

    public void div(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = t1.data[i].x / t2.data[i].x;
            data[i].y = t1.data[i].y / t2.data[i].y;
            data[i].z = t1.data[i].z / t2.data[i].z;
        }
    }

    public void div(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = t1.data[i].x / t2.data[i].x;
                data[i].y = t1.data[i].y / t2.data[i].y;
                data[i].z = t1.data[i].z / t2.data[i].z;
            }
    }

    /*
     * min
     */

    public void min(Tuple3fGrid t1, Tuple3f t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = Math.min(t1.data[i].x, t2.x);
            data[i].y = Math.min(t1.data[i].y, t2.y);
            data[i].z = Math.min(t1.data[i].z, t2.z);
        }
    }

    public void min(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Math.min(t1.data[i].x, t2.x);
                data[i].y = Math.min(t1.data[i].y, t2.y);
                data[i].z = Math.min(t1.data[i].z, t2.z);
            }
    }

    public void min(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = Math.min(t1.data[i].x, t2.data[i].x);
            data[i].y = Math.min(t1.data[i].y, t2.data[i].y);
            data[i].z = Math.min(t1.data[i].z, t2.data[i].z);
        }
    }

    public void min(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Math.min(t1.data[i].x, t2.data[i].x);
                data[i].y = Math.min(t1.data[i].y, t2.data[i].y);
                data[i].z = Math.min(t1.data[i].z, t2.data[i].z);
            }
    }

    /*
     * max
     */

    public void max(Tuple3fGrid t1, Tuple3f t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = Math.max(t1.data[i].x, t2.x);
            data[i].y = Math.max(t1.data[i].y, t2.y);
            data[i].z = Math.max(t1.data[i].z, t2.z);
        }
    }

    public void max(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Math.max(t1.data[i].x, t2.x);
                data[i].y = Math.max(t1.data[i].y, t2.y);
                data[i].z = Math.max(t1.data[i].z, t2.z);
            }
    }

    public void max(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i= 0; i < size; i++) {
            data[i].x = Math.max(t1.data[i].x, t2.data[i].x);
            data[i].y = Math.max(t1.data[i].y, t2.data[i].y);
            data[i].z = Math.max(t1.data[i].z, t2.data[i].z);
        }
    }

    public void max(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Math.max(t1.data[i].x, t2.data[i].x);
                data[i].y = Math.max(t1.data[i].y, t2.data[i].y);
                data[i].z = Math.max(t1.data[i].z, t2.data[i].z);
            }
    }

    /*
     * clamp
     */

    public void clamp(Tuple3fGrid t, Tuple3f min, Tuple3f max) {
        for (int i= 0; i < size; i++) {
            data[i].x = Calc.clamp(t.data[i].x, min.x, max.x);
            data[i].y = Calc.clamp(t.data[i].y, min.x, max.y);
            data[i].z = Calc.clamp(t.data[i].z, min.x, max.z);
        }
    }

    public void clamp(Tuple3fGrid t, Tuple3f min, Tuple3f max,
                      BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Calc.clamp(t.data[i].x, min.x, max.x);
                data[i].y = Calc.clamp(t.data[i].y, min.x, max.y);
                data[i].z = Calc.clamp(t.data[i].z, min.x, max.z);
            }
    }

    /*
     * mix
     */

    public void mix(Tuple3f t1, Tuple3f t2, FloatGrid alpha) {
        for (int i= 0; i < size; i++) {
            data[i].x = Calc.interpolate(t1.x, t2.x, alpha.data[i]);
            data[i].y = Calc.interpolate(t1.y, t2.y, alpha.data[i]);
            data[i].z = Calc.interpolate(t1.z, t2.z, alpha.data[i]);
        }
    }

    public void mix(Tuple3f t1, Tuple3f t2, FloatGrid alpha, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Calc.interpolate(t1.x, t2.x, alpha.data[i]);
                data[i].y = Calc.interpolate(t1.y, t2.y, alpha.data[i]);
                data[i].z = Calc.interpolate(t1.z, t2.z, alpha.data[i]);
            }
    }

    public void mix(Tuple3f t1, Tuple3fGrid t2, float alpha) {
        for (int i= 0; i < size; i++) {
            data[i].x = Calc.interpolate(t1.x, t2.data[i].x, alpha);
            data[i].y = Calc.interpolate(t1.y, t2.data[i].y, alpha);
            data[i].z = Calc.interpolate(t1.z, t2.data[i].z, alpha);
        }
    }

    public void mix(Tuple3f t1, Tuple3fGrid t2, float alpha, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Calc.interpolate(t1.x, t2.data[i].x, alpha);
                data[i].y = Calc.interpolate(t1.y, t2.data[i].y, alpha);
                data[i].z = Calc.interpolate(t1.z, t2.data[i].z, alpha);
            }
    }

    public void mix(Tuple3f t1, Tuple3fGrid t2, FloatGrid alpha) {
        for (int i= 0; i < size; i++) {
            data[i].x = Calc.interpolate(t1.x, t2.data[i].x, alpha.data[i]);
            data[i].y = Calc.interpolate(t1.y, t2.data[i].y, alpha.data[i]);
            data[i].z = Calc.interpolate(t1.z, t2.data[i].z, alpha.data[i]);
        }
    }

    public void mix(Tuple3f t1, Tuple3fGrid t2, FloatGrid alpha,
                    BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Calc.interpolate(t1.x, t2.data[i].x, alpha.data[i]);
                data[i].y = Calc.interpolate(t1.y, t2.data[i].y, alpha.data[i]);
                data[i].z = Calc.interpolate(t1.z, t2.data[i].z, alpha.data[i]);
            }
    }

    public void mix(Tuple3fGrid t1, Tuple3f t2, float alpha) {
        for (int i= 0; i < size; i++) {
            data[i].x = Calc.interpolate(t1.data[i].x, t2.x, alpha);
            data[i].y = Calc.interpolate(t1.data[i].y, t2.y, alpha);
            data[i].z = Calc.interpolate(t1.data[i].z, t2.z, alpha);
        }
    }

    public void mix(Tuple3fGrid t1, Tuple3f t2, float alpha, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Calc.interpolate(t1.data[i].x, t2.x, alpha);
                data[i].y = Calc.interpolate(t1.data[i].y, t2.y, alpha);
                data[i].z = Calc.interpolate(t1.data[i].z, t2.z, alpha);
            }
    }

    public void mix(Tuple3fGrid t1, Tuple3f t2, FloatGrid alpha) {
        for (int i= 0; i < size; i++) {
            data[i].x = Calc.interpolate(t1.data[i].x, t2.x, alpha.data[i]);
            data[i].y = Calc.interpolate(t1.data[i].y, t2.y, alpha.data[i]);
            data[i].z = Calc.interpolate(t1.data[i].z, t2.z, alpha.data[i]);
        }
    }

    public void mix(Tuple3fGrid t1, Tuple3f t2, FloatGrid alpha,
                    BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Calc.interpolate(t1.data[i].x, t2.x, alpha.data[i]);
                data[i].y = Calc.interpolate(t1.data[i].y, t2.y, alpha.data[i]);
                data[i].z = Calc.interpolate(t1.data[i].z, t2.z, alpha.data[i]);
            }
    }

    public void mix(Tuple3fGrid t1, Tuple3fGrid t2, float alpha) {
        for (int i= 0; i < size; i++) {
            data[i].x = Calc.interpolate(t1.data[i].x, t2.data[i].x, alpha);
            data[i].y = Calc.interpolate(t1.data[i].y, t2.data[i].y, alpha);
            data[i].z = Calc.interpolate(t1.data[i].z, t2.data[i].z, alpha);
        }
    }

    public void mix(Tuple3fGrid t1, Tuple3fGrid t2, float alpha,
                    BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x = Calc.interpolate(t1.data[i].x, t2.data[i].x, alpha);
                data[i].y = Calc.interpolate(t1.data[i].y, t2.data[i].y, alpha);
                data[i].z = Calc.interpolate(t1.data[i].z, t2.data[i].z, alpha);
            }
    }

    public void mix(Tuple3fGrid t1, Tuple3fGrid t2, FloatGrid alpha) {
        for (int i= 0; i < size; i++) {
            data[i].x =
                Calc.interpolate(t1.data[i].x, t2.data[i].x, alpha.data[i]);
            data[i].y =
                Calc.interpolate(t1.data[i].y, t2.data[i].y, alpha.data[i]);
            data[i].z =
                Calc.interpolate(t1.data[i].z, t2.data[i].z, alpha.data[i]);
        }
    }

    public void mix(Tuple3fGrid t1, Tuple3fGrid t2, FloatGrid alpha,
                    BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i]) {
                data[i].x =
                    Calc.interpolate(t1.data[i].x, t2.data[i].x, alpha.data[i]);
                data[i].y =
                    Calc.interpolate(t1.data[i].y, t2.data[i].y, alpha.data[i]);
                data[i].z =
                    Calc.interpolate(t1.data[i].z, t2.data[i].z, alpha.data[i]);
            }
    }

    /*
     * Du
     */

    public void Du(Tuple3fGrid t, FloatGrid du) {
        for (int i= 0; i < size; i += uSize) {
            for (int j= 0; j < uSize - 1; j++) {
                data[i + j].sub(t.data[i + j + 1], t.data[i + j]);
                data[i + j].scale(1f / du.data[i + j]);
            }
            data[i + uSize - 1].sub(t.data[i + uSize - 1],
                                    t.data[i + uSize - 2]);
            data[i + uSize - 1].scale(1f / du.data[i + uSize - 1]);
        }
    }

    public void Du(Tuple3fGrid t, FloatGrid du, BooleanGrid cond) {
        for (int i= 0; i < size; i += uSize) {
            for (int j= 0; j < uSize; j++)
                if (cond.data[i + j]) {
                    data[i + j].sub(t.data[i + j + 1], t.data[i + j]);
                    data[i + j].scale(1f / du.data[i + j]);
                }
            if (cond.data[i + uSize - 1]) {
                data[i + uSize - 1].sub(t.data[i + uSize - 1],
                                        t.data[i + uSize - 2]);
                data[i + uSize - 1].scale(1f / du.data[i + uSize - 1]);
            }
        }
    }

    /*
     * Dv
     */

    public void Dv(Tuple3fGrid t, FloatGrid dv) {
        for (int i= 0; i < size - uSize; i += uSize)
            for (int j= 0; j < uSize; j++) {
                data[i + j].sub(t.data[i + j + uSize], t.data[i + j]);
                data[i + j].scale(1f / dv.data[i + j]);
            }
        for (int i= size - uSize; i < size; i++) {
            data[i].sub(t.data[i], t.data[i - uSize]);
            data[i].scale(1f / dv.data[i]);
        }
    }

    public void Dv(Tuple3fGrid t, FloatGrid dv, BooleanGrid cond) {
        for (int i= 0; i < size - uSize; i += uSize)
            for (int j= 0; j < uSize; j++)
                if (cond.data[i + j]) {
                    data[i + j].sub(t.data[i + j + uSize], t.data[i + j]);
                    data[i + j].scale(1f / dv.data[i + j]);
                }
        for (int i= size - uSize; i < size; i++)
            if (cond.data[i]) {
                data[i].sub(t.data[i], t.data[i - uSize]);
                data[i].scale(1f / dv.data[i]);
            }
    }

    /*
     * random
     */

    public void random() {
        for (int i= 0; i < size; i++)
            data[i].set((float) Math.random(), (float) Math.random(),
                        (float) Math.random());
    }

    public void random(BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].set((float) Math.random(), (float) Math.random(),
                            (float)Math.random());
    }

    /*
     * noise
     */

    public void noise(FloatGrid x) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            data[i].set(0.5f * PerlinNoise.noise1d(fx) + 0.5f,
                        0.5f * PerlinNoise.noise1d(fx + fx) + 0.5f,
                        0.5f * PerlinNoise.noise1d(fx + fx + fx) + 0.5f);
        }
    }

    public void noise(FloatGrid x, FloatGrid y) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            data[i].set(0.5f * PerlinNoise.noise2d(fx, fy) + 0.5f,
                        0.5f * PerlinNoise.noise2d(fy, fx) + 0.5f,
                        0.5f * PerlinNoise.noise2d(fx + fy, fy) + 0.5f);
        }
    }

    public void noise(FloatGrid x, FloatGrid y, FloatGrid z) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            float fz = z.data[i];
            data[i].set(0.5f * PerlinNoise.noise3d(fx, fy, fz) + 0.5f,
                        0.5f * PerlinNoise.noise3d(fx, fz, fy) + 0.5f,
                        0.5f * PerlinNoise.noise3d(fz, fy, fx) + 0.5f);
        }
    }

    public void noise(FloatGrid x, FloatGrid y, FloatGrid z, float w) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            float fz = z.data[i];
            data[i].set(0.5f * PerlinNoise.noise4d(fx, fy, fz, w) + 0.5f,
                        0.5f * PerlinNoise.noise4d(fx, fz, fy, w) + 0.5f,
                        0.5f * PerlinNoise.noise4d(fz, fy, fx, w) + 0.5f);
        }
    }

    public void noise(Tuple3fGrid t) {
        for (int i= 0; i < size; i++) {
            Tuple3f tup= t.data[i];
            data[i].set(0.5f * PerlinNoise.noise3d(tup.x, tup.y, tup.z) + 0.5f,
                        0.5f * PerlinNoise.noise3d(tup.x, tup.z, tup.y) + 0.5f,
                        0.5f * PerlinNoise.noise3d(tup.z, tup.y, tup.x) + 0.5f);
        }
    }

    public void noise(Tuple3fGrid t, float w) {
        for (int i= 0; i < size; i++) {
            Tuple3f tup= t.data[i];
            data[i].set(0.5f * PerlinNoise.noise4d(tup.x, tup.y, tup.z, w)+0.5f,
                        0.5f * PerlinNoise.noise4d(tup.x, tup.z, tup.y, w)+0.5f,
                        0.5f * PerlinNoise.noise4d(tup.z, tup.y, tup.x, w)+0.5f);
        }
    }

    /*
     * snoise
     */

    public void snoise(FloatGrid x) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            data[i].set(PerlinNoise.noise1d(fx), PerlinNoise.noise1d(fx + fx),
                        PerlinNoise.noise1d(fx + fx + fx));
        }
    }

    public void snoise(FloatGrid x, FloatGrid y) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            data[i].set(0.5f * PerlinNoise.noise2d(fx, fy),
                        0.5f * PerlinNoise.noise2d(fy, fx),
                        0.5f * PerlinNoise.noise2d(fx + fy, fy));
        }
    }

    public void snoise(FloatGrid x, FloatGrid y, FloatGrid z) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            float fz = z.data[i];
            data[i].set(PerlinNoise.noise3d(fx, fy, fz),
                        PerlinNoise.noise3d(fx, fz, fy),
                        PerlinNoise.noise3d(fz, fy, fx));
        }
    }

    public void snoise(FloatGrid x, FloatGrid y, FloatGrid z, float w) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            float fz = z.data[i];
            data[i].set(PerlinNoise.noise4d(fx, fy, fz, w),
                        PerlinNoise.noise4d(fx, fz, fy, w),
                        PerlinNoise.noise4d(fz, fy, fx, w));
        }
    }

    public void snoise(Tuple3fGrid t) {
        for (int i= 0; i < size; i++) {
            Tuple3f tup= t.data[i];
            data[i].set(PerlinNoise.noise3d(tup.x, tup.y, tup.z),
                        PerlinNoise.noise3d(tup.x, tup.z, tup.y),
                        PerlinNoise.noise3d(tup.z, tup.y, tup.x));
        }
    }

    public void snoise(Tuple3fGrid t, float w) {
        for (int i= 0; i < size; i++) {
            Tuple3f tup= t.data[i];
            data[i].set(PerlinNoise.noise4d(tup.x, tup.y, tup.z, w),
                        PerlinNoise.noise4d(tup.x, tup.z, tup.y, w),
                        PerlinNoise.noise4d(tup.z, tup.y, tup.x, w));
        }
    }

    /*
     * pnoise
     */
    private float pnoiseInternal1(float x, float px) {
        // this method reuses the argument variables (they are passed by value)
        // to decrease allocation of additional temporary varables 
        x = x > 0 ? x % px : (-x) % px;
        float pmx = px - x;
        float ret= PerlinNoise.noise1d(x) * pmx + PerlinNoise.noise1d(-pmx) * x;
        ret /= px;
        return .5f + ret * .5f;
    }

    public void pnoise(FloatGrid x, float px) {
        assert px > 0f;
        for (int i= 0; i < size; i++) {
            float nx = x.data[i];
            data[i].set(pnoiseInternal1(nx, px), pnoiseInternal1(nx + nx, px),
                        pnoiseInternal1(nx + nx + nx, px));
        }
    }

    private float pnoiseInternal2(float x, float y, float px, float py) {
        // this method reuses the argument variables (they are passed by value)
        // to decrease allocation of additional temporary varables 
        x = x > 0 ? x % px : (-x) % px;
        y = y > 0 ? y % py : (-y) % py;
        float pxy = px * py;
        px = px - x;
        py = py - y;
        float ret =
            (PerlinNoise.noise2d(x, y) * px +
             PerlinNoise.noise2d(-px, y) * x) * py;
        ret += (PerlinNoise.noise2d(x, -py) * px +
                PerlinNoise.noise2d(-px, -py) * x) * y;
        ret /= pxy;
        return .5f + ret * .5f;
    }

    public void pnoise(FloatGrid x, FloatGrid y, float px, float py) {
        assert px > 0f && py > 0f;
        for (int i= 0; i < size; i++) {
            float nx = x.data[i];
            float ny = y.data[i];
            data[i].set(pnoiseInternal2(nx, ny, px, py),
                        pnoiseInternal2(ny, nx, px, py),
                        pnoiseInternal2(nx + ny, ny, px, py));
        }
    }

    private float pnoiseInternal3(float x, float y, float z, float px, float py, float pz) {
        // this method reuses the argument variables (they are passed by value)
        // to decrease allocation of additional temporary varables 
        x = x > 0 ? x % px : (-x) % px;
        y = y > 0 ? y % py : (-y) % py;
        z = z > 0 ? z % pz : (-z) % pz;
        float pxyz = px * py * pz;
        px = px - x;
        py = py - y;
        pz = pz - z;
        float ret=
            ((PerlinNoise.noise3d(x, y, z) * px +
              PerlinNoise.noise3d(-px, y, z) * x) * py +
             (PerlinNoise.noise3d(x, -py, z) * px +
              PerlinNoise.noise3d(-px, -py, z) * x) * y) * pz;
        ret +=
            ((PerlinNoise.noise3d(x, y, -pz) * px +
              PerlinNoise.noise3d(-px, y, -pz) * x) * py +
             (PerlinNoise.noise3d(x, -py, -pz) * px +
              PerlinNoise.noise3d(-px, -py, -pz) * x) * y) * z;
        ret /= pxyz;
        return .5f + ret * .5f;
    }

    public void pnoise(FloatGrid x, FloatGrid y, FloatGrid z,
                       float px, float py, float pz) {
        assert px > 0f && py > 0f && pz > 0f;
        for (int i= 0; i < size; i++) {
            float nx = x.data[i];
            float ny = y.data[i];
            float nz = z.data[i];
            data[i].set(pnoiseInternal3(nx, ny, nz, px, py, pz),
                        pnoiseInternal3(nx, nz, ny, px, py, pz),
                        pnoiseInternal3(nz, ny, nx, px, py, pz));
        }
    }

    public void pnoise(Tuple3fGrid t, float px, float py, float pz) {
        assert px > 0f && py > 0f && pz > 0f;
        for (int i= 0; i < size; i++) {
            Tuple3f tup= t.data[i];
            data[i].set(pnoiseInternal3(tup.x, tup.y, tup.z, px, py, pz),
                        pnoiseInternal3(tup.x, tup.z, tup.y, px, py, pz),
                        pnoiseInternal3(tup.z, tup.y, tup.x, px, py, pz));
        }
    }
    // TODO implement Tuple3f pnoise

    /*
     * cellnoise
     */

    public void cellnoise(FloatGrid x) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            data[i].set(0.5f * PerlinNoise.cellnoise1d(fx) + 0.5f,
                        0.5f * PerlinNoise.cellnoise1d(fx + fx) + 0.5f,
                        0.5f * PerlinNoise.cellnoise1d(fx + fx + fx) + 0.5f);
        }
    }

    public void cellnoise(FloatGrid x, FloatGrid y) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            data[i].set(0.5f * PerlinNoise.cellnoise2d(fx, fy) + 0.5f,
                        0.5f * PerlinNoise.cellnoise2d(fy, fx) + 0.5f,
                        0.5f * PerlinNoise.cellnoise2d(fx + fy, fy) + 0.5f);
        }
    }

    public void cellnoise(FloatGrid x, FloatGrid y, FloatGrid z) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            float fz = z.data[i];
            data[i].set(0.5f * PerlinNoise.cellnoise3d(fx, fy, fz) + 0.5f,
                        0.5f * PerlinNoise.cellnoise3d(fx, fz, fy) + 0.5f,
                        0.5f * PerlinNoise.cellnoise3d(fz, fy, fx) + 0.5f);
        }
    }

    public void cellnoise(FloatGrid x, FloatGrid y, FloatGrid z, float w) {
        for (int i= 0; i < size; i++) {
            float fx = x.data[i];
            float fy = y.data[i];
            float fz = z.data[i];
            data[i].set(0.5f * PerlinNoise.cellnoise4d(fx, fy, fz, w) + 0.5f,
                        0.5f * PerlinNoise.cellnoise4d(fx, fz, fy, w) + 0.5f,
                        0.5f * PerlinNoise.cellnoise4d(fz, fy, fx, w) + 0.5f);
        }
    }

    public void cellnoise(Tuple3fGrid t) {
        for (int i= 0; i < size; i++) {
            Tuple3f tup= t.data[i];
            data[i].set(0.5f * PerlinNoise.cellnoise3d(tup.x,tup.y,tup.z)+0.5f,
                        0.5f * PerlinNoise.cellnoise3d(tup.x,tup.z,tup.y)+0.5f,
                        0.5f * PerlinNoise.cellnoise3d(tup.z,tup.y,tup.x)+0.5f);
        }
    }

    public void cellnoise(Tuple3fGrid t, float w) {
        for (int i= 0; i < size; i++) {
            Tuple3f tup= t.data[i];
            data[i].set(0.5f*PerlinNoise.cellnoise4d(tup.x,tup.y,tup.z, w)+0.5f,
                        0.5f*PerlinNoise.cellnoise4d(tup.x,tup.z,tup.y, w)+0.5f,
                        0.5f*PerlinNoise.cellnoise4d(tup.z,tup.y,tup.x, w)+0.5f);
        }
    }

    /*
     * setxcomp
     */

    public void setxcomp(float x) {
        for (int i= 0; i < size; i++)
            data[i].x = x;
    }

    public void setxcomp(float x, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].x = x;
    }

    public void setxcomp(FloatGrid x) {
        for (int i= 0; i < size; i++)
            data[i].x = x.data[i];
    }

    public void setxcomp(FloatGrid x, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].x = x.data[i];
    }

    /*
    * setycomp
    */

    public void setycomp(float y) {
        for (int i= 0; i < size; i++)
            data[i].y = y;
    }

    public void setycomp(float y, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].y = y;
    }

    public void setycomp(FloatGrid y) {
        for (int i= 0; i < size; i++)
            data[i].y = y.data[i];
    }

    public void setycomp(FloatGrid y, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].y = y.data[i];
    }

    /*
     * setzcomp
     */

    public void setzcomp(float z) {
        for (int i= 0; i < size; i++)
            data[i].z = z;
    }

    public void setzcomp(float z, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].z = z;
    }

    public void setzcomp(FloatGrid z) {
        for (int i= 0; i < size; i++)
            data[i].z = z.data[i];
    }

    public void setzcomp(FloatGrid z, BooleanGrid cond) {
        for (int i= 0; i < size; i++)
            if (cond.data[i])
                data[i].z = z.data[i];
    }

    /*
     * toString
     */
    public String toString() {
        Tuple3f out = new javax.vecmath.Point3f();
        StringBuffer sb = new StringBuffer();
        for (int v = 0; v < vSize; v++) {
            for (int u = 0; u < uSize; u++) {
                get(u, v, out);
                sb.append(out).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
