/*
 Plane.java
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
import javax.vecmath.Vector3f;

public class Plane {
    
    private static Point3f p = new Point3f();
    
    private static Vector3f v = new Vector3f();

    private Point3f point;

    private Vector3f normal;
    
    public static class Side {
        
        public final static Side INSIDE = new Side("INSIDE");
        
        public final static Side OUTSIDE = new Side("OUTSIDE");
        
        public final static Side BOTH_SIDES = new Side("BOTH_SIDES");
        
        private String name;
        
        private Side(String name) {
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
        
    }

    public static Plane createWithPointAndNormal(Point3f point, Vector3f normal) {
        return new Plane(new Point3f(point), new Vector3f(normal));
    }

    public static Plane createWithThreePoints(Point3f a, Point3f b, Point3f c) {
        Vector3f v1 = new Vector3f(b);
        v1.sub(a);
        Vector3f v2 = new Vector3f(c);
        v2.sub(a);
        Vector3f normal = new Vector3f();
        normal.cross(v1, v2);
        return new Plane(new Point3f(a), normal);
    }

    protected Plane(Point3f point, Vector3f normal) {
        this.point = point;
        this.normal = normal;
    }
    
    public boolean isInside(Point3f p) {
        v.set(p);
        v.sub(point);
        return v.dot(normal) < 0f;
    }
    
    public boolean isInside(Point4f hp) {
        p.project(hp);
        return isInside(p);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Plane");
        sb.append(", point: ").append(point);
        sb.append(", normal: ").append(normal);
        return sb.toString();
    }

}
