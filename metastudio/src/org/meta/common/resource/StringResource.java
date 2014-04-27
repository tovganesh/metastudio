/*
 * StringResource.java
 *
 * Created on June 15, 2003, 7:43 PM
 */

package org.meta.common.resource;

import java.io.File;

/**
 * A class supplying all the string resources for the application.
 * Employs a singleton design pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class StringResource implements Resource {
    
    private static final String BUILD_NUMBER = "27042014";
    private static final String IDE_VERSION = "MeTA Studio v2.0." 
                                              + BUILD_NUMBER;

    private static final String META_STUDIO_WEBSITE =
                              "http://code.google.com/p/metastudio/";

    private static final String META_STUDIO_BLOG =
                              "http://tovganesh.blogspot.com/search/label/MeTA";
    
    private static final String META_DB = "/db/metadb";
    
    private static StringResource _stringResource;
    
    /** Holds value of property newWorkspaceTip. */
    private String newWorkspaceTip;
    
    /** Holds value of property openWorkspaceTip. */
    private String openWorkspaceTip;
    
    /** Holds value of property saveWorkspaceTip. */
    private String saveWorkspaceTip;
    
    /** Holds value of property saveAsWorkspaceTip. */
    private String saveAsWorkspaceTip;
    
    /** Holds value of property exitTip. */
    private String exitTip;
    
    /** Holds value of property uiClass. */
    private String uiClass;
    
    /** Holds value of property ideLoggerName. */
    private String ideLoggerName;
    
    /** Holds value of property loggerClass. */
    private String loggerClass;
    
    /** Holds value of property defaultImplResource. */
    private String defaultImplResource;
    
    /** Holds value of property openBeanShellTip. */
    private String openBeanShellTip;
    
    /** Holds value of property fileReaderResource. */
    private String fileReaderResource;
    
    /** Holds value of property ideHelpSet. */
    private String ideHelpSet;
    
    /** Holds value of property imageMapResource. */
    private String imageMapResource;
    
    /** Holds value of property viewerResource. */
    private String viewerResource;
    
    /** Holds value of property xmlHeader. */
    private String xmlHeader;
    
    /** Holds value of property defaultAtomInfo. */
    private String defaultAtomInfo;
    
    /** Holds value of property workspaceFileExtension. */
    private String workspaceFileExtension;
    
    /** Holds value of property workspaceItemFileExtension. */
    private String workspaceItemFileExtension;
    
    /** Holds value of property stdErrorMessage. */
    private String stdErrorMessage;
    
    /** Holds value of property workspaceItemImplResource. */
    private String workspaceItemImplResource;
    
    /**
     * Holds value of property recentFiles.
     */
    private String recentFiles;
    
    /**
     * Holds value of property appDir.
     */
    private String appDir;
    
    /**
     * Holds value of property remoteAppDir.
     */
    private String remoteAppDir;
    
    /**
     * Holds value of property updateDir.
     */
    private String updateDir;   
    
    /**
     * Holds value of property extLibDir.
     */
    private String extLibDir;   
    
    /**
     * Holds value of property extLibFile.
     */
    private String extLibFile;   
    
    /**
     * Holds value of property basisLibraryPath.
     */
    private String basisLibraryPath;
    
    /**
     * Holds value of property beanShellScriptExtension.
     */
    private String beanShellScriptExtension;
    
    /**
     * Holds value of property rdfList.
     */
    private String rdfList;
    
    /**
     * Holds value of property searchURLs.
     */
    private String searchURLs;

    /**
     * Holds value of property fragmentationSchemeResource.
     */
    private String fragmentationSchemeResource;

    /**
     * Holds value of property fragmentGoodnessProbeResource.
     */
    private String fragmentGoodnessProbeResource;

    /**
     * Holds value of property atomInfoSettingsTip.
     */
    private String atomInfoSettingsTip;

    /**
     * Holds value of property userAtomInfo.
     */
    private String userAtomInfo;
    
    /** Creates a new instance of StringResource -- private! */
    private StringResource() {
        // common strings for the IDE
        newWorkspaceTip    = "<html><body>"
                            + "Opens up a wizard which guides you for creation "
                            + "of a new workspace. <br>"
                            + "You can use workspace to organize all your "
                            + "data and analysis files pertaining to a "
                            + "particular project."
                            + "</html></body>";
        openWorkspaceTip   = "Opens an existing workspace " 
                              + "previously created by you";
        saveWorkspaceTip   = "Saves the current workspace";
        saveAsWorkspaceTip = "Saves the current workspace with a new name";
        exitTip            = "Exit the IDE";
        openBeanShellTip   = "Opens a new BeanShell interpreter in a GUI shell";
        atomInfoSettingsTip= "Opens up a dialog which allows you to change " +
                    "various paramenters related to representation of an atom";
        openMoleculeTip    = "<html><body>"
                            + "Opens up a molecule file in a viewer, so "
                            + "that you can view/ query the molecular "
                            + "geometry."
                            + "</html></body>";
        newMoleculeTip     = "<html><body>"
                            + "Opens up a molecule editor, so "
                            + "that you can create a molecule " 
                            + "[CAUTION: This is an experimental feature!]."
                            + "</html></body>";
        openBeanShellTip   = "<html><body>"
                            + "Opens up a powerful bash(unix) like object "
                            + "oriented shell. <br> "
                            + "You can use this shell for seemless control "
                            + "over each and every functionality of "
                            + "<b>MeTA Studio</b>."
                            + "</html></body>";
        openJRManTip       = "<html><body>"
                            + "Opens up JRMan for rendering RenderMan files."
                            + "</html></body>";
        saveMoleculeTip    = "<html><body>"
                            + "Exports the current geometry "
                            + "in Renderman format, <br> which can be rendered "
                            + "using renderman compliant rendering tools like " 
                            + "<i>JRMan</i>, <i>3Delight</i> etc.."
                            + "</html></body>";
        exportRIBTip       = "Save the current geometry";
        attachPropertyTip  = "Attach a property file";
        saveMoleculeImageTip = "Save current scene as image file";
        
        // the UI class
        uiClass = "com.incors.plaf.kunststoff.KunststoffLookAndFeel";
        
        // the logger space
        ideLoggerName = "org.meta";
        loggerClass   = "org.meta.shell.idebeans.LogWindow";   
        
        // default properties
        defaultImplResource = 
                       "org.meta.config.impl.DefaultConfiguration";
        fileReaderResource  = 
                "org.meta.moleculereader.impl.MoleculeFileReaders";
        propertyFileReaderResource  = 
                "org.meta.propertyreader.impl.PropertyFileReaders";
        imageMapResource    =
                       "org.meta.common.resource.ImageResourceMap";        
        viewerResource      =
               "org.meta.shell.idebeans.viewers.impl.QuickViewers";
        fragmentationSchemeResource  =
               "org.meta.fragmentor.impl.FragmentationSchemes";
        fragmentGoodnessProbeResource =
           "org.meta.fragmentor.impl.FragmentGoodnessProbes";        
        defaultAtomInfo     =
               "/org/meta/config/impl/AtomInfo.xml";
        edgeTableResource   = "/org/meta/shell/idebeans/graphics/"
                               + "surfaces/edgeTable.xml";
        triTableResource    = "/org/meta/shell/idebeans/graphics/"
                               + "surfaces/triTable.xml";
        federationRequestHandlers 
                            = "/org/meta/net/" 
                              + "FederationRequestHandlers.xml";
        
        try {
            appDir = System.getProperty("user.home") + "/.meta2.0";
        } catch (Exception e) {
            System.out.println("Running in restricted mode.");
            appDir = ".meta2.0";
        } // end of try catch 
        
        recentFiles         = appDir + "/recent.xml";
        rdfList             = appDir + "/rdflist.xml";
        searchURLs          = appDir + "/searchurls.xml";
        userAtomInfo        = appDir + "/AtomInfo.xml";
        lastVisitedFolder   = appDir + "/lastVisitedFolder";
        pluginDir           = appDir + "/plugins";
        widgetsDir          = appDir + "/widgets";
        java3DSettings      = appDir + "/java3DSettings";
        launcherConfig      = appDir + "/launcherConfig.xml";
        xmppConfig          = appDir + "/xmppConfig.xml";
        federationSecurityResource = appDir + "/federationSecurity.xml";
        remoteAppDir        = appDir + "/remoteapps";
        updateDir           = appDir + "/updates";
        extLibFile          = appDir + "/extLibs";
        extLibDir           = ".."   + File.separatorChar + "lib" 
                                     + File.separatorChar + "ext";
                
        workspaceItemImplResource =
               "org.meta.workspace.impl.WorkspaceItemTypeMap";
        basisLibraryPath = 
               "/org/meta/math/qm/basis/basis_";

        scriptengines =
                "org.meta.scripting.ScriptEngines";
        
        // the help sets
        ideHelpSet = "IdeHelp.hs";
        
        // the xml
        xmlHeader  = "<?xml version=\"1.0\" standalone=\"yes\" ?> \n" 
                     + "<!-- \n"
                     + " This document is automatically generated by: "
                     + getVersion() + "\n"
                     + "(c) V.Ganesh \n"
                     + "--> \n";
        
        // workspace related
        workspaceFileExtension     = "mws";
        workspaceItemFileExtension = "mwi";
        beanShellScriptExtension   = "bsh";
        
        // standard messages
        stdErrorMessage = "An unhandled error has occured. \n"
                          + "If you feel this is a bug, please save the"
                          + " contents of Workspace and Runtime log and inform"
                          + " the developers the same!";

        // meta studio archive file related
        marManifestFileName = "manifest.xml";        
        marFileExtension    = "mar";
        
        chemStoreManifestFileName = "manifest.xml";        
        
        // meta database related entries
        dbFramework = "embedded";
        dbDriver    = "org.apache.derby.jdbc.EmbeddedDriver";
        dbProtocol  = "jdbc:derby:" + appDir + META_DB + ";create=true";
    }
    
    /**
     * Refresh app specific settings. This is useful when an external application 
     * is being run, and you don't want to mess around with default MeTA Studio
     * settings.
     */
    public void refreshAppSettingsPath() {
        recentFiles         = appDir + "/recent.xml";
        rdfList             = appDir + "/rdflist.xml";
        searchURLs          = appDir + "/searchurls.xml";
        userAtomInfo        = appDir + "/AtomInfo.xml";
        lastVisitedFolder   = appDir + "/lastVisitedFolder";
        java3DSettings      = appDir + "/java3DSettings";
        launcherConfig      = appDir + "/launcherConfig.xml";
        xmppConfig          = appDir + "/xmppConfig.xml";
        federationSecurityResource = appDir + "/federationSecurity.xml";
    }
    
    /**
     * method to return instance of this object.
     *
     * @return StringResource a single global instance of this class
     */
    public static StringResource getInstance() {
        if (_stringResource == null) {
            _stringResource = new StringResource();            
        } // end if
        
        return _stringResource;
    }
    
    /** Getter for property version.
     * @return Value of property version.
     */
    @Override
    public String getVersion() {
        return IDE_VERSION;
    }    
    
     /** Getter for property buildNumber.
     * @return Value of property buildNumber.
     */
    public String getBuildNumber() {
        return BUILD_NUMBER;
    }
    
    /** Getter the MeTA Studio website
     * 
     * @return the MeTA Studio website URL as a String
     */
    public String getMeTAStudioWebSite() {
        return META_STUDIO_WEBSITE;
    }

    /** Getter the MeTA Studio blog
     *
     * @return the MeTA Studio blog URL as a String
     */
    public String getMeTAStudioBlog() {
        return META_STUDIO_BLOG;
    }

    /** Getter for property newWorkspaceTip.
     * @return Value of property newWorkspaceTip.
     *
     */
    public String getNewWorkspaceTip() {
        return this.newWorkspaceTip;
    }
    
    /** Setter for property newWorkspaceTip.
     * @param newWorkspaceTip New value of property newWorkspaceTip.
     *
     */
    public void setNewWorkspaceTip(String newWorkspaceTip) {
        this.newWorkspaceTip = newWorkspaceTip;
    }
    
    /** Getter for property openWorkspaceTip.
     * @return Value of property openWorkspaceTip.
     *
     */
    public String getOpenWorkspaceTip() {
        return this.openWorkspaceTip;
    }
    
    /** Setter for property openWorkspaceTip.
     * @param openWorkspaceTip New value of property openWorkspaceTip.
     *
     */
    public void setOpenWorkspaceTip(String openWorkspaceTip) {
        this.openWorkspaceTip = openWorkspaceTip;
    }
    
    /** Getter for property saveWorkspaceTip.
     * @return Value of property saveWorkspaceTip.
     *
     */
    public String getSaveWorkspaceTip() {
        return this.saveWorkspaceTip;
    }
    
    /** Setter for property saveWorkspaceTip.
     * @param saveWorkspaceTip New value of property saveWorkspaceTip.
     *
     */
    public void setSaveWorkspaceTip(String saveWorkspaceTip) {
        this.saveWorkspaceTip = saveWorkspaceTip;
    }
    
    /** Getter for property saveAsWorkspaceTip.
     * @return Value of property saveAsWorkspaceTip.
     *
     */
    public String getSaveAsWorkspaceTip() {
        return this.saveAsWorkspaceTip;
    }
    
    /** Setter for property saveAsWorkspaceTip.
     * @param saveAsWorkspaceTip New value of property saveAsWorkspaceTip.
     *
     */
    public void setSaveAsWorkspaceTip(String saveAsWorkspaceTip) {
        this.saveAsWorkspaceTip = saveAsWorkspaceTip;
    }
    
    /** Getter for property exitTip.
     * @return Value of property exitTip.
     *
     */
    public String getExitTip() {
        return this.exitTip;
    }
    
    /** Setter for property exitTip.
     * @param exitTip New value of property exitTip.
     *
     */
    public void setExitTip(String exitTip) {
        this.exitTip = exitTip;
    }
    
    /** Getter for property uiClass.
     * @return Value of property uiClass.
     *
     */
    public String getUiClass() {
        return this.uiClass;
    }
    
    /** Setter for property uiClass.
     * @param uiClass New value of property uiClass.
     *
     */
    public void setUiClass(String uiClass) {
        this.uiClass = uiClass;
    }
    
    /** Getter for property ideLoggerName.
     * @return Value of property ideLoggerName.
     *
     */
    public String getIdeLoggerName() {
        return this.ideLoggerName;
    }
    
    /** Setter for property ideLoggerName.
     * @param ideLoggerName New value of property ideLoggerName.
     *
     */
    public void setIdeLoggerName(String ideLoggerName) {
        this.ideLoggerName = ideLoggerName;
    }
    
    /** Getter for property loggerClass.
     * @return Value of property loggerClass.
     *
     */
    public String getLoggerClass() {
        return this.loggerClass;
    }
    
    /** Setter for property loggerClass.
     * @param loggerClass New value of property loggerClass.
     *
     */
    public void setLoggerClass(String loggerClass) {
        this.loggerClass = loggerClass;
    }
    
    /** Getter for property defaultImplResource.
     * @return Value of property defaultImplResource.
     *
     */
    public String getDefaultImplResource() {
        return this.defaultImplResource;
    }
    
    /** Setter for property defaultImplResource.
     * @param defaultImplResource New value of property defaultImplResource.
     *
     */
    public void setDefaultImplResource(String defaultImplResource) {
        this.defaultImplResource = defaultImplResource;
    }
    
    /** Getter for property openBeanShellTip.
     * @return Value of property openBeanShellTip.
     *
     */
    public String getOpenBeanShellTip() {
        return this.openBeanShellTip;
    }
    
    /** Setter for property openBeanShellTip.
     * @param openBeanShellTip New value of property openBeanShellTip.
     *
     */
    public void setOpenBeanShellTip(String openBeanShellTip) {
        this.openBeanShellTip = openBeanShellTip;
    }
    
    /** Getter for property fileReaderResource.
     * @return Value of property fileReaderResource.
     *
     */
    public String getFileReaderResource() {
        return this.fileReaderResource;
    }
    
    /** Setter for property fileReaderResource.
     * @param fileReaderResource New value of property fileReaderResource.
     *
     */
    public void setFileReaderResource(String fileReaderResource) {
        this.fileReaderResource = fileReaderResource;
    }
    
    /** Getter for property ideHelpSet.
     * @return Value of property ideHelpSet.
     *
     */
    public String getIdeHelpSet() {
        return this.ideHelpSet;
    }
    
    /** Setter for property ideHelpSet.
     * @param ideHelpSet New value of property ideHelpSet.
     *
     */
    public void setIdeHelpSet(String ideHelpSet) {
        this.ideHelpSet = ideHelpSet;
    }
    
    /** Getter for property imageMapResource.
     * @return Value of property imageMapResource.
     *
     */
    public String getImageMapResource() {
        return this.imageMapResource;
    }
    
    /** Setter for property imageMapResource.
     * @param imageMapResource New value of property imageMapResource.
     *
     */
    public void setImageMapResource(String imageMapResource) {
        this.imageMapResource = imageMapResource;
    }
    
    /** Getter for property viewerResource.
     * @return Value of property viewerResource.
     *
     */
    public String getViewerResource() {
        return this.viewerResource;
    }
    
    /** Setter for property viewerResource.
     * @param viewerResource New value of property viewerResource.
     *
     */
    public void setViewerResource(String viewerResource) {
        this.viewerResource = viewerResource;
    }
    
    /** Getter for property xmlHeader.
     * @return Value of property xmlHeader.
     *
     */
    public String getXmlHeader() {
        return this.xmlHeader;
    }
    
    /** Setter for property xmlHeader.
     * @param xmlHeader New value of property xmlHeader.
     *
     */
    public void setXmlHeader(String xmlHeader) {
        this.xmlHeader = xmlHeader;
    }
    
    /** Getter for property defaultAtomInfo.
     * @return Value of property defaultAtomInfo.
     *
     */
    public String getDefaultAtomInfo() {
        return this.defaultAtomInfo;
    }
    
    /** Setter for property defaultAtomInfo.
     * @param defaultAtomInfo New value of property defaultAtomInfo.
     *
     */
    public void setDefaultAtomInfo(String defaultAtomInfo) {
        this.defaultAtomInfo = defaultAtomInfo;
    }
    
    /** Getter for property workspaceFileExtension.
     * @return Value of property workspaceFileExtension.
     *
     */
    public String getWorkspaceFileExtension() {
        return this.workspaceFileExtension;
    }
    
    /** Setter for property workspaceFileExtension.
     * @param workspaceFileExtension New value of 
     *        property workspaceFileExtension.
     *
     */
    public void setWorkspaceFileExtension(String workspaceFileExtension) {
        this.workspaceFileExtension = workspaceFileExtension;
    }
    
    /** Getter for property workspaceItemFileExtension.
     * @return Value of property workspaceItemFileExtension.
     *
     */
    public String getWorkspaceItemFileExtension() {
        return this.workspaceItemFileExtension;
    }
    
    /** Setter for property workspaceItemFileExtension.
     * @param workspaceItemFileExtension New value of 
     *        property workspaceItemFileExtension.
     *
     */
    public void setWorkspaceItemFileExtension(
                                    String workspaceItemFileExtension) {
        this.workspaceItemFileExtension = workspaceItemFileExtension;
    }
    
    /** Getter for property stdErrorMessage.
     * @return Value of property stdErrorMessage.
     *
     */
    public String getStdErrorMessage() {
        return this.stdErrorMessage;
    }
    
    /** Setter for property stdErrorMessage.
     * @param stdErrorMessage New value of property stdErrorMessage.
     *
     */
    public void setStdErrorMessage(String stdErrorMessage) {
        this.stdErrorMessage = stdErrorMessage;
    }
    
    /** Getter for property workspaceItemImplResource.
     * @return Value of property workspaceItemImplResource.
     *
     */
    public String getWorkspaceItemImplResource() {
        return this.workspaceItemImplResource;
    }
    
    /** Setter for property workspaceItemImplResource.
     * @param workspaceItemImplResource New value of 
     *        property workspaceItemImplResource.
     *
     */
    public void setWorkspaceItemImplResource(String workspaceItemImplResource) {
        this.workspaceItemImplResource = workspaceItemImplResource;
    }
    
    /**
     * Getter for property recentFiles.
     * @return Value of property recentFiles.
     */
    public String getRecentFiles() {
        return this.recentFiles;
    }
    
    /**
     * Setter for property recentFiles.
     * @param recentFiles New value of property recentFiles.
     */
    public void setRecentFiles(String recentFiles) {
        this.recentFiles = recentFiles;
    }
    
    /**
     * Getter for property appDir.
     * @return Value of property appDir.
     */
    public String getAppDir() {
        return this.appDir;
    }
        
    /**
     * Setter for property appDir.
     * @param appDir New value of property appDir.
     */
    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }
    
    /**
     * Getter for property remoteAppDir.
     * @return Value of property remoteAppDir.
     */
    public String getRemoteAppDir() {
        return this.remoteAppDir;
    }
    
    /**
     * Getter for property basisLibraryPath.
     * @return Value of property basisLibraryPath.
     */
    public String getBasisLibraryPath() {
        return this.basisLibraryPath;
    }
    
    /**
     * Setter for property basisLibraryPath.
     * @param basisLibraryPath New value of property basisLibraryPath.
     */
    public void setBasisLibraryPath(String basisLibraryPath) {
        this.basisLibraryPath = basisLibraryPath;
    }
    
    /**
     * Getter for property beanShellScriptExtension.
     * @return Value of property beanShellScriptExtension.
     */
    public String getBeanShellScriptExtension() {
        return this.beanShellScriptExtension;
    }
    
    /**
     * Setter for property beanShellScriptExtension.
     * @param beanShellScriptExtension New value of 
     *        property beanShellScriptExtension.
     */
    public void setBeanShellScriptExtension(String beanShellScriptExtension) {
        this.beanShellScriptExtension = beanShellScriptExtension;
    }
    
    /**
     * Getter for property rdfList.
     * @return Value of property rdfList.
     */
    public String getRdfList() {
        return this.rdfList;
    }
    
    /**
     * Setter for property rdfList.
     * @param rdfList New value of property rdfList.
     */
    public void setRdfList(String rdfList) {
        this.rdfList = rdfList;
    }
    
    /**
     * Getter for property searchURLs.
     * @return Value of property searchURLs.
     */
    public String getSearchURLs() {
        return this.searchURLs;
    }
    
    /**
     * Setter for property searchURLs.
     * @param searchURLs New value of property searchURLs.
     */
    public void setSearchURLs(String searchURLs) {
        this.searchURLs = searchURLs;
    }

    /**
     * Getter for property fragmentationSchemeResource.
     * @return Value of property fragmentationSchemeResource.
     */
    public String getFragmentationSchemeResource() {
        return this.fragmentationSchemeResource;
    }

    /**
     * Setter for property fragmentationSchemeResource.
     * @param fragmentationSchemeResource New value of 
     *        property fragmentationSchemeResource.
     */
    public void setFragmentationSchemeResource(
                                String fragmentationSchemeResource) {
        this.fragmentationSchemeResource = fragmentationSchemeResource;
    }

    /**
     * Getter for property fragmentGoodnessProbeResource.
     * @return Value of property fragmentGoodnessProbeResource.
     */
    public String getFragmentGoodnessProbeResource() {
        return this.fragmentGoodnessProbeResource;
    }

    /**
     * Setter for property fragmentGoodnessProbeResource.
     * @param fragmentGoodnessProbeResource New value of 
     *        property fragmentGoodnessProbeResource.
     */
    public void setFragmentGoodnessProbeResource(
                           String fragmentGoodnessProbeResource) {
        this.fragmentGoodnessProbeResource = fragmentGoodnessProbeResource;
    }

    /**
     * Getter for property atomInfoSettingsTip.
     * @return Value of property atomInfoSettingsTip.
     */
    public String getAtomInfoSettingsTip() {
        return this.atomInfoSettingsTip;
    }

    /**
     * Setter for property atomInfoSettingsTip.
     * @param atomInfoSettingsTip New value of property atomInfoSettingsTip.
     */
    public void setAtomInfoSettingsTip(String atomInfoSettingsTip) {
        this.atomInfoSettingsTip = atomInfoSettingsTip;
    }

    /**
     * Getter for property userAtomInfo.
     * @return Value of property userAtomInfo.
     */
    public String getUserAtomInfo() {
        return this.userAtomInfo;
    }

    /**
     * Setter for property userAtomInfo.
     * @param userAtomInfo New value of property userAtomInfo.
     */
    public void setUserAtomInfo(String userAtomInfo) {
        this.userAtomInfo = userAtomInfo;
    }

    /**
     * Holds value of property propertyFileReaderResource.
     */
    private String propertyFileReaderResource;

    /**
     * Getter for property propertyFileReaderResource.
     * @return Value of property propertyFileReaderResource.
     */
    public String getPropertyFileReaderResource() {
        return this.propertyFileReaderResource;
    }

    /**
     * Setter for property propertyFileReaderResource.
     * @param propertyFileReaderResource New value of property 
     *        propertyFileReaderResource.
     */
    public void setPropertyFileReaderResource(
                               String propertyFileReaderResource) {
        this.propertyFileReaderResource = propertyFileReaderResource;
    }

    /**
     * Holds value of property lastVisitedFolder.
     */
    private String lastVisitedFolder;

    /**
     * Getter for property lastVisitedFolder.
     * @return Value of property lastVisitedFolder.
     */
    public String getLastVisitedFolder() {
        return this.lastVisitedFolder;
    }

    /**
     * Setter for property lastVisitedFolder.
     * @param lastVisitedFolder New value of property lastVisitedFolder.
     */
    public void setLastVisitedFolder(String lastVisitedFolder) {
        this.lastVisitedFolder = lastVisitedFolder;
    }

    /**
     * Holds value of property pluginDir.
     */
    private String pluginDir;

    /**
     * Getter for property pluginDir.
     * @return Value of property pluginDir.
     */
    public String getPluginDir() {
        return this.pluginDir;
    }

    /**
     * Setter for property pluginDir.
     * @param pluginDir New value of property pluginDir.
     */
    public void setPluginDir(String pluginDir) {
        this.pluginDir = pluginDir;
    }

    /**
     * Holds value of property launcherConfig.
     */
    private String launcherConfig;

    /**
     * Getter for property launcherConfig.
     * @return Value of property launcherConfig.
     */
    public String getLauncherConfig() {
        return this.launcherConfig;
    }

    /**
     * Setter for property launcherConfig.
     * @param launcherConfig New value of property launcherConfig.
     */
    public void setLauncherConfig(String launcherConfig) {
        this.launcherConfig = launcherConfig;
    }

    /**
     * Holds value of property edgeTableResource.
     */
    private String edgeTableResource;

    /**
     * Getter for property edgeTableResource.
     * @return Value of property edgeTableResource.
     */
    public String getEdgeTableResource() {
        return this.edgeTableResource;
    }

    /**
     * Setter for property edgeTableResource.
     * @param edgeTableResource New value of property edgeTableResource.
     */
    public void setEdgeTableResource(String edgeTableResource) {
        this.edgeTableResource = edgeTableResource;
    }

    /**
     * Holds value of property triTableResource.
     */
    private String triTableResource;

    /**
     * Getter for property triTableResource.
     * @return Value of property triTableResource.
     */
    public String getTriTableResource() {
        return this.triTableResource;
    }

    /**
     * Setter for property triTableResource.
     * @param triTableResource New value of property triTableResource.
     */
    public void setTriTableResource(String triTableResource) {
        this.triTableResource = triTableResource;
    }

    /**
     * Holds value of property federationRequestHandlers.
     */
    private String federationRequestHandlers;

    /**
     * Getter for property federationRequestHandlers.
     * @return Value of property federationRequestHandlers.
     */
    public String getFederationRequestHandlers() {
        return this.federationRequestHandlers;
    }

    /**
     * Setter for property federationRequestHandlers.
     * @param federationRequestHandlers New value of property 
     *        federationRequestHandlers.
     */
    public void setFederationRequestHandlers(String federationRequestHandlers) {
        this.federationRequestHandlers = federationRequestHandlers;
    }

    /**
     * Holds value of property openMoleculeTip.
     */
    private String openMoleculeTip;

    /**
     * Getter for property openMoleculeTip.
     * @return Value of property openMoleculeTip.
     */
    public String getOpenMoleculeTip() {
        return this.openMoleculeTip;
    }

    /**
     * Setter for property openMoleculeTip.
     * @param openMoleculeTip New value of property openMoleculeTip.
     */
    public void setOpenMoleculeTip(String openMoleculeTip) {
        this.openMoleculeTip = openMoleculeTip;
    }

    /**
     * Holds value of property newMoleculeTip.
     */
    private String newMoleculeTip;

    /**
     * Getter for property newMoleculeTip.
     * @return Value of property newMoleculeTip.
     */
    public String getNewMoleculeTip() {
        return this.newMoleculeTip;
    }

    /**
     * Setter for property newMoleculeTip.
     * @param newMoleculeTip New value of property newMoleculeTip.
     */
    public void setNewMoleculeTip(String newMoleculeTip) {
        this.newMoleculeTip = newMoleculeTip;
    }

    /**
     * Holds value of property openBeanshellTip.
     */
    private String openBeanshellTip;

    /**
     * Getter for property openBeanshellTip.
     * @return Value of property openBeanshellTip.
     */
    public String getOpenBeanshellTip() {
        return this.openBeanshellTip;
    }

    /**
     * Setter for property openBeanshellTip.
     * @param openBeanshellTip New value of property openBeanshellTip.
     */
    public void setOpenBeanshellTip(String openBeanshellTip) {
        this.openBeanshellTip = openBeanshellTip;
    }

    /**
     * Holds value of property openJRManTip.
     */
    private String openJRManTip;

    /**
     * Getter for property openJRManTip.
     * @return Value of property openJRManTip.
     */
    public String getOpenJRManTip() {
        return this.openJRManTip;
    }

    /**
     * Setter for property openJRManTip.
     * @param openJRManTip New value of property openJRManTip.
     */
    public void setOpenJRManTip(String openJRManTip) {
        this.openJRManTip = openJRManTip;
    }

    /**
     * Holds value of property exportRIBTip.
     */
    private String exportRIBTip;

    /**
     * Getter for property exportRIBTip.
     * @return Value of property exportRIBTip.
     */
    public String getExportRIBTip() {
        return this.exportRIBTip;
    }

    /**
     * Setter for property exportRIBTip.
     * @param exportRIBTip New value of property exportRIBTip.
     */
    public void setExportRIBTip(String exportRIBTip) {
        this.exportRIBTip = exportRIBTip;
    }

    /**
     * Holds value of property saveMoleculeTip.
     */
    private String saveMoleculeTip;

    /**
     * Getter for property saveMoleculeTip.
     * @return Value of property saveMoleculeTip.
     */
    public String getSaveMoleculeTip() {
        return this.saveMoleculeTip;
    }

    /**
     * Setter for property saveMoleculeTip.
     * @param saveMoleculeTip New value of property saveMoleculeTip.
     */
    public void setSaveMoleculeTip(String saveMoleculeTip) {
        this.saveMoleculeTip = saveMoleculeTip;
    }

    /**
     * Holds value of property saveMoleculeImageTip.
     */
    private String saveMoleculeImageTip;

    /**
     * Getter for property saveMoleculeImageTip.
     * @return Value of property saveMoleculeImageTip.
     */
    public String getSaveMoleculeImageTip() {
        return this.saveMoleculeImageTip;
    }

    /**
     * Setter for property saveMoleculeImageTip.
     * @param saveMoleculeImageTip New value of property saveMoleculeImageTip.
     */
    public void setSaveMoleculeImageTip(String saveMoleculeImageTip) {
        this.saveMoleculeImageTip = saveMoleculeImageTip;
    }

    /**
     * Holds value of property attachPropertyTip.
     */
    private String attachPropertyTip;

    /**
     * Getter for property attachPropertyTip.
     * @return Value of property attachPropertyTip.
     */
    public String getAttachPropertyTip() {
        return this.attachPropertyTip;
    }

    /**
     * Setter for property attachPropertyTip.
     * @param attachPropertyTip New value of property attachPropertyTip.
     */
    public void setAttachPropertyTip(String attachPropertyTip) {
        this.attachPropertyTip = attachPropertyTip;
    }

    /**
     * Holds value of property federationSecurityResource.
     */
    private String federationSecurityResource;

    /**
     * Getter for property federationSecurityResource.
     * @return Value of property federationSecurityResource.
     */
    public String getFederationSecurityResource() {
        return this.federationSecurityResource;
    }

    /**
     * Holds value of property widgetsDir.
     */
    private String widgetsDir;

    /**
     * Getter for property widgetsDir.
     * @return Value of property widgetsDir.
     */
    public String getWidgetsDir() {
        return this.widgetsDir;
    }

    /**
     * Holds value of property java3DSettings.
     */
    private String java3DSettings;

    /**
     * Getter for property java3DSettings.
     * @return Value of property java3DSettings.
     */
    public String getJava3DSettings() {
        return this.java3DSettings;
    }

    /**
     * Getter property for updateDir
     * @return Value of property updateDir.
     */
    public String getUpdateDir() {
        return updateDir;
    }
    
    /**
     * Getter property for extLibDir
     * @return Value of property extLibDir.
     */
    public String getExtLibDir() {
        return extLibDir;
    }

    /**
     * Getter property for extLibFile
     * @return Value of property extLibFile.
     */
    public String getExtLibFile() {
        return extLibFile;
    }

    private String xmppConfig;

    /**
     * Get the value of xmppConfig
     *
     * @return the value of xmppConfig
     */
    public String getXmppConfig() {
        return xmppConfig;
    }

    protected String marManifestFileName;

    /**
     * Get the value of marManifestFileName
     *
     * @return the value of marManifestFileName
     */
    public String getMarManifestFileName() {
        return marManifestFileName;
    }

    protected String marFileExtension;

    /**
     * Get the value of marFileExtension
     *
     * @return the value of marFileExtension
     */
    public String getMarFileExtension() {
        return marFileExtension;
    }

    protected String chemStoreManifestFileName;

    /**
     * Get the value of chemStoreManifestFileName
     *
     * @return the value of chemStoreManifestFileName
     */
    public String getChemStoreManifestFileName() {
        return chemStoreManifestFileName;
    }

    private final String scriptengines;
    
    /**
     * Get the value of scriptengines
     *
     * @return the value of scriptengines
     */
    public String getScriptengines() {
        return scriptengines;
    }

    protected String dbFramework;

    /**
     * Get the value of dbFramework
     *
     * @return the value of dbFramework
     */
    public String getDbFramework() {
        return dbFramework;
    }

    protected String dbDriver;

    /**
     * Get the value of dbDriver
     *
     * @return the value of dbDriver
     */
    public String getDbDriver() {
        return dbDriver;
    }

    protected String dbProtocol;

    /**
     * Get the value of dbProtocol
     *
     * @return the value of dbProtocol
     */
    public String getDbProtocol() {
        return dbProtocol;
    }

} // end of class StringResource
