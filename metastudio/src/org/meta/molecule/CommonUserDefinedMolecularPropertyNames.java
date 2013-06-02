/**
 * CommonUserDefinedMolecularPropertyNames.java
 *
 * Created on 03/08/2009
 */

package org.meta.molecule;

import java.io.Serializable;

/**
 * Some common user defined molecular property names.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum CommonUserDefinedMolecularPropertyNames implements Serializable {
    SOURCE_FILE_NAME,  /* the source file name of the molecule object */
    SIMPLE_BOND_DETECTED, /* simple bond detected */
    MULTIPLE_BOND_DETECTED, /* multiple bond detected */
    WEAK_BOND_DETECTED, /* weak bond detected */
    PARTIAL_CONNECTIVITY_FORMED, /* partial connectivity is formed */
    SPECIAL_STRUCTURE_DETECTED, /* special structure detected */
    READ_DEEP, /* deep information read from the corresponding source */
    PERIODIC, /* adding Periodic data */
    
    /* other properties which may be used by tools such as MoleculeBuilders
       progressively build a Molecule object */
    DETECT_ALL,
    DETECT_ONLY_SINGLE_BONDS,
    DETECT_ONLY_WEAK_BONDS,
    DETECT_ONLY_MULTIPLE_BONDS,
    DETECT_ONLY_SPECIAL_STRUCTURE,
    INITIATE_READ_DEEP
}
