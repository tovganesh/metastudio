/*
 * PowerList.java
 *
 * Created on July 22, 2004, 8:57 PM
 */

package org.meta.math.qm.basis;

import java.util.*;

import java.lang.ref.WeakReference;

/**
 * Orbital symbol to power list map.
 * Follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class PowerList {
    
    private static WeakReference<PowerList> _powerList = null;
    
    private HashMap<String, ArrayList<Power>> thePowerList;
    
    /** Creates a new instance of PowerList */
    private PowerList() {
        // put in the standard powers for the orbital symbols  
        
        thePowerList = new HashMap<String, ArrayList<Power>>(6);
        
        // the S orbital
        thePowerList.put("S", generatePowerList(0));
        
        // the P orbital
        thePowerList.put("P", generatePowerList(1));
        
        // the D orbital
        thePowerList.put("D", generatePowerList(2));
        
        // the F orbital
        thePowerList.put("F", generatePowerList(3));

        // the G orbital
        thePowerList.put("G", generatePowerList(4));

        // the H orbital
        thePowerList.put("H", generatePowerList(5));
    }
    
    /** 
     * Generate a power list for the given maximum angular momentum value
     * 
     * @param maxAngularMomentum the maximum angular momentum
     * @return the Power list in order for this maximum angular momentum
     */
    public ArrayList<Power> generatePowerList(int maxAngularMomentum) {
        ArrayList<Power> pList = new ArrayList<Power>();

        for(int i=maxAngularMomentum; i>=0; i--)
            for(int j=maxAngularMomentum-i; j>=0; j--)
                pList.add(new Power(i, j, maxAngularMomentum-i-j));

        return pList;
    }

    /**
     * Get an instance (and the only one) of PowerList
     *
     * @return PowerList instance
     */
    public static PowerList getInstance() {
        if (_powerList == null) {
            _powerList = new WeakReference<PowerList>(new PowerList());
        } // end if
        
        PowerList powerList = _powerList.get();
        
        if (powerList == null) {
            powerList  = new PowerList();
            _powerList = new WeakReference<PowerList>(powerList);
        } // end if
        
        return powerList;
    }
    
    /**
     * get the power list for the specified orbital symbol 
     * ('S', 'P', 'D' or 'F' .. no explicit error checking done, but 
     *  will throw a RuntimeException if the arguments are incorrect)
     *
     * @param orbital - 'S', 'P', 'D' or 'F'
     * @return Iterator of Power object representing the powers
     */
    public Iterator<Power> getPowerList(String orbital) {
        return thePowerList.get(orbital).iterator();
    }
} // end of class PowerList
