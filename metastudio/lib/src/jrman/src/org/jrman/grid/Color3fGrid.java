/*
 Color3fGrid.java
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

import javax.vecmath.Color3f;

import org.jrman.maps.MipMap;

public class Color3fGrid extends Tuple3fGrid {

    private static float[] mmData = new float[4];

    private static Color3f tmpc = new Color3f();

    private static void getColor(MipMap texture, float s, float t, float area, Color3f out) {
        texture.getData(s, t, area, mmData);
        out.x = mmData[0];
        out.y = mmData[1];
        out.z = mmData[2];
    }

    public static Color3fGrid getInstance() {
        return new Color3fGrid();
    }

    public Color3fGrid() {
        data = new Color3f[maxSize];
        for (int i = 0; i < data.length; i++)
            data[i] = new Color3f();
    }

    /*
     * texture
     */

    public void texture(String textureName, FloatGrid s, FloatGrid t, float blur) {
        MipMap texture = MipMap.getMipMap(textureName);
        for (int v = 0; v < vSize; v++)
            for (int u = 0; u < uSize; u++) {
                int u1 = u + 1;
                if (u1 == uSize)
                    u1 = u - 1;
                int v1 = v + 1;
                if (v1 == vSize)
                    v1 = v - 1;
                float area =
                    ((s.get(u, v) + s.get(u1, v)) * (t.get(u, v) - t.get(u1, v))
                        + (s.get(u1, v) + s.get(u1, v1)) * (t.get(u1, v) - t.get(u1, v1))
                        + (s.get(u1, v1) + s.get(u, v1)) * (t.get(u1, v1) - t.get(u, v1))
                        + (s.get(u, v1) + s.get(u, v)) * (t.get(u, v1) - t.get(u, v)))
                        / 2f;
                area = Math.abs(area) + blur;
                /*
                float avgS = (s.get(u, v) + s.get(u1, v) + s.get(u1, v1) + s.get(u, v1)) / 4f;
                float avgT = (t.get(u, v) + t.get(u1, v) + t.get(u1, v1) + t.get(u, v1)) / 4f;
                */
                float avgS = s.get(u, v);
                float avgT = t.get(u, v);
                getColor(texture, avgS, avgT, area, tmpc);
                set(u, v, tmpc);
            }
    }

}
