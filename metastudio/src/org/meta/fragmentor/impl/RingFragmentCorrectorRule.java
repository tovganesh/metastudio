/*
 * RingFragmentCorrectorRule.java
 *
 * Created on August 29, 2007, 10:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.fragmentor.impl;

import org.meta.fragment.Fragment;
import org.meta.fragmentor.FragmentCorrectorConstraints;
import org.meta.fragmentor.FragmentCorrectorRule;
import org.meta.fragmentor.FragmentCorrectorRuleViolationException;
import org.meta.fragmentor.FragmentationScheme;

/**
 * Correcter rule for breakage of ring at inappropariate place. 
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RingFragmentCorrectorRule extends FragmentCorrectorRule {
    
    /** Creates a new instance of RingFragmentCorrectorRule */
    public RingFragmentCorrectorRule() {
    }

    public void validateRule(FragmentationScheme fs, Fragment frag) 
                        throws FragmentCorrectorRuleViolationException {
    }

    public void validateAndCorrectRule(FragmentationScheme fs, Fragment frag, 
                                       FragmentCorrectorConstraints fcc) 
                        throws FragmentCorrectorRuleViolationException {
    }
    
} // end of class RingFragmentCorrectorRule
