/*
 Shader.java
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

import org.jrman.attributes.Attributes;
import org.jrman.grid.FloatGrid;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.Parameter;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.render.ShaderVariables;

public class Shader {

    protected String name;

    protected ParameterList parameters;

    protected Attributes attributes;

    protected ParameterList defaultParameters = new ParameterList();

    protected void init(String name, ParameterList parameters, Attributes attributes) {
        this.name = name;
        this.parameters = parameters;
        this.attributes = attributes;
        initDefaults();
    }
    
    protected void initDefaults() {
    }

    protected Parameter getParameter(ShaderVariables sv, String name) {
        Parameter result = sv.parameters.getParameter(name);
        if (result != null)
            return result;
        result = parameters.getParameter(name);
        if (result != null)
            return result;
        return defaultParameters.getParameter(name);
    }

    public String getName() {
        return name;
    }

    public ParameterList getParameters() {
        return parameters;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public float messagePassing(String paramName, Object resultHolder) {
        Parameter param = parameters.getParameter(paramName);
        if (param == null)
            return 0f;
        Declaration declaration = param.getDeclaration();
        if (declaration.getCount() != 1)
            throw new RuntimeException(
                "Can't handle arrays in shader message passing: " + paramName);
        Declaration.Type type = declaration.getType();
        Declaration.StorageClass storageClass = declaration.getStorageClass();
        if (type == Declaration.Type.FLOAT) {
            // float[][] paramValue = (float[][]) param.getData();
            if (resultHolder instanceof float[][]) {
                if (storageClass == Declaration.StorageClass.CONSTANT
                    || storageClass == Declaration.StorageClass.UNIFORM) {
                        float[][]result = (float[][]) resultHolder;
                        result[0][0] = ((UniformScalarFloat) param).getValue();
                } else
                    throw new IllegalArgumentException(
                        "Can't store a varying parameter in a uniform variable: " + paramName);
            } else if (resultHolder instanceof FloatGrid) {
                if (storageClass == Declaration.StorageClass.CONSTANT ||
                storageClass == Declaration.StorageClass.UNIFORM) {
                    FloatGrid result = (FloatGrid) resultHolder;
                    result.set(((UniformScalarFloat) param).getValue()); 
                } else {
                    // TODO handle vertex/varying parameters
                    // What should we do? Why or when would a shader have such a parameter?
                }
            } else
                throw new IllegalArgumentException(
                    "Wrong argument for a float parameter: "
                        + paramName
                        + " -> "
                        + resultHolder);
        } else if (type == Declaration.Type.INTEGER) {
            // TODO implement INTEGER message passing
        } else if (type == Declaration.Type.STRING) {
            // TODO implement STRING message passing
        } else if (type == Declaration.Type.COLOR) {
            // TODO implement COLOR message passing
        } else if (type == Declaration.Type.POINT) {
            // TODO implement POINT message passing
        } else if (type == Declaration.Type.VECTOR) {
            // TODO implement VECTOR message passing
        } else if (type == Declaration.Type.NORMAL) {
            // TODO implement NORMAL message passing
        } else if (type == Declaration.Type.MATRIX) {
            // TODO implement MATRIX message passing
        } else if (type == Declaration.Type.HPOINT) {
            // TODO implement HPOINT message passing
        } else
            throw new RuntimeException("Unknown Declaration type for parameter: " + paramName);
        return 1f;
    }

}
