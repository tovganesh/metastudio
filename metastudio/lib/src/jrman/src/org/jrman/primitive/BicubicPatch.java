/*
  BicubicPatch.java
  Copyright (C) 2004, 2006 Gerardo Horvilleur Martinez
  
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
import javax.vecmath.Vector4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point4f;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.ConvexHull3f;
import org.jrman.grid.Grid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.parameters.VaryingScalarHPoint;
import org.jrman.render.ShaderVariables;
import org.jrman.util.Calc;

public class BicubicPatch extends Primitive {
    
    private final static Declaration DECL_PW = new Declaration("Pw",
            Declaration.StorageClass.VERTEX,
            Declaration.Type.HPOINT, 1);
    
    private static Point4f P00 = new Point4f();

    private static Point4f P10 = new Point4f();

    private static Point4f P20 = new Point4f();

    private static Point4f P30 = new Point4f();

    private static Point4f P01 = new Point4f();

    private static Point4f P11 = new Point4f();

    private static Point4f P21 = new Point4f();

    private static Point4f P31 = new Point4f();

    private static Point4f P02 = new Point4f();

    private static Point4f P12 = new Point4f();

    private static Point4f P22 = new Point4f();

    private static Point4f P32 = new Point4f();

    private static Point4f P03 = new Point4f();

    private static Point4f P13 = new Point4f();

    private static Point4f P23 = new Point4f();

    private static Point4f P33 = new Point4f();

    private static Point4f PS00 = new Point4f();

    private static Point4f PS10 = new Point4f();

    private static Point4f PS20 = new Point4f();

    private static Point4f PS30 = new Point4f();

    private static Point4f PS01 = new Point4f();

    private static Point4f PS11 = new Point4f();

    private static Point4f PS21 = new Point4f();

    private static Point4f PS31 = new Point4f();

    private static Point4f PS02 = new Point4f();

    private static Point4f PS12 = new Point4f();

    private static Point4f PS22 = new Point4f();

    private static Point4f PS32 = new Point4f();

    private static Point4f PS03 = new Point4f();

    private static Point4f PS13 = new Point4f();

    private static Point4f PS23 = new Point4f();

    private static Point4f PS33 = new Point4f();

    private static Point3f P3F = new Point3f();

    private static Vector4f vtmp = new Vector4f();

    private static Point4f PV0 = new Point4f();

    private static Point4f PV1 = new Point4f();
    
    public BicubicPatch(ParameterList parameters, Attributes attributes) {
        this(parameters, attributes, true);
    }

    private BicubicPatch(ParameterList parameters, Attributes attributes,
                         boolean apply) {
        super(parameters, attributes);
        if (apply)
            applyBasis();
    }

    private void extractPoints() {
        VaryingScalarTuple3f param3f =
            (VaryingScalarTuple3f) parameters.getParameter("P");

        if (param3f != null) {
            parameters.removeParameter("P");
            parameters.addParameter(
                new VaryingScalarHPoint(DECL_PW, param3f));
        }
        VaryingScalarHPoint paramHp =
            (VaryingScalarHPoint) parameters.getParameter("Pw");

        paramHp.getValue(0, P00);
        paramHp.getValue(1, P10);
        paramHp.getValue(2, P20);
        paramHp.getValue(3, P30);
        paramHp.getValue(4, P01);
        paramHp.getValue(5, P11);
        paramHp.getValue(6, P21);
        paramHp.getValue(7, P31);
        paramHp.getValue(8, P02);
        paramHp.getValue(9, P12);
        paramHp.getValue(10, P22);
        paramHp.getValue(11, P32);
        paramHp.getValue(12, P03);
        paramHp.getValue(13, P13);
        paramHp.getValue(14, P23);
        paramHp.getValue(15, P33);
    }

    private void setPoints() {
        VaryingScalarHPoint param =
            (VaryingScalarHPoint) parameters.getParameter("Pw");
        param.setValue(0, P00);
        param.setValue(1, P10);
        param.setValue(2, P20);
        param.setValue(3, P30);
        param.setValue(4, P01);
        param.setValue(5, P11);
        param.setValue(6, P21);
        param.setValue(7, P31);
        param.setValue(8, P02);
        param.setValue(9, P12);
        param.setValue(10, P22);
        param.setValue(11, P32);
        param.setValue(12, P03);
        param.setValue(13, P13);
        param.setValue(14, P23);
        param.setValue(15, P33);
    }

    private void applyBasis() {
        extractPoints();
        Matrix4f hu = attributes.getUBasis().getToBezier();
        Matrix4f hv = attributes.getVBasis().getToBezier();

        applyBasis(P00, P10, P20, P30, hu);
        applyBasis(P01, P11, P21, P31, hu);
        applyBasis(P02, P12, P22, P32, hu);
        applyBasis(P03, P13, P23, P33, hu);

        applyBasis(P00, P01, P02, P03, hv);
        applyBasis(P10, P11, P12, P13, hv);
        applyBasis(P20, P21, P22, P23, hv);
        applyBasis(P30, P31, P32, P33, hv);

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

    private void applyBasis(
        float p0,
        float p1,
        float p2,
        float p3,
        Matrix4f hu,
        Point4f result) {
        PV1.set(p0, p1, p2, p3);
        hu.transform(PV1, result);
    }

    public BoundingVolume getBoundingVolume() {
        ConvexHull3f ch = new ConvexHull3f();
        extractPoints();
        ch.addHpoint(P00);
        ch.addHpoint(P10);
        ch.addHpoint(P20);
        ch.addHpoint(P30);
        ch.addHpoint(P01);
        ch.addHpoint(P11);
        ch.addHpoint(P21);
        ch.addHpoint(P31);
        ch.addHpoint(P02);
        ch.addHpoint(P12);
        ch.addHpoint(P22);
        ch.addHpoint(P32);
        ch.addHpoint(P03);
        ch.addHpoint(P13);
        ch.addHpoint(P23);
        ch.addHpoint(P33);
        return ch;
    }

    public Primitive[] split() {
        Primitive[] result = new Primitive[2];
        extractPoints();
        vtmp.sub(P30, P00);
        float l1 = vtmp.length();
        vtmp.sub(P33, P03);
        float l2 = vtmp.length();
        float ul = (l1 + l2) / 2f;
        vtmp.sub(P03, P00);
        l1 = vtmp.length();
        vtmp.sub(P33, P30);
        l2 = vtmp.length();
        float vl = (l1 + l2) / 2f;
        if (ul > vl) {
            result[0] =
                new BicubicPatch(bezierInterpolateParameters(0f, .5f, 0f, 1f),
                                 attributes, false);
            result[1] =
                new BicubicPatch(bezierInterpolateParameters(.5f, 1f, 0f, 1f),
                                 attributes, false);
        } else {
            result[0] =
                new BicubicPatch(bezierInterpolateParameters(0f, 1f, 0f, .5f),
                                 attributes, false);
            result[1] =
                new BicubicPatch(bezierInterpolateParameters(0f, 1f, .5f, 1f),
                                 attributes, false);
        }
        return result;
    }

    protected ParameterList bezierInterpolateParameters(
        float uv00,
        float uv10,
        float uv01,
        float uv11) {
        ParameterList result =
            bilinearInterpolateParameters(uv00, uv10, uv01, uv11);
        extractPoints();
        VaryingScalarHPoint sparam =
            new VaryingScalarHPoint(DECL_PW, new float[16 * 4]);
        if (uv00 == .5f) {
            splitUpper(P00, P10, P20, P30, PS00, PS10, PS20, PS30);
            splitUpper(P01, P11, P21, P31, PS01, PS11, PS21, PS31);
            splitUpper(P02, P12, P22, P32, PS02, PS12, PS22, PS32);
            splitUpper(P03, P13, P23, P33, PS03, PS13, PS23, PS33);
        } else if (uv10 == .5f) {
            splitLower(P00, P10, P20, P30, PS00, PS10, PS20, PS30);
            splitLower(P01, P11, P21, P31, PS01, PS11, PS21, PS31);
            splitLower(P02, P12, P22, P32, PS02, PS12, PS22, PS32);
            splitLower(P03, P13, P23, P33, PS03, PS13, PS23, PS33);
        } else if (uv01 == .5f) {
            splitUpper(P00, P01, P02, P03, PS00, PS01, PS02, PS03);
            splitUpper(P10, P11, P12, P13, PS10, PS11, PS12, PS13);
            splitUpper(P20, P21, P22, P23, PS20, PS21, PS22, PS23);
            splitUpper(P30, P31, P32, P33, PS30, PS31, PS32, PS33);
        } else {
            splitLower(P00, P01, P02, P03, PS00, PS01, PS02, PS03);
            splitLower(P10, P11, P12, P13, PS10, PS11, PS12, PS13);
            splitLower(P20, P21, P22, P23, PS20, PS21, PS22, PS23);
            splitLower(P30, P31, P32, P33, PS30, PS31, PS32, PS33);
        }
        sparam.setValue(0, PS00);
        sparam.setValue(1, PS10);
        sparam.setValue(2, PS20);
        sparam.setValue(3, PS30);
        sparam.setValue(4, PS01);
        sparam.setValue(5, PS11);
        sparam.setValue(6, PS21);
        sparam.setValue(7, PS31);
        sparam.setValue(8, PS02);
        sparam.setValue(9, PS12);
        sparam.setValue(10, PS22);
        sparam.setValue(11, PS32);
        sparam.setValue(12, PS03);
        sparam.setValue(13, PS13);
        sparam.setValue(14, PS23);
        sparam.setValue(15, PS33);
        result.addParameter(sparam);
        return result;
    }

    protected void splitLower(
        Point4f in0,
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

    protected void splitUpper(
        Point4f in0,
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

    protected void dice_P(ShaderVariables sv) {
        extractPoints();
        Point3f[] pdata = (Point3f[]) sv.P.data;
        int nu = Grid.getUSize();
        int nv = Grid.getVSize();
        float uStep = 1f / (nu - 1);
        float vStep = 1f / (nv - 1);
        float fv = 0f;
        int offset = 0;
        for (int v = 0; v < nv; v++) {
            float fu = 0f;
            for (int u = 0; u < nu; u++) {
                Calc.bezierInterpolate(
                    P00,
                    P10,
                    P20,
                    P30,
                    P01,
                    P11,
                    P21,
                    P31,
                    P02,
                    P12,
                    P22,
                    P32,
                    P03,
                    P13,
                    P23,
                    P33,
                    fu,
                    fv,
                    PS00);
                P3F.x = PS00.x / PS00.w;
                P3F.y = PS00.y / PS00.w;
                P3F.z = PS00.z / PS00.w;
                pdata[offset + u].set(P3F);
                fu += uStep;
            }
            fv += vStep;
            offset += nu;
        }
    }

    protected void dice_Ng(ShaderVariables sv) {
        sv.Ng.cross(sv.dPdu, sv.dPdv);
    }

}
