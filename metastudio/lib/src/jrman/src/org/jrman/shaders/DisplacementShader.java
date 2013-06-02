/*
 DisplacementShader.java
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

import org.jrman.attributes.Attributes;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.ParameterList;
import org.jrman.render.ShaderVariables;


public class DisplacementShader extends Shader {
    
    private static Vector3fGrid _dPdu = new Vector3fGrid();
    
    private static Vector3fGrid _dPdv = new Vector3fGrid();
    
    public static DisplacementShader createShader(
        String name,
        ParameterList parameters,
        Attributes attributes) {
        DisplacementShader result;
        String className =
            "Displacement" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        try {
            result = (DisplacementShader) Class.forName(className).newInstance();
        } catch (Exception e) {
            try {
                result =
                    (DisplacementShader) Class
                        .forName("org.jrman.shaders." + className)
                        .newInstance();
            } catch (Exception e2) {
                throw new IllegalArgumentException("Unknown displacement shader: " + name);
            }
        }
        result.init(name, parameters, attributes);
        return result;
    }
    
    protected void calculatenormal(ShaderVariables sv, Point3fGrid P,
                                   Vector3fGrid normal) {
        _dPdu.Du(P, sv.du);
        _dPdv.Dv(P, sv.dv);
        normal.cross(_dPdu, _dPdv);
    }
    
    public void shade(ShaderVariables sv) {
    }

}
