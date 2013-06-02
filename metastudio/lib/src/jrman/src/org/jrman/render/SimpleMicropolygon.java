/*
 SimpleMicropolygon.java
 Copyright (C) 2003, 2004 Gerardo Horvilleur Martinez

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

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

public class SimpleMicropolygon extends Micropolygon {

    private final static float EPSILON = 10E-5f;

    private float ax;

    private float ay;

    private float az;

    private float bx;

    private float by;

    private float bz;

    private float cx;

    private float cy;

    private float cz;

    private float colorRed;

    private float colorGreen;

    private float colorBlue;

    private float opacityRed;

    private float opacityGreen;

    private float opacityBlue;

    public boolean init(
        Point3f a,
        Point3f b,
        Point3f c,
        float cRed,
        float cGreen,
        float cBlue,
        float oRed,
        float oGreen,
        float oBlue) {
        if (oRed == 0f && oGreen == 0f && oBlue == 0f)
            return false;
        count++;
        // get bounds
        addPointToBounds(a.x, a.y, a.z);
        addPointToBounds(b.x, b.y, b.z);
        addPointToBounds(c.x, c.y, c.z);
        ax = a.x;
        ay = a.y;
        az = a.z;
        bx = b.x - ax;
        by = b.y - ay;
        bz = b.z;
        cx = c.x - ax;
        cy = c.y - ay;
        cz = c.z;
        float det = bx * cy - by * cx;
        if (det > -EPSILON && det < EPSILON)
            return false;
        bx /= det;
        by /= det;
        cx /= det;
        cy /= det;
        colorRed = cRed;
        colorGreen = cGreen;
        colorBlue = cBlue;
        opacityRed = oRed;
        opacityGreen = oGreen;
        opacityBlue = oBlue;
        return true;
    }

    public void sampleAtPoint(SamplePoint sp) {
        Point2f point = sp.getPoint();
        if (sp.isOpaque() && sp.getZ() < minZ)
            return;
        float ox = point.x - ax;
        float oy = point.y - ay;
        float u = ox * cy - oy * cx;
        if (u < 0f || u > 1f)
            return;
        float v = oy * bx - ox * by;
        if (v < 0f)
            return;
        float w = 1f - u - v;
        if (w < 0f)
            return;
        float z = w * az + u * bz + v * cz;
        if (sp.isOpaque() && sp.getZ() < z)
            return;
        sp.addSample(
            colorRed,
            colorGreen,
            colorBlue,
            opacityRed,
            opacityGreen,
            opacityBlue,
            z);
        return;
    }

}
