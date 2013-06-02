/*
  Torus.java
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

public class Torus extends Quadric {
    
    private static Point3f tmp = new Point3f();
    
    private static Vector3f vtmp = new Vector3f();

    private float majorRadius;

    private float minorRadius;

    private float phiMin;

    private float phiMax;

    private float thetaMin;

    private float thetaMax;

    public Torus(
        float majorRadius,
        float minorRadius,
        float phiMin,
        float phiMax,
        float thetaMin,
        float thetaMax,
        ParameterList parameters,
        Attributes attributes) {
        super(parameters, attributes);
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.phiMin = phiMin;
        this.phiMax = phiMax;
        this.thetaMin = thetaMin;
        this.thetaMax = thetaMax;
    }

    public BoundingVolume getBoundingVolume() {
        float zMin;
        float zMax;
        float sinPhiMinR = (float) Math.sin(phiMin) * minorRadius;
        float sinPhiMaxR = (float) Math.sin(phiMax) * minorRadius;
        if (phiMin < Math.PI / 2 && phiMax > Math.PI / 2)
            zMax = minorRadius;
         else
            zMax = Math.max(sinPhiMinR, sinPhiMaxR);
         if (phiMin < Math.PI * 3 / 2 && phiMax > Math.PI * 3 / 2)
            zMin = -minorRadius;
        else
            zMin = Math.min(sinPhiMinR, sinPhiMaxR);
        float cosPhiMinR = (float) Math.cos(phiMin) * minorRadius;
        float cosPhiMaxR = (float) Math.cos(phiMax) * minorRadius;
        float rMin;
        if (phiMin < Math.PI && phiMax > Math.PI)
            rMin = majorRadius - minorRadius;
        else
            rMin = majorRadius + Math.min(cosPhiMinR, cosPhiMaxR);
        float rMax = majorRadius + Math.max(cosPhiMinR, cosPhiMaxR);
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
        if ((thetaMax - thetaMin)  * majorRadius >
            (phiMax - phiMin) * minorRadius) {
            result[0] =
                new Torus(
                    majorRadius,
                    minorRadius,
                    phiMin,
                    phiMax,
                    thetaMin,
                    (thetaMin + thetaMax) / 2f,
                    bilinearInterpolateParameters(0f, .5f, 0f, 1f),
                    attributes);
            result[1] =
                new Torus(
                    majorRadius,
                    minorRadius,
                    phiMin,
                    phiMax,
                    (thetaMin + thetaMax) / 2f,
                    thetaMax,
                    bilinearInterpolateParameters(.5f, 1f, 0f, 1f),
                    attributes);
        } else {
            result[0] =
                new Torus(
                    majorRadius,
                    minorRadius,
                    phiMin,
                    (phiMin + phiMax) / 2f,
                    thetaMin,
                    thetaMax,
                    bilinearInterpolateParameters(0f, 1f, 0f, .5f),
                    attributes);
            result[1] =
                new Torus(
                    majorRadius,
                    minorRadius,
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
        float thetaDelta = (thetaMax - thetaMin) / ( uSize - 1);
        float phiDelta = (phiMax - phiMin) / (vSize - 1);
        Point3fGrid P = shaderVariables.P;
        float theta = thetaMin;
        for (int u = 0; u < uSize; u++) {
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);
            float phi = phiMin;
            for (int v = 0; v < vSize; v++) {
                float r = minorRadius * (float) Math.cos(phi);
                tmp.z = minorRadius * (float) Math.sin(phi);
                tmp.x = (majorRadius + r) * cosTheta;
                tmp.y = (majorRadius + r) * sinTheta;
                P.set(u, v, tmp);
                phi += phiDelta;
            }
            theta += thetaDelta;
        }
    }

    protected void dice_Ng(ShaderVariables shaderVariables) {
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        float thetaDelta = (thetaMax - thetaMin) / ( uSize - 1);
        Vector3fGrid Ng = shaderVariables.Ng;
        float theta = thetaMin;
        for (int u = 0; u < uSize; u++) {
            float cosTheta = (float) Math.cos(theta);
            float sinTheta = (float) Math.sin(theta);
            vtmp.z = 0f;
            vtmp.x = majorRadius * cosTheta;
            vtmp.y = majorRadius * sinTheta;
            for (int v = 0; v < vSize; v++)
                Ng.set(u, v, vtmp);
            theta += thetaDelta;
        }
        Ng.sub(shaderVariables.P, Ng);
    }

}
