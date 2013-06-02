/*
 * Sampler.java Copyright (C) 2003, 2006 Gerardo Horvilleur Martinez
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jrman.render;

import javax.vecmath.Color4f;
import javax.vecmath.Point2f;

import org.jrman.geom.Bounds2f;
import org.jrman.parser.Frame;
import org.jrman.util.Calc;
import org.jrman.util.Constants;
import org.jrman.util.Rand;

public class Sampler {

    private static Color4f tmpColor = new Color4f();

    private Point2f min = new Point2f();

    private int width;

    private int height;

    private int bucketWidth;

    private int bucketHeight;

    private int pixelWidth;

    private int pixelHeight;

    private float sampleWidth;

    private float sampleHeight;

    private float oneOverSampleWidth;

    private float oneOverSampleHeight;

    private SamplePoint[] samplePoints;

    private MaskElement rootVisibility = new MaskElement();

    private MaskElement[] pixelsVisibility;

    private int samplesPerPixel;

    private int pixelsPerBucket;

    private boolean hasSamples;

    private int rootCount;

    private int pixelCount;

    private int mpRootCount;

    public static class MaskElement {

        float z;

        boolean opaque;

        int count;

        void reset() {
            z = -Constants.INFINITY;
            opaque = false;
            count = 0;
        }

    }

    public Sampler(int bucketWidth, int bucketHeight, float hSamples,
            float vSamples, Frame frame) {
        this.bucketWidth = bucketWidth;
        this.bucketHeight = bucketHeight;
        pixelsPerBucket = bucketWidth * bucketHeight;
        width = (int) Math.ceil(bucketWidth * hSamples);
        height = (int) Math.ceil(bucketHeight * vSamples);
        pixelWidth = width / bucketWidth;
        pixelHeight = height / bucketHeight;
        samplesPerPixel = pixelWidth * pixelHeight;
        sampleWidth = 1f / hSamples;
        oneOverSampleWidth = hSamples;
        sampleHeight = 1f / vSamples;
        oneOverSampleHeight = vSamples;
        samplePoints = new SamplePoint[width * height];
        for (int row = 0; row < height; row++)
            for (int column = 0; column < width; column++)
                samplePoints[row * width + column] = new SamplePoint(this,
                        column / pixelWidth + (row / pixelHeight) * bucketWidth);
        pixelsVisibility = new MaskElement[bucketWidth * bucketHeight];
        for (int i = 0; i < pixelsVisibility.length; i++)
            pixelsVisibility[i] = new MaskElement();

        this.frame = frame;
    }

    public void init(float minX, float minY) {
        min.set(minX, minY);
        float y = minY;
        int offset = 0;
        for (int row = 0; row < height; row++) {
            float x = minX;
            for (int column = 0; column < width; column++) {
                float jx = x + Rand.uniform() * sampleWidth;
                float jy = y + Rand.uniform() * sampleHeight;
                samplePoints[offset + column].init(jx, jy);
                x += sampleWidth;
            }
            offset += width;
            y += sampleHeight;
        }
        rootVisibility.reset();
        for (int i = 0; i < pixelsVisibility.length; i++)
            pixelsVisibility[i].reset();
        hasSamples = false;
    }

    public void sampleBucket(Bucket bucket) {
        while (bucket.hasMoreMicropolygons()) {
            Micropolygon mp = bucket.getNextMicropolygon();
            sample(mp);
            hasSamples = true;
        }
    }

    public void getColors(float[] dst, int dstOffset, int rowLength, int bands,
            int bandOffset) {
        int destOffset = dstOffset + bandOffset;
        rowLength *= bands;
        if (hasSamples) {
            int srcOffset = 0;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    samplePoints[srcOffset++].getColor(tmpColor);
                    int offset = destOffset + col * bands;
                    dst[offset++] = tmpColor.x;
                    dst[offset++] = tmpColor.y;
                    dst[offset++] = tmpColor.z;
                    dst[offset++] = tmpColor.w;
                }
                destOffset += rowLength;
            }
        } else {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int offset = destOffset + col * bands;
                    dst[offset++] = 0f;
                    dst[offset++] = 0f;
                    dst[offset++] = 0f;
                    dst[offset] = 0f;
                }
                destOffset += rowLength;
            }
        }
    }

    public void getDepths(float[] depths) {
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++) {
                int offset = row * width + col;
                depths[offset] = samplePoints[offset].getDepth();
            }
    }

    public SamplePoint getSamplePoint(int column, int row) {
        return samplePoints[row * width + column];
    }

    public int getHeight() {
        return height;
    }

    public Point2f getMin() {
        return min;
    }

    public float getSampleHeight() {
        return sampleHeight;
    }

    public float getSampleWidth() {
        return sampleWidth;
    }

    public float getOneOverSampleHeight() {
        return oneOverSampleHeight;
    }

    public float getOneOverSampleWidth() {
        return oneOverSampleWidth;
    }

    public int getWidth() {
        return width;
    }

    public void updateDepth(int offset, float z) {
        MaskElement me = pixelsVisibility[offset];
        me.z = Calc.max(me.z, z);
        me.count++;
        if (me.count == samplesPerPixel) {
            me.opaque = true;
            rootVisibility.count++;
            rootVisibility.z = Calc.max(rootVisibility.z, z);
            if (rootVisibility.count == pixelsPerBucket)
                rootVisibility.opaque = true;
        }
    }

    public boolean isVisible(Bounds2f bounds, float z) {
        if (rootVisibility.opaque && rootVisibility.z < z) {
            rootCount++;
            return false;
        }
        boolean visible = false;
        float minX = bounds.getMinX() - min.x;
        float minY = bounds.getMinY() - min.y;
        float maxX = bounds.getMaxX() - min.x;
        float maxY = bounds.getMaxY() - min.y;
        int minCol = (int) Calc.clamp(minX, 0f, bucketWidth - 1);
        int minRow = (int) Calc.clamp(minY, 0f, bucketHeight - 1);
        int maxCol = (int) Calc.clamp((float) Calc.ceil(maxX), 0f,
                bucketWidth - 1);
        int maxRow = (int) Calc.clamp((float) Calc.ceil(maxY), 0f,
                bucketHeight - 1);
        PixelsLoop: for (int row = minRow; row <= maxRow; row++)
            for (int column = minCol; column <= maxCol; column++) {
                MaskElement me = pixelsVisibility[row * bucketWidth + column];
                if (!me.opaque || me.z > z) {
                    visible = true;
                    break PixelsLoop;
                }
            }
        if (!visible) {
            pixelCount++;
            return false;
        }
        return true;
    }

    public void sample(Micropolygon mp) {
        if (rootVisibility.opaque && rootVisibility.z < mp.getMinZ()) {
            mpRootCount++;
            return;
        }
        float minX = mp.getMinX() - min.x;
        float minY = mp.getMinY() - min.y;
        float maxX = mp.getMaxX() - min.x;
        float maxY = mp.getMaxY() - min.y;
        int minCol = (int) Calc.clamp(minX, 0f, bucketWidth - 1);
        int minRow = (int) Calc.clamp(minY, 0f, bucketHeight - 1);
        int maxCol = (int) Calc.clamp((float) Calc.ceil(maxX), 0f,
                bucketWidth - 1);
        int maxRow = (int) Calc.clamp((float) Calc.ceil(maxY), 0f,
                bucketHeight - 1);
        int sMinCol = Calc.clamp((int) (minX * oneOverSampleWidth), 0,
                width - 1);
        int sMinRow = Calc.clamp((int) (minY * oneOverSampleHeight), 0,
                height - 1);
        int sMaxCol = Calc.clamp((int) (maxX * oneOverSampleWidth), 0,
                width - 1);
        int sMaxRow = Calc.clamp((int) (maxY * oneOverSampleHeight), 0,
                height - 1);
        int rowOffset = minRow * pixelHeight;
        for (int row = minRow; row <= maxRow; row++) {
            int minR = Calc.max(rowOffset, sMinRow);
            int maxR = Calc.min(rowOffset + pixelHeight - 1, sMaxRow);
            int colOffset = minCol * pixelWidth;
            for (int column = minCol; column <= maxCol; column++) {
                MaskElement me = pixelsVisibility[row * bucketWidth + column];
                if (!me.opaque || me.z > mp.getMinZ()) {
                    int minC = Calc.max(colOffset, sMinCol);
                    int maxC = Calc.min(colOffset + pixelWidth - 1, sMaxCol);
                    for (int r = minR; r <= maxR; r++)
                        for (int c = minC; c <= maxC; c++) {
                            SamplePoint sp = getSamplePoint(c, r);
                            mp.sampleAtPoint(sp);
                        }
                }
                colOffset += pixelWidth;
            }
            rowOffset += pixelHeight;
        }
        return;
    }

    public int getBucketHeight() {
        return bucketHeight;
    }

    public int getBucketWidth() {
        return bucketWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    public MaskElement[] getPixelsVisibility() {
        return pixelsVisibility;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public MaskElement getRootVisibility() {
        return rootVisibility;
    }

    public int getPixelCount() {
        return pixelCount;
    }

    public int getRootCount() {
        return rootCount;
    }

    public int getMPRootCount() {
        return mpRootCount;
    }

    private Frame frame;

    /**
     * Get the value of frame
     *
     * @return the value of frame
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * Set the value of frame
     *
     * @param frame new value of frame
     */
    public void setFrame(Frame frame) {
        this.frame = frame;
    }

}