/*
 * Parser.java
 * Copyright (C) 2003, 2004, 2006 Gerardo Horvilleur Martinez
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

package org.jrman.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.jrman.attributes.Attributes;
import org.jrman.attributes.Basis;
import org.jrman.attributes.DetailRange;
import org.jrman.attributes.GeometricApproximation;
import org.jrman.attributes.MutableAttributes;
import org.jrman.attributes.Orientation;
import org.jrman.attributes.ShadingInterpolation;
import org.jrman.attributes.Space;
import org.jrman.attributes.TextureCoordinates;
import org.jrman.attributes.TrimCurveSense;
import org.jrman.geom.AffineTransform;
import org.jrman.geom.Bounds2f;
import org.jrman.geom.Bounds3f;
import org.jrman.geom.PerspectiveTransform;
import org.jrman.geom.Transform;
import org.jrman.main.JRMan;
import org.jrman.maps.MipMap;
import org.jrman.options.CameraProjection;
import org.jrman.options.Display;
import org.jrman.options.Exposure;
import org.jrman.options.Filter;
import org.jrman.options.Hider;
import org.jrman.options.Quantizer;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.UniformArrayInteger;
import org.jrman.parameters.UniformScalarFloat;
import org.jrman.parameters.UniformScalarInteger;
import org.jrman.parameters.UniformScalarString;
import org.jrman.parameters.VaryingArrayFloat;
import org.jrman.parameters.VaryingScalarFloat;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.parser.keywords.AbstractKeywordParser;
import org.jrman.parser.keywords.KeywordParser;
import org.jrman.primitive.BicubicPatch;
import org.jrman.primitive.BilinearPatch;
import org.jrman.primitive.Cone;
import org.jrman.primitive.CubicCurve;
import org.jrman.primitive.Cylinder;
import org.jrman.primitive.DelayedReadArchive;
import org.jrman.primitive.Disk;
import org.jrman.primitive.Hyperboloid;
import org.jrman.primitive.LinearCurve;
import org.jrman.primitive.NurbsRI;
import org.jrman.primitive.ObjectInstance;
import org.jrman.primitive.Paraboloid;
import org.jrman.primitive.Point;
import org.jrman.primitive.PointsPolygons;
import org.jrman.primitive.Primitive;
import org.jrman.primitive.Sphere;
import org.jrman.primitive.Torus;
import org.jrman.render.Renderer;
import org.jrman.shaders.DisplacementShader;
import org.jrman.shaders.Imager;
import org.jrman.shaders.LightShader;
import org.jrman.shaders.SurfaceShader;
import org.jrman.shaders.VolumeShader;
import org.jrman.util.Constants;

public class Parser {

    private final static String KEYWORD_PREFIX = "org.jrman.parser.keywords.Keyword";
    
    private final static Map<String, String> fullFileNames
                                               = new HashMap<String, String>();
    
    private String currentDirectory;

    private boolean inAreaLightSource;

    private String beginName;

    private World world;

    private Frame frame;

    private Renderer renderer;

    private Map<String, KeywordParser> keywordParsers
                                       = new HashMap<String, KeywordParser>();

    private MutableAttributes currentAttributes;

    private Attributes currentImmutableAttributes;

    private Stack<Attributes> attributeStack = new Stack<Attributes>();

    private Stack<Transform> transformStack = new Stack<Transform>();

    private Stack<World> worldStack = new Stack<World>();

    private Stack<Frame> frameStack = new Stack<Frame>();

    private Stack<State> stateStack = new Stack<State>();

    private State state = State.OUTSIDE;

    private String currentOperation; // Just now to avoid compiler warnings...

    private Map<Integer, ObjectInstanceList> objectInstanceLists
                   = new HashMap<Integer, ObjectInstanceList>();

    private ObjectInstanceList currentObjectInstanceList;

    private boolean inObject;

    private CommandLine cmdLine;

    private int firstFrame = -1;

    private int lastFrame = -1;
    
    private int sphereCount;
    
    private int torusCount;
    
    private int cylinderCount;
    
    private int coneCount;
    
    private int diskCount;
    
    private int paraboloidCount;
    
    private int hyperboloidCount;
    
    private int bilinearPatchCount;
    
    private int bicubicPatchCount;
    
    private int polygonCount;
    
    private int pointCount;
    
    private int nurbsCount;
    
    private int curveCount;
    
    public static class State {

        public final static State OUTSIDE = new State("OUTSIDE");

        public final static State BEGIN_END = new State("BEGIN_END");

        public final static State FRAME = new State("FRAME");

        public final static State WORLD = new State("WORLD");

        public final static State ATTRIBUTE = new State("ATTRIBUTE");

        public final static State TRANSFORM = new State("TRANSFORM");

        public final static State SOLID = new State("SOLID");

        public final static State MOTION = new State("MOTION");

        public final static State OBJECT = new State("OBJECT");

        private String name;

        public State(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
    
    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    
    public String getCurrentDirectory() {
        return currentDirectory;
    }
    
    public void parse(String filename) throws Exception {
        if (currentDirectory != null) {
            String fullFileName = (String) fullFileNames.get(filename);
            if (fullFileName == null) {
                fullFileName = currentDirectory + File.separator + filename;
                fullFileNames.put(filename, fullFileName);
            }
            filename = fullFileName;
        }
        FileReader fr = new FileReader(filename);
        Tokenizer st = new Tokenizer(new BufferedReader(fr));
        // st.commentChar('#');
        int tk;
        while ((tk = st.nextToken()) != StreamTokenizer.TT_EOF) {
            try {
                if (tk != StreamTokenizer.TT_WORD)
                    throw new Exception("Expected keyword at line " + st.lineno());
                String keyword = st.sval;
                KeywordParser kp = getKeyWordParser(keyword);
                if (!kp.getValidStates().contains(state))
                    throw new IllegalStateException(
                        "Keyword"
                            + kp
                            + " is not valid in state "
                            + state
                            + ", at line "
                            + st.lineno());
                kp.parse(st);
            } catch (Exception pe) {
                System.err.println("Error: " + pe);
                pe.printStackTrace();
            }
        }
        fr.close();
    }

    public Parser(CommandLine cmdLine) throws ParseException {
        if (cmdLine == null)
            throw new NullPointerException("Command line cannot be null");
        this.cmdLine = cmdLine;
        // parse frame subset numbers
        String frameValue = "";
        try {
            if (cmdLine.hasOption(JRMan.OPTION_FIRSTFRAME)) {
                frameValue = cmdLine.getOptionValue(JRMan.OPTION_FIRSTFRAME);
                firstFrame = Integer.parseInt(frameValue);
            }
            if (cmdLine.hasOption(JRMan.OPTION_LASTFRAME)) {
                frameValue = cmdLine.getOptionValue(JRMan.OPTION_LASTFRAME);
                lastFrame = Integer.parseInt(frameValue);
            }
        } catch (NumberFormatException nfe) {
            throw new ParseException(frameValue + " is not a valid frame number");
        }
        world = new World();
        frame = new Frame();
        frame.setEndOfFrameStatistics(cmdLine.hasOption(JRMan.OPTION_STATISTICS));
        frame.setShowProgressEnabled(cmdLine.hasOption(JRMan.OPTION_PROGRESS));
        frame.setFramebufferAlways(cmdLine.hasOption(JRMan.OPTION_FRAMEBUFFER));
        currentAttributes = new MutableAttributes();
    }

    private KeywordParser getKeyWordParser(String keyword) throws Exception {
        KeywordParser kp = (KeywordParser) keywordParsers.get(keyword);
        if (kp == null)
            kp = (KeywordParser) keywordParsers.get(keyword.toLowerCase());
        if (kp == null) {
            char c = keyword.charAt(0);
            if (Character.isLowerCase(c))
                keyword = Character.toUpperCase(c) + keyword.substring(1);
            kp = (KeywordParser) Class.forName(KEYWORD_PREFIX + keyword).newInstance();
            kp.setParser(this);
            keywordParsers.put(keyword, kp);
            keywordParsers.put(keyword.toLowerCase(), kp);
        }
        return kp;
    }

    private void pushState(State state) {
        stateStack.push(this.state);
        this.state = state;
    }

    private void popState() {
        state = (State) stateStack.pop();
    }

    private void pushAttributes() {
        attributeStack.push(new MutableAttributes(currentAttributes));
    }
    
    public void pushAttributes(Attributes attributes) {
        attributeStack.push(new MutableAttributes(currentAttributes));
        currentAttributes = new MutableAttributes(attributes);
        currentAttributes.setModified(true);
    }

    public void popAttributes() {
        currentAttributes = (MutableAttributes) attributeStack.pop();
        currentAttributes.setModified(true);
    }

    public Attributes getAttributes() {
        if (currentImmutableAttributes == null || currentAttributes.isModified()) {
            currentImmutableAttributes = new Attributes(currentAttributes);
            currentAttributes.setModified(false);
        }
        return currentImmutableAttributes;
    }

    private void newObjectInstanceList(int sequenceNumber) {
        currentObjectInstanceList = new ObjectInstanceList();
        objectInstanceLists.put(new Integer(sequenceNumber), currentObjectInstanceList);
    }

    private ObjectInstanceList getObjectInstanceList(int sequenceNumber) {
        ObjectInstanceList result =
            (ObjectInstanceList) objectInstanceLists.get(new Integer(sequenceNumber));
        if (result == null)
            throw new IllegalArgumentException("Unknown instance: " + sequenceNumber);
        return result;
    }

    public void attributeBegin() {
        pushAttributes();
        pushState(State.ATTRIBUTE);
    }

    public void attributeEnd() {
        popAttributes();
        popState();
        inAreaLightSource = false;
    }

    public void transformBegin() {
        transformStack.push(currentAttributes.getTransform());
        pushState(State.TRANSFORM);
    }

    public void transformEnd() {
        currentAttributes.setTransform((Transform) transformStack.pop());
        popState();
    }

    public void saveCurrentTransformAs(String name) {
        Global.setTransform(name, currentAttributes.getTransform());
    }

    public void setCurrentTransformTo(String name) {
        currentAttributes.setTransform(Global.getTransform(name));
    }

    public void setIdentity() {
        currentAttributes.setTransform(AffineTransform.IDENTITY);
    }

    public void rotate(float angle, float dx, float dy, float dz) {
        AxisAngle4f axisAngle = new AxisAngle4f(dx, dy, dz, (float) Math.toRadians(angle));
        Matrix4f rotation = new Matrix4f();
        rotation.setIdentity();
        rotation.set(axisAngle);
        concatTransform(rotation);
    }

    public void scale(float sx, float sy, float sz) {
        Matrix4f scale =
            new Matrix4f(sx, 0f, 0f, 0f, 0f, sy, 0f, 0f, 0f, 0f, sz, 0f, 0f, 0f, 0f, 1f);
        concatTransform(scale);
    }

    public void translate(float dx, float dy, float dz) {
        Matrix4f translation = new Matrix4f();
        translation.setIdentity();
        translation.setTranslation(new Vector3f(dx, dy, dz));
        concatTransform(translation);
    }

    public void perspective(float fov) {
        PerspectiveTransform perspective =
            PerspectiveTransform.createWithFov(fov, 1f, Constants.INFINITY);
        Transform current = currentAttributes.getTransform();
        currentAttributes.setTransform(perspective.preConcat(current));
    }

    public void setDetailRange(
        float minVisible,
        float lowerTransition,
        float upperTransition,
        float maxVisible) {
        DetailRange detailRange =
            new DetailRange(minVisible, lowerTransition, upperTransition, maxVisible);
        currentAttributes.setDetailRange(detailRange);
    }

    public void setDetail(Bounds3f detail) {
        currentAttributes.setDetail(detail);
    }

    public void setBound(Bounds3f bounds) {
        currentAttributes.setBound(bounds);
    }

    public void setTransform(Matrix4f matrix) {
        currentAttributes.setTransform(AffineTransform.IDENTITY.concat(matrix));
    }

    public void concatTransform(Matrix4f trans) {
        Transform current = currentAttributes.getTransform();
        current = current.concat(trans);
        currentAttributes.setTransform(current);
    }

    public void setColor(Color3f color) {
        currentAttributes.setColor(color);
    }

    public void setOpacity(Color3f opacity) {
        currentAttributes.setOpacity(opacity);
    }

    public void setShadingInterpolation(String type) {
        currentAttributes.setShadingInterpolation(ShadingInterpolation.getNamed(type));
    }

    public void setMatte(boolean b) {
        currentAttributes.setMatte(b);
    }

    public void setGeometricApproximation(String type, float value) {
        GeometricApproximation geometricApproximation =
            new GeometricApproximation(type, value);
        currentAttributes.setGeometricApproximation(geometricApproximation);
    }

    public void reverseOrientation() {
        Orientation orientation = currentAttributes.getOrientation();
        currentAttributes.setOrientation(orientation.getReverse());
    }

    public void frameBegin(int n) {
        pushState(State.FRAME);
        frameStack.push(new Frame(frame));
        worldStack.push(new World(world));
        pushAttributes();
        frame.setFrameNumber(n);
    }

    public void begin(String renderer) {
        this.beginName = renderer;
        pushState(State.BEGIN_END);
    }

    public void end() {
        popState();
    }

    public void frameEnd() {
        popAttributes();
        world = (World) worldStack.pop();
        frame = (Frame) frameStack.pop();
        popState();
    }

    public void motionBegin(float[] times) {
        pushState(State.MOTION);
        frame.setMotionTimes(times);
    }

    public void motionEnd() {
        popState();
    }

    public void objectBegin(int sequenceNumber) {
        pushAttributes();
        pushState(State.OBJECT);
        setIdentity();
        newObjectInstanceList(sequenceNumber);
        inObject = true;
    }

    public void objectEnd() {
        currentObjectInstanceList = null;
        inObject = false;
        popState();
        popAttributes();
    }

    public void solidBegin(String operation) {
        pushAttributes();
        pushState(State.SOLID);
        currentOperation = operation;
    }

    public void solidEnd() {
        popState();
        popAttributes();
    }

    public void worldBegin() {
        pushState(State.WORLD);
        frameStack.push(new Frame(frame));
        worldStack.push(new World(world));
        frame.defineScreen();
        Global.setTransform("raster", frame.getRasterTransform());
        // from "screen" to "raster"
        Global.setTransform("NDC", frame.getNDCTransform()); // from "raster"
        // to "NDC"
        saveCurrentTransformAs("camera"); // from "world" to "camera"
        setIdentity();
        currentAttributes.setSpace(Space.WORLD);
        pushAttributes();
        renderer = Renderer.createRenderer(frame, world, this);
        // Must be set after defining gridsize...
        SurfaceShader.setBetterHighlights(cmdLine.hasOption(JRMan.OPTION_QUALITY));
    }

    public void worldEnd() {
        AbstractKeywordParser.reset();
        boolean doRender = true;
        if (firstFrame > -1 && frame.getFrameNumber() < firstFrame)
            doRender = false;
        if (lastFrame > -1 && frame.getFrameNumber() > lastFrame)
            doRender = false;
        if (doRender)
            renderer.render();
        renderer = null;
        world = (World) worldStack.pop();
        frame = (Frame) frameStack.pop();
        popState();
        popAttributes();
        currentAttributes.setSpace(Space.CAMERA);
    }

    public void setBasis(Basis uBasis, int uStep, Basis vBasis, int vStep) {
        currentAttributes.setBasis(uBasis, uStep, vBasis, vStep);
    }

    public void setSides(int sides) {
        currentAttributes.setSides(sides);
    }

    public void setTextureCoordinates(
        float s1,
        float t1,
        float s2,
        float t2,
        float s3,
        float t3,
        float s4,
        float t4) {
        currentAttributes.setTextureCoordinates(
            new TextureCoordinates(s1, t1, s2, t2, s3, t3, s4, t4));
    }

    public Declaration getDeclaration(String name) {
        return Global.getDeclaration(name);
    }

    public void setDeclaration(String name, String decl) {
        Global.setDeclaration(name, decl);
    }

    public void setFormat(int xResolution, int yResolution, float pixelAspectRatio) {
        if (xResolution > 0)
            frame.setHorizontalResolution(xResolution);
        if (yResolution > 0)
            frame.setVerticalResolution(yResolution);
        if (pixelAspectRatio > 0)
            frame.setPixelAspectRatio(pixelAspectRatio);
    }

    public void setFrameAspectRatio(float frameAspectRatio) {
        frame.setFrameAspectRatio(frameAspectRatio);
    }

    public void setScreenWindow(float left, float right, float bottom, float top) {
        frame.setScreenWindow(new Bounds2f(left, right, bottom, top));
    }

    public void setCropWindow(float xmin, float xmax, float ymin, float ymax) {
        frame.setCropWindow(new Bounds2f(xmin, xmax, ymin, ymax));
    }

    public void setProjection(String name, ParameterList parameters) {
        CameraProjection cameraProjection = CameraProjection.getNamed(name);
        frame.setCameraProjection(cameraProjection);
        if (cameraProjection == CameraProjection.PERSPECTIVE) {
            UniformScalarFloat parameter =
                (UniformScalarFloat) parameters.getParameter("fov");
            if (parameter != null)
                frame.setFieldOfView(parameter.getValue());
            currentAttributes.setTransform(
                PerspectiveTransform.createWithFov(
                    frame.getFieldOfView(),
                    frame.getNearClipping(),
                    frame.getFarClipping()));
        } else if (cameraProjection == CameraProjection.ORTHOGRAPHIC) {
            currentAttributes.setTransform(
                AffineTransform.createOrthographic(
                    frame.getNearClipping(),
                    frame.getFarClipping()));
        }
        saveCurrentTransformAs("screen"); // from "camera" to "screen"
        setIdentity();
    }

    public void setClipping(float near, float far) {
        frame.setNearClipping(near);
        frame.setFarClipping(far);
    }

    public void setDepthOfField(float fstop, float focalLength, float focalDistance) {
        frame.setFStop(fstop);
        frame.setFocalLength(focalLength);
        frame.setFocalDistance(focalDistance);
    }

    public void setDepthOfField(float fstop) {
        frame.setFStop(fstop);
    }

    public void setShutter(float min, float max) {
        frame.setShutterOpen(min);
        frame.setShutterClose(max);
    }

    public void setPixelVariance(float variation) {
        frame.setPixelVariance(variation);
    }

    public void setPixelSamples(float xSamples, float ySamples) {
        frame.setHorizontalSamplingRate(xSamples);
        frame.setVerticalSamplingRate(ySamples);
    }

    public void setPixelFilter(String type, float xWidth, float yWidth) {
        frame.setFilter(new Filter(Filter.Type.getNamed(type), xWidth, yWidth));
    }

    public void setExposure(float gain, float gamma) {
        frame.setExposure(new Exposure(gain, gamma));
    }

    public void setQuantize(String type, int one, int min, int max, float ditherAmplitude) {
        if (type.equals("rgba")) {
            frame.setColorQuantizer(new Quantizer(one, min, max, ditherAmplitude));
        } else if (type.equals("z")) {
            frame.setDepthQuantizer(new Quantizer(one, min, max, ditherAmplitude));
        } else
            throw new IllegalArgumentException("no such quantizer: " + type);
    }

    public void setDisplay(String name, String type, String mode, ParameterList parameters) {
        frame.setDisplay(new Display(name, type, mode));
        UniformArrayInteger parameter =
            (UniformArrayInteger) parameters.getParameter("origin");
        if (parameter != null) {
            frame.setOriginX(parameter.getValue(0));
            frame.setOriginY(parameter.getValue(1));
        }
    }

    public void setHider(String type, ParameterList parameters) {
        frame.setHider(Hider.getNamed(type));
        frame.setHiderParameters(parameters);
    }

    public void setRelativeDetail(float relativeDetail) {
        frame.setRelativeDetail(relativeDetail);
    }

    public void setOption(String name, ParameterList parameters) {
        if (name.equals("limits")) {
            UniformScalarInteger param1 =
                (UniformScalarInteger) parameters.getParameter("gridsize");
            if (param1 != null)
                frame.setGridSize(param1.getValue());
            UniformArrayInteger param2 =
                (UniformArrayInteger) parameters.getParameter("bucketsize");
            if (param2 != null) {
                frame.setBucketSizeX(param2.getValue(0));
                frame.setBucketSizeY(param2.getValue(1));
            }
        } else if (name.equals("statistics")) {
            UniformScalarInteger param =
                (UniformScalarInteger) parameters.getParameter("endofframe");
            if (param != null)
                frame.setEndOfFrameStatistics(param.getValue() != 0);
        } else if (name.equals("quality")) {
            UniformScalarInteger param =
                (UniformScalarInteger) parameters.getParameter("highlights");
            if (param != null)
                SurfaceShader.setBetterHighlights(param.getValue() != 0);
        }
    }

    public void setAttribute(String name, ParameterList parameters) {
        if (name.equals("displacementbound")) {
            UniformScalarFloat param1 =
                (UniformScalarFloat) parameters.getParameter("sphere");
            if (param1 != null)
                currentAttributes.setDisplacementBound(param1.getValue());
            UniformScalarString param2 =
                (UniformScalarString) parameters.getParameter("coordinatesystem");
            if (param2 != null)
                currentAttributes.setDisplacementBoundCoordinateSystem(param2.getValue());
        } else if (name.equals("identifier")) {
            UniformScalarString param =
                (UniformScalarString) parameters.getParameter("name");
            if (param != null)
                currentAttributes.setObjectIdentifer(param.getValue());
        } else if (name.equals("trimcurve")) {
            UniformScalarString param =
                (UniformScalarString) parameters.getParameter("sense");
            if (param != null)
                currentAttributes.setTrimCurveSense(TrimCurveSense.getNamed(param.getValue()));
        } else if (name.equals("render")) {
            UniformScalarInteger param =
             (UniformScalarInteger) parameters.getParameter("truedisplacement");
            if (param != null)
                currentAttributes.setTrueDisplacement(param.getValue() == 1);
        }
    }

    public void setSurface(String name, ParameterList parameters) {
        currentAttributes.setSurface(
            SurfaceShader.createShader(name, parameters, getAttributes()));
    }

    public void setDisplacement(String name, ParameterList parameters) {
        currentAttributes.setDisplacement(
            DisplacementShader.createShader(name, parameters, getAttributes()));
    }

    public void setAtmosphere(String name, ParameterList parameters) {
        currentAttributes.setAtmosphere(
            VolumeShader.createShader(name, parameters, getAttributes()));
    }

    public void setExterior(String name, ParameterList parameters) {
        currentAttributes.setExterior(
            VolumeShader.createShader(name, parameters, getAttributes()));
    }

    public void setImager(String name, ParameterList parameters) {
        frame.setImager(Imager.createImager(name, parameters));
    }

    public void setInterior(String name, ParameterList parameters) {
        currentAttributes.setInterior(
            VolumeShader.createShader(name, parameters, getAttributes()));
    }

    public void createLightSource(String name, int sequenceNumber, ParameterList parameters) {
        LightShader light = LightShader.createShader(name, parameters, getAttributes());
        world.addLight(sequenceNumber, light);
        turnOnLight(light);
    }

    private void turnOnLight(LightShader light) {
        Set<LightShader> lightSources
                = new HashSet<LightShader>(currentAttributes.getLightSources());
        lightSources.add(light);
        currentAttributes.setLightSources(lightSources);
    }

    private void turnOffLight(LightShader light) {
        Set<LightShader> lightSources 
                = new HashSet<LightShader>(currentAttributes.getLightSources());
        lightSources.remove(light);
        currentAttributes.setLightSources(lightSources);
    }

    public void illuminate(int sequenceNumber, int onOff) {
        LightShader light = world.getLight(sequenceNumber);
        if (onOff != 0)
            turnOnLight(light);
        else
            turnOffLight(light);
    }

    public void createAreaLightSource(
        String name,
        int sequenceNumber,
        ParameterList parameters) {
        createLightSource(name, sequenceNumber, parameters);
        inAreaLightSource = true;
    }

    public void setShadingRate(float size) {
        currentAttributes.setShadingRate(size);
    }

    public void objectInstance(int n) {
        renderer.addPrimitive(new ObjectInstance(getObjectInstanceList(n), getAttributes()));
    }

    public void addSphere(
        final float radius,
        float zMin,
        float zMax,
        float tMax,
        final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        final float phiMin =
            (float) ((zMin <= -radius) ? -Math.PI / 2 : Math.asin(zMin / radius));
        final float phiMax =
            (float) ((zMax >= radius) ? Math.PI / 2 : Math.asin(zMax / radius));
        final float thetaMin = 0f;
        final float thetaMax = (float) Math.toRadians(tMax);
        if (!inObject) {
            renderer.addPrimitive(
                new Sphere(
                    radius,
                    phiMin,
                    phiMax,
                    thetaMin,
                    thetaMax,
                    parameters,
                    getAttributes()));
            sphereCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    sphereCount++;
                    return new Sphere(
                        radius,
                        phiMin,
                        phiMax,
                        thetaMin,
                        thetaMax,
                        parameters,
                        createAttributes(transform, attributes));
                }
            });
        }
    }

    public void addTorus(
        final float majorRadius,
        final float minorRadius,
        float pMin,
        float pMax,
        float tMax,
        final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        final float phiMin = (float) Math.toRadians(pMin);
        final float phiMax = (float) Math.toRadians(pMax);
        final float thetaMax = (float) Math.toRadians(tMax);
        if (!inObject) {
            renderer.addPrimitive(
                new Torus(
                    majorRadius,
                    minorRadius,
                    phiMin,
                    phiMax,
                    0f,
                    thetaMax,
                    parameters,
                    getAttributes()));
            torusCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    torusCount++;
                    return new Torus(
                        majorRadius,
                        minorRadius,
                        phiMin,
                        phiMax,
                        0f,
                        thetaMax,
                        parameters,
                        createAttributes(transform, attributes));
                }
            });
        }
    }

    public void addCylinder(
        final float radius,
        final float zmin,
        final float zmax,
        float tMax,
        final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        final float thetaMax = (float) Math.toRadians(tMax);
        if (!inObject) {
            renderer.addPrimitive(
                new Cylinder(radius, zmin, zmax, 0f, thetaMax, parameters, getAttributes()));
            cylinderCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    cylinderCount++;
                    return new Cylinder(
                        radius,
                        zmin,
                        zmax,
                        0f,
                        thetaMax,
                        parameters,
                        createAttributes(transform, attributes));
                }
            });
        }
    }

    public void addCone(
        final float height,
        final float radius,
        float tMax,
        final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        final float thetaMax = (float) Math.toRadians(tMax);
        if (!inObject) {
            renderer.addPrimitive(
                new Cone(
                    radius,
                    0f,
                    height,
                    0f,
                    thetaMax,
                    height,
                    parameters,
                    getAttributes()));
            coneCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    coneCount++;
                    return new Cone(
                        radius,
                        0f,
                        height,
                        0f,
                        thetaMax,
                        height,
                        parameters,
                        createAttributes(transform, attributes));
                }
            });
        }
    }

    public void addDisk(
        final float height,
        final float radius,
        float tMax,
        final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        final float thetaMax = (float) Math.toRadians(tMax);
        if (!inObject) {
            renderer.addPrimitive(
                new Disk(height, 0f, thetaMax, 0f, radius, parameters, getAttributes()));
            diskCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    coneCount++;
                    return new Disk(
                        height,
                        0f,
                        thetaMax,
                        0f,
                        radius,
                        parameters,
                        createAttributes(transform, attributes));
                }
            });
        }
    }

    public void addParaboloid(
            final float radius,
            final float zmin,
            final float zmax,
            float tMax,
            final ParameterList parameters) {
            if (inAreaLightSource)
                return;

            final float thetaMin = 0f;
            final float thetaMax = (float) Math.toRadians(tMax);
            if (!inObject) {
                renderer.addPrimitive(
                    new Paraboloid(
                        radius,
                        zmin,
                        zmax,
                        thetaMin,
                        thetaMax,
                        zmin,
                        zmax,
                        thetaMax,
                        parameters,
                        getAttributes()));
                paraboloidCount++;
            } else {
                final Transform transform = currentAttributes.getTransform();
                currentObjectInstanceList
                    .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                    public Primitive create(Attributes attributes) {
                        paraboloidCount++;
                        return new Paraboloid(
                            radius,
                            zmin,
                            zmax,
                            thetaMin,
                            thetaMax,
                            zmin,
                            zmax,
                            thetaMax,
                            parameters,
                            createAttributes(transform, attributes));
                    }
                });
            }
        }

        public void addHyperboloid(
            final float x1,
            final float y1,
            final float z1,
            final float x2,
            final float y2,
            final float z2,
            final float tMax,
            final ParameterList parameters) {
            if (inAreaLightSource)
                return;

            final float thetaMin = 0f;
            final float thetaMax = (float) Math.toRadians(tMax);
            if (!inObject) {
                renderer.addPrimitive(
                    new Hyperboloid(
                        x1,
                        y1,
                        z1,
                        x2,
                        y2,
                        z2,
                        thetaMin,
                        thetaMax,
                        parameters,
                        getAttributes()));
                hyperboloidCount++;
            } else {
                final Transform transform = currentAttributes.getTransform();
                currentObjectInstanceList
                    .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                    public Primitive create(Attributes attributes) {
                        hyperboloidCount++;
                        return new Hyperboloid(
                            x1,
                            y1,
                            z1,
                            x2,
                            y2,
                            z2,
                            thetaMin,
                            thetaMax,
                            parameters,
                            createAttributes(transform, attributes));
                    }
                });
            }

        }

    public void addBilinearPatch(final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        if (!inObject) {
            renderer.addPrimitive(new BilinearPatch(parameters, getAttributes()));
            bilinearPatchCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    bilinearPatchCount++;
                    return new BilinearPatch(
                        parameters,
                        createAttributes(transform, attributes));
                }
            });
        }
    }

    public void addBicubicPatch(final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        if (!inObject) {
            BicubicPatch patch = new BicubicPatch(parameters, getAttributes());
            renderer.addPrimitive(patch);
            bicubicPatchCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    bicubicPatchCount++;
                    BicubicPatch patch =
                        new BicubicPatch(parameters, createAttributes(transform, attributes));
                    return patch;
                }
            });
        }
    }

    private int evalIndex(int iu, int nu, int iv, int nv) {
        return (iv % nv) * nu + (iu % nu);
    }

    private void setPatchMeshDefaultParameters(int nu, int nv, ParameterList parameters) {
        Primitive.setDefaultParameters(parameters, getAttributes());
        VaryingScalarFloat p = (VaryingScalarFloat) parameters.getParameter("u");
        p.expandTo(nu, nv);
        p = (VaryingScalarFloat) parameters.getParameter("v");
        p.expandTo(nu, nv);
        p = (VaryingScalarFloat) parameters.getParameter("s");
        p.expandTo(nu, nv);
        p = (VaryingScalarFloat) parameters.getParameter("t");
        p.expandTo(nu, nv);
    }

    public void addBilinearPatchMesh(
        int nu,
        String uWrap,
        int nv,
        String vWrap,
        ParameterList parameters) {
        int nU;
        if (uWrap.equals("periodic"))
            nU = nu;
        else if (uWrap.equals("nonperiodic"))
            nU = nu - 1;
        else
            throw new IllegalArgumentException("Unknown u wrap type: " + uWrap);
        int nV;
        if (vWrap.equals("periodic"))
            nV = nv;
        else if (vWrap.equals("nonperiodic"))
            nV = nv - 1;
        else
            throw new IllegalArgumentException("Unknown v wrap type: " + vWrap);
        setPatchMeshDefaultParameters(nu, nv, parameters);
        int[] uniformIndex = new int[1];
        uniformIndex[0] = 0;
        int[] indexes = new int[4];
        for (int j = 0; j < nV; j++)
            for (int i = 0; i < nU; i++) {
                indexes[0] = evalIndex(i, nu, j, nv);
                indexes[1] = evalIndex(i + 1, nu, j, nv);
                indexes[2] = evalIndex(i, nu, j + 1, nv);
                indexes[3] = evalIndex(i + 1, nu, j + 1, nv);
                addBilinearPatch(parameters.selectValues(uniformIndex, indexes, indexes));
                uniformIndex[0]++;
            }
    }

    public void addBicubicPatchMesh(
        int nu,
        String uWrap,
        int nv,
        String vWrap,
        ParameterList parameters) {
        int uStep = currentAttributes.getUStep();
        int nU;
        if (uWrap.equals("periodic"))
            nU = nu / uStep;
        else if (uWrap.equals("nonperiodic"))
            nU = (nu - 4) / uStep + 1;
        else
            throw new IllegalArgumentException("Unknown u wrap type: " + uWrap);
        int vStep = currentAttributes.getVStep();
        int nV;
        if (vWrap.equals("periodic"))
            nV = nv / vStep;
        else if (vWrap.equals("nonperiodic"))
            nV = (nv - 4) / vStep + 1;
        else
            throw new IllegalArgumentException("Unknown v wrap type: " + vWrap);
        int blnu;
        if (uWrap.equals("periodic"))
            blnu = nU;
        else
            blnu = nU + 1;
        int blnv;
        if (vWrap.equals("periodic"))
            blnv = nV;
        else
            blnv = nV + 1;
        setPatchMeshDefaultParameters(blnu, blnv, parameters);
        int[] uniformIndex = new int[1];
        uniformIndex[0] = 0;
        int[] varyingIndexes = new int[4];
        int[] vertexIndexes = new int[16];
        int j = 0;
        for (int jj = 0; jj < nV; jj++) {
            int i = 0;
            for (int ii = 0; ii < nU; ii++) {
                varyingIndexes[0] = evalIndex(ii, blnu, jj, blnv);
                varyingIndexes[1] = evalIndex(ii + 1, blnu, jj, blnv);
                varyingIndexes[2] = evalIndex(ii, blnu, jj + 1, blnv);
                varyingIndexes[3] = evalIndex(ii + 1, blnu, jj + 1, blnv);
                vertexIndexes[0] = evalIndex(i, nu, j, nv);
                vertexIndexes[1] = evalIndex(i + 1, nu, j, nv);
                vertexIndexes[2] = evalIndex(i + 2, nu, j, nv);
                vertexIndexes[3] = evalIndex(i + 3, nu, j, nv);
                vertexIndexes[4] = evalIndex(i, nu, j + 1, nv);
                vertexIndexes[5] = evalIndex(i + 1, nu, j + 1, nv);
                vertexIndexes[6] = evalIndex(i + 2, nu, j + 1, nv);
                vertexIndexes[7] = evalIndex(i + 3, nu, j + 1, nv);
                vertexIndexes[8] = evalIndex(i, nu, j + 2, nv);
                vertexIndexes[9] = evalIndex(i + 1, nu, j + 2, nv);
                vertexIndexes[10] = evalIndex(i + 2, nu, j + 2, nv);
                vertexIndexes[11] = evalIndex(i + 3, nu, j + 2, nv);
                vertexIndexes[12] = evalIndex(i, nu, j + 3, nv);
                vertexIndexes[13] = evalIndex(i + 1, nu, j + 3, nv);
                vertexIndexes[14] = evalIndex(i + 2, nu, j + 3, nv);
                vertexIndexes[15] = evalIndex(i + 3, nu, j + 3, nv);
                addBicubicPatch(
                    parameters.selectValues(uniformIndex, varyingIndexes, vertexIndexes));
                uniformIndex[0]++;
                i += uStep;
            }
            j += vStep;
        }
    }

    private void addLinearCurve(final boolean periodic,
                                final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        if (!inObject) {
            LinearCurve curve = new LinearCurve(periodic, parameters,
                                                getAttributes());
            renderer.addPrimitive(curve);
            curveCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                        public Primitive create(Attributes attributes) {
                           curveCount++;
                           return new LinearCurve(periodic, parameters,
                                                  createAttributes(transform,
                                                                   attributes));
                        }
                    });
        }
    }
    
    public void addLinearCurves(int[] nVertices, String wrap,
                                ParameterList parameters) {
        boolean periodic = wrap.equals("periodic");
        int[] uniformIndex = new int[1];
        int ptr = 0;
        for (int i = 0; i < nVertices.length; i++) {
            uniformIndex[0] = i;
            int[] indexes = new int[nVertices[i]];
            for (int j = 0; j < indexes.length; j++)
                indexes[j] = ptr++;
            addLinearCurve(periodic,
                           parameters.selectValues(uniformIndex,
                                                   indexes, indexes));
        }
    }
    
    private void addCubicCurve(final boolean periodic,
                               final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        if (!inObject) {
            CubicCurve curve = new CubicCurve(periodic, parameters,
                                              getAttributes());
            renderer.addPrimitive(curve);
            curveCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                        public Primitive create(Attributes attributes) {
                            curveCount++;
                            return new CubicCurve(periodic, parameters,
                                                  createAttributes(transform,
                                                                   attributes));
                        }
                    });
        }
    }
    
    public void addCubicCurves(int[] nVertices, String wrap,
                               ParameterList parameters) {
        boolean periodic = wrap.equals("periodic");
        int vStep = currentAttributes.getVStep();
        int[] uniformIndex = new int[1];
        int varyingPtr = 0;
        int vertexPtr = 0;
        for (int i = 0; i < nVertices.length; i++) {
            uniformIndex[0] = i;
            int nv;
            if (periodic)
                nv = nVertices[i] / vStep;
            else
                nv = (nVertices[i] - 4) / vStep + 2;
            int[] varyingIndexes = new int[nv];
            for (int j = 0; j < nv; j++)
                varyingIndexes[j] = varyingPtr++;
            int[] vertexIndexes = new int[nVertices[i]];
            for (int j = 0; j < vertexIndexes.length; j++)
                vertexIndexes[j] = vertexPtr++;
            addCubicCurve(periodic,
                          parameters.selectValues(uniformIndex,
                                                  varyingIndexes,
                                                  vertexIndexes));
        }
    }

    public void addNuPatch(
        final int nu,
        final int uorder,
        final float[] uknot,
        final float umin,
        final float umax,
        final int nv,
        final int vorder,
        final float[] vknot,
        final float vmin,
        final float vmax,
        final ParameterList parameters) {            
        if (inAreaLightSource)
            return;
        if (!inObject) {
            renderer.addPrimitive(
                new NurbsRI(
                    nu,
                    uorder,
                    uknot,
                    umin,
                    umax,
                    nv,
                    vorder,
                    vknot,
                    vmin,
                    vmax,
                    true,
                    parameters,
                    getAttributes()));
            nurbsCount++;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    nurbsCount++;
                    return new NurbsRI(
                            nu,
                            uorder,
                            uknot,
                            umin,
                            umax,
                            nv,
                            vorder,
                            vknot,
                            vmin,
                            vmax,
                            true,
                            parameters,
                            createAttributes(transform, attributes));
                }
            });
        }
    }

  
    public void addPointsPolygons(
        final int[] nVertices,
        final int[] vertices,
        final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        setPolygonST(parameters);
        if (!inObject) {
            renderer.addPrimitive(
                new PointsPolygons(nVertices, vertices, parameters, getAttributes()));
            polygonCount += nVertices.length;
        } else {
            final Transform transform = currentAttributes.getTransform();
            currentObjectInstanceList
                .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                public Primitive create(Attributes attributes) {
                    polygonCount += nVertices.length;
                    return new PointsPolygons(
                        nVertices,
                        vertices,
                        parameters,
                        createAttributes(transform, attributes));
                }
            });
        }
    }

    private void setPolygonST(ParameterList parameters) {
        VaryingScalarFloat sParam = (VaryingScalarFloat) parameters.getParameter("s");
        VaryingScalarFloat tParam = (VaryingScalarFloat) parameters.getParameter("t");
        VaryingArrayFloat stParam = (VaryingArrayFloat) parameters.getParameter("st");
        VaryingScalarTuple3f pParam =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        if (sParam == null && stParam == null)
            parameters.addParameter(pParam.extract(Global.getDeclaration("s"), 0));
        if (tParam == null && stParam == null)
            parameters.addParameter(pParam.extract(Global.getDeclaration("t"), 1));
    }

    public void addPolygon(final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        setPolygonST(parameters);
        VaryingScalarTuple3f pParam =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        int[] uniformIndex = new int[1];
        uniformIndex[0] = 0;
        int[] indexes = new int[] { 0, 0, 0, 0 };
        for (int i = 1; i < pParam.getCount() - 1; i++) {
            indexes[1] = i;
            indexes[3] = i + 1;
            addBilinearPatch(parameters.selectValues(uniformIndex, indexes, indexes));
            polygonCount++;
            bilinearPatchCount--; // TODO: fix this
        }
    }

    public void addPoints(final ParameterList parameters) {
        if (inAreaLightSource)
            return;
        VaryingScalarTuple3f pParam =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        parameters.removeParameter("P");
        VaryingScalarFloat widthParam =
            (VaryingScalarFloat) parameters.getParameter("width");
        parameters.removeParameter("width");
        UniformScalarFloat constantWidthParam =
            (UniformScalarFloat) parameters.getParameter("constantwidth");
        parameters.removeParameter("constantwidth");
        // TODO: same space optimization as for PointsPolygon
        int[] uniformIndex = new int[1];
        uniformIndex[0] = 0;
        int[] indexes = new int[1];
        for (int i = 0; i < pParam.getCount(); i++) {
            final Point3f p = new Point3f();
            pParam.getValue(i, p);
            float tw;
            if (widthParam != null)
                tw = widthParam.getValue(i);
            else if (constantWidthParam != null)
                tw = constantWidthParam.getValue();
            else
                tw = 1f;
            final float w = tw;
            indexes[0] = i;
            final ParameterList param =
                parameters.selectValues(uniformIndex, indexes, indexes);
            if (!inObject) {
                renderer.addPrimitive(new Point(w, p.x, p.y, p.z, param, getAttributes()));
                pointCount++;
            }
            else {
                final Transform transform = currentAttributes.getTransform();
                currentObjectInstanceList
                    .addPrimitiveCreator(new ObjectInstanceList.PrimitiveCreator() {
                    public Primitive create(Attributes attributes) {
                        pointCount++;
                        return new Point(
                            w,
                            p.x,
                            p.y,
                            p.z,
                            param,
                            createAttributes(transform, attributes));
                    }
                });
            }
        }
    }

    public void addDelayedReadArchive(String filename, float xmin, float xmax,
            float ymin, float ymax, float zmin, float zmax) {
        renderer.addPrimitive(new DelayedReadArchive(this, filename, xmin, xmax,
                ymin, ymax, zmin, zmax, getAttributes()));
    }

    public void makeTexture(
        String picturename,
        String texturename,
        String swrap,
        String twrap,
        String filter,
        int swidth,
        int twidth) {
        try {
            MipMap.makeMipMap(
                picturename,
                texturename,
                MipMap.Mode.getNamed(swrap),
                MipMap.Mode.getNamed(twrap),
                filter,
                swidth,
                twidth);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't create texture: " + picturename);
        }
    }

    public void resetCounts() {
        sphereCount = 0;
        torusCount = 0;
        cylinderCount = 0;
        coneCount = 0;
        diskCount = 0;
        paraboloidCount = 0;
        hyperboloidCount = 0;
        bilinearPatchCount = 0;
        bicubicPatchCount = 0;
        polygonCount = 0;
        pointCount = 0;
        nurbsCount = 0;
        curveCount = 0;
    }

    public int getBicubicPatchCount() {
        return bicubicPatchCount;
    }
    public int getBilinearPatchCount() {
        return bilinearPatchCount;
    }
    public int getConeCount() {
        return coneCount;
    }
    public int getCylinderCount() {
        return cylinderCount;
    }
    public int getDiskCount() {
        return diskCount;
    }
    public int getHyperboloidCount() {
        return hyperboloidCount;
    }
    public int getParaboloidCount() {
        return paraboloidCount;
    }
    public int getPointCount() {
        return pointCount;
    }
    public int getPolygonCount() {
        return polygonCount;
    }
    public int getSphereCount() {
        return sphereCount;
    }
    public int getTorusCount() {
        return torusCount;
    }
    
    public int getNurbsCount() {
        return nurbsCount;
    }
    
    public int getCurveCount() {
        return curveCount;
    }

}
