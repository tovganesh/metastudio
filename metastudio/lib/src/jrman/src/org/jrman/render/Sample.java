/*
 Sample.java
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

import javax.vecmath.Color4f;

import org.jrman.util.Calc;

public class Sample {
    
    private float colorRed;
    
    private float colorGreen;
    
    private float colorBlue;
    
    private float opacityRed;
    
    private float opacityGreen;
    
    private float opacityBlue;
    
    private float z;

    private boolean opaque;

    public Sample(float cRed, float cGreen, float cBlue,
    float oRed, float oGreen, float oBlue, float z) {
        colorRed = cRed;
        colorGreen = cGreen;
        colorBlue = cBlue;
        opacityRed = oRed;
        opacityGreen = oGreen;
        opacityBlue = oBlue;
        this.z = z;
        opaque = (opacityRed == 1f && opacityGreen == 1f && opacityBlue == 1f);
    }

    public boolean behind(Sample sample) {
        return z > sample.z;
    }
    
    public boolean behind(float otherZ) {
        return z > otherZ;
    }
    
    public boolean isOpaque() {
        return opaque;
    }

    public void overlayOver(Color4f ocolor) {
        if (opaque) {
            ocolor.x = colorRed;
            ocolor.y = colorGreen;
            ocolor.z = colorBlue;
            ocolor.w = 1f;
        } else {
            ocolor.x = ocolor.x * (1f - opacityRed) + colorRed;
            ocolor.y = ocolor.y * (1f - opacityGreen) + colorGreen;
            ocolor.z = ocolor.z  * (1f - opacityBlue) + colorBlue;
            float opacity = (opacityRed + opacityGreen + opacityBlue) / 3f;
            ocolor.w = Calc.min(1f, opacity + ocolor.w);
        }
    }

    public float getZ() {
        return z;
    }

}
