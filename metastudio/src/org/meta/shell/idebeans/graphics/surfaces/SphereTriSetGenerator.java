/*
 * SphereTriSetGenerator.java
 *
 * Created on September 29, 2005, 10:14 PM
 *
 */

package org.meta.shell.idebeans.graphics.surfaces;

import java.util.*;

import org.meta.math.geom.Point3D;
import org.meta.math.geom.Point3DI;
import org.meta.math.geom.Triangle;
import org.meta.math.Matrix3D;

/**
 * A triset generator for sphere object.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SphereTriSetGenerator extends TriSetGenerator {         
    
    private ArrayList<Point3DI> points;
    private ArrayList<Triangle> triSets;
    private Iterator<Triangle> triSetsIterator;
    
    /** Creates a new instance of SphereTriSetGenerator 
     *
     * @param center - the center of the sphere, for which the tri set 
     *                 is to be generated
     * @param radius - the radius of the required sphere
     */
    public SphereTriSetGenerator(Point3D center, double radius) {
        this(center, radius, 10.0);
    }
    
    /** Creates a new instance of SphereTriSetGenerator 
     *
     * @param center - the center of the sphere, for which the tri set 
     *                 is to be generated
     * @param radius - the radius of the required sphere
     * @param divisions - the number of theta and phi divisions
     */
    public SphereTriSetGenerator(Point3D center, double radius, 
                                 double divisions) {
        this.center = center;
        this.radius = radius;                          
        
        this.deltaPhi   = Math.PI / divisions;
        this.deltaTheta = Math.PI / divisions;
        
        this.transform = new Matrix3D();
        
        initPoints();
    }
    
    /**
     * initialize the tri-set points
     */
    private void initPoints() {        
        double theta, phi;
        double x, y, z, r;
        int noOfPoints = 0, i, m, idx = 0;

        x = y = z = 0.0; 
        r = radius;

        this.points  = new ArrayList<Point3DI>();
        this.triSets = new ArrayList<Triangle>();
        
        // at the first call generate the trisets            
        for(phi=0, i=0; phi<=2.0*Math.PI; phi=deltaPhi*i, i++) {
            for(theta=0.0, m=0; theta<=2.0*Math.PI; theta=deltaTheta*m, m++) {
                x = r * Math.cos(theta);
                y = r * Math.sin(theta);

                points.add(new Point3DI(x+center.getX(), 
                                        y+center.getY(), 
                                        z+center.getZ(), idx++
                                       ));                
            } // end for                
            
            // get the number of points in one circle
            if (i == 0) { 
                noOfPoints = points.size();
            } else {                
                // form the tri-set
                int j = i * noOfPoints;
                int k = (i-1) * noOfPoints;
                int l;
                
                if (phi <= Math.PI) {
                 for(l=0; l<noOfPoints-1; l++) {                    
                    triSets.add(new Triangle(points.get(j+l),
                                           points.get(k+l), 
                                           points.get(k+1+l)));
                    triSets.add(new Triangle(points.get(j+l),
                                           points.get(k+1+l), 
                                           points.get(j+l+1)));
                 } // end for
                } else {
                 for(l=0; l<noOfPoints-1; l++) {                    
                    triSets.add(new Triangle(points.get(k+l),
                                           points.get(j+l), 
                                           points.get(j+1+l)));
                    triSets.add(new Triangle(points.get(j+1+l),
                                           points.get(k+1+l), 
                                           points.get(k+l)));
                 } // end for   
                } // end if
            } // end if

            r = radius * Math.cos(phi);
            z = radius * Math.sin(phi);
        } // end for                        

        // form the tri-set .. connect last set to the initial set
        int j = 0;
        int k = (i-1) * noOfPoints;
        int l;
        for(l=0; l<noOfPoints-1; l++) {
            triSets.add(new Triangle(points.get(k+l),
                                   points.get(j+l),
                                   points.get(j+1+l)));
            triSets.add(new Triangle(points.get(j+1+l),
                                   points.get(k+1+l),
                                   points.get(k+l)));
        } // end for
        
        // apply transforms
        applyTransforms();                        

        points.trimToSize();
        triSets.trimToSize();                
        
        triSetsIterator = triSets.iterator();                        
    }
    
    /**
     * Returns true or false indicating availablity of any further tri sets
     * for a 3D object or surface.
     *
     * @return a boolean indicative of availability of a tri-set for the object
     * under construction.
     */
    @Override
    public boolean isOver() {        
        return !triSetsIterator.hasNext();
    }
    
    /**
     * Returns the next visible tri set, after performing object based, self
     * back-face culling.
     *
     * @return a valid Triangle object if something is available, else returns
     * a null.
     */
    @Override
    public Triangle nextVisibleTriSet() {
        if (triSetsIterator.hasNext()) {            
            return triSetsIterator.next();
        } else {
            return null;
        } // end if
    }
    
    /**
     * method to apply scene transformations
     */
    @Override
    public void applyTransforms() {
        transform.transformPoints(points);
    }

    /**
     * Holds value of property radius.
     */
    private double radius;

    /**
     * Getter for property radius.
     * @return Value of property radius.
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Setter for property radius.
     * @param radius New value of property radius.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Holds value of property center.
     */
    private Point3D center;

    /**
     * Getter for property center.
     * @return Value of property center.
     */
    public Point3D getCenter() {
        return this.center;
    }

    /**
     * Setter for property center.
     * @param center New value of property center.
     */
    public void setCenter(Point3D center) {
        // if points have been generated, subtract the current center
        // and then add the new center.
        if (points != null) {
            double newX = center.getX(),
                   newY = center.getY(),
                   newZ = center.getZ();
            double oldX = this.center.getX(),
                   oldY = this.center.getY(),
                   oldZ = this.center.getZ();
            
            // generate new points
            for(Point3D point : points) {
                point.setX(point.getX() - oldX + newX);
                point.setY(point.getY() - oldY + newY);
                point.setZ(point.getZ() - oldZ + newZ);
            } // end for
            
            // apply transforms
            applyTransforms();                         

            // reset iterator
            triSetsIterator = triSets.iterator();
        } // end if
        
        this.center = center;
    }

    /**
     * Holds value of property deltaPhi.
     */
    private double deltaPhi;

    /**
     * Getter for property deltaPhi.
     * @return Value of property deltaPhi.
     */
    public double getDeltaPhi() {
        return this.deltaPhi;
    }

    /**
     * Setter for property deltaPhi.
     * @param deltaPhi New value of property deltaPhi.
     */
    public void setDeltaPhi(double deltaPhi) {
        this.deltaPhi = deltaPhi;
    }

    /**
     * Holds value of property deltaTheta.
     */
    private double deltaTheta;

    /**
     * Getter for property deltaTheta.
     * @return Value of property deltaTheta.
     */
    public double getDeltaTheta() {
        return this.deltaTheta;
    }

    /**
     * Setter for property deltaTheta.
     * @param deltaTheta New value of property deltaTheta.
     */
    public void setDeltaTheta(double deltaTheta) {
        this.deltaTheta = deltaTheta;
    }
    
    /**
     * resets the Triangle iterator
     */
    @Override
    public void resetTriSetIterator() {
        triSetsIterator = triSets.iterator();
    }
    
    /**
     * Number of elements generated.
     *
     * @return the size of the generated trisets.
     */
    @Override
    public int size() {
        return triSets.size();
    }
} // end of class SphereTriSetGenerator
