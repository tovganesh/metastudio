/**
 * AbstractGeometricObject.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

import java.io.Serializable;

/**
 * Default implementation of GeometricObject
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractGeometricObject implements GeometricObject,
        Serializable {

    /** Creates a new instance of AbstractGeometricObject */
    public AbstractGeometricObject() {
    }

    /**
     * Total surface area of this geometic object, zero in this case
     *
     * @return the surface in appropriate units
     */
    @Override
    public double totalSurfaceArea() {
        return 0.0;
    }

    /**
     * Total volume of this geometric object, zero in this case
     *
     * @return the volume in appropriate units
     */
    @Override
    public double volume() {
        return 0.0;
    }
}
