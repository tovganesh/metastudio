/*
 Global.java
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

package org.jrman.parser;

import java.util.HashMap;
import java.util.Map;

import org.jrman.geom.Transform;
import org.jrman.parameters.*;

public class Global {

    private static Map<String, Transform> transforms = new HashMap<String, Transform>();

    private static Map<String, Declaration> declarations = new HashMap<String, Declaration>();

    static {
        declarations.put("fov", new Declaration("fov", "float"));
        declarations.put("origin", new Declaration("origin", "integer[2]"));
        declarations.put("gridsize", new Declaration("gridsize", "integer"));
        declarations.put("bucketsize",
                         new Declaration("bucketsize", "integer[2]"));
        declarations.put("endofframe",
                         new Declaration("endofframe", "integer"));
        declarations.put("file", new Declaration("file", "integer"));
        declarations.put("highlights",
                         new Declaration("highlights", "integer"));
        declarations.put("intensity", new Declaration("intensity", "float"));
        declarations.put("lightcolor", new Declaration("lightcolor", "color"));
        declarations.put("from", new Declaration("from", "point"));
        declarations.put("to", new Declaration("to", "point"));
        declarations.put("coneangle", new Declaration("coneangle", "float"));
        declarations.put("conedeltaangle",
                         new Declaration("conedeltaangle", "float"));
        declarations.put("beamdistribution",
                         new Declaration("beamdistribution", "float"));
        declarations.put("Ka", new Declaration("Ka", "float"));
        declarations.put("Kd", new Declaration("Kd", "float"));
        declarations.put("Ks", new Declaration("Ks", "float"));
        declarations.put("Kr", new Declaration("Kr", "float"));
        declarations.put("Km", new Declaration("Km", "float"));
        declarations.put("roughness", new Declaration("roughness", "float"));
        declarations.put("specularcolor",
                         new Declaration("specularcolor", "color"));
        declarations.put("texturename",
                         new Declaration("texturename", "string"));
        declarations.put("amplitude", new Declaration("float", "float"));
        declarations.put("distance", new Declaration("distance", "float"));
        declarations.put("mindistance",
                         new Declaration("mindistance", "float"));
        declarations.put("maxdistance",
                         new Declaration("maxdistance", "float"));
        declarations.put("background",
                         new Declaration("background", "color"));
        declarations.put("bgcolor",
                         new Declaration("bgcolor", "color"));
        declarations.put("sphere",
                         new Declaration("sphere", "float"));
        declarations.put("coordinatesystem",
                         new Declaration("coordinatesystem", "string"));
        declarations.put("name", new Declaration("name", "string"));
        declarations.put("sense", new Declaration("name", "string"));
        declarations.put("P", new Declaration("P", "vertex point"));
        declarations.put("Pz", new Declaration("Pz", "vertex float"));
        declarations.put("Pw", new Declaration("Pw", "vertex hpoint"));
        declarations.put("N", new Declaration("N", "varying normal"));
        declarations.put("Cs", new Declaration("Cs", "varying color"));
        declarations.put("Os", new Declaration("Os", "varying color"));
        declarations.put("s", new Declaration("s", "varying float"));
        declarations.put("t", new Declaration("t", "varying float"));
        declarations.put("st", new Declaration("st", "varying float[2]"));
        declarations.put("width", new Declaration("width", "varying float"));
        declarations.put("constantwidth",
                         new Declaration("constantwidth", "uniform float"));
        declarations.put("__category",
                         new Declaration("__category", "uniform string"));
        declarations.put("__nondiffuse",
                         new Declaration("__nondiffuse", "uniform float"));
        declarations.put("__nonspecular",
                         new Declaration("__nonspecular", "uniform float"));
        declarations.put("truedisplacement",
                         new Declaration("truedisplacement", "integer"));
    }
    
    private Global() {
    }

    public static void setTransform(String name, Transform transform) {
        transforms.put(name, transform);
    }

    public static Transform getTransform(String name) {
        Transform result = (Transform) transforms.get(name);
        if (result == null)
            throw new IllegalArgumentException("no such coordinate system: " + name);
        return result;
    }

    public static void setDeclaration(String name, String decl) {
        declarations.put(name, new Declaration(name, decl));
    }

    public static Declaration getDeclaration(String name) {
        return (Declaration) declarations.get(name);
    }

}
