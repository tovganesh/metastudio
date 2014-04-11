/*
 * CylinderFactory.java
 *
 * Created on May 8, 2005, 7:14 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;
import java.util.*;
import java.awt.image.*;

import org.meta.config.impl.AtomInfo;

/**
 * Class for fast rendering of cylinders. Caching of cylinders (optimised to 
 * show bonds) is performed so as to have fast rendering.
 * Follows a singleton pattern.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CylinderFactory {
    
    /** the only instance of CylinderFactory */
    private static CylinderFactory _cylinderFactory;
    
    /** the common data associated with a cylinder, i.e. the pixels that
        make up a cylinder */
    private byte [] cylinderData;
    
    /** maximum radius of in-memory cylinder data */
    public final static int MAX_RADIUS = 100;
    
    /** maximum height of in-memory cylinder data */
    public final static int MAX_HEIGHT = 100;
    
    /** other cylinder related info */
    public final static int HX = MAX_RADIUS - 5;
    public final static int HY = MAX_RADIUS - 5;
    public final static int ALPHA_PIX = 30;
    
    /** Cache of the in-memory cylinder */
    private HashMap<String, InMemoryCylinder> cylinderCache;
    
    private int realMaxRadius = (int) MAX_RADIUS;
    
    /** Creates a new instance of CylinderFactory */
    public CylinderFactory() {
        // set up "raw" cylinder data
        cylinderData = new byte[MAX_RADIUS * 2 * MAX_HEIGHT * 2];
        
        int x0, y0, p, X, Y, x, y, r;
        
        realMaxRadius = 0;
        x0 = MAX_RADIUS-ALPHA_PIX;
        
        for (Y = 2*MAX_RADIUS; --Y >= 0;) {
            
            y0 = Y - MAX_RADIUS;        
            p = Y * (MAX_RADIUS * 2) + MAX_RADIUS - x0;                                    
            
            for (X = -x0; X<x0; X++) {
                x = X + HX;
                y = Y - MAX_RADIUS + HY;
                r = (int) (Math.sqrt(x * x + y * y) + 0.5);
                
                if (r > realMaxRadius) realMaxRadius = r;
                
                cylinderData[p++] = r <= 0 ? 1 : (byte) r;                
            } // end for
        } // end for                        
        
        // TODO: we assume that we don't require a bigger cache than this!
        cylinderCache = new HashMap<String, InMemoryCylinder>(10);
    }
    
    /**
     * get an instance of CylinderFactory class
     *
     * @return an instance of CylinderFactory
     */
    public static CylinderFactory getInstance() {
        if (_cylinderFactory == null) {
            _cylinderFactory = new CylinderFactory();
        } // end if
        
        return _cylinderFactory;
    }

    /**
     * Get the image of the cylinder for a particular atom symbol
     *
     * @param symbol the atomic symbol
     * @return the "formed" Image of the cylinder
     */
    public Image getCylinderImage(String symbol) {
        Color cylinderColor = AtomInfo.getInstance().getColor(symbol);
        InMemoryCylinder memCylinder = cylinderCache.get(symbol);
        
        if (memCylinder == null) {
            generateCylinder(symbol, cylinderColor);
        } else if (memCylinder.cylinderSymbol.equals(symbol) 
                   && (!memCylinder.cylinderColor.equals(cylinderColor))) {
            generateCylinder(symbol, cylinderColor);
        } // end if
            
        return cylinderCache.get(symbol).cylinderImage;
    } 
    
    /**
     * Generate the "in-memory" cylinder
     *
     * @param symbol the atomic symbol
     * @param cylinderColor the color of this cylinder
     */
    private void generateCylinder(String symbol, Color cylinderColor) {
        int R = cylinderColor.getRed();
        int G = cylinderColor.getGreen();
        int B = cylinderColor.getBlue();
        
        float b = (float) (HX/1.8+2) / (HX+1);
        int bgGrey = 20;
        float d;        
        
        byte red[] = new byte[realMaxRadius+1];
        red[0] = (byte) bgGrey;
        byte green[] = new byte[realMaxRadius+1];
        green[0] = (byte) bgGrey;
        byte blue[] = new byte[realMaxRadius+1];
        blue[0] = (byte) bgGrey;                    
                
        for (int i = realMaxRadius; i>=1; --i) {
            d        = (float) i / realMaxRadius;
            red[i]   = (byte) blend(blend(R, 255, d), bgGrey, b);
            green[i] = (byte) blend(blend(G, 255, d), bgGrey, b);
            blue[i]  = (byte) blend(blend(B, 255, d), bgGrey, b);        
        } // end for        
        
        IndexColorModel cylinderModel = new IndexColorModel(8, realMaxRadius+1,
                                                          red, green, blue, 0);
        
        Image cylinderImage = Toolkit.getDefaultToolkit().createImage(
           new MemoryImageSource(MAX_RADIUS*2, MAX_HEIGHT*2, 
                                 cylinderModel, cylinderData, 0, MAX_RADIUS*2));
        
        // and cache this image
        cylinderCache.put(symbol, 
                    new InMemoryCylinder(symbol, cylinderColor, cylinderImage));
        
        // clean up memory
        cylinderModel = null;
        red = green = blue = null;
    }
    
    /**
     * blend the color, sort of lighting function
     *
     * @param fg foreground
     * @param bg background
     * @param fgfactor where are we?
     */
    private int blend(int fg, int bg, float fgfactor) {
        return (int) (bg + (fg - bg) * fgfactor);
    }
    
    /** Inner class defining the "formed" cylinder of appropriate color */
    private class InMemoryCylinder {
        public Color cylinderColor;
        public Image cylinderImage;
        public String cylinderSymbol;
        
        public InMemoryCylinder(String symbol, Color color, Image image) {
            cylinderSymbol = symbol;
            cylinderColor  = color;
            cylinderImage  = image;            
        }
    } // end of class InMemoryCylinder
} // end of class CylinderFactory
