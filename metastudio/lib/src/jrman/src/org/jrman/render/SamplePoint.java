/*
 * SamplePoint.java Copyright (C) 2003 Gerardo Horvilleur Martinez
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

import org.jrman.util.Constants;

public class SamplePoint {

    private Point2f point = new Point2f();

    private Sample[] samples = new Sample[10];

    private int sampleCount = 0;

    private Sampler sampler;

    private int offset;

    private boolean opaque;

    private float z;

    public SamplePoint(Sampler sampler, int offset) {
        this.sampler = sampler;
        this.offset = offset;
    }

    public void init(float x, float y) {
        point.set(x, y);
        opaque = false;
        for (int i = 0; i < sampleCount; i++)
            samples[i] = null;
        sampleCount = 0;
        z = Constants.INFINITY;
    }

    public void addSample(float colorRed, float colorGreen, float colorBlue,
            float opacityRed, float opacityGreen, float opacityBlue, float z) {
        int i = 0;
        while (i < sampleCount) {
            Sample s = samples[i++];
            if (s.behind(z)) {
                Sample sample = new Sample(colorRed, colorGreen, colorBlue,
                        opacityRed, opacityGreen, opacityBlue, z);
                if (!sample.isOpaque()) {
                    if (sampleCount < samples.length) {
                        System.arraycopy(samples, i - 1, samples, i,
                                sampleCount - i + 1);
                    } else {
                        Sample[] tmp = new Sample[sampleCount * 2];
                        System.arraycopy(samples, 0, tmp, 0, i - 1);
                        System.arraycopy(samples, i - 1, tmp, i, sampleCount
                                - i + 1);
                        samples = tmp;
                    }
                    sampleCount++;
                } else
                    update(z);
                samples[i - 1] = sample;
                return;
            } else if (s.isOpaque())
                return;
        }
        Sample sample = new Sample(colorRed, colorGreen, colorBlue, opacityRed,
                opacityGreen, opacityBlue, z);
        if (sampleCount == samples.length) {
            Sample[] tmp = new Sample[sampleCount * 2];
            System.arraycopy(samples, 0, tmp, 0, sampleCount);
            samples = tmp;
        }
        samples[sampleCount++] = sample;
        if (sample.isOpaque())
            update(z);
    }

    private void update(float nz) {
        if (nz < z)
            z = nz;
        if (!opaque) {
            sampler.updateDepth(offset, nz);
            opaque = true;
        }
    }

    public void getColor(Color4f ocolor) {
        ocolor.set(0f, 0f, 0f, 0f);
        // sampler.getFrame().getImager().getBgColor(ocolor);
        
        int i = 0;
        while (i < sampleCount) {
            Sample sample = samples[i++];
            if (sample.isOpaque())
                break;
        }
        while (i > 0) {
            Sample sample = samples[--i];
            sample.overlayOver(ocolor);
        }
    }

    public float getDepth() {
        int i = 0;
        while (i < sampleCount) {
            Sample sample = samples[i++];
            if (sample.isOpaque())
                return sample.getZ();
        }
        return Constants.INFINITY;
    }

    public Point2f getPoint() {
        return point;
    }

    public boolean isOpaque() {
        return opaque;
    }

    public float getZ() {
        return z;
    }

}