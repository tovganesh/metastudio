/*
 MutableBounds3f.java
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

package org.jrman.geom;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

import org.jrman.util.Calc;

public class MutableBounds3f extends Bounds3f {
    
    public void addPoint(float x, float y, float z) {
        minX = Calc.min(x, minX);
        minY = Calc.min(y, minY);
        minZ = Calc.min(z, minZ);
        maxX = Calc.max(x, maxX);
        maxY = Calc.max(y, maxY);
        maxZ = Calc.max(z, maxZ);
    }
    
    public void addPoint(Point3f point) {
        addPoint(point.x, point.y, point.z);
    }
    
    public void addPoint(Point4f hpoint){
        addPoint(hpoint.x / hpoint.w, hpoint.y / hpoint.w, hpoint.z/hpoint.w);
    }
    
    public void addBounds3f(Bounds3f b) {
        addPoint(b.minX, b.minY, b.minZ);
        addPoint(b.maxX, b.maxY, b.maxZ);
    }
    
    public void addBoundingVolume(BoundingVolume bv) {
        addBounds3f(bv.getBoundingBox());
    }

}
