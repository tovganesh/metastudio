/*
 * EnergyEvaluater.java
 *
 * Created on January 26, 2007, 4:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.fragmentor;

import java.util.Hashtable;
import org.meta.fragment.FragmentList;
import org.meta.molecule.MolecularFormula;

/**
 * Energy evaluater, based in cardinality expression.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class EnergyEvaluater {
    
    private FragmentationScheme fragmentScheme;
    private Hashtable<Integer, Double> fragmentEnergies;
    
    /** Creates a new instance of EnergyEvaluater */
    public EnergyEvaluater(FragmentationScheme fragmentScheme, 
                           Hashtable<Integer, Double> fragmentEnergies) {
        this.fragmentScheme   = fragmentScheme;
        this.fragmentEnergies = fragmentEnergies;
        
        this.expressionVariable = "e";
    }
    
    /**
     * Return the cardinality energy expression
     *
     * @return the energy expression
     */
    public String getEnergyExpression() {
        FragmentList fl = fragmentScheme.getFragmentList();
        
        String expr = expressionVariable + "0";
        int sign;
        
        for(int i=1; i<fl.size(); i++) {
            sign = fl.getFragment(i).getCardinalitySign();
            
            if (sign > 0) { 
                if (sign > 1) 
                    expr += " + " + sign + expressionVariable + i ;
                else
                    expr += " + " + expressionVariable + i ;
            } else if (sign < 0) {     
                if (sign < -1) 
                    expr += " - " + Math.abs(sign) + expressionVariable + i;
                else
                    expr += " - " + expressionVariable + i;
            } // end if
        } // end for
        
        return expr;
    }
    
    /**
     * Return the energy obtained from cardinality energy expression
     *
     * @return the value of energy 
     */
    public double getEnergy() {
        FragmentList fl = fragmentScheme.getFragmentList();
        
        double energy = 0.0;
        int sign;
        
        for(int i=0; i<fl.size(); i++) {
            sign = fl.getFragment(i).getCardinalitySign();
            
            energy += sign * fragmentEnergies.get(i);
        } // end for
        
        return energy;
    }

    /**
     * Holds value of property expressionVariable.
     */
    private String expressionVariable;

    /**
     * Getter for property expressionVariable.
     * @return Value of property expressionVariable.
     */
    public String getExpressionVariable() {
        return this.expressionVariable;
    }

    /**
     * Setter for property expressionVariable.
     * @param expressionVariable New value of property expressionVariable.
     */
    public void setExpressionVariable(String expressionVariable) {
        this.expressionVariable = expressionVariable;
    }
        
} // end of class EnergyEvaluater
