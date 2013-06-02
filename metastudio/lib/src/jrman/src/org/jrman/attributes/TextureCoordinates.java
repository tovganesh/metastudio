/*
 TextureCoordinates.java
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

public class TextureCoordinates {

    private float s1;

    private float t1;

    private float s2;

    private float t2;

    private float s3;

    private float t3;

    private float s4;

    private float t4;

    public TextureCoordinates(
        float s1,
        float t1,
        float s2,
        float t2,
        float s3,
        float t3,
        float s4,
        float t4) {
        this.s1 = s1;
        this.t1 = t1;
        this.s2 = s2;
        this.t2 = t2;
        this.s3 = s3;
        this.t3 = t3;
        this.s4 = s4;
        this.t4 = t4;
    }

    public float getS1() {
        return s1;
    }

    public float getT1() {
        return t1;
    }

    public float getS2() {
        return s2;
    }

    public float getT2() {
        return t2;
    }

    public float getS3() {
        return s3;
    }

    public float getT3() {
        return t3;
    }

    public float getS4() {
        return s4;
    }

    public float getT4() {
        return t4;
    }

}