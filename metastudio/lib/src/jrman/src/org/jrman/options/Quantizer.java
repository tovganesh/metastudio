/*
 Quantizer.java
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

import org.jrman.util.Calc;

public class Quantizer {

    private float ditherAmplitude;

    private int max;

    private int min;

    private int one;

    public Quantizer(int one, int min, int max, float ditherAmplitude) {
        this.one = one;
        this.min = min;
        this.max = max;
        this.ditherAmplitude = ditherAmplitude;
    }

    public float getDitherAmplitude() {
        return ditherAmplitude;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getOne() {
        return one;
    }
    
    public int quantize(float value) {
      int v = (int) Math.round(one * value + ditherAmplitude * Math.random());
      return Calc.clamp(v,  min, max);
    }
    
}
