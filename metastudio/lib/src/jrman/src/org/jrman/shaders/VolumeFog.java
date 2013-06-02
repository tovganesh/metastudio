/*
 VolumeDepthcue.java
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

import org.jrman.grid.FloatGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.parameters.UniformScalarTuple3f;
import org.jrman.render.ShaderVariables;

public class VolumeFog extends VolumeShader {

    private final static Color3f OPAQUE = new Color3f(1f, 1f, 1f);

    private static FloatGrid fg1 = new FloatGrid();

    private static Color3f background = new Color3f();

    protected void initDefaults() {
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("distance", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(
                new Declaration("background", "uniform color"),
                0f,
                0f,
                0f));
    }

    public void shade(ShaderVariables sv, float near, float far) {
        super.shade(sv, near, far);
        UniformScalarFloat paramDistance = (UniformScalarFloat) getParameter(sv, "distance");
        final float distance = paramDistance.getValue();
        UniformScalarTuple3f paramBackground =
            (UniformScalarTuple3f) getParameter(sv, "background");
        paramBackground.getValue(background);
        fg1.length(sv.I);
        fg1.negate(fg1);
        fg1.div(fg1, distance);
        fg1.exp(fg1);
        fg1.sub(1f, fg1);
        sv.Ci.mix(sv.Ci, background, fg1);
        sv.Oi.mix(sv.Oi, OPAQUE, fg1);
    }

}
