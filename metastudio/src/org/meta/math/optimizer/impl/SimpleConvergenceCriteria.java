/**
 * SimpleConvergenceCriteria.java
 *
 * Created on Mar 24, 2009
 */
package org.meta.math.optimizer.impl;

import java.util.Iterator;
import org.meta.math.optimizer.ConvergenceCriteria;

/**
 * Simple implementation of ConvergenceCriteria
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SimpleConvergenceCriteria extends ConvergenceCriteria {

    /** Creates instance of SimpleConvergenceCriteria */
    public SimpleConvergenceCriteria() {
        name = "SimpleConvergenceCriteria";
    }

    /**
     * Return true if the convergence criteria is satisfied.
     *
     * @return true if the convergenc ecriterion is satisfied
     */
    @Override
    public boolean isConverged() {
        boolean isConverged = (Math.abs(newValue-oldValue) <= tolerance);

        if (isComposite()) {
            Iterator<ConvergenceCriteria> ccList = getAttachedSubCriteriaList();

            while(ccList.hasNext()) {
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
}
