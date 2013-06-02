/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.shell.idebeans.graphics.surfaces;

import java.util.ArrayList;
import org.meta.config.impl.AtomInfo;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.molecule.event.MoleculeStateChangeEvent;
import org.meta.molecule.event.MoleculeStateChangeListener;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.parallel.ForTask;
import org.meta.parallel.ReduceOperation;
import org.meta.parallel.SimpleParallelFor;

/**
 * Generate a vdW surface 
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class VdwGridPropertyGenerator implements GridPropertyGenerator,
                                                 MoleculeStateChangeListener {
    
    private Molecule molecule;
    private GridProperty gridProperty;
    
    private final static double VDW_GRID_EXTENT = 2.5;
    private final static int VDW_GRID_POINTS    = 100;
    
    private int noOfPointsAlongX, noOfPointsAlongY, noOfPointsAlongZ,
                noOfPointsInXY;
    private double xMin, yMin, zMin,
                   xInc, yInc, zInc;
    
    /** Create new instance of VdwGridPropertyGenerator */
    public VdwGridPropertyGenerator(Molecule mol) {
        this.molecule = mol;
        
        // generate the grid
        generateGrid();
        
        // in the event the molecule changes, re-generate the surface
        this.molecule.addMoleculeStateChangeListener(this);                
    }
    
    /** generate the grid for vdW surface */
    private void generateGrid() {
        BoundingBox bb = molecule.getBoundingBox();        
        bb = bb.expand(VDW_GRID_EXTENT);        
        
        gridProperty = new GridProperty(bb, VDW_GRID_POINTS,
                                            VDW_GRID_POINTS,
                                            VDW_GRID_POINTS);
        
        noOfPointsAlongX = gridProperty.getNoOfPointsAlongX();
        noOfPointsAlongY = gridProperty.getNoOfPointsAlongY();
        noOfPointsAlongZ = gridProperty.getNoOfPointsAlongZ(); 
        noOfPointsInXY   = noOfPointsAlongY*noOfPointsAlongZ;
        
        xInc = gridProperty.getXIncrement();
        yInc = gridProperty.getYIncrement();
        zInc = gridProperty.getZIncrement();
        
        xMin = bb.getUpperLeft().getX();
        yMin = bb.getUpperLeft().getY();
        zMin = bb.getUpperLeft().getZ();
        
        SimpleParallelFor<double []> vdwGridGenTask = 
                new SimpleParallelFor<double []>(0, noOfPointsAlongX-1, 
                  new VdwGridGenerator(gridProperty.getNumberOfGridPoints(), 
                                       0, noOfPointsAlongX-1));
        
        gridProperty.setFunctionValues(vdwGridGenTask.start());        
    }
    
    /**
     * Get the generated grid property function 
     * 
     * @return a GridProperty object that can be used to render a surface
     */ 
    @Override
    public GridProperty getGridProperty() {
       return gridProperty;
    }

    @Override
    public void moleculeChanged(MoleculeStateChangeEvent event) {
        // re-generate grid if the molecule object changed
        generateGrid();
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        
        this.molecule.removeMoleculeStateChangeListener(this);
    }        
    
    /** private class for generating vdw grid points */
    private class VdwGridGenerator extends ForTask< double [] > {
        private double [] fVals;
        private int noOfValues;
        
        public VdwGridGenerator(int noOfValues, int startIndex, int endIndex) {
            super(startIndex, endIndex);
            
            this.noOfValues = noOfValues;            
        }
        
        public VdwGridGenerator(int startIndex, int endIndex) {
            super(startIndex, endIndex);                       
        }
        
        @Override
        public ForTask<double[]> getNewInstance(int startIndex, int endIndex) {
            VdwGridGenerator vgg = new VdwGridGenerator(startIndex, endIndex);
            vgg.noOfValues = this.noOfValues;
            vgg.fVals = new double[noOfValues];
            
            return vgg;
        }

        @Override
        public void run() {
            int x, y, z, i, ii, xx, yy;
            double xpt, ypt, zpt;
            AtomInfo ai = AtomInfo.getInstance();
                        
            for(i=0; i<fVals.length; i++)
                fVals[i] = Double.MAX_VALUE;
            
            for (i = 0; i < molecule.getNumberOfAtoms(); i++) {
                Atom atom = molecule.getAtom(i);
                double vdwRadius = ai.getVdwRadius(atom.getSymbol());
                Point3D atomCenter = atom.getAtomCenter();
                
                for (x = startIndex; x < endIndex; x++) {
                    xx = x * noOfPointsInXY;
                    xpt = xMin + x * xInc;
                    for (y = 0; y < noOfPointsAlongY - 1; y++) {
                        yy = y * noOfPointsAlongZ;
                        ypt = yMin + y * yInc;
                        for (z = 0; z < noOfPointsAlongZ - 1; z++) {
                            zpt = zMin + z * zInc;
                            Point3D point = new Point3D(xpt, ypt, zpt);
                            ii = xx + yy + z;
            
                            double dist = point.distanceFrom(atomCenter);

                            fVals[ii] = Math.min(fVals[ii], dist - vdwRadius);                           
                        } // end for (z)
                    } // end for (y)
                } // end for (x)
            } // end for (i)
        }

        @Override
        public double[] getResult() {
            return fVals;
        }

        @Override
        public double[] reduceResult(ArrayList<double[]> partResults, ReduceOperation op) {
            double [] newFVals = new double[noOfValues];
            int i;
            
            for(i=0; i<noOfValues; i++)
                newFVals[i] = Double.MAX_VALUE;
            
            switch (op) {
                case USER_DEFINED:
                    for (double[] fv : partResults) {
                        for (i=0; i < noOfValues; i++) {
                            newFVals[i] = Math.min(fv[i], newFVals[i]);
                        }
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("The requested openration '"
                            + op + "' is not supported");
            } // end of switch .. case

            return newFVals; 
        }
        
    } 
}
