/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.shell.idebeans.graphics.surfaces;

import org.meta.molecule.property.electronic.GridProperty;

/**
 * Common interface for generating grid properties for various surfaces: 
 * vdW, Lee Richards, Connolly 
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface GridPropertyGenerator {
    
   /**
     * Get the generated grid property function 
     * 
     * @return a GridProperty object that can be used to render a surface
     */ 
   public GridProperty getGridProperty(); 
   
}
