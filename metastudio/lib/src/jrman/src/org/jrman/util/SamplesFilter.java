/*
 SamplesFilter.java
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

import org.jrman.maps.MipMap;
import org.jrman.maps.MipMap.Mode;

public abstract class SamplesFilter {

    protected int width;

    protected int height;

    protected float[] amplitude;

    protected float amplitudeSum;

    protected float oneOverAmplitudeSum;

    protected abstract float filterFunc(float x, float y, float xWidth, float yWidth);

    public void init(int width, int height, float xWidth, float yWidth) {
        this.width = width;
        this.height = height;
        amplitude = new float[width * height];
        float xInc = xWidth / width;
        float yInc = yWidth / height;
        float y = -yWidth / 2f + yInc / 2f;
        for (int row = 0; row < height; row++) {
            float x = -xWidth / 2f + xInc / 2f;
            for (int column = 0; column < width; column++) {
                float a = filterFunc(x, y, xWidth, yWidth);
                amplitude[row * width + column] = a;
                amplitudeSum += a;
                x += xInc;
            }
            y += yInc;
        }
        oneOverAmplitudeSum = 1f / amplitudeSum;
    }

    /*
    public void doFilter(
        float[] src,
        int srcOffset,
        int srcRowLength,
        float[] dst,
        int dstOffset,
        int dstRowLength,
        int colCount,
        int rowCount,
        int horizontalStep,
        int verticalStep,
        int bandCount) {
        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++)
                for (int band = 0; band < bandCount; band++) {
                    float sum = 0f;
                    int amplitudeOffset = 0;
                    for (int sampleRow = 0; sampleRow < height; sampleRow++)
                        for (int sampleCol = 0; sampleCol < width; sampleCol++) {
                            int offset =
                                srcOffset
                                    + (srcRowLength * (row * verticalStep + sampleRow)
                                        + col * horizontalStep
                                        + sampleCol)
                                        * bandCount
                                    + band;
                            // int amplitudeOffset = sampleRow * width + sampleCol;
                            sum += src[offset] * amplitude[amplitudeOffset++];
                        }
                    sum /= amplitudeSum;
                    dst[dstOffset + (dstRowLength * row + col) * bandCount + band] = sum;
                }
    }
    */

    public void doFilter(
        float[] src,
        int srcOffset,
        int srcRowLength,
        float[] dst,
        int dstOffset,
        int dstRowLength,
        int colCount,
        int rowCount,
        int horizontalStep,
        int verticalStep,
        int bandCount) {
        int rowStart = srcOffset;
        int rowOffsetIncr = srcRowLength * bandCount;
        int rowStartIncr = rowOffsetIncr * verticalStep;
        int colStartIncr = horizontalStep * bandCount;
        for (int row = 0; row < rowCount; row++) {
            int colStart = 0;
            for (int col = 0; col < colCount; col++) {
                for (int band = 0; band < bandCount; band++) {
                    float sum = 0f;
                    int amplitudeOffset = 0;
                    int rowOffset = rowStart + colStart + band;
                    for (int sampleRow = 0; sampleRow < height; sampleRow++) {
                        int offset = rowOffset;
                        for (int sampleCol = 0; sampleCol < width; sampleCol++) {
                            sum += src[offset] * amplitude[amplitudeOffset++];
                            offset += bandCount;
                        }
                        rowOffset += rowOffsetIncr;
                    }
                    sum *= oneOverAmplitudeSum;
                    dst[dstOffset + (dstRowLength * row + col) * bandCount + band] = sum;
                }
                colStart += colStartIncr;
            }
            rowStart += rowStartIncr;
        }
    }
    
    private int getValue(byte[] src, int s, int t, int size, Mode sMode, Mode tMode, int band) {
        if (s < 0 || s >= size) {
            if (sMode == MipMap.Mode.BLACK)
                return (band == 3 ? 255 : 0);
            else if (sMode == MipMap.Mode.CLAMP)
                s = Calc.clamp(s, 0, size - 1);
            else if (sMode == MipMap.Mode.PERIODIC)
                s &= (size - 1);
        }
        if (t < 0 || t >= size) {
            if (tMode == MipMap.Mode.BLACK)
                return (band == 3 ? 255 : 0);
            else if (tMode == MipMap.Mode.CLAMP)
                t = Calc.clamp(t, 0, size - 1);
            else if (tMode == MipMap.Mode.PERIODIC)
                t &= (size - 1);
        }
        return src[(size * t + s) * 4 + band] & 0xff;
    }

    public void doFilter(
        byte[] src,
        int srcSize,
        byte[] dst,
        int dstRowLength,
        int colCount,
        int rowCount,
        int horizontalStep,
        int verticalStep,
        MipMap.Mode sMode,
        MipMap.Mode tMode) {
        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++)
                for (int band = 0; band < 4; band++) {
                    float sum = 0f;
                    for (int sampleRow = 0; sampleRow < height; sampleRow++)
                        for (int sampleCol = 0; sampleCol < width; sampleCol++) {
                            int srcRow = row * verticalStep + sampleRow - height / 2;
                            int srcCol = col * horizontalStep + sampleCol - width / 2;
                            int value = getValue(src, srcCol, srcRow, srcSize, sMode, tMode, band);
                            int amplitudeOffset = sampleRow * width + sampleCol;
                            sum += value * amplitude[amplitudeOffset];
                        }
                    sum *= oneOverAmplitudeSum;
                    dst[(dstRowLength * row + col) * 4 + band] =
                        (byte) Calc.clamp(sum, 0f, 255f);
                }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
