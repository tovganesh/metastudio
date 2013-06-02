/*
 * CombinationGenerator.java
 *
 * Created on April 2, 2007, 10:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.math;

import org.meta.math.event.CombinationGeneratorListener;
import org.meta.math.event.CombinationGeneratorEvent;

/**
 * Lexicographical Combination generator based on callback mechanism.
 * For a description refer to the following article:
 * <a href="http://sites.google.com/site/tovganesh/comb.pdf?attredirects=0">
 * http://sites.google.com/site/tovganesh/comb.pdf?attredirects=0</a>
 *
 * The class also supports simple combinator to from a list 
 * of combinations of the form (nCr).
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class CombinationGenerator {
    
    private int noOfItems;
    
    private CombinationGeneratorEvent cge;
    
    private int [] combs;
    
    private int i, j, k, l, pos, m, n, r;
    
    private boolean useLexicographicalGenerator;
    
    /** Creates a new instance of CombinationGenerator. 
     * This will generate combinations in Lexicographical order.
     *
     * @param noOfItems number of items for generating combinations. 
     */
    public CombinationGenerator(int noOfItems) {
        this.noOfItems = noOfItems;
        
        this.combs = new int[noOfItems];
        
        this.n = combs.length;                
        
        this.cge = new CombinationGeneratorEvent(this); 
        
        this.useLexicographicalGenerator = true;
    }

    /** Creates a new instance of CombinationGenerator. 
     * This will generate combinations in a simple order.
     *
     * @param n number of items for generating combinations. 
     * @param r how many of the 'n' for generating combinations?
     */
    public CombinationGenerator(int n, int r) {
        this.noOfItems = n;
        this.r = r;
        
        this.combs = new int[n];
        
        this.n = combs.length;                
        
        this.cge = new CombinationGeneratorEvent(this); 
        
        this.useLexicographicalGenerator = false;
    }
    
    /**
     * Start the generator.
     */
    public void startGenerator() {
        if (useLexicographicalGenerator) {
            lexicographicalCombinator();
        } else {
            simpleCombinator();
        } // end if
    }
    
    /**
     * Lexicographical combinator
     */
    private void lexicographicalCombinator() {
        for(i=0; i<n; i++) {
            combs[i] = -1;
        } // end for
        
        for(i=0; i<n; i++) {
            for(j=i+1; j<n; j++) {
                combs[0] = i; combs[1] = j;
                             
                cge.setNoOfTerms(1);
                cge.setCombinations(combs);
                
                if (!combinationGeneratorListener.newCombination(cge)) 
                    continue;
                    
                if ((n <= 2) || (combs[1] == n-1)) continue;
                
                l=1; pos=1; m=pos+1;
                
                while(true) {
                    for(k=combs[pos]+1; k<n; k++) {
                        combs[m] = k;
                        
                        if (m > 0) {
                            if (combs[m] <= combs[m-1]) break;
                        } // end if

                        cge.setNoOfTerms(m);
                        cge.setCombinations(combs);
                        
                        if (!combinationGeneratorListener.newCombination(cge)) {
                            if (combs[m] == n-1) break;
                            m--;
                        } // end  if
                        
                        m++;
                    } // end for
                    
                    pos=n-l; m=pos;
                    
                    if (combs[pos] == n-1) l++;
                    else                   l=1;
                    
                    if (pos==1) break;
                    
                } // end while
            } // end for
        } // end for
    }
    
    /**
     * simple combinator
     */
    private void simpleCombinator() {         
        int i, j, l, total;
        
        for(j=1; j<=r; j++) {
            for(i=0; i<n; i++) {
                combs[i] = i;
            } // end for
            
            total = getTermsToCompute(n, j);            
            
            for(l=0; l<total; l++) {
                i = j-1;  // start with a location
                
                cge.setNoOfTerms(j-1);
                cge.setCombinations(combs);
                
                combinationGeneratorListener.newCombination(cge);
                
                while(combs[i] == n-j+i) {
                    i--;
                    
                    if (i == -1) break;
                } // end for
                
                if (i == -1) continue;
                
                combs[i] += 1;
                
                for(k=i+1; k<j; k++) combs[k] = combs[i] + k-i;
            } // end for
        } // end for
    }
    
    /**
     * return the total number of terms to compute
     */
    private int getTermsToCompute(int n, int r) {
        long nCr = n;
        
        for(int i=n-1; i>n-r; i--) {
            nCr *= i;
        } // end for                
        
        nCr /= MathUtil.factorial(r);
        
        return (int) nCr;
    }
    
    /**
     * Utility field holding the CombinationGeneratorListener.
     */
    private transient 
            CombinationGeneratorListener combinationGeneratorListener =  null;

    /**
     * Registers CombinationGeneratorListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addCombinationGeneratorListener(
            CombinationGeneratorListener listener) 
            throws java.util.TooManyListenersException {
        if (combinationGeneratorListener != null) {
            throw new java.util.TooManyListenersException ();
        }
        combinationGeneratorListener = listener;
    }

    /**
     * Removes CombinationGeneratorListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeCombinationGeneratorListener(
            CombinationGeneratorListener listener) {
        combinationGeneratorListener = null;
    }
} // end of class CombinationGenerator
