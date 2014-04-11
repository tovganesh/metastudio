/*
 * SurfaceShader.java Copyright (C) 2003 Gerardo Horvilleur Martinez
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jrman.shaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.vecmath.Color3f;

import org.jrman.attributes.Attributes;
import org.jrman.grid.Color3fGrid;
import org.jrman.grid.FloatGrid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Vector3fGrid;
import org.jrman.parameters.ParameterList;
import org.jrman.render.ShaderVariables;

public abstract class SurfaceShader extends Shader {

    private final static Color3f BLACK = new Color3f();

    private final static Color3f WHITE = new Color3f(1f, 1f, 1f);

    private final static Color3f OPAQUE = WHITE;

    private static Pattern SEPARATOR = Pattern.compile(", *");

    private static boolean betterHighlights = false;
    
    static FloatGrid _fg1 = new FloatGrid();

    static FloatGrid _fg2 = new FloatGrid();

    static FloatGrid _fg3 = new FloatGrid();

    static Vector3fGrid _vg1 = new Vector3fGrid();

    static Color3fGrid _cg1 = new Color3fGrid();

    static Map<String, Class> shaderClassCache = new HashMap<String, Class>();

    protected static abstract class Statement {
        public abstract void execute(ShaderVariables sv, LightShader ls);
    }

    private DiffuseStatement diffuseStatement = new DiffuseStatement();

    private SpecularStatement specularStatement = new SpecularStatement();

    private static class DiffuseStatement extends Statement {

        private Vector3fGrid Nn;

        private Color3fGrid out;

        public void setNn(Vector3fGrid nn) {
            Nn = nn;
        }

        public void setOut(Color3fGrid out) {
            this.out = out;
        }

        public void execute(ShaderVariables sv, LightShader ls) {
            FloatGrid nondiff = _fg1;
            nondiff.set(0f);
            ls.messagePassing("__nondiffuse", nondiff);
            _vg1.normalize(sv.L);
            _fg2.dot(Nn, _vg1);
            nondiff.sub(1f, nondiff);
            _fg2.mul(_fg2, nondiff);
            _cg1.set(_fg2);
            sv.Cl.mul(sv.Cl, _cg1);
            out.add(out, sv.Cl);
        }

    }

    private static class SpecularStatement extends Statement {

        private Vector3fGrid Nn;

        private Vector3fGrid V;

        private float roughness;

        private Color3fGrid out;

        public void setNn(Vector3fGrid nn) {
            Nn = nn;
        }

        private void setV(Vector3fGrid V) {
            this.V = V;
        }

        private void setRoughness(float roughness) {
            this.roughness = roughness;
        }

        public void setOut(Color3fGrid out) {
            this.out = out;
        }

        public void execute(ShaderVariables sv, LightShader ls) {
            FloatGrid nonspec = _fg1;
            nonspec.set(0f);
            ls.messagePassing("__nonspecular", nonspec);
            _vg1.normalize(sv.L);
            _vg1.add(_vg1, V);
            _vg1.normalize(_vg1);
            Vector3fGrid H = _vg1;
            _fg2.dot(Nn, H);
            _fg2.max(_fg2, 0f);
            if (betterHighlights)
                _fg2.pow(_fg2, 10f / roughness);
            else
                _fg2.simulPow(_fg2, 25f / roughness);
            nonspec.sub(1, nonspec);
            _fg2.mul(_fg2, nonspec);
            _cg1.set(_fg2);
            sv.Cl.mul(sv.Cl, _cg1);
            out.add(out, sv.Cl);
        }
    }

    public static SurfaceShader createShader(String name,
            ParameterList parameters, Attributes attributes) {
        Class clazz = (Class) shaderClassCache.get(name);
        if (clazz == null) {
            String className = "Surface" + name.substring(0, 1).toUpperCase()
                    + name.substring(1).toLowerCase();
            if (className.equals("SurfaceConstant"))
                clazz = SurfaceConstant.class;
            else if (className.equals("SurfaceMatte"))
                clazz = SurfaceMatte.class;
            else if (className.equals("SurfaceMetal")
                    || className.equals("SurfaceShinymetal"))
                clazz = SurfaceMetal.class;
            else if (className.equals("SurfacePlastic"))
                clazz = SurfacePlastic.class;
            else if (className.equals("SurfacePaintedplastic"))
                clazz = SurfacePaintedplastic.class;
            else if (className.equals("SurfaceReflectivepaintedplastic"))
                clazz = SurfaceReflectivepaintedplastic.class;
            else if (className.equals("SurfaceFakedlight"))
                clazz = SurfaceFakedlight.class;
            else
                try {
                    clazz = Class.forName(className);
                } catch (Exception e) {
                    System.out.println("Unknown surface shader: " + name
                            + ", replacing with fakedlight surface shader");
                    clazz = SurfaceFakedlight.class;
                }
            shaderClassCache.put(name, clazz);
        }
        SurfaceShader result;
        try {
            result = (SurfaceShader) clazz.newInstance();

        } catch (Exception e) {
            throw new RuntimeException("Couldn't create instance of shader: "
                    + name);
        }
        result.init(name, parameters, attributes);
        return result;
    }

    public static void setBetterHighlights(boolean value) {
        betterHighlights = value;
    }

    protected void ambient(ShaderVariables sv, Color3fGrid out) {
        out.set(BLACK);
        LightShader[] lights = sv.attributes.getLightSourcesArray();
        for (int i = 0; i < lights.length; i++) {
            LightShader ls = lights[i];
            if (ls.isAmbient()) {
                ls.shade(sv, null, null, 0);
                out.add(out, sv.Cl);
            }
        }
    }

    protected void diffuse(ShaderVariables sv, Vector3fGrid Nn, Color3fGrid out) {
        out.set(BLACK);
        diffuseStatement.setNn(Nn);
        diffuseStatement.setOut(out);
        illuminance(sv, null, sv.P, Nn, (float) Math.PI / 2, diffuseStatement);
    }

    protected void specular(ShaderVariables sv, Vector3fGrid Nn,
            Vector3fGrid V, float roughness, Color3fGrid out) {
        out.set(BLACK);
        specularStatement.setNn(Nn);
        specularStatement.setV(V);
        specularStatement.setRoughness(roughness);
        specularStatement.setOut(out);
        illuminance(sv, null, sv.P, Nn, (float) Math.PI / 2, specularStatement);
    }

    protected void doIlluminance(ShaderVariables sv, LightShader[] lights,
            Point3fGrid P, Vector3fGrid N, float angle, Statement statement) {
        for (int i = 0; i < lights.length; i++) {
            LightShader ls = lights[i];
            if (ls.isAmbient())
                continue;
            if (ls.shade(sv, P, N, angle))
                statement.execute(sv, ls);
        }
    }

    protected void illuminance(ShaderVariables sv, String categoryList,
            Point3fGrid P, Vector3fGrid N, float angle, Statement statement) {
        LightShader[] selectedLights;
        if (categoryList == null)
            selectedLights = sv.attributes.getLightSourcesArray();
        else
            selectedLights = getLightsByCategory(sv, categoryList);
        doIlluminance(sv, selectedLights, P, N, angle, statement);
    }

    protected LightShader[] getLightsByCategory(ShaderVariables sv,
            String categoryList) {
        boolean inverse = false;
        if (categoryList.startsWith("-")) {
            categoryList = categoryList.substring(1);
            inverse = true;
        }
        Set<String> categories = new TreeSet<String>();
        categories.addAll(Arrays.asList(SEPARATOR.split(categoryList)));
        List<LightShader> result = new ArrayList<LightShader>();
        LightShader[] lights = sv.attributes.getLightSourcesArray();
        for (int i = 0; i < lights.length; i++) {
            LightShader ls = lights[i];
            if (ls.isAmbient())
                continue;
            /*
               * MUST FIX THIS!!!! Map param = ls.getParameters(); String
               * __category = (String) param.get("__category"); if (inverse) {
               * if (__category == null || !categories.contains(__category))
               * result.add(ls); } else { if (__category != null &&
               * categories.contains(__category)) result.add(ls); }
               */
        }
        return (LightShader[]) result.toArray(new LightShader[result.size()]);
    }

    public void shade(ShaderVariables sv) {
        sv.Ci.set(WHITE);
        sv.Oi.set(OPAQUE);
    }

}