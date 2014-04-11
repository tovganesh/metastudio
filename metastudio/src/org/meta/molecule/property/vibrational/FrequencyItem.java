/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.molecule.property.vibrational;

import java.util.ArrayList;
import org.meta.math.Vector3D;
import org.meta.molecule.Molecule;

/**
 * Frequency number and corresponding reduced mass
 * and intensity. This also stores the corresponding vectors
 * for each atom in the a molecule representing their respective
 * motions for this frequency number.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FrequencyItem {

    /**
     * Creates a new instance of FrequencyItem
     * 
     * @param frequency frequency number
     * @param molecule the Molecule object to which this FrequencyItem
     *        is connected
     */
    public FrequencyItem(double frequency, Molecule molecule) {
        this.frequency         = frequency;
        this.referenceMolecule = molecule;
    }

    protected double frequency;

    /**
     * Get the value of frequency
     *
     * @return the value of frequency
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Set the value of frequency
     *
     * @param frequency new value of frequency
     */
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    protected double reducedMass;

    /**
     * Get the value of reducedMass
     *
     * @return the value of reducedMass
     */
    public double getReducedMass() {
        return reducedMass;
    }

    /**
     * Set the value of reducedMass
     *
     * @param reducedMass new value of reducedMass
     */
    public void setReducedMass(double reducedMass) {
        this.reducedMass = reducedMass;
    }

    protected boolean imaginary;

    /**
     * Get the value of imaginary
     *
     * @return the value of imaginary
     */
    public boolean isImaginary() {
        return imaginary;
    }

    /**
     * Set the value of imaginary
     *
     * @param imaginary new value of imaginary
     */
    public void setImaginary(boolean imaginary) {
        this.imaginary = imaginary;
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

    protected ArrayList<Vector3D> displacements;

    /**
     * Get the value of displacements
     *
     * @return the value of displacements
     */
    public ArrayList<Vector3D> getDisplacements() {
        return displacements;
    }

    /**
     * Set the value of displacements
     *
     * @param displacements new value of displacements
     */
    public void setDisplacements(ArrayList<Vector3D> displacements) {
        this.displacements = displacements;
    }

    /**
     * Add a displacement vector
     *
     * @param disp a new displacement vector
     */
    public void addDisplacement(Vector3D disp) {
        if (displacements == null) displacements = new ArrayList<Vector3D>();

        displacements.add(disp);
    }

    /**
     * Remove a displacement vector
     * 
     * @param disp instance of the displacement vector to be removed 
     */
    public void removeDisplacement(Vector3D disp) {
        if (displacements == null) return;

        displacements.remove(disp);
    }

    /**
     * Set the displacement vector for a particular atom index
     *
     * @param atomIndex the atom index for which the new vector is to be set
     * @param newDisp the instance of new displacement vector
     */
    public void setDisplacementFor(int atomIndex, Vector3D newDisp) {
        displacements.set(atomIndex, newDisp);
    }

    /**
     * Return the displacement corresponding to a particular atom index
     * 
     * @param atomIndex the desired atom index 
     * @return the displacement for this aotm index
     */
    public Vector3D getDisplacementFor(int atomIndex) {
        return this.displacements.get(atomIndex);
    }

    protected double depolarization;

    /**
     * Get the value of depolarization
     *
     * @return the value of depolarization
     */
    public double getDepolarization() {
        return depolarization;
    }

    /**
     * Set the value of depolarization
     *
     * @param depolarization new value of depolarization
     */
    public void setDepolarization(double depolarization) {
        this.depolarization = depolarization;
    }

    protected ArrayList<Intensity> intensities;

    /**
     * Add Intensity data corresponding to this frequency number
     *
     * @param intensity instance of Intensity
     */
    public void addIntensity(Intensity intensity) {
        if (intensities == null) {
            intensities = new ArrayList<Intensity>();
        } // end if

        intensities.add(intensity);
    }

    /**
     * Remove Intensity data corresponding to this frequency number
     *
     * @param intensity instance of Intensity object to be removed from the list
     *                  of initensities connected to this frequency number
     */
    public void removeIntensity(Intensity intensity) {
        if (intensities == null) return;

        intensities.add(intensity);
    }

    /**
     * Return a list of Intensity objects connected to this frequency number
     *
     * @return a list of Intensity objects
     */
    public ArrayList<Intensity> getIntensities() {
        return intensities;
    }
}
