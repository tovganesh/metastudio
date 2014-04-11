/*
 Bounds3f.java
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

import org.jrman.util.Constants;

public class Bounds3f implements BoundingVolume {

    // Not thread safe!
    private static Point3f p = new Point3f();

    protected float minX;

    protected float maxX;

    protected float minY;

    protected float maxY;

    protected float minZ;

    protected float maxZ;

    public static Bounds3f ALL =
        new Bounds3f(
            -Constants.INFINITY,
            Constants.INFINITY,
            -Constants.INFINITY,
            Constants.INFINITY,
            -Constants.INFINITY,
            Constants.INFINITY);

    protected Bounds3f() { // Empty
        this(
            Constants.INFINITY,
            -Constants.INFINITY,
            Constants.INFINITY,
            -Constants.INFINITY,
            Constants.INFINITY,
            -Constants.INFINITY);
    }

    public Bounds3f(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
        minX = xMin;
        maxX = xMax;
        minY = yMin;
        maxY = yMax;
        minZ = zMin;
        maxZ = zMax;
    }

    public Bounds3f(Point3f min, Point3f max) {
        this(min.x, max.x, min.y, max.y, min.z, max.z);
    }

    public float getMinZ() {
        return minZ;
    }
    
    public float getMaxZ() {
        return maxZ;
    }

    public Bounds2f toBounds2f() {
        return new Bounds2f(minX, maxX, minY, maxY);
    }

    public Bounds2f toBounds2f(float margin) {
        return new Bounds2f(minX - margin, maxX + margin, minY - margin, maxY + margin);
    }

    public boolean intersects(Bounds3f other) {
        if (minX > other.maxX)
            return false;
        if (maxX < other.minX)
            return false;
        if (minY > other.maxY)
            return false;
        if (maxY < other.minY)
            return false;
        if (minZ > other.maxZ)
            return false;
        if (maxZ < other.minZ)
            return false;
        return true;
    }

    public boolean contains(Point3f p) {
        if (p.x < minX)
            return false;
        if (p.x > maxX)
            return false;
        if (p.y < minY)
            return false;
        if (p.y > maxY)
            return false;
        if (p.z < minZ)
            return false;
        if (p.z > maxZ)
            return false;
        return true;
    }

    public BoundingVolume transform(Transform transform) {
        /*
        MutableBounds3f mb = new MutableBounds3f();
        p.set(minX, minY, minZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(minX, minY, maxZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(minX, maxY, minZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(minX, maxY, maxZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(maxX, minY, minZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(maxX, minY, maxZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(maxX, maxY, minZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        p.set(maxX, maxY, maxZ);
        transform.transformPoint(p);
        mb.addPoint(p);
        return mb;
        */
        ConvexHull3f c = new ConvexHull3f();
        c.addPoint(minX, minY, minZ);
        c.addPoint(minX, minY, maxZ);
        c.addPoint(minX, maxY, minZ);
        c.addPoint(minX, maxY, maxZ);
        c.addPoint(maxX, minY, minZ);
        c.addPoint(maxX, minY, maxZ);
        c.addPoint(maxX, maxY, minZ);
        c.addPoint(maxX, maxY, maxZ);
        return c.transform(transform);
    }

    public Plane.Side whichSideOf(Plane plane) {
        boolean inside = false;
        boolean outside = false;
        p.set(minX, minY, minZ);
        if (plane.isInside(p))
            inside = true;
        else
            outside = true;
        p.set(minX, minY, maxZ);
        if (plane.isInside(p)) {
            inside = true;
            if (outside)
                return Plane.Side.BOTH_SIDES;
        } else {
            outside = true;
            if (inside)
                return Plane.Side.BOTH_SIDES;
        }
        p.set(minX, maxY, minZ);
        if (plane.isInside(p)) {
            inside = true;
            if (outside)
                return Plane.Side.BOTH_SIDES;
        } else {
            outside = true;
            if (inside)
                return Plane.Side.BOTH_SIDES;
        }
        p.set(minX, maxY, maxZ);
        if (plane.isInside(p)) {
            inside = true;
            if (outside)
                return Plane.Side.BOTH_SIDES;
        } else {
            outside = true;
            if (inside)
                return Plane.Side.BOTH_SIDES;
        }
        p.set(maxX, minY, minZ);
        if (plane.isInside(p)) {
            inside = true;
            if (outside)
                return Plane.Side.BOTH_SIDES;
        } else {
            outside = true;
            if (inside)
                return Plane.Side.BOTH_SIDES;
        }
        p.set(maxX, minY, maxZ);
        if (plane.isInside(p)) {
            inside = true;
            if (outside)
                return Plane.Side.BOTH_SIDES;
        } else {
            outside = true;
            if (inside)
                return Plane.Side.BOTH_SIDES;
        }
        p.set(maxX, maxY, minZ);
        if (plane.isInside(p)) {
            inside = true;
            if (outside)
                return Plane.Side.BOTH_SIDES;
        } else {
            outside = true;
            if (inside)
                return Plane.Side.BOTH_SIDES;
        }
        p.set(maxX, maxY, maxZ);
        if (plane.isInside(p)) {
            inside = true;
            if (outside)
                return Plane.Side.BOTH_SIDES;
        } else {
            outside = true;
            if (inside)
                return Plane.Side.BOTH_SIDES;
        }
        return inside ? Plane.Side.INSIDE : Plane.Side.OUTSIDE;
    }

    public Bounds3f getBoundingBox() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb
            .append('[')
            .append(minX)
            .append(" ")
            .append(maxX)
            .append(" ")
            .append(minY)
            .append(" ")
            .append(maxY)
            .append(" ")
            .append(minZ)
            .append(" ")
            .append(maxZ)
            .append(']');
        return sb.toString();
    }

    public BoundingVolume enlarge(float margin) {
        return new Bounds3f(
            minX - margin,
            maxX + margin,
            minY - margin,
            maxY + margin,
            minZ - margin,
            maxZ + margin);
    }

}
