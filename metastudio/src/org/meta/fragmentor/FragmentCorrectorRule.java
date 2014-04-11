/*
 * FragmentCorrectorRule.java
 *
 * Created on August 28, 2007, 10:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.fragmentor;

import java.util.ArrayList;
import org.meta.fragment.Fragment;

/**
 * A fragment validity rule interface.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class FragmentCorrectorRule {
    
    protected ArrayList<FragmentCorrectorRule> subRuleList;
    
    /** Creates a new instance of FragmentCorrectorRule */
    public FragmentCorrectorRule() {
        subRuleList = new ArrayList<FragmentCorrectorRule>();
        
        subRuleFiresFirst = false; // by default subrules get lesser precedence
        strict = true; // by default each rule is a strict rule
    }
    
    /**
     * Add a subrule to this rule
     *
     * @param rule a correcter rule
     */     
    public void addSubRule(FragmentCorrectorRule rule) {
        subRuleList.add(rule);
    }
    
    /**
     * Remove a subrule to this rule
     *
     * @param rule a correcter rule
     */     
    public void removeSubRule(FragmentCorrectorRule rule) {
        subRuleList.remove(rule);
    }
    
    /**
     * Validate this rule. 
     * Note: A rule is always dependent on its subrule(s). It is up to the
     * implementation if it chooses to ignore this. However for proper 
     * functioning it is suggested that order of firing the subrule set be
     * decided by refering to subRuleFiresFirst (default: false) property;
     * if at all the subrule(s) is(are) fired.
     * Further, if a subrule is not strict this rule might chose to igonore
     * its violation, if any.
     *
     * @param fs the instance of FragmentationScheme
     * @param frag the Fragment object that is to be checked for rule compliance
     * @throws FragmentCorrectorRuleViolationException if rule is violated
     */
    public abstract void validateRule(FragmentationScheme fs, Fragment frag)
                           throws FragmentCorrectorRuleViolationException;

    /**
     * Validate this rule. If there is violation then modify the Fragment
     * object but still adhering correction constraints. If the constraints
     * cannot be adhered to then FragmentCorrectorRuleViolationException
     * is thrown.
     *
     * Note: A rule is always dependent on its subrule(s). It is up to the
     * implementation if it chooses to ignore this. However for proper 
     * functioning it is suggested that order of firing the subrule set be
     * decided by refering to subRuleFiresFirst (default: false) property;
     * if at all the subrule(s) is(are) fired.
     * Further, if a subrule is not strict this rule might chose to igonore
     * its violation, if any.
     *
     * @param fs the instance of FragmentationScheme
     * @param frag the Fragment object that is to be checked for rule compliance
     * @param fcc constraints to be adhered while correcting for a rule 
     *        violation
     * @throws FragmentCorrectorRuleViolationException if rule is violated and
     *         the corrections to the Fragment object could not be applied
     */
    public abstract void validateAndCorrectRule(
                                 FragmentationScheme fs, Fragment frag,
                                 FragmentCorrectorConstraints fcc)
                           throws FragmentCorrectorRuleViolationException;

    /**
     * Holds value of property subRuleFiresFirst.
     */
    protected boolean subRuleFiresFirst;

    /**
     * Getter for property subRuleFiresFirst.
     * @return Value of property subRuleFiresFirst.
     */
    public boolean isSubRuleFiresFirst() {
        return this.subRuleFiresFirst;
    }

    /**
     * Setter for property subRuleFiresFirst.
     * @param subRuleFiresFirst New value of property subRuleFiresFirst.
     */
    public void setSubRuleFiresFirst(boolean subRuleFiresFirst) {
        this.subRuleFiresFirst = subRuleFiresFirst;
    }

    /**
     * Holds value of property strict.
     */
    protected boolean strict;

    /**
     * Getter for property strict.
     * @return Value of property strict.
     */
    public boolean isStrict() {
        return this.strict;
    }

    /**
     * Setter for property strict.
     * @param strict New value of property strict.
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }
    
} // end of abstract class FragmentCorrectorRule
