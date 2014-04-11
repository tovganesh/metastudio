/*
 Display.java
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

public class Display {

    private Mode mode;

    private Type type;

    private String name;

    public static class Type {

        public final static Type FRAMEBUFFER = new Type("framebuffer");

        public final static Type FILE = new Type("file");

        private String name;

        private static Map<String, Type> map = new HashMap<String, Type>();

        static {
            map.put("framebuffer", FRAMEBUFFER);
            map.put("file", FILE);
        }

        private Type(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        public static Type getNamed(String name) {
            Type result = (Type) map.get(name);
            if (name == null)
                throw new IllegalArgumentException("no such display type: " + name);
            return result;
        }

    }
    public static class Mode {

        public final static Mode RGBA = new Mode("rgba");

        public final static Mode RGB = new Mode("rgb");

        public final static Mode Z = new Mode("z");

        public final static Mode A = new Mode("a");

        private static Map<String, Mode> map = new HashMap<String, Mode>();

        static {
            map.put("rgba", RGBA);
            map.put("rgb", RGB);
            map.put("z", Z);
            map.put("a", A);
        }

        private String name;

        private Mode(String name) {
            this.name = name;
        }

        public static Mode getNamed(String name) {
            Mode result = (Mode) map.get(name);
            if (result == null)
                throw new IllegalArgumentException("no such display mode: " + name);
            return result;
        }

        public String toString() {
            return name;
        }

    }

    public Display(String name, Type type, Mode mode) {
        this.name = name;
        this.type = type;
        this.mode = mode;
    }
    
    public Display(String name, String type, String mode){
        this(name, Type.getNamed(type), Mode.getNamed(mode));
    }
    
    public Mode getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
