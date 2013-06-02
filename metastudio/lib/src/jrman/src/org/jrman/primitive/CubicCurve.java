/*
  CubicCurve.java
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

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jrman.attributes.Attributes;
import org.jrman.attributes.MutableAttributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.ConvexHull3f;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Grid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.Parameter;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingScalarFloat;
import org.jrman.parameters.VaryingScalarHPoint;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.render.ShaderVariables;
import org.jrman.util.Calc;

public class CubicCurve extends Primitive {

    private final static Declaration DECL_PW = new Declaration("Pw",
            Declaration.StorageClass.VERTEX,
            Declaration.Type.HPOINT, 1);
    
    static Point4f tmpPoint = new Point4f();

    boolean periodic;

    public CubicCurve(boolean periodic,
                       ParameterList parameters,
                       Attributes attributes) {
        this.periodic = periodic;
        this.parameters = parameters;
        VaryingScalarTuple3f param3f =
            (VaryingScalarTuple3f) parameters.getParameter("P");

        if (param3f != null) {
            parameters.removeParameter("P");
            parameters.addParameter(
                new VaryingScalarHPoint(DECL_PW, param3f));
        }
        
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
            VaryingScalarHPoint points =
                (VaryingScalarHPoint) parameters.getParameter("Pw");
            int n = (points.getCount() - 4) / attributes.getVStep() + 2;
            float[] values = new float[n];
            float vInc = 1f / (1 - n);
            float v = 0f;
            for (int i = 0; i < n; i++) {
                values[i] = v;
                v += vInc;
            }
            parameters.addParameter(new VaryingScalarFloat(V_DECL, values));
        }
        MutableAttributes attr = new MutableAttributes(attributes);
        attr.setDisplacementBound(attributes.getDisplacementBound() + w / 2f);
        this.attributes = attr;
    }
    
    public BoundingVolume getBoundingVolume() {
        VaryingScalarHPoint points =
            (VaryingScalarHPoint) parameters.getParameter("Pw");
        ConvexHull3f ch = new ConvexHull3f();
        for (int i = 0, n = points.getCount(); i < n; i++) {
            points.getValue(i, tmpPoint);
            ch.addHpoint(tmpPoint);
        }
        return ch;
    }

    public Primitive[] split() {
        VaryingScalarHPoint points =
            (VaryingScalarHPoint) parameters.getParameter("Pw");
        int n = points.getCount();
        int vStep = attributes.getVStep();
        int nv;
        int lnv;
        if (periodic) {
            nv = n / vStep;
            lnv = nv;
        } else {
            nv = (n - 4) / vStep + 1;
            lnv = nv + 1;
        }
        Primitive[] result = new Primitive[nv];
        int[] uniformIndex = new int[1];
        uniformIndex[0] = 0;
        int[] varyingIndexes = new int[2];
        int[] vertexIndexes = new int[4];
        int i = 0;
        for (int ii = 0; ii < nv; ii++) {
            varyingIndexes[0] = ii % lnv;
            varyingIndexes[1] = (ii + 1) % lnv;
            vertexIndexes[0] = i % n;
            vertexIndexes[1] = (i + 1) % n;
            vertexIndexes[2] = (i + 2) % n;
            vertexIndexes[3] = (i + 3) % n;
            result[ii] =
                new CubicSegment(parameters.selectValues(uniformIndex,
                                                          varyingIndexes,
                                                          vertexIndexes),
                                  attributes);
            i += vStep;
        }
        return result;
    }

    public boolean isReadyToBeDiced(int gridsize) {
        return false;
    }

    private static class CubicSegment extends Primitive {
        
        static Point4f pa = new Point4f();
        
        static Point4f pb = new Point4f();

        static Point4f pc = new Point4f();

        static Point4f pd = new Point4f();

        static Point4f pta = new Point4f();
        
        static Point4f ptb = new Point4f();

        static Point4f ptc = new Point4f();

        static Point4f ptd = new Point4f();

        static Vector3f v3 = new Vector3f();

        static Vector3f vTmp = new Vector3f();

        static Point4f PV0 = new Point4f();
        
        static Point4f PV1 = new Point4f();

        static Point3f p3fa = new Point3f();
        
        static Point3f p3fb = new Point3f();
        
        static float wa;

        static float wd;
    
        CubicSegment(ParameterList parameters, Attributes attributes) {
            this(parameters, attributes, true);
        }

        CubicSegment(ParameterList parameters, Attributes attributes,
                     boolean apply) {
            this.parameters = parameters;
            this.attributes = attributes;
            Parameter param = parameters.getParameter("v");
            if (param == null) {
                float[] v = new float[2];
                v[0] = 0f;
                v[1] = 1f;
                parameters.addParameter(new VaryingScalarFloat(V_DECL, v));
            }
            if (apply)
                applyBasis();
        }

        public BoundingVolume getBoundingVolume() {
            ConvexHull3f ch = new ConvexHull3f();
            extractPoints();
            ch.addHpoint(pa);
            ch.addHpoint(pb);
            ch.addHpoint(pc);
            ch.addHpoint(pd);
            return ch;
        }

        private void extractPoints() {
            VaryingScalarHPoint paramHp =
                (VaryingScalarHPoint) parameters.getParameter("Pw");
            paramHp.getValue(0, pa);
            paramHp.getValue(1, pb);
            paramHp.getValue(2, pc);
            paramHp.getValue(3, pd);
        }

        private void setPoints() {
            VaryingScalarHPoint paramHp =
                (VaryingScalarHPoint) parameters.getParameter("Pw");
            paramHp.setValue(0, pa);
            paramHp.setValue(1, pb);
            paramHp.setValue(2, pc);
            paramHp.setValue(3, pd);
        }

        private void applyBasis() {
            extractPoints();
            Matrix4f hv = attributes.getVBasis().getToBezier();
            applyBasis(pa, pb, pc, pd, hv);
            setPoints();
        }
        
        private void applyBasis(Point4f p0, Point4f p1, Point4f p2, Point4f p3,
                                Matrix4f h) {
            applyBasis(p0.x, p1.x, p2.x, p3.x, h, PV0);
            p0.x = PV0.x;
            p1.x = PV0.y;
            p2.x = PV0.z;
            p3.x = PV0.w;
            applyBasis(p0.y, p1.y, p2.y, p3.y, h, PV0);
            p0.y = PV0.x;
            p1.y = PV0.y;
            p2.y = PV0.z;
            p3.y = PV0.w;
            applyBasis(p0.z, p1.z, p2.z, p3.z, h, PV0);
            p0.z = PV0.x;
            p1.z = PV0.y;
            p2.z = PV0.z;
            p3.z = PV0.w;
            applyBasis(p0.w, p1.w, p2.w, p3.w, h, PV0);
            p0.w = PV0.x;
            p1.w = PV0.y;
            p2.w = PV0.z;
            p3.w = PV0.w;
        }
        
        private void applyBasis(float p0,
                                float p1,
                                float p2,
                                float p3,
                                Matrix4f hu,
                                Point4f result) {
            PV1.set(p0, p1, p2, p3);
            hu.transform(PV1, result);
        }
        
        private void extractWidths() {
            VaryingScalarFloat widthParam =
                (VaryingScalarFloat) parameters.getParameter("width");
            if (widthParam != null) {
                wa = widthParam.getValue(0);
                wd = widthParam.getValue(1);
            } else {
                UniformScalarFloat constantWidthParam = (UniformScalarFloat)
                    parameters.getParameter("constantwidth");
                if (constantWidthParam != null)
                    wa = wd = constantWidthParam.getValue();
                else
                    wa = wd = 1f;
            }
        }

         public Primitive[] split() {
            Primitive[] result = new Primitive[2];
            result[0] = new CubicSegment(cubicInterpolateParameters(0f, .5f),
                                         attributes, false);
            result[1] = new CubicSegment(cubicInterpolateParameters(.5f, 1),
                                         attributes, false);
            return result;
        }

        private ParameterList cubicInterpolateParameters(float min, float max) {
            ParameterList result =
                linearInterpolateParameters(min, max);
            extractPoints();
            VaryingScalarHPoint sparam =
                new VaryingScalarHPoint(DECL_PW, new float[4 * 4]);
            if (min == 0.5f)
                splitUpper(pa, pb, pc, pd, pta, ptb, ptc, ptd);
            else
                splitLower(pa, pb, pc, pd, pta, ptb, ptc, ptd);
            sparam.setValue(0, pta);
            sparam.setValue(1, ptb);
            sparam.setValue(2, ptc);
            sparam.setValue(3, ptd);
            result.addParameter(sparam);
            return result;
        }

        private void splitLower(Point4f in0,
                                Point4f in1,
                                Point4f in2,
                                Point4f in3,
                                Point4f out0,
                                Point4f out1,
                                Point4f out2,
                                Point4f out3) {
            out0.set(in0);
            out1.add(in0, in1);
            out1.scale(.5f);
            out2.set(in1);
            out2.scale(2f);
            out2.add(in0);
            out2.add(in2);
            out2.scale(.25f);
            out3.add(in1, in2);
            out3.scale(3f);
            out3.add(in0);
            out3.add(in3);
            out3.scale(.125f);
        }

        private void splitUpper(Point4f in0,
                                Point4f in1,
                                Point4f in2,
                                Point4f in3,
                                Point4f out0,
                                Point4f out1,
                                Point4f out2,
                                Point4f out3) {
            out3.set(in3);
            out2.add(in3, in2);
            out2.scale(.5f);
            out1.set(in2);
            out1.scale(2f);
            out1.add(in3);
            out1.add(in1);
            out1.scale(.25f);
            out0.add(in2, in1);
            out0.scale(3f);
            out0.add(in3);
            out0.add(in0);
            out0.scale(.125f);
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
            objectToCamera.transformHPoint(pa, pa);
            objectToCamera.transformHPoint(pb, pb);
            objectToCamera.transformHPoint(pc, pc);
            objectToCamera.transformHPoint(pd, pd);
            Point3fGrid P = shaderVariables.P;
            int nv = Grid.getVSize();
            float vStep = 1f / (nv - 1);
            float v = 0f;
            for (int i = 0; i < nv; i++) {
                Calc.bezierInterpolate(pa, pb, pc, pd, v, PV0);
                p3fa.project(PV0);
                P.set(0, i, p3fa);
                v += vStep;
            }
            extractWidths();
            v3.set(wa, 0f, 0f);
            objectToCamera.transformVector(v3, v3);
            wa = v3.length() / 2;
            v3.set(wd, 0f, 0f);
            objectToCamera.transformVector(v3, v3);
            wd = v3.length() /2;
            Vector3fGrid N = shaderVariables.N;
            v = 0f;
            for (int i = 0; i < nv; i++) {
                float wv = Calc.interpolate(wa, wd, v);
                P.get(0, i, p3fa);
                if (i < nv - 1) {
                    P.get(0, i + 1, p3fb);
                    if (!hasN) {
                        p3fa.z = 0f;
                        p3fb.z = 0f;
                    }
                    v3.sub(p3fb, p3fa);
                }
                N.get(0, i, vTmp);
                vTmp.cross(v3, vTmp);
                vTmp.normalize();
                P.get(0, i, p3fa);
                p3fb.scaleAdd(wv, vTmp, p3fa);
                P.set(0, i, p3fb);
                p3fb.scaleAdd(-wv, vTmp, p3fa);
                P.set(1, i, p3fb);
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
