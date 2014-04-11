/*
 GeometricApproximation.java
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

package org.jrman.attributes;

import java.util.HashMap;
import java.util.Map;

public class GeometricApproximation {

    private GeometricApproximation.Type type;

    private float value;

    public static class Type {

        public static final GeometricApproximation.Type FLATNESS =
            new GeometricApproximation.Type("flatness");

        private final static Map<String, Type> map = new HashMap<String, Type>();

        static {
            Type.map.put("flatness", Type.FLATNESS);
        }

        private String name;

        private Type(String name) {
            this.name = name;
        }

        public static GeometricApproximation.Type getNamed(String name) {
            GeometricApproximation.Type result =
                (GeometricApproximation.Type) Type.map.get(name);
            if (result == null)
                throw new IllegalArgumentException("No such approximation type: " + name);
            return result;
        }

        public String toString() {
            return name;
        }

    }

    public GeometricApproximation(GeometricApproximation.Type type, float value) {
        this.type = type;
        this.value = value;
    }

    public GeometricApproximation(String type, float value) {
        this.type = GeometricApproximation.Type.getNamed(type);
        this.value = value;
    }

    public GeometricApproximation.Type getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

}