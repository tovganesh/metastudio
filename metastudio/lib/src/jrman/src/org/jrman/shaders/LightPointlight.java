/*
 LightPointlight.java
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

import org.jrman.attributes.Space;
import org.jrman.geom.Transform;
import org.jrman.grid.Color3fGrid;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.parameters.UniformScalarTuple3f;
import org.jrman.parser.Global;
import org.jrman.render.ShaderVariables;

public class LightPointlight extends LightShader {
    
    private static Point3f from = new Point3f();

    static FloatGrid fg1 = new FloatGrid();

    static Color3fGrid cg1 = new Color3fGrid();
    
    static Color3f lightcolor = new Color3f();

    private PointLightStatement statement = new PointLightStatement();

    private static class PointLightStatement extends Statement {
        
        private float intensity;
        
        public void setIntensity(float intensity) {
            this.intensity = intensity;
        }
        
        public void execute(ShaderVariables sv) {
            fg1.dot(sv.L, sv.L);
            fg1.div(intensity, fg1);
            cg1.set(fg1);
            sv.Cl.set(lightcolor, tmpCond1);
            sv.Cl.mul(sv.Cl, cg1, tmpCond1);
        }
        
    }

    protected void initDefaults() {
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("intensity", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(new Declaration("lightcolor", "uniform color"), 1f, 1f, 1f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(new Declaration("from", "uniform point"), 0f, 0f, 0f));
    }

    public boolean shade(
        ShaderVariables sv,
        Point3fGrid P,
        Vector3fGrid N,
        float angle) {
        super.shade(sv, P, N, angle);
        UniformScalarFloat paramIntensity =
            (UniformScalarFloat) getParameter(sv, "intensity");
        float intensity = paramIntensity.getValue();
        UniformScalarTuple3f paramLightcolor =
            (UniformScalarTuple3f) getParameter(sv, "lightcolor");
        paramLightcolor.getValue(lightcolor);
        Transform shaderTransform = attributes.getTransform();
        if (attributes.getSpace() == Space.WORLD)
            shaderTransform = Global.getTransform("camera").concat(shaderTransform);
        UniformScalarTuple3f paramFrom = (UniformScalarTuple3f) getParameter(sv, "from");
        paramFrom.getValue(from);
        shaderTransform.transformPoint(from, from);
        statement.setIntensity(intensity);
        return illuminate(sv, P, N, angle, from, null, 0f, statement);
    }
}
