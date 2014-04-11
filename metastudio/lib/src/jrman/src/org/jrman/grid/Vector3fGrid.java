/*
 Vector3fGrid.java
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

import javax.vecmath.Vector3f;

import org.jrman.geom.Transform;

public class Vector3fGrid extends Tuple3fGrid {

    public static Vector3fGrid getInstance() {
        return new Vector3fGrid();
    }

    public Vector3fGrid() {
        data = new Vector3f[maxSize];
        for (int i = 0; i < data.length; i++)
            data[i] = new Vector3f();
    }

    /*
     * normalize
     */

    public void normalize(Vector3fGrid v) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] vdata = (Vector3f[]) v.data;
        for (int i = 0; i < size; i++)
            tdata[i].normalize(vdata[i]);
    }

    public void normalize(Vector3fGrid v, BooleanGrid cond) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] vdata = (Vector3f[]) v.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                tdata[i].normalize(vdata[i]);
    }

    /*
     * vtransform
     */

    public void vtransform(Vector3fGrid v, Transform transform) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] vdata = (Vector3f[]) v.data;
        for (int i = 0; i < size; i++)
            transform.transformVector(vdata[i], tdata[i]);
    }

    public void vtransform(Vector3fGrid v, Transform transform,
                           BooleanGrid cond) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] vdata = (Vector3f[]) v.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                transform.transformVector(vdata[i], tdata[i]);
    }

    /*
     * ntransform
     */

    public void ntransform(Vector3fGrid n, Transform transform) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] ndata = (Vector3f[]) n.data;
        for (int i = 0; i < size; i++)
            transform.transformNormal(ndata[i], tdata[i]);
    }

    public void ntransform(Vector3fGrid n, Transform transform,
                           BooleanGrid cond) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] ndata = (Vector3f[]) n.data;
        for (int i = 0; i < size; i++)
            if (cond.data[i])
                transform.transformNormal(ndata[i], tdata[i]);
    }

    /*
     * faceforward
     */

    public void faceforward(Vector3fGrid v, Vector3f light) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] vdata = (Vector3f[]) v.data;
        for (int i = 0; i < size; i++) {
            float d = vdata[i].dot(light);
            if (d < 0)
                tdata[i].set(v.data[i]);
            else
                tdata[i].negate(v.data[i]);
        }
    }

    public void faceforward(Vector3fGrid v, Vector3fGrid light) {
        Vector3f[] tdata = (Vector3f[]) data;
        Vector3f[] vdata = (Vector3f[]) v.data;
        Vector3f[] ldata = (Vector3f[]) light.data;
        for (int i = 0; i < size; i++) {
            float d = vdata[i].dot(ldata[i]);
            if (d < 0)
                tdata[i].set(v.data[i]);
            else
                tdata[i].negate(v.data[i]);
        }
    }
    
    /*
     * cross
     */
     
     public void cross(Vector3fGrid v1, Vector3fGrid v2) {
         Vector3f[] vdata = (Vector3f[]) data;
         Vector3f[] v1data = (Vector3f[]) v1.data;
         Vector3f[] v2data = (Vector3f[]) v2.data;
         for (int i = 0; i < size; i++)
            vdata[i].cross(v1data[i], v2data[i]);

         // Ugly hack to attempt to avoid null normals...
         for (int i = 0; i <  uSize; i++) {
             if (vdata[i + uSize].x == 0 && vdata[i + uSize].y == 0 &&
                 vdata[i + uSize].z == 0)
                 vdata[i + uSize].set(vdata[i + uSize * 2]);
             if (vdata[i].x == 0 && vdata[i].y == 0 && vdata[i].z == 0)
                 vdata[i].set(vdata[i + uSize]);
         }
         for (int i = size - uSize; i < size; i++) {
             if (vdata[i - uSize].x == 0 && vdata[i - uSize].y == 0 &&
                 vdata[i - uSize].z == 0)
                 vdata[i - uSize].set(vdata[i - uSize * 2]);
             if (vdata[i].x == 0 && vdata[i].y == 0 && vdata[i].z == 0)
                 vdata[i].set(vdata[i - uSize]);
         }
         for (int i = 0; i < size; i += uSize) {
             if (vdata[i + 1].x == 0 && vdata[i + 1].y == 0 &&
                 vdata[i + 1].z == 0)
                 vdata[i + 1].set(vdata[i + 2]);
             if (vdata[i].x == 0 && vdata[i].y == 0 && vdata[i].z == 0)
                 vdata[i].set(vdata[i + 1]);
         }
         for (int i = uSize - 1; i < size; i += uSize) {
             if (vdata[i - 1].x == 0 && vdata[i - 1].y == 0 &&
                 vdata[i - 1].z == 0)
                 vdata[i - 1].set(vdata[i - 2]);
             if (vdata[i].x == 0 && vdata[i].y == 0 && vdata[i].z == 0)
                 vdata[i].set(vdata[i - 1]);
         }
     }

}
