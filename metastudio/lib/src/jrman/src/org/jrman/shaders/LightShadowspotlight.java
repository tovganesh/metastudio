/*
 LightShadowspotlight.java
 Copyright (C) 2003 Gerardo Horvilleur Martinez

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

package org.jrman.shaders;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jrman.attributes.Space;
import org.jrman.geom.Transform;
import org.jrman.grid.Color3fGrid;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.parameters.UniformScalarString;
import org.jrman.parameters.UniformScalarTuple3f;
import org.jrman.parser.Global;
import org.jrman.render.ShaderVariables;

public class LightShadowspotlight extends LightShader {

    static FloatGrid fg1 = new FloatGrid();

    static FloatGrid fg2 = new FloatGrid();

    static FloatGrid fg3 = new FloatGrid();
    
    static Color3fGrid cg1 = new Color3fGrid();

    private static Vector3f vtmp = new Vector3f();

    private static Point3f from = new Point3f();

    private static Point3f to = new Point3f();

    static Color3f lightcolor = new Color3f();

    static Point3fGrid pg = new Point3fGrid();
    
    static FloatGrid fg = new FloatGrid();
    
    static Color3fGrid cg = new Color3fGrid();

    private ShadowSpotLightStatement statement = new ShadowSpotLightStatement();

    private static class ShadowSpotLightStatement extends Statement {
        
        private float intensity;
        
        private float coneangle;
        
        private float conedeltaangle;
        
        private float beamdistribution;
        
        private Vector3f A;
        
        private float bias;
        
        private String shadowmap;
        
        private float blur;
        
        private float samples;
        
        public void setIntensity(float intensity) {
            this.intensity = intensity;
        }
        
        public void setA(Vector3f a) {
            A = a;
        }
        public void setBeamdistribution(float beamdistribution) {
            this.beamdistribution = beamdistribution;
        }
        public void setConeangle(float coneangle) {
            this.coneangle = coneangle;
        }
        public void setConedeltaangle(float conedeltaangle) {
            this.conedeltaangle = conedeltaangle;
        }
        
        public void setBias(float bias) {
            this.bias = bias;
        }
        public void setBlur(float blur) {
            this.blur = blur;
        }
        public void setSamples(float samples) {
            this.samples = samples;
        }
        public void setShadowmap(String shadowmap) {
            this.shadowmap = shadowmap;
        }

        public void execute(ShaderVariables sv) {
            fg1.dot(sv.L, A);
            fg2.length(sv.L);
            fg1.div(fg1, fg2);
            FloatGrid cosangle = fg1;
            fg2.dot(sv.L, sv.L);
            fg3.pow(cosangle, beamdistribution);
            fg3.div(fg3, fg2);
            FloatGrid atten = fg3;
            fg2.smoothstep(
                (float) Math.cos(coneangle),
                (float) Math.cos(coneangle - conedeltaangle),
                cosangle);
            atten.mul(atten, fg2);
            fg2.mul(atten, intensity);
            cg1.set(fg2);
            sv.Cl.set(lightcolor, tmpCond1);
            sv.Cl.mul(sv.Cl, cg1, tmpCond1);
            if (!shadowmap.equals("")) {
               pg.transform(sv.P, sv.cameraToWorld);
               fg.shadow(shadowmap, pg, bias, samples, blur);
               fg.sub(1f, fg);
               cg.set(fg);
               sv.Cl.mul(sv.Cl, cg);
           }
        }
        
    }

    protected void initDefaults() {
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("intensity", "uniform float"), 1f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(
                new Declaration("lightcolor", "uniform color"),
                1f,
                1f,
                1f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(new Declaration("from", "uniform point"), 0f, 0f, 0f));
        defaultParameters.addParameter(
            new UniformScalarTuple3f(new Declaration("to", "uniform point"), 0f, 0f, 1f));
        defaultParameters.addParameter(
            new UniformScalarFloat(
                new Declaration("coneangle", "uniform float"),
                (float) Math.toRadians(30)));
        defaultParameters.addParameter(
            new UniformScalarFloat(
                new Declaration("conedeltaangle", "uniform float"),
                (float) Math.toRadians(5)));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("beamdistribution", "uniform float"), 2f));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("bias", "uniform float"), .1f));
        defaultParameters.addParameter(
            new UniformScalarString(new Declaration("shadowmap", "string"), ""));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("samples", "uniform float"), 16f));
        defaultParameters.addParameter(
            new UniformScalarFloat(new Declaration("blur", "uniform float"), 0f));
    }

    public boolean shade(
        ShaderVariables sv,
        Point3fGrid P,
        Vector3fGrid N,
        float angle) {
        super.shade(sv, P, N, angle);
        UniformScalarFloat paramIntensity = (UniformScalarFloat) getParameter(sv, "intensity");
        float intensity = paramIntensity.getValue();
        UniformScalarTuple3f paramLightcolor =
            (UniformScalarTuple3f) getParameter(sv, "lightcolor");
        paramLightcolor.getValue(lightcolor);
        Transform shaderTransform = attributes.getTransform();
        if (attributes.getSpace() == Space.WORLD)
            shaderTransform = Global.getTransform("camera").concat(shaderTransform);
        UniformScalarTuple3f paramFrom = (UniformScalarTuple3f) getParameter(sv, "from");
        paramFrom.getValue(from);
        shaderTransform.transformPoint(from, from);
        UniformScalarTuple3f paramTo = (UniformScalarTuple3f) getParameter(sv, "to");
        paramTo.getValue(to);
        shaderTransform.transformPoint(to, to);
        UniformScalarFloat paramConeangle = (UniformScalarFloat) getParameter(sv, "coneangle");
        float coneangle = paramConeangle.getValue();
        UniformScalarFloat paramConedeltaangle =
            (UniformScalarFloat) getParameter(sv, "conedeltaangle");
        float conedeltaangle = paramConedeltaangle.getValue();
        UniformScalarFloat paramBeamdistribution =
            (UniformScalarFloat) getParameter(sv, "beamdistribution");
        float beamdistribution = paramBeamdistribution.getValue();
        UniformScalarFloat paramBias = (UniformScalarFloat) getParameter(sv, "bias");
        float bias = paramBias.getValue();
        UniformScalarString paramShadowmap =
            (UniformScalarString) getParameter(sv, "shadowmap");
        String shadowmap = paramShadowmap.getValue();
        UniformScalarFloat paramSamples = (UniformScalarFloat) getParameter(sv, "samples");
        float samples = paramSamples.getValue();
        UniformScalarFloat paramBlur = (UniformScalarFloat) getParameter(sv, "blur");
        float blur = paramBlur.getValue();
        vtmp.sub(to, from);
        Vector3f A = vtmp;
        A.normalize();
        statement.setIntensity(intensity);
        statement.setConeangle(coneangle);
        statement.setConedeltaangle(conedeltaangle);
        statement.setBeamdistribution(beamdistribution);
        statement.setA(A);
        statement.setBias(bias);
        statement.setShadowmap(shadowmap);
        statement.setSamples(samples);
        statement.setBlur(blur);
        return illuminate(sv, P, N, angle, from, A, coneangle, statement);
    }
}
