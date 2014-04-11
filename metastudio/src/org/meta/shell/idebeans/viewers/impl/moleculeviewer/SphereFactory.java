/*
 * SphereFactory.java
 *
 * Created on May 7, 2005, 7:34 AM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;
import java.util.*;
import java.awt.image.*;

import org.meta.config.impl.AtomInfo;

/**
 * Class for fast rendering of spheres. Caching of sphers (optimised to show
 * atoms) is performed so as to have fast rendering.
 * Follows a singleton pattern.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SphereFactory {
  
    /** the only instance of SphereFactory */
    private static SphereFactory _sphereFactory;
    
    /** the common data assosiated with a sphere, i.e. the pixels that
        make up a sphere */
    private byte [] sphereData;
    
    /** maximum radius of in-memory sphere data */
    public final static int MAX_RADIUS = 100;
    
    /** other sphere related info */
    public final static int HX = MAX_RADIUS - 5;
    public final static int HY = MAX_RADIUS - 5;
    
    private int realMaxRadius;        
    
    /** Cache of the in-memory spheres */
    private HashMap<String, InMemorySphere> sphereCache;
    
    /** Creates a new instance of SphereFactory */
    public SphereFactory() {
        // set up the "raw" sphere data
        sphereData = new byte[MAX_RADIUS * 2 * MAX_RADIUS * 2];        
        int x0, y0, p, X, Y, x, y, r;
        
        realMaxRadius = 0;
        
        for (Y = 2*MAX_RADIUS; --Y >= 0;) {
            
            y0 = Y - MAX_RADIUS;
            x0 = (int) (Math.sqrt(MAX_RADIUS * MAX_RADIUS - y0*y0) + 0.5);
            p = Y * (MAX_RADIUS * 2) + MAX_RADIUS - x0;
            
            for (X = -x0; X<x0; X++) {
                x = X + HX;
                y = Y - MAX_RADIUS + HY;
                r = (int) (Math.sqrt(x * x + y * y) + 0.5);
                
                if (r > realMaxRadius) realMaxRadius = r;
                
                sphereData[p++] = r <= 0 ? 1 : (byte) r;
            } // end for
        } // end for               
        
        // TODO: we assume that we don't require a bigger cache than this!
        sphereCache = new HashMap<String, InMemorySphere>(10);
    }
    
    /**
     * get an instance of SphereFactory class
     *
     * @return an instance of SphereFactory
     */
    public static SphereFactory getInstance() {
        if (_sphereFactory == null) {
            _sphereFactory = new SphereFactory();
        } // end if
        
        return _sphereFactory;
    }
    
    /**
     * Get the image of the sphere for a particular atom symbol
     *
     * @param symbol the atomic symbol
     * @return the "formed" Image of the sphere
     */
    public Image getSphereImage(String symbol) {
        Color sphereColor = AtomInfo.getInstance().getColor(symbol);
        InMemorySphere memSphere = sphereCache.get(symbol);
        
        if (memSphere == null) {
            generateSphere(symbol, sphereColor);
        } else if (memSphere.sphereSymbol.equals(symbol) 
                   && (!memSphere.sphereColor.equals(sphereColor))) {
            generateSphere(symbol, sphereColor);
        } // end if
            
        return sphereCache.get(symbol).sphereImage;
    } 
    
    /**
     * Generate the "in-memory" sphere
     *
     * @param symbol the atomic symbol
     * @param sphereColor the color of this sphere
     */
    private void generateSphere(String symbol, Color sphereColor) {
        int R = sphereColor.getRed();
        int G = sphereColor.getGreen();
        int B = sphereColor.getBlue();
        
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
        
        IndexColorModel sphereModel = new IndexColorModel(8, realMaxRadius+1,
                                                          red, green, blue, 0);
        
        Image sphereImage = Toolkit.getDefaultToolkit().createImage(
               new MemoryImageSource(MAX_RADIUS*2, MAX_RADIUS*2, 
                                     sphereModel, sphereData, 0, MAX_RADIUS*2));
        
        // and cache this image
        sphereCache.put(symbol, 
                        new InMemorySphere(symbol, sphereColor, sphereImage));
        
        // clean up memory
        sphereModel = null;
        red = green = blue = null;
    }
    
    /**
     * blend the color, sort of lighting function
     *
     * @param fg forground
     * @param bg background
     * @param fgfactor where are we?
     */
    private int blend(int fg, int bg, float fgfactor) {
        return (int) (bg + (fg - bg) * fgfactor);
    }
    
    /** Inner class defining the "formed" sphere of appropriate color */
    private class InMemorySphere {
        public Color sphereColor;
        public Image sphereImage;
        public String sphereSymbol;
        
        public InMemorySphere(String symbol, Color color, Image image) {
            sphereSymbol = symbol;
            sphereColor  = color;
            sphereImage  = image;            
        }
    } // end of class InMemorySphere
} // end of class SphereFactory
