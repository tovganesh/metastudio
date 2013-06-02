/**
 * ConvergenceCriteria.java
 *
 * Created on Mar 24, 2009
 */
package org.meta.math.optimizer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Interface defining convergence for a OptimizerFunction.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class ConvergenceCriteria {

    private static final double DEFAULT_TOLERANCE = 1.0e-5;

    /** Creates instance of ConvergenceCriteria */
    public ConvergenceCriteria() {
        criteriaCombinationType = CriteriaCombinationType.AND;
        composite = false;
        tolerance = DEFAULT_TOLERANCE;

        name = "ConvergenceCriteria";
    }

    protected double oldValue;

    /**
     * Get the value of oldValue
     *
     * @return the value of oldValue
     */
    public double getOldValue() {
        return oldValue;
    }

    /**
     * Set the value of oldValue
     *
     * @param oldValue new value of oldValue
     */
    public void setOldValue(double oldValue) {
        this.oldValue = oldValue;
    }

    protected double newValue;

    /**
     * Get the value of newValue
     *
     * @return the value of newValue
     */
    public double getNewValue() {
        return newValue;
    }

    /**
     * Set the value of newValue
     *
     * @param newValue new value of newValue
     */
    public void setNewValue(double newValue) {
        this.newValue = newValue;
    }

    protected boolean composite;

    /**
     * Get the value of composite
     *
     * @return the value of composite
     */
    public boolean isComposite() {
        return composite;
    }

    protected ArrayList<ConvergenceCriteria> subCriteria;

    /**
     * Add a sub-criteria
     *
     * @param cc instance of ConvergenceCriteria working in conjucation with
     *           the parent criteria
     */
    public void addSubCriteria(ConvergenceCriteria cc) {
        if (subCriteria == null)
            subCriteria = new ArrayList<ConvergenceCriteria>();

        subCriteria.add(cc);
        composite = true;
    }

    /**
     * Remove a sub-criteria
     *
     * @param cc instance of ConvergenceCriteria to be detached from parent 
     */
    public void removeSubCriteria(ConvergenceCriteria cc) {
        if (subCriteria == null) return;

        subCriteria.remove(cc);

        if (subCriteria.size() == 0) composite = false;
    }

    /**
     * Return an iterator to the sub criteria attached to this criteria
     * 
     * @return an iterator of ConvergenceCriteria, null if the list is null.
     */
    public Iterator<ConvergenceCriteria> getAttachedSubCriteriaList() {
        if (subCriteria == null) return null;

        return subCriteria.iterator();
    }

    /** Ways to combine two criteria */
    public enum CriteriaCombinationType { AND, OR };

    protected CriteriaCombinationType criteriaCombinationType;

    /**
     * Get the value of criteriaCombinationType
     *
     * @return the value of criteriaCombinationType
     */
    public CriteriaCombinationType getCriteriaCombinationType() {
        return criteriaCombinationType;
    }

    /**
     * Set the value of criteriaCombinationType
     *
     * @param criteriaCombinationType new value of criteriaCombinationType
     */
    public void setCriteriaCombinationType(
                           CriteriaCombinationType criteriaCombinationType) {
        this.criteriaCombinationType = criteriaCombinationType;
    }

    protected double tolerance;

    /**
     * Get the value of tolerance
     *
     * @return the value of tolerance
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Set the value of tolerance
     *
     * @param tolerance new value of tolerance
     */
    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    protected String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return true if the convergence criteria is satisfied.
     *
     * @return true if the convergenc ecriterion is satisfied
     */
    public abstract boolean isConverged();
}
