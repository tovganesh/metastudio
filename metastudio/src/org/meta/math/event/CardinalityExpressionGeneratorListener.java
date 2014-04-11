/*
 * CardinalityExpressionGeneratorListener.java
 *
 * Created on August 25, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.event;

/**
 * Cardinality expression generator listener interface.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface CardinalityExpressionGeneratorListener 
                 extends java.util.EventListener {
    
    /**
     * A new cardinality expression term event.
     *
     * @param ce CardinalityExpressionGeneratorEvent object describing the event
     * @return ture in case this is a useful combination else false
     */
    public boolean newExpressionTerm(CardinalityExpressionGeneratorEvent ce);
} // end of interface CardinalityExpressionGeneratorListener
