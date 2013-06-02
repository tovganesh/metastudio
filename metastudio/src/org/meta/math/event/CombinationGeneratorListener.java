/*
 * CombinationGeneratorListener.java
 *
 * Created on April 3, 2007, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.event;

/**
 * Combination generator listener interface.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface CombinationGeneratorListener extends java.util.EventListener {
    
    /**
     * A new combination event.
     *
     * @param cge CombinationGeneratorEvent object describing the event
     * @return ture in case this is a useful combination else false
     */
    public boolean newCombination(CombinationGeneratorEvent cge);
} // end of interface CombinationGeneratorListener

