/*
 * MoleculeEditorFrame.java
 *
 * Created on August 12, 2007, 12:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.UndoableEditEvent;
import javax.vecmath.Point2d;
import org.meta.common.resource.CursorResource;
import org.meta.common.resource.ImageResource;
import org.meta.config.impl.AtomInfo;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.editors.impl.moleculeditor.BuildState;
import org.meta.shell.idebeans.editors.impl.moleculeditor.MoleculeEditor;
import org.meta.shell.idebeans.editors.impl.moleculeditor.undo.AtomSymbolUndoableEdit;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.SelectionState;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.TransformState;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo.BondUndoableEdit;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo.SelectionUndoableEdit;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.application.JChemPaint;

/**
 * The internal frame interface that houses the MoleculeEditor for interfacing
 * with rest of the MeTA Studio UI.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeEditorFrame extends MoleculeViewerFrame {
    
    /** the instance of IDE of which this is a part */
    private MeTA ideInstance;
    
    /** the instance of MoleculeEditor which this frame is enclosing */
    private MoleculeEditor moleculeEditor;        
    
    /** the local tool bar */
    private JToolBar toolBar, drawToolBar;
    private JToggleButton zoom, translate, rotate, localTransform;    
    private JComboBox selectionTool;
    private JToggleButton fullscreen;
    private JButton undo, redo, globalReset;
    private ButtonGroup toolMode;
    private MemoryMonitor memoryMonitor;
    
    private JButton pencilTool, straightChainTool, planarRingTool, 
                    nonPlanarRingTool;
    private JToggleButton stockTools;    
    private JButton removeAtom;
    private JComboBox changeAtom;
    private JComboBox editBond;
    private JCheckBox showContextWizard;
    private JComboBox defaultAtom;
    private JComboBox defaultBond; 
        
    private CursorResource cursors;
    
    /** for full screen activity */
    private GraphicsDevice device; 
    private JFrame theFullScreen;
    private JDesktopPane screenPane;
    private Container originalParent;
    
    /** the cool find tool */
    private MoleculeViewerFindTool moleculeViewerFindTool;
    
    /** the counters */
    private static int moleculeEditorCount = 1;
    private int selfMoleculeEditorCount;
    
    /** 2D molecule editor pane */
    private JChemPaintPanel jChemPaintPanel;
    
    /** the current selection stack */
    private Stack<ScreenAtom> currentSelectionStack = null;
            
    /** Creates a new instance of MoleculeEditorFrame */
    public MoleculeEditorFrame(MeTA ideInstance) {
        setTitle("Molecule Editor - [" + moleculeEditorCount + "]"); 
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        
        this.selfMoleculeEditorCount = moleculeEditorCount;
        moleculeEditorCount++;
        
        this.ideInstance = ideInstance;
        
        GraphicsEnvironment env = GraphicsEnvironment.
                                          getLocalGraphicsEnvironment();
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
        
        // init all the editor related UI
        initComponents();
        
        setVisible(true);
        
        theFullScreen = new JFrame(device.getDefaultConfiguration());
        screenPane = new JDesktopPane();                        
        
        theFullScreen.getContentPane().setLayout(new BorderLayout());
        theFullScreen.getContentPane().add(screenPane);
        theFullScreen.setIconImage(ideInstance.getIconImage());
        theFullScreen.setTitle(ideInstance.getTitle() + " - (Fullscreen mode)");
        
        // add undo listener .. the sequence is purposely changed!
        moleculeEditor.removeUndoableEditListener(moleculeEditor);
        moleculeEditor.addUndoableEditListener(this);
        moleculeEditor.addUndoableEditListener(moleculeEditor);
        
        // set initial state for molecular viewer
        moleculeEditor.setSelectionState(SelectionState.NO_STATE);
        moleculeEditor.setTransformState(TransformState.SCALE_STATE);
        
        // frame listener .. for just resetting the status bar
        // and other UI
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                MoleculeEditorFrame.this.ideInstance.getStatusBar()
                                                    .setStatusText("Ready.");
                // hide the task list
                MoleculeEditorFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().deactivate("Molecule related Tasks");
                MoleculeEditorFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeEditorWorkflowDinamicVisibility(false);
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                // show the task list
                MoleculeEditorFrame.this.ideInstance.getWorkspaceExplorer()
                            .getTaskPanel().activate("Molecule related Tasks");
                MoleculeEditorFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeEditorWorkflowDinamicVisibility(true);
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                // hide the task list
                MoleculeEditorFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().deactivate("Molecule related Tasks");
                MoleculeEditorFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeEditorWorkflowDinamicVisibility(false);
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                MoleculeEditorFrame.this.ideInstance.getStatusBar()
                                                    .setStatusText("Ready.");

                // hide the task list
                MoleculeEditorFrame.this.ideInstance.getWorkspaceExplorer()
                          .getTaskPanel().deactivate("Molecule related Tasks");
                MoleculeEditorFrame.this.ideInstance.getWorkflowBar()
                          .setMoleculeEditorWorkflowDinamicVisibility(false);

                // remove the listeners
                Vector<MoleculeScene> sceneList = moleculeEditor.getSceneList();

                if (sceneList == null) return;

                Iterator<MoleculeScene> scenes = sceneList.iterator();

                while(scenes.hasNext()) {
                  scenes.next()
                   .removeMoleculeSceneChangeListener(MoleculeEditorFrame.this);
                } // end while

                moleculeEditor.removeUndoableEditListener(
                                                      MoleculeEditorFrame.this);

                // remove all elements to assist garbage collection
                MoleculeEditorFrame.this.removeAll();
                moleculeEditor.clearAllReferences();
                moleculeEditor         = null;
                moleculeViewerFindTool = null;

                memoryMonitor.setStopUpdate(true);
            }
        });
    }

    /**
     * init the components related to this frame
     */
    private void initComponents() {
        Container contentPane = getContentPane();
        
        contentPane.setLayout(new BorderLayout());
        
        // create editor 
        moleculeEditor = new MoleculeEditor(ideInstance, this);                
        
        // make the main tool bar
        toolBar = new JToolBar();
        toolBar.setRollover(true);
        toolBar.setFloatable(true);        
        
        // resources
        ImageResource images = ImageResource.getInstance();
        cursors = CursorResource.getInstance();
        
        // the buttons       
        toolMode = new ButtonGroup();
        
                zoom = new JToggleButton(images.getZoom());
        zoom.setToolTipText("Zoom, scale up or down");
        zoom.setMnemonic(KeyEvent.VK_S);
        zoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               ideInstance.getStatusBar().setStatusText("Drag the mouse"
               + "/ mouse wheel up or down to zoom in or zoom out the object.");
               selectionTool.setToolTipText("Choose a selection tool");
               moleculeEditor.setCursor(cursors.getZoomCursor());
               moleculeEditor.setTransformState(TransformState.SCALE_STATE);               
               ideInstance.getNotificationTray().notify(getTitle(), 
                                         TransformState.SCALE_STATE.toString());
            }
        });
        zoom.setSelected(true);
        // the initial state
        ideInstance.getStatusBar().setStatusText("Drag the mouse/ mouse wheel "
                 + "up or down to zoom in or zoom out the object.");
        moleculeEditor.setCursor(cursors.getZoomCursor());               
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
               selectionTool.setToolTipText("Choose a selection tool");
               moleculeEditor.setCursor(
                        Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
               moleculeEditor.setTransformState(TransformState.TRANSLATE_STATE);
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
               selectionTool.setToolTipText("Choose a selection tool");
               moleculeEditor.setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
               moleculeEditor.setTransformState(TransformState.ROTATE_STATE);
               ideInstance.getNotificationTray().notify(getTitle(), 
                                        TransformState.ROTATE_STATE.toString());
            }
        }); 
        toolMode.add(rotate);
        toolBar.add(rotate);
                
        BufferedImage locImage = new BufferedImage(images.getPlanarRing().getIconWidth(),
                                                   images.getPlanarRing().getIconHeight(),
                                                   BufferedImage.TYPE_INT_ARGB);
        Graphics locGraphics = locImage.getGraphics();
        locGraphics.drawImage(images.getFragment().getImage(), 0, 0, null);        
        locGraphics.drawImage(images.getRotate().getImage(), 0, 0, null);        
        localTransform = new JToggleButton(new ImageIcon(locImage));
        localTransform.setBorderPainted(false);
        localTransform.setToolTipText("Perform localized transform");
        localTransform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                moleculeEditor.setCursor(
                        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                moleculeEditor.setBulidState(BuildState.ORIENTATION_CHANGE);
                ideInstance.getNotificationTray().notify(getTitle(),
                                  BuildState.ORIENTATION_CHANGE.toString());
            }
        }); 
        toolMode.add(localTransform);
        toolBar.add(localTransform);
        
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
                    moleculeEditor.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    moleculeEditor.setSelectionState(
                            SelectionState.POINTER_SELECTION);
                    ideInstance.getNotificationTray().notify(getTitle(),
                            SelectionState.POINTER_SELECTION.toString());
                } else if (selectedItem.equals(images.getFreehand())) {
                    ideInstance.getStatusBar().setStatusText("Select multiple"
                            + " atoms. Use the selection for captions, " 
                            + "fragments etc...");
                    selectionTool.setToolTipText("Choose a selection tool " +
                            "(Current tool is free hand selection)");
                    moleculeEditor.setCursor(cursors.getPenCursor());
                    moleculeEditor.setSelectionState(
                            SelectionState.FREE_HAND_SELECTION);
                    ideInstance.getNotificationTray().notify(getTitle(),
                            SelectionState.FREE_HAND_SELECTION.toString());
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
                if (moleculeEditor.canUndo()) {
                    moleculeEditor.undo();
                } // end if
                undo.setEnabled(moleculeEditor.canUndo());
                redo.setEnabled(moleculeEditor.canRedo());
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
                if (moleculeEditor.canRedo()) {
                    moleculeEditor.redo();
                } // end if
                undo.setEnabled(moleculeEditor.canUndo());
                redo.setEnabled(moleculeEditor.canRedo());
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
               moleculeEditor.resetTransformState();
            }
        }); 
        toolBar.add(globalReset);                
        
        // full screen control
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
                        screenPane.add(MoleculeEditorFrame.this);
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
                        originalParent.add(MoleculeEditorFrame.this);
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
        
        // finally add the memory monitor, and hide it .. to be displayed only
        // in full screen mode
        toolBar.addSeparator();
        memoryMonitor = new MemoryMonitor();
        toolBar.add(memoryMonitor);        
        memoryMonitor.setVisible(false);  
        
        // the main tool bar        
        getContentPane().add(toolBar, BorderLayout.NORTH);
        
        // make the draw tool bar
        drawToolBar = new JToolBar();                
        
        pencilTool = new JButton(images.getPencilTool());
        pencilTool.setBorderPainted(false);
        pencilTool.setToolTipText("Draw single atom");
        pencilTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ideInstance.getStatusBar().setStatusText("Use the pencil " +
                            "tool to draw the indicate atom positions and " +
                            "bonds as you do it on paper.");
                selectionTool.setToolTipText("Choose a stock template " +
                        "(Current tool is pencil tool)");
                moleculeEditor.setCursor(
                        CursorResource.getInstance().getPencilCursor());
                moleculeEditor.setBulidState(BuildState.PENCIL_TOOL);
                ideInstance.getNotificationTray().notify(getTitle(),
                                  BuildState.PENCIL_TOOL.toString());
            }
        }); 
        drawToolBar.add(pencilTool);
        
        straightChainTool = new JButton(images.getStraightChain());
        straightChainTool.setBorderPainted(false);
        straightChainTool.setToolTipText("Draw straight chain (mostly Carbon)");
        straightChainTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ideInstance.getStatusBar().setStatusText("Drag mouse "
                            + " to generate the chain, default" 
                            + " atom is C (carbon).");
                selectionTool.setToolTipText("Choose a stock template " +
                        "(Current tool is straight chain)");
                moleculeEditor.setCursor(
                        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                moleculeEditor.setBulidState(BuildState.STRAIGHT_CHAIN);
                ideInstance.getNotificationTray().notify(getTitle(),
                                  BuildState.STRAIGHT_CHAIN.toString());
            }
        }); 
        drawToolBar.add(straightChainTool);
        
        planarRingTool = new JButton(images.getPlanarRing());
        planarRingTool.setBorderPainted(false);
        planarRingTool.setToolTipText("Draw planar ring");
        planarRingTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // TODO:
            }
        }); 
        drawToolBar.add(planarRingTool);
        
        nonPlanarRingTool = new JButton(images.getNonPlanarRing());
        nonPlanarRingTool.setBorderPainted(false);
        nonPlanarRingTool.setToolTipText("Draw non-planar ring");
        nonPlanarRingTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // TODO: 
            }
        }); 
        drawToolBar.add(planarRingTool);
        
        stockTools = new JToggleButton(images.getOptions());
        stockTools.setBorderPainted(false);
        stockTools.setToolTipText("Other stock templates");
        stockTools.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {                
                if (stockTools.isSelected()) {
                    
                    // TODO: need substantial improvement, ideal case would
                    // be enable user to portion of molecule to be edited in 2d
                    
                    if (sidePanel == null) {
                        initSidePanels();
                    } // end if

                    sidePanel.removeAll();
                    sidePanel.add(new JScrollPane(jChemPaintPanel), BorderLayout.CENTER);
                    sidePanel.setVisible(true);
                    splitPane.setDividerLocation(0.6);
                    updateUI();

                    jChemPaintPanel.setShowMenuBar(false);
                } else {
                    sidePanel.setVisible(false);
                    
                    // TODO: need substantial improvement, ideal case would
                    // be enable user to choose point of attachement
                    
                    IMoleculeSet ims = jChemPaintPanel.getChemModel().getMoleculeSet();
                    Molecule mol = moleculeEditor.getSceneList().get(0).getMolecule();

                    IMolecule imol = ims.getMolecule(0);

                    for (IAtom iatom : imol.atoms()) {
                        Point2d center = iatom.getPoint2d();
                    
                        System.out.println("center: " + center);
                        System.out.println("iatoms: " + iatom);
                        
                        mol.addAtom(iatom.getSymbol(), 
                                    center.x, center.y, 0.0);
                    } // end for
                } // end if
            }
        }); 
        drawToolBar.add(stockTools);                
        
        drawToolBar.addSeparator();                
        
        removeAtom = new JButton(images.getRemoveAtom());
        removeAtom.setEnabled(false);
        removeAtom.setBorderPainted(false);
        removeAtom.setToolTipText("Remove selected atom");
        removeAtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (currentSelectionStack == null) return;
                if (currentSelectionStack.size() != 1) return;
                
                Molecule mol = moleculeEditor.getSceneList()
                                             .get(0).getMolecule();
                mol.removeAtomAt(currentSelectionStack.get(0)
                                   .getAtom().getIndex());
                moleculeEditor.getSceneList().get(0).removeAllSelections();
                currentSelectionStack.removeAllElements();
            }
        }); 
        drawToolBar.add(removeAtom);
        
        final AtomInfo ai = AtomInfo.getInstance();
        
        changeAtom = new JComboBox(ai.getAtomicNumberTable().keySet().toArray());
        changeAtom.setEnabled(false);
        changeAtom.setSelectedItem("X");
        changeAtom.setToolTipText("Change selected atom");
        changeAtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (currentSelectionStack == null) return;
                if (currentSelectionStack.size() != 1) return;
                
                MoleculeScene ms = moleculeEditor.getSceneList().get(0);
                String oldSymbol = currentSelectionStack.get(0).getAtom().getSymbol();
                
                if (((String) changeAtom.getSelectedItem()).equals(oldSymbol)) return;
                
                currentSelectionStack.get(0).getAtom().setSymbol(
                                         (String) changeAtom.getSelectedItem());
                ms.moleculeChanged(null);
                ms.selectAtomIndex(
                        currentSelectionStack.get(0).getAtom().getIndex());
                moleculeEditor.repaint();
                
                changeAtom.setBackground(ai.getColor(
                                     (String) changeAtom.getSelectedItem()));
                
                moleculeEditor.addEdit(new AtomSymbolUndoableEdit(ms,
                        currentSelectionStack.get(0), 
                        oldSymbol,
                        (String) changeAtom.getSelectedItem()));
            }
        }); 
        drawToolBar.add(new JLabel("Current atom:", JLabel.RIGHT));
        drawToolBar.add(changeAtom);
        
        editBond = new JComboBox(new Object[] {
            BondType.SINGLE_BOND,
            BondType.DOUBLE_BOND,
            BondType.TRIPLE_BOND,
            BondType.AROMATIC_BOND,
            BondType.WEAK_BOND,
            BondType.IONIC_BOND,
            BondType.NO_BOND
        });
        editBond.setEnabled(false);        
        editBond.setToolTipText("Edit selected bond");
        editBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {                
                if (!editBond.isEnabled()) return;
                if (currentSelectionStack == null) return;
                if (currentSelectionStack.size() != 2) return;
                
                Atom a1 = currentSelectionStack.get(0).getAtom();
                Atom a2 = currentSelectionStack.get(1).getAtom();
                
                BondType oldBond = a1.getConnectivity(a2.getIndex());
                
                if (oldBond.equals((BondType) editBond.getSelectedItem())) return;
                
                BondType newBond = (BondType) editBond.getSelectedItem();
                
                a1.removeConnection(a2.getIndex());
                a1.addConnection(a2.getIndex(), newBond);
                a2.removeConnection(a1.getIndex());
                a2.addConnection(a1.getIndex(), newBond);
                
                MoleculeScene ms = moleculeEditor.getSceneList().get(0);
                ms.moleculeChanged(null);
                ms.selectAtomIndex(a1.getIndex());
                ms.selectAtomIndex(a2.getIndex());
                moleculeEditor.repaint();
                                
                moleculeEditor.addEdit(new BondUndoableEdit(ms.getMolecule(), 
                                                oldBond, newBond, 
                                                a1.getIndex(), a2.getIndex()));
            }
        }); 
        drawToolBar.add(new JLabel("Current bond:", JLabel.RIGHT));
        drawToolBar.add(editBond);  
        
        drawToolBar.addSeparator();
        
        // defaults
        defaultAtom = new JComboBox(ai.getAtomicNumberTable().keySet().toArray());
        defaultAtom.setSelectedItem(moleculeEditor.getDefaultAtomSymbol());
        defaultAtom.setToolTipText("Change default atom. Current atom : " 
                             + moleculeEditor.getDefaultAtomSymbol() + " ("
                             + ai.getName(moleculeEditor.getDefaultAtomSymbol()) 
                             + " )");      
        defaultAtom.setBackground(ai.getColor(
                                     moleculeEditor.getDefaultAtomSymbol()));
        defaultAtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                moleculeEditor.setDefaultAtomSymbol(
                                  (String) defaultAtom.getSelectedItem());
                defaultAtom.setBackground(ai.getColor(
                                     moleculeEditor.getDefaultAtomSymbol()));
            }
        }); 
        drawToolBar.add(new JLabel("Default atom:", JLabel.RIGHT));
        drawToolBar.add(defaultAtom);
        
        defaultBond = new JComboBox(new Object[] {
            BondType.SINGLE_BOND,
            BondType.DOUBLE_BOND,
            BondType.TRIPLE_BOND,
            BondType.AROMATIC_BOND,
            BondType.WEAK_BOND,
            BondType.IONIC_BOND,
            BondType.NO_BOND
        });        
        defaultBond.setToolTipText("Change default bond type. Current type : "
                              + moleculeEditor.getDefaultBondType());
        defaultBond.setSelectedItem(moleculeEditor.getDefaultBondType());
        defaultBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                moleculeEditor.setDefaultBondType(
                        (BondType) defaultBond.getSelectedItem());
            }
        }); 
        drawToolBar.add(new JLabel("Default bond:", JLabel.RIGHT));
        drawToolBar.add(defaultBond);
                
        drawToolBar.addSeparator();
        
        showContextWizard = new JCheckBox("Show context wizard");
        showContextWizard.setSelected(true);
        showContextWizard.setToolTipText("Shows a context dependent" +
                " activity wizard with selecting atoms / bonds.");
        showContextWizard.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ae) {
            moleculeEditor.setShowContextWizard(showContextWizard.isSelected());
          }
        });         
        drawToolBar.add(showContextWizard);
        
        drawToolBar.addSeparator(new Dimension(600, drawToolBar.getHeight()));                        
        
        // set the frame icon
        setFrameIcon(images.getMoleculeEditor());
        
        // and some size settings
        memoryMonitor.setPreferredSize(fullscreen.getPreferredSize());
        memoryMonitor.setMaximumSize(fullscreen.getPreferredSize()); 
        
        // initilize the side panels
        initSidePanels();                
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(moleculeEditor);
        splitPane.setRightComponent(sidePanel);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(1.0);
        splitPane.setDividerSize(1);
        
        contentPane.add(splitPane, BorderLayout.CENTER);
        contentPane.add(drawToolBar, BorderLayout.SOUTH);                
        
        // init jchempaint
        jChemPaintPanel = new JChemPaintPanel(new ChemModel(), 
                                              JChemPaint.GUI_APPLICATION, 
                                              false, null);                
                
    }
    
    /** Getter for property moleculeViewer.
     * Note that there is no viewer here, only editor component is returned. The
     * editor component in this case is the direct sub class of the viewer 
     * component
     *
     * @return Value of property moleculeViewer.
     *
     */
    @Override
    public MoleculeViewer getMoleculeViewer() {
        return this.moleculeEditor;
    }
    
    /** Getter for property moleculeEditor.
     * @return Value of property moleculeViewer.
     *
     */
    public MoleculeEditor getMoleculeEditor() {
        return this.moleculeEditor;
    }
    
    /**
     * add a MoleculeScene to this viewer
     */
    @Override
    public void addScene(MoleculeScene scene) {
        if (scene == null) return;              
        
        // just forward the calls
        moleculeEditor.addScene(scene);
        scene.addMoleculeSceneChangeListener(this);
        
        // update the find tool
        moleculeViewerFindTool.updateFindTragetSceneList();                
    }
    
    /**
     * remove a MoleculeScene from this viewer
     */
    @Override
    public void removeScene(MoleculeScene scene) {
        if (scene == null) return;
                
        // just forward the calls
        moleculeEditor.removeScene(scene);
        scene.removeMoleculeSceneChangeListener(this);
        
        // update the find tool
        moleculeViewerFindTool.updateFindTragetSceneList();
    }
    
    /**
     * Returns the scene list attached to this viewer.
     *
     * @return A vector list of scenes connected to this viewer
     */
    @Override
    public java.util.Vector<MoleculeScene> getSceneList() {
        return moleculeEditor.getSceneList();
    }
       
    /**
     * handling undo events
     */
    @Override
    public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
        undo.setEnabled(moleculeEditor.canUndo());
        redo.setEnabled(moleculeEditor.canRedo());                
        
        if (undoableEditEvent.getEdit() instanceof SelectionUndoableEdit) {
            SelectionUndoableEdit se = 
                          (SelectionUndoableEdit) undoableEditEvent.getEdit();
            
            Stack<ScreenAtom> ss  = se.getFinalSelectionStack();
            currentSelectionStack = ss;
                        
            removeAtom.setEnabled(false);
            editBond.setEnabled(false);
            changeAtom.setEnabled(false);
            
            if (ss.size() == 0) return;
                        
            switch(ss.size()) {
                case 1:              
                    removeAtom.setEnabled(true);
                    changeAtom.setEnabled(true); 
                                        
                    Atom atom   = ss.get(0).getAtom();
                    AtomInfo ai = AtomInfo.getInstance();
                    
                    changeAtom.setSelectedItem(atom.getSymbol());
                    changeAtom.setBackground(ai.getColor(atom.getSymbol()));
                    changeAtom.setToolTipText("Change selected atom. " +
                            "Currently selected atom is: " + atom
                            + " (" + ai.getName(atom.getSymbol()) + " )");
                    
                    break;
                case 2:
                    editBond.setEnabled(true);
                    editBond.setSelectedItem(ss.get(0).getAtom()
                            .getConnectivity(ss.get(1).getAtom().getIndex()));
                    break;                
            } // end of switch ... case
        } // end if
    }

    /**
     * initialize the side panels for cuboid and sphere based selection
     `*/
    @Override
    protected void initSidePanels() {
        sidePanel = new JPanel(new BorderLayout());                
        
        // all the side panels are lazy initilized for faster loading of 
        // the molecule viewer frame
        
        // add the side panel and hide it for now        
        sidePanel.setBackground(moleculeEditor.getBackgroundColor());
        sidePanel.setVisible(false);
    }
    
    /**
     * scene change listener
     */
    @Override
    public void sceneChanged(MoleculeSceneChangeEvent msce) {
        // TODO:      
    }            
} // end of class MoleculeEditorFrame
