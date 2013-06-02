/*
 Bounds2f.java
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

import org.jrman.util.Constants;

public class Bounds2f {
    
    // Not thread safe!
    private static Point2f p = new Point2f();

    protected float minX;
    
    protected float maxX;
    
    protected float minY;
    
    protected float maxY;

    public Bounds2f(float xMin, float xMax, float yMin, float yMax) {
        minX = xMin;
        maxX = xMax;
        minY = yMin;
        maxY = yMax;
    }

    public Bounds2f(Point2f min, Point2f max) {
        this(min.x, max.x, min.y, max.y);
    }

    protected Bounds2f() { // Empty
        this(Constants.INFINITY, -Constants.INFINITY, Constants.INFINITY, -Constants.INFINITY);
    }

    public boolean intersects(Bounds2f other) {
        if (minX > other.maxX)
            return false;
        if (maxX < other.minX)
            return false;
        if (minY > other.maxY)
            return false;
        if (maxY < other.minY)
            return false;
        return true;
    }

    public Bounds2f transform(Transform transform) {
        MutableBounds2f mb = new MutableBounds2f();
        p.set(minX, minY);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(minX, maxY);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(maxX, minY);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(maxX, maxY);
        transform.transformPoint(p);
        mb.addPoint(p);
        return mb;
    }

    public Point2f getMax() {
        return new Point2f(maxX, maxY);
    }

    public Point2f getMin() {
        return new Point2f(minX, minY);
    }
    
    public float getMinX() {
        return minX;
    }
    
    public float getMaxX() {
        return maxX;
    }
    
    public float getMinY() {
        return minY;
    }
    
    public float getMaxY() {
        return maxY;
    }
    
    public float getWidth() {
        return maxX - minX;
    }
    
    public float getHeight() {
        return maxY - minY;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Bounds2f");
        sb.append(", min: ").append('(').append(minX).append(", ").append(minY).append(')');
        sb.append(", max: ").append('(').append(maxX).append(", ").append(maxY).append(')');
        return sb.toString();
    }

}
