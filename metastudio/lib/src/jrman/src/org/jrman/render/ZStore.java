/*
 ZStore.java
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


public class ZStore {

    private int hSize;

    private int vSize;

    private float[] depths;

    public ZStore(int hSize, int vSize) {
        this.hSize = hSize;
        this.vSize = vSize;
        depths = new float[hSize * vSize];
    }

    public void setDepths(float[] bucketDepths, int x, int y, int w, int h, int rowLength) {
        for (int row = 0; row < h; row++) {
            if (y + row < 0 || y + row >= vSize)
                continue;
            for (int col = 0; col < w; col++) {
                if (x + col < 0 || x + col >= hSize)
                    continue;
                float depth = bucketDepths[row * rowLength + col];
                depths[(y+ row) * hSize + x + col] = depth;
            }
        }
    }

    public float[] getDepths() {
        return depths;
    }

    public int getHSize() {
        return hSize;
    }

    public int getVSize() {
        return vSize;
    }

}
