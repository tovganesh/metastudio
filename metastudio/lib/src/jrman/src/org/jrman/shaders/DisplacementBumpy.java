/*
 DisplacementBumpy.java
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
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.parameters.UniformScalarString;
import org.jrman.render.ShaderVariables;

public class DisplacementBumpy extends DisplacementShader {

    private static FloatGrid fg1 = new FloatGrid();

    private static Vector3fGrid Nn = new Vector3fGrid();

    private static Point3fGrid P2 = new Point3fGrid();

    protected void initDefaults() {
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("Km", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarString(new Declaration("texturename", "string"), ""));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("blur", "uniform float"),0f));
    }

    public void shade(ShaderVariables sv) {
        super.shade(sv);
        UniformScalarFloat paramKm = (UniformScalarFloat) getParameter(sv, "Km");
        final float Km = paramKm.getValue();
        UniformScalarString paramTexturename =
            (UniformScalarString) getParameter(sv, "texturename");
        final String texturename = paramTexturename.getValue();
        UniformScalarFloat paramBlur = (UniformScalarFloat) getParameter(sv, "blur");
        final float blur = paramBlur.getValue();
        if (!texturename.equals(""))
            fg1.texture(texturename, sv.s, sv.t, blur, 0);
        else
            fg1.set(0f);
        fg1.mul(fg1, Km);
        Nn.normalize(sv.N);
        P2.set(fg1);
        P2.mul(P2, Nn);
        sv.P.add(P2, sv.P);
        calculatenormal(sv, sv.P, sv.N);
    }

}
