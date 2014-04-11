/*
 Frame.java
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

package org.jrman.parser;

import javax.vecmath.Point2f;
import org.jrman.geom.AffineTransform;
import org.jrman.geom.Bounds2f;
import org.jrman.geom.Transform;
import org.jrman.grid.Grid;
import org.jrman.options.CameraProjection;
import org.jrman.options.Display;
import org.jrman.options.Exposure;
import org.jrman.options.Filter;
import org.jrman.options.Hider;
import org.jrman.options.Quantizer;
import org.jrman.parameters.ParameterList;
import org.jrman.shaders.Imager;
import org.jrman.util.Constants;

public class Frame {

    private float[] motionTimes; // Just now to avoid compiler warnings...

    private int frameNumber; // just now to avoid compiler warnings...

    private int horizontalResolution;

    private int verticalResolution;

    private float pixelAspectRatio;
    
    private Bounds2f cropWindow;

    private float frameAspectRatio;

    private Bounds2f screenWindow;

    private CameraProjection cameraProjection;

    private float fieldOfView;

    private float nearClipping;

    private float farClipping;

    private float fStop;

    private float focalLength;

    private float focalDistance;

    private float shutterOpen;

    private float shutterClose;

    private float pixelVariance;

    private float horizontalSamplingRate;

    private float verticalSamplingRate;

    private Filter filter;

    private Exposure exposure;

    private Imager imager;

    private Quantizer colorQuantizer;

    private Quantizer depthQuantizer;

    private Display display;

    private int originX;

    private int originY;

    private Hider hider;

    private ParameterList hiderParameters;

    private int colorSamples;

    private float relativeDetail;

    private int gridSize;

    private int bucketSizeX;

    private int bucketSizeY;
    
    private boolean endOfFrameStatistics;
    
    private boolean showProgressEnabled;
	
    private boolean framebufferAlways;

    public Frame() {
        horizontalResolution = 640;
        verticalResolution = 480;
        pixelAspectRatio = 1f;
        fieldOfView = 90f;
        cropWindow = new Bounds2f(0f, 1f, 0f, 1f);
        cameraProjection = CameraProjection.ORTHOGRAPHIC;
        nearClipping = Constants.EPSILON;
        farClipping = Constants.INFINITY;
        fStop = Constants.INFINITY;
        shutterOpen = 0f;
        shutterClose = 0f;
        horizontalSamplingRate = 2f;
        verticalSamplingRate = 2f;
        filter = new Filter(Filter.Type.GAUSSIAN, 2f, 2f);
        exposure = new Exposure(1f, 1f);
        colorQuantizer = new Quantizer(255, 0, 255, 0.5f);
        depthQuantizer = new Quantizer(Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0.5f);
        display = new Display("out.tif", Display.Type.FILE, Display.Mode.RGBA);
        hider = Hider.HIDDEN;
        colorSamples = 3;
        relativeDetail = 1f;
        setGridSize(256);
        bucketSizeX = 16;
        bucketSizeY = 16;
    }
    
    public Frame(Frame other) {
        motionTimes = other.motionTimes;
        frameNumber = other.frameNumber;
        horizontalResolution = other.horizontalResolution;
        verticalResolution = other.verticalResolution;
        pixelAspectRatio = other.pixelAspectRatio;
        cropWindow = other.cropWindow;
        frameAspectRatio = other.frameAspectRatio;
        screenWindow = other.screenWindow;
        cameraProjection = other.cameraProjection;
        fieldOfView = other.fieldOfView;
        nearClipping = other.nearClipping;
        farClipping = other.farClipping;
        fStop = other.fStop;
        focalLength = other.focalLength;
        focalDistance = other.focalDistance;
        shutterOpen = other.shutterOpen;
        shutterClose = other.shutterClose;
        pixelVariance = other.pixelVariance;
        horizontalSamplingRate = other.horizontalSamplingRate;
        verticalSamplingRate = other.verticalSamplingRate;
        filter = other.filter;
        exposure = other.exposure;
        imager = other.imager;
        colorQuantizer = other.colorQuantizer;
        depthQuantizer = other.depthQuantizer;
        display = other.display;
        originX = other.originX;
        originY = other.originY;
        hider = other.hider;
        hiderParameters = other.hiderParameters;
        colorSamples = other.colorSamples;
        relativeDetail = other.relativeDetail;
        gridSize = other.gridSize;
        bucketSizeX = other.bucketSizeX;
        bucketSizeY = other.bucketSizeY;
        endOfFrameStatistics = other.endOfFrameStatistics;
        showProgressEnabled = other.showProgressEnabled;
        framebufferAlways = other.framebufferAlways;
    }

    public void defineScreen() {
        if (frameAspectRatio == 0)
            frameAspectRatio = (horizontalResolution * pixelAspectRatio) / verticalResolution;
        if (screenWindow == null) {
            if (frameAspectRatio >= 1f)
                screenWindow =
                    new Bounds2f(-frameAspectRatio, frameAspectRatio, -1f, 1f);
            else
                screenWindow =
                    new Bounds2f(-1f, 1f, -1f / frameAspectRatio, 1f / frameAspectRatio);
        } else {
            Point2f min = screenWindow.getMin();
            Point2f max = screenWindow.getMax();
            // Doesn't handle "flipped" screen window
            float width = max.x - min.x;
            float height = max.y - min.y;
            frameAspectRatio = width / height;
        }
    }

    public CameraProjection getCameraProjection() {
        return cameraProjection;
    }

    public Quantizer getColorQuantizer() {
        return colorQuantizer;
    }

    public int getColorSamples() {
        return colorSamples;
    }

    public Bounds2f getCropWindow() {
        return cropWindow;
    }

    public Quantizer getDepthQuantizer() {
        return depthQuantizer;
    }

    public Display getDisplay() {
        return display;
    }

    public Exposure getExposure() {
        return exposure;
    }

    public float getFarClipping() {
        return farClipping;
    }

    public Filter getFilter() {
        return filter;
    }

    public float getFocalDistance() {
        return focalDistance;
    }

    public float getFocalLength() {
        return focalLength;
    }

    public float getFrameAspectRatio() {
        return frameAspectRatio;
    }

    public float getFStop() {
        return fStop;
    }

    public Hider getHider() {
        return hider;
    }

    public int getHorizontalResolution() {
        return horizontalResolution;
    }

    public float getHorizontalSamplingRate() {
        return horizontalSamplingRate;
    }

    public Imager getImager() {
        return imager;
    }

    public float getNearClipping() {
        return nearClipping;
    }

    public float getPixelAspectRatio() {
        return pixelAspectRatio;
    }

    public float getPixelVariance() {
        return pixelVariance;
    }

    public float getRelativeDetail() {
        return relativeDetail;
    }

    public Bounds2f getScreenWindow() {
        return screenWindow;
    }

    public float getShutterClose() {
        return shutterClose;
    }

    public float getShutterOpen() {
        return shutterOpen;
    }

    public int getVerticalResolution() {
        return verticalResolution;
    }

    public float getVerticalSamplingRate() {
        return verticalSamplingRate;
    }

    public void setCameraProjection(CameraProjection projection) {
        cameraProjection = projection;
    }

    public void setColorQuantizer(Quantizer quantizer) {
        colorQuantizer = quantizer;
    }

    public void setColorSamples(int i) {
        colorSamples = i;
    }

    public void setCropWindow(Bounds2f bounds2f) {
        cropWindow = bounds2f;
    }

    public void setDepthQuantizer(Quantizer quantizer) {
        depthQuantizer = quantizer;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void setExposure(Exposure exposure) {
        this.exposure = exposure;
    }

    public void setFarClipping(float f) {
        farClipping = f;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setFocalDistance(float f) {
        focalDistance = f;
    }

    public void setFocalLength(float f) {
        focalLength = f;
    }

    public void setFrameAspectRatio(float f) {
        frameAspectRatio = f;
    }

    public void setFStop(float f) {
        fStop = f;
    }

    public void setHider(Hider hider) {
        this.hider = hider;
    }

    public void setHorizontalResolution(int i) {
        horizontalResolution = i;
    }

    public void setHorizontalSamplingRate(float f) {
        horizontalSamplingRate = f;
    }

    public void setImager(Imager imager) {
        this.imager = imager;
    }

    public void setNearClipping(float f) {
        nearClipping = f;
    }

    public void setPixelAspectRatio(float f) {
        pixelAspectRatio = f;
    }

    public void setPixelVariance(float f) {
        pixelVariance = f;
    }

    public void setRelativeDetail(float f) {
        relativeDetail = f;
    }

    public void setScreenWindow(Bounds2f bounds2f) {
        screenWindow = bounds2f;
    }

    public void setShutterClose(float f) {
        shutterClose = f;
    }

    public void setShutterOpen(float f) {
        shutterOpen = f;
    }

    public void setVerticalResolution(int i) {
        verticalResolution = i;
    }

    public void setVerticalSamplingRate(float f) {
        verticalSamplingRate = f;
    }

    public void setFrameNumber(int n) {
        frameNumber = n;
    }

    public void setMotionTimes(float[] times) {
        this.motionTimes = new float[times.length];
        System.arraycopy(times, 0, this.motionTimes, 0, times.length);
    }
    
    public float[] getMotionTimes() {
        float[] result = new float[motionTimes.length];
        System.arraycopy(motionTimes, 0, result, 0, motionTimes.length);
        return result;
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFieldOfView(float f) {
        fieldOfView = f;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public void setOriginX(int i) {
        originX = i;
    }

    public void setOriginY(int i) {
        originY = i;
    }

    public int getBucketSizeX() {
        return bucketSizeX;
    }

    public int getBucketSizeY() {
        return bucketSizeY;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setBucketSizeX(int i) {
        bucketSizeX = i;
    }

    public void setBucketSizeY(int i) {
        bucketSizeY = i;
    }

    public void setGridSize(int i) {
        gridSize = i;
        int side = (int) (Math.ceil(Math.sqrt(i))) + 1;
        Grid.setMaxSize(side * side);
    }

    public ParameterList getHiderParameters() {
        return hiderParameters;
    }

    public void setHiderParameters(ParameterList parameters) {
        hiderParameters = parameters;
    }

    public Transform getRasterTransform() {
        return AffineTransform.createRaster(
            screenWindow,
            frameAspectRatio,
            horizontalResolution,
            verticalResolution,
            pixelAspectRatio);
    }

    public Transform getNDCTransform() {
        return AffineTransform.createNDC(horizontalResolution, verticalResolution);
    }

    public boolean endOfFrameStatisticsEnabled() {
        return endOfFrameStatistics;
    }

    public void setEndOfFrameStatistics(boolean b) {
        endOfFrameStatistics = b;
	}

	/**
	 * @return Returns the showProgress.
	 */
	public boolean isShowProgressEnabled() {
		return showProgressEnabled;
	}

	/**
	 * @param showProgress
	 *            The showProgress to set.
	 */
	public void setShowProgressEnabled(boolean showProgress) {
		this.showProgressEnabled= showProgress;
	}

	/**
	 * @return Returns the framebufferAlways.
	 */
	public boolean isFramebufferAlways() {
		return framebufferAlways;
	}

	/**
	 * @param framebufferAlways The framebufferAlways to set.
	 */
	public void setFramebufferAlways(boolean framebufferAlways) {
		this.framebufferAlways= framebufferAlways;
	}

}
