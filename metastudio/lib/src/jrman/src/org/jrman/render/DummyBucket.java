/*
 DummyBucket.java
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

public class DummyBucket implements Bucket {

    public final static Bucket UNIQUE = new DummyBucket();
    
    private DummyBucket() {
    }

    public void addPrimitive(Primitive primitive) {
    }
    
    public boolean hasMorePrimitives() {
        return false;
    }
    
    public Primitive getNextPrimitive() {
        throw new RuntimeException("No primitives in dummy bucket!");
    }

    public void sortPrimitives() {
    }
    
    public void sortMicropolygons() {
    }

    public void addMicropolygon(Micropolygon mp) {
    }
    
    public boolean hasMoreMicropolygons() {
        return false;
    }
    
    public Micropolygon getNextMicropolygon() {
        throw new RuntimeException("No micropolygons in dummy bucket!");
    }

}
