/**
 * BFGSOptimizer.java
 *
 * Created on Mar 25, 2009
 */
package org.meta.math.optimizer.impl;

import java.util.Iterator;
import org.meta.math.Matrix;
import org.meta.math.Vector;
import org.meta.math.optimizer.AbstractOptimizer;
import org.meta.math.optimizer.ConvergenceCriteria;
import org.meta.math.optimizer.OptimizerFunction;

/**
 * Implementation of Broyden-Fletcher-Goldfarb-Shanno (BFGS) optimizer method.
 * Based on <i> Numerical Recipes, Section 10.7 </i>.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class BFGSOptimizer extends AbstractOptimizer {

    private static final float STPMX = 100.0f;
    
    /** Create a new instance of BFGSOptimizer */
    public BFGSOptimizer(OptimizerFunction function) {
        super(function);

        convergenceCriteria = new SimpleConvergenceCriteria() {
            private final double EPSILON = 1.0e-10;;

            @Override
            public boolean isConverged() {
                boolean isConverged =
                   2.0 * Math.abs(newValue - oldValue) <= tolerance *
                    (Math.abs(newValue) + Math.abs(oldValue) + EPSILON);

                if (isComposite()) {
                    Iterator<ConvergenceCriteria> ccList
                                = getAttachedSubCriteriaList();

                    while (ccList.hasNext()) {
                        ConvergenceCriteria cc = ccList.next();

                        switch (cc.getCriteriaCombinationType()) {
                            case AND:
                                isConverged = isConverged && cc.isConverged();
                                break;
                            case OR:
                                isConverged = isConverged || cc.isConverged();
                                break;
                        } // end of switch .. case
                    } // end while
                } // end if

                return isConverged;
            }
        };
    }

    /**
     * Apply the minimization procedure
     */
    @Override
    public void minimize() {
        double[] previousVariables = variables.clone();
        double minValue = optimizerFunction.evaluate(variables);
        convergenceCriteria.setOldValue(minValue);
        currentMinima = minValue;
        System.out.println("Starting value = " + minValue);

        double [] g = optimizerFunction.getDerivatives();

        Matrix hessian = optimizerFunction.getHessian();
        hessian.makeIdentity();
        double [][] hessin = hessian.getMatrix();

        int n = g.length;

        Vector dgVec   = new Vector(n);
        Vector pnewVec = new Vector(n);
        Vector xiVec   = new Vector(n);

        double [] dg   = dgVec.getVector();
        double [] pnew = pnewVec.getVector();
        double [] xi   = xiVec.getVector();

        float sum = 0.0f;
        int i;

        for(i=0; i<n; i++) {
            xi[i] = -g[i];
            sum   += previousVariables[i]*previousVariables[i];
        } // end for

        float stpmax = STPMX * Math.max((float) Math.sqrt(sum), (float) n);

        double newValue;
        
        for(int iter=0; iter < getMaxIterations(); iter++) {
            previousVariables = variables.clone();
            LineMinimizer lineMinimizer = new LineMinimizer(optimizerFunction, xi);
            lineMinimizer.setVariables(variables);
            lineMinimizer.minimize();
            newValue = lineMinimizer.getCurrentMinima();
            convergenceCriteria.setNewValue(newValue);
            System.out.println("newValue = " + newValue);

            // TODO: BFGS
            for(i=0; i<n; i++) {
                
            } // end for

            if (convergenceCriteria.isConverged()) {
                // the normal end of minimization
                minValue = optimizerFunction.evaluate(previousVariables);
                System.out.println("final value = " + minValue);
                break;
            } // end if

            
        } // end for
    }
}
