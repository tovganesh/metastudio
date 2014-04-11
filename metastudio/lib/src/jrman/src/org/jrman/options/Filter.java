/*
 Filter.java
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

package org.jrman.options;

import java.util.HashMap;
import java.util.Map;

import org.jrman.util.BoxSamplesFilter;
import org.jrman.util.CatmullRomSamplesFilter;
import org.jrman.util.GaussianSamplesFilter;
import org.jrman.util.SamplesFilter;
import org.jrman.util.SincSamplesFilter;
import org.jrman.util.TriangleSamplesFilter;

public class Filter {

    private Type type;

    private float horizontalWidth;

    private float verticalWidth;

    public static class Type {

        public final static Type BOX = new Type("box");

        public final static Type TRIANGLE = new Type("triangle");

        public final static Type CATMULL_ROM = new Type("catmull-rom");

        public final static Type SINC = new Type("sinc");

        public final static Type GAUSSIAN = new Type("gaussian");

        private static Map<String, Type> map = new HashMap<String, Type>();

        static {
            map.put("box", BOX);
            map.put("triangle", TRIANGLE);
            map.put("catmull-rom", CATMULL_ROM);
            map.put("sinc", SINC);
            map.put("gaussian", GAUSSIAN);
        }

        private String name;

        private Type(String name) {
            this.name = name;
        }

        public static Type getNamed(String name) {
            Type result = (Type) map.get(name);
            if (result == null)
                throw new IllegalArgumentException("no such filter type: " + name);
                return result;
        }

        public String toString() {
            return name;
        }

    }

    public Filter(Type type, float horizontalWidth, float verticalWidth) {
        this.type = type;
        this.horizontalWidth = horizontalWidth;
        this.verticalWidth = verticalWidth;
    }
    public float getHorizontalWidth() {
        return horizontalWidth;
    }

    public Type getType() {
        return type;
    }

    public float getVerticalWidth() {
        return verticalWidth;
    }
    
    public SamplesFilter getSamplesFilter() {
        if (type == Type.BOX)
            return new BoxSamplesFilter();
       if (type == Type.TRIANGLE)
            return new TriangleSamplesFilter();
       if (type == Type.CATMULL_ROM)
            return new CatmullRomSamplesFilter();
        if (type == Type.GAUSSIAN)
            return new GaussianSamplesFilter();
        return new SincSamplesFilter();
    }

}
