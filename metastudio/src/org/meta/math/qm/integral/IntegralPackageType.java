/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.qm.integral;

/**
 * Enumeration of Integral package types available in
 * MeTA Studio.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public enum IntegralPackageType {
    NUCLEAR_TERM,  // the nuclear term
    ONE_ELECTRON_TERM, // one-electron term (s)
    TWO_ELECTRON_HUZINAGA, // two-electron, using Huzunaga scheme
    TWO_ELECTRON_RYS, // two-electron, using RYS polynomials
    TWO_ELECTRON_HGP  // two-electron, using Head-Gordon/Pople scheme
}
