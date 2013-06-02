/*
 * CardinalityExpressionGenerator.java
 *
 * Created on April 2, 2007, 10:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math;

import java.util.TooManyListenersException;
import org.meta.math.event.CardinalityExpressionGeneratorEvent;
import org.meta.math.event.CardinalityExpressionGeneratorListener;
import org.meta.math.event.CombinationGeneratorEvent;
import org.meta.math.event.CombinationGeneratorListener;

/**
 * Generate a cardinality expression.
 * For a description refer to the following article:
 * <a href="http://sites.google.com/site/tovganesh/comb.pdf?attredirects=0">
 * http://sites.google.com/site/tovganesh/comb.pdf?attredirects=0</a>
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CardinalityExpressionGenerator 
             implements CombinationGeneratorListener {
    
    private int noOfItems;
    
    private CardinalityExpressionGeneratorEvent ce;
    
    /** Creates a new instance of CardinalityExpressionGenerator 
     *
     * @param noOfItems number of items for generating the expression
     */
    public CardinalityExpressionGenerator(int noOfItems) {
        this.noOfItems = noOfItems;
        
        this.ce = new CardinalityExpressionGeneratorEvent(this);
    }
    
    /**
     * Start the generator.
     */
    public void startGenerator() {
       CombinationGenerator cg = new CombinationGenerator(noOfItems);
       
       try {
            cg.addCombinationGeneratorListener(this);
            cg.startGenerator();
            cg.removeCombinationGeneratorListener(this);
       } catch (TooManyListenersException te) {
            System.err.println("Unexpected error in " +
                    "CardinalityExpressionGenerator.startGenerator() : "
                    + te.toString());
            te.printStackTrace();
       } // end try .. catch block
    }

    /**
     * Return the sign of the term in a cardinality expression.
     *
     * @param m the number of terms (-1) in the expression
     * @return the sign of the this term
     */
    protected int getSignOfTerm(int m) {
        m++;
        
        if (m%2 == 0) return -1;
        else          return  1;
    }
    
    /**
     * A new combination event.
     *
     * @param cge CombinationGeneratorEvent object describing the envent
     * @return ture in case this is a useful combination else false
     */
    public boolean newCombination(CombinationGeneratorEvent cge) {        
        ce.setSign(getSignOfTerm(cge.getNoOfTerms()));
        ce.setNoOfTerms(cge.getNoOfTerms());
        ce.setCombinations(cge.getCombinations());
        
        return (cardinalityExpressionGeneratorListener.newExpressionTerm(ce));
    }

    /**
     * Utility field holding the CardinalityExpressionGeneratorListener.
     */
    private transient CardinalityExpressionGeneratorListener 
                                 cardinalityExpressionGeneratorListener =  null;

    /**
     * Registers CardinalityExpressionGeneratorListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addCardinalityExpressionGeneratorListener(
             CardinalityExpressionGeneratorListener listener) 
                        throws java.util.TooManyListenersException {
        if (cardinalityExpressionGeneratorListener != null) {
            throw new java.util.TooManyListenersException ();
        }
        cardinalityExpressionGeneratorListener = listener;
    }

    /**
     * Removes CardinalityExpressionGeneratorListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeCardinalityExpressionGeneratorListener(
                              CardinalityExpressionGeneratorListener listener) {
        cardinalityExpressionGeneratorListener = null;
    }   
} // end of class CardinalityExpressionGenerator
