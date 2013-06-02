/*
 * RIBTokenMarker.java
 *
 * Created on November 20, 2005, 9:25 PM
 *
 */

package org.meta.shell.idebeans.editors.impl;

import org.syntax.jedit.*;
import org.syntax.jedit.tokenmarker.*;

import javax.swing.text.Segment;

/**
 * RIB Token marker.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RIBTokenMarker extends CTokenMarker {
    
    /** Creates a new instance of RIBTokenMarker */
    public RIBTokenMarker() {
        super(false, getKeywords());
    }
    
    @Override
    public byte markTokensImpl(byte token, Segment line, int lineIndex) {
        char[] array = line.array;
        int offset = line.offset;
        lastOffset = offset;
        lastKeyword = offset;
        int length = line.count + offset;
        boolean backslash = false;
        
        loop: for(int i = offset; i < length; i++) {
            int i1 = (i+1);
            
            char c = array[i];
            if(c == '\\') {
                backslash = !backslash;
                continue;
            }
            
            switch(token) {
                case Token.NULL:
                    switch(c) {
                        case '#':
                            if(backslash)
                                backslash = false; 
                            doKeyword(line,i,c);
                            if(length - i > 1) {                                
                                addToken(i - lastOffset,token);
                                addToken(length - i,Token.COMMENT1);
                                lastOffset = lastKeyword = length;
                                break loop;
                            } // end if
                            
                            break;
                        case '"':
                            doKeyword(line,i,c);
                            if(backslash)
                                backslash = false;
                            else {
                                addToken(i - lastOffset,token);
                                token = Token.LITERAL1;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case '\'':
                            doKeyword(line,i,c);
                            if(backslash)
                                backslash = false;
                            else {
                                addToken(i - lastOffset,token);
                                token = Token.LITERAL2;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case ':':
                            if(lastKeyword == offset) {
                                if(doKeyword(line,i,c))
                                    break;
                                backslash = false;
                                addToken(i1 - lastOffset,Token.LABEL);
                                lastOffset = lastKeyword = i1;
                            }
                            else if(doKeyword(line,i,c))
                                break;
                            break;                        
                        default:
                            backslash = false;
                            if(!Character.isLetterOrDigit(c)
                            && c != '_')
                                doKeyword(line,i,c);
                            break;
                    }
                    break;
                case Token.COMMENT1:
                case Token.COMMENT2:
                    backslash = false;                    
                    i++;
                    addToken((i+1) - lastOffset,token);
                    token = Token.NULL;
                    lastOffset = lastKeyword = i+1;                        
                    break;
                case Token.LITERAL1:
                    if(backslash)
                        backslash = false;
                    else if(c == '"') {
                        addToken(i1 - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                case Token.LITERAL2:
                    if(backslash)
                        backslash = false;
                    else if(c == '\'') {
                        addToken(i1 - lastOffset,Token.LITERAL1);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                default:
                    throw new InternalError("Invalid state: "
                    + token);
            } // end switch .. case 
        } // end for
        
        if(token == Token.NULL)
            doKeyword(line,length,'\0');
        
        switch(token) {
            case Token.LITERAL1:
            case Token.LITERAL2:
                addToken(length - lastOffset,Token.INVALID);
                token = Token.NULL;
                break;
            case Token.KEYWORD2:
                addToken(length - lastOffset,token);
                if(!backslash)
                    token = Token.NULL;
            default:
                addToken(length - lastOffset,token);
                break;
        } // end switch .. case 
        
        return token;
    }
    
    public static KeywordMap getKeywords() {
        if(ribKeywords == null) {
            ribKeywords = new KeywordMap(false);
                        
            ribKeywords.add("Atmosphere", Token.KEYWORD1);
            ribKeywords.add("AttributeBegin", Token.KEYWORD1);
            ribKeywords.add("Basis", Token.KEYWORD1);
            ribKeywords.add("Blobby", Token.KEYWORD1);
            ribKeywords.add("Clipping", Token.KEYWORD1);
            ribKeywords.add("Color", Token.KEYWORD1);
            ribKeywords.add("ConcatTransform", Token.KEYWORD1);
            ribKeywords.add("CoordinateSystem", Token.KEYWORD1);
            ribKeywords.add("CropWindow", Token.KEYWORD1);
            ribKeywords.add("Cylinder", Token.KEYWORD1);
            ribKeywords.add("DepthOfField", Token.KEYWORD1);
            ribKeywords.add("DetailRange", Token.KEYWORD1);
            ribKeywords.add("Displacement", Token.KEYWORD1);
            ribKeywords.add("End", Token.KEYWORD1);
            ribKeywords.add("Exposure", Token.KEYWORD1);
            ribKeywords.add("Format", Token.KEYWORD1);
            ribKeywords.add("FrameBegin", Token.KEYWORD1);
            ribKeywords.add("GeneralPolygon", Token.KEYWORD1);
            ribKeywords.add("Geometry", Token.KEYWORD1);
            ribKeywords.add("Hyperboloid", Token.KEYWORD1);
            ribKeywords.add("Illuminate", Token.KEYWORD1);
            ribKeywords.add("Interior", Token.KEYWORD1);
            ribKeywords.add("MakeCubeFaceEnvironment", Token.KEYWORD1);
            ribKeywords.add("MakeShadow", Token.KEYWORD1);
            ribKeywords.add("Matte", Token.KEYWORD1);
            ribKeywords.add("MotionEnd", Token.KEYWORD1);
            ribKeywords.add("ObjectBegin", Token.KEYWORD1);
            ribKeywords.add("ObjectInstance", Token.KEYWORD1);
            ribKeywords.add("Option", Token.KEYWORD1);
            ribKeywords.add("Paraboloid", Token.KEYWORD1);
            ribKeywords.add("Patch", Token.KEYWORD1);
            ribKeywords.add("Perspective", Token.KEYWORD1);
            ribKeywords.add("PixelSamples", Token.KEYWORD1);
            ribKeywords.add("Points", Token.KEYWORD1);
            ribKeywords.add("PointsPolygons", Token.KEYWORD1);
            ribKeywords.add("Procedural", Token.KEYWORD1);
            ribKeywords.add("Quantize", Token.KEYWORD1);
            ribKeywords.add("RelativeDetail", Token.KEYWORD1);
            ribKeywords.add("Rotate", Token.KEYWORD1);
            ribKeywords.add("ScreenWindow", Token.KEYWORD1);
            ribKeywords.add("ShadingRate", Token.KEYWORD1);
            ribKeywords.add("Sides", Token.KEYWORD1);
            ribKeywords.add("SolidBegin", Token.KEYWORD1);
            ribKeywords.add("Sphere", Token.KEYWORD1);
            ribKeywords.add("Surface", Token.KEYWORD1);
            ribKeywords.add("Torus", Token.KEYWORD1);
            ribKeywords.add("TransformBegin", Token.KEYWORD1);
            ribKeywords.add("Translate", Token.KEYWORD1);
            ribKeywords.add("Version", Token.KEYWORD1);
            ribKeywords.add("WorldEnd", Token.KEYWORD1);
            ribKeywords.add("AreaLightSource", Token.KEYWORD1);
            ribKeywords.add("Attribute", Token.KEYWORD1);
            ribKeywords.add("AttributeEnd", Token.KEYWORD1);
            ribKeywords.add("Begin", Token.KEYWORD1);
            ribKeywords.add("Bound", Token.KEYWORD1);
            ribKeywords.add("ClippingPlane", Token.KEYWORD1);
            ribKeywords.add("ColorSamples", Token.KEYWORD1);
            ribKeywords.add("Cone", Token.KEYWORD1);
            ribKeywords.add("CoordSysTransform", Token.KEYWORD1);
            ribKeywords.add("Curves", Token.KEYWORD1);
            ribKeywords.add("Declare", Token.KEYWORD1);
            ribKeywords.add("Detail", Token.KEYWORD1);
            ribKeywords.add("Disk", Token.KEYWORD1);
            ribKeywords.add("Display", Token.KEYWORD1);
            ribKeywords.add("ErrorHandler", Token.KEYWORD1);
            ribKeywords.add("Exterior", Token.KEYWORD1);
            ribKeywords.add("FrameAspectRatio", Token.KEYWORD1);
            ribKeywords.add("FrameEnd", Token.KEYWORD1);
            ribKeywords.add("GeometricApproximation", Token.KEYWORD1);
            ribKeywords.add("Hider", Token.KEYWORD1);
            ribKeywords.add("Identity", Token.KEYWORD1);
            ribKeywords.add("Imager", Token.KEYWORD1);
            ribKeywords.add("LightSource", Token.KEYWORD1);
            ribKeywords.add("MakeLatLongEnvironment", Token.KEYWORD1);
            ribKeywords.add("MakeTexture", Token.KEYWORD1);
            ribKeywords.add("MotionBegin", Token.KEYWORD1);
            ribKeywords.add("NuPatch", Token.KEYWORD1);
            ribKeywords.add("ObjectEnd", Token.KEYWORD1);
            ribKeywords.add("Opacity", Token.KEYWORD1);
            ribKeywords.add("Orientation", Token.KEYWORD1);
            ribKeywords.add("Parser", Token.KEYWORD1);
            ribKeywords.add("PatchMesh", Token.KEYWORD1);
            ribKeywords.add("PixelFilter", Token.KEYWORD1);
            ribKeywords.add("PixelVariance", Token.KEYWORD1);
            ribKeywords.add("PointsGeneralPolygons", Token.KEYWORD1);
            ribKeywords.add("Polygon", Token.KEYWORD1);
            ribKeywords.add("Projection", Token.KEYWORD1);
            ribKeywords.add("ReadArchive", Token.KEYWORD1);
            ribKeywords.add("ReverseOrientation", Token.KEYWORD1);
            ribKeywords.add("Scale", Token.KEYWORD1);
            ribKeywords.add("ShadingInterpolation", Token.KEYWORD1);
            ribKeywords.add("Shutter", Token.KEYWORD1);
            ribKeywords.add("Skew", Token.KEYWORD1);
            ribKeywords.add("SolidEnd", Token.KEYWORD1);
            ribKeywords.add("SubdivisionMesh", Token.KEYWORD1);
            ribKeywords.add("TextureCoordinates", Token.KEYWORD1);
            ribKeywords.add("Transform", Token.KEYWORD1);
            ribKeywords.add("TransformEnd", Token.KEYWORD1);
            ribKeywords.add("TrimCurve", Token.KEYWORD1);
            ribKeywords.add("WorldBegin", Token.KEYWORD1);
        } // end if 
        
        return ribKeywords;
    }
    
    // private members
    private static KeywordMap ribKeywords;
    
} // end of class RIBTokenMarker
