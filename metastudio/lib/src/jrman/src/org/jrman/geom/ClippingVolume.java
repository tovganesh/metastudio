/*
 ClippingVolume.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClippingVolume {

    private List<Plane> planes = new ArrayList<Plane>();

    public void addPlane(Plane plane) {
        planes.add(plane);
    }

    public Plane.Side whereIs(BoundingVolume v) {
        boolean bothSides = false;
        for (int i = 0; i < planes.size(); i++) {
            Plane plane = (Plane) planes.get(i);
            Plane.Side side = v.whichSideOf(plane);
            if (side == Plane.Side.OUTSIDE)
                return Plane.Side.OUTSIDE;
            if (side == Plane.Side.BOTH_SIDES)
                bothSides = true;
        }
        return bothSides ? Plane.Side.BOTH_SIDES : Plane.Side.INSIDE;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ClippingVolume:\n");
        for (Iterator iter = planes.iterator(); iter.hasNext();)
            sb.append(iter.next()).append('\n');
        return sb.toString();
    }

}
