/**
 * GeometricObject.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

/**
 * Top level interface for all geometric objects in MeTA Studio
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface GeometricObject {

    /**
     * Total surface area of this geometic object
     * 
     * @return the surface in appropriate units
     */
    public double totalSurfaceArea();

    /**
     * Total volume of this geometric object
     *
     * @return the volume in appropriate units
     */
    public double volume();
}
