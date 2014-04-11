/*
  Nurbs.java
  Copyright (C) 2004-2006 Gerardo Horvilleur Martinez, Elmer Garduno Hernandez
  
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

import java.util.Arrays;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.jrman.attributes.Attributes;
import org.jrman.attributes.TextureCoordinates;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.ConvexHull3f;
import org.jrman.grid.Grid;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.Parameter;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingArrayFloat;
import org.jrman.parameters.VaryingScalarFloat;
import org.jrman.parameters.VaryingScalarHPoint;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.parser.Global;
import org.jrman.render.ShaderVariables;
import org.jrman.util.Calc;

public class NurbsRI extends Primitive {

    private final int upc;

    private final int uorder;

    private final float[] uknot;

    private final float umin;

    private final float umax;

    private final int vpc;

    private final int vorder;

    private final float[] vknot;

    private final float vmin;

    private final float vmax;

    private final boolean dirflag;

    private final Point4f[][] points;

    private final static Declaration DECL_PW =
        new Declaration("Pw",
                        Declaration.StorageClass.VERTEX,
                        Declaration.Type.HPOINT, 1);

    private static Point4f result = new Point4f();
    
    private static Point3f p = new Point3f();
    
    public NurbsRI(int upc, int uorder, float[] uknot, float umin, float umax,
                   int vpc, int vorder, float[] vknot, float vmin, float vmax, 
                   boolean dirflag,
                   ParameterList parameters, Attributes attributes) {    
        this.parameters = parameters;
        this.attributes = attributes;
        Parameter parameter = parameters.getParameter("u");
        if (parameter == null) {
            float[] u = new float[4];
            u[0] = umin;
            u[1] = umax;
            u[2] = umin;
            u[3] = umax;
            parameters.addParameter(new VaryingScalarFloat(U_DECL, u));
        }
        parameter = parameters.getParameter("v");
        if (parameter == null) {
            float[] v = new float[4];
            v[0] = vmin;
            v[1] = vmin;
            v[2] = vmax;
            v[3] = vmax;
            parameters.addParameter(new VaryingScalarFloat(V_DECL, v));
        }
        parameter = parameters.getParameter("s");
        if (parameter == null) {
            VaryingArrayFloat stParam =
                (VaryingArrayFloat) parameters.getParameter("st");
            if (stParam != null) {
                parameters.addParameter
                    (stParam.extract(Global.getDeclaration("s"), 0));
            } else {
                float[] s = new float[4];
                TextureCoordinates tc = attributes.getTextureCoordinates();
                if (tc != null)  {
                    s[0] = tc.getS1();
                    s[1] = tc.getS2();
                    s[2] = tc.getS3();
                    s[3] = tc.getS4();
                } else {
                    s[0] = umin;
                    s[1] = umax;
                    s[2] = umin;
                    s[3] = umax;
                }
                parameters.addParameter
                    (new VaryingScalarFloat(Global.getDeclaration("s"), s));
            }
        }
        parameter = parameters.getParameter("t");
        if (parameter == null) {
            VaryingArrayFloat stParam =
                (VaryingArrayFloat) parameters.getParameter("st");
            if (stParam != null) {
                parameters.addParameter
                    (stParam.extract(Global.getDeclaration("t"), 1));
            } else {
                float[] t = new float[4];
                TextureCoordinates tc = attributes.getTextureCoordinates();
                if (tc != null) {
                    t[0] = tc.getT1();
                    t[1] = tc.getT2();
                    t[2] = tc.getT3();
                    t[3] = tc.getT4();
                } else  {
                    t[0] = vmin;
                    t[1] = vmin;
                    t[2] = vmax;
                    t[3] = vmax;
                }
                parameters.addParameter
                    (new VaryingScalarFloat(Global.getDeclaration("t"), t));
            }
        }
        parameters.removeParameter("st");
        this.upc = upc;
        this.uorder = uorder;
        this.uknot = uknot;
        this.umin = umin;
        this.umax = umax;
        this.vpc = vpc;
        this.vorder = vorder;
        this.vknot = vknot;
        this.vmin = vmin;
        this.vmax = vmax;
        this.dirflag = dirflag;
        this.points = extractPoints(parameters);
    }

    private Point4f[][] extractPoints(ParameterList parameters) {
        VaryingScalarTuple3f param3f =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        if (param3f != null) {
            parameters.removeParameter("P");
            parameters.addParameter(new VaryingScalarHPoint
                                    (new Declaration("Pw", "vertex hpoint"), 
                                     param3f));
        }
        VaryingScalarHPoint paramHp =
            (VaryingScalarHPoint) parameters.getParameter("Pw");
        Point4f[][] points = new Point4f[vpc][upc];
        for (int i = 0; i < vpc; i++) {
            for (int j = 0; j < upc; j++) {
                Point4f p = new Point4f();
                paramHp.getValue((i * upc) + j, p);
                points[i][j] = p;
            }
        }
        return points;
    }
            
    public BoundingVolume getBoundingVolume() {
        ConvexHull3f ch = new ConvexHull3f();
        for (int i = 0; i < vpc; i++) {
            for (int j = 0; j < upc; j++) {
                ch.addHpoint(points[i][j]);
            }
        }
        return ch;
    }
    
    public Primitive[] split() {
        NurbsRI[] result = new NurbsRI[2];
        
        float[] knot = dirflag ? uknot : vknot;
        int order = dirflag ? uorder : vorder;
        SplitArgs sa = getKnotVector(knot);
        
        Point4f[][] npoints = dirflag ?
            getPointsU(sa.kv, uorder, knot, points) :
            getPointsV(sa.kv, vorder, knot, points) ;
        
        float[] kv0 = new float[sa.pos + order];
        System.arraycopy(sa.kv, 0, kv0, 0, kv0.length);

        float[] kv1 = new float[sa.kv.length - sa.pos];
        System.arraycopy(sa.kv, sa.pos, kv1, 0, kv1.length);
        
        if (dirflag) {
            float splitKnot = kv1[0];
            float mid = (splitKnot - kv0[0]) / 
                (kv1[kv1.length -1] - kv0[0]); 
            int p0c = kv0.length - uorder;
            Point4f[][] points0 = new Point4f[vpc][p0c];
            for (int i = 0; i < vpc; i++) {
                for (int j = 0; j < p0c; j++) {
                    points0[i][j] = npoints[i][j];
                }
            }
            result[0] = new NurbsRI(p0c, uorder, kv0, umin, splitKnot,
                                    vpc, vorder, vknot, vmin, vmax, !dirflag,
                                    nuInterpolateParameters
                                    (0f, mid, 0f, 1f, points0), 
                                    attributes);
            int p1c = kv1.length - uorder;
            Point4f[][] points1 = new Point4f[vpc][p1c];
            for (int i = 0; i < vpc; i++) {
                for (int j = 0; j < p1c; j++) {
                    points1[i][j] = npoints[i][j + p0c];
                }
            }
            result[1] = new NurbsRI(p1c, uorder, kv1, splitKnot, umax,
                                    vpc, vorder, vknot, vmin, vmax, !dirflag,
                                    nuInterpolateParameters
                                    (mid, 1f, 0f, 1f, points1), 
                                    attributes);
        } else {

            float splitKnot = kv1[0];
            float mid = (splitKnot - kv0[0]) / 
                (kv1[kv1.length -1] - kv0[0]); 
            int p0c = kv0.length - vorder;
            Point4f[][] points0 = new Point4f[p0c][upc];
            for (int i = 0; i < upc; i++) {
                for (int j = 0; j < p0c; j++) {
                    points0[j][i] = npoints[j][i];
                }
            }
            result[0] = new NurbsRI(upc, uorder, uknot, umin, umax,
                                    p0c, vorder, kv0, vmin, splitKnot, !dirflag,
                                    nuInterpolateParameters
                                    (0f, 1f, 0f, mid, points0), 
                                    attributes);
            
            int p1c = kv1.length - vorder;
            Point4f[][] points1 = new Point4f[p1c][upc];
            for (int i = 0; i < upc; i++) {
                for (int j = 0; j < p1c; j++) {
                    points1[j][i] = npoints[j + p0c][i];
                }
            }
            result[1] = new NurbsRI(upc, uorder, uknot, umin, umax,
                                    p1c, vorder, kv1, splitKnot, vmax, !dirflag,
                                    nuInterpolateParameters
                                    (0f, 1f, mid, 1f, points1), 
                                    attributes);
        }
                
        return result;
    }    
    
      
    private ParameterList nuInterpolateParameters(float umin,
                                                  float umax,
                                                  float vmin,
                                                  float vmax,
                                                  Point4f[][] points) {
        ParameterList result = 
            bilinearInterpolateParameters(umin, umax, vmin, vmax);
        int vpc = points.length;
        int upc = points[0].length;

        VaryingScalarHPoint sparam =
            new VaryingScalarHPoint(DECL_PW, new float[vpc * upc * 4]);
        
        for (int i = 0; i < vpc; i++) {
            for (int j = 0; j < upc; j++) {
                sparam.setValue((i * upc) + j, points[i][j]);
            }
        }
        result.addParameter(sparam);
        return result;
    } 
    
    private SplitArgs getKnotVector(float[] v) {
        float mid = (v[v.length - 1] + v[0]) / 2;
        int count = 0;
        int pos = 0;
        for (int i =0; i < v.length; i++) {
            if (v[i] == mid) {
                count ++;
            } else if (v[i] > mid){
                pos = i;
                break;
            }
        }
        int add = uorder - count;
        float[] kv = new float[v.length + add];
        for(int i = 0; i < pos; i++) {
            kv[i] = v[i];
        }
        for(int i = 0; i < add; i++) {
            kv[i + pos] = mid;
        }
        for(int i = 0; i < v.length - pos; i++) {
            kv[i + pos + add] = v[i + pos];
        }       
        return new SplitArgs(kv, pos - count);
    }

    private final static class SplitArgs {
        final float[] kv;
     
        final int pos;

        public SplitArgs(float[] kv, int pos) {
            this.kv = kv;
            this.pos = pos;
        }
    }

    private Point4f[][] getPointsU(float[] nv, int k, float[] v, 
                                   Point4f[][] points) {
        Point4f[][] result = new Point4f[vpc][nv.length - k];
        for (int pos = 0; pos < vpc; pos++) {
            for (int j = 0; j < nv.length - k; j++) {
                Point4f point = new Point4f();
                for (int i = 0; i < v.length - k; i++) {
                    float alpha = getAlpha(nv, k, i, j, v);
                    point.x += alpha * points[pos][i].x;
                    point.y += alpha * points[pos][i].y;
                    point.z += alpha * points[pos][i].z;
                    point.w += alpha * points[pos][i].w;
                }
                result[pos][j] = point;
            }
        }
        return result;
    }

    private Point4f[][] getPointsV(float[] nv, int k, float[] v, 
                                   Point4f[][] points) {
        Point4f[][] result = new Point4f[nv.length - k][upc];
        for (int pos = 0; pos < upc; pos++) {
            for (int j = 0; j < nv.length - k; j++) {
                Point4f point = new Point4f();
                for (int i = 0; i < v.length - k; i++) {
                    float alpha = getAlpha(nv, k, i, j, v);
                    point.x += alpha * points[i][pos].x;
                    point.y += alpha * points[i][pos].y;
                    point.z += alpha * points[i][pos].z;
                    point.w += alpha * points[i][pos].w;
                }
                result[j][pos] = point;
            }
        }
        return result;
    }

    
    private float getAlpha(float[] nv, int k, int i, int j, float[] v) {
        if (k == 1) {
            return (nv[j] >= v[i] && nv[j] < v[i + 1]) ? 1 : 0;
        } else {
            float aa = ratio(nv[j + k - 1] - v[i], v[i + k -1] - v[i]) * 
                getAlpha(nv, k - 1, i, j, v) +
                ratio(v[i + k] - nv[j + k - 1], v[i + k] - v[i + 1]) * 
                getAlpha(nv, k - 1, i + 1, j, v);
            return aa;
        }            
    } 

    protected void dice_P(ShaderVariables sv) {
        Point3fGrid P = sv.P;
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        for (int v = 0; v < vSize; v++) {
            float vPos = (v / (float) (vSize - 1))
                * (vknot[vpc] - vknot[vorder - 1])
                + vknot[vorder - 1];
            for (int u = 0; u < uSize; u++) {
                float uPos = ( u /(float) (uSize - 1))
                    * (uknot[upc] - uknot[uorder - 1])
                    + uknot[uorder - 1];
                eval(uPos, vPos, p);
                P.set(u, v, p);
            }
        }
    }

    protected void dice_Ng(ShaderVariables sv) {
        sv.Ng.cross(sv.dPdu, sv.dPdv);
    }

     /*
     * Compute Bi,k(u), for i = 0..k.
     *  u           is the parameter of the spline to find the basis 
     *              functions for
     *  brkPoint    is the start of the knot interval ("segment")
     *  kv          is the knot vector
     *  k           is the order of the curve
     *  bvals       is the array of returned basis values.
     *
     * (From Bartels, Beatty & Barsky, p.387)
     */

    private float[] basisFunctions(float u, int brkPoint, float[] kv, int k) {
        float[] bvals = new float[k];
        bvals[0] = 1.0f;
        for (int r = 2; r <= k; r++) {
            int i = brkPoint - r + 1;
            bvals[r - 1] = 0.0f;
            for (int s = r - 2; s >= 0; s--) {
                i++;
                float omega;
                if (i < 0)
                    omega = 0;
                else {
                    int ir1 = Calc.clamp(i + r - 1, 0, kv.length - 1);
                    int ic = Calc.clamp(i, 0, kv.length - 1);
                    omega = (u - kv[ic]) / (kv[ir1] - kv[ic]);
                }
                bvals[s + 1] = bvals[s + 1] + (1 - omega) * bvals[s];
                bvals[s] = omega * bvals[s];
            }
        }
        return bvals;
    }
    
    /*
     * Return the current knot the parameter u is less than or equal to. Find
     * this "breakpoint" allows the evaluation routines to concentrate on only
     * those control points actually effecting the curve around u.]
     * 
     * m is the number of points on the curve (or surface direction) k is the
     * order of the curve (or surface direction) kv is the knot vector
     * ([0..m+k-1]) to find the break point in.
     */

    private int findBreakPoint(float u, float[] kv, int m, int k) {
        if (u == kv[m + 1]) /* Special case for closed interval */
            return m;
        int i = m + k;
        while ((u < kv[i]) && (i > 0))
            i--;
        return i;
    }
    
    private void eval(float u, float v, Point3f p) {  
        int ubrkPoint = findBreakPoint(u, uknot, upc - 1, uorder);
        int ufirst = ubrkPoint - uorder + 1;      
        float[] bu = basisFunctions(u, ubrkPoint, uknot, uorder);
        
        int vbrkPoint = findBreakPoint(v, vknot, vpc - 1, vorder);
        int vfirst = vbrkPoint - vorder + 1;
        float[] bv = basisFunctions(v, vbrkPoint, vknot, vorder);
        
        result.x = 0;
        result.y = 0;
        result.z = 0;
        result.w = 0;

        for (int i = 0; i < vorder; i++) {
            for (int j = 0; j < uorder; j++) {
                int ri = vorder - 1 - i;
                int rj = uorder - 1 - j;

                float tmp = bu[rj] * bv[ri];
                int ivfirst = Calc.clamp(i + vfirst, 0, points.length - 1);
                int jufirst = Calc.clamp(j + ufirst, 0, points[0].length - 1);
                Point4f cp = points[ivfirst][jufirst];
                result.x += cp.x * tmp;
                result.y += cp.y * tmp;
                result.z += cp.z * tmp;
                result.w += cp.w * tmp;  
            }
        }

        p.x = result.x / result.w;
        p.y = result.y / result.w;
        p.z = result.z / result.w;
    }
    
    private static float ratio(float a, float b) {
        return b != 0f ? a / b : 0f;
    }
}
