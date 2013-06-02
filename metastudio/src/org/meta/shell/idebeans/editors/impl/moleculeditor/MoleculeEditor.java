/*
 * MoleculeEditor.java
 *
 * Created on August 12, 2007, 11:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JInternalFrame;
import javax.swing.event.UndoableEditListener;
import org.meta.math.Matrix3D;
import org.meta.math.Vector3D;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.NotificationWindow;
import org.meta.shell.idebeans.editors.IDEEditor;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;

/**
 * A part of MeTA Studio interface that provides a way to edit molecule
 * structure in a graphical, cut-paste and drag-drop way.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeEditor extends MoleculeViewer
                            implements IDEEditor {        
    
    // context dependent wizard for selection of atoms / bonds
    private NotificationWindow contextWizard;
    
    /** Creates a new instance of MoleculeEditor */
    public MoleculeEditor(MeTA ideInstance, JInternalFrame parentFrame) {
        super(ideInstance, parentFrame);
        
        // editor specific mouse handlers
        mouseAdapterMap.put(BuildState.PENCIL_TOOL,
                            new PencilToolBuildingMouseAdapter());
        mouseAdapterMap.put(BuildState.STRAIGHT_CHAIN,
                            new StraightChainBuildingMouseAdapter()); 
        mouseAdapterMap.put(BuildState.ORIENTATION_CHANGE,
                            new OrientationChangeMouseAdapter());

        // default symbol, to add atoms
        defaultAtomSymbol = "C"; // carbon
        
        // default bond type is single bond
        defaultBondType   = BondType.SINGLE_BOND;                
    }

    /**
     * Opens up the specified file in editor.
     *
     * @param fileName the file name (absolute path) to be opened 
     */
    @Override
    public void open(String fileName) {
        // TODO:
    }

    /**
     * Saves the currently loaded file
     *
     * @param fileName the file name (absolute path) to which to save
     */
    @Override
    public void save(String fileName) {
        // TODO:
    }

    /** are contents of this editor dirty ? */ 
    private boolean dirty;
    
    /**
     * Getter for property dirty.
     * @return Value of property dirty.
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * overridden paint method
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);                
    }
    
    protected BuildState buildState;

    /**
     * Getter of property buildState
     * 
     * @return current buildState 
     */
    public BuildState getBulidState() {
        return buildState;
    }

    protected String defaultAtomSymbol;

    /**
     * Get the value of defaultAtomSymbol
     *
     * @return the value of defaultAtomSymbol
     */
    public String getDefaultAtomSymbol() {
        return defaultAtomSymbol;
    }

    /**
     * Set the value of defaultAtomSymbol
     *
     * @param defaultAtomSymbol new value of defaultAtomSymbol
     */
    public void setDefaultAtomSymbol(String defaultAtomSymbol) {
        this.defaultAtomSymbol = defaultAtomSymbol;
    }

    protected BondType defaultBondType;

    /**
     * Get the value of defaultBondType
     *
     * @return the value of defaultBondType
     */
    public BondType getDefaultBondType() {
        return defaultBondType;
    }

    /**
     * Set the value of defaultBondType
     *
     * @param defaultBondType new value of defaultBondType
     */
    public void setDefaultBondType(BondType defaultBondType) {
        this.defaultBondType = defaultBondType;
    }

    /**
     * Setter for property buildState
     * 
     * @param buildState the new build state
     */
    public void setBulidState(BuildState buildState) {
        this.buildState = buildState;
        
        currentMouseAdapter =
               (MoleculeViewerMouseAdapter) mouseAdapterMap.get(buildState);
    }        
    
    protected boolean showContextWizard;

    /**
     * Get the value of showContextWizard
     *
     * @return the value of showContextWizard
     */
    public boolean isShowContextWizard() {
        return showContextWizard;
    }

    /**
     * Set the value of showContextWizard
     *
     * @param showContextWizard new value of showContextWizard
     */
    public void setShowContextWizard(boolean showContextWizard) {
        this.showContextWizard = showContextWizard;
    }
    
    /** inner class for handling a straight chain building event */
    protected class StraightChainBuildingMouseAdapter 
                    extends MoleculeViewerMouseAdapter {

        private int firstX, firstY;
        private Atom nearestAtom;

        private AddAtomChainCommand addAtomChainCommand;
        
        /** Creates an instance of StraightChainBuildingMouseAdapter */
        public StraightChainBuildingMouseAdapter() {
            addAtomChainCommand = new AddAtomChainCommand();
        }
        
        /**
         * click event handler
         */
        @Override
        public void mouseClicked(MouseEvent e) {      
            firstX = e.getX();
            firstY = e.getY();            
            
            MoleculeScene ms = getSceneList().get(0);
            nearestAtom = ms.getClosestAtom(firstX, firstY);
            System.out.println("The closest atom is: [" + nearestAtom.getIndex()
                               + "]: " + nearestAtom.toString());
            ms.getMolecule().addAtom(
                      new Atom("C", 0.0, nearestAtom.getAtomCenter().add(1.5),
                               ms.getMolecule().getNumberOfAtoms()));
        }
        
        /**
         * mouse dragged handler
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
                
            if (nearestAtom == null) {
                mouseClicked(e);
            } // end if
            
            // TODO: change this!
            if (x-firstX > 5 || y-firstY > 5) {
                MoleculeScene ms = getSceneList().get(0);
                ms.getMolecule().addAtom(
                      new Atom(defaultAtomSymbol, 0.0, 
                               nearestAtom.getAtomCenter().add(1.2),
                               ms.getMolecule().getNumberOfAtoms()));
                firstX = x;
                firstY = y;
                nearestAtom = ms.getClosestAtom(firstX, firstY);

                addAtomChainCommand.update(ms, firstX, firstY);
            } // end if
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            
        }
    }

    /** inner class for orientation change event */
    protected class OrientationChangeMouseAdapter
                    extends MoleculeViewerMouseAdapter {
        
        /** Creates an instance of OrientationChangeMouseAdapter */
        public OrientationChangeMouseAdapter() {
            
        }


        /**
         * click event handler
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            defaultMouseClicked(e);
        }

        /**
         * mouse dragged handler
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if (!e.isShiftDown()) { // rotation along x-y                
                xTheta = (previousY - y) * (360.0 / theCanvas.getWidth());
                yTheta = (x - previousX) * (360.0 / theCanvas.getHeight());

                zTheta = (x - previousX + previousY - y)
                          * (360.0 / theCanvas.getWidth());

                MoleculeScene ms = getFirstSelectedScene();

                if (ms != null) {                                                            
                    try {                        
                        Molecule newMol = ms.getMolecule();

                        Iterator<Atom> atoms = newMol.getAtoms();
                        String atmStr = "";
                        
                        while(atoms.hasNext()) {
                            atmStr += atoms.next().toString() + "\n";
                        } // end while

                        System.out.println(atmStr);
                        
                        Vector3D axis = new Vector3D(newMol.getCenterOfMass());

                        axis = axis.normalize();

                        newMol = transform.rotate(newMol, axis, zTheta);

                        ms.setMolecule(newMol);

                        atoms = newMol.getAtoms();
                        atmStr = "";
                        while(atoms.hasNext()) {
                            atmStr += atoms.next().toString() + "\n";
                        } // end while

                        System.out.println(atmStr);

                        System.out.println("reached here ");
                        
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        System.err.println("Exception : " + ex.toString());
                    } // end try .. catch block
                } // end if
            } else { // translate

                MoleculeScene ms = getFirstSelectedScene();

                if (ms != null) {
                    try {
                        Molecule newMol = ms.getMolecule();

                        Matrix3D tempMatrix = new Matrix3D();
                        
                        tempMatrix.unit();
                        xTranslate = (x - previousX) / newMol.getBoundingBox().getXWidth();
                        yTranslate = (y - previousY) / newMol.getBoundingBox().getYWidth();

                        tempMatrix.translate(xTranslate, yTranslate, 0.0);                        

                        newMol = tempMatrix.transform(newMol);
                        
                        ms.setMolecule(newMol);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                        System.err.println("Exception : " + ex.toString());
                    } // end try .. catch block
                } // end if
            } // end if

            previousX = x;
            previousY = y;
        }

        /**
         * press(!) event handler
         */
        @Override
        public void mousePressed(MouseEvent e) {
            // TODO: handle undo
        }

        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO: handle undo
        }
    }

    /** inner class for handling a pencil tool building event */
    protected class PencilToolBuildingMouseAdapter
                    extends MoleculeViewerMouseAdapter {

        private int lastX, lastY;
        private int currentX, currentY;
        
        private AddSingleAtomCommand addSingleAtomCommand;
                  
        private ArrayList<MoleculeEditorCommandHelper> helperList;
        
        /** Creates an instance of StraightChainBuildingMouseAdapter */
        public PencilToolBuildingMouseAdapter() {
            addSingleAtomCommand = new AddSingleAtomCommand();

            helperList = new ArrayList<MoleculeEditorCommandHelper>();
            helperList.add(new AutoSelectionHelper());
            helperList.add(new DistanceHelper());
            helperList.add(new AngleHelper());
        }

        /** update undo listeners */
        @Override
        public void updateUndoListeners() {
            // add undo listeners
            Object [] listeners
                    = MoleculeEditor.this.listenerList.getListenerList();

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == UndoableEditListener.class) {
                    System.out.println(listeners[i]);
                    addSingleAtomCommand.addUndoableEditListener(
                            (UndoableEditListener) listeners[i + 1]);
                } // end if
            } // end for
        }
        
        /**
         * mouse goes out wandering         
         */
        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            
            for(MoleculeEditorCommandHelper mech : helperList) {
                try {
                    mech.clear();
                } catch (Exception ignored) { }
            } // end for 
        }
        
        /**
         * mouse dragged handler
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY(); 
        }       
                
        /**
         * mouse moved handler
         */
        @Override
        public void mouseMoved(MouseEvent e) {    
            lastX = currentX = e.getX();
            lastY = currentY = e.getY();            
                        
            MoleculeScene ms = getSceneList().get(0);    
            
            for(MoleculeEditorCommandHelper mech : helperList) {
                try {
                    mech.update(ms, currentX, currentY);
                } catch (Exception ignored) { }
            } // end for            
            
            // validate this command
            addSingleAtomCommand.setDefaultAtomSymbol(defaultAtomSymbol);
            addSingleAtomCommand.setDefaultBondType(defaultBondType);
            if (!addSingleAtomCommand.validate(ms, currentX, currentY)) {
                for(MoleculeEditorCommandHelper mech : helperList) {
                    mech.setHelperColor(Color.red);
                } // end for                
            } // end if
            
            repaint();
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {     
            lastX = e.getX();
            lastY = e.getY();
            
            // execute the command
            MoleculeScene ms = getSceneList().get(0);
            addSingleAtomCommand.setDefaultAtomSymbol(defaultAtomSymbol);
            addSingleAtomCommand.setDefaultBondType(defaultBondType);
            addSingleAtomCommand.update(ms, lastX, lastY);
            
            // recalculate the tranform factors
            recalculateBoundingBox();
            recalculateTransformFactor();
            updateTransforms();
            
            for(MoleculeEditorCommandHelper mech : helperList) {
                try {
                    mech.clear();
                } catch (Exception ignored) { }
            } // end for      
            
            // re-build the molecule scene and then repaint
            ms.moleculeChanged(null);                                    
                                        
            repaint();
        }                
    }
} // end of class MoleculeEditor
