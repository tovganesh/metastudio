/*
 Polyhedron3f.java
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

import org.jrman.util.Constants;

public class ConvexHull3f implements BoundingVolume {
    
    private static Point3f tmpPoint = new Point3f();
    
    private float[] pointsData = new float[4 * 3];
    
    private int count = 0;
    
    private float minZ = Constants.INFINITY;
    
    private void add(Point3f point) {
        if (count * 3 == pointsData.length) {
            float[] tmp = new float[pointsData.length * 4];
            System.arraycopy(pointsData, 0, tmp, 0, pointsData.length);
            pointsData = tmp;
        }
        pointsData[count * 3] = point.x;
        pointsData[count * 3 + 1] = point.y;
        pointsData[count * 3 + 2] = point.z;
        count++;
    }
    
    private void add(float x, float y, float z) {
        if (count * 3 == pointsData.length) {
            float[] tmp = new float[pointsData.length * 4];
            System.arraycopy(pointsData, 0, tmp, 0, pointsData.length);
            pointsData = tmp;
        }
        pointsData[count * 3] = x;
        pointsData[count * 3 + 1] = y;
        pointsData[count * 3 + 2] = z;
        count++;
    }
    
    private void get(int index, Point3f point) {
        point.x = pointsData[index * 3];
        point.y = pointsData[index * 3 + 1];
        point.z = pointsData[index * 3 + 2];
    }

    public void addPoint(Point3f point) {
        add(point);
        minZ = Math.min(minZ, point.z);
    }

    public void addPoint(float x, float y, float z) {
        add(x, y, z);
        minZ = Math.min(minZ, z);
    }

    public void addHpoint(Point4f hpoint) {
        tmpPoint.project(hpoint);
        add(tmpPoint);
        minZ = Math.min(minZ, tmpPoint.z);
    }
    
    public float getMinZ() {
         return minZ;
    }

    public BoundingVolume transform(Transform transform) {
        ConvexHull3f result = new ConvexHull3f();
        for (int i = 0; i < count; i++) {
            get(i, tmpPoint);
            transform.transformPoint(tmpPoint, tmpPoint);
            result.addPoint(tmpPoint);
        }
        return result;
    }

    public Plane.Side whichSideOf(Plane plane) {
        boolean inside = false;
        boolean outside = false;
        for (int i = 0; i < count; i++) {
            get(i, tmpPoint);
            if (plane.isInside(tmpPoint)) {
                inside = true;
                if (outside)
                    return Plane.Side.BOTH_SIDES;
            } else {
                outside = true;
                if (inside)
                    return Plane.Side.BOTH_SIDES;
            }
        }
        return inside ? Plane.Side.INSIDE : Plane.Side.OUTSIDE;
    }

    public Bounds3f getBoundingBox() {
        MutableBounds3f result = new MutableBounds3f();
        for (int i = 0; i < count; i++) {
            get(i, tmpPoint);
            result.addPoint(tmpPoint);
        }
        return result;
    }
    
    public Bounds2f toBounds2f() {
        MutableBounds2f result = new MutableBounds2f();
        for (int i = 0; i < count; i++) {
            get(i, tmpPoint);
            result.addPoint(tmpPoint.x, tmpPoint.y);
        }
        return result;
    }

    public Bounds2f toBounds2f(float margin) {
        MutableBounds2f result = new MutableBounds2f();
        for (int i = 0; i < count; i++) {
            get(i, tmpPoint);
            result.addPoint(tmpPoint.x, tmpPoint.y);
        }
        result.enlarge(margin);
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ConvexHull:\n");
        for (int i = 0; i < count; i++) {
            get(i, tmpPoint);
            sb.append(tmpPoint).append('\n');
        }
        return sb.toString();
    }

    public BoundingVolume enlarge(float margin) {
        return getBoundingBox().enlarge(margin);
    }

}
