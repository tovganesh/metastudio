/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.math.qm.integral;

/**
 * Factory interface for instantiating appropriate integral
 * packages. Follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IntegralsPackageFactory {

    /** Private constructor */
    private IntegralsPackageFactory() {
        defaultTwoElectronIntegralPackage
                 = IntegralPackageType.TWO_ELECTRON_HUZINAGA;
    }

    private static IntegralsPackageFactory _theInstance;

    /**
     * Get the instance of IntegralsPackageFactory
     * 
     * @return instance of IntegralsPackageFactory
     */
    public static IntegralsPackageFactory getInstance() {
        if (_theInstance == null) {
            _theInstance = new IntegralsPackageFactory();
        } // end if

        return _theInstance;
    }

    private NuclearTerm _nuclearTerm;

    /**
     * Request a nuclear term integral package
     * 
     * @return instance of NuclearTerm
     */
    public NuclearTerm getNuclearTerm() {
        if (_nuclearTerm == null) _nuclearTerm = new NuclearTerm();

        return _nuclearTerm;
    }

    private OneElectronTerm _oneElectronTerm;

    /**
     * Request a one electron integral package
     *
     * @return instance of OneElectronTerm
     */
    public OneElectronTerm getOneElectronTerm() {
        if (_oneElectronTerm == null)
            _oneElectronTerm = new OneElectronTerm();

        return _oneElectronTerm;
    }

    private TwoElectronTerm _twoElectronTerm;

    /**
     * Request a 2E integral package.
     *
     * @param type requests a 2-E integral package
     * @return the instance of 2-E integral package, if no suitable one
     *         available, then UnsupportedOperationException is thrown
     */
    public TwoElectronTerm getTwoElectronTerm(IntegralPackageType type) {
        switch(type) {
            case TWO_ELECTRON_HUZINAGA:
                _twoElectronTerm = new HuzinagaTwoElectronTerm();
                break;
            case TWO_ELECTRON_RYS:
                _twoElectronTerm = new RysTwoElectronTerm();
                break;
            case TWO_ELECTRON_HGP:
                _twoElectronTerm = new HGPTwoElectronTerm();
                break;
            default:
                throw new UnsupportedOperationException("No 2E integral " +
                          "package yet for: " + type);
        } // end of switch .. case block

        return _twoElectronTerm;
    }

    /**
     * Get the default 2E integral package. Currently this is:
     * TWO_ELECTRON_HUZINAGA
     *
     * @return the instance of 2-E integral package, if no suitable one
     *         available, then UnsupportedOperationException is thrown
     */
    public TwoElectronTerm getTwoElectronTerm() {
        return getTwoElectronTerm(defaultTwoElectronIntegralPackage);
    }

    protected IntegralPackageType defaultTwoElectronIntegralPackage;

    /**
     * Get the value of defaultTwoElectronIntegralPackage
     *
     * @return the value of defaultTwoElectronIntegralPackage
     */
    public IntegralPackageType getDefaultTwoElectronIntegralPackage() {
        return defaultTwoElectronIntegralPackage;
    }

    /**
     * Set the value of defaultTwoElectronIntegralPackage
     *
     * @param defaultTwoElectronIntegralPackage new value of
     *              defaultTwoElectronIntegralPackage
     */
    public void setDefaultTwoElectronIntegralPackage(
                       IntegralPackageType defaultTwoElectronIntegralPackage) {
        this.defaultTwoElectronIntegralPackage
                     = defaultTwoElectronIntegralPackage;

        // change here affects the statically initilised Integral package
        // so update reference there
        Integrals.reInitPackageReference();
    }

    /**
     * Get a requested integral package.
     *
     * @param type the requested IntegralPackageType
     * @return the instance of the requested IntegralPackageType, if not
     *         available the an UnsupportedOperationException is thrown
     */
    public IntegralsPackage getPackage(IntegralPackageType type) {
        switch(type) {
            case NUCLEAR_TERM:
                return getNuclearTerm();
            case ONE_ELECTRON_TERM:
                return getOneElectronTerm();
            case TWO_ELECTRON_HUZINAGA:
            case TWO_ELECTRON_HGP:
            case TWO_ELECTRON_RYS:
                return getTwoElectronTerm(type);
            default:
                throw new UnsupportedOperationException("No integral " +
                          "package yet for: " + type);
        } // end of switch .. case block
    }
}
