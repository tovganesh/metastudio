/*
  Sphere.java
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

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.Bounds3f;
import org.jrman.grid.Grid;
import org.jrman.grid.Point3fGrid;
import org.jrman.parameters.ParameterList;
import org.jrman.render.ShaderVariables;

public class Sphere extends Quadric {
    
    private static Point3f tmp = new Point3f();

    private float radius;

    private float phiMin;

    private float phiMax;

    private float thetaMin;

    private float thetaMax;

    public Sphere(
        float radius,
        float phiMin,
        float phiMax,
        float thetaMin,
        float thetaMax,
        ParameterList parameters,
        Attributes attributes) {
        super(parameters, attributes);
        this.radius = radius;
        this.phiMin = phiMin;
        this.phiMax = phiMax;
        this.thetaMin = thetaMin;
        this.thetaMax = thetaMax;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Sphere,");
        sb.append(" radius: ").append(radius);
        sb.append(", phi: ").append(phiMin).append(" -> ").append(phiMax);
        sb.append(", theta: ").append(thetaMin).append(" -> ").append(thetaMax);
        return sb.toString();
    }

    public BoundingVolume getBoundingVolume() {
        float zMin = (float) Math.sin(phiMin) * radius;
        float zMax = (float) Math.sin(phiMax) * radius;
        float rMin;
        float rMax;
        float cosPhiMaxR = (float) Math.cos(phiMax) * radius;
        float cosPhiMinR = (float) Math.cos(phiMin) * radius;
        if (phiMin < 0 && phiMax > 0)
            rMax = radius;
        else
            rMax = Math.max(cosPhiMaxR, cosPhiMinR);
        rMin = Math.min(cosPhiMaxR, cosPhiMinR);
        float cosThetaMin = (float) Math.cos(thetaMin);
        float sinThetaMin = (float) Math.sin(thetaMin);
        float cosThetaMax = (float) Math.cos(thetaMax);
        float sinThetaMax = (float) Math.sin(thetaMax);
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
        if (thetaMax - thetaMin > phiMax - phiMin) {
            result[0] =
                new Sphere(
                    radius,
                    phiMin,
                    phiMax,
                    thetaMin,
                    (thetaMin + thetaMax) / 2f,
                    bilinearInterpolateParameters(0f, .5f, 0f, 1f),
                    attributes);
            result[1] =
                new Sphere(
                    radius,
                    phiMin,
                    phiMax,
                    (thetaMin + thetaMax) / 2f,
                    thetaMax,
                    bilinearInterpolateParameters(.5f, 1f, 0f, 1f),
                    attributes);
        } else {
            result[0] =
                new Sphere(
                    radius,
                    phiMin,
                    (phiMin + phiMax) / 2f,
                    thetaMin,
                    thetaMax,
                    bilinearInterpolateParameters(0f, 1f, 0f, .5f),
                    attributes);
            result[1] =
                new Sphere(
                    radius,
                    (phiMin + phiMax) / 2f,
                    phiMax,
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
        float phiDelta = (phiMax - phiMin) / (vSize - 1);
        Point3fGrid P = shaderVariables.P;
        float theta = thetaMin;
        for (int u = 0; u < uSize; u++) {
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);
            float phi = phiMin;
            for (int v = 0; v < vSize; v++) {
                float cosPhi = (float) Math.cos(phi);
                float sinPhi = (float) Math.sin(phi);
                tmp.x = radius * cosTheta * cosPhi;
                tmp.y = radius * sinTheta * cosPhi;
                tmp.z = radius * sinPhi;
                P.set(u, v, tmp);
                phi += phiDelta;
            }
            theta += thetaDelta;
        }
    }

    protected void dice_Ng(ShaderVariables shaderVariables) {
        shaderVariables.Ng.set(shaderVariables.P);
    }

}
