/*
 * MoleculeViewerFrame.java
 *
 * Created on December 31, 2003, 6:49 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import java.net.InetAddress;
import javax.swing.*;
import javax.swing.event.*;

import java.text.DecimalFormat;
import org.meta.math.geom.BoundingBox;
import org.meta.common.ColorToFunctionRangeMap;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.common.resource.CursorResource;
import org.meta.common.resource.ImageResource;
import org.meta.fragment.Fragment;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.molecule.Atom;
import org.meta.molecule.CommonUserDefinedMolecularPropertyNames;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;

import org.meta.net.FederationRequest;
import org.meta.net.impl.consumer.FederationServiceObjectPushConsumer;
import org.meta.net.impl.service.talk.MoleculeTalkObject;
import org.meta.parallel.SimpleAsyncTask;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.event.MultiMoleculeViewChangeEvent;
import org.meta.shell.idebeans.event.MultiMoleculeViewChangeListerner;
import org.meta.shell.idebeans.propertysheet.IDEPropertySheetUI;
import org.meta.shell.idebeans.talk.IDETalkUser;
import org.meta.shell.idebeans.talk.IntegratedIDETalkUI;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.SelectionState;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.TransformState;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.*;

/**
 * The container for MoleculeViewer acting as an interface with MeTA Studio
 * desktop environment.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 * @since 2.0.09022009 Josh Milthorpe (fixed spelling of "property")
 */
public class MoleculeViewerFrame extends JInternalFrame 
                                 implements UndoableEditListener,
                                            MoleculeSceneChangeListener,
                                            MultiMoleculeViewChangeListerner,
                                            PropertyInterrogataionListener,
                                            DockableFrame {
    
    /** the local tool bar */
    private JToolBar toolBar;
    private MovieModeToolBar movieModeToolBar;
    private JToggleButton zoom, translate, rotate;    
    private JComboBox selectionTool;
    private JToggleButton fullscreen, movieMode;
    private JButton undo, redo, globalReset;
    private JToggleButton propertyPanelButton;
    private ButtonGroup toolMode;
    private JButton moreOptions;
    private JPopupMenu moreOptionsPopup;
    private JCheckBoxMenuItem showMultipleBonds, showWeakBonds,
                              showSpecialStructures, readDeep;
    private JMenuItem shareThis;
    private MemoryMonitor memoryMonitor;
    
    /** the instance of MoleculeViewer */
    private MoleculeViewer moleculeViewer;
    
    /** multiple views of the same molecule */
    private MultiMoleculeViewPanel multiView;
    
    /** IDE related instances */
    private MeTA ideInstance;
    
    private CursorResource cursors;
    
    /** for full screen activity */
    private GraphicsDevice device; 
    private JFrame theFullScreen;
    private JDesktopPane screenPane;
    private Container originalParent;
    
    /** the side panel */
    private SidePanel cuboidSidePanel, sphereSidePanel;
    private JSpinner cuboidAtomCenter, cuboidLengthX, cuboidLengthY, 
                     cuboidLengthZ,
                     cuboidCenterX, cuboidCenterY, cuboidCenterZ,
                     sphereCenterX, sphereCenterY, sphereCenterZ,
                     sphereAtomCenter, sphereRadius;
    private SpinnerNumberModel sphereRadiusModel, 
                               centerXModel, centerYModel, centerZModel,
                               atomCenterModel, cuboidLengthXModel,
                               cuboidLengthYModel, cuboidLengthZModel;
    private JRadioButton cuboidAtomCenterOption, cuboidCenterOption,
                         sphereAtomCenterOption, sphereCenterOption;
    private JLabel cuboidInfo;
    private JLabel sphereInfo;
    private ButtonGroup sphereGroup, cuboidGroup;    
    protected JPanel sidePanel;
    
    /** side panel for property controls */
    private SidePanel propertySidePanel;
    private JButton manageProperties, additionalSettings;
    private JLabel currentPropertyLabel, currentFunctionValueLabel,
                   colorMapLabel, minRangeLabel, maxRangeLabel;
    private JComboBox currentProperty;
    private JSpinner currentFunctionValue, minRange, maxRange;

    private JButton colorMapButton;
    private JCheckBox showProperty, showPropertyBox, interrogateMode;
    private JPanel propertySettingsPanel, managePropertiesPanel;
    private SpinnerNumberModel currentFunctionValueModel, minRangeModel,
                               maxRangeModel;
    
    /** side panel for goodness probes */
    private SidePanel goodnessSidePanel;
    private JLabel fragSchemeLabel, goodnessThresholdLabel, offendingAtomsLabel;
    private JComboBox fragScheme;
    private JSpinner goodnessThreshold;
    private JLabel goodnessIndicatorLabel;
    private GoodnessIndicator indicator;
    private JList offendingAtoms;
    private JCheckBox excludePendantAtoms;
    
    /** side panel for grid generator */
    private SidePanel gridGeneratorSidePanel;    
    private JSpinner gridLengthX, gridLengthY, gridLengthZ;    
    private JRadioButton equalLength, variableLength;
    private JSpinner pointsX, pointsY, pointsZ;
    private JRadioButton equalPoints, variablePoints;
    private JSpinner gridCenterX, gridCenterY, gridCenterZ;
    private JButton defaultCenter;
    private SpinnerNumberModel gridCenterXModel, gridCenterYModel, 
                               gridCenterZModel,
                               gridLengthXModel, gridLengthYModel, 
                               gridLengthZModel,
                               pointsXModel, pointsYModel, pointsZModel;
    private JLabel gridGeneratorLabel;
    private JButton logRangeDetails, generateSubProperty;
    
    /** the cool find tool */
    private MoleculeViewerFindTool moleculeViewerFindTool;
    
    private int selfMoleculeViewerCount;
    private static int moleculeViewerCount = 1;

    private ManagePropertiesDialog managePropertiesDialog;

    /** maximum extent of a grid from molecular bounding box */
    private static final double MAX_GRID_EXTENT = 100.0; // 100 angstroms
    
    /** maximum number of grid points along an axis */
    private static final int MAX_GRID_POINTS = 1000000;
    
    /** default number of grid points along an axis */
    private static final int DEFAULT_GRID_POINTS = 100;
    
    private JScrollPane multiViewScrollPane;
    
    protected JSplitPane splitPane;
            
    /** Creates a new (empty) instance of MoleculeViewerFrame */
    protected MoleculeViewerFrame() { }
    
    /** Creates a new instance of MoleculeViewerFrame */
    public MoleculeViewerFrame(MeTA ideInstance) {
        super("Molecule Viewer - [" + moleculeViewerCount + "]",
               true, true, true, true); 
        
        selfMoleculeViewerCount = moleculeViewerCount;
        moleculeViewerCount++;
        
        this.showMoleculeRelatedUI = true;
        this.ideInstance = ideInstance;
    
        this.multiViewEnabled = false;
        
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = env.getScreenDevices();
        
        // we consider that there is only a single monitor device
        // so only handle devices[0] / a device that has full
        // screen support
        boolean isFullScreen = false;
        for (int i=0; i<devices.length; i++) {
            if (devices[i].isFullScreenSupported()) {
                device = devices[i];
                isFullScreen = true; break;
            } // end if
        } // end for
        
        if (!isFullScreen) device = devices[0];
        
        cursors = CursorResource.getInstance();
        
        initComponents();
                
        setVisible(true);
        
        theFullScreen = new JFrame(device.getDefaultConfiguration());
        screenPane = new JDesktopPane();                        
        
        theFullScreen.getContentPane().setLayout(new BorderLayout());
        theFullScreen.getContentPane().add(screenPane);
        theFullScreen.setIconImage(ideInstance.getIconImage());
        theFullScreen.setTitle(ideInstance.getTitle() + " - (Fullscreen mode)");
        
        // add undo listener .. the sequence is purposely changed!
        moleculeViewer.removeUndoableEditListener(moleculeViewer);
        moleculeViewer.addUndoableEditListener(this);
        moleculeViewer.addUndoableEditListener(moleculeViewer);
        
        // add interrogation listener
        moleculeViewer.addPropertyInterrogataionListener(this);
        
        // set initial state for molecular viewer
        moleculeViewer.setSelectionState(SelectionState.NO_STATE);
        moleculeViewer.setTransformState(TransformState.ROTATE_STATE);
               
        // show the task list
        MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                  .getTaskPanel().activate("Molecule related Tasks");
        MoleculeViewerFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeWorkflowDinamicVisibility(true);

        // frame listener .. for just resetting the status bar
        // and other UI
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                MoleculeViewerFrame.this.ideInstance.getStatusBar()
                                                    .setStatusText("Ready.");
                // hide the task list
                MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().deactivate("Molecule related Tasks");
                MoleculeViewerFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeWorkflowDinamicVisibility(false);

                // hide additional panel
                MoleculeViewerFrame.this.moleculeViewer
                          .showAdditionalInfoPanel(false);
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                // show the task list
                if (showMoleculeRelatedUI) {
                    MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                            .getTaskPanel().activate("Molecule related Tasks");
                    MoleculeViewerFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeWorkflowDinamicVisibility(true);
                } else {
                    MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                           .getTaskPanel().deactivate("Molecule related Tasks");
                    MoleculeViewerFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeWorkflowDinamicVisibility(false);
                } // end if
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                // hide the task list
                MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().deactivate("Molecule related Tasks");
                MoleculeViewerFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeWorkflowDinamicVisibility(false);
                MoleculeViewerFrame.this.getMoleculeViewer()
                                   .showAdditionalInfoPanel(false);
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                MoleculeViewerFrame.this.ideInstance.getStatusBar()
                                                    .setStatusText("Ready.");
                // hide the task list
                MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().deactivate("Molecule related Tasks");
                MoleculeViewerFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeWorkflowDinamicVisibility(false);

                MoleculeViewerFrame.this.getMoleculeViewer()
                                   .showAdditionalInfoPanel(false);
                
                // stop if any movie is going on
                MoleculeViewerFrame.this.getMoleculeViewer().endMovie();

                // remove the listeners
                Vector<MoleculeScene> sceneList = moleculeViewer.getSceneList();
                
                if (sceneList == null) return;
                
                Iterator<MoleculeScene> scenes = sceneList.iterator();
                
                while(scenes.hasNext()) {
                  scenes.next()
                   .removeMoleculeSceneChangeListener(MoleculeViewerFrame.this);
                } // end while
                
                moleculeViewer.removeUndoableEditListener(
                                                      MoleculeViewerFrame.this);
                
                // remove all elements to assist garbage collection
                MoleculeViewerFrame.this.removeAll();
                moleculeViewer.clearAllReferences();
                moleculeViewer         = null;               
                moleculeViewerFindTool = null;   
                
                if (cuboidSidePanel != null) {
                    cuboidSidePanel.removeAll();
                    cuboidSidePanel   = null;
                } // end if
                
                if (sphereSidePanel != null) {
                    sphereSidePanel.removeAll();
                    sphereSidePanel = null;
                } // end if
                
                if (goodnessSidePanel != null) {
                    goodnessSidePanel.removeAll();
                    goodnessSidePanel = null;
                } // end if
                
                if (gridGeneratorSidePanel != null) {
                    gridGeneratorSidePanel.removeAll();
                    gridGeneratorSidePanel = null;
                } // end if
                
                if (sidePanel != null) {
                    sidePanel.removeAll();
                    sidePanel = null;
                } // end if
                
                memoryMonitor.setStopUpdate(true);
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        
        // the viewer
        moleculeViewer = new MoleculeViewer(ideInstance, this);        
        
        // make the tool bar
        toolBar = new JToolBar();
        toolBar.setRollover(true);
        toolBar.setFloatable(true);        
        
        // the buttons
        final ImageResource images = ImageResource.getInstance();
        
        toolMode = new ButtonGroup();
        
        zoom = new JToggleButton(images.getZoom());
        zoom.setToolTipText("Zoom, scale up or down");
        zoom.setMnemonic(KeyEvent.VK_S);
        zoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               ideInstance.getStatusBar().setStatusText("Drag the mouse"
               + "/ mouse wheel up or down to zoom in or zoom out the object.");
               
               Object selectedItem = selectionTool.getSelectedItem();
               if (!selectedItem.equals(images.getCube())
                   && !selectedItem.equals(images.getSphere())) {
                selectionTool.setToolTipText("Choose a selection tool");
                selectionTool.setSelectedItem(images.getTip());
               } // end if
               
               moleculeViewer.setCursor(cursors.getZoomCursor());
               moleculeViewer.setTransformState(TransformState.SCALE_STATE);               
               ideInstance.getNotificationTray().notify(getTitle(), 
                                         TransformState.SCALE_STATE.toString());
            }
        });
        
        // the initial state                    
        toolMode.add(zoom);
        toolBar.add(zoom);
        
        translate = new JToggleButton(images.getTranslate());
        translate.setToolTipText("Translate the molecule center");
        translate.setMnemonic(KeyEvent.VK_T);
        translate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               ideInstance.getStatusBar().setStatusText("Drag the mouse "
                 + "to translate the object center along X-Y.");
               
               Object selectedItem = selectionTool.getSelectedItem();
               if (!selectedItem.equals(images.getCube())
                   && !selectedItem.equals(images.getSphere())) {
                selectionTool.setToolTipText("Choose a selection tool");
                selectionTool.setSelectedItem(images.getTip());
               } // end if
               
               moleculeViewer.setCursor(
                        Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
               moleculeViewer.setTransformState(TransformState.TRANSLATE_STATE);
               ideInstance.getNotificationTray().notify(getTitle(), 
                                     TransformState.TRANSLATE_STATE.toString());
            }
        }); 
        toolMode.add(translate);
        toolBar.add(translate);
        
        rotate = new JToggleButton(images.getRotate());
        rotate.setToolTipText("Rotate the molecule to get different views");
        rotate.setMnemonic(KeyEvent.VK_R);
        rotate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               ideInstance.getStatusBar().setStatusText("Drag the mouse "
                 + "to rotate the object along X-Y. "
                 + "Use Shift + Drag to rotate the object along Z.");
               
               Object selectedItem = selectionTool.getSelectedItem();
               if (!selectedItem.equals(images.getCube())
                   && !selectedItem.equals(images.getSphere())) {
                selectionTool.setToolTipText("Choose a selection tool");
                selectionTool.setSelectedItem(images.getTip());
               } // end if
               
               moleculeViewer.setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
               moleculeViewer.setTransformState(TransformState.ROTATE_STATE);
               ideInstance.getNotificationTray().notify(getTitle(), 
                                        TransformState.ROTATE_STATE.toString());
            }
        }); 
        
        ideInstance.getStatusBar().setStatusText("Drag the mouse "
                 + "to rotate the object along X-Y. "
                 + "Use Shift + Drag to rotate the object along Z.");
        moleculeViewer.setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moleculeViewer.setTransformState(TransformState.ROTATE_STATE);
        rotate.setSelected(true);
        toolMode.add(rotate);
        toolBar.add(rotate);
        
        toolBar.addSeparator();        
        
        // the selection tool
        // note: we purposely skip adding cube and sphere selection
        // here as these are added dynamically during adding or deleting 
        // molecules from the connected scene.
        // also the following 3 lines defines a "work around" for selecting
        // appropriate tool combinations.
        ButtonGroup toolMode2 = new ButtonGroup();
        final JToggleButton selectionToolButton = new JToggleButton();
        final JToggleButton selectionToolButton2 = new JToggleButton();
        
        toolMode.add(selectionToolButton); 
        toolMode2.add(selectionToolButton2);
        selectionTool = new JComboBox(new Object [] {
            images.getTip(),
            images.getPointer(),
            images.getFreehand(),              
        });
        
        selectionTool.setToolTipText("Choose a selection tool");
        selectionTool.setEditable(false);        
        selectionTool.setPrototypeDisplayValue(images.getPointer());
        selectionTool.setMaximumSize(rotate.getPreferredSize());
        selectionTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Object selectedItem = selectionTool.getSelectedItem();
                ImageResource images = ImageResource.getInstance(); 
                
                selectionToolButton.setSelected(true);
                selectionToolButton2.setSelected(true);
                
                if (selectedItem.equals(images.getPointer())) {
                    ideInstance.getStatusBar().setStatusText("Use mouse pointer"
                            + " to select individual atoms.");
                    selectionTool.setToolTipText("Choose a selection tool " +
                            "(Current tool is pointer selection)");
                    moleculeViewer.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    moleculeViewer.setSelectionState(
                            SelectionState.POINTER_SELECTION);
                    ideInstance.getNotificationTray().notify(getTitle(),
                            SelectionState.POINTER_SELECTION.toString());
                    sidePanel.removeAll();
                    
                    splitPane.setDividerLocation(1.0); updateUI();
                } else if (selectedItem.equals(images.getFreehand())) {
                    ideInstance.getStatusBar().setStatusText("Select multiple"
                            + " atoms. Hold Ctrl for non exclusive selection." 
                            + " Use the selection for captions, "
                            + "fragments etc...");
                    selectionTool.setToolTipText("Choose a selection tool " +
                            "(Current tool is free hand selection)");
                    moleculeViewer.setCursor(cursors.getPenCursor());
                    moleculeViewer.setSelectionState(
                            SelectionState.FREE_HAND_SELECTION);
                    ideInstance.getNotificationTray().notify(getTitle(),
                            SelectionState.FREE_HAND_SELECTION.toString());
                    sidePanel.removeAll();
                    
                    splitPane.setDividerLocation(1.0); updateUI();
                } else if (selectedItem.equals(images.getCube())) {
                    ideInstance.getStatusBar().setStatusText("Select multiple"
                            + " atoms with a cuboid centered on an atom or " 
                            + "arbitary point."
                            + " Use the selection for captions, " 
                            + "fragments etc...");
                    selectionTool.setToolTipText("Choose a selection tool " +
                            "(Current tool is cube selection)");
                    moleculeViewer.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    moleculeViewer.setSelectionState(
                            SelectionState.CUBOID_SELECTION);
                    ideInstance.getNotificationTray().notify(getTitle(),
                            SelectionState.CUBOID_SELECTION.toString());
                    sidePanel.removeAll();
                    initCuboidSidePanel();
                    // update spinner models
                    updateSpinnerModels();
                    sidePanel.add(cuboidSidePanel, BorderLayout.CENTER);
                    sidePanel.setVisible(true);
                    cuboidSidePanel.setVisible(true);
                    
                    splitPane.setDividerLocation(0.8); updateUI();
                } else if (selectedItem.equals(images.getSphere())) {
                    ideInstance.getStatusBar().setStatusText("Select multiple"
                            + " atoms with a sphere centered on an atom or " 
                            + "arbitary point."
                            + " Use the selection for captions, " 
                            + "fragments etc...");
                    selectionTool.setToolTipText("Choose a selection tool " +
                            "(Current tool is sphere selection)");
                    moleculeViewer.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    moleculeViewer.setSelectionState(
                            SelectionState.SPHERE_SELECTION);
                    ideInstance.getNotificationTray().notify(getTitle(),
                            SelectionState.SPHERE_SELECTION.toString());
                    sidePanel.removeAll();
                    initSphereSidePanel();
                    // update spinner models
                    updateSpinnerModels();
                    sidePanel.add(sphereSidePanel, BorderLayout.CENTER);
                    sidePanel.setVisible(true);
                    sphereSidePanel.setVisible(true);
                    
                    splitPane.setDividerLocation(0.8); updateUI();
                } else { 
                    selectionTool.setToolTipText("Choose a selection tool");
                    selectionTool.setSelectedItem(images.getTip());
                    
                    if (cuboidSidePanel != null) {
                        if (cuboidSidePanel.isVisible()) {
                            cuboidSidePanel.setVisible(false);
                            sidePanel.removeAll();
                            
                            splitPane.setDividerLocation(1.0); updateUI();
                        } // end if
                    } // end if
                    
                    if (sphereSidePanel != null) {
                        if (sphereSidePanel.isVisible()) {
                            sphereSidePanel.setVisible(false);
                            sidePanel.removeAll();
                            
                            splitPane.setDividerLocation(1.0); updateUI();
                        } // end if
                    } // end if
                    
                    moleculeViewer.setSelectionState(SelectionState.NO_STATE);
                    rotate.doClick();
                } // end if
            }
        });
        toolBar.add(selectionTool);
        
        toolBar.addSeparator();
        undo = new JButton(images.getUndo());
        undo.setToolTipText("Undo the last action");
        undo.setEnabled(false);
        ActionListener actionUndo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (moleculeViewer.canUndo()) {
                    moleculeViewer.undo();
                } // end if
                undo.setEnabled(moleculeViewer.canUndo());
                redo.setEnabled(moleculeViewer.canRedo());
            }
        };
        undo.addActionListener(actionUndo);
        // and add the accelerator
        KeyStroke keyStrokeUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
                                                     InputEvent.CTRL_MASK);
        undo.registerKeyboardAction(actionUndo, "undo", keyStrokeUndo, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        toolBar.add(undo);
        
        redo = new JButton(images.getRedo());
        redo.setToolTipText("Restore the last undo action");        
        redo.setEnabled(false);
        ActionListener actionRedo = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (moleculeViewer.canRedo()) {
                    moleculeViewer.redo();
                } // end if
                undo.setEnabled(moleculeViewer.canUndo());
                redo.setEnabled(moleculeViewer.canRedo());
            }
        };
        redo.addActionListener(actionRedo);
        // and add the accelerator
        KeyStroke keyStrokeRedo = KeyStroke.getKeyStroke(KeyEvent.VK_R, 
                                                     InputEvent.CTRL_MASK);
        redo.registerKeyboardAction(actionRedo, "redo", keyStrokeRedo, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        toolBar.add(redo);
        
        globalReset = new JButton(images.getGlobalReset());
        globalReset.setToolTipText("Reset the entire scene to its original"
                                   + " position");
        globalReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {               
               moleculeViewer.resetTransformState();
            }
        }); 
        toolBar.add(globalReset);
        
        toolBar.addSeparator();
        movieMode = new JToggleButton(images.getMovieMode());
        movieMode.setToolTipText("Enable the movie mode in case multiple" +
                                 " molecules are available in the viewer.");
        movieMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) { 
                if (movieModeToolBar == null) {
                    movieModeToolBar = new MovieModeToolBar(
                                                MoleculeViewerFrame.this);
                    // add this toolbar
                    getContentPane().add(movieModeToolBar,
                                         BorderLayout.SOUTH);
                    updateUI();
                } // end if
                
                if (movieMode.isSelected()) {                    
                    movieModeToolBar.setVisible(true);
                } else {
                    movieModeToolBar.setVisible(false);
                } // end if                                  
            }
        });
        movieMode.setEnabled(false);
        toolBar.add(movieMode);
        
        propertyPanelButton = new JToggleButton(images.getSurface());
        propertyPanelButton.setToolTipText("Show/ Hide property panel");
        toolMode2.add(propertyPanelButton);
        propertyPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) { 
                sidePanel.removeAll();
                initPropertySidePanel();
                sidePanel.add(propertySidePanel, BorderLayout.CENTER);
                sidePanel.setVisible(propertyPanelButton.isSelected());
                propertySidePanel.setVisible(propertyPanelButton.isSelected());
                
                splitPane.setDividerLocation(0.8); updateUI();
            }
        });
        toolBar.add(propertyPanelButton);
        
        toolBar.addSeparator();
        fullscreen = new JToggleButton(images.getFullscreen());
        if (device.isFullScreenSupported()) {
            fullscreen.setToolTipText("Fullscreen mode");
        } else {
            fullscreen.setToolTipText("Fullscreen mode not supported");
            fullscreen.setEnabled(false);
        } // end if
        fullscreen.setMnemonic(KeyEvent.VK_F11);
        fullscreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {  
                boolean isFullScreen = device.isFullScreenSupported(); 
                
                if (!theFullScreen.isDisplayable()) {
                    theFullScreen.setUndecorated(isFullScreen);
                    theFullScreen.setResizable(!isFullScreen);
                } // end if        
                
                if (isFullScreen) {
                    if (fullscreen.isSelected()) {
                        // Full-screen mode                    
                        device.setFullScreenWindow(theFullScreen);
                        
                        // prepare the screen
                        screenPane.removeAll();
                        screenPane.setLayout(new BorderLayout());
                        originalParent = getParent();
                        
                        // the viewer
                        setClosable(false);
                        setMaximizable(false);
                        setResizable(false);
                        setIconifiable(false);                        
                        screenPane.add(MoleculeViewerFrame.this);
                        theFullScreen.validate();
                        fullscreen.setToolTipText("Back to windowed mode");
                        memoryMonitor.setVisible(true);
                    } else { 
                        // windowed mode
                        device.setFullScreenWindow(null);
                        
                        // the viewer
                        setClosable(true);
                        setMaximizable(true);
                        setResizable(true);
                        setIconifiable(true);
                        originalParent.add(MoleculeViewerFrame.this);
                        fullscreen.setToolTipText("Fullscreen mode"); 
                        memoryMonitor.setVisible(false);
                        setVisible(true);
                    } // end if
                                        
                    theFullScreen.setVisible(fullscreen.isSelected());                    
                    ideInstance.setVisible(!fullscreen.isSelected());
                    ideInstance.validate();
                } // else do nothing
            }
        });
        toolBar.add(fullscreen);
        
        // the find tool
        toolBar.addSeparator();
        moleculeViewerFindTool = new MoleculeViewerFindTool(this);
        toolBar.add(moleculeViewerFindTool);

        // additional options
        makeMoreOptionsPopup();
        toolBar.addSeparator();
        moreOptions = new JButton("More options", images.getExpand());
        moreOptions.setHorizontalTextPosition(SwingConstants.LEFT);
        moreOptions.setMnemonic('p');
        moreOptions.setToolTipText("Click here for more options");
        moreOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // first update the menus to be shown
                updateMoreOptionsMenu();
                
                // then show this menu
                moreOptionsPopup.show(moreOptions, 0, moreOptions.getHeight());
            }
        });
        toolBar.add(moreOptions);
        
        // finally add the memory monitor, and hide it .. to be displayed only
        // in full screen mode
        toolBar.addSeparator();
        memoryMonitor = new MemoryMonitor();
        toolBar.add(memoryMonitor);        
        memoryMonitor.setVisible(false);                
        
        // the tool bar        
        getContentPane().add(toolBar, BorderLayout.NORTH);                
        
        // set the frame icon
        setFrameIcon(images.getMoleculeViewer());
        
        // and some size settings
        memoryMonitor.setPreferredSize(fullscreen.getPreferredSize());
        memoryMonitor.setMaximumSize(fullscreen.getPreferredSize());  
        
        // initilize the side panels
        initSidePanels();                
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(moleculeViewer);
        splitPane.setRightComponent(sidePanel);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(1.0);
        splitPane.setDividerSize(1);
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    /**
     * make extra options popup
     */
    private void makeMoreOptionsPopup() {
        moreOptionsPopup = new JPopupMenu();

        showMultipleBonds = new JCheckBoxMenuItem("Show multiple bonds");
        showMultipleBonds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                boolean detectMulti = true;
                
                for(MoleculeScene scene : moleculeViewer.getSceneList()) {
                    Molecule molecule = scene.getMolecule();    
                    
                    detectMulti = detectMulti && 
                       (Boolean) molecule.getCommonUserDefinedProperty(
                        CommonUserDefinedMolecularPropertyNames.MULTIPLE_BOND_DETECTED);
                } // end for

                if ((!detectMulti) && showMultipleBonds.isSelected()) {
                    doMoreOptionsTask(
                      CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_MULTIPLE_BONDS);
                } // end if

                // TODO: initiate a show/ hide operation
            }
        });
        moreOptionsPopup.add(showMultipleBonds);

        showWeakBonds = new JCheckBoxMenuItem("Show weak bonds");
        showWeakBonds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                boolean detectWeak = true;

                for(MoleculeScene scene : moleculeViewer.getSceneList()) {
                    Molecule molecule = scene.getMolecule();

                    detectWeak = detectWeak &&
                      (Boolean) molecule.getCommonUserDefinedProperty(
                        CommonUserDefinedMolecularPropertyNames.WEAK_BOND_DETECTED);
                } // end for

                if (!detectWeak && showWeakBonds.isSelected()) {
                   doMoreOptionsTask(
                     CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_WEAK_BONDS);
                } // end if

                // TODO: initiate a show/ hide operation
            }
        });
        moreOptionsPopup.add(showWeakBonds);

        showSpecialStructures = new JCheckBoxMenuItem("Show special structures");
        showSpecialStructures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                boolean detectSpecial = true;

                for(MoleculeScene scene : moleculeViewer.getSceneList()) {
                    Molecule molecule = scene.getMolecule();

                    detectSpecial = detectSpecial &&
                      (Boolean) molecule.getCommonUserDefinedProperty(
                         CommonUserDefinedMolecularPropertyNames.SPECIAL_STRUCTURE_DETECTED);
                } // end for

                if (!detectSpecial && showSpecialStructures.isSelected()) {
                    doMoreOptionsTask(
                      CommonUserDefinedMolecularPropertyNames.DETECT_ONLY_SPECIAL_STRUCTURE);
                } // end if

                // TODO: initiate a show/ hide operation
            }
        });
        moreOptionsPopup.add(showSpecialStructures);

        readDeep = new JCheckBoxMenuItem("Read deep into the input file");
        readDeep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                boolean readInDeep = false;
                
                for(MoleculeScene scene : moleculeViewer.getSceneList()) {
                    Molecule molecule = scene.getMolecule();

                    try {
                        readInDeep &=
                           (Boolean) molecule.getCommonUserDefinedProperty(
                                  CommonUserDefinedMolecularPropertyNames.READ_DEEP);
                    } catch(Exception e) {
                        readInDeep = false;
                    } // end of try .. catch block

                    if (!readInDeep) {
                        // TODO : then initiate a read deep process
                    } else {
                        // if this has been done, no point in having this
                        // menu enabled
                        readDeep.setSelected(true);
                        readDeep.setEnabled(false);
                    } // end if
                }
            }
        });
        moreOptionsPopup.add(readDeep);

        moreOptionsPopup.addSeparator();

        shareThis = new JMenuItem("Share this!");
        shareThis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                IntegratedIDETalkUI talkUI
                                = new IntegratedIDETalkUI(ideInstance, true);
                
                IDETalkUser user = talkUI.showIt(
                        "Choose a user to share this molecule object.");

                System.out.println("Molecule will be shared with: " + user);

                // TODO: right now this will only send one molecule
                Molecule mol = getMoleculeViewer().getSceneList().get(0).getMolecule();
                MoleculeTalkObject mto = new MoleculeTalkObject();
                mto.setTalkObjectContent(mol);

                // Do this using an object push, if a federation client
                // wont work with XMPP for the moment
                if (user.getUserDomain().equals(IDETalkUser.TalkUserDomain.MeTA)) {
                    try {
                        FederationServiceObjectPushConsumer f =
                            new FederationServiceObjectPushConsumer(mto);
                        FederationRequest req = f.discover(
                                       InetAddress.getByName(user.getHostID()));
                    
                        f.consume(req);
                    } catch(Exception e) {
                        System.err.println("Exception in sharing molecule object!");
                        e.printStackTrace();
                    } // end of try .. catch block
                } // end if
            }
        });
        moreOptionsPopup.add(shareThis);
    }

    /** do specified more options task */
    private void doMoreOptionsTask(CommonUserDefinedMolecularPropertyNames pn) {
        try {
            for (MoleculeScene scene : moleculeViewer.getSceneList()) {
                Molecule molecule = scene.getMolecule();
                MoleculeBuilder mb = Utility.createMoleculeBuilderObject();

                if (mb == null)
                    continue;
               
                molecule.setCommonUserDefinedProperty(pn, true);

                SimpleAsyncTask.init(mb, mb.getClass().getMethod(
                        "makeConnectivity", new Class[]{Molecule.class}),
                        new Object[]{molecule}).start();
            } // end for
        } catch (Exception e) {
            System.err.println("Unable to do task: " + e.toString());
            e.printStackTrace();
        } // end of try .. catch block
    }

    /**
     * initialize the side panels for cuboid and sphere based selection
     `*/
    protected void initSidePanels() {
        sidePanel = new JPanel(new BorderLayout());                
        
        // all the side panels are lazy initilized for faster loading of 
        // the molecule viewer frame
        
        // add the side panel and hide it for now        
        sidePanel.setBackground(moleculeViewer.getBackgroundColor());
        sidePanel.setVisible(false);
    }
    
    /**
     * init cuboid selection side panel
     */
    private void initCuboidSidePanel() {
        if (cuboidSidePanel != null) return;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;        
        
        // the cuboid controls
        cuboidSidePanel = new SidePanel("Cuboid selection parameters:");  
        JComponent cuboidComponent = cuboidSidePanel.getContentPane();
        cuboidComponent.setLayout(new GridBagLayout());
        
        gbc.gridwidth = 1;
        
        // the cuboid length component
        JPanel cuboidLengthPanel = new JPanel(new GridBagLayout());            
        cuboidLengthPanel.setBorder(
                  BorderFactory.createTitledBorder("Cuboid lengths : "));
            
        final ChangeListener cuboidUpdateAction = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if (selectionTool.getSelectedItem().equals(
                        ImageResource.getInstance().getCube())) {
                    doCuboidSelection();                    
                } // end if
            }
        };
        
            // length along X (a)
            JLabel cuboidLengthXLabel = new JLabel("On X(a) : ", JLabel.RIGHT);
            cuboidLengthXLabel.setDisplayedMnemonic('a');
            cuboidLengthXModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            cuboidLengthX = new JSpinner(cuboidLengthXModel);
            cuboidLengthX.setToolTipText("Length along X axis");
            cuboidLengthX.addChangeListener(cuboidUpdateAction);
            cuboidLengthXLabel.setLabelFor(cuboidLengthX);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            cuboidLengthPanel.add(cuboidLengthXLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 0;
            cuboidLengthPanel.add(cuboidLengthX, gbc);
            
            // length along Y (b)
            JLabel cuboidLengthYLabel = new JLabel("On Y(b) : ", JLabel.RIGHT);
            cuboidLengthYLabel.setDisplayedMnemonic('b');
            cuboidLengthYModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            cuboidLengthY = new JSpinner(cuboidLengthYModel);
            cuboidLengthY.setToolTipText("Length along Y axis");
            cuboidLengthY.addChangeListener(cuboidUpdateAction);
            cuboidLengthYLabel.setLabelFor(cuboidLengthY);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            gbc.weighty = 0;
            cuboidLengthPanel.add(cuboidLengthYLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.weightx = 1;
            gbc.weighty = 0;
            cuboidLengthPanel.add(cuboidLengthY, gbc);
            
            // length along Z (c)
            JLabel cuboidLengthZLabel = new JLabel("On Z(c) : ", JLabel.RIGHT);
            cuboidLengthZLabel.setDisplayedMnemonic('c');
            cuboidLengthZModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            cuboidLengthZ = new JSpinner(cuboidLengthZModel);
            cuboidLengthZ.setToolTipText("Length along Z axis");
            cuboidLengthZ.addChangeListener(cuboidUpdateAction);
            cuboidLengthZLabel.setLabelFor(cuboidLengthZ);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            gbc.weighty = 0;
            cuboidLengthPanel.add(cuboidLengthZLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.weightx = 1;
            gbc.weighty = 0;
            cuboidLengthPanel.add(cuboidLengthZ, gbc);
            
        gbc.insets = new Insets(4, 1, 4, 1);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        cuboidComponent.add(cuboidLengthPanel, gbc);        
        
        // the centering component
        JPanel cuboidCenteredPanel = new JPanel(new GridBagLayout());
        cuboidCenteredPanel.setBorder(
              BorderFactory.createTitledBorder("Centered on: "));
        cuboidGroup = new ButtonGroup();
        
            gbc.insets = new Insets(0, 0, 0, 0);
            
            // sphere atom center
            cuboidAtomCenterOption = new JRadioButton("Atom index: ", true);
            cuboidAtomCenterOption.setMnemonic('i');
            cuboidAtomCenterOption.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   if (selectionTool.getSelectedItem().equals(
                             ImageResource.getInstance().getCube())
                       && cuboidAtomCenterOption.isSelected()) {
                       cuboidAtomCenter.setEnabled(true);

                       cuboidCenterX.setEnabled(false);
                       cuboidCenterX.removeChangeListener(cuboidUpdateAction);
                       cuboidCenterY.setEnabled(false);
                       cuboidCenterY.removeChangeListener(cuboidUpdateAction);
                       cuboidCenterZ.setEnabled(false);                     
                       cuboidCenterZ.removeChangeListener(cuboidUpdateAction);
                   } // end if
               }
            });
            cuboidGroup.add(cuboidAtomCenterOption);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(cuboidAtomCenterOption, gbc);
        
            if (atomCenterModel == null) {
               atomCenterModel = new SpinnerNumberModel(0, 0, 0, 1);
            } // end if
            cuboidAtomCenter = new JSpinner(atomCenterModel);  
            cuboidAtomCenter.setToolTipText("The atom index on which cube " +
                                            "will be centered");
            cuboidAtomCenter.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (selectionTool.getSelectedItem().equals(
                         ImageResource.getInstance().getCube())) {
                        doCuboidSelection();
                    
                        Point3D center = (moleculeViewer.getSceneList().get(0))
                                 .getMolecule().getAtom(((Integer) 
                                atomCenterModel.getNumber()).intValue())
                                                               .getAtomCenter();
                        centerXModel.setValue(new Double(center.getX()));
                        centerYModel.setValue(new Double(center.getY()));
                        centerZModel.setValue(new Double(center.getZ()));
                    } // end if
                }
            });
            
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(cuboidAtomCenter, gbc);
            
            // sphere point center option
            cuboidCenterOption = new JRadioButton("Point: ", true);
            cuboidCenterOption.setMnemonic('P');
            cuboidCenterOption.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   if (selectionTool.getSelectedItem().equals(
                        ImageResource.getInstance().getCube())
                       && cuboidCenterOption.isSelected()) {
                       cuboidCenterX.setEnabled(true);
                       cuboidCenterX.addChangeListener(cuboidUpdateAction);
                       cuboidCenterY.setEnabled(true);
                       cuboidCenterY.addChangeListener(cuboidUpdateAction);
                       cuboidCenterZ.setEnabled(true);
                       cuboidCenterZ.addChangeListener(cuboidUpdateAction);

                       cuboidAtomCenter.setEnabled(false);
                   } // end if
               }
            });
            cuboidGroup.add(cuboidCenterOption);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.gridwidth = 2;
            cuboidCenteredPanel.add(cuboidCenterOption, gbc);
        
            gbc.gridwidth = 1;
            
            // X ...
            JLabel xcuboidLabel = new JLabel("X : ", JLabel.RIGHT);
            xcuboidLabel.setDisplayedMnemonic('X');
            
            if (centerXModel == null) {
                centerXModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            } // end if
            cuboidCenterX = new JSpinner(centerXModel);
            cuboidCenterX.setToolTipText("The X coordinate of cube center");
            cuboidCenterX.setEnabled(false);
            xcuboidLabel.setLabelFor(cuboidCenterX);
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(xcuboidLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.weightx = 1;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(cuboidCenterX, gbc);
            
            // Y ...
            JLabel ycuboidLabel = new JLabel("Y : ", JLabel.RIGHT);
            ycuboidLabel.setDisplayedMnemonic('Y');
            
            if (centerYModel == null) {
                centerYModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            } // end if
            cuboidCenterY = new JSpinner(centerYModel);  
            cuboidCenterY.setToolTipText("The Y coordinate of cube center");
            cuboidCenterY.setEnabled(false);                       
            ycuboidLabel.setLabelFor(cuboidCenterY);
            
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(ycuboidLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.weightx = 1;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(cuboidCenterY, gbc);
            
            // Z ...
            JLabel zcuboidLabel = new JLabel("Z : ", JLabel.RIGHT);
            zcuboidLabel.setDisplayedMnemonic('Z');
            
            if (centerZModel == null) {
                centerZModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            } // end if   
            cuboidCenterZ = new JSpinner(centerZModel);  
            cuboidCenterZ.setToolTipText("The Z coordinate of cube center");
            cuboidCenterZ.setEnabled(false);
            zcuboidLabel.setLabelFor(cuboidCenterZ);
            
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(zcuboidLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.weightx = 1;
            gbc.weighty = 0;
            cuboidCenteredPanel.add(cuboidCenterZ, gbc);
            
            cuboidInfo = new JLabel("<html><head></head><body>" +
                                           "<u>Cuboid selection panel</u>" +
                                           "</body></html>", 
                                           JLabel.LEFT);
            cuboidInfo.setVerticalAlignment(JLabel.TOP);
            cuboidInfo.setVerticalTextPosition(JLabel.TOP);
            
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 1;
            gbc.weighty = 1;        
            gbc.gridwidth = 2;
            cuboidCenteredPanel.add(cuboidInfo, gbc);
        
            gbc.gridwidth = 1;
            
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        cuboidComponent.add(cuboidCenteredPanel, gbc);
        cuboidSidePanel.setVisible(false);
        
        cuboidSidePanel.addCloseButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane.setDividerLocation(1.0);
                updateUI();
            }
        });
    }
    
    /**
     * init cuboid selection side panel
     */
    private void initSphereSidePanel() {
        if (sphereSidePanel != null) return;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH; 
        
        gbc.insets = new Insets(4, 1, 4, 1);
        
        // the sphere controls
        sphereSidePanel = new SidePanel("Sphere selection parameters:");
        JComponent sphereComponent = sphereSidePanel.getContentPane();
        sphereComponent.setLayout(new GridBagLayout());
        
        // the radius component
        JPanel sphereRadiusPanel = new JPanel(new BorderLayout());
        JLabel sphereRadiusLabel = new JLabel("Sphere radius: ");
        sphereRadiusLabel.setDisplayedMnemonic('a');
        
        final ChangeListener sphereUpdateAction = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if (selectionTool.getSelectedItem().equals(
                        ImageResource.getInstance().getSphere())) {
                    doSphereSelection();                    
                } // end if                                
            }
        };
            
        sphereRadiusModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
        sphereRadius = new JSpinner(sphereRadiusModel);
        sphereRadius.setToolTipText("Radius of the sphere");
        sphereRadius.addChangeListener(sphereUpdateAction);
        sphereRadiusLabel.setLabelFor(sphereRadius);
        sphereRadiusPanel.add(sphereRadiusLabel, BorderLayout.WEST);
        sphereRadiusPanel.add(sphereRadius     , BorderLayout.CENTER);
                
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        sphereComponent.add(sphereRadiusPanel, gbc);        
        
        // the centering component
        JPanel sphereCenteredPanel = new JPanel(new GridBagLayout());
        sphereCenteredPanel.setBorder(
              BorderFactory.createTitledBorder("Centered on: "));
        sphereGroup = new ButtonGroup();
        
            gbc.insets = new Insets(0, 0, 0, 0);
            
            // sphere atom center
            sphereAtomCenterOption = new JRadioButton("Atom index: ", true);
            sphereAtomCenterOption.setMnemonic('i');
            sphereAtomCenterOption.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   if (sphereAtomCenterOption.isSelected()) {
                       sphereAtomCenter.setEnabled(true);

                       sphereCenterX.setEnabled(false);
                       sphereCenterX.removeChangeListener(sphereUpdateAction);
                       sphereCenterY.setEnabled(false);
                       sphereCenterY.removeChangeListener(sphereUpdateAction);
                       sphereCenterZ.setEnabled(false);                       
                       sphereCenterZ.removeChangeListener(sphereUpdateAction);
                   } // end if
               }
            });
            sphereGroup.add(sphereAtomCenterOption);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            sphereCenteredPanel.add(sphereAtomCenterOption, gbc);
                    
            if (atomCenterModel == null) {
               atomCenterModel = new SpinnerNumberModel(0, 0, 0, 1);
            } // end if
            sphereAtomCenter = new JSpinner(atomCenterModel); 
            sphereAtomCenter.setToolTipText("The atom index on which this " +
                                            "sphere will be centered");
            sphereAtomCenter.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (selectionTool.getSelectedItem().equals(
                        ImageResource.getInstance().getSphere())) {
                        doSphereSelection();
                    
                        Point3D center = (moleculeViewer.getSceneList().get(0))
                                 .getMolecule().getAtom(((Integer) 
                                atomCenterModel.getNumber()).intValue())
                                                               .getAtomCenter();
                        centerXModel.setValue(new Double(center.getX()));
                        centerYModel.setValue(new Double(center.getY()));
                        centerZModel.setValue(new Double(center.getZ()));
                    } // end if
                }
            });
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 0;
            sphereCenteredPanel.add(sphereAtomCenter, gbc);
            
            // sphere point center option
            sphereCenterOption = new JRadioButton("Point: ", true);
            sphereCenterOption.setMnemonic('P');
            sphereCenterOption.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   if (selectionTool.getSelectedItem().equals(
                        ImageResource.getInstance().getSphere())
                       && sphereCenterOption.isSelected()) {
                       sphereCenterX.setEnabled(true);
                       sphereCenterX.addChangeListener(sphereUpdateAction);
                       sphereCenterY.setEnabled(true);
                       sphereCenterY.addChangeListener(sphereUpdateAction);
                       sphereCenterZ.setEnabled(true);
                       sphereCenterZ.addChangeListener(sphereUpdateAction);

                       sphereAtomCenter.setEnabled(false);
                   } // end if
               }
            });
            sphereGroup.add(sphereCenterOption);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.gridwidth = 2;
            sphereCenteredPanel.add(sphereCenterOption, gbc);
        
            gbc.gridwidth = 1;
            
            // X ...
            JLabel xSphereLabel = new JLabel("X : ", JLabel.RIGHT);
            xSphereLabel.setDisplayedMnemonic('X');

            if (centerXModel == null) {
                centerXModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            } // end if
            sphereCenterX = new JSpinner(centerXModel);
            sphereCenterX.setToolTipText("The X coordinate of sphere center");
            sphereCenterX.setEnabled(false);            
            xSphereLabel.setLabelFor(sphereCenterX);
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 0;
            gbc.weighty = 0;
            sphereCenteredPanel.add(xSphereLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.weightx = 1;
            gbc.weighty = 0;
            sphereCenteredPanel.add(sphereCenterX, gbc);
            
            // Y ...
            JLabel ySphereLabel = new JLabel("Y : ", JLabel.RIGHT);
            ySphereLabel.setDisplayedMnemonic('Y');
                        
            if (centerYModel == null) {
                centerYModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            } // end if
            sphereCenterY = new JSpinner(centerYModel); 
            sphereCenterX.setToolTipText("The Y coordinate of sphere center");
            sphereCenterY.setEnabled(false);                       
            ySphereLabel.setLabelFor(sphereCenterY);
            
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 0;
            gbc.weighty = 0;
            sphereCenteredPanel.add(ySphereLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.weightx = 1;
            gbc.weighty = 0;
            sphereCenteredPanel.add(sphereCenterY, gbc);
            
            // Z ...
            JLabel zSphereLabel = new JLabel("Z : ", JLabel.RIGHT);
            zSphereLabel.setDisplayedMnemonic('Z');
                        
            if (centerZModel == null) {
                centerZModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
            } // end if
            sphereCenterZ = new JSpinner(centerZModel);  
            sphereCenterX.setToolTipText("The Z coordinate of sphere center");
            sphereCenterZ.setEnabled(false);
            zSphereLabel.setLabelFor(sphereCenterZ);
            
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.weightx = 0;
            gbc.weighty = 0;
            sphereCenteredPanel.add(zSphereLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.weightx = 1;
            gbc.weighty = 0;
            sphereCenteredPanel.add(sphereCenterZ, gbc);
            
            sphereInfo = new JLabel("<html><head></head><body>" +
                                    "<u>Sphere selection panel</u>" +
                                    "</body></html>", 
                                    JLabel.LEFT);
            sphereInfo.setVerticalAlignment(JLabel.TOP);
            sphereInfo.setVerticalTextPosition(JLabel.TOP);
            
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.weightx = 1;
            gbc.weighty = 1;        
            gbc.gridwidth = 2;
            sphereCenteredPanel.add(sphereInfo, gbc);
        
            gbc.gridwidth = 1;
            
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        sphereComponent.add(sphereCenteredPanel, gbc);                
        
        sphereSidePanel.setVisible(false);
        sphereSidePanel.addCloseButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane.setDividerLocation(1.0);
                updateUI();
            }
        });
    }
    
    /**
     * init property control side panel
     */
    private void initPropertySidePanel() {
        if (propertySidePanel != null) return;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH; 
        
        // the property controls
        propertySidePanel = new SidePanel("Propery panel:");          
        JComponent propertyComponent = propertySidePanel.getContentPane();
        propertyComponent.setLayout(new BorderLayout());
            
        managePropertiesPanel = new JPanel(new GridBagLayout());
            manageProperties = new JButton("Manage properties...");
            manageProperties.setToolTipText("Click here to perform various" +
              " management taks related to properties (add, remove, rename)");
            manageProperties.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   managePropertiesDialog = 
                                   new ManagePropertiesDialog(ideInstance);
                   managePropertiesDialog.setVisible(true);
               }
            });
            gbc.insets = new Insets(4, 1, 4, 1);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            managePropertiesPanel.add(manageProperties, gbc);
            
            currentPropertyLabel = new JLabel("Select a property: ", 
                                              JLabel.RIGHT);            
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            managePropertiesPanel.add(currentPropertyLabel, gbc);
            
            currentProperty = new JComboBox();
            currentProperty.setToolTipText("Click to show a list of " +
                    "properties that are attached to this molecule scene");
            currentProperty.setEditable(false);
            currentProperty.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {                   
                   PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                   if (ps == null) return;
                   
                   propertySettingsPanel.setBorder(
                           BorderFactory.createTitledBorder("Settings for < "
                                                     + ps.getName() + " >: "));
                   showProperty.setSelected(ps.isVisible());
                   showPropertyBox.setSelected(ps.getGridPropertyRenderer()
                                                     .isShowBoundingBox());
                   
                   // update spinner controls
                   Double min = new Double(
                                   ps.getGridProperty().getMinFunctionValue());
                   Double max = new Double(
                                   ps.getGridProperty().getMaxFunctionValue());
                   currentFunctionValueModel.setMinimum(min);
                   currentFunctionValueModel.setMaximum(max);
                   currentFunctionValueModel.setValue(new Double(
                      ps.getGridPropertyRenderer().getCurrentFunctionValue()));
                   
                   max = new Double(ps.getGridPropertyRenderer().getColorMap()
                                      .getMaxFunctionValue());
                   min = new Double(ps.getGridPropertyRenderer().getColorMap()
                                      .getMinFunctionValue());

                   minRangeModel.setMinimum(min);
                   minRangeModel.setMaximum(max);
                   minRangeModel.setValue(min);
                   maxRangeModel.setMinimum(min);
                   maxRangeModel.setMaximum(max);
                   maxRangeModel.setValue(max);
               } 
            });
            gbc.gridx = 1;
            managePropertiesPanel.add(currentProperty, gbc);
            
        propertyComponent.add(managePropertiesPanel, BorderLayout.NORTH);
                
        propertySettingsPanel = new JPanel(new GridBagLayout());
        propertySettingsPanel.setBorder(
                BorderFactory.createTitledBorder("Settings for <none>: "));
            currentFunctionValueLabel = new JLabel("Current function value: ");            
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.gridwidth  = 1;
            gbc.gridheight = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            propertySettingsPanel.add(currentFunctionValueLabel, gbc);

            currentFunctionValueModel = new SpinnerNumberModel(0.0, 0.0, 
                                                               0.0, 0.001);
            currentFunctionValue = new JSpinner(currentFunctionValueModel);
            currentFunctionValue.setToolTipText("Set the currently " +
                    "displayed function value of the property");
            currentFunctionValue.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                    if (ps == null) return;

                    currentFunctionValue.setToolTipText(
                    "<html><body>Set the currently " +
                    "displayed function value of the property. <br>" +
                    "Current value: " + currentFunctionValue.getValue()
                    + "</body></html>");

                    ps.getGridPropertyRenderer().setCurrentFunctionValue(
                      ((Double) currentFunctionValue.getValue()).doubleValue());
                }
            });
            gbc.gridx = 1;
            propertySettingsPanel.add(currentFunctionValue, gbc);

            currentFunctionValue.setEditor(
                    new JSpinner.NumberEditor(currentFunctionValue,
                                              "#.##########"));

            colorMapLabel = new JLabel("Color Map: ", JLabel.RIGHT);
            gbc.gridx = 0;
            gbc.gridy = 1;
            propertySettingsPanel.add(colorMapLabel, gbc);
            
            // TODO: change this control
            gbc.gridx = 1;
            colorMapButton = new JButton("Change...");
            colorMapButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {                   
                   (new ColorMapDialog(ideInstance)).setVisible(true);
               }
            });
            propertySettingsPanel.add(colorMapButton, gbc);            
            
            JPanel rangePanel = new JPanel(new GridBagLayout());
            rangePanel.setBorder(BorderFactory.createTitledBorder("Range: "));
                minRangeLabel = new JLabel("Min: ", JLabel.RIGHT);
                gbc.weightx = 1;
                gbc.weighty = 0;
                gbc.gridx = 0;
                gbc.gridy = 0;
                rangePanel.add(minRangeLabel, gbc);
                
                minRangeModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.001);
                minRange = new JSpinner(minRangeModel);
                minRange.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent ce) {
                        maxRangeModel.setMinimum((Double) minRange.getValue());
                        
                        PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                        if (ps == null) return;
                        
                        ps.getGridPropertyRenderer().getColorMap()
                          .setMinFunctionValue(
                                ((Double) minRange.getValue()).doubleValue());
                        moleculeViewer.repaint();
                    }
                });
                minRange.setToolTipText("Minimum function value to consider" +
                        " for defining color range");
                gbc.gridx = 1;
                gbc.gridy = 0;
                rangePanel.add(minRange, gbc);
            
                maxRangeLabel = new JLabel("Max: ", JLabel.RIGHT);                
                gbc.gridx = 2;
                gbc.gridy = 0;
                rangePanel.add(maxRangeLabel, gbc);
                
                maxRangeModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.001);
                maxRange = new JSpinner(maxRangeModel);
                maxRange.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent ce) {
                        minRangeModel.setMaximum((Double) maxRange.getValue());
                        
                        PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                        if (ps == null) return;
                        
                        ps.getGridPropertyRenderer().getColorMap()
                          .setMaxFunctionValue(
                                ((Double) maxRange.getValue()).doubleValue());
                        moleculeViewer.repaint();
                    }
                });
                maxRange.setToolTipText("Maximum function value to consider" +
                        " for defining color range");
                gbc.gridx = 3;
                gbc.gridy = 0;
                rangePanel.add(maxRange, gbc);
            
            gbc.weighty = 0;
            gbc.gridwidth  = 2;
            gbc.gridheight = 2;
            gbc.gridx = 0;
            gbc.gridy = 2;
            propertySettingsPanel.add(rangePanel, gbc);
                
            showProperty = new JCheckBox("Show the selected property");
            showProperty.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                   if (ps == null) return;
                   
                   ps.setVisible(showProperty.isSelected());
               }
            });
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.gridheight = 1;
            gbc.gridx = 0;
            gbc.gridy = 4;
            propertySettingsPanel.add(showProperty, gbc);
            
            showPropertyBox = new JCheckBox("Show property limits");
            showPropertyBox.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                   if (ps == null) return;
                   ps.getGridPropertyRenderer().setShowBoundingBox(
                                                  showPropertyBox.isSelected());
               }
            });
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.gridheight = 1;
            gbc.gridx = 0;
            gbc.gridy = 5;
            propertySettingsPanel.add(showPropertyBox, gbc);
            
            interrogateMode = new JCheckBox("Interrogate mode");
            interrogateMode.setToolTipText("Enable to interrogate " +
                    "function value via mouse click");
            interrogateMode.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                   if (ps == null) return;
                   
                   moleculeViewer.setInterrogationMode(
                                     interrogateMode.isSelected());
               }
            });
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.gridheight = 1;
            gbc.gridx = 0;
            gbc.gridy = 6;
            propertySettingsPanel.add(interrogateMode, gbc);
            
            additionalSettings = new JButton("Additional Settings...");
            additionalSettings.setToolTipText("Click for some advanced " +
                    "settings related to the property being shown");
            additionalSettings.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                    PropertyScene ps = (PropertyScene) 
                                           currentProperty.getSelectedItem();
                   
                    if (ps == null) return;
                   
                    IDEPropertySheetUI ui = new IDEPropertySheetUI(
                               ps.getGridPropertyRenderer().getClass(), 
                               ps.getGridPropertyRenderer(), 
                               ideInstance);
                    
                    // show the UI
                    ui.show(null); 
               }
            });
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.gridheight = 1;
            gbc.gridx = 0;
            gbc.gridy = 7;
            propertySettingsPanel.add(additionalSettings, gbc);
            
            JLabel propertyInfo = new JLabel("Use this panel to modify" +
                                             " property settings.", 
                                             JLabel.LEFT);
            propertyInfo.setVerticalAlignment(JLabel.TOP);
            propertyInfo.setVerticalTextPosition(JLabel.TOP);
            
            gbc.gridx = 0;
            gbc.gridy = 8;
            gbc.weightx = 1;
            gbc.weighty = 1;        
            gbc.gridwidth = 2;
            propertySettingsPanel.add(propertyInfo, gbc);
            
        gbc.insets = new Insets(0, 0, 0, 0);
        
        propertyComponent.add(propertySettingsPanel, BorderLayout.CENTER);            
                
        propertySidePanel.setVisible(false);
        propertySidePanel.addCloseButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane.setDividerLocation(1.0);
                updateUI();
            }
        });
    }
    
    /**
     * init goodness indicator side panel
     */
    private void initGoodnessSidePanel() {
        if (goodnessSidePanel != null) return;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH; 
        gbc.insets = new Insets(4, 1, 4, 1);
        
        // add the goodness probe side panel
        goodnessSidePanel = new SidePanel("Goodness panel:");
        JComponent goodnessComponent = goodnessSidePanel.getContentPane();
        goodnessComponent.setLayout(new BorderLayout());
        
        JPanel goodnessIndicatorPanel = new JPanel(new GridBagLayout());
        final String goodnessInfoStr = "<html><body>Use this panel to "
                    + "view current goodness parameter " 
                    + "<br>of the fragmentation scheme.";
        final JLabel goodnessInfo = new JLabel(goodnessInfoStr 
                                             + "</body><html>", 
                                             JLabel.LEFT);
        
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;        
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            fragSchemeLabel = new JLabel("Fragmentation Scheme: ", 
                                         JLabel.RIGHT);
            goodnessIndicatorPanel.add(fragSchemeLabel, gbc);
        
            gbc.gridx = 1;
            gbc.gridy = 0;
            fragScheme = new JComboBox();            
            fragScheme.setEditable(false);                     
            goodnessIndicatorPanel.add(fragScheme, gbc);
            try {
               Iterator<FragmentationScheme> fs = getSceneList().firstElement()
                                    .getMolecule().getAllFragmentationSchemes();
               
               while (fs.hasNext()) {
                    fragScheme.addItem(fs.next());
               } // end while
            } catch (Exception ignored) {
                System.err.println("Warning: Unabled to determine " +
                        "fragmentation schemes, using default. Error is: " 
                        + ignored.toString());
            } // end of try .. catch block
        
            gbc.gridx = 0;
            gbc.gridy = 1;
            goodnessThresholdLabel = new JLabel("Goodness Threshold: ",
                                           JLabel.RIGHT);
            goodnessIndicatorPanel.add(goodnessThresholdLabel, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 1;
            goodnessThreshold = new JSpinner(
                    new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1)); 
            goodnessThreshold.addChangeListener(new ChangeListener() {
               @Override
               public void stateChanged(ChangeEvent ce) {
                   indicator.setGoodnessThreshold(
                                (Double) goodnessThreshold.getValue());
                   
                   Object [] offAtms = indicator.getOffendingAtoms(
                           excludePendantAtoms.isSelected()).toArray();
                   
                   offendingAtoms.setListData(offAtms);
                   
                   goodnessInfo.setText(goodnessInfoStr + "<hr>"
                           + "Number of offending atoms: " + offAtms.length
                           + "</body><html>");
               }
            });
            goodnessIndicatorPanel.add(goodnessThreshold, gbc);
            
            gbc.insets = new Insets(4, 1, 1, 1);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridheight = 1;
            gbc.gridwidth = 2;
            goodnessIndicatorLabel = new JLabel("Goodness meter: ", 
                                           JLabel.LEFT);
            goodnessIndicatorPanel.add(goodnessIndicatorLabel, gbc);
            
            gbc.insets = new Insets(1, 1, 1, 1);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridheight = 1;
            gbc.gridwidth = 2;
            indicator = new GoodnessIndicator();
            goodnessIndicatorPanel.add(new JScrollPane(indicator, 
                    JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);
            
            gbc.insets = new Insets(4, 1, 1, 1);
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridheight = 1;
            gbc.gridwidth = 2;
            offendingAtomsLabel = new JLabel("Offending atoms:", JLabel.LEFT);
            goodnessIndicatorPanel.add(offendingAtomsLabel, gbc);
                        
            gbc.insets = new Insets(1, 1, 1, 1);
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridheight = 1;
            gbc.gridwidth = 2;
            offendingAtoms = new JList();
            offendingAtoms.addListSelectionListener(new ListSelectionListener(){
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (offendingAtoms.getSelectedValue() == null) return;
                    
                    Atom atm = ((Atom) offendingAtoms.getSelectedValue());
                    
                    getSceneList().get(0).selectIfContainedIn(
                                                atm.getAtomCenter(), 0.5);
                    repaint();
                    
                    String goodness = new DecimalFormat("#.###").format(
                     indicator.getReferenceGoodnessProbe().getBestGoodnessIndex(
                            atm.getIndex(), atm.getIndex()).getValue());
                            
                    goodnessInfo.setText(goodnessInfoStr + "<hr>"
                           + "Number of offending atoms: " 
                           + offendingAtoms.getModel().getSize()
                           + "<br> Selected Atom Index: "
                           + atm.getIndex() + " ("
                           + goodness + ")"
                           + "</body><html>");
                    
                    
                }
            });
            goodnessIndicatorPanel.add(new JScrollPane(offendingAtoms, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);
            
            gbc.insets = new Insets(4, 1, 4, 1);
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 2;
            excludePendantAtoms = new JCheckBox("Exclude pendant atoms");
            excludePendantAtoms.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   Object [] offAtms = indicator.getOffendingAtoms(
                           excludePendantAtoms.isSelected()).toArray();
                   
                   offendingAtoms.setListData(offAtms);
                   
                   goodnessInfo.setText(goodnessInfoStr + "<hr>"
                           + "Number of offending atoms: " + offAtms.length
                           + "</body><html>");
               }
            });
            goodnessIndicatorPanel.add(excludePendantAtoms, gbc);
            
            goodnessInfo.setVerticalAlignment(JLabel.TOP);
            goodnessInfo.setVerticalTextPosition(JLabel.TOP);
            
            gbc.gridx = 0;
            gbc.gridy = 7;   
            gbc.weighty = 1;
            gbc.gridheight = 2;
            goodnessIndicatorPanel.add(goodnessInfo, gbc);
            
        goodnessComponent.add(goodnessIndicatorPanel, BorderLayout.CENTER);
        goodnessSidePanel.setVisible(false);         
        goodnessSidePanel.addCloseButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane.setDividerLocation(1.0);
                updateUI();
            }
        });
    }
    
    /**
     * method to show up the goodness side panel
     */
    public void showGoodnessSidePanel() {
        sidePanel.removeAll();
        initGoodnessSidePanel();        
        sidePanel.add(goodnessSidePanel, BorderLayout.CENTER);
        sidePanel.setVisible(true);
        goodnessSidePanel.setVisible(true);
        
        // TODO: change this?
        Molecule mol = moleculeViewer.getSceneList().get(0).getMolecule();
        indicator.setReferenceMolecule(mol);
        
        splitPane.setDividerLocation(0.8);
    }
    
    /**
     * method to show up the grid generator side panel
     */
    public void showGridGeneratorSidePanel() {
        sidePanel.removeAll();
        initGridSidePanel();        
        updateSpinnerModels();
        GridProperty gp = moleculeViewer.getScreenGridProperty();
        updateGridProperty(gp);
        moleculeViewer.setScreenGridProperty(gp);        
        sidePanel.add(gridGeneratorSidePanel, BorderLayout.CENTER);
        sidePanel.setVisible(true);
        gridGeneratorSidePanel.setVisible(true);        
        
        splitPane.setDividerLocation(0.8);
        updateUI();
    }
    
    /**
     * Hide the sidepanel
     */
    public void hideSidePanel() {
        if (sidePanel != null) 
           sidePanel.setVisible(false);           
        
        splitPane.setDividerLocation(1.0);
        updateUI();
    }
    
    /**
     * do the sphere based selection
     */
    private void doSphereSelection() {
        double radius = ((Double) sphereRadiusModel.getNumber()).doubleValue();
        Point3D center;
        
        if (sphereAtomCenterOption.isSelected()) {
            center = (moleculeViewer.getSceneList().get(0))
                  .getMolecule().getAtom(((Integer) 
                 atomCenterModel.getNumber()).intValue()).getAtomCenter();            
        } else {
            center = new Point3D(
                ((Double) centerXModel.getNumber()).doubleValue(), 
                ((Double) centerYModel.getNumber()).doubleValue(), 
                ((Double) centerZModel.getNumber()).doubleValue()
            );
        } // end if
        
        // select the sphere based stuff
        if (moleculeViewer == null) return;
        moleculeViewer.selectIfContainedIn(center, radius);
        
        // update the cuboid info panel
        StringBuffer sb = new StringBuffer();
        
        sb.append("<html><head><body>");
        sb.append("<u>Sphere selection panel</u>");
        sb.append("<br>");
        try {
            sb.append(getSelectionInfo());
        } catch (Exception ignored) {
            System.err.println(ignored);
            ignored.printStackTrace();
        } // end try .. catch block
        DecimalFormat formatter = new DecimalFormat("#.###");
        sb.append("- Volume: ")
          .append(formatter.format(4.0 / 3.0 * Math.PI * Math.pow(radius, 3.0)))
          .append(Utility.ANGSTROM_HTML_CODE + "<sup>3</sup>");
        sb.append("<br>- Total surface area: ")
          .append(formatter.format(4.0 * Math.PI * Math.pow(radius, 2.0)))
          .append(Utility.ANGSTROM_HTML_CODE + "<sup>2</sup>");
        sb.append("</body>");                
        
        sphereInfo.setText(sb.toString());
        
        sb = null; 
        formatter = null;
        
        // repaint the stuff
        moleculeViewer.repaint();
    }
    
    /** to check if spinner model is being updated, in such a case 
        we may not have a valid 'gp' and hence we do not run 
        updatedGridProperty().*/
    private boolean inSpinnerModelUpdateState = false;
    
    /**
     * update the grid property object
     */
    private void updateGridProperty(GridProperty gp) {
        if (inSpinnerModelUpdateState) return;
        
        if (gp == null) return;
        
        BoundingBox bb = gp.getBoundingBox();
        BoundingBox orgbb = moleculeViewer.getBoundingBox();
        
        Point3D center = new Point3D(
                ((Double) gridCenterXModel.getNumber()).doubleValue(), 
                ((Double) gridCenterYModel.getNumber()).doubleValue(), 
                ((Double) gridCenterZModel.getNumber()).doubleValue()
            );
        
        Point3D ul = bb.getUpperLeft();
        Point3D br = bb.getBottomRight();
        
        double lengthX = 
               ((Double) gridLengthXModel.getNumber()).doubleValue();
        double lengthY = 
               ((Double) gridLengthYModel.getNumber()).doubleValue();
        double lengthZ = 
               ((Double) gridLengthZModel.getNumber()).doubleValue();
        
        ul.setX((2.0 * center.getX() - lengthX) / 2.0);
        ul.setY((2.0 * center.getY() - lengthY) / 2.0);
        ul.setZ((2.0 * center.getZ() - lengthZ) / 2.0);
        
        br.setX(ul.getX() + lengthX);
        br.setY(ul.getY() + lengthY);
        br.setZ(ul.getZ() + lengthZ); 
        
        // update the gridGeneratorLabel with range specs
        gp.setNoOfPointsAlongX(((Integer) pointsXModel.getNumber()).intValue());
        gp.setNoOfPointsAlongY(((Integer) pointsYModel.getNumber()).intValue());
        gp.setNoOfPointsAlongZ(((Integer) pointsZModel.getNumber()).intValue());
        gp.recomputeIncrements();
        
        StringBuilder sb = new StringBuilder();
        DecimalFormat fmt = new DecimalFormat("#.####");
        
        sb.append("<html><head></head><body>");
        sb.append("Units in " + Utility.ANGSTROM_HTML_CODE);
        sb.append("<table border=\"0\">");
        sb.append("<tr><td>Min X</td><td>Min Y</td><td>Min Z</td></tr>");        
        
        Point3D minPoint = gp.getBoundingBox().getUpperLeft();
        sb.append("<tr><td>").append(fmt.format(minPoint.getX()))
                             .append("</td><td>")
                             .append(fmt.format(minPoint.getY()))
                             .append("</td><td>")
                             .append(fmt.format(minPoint.getZ()))
                             .append("</td></tr>");
        sb.append("<tr><td>Step X</td><td>Step Y</td><td>Step Z</td></tr>");
        sb.append("<tr><td>").append(fmt.format(gp.getXIncrement()))
                             .append("</td><td>")
                             .append(fmt.format(gp.getYIncrement()))
                             .append("</td><td>")
                             .append(fmt.format(gp.getZIncrement()))
                             .append("</td></tr>");
        sb.append("<tr><td>Extent X</td><td>Extent Y</td>")
                  .append("<td>Extent Z</td></tr>");
        sb.append("<tr><td>");
        sb.append(fmt.format(gp.getBoundingBox().getXWidth()-orgbb.getXWidth()))
                  .append("</td><td>");
        sb.append(fmt.format(gp.getBoundingBox().getYWidth()-orgbb.getYWidth()))
                  .append("</td><td>");
        sb.append(fmt.format(gp.getBoundingBox().getZWidth()-orgbb.getZWidth()))
                  .append("</td></tr>");
        sb.append("</table>");
        sb.append("</body></html>");
        gridGeneratorLabel.setText(sb.toString());
    }
    
    /**
     * do the cuboid based selection
     */
    private void doCuboidSelection() {
        BoundingBox bb = new BoundingBox();
        
        Point3D center;
        
        if (cuboidAtomCenterOption.isSelected()) {
            center = (moleculeViewer.getSceneList().get(0))
                  .getMolecule().getAtom(((Integer) 
                 atomCenterModel.getNumber()).intValue()).getAtomCenter();            
        } else {
            center = new Point3D(
                ((Double) centerXModel.getNumber()).doubleValue(), 
                ((Double) centerYModel.getNumber()).doubleValue(), 
                ((Double) centerZModel.getNumber()).doubleValue()
            );
        } // end if
        
        Point3D ul = bb.getUpperLeft();
        Point3D br = bb.getBottomRight();
        
        double lengthX = 
               ((Double) cuboidLengthXModel.getNumber()).doubleValue();
        double lengthY = 
               ((Double) cuboidLengthYModel.getNumber()).doubleValue();
        double lengthZ = 
               ((Double) cuboidLengthZModel.getNumber()).doubleValue();
        
        ul.setX((2.0 * center.getX() - lengthX) / 2.0);
        ul.setY((2.0 * center.getY() - lengthY) / 2.0);
        ul.setZ((2.0 * center.getZ() - lengthZ) / 2.0);
        
        br.setX(ul.getX() + lengthX);
        br.setY(ul.getY() + lengthY);
        br.setZ(ul.getZ() + lengthZ);
        
        // select the cuboid based stuff
        if (moleculeViewer == null) return;
        moleculeViewer.selectIfContainedIn(bb);
        
        // update the cuboid info panel
        StringBuffer sb = new StringBuffer();
        
        sb.append("<html><head><body>");
        sb.append("<u>Cuboid selection panel</u>");
        sb.append("<br>");
        try {
            sb.append(getSelectionInfo());
        } catch (Exception ignored) {
            System.err.println(ignored);
            ignored.printStackTrace();
        } // end try .. catch block
        DecimalFormat formatter = new DecimalFormat("#.###");
        sb.append("- Volume: ").append(formatter.format(bb.volume()))
          .append(Utility.ANGSTROM_HTML_CODE + "<sup>3</sup>");
        sb.append("<br>- Total surface area: ")
          .append(formatter.format(bb.totalSurfaceArea()))
          .append(Utility.ANGSTROM_HTML_CODE + "<sup>2</sup>");
        sb.append("</body>");                
        
        cuboidInfo.setText(sb.toString());
        
        sb = null; 
        formatter = null;
        
        // repaint the stuff
        moleculeViewer.repaint();
    }
    
    /**
     * return selection info as an HTML string
     */
    private String getSelectionInfo() throws Exception {
        // check first if any thing is selected?
        if (moleculeViewer.getFirstSelectedScene() == null) return "";
        
        // if yes, make a valid statement!
        Molecule selectedFragment = moleculeViewer.getFirstSelectedScene()
                                                  .getSelectionAsMolecule();
        
        // check again if any thing is selected?
        if (selectedFragment == null) return "";
        
        return ("- You have selected " + selectedFragment.getNumberOfAtoms() 
                + " atoms as: <center>" 
                + selectedFragment.getFormula().getHTMLFormula() + "</center>");
    }
    
    /**
     * Returns the scene list attached to this viewer.
     *
     * @return A vector list of senes connected to this viewer
     */
    public java.util.Vector<MoleculeScene> getSceneList() {
        return moleculeViewer.getSceneList();
    }
    
    /**
     * add a MoleculeScene to this viewer
     */
    public void addScene(MoleculeScene scene) {
        if (scene == null) return;
        
        // just forward the calls
        moleculeViewer.addScene(scene);
        scene.addMoleculeSceneChangeListener(this);
        
        // update the find tool
        moleculeViewerFindTool.updateFindTragetSceneList();
        
        if (moleculeViewer.getSceneList().size() == 1) {
            // only one molecule in the viewer...
            selectionTool.addItem(ImageResource.getInstance().getCube());
            selectionTool.addItem(ImageResource.getInstance().getSphere()); 
        } else {
            // hide all the special selection controls
            selectionTool.removeItem(ImageResource.getInstance().getCube());
            selectionTool.removeItem(ImageResource.getInstance().getSphere());
            
            sidePanel.setVisible(false);  
            
            // .. and activate rotate tool instead
            if (!rotate.isSelected()) rotate.doClick();
        } // end if
          
        // enable / disable movieMode button
        movieMode.setEnabled(moleculeViewer.getSceneList().size() > 1);
        
        // and set a fuzzy title
        setTitle("Molecule Viewer - [" + selfMoleculeViewerCount 
                 + " (" + moleculeViewer.getViewerTitle() + ")]");       
        
        // and update the fragScheme list
        if (fragScheme != null) {
            fragScheme.addItem(getSceneList().firstElement()
                               .getMolecule().getDefaultFragmentationScheme());
        } // end if
    }
    
    /** update the more options menu */
    private void updateMoreOptionsMenu() {
        boolean multiBond = true, weakBond = true,
                specialStructure = true, rdDeep = true;
        
        for(MoleculeScene scene : moleculeViewer.getSceneList()) {
            Molecule molecule = scene.getMolecule();

            multiBond = multiBond && (Boolean) molecule.getCommonUserDefinedProperty(
                CommonUserDefinedMolecularPropertyNames.MULTIPLE_BOND_DETECTED);
            weakBond = weakBond && (Boolean) molecule.getCommonUserDefinedProperty(
                CommonUserDefinedMolecularPropertyNames.WEAK_BOND_DETECTED);
            specialStructure = specialStructure
                && (Boolean) molecule.getCommonUserDefinedProperty(
                CommonUserDefinedMolecularPropertyNames.SPECIAL_STRUCTURE_DETECTED);
            rdDeep = rdDeep && (Boolean) molecule.getCommonUserDefinedProperty(
                CommonUserDefinedMolecularPropertyNames.READ_DEEP);
        } // end for

        showMultipleBonds.setSelected(multiBond);
        showWeakBonds.setSelected(weakBond);
        showSpecialStructures.setSelected(specialStructure);
        readDeep.setSelected(rdDeep);
        readDeep.setEnabled(!rdDeep);
    }

    /**
     * remove a MoleculeScene from this viewer
     */
    public void removeScene(MoleculeScene scene) {
        if (scene == null) return;
                
        // just forward the calls
        moleculeViewer.removeScene(scene);
        scene.removeMoleculeSceneChangeListener(this);
        
        // update the find tool
        moleculeViewerFindTool.updateFindTragetSceneList();
        
        if (moleculeViewer.getSceneList().size() == 1) {
            // only one molecule in the viewer...
            selectionTool.addItem(ImageResource.getInstance().getCube());
            selectionTool.addItem(ImageResource.getInstance().getSphere());
        } else {
            // hide all the special selection controls
            selectionTool.removeItem(ImageResource.getInstance().getCube());
            selectionTool.removeItem(ImageResource.getInstance().getSphere());
            
            sidePanel.setVisible(false);
            
            // .. and activate rotate tool instead
            rotate.doClick();
        } // end if
        
        // enable / disable movieMode button
        movieMode.setEnabled(moleculeViewer.getSceneList().size() > 1);                
    }
    
    /**
     * handling undo events
     */
    @Override
    public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
        undo.setEnabled(moleculeViewer.canUndo());
        redo.setEnabled(moleculeViewer.canRedo());    
        
        // update the multi views if valid
        if (isMultiViewEnabled()) {
            if (multiView != null) {
                // send the update message only if there was
                // a change in the current main view   
                if (((MoleculeViewer) undoableEditEvent.getSource())
                     == moleculeViewer) {
                    multiView.updateViews(undoableEditEvent);
                } // end if
            } // end if
        } // end if
    }
    
    /** Getter for property moleculeViewer.
     * @return Value of property moleculeViewer.
     *
     */
    public MoleculeViewer getMoleculeViewer() {
        return this.moleculeViewer;
    }
    
    /**
     * scene change listener
     */
    @Override
    public void sceneChanged(MoleculeSceneChangeEvent msce) {                
        if (msce.getType() == MoleculeSceneChangeEvent.MOLECULE_CHANGE) {
            // set a fuzzy title
            setTitle("Molecule Viewer - [" + selfMoleculeViewerCount 
                     + " (" + moleculeViewer.getViewerTitle() + ")]");
            
            // update the find tool
            moleculeViewerFindTool.updateFindTragetSceneList();    
            
            // also check if we need an update on godness panel
            if (goodnessSidePanel != null) {
                if (goodnessSidePanel.isVisible()) {
                    indicator.setGoodnessThreshold(
                                (Double) goodnessThreshold.getValue());
                   
                    offendingAtoms.setListData(indicator.getOffendingAtoms(
                           excludePendantAtoms.isSelected()).toArray());
                } // end if
            } // end if
        } else if (msce.getType() == MoleculeSceneChangeEvent.PROPERTY_CHANGE) {
            // update properties dialog if visible
            if ((managePropertiesDialog != null) 
                && (managePropertiesDialog.isVisible())) {
                managePropertiesDialog.updateControls();
            } // end if
        } else if (msce.getType() == MoleculeSceneChangeEvent.PROPERTY_ADDED) {        
            if (currentProperty == null) {
                initPropertySidePanel();
            } // end if

            currentProperty.addItem(msce.getChangedPropertyScene());
            
            // update properties dialog if visible
            if ((managePropertiesDialog != null) 
                && (managePropertiesDialog.isVisible())) {
                managePropertiesDialog.updateControls();
            } // end if
        } else if (msce.getType() == MoleculeSceneChangeEvent.PROPERTY_REMOVED){
            if (currentProperty == null) {
                initPropertySidePanel();
            } // end if

            currentProperty.removeItem(msce.getChangedPropertyScene());
            
            // update properties dialog if visible
            if ((managePropertiesDialog != null) 
                && (managePropertiesDialog.isVisible())) {
                managePropertiesDialog.updateControls();
            } // end if
        } // end if
    }
    
    /**
     * update the spinner control models
     */
    private void updateSpinnerModels() {
        inSpinnerModelUpdateState = true;
        
        // update selection contorl models
        BoundingBox bb = moleculeViewer.getBoundingBox();
        Point3D center = moleculeViewer.getSceneList()
                                       .get(0).getMolecule().getCenterOfMass();
        double maxSpan = Math.max(Math.max(bb.getXWidth(), bb.getYWidth()),
                                  bb.getZWidth());        
        
        // sphere radius
        if (sphereRadiusModel != null) {
            sphereRadiusModel.setMaximum(new Double(maxSpan));
            sphereRadius.setModel(sphereRadiusModel);
        } // end if
        
        // cuboid lengths
        
        if (cuboidLengthXModel != null) { // assume the all are inited!
            // X                    
            cuboidLengthXModel.setMaximum(new Double(bb.getXWidth()));
        
            // Y                
            cuboidLengthYModel.setMaximum(new Double(bb.getYWidth()));
        
            // Z        
            cuboidLengthZModel.setMaximum(new Double(bb.getZWidth()));
        } // end if
        
        // the centers...
        
        if (centerXModel != null) { // assume again ... all are inited...
            // X
            centerXModel.setMinimum(new Double(bb.getUpperLeft().getX()));
            centerXModel.setMaximum(new Double(bb.getBottomRight().getX()));
        
            // Y
            centerYModel.setMinimum(new Double(bb.getUpperLeft().getY()));
            centerYModel.setMaximum(new Double(bb.getBottomRight().getY()));
        
            // Z
            centerZModel.setMinimum(new Double(bb.getUpperLeft().getZ()));
            centerZModel.setMaximum(new Double(bb.getBottomRight().getZ()));
        } // end if
        
        // for the grid
        bb = moleculeViewer.getGridBoundingBox();
        
        if (gridCenterXModel != null) { // again assume ;)
            // X
            gridCenterXModel.setMinimum(new Double(bb.getUpperLeft().getX())
                                         - MAX_GRID_EXTENT);
            gridCenterXModel.setMaximum(new Double(bb.getBottomRight().getX())
                                         + MAX_GRID_EXTENT);
            gridCenterXModel.setValue(new Double(center.getX()));
        
            // Y
            gridCenterYModel.setMinimum(new Double(bb.getUpperLeft().getY())
                                         - MAX_GRID_EXTENT);
            gridCenterYModel.setMaximum(new Double(bb.getBottomRight().getY())
                                         + MAX_GRID_EXTENT);
            gridCenterYModel.setValue(new Double(center.getY()));
        
            // Z
            gridCenterZModel.setMinimum(new Double(bb.getUpperLeft().getZ())
                                         - MAX_GRID_EXTENT);
            gridCenterZModel.setMaximum(new Double(bb.getBottomRight().getZ())
                                         + MAX_GRID_EXTENT);
            gridCenterZModel.setValue(new Double(center.getZ()));
        } // end if
        
        if (gridLengthXModel != null) { // again assume ;)
            // X                        
            gridLengthXModel.setMaximum(
                    new Double(bb.getBottomRight().getX()+MAX_GRID_EXTENT));
            gridLengthXModel.setValue(new Double(bb.getXWidth()));
        
            // Y                        
            gridLengthYModel.setMaximum(
                    new Double(bb.getBottomRight().getY()+MAX_GRID_EXTENT));
            gridLengthYModel.setValue(new Double(bb.getYWidth()));
        
            // Z            
            gridLengthZModel.setMaximum(
                    new Double(bb.getBottomRight().getZ()+MAX_GRID_EXTENT)); 
            gridLengthZModel.setValue(new Double(bb.getZWidth()));
        } // end if
        
        if (atomCenterModel != null) {
           // atom indices (0 .. numberOfAtoms-1)
           Integer maxAtoms = new Integer((moleculeViewer.getSceneList().get(0))
                                           .getMolecule().getNumberOfAtoms()-1);        
           atomCenterModel.setMaximum(maxAtoms);        
        } // end if
        
        inSpinnerModelUpdateState = false;
    }
    
    /**
     * the finalize method
     */
    @Override
    public void finalize() throws Throwable {       
       super.finalize();
    }
    
    /**
     * internal class to handle interface of color maps.
     */
    private class ColorMapDialog extends JDialog {
        
        private JButton minColorButton, maxColorButton;
        private JList colorList;
        
        private JPanel twoColorPanel, multiColorPanel;
        private JRadioButton twoColor, multiColor;
        private ButtonGroup colorGroup;
        private ColorToFunctionRangeMap colorMap;
        
        private JButton saveToFile, readFromFile;
                
        /** Creates a new instance of ColorMapDialog */
        public ColorMapDialog(JFrame parent) {
            super(parent);
            
            initComponents();
            setSize(new Dimension(400, 350));
            setLocationRelativeTo(parent);
            setModal(true);
            setTitle("Color map configuration");        
        }
        
        /**
         * initilize the components
         */
        private void initComponents() {     
            PropertyScene ps = (PropertyScene) 
                                   currentProperty.getSelectedItem();
            
            if (ps != null) {                        
                colorMap = ps.getGridPropertyRenderer().getColorMap();
            } else {
                colorMap = new ColorToFunctionRangeMap();
            } // end if
            
            Container container = getContentPane();
            
            container.setLayout(new BorderLayout());
            
            colorGroup = new ButtonGroup();
            
            twoColorPanel = new JPanel(new BorderLayout());
                twoColor = new JRadioButton("Two color map");
                twoColor.addActionListener(new ActionListener() {
                @Override
                   public void actionPerformed(ActionEvent ae) {
                       boolean enable = twoColor.isSelected();
                       
                       minColorButton.setEnabled(enable);
                       maxColorButton.setEnabled(enable);
                       
                       colorList.setEnabled(multiColor.isSelected());
                   }
                });
                colorGroup.add(twoColor);
                twoColorPanel.add(twoColor, BorderLayout.NORTH);
                
                JPanel colorButtonPanel1 = new JPanel(
                                  new FlowLayout(FlowLayout.LEFT));
                minColorButton = new JButton("Min Color");
                minColorButton.setMnemonic('n');
                minColorButton.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) {
                        Color newColor = JColorChooser.showDialog(ideInstance, 
                                "Choose a color of the minimum function value",
                                colorMap.getMinColor());
                        
                        if (newColor != null) {
                            colorMap.setMinColor(newColor);
                            minColorButton.setBackground(newColor);
                            MoleculeViewerFrame.this.repaint();
                        } // end if 
                   }
                });
                minColorButton.setBackground(colorMap.getMinColor());
                colorButtonPanel1.add(minColorButton);
                
                maxColorButton = new JButton("Max Color");
                maxColorButton.setMnemonic('x');
                maxColorButton.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) {
                        Color newColor = JColorChooser.showDialog(ideInstance, 
                                "Choose a color of the maximum function value",
                                colorMap.getMaxColor());
                        
                        if (newColor != null) {
                            colorMap.setMaxColor(newColor);
                            maxColorButton.setBackground(newColor);
                            MoleculeViewerFrame.this.repaint();
                        } // end if 
                   }
                });
                maxColorButton.setBackground(colorMap.getMaxColor());
                colorButtonPanel1.add(maxColorButton);
                twoColorPanel.add(colorButtonPanel1, BorderLayout.CENTER);
                
            container.add(twoColorPanel, BorderLayout.NORTH);
                        
            multiColorPanel = new JPanel(new BorderLayout());            
                // color controls
                JPanel colorButtonPanel2 = new JPanel(
                                  new IDEVerticalFlowLayout());
                final JButton addColor, removeColor, editColor, 
                        moveUpColor, moveDownColor;
                
                addColor = new JButton("Add...");
                addColor.setMnemonic('A');
                addColor.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       Color newColor = JColorChooser.showDialog(ideInstance, 
                                "Choose a color ...",
                                colorMap.getMaxColor());
                        
                       if (newColor != null) {
                            if (colorMap.getColorRange() == null) {
                                ArrayList<Color> ar = new ArrayList<Color>();
                                
                                ar.add(newColor);
                                colorMap.setColorRange(ar);
                            } else {
                                colorMap.getColorRange().add(newColor);
                                colorMap.setColorRange(
                                        colorMap.getColorRange());
                            } // end if
                            
                            colorList.setListData(
                                    colorMap.getColorRange().toArray());
                            MoleculeViewerFrame.this.repaint();                           
                       } // end if 
                   }
                });
                colorButtonPanel2.add(addColor);
                   
                removeColor = new JButton("Remove...");
                removeColor.setMnemonic('R');
                removeColor.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       Color color = (Color) colorList.getSelectedValue();
                       if (color == null) return;
                       
                       colorMap.getColorRange().remove(
                                   colorList.getSelectedIndex());
                       colorMap.setColorRange(colorMap.getColorRange());
                       colorList.setListData(
                                        colorMap.getColorRange().toArray());
                       MoleculeViewerFrame.this.repaint();
                   }
                });
                colorButtonPanel2.add(removeColor);
                
                editColor = new JButton("Edit...");
                editColor.setMnemonic('E');
                editColor.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       Color color = (Color) colorList.getSelectedValue();
                       if (color == null) return;
                       
                       Color newColor = JColorChooser.showDialog(ideInstance, 
                                "Choose a color ...",
                                colorMap.getMaxColor());
                        
                       if (newColor != null) {
                           if (!color.equals(newColor)) {
                                colorMap.getColorRange().set(
                                        colorList.getSelectedIndex(), newColor);
                                
                                colorMap.setColorRange(
                                            colorMap.getColorRange());
                                colorList.setListData(
                                        colorMap.getColorRange().toArray());
                                MoleculeViewerFrame.this.repaint();
                           } // end if
                       } // end if 
                   }
                });
                colorButtonPanel2.add(editColor);
                
                moveUpColor = new JButton("Move up");
                moveUpColor.setMnemonic('u');
                moveUpColor.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       Color color = (Color) colorList.getSelectedValue();
                       if (color == null) return;
                       
                       int idx = colorList.getSelectedIndex();
                       
                       if (idx == 0) return;
                       
                       Color flipColor = colorMap.getColorRange().get(idx-1);
                               
                       colorMap.getColorRange().set(idx-1, color);
                       colorMap.getColorRange().set(idx, flipColor);
                       colorMap.setColorRange(colorMap.getColorRange());
                       colorList.setListData(
                                        colorMap.getColorRange().toArray());
                       MoleculeViewerFrame.this.repaint();
                   }
                });
                colorButtonPanel2.add(moveUpColor);
                
                moveDownColor = new JButton("Move down");
                moveDownColor.setMnemonic('d');
                moveDownColor.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       Color color = (Color) colorList.getSelectedValue();
                       if (color == null) return;
                       
                       int idx = colorList.getSelectedIndex();
                       
                       if (idx == colorMap.getColorRange().size()-1) return;
                       
                       Color flipColor = colorMap.getColorRange().get(idx+1);
                               
                       colorMap.getColorRange().set(idx+1, color);
                       colorMap.getColorRange().set(idx, flipColor);
                       colorMap.setColorRange(colorMap.getColorRange());
                       colorList.setListData(
                                        colorMap.getColorRange().toArray());
                       MoleculeViewerFrame.this.repaint();
                   }
                });
                colorButtonPanel2.add(moveDownColor);
                
                saveToFile = new JButton("Save to file...");
                saveToFile.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       IDEFileChooser fileChooser = new IDEFileChooser();
                       
                       if (fileChooser.showSaveDialog(ideInstance)
                            == IDEFileChooser.APPROVE_OPTION) {
                           try {
                               Utility.saveColorMapToFile(colorMap,
                                        fileChooser.getSelectedFile());
                           } catch (Exception e) {
                               System.err.println(e.toString());
                               e.printStackTrace();
                           } // end try .. catch block
                       } // end if
                   }
                });
                colorButtonPanel2.add(saveToFile);
                
                readFromFile = new JButton("Load from file...");
                readFromFile.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       IDEFileChooser fileChooser = new IDEFileChooser();
                       
                       if (fileChooser.showOpenDialog(ideInstance)
                            == IDEFileChooser.APPROVE_OPTION) {
                           try {
                               Utility.updateColorMapFromFile(colorMap,
                                        fileChooser.getSelectedFile());
                               
                               colorList.setListData(
                                        colorMap.getColorRange().toArray());
                               MoleculeViewerFrame.this.repaint();
                           } catch (Exception e) {
                               System.err.println(e.toString());
                               e.printStackTrace();
                           } // end try .. catch block
                       } // end if
                   }
                });
                colorButtonPanel2.add(readFromFile);
                
                multiColorPanel.add(colorButtonPanel2, BorderLayout.EAST);
                
                // color list                
                if (colorMap.getColorRange() != null) {
                    colorList = new JList(colorMap.getColorRange().toArray());   
                } else {
                    colorList = new JList();
                } // end if
                
                colorList.setCellRenderer(new ColorCellRenderer());
                colorList.setEnabled(false);
                colorList.addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        Color selectedColor = 
                                (Color) colorList.getSelectedValue();
                        
                        editColor.setBackground(selectedColor);
                        removeColor.setBackground(selectedColor);
                        moveUpColor.setBackground(selectedColor);
                        moveDownColor.setBackground(selectedColor);
                    }
                });
                multiColorPanel.add(new JScrollPane(colorList), 
                                    BorderLayout.CENTER);  
                
                colorList.setEnabled(false);
                addColor.setEnabled(false);
                removeColor.setEnabled(false);
                editColor.setEnabled(false);
                moveUpColor.setEnabled(false);
                moveDownColor.setEnabled(false);
                       
                multiColor = new JRadioButton("Multi color map");
                multiColor.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) { 
                       boolean enable = multiColor.isSelected();
                       colorList.setEnabled(enable);
                       addColor.setEnabled(enable);
                       removeColor.setEnabled(enable);
                       editColor.setEnabled(enable);
                       moveUpColor.setEnabled(enable);
                       moveDownColor.setEnabled(enable);
                       
                       enable = twoColor.isSelected();
                       
                       minColorButton.setEnabled(enable);
                       maxColorButton.setEnabled(enable);
                   }
                });
                colorGroup.add(multiColor);
                multiColorPanel.add(multiColor, BorderLayout.NORTH);  
                
            twoColor.setSelected(true);
            
            container.add(multiColorPanel, BorderLayout.CENTER);
        }
        
        /** Inner class implementing color cell rendering */
        private class ColorCellRenderer extends JLabel 
                                        implements ListCellRenderer {
           @Override
           public Component getListCellRendererComponent(
               JList list,
               Object value,         // value to display
               int index,            // cell index
               boolean isSelected,   // is the cell selected
               boolean cellHasFocus) // the list, cell have the focus
             {
               Color selection = (Color) value;
                
               setText(" ");
               setBackground(selection);
               setEnabled(list.isEnabled());
               setOpaque(true);
               
               return this;
           } 
        } // end of inner class ColorCellRenderer
    } // end of class ColorMapDialog
    
    /**
     * internal class to handle interface of managing properties attached
     * to a molecule scene.
     */
    private class ManagePropertiesDialog extends JDialog {
        
        private JList propertyList;
        private JLabel propertyTypeLabel;
        private JComboBox propertyType;
                
        /** Creates a new instance of ManagePropertiesDialog */
        public ManagePropertiesDialog(JFrame parent) {
            super(parent);
            
            initComponents();
            setSize(new Dimension(400, 350));
            setLocationRelativeTo(parent);
            setModal(true);
            setTitle("Manage Properties");
        }
        
        /**
         * Update controls
         */        
        public void updateControls() {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            // populate propertyList
            Vector<MoleculeScene> scenes = getSceneList();
            
            if ((scenes != null) && (scenes.size() == 1)) {
                MoleculeScene scene = scenes.get(0);
                Iterator<PropertyScene> ps = scene.getAllPropertyScene();
                
                if (ps == null) return;
                
                Vector<Object> p = new Vector<Object>();
                
                while(ps.hasNext()) p.add(ps.next());
                
                propertyList.setListData(p);
            } // end if
        }
        
        /**
         * initilize the components
         */
        private void initComponents() {
            Container container = getContentPane();
            
            container.setLayout(new BorderLayout());
            
            JPanel addRemovePanel = new JPanel(new BorderLayout());
            addRemovePanel.setBorder(BorderFactory.createTitledBorder(
                    "Add/ Edit/ Remove Properties: "));
            propertyList  = new JList();
            propertyList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent evt) {
                    PropertyScene pScene = 
                           (PropertyScene) propertyList.getSelectedValue();
                    
                    if (pScene == null) return;
                    
                    propertyTypeLabel.setText("Property type for <" +
                            pScene.getName() + "> : ");
                    
                    propertyType.setSelectedItem(pScene.getType());
                }
            });                        
            
            updateControls();
            addRemovePanel.add(propertyList, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 0, 5, 0);
            gbc.gridx = 0;
            gbc.gridy = 0;
            
            JButton attach, remove, edit, info, save;
            
            attach = new JButton("Attach...");
            attach.setToolTipText("Attach a new propery");
            attach.setMnemonic('A');
            attach.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   // we use the bean shell command here!
                   // for a great simplicity 
                   try { 
                        setCursor(
                           CursorResource.getInstance().getOpeningCursor());
                        Utility.executeBeanShellScript(
                                                "attachProperty()");
                   } catch (Exception ignored) {                        
                        ideInstance.setCursor(Cursor.getDefaultCursor());
                        System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                            ignored.printStackTrace();
                        
                        JOptionPane.showMessageDialog(ideInstance,
                            "Error while attaching property file. " 
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while attaching property!",
                            JOptionPane.ERROR_MESSAGE);
                   } // end try .. catch block
               }
            });
            buttonPanel.add(attach, gbc);            
            
            remove = new JButton("Remove");
            remove.setToolTipText("Remove a selected propery");
            remove.setMnemonic('R');
            remove.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   Vector<MoleculeScene> scenes = getSceneList();
                   
                   // we can handle only one-to-one relations at this time!
                   if (scenes.size() > 1) return;
                    
                   // get the current selection from JList
                   PropertyScene pScene = 
                           (PropertyScene) propertyList.getSelectedValue();
                   
                   if (pScene == null) return;
                   
                   // if it is there, remove it
                   scenes.get(0).removePropertyScene(pScene);    
                   updateControls();
               }
            });            
            gbc.gridy = 1;
            buttonPanel.add(remove, gbc);
                        
            edit = new JButton("Edit...");
            edit.setToolTipText("Edit name of a selected propery");
            edit.setMnemonic('E');
            edit.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   // get the current selection from JList
                   PropertyScene pScene = 
                           (PropertyScene) propertyList.getSelectedValue();
                   
                   if (pScene == null) return;
                   
                   // if we have it, get its name and present to user
                   // for changing
                   String result = JOptionPane.showInputDialog(
                           ManagePropertiesDialog.this, 
                           "Enter a new name for property: " + pScene.getName(), 
                           pScene.getName());
                   
                   if (result != null) pScene.setName(result);
                   
                   updateControls();
               }
            });
            gbc.gridy = 2;
            buttonPanel.add(edit, gbc);
            
            info = new JButton("Info...");
            info.setToolTipText("Get additional information about this " +
                    "property...");
            info.setMnemonic('I');
            info.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                  // get the current selection from JList
                  PropertyScene pScene = 
                          (PropertyScene) propertyList.getSelectedValue();
                   
                  if (pScene == null) return;
                
                  GridProperty gp = pScene.getGridProperty();
                  DecimalFormat df = new DecimalFormat("#.#######");
                    
                  // construct the details string
                  StringBuffer detailString = new StringBuffer();
                  detailString.append("<html><head></head><body>");
                  detailString.append("<u>Property Range:</u><br>");
                  detailString.append("<table border=\"1\">");
                  detailString.append("<tr>");
                  detailString.append("<td> </td>");
                  detailString.append("<td><b>Min</b></td>");
                  detailString.append("<td><b>Max</b></td>");
                  detailString.append("<td><b>Step size</b></td>");
                  detailString.append("<td><b>No. of points</b></td>");
                  detailString.append("</tr>");
                  detailString.append("<tr>");
                  detailString.append("<td><b>X</b></td>");
                  detailString.append("<td>" + 
                        df.format(gp.getBoundingBox().getUpperLeft().getX())
                        + "</td>");
                  detailString.append("<td>" + 
                        df.format(gp.getBoundingBox().getBottomRight().getX())
                        + "</td>");
                  detailString.append("<td>" + df.format(gp.getXIncrement())
                                      + "</td>");
                  detailString.append("<td>" + gp.getNoOfPointsAlongX()
                                      + "</td>");
                  detailString.append("</tr>");
                  detailString.append("<tr>");
                  detailString.append("<td><b>Y</b></td>");
                  detailString.append("<td>" + 
                        df.format(gp.getBoundingBox().getUpperLeft().getY())
                        + "</td>");
                  detailString.append("<td>" + 
                        df.format(gp.getBoundingBox().getBottomRight().getY())
                        + "</td>");
                  detailString.append("<td>" + df.format(gp.getYIncrement())
                                      + "</td>");
                  detailString.append("<td>" + gp.getNoOfPointsAlongY()
                                      + "</td>");
                  detailString.append("</tr>");
                  detailString.append("<tr>");
                  detailString.append("<td><b>Z</b></td>");
                  detailString.append("<td>" + 
                        df.format(gp.getBoundingBox().getUpperLeft().getZ())
                        + "</td>");
                  detailString.append("<td>" + 
                        df.format(gp.getBoundingBox().getBottomRight().getZ())
                        + "</td>");
                  detailString.append("<td>" + df.format(gp.getZIncrement())
                                      + "</td>");
                  detailString.append("<td>" + gp.getNoOfPointsAlongZ()
                                      + "</td>");
                  detailString.append("</tr>");
                  detailString.append("</table>");    
                  
                  detailString.append("<br><u>Function value summary:</u><br>");
                  detailString.append("<table border=\"0\">");
                  detailString.append("<tr>");
                  detailString.append("<td><b>Min function value:</b></td>");
                  detailString.append("<td>" 
                                      + gp.getMinFunctionValue()
                                      + "</td>");
                  detailString.append("</tr>");
                  detailString.append("<tr>");
                  detailString.append("<td><b>Max function value:</b></td>");
                  detailString.append("<td>" 
                                      + gp.getMaxFunctionValue()
                                      + "</td>");
                  detailString.append("</tr>");
                  detailString.append("<tr>");
                  detailString.append("<td><b>Total no. of " +
                                      "function values:</b></td>");
                  detailString.append("<td>" + gp.getNumberOfGridPoints()
                                      + "</td>");
                  detailString.append("</tr>");
                  detailString.append("<td><b>No. of positive " +
                                      "function values:</b></td>");
                  detailString.append("<td>" 
                                       + gp.getNoOfPositiveFunctionValues()
                                       + "</td>");
                  detailString.append("</tr>");
                  detailString.append("<tr>");
                  detailString.append("<td><b>No. of negative " +
                                      "function values:</b></td>");
                  detailString.append("<td>"
                                      + gp.getNoOfNegativeFunctionValues()
                                      + "</td>");
                  detailString.append("</tr>");
                  detailString.append("</table>");
                  detailString.append("</body></html>");
                  
                  JOptionPane.showMessageDialog(ManagePropertiesDialog.this, 
                         detailString.toString(), "Information about property: " 
                         + pScene.getName(), JOptionPane.INFORMATION_MESSAGE);
               }
            });
            gbc.gridy = 3;
            buttonPanel.add(info, gbc);
            
            save = new JButton("Save...");
            save.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                  // get the current selection from JList
                  PropertyScene pScene = 
                          (PropertyScene) propertyList.getSelectedValue();
                   
                  if (pScene == null) return;
                  
                  // we use the bean shell command here!
                  // for a great simplicity 
                  try {                   
                        Utility.executeBeanShellScript(
                                                "saveProperty(\"" +
                                propertyList.getSelectedIndex() + "\")");
                  } catch (Exception ignored) {                        
                        ideInstance.setCursor(Cursor.getDefaultCursor());
                        System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                            ignored.printStackTrace();

                        JOptionPane.showMessageDialog(ideInstance,
                            "Error while saving property file. " 
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while saving property!",
                            JOptionPane.ERROR_MESSAGE);
                  } // end try .. catch block 
               }
            });
            gbc.gridy = 4;
            buttonPanel.add(save, gbc);
            
            addRemovePanel.add(buttonPanel, BorderLayout.EAST);
                        
            container.add(addRemovePanel, BorderLayout.CENTER);
                        
            JPanel properyTypePanel = new JPanel(new FlowLayout());
            
            propertyTypeLabel = new JLabel("Property type for <> : ");
            properyTypePanel.add(propertyTypeLabel);
            
            propertyType = new JComboBox();
            propertyType.setEditable(false);
                         
            for(PropertyScene.PropertySceneType pType
                                   : PropertyScene.PropertySceneType.values()) {
                propertyType.addItem(pType);
            } // end for
                        
            propertyTypeLabel.setLabelFor(propertyType);
            propertyType.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {                    
                    PropertyScene pScene = 
                           (PropertyScene) propertyList.getSelectedValue();
                    
                    if (pScene == null) return;
                    
                    pScene.setType((PropertyScene.PropertySceneType)
                                    propertyType.getSelectedItem());
                }
            });
            properyTypePanel.add(propertyType);
            
            container.add(properyTypePanel, BorderLayout.SOUTH);
        }       
    } // end of class ManagePropertiesDialog

    /**
     * internal class to handle interface of managing properties attached
     * to a molecule scene.
     */
    private class GenerateSubPropertyDialog extends JDialog {
        
        private BoundingBox newBoundingBox;
        
        private JComboBox propertyList;
        private MoleculeScene targetScene;
                
        /** Creates a new instance of ManagePropertiesDialog */
        public GenerateSubPropertyDialog(JFrame parent,
                                         BoundingBox newBoundingBox) {
            super(parent);
            
            this.newBoundingBox = newBoundingBox;
            
            initComponents();            
            setLocationRelativeTo(parent);
            setModal(true);
            setTitle("Generate Sub Poperty");
            pack();
            setVisible(true);            
        }
        
        /** 
         * initilize the components
         */
        private void initComponents() {
            Container container = getContentPane();
            
            container.setLayout(new BorderLayout());
            
            // populate propertyList
            Vector<MoleculeScene> scenes = getSceneList();
            
            if ((scenes != null) && (scenes.size() == 1)) {
                MoleculeScene scene = scenes.get(0);
                targetScene = scene;
                
                Iterator<PropertyScene> ps = scene.getAllPropertyScene();
                
                if (ps == null) {
                    container.add(new JLabel("There are no properties loaded" +
                            " to apply this operation!"), BorderLayout.CENTER);
                    return;
                } // end if
                
                Vector<Object> p = new Vector<Object>();
                
                while(ps.hasNext()) p.add(ps.next());
                
                propertyList = new JComboBox(p.toArray());
                propertyList.setEditable(false);
                
                JPanel propPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                propPanel.add(new JLabel("Choose a property from which a " +
                        "sub property will be derived:"));
                propPanel.add(propertyList);
                
                container.add(propPanel, BorderLayout.CENTER);
                
                JButton ok = new JButton("Ok");
                ok.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae)  {
                       PropertyScene scene = 
                               (PropertyScene) propertyList.getSelectedItem();
                       
                       GridProperty newGP = scene.getGridProperty()
                                                 .subProperty(newBoundingBox);
                
                       targetScene.addPropertyScene(new PropertyScene(
                                                        targetScene, newGP));
                       
                       setVisible(false);
                       dispose();
                   }
                });
                container.add(ok, BorderLayout.SOUTH);                
            } // end if
        }
    } // end of class GenerateSubPropertyDialog
    
    /**
     * Holds value of property showMoleculeRelatedUI.
     */
    private boolean showMoleculeRelatedUI;

    /**
     * Getter for property showMoleculeRelatedUI.
     * @return Value of property showMoleculeRelatedUI.
     */
    public boolean isShowMoleculeRelatedUI() {
        return this.showMoleculeRelatedUI;
    }

    /**
     * Setter for property showMoleculeRelatedUI.
     * @param showMoleculeRelatedUI New value of property showMoleculeRelatedUI.
     */
    public void setShowMoleculeRelatedUI(boolean showMoleculeRelatedUI) {
        this.showMoleculeRelatedUI = showMoleculeRelatedUI;
                
        selectionTool.setVisible(showMoleculeRelatedUI);
        movieMode.setVisible(showMoleculeRelatedUI);
        propertyPanelButton.setVisible(showMoleculeRelatedUI);
        moleculeViewerFindTool.setVisible(showMoleculeRelatedUI);
        moleculeViewer.setShowViewerPopup(showMoleculeRelatedUI);
        
        if (showMoleculeRelatedUI) {
            MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().activate("Molecule related Tasks");
        } else {
            MoleculeViewerFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().deactivate("Molecule related Tasks");
        } // end if
    }

    /**
     * Form the grid generator panel
     */
    private void initGridSidePanel() {
        if (gridGeneratorSidePanel != null) return;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH; 
        
        gridGeneratorSidePanel = new SidePanel("Grid generator:");
        JComponent gridContentPane = gridGeneratorSidePanel.getContentPane();        
        gridContentPane.setLayout(new GridBagLayout());
        
        JPanel lengthPanel = new JPanel(new GridBagLayout());
        lengthPanel.setBorder(
                       BorderFactory.createTitledBorder("Grid side length:"));
         
           gbc.gridx = 0;
           gbc.gridy = 0;
           gbc.gridwidth = 1;           
           equalLength = new JRadioButton("Equal length");
           lengthPanel.add(equalLength, gbc);
                      
           gbc.gridx = 1;
           variableLength = new JRadioButton("Variable length");
           lengthPanel.add(variableLength, gbc);
           
           ButtonGroup lengthGroup = new ButtonGroup();
           lengthGroup.add(equalLength);
           lengthGroup.add(variableLength);
           variableLength.setSelected(true);
           
           gbc.gridx = 0;
           gbc.gridy = 1;
           gbc.gridwidth = 1;           
           lengthPanel.add(new JLabel("X: ", JLabel.RIGHT), gbc);
           gridLengthXModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
           gridLengthX = new JSpinner(gridLengthXModel); 
           gridLengthX.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (equalLength.isSelected()) {
                        Object currentValue = gridLengthXModel.getValue();
                        gridLengthYModel.setValue(currentValue);
                        gridLengthZModel.setValue(currentValue); 
                    } // end if
                    
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 1;
           lengthPanel.add(gridLengthX, gbc);
           
           gbc.gridx = 0;
           gbc.gridy = 2;           
           lengthPanel.add(new JLabel("Y: ", JLabel.RIGHT), gbc);
           gridLengthYModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
           gridLengthY = new JSpinner(gridLengthYModel);
           gridLengthY.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (equalLength.isSelected()) {
                        Object currentValue = gridLengthYModel.getValue();
                        gridLengthXModel.setValue(currentValue);
                        gridLengthZModel.setValue(currentValue);
                    } // end if
                    
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 1;
           lengthPanel.add(gridLengthY, gbc);
           
           gbc.gridx = 0;
           gbc.gridy = 3;
           gbc.gridwidth = 1;           
           lengthPanel.add(new JLabel("Z: ", JLabel.RIGHT), gbc);
           gridLengthZModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
           gridLengthZ = new JSpinner(gridLengthZModel);
           gridLengthZ.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (equalLength.isSelected()) {
                        Object currentValue = gridLengthZModel.getValue();
                        gridLengthXModel.setValue(currentValue);
                        gridLengthYModel.setValue(currentValue);
                    } // end if
                    
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 1;
           lengthPanel.add(gridLengthZ, gbc);
           
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;           
        gridContentPane.add(lengthPanel, gbc);        
                   
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(
                       BorderFactory.createTitledBorder("Grid center:"));                    
           
           gbc.gridx = 0;
           gbc.gridy = 0;
           gbc.gridwidth = 2;
           centerPanel.add(new JLabel("X: ", JLabel.RIGHT), gbc);
           gridCenterXModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
           gridCenterX = new JSpinner(gridCenterXModel);
           gridCenterX.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 2;
           gbc.gridwidth = 1;
           centerPanel.add(gridCenterX, gbc);
           
           gbc.gridx = 0;
           gbc.gridy = 1;    
           gbc.gridwidth = 2;
           centerPanel.add(new JLabel("Y: ", JLabel.RIGHT), gbc);
           gridCenterYModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
           gridCenterY = new JSpinner(gridCenterYModel);
           gridCenterY.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 2;
           gbc.gridwidth = 1;
           centerPanel.add(gridCenterY, gbc);
           
           gbc.gridx = 0;
           gbc.gridy = 2;    
           gbc.gridwidth = 2;
           centerPanel.add(new JLabel("Z: ", JLabel.RIGHT), gbc);
           gridCenterZModel = new SpinnerNumberModel(0.0, 0.0, 0.0, 0.1);
           gridCenterZ = new JSpinner(gridCenterZModel);
           gridCenterZ.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 2;
           gbc.gridwidth = 1;
           centerPanel.add(gridCenterZ, gbc);
           
           gbc.gridx = 0;
           gbc.gridy = 3;           
           gbc.gridwidth = 3;           
           defaultCenter = new JButton("Restore default grid center");
           defaultCenter.setToolTipText("Click to restore the default center" +
                   ", generally the center of mass (COM) of molecule.");
           defaultCenter.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   Point3D center = moleculeViewer.getSceneList()
                                       .get(0).getMolecule().getCenterOfMass();
                   gridCenterXModel.setValue(new Double(center.getX()));
                   gridCenterYModel.setValue(new Double(center.getY()));
                   gridCenterZModel.setValue(new Double(center.getZ()));
               }
           });
           centerPanel.add(defaultCenter, gbc);
           
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;           
        gridContentPane.add(centerPanel, gbc);                
        
        JPanel pointsPanel = new JPanel(new GridBagLayout());
        pointsPanel.setBorder(BorderFactory.createTitledBorder("Grid points:"));
         
           gbc.gridx = 0;
           gbc.gridy = 0;
           gbc.gridwidth = 1;           
           equalPoints = new JRadioButton("Equal points");
           pointsPanel.add(equalPoints, gbc);
                      
           gbc.gridx = 1;
           variablePoints = new JRadioButton("Variable points");
           pointsPanel.add(variablePoints, gbc);
           
           ButtonGroup pointGroup = new ButtonGroup();
           pointGroup.add(equalPoints);
           pointGroup.add(variablePoints);
           equalPoints.setSelected(true);
           
           gbc.gridx = 0;
           gbc.gridy = 1;
           gbc.gridwidth = 1;           
           pointsPanel.add(new JLabel("X: ", JLabel.RIGHT), gbc);
           pointsXModel = new SpinnerNumberModel(DEFAULT_GRID_POINTS, 
                                                 1, MAX_GRID_POINTS, 1);
           pointsX = new JSpinner(pointsXModel);
           pointsX.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (equalPoints.isSelected()) {
                        Object currentValue = pointsXModel.getValue();
                        pointsYModel.setValue(currentValue);
                        pointsZModel.setValue(currentValue);
                    } // end if
                    
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 1;
           pointsPanel.add(pointsX, gbc);
           
           gbc.gridx = 0;
           gbc.gridy = 2;           
           pointsPanel.add(new JLabel("Y: ", JLabel.RIGHT), gbc);
           pointsYModel = new SpinnerNumberModel(DEFAULT_GRID_POINTS, 
                                                 1, MAX_GRID_POINTS, 1);
           pointsY = new JSpinner(pointsYModel);
           pointsY.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (equalPoints.isSelected()) {
                        Object currentValue = pointsYModel.getValue();
                        pointsXModel.setValue(currentValue);
                        pointsZModel.setValue(currentValue);
                    } // end if
                    
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 1;
           pointsPanel.add(pointsY, gbc);
           
           gbc.gridx = 0;
           gbc.gridy = 3;
           gbc.gridwidth = 1;           
           pointsPanel.add(new JLabel("Z: ", JLabel.RIGHT), gbc);
           pointsZModel = new SpinnerNumberModel(DEFAULT_GRID_POINTS,
                                                 1, MAX_GRID_POINTS, 1);
           pointsZ = new JSpinner(pointsZModel);
           pointsZ.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    if (equalPoints.isSelected()) {
                        Object currentValue = pointsZModel.getValue();
                        pointsXModel.setValue(currentValue);
                        pointsYModel.setValue(currentValue);
                    } // end if
                    
                    GridProperty gp = moleculeViewer.getScreenGridProperty();
                    updateGridProperty(gp);
                    moleculeViewer.setScreenGridProperty(gp);
                }
           });
           gbc.gridx = 1;
           pointsPanel.add(pointsZ, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;            
        gridContentPane.add(pointsPanel, gbc);       
        
        logRangeDetails = new JButton("Log Range Details");
        logRangeDetails.setToolTipText("Click to generate a log of grid range" +
                " and its details.");
        logRangeDetails.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {
               DecimalFormat fmt = new DecimalFormat("#.####");
               GridProperty gp = moleculeViewer.getScreenGridProperty();
               Point3D minPoint = gp.getBoundingBox().getUpperLeft();
               
               double ox = minPoint.getX()/Utility.AU_TO_ANGSTROM_FACTOR,
                      oy = minPoint.getY()/Utility.AU_TO_ANGSTROM_FACTOR,
                      oz = minPoint.getZ()/Utility.AU_TO_ANGSTROM_FACTOR; 
               
               double stepX = gp.getXIncrement()/Utility.AU_TO_ANGSTROM_FACTOR,
                      stepY = gp.getYIncrement()/Utility.AU_TO_ANGSTROM_FACTOR,
                      stepZ = gp.getZIncrement()/Utility.AU_TO_ANGSTROM_FACTOR;
               
               System.out.println("Range in a.u. :");
               System.out.println("RANGE");
               System.out.println(
                 fmt.format(ox) + " "
                +fmt.format(oy) + " "
                +fmt.format(oz));
               System.out.println(
                 fmt.format(stepX) + " "
                +fmt.format(stepY) + " "
                +fmt.format(stepZ));
               System.out.println(
                 (gp.getNoOfPointsAlongX()) + " "
                +(gp.getNoOfPointsAlongY()) + " "
                +(gp.getNoOfPointsAlongZ()));
               
               double step = Math.min(stepX, Math.min(stepY, stepZ));
               
               System.out.println("$grid modgrd=1 units=bohr size=" + fmt.format(step));
               System.out.println(" origin(1)=" + fmt.format(ox) + "," 
                                  + fmt.format(oy) + "," + fmt.format(oz));
               System.out.println(" xvec(1)=" + fmt.format(ox + (step * gp.getNoOfPointsAlongX())) + "," 
                                  + fmt.format(oy) + "," 
                                  + fmt.format(oz));
               System.out.println(" yvec(1)=" + fmt.format(ox) + "," 
                                  + fmt.format(oy + (step * gp.getNoOfPointsAlongY())) + "," 
                                  + fmt.format(oz));
               System.out.println(" zvec(1)=" + fmt.format(ox) + "," 
                                  + fmt.format(oy) + "," 
                                  + fmt.format(oz + (step + gp.getNoOfPointsAlongZ())));
               System.out.println("$end");
           }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;          
        gbc.gridwidth = 1;           
        gridContentPane.add(logRangeDetails, gbc);
        
        generateSubProperty = new JButton("Generate Sub Property");
        generateSubProperty.setToolTipText("Generate a new Sub Property " +
                "based on the currently defined grid.");
        generateSubProperty.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {
                new GenerateSubPropertyDialog(ideInstance,
                       moleculeViewer.getScreenGridProperty().getBoundingBox());
           }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;          
        gbc.gridwidth = 1;           
        gridContentPane.add(generateSubProperty, gbc);
                
        gridGeneratorLabel = new JLabel("");
        gridGeneratorLabel.setToolTipText("<html><body>" +
            "<ul><li>Min X,Y,Z: is mimimum point coordinates of bounding box." +
            "</li><li>Step X,Y,Z: is step size along each axis." +
            "</li><li>Extent X,Y,Z: is differene in width along each axis of " +
                "original and the user modified bounding box." +
            "</body><html>");
        gbc.gridx = 0;
        gbc.gridy = 5;      
        gbc.weightx = 1;
        gbc.weighty = 1;  
        gridContentPane.add(gridGeneratorLabel, gbc);               
        
        gridGeneratorSidePanel.setVisible(false);
        gridGeneratorSidePanel.addCloseButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane.setDividerLocation(1.0);
                updateUI();
            }
        });
    }

    /**
     * Holds value of property multiViewEnabled.
     */
    private boolean multiViewEnabled;

    /**
     * Getter for property multiViewEnabled.
     * @return Value of property multiViewEnabled.
     */
    public boolean isMultiViewEnabled() {
        return this.multiViewEnabled;
    }

    /**
     * Setter for property multiViewEnabled.
     * @param multiViewEnabled New value of property multiViewEnabled.
     */
    public void setMultiViewEnabled(boolean multiViewEnabled) {
        this.multiViewEnabled = multiViewEnabled;
        
        if (multiViewEnabled) {
            if (multiView == null) {
                multiView = new MultiMoleculeViewPanel();
                multiView.addMoleculeViewer(moleculeViewer);
                multiView.addMultiMoleculeViewChangeListerner(this);
            } // end if
                            
            multiViewScrollPane = new JScrollPane(multiView, 
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            getContentPane().add(multiViewScrollPane, 
                                     BorderLayout.WEST);    
            
            multiView.setVisible(true);
            updateUI();
        } else { 
            if (multiView != null) {
                multiViewScrollPane.setVisible(false);
                multiView.setVisible(false);
                getContentPane().remove(multiViewScrollPane);
            } // end if
            updateUI();
        } // end if
        
        moleculeViewer.setMultiViewEnabled(multiViewEnabled);
    }
    
    /**
     * add a new view molecule in case multi view is enabled
     */
    public void addNewMoleculeView() {
        if (!isMultiViewEnabled()) return;
        
        if (multiView == null) return;
        
        multiView.addMoleculeViewer(moleculeViewer);
        updateUI();
    }

    /**
     * The interface for call back on a view change request
     *
     * @param mmvce the object of the change event
     */
    @Override
    public void moleculeViewChanged(MultiMoleculeViewChangeEvent mmvce) {
        MoleculeViewer mv = mmvce.getMoleculeViewer();
        MoleculeViewer newMV = new MoleculeViewer(mv.getIDEInstance(),
                                                  mv.getParentFrame());
        
        newMV.disableUndo();
        int sceneIndex = 0;
        for(MoleculeScene scene : mv.getSceneList()) {
           MoleculeScene newScene = new MoleculeScene(scene.getMolecule());
           
           Iterator<PropertyScene> pScenes = scene.getAllPropertyScene();
           PropertyScene ps;
           
           // add all property scenes
           if (pScenes != null) {
             while(pScenes.hasNext()) {
               ps = pScenes.next();
               
               newScene.addPropertyScene(new PropertyScene(newScene, 
                                                     ps.getGridProperty()));
             } // end while
           } // end if
                      
           // the selection stack restore
           newScene.setSelectionStack(scene.getSelectionStack(), true);
           
           // and show the currently viewing fragments
           Molecule molecule = newScene.getMolecule();
           Iterator<FragmentationScheme> fs 
                            = molecule.getAllFragmentationSchemes();
           FragmentationScheme scheme;
           
           while(fs.hasNext()) {
               scheme = fs.next();
               
               Iterator<Fragment> frags 
                         = scheme.getFragmentList().getFragments();
               Fragment frag;
               
               while(frags.hasNext()) {
                   frag = frags.next();
                   
                   newScene.showFragment(scheme, frag,
                                moleculeViewer.getSceneList().get(sceneIndex)
                                        .isFragmentVisible(scheme, frag));
               } // end while
           } // end while
           
           newMV.addScene(newScene);
           sceneIndex++;
        } // end for                
       
        newMV.setRotationMatrix(mv.getRotationMatrix());
        newMV.setTranslateMatrix(mv.getTranslateMatrix());
        newMV.setScaleFactor(mv.getScaleFactor());
        newMV.setAxisDrawn(mv.isAxisDrawn());
        newMV.setTransformState(mv.getTransformState());        
        newMV.setShowViewerPopup(true);
        newMV.setMultiViewEnabled(true);
        newMV.enableUndo();
        
        moleculeViewer.removeUndoableEditListener(this);
        moleculeViewer.removePropertyInterrogataionListener(this);
        splitPane.remove(moleculeViewer);
        
        splitPane.setLeftComponent(newMV);
        newMV.removeUndoableEditListener(newMV);
        newMV.addUndoableEditListener(this);
        newMV.addUndoableEditListener(newMV);
        newMV.addPropertyInterrogataionListener(this);
        moleculeViewer = newMV;
        updateUI();
        
        if (newMV.getTransformState().equals(TransformState.SCALE_STATE)) {
            zoom.setSelected(true);
        } else if (newMV.getTransformState()
                        .equals(TransformState.TRANSLATE_STATE)) {
            translate.setSelected(true);
        } else if (newMV.getTransformState()
                        .equals(TransformState.ROTATE_STATE)) {
            rotate.setSelected(true);
        } // end if
        
        // update the find tool
        moleculeViewerFindTool.updateFindTragetSceneList();
        
        // update property panel
        boolean needToSwitchOn = false;
        if (propertySidePanel != null) {
            if (propertySidePanel.isVisible() && sidePanel.isVisible()) {
               propertySidePanel.setVisible(false);
               needToSwitchOn = true;
            } // end if
            propertySidePanel = null;
        } // end if
        currentProperty = null;
        initPropertySidePanel();
        if (needToSwitchOn) {
            sidePanel.removeAll();
            sidePanel.add(propertySidePanel);
            sidePanel.setVisible(true);
            propertySidePanel.setVisible(true);
        } // end if
        
        // add the saved set of properties
        for(MoleculeScene scene : newMV.getSceneList()) {
            Iterator<PropertyScene> pScenes = scene.getAllPropertyScene();

            if (pScenes == null) continue;
            
            while(pScenes.hasNext()) {
                currentProperty.addItem(pScenes.next());
            } // end while
        } // end for
    }

    /**
     * the interface for call back when an interrogation event occurs
     *
     * @param pie the interrogation even object
     */
    @Override
    public void interrogatedValue(PropertyInterrogataionEvent pie) {
        if (propertySidePanel == null) return;
        
        PointProperty pp = pie.getPointProperty();
        
        if (pp == null) return;
        
        ideInstance.getNotificationTray().notify("Interrogated function value",
                                                 pp.toString(), true);
    }

    /**
     * Dock a JPanel instance to this Frame.
     * 
     * @param dockPanel
     */
    @Override
    public void dockIt(String title, DockingPanel dockPanel) {
        if (sidePanel == null) {
           initSidePanels();
        } // end if
        
        dockPanel.setDockedFrame(this);
        
        sidePanel.removeAll();
        sidePanel.add(new JScrollPane(dockPanel), BorderLayout.CENTER);
        sidePanel.setVisible(true);
        dockPanel.setVisible(true);
        splitPane.setDividerLocation(0.7);
        updateUI();
    }

    /**
     * Undock a previously docked JPanel
     */
    @Override
    public void unDock() {
        sidePanel.removeAll();
        sidePanel.setVisible(false);
    }
} // end of class MoleculeViewerFrame
