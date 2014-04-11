/*
 * Point3DI.java
 *
 * Created on October 21, 2005, 10:02 PM
 *
 */

package org.meta.math.geom;

/**
 * A special class to hold transformed integer part of a Point3D object.
 * This special object also contains a reference to a normal defined for this
 * point.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Point3DI extends Point3D {
    
    /** Creates a new instance of Point3DI */
    public Point3DI(double x, double y, double z) {
        this(x, y, z, -1);
    }
    
    /** Creates a new instance of Point3DI */
    public Point3DI(Point3D p) {
        this(p.getX(), p.getY(), p.getZ(), -1);
    }
    
    /** Creates a new instance of Point3DI */
    public Point3DI(double x, double y, double z, int index) {
        super(x, y, z);
        
        currentX = (int) x;
        currentY = (int) y;
        currentZ = (int) z;
                
        this.index = index;
    }
    
    /**
     * Holds value of property currentX.
     */
    private int currentX;

    /**
     * Getter for property currentX.
     * @return Value of property currentX.
     */
    public int getCurrentX() {
        return this.currentX;
    }

    /**
     * Setter for property currentX.
     * @param currentX New value of property currentX.
     */
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    /**
     * Holds value of property currentY.
     */
    private int currentY;

    /**
     * Getter for property currentY.
     * @return Value of property currentY.
     */
    public int getCurrentY() {
        return this.currentY;
    }

    /**
     * Setter for property currentY.
     * @param currentY New value of property currentY.
     */
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    /**
     * Holds value of property currentZ.
     */
    private int currentZ;

    /**
     * Getter for property currentZ.
     * @return Value of property currentZ.
     */
    public int getCurrentZ() {
        return this.currentZ;
    }

    /**
     * Setter for property currentZ.
     * @param currentZ New value of property currentZ.
     */
    public void setCurrentZ(int currentZ) {
        this.currentZ = currentZ;
    }        

    /**
     * Holds value of property index.
     */
    private int index;

    /**
     * Getter for property index.
     * @return Value of property index.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Setter for property index.
     * @param index New value of property index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

     /**
     * i do some cloning business ;)
     */
    @Override
    public Object clone() {
        Point3DI p = new Point3DI(getX(), getY(), getZ());
        p.currentX = currentX;
        p.currentY = currentY;
        p.currentZ = currentZ;

        return p;
    }

    /**
     * overloaded equals() method
     */
    @Override
    public boolean equals(Object obj) { 
        if (this == obj) return true;
        
        if ((obj == null) || (!(obj instanceof Point3DI))) {
            return false;
        } else {
            Point3DI o = (Point3DI) obj;
        
            return ((super.equals(o)) && (this.index == o.index));
        } // end if
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.currentX;
        hash = 83 * hash + this.currentY;
        hash = 83 * hash + this.currentZ;
        hash = 83 * hash + this.index;
        return hash;
    }
    
} // end of class Point3DI
