/**
 * Units.java
 *
 * Created on Apr 3, 2009
 */
package org.meta.common;

/**
 * Enum defining Units used to represent various numbers used in IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum Units {
    AU {
        @Override
        public String toString() { return "a.u."; }
    },
    ANGSTROM {
        @Override
        public String toString() { return "angstroms"; }
    },
    KCAL {
        @Override
        public String toString() { return "kcal"; }
    }
}
