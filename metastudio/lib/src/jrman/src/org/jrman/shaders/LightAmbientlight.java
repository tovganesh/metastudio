/*
 LightAmbientlight.java
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
import javax.vecmath.Vector3f;

import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.parameters.UniformScalarTuple3f;
import org.jrman.render.ShaderVariables;

public class LightAmbientlight extends LightShader {

    private static Vector3f ZERO = new Vector3f();

    private static Color3f c1 = new Color3f();

    protected void initDefaults() {
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("intensity", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(new Declaration("lightcolor", "uniform color"), 1f, 1f, 1f));
    }

    public boolean isAmbient() {
        return true;
    }

    public boolean shade(ShaderVariables sv, Point3fGrid P, Vector3fGrid N, float angle) {
        super.shade(sv, P, N, angle);
        UniformScalarFloat paramIntensity = (UniformScalarFloat) getParameter(sv, "intensity");
        float intensity = paramIntensity.getValue();
        UniformScalarTuple3f paramLightcolor = (UniformScalarTuple3f) getParameter(sv, "lightcolor");
        paramLightcolor.getValue(c1);
        c1.x *= intensity;
        c1.y *= intensity;
        c1.z *= intensity;
        sv.Cl.set(c1);
        sv.L.set(ZERO);
        return true;
    }

}
