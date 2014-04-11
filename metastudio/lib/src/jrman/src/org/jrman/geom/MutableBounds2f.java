/*
 MutableBounds2f.java
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

import javax.vecmath.Point2f;

public class MutableBounds2f extends Bounds2f {
    
    public void addPoint(float x, float y) {
        minX = Math.min(x, minX);
        minY = Math.min(y, minY);
        maxX =Math.max(x, maxX);
        maxY = Math.max(y, maxY);
    }
    
    public void addPoint(Point2f point) {
        addPoint(point.x, point.y);
    }
    
    public void enlarge(float f) {
        minX -= f;
        minY -= f;
        maxX += f;
        maxY += f;
    }

}
