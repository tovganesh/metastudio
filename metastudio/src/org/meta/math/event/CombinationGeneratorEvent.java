/*
 * CombinationGeneratorEvent.java
 *
 * Created on April 3, 2007, 9:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math.event;

/**
 * The combination generator event object.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CombinationGeneratorEvent extends java.util.EventObject {
    
    /** Creates a new instance of CombinationGeneratorEvent */
    public CombinationGeneratorEvent(Object source) {
        super(source);
    }

    /**
     * Holds value of property noOfTerms.
     */
    private int noOfTerms;

    /**
     * Getter for property noOfTerms.
     * @return Value of property noOfTerms.
     */
    public int getNoOfTerms() {
        return this.noOfTerms;
    }

    /**
     * Setter for property noOfTerms.
     * @param noOfTerms New value of property noOfTerms.
     */
    public void setNoOfTerms(int noOfTerms) {
        this.noOfTerms = noOfTerms;
    }

    /**
     * Holds value of property combinations.
     */
    private int[] combinations;

    /**
     * Getter for property combinations.
     * @return Value of property combinations.
     */
    public int[] getCombinations() {
        return this.combinations;
    }

    /**
     * Setter for property combinations.
     * @param combinations New value of property combinations.
     */
    public void setCombinations(int[] combinations) {
        this.combinations = combinations;
    }
    
} // end of class CombinationGeneratorEvent
