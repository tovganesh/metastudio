/*
 Point3fGrid.java
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

package org.jrman.grid;

import javax.vecmath.Point3f;

import org.jrman.geom.Transform;

public class Point3fGrid extends Tuple3fGrid {

    public static Point3fGrid getInstance() {
        return new Point3fGrid();
    }

    public Point3fGrid() {
        data = new Point3f[maxSize];
        for (int i = 0; i < data.length; i++)
            data[i] = new Point3f();
    }

    /*
     * transform
     */

    public void transform(Point3fGrid p, Transform transform) {
        Point3f[] tdata = (Point3f[]) data;
        Point3f[] pdata = (Point3f[]) p.data;
        for (int i = 0; i < size; i++)
            transform.transformPoint(pdata[i], tdata[i]);
    }

    public void transform(Point3fGrid p, Transform transform,
                          BooleanGrid cond) {
        Point3f[] tdata = (Point3f[]) data;
        Point3f[] pdata = (Point3f[]) p.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                transform.transformPoint(pdata[i], tdata[i]);
    }

}
