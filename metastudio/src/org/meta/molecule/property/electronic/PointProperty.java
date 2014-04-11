/*
 * PointProperty.java
 *
 * Created on October 11, 2007, 9:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.molecule.property.electronic;

import org.meta.math.geom.Point3D;

/**
 * Represents a property value at a point. This is just a wrapper and is
 * not used in GridProperty class because of performance and memory penalties.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PointProperty {
    
    /** Creates a new instance of PointProperty */
    public PointProperty() {
    }
    
    /**
     * Holds value of property point.
     */
    private Point3D point;
    
    /**
     * Getter for property point.
     * @return Value of property point.
     */
    public Point3D getPoint() {
        return this.point;
    }
    
    /**
     * Setter for property point.
     * @param point New value of property point.
     */
    public void setPoint(Point3D point) {
        this.point = point;
    }
    
    /**
     * Holds value of property value.
     */
    private double value;
    
    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public double getValue() {
        return this.value;
    }
    
    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(double value) {
        this.value = value;
    }
    
    
    /**
     * overloaded toString()
     */
    @Override
    public String toString() {
        return "Point: " + point.toString() 
               + "; Function value: " + value;
    }
} // end of class PointProperty
