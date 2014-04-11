/*
 * RendererHidden.java Copyright (C) 2003, 2006  Gerardo Horvilleur Martinez
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jrman.render;

import java.io.File;

import javax.imageio.ImageIO;
import javax.vecmath.Color3f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.Bounds2f;
import org.jrman.geom.ClippingVolume;
import org.jrman.geom.PerspectiveTransform;
import org.jrman.geom.Plane;
import org.jrman.geom.Transform;
import org.jrman.grid.Point3fGrid;
import org.jrman.maps.ShadowMap;
import org.jrman.options.CameraProjection;
import org.jrman.options.Display;
import org.jrman.options.Filter;
import org.jrman.options.JRManStringResource;
import org.jrman.options.Quantizer;
import org.jrman.parameters.ParameterList;
import org.jrman.parser.Frame;
import org.jrman.parser.Global;
import org.jrman.parser.Parser;
import org.jrman.parser.World;
import org.jrman.primitive.Primitive;
import org.jrman.shaders.DisplacementShader;
import org.jrman.shaders.SurfaceShader;
import org.jrman.shaders.VolumeShader;
import org.jrman.ui.Framebuffer;
import org.jrman.util.Calc;
import org.jrman.util.Format;
import org.jrman.util.SamplesFilter;

public class RendererHidden extends Renderer {
    
    private final static int MAX_EYE_SPLITS = 20;
    
    private final static Vector3f ORTHO_I = new Vector3f(0f, 0f, 1f);
    
    private ClippingVolume clippingVolume;
    
    private Plane eyePlane;
    
    private Transform worldToCamera;
    
    private Transform cameraToRaster;
    
    private Bounds2f rasterWindow;
    
    private Point2f rasterWindowMin;
    
    private Bucket[] buckets;
    
    private int bucketColumns;
    
    private int bucketRows;
    
    private int bucketWidth;
    
    private int bucketHeight;
    
    private int primitivePatchCount;
    
    private int invisiblePatchCount;
    
    private int clippedPrimitivesCount;
    
    private long worldParseStart;
    
    private long worldParseEnd;
    
    private int hFilterExtra;
    
    private int vFilterExtra;
    
    private int hPixelLess;
    
    private int vPixelLess;
    
    private SamplesFilter samplesFilter;
    
    private int hExtra;
    
    private int wExtra;
    
    private Transform oldObjectToWorld;
    
    private Transform oldObjectToCamera;
    
    private int row;
    
    private int column;
    
    private boolean rendering;
    
    private Point3fGrid pointsTmp;
    
    public void init(Frame frame, World world, Parser parser) {
        super.init(frame, world, parser);
        worldToCamera = Global.getTransform("camera");
        Transform cameraToScreen = Global.getTransform("screen");
        Transform screenToRaster = Global.getTransform("raster");
        if (cameraToScreen instanceof PerspectiveTransform)
            cameraToRaster = ((PerspectiveTransform) cameraToScreen).preConcat(screenToRaster);
        else
            cameraToRaster = screenToRaster.concat(cameraToScreen);
        eyePlane = Plane.createWithPointAndNormal(new Point3f(), new Vector3f(0f, 0f, -1f));
        createClippingVolume();
        createBuckets();
        samplesFilter = frame.getFilter().getSamplesFilter();
        pointsTmp = new Point3fGrid();
        worldParseStart = System.currentTimeMillis();
    }
    
    public void addPrimitive(Primitive primitive) {
        internalAddPrimitive(primitive, column, row, rendering);
    }
    
    private void internalAddPrimitive(
            Primitive primitive,
            int curColumn,
            int curRow,
            boolean check) {
        Transform objectToCamera = primitive.getObjectToCamera();
        if (objectToCamera == null) {
            Transform objectToWorld = primitive.getAttributes().getTransform();
            if (oldObjectToWorld != null && oldObjectToWorld == objectToWorld)
                objectToCamera = oldObjectToCamera;
            else {
                objectToCamera = worldToCamera.concat(objectToWorld);
                oldObjectToWorld = objectToWorld;
                oldObjectToCamera = objectToCamera;
            }
            primitive.setObjectToCamera(objectToCamera);
        }
        if (!addPrimitive(primitive, 0, objectToCamera, curColumn, curRow, check))
            System.err.println("Can't split primitive at eye plane: " + primitive);
    }
    
    private boolean addPrimitive(
            Primitive primitive,
            int recursionLevel,
            Transform objectToCamera,
            int curColumn,
            int curRow,
            boolean check) {
        BoundingVolume bv = primitive.getBoundingVolume();
        float db = primitive.getAttributes().getDisplacementBound();
        BoundingVolume bve;
        if (db != 0f) {
            bve = bv.enlarge(db);
            bve = bve.transform(objectToCamera);
            bv = bv.transform(objectToCamera);
        } else {
            bv = bv.transform(objectToCamera);
            bve = bv;
        }
        if (clippingVolume.whereIs(bve) == Plane.Side.OUTSIDE) {
            clippedPrimitivesCount++;
            return true;
        }
        Plane.Side side = bv.whichSideOf(eyePlane);
        if (side == Plane.Side.INSIDE) {
            placeInBucket(primitive, bve, bv, curColumn, curRow, check);
            return true;
        }
        // BOTH_SIDES
        if (recursionLevel == MAX_EYE_SPLITS)
            return false;
        Primitive[] sub = primitive.split();
        boolean result = true;
        for (int i = 0; i < sub.length; i++)
            if (!addPrimitive(sub[i],
                recursionLevel + 1,
                objectToCamera,
                curColumn,
                curRow,
                check))
                result = false;
        return result;
    }
    
    private void placeInBucket(
            Primitive primitive,
            BoundingVolume bve,
            BoundingVolume bv,
            int column,
            int row,
            boolean check) {
        primitive.setDistance(bve.getMinZ());
        bve = bve.transform(cameraToRaster);
        Bounds2f bounds = bve.toBounds2f();
        float db = primitive.getAttributes().getDisplacementBound();
        if (db != 0f) {
            bv = bv.transform(cameraToRaster);
            Bounds2f bnd = bv.toBounds2f();
            float dw = bounds.getWidth() - bnd.getWidth();
            float dh = bounds.getHeight() - bnd.getHeight();
            primitive.setScreenDisplacementWidth(dw);
            primitive.setScreenDisplacementHeight(dh);
        }
        primitive.setRasterBounds(bounds);
        doPlaceInBucket(primitive, column, row, check);
    }
    
    private void doPlaceInBucket(
            Primitive primitive,
            int curColumn,
            int curRow,
            boolean check) {
        Bounds2f bounds = primitive.getRasterBounds();
        if (!bounds.intersects(rasterWindow))
            return;
        float minX = bounds.getMinX() - rasterWindow.getMinX();
        float minY = bounds.getMinY() - rasterWindow.getMinY();
        int column = Calc.clamp((int) (minX / bucketWidth), 0, bucketColumns - 1);
        int row = Calc.clamp((int) (minY / bucketHeight), 0, bucketRows - 1);
        if (check && row <= curRow) {
            float maxY = bounds.getMaxY() - rasterWindow.getMinY();
            int maxRow = Calc.clamp((int) Math.ceil(maxY / bucketHeight), 0, bucketRows - 1);
            if (maxRow < curRow)
                return;
            row = curRow;
            float maxX = bounds.getMaxX() - rasterWindow.getMinX();
            int maxColumn =
                    Calc.clamp((int) Math.ceil(maxX / bucketWidth), 0, bucketColumns - 1);
            if (column <= curColumn && curColumn <= maxColumn) {
                getBucket(curColumn, curRow).addPrimitive(primitive);
                return;
            }
            if (column < curColumn) {
                if (maxRow < curRow + 1)
                    return;
                if (curRow == bucketRows - 1)
                    return;
                row++;
            }
        }
        getBucket(column, row).addPrimitive(primitive);
    }
    
    public void addToBuckets(Micropolygon mp) {
        if (mp.getMaxX() < rasterWindowMin.x || mp.getMaxY() < rasterWindowMin.y)
            return;
        float mpMinX = mp.getMinX() - rasterWindowMin.x;
        float mpMinY = mp.getMinY() - rasterWindowMin.y;
        float mpMaxX = mp.getMaxX() - rasterWindowMin.x;
        float mpMaxY = mp.getMaxY() - rasterWindowMin.y;
        int minColumn = (int) (mpMinX / bucketWidth);
        int minRow = (int) (mpMinY / bucketHeight);
        int maxColumn = (int) (mpMaxX / bucketWidth);
        int maxRow = (int) (mpMaxY / bucketHeight);
        for (int col = minColumn; col <= maxColumn; col++)
            for (int row = minRow; row <= maxRow; row++)
                if (col >= 0 && col < bucketColumns && row >= 0 && row < bucketRows)
                    getBucket(col, row).addMicropolygon(mp);
    }
    
    private Bucket getBucket(int column, int row) {
        int idx = row * bucketColumns + column;
        if (buckets[idx] == null)
            buckets[idx] = new MemoryBucket();
        return buckets[idx];
    }
    
    private void discardBucket(int column, int row) {
        buckets[row * bucketColumns + column] = DummyBucket.UNIQUE;
    }
    
    public void render() {
        rendering = true;
        worldParseEnd = System.currentTimeMillis();
        Display.Mode displayMode = frame.getDisplay().getMode();
        Display.Type displayType = frame.getDisplay().getType();
        ImageStore imageStore =
                new ImageStore(
                frame.getHorizontalResolution(),
                frame.getVerticalResolution(),
                frame.getDisplay());
        ZStore zStore = null;
        if (displayMode == Display.Mode.Z) {
            zStore =
                    new ZStore(frame.getHorizontalResolution(), frame.getVerticalResolution());
            ShadowMap.flushShadowMap(frame.getDisplay().getName());
        }
        Framebuffer fb = null;
        if (displayType == Display.Type.FRAMEBUFFER || frame.isFramebufferAlways()) {
            try {
                // fb = new Framebuffer(frame.getDisplay().getName(), imageStore.getImage());
                fb = (Framebuffer)
                     (Class.forName(JRManStringResource.getInstance().getFramebufferClass())
                           .getConstructor(String.class, java.awt.image.BufferedImage.class)
                     ).newInstance(frame.getDisplay().getName(), imageStore.getImage());

                fb.setVisible(true);
            } catch (Exception e) {
                // ignored
                e.printStackTrace();
            } // end for
        }
        
        int gridSize = frame.getGridSize();
        ShaderVariables shaderVariables = new ShaderVariables(worldToCamera.getInverse());
        int gridCount = 0;
        Sampler sampler =
                new Sampler(
                bucketWidth,
                bucketHeight,
                frame.getHorizontalSamplingRate(),
                frame.getVerticalSamplingRate(),
                frame);
        Filter filter = frame.getFilter();
        samplesFilter.init(
                (int) Math.ceil(filter.getHorizontalWidth() * sampler.getPixelWidth()),
                (int) Math.ceil(filter.getVerticalWidth() * sampler.getPixelHeight()),
                filter.getHorizontalWidth(),
                filter.getVerticalWidth());
        float[] samples =
                new float[sampler.getWidth()
                * bucketColumns
                * (sampler.getHeight() + vFilterExtra + vPixelLess * sampler.getPixelHeight())
                * 4];
        float[] filteredSamples = new float[bucketWidth * bucketHeight * 4];
        int[] pixels = new int[bucketWidth * bucketHeight];
        float[] depths = new float[bucketWidth * bucketHeight];
        long start = System.currentTimeMillis();
        Point2f rmin = rasterWindow.getMin();
        int percentComplete = -1;
        for (row = 0; row < bucketRows; row++) {
            for (column = 0; column < bucketColumns; column++) {
                Bucket bucket = getBucket(column, row);
                bucket.sortPrimitives();
                float samplerX = rmin.x + bucketWidth * column;
                float samplerY = rmin.y + bucketHeight * row;
                sampler.init(samplerX, samplerY);
                while (bucket.hasMorePrimitives()) {
                    primitivePatchCount++;
                    Primitive p = bucket.getNextPrimitive();
                    if (isPrimitiveVisible(p, sampler)) {
                        if (p.isReadyToBeDiced(gridSize)) {
                            gridCount++;
                            p.dice(shaderVariables);
                            Attributes attr = p.getAttributes();
                            DisplacementShader ds = attr.getDisplacement();
                            if (ds != null) {
                                Attributes attrShader = ds.getAttributes();
                                boolean ts = attrShader.getTrueDisplacement();
                                if (!ts)
                                    pointsTmp.set(shaderVariables.P);
                                ds.shade(shaderVariables);
                                if (!ts)
                                    shaderVariables.P.set(pointsTmp);
                            }
                            if (cameraToRaster.isPerspective())
                                shaderVariables.I.set(shaderVariables.P);
                            else
                                shaderVariables.I.set(ORTHO_I);
                            SurfaceShader surface = attr.getSurface();
                            if (surface == null)
                                surface =
                                        SurfaceShader.createShader(
                                        "fakedlight",
                                        new ParameterList(),
                                        attr);
                            surface.shade(shaderVariables);
                            VolumeShader vs = attr.getAtmosphere();
                            if (vs != null)
                                vs.shade(
                                        shaderVariables,
                                        frame.getNearClipping(),
                                        frame.getFarClipping());
                            shaderVariables.transform(cameraToRaster);
                            shaderVariables.getMicropolygons(this);
                            bucket.sortMicropolygons();
                            sampler.sampleBucket(bucket);
                        } else {
                            Primitive[] sub = p.split();
                            for (int i = 0; i < sub.length; i++) {
                                sub[i].setObjectToCamera(p.getObjectToCamera());
                                internalAddPrimitive(sub[i], column, row, true);
                            }
                            if (p.shouldSortBucket())
                                bucket.sortPrimitives();
                        }
                    } else {
                        placeInNextBucket(p, column, row);
                        invisiblePatchCount++;
                    }
                }
                if (bucket.hasMoreMicropolygons())
                    sampler.sampleBucket(bucket);
                discardBucket(column, row);
                if (displayMode == Display.Mode.RGB || displayMode == Display.Mode.RGBA) {
                    sampler.getColors(
                            samples,
                            (column * sampler.getWidth()
                            + (row == 0 ? 0 : 1)
                            * (vFilterExtra + vPixelLess * sampler.getPixelHeight())
                            * bucketColumns
                            * sampler.getWidth())
                            * 4,
                            bucketColumns * sampler.getWidth(),
                            4,
                            0);
                    samplesFilter.doFilter(
                            samples,
                            (column * sampler.getWidth()
                            - (column == 0
                            ? 0
                            : (hFilterExtra + hPixelLess * sampler.getPixelWidth())))
                            * 4,
                            bucketColumns * sampler.getWidth(),
                            filteredSamples,
                            0,
                            bucketWidth,
                            bucketWidth - (column == 0 ? hPixelLess : 0),
                            bucketHeight - (row == 0 ? vPixelLess : 0),
                            sampler.getPixelWidth(),
                            sampler.getPixelHeight(),
                            4);
                    colorsToPixels(filteredSamples, pixels);
                    imageStore.setPixels(
                            pixels,
                            column * bucketWidth - (column == 0 ? 0 : hPixelLess) + (int) rmin.x,
                            row * bucketHeight - (row == 0 ? 0 : vPixelLess) + (int) rmin.y,
                            bucketWidth
                            - (column == 0 ? hPixelLess : 0)
                            - (column == bucketColumns - 1 ? wExtra : 0),
                            bucketHeight
                            - (row == 0 ? vPixelLess : 0)
                            - (row == bucketRows - 1 ? hExtra : 0),
                            bucketWidth);
                    if (fb != null)
                        fb.refresh(
                                column * bucketWidth
                                - (column == 0 ? 0 : hPixelLess)
                                + (int) rmin.x,
                                row * bucketHeight - (row == 0 ? 0 : vPixelLess) + (int) rmin.y,
                                bucketWidth - (column == 0 ? hPixelLess : 0),
                                bucketHeight - (row == 0 ? vPixelLess : 0));
                } else if (displayMode == Display.Mode.Z) {
                    sampler.getDepths(depths);
                    zStore.setDepths(
                            depths,
                            column * bucketWidth + (int) rmin.x,
                            row * bucketHeight + (int) rmin.y,
                            bucketWidth,
                            bucketHeight,
                            bucketWidth);
                }
                if (frame.isShowProgressEnabled()) {
                    int percent = (row * bucketColumns + column + 1) * 100 / buckets.length;
                    if (percent > percentComplete) {
                        percentComplete = percent;
                        System.out.print(
                                "\r"
                                + frame.getDisplay().getName()
                                + " (frame "
                                + frame.getFrameNumber()
                                + "): "
                                + percentComplete
                                + "% complete");
                    }
                    
                }
            }
            System.arraycopy(
                    samples,
                    (bucketHeight * sampler.getPixelHeight()
                    - (row == 0 ? (vFilterExtra + vPixelLess * sampler.getPixelHeight()) : 0))
                    * bucketColumns
                    * sampler.getWidth()
                    * 4,
                    samples,
                    0,
                    (vFilterExtra + vPixelLess * sampler.getPixelHeight())
                    * bucketColumns
                    * sampler.getWidth()
                    * 4);
        }
        if (frame.isShowProgressEnabled())
            System.out.println();
        if (fb != null)
            fb.completed();
        if (displayType == Display.Type.FILE)
            try {
                if (displayMode == Display.Mode.RGB || displayMode == Display.Mode.RGBA) {
                    String name = frame.getDisplay().getName();
                    String currentDirectory = parser.getCurrentDirectory();
                    if (currentDirectory != null)
                        name = currentDirectory + File.separator + name;
                    String ext = name.substring(name.lastIndexOf('.') + 1);
                    File file = new File(name);
                    ImageIO.write(imageStore.getImage(), ext, file);
                } else if (displayMode == Display.Mode.Z)
                    ShadowMap.writeShadowMap(
                            frame.getDisplay().getName(),
                            worldToCamera,
                            cameraToRaster,
                            frame.getHorizontalResolution(),
                            frame.getVerticalResolution(),
                            zStore.getDepths());
            } catch (Exception e) {
                System.err.println("Error writing image file: " + e);
            }
        long end = System.currentTimeMillis();
        float time = (end - start) / 1000f;
        displayStats(gridCount, sampler, time);
        rendering = false;
    }
    
    private void displayStats(int gridCount, Sampler sampler, float time) {
        if (frame.endOfFrameStatisticsEnabled()) {
            System.out.println("*** Frame Statistics ***");
            System.out.println(
                    "World parse time: " + Format.time(worldParseEnd - worldParseStart));
            display("spheres", parser.getSphereCount());
            display("cylinders", parser.getCylinderCount());
            display("toruses", parser.getTorusCount());
            display("cones", parser.getConeCount());
            display("disks", parser.getDiskCount());
            display("paraboloids", parser.getParaboloidCount());
            display("hyperboloids", parser.getHyperboloidCount());
            display("bilinear patches", parser.getBilinearPatchCount());
            display("bicubic patches", parser.getBicubicPatchCount());
            display("polygons", parser.getPolygonCount());
            display("points", parser.getPointCount());
            display("nurbs", parser.getNurbsCount());
            display("curves", parser.getCurveCount());
            System.out.println("Clipped primitives: " + clippedPrimitivesCount);
            System.out.println("Total patches: " + primitivePatchCount);
            System.out.println("Patches/second: " + primitivePatchCount / time);
            System.out.println("Invisible patches: " + invisiblePatchCount);
            System.out.println(
                    "Patches culled: " + invisiblePatchCount * 100f / primitivePatchCount + "%");
            System.out.println("Root count: " + sampler.getRootCount());
            System.out.println("Pixel count: " + sampler.getPixelCount());
            System.out.println("MP Root count: " + sampler.getMPRootCount());
            System.out.println(
                    "gridCount = "
                    + gridCount
                    + " in "
                    + time
                    + " seconds\n"
                    + gridCount / time
                    + "grids/second");
            System.out.println("Total micropolygons: " + Micropolygon.count);
            System.out.println("Micropolygons/second: " + Micropolygon.count / time);
            System.out.println("---------------------------------------------------");
            clippedPrimitivesCount = 0;
            primitivePatchCount = 0;
            invisiblePatchCount = 0;
            gridCount = 0;
            Micropolygon.count = 0;
            parser.resetCounts();
        }
    }
    
    private void display(String primitiveName, int count) {
        if (count != 0)
            System.out.println("Total " + primitiveName + ": " + count);
    }
    
    private boolean isPrimitiveVisible(Primitive p, Sampler sampler) {
        return sampler.isVisible(p.getRasterBounds(), p.getDistance());
    }
    
    private void placeInNextBucket(Primitive p, int column, int row) {
        if (column < bucketColumns - 1)
            doPlaceInBucket(p, column + 1, row, true);
        else if (row < bucketRows - 1)
            doPlaceInBucket(p, 0, row + 1, true);
    }
    
    private void colorsToPixels(float[] colors, int[] result) {
        Color3f tmp = new Color3f();
        for (int row = 0; row < bucketHeight; row++)
            for (int col = 0; col < bucketWidth; col++) {
            int offset = (row * bucketWidth + col) * 4;
            tmp.x = colors[offset++];
            tmp.y = colors[offset++];
            tmp.z = colors[offset++];
            float alphaFloat = colors[offset];
            frame.getExposure().expose(tmp, tmp);
            Quantizer qt = frame.getColorQuantizer();
            int red = qt.quantize(tmp.x);
            int green = qt.quantize(tmp.y);
            int blue = qt.quantize(tmp.z);
            int alpha = qt.quantize(alphaFloat);
            offset = row * bucketWidth + col;
            result[offset] = ((alpha << 24) | (red << 16) | (green << 8) | blue);
            }
    }
    
    private void createClippingVolume() {
        rasterWindow = createRasterWindow();
        rasterWindowMin = rasterWindow.getMin();
        Transform rasterToScreen = Global.getTransform("raster").getInverse();
        Bounds2f renderingScreenWindow = rasterWindow.transform(rasterToScreen);
        Point2f min = renderingScreenWindow.getMin();
        Point2f max = renderingScreenWindow.getMax();
        clippingVolume = new ClippingVolume();
        if (frame.getCameraProjection() == CameraProjection.PERSPECTIVE) {
            float focalLength = Calc.fovToFocalLength(frame.getFieldOfView());
            Point3f origin = new Point3f();
            Point3f upperLeft = new Point3f(min.x, max.y, focalLength);
            Point3f upperRight = new Point3f(max.x, max.y, focalLength);
            Point3f lowerLeft = new Point3f(min.x, min.y, focalLength);
            Point3f lowerRight = new Point3f(max.x, min.y, focalLength);
            clippingVolume.addPlane(
                    Plane.createWithThreePoints(origin, upperLeft, upperRight));
            clippingVolume.addPlane(
                    Plane.createWithThreePoints(origin, upperRight, lowerRight));
            clippingVolume.addPlane(
                    Plane.createWithThreePoints(origin, lowerRight, lowerLeft));
            clippingVolume.addPlane(Plane.createWithThreePoints(origin, lowerLeft, upperLeft));
        } else {
            Point3f upperLeft = new Point3f(min.x, max.y, 1f);
            Point3f upperRight = new Point3f(max.x, max.y, 1f);
            Point3f lowerLeft = new Point3f(min.x, min.y, 1f);
            Point3f lowerRight = new Point3f(max.x, min.y, 1f);
            Point3f originUpperLeft = new Point3f(min.x, max.y, 0f);
            Point3f originUpperRight = new Point3f(max.x, max.y, 0f);
            Point3f originLowerLeft = new Point3f(min.x, min.y, 0f);
            Point3f originLowerRight = new Point3f(max.x, min.y, 0f);
            clippingVolume.addPlane(
                    Plane.createWithThreePoints(originUpperLeft, upperLeft, upperRight));
            clippingVolume.addPlane(
                    Plane.createWithThreePoints(originUpperRight, upperRight, lowerRight));
            clippingVolume.addPlane(
                    Plane.createWithThreePoints(originLowerRight, lowerRight, lowerLeft));
            clippingVolume.addPlane(
                    Plane.createWithThreePoints(originLowerLeft, lowerLeft, upperLeft));
        }
        float near = frame.getNearClipping();
        clippingVolume.addPlane(
                Plane.createWithPointAndNormal(
                new Point3f(0f, 0f, near),
                new Vector3f(0f, 0f, -1f)));
        float far = frame.getFarClipping();
        clippingVolume.addPlane(
                Plane.createWithPointAndNormal(
                new Point3f(0f, 0f, far),
                new Vector3f(0f, 0f, 1f)));
    }
    
    private Bounds2f createRasterWindow() {
        Bounds2f cropWindow = frame.getCropWindow();
        Point2f min = cropWindow.getMin();
        Point2f max = cropWindow.getMax();
        int xResolution = frame.getHorizontalResolution();
        int yResolution = frame.getVerticalResolution();
        float rxmin = Calc.clamp((float) Math.ceil(xResolution * min.x), 0f, xResolution - 1);
        float rxmax =
                Calc.clamp((float) Math.ceil(xResolution * max.x - 1f), 0f, xResolution - 1);
        float rymin = Calc.clamp((float) Math.ceil(yResolution * min.y), 0f, yResolution - 1);
        float rymax =
                Calc.clamp((float) Math.ceil(yResolution * max.y - 1f), 0f, yResolution - 1);
        Filter filter = frame.getFilter();
        float xw = (filter.getHorizontalWidth() - 1) / 2f;
        hFilterExtra = (int) Math.ceil(xw * frame.getHorizontalSamplingRate());
        hPixelLess = (int) Math.ceil((float) hFilterExtra / frame.getHorizontalSamplingRate());
        float yw = (filter.getVerticalWidth() - 1) / 2f;
        vFilterExtra = (int) Math.ceil(yw * frame.getVerticalSamplingRate());
        vPixelLess = (int) Math.ceil((float) vFilterExtra / frame.getVerticalSamplingRate());
        rxmin = (float) Math.floor(rxmin - xw);
        rxmax = (float) Math.ceil(rxmax + xw);
        rymin = (float) Math.floor(rymin - yw);
        rymax = (float) Math.ceil(rymax + yw);
        return new Bounds2f(rxmin, rxmax, rymin, rymax);
    }
    
    private void createBuckets() {
        Point2f min = rasterWindow.getMin();
        Point2f max = rasterWindow.getMax();
        float width = max.x - min.x + 1;
        float height = max.y - min.y + 1;
        bucketWidth = frame.getBucketSizeX();
        bucketHeight = frame.getBucketSizeY();
        bucketColumns = (int) Math.ceil(width / bucketWidth);
        bucketRows = (int) Math.ceil(height / bucketHeight);
        buckets = new Bucket[bucketColumns * bucketRows];
        if (width < frame.getHorizontalResolution())
            wExtra = bucketWidth - (int) width % bucketWidth;
        else
            wExtra = 0;
        if (height < frame.getVerticalResolution())
            hExtra = bucketHeight - (int) height % bucketHeight;
        else
            hExtra = 0;
    }
    
}
