/*
 SolidOperation.java
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

public class SolidOperation {
    
    public final static SolidOperation PRIMITIVE = new SolidOperation("primitive");
    
    public final static SolidOperation INTERSECTION = new SolidOperation("intersection");
    
    public final static SolidOperation UNION = new SolidOperation("union");
    
    public final static SolidOperation DIFFERENCE = new SolidOperation("difference");
    
    private static Map<String, SolidOperation> map = new HashMap<String, SolidOperation>();
    
    static {
        map.put("primitive", PRIMITIVE);
        map.put("intersection", INTERSECTION);
        map.put("union", UNION);
        map.put("difference", DIFFERENCE);
    }

    private String name;

    private SolidOperation(String name) {
        this.name = name;
    }
    
    public static SolidOperation getNamed(String name){
        SolidOperation result = (SolidOperation) map.get(name);
        if (result == null)
            throw new IllegalArgumentException("No such solid operation: " + name);
        return result;
    }
    
    public String toString() {
        return name;
    }

}
