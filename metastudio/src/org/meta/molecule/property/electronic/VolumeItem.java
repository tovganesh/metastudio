/**
 * VolumeItem.java
 *
 * Created on Feb 17, 2009
 */
package org.meta.molecule.property.electronic;

import org.meta.molecule.Molecule;

/**
 * Represents a GridProperty connected to a Molecule object.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class VolumeItem {

    /**
     * Creates a new instance of VolumeItem
     * 
     * @param mol the reference Molecule object
     * @param gp the corresponding GridProperty object
     */
    public VolumeItem(Molecule mol, GridProperty gp) {
        this(mol, gp, MolecularVolumetricPropertyType.UNKNOWN_TYPE);
    }

    /**
     * Creates a new instance of VolumeItem
     *
     * @param mol the reference Molecule object
     * @param gp the corresponding GridProperty object
     * @param propertyType type of this VolumeItem
     */
    public VolumeItem(Molecule mol, GridProperty gp,
                      MolecularVolumetricPropertyType propertyType) {
        this.referenceMolecule = mol;
        this.gridProperty      = gp;
        this.propertyType      = propertyType;
    }

    protected MolecularVolumetricPropertyType propertyType;

    /**
     * Get the value of propertyType
     *
     * @return the value of propertyType
     */
    public MolecularVolumetricPropertyType getPropertyType() {
        return propertyType;
    }

    /**
     * Set the value of propertyType
     *
     * @param propertyType new value of propertyType
     */
    public void setPropertyType(MolecularVolumetricPropertyType propertyType) {
        this.propertyType = propertyType;
    }

    protected Molecule referenceMolecule;

    /**
     * Get the value of referenceMolecule
     *
     * @return the value of referenceMolecule
     */
    public Molecule getReferenceMolecule() {
        return referenceMolecule;
    }

    /**
     * Set the value of referenceMolecule
     *
     * @param referenceMolecule new value of referenceMolecule
     */
    public void setReferenceMolecule(Molecule referenceMolecule) {
        this.referenceMolecule = referenceMolecule;
    }

    protected GridProperty gridProperty;

    /**
     * Get the value of gridProperty
     *
     * @return the value of gridProperty
     */
    public GridProperty getGridProperty() {
        return gridProperty;
    }

    /**
     * Set the value of gridProperty
     *
     * @param gridProperty new value of gridProperty
     */
    public void setGridProperty(GridProperty gridProperty) {
        this.gridProperty = gridProperty;
    }
}
