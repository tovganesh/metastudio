/*
 SurfacePaintedplastic.java
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

import org.jrman.grid.Color3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.parameters.UniformScalarString;
import org.jrman.parameters.UniformScalarTuple3f;
import org.jrman.render.ShaderVariables;

public class SurfacePaintedplastic extends SurfaceShader {

    private static Vector3fGrid vg1 = new Vector3fGrid();

    private static Vector3fGrid vg2 = new Vector3fGrid();

    private static Color3fGrid cg1 = new Color3fGrid();

    private static Color3fGrid cg2 = new Color3fGrid();

    private static Color3fGrid cg3 = new Color3fGrid();

    private static Color3f specularcolor = new Color3f();

    protected void initDefaults() {
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("Ka", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("Kd", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("Ks", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("roughness", "uniform float"), .1f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(
                new Declaration("specularcolor", "uniform color"),
                1f,
                1f,
                1f));
        defaultParameters.addParameter(
            new UniformScalarString(new Declaration("texturename", "string"), ""));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("blur", "uniform float"), 0f));
    }

    public void shade(ShaderVariables sv) {
        super.shade(sv);
        UniformScalarFloat paramKa = (UniformScalarFloat) getParameter(sv, "Ka");
        final float Ka = paramKa.getValue();
        UniformScalarFloat paramKd = (UniformScalarFloat) getParameter(sv, "Kd");
        final float Kd = paramKd.getValue();
        UniformScalarFloat paramKs = (UniformScalarFloat) getParameter(sv, "Ks");
        final float Ks = paramKs.getValue();
        UniformScalarFloat paramRoughness = (UniformScalarFloat) getParameter(sv, "roughness");
        final float roughness = paramRoughness.getValue();
        UniformScalarTuple3f paramSpecularcolor =
            (UniformScalarTuple3f) getParameter(sv, "specularcolor");
        paramSpecularcolor.getValue(specularcolor);
        UniformScalarString paramTexturename =
            (UniformScalarString) getParameter(sv, "texturename");
        final String texturename = paramTexturename.getValue();
        UniformScalarFloat paramBlur = (UniformScalarFloat) getParameter(sv, "blur");
        final float blur = paramBlur.getValue(); 
        vg1.normalize(sv.N);
        vg1.faceforward(vg1, sv.I);
        vg2.normalize(sv.I);
        vg2.negate(vg2);
        sv.Oi.set(sv.Os);
        ambient(sv, cg1);
        cg3.set(Ka);
        cg1.mul(cg1, cg3);
        diffuse(sv, vg1, cg2);
        cg3.set(Kd);
        cg2.mul(cg2, cg3);
        cg1.add(cg1, cg2);
        cg1.mul(cg1, sv.Cs);
        if (!texturename.equals("")) {
            cg2.texture(texturename, sv.s, sv.t, blur);
            cg1.mul(cg1, cg2);
        }
        specular(sv, vg1, vg2, roughness, cg2);
        cg3.set(Ks);
        cg2.mul(cg2, cg3);
        cg3.set(specularcolor);
        cg2.mul(cg2, cg3);
        cg1.add(cg1, cg2);
        sv.Ci.mul(sv.Os, cg1);
    }

}
