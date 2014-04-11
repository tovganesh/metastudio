/*
 * FragmentCorrector.java
 *
 * Created on August 29, 2004, 7:11 PM
 */

package org.meta.fragmentor;

import java.util.ArrayList;
import org.meta.fragment.Fragment;

/**
 * Defines the interfaces of how a fragment corrector should be written.
 * This abstraction is needed as the corrector can be implemented either 
 * programatically or using a rule based language.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class FragmentCorrector {
    
    protected ArrayList<FragmentCorrectorRule> ruleList;
    
    /** Creates a new instance of FragmentCorrector */
    public FragmentCorrector() {
        ruleList = new ArrayList<FragmentCorrectorRule>();
    }
    
    /**
     * Add a corrector rule.
     *
     * @param fcr the instanc of FragmentCorrectorRule
     */
    public void addRule(FragmentCorrectorRule fcr) {
        ruleList.add(fcr);
    }
    
    /**
     * Remove a corrector rule.
     *
     * @param fcr the instanc of FragmentCorrectorRule
     */
    public void removeRule(FragmentCorrectorRule fcr) {
        ruleList.remove(fcr);
    }
    
    /**
     * Validate all the rules in the corrector.      
     *
     * @param fs the instance of FragmentationScheme
     * @param frag the Fragment object that is to be checked for rule compliance
     * @throws FragmentCorrectorRuleViolationException if rule is violated
     */
    public abstract void validateRules(FragmentationScheme fs, Fragment frag)
                           throws FragmentCorrectorRuleViolationException;
   
    /**
     * Validate all the rules in the corrector. If there is violation then 
     * modify the Fragment object but still adhering correction constraints. 
     * If the constraints cannot be adhered to then 
     * FragmentCorrectorRuleViolationException is thrown.
     *
     * @param fs the instance of FragmentationScheme
     * @param frag the Fragment object that is to be checked for rule compliance
     * @param fcc constraints to be adhered while correcting for a rule 
     *        violation
     * @throws FragmentCorrectorRuleViolationException if rule is violated and
     *         the corrections to the Fragment object could not be applied
     */
    public abstract void applyCorrections(FragmentationScheme fs, Fragment frag,
                                 FragmentCorrectorConstraints fcc)
                           throws FragmentCorrectorRuleViolationException;
} // end of class FragmentCorrector
