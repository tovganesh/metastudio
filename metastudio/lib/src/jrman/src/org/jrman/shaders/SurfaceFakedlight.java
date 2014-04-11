/*
 SurfaceFakedlight.java
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

import javax.vecmath.Vector3f;

import org.jrman.grid.FloatGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.render.ShaderVariables;

public class SurfaceFakedlight extends SurfaceShader {
    
    private static Vector3fGrid tmpN = new Vector3fGrid();
    
    private static FloatGrid light = new FloatGrid();
    
    private Vector3f lightDirection = new Vector3f(1f, 1f, -1f);
    
    public void shade(ShaderVariables sv) {
        super.shade(sv);
        tmpN.faceforward(sv.N, sv.I);
        tmpN.normalize(tmpN);
        light.dot(tmpN, lightDirection);
        light.max(light, 0f);
        light.mul(light, .7f);
        light.add(light, .2f);
        light.clamp(light, 0f, 1f);
        sv.Oi.set(sv.Os);
        sv.Ci.mul(sv.Os, sv.Cs);
        tmpN.set(light);
        sv.Ci.mul(sv.Ci, tmpN);
    }

}
