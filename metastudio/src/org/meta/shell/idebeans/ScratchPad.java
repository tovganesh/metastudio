/*
 * ScratchPad.java
 *
 * Created on January 8, 2006, 10:48 AM
 *
 */

package org.meta.shell.idebeans;


import java.awt.Color;
import java.util.Stack;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.shell.ide.MeTA;
import org.meta.common.resource.ImageResource;
import org.meta.math.Vector3D;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 * A general scratch pad, with options to paint objects in 3d.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScratchPad extends MoleculeViewerFrame {
    
    /** Creates a new instance of ScratchPad */
    public ScratchPad(MeTA ideInstance) {
        super(ideInstance);
        
        setTitle("Scratch Pad");
        setShowMoleculeRelatedUI(false);
        setFrameIcon(ImageResource.getInstance().getScratchPad());
    }
    
    /**
     * Draws a line between two specified points
     *
     * @param p1 the first point
     * @param p2 the second point
     */
    public void drawLine3D(Point3D p1, Point3D p2) {
        try {
            Molecule mol = (Molecule) 
                   Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom("X", 0.0, p1, 0));
            mol.addAtom(new Atom("X", 0.0, p2, 1));
            
            mol.getAtom(0).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(0, BondType.SINGLE_BOND);
            
            addScene(new MoleculeScene(mol));
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawLine3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * Draws a line between two specified points
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param color the color for this line
     */
    public void drawLine3D(Point3D p1, Point3D p2, Color color) {
        try {
            Molecule mol = (Molecule) 
                   Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom("X", 0.0, p1, 0));
            mol.addAtom(new Atom("X", 0.0, p2, 1));
            
            mol.getAtom(0).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(0, BondType.SINGLE_BOND);
            
            MoleculeScene ms = new MoleculeScene(mol);
            ms.setUniformColor(color);
            
            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawLine3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }

    /**
     * Draws a 3D box
     *
     * @param bb the bounding box
     * @param color the color for this bounding box
     */
    public void drawBox3D(BoundingBox bb, Color color) {
        try {
            Molecule mol = (Molecule)
                   Utility.getDefaultImplFor(Molecule.class).newInstance();

            int i = 0;
            for(Point3D pt : bb.corners())
                mol.addAtom(new Atom("X", 0.0, pt, i++));

            mol.getAtom(0).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(0, BondType.SINGLE_BOND);
            mol.getAtom(0).addConnection(3, BondType.SINGLE_BOND);
            mol.getAtom(3).addConnection(0, BondType.SINGLE_BOND);
            mol.getAtom(0).addConnection(6, BondType.SINGLE_BOND);
            mol.getAtom(6).addConnection(0, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(2, BondType.SINGLE_BOND);
            mol.getAtom(2).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(7, BondType.SINGLE_BOND);
            mol.getAtom(7).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(2).addConnection(3, BondType.SINGLE_BOND);
            mol.getAtom(3).addConnection(2, BondType.SINGLE_BOND);
            mol.getAtom(2).addConnection(4, BondType.SINGLE_BOND);
            mol.getAtom(4).addConnection(2, BondType.SINGLE_BOND);
            mol.getAtom(3).addConnection(5, BondType.SINGLE_BOND);
            mol.getAtom(5).addConnection(3, BondType.SINGLE_BOND);
            mol.getAtom(4).addConnection(7, BondType.SINGLE_BOND);
            mol.getAtom(7).addConnection(4, BondType.SINGLE_BOND);
            mol.getAtom(4).addConnection(5, BondType.SINGLE_BOND);
            mol.getAtom(5).addConnection(4, BondType.SINGLE_BOND);
            mol.getAtom(5).addConnection(6, BondType.SINGLE_BOND);
            mol.getAtom(6).addConnection(6, BondType.SINGLE_BOND);
            mol.getAtom(6).addConnection(7, BondType.SINGLE_BOND);
            mol.getAtom(7).addConnection(6, BondType.SINGLE_BOND);

            MoleculeScene ms = new MoleculeScene(mol);
            ms.setUniformColor(color);

            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawLine3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }

    /**
     * Draws a 3D box
     *
     * @param bb the bounding box
     */
    public void drawBox3D(BoundingBox bb) {
        try {
            Molecule mol = (Molecule)
                   Utility.getDefaultImplFor(Molecule.class).newInstance();

            int i = 0;
            for(Point3D pt : bb.corners())
                mol.addAtom(new Atom("X", 0.0, pt, i++));

            mol.getAtom(0).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(0, BondType.SINGLE_BOND);
            mol.getAtom(0).addConnection(3, BondType.SINGLE_BOND);
            mol.getAtom(3).addConnection(0, BondType.SINGLE_BOND);
            mol.getAtom(0).addConnection(6, BondType.SINGLE_BOND);
            mol.getAtom(6).addConnection(0, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(2, BondType.SINGLE_BOND);
            mol.getAtom(2).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(1).addConnection(7, BondType.SINGLE_BOND);
            mol.getAtom(7).addConnection(1, BondType.SINGLE_BOND);
            mol.getAtom(2).addConnection(3, BondType.SINGLE_BOND);
            mol.getAtom(3).addConnection(2, BondType.SINGLE_BOND);
            mol.getAtom(2).addConnection(4, BondType.SINGLE_BOND);
            mol.getAtom(4).addConnection(2, BondType.SINGLE_BOND);
            mol.getAtom(3).addConnection(5, BondType.SINGLE_BOND);
            mol.getAtom(5).addConnection(3, BondType.SINGLE_BOND);
            mol.getAtom(4).addConnection(7, BondType.SINGLE_BOND);
            mol.getAtom(7).addConnection(4, BondType.SINGLE_BOND);
            mol.getAtom(4).addConnection(5, BondType.SINGLE_BOND);
            mol.getAtom(5).addConnection(4, BondType.SINGLE_BOND);
            mol.getAtom(5).addConnection(6, BondType.SINGLE_BOND);
            mol.getAtom(6).addConnection(6, BondType.SINGLE_BOND);
            mol.getAtom(6).addConnection(7, BondType.SINGLE_BOND);
            mol.getAtom(7).addConnection(6, BondType.SINGLE_BOND);

            MoleculeScene ms = new MoleculeScene(mol);
            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawLine3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }

    /**
     * Draws a line between two specified points
     *
     * @param v1 the first vector
     */
    public void drawVector3D(Vector3D v1) {
        try {
            Molecule mol = (Molecule) 
                 Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom("vec", 0.0, v1.toPoint3D(), 0));
            
            addScene(new MoleculeScene(mol));
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawVector3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * Draws a vector
     *
     * @param v1 the first vector
     * @param base use a different base than the one defined by this vector
     */
    public void drawVector3D(Vector3D v1, Point3D base) {
        try {
            Molecule mol = (Molecule) 
                 Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom("X", 0.0, base, 0));            
            MoleculeScene ms = new MoleculeScene(mol);
            ScreenAtom sa = new ScreenAtom(new Atom("vec", 0.0, 
                                                     v1.toPoint3D(), 1));
            sa.setVectorBase(base);
            ms.addScreenAtom(sa);
            
            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawVector3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * Draws a vector 
     *
     * @param v1 the first vector
     * @param color the color of this vector object
     */
    public void drawVector3D(Vector3D v1, Color color) {
        try {
            Molecule mol = (Molecule) 
                 Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom("vec", 0.0, v1.toPoint3D(), 0));
            
            MoleculeScene ms = new MoleculeScene(mol);
            
            ms.setUniformColor(color);
            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawVector3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * Draws a vector
     *
     * @param v1 the first vector
     * @param base use a different base than the one defined by this vector
     * @param color the color of this vector object
     */
    public void drawVector3D(Vector3D v1, Point3D base, Color color) {
        try {
            Molecule mol = (Molecule) 
                 Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom("X", 0.0, base, 0));            
            MoleculeScene ms = new MoleculeScene(mol);
            ScreenAtom sa = new ScreenAtom(new Atom("vec", 0.0, 
                                                     v1.toPoint3D(), 1));
            sa.setVectorBase(base);
            ms.addScreenAtom(sa);
            
            addScene(ms);
            ms.setUniformColor(color);
            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawVector3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * Draws a text item on a specified points
     *
     * @param p1 the point
     * @param text the text to be drawn
     */
    public void drawText(Point3D p1, String text) {
        try {
            Molecule mol = (Molecule) 
                 Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom(text, 0.0, p1, 0));
            
            MoleculeScene ms = new MoleculeScene(mol);
            
            Stack<Integer> s = new Stack<Integer>();
            s.push(0);
            ms.setIndexSelectionStack(s);
            ms.showSymbolLables(true);
            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawVector3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * Draws a text item on a specified points
     *
     * @param p1 the point
     * @param text the text to be drawn
     * @param color the color of this text
     */
    public void drawText(Point3D p1, String text, Color color) {
        try {
            Molecule mol = (Molecule) 
                 Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            mol.addAtom(new Atom(text, 0.0, p1, 0));
            
            MoleculeScene ms = new MoleculeScene(mol);
            
            Stack<Integer> s = new Stack<Integer>();
            s.push(0);
            ms.setIndexSelectionStack(s);
            ms.showSymbolLables(true);
            ms.setUniformColor(color);
            addScene(ms);
        } catch (Exception ignored) {
            System.out.println("Exception in ScratchPad.drawVector3D(): "
                    + ignored);
            ignored.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * add a MoleculeScene to this viewer
     */
    @Override
    public void addScene(MoleculeScene scene) {
        super.addScene(scene);
        
        setTitle("Scratch Pad");
    }
} // end of class ScratchPad
