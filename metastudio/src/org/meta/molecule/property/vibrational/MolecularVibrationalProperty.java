/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.molecule.property.vibrational;

import java.util.ArrayList;
import java.util.Iterator;
import org.meta.molecule.Molecule;

/**
 * Represents vibrational property of a molecule in terms
 * of normal modes of vibrarions for 3N-6 co-ordinates, N being
 * number of atoms.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MolecularVibrationalProperty {

    private ArrayList<FrequencyItem> frequencyItems;

    /** Creates an instance of MolecularVibrationalProperty */
    public MolecularVibrationalProperty(Molecule molecule) {
        frequencyItems = new ArrayList<FrequencyItem>();

        this.molecule = molecule;
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
     * Add a FrequencyItem object to this property.
     *
     * @param frequencyItem instance of new FrequencyItem
     */
    public void addFrequencyItem(FrequencyItem frequencyItem) {
        frequencyItem.setReferenceMolecule(molecule);
        frequencyItems.add(frequencyItem);
    }

    /**
     * Remove a FrequencyItem object from this property
     *
     * @param frequencyItem instance of FrequencyItem to be removed
     */
    public void removeFrequencyItem(FrequencyItem frequencyItem) {
        frequencyItem.setReferenceMolecule(null);
        frequencyItems.remove(frequencyItem);
    }

    /**
     * The iterator of frequency items.
     *
     * @return an iterator for FrequencyItem s corresponding to this property
     */
    public Iterator<FrequencyItem> getFrequencyItems() {
        return frequencyItems.iterator();
    }
}
