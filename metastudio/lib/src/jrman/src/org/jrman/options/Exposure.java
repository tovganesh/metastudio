/*
 Exposure.java
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

package org.jrman.options;

import javax.vecmath.Color3f;

public class Exposure {
    
    private float gain;
    
    private float gamma;
    
    private float oneOverGamma;

    public Exposure(float gain, float gamma) {
        this.gain = gain;
        this.gamma = gamma;
        oneOverGamma = 1f / gamma;
    }

    public float getGain() {
        return gain;
    }

    public float getGamma() {
        return gamma;
    }
    
    public void expose(Color3f color,Color3f out) {
        out.scale(gain, color);
        out.x = (float) Math.pow(out.x, oneOverGamma);
        out.y = (float) Math.pow(out.y, oneOverGamma);
        out.z = (float) Math.pow(out.z, oneOverGamma);
    }

}
