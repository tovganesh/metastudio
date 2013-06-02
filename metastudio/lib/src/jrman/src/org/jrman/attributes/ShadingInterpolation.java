/*
 ShadingInterpolation.java
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

public class ShadingInterpolation {

    public static final ShadingInterpolation CONSTANT =
        new ShadingInterpolation("constant");

    public static final ShadingInterpolation SMOOTH = new ShadingInterpolation("smooth");

    private final static Map<String, ShadingInterpolation> map = new HashMap<String, ShadingInterpolation>();

    static {
        ShadingInterpolation.map.put("constant", ShadingInterpolation.CONSTANT);
        ShadingInterpolation.map.put("smooth", ShadingInterpolation.SMOOTH);
    }

    private String name;

    private ShadingInterpolation(String name) {
        this.name = name;
    }

    public static ShadingInterpolation getNamed(String name) {
        ShadingInterpolation result = (ShadingInterpolation) ShadingInterpolation.map.get(name);
        if (result == null)
            throw new IllegalArgumentException("No such shading interpolation: " + name);
        return result;
    }

    public String toString() {
        return name;
    }

}