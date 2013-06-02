/*
 DetailRange.java
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

package org.jrman.attributes;

public class DetailRange {

    private float minVisible;

    private float lowerTransition;

    private float upperTransition;

    private float maxVisible;

    public DetailRange() {
        this(0f, 0f, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public DetailRange(
        float minVisible,
        float lowerTransition,
        float upperTransition,
        float maxVisible) {
        this.minVisible = minVisible;
        this.lowerTransition = lowerTransition;
        this.upperTransition = upperTransition;
        this.maxVisible = maxVisible;

    }

    public float getMinVisible() {
        return minVisible;
    }

    public float getLowerTranstion() {
        return lowerTransition;
    }

    public float getUpperTransition() {
        return upperTransition;
    }

    public float getMaxVisible() {
        return maxVisible;
    }

}