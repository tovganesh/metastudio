/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.molecule.property.electronic;

import java.util.ArrayList;
import org.meta.molecule.Molecule;

/**
 * Represents one or many volumetric properties connected to a
 * molecule object.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MolecularVolumetricProperty {

    protected ArrayList<VolumeItem> volumeItemList;
    
    /** Creates a new instance of MolecularVolumetricProperty */
    public MolecularVolumetricProperty(Molecule molecule) {
        this.molecule = molecule;

        this.volumeItemList = new ArrayList<VolumeItem>();
    }

    protected Molecule molecule;

    /**
     * Get the value of molecule
     *
     * @return the value of molecule
     */
    public Molecule getMolecule() {
        return molecule;
    }

    /**
     * Add a new VolumeItem object to this property
     *
     * @param volumeItem instance of new VolumeItem
     */
    public void addVolumeItem(VolumeItem volumeItem) {
        volumeItem.setReferenceMolecule(molecule);
        volumeItemList.add(volumeItem);
    }

    /**
     * Remove an existing VolumeItem from this property
     *
     * @param volumeItem instance of VolumeItem to be removed
     */
    public void removeVolumeItem(VolumeItem volumeItem) {
        volumeItem.setReferenceMolecule(null);
        volumeItemList.remove(volumeItem);
    }
}
