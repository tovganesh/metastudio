/*
  LinearCurve.java
  Copyright (C) 2006 Gerardo Horvilleur Martinez
  
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
import org.jrman.attributes.MutableAttributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.ConvexHull3f;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Grid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Parameter;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingScalarFloat;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.render.ShaderVariables;
import org.jrman.util.Calc;

public class LinearCurve extends Primitive {

    static Point3f tmpPoint = new Point3f();

    boolean periodic;

    public LinearCurve(boolean periodic,
                       ParameterList parameters,
                       Attributes attributes) {
        this.periodic = periodic;
        this.parameters = parameters;
        VaryingScalarFloat widthParam =
            (VaryingScalarFloat) parameters.getParameter("width");
        UniformScalarFloat constantWidthParam =
            (UniformScalarFloat) parameters.getParameter("constantwidth");
        float w;
        if (widthParam != null) {
            w = 0f;
            int n = widthParam.getCount();
            for (int i = 0; i < n; i++) {
                float wi = widthParam.getValue(i);
                if (wi > w)
                    w = wi;
            }
        } else if (constantWidthParam != null)
            w = constantWidthParam.getValue();
        else
            w = 1f;
        Parameter param = parameters.getParameter("v");
        if (param == null && !periodic) {
            VaryingScalarTuple3f points =
                (VaryingScalarTuple3f) parameters.getParameter("P");
            int n = points.getCount();
            float[] values = new float[n];
            float vStep = 1f / (1 - n);
            float v = 0f;
            for (int i = 0; i < n; i++) {
                values[i] = v;
                v += vStep;
            }
            parameters.addParameter(new VaryingScalarFloat(V_DECL, values));
        }
        MutableAttributes attr = new MutableAttributes(attributes);
        attr.setDisplacementBound(attributes.getDisplacementBound() + w / 2f);
        this.attributes = attr;
    }
    
    public BoundingVolume getBoundingVolume() {
        VaryingScalarTuple3f points =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        ConvexHull3f ch = new ConvexHull3f();
        for (int i = 0, n = points.getCount(); i < n; i++) {
            points.getValue(i, tmpPoint);
            ch.addPoint(tmpPoint);
        }
        return ch;
    }

    public Primitive[] split() {
        VaryingScalarTuple3f points =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        int n = points.getCount();
        Primitive[] result = new Primitive[n - (periodic ? 0 : 1)];
        int[] uniformIndex = new int[1];
        uniformIndex[0] = 0;
        int[] indexes = new int[2];
        for (int i = 0; i < n - 1; i++) {
            indexes[0] = i;
            indexes[1] = i + 1;
            result[i] =
                new LinearSegment(parameters.selectValues(uniformIndex,
                                                          indexes,
                                                          indexes),
                                  attributes);
        }
        if (periodic) {
            indexes[0] = n - 1;
            indexes[1] = 0;
            result[n - 1] =
                new LinearSegment(parameters.selectValues(uniformIndex,
                                                          indexes,
                                                          indexes),
                                  attributes);
        }
        return result;
    }

    public boolean isReadyToBeDiced(int gridsize) {
        return false;
    }

    private static class LinearSegment extends Primitive {
        
        static Point3f pa = new Point3f();
        
        static Point3f pb = new Point3f();

        static Point3f pTmp1 = new Point3f();

        static Point3f pTmp2 = new Point3f();

        static Vector3f v3 = new Vector3f();

        static Vector3f vTmp = new Vector3f();

        static float wa;

        static float wb;
    
        LinearSegment(ParameterList parameters, Attributes attributes) {
            this.parameters = parameters;
            this.attributes = attributes;
            Parameter param = parameters.getParameter("v");
            if (param == null) {
                float[] v = new float[2];
                v[0] = 0f;
                v[1] = 1f;
                parameters.addParameter(new VaryingScalarFloat(V_DECL, v));
            }
        }

        public BoundingVolume getBoundingVolume() {
            ConvexHull3f ch = new ConvexHull3f();
            extractPoints();
            ch.addPoint(pa);
            ch.addPoint(pb);
            return ch;
        }

        public Primitive[] split() {
            Primitive[] result = new Primitive[2];
            result[0] = new LinearSegment(linearInterpolateParameters(0f, .5f),
                                          attributes);
            result[1] = new LinearSegment(linearInterpolateParameters(.5f, 1),
                                          attributes);
            return result;
        }

        private void extractPoints() {
            VaryingScalarTuple3f param =
                (VaryingScalarTuple3f) parameters.getParameter("P");
            param.getValue(0, pa);
            param.getValue(1, pb);
        }

        private void extractWidths() {
            VaryingScalarFloat widthParam =
                (VaryingScalarFloat) parameters.getParameter("width");
            if (widthParam != null) {
                wa = widthParam.getValue(0);
                wb = widthParam.getValue(1);
            } else {
                UniformScalarFloat constantWidthParam = (UniformScalarFloat)
                    parameters.getParameter("constantwidth");
                if (constantWidthParam != null)
                    wa = wb = constantWidthParam.getValue();
                else
                    wa = wb = 1f;
            }
        }

        public boolean isReadyToBeDiced(int gridSize) {
            float sw = rasterBounds.getWidth();
            float sh = rasterBounds.getHeight();
            float length = (float) Math.sqrt(sw * sw + sh * sw);
            boolean ready = length <=
                (gridSize / 2) * attributes.getShadingRate();
            if (!ready)
                return false;
            float idealSize = length / (2f * attributes.getShadingRate());
            idealSize = Calc.clamp(idealSize, 40, gridSize / 2);
            int vSize = (int) Math.ceil(idealSize);
            Grid.setSize(2, vSize);
            return true;
        }

        public void dice(ShaderVariables shaderVariables) {
            shaderVariables.attributes = attributes;
            shaderVariables.parameters = parameters;
            dice_u(shaderVariables);
            dice_v(shaderVariables);
            // dice_s(shaderVariables);
            // dice_t(shaderVariables);
            dice_Ng_N_P(shaderVariables);
            dice_du(shaderVariables);
            dice_dv(shaderVariables);
            dice_dPdu(shaderVariables);
            dice_dPdv(shaderVariables);
            dice_Cs(shaderVariables);
            dice_Os(shaderVariables);
            dice_others(shaderVariables);
        }

        protected void dice_Ng_N_P(ShaderVariables shaderVariables) {
            boolean hasN = false;
            v3.set(0f, 0f, -1f);
            shaderVariables.Ng.set(v3);
            VaryingScalarTuple3f param =
                (VaryingScalarTuple3f) parameters.getParameter("N");
            if (param != null) {
                param.linearDice(shaderVariables.N);
                hasN = true;
            } else
                shaderVariables.N.set(shaderVariables.Ng);
            extractPoints();
            extractWidths();
            objectToCamera.transformPoint(pa, pa);
            objectToCamera.transformPoint(pb, pb);
            v3.set(wa, 0f, 0f);
            objectToCamera.transformVector(v3, v3);
            wa = v3.length() / 2;
            v3.set(wb, 0f, 0f);
            objectToCamera.transformVector(v3, v3);
            wb = v3.length() /2;
            pTmp1.set(pa);
            pTmp2.set(pb);
            if (!hasN) {
                pTmp1.z = 0f;
                pTmp2.z = 0f;
            }
            v3.sub(pTmp2, pTmp1);
            Point3fGrid P = shaderVariables.P;
            Vector3fGrid N = shaderVariables.N;
            int vSize = Grid.getVSize();
            float vStep = 1f / (vSize - 1);
            float v = 0;
            for (int i = 0; i < vSize; i++) {
                N.get(0, i, vTmp);
                vTmp.cross(v3, vTmp);
                vTmp.normalize();
                Calc.interpolate(pa, pb, v, pTmp1);
                float w = Calc.interpolate(wa, wb, v);
                pTmp2.scaleAdd(w, vTmp, pTmp1);
                P.set(0, i, pTmp2);
                pTmp2.scaleAdd(-w, vTmp, pTmp1);
                P.set(1, i, pTmp2);
                v += vStep;
            }
        }
        
        protected void dice_u(ShaderVariables shaderVariables) {
            FloatGrid g = shaderVariables.u;
            int vSize = Grid.getVSize();
            for (int i = 0; i < vSize; i++) {
                g.set(0, i, 0f);
                g.set(1, i, 1f);
            }
        }

        /*
        protected void dice_v(ShaderVariables shaderVariables) {
            FloatGrid g = shaderVariables.v;
            int vSize = Grid.getVSize();
            float vStep = 1f / (vSize - 1);
            float v = 0;
            for (int i = 0; i < vSize; i++) {
                g.set(0, i, v);
                g.set(1, i, v);
                v += vStep;
            }
        }
        */
        
    }

}
