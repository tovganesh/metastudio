/*
 * GridProperty.java
 *
 * Created on July 15, 2005, 2:58 PM
 *
 */

package org.meta.molecule.property.electronic;

import java.util.*;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.math.interpolater.Interpolater;

/**
 * This class define the data store for a grid based property object
 * (one-electron properties like MESP, MED etc.)
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GridProperty {
    
    // internal variable used to maintain consistancy of data provided
    private boolean stateChanged;
    
    private double minFunctionValue, maxFunctionValue;
    private int noOfPositiveFunctionValues, noOfNegativeFunctionValues;
    
    /** Creates a new instance of GridProperty */
    public GridProperty() {
        this(new BoundingBox(), 0.0, 0.0, 0.0, 0, 0, 0, new double[1]);
    }

    /** Creates a new instance of GridProperty */
    public GridProperty(BoundingBox bb, double inc) {
        this(bb, inc, inc, inc,
             (int) (bb.getXWidth()/inc),
             (int) (bb.getYWidth()/inc),
             (int) (bb.getZWidth()/inc),
             new double[((int) (bb.getXWidth()/inc)) *
                        ((int) (bb.getYWidth()/inc)) *
                        ((int) (bb.getZWidth()/inc))]);
    }

    /** Creates a new instance of GridProperty */
    public GridProperty(BoundingBox bb, double xinc, double yinc, double zinc) {
        this(bb, xinc, yinc, zinc,
             (int) (bb.getXWidth()/xinc),
             (int) (bb.getYWidth()/yinc),
             (int) (bb.getZWidth()/zinc),
             new double[((int) (bb.getXWidth()/xinc)) *
                        ((int) (bb.getYWidth()/yinc)) *
                        ((int) (bb.getZWidth()/zinc))]);
    }

    /** Creates a new instance of GridProperty */
    public GridProperty(BoundingBox bb, int xyzPoints) {
        this(bb, bb.getXWidth()/xyzPoints, bb.getYWidth()/xyzPoints,
                 bb.getZWidth()/xyzPoints);
    }

    /** Creates a new instance of GridProperty */
    public GridProperty(BoundingBox bb, int xpnt, int ypnt, int zpnt) {
        this(bb, bb.getXWidth()/xpnt, bb.getYWidth()/ypnt, bb.getZWidth()/zpnt);
    }

    /** Creates a new instance of GridProperty */
    public GridProperty(BoundingBox bb, double xinc, double yinc, double zinc,
                        int xpnt, int ypnt, int zpnt, double [] funcVals) {
        boundingBox = bb;
        
        xIncrement = xinc;
        yIncrement = yinc;
        zIncrement = zinc;
        noOfPointsAlongX = xpnt;
        noOfPointsAlongY = ypnt;
        noOfPointsAlongZ = zpnt;
        functionValues = funcVals;
        
        stateChanged = true;
    }
    
    /**
     * compute min, max function values
     */
    private synchronized void computeMinMax() {
        if (functionValues == null) {
            stateChanged = false;
            minFunctionValue = maxFunctionValue = 0.0;
            noOfNegativeFunctionValues = noOfPositiveFunctionValues = 0;
            
            return;
        } // end if
        
        minFunctionValue = maxFunctionValue = functionValues[0];
        
        noOfNegativeFunctionValues = noOfPositiveFunctionValues = 0;
        
        for(int i=0; i<functionValues.length; i++) {
            if (functionValues[i] < minFunctionValue)
                minFunctionValue = functionValues[i];
            if (functionValues[i] > maxFunctionValue)
                maxFunctionValue = functionValues[i];
            
            if (functionValues[i] >= 0.0) noOfPositiveFunctionValues++;
            else                          noOfNegativeFunctionValues++;
        } // end for
        
        stateChanged = false;
    }
    
    /**
     * Recompute increments along X, Y and Z
     */
    public synchronized void recomputeIncrements() {
        setXIncrement(getBoundingBox().getXWidth() / getNoOfPointsAlongX());
        setYIncrement(getBoundingBox().getYWidth() / getNoOfPointsAlongY());
        setZIncrement(getBoundingBox().getZWidth() / getNoOfPointsAlongZ());
    }
    
    /**
     * Holds value of property boundingBox.
     */
    private BoundingBox boundingBox;

    /**
     * Getter for property boundingBox.
     * @return Value of property boundingBox.
     */
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * Setter for property boundingBox.
     * @param boundingBox New value of property boundingBox.
     */
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * Holds value of property xIncrement.
     */
    private double xIncrement;

    /**
     * Getter for property xIncrement.
     * @return Value of property xIncrement.
     */
    public double getXIncrement() {
        return this.xIncrement;
    }

    /**
     * Setter for property xIncrement.
     * @param xIncrement New value of property xIncrement.
     */
    public void setXIncrement(double xIncrement) {
        this.xIncrement = xIncrement;
    }

    /**
     * Holds value of property yIncrement.
     */
    private double yIncrement;

    /**
     * Getter for property yIncrement.
     * @return Value of property yIncrement.
     */
    public double getYIncrement() {
        return this.yIncrement;
    }

    /**
     * Setter for property yIncrement.
     * @param yIncrement New value of property yIncrement.
     */
    public void setYIncrement(double yIncrement) {
        this.yIncrement = yIncrement;
    }

    /**
     * Holds value of property zIncrement.
     */
    private double zIncrement;

    /**
     * Getter for property zIncrement.
     * @return Value of property zIncrement.
     */
    public double getZIncrement() {
        return this.zIncrement;
    }

    /**
     * Setter for property zIncrement.
     * @param zIncrement New value of property zIncrement.
     */
    public void setZIncrement(double zIncrement) {
        this.zIncrement = zIncrement;
    }

    /**
     * Holds value of property noOfPointsAlongX.
     */
    private int noOfPointsAlongX;

    /**
     * Getter for property noOfPointsAlongX.
     * @return Value of property noOfPointsAlongX.
     */
    public int getNoOfPointsAlongX() {
        return this.noOfPointsAlongX;
    }

    /**
     * Setter for property noOfPointsAlongX.
     * @param noOfPointsAlongX New value of property noOfPointsAlongX.
     */
    public void setNoOfPointsAlongX(int noOfPointsAlongX) {
        this.noOfPointsAlongX = noOfPointsAlongX;
    }

    /**
     * Holds value of property noOfPointsAlongY.
     */
    private int noOfPointsAlongY;

    /**
     * Getter for property noOfPointsAlongY.
     * @return Value of property noOfPointsAlongY.
     */
    public int getNoOfPointsAlongY() {
        return this.noOfPointsAlongY;
    }

    /**
     * Setter for property noOfPointsAlongY.
     * @param noOfPointsAlongY New value of property noOfPointsAlongY.
     */
    public void setNoOfPointsAlongY(int noOfPointsAlongY) {
        this.noOfPointsAlongY = noOfPointsAlongY;
    }

    /**
     * Holds value of property noOfPointsAlongZ.
     */
    private int noOfPointsAlongZ;

    /**
     * Getter for property noOfPointsAlongZ.
     * @return Value of property noOfPointsAlongZ.
     */
    public int getNoOfPointsAlongZ() {
        return this.noOfPointsAlongZ;
    }

    /**
     * Setter for property noOfPointsAlongZ.
     * @param noOfPointsAlongZ New value of property noOfPointsAlongZ.
     */
    public void setNoOfPointsAlongZ(int noOfPointsAlongZ) {
        this.noOfPointsAlongZ = noOfPointsAlongZ;
    }

    /**
     * Get the function value on or near this point
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the value
     */
    public double getFunctionValueAt(double x, double y, double z) {
        double xMin = boundingBox.getUpperLeft().getX(),
               yMin = boundingBox.getUpperLeft().getY(),
               zMin = boundingBox.getUpperLeft().getZ();  
        
        int i = (int) Math.rint(Math.abs((x-xMin)/xIncrement));
        int j = (int) Math.rint(Math.abs((y-yMin)/yIncrement));
        int k = (int) Math.rint(Math.abs((z-zMin)/zIncrement));
        
        return getFunctionValueAt(i, j, k);
    }        

    /**
     * Get the function value on or near this point
     *
     * @param i ith index alog X direction
     * @param j jth index alog Y direction
     * @param k kth index alog Z direction
     * @return the value
     */    
    public double getFunctionValueAt(int i, int j, int k) {
        if (i >= noOfPointsAlongX) i = noOfPointsAlongX-1;
        if (j >= noOfPointsAlongY) j = noOfPointsAlongY-1;
        if (k >= noOfPointsAlongZ) k = noOfPointsAlongZ-1;
        
        int pointsAlongSlice = noOfPointsAlongZ*noOfPointsAlongY;
        
        return getFunctionValues((i*pointsAlongSlice)
                                    + (j*noOfPointsAlongZ) + k);
    }        
    
    
    private double [] xVals = new double[9];
    private double [] yVals = new double[8];
    /**
     * Get interpolated function value on this point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param interpolater the Interpolater (preferably trilinear)     
     * @return the value
     */
    public double getInterpolatedFunctionValueAt(double x, double y, double z,
                                                 Interpolater interpolater) {
        Point3D p0 = getGridPointNear(x, y, z);
        int [] idx = getGridIndexNear(x, y, z);
        
        // else we apply interpolation        
        xVals = new double [] {p0.getX(), p0.getY(), p0.getZ(),
                               x, y, z,
                               xIncrement, yIncrement, zIncrement}; 
        
        yVals[0] = getFunctionValueAt(idx[0]  , idx[1]  , idx[2]  );
        yVals[1] = getFunctionValueAt(idx[0]  , idx[1]  , idx[2]+1);
        yVals[2] = getFunctionValueAt(idx[0]  , idx[1]+1, idx[2]  );
        yVals[3] = getFunctionValueAt(idx[0]  , idx[1]+1, idx[2]+1);
        yVals[4] = getFunctionValueAt(idx[0]+1, idx[1]  , idx[2]  );
        yVals[5] = getFunctionValueAt(idx[0]+1, idx[1]  , idx[2]+1);
        yVals[6] = getFunctionValueAt(idx[0]+1, idx[1]+1, idx[2]  );
        yVals[7] = getFunctionValueAt(idx[0]+1, idx[1]+1, idx[2]+1);
                
        return interpolater.interpolate(yVals, xVals);
    }
    
    /**
     * Get the function value on or near this point
     *
     * @param point the point at which the function value needs to be found
     * @return the value
     */
    public double getFunctionValueAt(Point3D point) {
        return getFunctionValueAt(point.getX(), point.getY(), point.getZ());
    }
    
    /**
     * Holds value of property functionValues.
     */
    private double[] functionValues;

    /**
     * Indexed getter for property functionValues.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public double getFunctionValues(int index) {
           return this.functionValues[index];
    }

    /**
     * Getter for property functionValues.
     * @return Value of property functionValues.
     */
    public double[] getFunctionValues() {
        return this.functionValues;
    }

    /**
     * Indexed setter for property functionValues.
     * @param index Index of the property.
     * @param functionValues New value of the property at <CODE>index</CODE>.
     */
    public void setFunctionValues(int index, double functionValues) {
        this.functionValues[index] = functionValues;
        
        stateChanged = true;
    }

    /**
     * Setter for property functionValues.
     * @param functionValues New value of property functionValues.
     */
    public void setFunctionValues(double[] functionValues) {
        this.functionValues = functionValues;
        
        stateChanged = true;
    }
    
    /**
     * return the total number of points in the grid
     *
     * @return the number of points in the grid
     */
    public int getNumberOfGridPoints() {
        return (noOfPointsAlongX * noOfPointsAlongY * noOfPointsAlongZ);
    }
    
    /**
     * Get the maximum function value for this grid.
     *
     * @return the maximum function value for this grid
     */
    public double getMaxFunctionValue() {
        if (stateChanged) computeMinMax();
        
        return maxFunctionValue;
    }
    
    /**
     * Get the minimum function value for this grid.
     *
     * @return the minimum function value for this grid
     */
    public double getMinFunctionValue() {
        if (stateChanged) computeMinMax();
        
        return minFunctionValue;
    }

    /**
     * Return a list of values that fall in the range (lowerLimit, upperLimit).
     *
     * @param lowerLimit the lower limit (inclusive)
     * @param upperLimit the upper limit (inclusive)
     * @return an ArrayList object containing all the function values which fall 
     *         with in the range (lowerLimit, upperLimit). In case there are
     *         no function values at all to check a <code>null</code> 
     *         is returned.
     */
    public ArrayList<Double> getFunctionValuesInRange(double lowerLimit, 
                                                      double upperLimit) {
        if (functionValues == null) return null;
        
        ArrayList<Double> fVals = new ArrayList<Double>();
        
        // just flip if not normal
        if (lowerLimit > upperLimit) {
            double temp = lowerLimit;
            lowerLimit = upperLimit;
            upperLimit = temp;
        } // end if
        
        for(int i=0; i<functionValues.length; i++) {
            if ((functionValues[i] >= lowerLimit) 
                && (functionValues[i] <= upperLimit)) {
                fVals.add(new Double(functionValues[i]));
            } // end if
        } // end for
        
        return fVals;
    }
    
    /**
     * Return number of values that fall in the range (lowerLimit, upperLimit).
     *
     * @param lowerLimit the lower limit (inclusive)
     * @param upperLimit the upper limit (inclusive)
     * @return an integer indicating the number of function values satisfying 
     *         the range constraint
     */
    public int getNumberOfFunctionValuesInRange(double lowerLimit, 
                                                double upperLimit) {
        if (functionValues == null) return 0;
        
        int count = 0;
        
        // just flip if not normal
        if (lowerLimit > upperLimit) {
            double temp = lowerLimit;
            lowerLimit = upperLimit;
            upperLimit = temp;
        } // end if
        
        for(int i=0; i<functionValues.length; i++) {
            if ((functionValues[i] >= lowerLimit) 
                && (functionValues[i] <= upperLimit)) {
                count++;
            } // end if
        } // end for
        
        return count;
    }
    
    /**
     * Get number of negative function values.
     *
     * @return an integer indicating number of negative function values
     */
    public int getNoOfNegativeFunctionValues() {
        if (stateChanged) computeMinMax();
        
        return noOfNegativeFunctionValues;
    }
    
    /**
     * Get number of positive function values.
     *
     * @return an integer indicating number of positive function values
     */
    public int getNoOfPositiveFunctionValues() {
        if (stateChanged) computeMinMax();
        
        return noOfPositiveFunctionValues;
    }
    
    /**
     * the overridden toString() method.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Grid limits: \n");
        sb.append(boundingBox + "\n");
        sb.append("Increment (points) along X: " + xIncrement + " (" + 
                   noOfPointsAlongX + ") \n");
        sb.append("Increment (points) along Y: " + yIncrement + " (" + 
                   noOfPointsAlongY + ") \n");
        sb.append("Increment (points) along Z: " + zIncrement + " (" + 
                   noOfPointsAlongZ + ") \n");
        sb.append("Total number of points: " + getNumberOfGridPoints() + "\n");
        
        return sb.toString();
    }
    
    /**
     * Addition of two GridProperty objects.
     * This operation is only defined at the common positions between the 
     * GridProperty objects this and gp.
     * This operation is defined a specified position as <code>this + gp</code>. 
     *
     * @param gp the GridProperty object to be added to this object
     * @return the resultant new GridProperty object     
     */
    public GridProperty add(GridProperty gp) {
        // first the new bounding box is a combination
        // of the two boxes.
        BoundingBox newBB = gp.getBoundingBox().intersect(getBoundingBox());
        Point3D newUL = newBB.getUpperLeft();
                
        // then the new increments will be lesser of the two
        double xInc = Math.min(gp.getXIncrement(), xIncrement);
        double yInc = Math.min(gp.getYIncrement(), yIncrement);
        double zInc = Math.min(gp.getZIncrement(), zIncrement);
        
        // and then we need to recompute the number of points in the
        // "added" grid
        int nx = (int) (newBB.getXWidth() / xInc);
        int ny = (int) (newBB.getYWidth() / yInc);
        int nz = (int) (newBB.getZWidth() / zInc);
        
        // then fill in the new function values
        double [] fvals = new double[nx*ny*nz];
        int ii = 0, i, j, k;
        double x, y, z;
        
        for(x=newUL.getX(), i=0; i<nx; x=newUL.getX()+(xInc*i), i++) {
            for(y=newUL.getY(), j=0; j<ny; y=newUL.getY()+(yInc*j), j++) {
                for(z=newUL.getZ(), k=0; k<nz; z=newUL.getZ()+(zInc*k), k++) {
                    fvals[ii++] = getFunctionValueAt(x, y, z)
                                  + gp.getFunctionValueAt(x, y, z);
                } // end for
            } // end for
        } // end for      
        
        return new GridProperty(newBB, xInc, yInc, zInc, nx, ny, nz, fvals);
    }
    
    /**
     * Substraction of two GridProperty objects.
     * This operation is only defined at the common positions between the 
     * GridProperty objects this and gp.
     * This operation is defined a specified position as <code>this - gp</code>. 
     *
     * @param gp the GridProperty object to be subtracted from this object
     * @return the resultant new GridProperty object     
     */
    public GridProperty subtract(GridProperty gp) {
        // first the new bounding box is a combination
        // of the two boxes.
        BoundingBox newBB = gp.getBoundingBox().intersect(getBoundingBox());
        Point3D newUL = newBB.getUpperLeft();
                
        // then the new increments will be lesser of the two
        double xInc = Math.min(gp.getXIncrement(), xIncrement);
        double yInc = Math.min(gp.getYIncrement(), yIncrement);
        double zInc = Math.min(gp.getZIncrement(), zIncrement);
        
        // and then we need to recompute the number of points in the
        // "added" grid
        int nx = (int) (newBB.getXWidth() / xInc);
        int ny = (int) (newBB.getYWidth() / yInc);
        int nz = (int) (newBB.getZWidth() / zInc);
        
        // then fill in the new function values
        double [] fvals = new double[nx*ny*nz];
        int ii = 0, i, j, k;
        double x, y, z;
        
        for(x=newUL.getX(), i=0; i<nx; x=newUL.getX()+(xInc*i), i++) {
            for(y=newUL.getY(), j=0; j<ny; y=newUL.getY()+(yInc*j), j++) {
                for(z=newUL.getZ(), k=0; k<nz; z=newUL.getZ()+(zInc*k), k++) {
                    fvals[ii++] = getFunctionValueAt(x, y, z)
                                  - gp.getFunctionValueAt(x, y, z);
                } // end for
            } // end for
        } // end for      
        
        return new GridProperty(newBB, xInc, yInc, zInc, nx, ny, nz, fvals);
    }
    
    /**
     * Product of two GridProperty objects.
     * This operation is only defined at the common positions between the 
     * GridProperty objects this and gp.
     * This operation is defined a specified position as <code>this * gp</code>. 
     *
     * @param gp the GridProperty object to be subtracted from this object
     * @return the resultant new GridProperty object     
     */
    public GridProperty product(GridProperty gp) {
        // first the new bounding box is a combination
        // of the two boxes.
        BoundingBox newBB = gp.getBoundingBox().intersect(getBoundingBox());
        Point3D newUL = newBB.getUpperLeft();
                
        // then the new increments will be lesser of the two
        double xInc = Math.min(gp.getXIncrement(), xIncrement);
        double yInc = Math.min(gp.getYIncrement(), yIncrement);
        double zInc = Math.min(gp.getZIncrement(), zIncrement);
        
        // and then we need to recompute the number of points in the
        // "added" grid
        int nx = (int) (newBB.getXWidth() / xInc);
        int ny = (int) (newBB.getYWidth() / yInc);
        int nz = (int) (newBB.getZWidth() / zInc);
        
        // then fill in the new function values
        double [] fvals = new double[nx*ny*nz];
        int ii = 0, i, j, k;
        double x, y, z;
        
        for(x=newUL.getX(), i=0; i<nx; x=newUL.getX()+(xInc*i), i++) {
            for(y=newUL.getY(), j=0; j<ny; y=newUL.getY()+(yInc*j), j++) {
                for(z=newUL.getZ(), k=0; k<nz; z=newUL.getZ()+(zInc*k), k++) {
                    fvals[ii++] = getFunctionValueAt(x, y, z)
                                  * gp.getFunctionValueAt(x, y, z);
                } // end for
            } // end for
        } // end for      
        
        return new GridProperty(newBB, xInc, yInc, zInc, nx, ny, nz, fvals);
    }
    
    /**
     * Lock & Key operation on two GridProperty objects.
     * The lock and key operation is defined on only the common bouding box 
     * region. The operation performed is the following: <br>
     * <code>this + gp</code> only if the following conditions are satisfied:
     * <ol>
     *  <li> this and gp at the specified position are opposite in signs.</li>
     *  <li> <code>|this + gp|</code> at the specified postion is less than 
     *       <code>threshold</code>.</li>
     * </ol>
     * Else the value at the specified postion is set to zero.
     *
     * @param gp the GridProperty object to be subtracted from this object
     * @param threshold the threshold condition for perfoming lock and key 
     *        operation (see above).
     * @return the resultant new GridProperty object     
     */
    public GridProperty lockAndKey(GridProperty gp, double threshold) {
        // first the new bounding box is a combination
        // of the two boxes.
        BoundingBox newBB = gp.getBoundingBox().intersect(getBoundingBox());
        Point3D newUL = newBB.getUpperLeft();
                
        // then the new increments will be lesser of the two
        double xInc = Math.min(gp.getXIncrement(), xIncrement);
        double yInc = Math.min(gp.getYIncrement(), yIncrement);
        double zInc = Math.min(gp.getZIncrement(), zIncrement);
        
        // and then we need to recompute the number of points in the
        // "added" grid
        int nx = (int) (newBB.getXWidth() / xInc);
        int ny = (int) (newBB.getYWidth() / yInc);
        int nz = (int) (newBB.getZWidth() / zInc);
        
        // then fill in the new function values
        double [] fvals = new double[nx*ny*nz];
        int ii = 0, i, j, k;
        double x, y, z, f1, f2;
        
        for(x=newUL.getX(), i=0; i<nx; x=newUL.getX()+(xInc*i), i++) {
            for(y=newUL.getY(), j=0; j<ny; y=newUL.getY()+(yInc*j), j++) {
                for(z=newUL.getZ(), k=0; k<nz; z=newUL.getZ()+(zInc*k), k++) {
                    f1 = getFunctionValueAt(x, y, z);
                    f2 = gp.getFunctionValueAt(x, y, z);
                    
                    if (Math.signum(f1) != Math.signum(f2)) {
                        if (Math.abs(f1 + f2) < threshold) {
                            fvals[ii++] = f1 + f2;
                        } // end if
                    } // end if                    
                } // end for
            } // end for
        } // end for      
        
        return new GridProperty(newBB, xInc, yInc, zInc, nx, ny, nz, fvals);
    }
    
    /**
     * Return a grid point near to this point.
     *
     * @param x the X coordinated of the point
     * @param y the Y coordinated of the point
     * @param z the Z coordinated of the point
     *
     * @return the grid point near the specified point.
     */
    public Point3D getGridPointNear(double x, double y, double z) {
        double xMin = boundingBox.getUpperLeft().getX(),
               yMin = boundingBox.getUpperLeft().getY(),
               zMin = boundingBox.getUpperLeft().getZ();       
                
        int i = (int) Math.rint(Math.abs((x-xMin)/xIncrement));
        int j = (int) Math.rint(Math.abs((y-yMin)/yIncrement));
        int k = (int) Math.rint(Math.abs((z-zMin)/zIncrement));
        
        return new Point3D(xMin + (i * xIncrement),
                           yMin + (j * yIncrement),
                           zMin + (k * zIncrement));
    }
    
    /**
     * Return a grid point near to this point.
     *
     * @param point the Point3D object near which the grid point is to be 
     *        searched
     * @return the grid point near the specified point.
     */
    public Point3D getGridPointNear(Point3D point) {
        return getGridPointNear(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Return a grid index near this point
     *
     * @param point the Point3D object near which the grid index is to be 
     *        searched
     * @return an interger array with three values indicating the index
     */    
    public int [] getGridIndexNear(Point3D point) {
        return getGridIndexNear(point.getX(), point.getY(), point.getZ());        
    }
    
    /**
     * Return a grid index near this point
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     *
     * @return an interger array with three values indicating the index
     */    
    public int [] getGridIndexNear(double x, double y, double z) {
        double xMin = boundingBox.getUpperLeft().getX(),
               yMin = boundingBox.getUpperLeft().getY(),
               zMin = boundingBox.getUpperLeft().getZ();       
        
        return new int [] {
            (int) Math.rint(Math.abs((x-xMin)/xIncrement)),
            (int) Math.rint(Math.abs((y-yMin)/yIncrement)),
            (int) Math.rint(Math.abs((z-zMin)/zIncrement))
        };
    }
    
    /**
     * Return a grid point near to the index (i, j, k).
     *
     * @param i the ith index in the grid
     * @param j the ith index in the grid
     * @param k the ith index in the grid
     *
     * @return the grid point near the specified point.
     */
    public Point3D getGridPointNear(int i, int j, int k) {
        double xMin = boundingBox.getUpperLeft().getX(),
               yMin = boundingBox.getUpperLeft().getY(),
               zMin = boundingBox.getUpperLeft().getZ();           
        
        return new Point3D(xMin + (i * xIncrement),
                           yMin + (j * yIncrement),
                           zMin + (k * zIncrement));
    }
    
    /**
     * Returns a new sub property defined by a new bounding box. The resultunt
     * sub property may not have the same bounding box as the one that is 
     * supplied but will be best approximation to the existing grid. Also it 
     * is required to have the new bounding box to completely lie inside 
     * the original bounding box. In case is this condition is violated then
     * the returned subProperty will be exactly same as the original one.
     *
     * @param newBoundingBox the new bounding box of grid property
     * @return the resultant new GridProperty object     
     */
    public GridProperty subProperty(BoundingBox newBoundingBox) {
        Point3D newUL = newBoundingBox.getUpperLeft();
        Point3D newBR = newBoundingBox.getBottomRight();
        
        // check if we violate the condition put above?
        if (boundingBox.contains(newUL) && boundingBox.contains(newBR)) {
            // find new min and max
            newUL = getGridPointNear(newUL);
            newBR = getGridPointNear(newBR);                        
            
            // find new nx, ny, nz
            int newNX = (int) Math.abs((newBR.getX()-newUL.getX())/xIncrement);
            int newNY = (int) Math.abs((newBR.getY()-newUL.getY())/yIncrement);
            int newNZ = (int) Math.abs((newBR.getZ()-newUL.getZ())/zIncrement);
            
            // and the collect the sub property values            
            ArrayList<Double> fVals = new ArrayList<Double>();
            double x, y, z;
            int i, j, k;
            
            for(x=newUL.getX(), i=0; i<newNX; 
                                        x=newUL.getX()+(xIncrement*i), i++) {
                for(y=newUL.getY(), j=0; j<newNY; 
                                        y=newUL.getY()+(yIncrement*j), j++) {
                    for(z=newUL.getZ(), k=0; k<newNZ; 
                                        z=newUL.getZ()+(zIncrement*k), k++) {
                        fVals.add(getFunctionValueAt(x, y, z));
                    } // end for
                } // end for
            } // end for                       
            
            double [] newFunctionValues = new double[fVals.size()];
            for(i=0; i<fVals.size(); i++) newFunctionValues[i] = fVals.get(i);
            
            fVals = null;
                        
            return new GridProperty(new BoundingBox(newUL, newBR), 
                                    xIncrement, yIncrement, zIncrement,
                                    newNX, newNY, newNZ,
                                    newFunctionValues);
        } else {         
            return this;
        } // end if
    }
    
    /**
     * Interpolate this GridProperty object by changing increments along
     * x, y and z equally by [x | y | z] / factor.
     *
     * @param factor a factor with which to reduce the increments along x, y, z
     * @param interpolater the Interpolater method to use
     * @return a new GridProperty which is interpolated along the increments
     */
    public GridProperty interpolate(double factor, Interpolater interpolater) {
        return interpolate(factor, factor, factor, interpolater);
    }
    
    /**
     * Interpolate this GridProperty object by changing increments along
     * x, y and z by [x / fx | y / fy | z / fz].
     *
     * @param fx a factor with which to reduce the increments along x
     * @param fy a factor with which to reduce the increments along y
     * @param fz a factor with which to reduce the increments along z
     * @param interpolater the Interpolater method to use
     * @return a new GridProperty which is interpolated along the increments
     */
    public GridProperty interpolate(double fx, double fy, double fz,
                                    Interpolater interpolater) {        
        // recompute increments
        double xInc = xIncrement / fx;
        double yInc = yIncrement / fy;
        double zInc = zIncrement / fz;

        // and then the number of points
        int nx = (int) (boundingBox.getXWidth() / xInc);
        int ny = (int) (boundingBox.getYWidth() / yInc);
        int nz = (int) (boundingBox.getZWidth() / zInc);
                
        // and re-form the function values
        double [] fvals = new double[nx*ny*nz];
        
        double x, y, z;
        int i, j, k;
        Point3D ul = boundingBox.getUpperLeft();
        
        int ii = 0;
        for(x=ul.getX(), i=0; i<nx; x=ul.getX()+(i*xInc), i++) {
            for(y=ul.getY(), j=0; j<ny; y=ul.getY()+(j*yInc), j++) {
                for(z=ul.getZ(), k=0; k<nz; z=ul.getZ()+(k*zInc), k++) {
                    fvals[ii++] = getInterpolatedFunctionValueAt(x, y, z,
                                                                 interpolater);
                } // end for
            } // end for
        } // end for
        
        return new GridProperty(boundingBox, 
                                xInc, yInc, zInc, nx, ny, nz, fvals);
    }
} // end of class GridProperty
