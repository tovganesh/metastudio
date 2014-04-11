/**
 * Cube.java
 *
 * Created on 27/04/2010
 */

package org.meta.math.geom;

/**
 * Represents a cube, which essentially is a BoundingBox
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Cube extends BoundingBox {

    /** Creates a new instance of Cube */
    public Cube() {
        super();
    }

    /** Creates a new instance of Cube */
    public Cube(Point3D upperLeft, Point3D bottomRight) {
        super(upperLeft, bottomRight);
    }
}
