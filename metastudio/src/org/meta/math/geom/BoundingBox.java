/*
 * BoundingBox.java
 *
 * Created on January 25, 2004, 11:49 AM
 */

package org.meta.math.geom;

/**
 * A class representing a rectangular bonding box.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BoundingBox extends AbstractGeometricObject
                         implements Cloneable {
    
    /** Holds value of property upperLeft. */
    private Point3D upperLeft;
    
    /** Holds value of property bottomRight. */
    private Point3D bottomRight;
    
    /** Creates a new instance of BoundingBox */
    public BoundingBox() {
        this(new Point3D(), new Point3D());
    }
    
    public BoundingBox(Point3D upperLeft, Point3D bottomRight) {
        this.upperLeft   = upperLeft;
        this.bottomRight = bottomRight;
    }
    
    /** Getter for property upperLeft.
     * @return Value of property upperLeft.
     *
     */
    public Point3D getUpperLeft() {
        return this.upperLeft;
    }
    
    /** Setter for property upperLeft.
     * @param upperLeft New value of property upperLeft.
     *
     */
    public void setUpperLeft(Point3D upperLeft) {
        this.upperLeft = upperLeft;
    }
    
    /** Getter for property bottomRight.
     * @return Value of property bottomRight.
     *
     */
    public Point3D getBottomRight() {
        return this.bottomRight;
    }
    
    /** Setter for property bottomRight.
     * @param bottomRight New value of property bottomRight.
     *
     */
    public void setBottomRight(Point3D bottomRight) {
        this.bottomRight = bottomRight;
    }
    
    /**
     * return the width in X direction
     *
     * @return width in X direction
     */
    public double getXWidth() {
        return Math.abs(bottomRight.getX() - upperLeft.getX());
    }
    
    /**
     * return the width in Y direction
     *
     * @return width in Y direction
     */
    public double getYWidth() {
        return Math.abs(bottomRight.getY() - upperLeft.getY());
    }
    
    /**
     * return the width in Z direction
     *
     * @return width in Z direction
     */
    public double getZWidth() {
        return Math.abs(bottomRight.getZ() - upperLeft.getZ());
    }
    
    /**
     * return the center of this box, defined as the midpoint of the 
     * line joining the points that define this BoundingBox
     *
     * @return Point3D - the center
     */
    public Point3D center() {
        Point3D center = new Point3D();
        
        center.setX((upperLeft.getX() + bottomRight.getX()) / 2.0);
        center.setY((upperLeft.getY() + bottomRight.getY()) / 2.0);
        center.setZ((upperLeft.getZ() + bottomRight.getZ()) / 2.0);
        
        return center;
    }

    /**
     * Return all coordinates of all the 8 corner points associated
     * with this bounding box. The order of points is:
     * (upperLeft, the rest of the three points in clock wise order)
     * (bottomRight, the rest of the three points in anti-clock wise order).
     *
     * The size of the returned array is always 8, representing 8 corners of
     * this bounding box.
     *
     * @return the corner points
     */
    public Point3D[] corners() {
        Point3D[] corners = new Point3D[8];

        corners[0] = (Point3D) upperLeft.clone();
        corners[1] = new Point3D(upperLeft.getX() + getXWidth(),
                                 upperLeft.getY(),
                                 upperLeft.getZ());
        corners[2] = new Point3D(upperLeft.getX() + getXWidth(),
                                 upperLeft.getY() + getYWidth(),
                                 upperLeft.getZ());
        corners[3] = new Point3D(upperLeft.getX(),
                                 upperLeft.getY() + getYWidth(),
                                 upperLeft.getZ());
        corners[4] = (Point3D) bottomRight.clone();
        corners[5] = new Point3D(bottomRight.getX() - getXWidth(),
                                 bottomRight.getY(),
                                 bottomRight.getZ());
        corners[6] = new Point3D(bottomRight.getX() - getXWidth(),
                                 bottomRight.getY() - getYWidth(),
                                 bottomRight.getZ());
        corners[7] = new Point3D(bottomRight.getX(),
                                 bottomRight.getY() - getYWidth(),
                                 bottomRight.getZ());
        
        return corners;
    }
    
    /**
     * Shift this bounding box to a new center
     *
     * @param center the new center
     * @return a new instalce of the shifted bounding box
     */
    public BoundingBox shiftTo(Point3D center) {        
        BoundingBox bb = new BoundingBox();                
        
        Point3D ul = bb.getUpperLeft();
        Point3D br = bb.getBottomRight();
        
        ul.setX((2.0 * center.getX() - getXWidth()) / 2.0);
        ul.setY((2.0 * center.getY() - getYWidth()) / 2.0);
        ul.setZ((2.0 * center.getZ() - getZWidth()) / 2.0);
        
        br.setX(ul.getX() + getXWidth());
        br.setY(ul.getY() + getYWidth());
        br.setZ(ul.getZ() + getZWidth());
        
        return bb;
    }
    
    /**
     * volume of this box given by: <br>
     * <code>V = a b c; where a, b and c are lengths along x, y and z 
     *       respectively.</code>
     *
     * @return the volume of this box
     */
    @Override
    public double volume() {
        return (getXWidth() * getYWidth() * getZWidth());
    }
    
    /**
     * total surface area of this box given by: <br>
     * <code>S = 2(ab + bc + ca); where a, b and c are lengths along x, y and z 
     *       respectively.</code>
     *
     * @return the volume of this box
     */
    @Override
    public double totalSurfaceArea() {
        return (2.0 * (
                         (getXWidth() * getYWidth()) 
                       + (getYWidth() * getZWidth()) 
                       + (getZWidth() * getXWidth())
                      ) 
               );
    }
    
    /**
     * combine 2 bounding boxes and return a third bigger one,
     * enclosing both of them.
     *
     * @param box - the second box to be considered
     * @return the bigger BoundingBox enclosing the two boxes
     */
    public BoundingBox combine(BoundingBox box) {
        BoundingBox bb = new BoundingBox();                              
        
        bb.upperLeft.setX(Math.min(upperLeft.getX(), box.upperLeft.getX()));
        bb.upperLeft.setY(Math.min(upperLeft.getY(), box.upperLeft.getY()));
        bb.upperLeft.setZ(Math.min(upperLeft.getZ(), box.upperLeft.getZ()));
        
        bb.bottomRight.setX(Math.max(bottomRight.getX(), 
                                     box.bottomRight.getX()));
        bb.bottomRight.setY(Math.max(bottomRight.getY(), 
                                     box.bottomRight.getY()));
        bb.bottomRight.setZ(Math.max(bottomRight.getZ(), 
                                     box.bottomRight.getZ()));                
        
        return bb;
    }

    /**
     * intersect 2 bounding boxes and return a third smaller one,
     * enclosing the common part among both of them.
     *
     * @param box - the second box to be considered
     * @return the bigger BoundingBox enclosing the two boxes
     */
    public BoundingBox intersect(BoundingBox box) {
        BoundingBox bb = new BoundingBox();                              
        Point3D bul = box.getUpperLeft();
        Point3D bbr = box.getBottomRight();
        
        Point3D ul = new Point3D(
                           Math.max(upperLeft.getX(), bul.getX()),
                           Math.max(upperLeft.getY(), bul.getY()),
                           Math.max(upperLeft.getZ(), bul.getZ())
                         );
        bb.setUpperLeft(ul);
        
        Point3D br = new Point3D(
                           Math.min(bottomRight.getX(), bbr.getX()),
                           Math.min(bottomRight.getY(), bbr.getY()),
                           Math.min(bottomRight.getZ(), bbr.getZ())
                         );
        bb.setBottomRight(br);
        
        return bb;
    }
    
    /**
     * Check whether a point is contained within this bounding box.
     *
     * @param point - the point to be checked
     * @return boolen - returns true if the point is within the bounding box
     *                  false otherwise
     */
    public boolean contains(Point3D point) {
        if ((point.getX() >= upperLeft.getX())
            && (point.getY() >= upperLeft.getY())
            && (point.getZ() >= upperLeft.getZ())
            && (point.getX() <= bottomRight.getX())
            && (point.getY() <= bottomRight.getY())
            && (point.getZ() <= bottomRight.getZ())) {
            return true;
        } else {
            return false;
        } // end if
    }

    /**
     * Expand this box along each side by a constant amount
     *
     * @param e the expansion parameter
     * @return the new bounding box
     */
    public BoundingBox expand(double e) {
        return new BoundingBox(upperLeft.add(-e), bottomRight.add(e));
    }

    /**
     * Expand this bounding box with variable lengths in each direction
     *
     * @param ex expansion length in X direction
     * @param ey expansion length in Y direction
     * @param ez expansion length in Z direction
     * @return the new bounding box
     */
    public BoundingBox expand(double ex, double ey, double ez) {
        return new BoundingBox(upperLeft.add(new Point3D(-ex, -ey, -ez)),
                               bottomRight.add(new Point3D(ex, ey, ez)));
    }

    /**
     * overridden toString()
     */
    @Override
    public String toString() {
        return ("UpperLeft: " + upperLeft + ", BottomRight: " + bottomRight);
    }
    
    /**
     * i am clonable
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return (new BoundingBox((Point3D) upperLeft.clone(), 
                                (Point3D) bottomRight.clone()));
    }

} // end of class BoundingBox
