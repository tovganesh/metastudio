/*
 CatmullRomSamplesFilter.java
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

package org.jrman.util;

public class CatmullRomSamplesFilter extends SamplesFilter {

    protected float filterFunc(float x, float y, float xWidth, float yWidth) {
        float r2 = (x * x + y * y);
        float r = (float) Math.sqrt(r2);
        return (r >= 2f) ? 0f : (r < 1f) ? (3f * r * r2 - 5f * r2 + 2f) : (-r * r2 + 5f * r2 - 8f * r + 4);
    }

}
