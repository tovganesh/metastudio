/*
  Points.java
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

package org.jrman.primitive;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.Bounds3f;
import org.jrman.grid.Grid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.render.ShaderVariables;

public class Point extends Primitive {
    
    private static Point3f tmp = new Point3f();

    private static Point3f camera = new Point3f();

    private static Vector3f v = new Vector3f();
    
    private static Color3f color = new Color3f();

    private float width;

    private float x;

    private float y;

    private float z;

    public Point(
        float width,
        float x,
        float y,
        float z,
        ParameterList parameters,
        Attributes attributes) {
        super(parameters, attributes);
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
    }

    public BoundingVolume getBoundingVolume() {
        Bounds3f result =
            new Bounds3f(
                x - width / 2f,
                x + width / 2f,
                y - width / 2f,
                y + width / 2f,
                z - width / 2f,
                z + width / 2f);
        return result;
    }

    public Primitive[] split() {
        throw new RuntimeException("Tried to split a point!");
    }

    public void dice(ShaderVariables sv) {
        super.dice(sv);
        dice_PandNg(sv);
        sv.N.set(sv.Ng);
        VaryingScalarTuple3f csParam =
            (VaryingScalarTuple3f) parameters.getParameter("Cs");
        if (csParam != null) {
            csParam.getValue(0, color);
            sv.Cs.set(color);
        } else
            sv.Cs.set(attributes.getColor());
        VaryingScalarTuple3f osParam =
            (VaryingScalarTuple3f) parameters.getParameter("Os");
        if (osParam != null) {
            osParam.getValue(0, color);
            sv.Os.set(color);
        } else
            sv.Os.set(attributes.getOpacity());
    }

    protected void dice_PandNg(ShaderVariables shaderVariables) {
        tmp.set(x, y, z);
        objectToCamera.transformPoint(tmp, camera);
        v.set(width, 0f, 0f);
        objectToCamera.transformVector(v, v);
        float twidth = v.length();
        float halfWidth = twidth / 2f;
        float height = (float) (Math.sqrt(twidth * twidth - halfWidth * halfWidth)) / 2f;
        v.set(0f, 0f, -1f);
        Point3fGrid P = shaderVariables.P;
        Vector3fGrid Ng = shaderVariables.Ng;
        Ng.set(v);
        tmp.x = -halfWidth / 2f + camera.x;
        tmp.y = height + camera.y;
        tmp.z = camera.z;
        P.set(0, 0, tmp);
        tmp.x = halfWidth / 2f + camera.x;
        tmp.y = height + camera.y;
        tmp.z = camera.z;
        P.set(1, 0, tmp);
        tmp.x = -halfWidth + camera.x;
        tmp.y = camera.y;
        tmp.z = camera.z;
        P.set(0, 1, tmp);
        tmp.x = halfWidth + camera.x;
        tmp.y = camera.y;
        tmp.z = camera.z;
        P.set(1, 1, tmp);
        tmp.x = halfWidth / 2f + camera.x;
        tmp.y = -height + camera.y;
        tmp.z = camera.z;
        P.set(1, 2, tmp);
        tmp.x = -halfWidth / 2f + camera.x;
        tmp.y = -height + camera.y;
        tmp.z = camera.z;
        P.set(0, 2, tmp);
    }

    public boolean isReadyToBeDiced(int gridSize) {
        Grid.setSize(2, 3);
        return true;
    }

}
