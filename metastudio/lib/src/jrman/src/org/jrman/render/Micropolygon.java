/*
 Micropolygon.java
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

import org.jrman.util.Calc;
import org.jrman.util.Constants;

public abstract class Micropolygon implements Comparable{

    public static int count;

    protected float minX = Constants.INFINITY;

    protected float maxX = -Constants.INFINITY;

    protected float minY = Constants.INFINITY;

    protected float maxY = -Constants.INFINITY;

    protected float minZ = Constants.INFINITY;

    public abstract void sampleAtPoint(SamplePoint sp);

    public final float getMaxX() {
        return maxX;
    }

    public final float getMaxY() {
        return maxY;
    }

    public final float getMinX() {
        return minX;
    }

    public final float getMinY() {
        return minY;
    }

    public final float getMinZ() {
        return minZ;
    }
    
    public int compareTo(Object other) {
        Micropolygon mp = (Micropolygon) other;
        if (minZ > mp.minZ)
            return -1;
        if (minZ < mp.minZ)
            return 1;
        return 0;
    }

    protected void addPointToBounds(float x, float y, float z) {
        minX = Calc.min(minX, x);
        maxX = Calc.max(maxX, x);
        minY = Calc.min(minY, y);
        maxY = Calc.max(maxY, y);
        minZ = Calc.min(minZ, z);
    }

}
