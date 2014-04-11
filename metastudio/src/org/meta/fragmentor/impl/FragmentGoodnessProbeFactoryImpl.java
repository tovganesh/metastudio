/*
 * FragmentGoodnessProbeFactoryImpl.java
 *
 * Created on April 3, 2005, 7:48 PM
 */

package org.meta.fragmentor.impl;

import java.util.*;
import org.meta.common.resource.StringResource;
import org.meta.fragmentor.FragmentGoodnessProbe;
import org.meta.fragmentor.FragmentGoodnessProbeFactory;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.molecule.Molecule;

/**
 * Default implementation of FragmentGoodnessProbeFactory.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentGoodnessProbeFactoryImpl 
                     implements FragmentGoodnessProbeFactory {
    
    private ResourceBundle resources;   
    private HashMap<String, String> probes;
    
    /** Creates a new instance of FragmentGoodnessProbeFactoryImpl */
    public FragmentGoodnessProbeFactoryImpl() {
        probes = new HashMap<String, String>(5);        
        
        // the initial parameters
        setDefaultParams();  
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() {        
        StringResource strings   = StringResource.getInstance();
        resources = 
           ResourceBundle.getBundle(strings.getFragmentGoodnessProbeResource());
        
        Enumeration<String> implKeys = resources.getKeys();        
        String theKey = "";
        String theProbe = "";
        
        try {
            while(implKeys.hasMoreElements()) {
                theKey = implKeys.nextElement();
                theProbe = resources.getString(theKey);
                
                probes.put(theKey, theProbe);
            } // end while
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + theKey + "."
                      + " Exception is : " + e.toString());
        } // end try .. catch block
    }
    
    /**
     * Get an implementation of FragmentGoodnessProbe with the specified name.
     *
     * @param fragmentationScheme the scheme to which the goodness probe will
     *                            be attached
     * @param molecule the molecule to which the fragmentationScheme is attached
     * @param goodnessProbeName a name with which the goodness probe is 
     *                          identified
     * @return FragmentGoodnessProbe an appropriate instance of goodness probe,
     *                               null otherwise.
     */
    public FragmentGoodnessProbe getFragmentGoodnessProbe(
                                       FragmentationScheme fragmentationScheme, 
                                       Molecule molecule,
                                       String goodnessProbeName
                                 ) {
        try {
            return ((FragmentGoodnessProbe)
                    (Class.forName(probes.get(goodnessProbeName))
                       .getDeclaredConstructor(new Class[] {
                                                   FragmentationScheme.class,
                                                   Molecule.class
                                              })
                    ).newInstance(fragmentationScheme, molecule));
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + goodnessProbeName + "."
                      + " Exception is : " + e.toString());
        } // end of try .. catch block
    }
    
    /**
     * Get default implementation of FragmentGoodnessProbe.
     *
     * @param fragmentationScheme the scheme to which the goodness probe will
     *                            be attached
     * @param molecule the molecule to which the fragmentationScheme is attached    
     * @return FragmentGoodnessProbe an appropriate instance of goodness probe,
     *                               null otherwise.
     */
    public FragmentGoodnessProbe getDefaultFragmentGoodnessProbe(
                                       FragmentationScheme fragmentationScheme, 
                                       Molecule molecule
                                 ) {
        try {
            return ((FragmentGoodnessProbe)
                    (Class.forName(probes.get("defaultProbe"))
                       .getDeclaredConstructor(new Class[] {
                                                   FragmentationScheme.class,
                                                   Molecule.class
                                              })
                    ).newInstance(fragmentationScheme, molecule));
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : defaultProbe."
                      + " Exception is : " + e.toString());
        } // end of try .. catch block
    }    
} // end class FragmentGoodnessProbeFactoryImpl
