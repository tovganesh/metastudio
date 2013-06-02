/*
 BooleanGrid.java
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

public class BooleanGrid extends Grid {

    public boolean[] data;

    public static BooleanGrid getInstance() {
        return new BooleanGrid();
    }

    public BooleanGrid() {
        data = new boolean[maxSize];
    }

    public boolean get(int i) {
        return data[i];
    }

    public void set(int i, boolean in) {
        data[i] = in;
    }

    public boolean get(int u, int v) {
        return get(v * uSize + u);
    }

    public void set(int u, int v, boolean in) {
        set(v * uSize + u, in);
    }

    /*
     * set
     */

    public void set(boolean b) {
        for (int i = 0; i < size; i++)
            data[i] = b;
    }

    public void set(boolean b, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = b;
    }
    
    public void set(BooleanGrid b) {
        for (int i = 0; i < size; i++)
            data[i] = b.data[i];
    }
    
    public void set(BooleanGrid b, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = b.data[i];
    }

    /*
     * allTrue
     */

    public boolean allTrue() {
        for (int i = 0; i < size; i++)
            if (!data[i])
                return false;
        return true;
    }

    public boolean allTrue(BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                if (!data[i])
                    return false;
        return true;
    }

    /*
     * allFalse
     */

    public boolean allFalse() {
        for (int i = 0; i < size; i++)
            if (data[i])
                return false;
        return true;
    }

    public boolean allFalse(BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                if (data[i])
                    return false;
        return true;
    }

    /*
     * and
     */

    public void and(BooleanGrid b1, boolean b2) {
        for (int i = 0; i < size; i++)
            data[i] = (b1.data[i] & b2);
    }

    public void and(BooleanGrid b1, boolean b2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (b1.data[i] & b2);
    }

    public void and(BooleanGrid b1, BooleanGrid b2) {
        for (int i = 0; i < size; i++)
            data[i] = (b1.data[i] & b2.data[i]);
    }

    public void and(BooleanGrid b1, BooleanGrid b2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (b1.data[i] & b2.data[i]);
    }

    /*
     * or
     */

    public void or(BooleanGrid b1, boolean b2) {
        for (int i = 0; i < size; i++)
            data[i] = (b1.data[i] | b2);
    }

    public void or(BooleanGrid b1, boolean b2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (b1.data[i] | b2);
    }

    public void or(BooleanGrid b1, BooleanGrid b2) {
        for (int i = 0; i < size; i++)
            data[i] = (b1.data[i] | b2.data[i]);
    }

    public void or(BooleanGrid b1, BooleanGrid b2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (b1.data[i] | b2.data[i]);
    }
    
    /*
     * not
     */
     
     public void not(BooleanGrid b1) {
         for (int i = 0; i < size; i++)
            data[i] = !(b1.data[i]);
     }
     
     public void not(BooleanGrid b1, BooleanGrid cond) {
         for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = !(b1.data[i]);
     }

    /*
     * equal
     */

    public void equal(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] == f2);
    }

    public void equal(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] == f2);
    }

    public void equal(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] == f2.data[i]);
    }

    public void equal(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] == f2.data[i]);
    }

    public void equal(Tuple3fGrid t1, Tuple3f t2) {
        for (int i = 0; i < size; i++)
            data[i] = t1.data[i].equals(t2);
    }

    public void equal(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = t1.data[i].equals(t2);
    }

    public void equal(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i = 0; i < size; i++)
            data[i] = t1.data[i].equals(t2.data[i]);
    }

    public void equal(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = t1.data[i].equals(t2.data[i]);
    }

    /*
     * notEqual
     */

    public void notEqual(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] != f2);
    }

    public void notEqual(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] != f2);
    }

    public void notEqual(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] != f2.data[i]);
    }

    public void notEqual(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] != f2.data[i]);
    }

    public void notEqual(Tuple3fGrid t1, Tuple3f t2) {
        for (int i = 0; i < size; i++)
            data[i] = !(t1.data[i].equals(t2));
    }

    public void notEqual(Tuple3fGrid t1, Tuple3f t2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = !(t1.data[i].equals(t2));
    }

    public void notEqual(Tuple3fGrid t1, Tuple3fGrid t2) {
        for (int i = 0; i < size; i++)
            data[i] = !(t1.data[i].equals(t2.data[i]));
    }

    public void notEqual(Tuple3fGrid t1, Tuple3fGrid t2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = !(t1.data[i].equals(t2.data[i]));
    }

    /*
     * less
     */

    public void less(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] < f2);
    }

    public void less(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] < f2);
    }

    public void less(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] < f2.data[i]);
    }

    public void less(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] < f2.data[i]);
    }

    /*
     * lessOrEqual
     */

    public void lessOrEqual(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] <= f2);
    }

    public void lessOrEqual(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] <= f2);
    }

    public void lessOrEqual(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] <= f2.data[i]);
    }

    public void lessOrEqual(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] <= f2.data[i]);
    }

    /*
     * greater
     */

    public void greater(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] > f2);
    }

    public void greater(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] > f2);
    }

    public void greater(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] > f2.data[i]);
    }

    public void greater(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] > f2.data[i]);
    }

    /*
     * greaterOrEqual
     */

    public void greaterOrEqual(FloatGrid f1, float f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] >= f2);
    }

    public void greaterOrEqual(FloatGrid f1, float f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] >= f2);
    }

    public void greaterOrEqual(FloatGrid f1, FloatGrid f2) {
        for (int i = 0; i < size; i++)
            data[i] = (f1.data[i] >= f2.data[i]);
    }

    public void greaterOrEqual(FloatGrid f1, FloatGrid f2, BooleanGrid cond) {
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                data[i] = (f1.data[i] >= f2.data[i]);
    }

}
