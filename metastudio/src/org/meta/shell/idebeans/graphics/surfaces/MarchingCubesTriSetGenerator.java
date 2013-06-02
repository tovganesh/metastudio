/*
 * MarchingCubesTriSetGenerator.java
 *
 * Created on November 28, 2005, 6:23 PM
 *
 */

package org.meta.shell.idebeans.graphics.surfaces;

import java.util.*;

import org.meta.math.geom.Point3DI;
import org.meta.math.geom.Triangle;
import org.meta.math.Matrix3D;
import org.meta.math.Vector3D;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.parallel.ForTask;
import org.meta.parallel.ReduceOperation;
import org.meta.parallel.SimpleParallelFor;

/**
 * The "marching cubes" tri-set generator for surface generation on a grid.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MarchingCubesTriSetGenerator extends TriSetGenerator  {
    
    private ArrayList<Point3DI> points;
    private ArrayList<Triangle> triSets;
    private Iterator<Triangle> triSetsIterator;
    
    private GridProperty gridProperty;
    private double currentFunctionValue;
    
    private int[] edgeTable;
    private int[][] triTable;
    
    private int noOfPointsAlongX, noOfPointsAlongY, noOfPointsAlongZ;
    private double lengthX, lengthY, lengthZ;
    private double startX, startY, startZ;
    private double[] functionValues;
    
    private int noOfPointsInZDirection, noOfPointsInSlice;
    
    private SimpleParallelFor<TrianglePointTuple> marchingCubesTask;
    
    /** Creates a new instance of MarchingCubesTriSetGenerator */
    public MarchingCubesTriSetGenerator(GridProperty gridProperty,
                                        double currentFunctionValue) {
        this.gridProperty = gridProperty;
        this.currentFunctionValue = currentFunctionValue;
        
        this.transform = new Matrix3D();
        
        noOfPointsAlongX = gridProperty.getNoOfPointsAlongX();
        noOfPointsAlongY = gridProperty.getNoOfPointsAlongY();
        noOfPointsAlongZ = gridProperty.getNoOfPointsAlongZ();
        lengthX = gridProperty.getXIncrement();
        lengthY = gridProperty.getYIncrement();
        lengthZ = gridProperty.getZIncrement();
        startX  = gridProperty.getBoundingBox().getUpperLeft().getX();
        startY  = gridProperty.getBoundingBox().getUpperLeft().getY();
        startZ  = gridProperty.getBoundingBox().getUpperLeft().getZ();
        functionValues = gridProperty.getFunctionValues();
        
        noOfPointsInZDirection = noOfPointsAlongZ;
        noOfPointsInSlice = noOfPointsAlongY * noOfPointsAlongZ;
        
       
        marchingCubesTask = new SimpleParallelFor<TrianglePointTuple>(0, noOfPointsAlongX-1,
                                 new MarchingCubesForTask(0, noOfPointsAlongX-1));
        
        initPoints();
    }
    
    /**
     * initialize the tri-set points
     */
    private void initPoints() {
        this.points  = new ArrayList<Point3DI>();
        this.triSets = new ArrayList<Triangle>();
        
        try {
            edgeTable = EdgeTable.getInstance().getTableAsArray();
            triTable = TriTable.getInstance().getTableAsArray();
        } catch (Exception ignored) {
            System.out.println("Unexpected Error: " + ignored);
            ignored.printStackTrace();
            
            triSetsIterator = triSets.iterator();
            
            return; // return abruptly
        } // end of try .. catch block               
 
        TrianglePointTuple triTuple = marchingCubesTask.start();
        triSets = triTuple.triSet;
        points  = triTuple.points;                                     
        
        // init the iterators        
        triSets.trimToSize();
        
        triSetsIterator = triSets.iterator();
    }
    
    public ArrayList<Vector3D> getVertexNormals() {
        ArrayList<Vector3D> vertexNormals 
               = new ArrayList<Vector3D>(gridProperty.getNumberOfGridPoints());
        Point3DI point1, point2, point3;
        Vector3D n1, n2, n3;
        
        noOfPointsAlongX = gridProperty.getNoOfPointsAlongX();
        noOfPointsAlongY = gridProperty.getNoOfPointsAlongY();
        noOfPointsAlongZ = gridProperty.getNoOfPointsAlongZ();
        lengthX = gridProperty.getXIncrement();
        lengthY = gridProperty.getYIncrement();
        lengthZ = gridProperty.getZIncrement();
        startX  = gridProperty.getBoundingBox().getUpperLeft().getX();
        startY  = gridProperty.getBoundingBox().getUpperLeft().getY();
        startZ  = gridProperty.getBoundingBox().getUpperLeft().getZ();
        functionValues = gridProperty.getFunctionValues();
        
        noOfPointsInZDirection = noOfPointsAlongZ;
        noOfPointsInSlice = noOfPointsAlongY * noOfPointsAlongZ;
        
        for(int i=0; i<gridProperty.getNumberOfGridPoints(); i++) 
            vertexNormals.add(new Vector3D());
        
        for(Triangle ts : triSets) {
            point1 = ts.getPoint1();
            point2 = ts.getPoint2();
            point3 = ts.getPoint3();
            
            n1 = computeGradient(point1);
            n2 = computeGradient(point2);
            n3 = computeGradient(point3);
            
            // average out the normals        
            vertexNormals.set(point1.getIndex(),
                              (vertexNormals.get(point1.getIndex())).add(n1));
                        
            vertexNormals.set(point2.getIndex(),
                              (vertexNormals.get(point2.getIndex())).add(n2));
                        
            vertexNormals.set(point3.getIndex(),
                              (vertexNormals.get(point3.getIndex())).add(n3));
            
        } // end for
        
        // and normalize all the vertex normals
        for(int i=0; i<vertexNormals.size(); i++) {            
            vertexNormals.set(i, vertexNormals.get(i).normalize());
        } // end for
        
        return vertexNormals;
    }
    
    /**
     * Compute gradients at this vertex point using forward difference
     *
     * @return Vector3D representing the normalized Gradient
     */
    private Vector3D computeGradient(Point3DI point) {
        int i, j, k;        
        double f, f1;
        Vector3D gradient = new Vector3D();
        
        i = (int) Math.round(Math.abs((point.getX()-startX)/lengthX));
        j = (int) Math.round(Math.abs((point.getY()-startY)/lengthY));
        k = (int) Math.round(Math.abs((point.getZ()-startZ)/lengthZ));
        
        if (i == 0) {
           f = functionValues[(i*noOfPointsInSlice) + (j*noOfPointsInZDirection)
                              + k];
           f1 = functionValues[((i+1)*noOfPointsInSlice) 
                               + (j*noOfPointsInZDirection) + k];
           gradient.setI((f1-f) / lengthX);
        } else if (i == gridProperty.getNoOfPointsAlongX()-1) {
           f = functionValues[(i*noOfPointsInSlice) + (j*noOfPointsInZDirection) 
                              + k];
           f1 = functionValues[((i-1)*noOfPointsInSlice) 
                               + (j*noOfPointsInZDirection) + k];
           gradient.setI((f-f1) / lengthX);
        } else {
           f = functionValues[((i-1)*noOfPointsInSlice) 
                              + (j*noOfPointsInZDirection) + k];
           f1 = functionValues[((i+1)*noOfPointsInSlice) 
                               + (j*noOfPointsInZDirection) + k];
           gradient.setI((f1-f) / 2.0*lengthX);
        } // end if
                
        if (j == 0) {
           f = functionValues[(i*noOfPointsInSlice) 
                              + (j*noOfPointsInZDirection) + k];
           f1 = functionValues[(i*noOfPointsInSlice) 
                               + ((j+1)*noOfPointsInZDirection) + k];
           gradient.setJ((f1-f) / lengthY);
        } else if (j == gridProperty.getNoOfPointsAlongY()-1) {
           f = functionValues[(i*noOfPointsInSlice) 
                              + (j*noOfPointsInZDirection) + k];
           f1 = functionValues[(i*noOfPointsInSlice) 
                               + ((j-1)*noOfPointsInZDirection) + k];
           gradient.setJ((f-f1) / lengthY);
        } else {
           f = functionValues[(i*noOfPointsInSlice) 
                              + ((j-1)*noOfPointsInZDirection) + k];
           f1 = functionValues[(i*noOfPointsInSlice) 
                               + ((j+1)*noOfPointsInZDirection) + k];
           gradient.setJ((f1-f) / 2.0*lengthY);
        } // end if
        
        if (k == 0) {
           f = functionValues[(i*noOfPointsInSlice) 
                              + (j*noOfPointsInZDirection) + k];
           f1 = functionValues[(i*noOfPointsInSlice) 
                               + (j*noOfPointsInZDirection) + (k+1)];
           gradient.setK((f1-f) / lengthZ);
        } else if (k == gridProperty.getNoOfPointsAlongZ()-1) {
           f = functionValues[(i*noOfPointsInSlice) 
                              + (j*noOfPointsInZDirection) + k];
           f1 = functionValues[(i*noOfPointsInSlice) 
                               + (j*noOfPointsInZDirection) + (k-1)];
           gradient.setK((f-f1) / lengthZ);
        } else {
           f = functionValues[(i*noOfPointsInSlice) 
                              + (j*noOfPointsInZDirection) + (k-1)];
           f1 = functionValues[(i*noOfPointsInSlice) 
                               + (j*noOfPointsInZDirection) + (k+1)];
           gradient.setK((f1-f) / 2.0*lengthZ);
        } // end if
        
        return gradient;
    }       
    
    /**
     * Form the correct set of points by simple interpolation.
     *
     * @param x the X index
     * @param y the Y index
     * @param z the Z index
     * @param edgeNo number of edges
     */
    private Point3DI calculateIntersection(int x, int y, int z, int edgeNo) {
        Point3DI thePoint;
        
        double x1, y1, z1, x2, y2, z2;
        int v1x = x, v1y = y, v1z = z;
        int v2x = x, v2y = y, v2z = z;
        
        switch (edgeNo) {
            case 0:   // (0, 1)
                v1x++;
                v1y++;
                v2x++;
                v2y++;
                v2z++;
                break;
            case 1:  // (1, 2)
                v1x++;
                v1y++;
                v1z++;
                v2y++;
                v2z++;
                break;
            case 2:  // (2, 3)
                v1y++;
                v1z++;
                v2y++;
                break;
            case 3:  // (3, 0)
                v1y++;
                v2x++;
                v2y++;
                break;
            case 4:  // (4, 5)
                v1x++;
                v2x++;
                v2z++;
                break;
            case 5:  // (5, 6)
                v1x++;
                v1z++;
                v2z++;
                break;
            case 6:  // (6, 7)
                v1z++;                
                break;
            case 7:  // (7, 4)
                v2x++;
                break;
            case 8:  // (0, 4)
                v1x++;
                v1y++;
                v2x++;
                break;
            case 9:  // (1, 5)
                v1x++;
                v1y++;
                v1z++;
                v2x++;
                v2z++;
                break;
            case 10:  // (2, 6)
                v1y++;
                v1z++;
                v2z++;
                break;
            case 11:  // (3, 7)
                v1y++;
                break;
        } // end switch .. catch block
        
        x1 = startX + (v1x*lengthX);
        y1 = startY + (v1y*lengthY);
        z1 = startZ + (v1z*lengthZ);
        x2 = startX + (v2x*lengthX);
        y2 = startY + (v2y*lengthY);
        z2 = startZ + (v2z*lengthZ);
        
        double val1 = functionValues[v1x*noOfPointsInSlice
                + v1y*noOfPointsInZDirection + v1z];
        double val2 = functionValues[v2x*noOfPointsInSlice
                + v2y*noOfPointsInZDirection + v2z];
        
        double alpha = (val2 - currentFunctionValue) / (val2 - val1);
            
        thePoint = new Point3DI(x1*alpha + x2*(1.0-alpha),
                                y1*alpha + y2*(1.0-alpha),
                                z1*alpha + z2*(1.0-alpha));
        
        int i = (int) Math.round(Math.abs((thePoint.getX()-startX)/lengthX));
        int j = (int) Math.round(Math.abs((thePoint.getY()-startY)/lengthY));
        int k = (int) Math.round(Math.abs((thePoint.getZ()-startZ)/lengthZ));
        
        thePoint.setIndex(i*noOfPointsInSlice + j*noOfPointsInZDirection + k);
            
        return thePoint;
    }
    
    /**
     * Returns true or false indicating availability of any further tri sets
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
        transform.transformPoints(points.iterator());
        triSetsIterator = triSets.iterator();
    }
    
    /**
     * Getter for property currentFunctionValue.
     * @return Value of property currentFunctionValue.
     */
    public double getCurrentFunctionValue() {
        return this.currentFunctionValue;
    }
    
    /**
     * Setter for property currentFunctionValue.
     * @param currentFunctionValue New value of property currentFunctionValue.
     */
    public void setCurrentFunctionValue(double currentFunctionValue) {
        this.currentFunctionValue = currentFunctionValue;
        
        initPoints();
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
     * @return the size of the generated tri-sets.
     */
    @Override
    public int size() {
        return triSets.size();
    }    
    
    /** private class to represent a tuple for Triangle and Point array */
    private class TrianglePointTuple {
        ArrayList<Triangle> triSet;
        ArrayList<Point3DI> points;
    }
    
    /** private class for generating Marching cubes tri-sets in parallel */
    private class MarchingCubesForTask extends ForTask< TrianglePointTuple > {
        private TrianglePointTuple triTuple;
        
        public MarchingCubesForTask(int startIndex, int endIndex) {
            super(startIndex, endIndex);                       
        }

        @Override
        public ForTask<TrianglePointTuple> getNewInstance(int startIndex, int endIndex) {
            return new MarchingCubesForTask(startIndex, endIndex);
        }

        @Override
        public void run() {
            triTuple = new TrianglePointTuple();
            triTuple.triSet = new ArrayList<Triangle>();
            triTuple.points  = new ArrayList<Point3DI>();
            
            int i, x, y, z, tableIndex;
            Point3DI[] edgePoints = new Point3DI[triTable[0].length];
           
            // generate the isosurface
            for (x = startIndex; x < endIndex; x++) {
                for (y = 0; y < noOfPointsAlongY-1; y++) {
                    for (z = 0; z < noOfPointsAlongZ-1; z++) {
                        tableIndex = 0;

                        // compute the table lookup index from those vertices whose
                        // function value are below the isolevel, so that
                        // we zero down to a particular condition
                        if (functionValues[(x + 1) * noOfPointsInSlice
                                + (y + 1) * noOfPointsInZDirection + z] < currentFunctionValue) {
                            tableIndex |= 1;
                        }
                        if (functionValues[(x + 1) * noOfPointsInSlice
                                + (y + 1) * noOfPointsInZDirection + (z + 1)] < currentFunctionValue) {
                            tableIndex |= 2;
                        }
                        if (functionValues[x * noOfPointsInSlice
                                + (y + 1) * noOfPointsInZDirection + (z + 1)] < currentFunctionValue) {
                            tableIndex |= 4;
                        }
                        if (functionValues[x * noOfPointsInSlice
                                + (y + 1) * noOfPointsInZDirection + z] < currentFunctionValue) {
                            tableIndex |= 8;
                        }
                        if (functionValues[(x + 1) * noOfPointsInSlice
                                + y * noOfPointsInZDirection + z] < currentFunctionValue) {
                            tableIndex |= 16;
                        }
                        if (functionValues[(x + 1) * noOfPointsInSlice
                                + y * noOfPointsInZDirection + (z + 1)] < currentFunctionValue) {
                            tableIndex |= 32;
                        }
                        if (functionValues[x * noOfPointsInSlice
                                + y * noOfPointsInZDirection + (z + 1)] < currentFunctionValue) {
                            tableIndex |= 64;
                        }
                        if (functionValues[x * noOfPointsInSlice
                                + y * noOfPointsInZDirection + z] < currentFunctionValue) {
                            tableIndex |= 128;
                        }

                        // Now we form the triangles by indexing into the
                        // edgeTable
                        if (edgeTable[tableIndex] != 0) {
                            for (i = 0; i < edgePoints.length; i++) {
                                edgePoints[i] = null;
                            }

                            // we do have something to draw here
                            // find out what is that?
                            if ((edgeTable[tableIndex] & 1) != 0) {
                                triTuple.points.add(edgePoints[0] = calculateIntersection(x, y, z, 0));
                            } // end if

                            if ((edgeTable[tableIndex] & 2) != 0) {
                                triTuple.points.add(edgePoints[1] = calculateIntersection(x, y, z, 1));
                            } // end if

                            if ((edgeTable[tableIndex] & 4) != 0) {
                                triTuple.points.add(edgePoints[2] = calculateIntersection(x, y, z, 2));
                            } // end if

                            if ((edgeTable[tableIndex] & 8) != 0) {
                                triTuple.points.add(edgePoints[3] = calculateIntersection(x, y, z, 3));
                            } // end if

                            if ((edgeTable[tableIndex] & 16) != 0) {
                                triTuple.points.add(edgePoints[4] = calculateIntersection(x, y, z, 4));
                            } // end if

                            if ((edgeTable[tableIndex] & 32) != 0) {
                                triTuple.points.add(edgePoints[5] = calculateIntersection(x, y, z, 5));
                            } // end if

                            if ((edgeTable[tableIndex] & 64) != 0) {
                                triTuple.points.add(edgePoints[6] = calculateIntersection(x, y, z, 6));
                            } // end if

                            if ((edgeTable[tableIndex] & 128) != 0) {
                                triTuple.points.add(edgePoints[7] = calculateIntersection(x, y, z, 7));
                            } // end if

                            if ((edgeTable[tableIndex] & 256) != 0) {
                                triTuple.points.add(edgePoints[8] = calculateIntersection(x, y, z, 8));
                            } // end if

                            if ((edgeTable[tableIndex] & 512) != 0) {
                                triTuple.points.add(edgePoints[9] = calculateIntersection(x, y, z, 9));
                            } // end if

                            if ((edgeTable[tableIndex] & 1024) != 0) {
                                triTuple.points.add(edgePoints[10] = calculateIntersection(x, y, z, 10));
                            } // end if

                            if ((edgeTable[tableIndex] & 2048) != 0) {
                                triTuple.points.add(edgePoints[11] = calculateIntersection(x, y, z, 11));
                            } // end if

                            // add the trisets
                            for (i = 0; triTable[tableIndex][i] != -1; i += 3) {
                                triTuple.triSet.add(new Triangle(
                                        edgePoints[triTable[tableIndex][i]],
                                        edgePoints[triTable[tableIndex][i + 1]],
                                        edgePoints[triTable[tableIndex][i + 2]]));
                            } // end for
                        } // end if
                    } // end for (z)
                } // end for (y)
            } // end for (x)            
        }

        @Override
        public TrianglePointTuple getResult() {
            return triTuple;
        }

        @Override
        public TrianglePointTuple reduceResult(ArrayList<TrianglePointTuple> partResults, ReduceOperation op) {
            TrianglePointTuple newTriTuple = new TrianglePointTuple();
            
            newTriTuple.triSet = new ArrayList<Triangle>();
            newTriTuple.points = new ArrayList<Point3DI>();
            
            switch(op) {
                case USER_DEFINED:
                    
                    for(TrianglePointTuple tList : partResults) {
                        newTriTuple.triSet.addAll(tList.triSet);                        
                    } // end for
                    
                    for(TrianglePointTuple tList : partResults) {
                        newTriTuple.points.addAll(tList.points);                        
                    } // end for
                                        
                    break;
                default:
                    throw new UnsupportedOperationException("The requested openration '"
                               + op + "' is not supported");
            }
            
            return newTriTuple;
        }
    }
} // end of class MarchingCubesTriSetGenerator
