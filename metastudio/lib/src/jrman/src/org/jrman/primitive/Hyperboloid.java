/*
  Hyperboloid.java
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

public class Hyperboloid extends Quadric {

	private static Point3f tmp = new Point3f();

	private static Vector3f vtmp = new Vector3f();
	
	private static Point3f p1 = new Point3f();
	
	private static Point3f p2 = new Point3f();
	
	private static Vector3f v1 = new Vector3f();
	
	private static Vector3f v2 = new Vector3f();

	private float x1;

	private float y1;

	private float z1;

	private float x2;

	private float y2;

	private float z2;

	private float thetaMin;

	private float thetaMax;

	public Hyperboloid(
		float x1,
		float y1,
		float z1,
		float x2,
		float y2,
		float z2,
		float thetaMin,
		float thetaMax,
		ParameterList parameters,
		Attributes attributes) {
		super(parameters, attributes);
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.thetaMin = thetaMin;
		this.thetaMax = thetaMax;
	}

	public BoundingVolume getBoundingVolume() {

		float cosTMin = (float) Math.cos(thetaMin);
		float sinTMin = (float) Math.sin(thetaMin);
		float cosTMax = (float) Math.cos(thetaMax);
		float sinTMax = (float) Math.sin(thetaMax);
		float x1Min = x1 * cosTMin - y1 * sinTMin;
		float y1Min = x1 * sinTMin + y1 * cosTMin;

		float gamma1Min = (float) Math.atan2(y1Min, x1Min);
		if (gamma1Min < 0)
			gamma1Min += (float) Math.PI * 2f;
		float gamma1Max = gamma1Min + (thetaMax - thetaMin);

		float r1 = (float) Math.sqrt((x1 * x1) + (y1 * y1));
		float[] values1 = analizeAngleCase(r1, gamma1Min, gamma1Max);
		float x11Min = values1[0];
		float x11Max = values1[1];
		float y11Min = values1[2];
		float y11Max = values1[3];

		float x2Min = x2 * cosTMin - y2 * sinTMin;
		float y2Min = x2 * sinTMin + y2 * cosTMin;

		float gamma2Min = (float) Math.atan2(y2Min, x2Min);
		if (gamma2Min < 0)
			gamma2Min += (float) Math.PI * 2f;
		float gamma2Max = gamma2Min + (thetaMax - thetaMin);

		float r2 = (float) Math.sqrt((x2 * x2) + (y2 * y2));
		float[] values2 = analizeAngleCase(r2, gamma2Min, gamma2Max);
		float x22Min = values2[0];
		float x22Max = values2[1];
		float y22Min = values2[2];
		float y22Max = values2[3];

		float xMin = Math.min(x11Min, x22Min);
		float xMax = Math.max(x11Max, x22Max);
		float yMin = Math.min(y11Min, y22Min);
		float yMax = Math.max(y11Max, y22Max);
		float zMin = Math.min(z1, z2);
		float zMax = Math.max(z1, z2);

		Bounds3f b3f = new Bounds3f(xMin, xMax, yMin, yMax, zMin, zMax);
		return b3f;
	}

	private boolean passAngle(float angle, float minAngle, float maxAngle) {
		return minAngle < angle && angle < maxAngle;
	}

	private float[] analizeAngleCase(float r, float gammaMin,
                                         float gammaMax) {

		float[] values = new float[4];
		float cosGammaMin = (float) Math.cos(gammaMin);
		float sinGammaMin = (float) Math.sin(gammaMin);
		float cosGammaMax = (float) Math.cos(gammaMax);
		float sinGammaMax = (float) Math.sin(gammaMax);
		float xMin = Math.min(r * cosGammaMin, r * cosGammaMax);
		float xMax = Math.max(r * cosGammaMin, r * cosGammaMax);
		float yMin = Math.min(r * sinGammaMin, r * sinGammaMax);
		float yMax = Math.max(r * sinGammaMin, r * sinGammaMax);

		if (passAngle((float) Math.PI / 2f, gammaMin, gammaMax)
			|| passAngle(
				(float) Math.PI / 2f + (float) Math.PI * 2f,
				gammaMin,
				gammaMax)) {
			yMax = r;
		}
		if (passAngle((float) Math.PI, gammaMin, gammaMax)
			|| passAngle(
				(float) Math.PI + (float) Math.PI * 2f,
				gammaMin,
				gammaMax)) {
			xMin = -r;
		}
		if (passAngle((float) Math.PI * 3f / 2f, gammaMin, gammaMax)
			|| passAngle(
			(float) Math.PI * 3f / 2f + (float) Math.PI * 2f,
				gammaMin,
				gammaMax)) {
			yMin = -r;
		}
		if (passAngle(0f, gammaMin, gammaMax)
			|| passAngle(
                                    (float) Math.PI * 2f, gammaMin, gammaMax)) {
			xMax = r;
		}
		values[0] = xMin;
		values[1] = xMax;
		values[2] = yMin;
		values[3] = yMax;
		return values;
	}

	public Primitive[] split() {

		float r1 = (float) Math.sqrt((x1 * x1) + (y1 * y1));
		float r2 = (float) Math.sqrt((x2 * x2) + (y2 * y2));
		float r =
                    (float) Math.sqrt(
                                      (x2 - x1) * (x2 - x1)
                                      + (y2 - y1) * (y2 - y1)
                                      + (z2 - z1) * (z2 - z1));
		Primitive[] result = new Primitive[2];
		if ((thetaMax - thetaMin) * (float) Math.max(r1, r2) > r) {
                    result[0] =
                        new Hyperboloid(
					x1,
					y1,
					z1,
					x2,
					y2,
					z2,
					thetaMin,
					(thetaMin + thetaMax) / 2f,
					bilinearInterpolateParameters(0f, .5f,
                                                                      0f, 1f),
					attributes);
                    result[1] =
                        new Hyperboloid(
					x1,
					y1,
					z1,
					x2,
					y2,
					z2,
					(thetaMin + thetaMax) / 2f,
					thetaMax,
					bilinearInterpolateParameters(.5f, 1f,
                                                                      0f, 1f),
					attributes);
		} else {
                    result[0] =
                        new Hyperboloid(
					x1,
					y1,
					z1,
					(x1 + x2) / 2f,
					(y1 + y2) / 2f,
					(z1 + z2) / 2f,
					thetaMin,
					thetaMax,
					bilinearInterpolateParameters(0f, 1f,
                                                                      0f, .5f),
					attributes);
                    result[1] =
                        new Hyperboloid(
					(x1 + x2) / 2f,
					(y1 + y2) / 2f,
					(z1 + z2) / 2f,
					x2,
					y2,
					z2,
					thetaMin,
					thetaMax,
					bilinearInterpolateParameters(0f, 1f,
                                                                      .5f, 1f),
					attributes);
		}
		return result;
	}

	protected void dice_P(ShaderVariables shaderVariables) {
		int uSize = Grid.getUSize();
		int vSize = Grid.getVSize();
		float thetaDelta = (thetaMax - thetaMin) / (uSize - 1);
		float vDelta = 1f / (vSize - 1);
		Point3fGrid P = shaderVariables.P;
		float theta = thetaMin;
		for (int u = 0; u < uSize; u++) {
			float cosTheta = (float) Math.cos(theta);
			float sinTheta = (float) Math.sin(theta);
			float nv = 0f;
			for (int v = 0; v < vSize; v++) {
				float xr = (1f - nv) * x1 + nv * x2;
				float yr = (1f - nv) * y1 + nv * y2;
				float zr = (1f - nv) * z1 + nv * z2;
				tmp.x = xr * cosTheta - yr * sinTheta;
				tmp.y = xr * sinTheta + yr * cosTheta;
				tmp.z = zr;
				P.set(u, v, tmp);
				nv += vDelta;
			}
			theta += thetaDelta;
		}
	}

	protected void dice_Ng(ShaderVariables shaderVariables) {
		int uSize = Grid.getUSize();
		int vSize = Grid.getVSize();
		float thetaDelta = (thetaMax - thetaMin) / (uSize - 1);
		Vector3fGrid Ng = shaderVariables.Ng;
		for (int u = 0; u < uSize; u++) {
			float theta = thetaMin + thetaDelta * u;
			float cosTheta = (float) Math.cos(theta);
			float sinTheta = (float) Math.sin(theta);
			float x11 = x1 * cosTheta - y1 * sinTheta;
			float x22 = x2 * cosTheta - y2 * sinTheta;
			float y11 = x1 * sinTheta + y1 * cosTheta;
			float y22 = x2 * sinTheta + y2 * cosTheta;
			p1.set(x11, y11, z1);
			p2.set(x22, y22, z2);
			v1.sub(p1, p2);
			for (int v = 0; v < vSize; v++) {
				float nv = (float) v / (vSize - 1);
				float xi = (1f - nv) * x11 + nv * x22;
				float yi = (1f - nv) * y11 + nv * y22;
				v2.set(-yi, xi, 0f);
				vtmp.cross(v1, v2);
				Ng.set(u, v, vtmp);
			}
		}
	}

}
