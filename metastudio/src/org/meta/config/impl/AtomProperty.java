/*
 * AtomProperty.java
 *
 * Created on November 30, 2003, 11:50 AM
 */

package org.meta.config.impl;

import java.awt.Color;
import org.meta.config.Parameter;

/**
 * Represents an collection "wrapper" of all the properties of a single atom.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AtomProperty implements Parameter {
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property covalentRadius. */
    private double covalentRadius;
    
    /** Holds value of property vdwRadius. */
    private double vdwRadius;
    
    /** Holds value of property atomicNumber. */
    private int atomicNumber;
    
    /** Holds value of property defaultValency. */
    private int defaultValency;
    
    /** Holds value of property weakBondAngle. */
    private double weakBondAngle;
    
    /** Holds value of property color. */
    private Color color;
    
    /** Holds value of property atomicWeight. */
    private double atomicWeight;
    
    /** Holds value of property doubleBondOverlap. */
    private double doubleBondOverlap;
    
    /** Creates a new instance of AtomProperty */
    public AtomProperty(String name, int atomicNumber, double atomicWeight,
           int defaultValency, double covalentRadius, double vdwRadius,
           double weakBondAngle, double doubleBondOverlap, Color color) {
           
           this.name              = name;
           this.atomicNumber      = atomicNumber;
           this.atomicWeight      = atomicWeight;
           this.defaultValency    = defaultValency;
           this.covalentRadius    = covalentRadius;
           this.vdwRadius         = vdwRadius;
           this.weakBondAngle     = weakBondAngle;
           this.doubleBondOverlap = doubleBondOverlap;
           this.color             = color;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName() {
        return this.name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     *
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Getter for property covalentRadius.
     * @return Value of property covalentRadius.
     *
     */
    public double getCovalentRadius() {
        return this.covalentRadius;
    }
    
    /** Setter for property covalentRadius.
     * @param covalentRadius New value of property covalentRadius.
     *
     */
    public void setCovalentRadius(double covalentRadius) {
        this.covalentRadius = covalentRadius;
    }
    
    /** Getter for property vdwRadius.
     * @return Value of property vdwRadius.
     *
     */
    public double getVdwRadius() {
        return this.vdwRadius;
    }
    
    /** Setter for property vdwRadius.
     * @param vdwRadius New value of property vdwRadius.
     *
     */
    public void setVdwRadius(double vdwRadius) {
        this.vdwRadius = vdwRadius;
    }
    
    /** Getter for property atomicNumber.
     * @return Value of property atomicNumber.
     *
     */
    public int getAtomicNumber() {
        return this.atomicNumber;
    }
    
    /** Setter for property atomicNumber.
     * @param atomicNumber New value of property atomicNumber.
     *
     */
    public void setAtomicNumber(int atomicNumber) {
        this.atomicNumber = atomicNumber;
    }
    
    /** Getter for property defaultValency.
     * @return Value of property defaultValency.
     *
     */
    public int getDefaultValency() {
        return this.defaultValency;
    }
    
    /** Setter for property defaultValency.
     * @param defaultValency New value of property defaultValency.
     *
     */
    public void setDefaultValency(int defaultValency) {
        this.defaultValency = defaultValency;
    }
    
    /** Getter for property weakBondAngle.
     * @return Value of property weakBondAngle.
     *
     */
    public double getWeakBondAngle() {
        return this.weakBondAngle;
    }
    
    /** Setter for property weakBondAngle.
     * @param weakBondAngle New value of property weakBondAngle.
     *
     */
    public void setWeakBondAngle(double weakBondAngle) {
        this.weakBondAngle = weakBondAngle;
    }
    
    /** Getter for property color.
     * @return Value of property color.
     *
     */
    public Color getColor() {
        return this.color;
    }
    
    /** Setter for property color.
     * @param color New value of property color.
     *
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /** Getter for property atomicWeight.
     * @return Value of property atomicWeight.
     *
     */
    public double getAtomicWeight() {
        return this.atomicWeight;
    }
    
    /** Setter for property atomicWeight.
     * @param atomicWeight New value of property atomicWeight.
     *
     */
    public void setAtomicWeight(double atomicWeight) {
        this.atomicWeight = atomicWeight;
    }
    
    public Object getValue() {
        return this;
    }
    
    public void setValue(Object value) {
        AtomProperty ap = (AtomProperty) value;
        
        this.name              = ap.name;
        this.atomicNumber      = ap.atomicNumber;
        this.atomicWeight      = ap.atomicWeight;
        this.defaultValency    = ap.defaultValency;
        this.covalentRadius    = ap.covalentRadius;
        this.vdwRadius         = ap.vdwRadius;
        this.weakBondAngle     = ap.weakBondAngle;
        this.doubleBondOverlap = ap.doubleBondOverlap;
        this.color             = ap.color;
    }
    
    /** Getter for property doubleBondOverlap.
     * @return Value of property doubleBondOverlap.
     *
     */
    public double getDoubleBondOverlap() {
        return this.doubleBondOverlap;
    }
    
    /** Setter for property doubleBondOverlap.
     * @param doubleBondOverlap New value of property doubleBondOverlap.
     *
     */
    public void setDoubleBondOverlap(double doubleBondOverlap) {
        this.doubleBondOverlap = doubleBondOverlap;
    }
    
} // end of class AtomProperty
