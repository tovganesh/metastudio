/*
 * FragmentAtomGoodness.java
 *
 * Created on September 25, 2004, 7:19 AM
 */

package org.meta.fragmentor;

import org.meta.fragment.Fragment;

/**
 * Placeholder of goodness value for an atom or an atom pair and the fragment 
 * in which this value is calculated.
 *
 * PS. The atom or the atom pair is not represented here so as to allow wide
 * ways of storing the same along the objects of FragmentAtomGoodness.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentAtomGoodness {
    
    /**
     * Holds value of property value.
     */
    private double value;
    
    /**
     * Holds value of property fragment.
     */
    private Fragment fragment;

    /**
     * Holds value of property best.
     */
    private boolean best;
    
    /** Creates a new instance of FragmentGoodness */
    public FragmentAtomGoodness(Fragment fragment, double value) {
        this.fragment = fragment;
        this.value    = value;
        this.best   = false;
    }
    
    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public double getValue() {
        return this.value;
    }
    
    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(double value) {
        this.value = value;
    }
    
    /**
     * Getter for property fragment.
     * @return Value of property fragment.
     */
    public Fragment getFragment() {
        return this.fragment;
    }
    
    /**
     * Setter for property fragment.
     * @param fragment New value of property fragment.
     */
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Getter for property best.
     * @return Value of property best.
     */
    public boolean isBest()  {
        return this.best;
    }

    /**
     * Setter for property best.
     * @param best New value of property best.
     */
    public void setBest(boolean best)  {
        this.best = best;
    }
    
    /**
     * To string!
     *
     * @return the string representation
     */
    public String toString() {
        return value + "";
    }
} // end of class FragmentAtomGoodness
