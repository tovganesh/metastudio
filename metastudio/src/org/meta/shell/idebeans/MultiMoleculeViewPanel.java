/*
 * MultiMoleculeViewPanel.java
 *
 * Created on August 7, 2007, 8:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import org.meta.common.resource.ImageResource;
import org.meta.shell.idebeans.event.MultiMoleculeViewChangeEvent;
import org.meta.shell.idebeans.event.MultiMoleculeViewChangeListerner;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.TransformState;

/**
 * Provides the interface for multiple views of the same molecule.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MultiMoleculeViewPanel extends JPanel {
    
    private JScrollPane scrollPane;
    
    /** the instances of MoleculeViewer which provide different views of the
     * same molecule */
    private Vector<MoleculeViewPreviewPanel> multiViews;
    
    private ButtonGroup makeCurrentGroup;
    
    private MultiMoleculeViewChangeEvent mmvce;
    
    /** Creates a new instance of MultiMoleculeViewPanel */
    public MultiMoleculeViewPanel() {
        scrollPane = new JScrollPane(this);
        
        setAutoscrolls(true);
        setLayout(new IDEVerticalFlowLayout());
        
        makeCurrentGroup = new ButtonGroup();
        multiViews = new Vector<MoleculeViewPreviewPanel>();
        
        mmvce = new MultiMoleculeViewChangeEvent(this);
    }
    
    /**
     * Add a molecule viewer to this multi view panel
     *
     * @param mv the instance of molecule viewer to be replicated
     *        and added to this panel.
     */
    public void addMoleculeViewer(MoleculeViewer mv) {
        MoleculeViewer newMV = new MoleculeViewer(mv.getIDEInstance(),
                                                  mv.getParentFrame());
        
        newMV.disableUndo();
        for(MoleculeScene scene : mv.getSceneList()) {
           MoleculeScene newScene = new MoleculeScene(scene.getMolecule());
           
           Iterator<PropertyScene> pScenes = scene.getAllPropertyScene();
           PropertyScene ps;
           
           if (pScenes != null) {
             while(pScenes.hasNext()) {
               ps = pScenes.next();
               
               // TODO: need refinement and clear
               newScene.addPropertyScene(new PropertyScene(newScene, 
                                                     ps.getGridProperty()));
             } // end while
           } // end if
                      
           newScene.setSelectionStack(scene.getSelectionStack(), true);
           newMV.addScene(newScene);
        } // end for                
       
        newMV.setRotationMatrix(mv.getRotationMatrix());
        newMV.setTranslateMatrix(mv.getTranslateMatrix());
        newMV.setScaleFactor(mv.getScaleFactor());
        newMV.setAxisDrawn(mv.isAxisDrawn());
        newMV.setTransformState(TransformState.ROTATE_STATE);
        newMV.setShowViewerPopup(false);
        newMV.setMultiViewEnabled(true);
        newMV.enableUndo();
        
        add(Box.createVerticalStrut(2));
        MoleculeViewPreviewPanel mvp = new MoleculeViewPreviewPanel(newMV);
        add(mvp);
        
        updateUI();
        scrollRectToVisible(new Rectangle(0, getSize().height, getSize().width, 
                                             getSize().height));
        
        multiViews.add(mvp);
    }

    /**
     * Update views with a change (possibly in selection change of main view).
     * We only accept the unduable evets as these are the once that
     * broaught about change in selection.
     *
     * @param undoableEditEvent the event object indicating the source of change
     */
    public void updateViews(UndoableEditEvent undoableEditEvent) {        
        MoleculeViewer mv = (MoleculeViewer) undoableEditEvent.getSource();
        Vector<MoleculeScene> vsl, msl;
        MoleculeViewer viewer;
        
        msl = mv.getSceneList();
        for(MoleculeViewPreviewPanel mvp : multiViews) {            
            viewer = mvp.getMoleculeViewer();
            
            if (viewer == mv) continue;  // do not update self object
            
            vsl = viewer.getSceneList();
            
            for(int i=0; i<vsl.size(); i++) {                
                vsl.get(i).setSelectionStack(msl.get(i).getSelectionStack(),
                                             true);
            } // end for
        } // end for
    }
    
    /** molecule view preview panel */
    private class MoleculeViewPreviewPanel extends JPanel {
        
        public MoleculeViewPreviewPanel(MoleculeViewer mv) {
            this.moleculeViewer = mv;
            
            initComponents();
        }
        
        private void initComponents() {
            setLayout(new BorderLayout());
            
            JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
            final JToggleButton makeCurrent;
            final JComboBox transformMode;
            final JCheckBox showAxis;
            JButton remove;
            
            ImageResource images = ImageResource.getInstance();
            
            makeCurrent = new JToggleButton(images.getMakeCurrentView());
            makeCurrent.setToolTipText("Make this the current view");
            makeCurrentGroup.add(makeCurrent);
            makeCurrent.setSelected(true);
            setCurrentView(makeCurrent.isSelected());
            makeCurrent.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    setCurrentView(makeCurrent.isSelected());
                    
                    if (makeCurrent.isSelected()) {
                        mmvce.setMoleculeViewer(
                            MoleculeViewPreviewPanel.this.moleculeViewer);
                        fireMultiMoleculeViewChangeListernerMoleculeViewChanged(
                                                                        mmvce);
                    } // end if
                }
            });
            buttonPanel.add(makeCurrent);
            
            transformMode = new JComboBox(new Object[] {
               images.getRotate(),
               images.getTranslate(),
               images.getZoom()
            });
            transformMode.setToolTipText("Set the current transform mode");
            buttonPanel.add(transformMode);
            transformMode.setSelectedIndex(0); // Rotate mode
            transformMode.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch(transformMode.getSelectedIndex()) {
                        case 0:  // rotate mode
                            moleculeViewer.setTransformState(
                                              TransformState.ROTATE_STATE);
                            break;
                        case 1: // translate mode
                            moleculeViewer.setTransformState(
                                              TransformState.TRANSLATE_STATE);
                            break;
                        case 2: // zoom state
                            moleculeViewer.setTransformState(
                                              TransformState.SCALE_STATE);
                            break;
                    } // end of switch .. case
                }
            });
            
            remove = new JButton("X");
            remove.setToolTipText("Remove this view");
            remove.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    MultiMoleculeViewPanel.this.remove(
                            MoleculeViewPreviewPanel.this); 
                    MultiMoleculeViewPanel.this.remove(moleculeViewer);
                    MultiMoleculeViewPanel.this.updateUI();
                }
            });
            buttonPanel.add(remove);
            
            showAxis = new JCheckBox("Axis");
            showAxis.setSelected(moleculeViewer.isAxisDrawn());
            showAxis.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moleculeViewer.setAxisDrawn(showAxis.isSelected());
                }
            });
            buttonPanel.add(showAxis);
            
            add(buttonPanel, BorderLayout.NORTH);
            add(moleculeViewer, BorderLayout.CENTER);
            
            setPreferredSize(new Dimension(250, 200));
            setSize(new Dimension(250, 200));
            setMinimumSize(new Dimension(250, 200));
        }
        
       /**
         * Holds value of property moleculeViewer.
         */
        private MoleculeViewer moleculeViewer;
        
        /**
         * Getter for property moleculeViewer.
         * @return Value of property moleculeViewer.
         */
        public MoleculeViewer getMoleculeViewer() {
            return this.moleculeViewer;
        }
        
        /**
         * Setter for property moleculeViewer.
         * @param moleculeViewer New value of property moleculeViewer.
         */
        public void setMoleculeViewer(MoleculeViewer moleculeViewer) {
            this.moleculeViewer = moleculeViewer;
        }

        /**
         * Holds value of property currentView.
         */
        private boolean currentView;

        /**
         * Getter for property currentView.
         * @return Value of property currentView.
         */
        public boolean isCurrentView() {
            return this.currentView;
        }

        /**
         * Setter for property currentView.
         * @param currentView New value of property currentView.
         */
        public void setCurrentView(boolean currentView) {
            this.currentView = currentView;
            
            if (currentView) {
                setBorder(BorderFactory.createEtchedBorder(Color.blue,
                                                           Color.blue));
            } else {
                setBorder(BorderFactory.createEtchedBorder());
            } // end if
        }
    } // end of inner class MoleculeViewPreviewPanel

    /**
     * Utility field used by event firing mechanism.
     */
    private javax.swing.event.EventListenerList listenerList =  null;

    /**
     * Registers MultiMoleculeViewChangeListerner to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addMultiMoleculeViewChangeListerner(
                                MultiMoleculeViewChangeListerner listener) {
        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(MultiMoleculeViewChangeListerner.class, listener);
    }

    /**
     * Removes MultiMoleculeViewChangeListerner from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeMultiMoleculeViewChangeListerner(
                                   MultiMoleculeViewChangeListerner listener) {
        listenerList.remove(MultiMoleculeViewChangeListerner.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event The event to be fired
     */
    private void fireMultiMoleculeViewChangeListernerMoleculeViewChanged(
                                          MultiMoleculeViewChangeEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i]==MultiMoleculeViewChangeListerner.class) {
                ((MultiMoleculeViewChangeListerner)listeners[i+1])
                       .moleculeViewChanged(event);
            }
        }
    }
    
} // end of class MultiMoleculeViewPanel
