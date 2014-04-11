/*
 LightShader.java
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

package org.jrman.shaders;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jrman.attributes.Attributes;
import org.jrman.grid.BooleanGrid;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.ParameterList;
import org.jrman.render.ShaderVariables;

public abstract class LightShader extends Shader {

    private final static Color3f BLACK = new Color3f();

    private final static Vector3f _v1 = new Vector3f();

    protected static FloatGrid _fg1 = new FloatGrid();

    protected static Vector3fGrid _vg1 = new Vector3fGrid();

    protected static BooleanGrid tmpCond1 = new BooleanGrid();

    protected static BooleanGrid tmpCond2 = new BooleanGrid();

    protected abstract static class Statement {
        public abstract void execute(ShaderVariables sv);
    }

    public static LightShader createShader(
        String name,
        ParameterList parameters,
        Attributes attributes) {
        LightShader result;
        String className =
            "Light" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        try {
            result = (LightShader) Class.forName(className).newInstance();
        } catch (Exception e) {
            try {
                result =
                    (LightShader) Class
                        .forName("org.jrman.shaders." + className)
                        .newInstance();
            } catch (Exception e2) {
                throw new IllegalArgumentException("Unknown light shader: " + name);
            }
        }
        result.init(name, parameters, attributes);
        return result;
    }

    protected boolean solar(
        ShaderVariables sv,
        Point3fGrid P,
        Vector3fGrid N,
        float surfaceAngle,
        Vector3f D,
        float lightAngle,
        Statement statement) {
        if (P != null)
            sv.Ps.set(P);
        else
            sv.Ps.set(sv.P);
        if (N != null) {
            _v1.negate(D);
            _fg1.dot(N, _v1);
            tmpCond1.greaterOrEqual(_fg1, (float) Math.cos(surfaceAngle + lightAngle));
            if (tmpCond1.allFalse())
                return false;
        } else
            tmpCond1.set(true);
        statement.execute(sv);
        sv.L.set(D);
        sv.L.negate(sv.L);
        return true;
    }

    protected boolean illuminate(
        ShaderVariables sv,
        Point3fGrid P,
        Vector3fGrid N,
        float surfaceAngle,
        Point3f position,
        Vector3f axis,
        float lightAngle,
        Statement statement) {
        if (P != null)
            sv.Ps.set(P);
        else
            sv.Ps.set(sv.P);
        sv.L.sub(sv.Ps, position);
        _vg1.normalize(sv.L);
        if (axis != null) {
            _v1.normalize(axis);
            _fg1.dot(_vg1, _v1);
            tmpCond1.greaterOrEqual(_fg1, (float) Math.cos(lightAngle));
        } else
            tmpCond1.set(true);
        if (N != null) {
            _vg1.negate(_vg1);
            _fg1.dot(N, _vg1);
            tmpCond2.greaterOrEqual(_fg1, (float) Math.cos(surfaceAngle));
            tmpCond1.and(tmpCond1, tmpCond2);
        }
        if (tmpCond1.allFalse())
            return false;
        statement.execute(sv);
        sv.L.negate(sv.L);
        return true;
    }

    public boolean isAmbient() {
        return false;
    }

    public boolean shade(ShaderVariables sv, Point3fGrid P, Vector3fGrid N, float angle) {
        sv.Cl.set(BLACK);
        return false;
    }

}
