/*
 * InterpolaterFactory.java
 *
 * Created on July 29, 2007, 10:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.interpolater;

import java.util.Hashtable;

/**
 * A factory interface for creating Interpolater instance of various types.
 * Uses a Singleton pattern for creating instance of this class.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class InterpolaterFactory {
  
    private static InterpolaterFactory _interpolaterFactory;

    private Hashtable<InterpolaterType, Interpolater> interpolaters;
    
    /** Creates a new instance of InterpolaterFactory */
    private InterpolaterFactory() {
        interpolaters = new Hashtable<InterpolaterType, Interpolater>();
        
        interpolaters.put(InterpolaterType.LINEAR, new LinearInterpolater());
        interpolaters.put(InterpolaterType.COSINE, new CosineInterpolater());
        interpolaters.put(InterpolaterType.CUBIC,  new CubicInterpolater());
        interpolaters.put(InterpolaterType.TRI_LINEAR, 
                                                   new TriLinearInterpolater());
        interpolaters.put(InterpolaterType.THREE_POINT, 
                                                   new ThreePointInterpolater());
    }
    
    /**
     * Returns an instance of InterpolaterFactory.
     *
     * @return InterpolaterFactory instance (one and only one)
     */    
    public static final InterpolaterFactory getInstance() {        
        if (_interpolaterFactory == null) {
            _interpolaterFactory = new InterpolaterFactory();
        } // end if
        
        return _interpolaterFactory;
    }

    /**
     * Supported interpolater types
     */
    public enum InterpolaterType {
        LINEAR, COSINE, CUBIC, TRI_LINEAR, THREE_POINT
    }

    /**
     * Get instance of a Interpolater type
     *
     * @param iType the type of Interpolater requested
     * @return an instance of Interpolater as requested, if none available 
     *         a null is returned.
     */
    public Interpolater getInterpolater(InterpolaterType iType) {
        try {
            return interpolaters.get(iType);
        } catch (Exception e) {
            return null;
        } // end of try .. catch block
    }
} // end of class InterpolaterFactory
