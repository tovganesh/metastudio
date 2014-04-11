/*
 * ImageResource.java
 *
 * Created on June 29, 2003, 10:09 AM
 */

package org.meta.common.resource;

import java.awt.*;
import java.util.*;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Repositery of all the image files used in the IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ImageResource implements Resource {
    
    private static ImageResource _imageResource;
    
    /** Holds value of property imagePath. */
    private String imagePath;
    
    /** Holds value of property ideSplash. */
    private Image ideSplash;
    
    /** Holds value of property waterMark. */
    private Image waterMark;
    
    /** Holds value of property ideSplashURL. */
    private URL ideSplashURL;
    
    /** Holds value of property waterMarkURL. */
    private URL waterMarkURL;
    
    /** Holds value of property about. */
    private ImageIcon about;
    
    /** Holds value of property aboutURL. */
    private URL aboutURL;
    
    /** Holds value of property newWorkspace. */
    private ImageIcon newWorkspace;
    
    /** Holds value of property newWorkspaceURL. */
    private URL newWorkspaceURL;
    
    /** Holds value of property saveWorkspace. */
    private ImageIcon saveWorkspace;
    
    /** Holds value of property saveWorkspaceURL. */
    private URL saveWorkspaceURL;
    
    /** Holds value of property openWorkspace. */
    private ImageIcon openWorkspace;
    
    /** Holds value of property openWorkspaceURL. */
    private URL openWorkspaceURL;
    
    /** Holds value of property iconURL. */
    private URL iconURL;
    
    /** Holds value of property icon. */
    private ImageIcon icon;
    
    /** Holds value of property explorerURL. */
    private URL explorerURL;
    
    /** Holds value of property explorer. */
    private ImageIcon explorer;
    
    /** Holds value of property help. */
    private ImageIcon help;
    
    /** Holds value of property helpURL. */
    private URL helpURL;
    
    /** for handling image resource map */
    private ResourceBundle resources;
    private Vector<String> theKeys;
    
    /** Holds value of property cut. */
    private ImageIcon cut;
    
    /** Holds value of property cutURL. */
    private URL cutURL;
    
    /** Holds value of property copy. */
    private ImageIcon copy;
    
    /** Holds value of property copyURL. */
    private URL copyURL;
    
    /** Holds value of property paste. */
    private ImageIcon paste;
    
    /** Holds value of property pasteURL. */
    private URL pasteURL;
    
    /** Holds value of property watch. */
    private ImageIcon watch;
    
    /** Holds value of property watchURL. */
    private URL watchURL;
    
    /** Holds value of property logWindow. */
    private ImageIcon logWindow;
    
    /** Holds value of property logWindowURL. */
    private URL logWindowURL;
    
    /** Holds value of property workspace. */
    private ImageIcon workspace;
    
    /** Holds value of property workspaceURL. */
    private URL workspaceURL;
    
    /** Holds value of property fileSystem. */
    private ImageIcon fileSystem;
    
    /** Holds value of property fileSystemURL. */
    private URL fileSystemURL;
    
    /** Holds value of property molecule. */
    private ImageIcon molecule;
    
    /** Holds value of property atoms. */
    private ImageIcon atoms;
    
    /** Holds value of property moleculeURL. */
    private URL moleculeURL;
    
    /** Holds value of property atomsURL. */
    private URL atomsURL;
    
    /** Holds value of property beany. */
    private ImageIcon beany;
    
    /** Holds value of property beanyURL. */
    private URL beanyURL;
    
    /** Holds value of property zoom. */
    private ImageIcon zoom;
    
    /** Holds value of property zoomURL. */
    private URL zoomURL;
    
    /** Holds value of property freehand. */
    private ImageIcon freehand;
    
    /** Holds value of property freehandURL. */
    private URL freehandURL;
    
    /** Holds value of property pencilTool. */
    private ImageIcon pencilTool;
    
    /** Holds value of property pencilToolURL. */
    private URL pencilToolURL;
    
    /** Holds value of property fullscreen. */
    private ImageIcon fullscreen;
    
    /** Holds value of property fullscreenURL. */
    private URL fullscreenURL;
    
    /** Holds value of property translate. */
    private ImageIcon translate;
    
    /** Holds value of property translateURL. */
    private URL translateURL;
    
    /** Holds value of property rotate. */
    private ImageIcon rotate;
    
    /** Holds value of property rotateURL. */
    private URL rotateURL;
    
    /** Holds value of property removeAtom. */
    private ImageIcon removeAtom;
    
    /** Holds value of property removeAtomURL. */
    private URL removeAtomURL;
    
    /** Holds value of property redo. */
    private ImageIcon redo;
    
    /** Holds value of property redoURL. */
    private URL redoURL;
    
    /** Holds value of property undo. */
    private ImageIcon undo;
    
    /** Holds value of property undoURL. */
    private URL undoURL;
    
    /** Holds value of property refresh. */
    private ImageIcon refresh;
    
    /** Holds value of property refreshURL. */
    private URL refreshURL;
    
    /** Holds value of property globalReset. */
    private ImageIcon globalReset;
    
    /** Holds value of property globalResetURL. */
    private URL globalResetURL;
    
    /** Holds value of property localReset. */
    private ImageIcon localReset;
    
    /** Holds value of property localResetURL. */
    private URL localResetURL;
    
    /** Holds value of property pointer. */
    private ImageIcon pointer;
    
    /** Holds value of property pointerURL. */
    private URL pointerURL;
    
    /** Holds value of property moleculeViewer. */
    private ImageIcon moleculeViewer;
    
    /** Holds value of property moleculeViewerURL. */
    private URL moleculeViewerURL;
    
    /** Holds value of property expand. */
    private ImageIcon expand;
    
    /** Holds value of property expandURL. */
    private URL expandURL;
    
    /** Holds value of property shrink. */
    private ImageIcon shrink;
    
    /** Holds value of property shrinkURL. */
    private URL shrinkURL;
    
    /** Holds value of property tasks. */
    private ImageIcon tasks;
    
    /** Holds value of property tasksURL. */
    private URL tasksURL;
    
    /** Holds value of property render. */
    private ImageIcon render;
    
    /** Holds value of property renderURL. */
    private URL renderURL;
    
    /** Holds value of property closeWindow. */
    private ImageIcon closeWindow;
    
    /** Holds value of property closeWindowURL. */
    private URL closeWindowURL;
    
    /** Holds value of property alert. */
    private ImageIcon alert;
    
    /** Holds value of property alertURL. */
    private URL alertURL;
        
    /**
     * Holds value of property editor.
     */
    private ImageIcon editor;
    
    /**
     * Holds value of property editorURL.
     */
    private URL editorURL;
    
    /**
     * Holds value of property ring.
     */
    private ImageIcon ring;
    
    /**
     * Holds value of property ringURL.
     */
    private URL ringURL;
    
    /**
     * Holds value of property planarRing.
     */
    private ImageIcon planarRing;
    
    /**
     * Holds value of property planarRingURL.
     */
    private URL planarRingURL;
    
    /**
     * Holds value of property nonPlanarRing.
     */
    private ImageIcon nonPlanarRing;
    
    /**
     * Holds value of property nonPlanarRingURL.
     */
    private URL nonPlanarRingURL;
    
    /**
     * Holds value of property informURL.
     */
    private URL informURL;
    
    /**
     * Holds value of property inform.
     */
    private ImageIcon inform;
    
    /**
     * Holds value of property warnURL.
     */
    private URL warnURL;
    
    /**
     * Holds value of property warn.
     */
    private ImageIcon warn;
    
    /**
     * Holds value of property questionURL.
     */
    private URL questionURL;
    
    /**
     * Holds value of property question.
     */
    private ImageIcon question;
    
    /**
     * Holds value of property errorURL.
     */
    private URL errorURL;
    
    /**
     * Holds value of property error.
     */
    private ImageIcon error;
    
    /**
     * Holds value of property scriptFolder.
     */
    private ImageIcon scriptFolder;
    
    /**
     * Holds value of property scriptFolderURL.
     */
    private URL scriptFolderURL;
    
    /**
     * Holds value of property cube.
     */
    private ImageIcon cube;
    
    /**
     * Holds value of property cubeURL.
     */
    private URL cubeURL;
    
    /**
     * Holds value of property sphere.
     */
    private ImageIcon sphere;
    
    /**
     * Holds value of property sphereURL.
     */
    private URL sphereURL;
    
    /**
     * Holds value of property atomGroup.
     */
    private ImageIcon atomGroup;
    
    /**
     * Holds value of property atomGroupURL.
     */
    private URL atomGroupURL;
    
    /**
     * Holds value of property fragments.
     */
    private ImageIcon fragments;
    
    /**
     * Holds value of property fragmentsURL.
     */
    private URL fragmentsURL;
    
    /**
     * Holds value of property fragment.
     */
    private ImageIcon fragment;
    
    /**
     * Holds value of property fragmentURL.
     */
    private URL fragmentURL;
    
    /**
     * Holds value of property correctorConstraints.
     */
    private ImageIcon correctorConstraints;
    
    /**
     * Holds value of property correctorConstraintsURL.
     */
    private URL correctorConstraintsURL;
    
    /**
     * Holds value of property corrector.
     */
    private ImageIcon corrector;
    
    /**
     * Holds value of property correctorURL.
     */
    private URL correctorURL;
    
    /**
     * Holds value of property goodnessProbes.
     */
    private ImageIcon goodnessProbes;
    
    /**
     * Holds value of property goodnessProbesURL.
     */
    private URL goodnessProbesURL;
    
    /**
     * Holds value of property goodnessProbe.
     */
    private ImageIcon goodnessProbe;
    
    /**
     * Holds value of property goodnessProbeURL.
     */
    private URL goodnessProbeURL;
    
    /**
     * Holds value of property fragmentationSchemes.
     */
    private ImageIcon fragmentationSchemes;
    
    /**
     * Holds value of property fragmentationSchemesURL.
     */
    private URL fragmentationSchemesURL;
    
    /**
     * Holds value of property fragmentationScheme.
     */
    private ImageIcon fragmentationScheme;
    
    /**
     * Holds value of property fragmentationSchemeURL.
     */
    private URL fragmentationSchemeURL;
    
    /**
     * Holds value of property rss.
     */
    private ImageIcon rss;
    
    /**
     * Holds value of property rssURL.
     */
    private URL rssURL;
    
    /**
     * Holds value of property options.
     */
    private ImageIcon options;
    
    /**
     * Holds value of property optionsURL.
     */
    private URL optionsURL;
    
    /**
     * Holds value of property go.
     */
    private ImageIcon go;
    
    /**
     * Holds value of property goURL.
     */
    private URL goURL;
    
    /**
     * Holds value of property jobUpdate.
     */
    private ImageIcon jobUpdate;
    
    /**
     * Holds value of property jobUpdateURL.
     */
    private URL jobUpdateURL;
    
    /**
     * Holds value of property jobItem.
     */
    private ImageIcon jobItem;
    
    /**
     * Holds value of property jobItemURL.
     */
    private URL jobItemURL;
    
    /**
     * Holds value of property movieMode.
     */
    private ImageIcon movieMode;
    
    /**
     * Holds value of property movieModeURL.
     */
    private URL movieModeURL;
    
    /**
     * Holds value of property play.
     */
    private ImageIcon play;
    
    /**
     * Holds value of property playURL.
     */
    private URL playURL;
    
    /**
     * Holds value of property pause.
     */
    private ImageIcon pause;
    
    /**
     * Holds value of property pauseURL.
     */
    private URL pauseURL;
    
    /**
     * Holds value of property stop.
     */
    private ImageIcon stop;
    
    /**
     * Holds value of property stopURL.
     */
    private URL stopURL;
    
    /** Creates a new instance of ImageResource */
    private ImageResource() {
        imagePath = "/org/meta/common/resource/images/";
        
        Dimension splashSize = MiscResource.getInstance()
                                               .getSplashImageDimension();
        ideSplashURL = getClass().getResource(imagePath + "metaSplash.png");
        ideSplash    = Toolkit.getDefaultToolkit().getImage(ideSplashURL); 
        ideSplash    = ideSplash.getScaledInstance(splashSize.width, 
                                                   splashSize.height, 
                                                   Image.SCALE_SMOOTH);
        
        waterMarkURL = getClass().getResource(imagePath + "metaWatermark.png");
        waterMark    = Toolkit.getDefaultToolkit().getImage(waterMarkURL);   
        
        aboutURL = getClass().getResource(imagePath + "about.gif");
        about    = new ImageIcon(aboutURL); 
        
        newWorkspaceURL = getClass().getResource(imagePath + "new.gif");
        newWorkspace    = new ImageIcon(newWorkspaceURL); 
        
        openWorkspaceURL = getClass().getResource(imagePath + "open.gif");
        openWorkspace    = new ImageIcon(openWorkspaceURL);
        
        saveWorkspaceURL = getClass().getResource(imagePath + "save.gif");
        saveWorkspace    = new ImageIcon(saveWorkspaceURL); 
        
        iconURL = getClass().getResource(imagePath + "icon.png");
        icon    = new ImageIcon(iconURL);                
        
        explorerURL = getClass().getResource(imagePath + "find.gif");
        explorer    = new ImageIcon(explorerURL);
        
        helpURL = getClass().getResource(imagePath + "help.gif");
        help    = new ImageIcon(helpURL);
        
        cutURL = getClass().getResource(imagePath + "cut.gif");
        cut    = new ImageIcon(cutURL);
        
        copyURL = getClass().getResource(imagePath + "copy.gif");
        copy    = new ImageIcon(copyURL);
        
        pasteURL = getClass().getResource(imagePath + "paste.gif");
        paste    = new ImageIcon(pasteURL);
        
        watchURL = getClass().getResource(imagePath + "watch.gif");
        watch    = new ImageIcon(watchURL);
        
        logWindowURL = getClass().getResource(imagePath + "logWindow.gif");
        logWindow    = new ImageIcon(logWindowURL);
        
        workspaceURL = getClass().getResource(imagePath + "workspace.gif");
        workspace    = new ImageIcon(workspaceURL);
        
        fileSystemURL = getClass().getResource(imagePath + "filesystem.gif");
        fileSystem    = new ImageIcon(fileSystemURL);
        
        redoURL = getClass().getResource(imagePath + "redo.gif");
        redo    = new ImageIcon(redoURL);
        
        undoURL = getClass().getResource(imagePath + "undo.gif");
        undo    = new ImageIcon(undoURL);
        
        refreshURL = getClass().getResource(imagePath + "refresh.gif");
        refresh    = new ImageIcon(refreshURL);
        
        globalResetURL = getClass().getResource(imagePath + "globalreset.png");
        globalReset    = new ImageIcon(globalResetURL);
        
        localResetURL = getClass().getResource(imagePath + "localreset.png");
        localReset    = new ImageIcon(localResetURL);
        
        pointerURL = getClass().getResource(imagePath + "pointer.png");
        pointer    = new ImageIcon(pointerURL);
        
        moleculeViewerURL = getClass().getResource(imagePath 
                                                   + "moleculeviewer.png");
        moleculeViewer    = new ImageIcon(moleculeViewerURL);
        
        moleculeEditorURL = getClass().getResource(imagePath 
                                                   + "moleculeditor.png");
        moleculeEditor    = new ImageIcon(moleculeEditorURL);
        
        beanyURL      = getClass().getResource(imagePath + "beany.png");
        beany         = new ImageIcon(beanyURL);
        
        jrManURL      = getClass().getResource(imagePath + "jrman.png");
        jrMan         = new ImageIcon(Toolkit.getDefaultToolkit()
                                 .getImage(jrManURL)
                                 .getScaledInstance(beany.getIconWidth(), 
                                                    beany.getIconHeight(), 
                                                    Image.SCALE_SMOOTH));
        
        talkURL = getClass().getResource(imagePath + "talk.png");
        talk    = new ImageIcon(Toolkit.getDefaultToolkit()
                                 .getImage(talkURL)
                                 .getScaledInstance(beany.getIconWidth(), 
                                                    beany.getIconHeight(), 
                                                    Image.SCALE_SMOOTH));
        
        voiceURL = getClass().getResource(imagePath + "voice.png");
        voice    = new ImageIcon(Toolkit.getDefaultToolkit()
                                 .getImage(voiceURL)
                                 .getScaledInstance(beany.getIconWidth(), 
                                                    beany.getIconHeight(), 
                                                    Image.SCALE_SMOOTH));
        
        novoiceURL = getClass().getResource(imagePath + "novoice.png");
        novoice    = new ImageIcon(Toolkit.getDefaultToolkit()
                                 .getImage(novoiceURL)
                                 .getScaledInstance(beany.getIconWidth(), 
                                                    beany.getIconHeight(), 
                                                    Image.SCALE_SMOOTH));
        
        scratchPadURL = getClass().getResource(imagePath + "scratchpad.png");
        scratchPad    = new ImageIcon(Toolkit.getDefaultToolkit()
                                 .getImage(scratchPadURL)
                                 .getScaledInstance(beany.getIconWidth(), 
                                                    beany.getIconHeight(), 
                                                    Image.SCALE_SMOOTH));
        
        fullscreenURL = getClass().getResource(imagePath + "fullscreen.png");
        fullscreen    = new ImageIcon(fullscreenURL);
        
        makeCurrentViewURL = getClass().getResource(
                                          imagePath + "makecurrent.png");
        makeCurrentView    = new ImageIcon(makeCurrentViewURL);
        
        freehandURL = getClass().getResource(imagePath + "freehand.png");
        freehand    = new ImageIcon(freehandURL);
        
        sphereURL = getClass().getResource(imagePath + "sphere.png");
        sphere    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(sphereURL)
                               .getScaledInstance(freehand.getIconWidth(), 
                                                  freehand.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        cubeURL = getClass().getResource(imagePath + "cube.png");
        cube    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(cubeURL)
                               .getScaledInstance(freehand.getIconWidth(), 
                                                  freehand.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        surfaceURL = getClass().getResource(imagePath + "surface.png");
        surface    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(surfaceURL)
                               .getScaledInstance(freehand.getIconWidth(), 
                                                  freehand.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        pencilToolURL = getClass().getResource(imagePath + "pencilTool.png");
        pencilTool    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(pencilToolURL)
                               .getScaledInstance(freehand.getIconWidth(), 
                                                  freehand.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        removeAtomURL = getClass().getResource(imagePath + "removeAtom.png");
        removeAtom    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(removeAtomURL)
                               .getScaledInstance(freehand.getIconWidth(), 
                                                  freehand.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        zoomURL = getClass().getResource(imagePath + "zoom.png");
        zoom    = new ImageIcon(zoomURL);
        
        translateURL = getClass().getResource(imagePath + "translate.png");
        translate    = new ImageIcon(translateURL);
        
        rotateURL = getClass().getResource(imagePath + "rotate.png");
        rotate    = new ImageIcon(rotateURL);
        
        atomsURL = getClass().getResource(imagePath + "atoms.jpg");
        atoms    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(atomsURL)
                               .getScaledInstance(workspace.getIconWidth(), 
                                                  workspace.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        expandURL = getClass().getResource(imagePath + "expand.png");
        expand    = new ImageIcon(expandURL);
        
        shrinkURL = getClass().getResource(imagePath + "shrink.png");
        shrink    = new ImageIcon(shrinkURL);
        
        renderURL = getClass().getResource(imagePath + "render.png");
        render    = new ImageIcon(renderURL);
        
        alertURL = getClass().getResource(imagePath + "alert.png");
        alert    = new ImageIcon(alertURL);
        
        informURL = getClass().getResource(imagePath + "inform.gif");
        inform    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(informURL)
                               .getScaledInstance(alert.getIconWidth(), 
                                                  alert.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        warnURL = getClass().getResource(imagePath + "warn.gif");
        warn    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(warnURL)
                               .getScaledInstance(alert.getIconWidth(), 
                                                  alert.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        questionURL = getClass().getResource(imagePath + "question.gif");
        question    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(questionURL)
                               .getScaledInstance(alert.getIconWidth(), 
                                                  alert.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        errorURL = getClass().getResource(imagePath + "error.gif");
        error    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(errorURL)
                               .getScaledInstance(alert.getIconWidth(), 
                                                  alert.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        tipURL = getClass().getResource(imagePath + "tipOfTheDay.gif");
        tip    = new ImageIcon(tipURL);
        
        closeWindowURL = getClass().getResource(imagePath + "closeWindow.png");
        closeWindow    = new ImageIcon(closeWindowURL);
        
        tasksURL = getClass().getResource(imagePath + "tasks.gif");
        tasks    = new ImageIcon(tasksURL);
        
        editorURL = getClass().getResource(imagePath + "editor.gif");
        editor    = new ImageIcon(editorURL);
        
        moleculeURL = getClass().getResource(imagePath + "molecule.gif");
        molecule    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(moleculeURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        ringURL = getClass().getResource(imagePath + "ring.png");
        ring    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(ringURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        planarRingURL = getClass().getResource(imagePath + "planarRing.png");
        planarRing    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(planarRingURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        nonPlanarRingURL = getClass().getResource(imagePath 
                                                  + "nonPlanarRing.png");
        nonPlanarRing    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(nonPlanarRingURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        straightChainURL = getClass().getResource(imagePath  
                                                  + "straightChain.png");
        straightChain    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(straightChainURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        scriptFolderURL = getClass().getResource(imagePath 
                                                  + "scriptFolder.png");
        scriptFolder    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(scriptFolderURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        atomGroupURL = getClass().getResource(imagePath + "atomGroup.png");
        atomGroup    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(atomGroupURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        fragmentsURL = getClass().getResource(imagePath + "fragments.png");
        fragments    = new ImageIcon(fragmentsURL);
        
        fragmentURL = getClass().getResource(imagePath + "fragment.png");
        fragment    = new ImageIcon(Toolkit.getDefaultToolkit()
                               .getImage(fragmentURL)
                               .getScaledInstance(atoms.getIconWidth(), 
                                                  atoms.getIconHeight(), 
                                                  Image.SCALE_SMOOTH));
        
        fragmentationSchemesURL = getClass().getResource(imagePath 
                                                  + "fragmentationSchemes.png");
        fragmentationSchemes    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(fragmentationSchemesURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        fragmentationSchemeURL = getClass().getResource(imagePath 
                                                  + "fragmentationScheme.png");
        fragmentationScheme    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(fragmentationSchemeURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));

        mainFragmentsURL = getClass().getResource(imagePath 
                                                  + "main.png");
        mainFragments    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(mainFragmentsURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));

        overlapFragmentsURL = getClass().getResource(imagePath 
                                                  + "overlap.png");
        overlapFragments    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(overlapFragmentsURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        correctorConstraintsURL = getClass().getResource(imagePath 
                                                  + "correctorConstraints.gif");
        correctorConstraints    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(correctorConstraintsURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        correctorURL = getClass().getResource(imagePath + "corrector.png");
        corrector    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(correctorURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        goodnessProbesURL = getClass().getResource(imagePath 
                                                  + "goodnessProbes.gif");
        goodnessProbes    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(goodnessProbesURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        goodnessProbeURL = getClass().getResource(imagePath 
                                                  + "goodnessProbe.png");
        goodnessProbe    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(goodnessProbeURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        rssURL = getClass().getResource(imagePath + "rss.png");
        rss    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(rssURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        goURL = getClass().getResource(imagePath + "go.png");
        go    = new ImageIcon(goURL);
        
        optionsURL = getClass().getResource(imagePath + "options.png");
        options    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(optionsURL)
                                      .getScaledInstance(atoms.getIconWidth(), 
                                                         atoms.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        jobUpdateURL = getClass().getResource(imagePath + "jobUpdate.png");
        jobUpdate    = new ImageIcon(jobUpdateURL);
        
        jobItemURL = getClass().getResource(imagePath + "jobItem.png");
        jobItem    = new ImageIcon(jobItemURL);
        
        movieModeURL = getClass().getResource(imagePath + "movieMode.png");
        movieMode    = new ImageIcon(Toolkit.getDefaultToolkit()
                                      .getImage(movieModeURL)
                                      .getScaledInstance(zoom.getIconWidth(), 
                                                         zoom.getIconHeight(), 
                                                         Image.SCALE_SMOOTH));
        
        playURL = getClass().getResource(imagePath + "play.png");
        play    = new ImageIcon(playURL);
        
        pauseURL = getClass().getResource(imagePath + "pause.png");
        pause    = new ImageIcon(pauseURL);
        
        stopURL = getClass().getResource(imagePath + "stop.png");
        stop    = new ImageIcon(stopURL);                
                
        // the initial image maps
        theKeys = new Vector<String>();
        setDefaultParams();                
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() {
        StringResource strings   = StringResource.getInstance();
        resources = ResourceBundle.getBundle(strings.getImageMapResource());
        
        Enumeration<String> implKeys = resources.getKeys();        
                
        while(implKeys.hasMoreElements()) {            
            theKeys.add(implKeys.nextElement());
        }
    }
    
    /**
     * method returns an image map for a file type passed as argument
     * 
     * @param file - the instance of file object whose associated image icon
     *        will be returned if available
     * @return ImageIcon for the specified file type if available.
     */
    public ImageIcon getIconFor(File file) {
        if (file.isDirectory()) return null;  // dont look at dir
        
        String extension = file.getName();
        
        if (extension.lastIndexOf('.') == -1) return null; // no extension
        
        // find the extension
        extension = extension.substring(extension.lastIndexOf('.')+1, 
                                        extension.length()).toLowerCase();
        
        // try to check if that is available in the key list
        if (theKeys.contains(extension)) {
            return new ImageIcon(getClass().getResource(imagePath 
                                            + resources.getString(extension)));
        } else {
            // no icon map found
            return null;
        } // end if
    }
    
    /**
     * method returns an image map for an atom type passed as argument
     * 
     * @param symbol - the atomic symbol of the atom.
     *
     * @return ImageIcon for the specified atom type if available, 
     *         else a default image is returned
     */
    public ImageIcon getAtomIconFor(String symbol) {        
        // try to check if that is available in the key list
        if (theKeys.contains(symbol)) {            
            return new ImageIcon(getClass().getResource(imagePath 
                                            + resources.getString(symbol)));
        } else {
            // no icon map found .. return the default
            return new ImageIcon(getClass().getResource(imagePath 
                                        + resources.getString("defaultAtom")));
        } // end if
    }
    
    /**
     * method to return instance of this object.
     *
     * @return ImageResource a single global instance of this class
     */
    public static ImageResource getInstance() {
        if (_imageResource == null) {
            _imageResource = new ImageResource();            
        } // end if
        
        return _imageResource;
    }
    
    /**
     * the current version!
     */    
    @Override
    public String getVersion() {
        return StringResource.getInstance().getVersion();
    }
    
    /** Getter for property imagePath.
     * @return Value of property imagePath.
     *
     */
    public String getImagePath() {
        return this.imagePath;
    }
    
    /** Getter for property ideSplash.
     * @return Value of property ideSplash.
     *
     */
    public Image getIdeSplash() {
        return this.ideSplash;
    }
    
    /** Getter for property waterMark.
     * @return Value of property waterMark.
     *
     */
    public Image getWaterMark() {
        return this.waterMark;
    }
    
    /** Getter for property ideSplashURL.
     * @return Value of property ideSplashURL.
     *
     */
    public URL getIdeSplashURL() {
        return this.ideSplashURL;
    }
    
    /** Getter for property waterMarkURL.
     * @return Value of property waterMarkURL.
     *
     */
    public URL getWaterMarkURL() {
        return this.waterMarkURL;
    }
    
    /** Getter for property about.
     * @return Value of property about.
     *
     */
    public ImageIcon getAbout() {
        return this.about;
    }
    
    /** Getter for property aboutURL.
     * @return Value of property aboutURL.
     *
     */
    public URL getAboutURL() {
        return this.aboutURL;
    }
    
    /** Getter for property newWorkspace.
     * @return Value of property newWorkspace.
     *
     */
    public ImageIcon getNewWorkspace() {
        return this.newWorkspace;
    }
    
    /** Getter for property newWorkspaceURL.
     * @return Value of property newWorkspaceURL.
     *
     */
    public URL getNewWorkspaceURL() {
        return this.newWorkspaceURL;
    }
    
    /** Getter for property saveWorkspace.
     * @return Value of property saveWorkspace.
     *
     */
    public ImageIcon getSaveWorkspace() {
        return this.saveWorkspace;
    }
    
    /** Getter for property saveWorkspaceURL.
     * @return Value of property saveWorkspaceURL.
     *
     */
    public URL getSaveWorkspaceURL() {
        return this.saveWorkspaceURL;
    }
    
    /** Getter for property openWorkspace.
     * @return Value of property openWorkspace.
     *
     */
    public ImageIcon getOpenWorkspace() {
        return this.openWorkspace;
    }
    
    /** Getter for property openWorkspaceURL.
     * @return Value of property openWorkspaceURL.
     *
     */
    public URL getOpenWorkspaceURL() {
        return this.openWorkspaceURL;
    }
    
    /** Getter for property iconURL.
     * @return Value of property iconURL.
     *
     */
    public URL getIconURL() {
        return this.iconURL;
    }
    
    /** Getter for property icon.
     * @return Value of property icon.
     *
     */
    public ImageIcon getIcon() {
        return this.icon;
    }
    
    /** Getter for property explorerURL.
     * @return Value of property explorerURL.
     *
     */
    public URL getExplorerURL() {
        return this.explorerURL;
    }
    
    /** Getter for property explorer.
     * @return Value of property explorer.
     *
     */
    public ImageIcon getExplorer() {
        return this.explorer;
    }
    
    /** Getter for property help.
     * @return Value of property help.
     *
     */
    public ImageIcon getHelp() {
        return this.help;
    }
    
    /** Getter for property helpURL.
     * @return Value of property helpURL.
     *
     */
    public URL getHelpURL() {
        return this.helpURL;
    }
    
    /** Getter for property cut.
     * @return Value of property cut.
     *
     */
    public ImageIcon getCut() {
        return this.cut;
    }
    
    /** Getter for property cutURL.
     * @return Value of property cutURL.
     *
     */
    public URL getCutURL() {
        return this.cutURL;
    }
    
    /** Getter for property copy.
     * @return Value of property copy.
     *
     */
    public ImageIcon getCopy() {
        return this.copy;
    }
    
    /** Getter for property copyURL.
     * @return Value of property copyURL.
     *
     */
    public URL getCopyURL() {
        return this.copyURL;
    }
    
    /** Getter for property paste.
     * @return Value of property paste.
     *
     */
    public ImageIcon getPaste() {
        return this.paste;
    }
    
    /** Getter for property pasteURL.
     * @return Value of property pasteURL.
     *
     */
    public URL getPasteURL() {
        return this.pasteURL;
    }
    
    /** Getter for property watch.
     * @return Value of property watch.
     *
     */
    public ImageIcon getWatch() {
        return this.watch;
    }
    
    /** Getter for property watchURL.
     * @return Value of property watchURL.
     *
     */
    public URL getWatchURL() {
        return this.watchURL;
    }
    
    /** Getter for property logWindow.
     * @return Value of property logWindow.
     *
     */
    public ImageIcon getLogWindow() {
        return this.logWindow;
    }
    
    /** Getter for property logWindowURL.
     * @return Value of property logWindowURL.
     *
     */
    public URL getLogWindowURL() {
        return this.logWindowURL;
    }
    
    /** Getter for property workspace.
     * @return Value of property workspace.
     *
     */
    public ImageIcon getWorkspace() {
        return this.workspace;
    }
    
    /** Getter for property workspaceURL.
     * @return Value of property workspaceURL.
     *
     */
    public URL getWorkspaceURL() {
        return this.workspaceURL;
    }
    
    /** Getter for property fileSystem.
     * @return Value of property fileSystem.
     *
     */
    public ImageIcon getFileSystem() {
        return this.fileSystem;
    }
    
    /** Getter for property fileSystemURL.
     * @return Value of property fileSystemURL.
     *
     */
    public URL getFileSystemURL() {
        return this.fileSystemURL;
    }
    
    /** Getter for property molecule.
     * @return Value of property molecule.
     *
     */
    public ImageIcon getMolecule() {
        return this.molecule;
    }
    
    /** Getter for property atoms.
     * @return Value of property atoms.
     *
     */
    public ImageIcon getAtoms() {
        return this.atoms;
    }
    
    /** Getter for property moleculeURL.
     * @return Value of property moleculeURL.
     *
     */
    public URL getMoleculeURL() {
        return this.moleculeURL;
    }
    
    /** Getter for property atomsURL.
     * @return Value of property atomsURL.
     *
     */
    public URL getAtomsURL() {
        return this.atomsURL;
    }
    
    /** Getter for property beany.
     * @return Value of property beany.
     *
     */
    public ImageIcon getBeany() {
        return this.beany;
    }
    
    /** Getter for property beanyURL.
     * @return Value of property beanyURL.
     *
     */
    public URL getBeanyURL() {
        return this.beanyURL;
    }
    
    /** Getter for property zoom.
     * @return Value of property zoom.
     *
     */
    public ImageIcon getZoom() {
        return this.zoom;
    }
    
    /** Getter for property zoomURL.
     * @return Value of property zoomURL.
     *
     */
    public URL getZoomURL() {
        return this.zoomURL;
    }
    
    /** Getter for property freehand.
     * @return Value of property freehand.
     *
     */
    public ImageIcon getFreehand() {
        return this.freehand;
    }
    
    /** Getter for property freehandURL.
     * @return Value of property freehandURL.
     *
     */
    public URL getFreehandURL() {
        return this.freehandURL;
    }
    
    /** Getter for property pencilToolURL.
     * @return Value of property pencilToolURL.
     *
     */
    public URL getPencilToolURL() {
        return this.pencilToolURL;
    }
    
    /** Getter for property pencilTool.
     * @return Value of property pencilTool.
     *
     */
    public ImageIcon getPencilTool() {
        return this.pencilTool;
    }

    /** Getter for property fullscreen.
     * @return Value of property fullscreen.
     *
     */
    public ImageIcon getFullscreen() {
        return this.fullscreen;
    }
    
    /** Getter for property fullscreenURL.
     * @return Value of property fullscreenURL.
     *
     */
    public URL getFullscreenURL() {
        return this.fullscreenURL;
    }
    
    /** Getter for property translate.
     * @return Value of property translate.
     *
     */
    public ImageIcon getTranslate() {
        return this.translate;
    }
    
    /** Getter for property translateURL.
     * @return Value of property translateURL.
     *
     */
    public URL getTranslateURL() {
        return this.translateURL;
    }
    
    /** Getter for property rotate.
     * @return Value of property rotate.
     *
     */
    public ImageIcon getRotate() {
        return this.rotate;
    }
    
    /** Getter for property rotateURL.
     * @return Value of property rotateURL.
     *
     */
    public URL getRotateURL() {
        return this.rotateURL;
    }
    
    /** Getter for property redo.
     * @return Value of property redo.
     *
     */
    public ImageIcon getRedo() {
        return this.redo;
    }
    
    /** Getter for property redoURL.
     * @return Value of property redoURL.
     *
     */
    public URL getRedoURL() {
        return this.redoURL;
    }
    
    /** Getter for property undo.
     * @return Value of property undo.
     *
     */
    public ImageIcon getUndo() {
        return this.undo;
    }
    
    /** Getter for property undoURL.
     * @return Value of property undoURL.
     *
     */
    public URL getUndoURL() {
        return this.undoURL;
    }
    
    /** Getter for property refresh.
     * @return Value of property refresh.
     *
     */
    public ImageIcon getRefresh() {
        return this.refresh;
    }
    
    /** Getter for property refreshURL.
     * @return Value of property refreshURL.
     *
     */
    public URL getRefreshURL() {
        return this.refreshURL;
    }
    
    /** Getter for property globalReset.
     * @return Value of property globalReset.
     *
     */
    public ImageIcon getGlobalReset() {
        return this.globalReset;
    }
    
    /** Getter for property globalResetURL.
     * @return Value of property globalResetURL.
     *
     */
    public URL getGlobalResetURL() {
        return this.globalResetURL;
    }
    
    /** Getter for property localReset.
     * @return Value of property localReset.
     *
     */
    public ImageIcon getLocalReset() {
        return this.localReset;
    }
    
    /** Getter for property localResetURL.
     * @return Value of property localResetURL.
     *
     */
    public URL getLocalResetURL() {
        return this.localResetURL;
    }
    
    /** Getter for property pointer.
     * @return Value of property pointer.
     *
     */
    public ImageIcon getPointer() {
        return this.pointer;
    }
    
    /** Getter for property pointerURL.
     * @return Value of property pointerURL.
     *
     */
    public URL getPointerURL() {
        return this.pointerURL;
    }
    
    /** Getter for property moleculeViewer.
     * @return Value of property moleculeViewer.
     *
     */
    public ImageIcon getMoleculeViewer() {
        return this.moleculeViewer;
    }
    
    /** Getter for property moleculeViewerURL.
     * @return Value of property moleculeViewerURL.
     *
     */
    public URL getMoleculeViewerURL() {
        return this.moleculeViewerURL;
    }
    
    /** Getter for property expand.
     * @return Value of property expand.
     *
     */
    public ImageIcon getExpand() {
        return this.expand;
    }
    
    /** Getter for property expandURL.
     * @return Value of property expandURL.
     *
     */
    public URL getExpandURL() {
        return this.expandURL;
    }
    
    /** Getter for property shrink.
     * @return Value of property shrink.
     *
     */
    public ImageIcon getShrink() {
        return this.shrink;
    }
    
    /** Getter for property shrinkURL.
     * @return Value of property shrinkURL.
     *
     */
    public URL getShrinkURL() {
        return this.shrinkURL;
    }
    
    /** Getter for property tasks.
     * @return Value of property tasks.
     *
     */
    public ImageIcon getTasks() {
        return this.tasks;
    }
    
    /** Getter for property tasksURL.
     * @return Value of property tasksURL.
     *
     */
    public URL getTasksURL() {
        return this.tasksURL;
    }
    
    /** Getter for property render.
     * @return Value of property render.
     *
     */
    public ImageIcon getRender() {
        return this.render;
    }
    
    /** Getter for property renderURL.
     * @return Value of property renderURL.
     *
     */
    public URL getRenderURL() {
        return this.renderURL;
    }
    
    /** Getter for property closeWindow.
     * @return Value of property closeWindow.
     *
     */
    public ImageIcon getCloseWindow() {
        return this.closeWindow;
    }
    
    /** Getter for property closeWindowURL.
     * @return Value of property closeWindowURL.
     *
     */
    public URL getCloseWindowURL() {
        return this.closeWindowURL;
    }
    
    /** Getter for property alert.
     * @return Value of property alert.
     *
     */
    public ImageIcon getAlert() {
        return this.alert;
    }
    
    /** Getter for property alertURL.
     * @return Value of property alertURL.
     *
     */
    public URL getAlertURL() {
        return this.alertURL;
    }
    
    /**
     * Getter for property editor.
     * @return Value of property editor.
     */
    public ImageIcon getEditor() {
        return this.editor;
    }
    
    /**
     * Getter for property editorURL.
     * @return Value of property editorURL.
     */
    public URL getEditorURL() {
        return this.editorURL;
    }
    
    /**
     * Getter for property ring.
     * @return Value of property ring.
     */
    public ImageIcon getRing() {
        return this.ring;
    }
    
    /**
     * Getter for property ringURL.
     * @return Value of property ringURL.
     */
    public URL getRingURL() {
        return this.ringURL;
    }
    
    /**
     * Getter for property planarRing.
     * @return Value of property planarRing.
     */
    public ImageIcon getPlanarRing() {
        return this.planarRing;
    }
    
    /**
     * Getter for property planarRingURL.
     * @return Value of property planarRingURL.
     */
    public URL getPlanarRingURL() {
        return this.planarRingURL;
    }
    
    /**
     * Getter for property nonPlanarRing.
     * @return Value of property nonPlanarRing.
     */
    public ImageIcon getNonPlanarRing() {
        return this.nonPlanarRing;
    }
    
    /**
     * Getter for property nonPlanarRingURL.
     * @return Value of property nonPlanarRingURL.
     */
    public URL getNonPlanarRingURL() {
        return this.nonPlanarRingURL;
    }
    
    /**
     * Getter for property informURL.
     * @return Value of property informURL.
     */
    public URL getInformURL() {
        return this.informURL;
    }
    
    /**
     * Getter for property inform.
     * @return Value of property inform.
     */
    public ImageIcon getInform() {
        return this.inform;
    }
    
    /**
     * Getter for property warnURL.
     * @return Value of property warnURL.
     */
    public URL getWarnURL() {
        return this.warnURL;
    }
    
    /**
     * Getter for property warn.
     * @return Value of property warn.
     */
    public ImageIcon getWarn() {
        return this.warn;
    }
    
    /**
     * Getter for property questionURL.
     * @return Value of property questionURL.
     */
    public URL getQuestionURL() {
        return this.questionURL;
    }
    
    /**
     * Getter for property question.
     * @return Value of property question.
     */
    public ImageIcon getQuestion() {
        return this.question;
    }
    
    /**
     * Getter for property errorURL.
     * @return Value of property errorURL.
     */
    public URL getErrorURL() {
        return this.errorURL;
    }
    
    /**
     * Getter for property error.
     * @return Value of property error.
     */
    public ImageIcon getError() {
        return this.error;
    }
    
    /**
     * Getter for property scriptFolder.
     * @return Value of property scriptFolder.
     */
    public ImageIcon getScriptFolder() {
        return this.scriptFolder;
    }
    
    /**
     * Getter for property scriptFolderURL.
     * @return Value of property scriptFolderURL.
     */
    public URL getScriptFolderURL() {
        return this.scriptFolderURL;
    }
    
    /**
     * Getter for property cube.
     * @return Value of property cube.
     */
    public ImageIcon getCube() {
        return this.cube;
    }
    
    /**
     * Getter for property cubeURL.
     * @return Value of property cubeURL.
     */
    public URL getCubeURL() {
        return this.cubeURL;
    }
    
    /**
     * Getter for property sphere.
     * @return Value of property sphere.
     */
    public ImageIcon getSphere() {
        return this.sphere;
    }
    
    /**
     * Getter for property sphereURL.
     * @return Value of property sphereURL.
     */
    public URL getSphereURL() {
        return this.sphereURL;
    }
    
    /**
     * Getter for property atomGroup.
     * @return Value of property atomGroup.
     */
    public ImageIcon getAtomGroup() {
        return this.atomGroup;
    }
    
    /**
     * Getter for property atomGroupURL.
     * @return Value of property atomGroupURL.
     */
    public URL getAtomGroupURL() {
        return this.atomGroupURL;
    }
    
    /**
     * Getter for property fragments.
     * @return Value of property fragments.
     */
    public ImageIcon getFragments() {
        return this.fragments;
    }
    
    /**
     * Getter for property fragmentsURL.
     * @return Value of property fragmentsURL.
     */
    public URL getFragmentsURL() {
        return this.fragmentsURL;
    }
    
    /**
     * Getter for property fragment.
     * @return Value of property fragment.
     */
    public ImageIcon getFragment() {
        return this.fragment;
    }
    
    /**
     * Getter for property fragmentURL.
     * @return Value of property fragmentURL.
     */
    public URL getFragmentURL() {
        return this.fragmentURL;
    }
    
    /**
     * Getter for property correctorConstraints.
     * @return Value of property correctorConstraints.
     */
    public ImageIcon getCorrectorConstraints() {
        return this.correctorConstraints;
    }
    
    /**
     * Getter for property correctorConstraintsURL.
     * @return Value of property correctorConstraintsURL.
     */
    public URL getCorrectorConstraintsURL() {
        return this.correctorConstraintsURL;
    }
    
    /**
     * Getter for property corrector.
     * @return Value of property corrector.
     */
    public ImageIcon getCorrector() {
        return this.corrector;
    }
    
    /**
     * Getter for property correctorURL.
     * @return Value of property correctorURL.
     */
    public URL getCorrectorURL() {
        return this.correctorURL;
    }
    
    /**
     * Getter for property goodnessProbes.
     * @return Value of property goodnessProbes.
     */
    public ImageIcon getGoodnessProbes() {
        return this.goodnessProbes;
    }
    
    /**
     * Getter for property goodnessProbesURL.
     * @return Value of property goodnessProbesURL.
     */
    public URL getGoodnessProbesURL() {
        return this.goodnessProbesURL;
    }
    
    /**
     * Getter for property goodnessProbe.
     * @return Value of property goodnessProbe.
     */
    public ImageIcon getGoodnessProbe() {
        return this.goodnessProbe;
    }
    
    /**
     * Getter for property goodnessProbeURL.
     * @return Value of property goodnessProbeURL.
     */
    public URL getGoodnessProbeURL() {
        return this.goodnessProbeURL;
    }
    
    /**
     * Getter for property fragmentationScheme.
     * @return Value of property fragmentationScheme.
     */
    public ImageIcon getFragmentationSchemes() {
        return this.fragmentationSchemes;
    }
    
    /**
     * Getter for property fragmentationSchemeURL.
     * @return Value of property fragmentationSchemeURL.
     */
    public URL getFragmentationSchemesURL() {
        return this.fragmentationSchemesURL;
    }
    
    /**
     * Getter for property fragmentationScheme.
     * @return Value of property fragmentationScheme.
     */
    public ImageIcon getFragmentationScheme() {
        return this.fragmentationScheme;
    }
    
    /**
     * Getter for property fragmentationSchemeURL.
     * @return Value of property fragmentationSchemeURL.
     */
    public URL getFragmentationSchemeURL() {
        return this.fragmentationSchemeURL;
    }
    
    /**
     * Getter for property rss.
     * @return Value of property rss.
     */
    public ImageIcon getRss() {
        return this.rss;
    }
    
    /**
     * Getter for property rssURL.
     * @return Value of property rssURL.
     */
    public URL getRssURL() {
        return this.rssURL;
    }
    
    /**
     * Getter for property options.
     * @return Value of property options.
     */
    public ImageIcon getOptions() {
        return this.options;
    }
    
    /**
     * Getter for property optionsURL.
     * @return Value of property optionsURL.
     */
    public URL getOptionsURL() {
        return this.optionsURL;
    }
    
    /**
     * Getter for property go.
     * @return Value of property go.
     */
    public ImageIcon getGo() {
        return this.go;
    }
    
    /**
     * Getter for property goURL.
     * @return Value of property goURL.
     */
    public URL getGoURL() {
        return this.goURL;
    }
    
    /**
     * Getter for property jobUpdate.
     * @return Value of property jobUpdate.
     */
    public ImageIcon getJobUpdate() {
        return this.jobUpdate;
    }
    
    /**
     * Getter for property jobUpdateURL.
     * @return Value of property jobUpdateURL.
     */
    public URL getJobUpdateURL() {
        return this.jobUpdateURL;
    }
    
    /**
     * Getter for property jobItem.
     * @return Value of property jobItem.
     */
    public ImageIcon getJobItem() {
        return this.jobItem;
    }
    
    /**
     * Getter for property jobItemURL.
     * @return Value of property jobItemURL.
     */
    public URL getJobItemURL() {
        return this.jobItemURL;
    }
    
    /**
     * Getter for property movieMode.
     * @return Value of property movieMode.
     */
    public ImageIcon getMovieMode() {
        return this.movieMode;
    }
    
    /**
     * Getter for property movieModeURL.
     * @return Value of property movieModeURL.
     */
    public URL getMovieModeURL() {
        return this.movieModeURL;
    }
    
    /**
     * Getter for property play.
     * @return Value of property play.
     */
    public ImageIcon getPlay() {
        return this.play;
    }
    
    /**
     * Getter for property playURL.
     * @return Value of property playURL.
     */
    public URL getPlayURL() {
        return this.playURL;
    }
    
    /**
     * Getter for property pause.
     * @return Value of property pause.
     */
    public ImageIcon getPause() {
        return this.pause;
    }
    
    /**
     * Getter for property pauseURL.
     * @return Value of property pauseURL.
     */
    public URL getPauseURL() {
        return this.pauseURL;
    }
    
    /**
     * Getter for property stop.
     * @return Value of property stop.
     */
    public ImageIcon getStop() {
        return this.stop;
    }
    
    /**
     * Getter for property stopURL.
     * @return Value of property stopURL.
     */
    public URL getStopURL() {
        return this.stopURL;
    }

    /**
     * Holds value of property surface.
     */
    private ImageIcon surface;

    /**
     * Getter for property surface.
     * @return Value of property surface.
     */
    public ImageIcon getSurface() {
        return this.surface;
    }

    /**
     * Holds value of property surfaceURL.
     */
    private URL surfaceURL;

    /**
     * Getter for property surfaceURL.
     * @return Value of property surfaceURL.
     */
    public URL getSurfaceURL() {
        return this.surfaceURL;
    }

    /**
     * Holds value of property jrManURL.
     */
    private URL jrManURL;

    /**
     * Getter for property jrManURL.
     * @return Value of property jrManURL.
     */
    public URL getJrManURL() {
        return this.jrManURL;
    }

    /**
     * Holds value of property jrMan.
     */
    private ImageIcon jrMan;

    /**
     * Getter for property jrMan.
     * @return Value of property jrMan.
     */
    public ImageIcon getJrMan() {
        return this.jrMan;
    }

    /**
     * Holds value of property scratchPad.
     */
    private ImageIcon scratchPad;

    /**
     * Getter for property scratchPad.
     * @return Value of property scratchPad.
     */
    public ImageIcon getScratchPad() {
        return this.scratchPad;
    }

    /**
     * Holds value of property scratchPadURL.
     */
    private URL scratchPadURL;

    /**
     * Getter for property scratchPadURL.
     * @return Value of property scratchPadURL.
     */
    public URL getScratchPadURL() {
        return this.scratchPadURL;
    }

    /**
     * Holds value of property mainFragments.
     */
    private ImageIcon mainFragments;

    /**
     * Getter for property mainFragments.
     * @return Value of property mainFragments.
     */
    public ImageIcon getMainFragments() {
        return this.mainFragments;
    }

    /**
     * Holds value of property mainFragmentsURL.
     */
    private URL mainFragmentsURL;

    /**
     * Getter for property mainFragmentsURL.
     * @return Value of property mainFragmentsURL.
     */
    public URL getMainFragmentsURL() {
        return this.mainFragmentsURL;
    }

    /**
     * Holds value of property overlapFragments.
     */
    private ImageIcon overlapFragments;

    /**
     * Getter for property overlapFragments.
     * @return Value of property overlapFragments.
     */
    public ImageIcon getOverlapFragments() {
        return this.overlapFragments;
    }

    /**
     * Holds value of property overlapFragmentsURL.
     */
    private URL overlapFragmentsURL;

    /**
     * Getter for property overlapFragmentsURL.
     * @return Value of property overlapFragmentsURL.
     */
    public URL getOverlapFragmentsURL() {
        return this.overlapFragmentsURL;
    }

    /**
     * Holds value of property makeCurrentView.
     */
    private ImageIcon makeCurrentView;

    /**
     * Getter for property makeCurrentView.
     * @return Value of property makeCurrentView.
     */
    public ImageIcon getMakeCurrentView() {
        return this.makeCurrentView;
    }

    /**
     * Holds value of property makeCurrentViewURL.
     */
    private URL makeCurrentViewURL;

    /**
     * Getter for property makeCurrentViewURL.
     * @return Value of property makeCurrentViewURL.
     */
    public URL getMakeCurrentViewURL() {
        return this.makeCurrentViewURL;
    }

    /**
     * Holds value of property tip.
     */
    private ImageIcon tip;

    /**
     * Getter for property tip.
     * @return Value of property tip.
     */
    public ImageIcon getTip() {
        return this.tip;
    }

    /**
     * Holds value of property tipURL.
     */
    private URL tipURL;

    /**
     * Getter for property tipURL.
     * @return Value of property tipURL.
     */
    public URL getTipURL() {
        return this.tipURL;
    }
    
    /**
     * Holds value of property straightChain.
     */
    private ImageIcon straightChain;

    /**
     * Getter for property straightChain.
     * @return Value of property straightChain.
     */
    public ImageIcon getStraightChain() {
        return this.straightChain;
    }

    /**
     * Holds value of property moleculeEditorURL.
     */
    private URL moleculeEditorURL;

    /**
     * Getter for property moleculeEditorURL.
     * @return Value of property moleculeEditorURL.
     */
    public URL getMoleculeEditorURL() {
        return this.moleculeEditorURL;
    }
    
    /**
     * Holds value of property moleculeEditor.
     */
    private ImageIcon moleculeEditor;

    /**
     * Getter for property moleculeEditor.
     * @return Value of property moleculeEditor.
     */
    public ImageIcon getMoleculeEditor() {
        return this.moleculeEditor;
    }

    /**
     * Holds value of property straightChainURL.
     */
    private URL straightChainURL;

    /**
     * Getter for property straightChainURL.
     * @return Value of property straightChainURL.
     */
    public URL getStraightChainURL() {
        return this.straightChainURL;
    }
    
    /**
     * Getter for property removeAtom.
     * @return Value of property removeAtom.
     */
    public ImageIcon getRemoveAtom() {
        return this.removeAtom;
    }    

    /**
     * Getter for property removeAtomURL.
     * @return Value of property removeAtomURL.
     */
    public URL getRemoveAtomURL() {
        return this.removeAtomURL;
    }
    
    private URL talkURL;

    /**
     * Get the value of talkURL
     *
     * @return the value of talkURL
     */
    public URL getTalkURL() {
        return talkURL;
    }

    private ImageIcon talk;

    /**
     * Get the value of talk
     *
     * @return the value of talk
     */
    public ImageIcon getTalk() {
        return talk;
    }
    
    private URL voiceURL;

    /**
     * Get the value of voiceURL
     *
     * @return the value of voiceURL
     */
    public URL getVoiceURL() {
        return voiceURL;
    }

    private ImageIcon voice;

    /**
     * Get the value of voice
     *
     * @return the value of voice
     */
    public ImageIcon getVoice() {
        return voice;
    }
    
    private URL novoiceURL;

    /**
     * Get the value of novoiceURL
     *
     * @return the value of novoiceURL
     */
    public URL getNovoiceURL() {
        return novoiceURL;
    }

    private ImageIcon novoice;

    /**
     * Get the value of novoice
     *
     * @return the value of novoice
     */
    public ImageIcon getNovoice() {
        return novoice;
    }
} // end of class ImageResource
