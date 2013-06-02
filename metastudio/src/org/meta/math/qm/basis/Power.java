/*
 * Power.java
 *
 * Created on July 23, 2004, 6:51 AM
 */

package org.meta.math.qm.basis;

/**
 * Represents the powers on orbitals. <br>
 * They are also the magnetic quantum numbers.
 *
 * @author  V.Ganesh
 */
public class Power implements Cloneable {
    
    /**
     * Holds value of property l.
     */
    private int l;
    
    /**
     * Holds value of property m.
     */
    private int m;
    
    /**
     * Holds value of property n.
     */
    private int n;
    
    /** Creates a new instance of Power */
    public Power(int l, int m, int n) {
        this.l = l;
        this.m = m;
        this.n = n;
    }
    
    /**
     * Getter for property l.
     * @return Value of property l.
     */
    public int getL() {
        return this.l;
    }
    
    /**
     * Setter for property l.
     * @param l New value of property l.
     */
    public void setL(int l) {
        this.l = l;
    }
    
    /**
     * Getter for property m.
     * @return Value of property m.
     */
    public int getM() {
        return this.m;
    }
    
    /**
     * Setter for property m.
     * @param m New value of property m.
     */
    public void setM(int m) {
        this.m = m;
    }
    
    /**
     * Getter for property n.
     * @return Value of property n.
     */
    public int getN() {
        return this.n;
    }
    
    /**
     * Setter for property n.
     * @param n New value of property n.
     */
    public void setN(int n) {
        this.n = n;
    }

    /**
     * Return the maximum angular momentum of this Power
     * 
     * @return the maximum of the powers
     */
    public int getMaximumAngularMomentum() {
        return Math.max(Math.max(l, m), n);
    }

    /**
     * Return the minimum angular momentum of this Power
     *
     * @return the minimum of the powers
     */
    public int getMinimumAngularMomentum() {
        return Math.min(Math.min(l, m), n);
    }

    /**
     * Return the total angular momentum of this Power
     *
     * @return the maximum of the powers
     */
    public int getTotalAngularMomentum() {
        return l+m+n;
    }

    /**
     * Clone this !
     *
     * @return the clone
     */
    @Override
    public Power clone() {
        return new Power(l, m, n);
    }

    /**
     * overloaded toString()
     */
    @Override
    public String toString() {
        return "[" + l + ", " + m + ", " + n  + "]";
    }
} // end of class Power
