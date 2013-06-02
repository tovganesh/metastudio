/*
 MemoryBucket.java
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

package org.jrman.render;

import java.util.Arrays;

import org.jrman.primitive.Primitive;

public class MemoryBucket implements Bucket {
    
    private Primitive[] primitives = new Primitive[0];
    
    private int primitiveCount = 0;
    
    private Micropolygon[] micropolygons = new Micropolygon[0];
    
    private int micropolygonCount = 0;
    
    private boolean primitivesModified;
    
    private boolean micropolygonsModified;

    public MemoryBucket() {
    }

    public void addPrimitive(Primitive primitive) {
        if (primitiveCount == primitives.length) {
            Primitive[] tmp;
            if (primitiveCount == 0)
                tmp = new Primitive[50];
            else
                tmp = new Primitive[primitiveCount * 2];
            System.arraycopy(primitives, 0, tmp, 0, primitiveCount);
            primitives = tmp;
        }
        primitives[primitiveCount++] = primitive;
        primitivesModified = true;
    }
    
    public boolean hasMorePrimitives() {
        return primitiveCount != 0;
    }
    
    public Primitive getNextPrimitive() {
        Primitive p = primitives[--primitiveCount];
        primitives[primitiveCount] = null;
        return p;
    }

    public void sortPrimitives() {
        if (primitivesModified) {
            Arrays.sort(primitives, 0, primitiveCount);
            primitivesModified = false;
        }
    }
    
    public void sortMicropolygons() {
        if (micropolygonsModified) {
            Arrays.sort(micropolygons, 0, micropolygonCount);
            micropolygonsModified = false;
        }
    }

    public void addMicropolygon(Micropolygon mp) {
        if (micropolygonCount == micropolygons.length) {
            Micropolygon[] tmp;
            if (micropolygonCount == 0)
                tmp = new Micropolygon[1024];
            else
                	tmp = new Micropolygon[micropolygonCount * 2];
            System.arraycopy(micropolygons, 0, tmp, 0, micropolygonCount);
            micropolygons = tmp;
        }
        micropolygons[micropolygonCount++] = mp;
        micropolygonsModified = true;
    }
    
    public boolean hasMoreMicropolygons() {
        return micropolygonCount != 0;
    }
    
    public Micropolygon getNextMicropolygon() {
        Micropolygon mp = micropolygons[--micropolygonCount];
        micropolygons[micropolygonCount] = null;
        return mp;
    }

}
