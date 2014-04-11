/*
  Paraboloid.java
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

public class Paraboloid extends Quadric {

    private static Point3f tmp = new Point3f();

    private static Vector3f vtmp = new Vector3f();

    private float radius;

    private float zMin;

    private float zMax;

    private float thetaMin;

    private float thetaMax;

    private float minZ;

    private float maxZ;

    private float maxTheta;

    public Paraboloid(
        float radius,
        float zMin,
        float zMax,
        float thetaMin,
        float thetaMax,
        float minZ,
        float maxZ,
        float maxTheta,
        ParameterList parameters,
        Attributes attributes) {
        super(parameters, attributes);
        this.radius = radius;
        this.zMin = zMin;
        this.zMax = zMax;
        this.thetaMin = thetaMin;
        this.thetaMax = thetaMax;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.maxTheta = maxTheta;
    }

    public BoundingVolume getBoundingVolume() {
        float cosThetaMin = (float) Math.cos(thetaMin);
        float sinThetaMin = (float) Math.sin(thetaMin);
        float cosThetaMax = (float) Math.cos(thetaMax);
        float sinThetaMax = (float) Math.sin(thetaMax);
        float rMax = radius * (float) Math.sqrt(zMax / maxZ);
        float rMin = radius * (float) Math.sqrt(zMin / maxZ);
        float xMin;
        float xMax = cosThetaMin * rMin;
        xMax = Math.max(xMax, cosThetaMin * rMax);
        xMax = Math.max(xMax, cosThetaMax * rMin);
        xMax = Math.max(xMax, cosThetaMax * rMax);
        float yMin;
        float yMax;
        if (thetaMin < Math.PI && thetaMax > Math.PI)
            xMin = -rMax;
        else {
            xMin = cosThetaMin * rMin;
            xMin = Math.min(xMin, cosThetaMin * rMax);
            xMin = Math.min(xMin, cosThetaMax * rMin);
            xMin = Math.min(xMin, cosThetaMax * rMax);
        }
        if (thetaMin < Math.PI / 2 && thetaMax > Math.PI / 2)
            yMax = rMax;
        else {
            yMax = sinThetaMin * rMin;
            yMax = Math.max(yMax, sinThetaMin * rMax);
            yMax = Math.max(yMax, sinThetaMax * rMin);
            yMax = Math.max(yMax, sinThetaMax * rMax);
        }
        if (thetaMin < Math.PI * 3 / 2 && thetaMax > Math.PI * 3 / 2)
            yMin = -rMax;
        else {
            yMin = sinThetaMin * rMin;
            yMin = Math.min(yMin, sinThetaMin * rMax);
            yMin = Math.min(yMin, sinThetaMax * rMin);
            yMin = Math.min(yMin, sinThetaMax * rMax);
        }
        Bounds3f result = new Bounds3f(xMin, xMax, yMin, yMax, zMin, zMax);
        return result;
    }

    public Primitive[] split() {
        Primitive[] result = new Primitive[2];
        float r =
            Math.max(
                radius * (float) Math.sqrt(zMin / maxZ),
                radius * (float) Math.sqrt(zMax / maxZ));
        if ((thetaMax - thetaMin) * r > zMax - zMin) {
            result[0] =
                new Paraboloid(
                    radius,
                    zMin,
                    zMax,
                    thetaMin,
                    (thetaMin + thetaMax) / 2f,
                    minZ,
                    maxZ,
                    maxTheta,
                    bilinearInterpolateParameters(0f, .5f, 0f, 1f),
                    attributes);
            result[1] =
                new Paraboloid(
                    radius,
                    zMin,
                    zMax,
                    (thetaMin + thetaMax) / 2f,
                    thetaMax,
                    minZ,
                    maxZ,
                    maxTheta,
                    bilinearInterpolateParameters(.5f, 1f, 0f, 1f),
                    attributes);
        } else {
            result[0] =
                new Paraboloid(
                    radius,
                    zMin,
                    (zMin + zMax) / 2f,
                    thetaMin,
                    thetaMax,
                    minZ,
                    maxZ,
                    maxTheta,
                    bilinearInterpolateParameters(0f, 1f, 0f, .5f),
                    attributes);
            result[1] =
                new Paraboloid(
                    radius,
                    (zMin + zMax) / 2f,
                    zMax,
                    thetaMin,
                    thetaMax,
                    minZ,
                    maxZ,
                    maxTheta,
                    bilinearInterpolateParameters(0f, 1f, .5f, 1f),
                    attributes);
        }
        return result;
    }

    protected void dice_P(ShaderVariables shaderVariables) {
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        float thetaDelta = (thetaMax - thetaMin) / (uSize - 1);
        float zDelta = (zMax - zMin) / (vSize - 1);
        Point3fGrid P = shaderVariables.P;
        for (int u = 0; u < uSize; u++) {
            float theta = thetaMin + thetaDelta * u;
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);
            for (int v = 0; v < vSize; v++) {
                float z = zMin + zDelta * v;
                float r = radius * (float) Math.sqrt(z / maxZ);
                tmp.x = r * cosTheta;
                tmp.y = r * sinTheta;
                tmp.z = z;
                P.set(u, v, tmp);
            }
        }
    }

    protected void dice_Ng(ShaderVariables shaderVariables) {
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        float thetaDelta = (thetaMax - thetaMin) / (uSize - 1);
        float zDelta = (zMax - zMin) / (vSize - 1);
        float dZ = (maxZ - minZ) * thetaMax;
        Vector3fGrid Ng = shaderVariables.Ng;
        for (int u = 0; u < uSize; u++) {
            float theta = thetaMin + thetaDelta * u;
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);
            for (int v = 0; v < vSize; v++) {
                float zv = zMin + zDelta * v;
                float r = radius * (float) Math.sqrt(zv / maxZ);
                vtmp.x = r * cosTheta * dZ;
                vtmp.y = r * sinTheta * dZ;
                vtmp.z =
                    -0.5f
                        * radius
                        * radius
                        * dZ
                        / maxZ
                        * (sinTheta * sinTheta + cosTheta * cosTheta);
                Ng.set(u, v, vtmp);
            }
        }
    }
    
 }
