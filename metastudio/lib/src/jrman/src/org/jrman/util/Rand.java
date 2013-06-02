/*
 Rand.java
 Copyright (C) 1995,1996, 2003 Gerardo Horvilleur Martinez

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

public class Rand {

    final static long twoToThe31 = 0x80000000L;

    static int A[] = new int[56];

    static int ptr;
    
    static float[] uniforms = new float[7867];
    
    static int nextUniform;

    static {
        setSeed(31415926);
        for (int i = 0; i < uniforms.length; i++)
            uniforms[i] = newUniform();
    }

    final static int modDiff(int x, int y) {
        return (x - y) & 0x7fffffff;
    }

    final static int flipCycle() {
        int ii;
        int jj;

        for (ii = 1, jj = 32; jj <= 55; ii++, jj++)
            A[ii] = modDiff(A[ii], A[jj]);
        for (jj = 1; ii <= 55; ii++, jj++)
            A[ii] = modDiff(A[ii], A[jj]);
        ptr = 54;
        return A[55];
    }

    public final static int next() {
        if (A[ptr] >= 0)
            return A[ptr--];
        else
            return flipCycle();
    }

    public final static void setSeed(int seed) {
        int i;
        int prev = seed;
        int next = 1;
        A[0] = -1;
        ptr = 0;
        seed = prev = modDiff(prev, 0);
        A[55] = prev;
        for (i = 21; i != 0; i = (i + 21) % 55) {
            A[i] = next;
            next = modDiff(prev, next);
            if ((seed & 1) != 0)
                seed = 0x40000000 + (seed >> 1);
            else
                seed >>= 1;
            next = modDiff(next, seed);
            prev = A[i];
        }
        flipCycle();
        flipCycle();
        flipCycle();
        flipCycle();
        flipCycle();
    }

    public final static int uniform(int m) {
        long t = twoToThe31 - (twoToThe31 % m);
        int r;

        do {
            r = next();
        } while (t <= r);
        return (int) (r % m);
    }

    public final static float newUniform() {
        return (float) uniform(10000000) / 9999999f;
    }
    
    public final static float uniform() {
        float result = uniforms[nextUniform++];
        if (nextUniform == uniforms.length)
            nextUniform = 0;
        return result;
    }

}
