/*
 Orientation.java
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

public class Orientation {

    public static final Orientation OUTSIDE = new Orientation("outside");

    public static final Orientation INSIDE = new Orientation("inside");

    public static final Orientation LH = new Orientation("lh");

    public static final Orientation RH = new Orientation("rh");

    private final static Map<String, Orientation> map = new HashMap<String, Orientation>();

    static {
        Orientation.map.put("outside", Orientation.OUTSIDE);
        Orientation.map.put("inside", Orientation.INSIDE);
        Orientation.map.put("lh", Orientation.LH);
        Orientation.map.put("rh", Orientation.RH);
    }

    private String name;

    private Orientation(String name) {
        this.name = name;
    }

    public static Orientation getNamed(String name) {
        Orientation result = (Orientation) Orientation.map.get(name);
        if (result == null)
            throw new IllegalArgumentException("No such orientation: " + name);
        return result;
    }

    public Orientation getReverse() {
        Orientation result = null;
        if (this == Orientation.OUTSIDE)
            result = Orientation.INSIDE;
        else if (this == Orientation.INSIDE)
            result = Orientation.OUTSIDE;
        else if (this == Orientation.LH)
            result = Orientation.RH;
        else if (this == Orientation.RH)
            result = Orientation.LH;
        return result;
    }

    public String toString() {
        return name;
    }

}