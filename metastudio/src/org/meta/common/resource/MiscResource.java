/*
 * MiscResource.java
 *
 * Created on June 29, 2003, 9:05 AM
 */

package org.meta.common.resource;

import java.awt.Dimension;

/**
 * All misc. resources from here.
 * Uses singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MiscResource implements Resource {
    
    private static MiscResource _miscResource;
    
    /** Holds value of property splashScreenTime. */
    private int splashScreenTime;
    
    /** Holds value of property splashImageDimension. */
    private Dimension splashImageDimension;
    
    /** Holds value of property newWorkspaceWizardDimension. */
    private Dimension newWorkspaceWizardDimension;
    
    /** Holds value of property federationRuleEditorDimension. */
    private Dimension federationRuleEditorDimension;   
    
    /** Holds value of property splashScreenDimension. */
    private Dimension splashScreenDimension;
    
    /** Holds value of property guidePanelDimension. */
    private Dimension guidePanelDimension;
    
    /** Holds value of property maxLogBufferSize. */
    private int maxLogBufferSize;
    
    /** Holds value of property helpFrameDimension. */
    private Dimension helpFrameDimension;
    
    /** Holds value of property quickViewerDimension. */
    private Dimension quickViewerDimension;
    
    /**
     * Holds value of property rdfOptionsDialogSize.
     */
    private Dimension rdfOptionsDialogSize;

    /**
     * Holds value of property fragmentationWizardDimension.
     */
    private Dimension fragmentationWizardDimension;

    /**
     * Holds value of property atomInfoDialogDimension.
     */
    private Dimension atomInfoDialogDimension;
    
    /**
     * Holds the property simpleConnectivityMoleculeBuilderCutoff.
     * 
     * May be used by implementation of MoleculeBuilder, or any control
     * code that inturn calls MoleculeBuilder to form staggered connectivity.
     */
    private int simpleConnectivityMoleculeBuilderCutoff;

    /** Creates a new instance of MiscResource */
    private MiscResource() {
        splashScreenTime = 5000;
        maxLogBufferSize = 2048;
        
        splashImageDimension          = new Dimension(340, 200);
        splashScreenDimension         = new Dimension(340, 220);
        atomInfoDialogDimension       = new Dimension(400, 430);
        guidePanelDimension           = new Dimension(160, 360);
        newWorkspaceWizardDimension   = new Dimension(680, 430);
        helpFrameDimension            = new Dimension(650, 500);
        quickViewerDimension          = new Dimension(500, 450);
        rdfOptionsDialogSize          = new Dimension(500, 350);
        fragmentationWizardDimension  = new Dimension(650, 430);
        mainWindowDimension           = new Dimension(800, 600);
        energyEvaulaterUIDimension    = new Dimension(680, 600);
        federationRuleEditorDimension = new Dimension(680, 430);

        // The default trigger for forming initial simple connectivity
        // is set to 1000 atoms. Meaning for molecules with more than
        // 1000 atoms, the molecular builder might decide to only
        // detect simple connectivity initially to fasten up the
        // displaying process
        simpleConnectivityMoleculeBuilderCutoff = 1000;

        // default i/o buffer size 
        ioBufferSize = 8192;
    }
    
    /**
     * method to return instance of this object.
     *
     * @return MiscResource a single global instance of this class
     */
    public static MiscResource getInstance() {
        if (_miscResource == null) {
            _miscResource = new MiscResource();            
        } // end if
        
        return _miscResource;
    }
    
     /** Getter for property version.
     * @return Value of property version.
     */
    @Override
    public String getVersion() {
        return StringResource.getInstance().getVersion();
    }
    
    /** Getter for property splashScreenTime.
     * @return Value of property splashScreenTime.
     *
     */
    public int getSplashScreenTime() {
        return this.splashScreenTime;
    }
    
    /** Setter for property splashScreenTime.
     * @param splashScreenTime New value of property splashScreenTime.
     *
     */
    public void setSplashScreenTime(int splashScreenTime) {
        this.splashScreenTime = splashScreenTime;
    }
    
    /** Getter for property splashImageDimension.
     * @return Value of property splashImageDimension.
     *
     */
    public Dimension getSplashImageDimension() {
        return this.splashImageDimension;
    }
    
    /** Setter for property splashImageDimension.
     * @param splashImageDimension New value of property splashImageDimension.
     *
     */
    public void setSplashImageDimension(Dimension splashImageDimension) {
        this.splashImageDimension = splashImageDimension;
    }
    
    /** Getter for property newWorkspaceWizardDimension.
     * @return Value of property newWorkspaceWizardDimension.
     *
     */
    public Dimension getNewWorkspaceWizardDimension() {
        return this.newWorkspaceWizardDimension;
    }
    
    /** Setter for property newWorkspaceWizardDimension.
     * @param newWorkspaceWizardDimension New value of property 
     *        newWorkspaceWizardDimension.
     *
     */
    public void setNewWorkspaceWizardDimension(
                               Dimension newWorkspaceWizardDimension) {
        this.newWorkspaceWizardDimension = newWorkspaceWizardDimension;
    }
    
    /** Getter for property splashScreenDimension.
     * @return Value of property splashScreenDimension.
     *
     */
    public Dimension getSplashScreenDimension() {
        return this.splashScreenDimension;
    }
    
    /** Setter for property splashScreenDimension.
     * @param splashScreenDimension New value of property splashScreenDimension.
     *
     */
    public void setSplashScreenDimension(Dimension splashScreenDimension) {
        this.splashScreenDimension = splashScreenDimension;
    }
    
    /** Getter for property guidePanelDimension.
     * @return Value of property guidePanelDimension.
     *
     */
    public Dimension getGuidePanelDimension() {
        return this.guidePanelDimension;
    }
    
    /** Setter for property guidePanelDimension.
     * @param guidePanelDimension New value of property guidePanelDimension.
     *
     */
    public void setGuidePanelDimension(Dimension guidePanelDimension) {
        this.guidePanelDimension = guidePanelDimension;
    }
    
    /** Getter for property maxLogBufferSize.
     * @return Value of property maxLogBufferSize.
     *
     */
    public int getMaxLogBufferSize() {
        return this.maxLogBufferSize;
    }
    
    /** Setter for property maxLogBufferSize.
     * @param maxLogBufferSize New value of property maxLogBufferSize.
     *
     */
    public void setMaxLogBufferSize(int maxLogBufferSize) {
        this.maxLogBufferSize = maxLogBufferSize;
    }
    
    /** Getter for property helpFrameDimension.
     * @return Value of property helpFrameDimension.
     *
     */
    public Dimension getHelpFrameDimension() {
        return this.helpFrameDimension;
    }
    
    /** Setter for property helpFrameDimension.
     * @param helpFrameDimension New value of property helpFrameDimension.
     *
     */
    public void setHelpFrameDimension(Dimension helpFrameDimension) {
        this.helpFrameDimension = helpFrameDimension;
    }
    
    /** Getter for property quickViewerDimension.
     * @return Value of property quickViewerDimension.
     *
     */
    public Dimension getQuickViewerDimension() {
        return this.quickViewerDimension;
    }
    
    /** Setter for property quickViewerDimension.
     * @param quickViewerDimension New value of property quickViewerDimension.
     *
     */
    public void setQuickViewerDimension(Dimension quickViewerDimension) {
        this.quickViewerDimension = quickViewerDimension;
    }
    
    /**
     * Getter for property rdfOptionsDialogSize.
     * @return Value of property rdfOptionsDialogSize.
     */
    public Dimension getRdfOptionsDialogSize() {
        return this.rdfOptionsDialogSize;
    }
    
    /**
     * Setter for property rdfOptionsDialogSize.
     * @param rdfOptionsDialogSize New value of property rdfOptionsDialogSize.
     */
    public void setRdfOptionsDialogSize(Dimension rdfOptionsDialogSize) {
        this.rdfOptionsDialogSize = rdfOptionsDialogSize;
    }

    /**
     * Getter for property fragmentationWizardDimension.
     * @return Value of property fragmentationWizardDimension.
     */
    public Dimension getFragmentationWizardDimension() {
        return this.fragmentationWizardDimension;
    }

    /**
     * Setter for property fragmentationWizardDimension.
     * @param fragmentationWizardDimension New value of property 
     *        fragmentationWizardDimension.
     */
    public void setFragmentationWizardDimension(
                                      Dimension fragmentationWizardDimension) {
        this.fragmentationWizardDimension = fragmentationWizardDimension;
    }

    /**
     * Getter for property atomInfoDialogDimension.
     * @return Value of property atomInfoDialogDimension.
     */
    public Dimension getAtomInfoDialogDimension() {
        return this.atomInfoDialogDimension;
    }

    /**
     * Setter for property atomInfoDialogDimension.
     * @param atomInfoDialogDimension New value of property 
     *                                atomInfoDialogDimension.
     */
    public void setAtomInfoDialogDimension(Dimension atomInfoDialogDimension) {
        this.atomInfoDialogDimension = atomInfoDialogDimension;
    }

    /**
     * Holds value of property mainWindowDimension.
     */
    private Dimension mainWindowDimension;

    /**
     * Getter for property mainWindowDimension.
     * @return Value of property mainWindowDimension.
     */
    public Dimension getMainWindowDimension() {
        return this.mainWindowDimension;
    }

    /**
     * Setter for property mainWindowDimension.
     * @param mainWindowDimension New value of property mainWindowDimension.
     */
    public void setMainWindowDimension(Dimension mainWindowDimension) {
        this.mainWindowDimension = mainWindowDimension;
    }

    /**
     * Holds value of property energyEvaulaterUIDimension.
     */
    private Dimension energyEvaulaterUIDimension;

    /**
     * Getter for property energyEvaulaterUIDimension.
     * @return Value of property energyEvaulaterUIDimension.
     */
    public Dimension getEnergyEvaulaterUIDimension() {
        return this.energyEvaulaterUIDimension;
    }

    /**
     * Setter for property energyEvaulaterUIDimension.
     * @param energyEvaulaterUIDimension New value of property 
     *        energyEvaulaterUIDimension.
     */
    public void setEnergyEvaulaterUIDimension(Dimension 
                                              energyEvaulaterUIDimension) {
        this.energyEvaulaterUIDimension = energyEvaulaterUIDimension;
    }

    /**
     * Getter for property federationRuleEditorDimension.
     * @return Value of property federationRuleEditorDimension
     */
    public Dimension getFederationRuleEditorDimension() {
        return federationRuleEditorDimension;
    }

    /**
     * Setter for property federationRuleEditorDimension.
     * @param federationRuleEditorDimension New value of property 
     *        federationRuleEditorDimension.
     */
    public void setFederationRuleEditorDimension(
                                 Dimension federationRuleEditorDimension) {
        this.federationRuleEditorDimension = federationRuleEditorDimension;
    }

    /**
     * Get the value of simpleConnectivityMoleculeBuilderCutoff
     *
     * @return the value of simpleConnectivityMoleculeBuilderCutoff
     */
    public int getSimpleConnectivityMoleculeBuilderCutoff() {
        return simpleConnectivityMoleculeBuilderCutoff;
    }

    /**
     * Set the value of simpleConnectivityMoleculeBuilderCutoff
     *
     * @param simpleConnectivityMoleculeBuilderCutoff new
     *        value of simpleConnectivityMoleculeBuilderCutoff
     */
    public void setSimpleConnectivityMoleculeBuilderCutoff(
                         int simpleConnectivityMoleculeBuilderCutoff) {
        this.simpleConnectivityMoleculeBuilderCutoff
                    = simpleConnectivityMoleculeBuilderCutoff;
    }

    protected int ioBufferSize;

    /**
     * Get the value of ioBufferSize
     *
     * @return the value of ioBufferSize
     */
    public int getIoBufferSize() {
        return ioBufferSize;
    }

    /**
     * Set the value of ioBufferSize
     *
     * @param ioBufferSize new value of ioBufferSize
     */
    public void setIoBufferSize(int ioBufferSize) {
        this.ioBufferSize = ioBufferSize;
    }
} // end of class MiscResource
