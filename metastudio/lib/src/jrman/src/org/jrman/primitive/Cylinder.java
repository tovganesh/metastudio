/*
  Cylinder.java
  Copyright (C) 2003, 2006 Gerardo Horvilleur Martinez
  
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

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.Bounds3f;
import org.jrman.grid.Grid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.ParameterList;
import org.jrman.render.ShaderVariables;

public class Cylinder extends Quadric {
    
    private static Point3f tmp = new Point3f();
    
    private static Vector3f vtmp = new Vector3f();

    float radius;

    float zmin;

    float zmax;

    float thetaMin;

    float thetaMax;

    public Cylinder(
        float radius,
        float zmin,
        float zmax,
        float thetaMin,
        float thetaMax,
        ParameterList parameters,
        Attributes attributes) {
        super(parameters, attributes);
        this.radius = radius;
        this.zmin = zmin;
        this.zmax = zmax;
        this.thetaMin = thetaMin;
        this.thetaMax = thetaMax;
    }

    public BoundingVolume getBoundingVolume() {
        float cosThetaMin = (float) Math.cos(thetaMin);
        float sinThetaMin = (float) Math.sin(thetaMin);
        float cosThetaMax = (float) Math.cos(thetaMax);
        float sinThetaMax = (float) Math.sin(thetaMax);
        float xMin;
        float xMax = cosThetaMin * radius;
        xMax = Math.max(xMax, cosThetaMax * radius);
        float yMin;
        float yMax;
        if (thetaMin < Math.PI && thetaMax > Math.PI)
            xMin = -radius;
        else {
            xMin = cosThetaMin * radius;
            xMin = Math.min(xMin, cosThetaMax * radius);
        }
        if (thetaMin < Math.PI / 2 && thetaMax > Math.PI / 2)
            yMax = radius;
        else {
            yMax = sinThetaMin * radius;
            yMax = Math.max(yMax, sinThetaMax * radius);
        }
        if (thetaMin < Math.PI * 3 / 2 && thetaMax > Math.PI * 3 / 2)
            yMin = -radius;
        else {
            yMin = sinThetaMin * radius;
            yMin = Math.min(yMin, sinThetaMax * radius);
        }
        Bounds3f result = new Bounds3f(xMin, xMax, yMin, yMax, zmin, zmax);
        return result;
    }

    public Primitive[] split() {
        Primitive[] result = new Primitive[2];
        if ((thetaMax - thetaMin) * radius > zmax - zmin) {
            result[0] =
                new Cylinder(
                    radius,
                    zmin,
                    zmax,
                    thetaMin,
                    (thetaMin + thetaMax) / 2f,
                    bilinearInterpolateParameters(0f, .5f, 0f, 1f),
                    attributes);
            result[1] =
                new Cylinder(
                    radius,
                    zmin,
                    zmax,
                    (thetaMin + thetaMax) / 2f,
                    thetaMax,
                    bilinearInterpolateParameters(.5f, 1f, 0f, 1f),
                    attributes);
        } else {
            result[0] =
                new Cylinder(
                    radius,
                    zmin,
                    (zmin + zmax) / 2f,
                    thetaMin,
                    thetaMax,
                    bilinearInterpolateParameters(0f, 1f, 0f, .5f),
                    attributes);
            result[1] =
                new Cylinder(
                    radius,
                    (zmin + zmax) / 2f,
                    zmax,
                    thetaMin,
                    thetaMax,
                    bilinearInterpolateParameters(0f, 1f, .5f, 1f),
                    attributes);
        }
        return result;
    }

    protected void dice_P(ShaderVariables shaderVariables) {
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        float thetaDelta = (thetaMax - thetaMin) / (uSize - 1);
        float zDelta = (zmax - zmin) / (vSize - 1);
        Point3fGrid P = shaderVariables.P;
        float theta = thetaMin;
        for (int u = 0; u < uSize; u++) {
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);
            float z = zmin;
            for (int v = 0; v < vSize; v++) {
                tmp.x = radius * cosTheta;
                tmp.y = radius * sinTheta;
                tmp.z = z;
                P.set(u, v, tmp);
                z += zDelta;
            }
            theta += thetaDelta;
        }
    }

    protected void dice_Ng(ShaderVariables shaderVariables) {
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        float thetaDelta = (thetaMax - thetaMin) / (uSize - 1);
        float zDelta = (zmax - zmin) / (vSize - 1);
        Vector3fGrid Ng = shaderVariables.Ng;
        float theta = thetaMin;
        vtmp.x = 0f;
        vtmp.y = 0f;
        for (int u = 0; u < uSize; u++) {
            float z = zmin;
            for (int v = 0; v < vSize; v++) {
                vtmp.z = z;
                Ng.set(u, v, vtmp);
                z += zDelta;
            }
            theta += thetaDelta;
        }
        Ng.sub(shaderVariables.P, Ng);
    }

}
