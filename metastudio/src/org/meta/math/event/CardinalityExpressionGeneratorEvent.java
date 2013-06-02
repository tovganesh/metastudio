/*
 * CardinalityExpressionGeneratorEvent.java
 *
 * Created on August 25, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.event;

/**
 * Cardinality expression generator event.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CardinalityExpressionGeneratorEvent 
             extends CombinationGeneratorEvent {
    
    /** Creates a new instance of CardinalityExpressionGeneratorEvent */
    public CardinalityExpressionGeneratorEvent(Object source) {
        super(source);
    }

    /**
     * Holds value of property sign.
     */
    private int sign;

    /**
     * Getter for property sign.
     * @return Value of property sign.
     */
    public int getSign() {
        return this.sign;
    }

    /**
     * Setter for property sign.
     * @param sign New value of property sign.
     */
    public void setSign(int sign) {
        this.sign = sign;
    }
    
} // end of class CardinalityExpressionGeneratorEvent
