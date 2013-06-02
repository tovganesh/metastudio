package org.meta.math.mm;

import java.lang.ref.WeakReference;
import org.meta.molecule.Molecule;


/**
 * Factory of MM Energy methods. <br>
 * Follows a singleton pattern.
 *
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MMEnergyMethodFactory {

    private static WeakReference<MMEnergyMethodFactory> _MMEnergyMethodFactory = null;

    /** Creates a new instance of MMEnergyMethodFactory */
    private MMEnergyMethodFactory() {
    }

    /**
     * Get an instance (and the only one) of MMEnergyMethodFactory
     *
     * @return MMEnergyMethodFactory instance
     */
    public static MMEnergyMethodFactory getInstance() {
        if (_MMEnergyMethodFactory == null) {
            _MMEnergyMethodFactory =
                    new WeakReference<MMEnergyMethodFactory>(new MMEnergyMethodFactory());
        } // end if

        MMEnergyMethodFactory MMEnergyMethodFactory = _MMEnergyMethodFactory.get();

        if (MMEnergyMethodFactory == null) {
            MMEnergyMethodFactory  = new MMEnergyMethodFactory();
            _MMEnergyMethodFactory =
                    new WeakReference<MMEnergyMethodFactory>(MMEnergyMethodFactory);
        } // end if

        return MMEnergyMethodFactory;
    }

    /**
     * Return an appropriate class
     *
     * @param molecule the molecule for which calculations are to be performed
     */
    public MMEnergyMethod getMMEnergyMethod(Molecule molecule) {
        return new UniversalForceFieldMethod(molecule, true);
    }
} // end of class MMEnergyMethodFactory
