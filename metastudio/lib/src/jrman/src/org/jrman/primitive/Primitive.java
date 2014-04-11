/*
  Primitive.java
  Copyright (C) 2003, 2004, 2006 Gerardo Horvilleur Martinez
  
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

import javax.vecmath.Color3f;

import org.jrman.attributes.Attributes;
import org.jrman.attributes.TextureCoordinates;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.Bounds2f;
import org.jrman.geom.Transform;
import org.jrman.grid.Grid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.Parameter;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingArrayFloat;
import org.jrman.parameters.VaryingScalarFloat;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.parser.Global;
import org.jrman.render.ShaderVariables;
import org.jrman.util.Calc;

public abstract class Primitive implements Comparable {
    
    protected static Declaration U_DECL = new Declaration("u", "varying float");

    protected static Declaration V_DECL = new Declaration("v", "varying float");

    private static Color3f c0 = new Color3f();

    protected ParameterList parameters;

    protected Attributes attributes;

    protected Bounds2f rasterBounds;

    protected float screenDisplacementWidth;

    protected float screenDisplacementHeight;

    protected float distance;

    protected Transform objectToCamera;

    protected Primitive() {
    }

    protected Primitive(ParameterList parameters, Attributes attributes) {
        this.parameters = parameters;
        this.attributes = attributes;
        if (parameters != null)
            setDefaultParameters(parameters, attributes);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public abstract BoundingVolume getBoundingVolume();

    public abstract Primitive[] split();

    public float getMinGridSize() {
        return 9f;
    }

    public void setRasterBounds(Bounds2f bounds) {
        this.rasterBounds = bounds;
    }

    public Bounds2f getRasterBounds() {
        return rasterBounds;
    }

    public void setScreenDisplacementWidth(float sd) {
        screenDisplacementWidth = sd;
    }

    public float getScreenDisplacementWidth() {
        return screenDisplacementWidth;
    }

    public void setScreenDisplacementHeight(float sd) {
        screenDisplacementHeight = sd;
    }

    public float getScreenDisplacementHeight() {
        return screenDisplacementHeight;
    }

    public void setDistance(float f) {
        distance = f;
    }

    public float getDistance() {
        return distance;
    }
    
    public int compareTo(Object other) {
        Primitive op = (Primitive) other;
        if (distance > op.distance)
            return -1;
        if (distance < op.distance)
            return 1;
        return 0;
    }

    public Transform getObjectToCamera() {
        return objectToCamera;
    }

    public void setObjectToCamera(Transform objectToCamera) {
        this.objectToCamera = objectToCamera;
    }

    public void dice(ShaderVariables shaderVariables) {
        shaderVariables.attributes = attributes;
        shaderVariables.parameters = parameters;
        dice_u(shaderVariables);
        dice_v(shaderVariables);
        dice_s(shaderVariables);
        dice_t(shaderVariables);
        dice_P(shaderVariables);
        dice_du(shaderVariables);
        dice_dv(shaderVariables);
        dice_dPdu(shaderVariables);
        dice_dPdv(shaderVariables);
        dice_Ng(shaderVariables);
        dice_N(shaderVariables);
        dice_Cs(shaderVariables);
        dice_Os(shaderVariables);
        dice_others(shaderVariables);
        shaderVariables.primitiveTransform(objectToCamera);
    }

    protected void dice_P(ShaderVariables shaderVariables) {
    }

    protected void dice_Ng(ShaderVariables shaderVariables) {
    }

    protected void dice_N(ShaderVariables shaderVariables) {
        VaryingScalarTuple3f param =
            (VaryingScalarTuple3f) parameters.getParameter("N");
        if (param != null) {
            if (param.getCount() == 4)
                param.bilinearDice(shaderVariables.N);
            else
                param.linearDice(shaderVariables.N);
        } else
            shaderVariables.N.set(shaderVariables.Ng);
    }

    protected void dice_Cs(ShaderVariables shaderVariables) {
        VaryingScalarTuple3f param =
            (VaryingScalarTuple3f) parameters.getParameter("Cs");
        if (param != null) {
            if (param.getCount() == 4)
                param.bilinearDice(shaderVariables.Cs);
            else if (param.getCount() == 2)
                param.linearDice(shaderVariables.Cs);
            else {
                param.getValue(0, c0);
                shaderVariables.Cs.set(c0);
            }
        } else
            shaderVariables.Cs.set(attributes.getColor());
    }

    protected void dice_Os(ShaderVariables shaderVariables) {
        VaryingScalarTuple3f param =
            (VaryingScalarTuple3f) parameters.getParameter("Os");
        if (param != null) {
            if (param.getCount() == 4)
                param.bilinearDice(shaderVariables.Os);
            else if (param.getCount() == 2)
                param.linearDice(shaderVariables.Os);
            else {
                param.getValue(0, c0);
                shaderVariables.Os.set(c0);
            }
        } else
            shaderVariables.Os.set(attributes.getOpacity());
    }

    protected void dice_u(ShaderVariables shaderVariables) {
        VaryingScalarFloat param =
            (VaryingScalarFloat) parameters.getParameter("u");
        param.bilinearDice(shaderVariables.u);
    }

    protected void dice_v(ShaderVariables shaderVariables) {
        VaryingScalarFloat param =
            (VaryingScalarFloat) parameters.getParameter("v");
        if (param.getCount() == 4)
            param.bilinearDice(shaderVariables.v);
        else
            param.linearDice(shaderVariables.v);
    }

    protected void dice_s(ShaderVariables shaderVariables) {
        VaryingScalarFloat param =
            (VaryingScalarFloat) parameters.getParameter("s");
        param.bilinearDice(shaderVariables.s);
    }

    protected void dice_t(ShaderVariables shaderVariables) {
        VaryingScalarFloat param =
            (VaryingScalarFloat) parameters.getParameter("t");
        if (param.getCount() == 4)
            param.bilinearDice(shaderVariables.t);
        else
            param.linearDice(shaderVariables.t);
    }

    protected void dice_du(ShaderVariables shaderVariables) {
        shaderVariables.du.du(shaderVariables.u);
    }

    protected void dice_dv(ShaderVariables shaderVariables) {
        shaderVariables.dv.dv(shaderVariables.v);
    }

    protected void dice_dPdu(ShaderVariables shaderVariables) {
        shaderVariables.dPdu.Du(shaderVariables.P, shaderVariables.du);
    }

    protected void dice_dPdv(ShaderVariables shaderVariables) {
        shaderVariables.dPdv.Dv(shaderVariables.P, shaderVariables.dv);
    }

    protected void dice_others(ShaderVariables shaderVariables) {
        // TODO Auto-generated method stub

    }

    public static void setDefaultParameters(ParameterList parameters, Attributes attributes) {
        Parameter parameter = parameters.getParameter("u");
        if (parameter == null) {
            float[] u = new float[4];
            u[0] = 0f;
            u[1] = 1f;
            u[2] = 0f;
            u[3] = 1f;
            parameters.addParameter(new VaryingScalarFloat(U_DECL, u));
        }
        parameter = parameters.getParameter("v");
        if (parameter == null) {
            float[] v = new float[4];
            v[0] = 0f;
            v[1] = 0f;
            v[2] = 1f;
            v[3] = 1f;
            parameters.addParameter(new VaryingScalarFloat(V_DECL, v));
        }
        parameter = parameters.getParameter("s");
        if (parameter == null) {
            VaryingArrayFloat stParam =
                (VaryingArrayFloat) parameters.getParameter("st");
            if (stParam != null) {
                parameters.addParameter(stParam.extract(Global.getDeclaration("s"), 0));
            } else {
                float[] s = new float[4];
                TextureCoordinates tc = attributes.getTextureCoordinates();
                if (tc != null)  {
                    s[0] = tc.getS1();
                    s[1] = tc.getS2();
                    s[2] = tc.getS3();
                    s[3] = tc.getS4();
                } else {
                    s[0] = 0;
                    s[1] = 1;
                    s[2] = 0;
                    s[3] = 1;
                }
                parameters.addParameter(new VaryingScalarFloat(Global.getDeclaration("s"), s));
            }
        }
        parameter = parameters.getParameter("t");
        if (parameter == null) {
            VaryingArrayFloat stParam =
                (VaryingArrayFloat) parameters.getParameter("st");
            if (stParam != null) {
                parameters.addParameter(stParam.extract(Global.getDeclaration("t"), 1));
            } else {
                float[] t = new float[4];
                TextureCoordinates tc = attributes.getTextureCoordinates();
                if (tc != null) {
                    t[0] = tc.getT1();
                    t[1] = tc.getT2();
                    t[2] = tc.getT3();
                    t[3] = tc.getT4();
                } else  {
                    t[0] = 0;
                    t[1] = 0;
                    t[2] = 1;
                    t[3] = 1;
                }
                parameters.addParameter(new VaryingScalarFloat(Global.getDeclaration("t"), t));
            }
        }
        parameters.removeParameter("st");
    }

    protected ParameterList linearInterpolateParameters(float min, float max) {
        return parameters.linearInterpolate(min, max);
    }

    protected ParameterList bilinearInterpolateParameters(
        float uMin,
        float uMax,
        float vMin,
        float vMax) {
        return parameters.bilinearInterpolate(uMin, uMax, vMin, vMax);
    }

    public boolean isReadyToBeDiced(int gridSize) {
        float width = rasterBounds.getWidth() - screenDisplacementWidth;
        float height = rasterBounds.getHeight() - screenDisplacementHeight;
        boolean ready = width * height <= gridSize * attributes.getShadingRate();
        if (!ready)
            return false;
        float idealSize = (width * height) / attributes.getShadingRate();
        idealSize = Calc.clamp(idealSize, getMinGridSize(), gridSize);
        int side = (int) (Math.ceil(Math.sqrt(idealSize))) + 1;
        Grid.setSize(side, side);
        return true;
    }

    public boolean shouldSortBucket() {
        return true;
    }

}
