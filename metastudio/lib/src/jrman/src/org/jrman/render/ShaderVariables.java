/*
 ShaderVariables.java
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

package org.jrman.render;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import org.jrman.attributes.Attributes;
import org.jrman.attributes.ShadingInterpolation;
import org.jrman.geom.Transform;
import org.jrman.grid.Color3fGrid;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Grid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.ParameterList;

public class ShaderVariables {

    public Transform cameraToWorld;

    public Point3fGrid P = new Point3fGrid();

    public Vector3fGrid N = new Vector3fGrid();

    public Vector3fGrid Ng = new Vector3fGrid();

    public Vector3fGrid I = new Vector3fGrid();

    public Color3fGrid Cs = new Color3fGrid();

    public Color3fGrid Os = new Color3fGrid();

    public FloatGrid u = new FloatGrid();

    public FloatGrid v = new FloatGrid();

    public FloatGrid s = new FloatGrid();

    public FloatGrid t = new FloatGrid();

    public Vector3fGrid dPdu = new Vector3fGrid();

    public Vector3fGrid dPdv = new Vector3fGrid();

    public FloatGrid du = new FloatGrid();

    public FloatGrid dv = new FloatGrid();

    public Vector3fGrid L = new Vector3fGrid();

    public Color3fGrid Cl = new Color3fGrid();

    public Point3fGrid Ps = new Point3fGrid();

    public Color3fGrid Ci = new Color3fGrid();

    public Color3fGrid Oi = new Color3fGrid();

    public Point3f E = new Point3f();

    public Attributes attributes;

    public ParameterList parameters;

    private Map<String, Grid> map = new HashMap<String, Grid>();

    public ShaderVariables(Transform worldToCamera) {
        this.cameraToWorld = worldToCamera;
    }

    public void set(String name, Grid grid) {
        map.put(name, grid);
    }

    public Grid get(String name) {
        return (Grid) map.get(name);
    }

    public void transform(Transform transform) {
        P.transform(P, transform);
    }

    public void primitiveTransform(Transform transform) {
        P.transform(P, transform);
        N.ntransform(N, transform);
        Ng.ntransform(Ng, transform);
        dPdu.vtransform(dPdu, transform);
        dPdv.vtransform(dPdv, transform);
    }

    public void getMicropolygons(RendererHidden rh) {
        int columns = Grid.getUSize();
        int rows = Grid.getVSize();
        Point3f[] p = (Point3f[]) P.data;
        Color3f[] c = (Color3f[]) Ci.data;
        Color3f[] o = (Color3f[]) Oi.data;
        if (attributes.getShadingInterpolation() == ShadingInterpolation.CONSTANT) {
            for (int col = 0; col < columns - 1; col++)
                for (int row = 0; row < rows - 1; row++) {
                    int offset = row * columns + col;
                    SimpleMicropolygon mp = new SimpleMicropolygon();
                    if (mp
                        .init(
                            p[offset],
                            p[offset + 1],
                            p[offset + columns],
                            c[offset].x,
                            c[offset].y,
                            c[offset].z,
                            o[offset].x,
                            o[offset].y,
                            o[offset].z))
                        rh.addToBuckets(mp);
                    mp = new SimpleMicropolygon();
                    if (mp
                        .init(
                            p[offset + columns + 1],
                            p[offset + 1],
                            p[offset + columns],
                            c[offset + columns + 1].x,
                            c[offset + columns + 1].y,
                            c[offset + columns + 1].z,
                            o[offset + columns + 1].x,
                            o[offset + columns + 1].y,
                            o[offset + columns + 1].z))
                        rh.addToBuckets(mp);
                }
        } else {
            for (int col = 0; col < columns - 1; col++)
                for (int row = 0; row < rows - 1; row++) {
                    int offset = row * columns + col;
                    SmoothMicropolygon mp = new SmoothMicropolygon();
                    if (mp
                        .init(
                            p[offset],
                            p[offset + 1],
                            p[offset + columns],
                            c[offset].x,
                            c[offset].y,
                            c[offset].z,
                            c[offset + 1].x,
                            c[offset + 1].y,
                            c[offset + 1].z,
                            c[offset + columns].x,
                            c[offset + columns].y,
                            c[offset + columns].z,
                            o[offset].x,
                            o[offset].y,
                            o[offset].z,
                            o[offset + 1].x,
                            o[offset + 1].y,
                            o[offset + 1].z,
                            o[offset + columns].x,
                            o[offset + columns].y,
                            o[offset + columns].z))
                        rh.addToBuckets(mp);
                    mp = new SmoothMicropolygon();
                    if (mp
                        .init(
                            p[offset + columns + 1],
                            p[offset + 1],
                            p[offset + columns],
                            c[offset + columns + 1].x,
                            c[offset + columns + 1].y,
                            c[offset + columns + 1].z,
                            c[offset + 1].x,
                            c[offset + 1].y,
                            c[offset + 1].z,
                            c[offset + columns].x,
                            c[offset + columns].y,
                            c[offset + columns].z,
                            o[offset + columns + 1].x,
                            o[offset + columns + 1].y,
                            o[offset + columns + 1].z,
                            o[offset + 1].x,
                            o[offset + 1].y,
                            o[offset + 1].z,
                            o[offset + columns].x,
                            o[offset + columns].y,
                            o[offset + columns].z))
                        rh.addToBuckets(mp);
                }
        }
    }

}
