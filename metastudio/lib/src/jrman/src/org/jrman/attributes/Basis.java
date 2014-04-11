/*
 Basis.java
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

package org.jrman.attributes;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Matrix4f;

public class Basis {

    private static final Matrix4f bezierInverse;
    
    static {
        bezierInverse = 
            new Matrix4f(-1f, 3f, -3f, 1, 3f, -6f, 3f, 0f, -3f, 3f, 0f, 0f, 1f, 0f, 0f, 0f);
        bezierInverse.invert();
    }

    public final static Basis BEZIER =
        new Basis(
            new Matrix4f(-1f, 3f, -3f, 1, 3f, -6f, 3f, 0f, -3f, 3f, 0f, 0f, 1f, 0f, 0f, 0f));

    public final static Basis B_SPLINE =
        new Basis(
            new Matrix4f(
                -1f / 6f,
                3f / 6f,
                -3f / 6f,
                1f / 6f,
                3f / 6f,
                -6f / 6f,
                0f / 6f,
                4f / 6f,
                -3f / 6f,
                3f / 6f,
                3f / 6f,
                1f / 6f,
                1f / 6f,
                0f / 6f,
                0f / 6f,
                0f / 6f));

    public final static Basis CATMULL_ROM =
        new Basis(
            new Matrix4f(
                -1f / 2f,
                2f / 2f,
                -1f / 2f,
                0f / 2f,
                3f / 2f,
                -5f / 2f,
                0f / 2f,
                2f / 2f,
                -3f / 2f,
                4f / 2f,
                1f / 2f,
                0f / 2f,
                1f / 2f,
                -1f / 2f,
                0f / 2f,
                0f / 2f));

    public final static Basis HERMITE =
        new Basis(
            new Matrix4f(2f, -3f, 0f, 1f, 1f, -2f, 1f, 0f, -2f, 3f, 0f, 0f, 1f, -1f, 0f, 0f));

    public final static Basis POWER =
        new Basis(
            new Matrix4f(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f));

    private static final Map<String, Basis> map = new HashMap<String, Basis>();
    
    static {
        map.put("bezier", BEZIER);
        map.put("b-spline", B_SPLINE);
        map.put("catmull-rom", CATMULL_ROM);
        map.put("hermite", HERMITE);
        map.put("power", POWER);
    }

    private Matrix4f toBezier;

    public Basis(Matrix4f matrix) {
        toBezier = new Matrix4f();
        toBezier.mul(matrix, bezierInverse);
        toBezier.transpose();
    }

    public static Basis getNamed(String name) {
        Basis result = (Basis) map.get(name);
        if (result == null)
            throw new IllegalArgumentException("No such basis: " + name);
        return result;
    }

    public Matrix4f getToBezier() {
        //return new Matrix4f(toBezier);
        return toBezier;
    }

}
