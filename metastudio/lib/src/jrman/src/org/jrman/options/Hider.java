/*
 Hider.java
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

public class Hider {

    public static final Hider HIDDEN = new Hider("hidden");
    
    public static final Hider NULL = new Hider("null");
    
    public static final Hider BOUNDING_BOX = new Hider("boundingbox");

    private String name;

    private static Map<String, Hider> map = new HashMap<String, Hider>();

    static {
        map.put("hidden", HIDDEN);
        map.put("null", NULL);
        map.put("boundingbox", BOUNDING_BOX);
    }

    private Hider(String name) {
        this.name = name;
    }

    public static Hider getNamed(String name) {
        Hider result = (Hider) map.get(name);
        if (result == null)
            throw new IllegalArgumentException("no such hider: " + name);
        return result;
    }

    public String toString() {
        return name;
    }

}
