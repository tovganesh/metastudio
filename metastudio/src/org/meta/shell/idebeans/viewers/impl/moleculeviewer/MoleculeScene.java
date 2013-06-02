/*
 * MoleculeScene.java
 *
 * Created on January 18, 2004, 10:02 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.*;
import java.util.*;
import org.meta.common.Utility;
import org.meta.config.event.AtomInfoChangeEvent;
import org.meta.config.event.AtomInfoChangeListener;
import org.meta.config.impl.AtomInfo;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.math.Matrix3D;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.molecule.event.MoleculeStateChangeEvent;
import org.meta.molecule.event.MoleculeStateChangeListener;
import org.meta.parallel.AbstractSimpleParallelTask;
import org.meta.parallel.SimpleParallelTask;
import org.meta.shell.idebeans.graphics.AbstractScene;
import org.meta.shell.idebeans.graphics.Glyph;
import org.meta.shell.idebeans.graphics.PaintGlyphObject;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j2d.ScreenAtom3D;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j2d.SingleScreenBond3D;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;

/**
 * A simple scene graph for Molecule!
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeScene extends AbstractScene
                           implements MoleculeStateChangeListener,
                                      PropertySceneChangeListener {
    
    /** Holds value of property molecule. */
    protected Molecule molecule;
    
    /** the items that form the MoleculeScene */
    protected ArrayList<ScreenAtom> screenAtoms;
    protected ArrayList<ScreenBond> screenBonds;
    protected ArrayList<Tracker> trackers;
    protected ArrayList<ScreenLabel> screenLabels;
    protected HashMap<FragmentationScheme, ArrayList<FragmentScene>> fragmentSceneMap;
    protected ArrayList<PropertyScene> propertyScenes;
    
    /** Utility field holding the MoleculeSceneChangeListener. */
    protected transient MoleculeSceneChangeListener
            moleculeSceneChangeListener =  null;
    
    protected transient MoleculeSceneChangeEvent sceneChangeEvent;
    
    /** Holds value of property selectionStack. */
    protected Stack<ScreenAtom> selectionStack;
    
    /** Holds value of property enable3D. */
    protected boolean enable3D;
    
    /** Utility field used by event firing mechanism. */
    protected javax.swing.event.EventListenerList listenerList =  null;
    
    // the atom info change listener
    protected AtomInfoChangeListener atomInfoChangeListener;
    
    // the Z-buffer
    protected int [] zBuffer;
    
    // count for providing "names" to the property objects
    protected int propertyCount;
    
    /** Creates a new instance of MoleculeScene */
    public MoleculeScene(Molecule molecule) {
        this.molecule  = molecule;
        this.visible   = true;
        
        // no 3D by default
        this.enable3D = false;
                
        transform = new Matrix3D();
        transform.unit();
        
        screenLabels = new ArrayList<ScreenLabel>();
        
        sceneChangeEvent = new MoleculeSceneChangeEvent(this);
        selectionStack   = new Stack<ScreenAtom>();
        
        buildMoleculeScene();
        
        // a very ineffetient implimentation of refreshing molecule geometry
        // in a way programatically providing a simple but very powerful
        // and scriptable molecule editor
        molecule.addMoleculeStateChangeListener(this);
        
        // another ineffective way of refreshing the molecule scene, when
        // the AtomInfo information changes
        atomInfoChangeListener = new AtomInfoChangeListener() {
            @Override
            public void atomInfoChanged(AtomInfoChangeEvent aice) {
                // TODO: what about other events?
                switch(aice.getChangeType()) {
                    case AtomInfoChangeEvent.COLOR_VALUE:
                        Iterator<ScreenAtom> items = screenAtoms.iterator();
                        // change color of atoms
                        AtomInfo atomInfo = AtomInfo.getInstance();
                        ScreenAtom sa;
                        
                        while(items.hasNext()) {
                            sa = items.next();
                            sa.setColor(atomInfo.getColor(
                                    sa.getAtom().getSymbol()));
                        } // end while
                        
                        // fire the change event
                        fireMoleculeSceneChangeListenerSceneChanged(
                                sceneChangeEvent);
                        break;
                    case AtomInfoChangeEvent.COVALENT_RADIUS:
                    case AtomInfoChangeEvent.VDW_RADIUS:
                    case AtomInfoChangeEvent.WEAK_BOND_ANGLE:
                    case AtomInfoChangeEvent.DOUBLE_BOND_OVERLAP:
                    case AtomInfoChangeEvent.ALL_CANGED:
                        // rebuild the complete scene and then fire
                        // the change event
                        MoleculeStateChangeEvent msce =
                                new MoleculeStateChangeEvent(
                                MoleculeScene.this.molecule);
                        msce.setEventType(
                                MoleculeStateChangeEvent.MAJOR_MODIFICATION);
                        moleculeChanged(msce);
                        break;
                    default:
                        break;
                } // end of switch .. case
            }
        };
        
        // add a listener
        AtomInfo.getInstance().addAtomInfoChangeListener(atomInfoChangeListener);
    }
    
    /**
     * private method to build the scene
     */
    protected void buildMoleculeScene() {
        int noOfAtoms = molecule.getNumberOfAtoms();
        
        // TODO : PRIMITIVE STATE : MAY NEED OPTIMIZATION
        
        // init the arrays
        screenAtoms = new ArrayList<ScreenAtom>(noOfAtoms);
        screenBonds = new ArrayList<ScreenBond>();
        trackers    = new ArrayList<Tracker>();
        
        // add the atoms first
        Iterator<Atom> atoms = molecule.getAtoms();
        
        if (enable3D) {
            while(atoms.hasNext()) {
                screenAtoms.add(new ScreenAtom3D(atoms.next()));
            } // end while
        } else {
            while(atoms.hasNext()) {
                screenAtoms.add(new ScreenAtom(atoms.next()));
            } // end while
        } // end if
        
        // and the bonds between them!
        ScreenAtom  screenAtom;
        Hashtable<Integer, BondType> connList;
        Enumeration<Integer> connAtoms;
        Integer theIndex;
        int index;
        ScreenBond screenBond;
        
        // init the zBuffer
        zBuffer = new int[noOfAtoms];
        
        for(int i=0; i<noOfAtoms; i++) {
            zBuffer[i] = i;
            screenAtom = screenAtoms.get(i);
            connList   = screenAtom.getAtom().getConnectedList();
            connAtoms  = connList.keys();
            
            while(connAtoms.hasMoreElements()) {
                theIndex = connAtoms.nextElement();
                index    = theIndex.intValue();
                
                if (i > index) continue;
                
                if (connList.get(theIndex).equals(BondType.SINGLE_BOND)) {
                    if (enable3D) {
                        screenBond = new SingleScreenBond3D(screenAtom,
                                                       screenAtoms.get(index));
                    } else {
                        screenBond = new SingleScreenBond(screenAtom,
                                                       screenAtoms.get(index));
                    } // end if
                    
                    screenBonds.add(screenBond);
                    screenAtom.getScreenBondList().add(screenBond);
                } else if (connList.get(theIndex).equals(BondType.WEAK_BOND)) {
                    screenBond = new WeakScreenBond(screenAtom,
                                                    screenAtoms.get(index));
                    screenBonds.add(screenBond);
                    screenAtom.getScreenBondList().add(screenBond);
                } else if (!connList.get(theIndex).equals(BondType.NO_BOND)) {
                    if (enable3D) {
                        screenBond = new SingleScreenBond3D(screenAtom,
                                                       screenAtoms.get(index));
                    } else {
                        screenBond = new MultipleScreenBond(screenAtom,
                                                       screenAtoms.get(index));
                    } // end if
                    screenBonds.add(screenBond);
                    screenAtom.getScreenBondList().add(screenBond);
                } // end if
            } // end while
        } // end for
        
        // remove all the references
        screenAtom = null;
        connList   = null;
        connAtoms  = null;
        theIndex   = null;
    }
    
    /**
     * Add a screen atom to this scene. <br>
     *
     * <b>Warning</b>: This is a very expensive method. And should not be used 
     * by any external routine. This is internally used by ScratchPad interface.
     *
     * @param screenAtom the ScreenAtom object to be added to this scene
     */
    public void addScreenAtom(ScreenAtom screenAtom) {
       if (screenAtoms != null) { 
           screenAtoms.add(screenAtom);
           
           // recreate zBuffer                      
           zBuffer = new int[zBuffer.length+1];
           
           for(int i=0; i<zBuffer.length; i++) zBuffer[i] = i;
           
           fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
       } // end if       
    }
    
    /**
     * Set visibility of a screen atom. If the index is invalid a runtime
     * NullPointerException is thrown.
     * 
     * @param screenAtomIndex the screen atom index
     * @param visible the visibility state
     */
    public void setScreenAtomVisibility(int screenAtomIndex, boolean visible) {        
        screenAtoms.get(screenAtomIndex).setVisible(visible);
            
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);        
    }
    
    /**
     * Get visibility status of a screen atom. If the index is invalid a runtime
     * NullPointerException is thrown.
     * 
     * @param screenAtomIndex the screen atom index
     * @return current visibility status of this screen atom
     */
    public boolean getScreenAtomVisibility(int screenAtomIndex) {        
        return screenAtoms.get(screenAtomIndex).isVisible();                    
    }        
    
    /**
     * The Z-Buffer sorter.
     */
    private void sortZBuffer() {
        int noOfAtoms = screenAtoms.size();
        int i, j, a, b;
        boolean flipped;
        
        for(i=noOfAtoms-1; --i>=0;) {
            flipped = false;
            
            for(j=0; j<=i; j++) {
                a = zBuffer[j]; b = zBuffer[j+1];
                
                if (screenAtoms.get(a).getCurrentZ()
                    > screenAtoms.get(b).getCurrentZ()) {
                    zBuffer[j+1] = a;
                    zBuffer[j]   = b;
                    flipped      = true;
                } // end if
            } // end for
            
            if (!flipped) break;
        } // end for
    }
    
    /**
     * Return the sorted available Z-Buffer
     */
    public int[] getZBuffer() {
        sortZBuffer();
        
        return zBuffer;
    }

    /**
     * Generic paint method for this glyph.
     *
     * @param pgo The instance of PaintGlyphObject that will actually provide
     *            primitives to render this glyph.
     */
    @Override
    public void paintGlyph(PaintGlyphObject pgo) {
        if (!isVisible()) return;

        for(Glyph glyph : getAllGlyphs()) {
            glyph.paintGlyph(pgo);
        } // end for
    }
    
    /**
     * Method to appropriately draw the molecule on to the canvas.
     *
     * @param g2d - the graphics object connected to the canvas
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (!visible) return;
        
        if (enable3D) sortZBuffer();
        
        if (useUniformColor) setUniformColor(uniformColor);
        
        // the property scenes .. textured planes first
        if (propertyScenes != null) {
            for(PropertyScene scene : propertyScenes) {
                if (scene.getType()
                    == PropertyScene.PropertySceneType.TEXTURED_PLANAR_CUT) {
                    scene.draw(g2d);
                } else if (scene.getType()
                    == PropertyScene.PropertySceneType.TEXTURED_VDW_SURFACE) {
                    scene.draw(g2d);
                } // end if
            } // end for
        } // end if
        
        // the atoms
        ScreenAtom atom;
        for(int i=0; i<screenAtoms.size(); i++) {
            atom = screenAtoms.get(zBuffer[i]);

            // the bonds
            for(ScreenBond bond : atom.getScreenBondList()) {
                bond.draw(g2d);
            } // end for

            atom.draw(g2d);
        } // end for

        // TODO : correct multi-core rendering
        // presently for 2D rendering, the order is important, for the
        // multi-core version you always get different order, resulting in
        // the image being "changed" with each re-draw
        // SimpleParallelTaskExecuter spte = new SimpleParallelTaskExecuter();
        // spte.execute(new RenderingTask(screenAtoms.size(), g2d)) ;
        
        // the trackers
        for(Tracker item : trackers) {
            item.draw(g2d);
        } // end for
        
        // the fragments
        if (fragmentSceneMap != null) {
            for(ArrayList<FragmentScene> scenes : fragmentSceneMap.values()) {
                for(FragmentScene scene : scenes) {
                    scene.draw(g2d);
                } // end for
            } // end for
        } // end if
        
        // the property scenes ... other properties, actually contours?
        if (propertyScenes != null) {
            for(PropertyScene scene : propertyScenes) {
                if ((scene.getType()
                      != PropertyScene.PropertySceneType.TEXTURED_PLANAR_CUT)
                    && (scene.getType()
                      != PropertyScene.PropertySceneType.TEXTURED_VDW_SURFACE)){
                    scene.draw(g2d);
                } // end if
            } // end for
        } // end if
        
        // and finally the screen lables
        for(ScreenLabel sl : screenLabels) {
            sl.draw(g2d);
        } // end for
    }
    
    /**
     * must be called to aid garbage collection, after calling this method,
     * this object becomes unusable and must not be further used!
     */
    public void clearAllReferences() {
        // the components
        screenAtoms = null;
        screenBonds = null;
        trackers    = null;
        
        // remove references
        sceneChangeEvent = null;
        fragmentSceneMap = null;
        propertyScenes   = null;
        
        // remove a listeners...
        molecule.removeMoleculeStateChangeListener(this);
        
        AtomInfo.getInstance().removeAtomInfoChangeListener(atomInfoChangeListener);
        
        // finally remove listener list
        listenerList = null;
    }
    
    /**
     * the reference cloner
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        MoleculeScene newScene = new MoleculeScene(molecule);
        
        newScene.buildMoleculeScene();
        return newScene;
    }
    
    /** Getter for property molecule.
     * @return Value of property molecule.
     *
     */
    public Molecule getMolecule() {
        return this.molecule;
    }
    
    /** Setter for property molecule.
     * @param molecule New value of property molecule.
     *
     */
    public void setMolecule(Molecule molecule) {
        if (this.molecule != null) {
            this.molecule.removeMoleculeStateChangeListener(this);
        } // end if
        
        this.molecule = molecule;
        this.molecule.addMoleculeStateChangeListener(this);
        
        buildMoleculeScene();
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * method to apply scene transformations
     */
    @Override
    public synchronized void applyTransforms() {
        // transform screen atoms
        transform.transform(screenAtoms);
        
        // transform isolated trackers
        for(Tracker tracker : trackers) {
            if (tracker.isIsolated()) {
                tracker.setTransform(transform);
                tracker.applyTransforms();
            } // end if
        } // end if
        
        // transform the fragments
        if (fragmentSceneMap != null) {
            for(ArrayList<FragmentScene> scenes : fragmentSceneMap.values()) {
                for(FragmentScene scene : scenes) {
                    scene.setTransform(transform);
                    scene.applyTransforms();
                } // end for
            } // end for
        } // end if
        
        // transform the property scenes
        if (propertyScenes != null) {
            for(PropertyScene scene : propertyScenes) {
                scene.setTransform(transform);
                scene.applyTransforms();
            } // end for
        } // end if
    }
    
    /**
     * Returns a new instance of molecule with the transformed
     * coordinates.
     *
     * @param tMat - the transformation matrix
     * @return Molecule - the transformed molecule
     * @throws Exception - may throw InstantiationException, if unable to
     *  create instance of Molecule class.
     */
    public synchronized Molecule getTransformedMolecule(Matrix3D tMat)
                                                             throws Exception {
        return tMat.transform(molecule);
    }
    
    /**
     * method to test whether the specified point is near any atom
     * in this scene, and select it if so
     *
     * @param point - the point to be tested
     * @return boolean - true is the point is near by, else false.
     */
    public boolean selectIfContains(Point point) {
        return selectIfContains(point.x, point.y);
    }
    
    /**
     * method to test whether the specified point is near bany atom
     * in this scene, and select it if so
     *
     * @param x the X coordinate of the point to be tested
     * @param y the Y coordinate of the point to be tested
     * @return boolean - true is the point is near by, else false.
     */
    public boolean selectIfContains(int x, int y) {
        if (!isVisible()) return false;
        
        ScreenAtom atom;
        
        for(int i=screenAtoms.size(); --i>=0;) {
            atom = screenAtoms.get(zBuffer[i]);
            
            if (atom.contains(x, y)) {
                // add / remove to selection stack -- pre state
                if (atom.isSelected()) {
                    selectionStack.remove(atom);
                } // end if
                
                // invert selection!
                atom.setSelected(!atom.isSelected());
                
                // add / remove to selection stack -- pro state
                if (atom.isSelected()) {
                    selectionStack.push(atom);
                } else {
                    selectionStack.remove(atom);
                } // end if
                
                return true;
            } // end if
        } // end for
        
        return false;
    }
    
    /**
     * Select an atom index if not selected, else deselect it!
     * 
     * @param atomIndex the atom index to be selected / deselcted
     */
    public void selectAtomIndex(int atomIndex) {
        ScreenAtom atom = screenAtoms.get(atomIndex);
        
        atom.setSelected(!atom.isSelected());
        selectionStack.push(atom);
    }

    /**
     * Select/ deselect an atom index
     *
     * @param atomIndex the atom index to be selected / deselcted
     * @param select to select or to un-select
     */
    public void selectAtomIndex(int atomIndex, boolean select) {
        ScreenAtom atom = screenAtoms.get(atomIndex);

        atom.setSelected(select);
        if (select) selectionStack.push(atom);
        else        selectionStack.remove(atom);
    }

    /**
     * Is an atomIndex selected?
     * 
     * @param atomIndex the atomIndex to be checked for 
     * @return the selected atom index
     */
    public boolean isAtomIndexSelected(int atomIndex) {
        return screenAtoms.get(atomIndex).isSelected();
    }
    
    /**
     * Select an atom index if not selected, else deselect it!
     * 
     * @param atomIndex the atom index to be selected / deselcted
     */
    public void selectOnlyThisAtomIndex(int atomIndex) {
        for(ScreenAtom atom : screenAtoms) {
            atom.setSelected(false);
        } // end for
        
        ScreenAtom atom = screenAtoms.get(atomIndex);        
        atom.setSelected(true);
        selectionStack.removeAllElements();
        selectionStack.push(atom);
    }
    
    /**
     * method to select all the atoms that can be reached by a breadth
     * first traversal starting from the atom near or on the specified
     * point; passed as a parameter.
     *
     * @param point - the point to be tested
     * @return boolean - true is the point is near by, else false.
     */
    public boolean selectPathThatContains(Point point) {
        return selectPathThatContains(point.x, point.y);
    }
    
    /**
     * method to select all the atoms that can be reached by a breadth
     * first traversal starting from the atom near or on the specified
     * point; passed as a parameter.
     *
     * @param point - the point to be tested
     * @param noDepth only one depth extension [false: full depth, true: one depth]
     * @return boolean - true is the point is near by, else false.
     */
    public boolean selectPathThatContains(Point point, boolean noDepth) {
        return selectPathThatContains(point.x, point.y, noDepth);
    }
    
    /**
     * method to select all the atoms that can be reached by a breadth
     * first traversal starting from the atom near or on the specified
     * point; passed as a parameter.
     *
     * @param x the X coordinate of the point to be tested
     * @param y the Y coordinate of the point to be tested
     * @return boolean - true is the point is near by, else false.
     */
    public boolean selectPathThatContains(int x, int y) {
        return selectPathThatContains(x, y, false);
    }
    
    /**
     * method to select all the atoms that can be reached by a breadth
     * first traversal starting from the atom near or on the specified
     * point; passed as a parameter.
     *
     * @param x the X coordinate of the point to be tested
     * @param y the Y coordinate of the point to be tested
     * @param noDepth only one depth extension [false: full depth, true: one depth]
     * @return boolean - true is the point is near by, else false.
     */
    public boolean selectPathThatContains(int x, int y, boolean noDepth) {
        if (!isVisible()) return false;
        
        selectionStack.clear();
        ScreenAtom atom;
        
        for(int i=screenAtoms.size(); --i>=0;) {
            atom = screenAtoms.get(zBuffer[i]);
            
            if (atom.contains(x, y)) {
                // do a path traversal
                Iterator<Integer> path = null;

                if (noDepth) {
                    path = molecule.traversePath(atom.getAtom().getIndex(), noDepth);
                } else {
                    ArrayList<Integer> pathList = new ArrayList<Integer>();
                    for(int j=0; j<molecule.getNumberOfAtoms(); j++)
                        pathList.add(j);
                    path = pathList.iterator();
                } // end if
                
                // unselect all atoms
                for(ScreenAtom uAtom : screenAtoms) {
                    uAtom.setSelected(false);
                } // end for
                
                // and then select all the atoms with in the path
                while(path.hasNext()) {
                    atom = screenAtoms.get(((Integer) path.next()).intValue());
                    
                    atom.setSelected(true);
                    selectionStack.push(atom);
                } // end while
                
                return true;
            } // end if
        } // end for
        
        return false;
    }

    /**
     * invert current selection stack
     */
    public void invertSelection() {
        if (!isVisible()) return;

        selectionStack.clear();
        ScreenAtom atom;

        for(int i=screenAtoms.size(); --i>=0;) {
            atom = screenAtoms.get(zBuffer[i]);

            if (atom.isSelected()) atom.setSelected(false);
            else {
                atom.setSelected(true);
                selectionStack.add(atom);
            } // end for
        } // end for

        // fire the change event
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }

    /**
     * method to select all those atoms contained in the selecion unit.
     * Also pushes all the selected atoms to selection stack. If not with in the
     * selection unit, they are unselected.
     *
     * @param selectionUnit - the Polygon which defines the selection unit
     */
    public void selectIfContainedIn(Polygon selectionUnit) {
        if (!isVisible()) return;
        
        selectionStack.clear();
        ScreenAtom atom;
        
        for(int i=screenAtoms.size(); --i>=0;) {
            atom = screenAtoms.get(zBuffer[i]);
            
            if (selectionUnit.contains(atom.getCurrentX(), atom.getCurrentY())) {
                atom.setSelected(true);
                selectionStack.push(atom);
            } else {
                atom.setSelected(false);
            } // end if
        } // end for
    }

    /**
     * method to select all those atoms contained in the selecion unit.
     * Also pushes all the selected atoms to selection stack
     *
     * @param selectionUnit - the Polygon which defines the selection unit
     * @param exclusive if true, other atoms already selected are set to false
     *        if they dont line in the Polygon unit. If false no such checks are
     *        made
     */
    public void selectIfContainedIn(Polygon selectionUnit, boolean exclusive) {
        if (!isVisible()) return;
        
        ScreenAtom atom;

        if (exclusive) {
            selectIfContainedIn(selectionUnit);
        } else {
            for (int i = screenAtoms.size(); --i >= 0;) {
                atom = screenAtoms.get(zBuffer[i]);

                if (!atom.isSelected()) {
                    if (selectionUnit.contains(atom.getCurrentX(),
                                               atom.getCurrentY())) {
                        atom.setSelected(true);
                        selectionStack.push(atom);
                    } // end if
                } // end if
            } // end for
        }
    }
    
    /**
     * method to select all those atoms contained in the selecion unit defined
     * by a sphere with a center and appropriate radius.
     *
     * @param center - the center of the sphere
     * @param radius - the radius of the sphere
     */
    public void selectIfContainedIn(Point3D center, double radius) {
        selectIfContainedIn(center, radius, true);
    }
    
    /**
     * method to select all those atoms contained in the selecion unit defined
     * by a sphere with a center and appropriate radius.
     *
     * @param center - the center of the sphere
     * @param radius - the radius of the sphere
     * @param clearPrevious clears the previous selection (if true)
     */
    public void selectIfContainedIn(Point3D center, double radius, 
                                    boolean clearPrevious) {
        if (!isVisible()) return;
        
        if (clearPrevious) selectionStack.clear();
        ScreenAtom atom;
        
        for(int i=screenAtoms.size(); --i>=0;) {
            atom = screenAtoms.get(zBuffer[i]);
            
            if (center.distanceFrom(atom.getAtom().getAtomCenter()) <= radius) {
                atom.setSelected(true);
                selectionStack.push(atom);
            } else {
                if (clearPrevious) atom.setSelected(false);
            } // end if
        } // end for
    }
    
    /**
     * method to select all those atoms contained in the selecion unit defined
     * by a bounding box.
     *
     * @param bb - the bounding box
     */
    public void selectIfContainedIn(BoundingBox bb) {
        if (!isVisible()) return;
        
        selectionStack.clear();
        
        ScreenAtom atom;
        
        for(int i=screenAtoms.size(); --i>=0;) {
            atom = screenAtoms.get(zBuffer[i]);
            
            if (bb.contains(atom.getAtom().getAtomCenter())) {
                atom.setSelected(true);
                selectionStack.push(atom);
            } else {
                atom.setSelected(false);
            } // end if
        } // end for
    }
    
    /**
     * Traverse through all screen atoms and get the closest visible one
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return the closest screen atom's Atom object ;)
     */
    public Atom getClosestAtom(int x, int y) {
        ScreenAtom satom = getClosestScreenAtom(x, y);
        
        if (satom != null) {
            return satom.getAtom();
        } else {
            return null;
        } // end if
    }

    /**
     * Get the atom that is closest to this atom in the selection stack
     *
     * @param sa the reference atom
     * @return the ScreenAtom that is closest to this atom
     */
    public ScreenAtom getClosestSelectedAtom(ScreenAtom sa) {
        ScreenAtom atom = null;
        double dist, minD = Double.MAX_VALUE, xx, yy, zz;
        int minIdx = -1;

        for(int i=0; i<selectionStack.size(); i++) {
            atom = selectionStack.get(i);

            if (atom == sa) continue;
            if (atom.getAtom().getIndex() == sa.getAtom().getIndex())
                continue;
            
            xx = atom.getX() - sa.getX();
            yy = atom.getY() - sa.getY();
            zz = atom.getZ() - sa.getZ();

            dist = xx*xx + yy*yy + zz*zz;

            if (dist < minD) {
                minD = dist;
                minIdx = i;
            } // end if
        } // end for

        if (minIdx == -1) return null;
        else return selectionStack.get(minIdx);
    }
    
    /**
     * Traverse through all screen atoms and get the closest visible 
     * ScreenAtom
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return the closest screen atom object ;)
     */
    public ScreenAtom getClosestScreenAtom(int x, int y) {
        ScreenAtom atom = null;
        int xx, yy, zz, dist, minD = Integer.MAX_VALUE, minIdx = -1;
        
        for(int i=screenAtoms.size(); --i>=0;) {
            atom = screenAtoms.get(zBuffer[i]);
            xx = atom.getCurrentX() - x;
            yy = atom.getCurrentY() - y;       
            zz = atom.getCurrentZ();
            dist = xx*xx + yy*yy + zz*zz;
            
            if (dist < minD ) { minD = dist; minIdx = i; }
        } // end for
        
        if (minIdx != -1) {
            return screenAtoms.get(zBuffer[minIdx]);
        } else {
            return null;
        } // end if
    }
    
    /**
     * called if the concerened molecule is changed
     */
    @Override
    public void moleculeChanged(MoleculeStateChangeEvent msce) {
        // if this is a dummy call, just refresh the scene 
        // .. and return silently
        if (msce == null) {
            buildMoleculeScene();
            return;
        } // end if
        
        System.out.println("MoleculeScene => Molecule Changed : " + msce
                + "; from : " + msce.getSource());
        
        switch (msce.getEventType()) {
            case MoleculeStateChangeEvent.REFRESH_EVENT:
                break; // skip any re creation of connectivity matrix
            case MoleculeStateChangeEvent.ATOM_ADDED:
            case MoleculeStateChangeEvent.ATOM_REMOVED:
            case MoleculeStateChangeEvent.MAJOR_MODIFICATION:
                try {
                    MoleculeBuilder mb = (MoleculeBuilder)
                            Utility.getDefaultImplFor(MoleculeBuilder.class)
                                   .newInstance();
                    
                    mb.rebuildConnectivity(msce);
                } catch (Exception e) {
                    System.err.println("MoleculeScene => " +
                            "Error while refresing geometry : "
                            + e.toString());
                    e.printStackTrace();
                } // end try .. catch block
                break;
        } // end of switch .. case block
        
        // TODO : more effective
        buildMoleculeScene();
        
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.MOLECULE_CHANGE);
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.GENERAL_SCENE_CHANGE);
    }
    
    /**
     * add a distance tracker
     *
     * @param atomIndex1 first index
     * @param atomIndex2 second index
     */
    public void addDistanceTracker(int atomIndex1, int atomIndex2) {
        trackers.add(
                new DistanceTracker(screenAtoms.get(atomIndex1),
                screenAtoms.get(atomIndex2)));
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * add a angle tracker
     *
     * @param atomIndex1 the fiest index
     * @param atomIndex2 the second index (atomIndex2 is the central angle)
     * @param atomIndex3 - the third index
     */
    public void addAngleTracker(int atomIndex1, int atomIndex2,
            int atomIndex3) {
        trackers.add(
                new AngleTracker(screenAtoms.get(atomIndex1),
                screenAtoms.get(atomIndex2),
                screenAtoms.get(atomIndex3)));
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * add a dihedral tracker
     *
     * @param atomIndex1 first index
     * @param atomIndex2 second index
     * @param atomIndex3 third indes(atomIndex2,atomIndex3 is the central angle)
     * @param atomIndex4 fourth index
     */
    public void addDihedralTracker(int atomIndex1, int atomIndex2,
            int atomIndex3, int atomIndex4) {
        trackers.add(
                new DihedralTracker(screenAtoms.get(atomIndex1),
                screenAtoms.get(atomIndex2),
                screenAtoms.get(atomIndex3),
                screenAtoms.get(atomIndex4)));
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * returns the last added tracker
     *
     * @return Tracker - the last added tracker
     */
    public Tracker getLastAddedTracker() {
        return trackers.get(trackers.size()-1);
    }
    
    /**
     * method to add a generic tracker
     *
     * @param tracker - the tracker to be added
     */
    public void addTracker(Tracker tracker) {
        trackers.add(tracker);
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * method to remove a generic tracker
     *
     * @param tracker - the tracker to be removed
     */
    public void removeTracker(Tracker tracker) {
        trackers.remove(tracker);
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * method to add a screen label
     *
     * @param label - the label to be added
     */
    public void addLabel(ScreenLabel label) {
        screenLabels.add(label);
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * method to remove a screen label
     *
     * @param label - the label to be removed
     */
    public void removeLabel(ScreenLabel label) {
        screenLabels.remove(label);
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * method to remove all trackers
     */
    public void removeAllTrackers() {
        trackers.clear();
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * show/ hide the symbol label
     *
     * @param showSymbol - boolean true/ false to show or hide the symbols
     */
    public void showSymbolLables(boolean showSymbol) {
        for(ScreenAtom atom : screenAtoms) {
            if (atom.isSelected()) {
                atom.showSymbolLabel(showSymbol);
                atom.setSelected(false);
            } // end if
        } // end for
        
        selectionStack.clear();
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * show/ hide the ID label
     *
     * @param showID - boolean true/ false to show or hide the ID
     */
    public void showIDLables(boolean showID) {
        for(ScreenAtom atom : screenAtoms) {
            if (atom.isSelected()) {
                atom.showIDLabel(showID);
                atom.setSelected(false);
            } // end if
        } // end for
        
        selectionStack.clear();
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * show/ hide the center label
     *
     * @param showCenter - boolean true/ false to show or hide the atom center
     */
    public void showCenterLables(boolean showCenter) {
        for(ScreenAtom atom : screenAtoms) {
            if (atom.isSelected()) {
                atom.showCenterLabel(showCenter);
                atom.setSelected(false);
            } // end if
        } // end for
        
        selectionStack.clear();
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * show/ hide a fragment
     *
     * @param scheme The fragmentation scheme under consideration
     * @param fragment The fragment to be shown or hidden
     * @param showIt boolean true/ flase to show or hide the specified fragment
     */
    public void showFragment(FragmentationScheme scheme, Fragment fragment,
                             boolean showIt) {
        if (fragmentSceneMap == null) {
            fragmentSceneMap = new HashMap<FragmentationScheme,
                    ArrayList<FragmentScene>>();
        } // end if
        
        // first check if we already have cached this?
        if (fragmentSceneMap.containsKey(scheme)) {
            ArrayList<FragmentScene> scenes = fragmentSceneMap.get(scheme);
            
            for(FragmentScene scene : scenes) {
                // NOTE: here a real object comparison is intended!
                // Because thats what we really want to have while
                // caching fragment scenes.
                if (scene.getFragment() == fragment) {
                    scene.setVisible(showIt);
                    fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
                    return;
                } // end if
            } // end for
        } else {
            // add the new key
            fragmentSceneMap.put(scheme, new ArrayList<FragmentScene>());
        } // end if
        
        // if not found then make a new entry and cache the FragmentScene
        FragmentScene newScene = new FragmentScene(scheme, fragment, molecule);
        
        newScene.setVisible(showIt); // ?? first time should be true
        fragmentSceneMap.get(scheme).add(newScene);
        
        // notify the scene painters!
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * show/ hide a fragment
     *
     * @param scheme The fragmentation scheme under consideration
     * @param fragment The fragment to be shown or hidden
     * @return boolean true/ flase to show or hide the specified fragment
     */
    public boolean isFragmentVisible(FragmentationScheme scheme,
                                     Fragment fragment) {
        if (fragmentSceneMap == null) {
            return false;
        } // end if
        
        // first check if we already have cached this?
        if (fragmentSceneMap.containsKey(scheme)) {
            ArrayList<FragmentScene> scenes = fragmentSceneMap.get(scheme);
            
            for(FragmentScene scene : scenes) {
                // NOTE: here a real object comparison is intended!
                // Because thats what we really want to have while
                // caching fragment scenes.
                if (scene.getFragment() == fragment) {
                    return scene.isVisible();
                } // end if
            } // end for
        } // end if
        
        // probably not visible
        return false;
    }
    
    /**
     * Attach a property scene to this molecule scene
     *
     * @param ps the property scene object to attach
     */
    public void addPropertyScene(PropertyScene ps) {
        if (propertyScenes == null)
            propertyScenes = new ArrayList<PropertyScene>();
        
        propertyScenes.add(ps);
        ps.setName((++propertyCount) + " ");
        ps.addPropertySceneChangeListener(this);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.PROPERTY_ADDED);
        sceneChangeEvent.setChangedPropertyScene(ps);
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
        sceneChangeEvent.setChangedPropertyScene(null);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.GENERAL_SCENE_CHANGE);
    }
    
    /**
     * Remove a property scene from this molecule scene
     *
     * @param ps the property scene object to remove
     */
    public void removePropertyScene(PropertyScene ps) {
        propertyScenes.remove(ps);
        ps.removePropertySceneChangeListener(this);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.PROPERTY_REMOVED);
        sceneChangeEvent.setChangedPropertyScene(ps);
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
        sceneChangeEvent.setChangedPropertyScene(null);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.GENERAL_SCENE_CHANGE);
    }
    
    /**
     * return a list of all property scene objects
     *
     * @return return a list of all property scene objects, null if no objects
     *         are found.
     */
    public Iterator<PropertyScene> getAllPropertyScene() {
        if (propertyScenes == null) return null;
        
        return propertyScenes.iterator();
    }
    
    /**
     * removes all the captions from the screen atoms, and removes
     * their selection state; if they are selected.
     */
    public void removeAllCaptions() {
        for(ScreenAtom atom : screenAtoms) {
            atom.showSymbolLabel(false);
            atom.showIDLabel(false);
            atom.showCenterLabel(false);
            atom.setSelected(false);
        } // end for
        
        selectionStack.clear();
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * clears the selection stack and removes all selections
     */
    public void removeAllSelections() {
        selectionStack.clear();
        
        for(ScreenAtom sa : screenAtoms) {
            sa.setSelected(false);
        } // end for
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /**
     * Get the selection as a plain molecule object, with no bonding
     * information. The atom indices and the connectivity are all
     * <strong> same </strong> objects.
     *
     * @return Molecule object of the selection, null if no selection is made
     * @throws ClassNotFoundException if unable to find Molecule class
     * @throws InstantiationException if unable to instantiate Molecule class
     * @throws IllegalAccessException if unable to instantiate Molecule class
     */
    public Molecule getSelectionAsMolecule() throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        if (selectionStack == null) return null;
        if (selectionStack.size() == 0) return null;
        
        // TODO : this may be optimized to generate the new molecule object
        // only when the selection changes
        
        Molecule mol =
                (Molecule) Utility.getDefaultImplFor(Molecule.class).newInstance();
        
        mol.setTitle(this.molecule.getTitle() + "-part");
        
        for(ScreenAtom selection : selectionStack) {
            mol.addAtom(selection.getAtom());
        } // end for
        
        return mol;
    }
    
    /**
     * Get the selection as a plain molecule object, with no bonding
     * information. The atom indices and the connectivity are all
     * <strong> new </strong> objects.
     *
     * @return Molecule object of the selection, null if no selection is made
     * @throws ClassNotFoundException if unable to find Molecule class
     * @throws InstantiationException if unable to instantiate Molecule class
     * @throws IllegalAccessException if unable to instantiate Molecule class
     */
    public Molecule getSelectionAsNewMolecule() throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        if (selectionStack == null) return null;
        if (selectionStack.size() == 0) return null;
        
        // TODO : this may be optimized to generate the new molecule object
        // only when the selection changes
        
        Molecule mol =
                (Molecule) Utility.getDefaultImplFor(Molecule.class).newInstance();
        
        mol.setTitle(this.molecule.getTitle() + "-part");
        
        Iterator<ScreenAtom> selections = selectionStack.iterator();
        Atom atom;
        int index = 0;
        
        while(selections.hasNext()) {
            atom = selections.next().getAtom();
            mol.addAtom(new Atom(atom.getSymbol(), atom.getCharge(),
                    atom.getAtomCenter(), index));
            index++;
        } // end while
        
        return mol;
    }
    
    /** Getter for property selectionStack.
     * @return Value of property selectionStack.
     *
     */
    public Stack<ScreenAtom> getSelectionStack() {
        return this.selectionStack;
    }
    
    /** Setter for property selectionStack.
     * @param selectionStack New value of property selectionStack.
     *
     */
    public void setSelectionStack(Stack<ScreenAtom> selectionStack) {
        setSelectionStack(selectionStack, false);
    }
    
    /** Setter for property selectionStack.
     * @param selectionStack New value of property selectionStack.
     * @param compareEach compare each atom before selecting it
     */
    public void setSelectionStack(Stack<ScreenAtom> selectionStack,
                                  boolean compareEach) {
        this.selectionStack = selectionStack;
        
        if (selectionStack == null) return;
        
        // first deselect all atoms
        for(ScreenAtom sa : screenAtoms) {
            sa.setSelected(false);
        } // end for
        
        if (!compareEach) {
            // and then select only those on stack
            for(ScreenAtom sa : selectionStack) {
                sa.setSelected(true);
            } // end for
        } else {
            for(ScreenAtom sa1 : screenAtoms) {
                for(ScreenAtom sa2 : selectionStack) {
                    if ((sa2.getAtom() == sa1.getAtom())
                        || (sa2.getAtom().equals(sa1.getAtom()))) {
                        sa1.setSelected(true);
                    } // end if
                } // end for
            } // end for
        } // end for
        
        // fire the change event
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /** Setter for property indexSelectionStack.
     * @param selectionStack New value of property selectionStack.
     *
     */
    public void setIndexSelectionStack(Stack<Integer> selectionStack) {
        // the method has been coded in this way so as to maintain the order
        // of stack elecments
        
        this.selectionStack.clear();
        
        // first deselect all atoms
        for(ScreenAtom sa : screenAtoms) {
            sa.setSelected(false);
        } // end for
        
        Iterator<Integer> atomIndices = selectionStack.iterator();
        ScreenAtom atom;
        
        // and then select the atoms in that required order
        while(atomIndices.hasNext()) {
            atom = screenAtoms.get((atomIndices.next()).intValue());
            if(atom != null) {
                atom.setSelected(true);
                this.selectionStack.push(atom);
            } // end if
        } // end while
    }
    
    /** Getter for property trackers.
     * @return Value of property trackers.
     *
     */
    public ArrayList<Tracker> getTrackers() {
        return this.trackers;
    }
    
    /** Setter for property visible.
     * @param visible New value of property visible.
     *
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /** Getter for property enable3D.
     * @return Value of property enable3D.
     *
     */
    public boolean isEnable3D() {
        return this.enable3D;
    }
    
    /** Setter for property enable3D.
     * @param enable3D New value of property enable3D.
     *
     */
    public void setEnable3D(boolean enable3D) {
        // rebuild scene if required
        if (this.enable3D != enable3D) {
            this.enable3D = enable3D;
            buildMoleculeScene();
        } else {
            this.enable3D = enable3D;
        } // end if
        
        // and fire up the change event
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
    }
    
    /** Registers MoleculeSceneChangeListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addMoleculeSceneChangeListener(
            MoleculeSceneChangeListener listener) {
        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(MoleculeSceneChangeListener.class, listener);
    }
    
    /** Removes MoleculeSceneChangeListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeMoleculeSceneChangeListener(
            MoleculeSceneChangeListener listener) {
        if (listenerList != null) {
            listenerList.remove(MoleculeSceneChangeListener.class, listener);
        } // end if
    }
    
    /**
     * The overloaded toString()
     */
    @Override
    public String toString() {
        return molecule.toString();
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     *
     */
    protected void fireMoleculeSceneChangeListenerSceneChanged(
            MoleculeSceneChangeEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MoleculeSceneChangeListener.class) {
                ((MoleculeSceneChangeListener)listeners[i+1]).sceneChanged(event);
            }
        }
    }
    
    /**
     * called when a property scene change event occurs.
     */
    @Override
    public void propertySceneChanged(PropertySceneChangeEvent ce) {
        // we just need to "forward" this fire call
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.PROPERTY_CHANGE);
        fireMoleculeSceneChangeListenerSceneChanged(sceneChangeEvent);
        sceneChangeEvent.setType(MoleculeSceneChangeEvent.GENERAL_SCENE_CHANGE);
    }
    
    /**
     * overridden finalize() method
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
    }
    
    /**
     * Add selection to visible fragment and notify back the stuff
     *
     * @param fael FragmentAtomEditListener object to call back and
     *        notify any changes
     */ 
    public void addSelectionToVisibleFragments(FragmentAtomEditListener fael) {
        if (fragmentSceneMap == null) {
            return;
        } // end if
        
        for(FragmentationScheme scheme : fragmentSceneMap.keySet()) {
            for(FragmentScene fScene : fragmentSceneMap.get(scheme)) {
                if (fScene.isVisible()) {
                    Fragment frag = fScene.getFragment();
                    for(ScreenAtom sa : selectionStack) {
                        FragmentAtom fa = new FragmentAtom(sa.getAtom());
                        
                        frag.addFragmentAtom(fa);
                        
                        if (fael == null) continue;
                        
                        FragmentAtomEditEvent faee 
                                    = new FragmentAtomEditEvent(this);
                        
                        faee.setMolecule(molecule);
                        faee.setFragmentationScheme(scheme);
                        faee.setFragment(frag);
                        faee.setFragmentAtom(fa);
                        faee.setEventType(FragmentAtomEditEvent
                                         .FragmentAtomEditEventType.ATOM_ADDED);
                        fael.fragmentAtomEdited(faee);
                    } // end for
                    
                    scheme.getFragmentList().triggerListeners(frag);
                } // end if
            } // end for
        } // end for
        
        // clear the stack
        selectionStack.clear();
    }

    /**
     * Add selection as a new fragment and notify back the stuff
     *
     * @param fael FragmentAtomEditListener object to call back and
     *        notify any changes
     */ 
    public void addSelectionAsFragments(FragmentAtomEditListener fael) {
        // TODO: Warning this method as of now adds the fragment to 
        // each and every fragmentation scheme .. which needs to be
        // changed.
        try { 
         Iterator<FragmentationScheme> fss 
                 = molecule.getAllFragmentationSchemes();
         
         while(fss.hasNext()) {
            FragmentationScheme scheme = fss.next();
         
            Fragment frag = (Fragment) 
                    Utility.getDefaultImplFor(Fragment.class).newInstance();
            
            for(ScreenAtom sa : selectionStack) {
                FragmentAtom fa = new FragmentAtom(sa.getAtom());

                frag.addFragmentAtom(fa);

                if (fael == null) continue;

                FragmentAtomEditEvent faee 
                            = new FragmentAtomEditEvent(this);

                faee.setMolecule(molecule);
                faee.setFragmentationScheme(scheme);
                faee.setFragment(frag);
                faee.setFragmentAtom(fa);
                faee.setEventType(FragmentAtomEditEvent
                                 .FragmentAtomEditEventType.ATOM_ADDED);
                fael.fragmentAtomEdited(faee);
            } // end for
           
            frag.setParentMolecule(molecule);
            scheme.getFragmentList().addFragment(frag);
         } // end for
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in adding fragment: " + e.toString());
        } // end try .. catch block
        
        // clear the stack
        selectionStack.clear();
    }
    
    /**
     * Remove selection from visible fragment
     *
     * @param fael FragmentAtomEditListener object to call back and
     *        notify any changes
     */ 
    public void removeSelectionFromVisibleFragments(
                                          FragmentAtomEditListener fael) {
        if (fragmentSceneMap == null) {
            return;
        } // end if
        
        for(FragmentationScheme scheme : fragmentSceneMap.keySet()) {
            for(FragmentScene fScene : fragmentSceneMap.get(scheme)) {
                if (fScene.isVisible()) {
                    Fragment frag = fScene.getFragment();
                    for(ScreenAtom sa : selectionStack) {
                        FragmentAtom fa = new FragmentAtom(sa.getAtom());
                                                
                        frag.removeFragmentAtom(fa);
                         
                        if (fael == null) continue;
                        
                        FragmentAtomEditEvent faee 
                                    = new FragmentAtomEditEvent(this);
                        
                        faee.setMolecule(molecule);
                        faee.setFragmentationScheme(scheme);
                        faee.setFragment(frag);
                        faee.setFragmentAtom(fa);
                        faee.setEventType(FragmentAtomEditEvent
                                       .FragmentAtomEditEventType.ATOM_REMOVED);
                        fael.fragmentAtomEdited(faee);
                    } // end for
                } // end if
            } // end for
            
            scheme.getFragmentList().triggerListeners();
        } // end for
        
        // clear the stack
        selectionStack.clear();
    }
    
    private boolean useUniformColor = false;
    private Color uniformColor = null;
    
    /**
     * Set uniform color for this molecule scene!
     *
     * @param color the uniform color to be used.
     */
    public void setUniformColor(Color color) {
        uniformColor = color;
        useUniformColor = true;
        
        for(ScreenAtom sa : screenAtoms) {
            sa.setColor(color);
        } // end for
    }
    
    /**
     * Get current uniform color for this molecule scene.
     * 
     * @return the uniform color value
     */
    public Color getUniformColor() {
        return uniformColor;
    }
    
    /**
     * Returns a list of current screen atoms
     * 
     * @return list of screen atoms
     */
    public Iterator<ScreenAtom> getScreenAtoms() {
        return screenAtoms.iterator();
    }

    /** a rendering task */
    private class RenderingTask extends AbstractSimpleParallelTask {

        private int startAtomIndex, endAtomIndex;
        private Graphics2D g2d;
        private PaintGlyphObject pgo;

        public RenderingTask(int totalItems, Graphics2D g2d) {
            this.totalItems = totalItems;
            this.g2d = g2d;
        }

        public RenderingTask(int totalItems, PaintGlyphObject pgo) {
            this.totalItems = totalItems;
            this.pgo = pgo;
        }
        
        private RenderingTask(int startItem, int endItem, Graphics2D g2d) {
            this.startAtomIndex = startItem;
            this.endAtomIndex   = endItem;
            this.g2d = g2d;
        }

        private RenderingTask(int startItem, int endItem, PaintGlyphObject pgo) {
            this.startAtomIndex = startItem;
            this.endAtomIndex   = endItem;
            this.pgo = pgo;
        }
        
        @Override
        public SimpleParallelTask init(int startItem, int endItem) {
            return new RenderingTask(startItem, endItem, g2d);
        }

        @Override
        public void run() {
            ScreenAtom atom;
            
            for (int i = startAtomIndex; i < endAtomIndex; i++) {
                atom = screenAtoms.get(zBuffer[i]);

                // the bonds
                for (ScreenBond bond : atom.getScreenBondList()) {
                    bond.draw(g2d);
                } // end for

                atom.draw(g2d);
            } // end for
        }
    }
} // end of class MoleculeScene
