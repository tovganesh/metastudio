/*
 * MoleculeViewer.java
 *
 * Created on December 21, 2003, 2:29 PM
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j2d.Axis;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.meta.math.Matrix3D;
import org.meta.shell.ide.MeTA;
import org.meta.common.Utility;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.common.resource.ColorResource;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.movie.MovieMode;
import org.meta.movie.event.MovieUpdateEvent;
import org.meta.movie.event.MovieUpdateListener;
import org.meta.shell.idebeans.FragmentationWizard;
import org.meta.shell.idebeans.NotificationWindow;
import org.meta.shell.idebeans.viewers.Viewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.*;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo.*;
import org.meta.common.resource.StringResource;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.molecule.property.electronic.PointProperty;
import org.meta.molecule.property.electronic.PropertyNotDefinedException;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.*;

/**
 * A very powerful and most important component of MeTA Studio. <BR>
 * A powerful molecule viewer with capabilities of manually generating fragments
 * and changing bonding and grouping information; which aids in fragment
 * creation by automatic algorithm(s).
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 * 
 * @since 2.0.09022009 Josh Milthorpe (minimum scale factor of 0.1 to prevent
 *                           reversing of molecule while zooming out)
 */
public class MoleculeViewer extends JPanel implements Viewer, MovieMode,
        MouseListener, MouseMotionListener,
        MouseWheelListener,
        MoleculeSceneChangeListener,
        UndoableEditListener {
    
    /** The place where all the drawing takes place .. in the background */
    protected BufferedImage theCanvas;
    
    /** The canvas to be used in case Java3D is enabled */
    private JPanel theCanvas3D;
    
    /** Holds value of property sceneList. */
    private Vector<MoleculeScene> sceneList;
    
    /** Holds value of property transform. */
    protected Matrix3D transform;
    
    protected BoundingBox boundingBox;
    
    /** Holds value of property scaleFactor. */
    private double scaleFactor;
    
    /** Holds value of property xFactor. */
    private double xFactor;
    
    /** Holds value of property xTheta. */
    protected double xTheta;
    
    /** Holds value of property yTheta. */
    protected double yTheta;
    
    /** Holds value of property zTheta. */
    protected double zTheta;
    
    protected int previousX, previousY;
    
    /** Holds value of property transformState. */
    private TransformState transformState;
    
    protected Matrix3D rotationMatrix, translationMatrix, tempMatrix;
    
    /** Holds value of property backgroundColor. */
    private Color backgroundColor;
    
    /** Holds value of property xTranslate. */
    protected double xTranslate;
    
    /** Holds value of property yTranslate. */
    protected double yTranslate;
    
    /** Holds value of property currentMovieScene. */
    private int currentMovieScene;
    
    /** Holds value of property movieDelay. */
    private int movieDelay;
    
    /** Holds value of property repeatMovie. */
    private boolean repeatMovie;
    
    private ViewerPopupMenu popup;
    
    private MeTA ideInstance;
    
    private Polygon selectionUnit;
    
    /** Holds value of property selectionState. */
    private SelectionState selectionState;
    
    /** Utility field used by event firing mechanism. */
    protected EventListenerList listenerList =  null;
    
    /** for handling the undo framework */
    protected UndoableEditEvent editEvent;
    protected ZoomUndoableEdit zoomEdit;
    protected TranslateUndoableEdit translateEdit;
    protected RotateUndoableEdit rotateEdit;
    protected SelectionUndoableEdit selectionEdit;
    protected UndoManager undoManager;
    protected CompoundEdit compoundEdit;
    protected boolean enableUndo;
    protected boolean inCompoundEditMode;
    
    // for notification window
    private NotificationWindow notificationWindow;
    private JLabel infoPane;
    
    // for drawing axis
    private Axis axis;
    
    // for drawing cuboid
    private ScreenCuboid screenCuboid;
    
    // for drawing "flat" sphere
    private ScreenSphere screenSphere;
    
    // for drawing adjustable grid
    private ScreenGrid screenGrid;
    
    // for movieMode
    private Thread movieThread;
    
    // for controling the movie
    private boolean pauseTheMovie, stopTheMovie;
    
    // flag to supress painting
    protected boolean doNotPaint;
    
    // movie update event
    private MovieUpdateEvent movieUpdateEvent;
    
    /**
     * Holds value of property smoothDrawing.
     */
    private boolean smoothDrawing;
    
    /**
     * Holds value of property axisDrawn.
     */
    private boolean axisDrawn;
    
    /**
     * Holds value of property enable3D
     */
    private boolean enable3D;
    
    // reference to parent frame
    private JInternalFrame parentFrame;
    private boolean isPartOfWorkspace;
    
    // for tool based mouse handlers
    // the current adapter
    protected MoleculeViewerMouseAdapter currentMouseAdapter;
    // the defult empty mouse adapter
    protected MoleculeViewerMouseAdapter defaultMouseAdapter;
    // tool state :: mouse adapter map
    protected HashMap<ViewerState, MoleculeViewerMouseAdapter> mouseAdapterMap;
    
    /** Creates a new instance of MoleculeViewer */
    public MoleculeViewer(MeTA ideInstance, JInternalFrame parentFrame) {
        sceneList = new Vector<MoleculeScene>(); // the scene list
        
        enableUndo = true; // yes we r able to undo by default
        showViewerPopup = true; // show popups by default
        
        // the transformations
        transform = new Matrix3D();
        transform.unit();
        
        tempMatrix = new Matrix3D();
        tempMatrix.unit();
        
        rotationMatrix = new Matrix3D();
        rotationMatrix.unit();
        
        translationMatrix = new Matrix3D();
        translationMatrix.unit();
        
        // the controls
        boundingBox = new BoundingBox();
        
        scaleFactor = 1.0;
        
        backgroundColor = Color.black;
        
        this.ideInstance = ideInstance;
        this.parentFrame = parentFrame;
        
        selectionUnit = new Polygon(); // the selection unit
        
        // the componants
        initComponents();
        
        transformState = TransformState.SCALE_STATE; // the default state
        selectionState = SelectionState.NO_STATE; // not in selection mode
        
        // the listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        
        // undo stuff
        undoManager  = new UndoManager();
        compoundEdit = new CompoundEdit();
        addUndoableEditListener(this);
        inCompoundEditMode = false;
        
        // smooth drawing is disabled by default
        smoothDrawing = false;
        
        // we need painting by default
        doNotPaint = false;
        
        // axis is not shown by default
        axisDrawn = false;
        
        // movie defaults
        repeatMovie = false;
        movieDelay  = 500; // seconds
        currentMovieScene = 0;
        movieUpdateEvent  = new MovieUpdateEvent(this);
        
        // no 3D by default
        enable3D = false;
        // no java 3d by default
        enableJava3D = false;
        
        // not a part of multi view by default
        multiViewEnabled = false;
        
        // not in interrogation mode by default
        interrogationMode = false;
        
        // mouse adapters maps
        mouseAdapterMap =
                new HashMap<ViewerState, MoleculeViewerMouseAdapter>(10);
        defaultMouseAdapter = new MoleculeViewerMouseAdapter();
        currentMouseAdapter = defaultMouseAdapter;
        
        mouseAdapterMap.put(TransformState.SCALE_STATE,
                new ScaleStateMouseAdapter());
        mouseAdapterMap.put(TransformState.TRANSLATE_STATE,
                new TranslateStateMouseAdapter());
        mouseAdapterMap.put(TransformState.ROTATE_STATE,
                new RotateStateMouseAdapter());
        mouseAdapterMap.put(TransformState.NO_STATE,
                defaultMouseAdapter);
        mouseAdapterMap.put(SelectionState.NO_STATE,
                defaultMouseAdapter);
        mouseAdapterMap.put(SelectionState.POINTER_SELECTION,
                new PointerSelectionMouseAdapter());
        mouseAdapterMap.put(SelectionState.FREE_HAND_SELECTION,
                new FreeHandSelectionMouseAdapter());
        mouseAdapterMap.put(SelectionState.CUBOID_SELECTION,
                new CuboidSelectionMouseAdapter());
        mouseAdapterMap.put(SelectionState.SPHERE_SELECTION,
                new SphereSelectionMouseAdapter());
    }
    
    /**
     * get the associated IDE instance
     */
    public MeTA getIDEInstance() {
        return ideInstance;
    }
    
    /**
     * get associated parent frame reference
     */
    public JInternalFrame getParentFrame() {
        return parentFrame;
    }
    
    /** This method is called from within the constructor to
     * initialize the UI.
     */
    private void initComponents() {
        popup = new ViewerPopupMenu(this);
    }
    
    /**
     * initialize the canvas on which the molecule is drawn
     */
    private synchronized void initCanvas() {
        if (!enableJava3D) {
            if ((theCanvas == null)
            && (getWidth() != 0) && (getHeight() != 0)
            || ((theCanvas.getWidth()  != getWidth())
            || (theCanvas.getHeight() != getHeight()))) {
                
                theCanvas = new BufferedImage(getWidth(),
                        getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                
                // and also recalculate the transforms
                recalculateTransformFactor();
                
                // and remake the axis
                axis = new Axis(30, getHeight()-30);
            } // end if
        } else {
            // Java3D is enabled
            if (theCanvas3D == null) {
                Java3DState state = readJava3DState();
                
                if (state.equals(Java3DState.UNDETERMINED)
                    || state.equals(Java3DState.EMBEDDED)) {
                    
                    writeJava3DState(Java3DState.EMBEDDING);
                    setSecondaryGraphicsEngine(
                            MoleculeViewerGraphicsEngineFactory.getInstance()
                            .getGraphicsEngine(
                               MoleculeViewerGraphicsEngineFactory
                                 .MoleculeViewerGraphicsEngineType.JAVA3D));
                    secondaryGraphicsEngine.setEnclosingMoleculeViewer(this);
                    theCanvas3D = secondaryGraphicsEngine.createEngineGraphics(
                                                                     sceneList);
                    setLayout(new BorderLayout());        
                    if ((getWidth() != 0) && (getHeight() != 0))
                        setSize(getWidth(), getHeight());
                    add(theCanvas3D, BorderLayout.CENTER);
                    secondaryGraphicsEngine.updateEngineGraphics();
                    writeJava3DState(Java3DState.EMBEDDED);
                } else {
                    // we need to set up an external JFrame
                    
                    // TODO: remove this hack
                    enableJava3D = false;
                    initCanvas();
                } // end if
            } // end if
        } // end if
    }
    
    /**
     * overridden paint method
     */
    @Override
    public void paint(Graphics g) {
        if (enableJava3D && (theCanvas3D != null)) {
            initCanvas();
            super.paint(g);
            theCanvas3D.setBackground(backgroundColor);
            theCanvas3D.repaint();
            
            return;
        } // end if
        
        if (doNotPaint) {
            // draw the prepared scene .. to avoid garbage on screen
            g.drawImage(theCanvas, 0, 0, getWidth(), getHeight(), this);
            
            return;
        } // end if                
        
        initCanvas();
        
        if (theCanvas == null) return;
        
        Graphics2D g2d = theCanvas.createGraphics();
                
        g2d.setBackground(backgroundColor);
        g2d.clearRect(0, 0, theCanvas.getWidth(), theCanvas.getHeight());
        
        // before drawing apply general scene transformations
        applyTransforms();
        
        // smooth drawing needed ?
        if (smoothDrawing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        } // end if
        
        // draw the molecules if they are there!
        if ((sceneList != null) && !sceneList.isEmpty()) {
            Enumeration<MoleculeScene> scenes = sceneList.elements();
            MoleculeScene scene;
            
            while(scenes.hasMoreElements()) {
                scene = scenes.nextElement();
                applyTransforms(scene);
                scene.draw(g2d);
            } // end while
        } // end if
        
        // check if we are in free hand selection mode ?
        if (selectionState.equals(SelectionState.FREE_HAND_SELECTION)) {
            g2d.setColor(ColorResource.getInstance().getSelectedAtomColor());
            g2d.draw(selectionUnit);
        } else if (selectionState.equals(SelectionState.CUBOID_SELECTION)) {
            // or we need to show cubiod selection?
            if (screenCuboid != null) screenCuboid.draw(g2d);
        } else if (selectionState.equals(SelectionState.SPHERE_SELECTION)) {
            // or we need to show sphere selection?
            if (screenSphere != null) screenSphere.draw(g2d);
        } // end if
        
        // check if we need to draw axis?
        if (axisDrawn) {
            if (axis == null) {
                axis = new Axis(30, getHeight()-30);
            } // end if
            
            axis.setTransform(rotationMatrix);
            axis.applyTransforms();
            axis.draw(g2d);
        } // end if
        
        // check if we need to draw the grid?
        if (gridDrawn) {
            initScreenGrid();
            
            screenGrid.setTransform(transform);
            screenGrid.applyTransforms();
            screenGrid.draw(g2d);
        } // end if
        
        // draw the prepared scene
        g.drawImage(theCanvas, 0, 0, getWidth(), getHeight(), this);
    }
    
    /**
     * Overridden update method.
     * 
     * @param g the Graphics instance
     */
    @Override
    public void update(Graphics g) { 
        paint(g); 
    } 

    /**
     * get RenderedImage instance of Java3D scene, may throw exception
     */
    private RenderedImage getJava3DSceneImage() {        
        if (secondaryGraphicsEngine != null) 
            return secondaryGraphicsEngine.getScreenImage();
        
        return null;
    }
    
    /**
     * method for saving images to disk in specified format
     *
     * @param fileName the file to which the image is to be saved
     * @param format the format of the image, typically "png", "jpg" etc.
     * @throws IOException if the operation is unsuccessful
     */
    public void writeImageToFile(String fileName, String format)
    throws IOException {
        if (theCanvas == null) {
            throw new IOException("Couldn't save image to file : " + fileName);
        } // end if
        
        FileOutputStream fos = new FileOutputStream(fileName);
        
        if (!enableJava3D) {
            if (!ImageIO.write((RenderedImage) theCanvas, format, fos)) {
                fos.close();
                
                throw new IOException("Couldn't save image to file : "
                        + fileName);
            } // end if
        } else {
            if (!ImageIO.write(getJava3DSceneImage(), format, fos)) {
                fos.close();
                
                throw new IOException("Couldn't save image to file : "
                        + fileName);
            } // end if
        } // end if
        
        fos.close();
    }

    /**
     * Return the instance of the first visible molecule scene
     *
     * @return MoleculeScene instance of the first visible molecule scene
     *         or else null
     */
    public MoleculeScene getFirstVisibleScene() {
        for(MoleculeScene scene : sceneList) {
            if (scene.isVisible()) return scene;
        } // end for

        return null;
    }
    
    /**
     * Return the instance of the first selected molecule scene
     *
     * @return MoleculeScene instance of the first selected one in the list
     *         else null.
     */
    public MoleculeScene getFirstSelectedScene() {
        Iterator scenes = sceneList.iterator();
        
        while(scenes.hasNext()) {
            MoleculeScene scene = (MoleculeScene) scenes.next();
            
            if (scene.getSelectionStack().size() > 0) {
                return scene;
            } // end if
        } // end while
        
        // no selection !
        return null;
    }
    
    /**
     * Is this viewer a part of a workspace UI?
     */
    public boolean isPartOfWorkspace() {
        return ideInstance.getWorkspaceDesktop().isWorkspaceFrame(parentFrame);
    }
    
    /** Getter for property sceneList.
     * @return Value of property sceneList.
     *
     */
    public Vector<MoleculeScene> getSceneList() {
        return this.sceneList;
    }
    
    /** Setter for property sceneList.
     * @param sceneList New value of property sceneList.
     *
     */
    public void setSceneList(Vector<MoleculeScene> sceneList) {
        this.sceneList = null;

        for(MoleculeScene scene : sceneList) addScene(scene);
    }
    
    /**
     * add a MoleculeScene to this viewer
     */
    public synchronized void addScene(MoleculeScene scene) {
        if (sceneList == null) {
            sceneList = new Vector<MoleculeScene>();
        } // end if
        
        scene.addMoleculeSceneChangeListener(this);
        scene.setEnable3D(enable3D);        
        sceneList.add(scene);
        
        // recalculate bounding box on addition
        // if we are the first item, this is the bounding box
        if (sceneList.size() == 1) {
            try {
                boundingBox = (BoundingBox) scene.getMolecule().getBoundingBox()
                                                               .clone();
            } catch (CloneNotSupportedException cnse) {
                System.out.println("Unexpected error : " + cnse.toString());
                cnse.printStackTrace();
            } // end of try .. catch block
        } else { // else we need to combine from previous boxes
            boundingBox = boundingBox.combine(
                                         scene.getMolecule().getBoundingBox());
        } // end if
        
        // fire the undo event, so that we can undo!
        SceneUndoableEdit sceneUndo = new SceneUndoableEdit(this);
        sceneUndo.setMoleculeScene(scene);
        editEvent = new UndoableEditEvent(this, sceneUndo);
        fireUndoableEditListenerUndoableEditHappened(editEvent);
        
        // set up the new transformations .. to bring the scene to the center
        recalculateTransformFactor();
        
        // repaint the scenes
        repaint();
    }    
    
    /**
     * remove a MoleculeScene from this viewer
     */
    public synchronized void removeScene(MoleculeScene scene) {
        if (sceneList == null) return;
        if (sceneList.size() == 0) return;
        
        scene.removeMoleculeSceneChangeListener(this);
        scene.clearAllReferences();
        
        sceneList.remove(scene);
        
        // recalculate bounding box on removal
        if (sceneList.size() != 0) {
            boundingBox = ((MoleculeScene) sceneList.get(0))
            .getMolecule().getBoundingBox();
            for(int i=1; i<sceneList.size(); i++) {
                boundingBox = boundingBox.combine(
                        ((MoleculeScene) sceneList.get(i))
                        .getMolecule().getBoundingBox());
            } // end for
        } else {
            boundingBox = new BoundingBox();
        } // end for
        
        // fire the undo event, so that we can undo!
        SceneUndoableEdit sceneUndo = new SceneUndoableEdit(this);
        sceneUndo.setMoleculeScene(scene);
        editEvent = new UndoableEditEvent(this, sceneUndo);
        fireUndoableEditListenerUndoableEditHappened(editEvent);
        
        // set up the new transformations .. to bring the scene to the center
        recalculateTransformFactor();
        
        // repaint the scenes
        repaint();
    }
    
    /** Getter for property transform.
     * @return Value of property transform.
     *
     */
    public Matrix3D getTransform() {
        return this.transform;
    }
    
    /** Setter for property transform.
     * @param transform New value of property transform.
     *
     */
    public void setTransform(Matrix3D transform) {
        this.transform = transform;
        
        repaint();
    }
    
    /** Getter for property scaleFactor.
     * @return Value of property scaleFactor.
     *
     */
    public double getScaleFactor() {
        return this.scaleFactor;
    }
    
    /** Setter for property scaleFactor.
     * @param scaleFactor New value of property scaleFactor.
     *
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        
        recalculateTransformFactor();
        repaint();
    }
    
    /** Undoable Setter for property scaleFactor.
     * @param scaleFactor New value of property scaleFactor.
     *
     */
    public void setUndoableScaleFactor(double scaleFactor) {
        // save initial scale factor
        zoomEdit  = new ZoomUndoableEdit(this);
        zoomEdit.setInitialScaleFactor(this.scaleFactor);
        
        // set the new scale factor
        setScaleFactor(scaleFactor);
        
        // and save that too
        zoomEdit.setFinalScaleFactor(this.scaleFactor);
        
        // fire undo event
        editEvent = new UndoableEditEvent(this, zoomEdit);
        fireUndoableEditListenerUndoableEditHappened(editEvent);
    }
    
    /** Getter for property xFactor.
     * @return Value of property xFactor.
     *
     */
    public double getXFactor() {
        return this.xFactor;
    }
    
    /** Setter for property xFactor.
     * @param xFactor New value of property xFactor.
     *
     */
    public void setXFactor(double xFactor) {
        this.xFactor = xFactor;
        
        repaint();
    }
    
    /**
     * return the bounding box as computed for this viewer
     *
     * @return bounding box of this viewer
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
    
    /** initilize the screen grid is not already done */
    private void initScreenGrid() {
        if (screenGrid == null) {
            Molecule mol = sceneList.firstElement().getMolecule();
            screenGrid = new ScreenGrid(
                    mol.getMassCenteredBoundingBox().shiftTo(
                    mol.getCenterOfMass()));
        } // end if
    }
    
    /**
     * return the bounding box for the grid enclosing the first molecule
     * in this viewer
     *
     * @return bounding box of grid enclosing first molecule in this viewer
     */
    public BoundingBox getGridBoundingBox() {
        initScreenGrid();
        
        return screenGrid.getGridProperty().getBoundingBox();
    }
    
    /**
     * private method to recalculate transformations
     */
    protected synchronized void recalculateTransformFactor() {
        if (theCanvas == null) return;
        
        double xw = boundingBox.getXWidth();
        double yw = boundingBox.getYWidth();
        double zw = boundingBox.getZWidth();
        
        // get the direction in which maximum span is present
        if (yw > xw) xw = yw;
        if (zw > xw) xw = zw;

        if (xw == 0) xw = 1.0;
        
        // and then calculate the ratio...
        double f1 = theCanvas.getWidth() / xw;
        double f2 = theCanvas.getHeight() / xw;
        
        xFactor = 0.7 * (f1 < f2 ? f1 : f2) * scaleFactor;
    }
    
    /**
     * recalculate bounding box
     */
    protected void recalculateBoundingBox() {
        if (sceneList == null || sceneList.isEmpty()) return;
        
        MoleculeScene scene = sceneList.get(sceneList.size()-1);
        
        if (sceneList.size() == 1) {
            try {
                boundingBox = (BoundingBox) scene.getMolecule().getBoundingBox()
                                                               .clone();
            } catch (CloneNotSupportedException cnse) {
                System.out.println("Unexpected error : " + cnse.toString());
                cnse.printStackTrace();
            } // end of try .. catch block
        } else { // else we need to combine from previous boxes
            boundingBox = boundingBox.combine(
                                         scene.getMolecule().getBoundingBox());
        } // end if
    }
    
    /**
     * forcefully updates the transformation matrix
     */
    public synchronized void updateTransforms() {
        // set up the transform matrix
        transform.unit();
        transform.setScaleFactor(scaleFactor);
        transform.translate(-(boundingBox.getUpperLeft().getX()
                + boundingBox.getBottomRight().getX()) / 2,
                -(boundingBox.getUpperLeft().getY()
                + boundingBox.getBottomRight().getY()) / 2,
                -(boundingBox.getUpperLeft().getZ()
                + boundingBox.getBottomRight().getZ()) / 2);
        transform.mul(rotationMatrix);
                
        transform.scale(xFactor, -xFactor,
                        16 * xFactor / theCanvas.getWidth());        
        transform.mul(translationMatrix);        
        transform.translate(theCanvas.getWidth() / 2,
                            theCanvas.getHeight() / 2, 8);        
    }
    
    /**
     * return the transformation matrix w/o applying rotation matrix
     */
    public synchronized Matrix3D getTransformSansRotation() {
        // set up the transform matrix
        Matrix3D newTransform = new Matrix3D();
        
        newTransform.unit();
        newTransform.translate(-(boundingBox.getUpperLeft().getX()
                + boundingBox.getBottomRight().getX()) / 2,
                -(boundingBox.getUpperLeft().getY()
                + boundingBox.getBottomRight().getY()) / 2,
                -(boundingBox.getUpperLeft().getZ()
                + boundingBox.getBottomRight().getZ()) / 2);
        newTransform.scale(xFactor, -xFactor,
                           16 * xFactor / theCanvas.getWidth());
        newTransform.mul(translationMatrix);
        newTransform.translate(theCanvas.getWidth() / 2,
                               theCanvas.getHeight() / 2, 8);
        
        return newTransform;
    }

    /** the additional info panel */
    private MoleculeViewerAdditionalInfoPanel mvaip;

    /**
     * Show additional information on the first visible molecule scene, if
     * available.
     * 
     * @param showHide show or hide this panel
     */
    public void showAdditionalInfoPanel(boolean showHide) {
        if (mvaip == null) 
            mvaip = MoleculeViewerStockUIFactory.getInstance()
                                              .getDefaultAdditionalInfoPanel();
        if (showHide) mvaip.showIt(this);
        else mvaip.hideIt();
    }

    /**
     * private method to apply transformations
     */
    private synchronized void applyTransforms() {
        // update the transformation matrix
        updateTransforms();        
        
        // update the cuboid transforms
        if ((screenCuboid != null)
        && (selectionState.equals(SelectionState.CUBOID_SELECTION))) {
            screenCuboid.setTransform(transform);
            screenCuboid.applyTransforms();
        } // end if
        
        // update the sphere transforms
        if ((screenSphere != null)
        && (selectionState.equals(SelectionState.SPHERE_SELECTION))) {
            screenSphere.setTransform(transform);
            screenSphere.setScaleFactor(xFactor);
            screenSphere.applyTransforms();
        } // end if
    }
    
    /**
     * private method to apply transformations
     */
    private synchronized void applyTransforms(MoleculeScene ms) {
        // get each scene and set its transformation matrix
        Matrix3D mat = ms.getTransform();
        mat.unit();
        mat.setScaleFactor(transform.getScaleFactor());
        mat.mul(transform);

        // and then apply it
        ms.setTransform(mat);
        ms.applyTransforms();
    }
    
    /**
     * default click event handler
     */
    protected void defaultMouseClicked(MouseEvent e) {
        if (e.isConsumed() || SwingUtilities.isRightMouseButton(e)) {
            return;
        } // end if
        
        Iterator scenes = sceneList.iterator();
        MoleculeScene scene;
        boolean isDoubleClick = (e.getClickCount() == 2);
        
        selectionEdit = new SelectionUndoableEdit(this);
        
        while(scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();
            
            selectionEdit.setMoleculeScene(scene);
            selectionEdit.setInitialSelectionStack(scene.getSelectionStack());
            
            if (isDoubleClick) { // double click .. select a path               
                if (scene.selectPathThatContains(e.getPoint(),
                    (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)
                       == MouseEvent.SHIFT_DOWN_MASK)) {                   
                    selectionEdit.setFinalSelectionStack(
                            scene.getSelectionStack());
                    
                    // fire the event
                    editEvent = new UndoableEditEvent(this, selectionEdit);
                    fireUndoableEditListenerUndoableEditHappened(editEvent);
                    
                    // repaint
                    repaint();
                    
                    e.consume();
                    return;
                } // end if
            } else { // single click .. select a single atom
                if (scene.selectIfContains(e.getPoint())) {
                    selectionEdit.setFinalSelectionStack(
                            scene.getSelectionStack());
                    
                    // fire the event
                    editEvent = new UndoableEditEvent(this, selectionEdit);
                    fireUndoableEditListenerUndoableEditHappened(editEvent);
                    
                    // repaint
                    repaint();
                    
                    e.consume();
                    return;
                } // end if
            } // end if
        } // end while
        
        // if in interrogation mode then pass on the info to
        // the available PropertyScene(s) if any responce is
        // generated then fire an interrogation event to notify
        // the listeners
        if (interrogationMode) {
            for(MoleculeScene ms : sceneList) {
                if (!ms.isVisible()) continue;
                
                Iterator<PropertyScene> pss = ms.getAllPropertyScene();
                
                while(pss.hasNext()) {
                    try {
                       PropertyScene ps = pss.next();
                        
                       if (!ps.isVisible()) continue;
                        
                       PointProperty pp = ps.getClosestVisiblePoint(e.getPoint());
                       System.out.println(pp);
                        
                       PropertyInterrogataionEvent pie = 
                           new PropertyInterrogataionEvent(MoleculeViewer.this);
                        
                       pie.setPointProperty(pp);
                        
                       firePropertyInterrogataionListenerInterrogatedValue(pie);
                        
                       break;
                    } catch(PropertyNotDefinedException pnde) {
                        // ignored!
                    } // end of try .. catch block
                } // end while
            } // end for
        } // end if
        
        e.consume();
    }
    
    /**
     * click event handler
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        currentMouseAdapter.mouseClicked(e);
    }
    
    /**
     * drag event handler
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.isConsumed() || SwingUtilities.isRightMouseButton(e)) {
            return;
        } // end if
        
        int x = e.getX();
        int y = e.getY();
        
        currentMouseAdapter.mouseDragged(e);
        
        repaint();
        
        previousX = x;
        previousY = y;
        e.consume();
    }
    
    /**
     * there comes the mouse...
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        currentMouseAdapter.mouseEntered(e);
        e.consume();
    }
    
    /**
     * there goes the mouse...
     */
    @Override
    public void mouseExited(MouseEvent e) {
        currentMouseAdapter.mouseExited(e);
        e.consume();
    }
    
    /**
     * he moved!!
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        currentMouseAdapter.mouseMoved(e);
        e.consume();
    }
    
    
    /**
     * press(!) event handler
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // does the user wants the popup?
        if (e.isPopupTrigger() && (ideInstance != null) && showViewerPopup) {
            popup.updateMenus();
            popup.show(this, e.getX(), e.getY());
            e.consume();
            
            return;
        } // end if
        
        if (e.isConsumed() || SwingUtilities.isRightMouseButton(e)) {
            return;
        } // end if
        
        previousX = e.getX();
        previousY = e.getY();
        
        selectionUnit.reset();
        selectionUnit.addPoint(previousX, previousY);
        
        currentMouseAdapter.mousePressed(e);
        
        e.consume();
    }
    
    /**
     * release event handler
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // does the user wants the popup?
        if (e.isPopupTrigger() && (ideInstance != null) && showViewerPopup) {
            popup.updateMenus();
            popup.show(this, e.getX(), e.getY());
            e.consume();
            
            return;
        } // end if
        
        if (e.isConsumed() || SwingUtilities.isRightMouseButton(e)) {
            return;
        } // end if
        
        currentMouseAdapter.mouseReleased(e);
        
        e.consume();
    }
    
    /**
     * mouse wheel state listener
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        currentMouseAdapter.mouseWheelMoved(e);
        
        e.consume();
    }
    
    /** Getter for property xTheta.
     * @return Value of property xTheta.
     *
     */
    public double getXTheta() {
        return this.xTheta;
    }
    
    /** Setter for property xTheta.
     * @param xTheta New value of property xTheta.
     *
     */
    public void setXTheta(double xTheta) {
        this.xTheta = xTheta;
        
        repaint();
    }
    
    /** Getter for property yTheta.
     * @return Value of property yTheta.
     *
     */
    public double getYTheta() {
        return this.yTheta;
    }
    
    /** Setter for property yTheta.
     * @param yTheta New value of property yTheta.
     *
     */
    public void setYTheta(double yTheta) {
        this.yTheta = yTheta;
        
        repaint();
    }
    
    /** Getter for property zTheta.
     * @return Value of property zTheta.
     *
     */
    public double getZTheta() {
        return this.zTheta;
    }
    
    /** Setter for property zTheta.
     * @param zTheta New value of property zTheta.
     *
     */
    public void setZTheta(double zTheta) {
        this.zTheta = zTheta;
        
        repaint();
    }
    
    /** Getter for property translateState.
     * @return Value of property translateState.
     *
     */
    public TransformState getTransformState() {
        return this.transformState;
    }
    
    /** Setter for property transformState.
     * @param transformState New value of property transformState.
     *
     */
    public void setTransformState(TransformState transformState) {
        this.transformState = transformState;
        
        currentMouseAdapter =
               (MoleculeViewerMouseAdapter) mouseAdapterMap.get(transformState);
        
        if (secondaryGraphicsEngine != null)
            secondaryGraphicsEngine.setTransformState(transformState);
    }
    
    /** Getter for property backgroundColor.
     * @return Value of property backgroundColor.
     *
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    
    /** Setter for property backgroundColor.
     * @param backgroundColor New value of property backgroundColor.
     *
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        
        repaint();
    }
    
    /** Getter for property xTranslate.
     * @return Value of property xTranslate.
     *
     */
    public double getXTranslate() {
        return this.xTranslate;
    }
    
    /** Setter for property xTranslate.
     * @param xTranslate New value of property xTranslate.
     *
     */
    public void setXTranslate(double xTranslate) {
        this.xTranslate = xTranslate;
        
        translationMatrix.unit();
        translationMatrix.translate(xTranslate, 0.0, 0.0);
        
        recalculateTransformFactor();
        repaint();
    }
    
    /** Getter for property yTranslate.
     * @return Value of property yTranslate.
     *
     */
    public double getYTranslate() {
        return this.yTranslate;
    }
    
    /** Setter for property yTranslate.
     * @param yTranslate New value of property yTranslate.
     *
     */
    public void setYTranslate(double yTranslate) {
        this.yTranslate = yTranslate;
        
        translationMatrix.unit();
        translationMatrix.translate(0.0, yTranslate, 0.0);
        
        recalculateTransformFactor();
        repaint();
    }
    
    /**
     * do XY translation
     *
     * @param xTranslate New value of property xTranslate.
     * @param yTranslate New value of property yTranslate.
     */
    public void setXYTranslate(double xTranslate, double yTranslate) {
        translationMatrix.unit();
        translationMatrix.translate(xTranslate, yTranslate, 0.0);
        
        recalculateTransformFactor();
        repaint();
    }
    
    /**
     * translation matrix here....
     *
     * @param tMat : Matrix3D - the translation matrix
     */
    public void setTranslateMatrix(Matrix3D tMat) {
        translationMatrix = tMat;
        
        recalculateTransformFactor();
        repaint();
    }
    
    /**
     * An undoable translation matrix here....
     *
     * @param tMat : Matrix3D - the translation matrix
     */
    public void setUndoableTranslateMatrix(Matrix3D tMat) {
        // save initial state
        translateEdit = new TranslateUndoableEdit(this);
        translateEdit.setInitialMatrix(translationMatrix);
        
        // the new matrix
        setTranslateMatrix(tMat);
        
        // save final state
        translateEdit.setFinalMatrix(translationMatrix);
        
        // fire undo event
        editEvent = new UndoableEditEvent(this, translateEdit);
        fireUndoableEditListenerUndoableEditHappened(editEvent);
    }
    
    /**
     * returns cloned translation matrix
     *
     * @return Matrix3D - the translation matrix
     */
    public Matrix3D getTranslateMatrix() {
        return (Matrix3D) translationMatrix.clone();
    }
    
    /**
     * rotation matrix here....
     *
     * @param rMat : Matrix3D - the rotation matrix
     */
    public void setRotationMatrix(Matrix3D rMat) {
        rotationMatrix = rMat;
        
        recalculateTransformFactor();
        repaint();
    }
    
    /**
     * Undoable rotation matrix here....
     *
     * @param rMat : Matrix3D - the rotation matrix
     */
    public void setUndoableRotationMatrix(Matrix3D rMat) {
        // save the initial matrix
        rotateEdit = new RotateUndoableEdit(this);
        rotateEdit.setInitialMatrix(rotationMatrix);
        
        // set the new matrix
        setRotationMatrix(rMat);
        
        rotateEdit.setFinalMatrix(rotationMatrix);
        
        // fire undo event
        editEvent = new UndoableEditEvent(this, rotateEdit);
        fireUndoableEditListenerUndoableEditHappened(editEvent);
    }
    
    /**
     * returns cloned rotation matrix
     *
     * @return Matrix3D - the rotation matrix
     */
    public Matrix3D getRotationMatrix() {
        return (Matrix3D) rotationMatrix.clone();
    }
    
    /**
     * reset the transform state of the whole screen
     */
    public void resetTransformState() {
        transform.unit();
        tempMatrix.unit();
        rotationMatrix.unit();
        translationMatrix.unit();
        
        scaleFactor = 1.0;
        
        recalculateTransformFactor();
        repaint();
    }
    
    /**
     * called when scene has changed
     */
    @Override
    public void sceneChanged(MoleculeSceneChangeEvent msce) {
        // ok .. then just redraw the stuff
        if (enableJava3D
              && (msce.getType() == MoleculeSceneChangeEvent.PROPERTY_CHANGE)) {
            if (secondaryGraphicsEngine != null)
                secondaryGraphicsEngine.updateEngineGraphics();
        } // end if
        
        repaint();
    }
    
    /** Getter for property selectionState.
     * @return Value of property selectionState.
     *
     */
    public SelectionState getSelectionState() {
        return this.selectionState;
    }
    
    /** Setter for property selectionState.
     * @param selectionState New value of property selectionState.
     *
     */
    public void setSelectionState(SelectionState selectionState) {
        this.selectionState = selectionState;
        
        currentMouseAdapter =
               (MoleculeViewerMouseAdapter) mouseAdapterMap.get(selectionState);
    }
    
    /** Registers UndoableEditListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addUndoableEditListener(
            UndoableEditListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList();
        }
        listenerList.add(UndoableEditListener.class, listener);

        updateUndoListeners();
    }
    
    /** Removes UndoableEditListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeUndoableEditListener(
            UndoableEditListener listener) {
        listenerList.remove(UndoableEditListener.class, listener);

        updateUndoListeners();
    }

    /** update undolisteners in connected event adapters */
    protected void updateUndoListeners() {
        if (mouseAdapterMap == null) return;
        
        for(ViewerState vs : mouseAdapterMap.keySet()) {
            mouseAdapterMap.get(vs).updateUndoListeners();
        } // end for
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     *
     */
    private void fireUndoableEditListenerUndoableEditHappened(
            UndoableEditEvent event) {
        if (!enableUndo) return;
        
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==UndoableEditListener.class) {
                ((UndoableEditListener)listeners[i+1])
                                       .undoableEditHappened(event);
            }
        }
    }
    
    /** Registers MovieUpdateListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addMovieUpdateListener(
            MovieUpdateListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList();
        }
        listenerList.add(MovieUpdateListener.class, listener);
    }
    
    /** Removes MovieUpdateListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeMovieUpdateListener(
            MovieUpdateListener listener) {
        listenerList.remove(MovieUpdateListener.class, listener);
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     *
     */
    private void fireMovieUpdateListenerMovieUpdateHappened(
                                                   MovieUpdateEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MovieUpdateListener.class) {
                ((MovieUpdateListener)listeners[i+1]).movieUpdate(event);
            }
        }
    }
    
    /**
     * listening to my own changes
     */
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        if (inCompoundEditMode) {
            compoundEdit.addEdit(e.getEdit());
        } else {
            undoManager.addEdit(e.getEdit());
        } // end if
    }
    
    /**
     * begin a compound edit
     */
    public synchronized void beginCompoundEdit() {
        compoundEdit = new CompoundEdit();
        inCompoundEditMode = true;
    }
    
    /**
     * end a compound edit
     */
    public synchronized void endCompoundEdit() {
        compoundEdit.end();
        inCompoundEditMode = false;
        addEdit(compoundEdit);
    }
    
    /**
     * externally add an undoable edit
     */
    public void addEdit(UndoableEdit ue) {
        // fire the event
        editEvent = new UndoableEditEvent(this, ue);
        fireUndoableEditListenerUndoableEditHappened(editEvent);
    }
    
    /**
     * undo the last event
     */
    public void undo() {
        undoManager.undo();
    }
    
    /**
     * redo the last event
     */
    public void redo() {
        undoManager.redo();
    }
    
    /**
     * is undo possible?
     */
    public boolean canUndo() {
        return undoManager.canUndo();
    }
    
    /**
     * is redo possible?
     */
    public boolean canRedo() {
        return undoManager.canRedo();
    }
    
    /**
     * enable undo
     */
    public synchronized void enableUndo() {
        enableUndo = true;
    }
    
    /**
     * disable undo
     */
    public synchronized void disableUndo() {
        enableUndo = false;
    }
    
    /** Setter for property indexSelectionStack.
     * @param selectionStack New value of property selectionStack.
     * @param scene the Molecule scene in which selection is to be made
     */
    public void setIndexSelectionStack(Stack<Integer> selectionStack,
                                       MoleculeScene scene) {
        selectionEdit = new SelectionUndoableEdit(this);
        
        selectionEdit.setMoleculeScene(scene);
        selectionEdit.setInitialSelectionStack(scene.getSelectionStack());
        
        scene.setIndexSelectionStack(selectionStack);
        
        if (!scene.getSelectionStack().equals(
                selectionEdit.getInitialSelectionStack())) {
            
            selectionEdit.setFinalSelectionStack(scene.getSelectionStack());
            
            // fire the event
            editEvent = new UndoableEditEvent(this, selectionEdit);
            fireUndoableEditListenerUndoableEditHappened(editEvent);
        } // end if
        
        repaint();
    }   
    
    /**
     * returns a fuzzy title for this viewer
     */
    public String getViewerTitle() {
        if ((sceneList == null) || (sceneList.size() == 0)) return "";
        
        return ((MoleculeScene) sceneList.get(0)).getMolecule().getTitle()
        + "...";
    }
    
    /**
     * overridden toString()
     */
    @Override
    public String toString() {
        return getViewerTitle();
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
        disablePainting();
        
        for(MoleculeScene scene : sceneList) {
            scene.setEnable3D(enable3D);
        } // end for
        
        this.enable3D = enable3D;
        
        enablePainting();
        repaint();
    }
    
    /**
     * must be called to aid garbage collection, after calling this method,
     * this object becomes unusable and must not be further used!
     */
    public void clearAllReferences() {
        // the canvas reference
        theCanvas = null;
        
        if (sceneList != null) {
            // the listeners
            
            Iterator scenes = sceneList.iterator();
            MoleculeScene scene;
            
            while(scenes.hasNext()) {
                scene = (MoleculeScene) scenes.next();
                
                scene.removeMoleculeSceneChangeListener(this);
                scene.clearAllReferences();
            } // end while
            
            sceneList.removeAllElements();
            sceneList = null;
        } // end if
        
        removeMouseListener(this);
        removeMouseMotionListener(this);
        removeMouseWheelListener(this);
        removeUndoableEditListener(this);
        
        // the popup reference
        popup = null;
        
        // remove references from undo manager
        undoManager.discardAllEdits();
        undoManager = null;        
        listenerList = null;        
    }
    
    /**
     * the finalize method
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
    }
    
    /**
     * Getter for property popup.
     * @return Value of property popup.
     */
    public ViewerPopupMenu getPopup() {
        return this.popup;
    }
    
    /**
     * Getter for property smoothDrawing.
     * @return Value of property smoothDrawing.
     */
    public boolean isSmoothDrawing() {
        return this.smoothDrawing;
    }
    
    /**
     * Setter for property smoothDrawing.
     * @param smoothDrawing New value of property smoothDrawing.
     */
    public void setSmoothDrawing(boolean smoothDrawing) {
        this.smoothDrawing = smoothDrawing;
        
        repaint();
    }
    
    /**
     * Getter for property axisDrawn.
     * @return Value of property axisDrawn.
     */
    public boolean isAxisDrawn() {
        return this.axisDrawn;
    }
    
    /**
     * Setter for property axisDrawn.
     * @param axisDrawn New value of property axisDrawn.
     */
    public void setAxisDrawn(boolean axisDrawn) {
        this.axisDrawn = axisDrawn;
        
        repaint();
    }
    
    /**
     * method to select all those atoms contained in the selecion unit defined
     * by a sphere with a center and appropriate radius.
     *
     * @param center - the center of the sphere
     * @param radius - the radius of the sphere
     */
    public void selectIfContainedIn(Point3D center, double radius) {
        if (sceneList == null || center == null) return;
        
        // make the screen "flat" sphere
        screenSphere = new ScreenSphere(center, radius);
        
        // select the sphere based stuff
        Iterator scenes = sceneList.iterator();
        MoleculeScene scene;
        
        while(scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();
            
            selectionEdit = new SelectionUndoableEdit(MoleculeViewer.this);
            
            selectionEdit.setMoleculeScene(scene);
            selectionEdit.setInitialSelectionStack(scene.getSelectionStack());
            
            scene.selectIfContainedIn(center, radius);
            
            if (!scene.getSelectionStack().equals(
                    selectionEdit.getInitialSelectionStack())) {
                
                selectionEdit.setFinalSelectionStack(scene.getSelectionStack());
                
                // fire the event
                editEvent = new UndoableEditEvent(MoleculeViewer.this,
                        selectionEdit);
                fireUndoableEditListenerUndoableEditHappened(editEvent);
            } // end if
        } // end while
    }
    
    /**
     * method to select all those atoms contained in the selecion unit defined
     * by a bounding box.
     *
     * @param bb - the bounding box
     */
    public void selectIfContainedIn(BoundingBox bb) {
        if (sceneList == null || bb == null) return;
        
        // make the screen cuboid
        screenCuboid = new ScreenCuboid(bb);
        
        // select the cuboid based stuff
        Iterator scenes = sceneList.iterator();
        MoleculeScene scene;
        
        while(scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();
            
            selectionEdit = new SelectionUndoableEdit(MoleculeViewer.this);
            
            selectionEdit.setMoleculeScene(scene);
            selectionEdit.setInitialSelectionStack(scene.getSelectionStack());
            
            scene.selectIfContainedIn(bb);
            
            if (!scene.getSelectionStack().equals(
                    selectionEdit.getInitialSelectionStack())) {
                
                selectionEdit.setFinalSelectionStack(scene.getSelectionStack());
                
                // fire the event
                editEvent = new UndoableEditEvent(MoleculeViewer.this,
                        selectionEdit);
                fireUndoableEditListenerUndoableEditHappened(editEvent);
            } // end if
        } // end while
    }        
    
    /**
     * method to disable painting
     */
    public synchronized void disablePainting() {
        doNotPaint = true;
    }
    
    /**
     * method to enable painting
     */
    public synchronized void enablePainting() {
        doNotPaint = false;
    }
    
    /**
     * start the movie
     */
    @Override
    public void beginMovie() {
        if ((movieThread == null) || (!movieThread.isAlive())) {
            movieThread = new Thread() {
                @Override
                public void run() {
                    // the movie logic goes here
                    movieUpdateEvent.setCurrentMovieScene(0);
                    movieUpdateEvent.setMovieStatus(
                            MovieUpdateEvent.MovieStatus.PLAYING);
                    fireMovieUpdateListenerMovieUpdateHappened(
                            movieUpdateEvent);
                    
                    // disable painting
                    disablePainting();
                    
                    pauseTheMovie = stopTheMovie = false;
                    
                    Iterator<MoleculeScene> scenes = sceneList.iterator();
                    MoleculeScene currentScene;
                    
                    // make sure that there is some thing to show!!
                    if (!scenes.hasNext()) {
                        stopTheMovie = true;
                        enablePainting();
                        return;
                    } else {
                        // hide all the scene
                        for(MoleculeScene scene : sceneList) {
                            scene.setVisible(false);
                        } // end for
                    } // end if
                    
                    currentScene = scenes.next();
                    enablePainting();
                    currentScene.setVisible(true);
                    currentMovieScene = 0;
                    
                    // start the movie loop
                    while(!stopTheMovie) {
                        if (pauseTheMovie) {
                            try {
                                Thread.sleep(movieDelay);  // doose off
                            } catch (InterruptedException ignored) { }
                            
                            continue;
                        } // end if
                        
                        try {
                            Thread.sleep(movieDelay);  // doose off
                        } catch (InterruptedException ignored) { }
                        
                        disablePainting();
                        currentScene.setVisible(false);
                        
                        // is a repeat required?
                        if (!scenes.hasNext()) {
                            if (repeatMovie) {
                                scenes = sceneList.iterator();
                                currentMovieScene = 0;
                            } else {
                                stopTheMovie = true;
                                enablePainting();
                                break;
                            } // end if
                        } // end if
                        
                        currentScene = scenes.next();
                        enablePainting();
                        currentScene.setVisible(true);
                        
                        movieUpdateEvent.setCurrentMovieScene(
                                currentMovieScene);
                        movieUpdateEvent.setMovieStatus(
                                MovieUpdateEvent.MovieStatus.PLAYING);
                        fireMovieUpdateListenerMovieUpdateHappened(
                                movieUpdateEvent);
                        
                        currentMovieScene++;
                    } // end while
                    
                    pauseTheMovie = stopTheMovie = false;
                    
                    // show all the scenes
                    enablePainting();
                    for(MoleculeScene scene : sceneList) {
                        scene.setVisible(true);
                    } // end for
                    
                    movieUpdateEvent.setCurrentMovieScene(currentMovieScene);
                    movieUpdateEvent.setMovieStatus(
                            MovieUpdateEvent.MovieStatus.STOPED);
                    fireMovieUpdateListenerMovieUpdateHappened(
                            movieUpdateEvent);
                }
            }; // end of movie thread
        } // end if
        
        // if the movie is stopped start it ... else do nothing
        if (!movieThread.isAlive()) {
            movieThread.setName("MoleculeViewer movie Thread");
            movieThread.setPriority(Thread.MIN_PRIORITY);
            movieThread.start();
        } // end if
    }
    
    /**
     * stop the movie
     */
    @Override
    public void endMovie() {
        stopTheMovie = true;
        movieThread  = null;
    }
    
    /**
     * pause the movie
     */
    @Override
    public void pauseMovie() {
        pauseTheMovie = true;
        
        movieUpdateEvent.setCurrentMovieScene(currentMovieScene);
        movieUpdateEvent.setMovieStatus(MovieUpdateEvent.MovieStatus.PAUSED);
        fireMovieUpdateListenerMovieUpdateHappened(movieUpdateEvent);
    }
    
    /**
     * resume the movie
     */
    @Override
    public void resumeMovie() {
        pauseTheMovie = false;
        
        movieUpdateEvent.setCurrentMovieScene(currentMovieScene);
        movieUpdateEvent.setMovieStatus(MovieUpdateEvent.MovieStatus.RESUMED);
        fireMovieUpdateListenerMovieUpdateHappened(movieUpdateEvent);
    }
    
    /**
     * Getter for property currentMovieScene.
     * @return Value of property currentMovieScene.
     */
    @Override
    public int getCurrentMovieScene() {
        return this.currentMovieScene;
    }
    
    /**
     * Setter for property currentMovieScene.
     * No movie update events are fired.
     * @param currentMovieScene New value of property currentMovieScene.
     */
    @Override
    public void setCurrentMovieScene(int currentMovieScene) {
        // check first if a valid scene
        if (currentMovieScene < 0 || currentMovieScene > (sceneList.size()-1)) {
            return;
        } // end if
        
        this.currentMovieScene = currentMovieScene;
        
        // stop the current movie
        endMovie();
        while(!stopTheMovie) {
            // yield control so that the movie is hopefully stopped
            try {
                Thread.yield();
                Thread.sleep(movieDelay);
            } catch (InterruptedException ignored) { }
        } // end while
        
        // disable painting
        disablePainting();
        
        // hide all the scene
        for(MoleculeScene scene : sceneList) {
            scene.setVisible(false);
        } // end for
        
        // enable painting
        enablePainting();
        sceneList.get(currentMovieScene).setVisible(true);
    }
    
    /**
     * Getter for property movieDelay.
     * @return Value of property movieDelay.
     */
    @Override
    public int getMovieDelay() {
        return this.movieDelay;
    }
    
    /**
     * Setter for property movieDelay.
     * @param movieDelay New value of property movieDelay.
     */
    @Override
    public void setMovieDelay(int movieDelay) {
        this.movieDelay = movieDelay;
    }
    
    /**
     * Getter for property repeatMovie.
     * @return Value of property repeatMovie.
     */
    @Override
    public boolean isRepeatMovie() {
        return repeatMovie;
    }
    
    /**
     * Setter for property repeatMovie.
     * @param repeatMovie New value of property repeatMovie.
     */
    @Override
    public void setRepeatMovie(boolean repeatMovie) {
        this.repeatMovie = repeatMovie;
    }
    
    /**
     * default empty implementation of MoleculeViewerMouseAdapter
     */
    protected class MoleculeViewerMouseAdapter implements MouseListener,
            MouseMotionListener,
            MouseWheelListener {
        
        public MoleculeViewerMouseAdapter() {}
        
        @Override
        public void mouseClicked(MouseEvent e) {}
        
        @Override
        public void mouseDragged(MouseEvent e) {}
        
        @Override
        public void mouseEntered(MouseEvent e) {}
        
        @Override
        public void mouseExited(MouseEvent e) {}
        
        @Override
        public void mouseMoved(MouseEvent e) {}
        
        @Override
        public void mousePressed(MouseEvent e) {}
        
        @Override
        public void mouseReleased(MouseEvent e) {}
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {}
        
        /** Update any undo listners connected to this adapter */
        public void updateUndoListeners() {}
    } // end of class MoleculeViewerMouseAdapter
    
    /**
     * handles mouse events in scale state
     */
    protected class ScaleStateMouseAdapter extends MoleculeViewerMouseAdapter {
        /** Creates a new instance of ScaleStateMouseAdapter */
        public ScaleStateMouseAdapter() { }
        
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
            
            if (previousY > y) {
                scaleFactor += 0.1;
            } else {
                scaleFactor -= 0.1;

                if (scaleFactor < 0.1) scaleFactor = 0.1;
            } // end if
            
            recalculateTransformFactor();
        }
        
        /**
         * press(!) event handler
         */
        @Override
        public void mousePressed(MouseEvent e) {
            zoomEdit  = new ZoomUndoableEdit(MoleculeViewer.this);
            zoomEdit.setInitialScaleFactor(scaleFactor);
        }
        
        /**
         * mouse wheel state listener
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            zoomEdit  = new ZoomUndoableEdit(MoleculeViewer.this);
            zoomEdit.setInitialScaleFactor(scaleFactor);
            
            if (e.getWheelRotation() < 0) {
                scaleFactor += 0.1;
            } else {
                scaleFactor -= 0.1;

                if (scaleFactor < 0.1) scaleFactor = 0.1;
            } // end if
            
            recalculateTransformFactor();
            repaint();
            
            zoomEdit.setFinalScaleFactor(scaleFactor);
            
            // fire the event
            editEvent = new UndoableEditEvent(MoleculeViewer.this, zoomEdit);
            fireUndoableEditListenerUndoableEditHappened(editEvent);
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            if (zoomEdit == null) return;
            
            zoomEdit.setFinalScaleFactor(scaleFactor);
            
            // fire the event
            editEvent = new UndoableEditEvent(MoleculeViewer.this, zoomEdit);
            fireUndoableEditListenerUndoableEditHappened(editEvent);
        }
    } // end of inner class ScaleStateMouseAdapter
    
    /**
     * handles mouse events in translate state
     */
    protected class TranslateStateMouseAdapter
            extends MoleculeViewerMouseAdapter {
        /** Creates a new instance of TranslateStateMouseAdapter */
        public TranslateStateMouseAdapter() { }
        
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
            
            tempMatrix.unit();
            
            xTranslate = (x - previousX);
            yTranslate = (y - previousY);
            
            tempMatrix.translate(xTranslate, yTranslate, 0.0);
            translationMatrix.mul(tempMatrix);
            
            recalculateTransformFactor();
        }
        
        /**
         * press(!) event handler
         */
        @Override
        public void mousePressed(MouseEvent e) {
            translateEdit = new TranslateUndoableEdit(MoleculeViewer.this);
            translateEdit.setInitialMatrix(translationMatrix);
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            translateEdit.setFinalMatrix(translationMatrix);
            
            // fire the event
            editEvent = new UndoableEditEvent(MoleculeViewer.this,
                    translateEdit);
            fireUndoableEditListenerUndoableEditHappened(editEvent);
        }
    } // end of class TranslateStateMouseAdapter
    
    /**
     * handles mouse events in rotate state
     */
    protected class RotateStateMouseAdapter extends MoleculeViewerMouseAdapter {
        /** Creates a new instance of RotateStateMouseAdapter */
        public RotateStateMouseAdapter() { }
        
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
                tempMatrix.unit();
                
                xTheta = (previousY - y) * (360.0 / theCanvas.getWidth());
                yTheta = (x - previousX) * (360.0 / theCanvas.getHeight());
                
                tempMatrix.rotateAlongX(xTheta);
                tempMatrix.rotateAlongY(yTheta);
                rotationMatrix.mul(tempMatrix);
            } else { // rotation along z
                tempMatrix.unit();
                
                zTheta = (x - previousX + previousY - y)
                          * (360.0 / theCanvas.getWidth());
                
                tempMatrix.rotateAlongZ(zTheta);
                rotationMatrix.mul(tempMatrix);
            } // end if
            
            recalculateTransformFactor();
        }
        
        /**
         * press(!) event handler
         */
        @Override
        public void mousePressed(MouseEvent e) {
            rotateEdit = new RotateUndoableEdit(MoleculeViewer.this);
            rotateEdit.setInitialMatrix(rotationMatrix);
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            rotateEdit.setFinalMatrix(rotationMatrix);
            
            // fire the event
            editEvent = new UndoableEditEvent(MoleculeViewer.this, rotateEdit);
            fireUndoableEditListenerUndoableEditHappened(editEvent);
        }
    } // end of class RotateStateMouseAdapter
    
    /**
     * handles mouse events in free hand selection state
     */
    protected class FreeHandSelectionMouseAdapter
            extends MoleculeViewerMouseAdapter {
        /** Creates a new instance of FreeHandSelectionMouseAdapter */
        public FreeHandSelectionMouseAdapter() { }
        
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
            
            selectionUnit.addPoint(x, y);
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            Iterator scenes = sceneList.iterator();
            MoleculeScene scene;
            
            while(scenes.hasNext()) {
                scene = (MoleculeScene) scenes.next();
                
                selectionEdit = new SelectionUndoableEdit(MoleculeViewer.this);
                
                selectionEdit.setMoleculeScene(scene);
                selectionEdit.setInitialSelectionStack(
                                        scene.getSelectionStack());
                
                scene.selectIfContainedIn(selectionUnit, !e.isControlDown());
                
                if (!scene.getSelectionStack().equals(
                        selectionEdit.getInitialSelectionStack())) {
                    
                    selectionEdit.setFinalSelectionStack(
                                          scene.getSelectionStack());
                    
                    // fire the event
                    editEvent = new UndoableEditEvent(MoleculeViewer.this,
                                                      selectionEdit);
                    fireUndoableEditListenerUndoableEditHappened(editEvent);
                } // end if
            } // end while
            
            // clear the selections
            selectionUnit.reset();
            
            // repaint the stuff to make selection visible
            repaint();
            
            // and if we are in fragmentation mode, then show the jazzy stuff
            // the notification window .. indicating what is to be done
            if (popup.isFragmentationMode()) {
                if (notificationWindow == null) {
                    notificationWindow =  new NotificationWindow(
                            "What can I do with the selection?");
                    JComponent contentPane 
                               = notificationWindow.getContentPane();
                    JButton addFragment, openFragmentWizard, exportSelection;
                    
                    isPartOfWorkspace = MoleculeViewer.this.isPartOfWorkspace();
                    
                    if (isPartOfWorkspace) {
                        contentPane.setLayout(new GridLayout(4, 1, 0, 0));
                    } else {
                        contentPane.setLayout(new GridLayout(2, 1, 0, 0));
                    } // end if
                    infoPane = new JLabel("Choose from the following actions: ",
                            JLabel.LEFT);
                    contentPane.add(infoPane);
                    
                    if (isPartOfWorkspace) {
                        // do show these controls only if part of a workspace
                        addFragment = new JButton("Add as new fragment");
                        addFragment.setToolTipText("Adds the selection as a new"
                                + " fragment");
                        addFragment.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                notificationWindow.hideIt();
                                
                                // first get the selected scene
                                MoleculeScene scene = getFirstSelectedScene();
                                
                                if (scene == null) return;
                                
                                // get the associated molecule
                                Molecule mainMolecule = scene.getMolecule();
                                
                                try {
                                    // then get the molecule selection
                                    Molecule molecule =
                                            scene.getSelectionAsMolecule();
                                    
                                    if (molecule == null) return;
                                    
                                    // now convert the molecule selection to
                                    // a Fragment object and push into the
                                    // fragment list of the main molecule
                                    Fragment fragment = (Fragment)
                                    Utility.getDefaultImplFor(Fragment.class)
                                    .newInstance();
                                    fragment.setParentMolecule(mainMolecule);
                                    
                                    // prepare the fragment object
                                    Iterator<Atom> atoms = molecule.getAtoms();
                                    while(atoms.hasNext()) {
                                        fragment.addFragmentAtom(
                                                new FragmentAtom(atoms.next()));
                                    } // end while
                                    
                                    // and push it into default scheme of
                                    // the relevent molecule
                                    FragmentationScheme scheme = mainMolecule
                                              .getDefaultFragmentationScheme();
                                    
                                    scheme.getFragmentList().addFragment(
                                                                fragment);
                                    System.out.println("added fragment!");
                                    System.out.println(scheme.getFragmentList()
                                                     .getFragments().hasNext());
                                    
                                    // TODO: for testing, remove
                                    scene.showFragment(scheme, fragment, true);
                                } catch (Exception ignored) {
                                    System.err.println(
                                            "Warning! Unable to create "
                                            + "fragment : " + ignored);
                                    ignored.printStackTrace();
                                    
                                    JOptionPane.showMessageDialog(ideInstance,
                                            "Error while creating fragment. "
                                            + ". \n Please look into Runtime " 
                                            + "log for more "
                                            + "information.",
                                            "Error while creating fragment!",
                                            JOptionPane.ERROR_MESSAGE);
                                } // end try .. catch block
                            }
                        });
                        contentPane.add(addFragment);
                        
                        openFragmentWizard 
                                    = new JButton("Open fragment wizard");
                        openFragmentWizard.setToolTipText("Opens a fragment "
                                + "wizard which guides you towards " 
                                + "refinement of the "
                                + "currently selected fragment");
                        openFragmentWizard.addActionListener(
                                new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                notificationWindow.hideIt();
                                
                                // TODO: for testing ... need change
                                FragmentationWizard fw =
                                        new FragmentationWizard(ideInstance);
                                fw.setLocationRelativeTo(ideInstance);
                                fw.setVisible(true);
                            }
                        });
                        contentPane.add(openFragmentWizard);
                    } // emd if
                    
                    // dispace this option always
                    exportSelection = new JButton("Export selection...");
                    exportSelection.setToolTipText("Allows you to save the " +
                            "selection as an external file.");
                    exportSelection.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            notificationWindow.hideIt();
                            
                            // we use the bean shell command here!
                            // for a great simplicity
                            try {
                               Utility.executeBeanShellScript(
                                        "saveSelectedMoleculePart()");
                               ideInstance.setCursor(Cursor.getDefaultCursor());
                            } catch (Exception ignored) {
                               ideInstance.setCursor(Cursor.getDefaultCursor());
                               System.err.println("Warning! Unable to import " +
                                        "commands : " + ignored);
                               ignored.printStackTrace();
                                
                               JOptionPane.showMessageDialog(ideInstance,
                                 "Error while saving file. "
                                 + ". \n Please look into Runtime log for more "
                                 + "information.",
                                 "Error while saving content!",
                                 JOptionPane.ERROR_MESSAGE);
                            } // end try .. catch block
                        }
                    });
                    contentPane.add(exportSelection);
                } // end if
                
                e.consume();
                
                // before we show the notification, set some info
                // get the selected part only
                MoleculeScene selectedScene = getFirstSelectedScene();
                
                if (selectedScene == null) return;
                
                try {
                    Molecule molecule = selectedScene.getSelectionAsMolecule();
                    StringBuffer info = new StringBuffer();
                    
                    info.append("<html><head></head><body>");
                    info.append("You have selected ");
                    info.append(molecule.getNumberOfAtoms());
                    info.append(" atoms as ");
                    info.append(molecule.getFormula().getHTMLFormula());
                    info.append("</body></html>");
                    
                    infoPane.setText(info.toString());
                } catch (Exception ignored) {
                    System.out.println("Unexpected exception : " + ignored);
                    ignored.printStackTrace();
                } // end of try ... catch block
                
                // and then show the notification
                Point p = e.getPoint();
                SwingUtilities.convertPointToScreen(p, MoleculeViewer.this);
                notificationWindow.showIt(MoleculeViewer.this, p.x, p.y);
            } // end if
        }
    } // end of class FreeHandSelectionMouseAdapter
    
    /**
     * handles mouse events in sphere selection state
     */
    protected class PointerSelectionMouseAdapter
            extends MoleculeViewerMouseAdapter {
        /** Creates a new instance of PointerSelectionMouseAdapter */
        public PointerSelectionMouseAdapter() { }
        
        /**
         * click event handler
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            defaultMouseClicked(e);
        }
    } // end of class PointerSelectionMouseAdapter
    
    /**
     * handles mouse events in cuboid selection state
     */
    protected class CuboidSelectionMouseAdapter
            extends MoleculeViewerMouseAdapter {
        /** Creates a new instance of CuboidSelectionMouseAdapter */
        public CuboidSelectionMouseAdapter() { }
        
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
        }
        
        /**
         * press(!) event handler
         */
        @Override
        public void mousePressed(MouseEvent e) {
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
        }
    } // end of class CuboidSelectionMouseAdapter
    
    /**
     * handles mouse events in sphere selection state
     */
    protected class SphereSelectionMouseAdapter
            extends MoleculeViewerMouseAdapter {
        /** Creates a new instance of SphereSelectionMouseAdapter */
        public SphereSelectionMouseAdapter() { }
        
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
        }
        
        /**
         * press(!) event handler
         */
        @Override
        public void mousePressed(MouseEvent e) {
        }
        
        /**
         * release event handler
         */
        @Override
        public void mouseReleased(MouseEvent e) {
        }
    } // end of class SphereSelectionMouseAdapter
    
    /**
     * Holds value of property showViewerPopup.
     */
    protected boolean showViewerPopup;
    
    /**
     * Getter for property showViewerPopup.
     * @return Value of property showViewerPopup.
     */
    public boolean isShowViewerPopup() {
        return this.showViewerPopup;
    }
    
    /**
     * Setter for property showViewerPopup.
     * @param showViewerPopup New value of property showViewerPopup.
     */
    public void setShowViewerPopup(boolean showViewerPopup) {
        this.showViewerPopup = showViewerPopup;
    }
    
    /**
     * Holds value of property enableJava3D.
     */
    protected boolean enableJava3D;
    
    /**
     * Getter for property enableJava3D.
     * @return Value of property enableJava3D.
     */
    public boolean isEnableJava3D() {
        return this.enableJava3D;
    }
    
    /**
     * Setter for property enableJava3D.
     * @param enableJava3D New value of property enableJava3D.
     */
    public void setEnableJava3D(boolean enableJava3D) {
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        
        this.enableJava3D = enableJava3D;
        
        if (!enableJava3D) {
            if (theCanvas3D != null) {                
                remove(theCanvas3D);
                theCanvas3D = null;
            } // end if
        } else {
            initCanvas();
        } // end if             
                
        updateUI();
        parentFrame.updateUI();
    }
    
    /** defines the Java3D state */
    public enum Java3DState {
        EMBEDDING, EMBEDDED, EXTERNAL_FRAME, UNSUPPORTED, UNDETERMINED
    }
    
    /**
     * Write the current Java3D state
     *
     * @param state - the java3d state that the viewer (global) is in
     */
    public void writeJava3DState(Java3DState state) {
        StringResource strings = StringResource.getInstance();
        
        try {
            FileOutputStream fos = new FileOutputStream(
                    strings.getJava3DSettings());
            fos.write(state.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            System.err.println("Warning! Unable to save Java3D state: "
                    + e.toString());
        } // end try .. catch block
    }
    
    /**
     * Read the current Java3D state
     *
     * @return state - the java3d state that the viewer was previously in
     */
    public Java3DState readJava3DState() {
        StringResource strings = StringResource.getInstance();
        Java3DState state = Java3DState.UNDETERMINED;
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(strings.getJava3DSettings())));
            
            String line = br.readLine().trim();
            br.close();
            
            for (Java3DState s : state.values()) {
                if (s.toString().equals(line)) {
                    state = s;
                    break;
                } // end if
            } // end for
            
        } catch (Exception e) {
            System.err.println("Warning! Unable to read Java3D state: "
                    + e.toString());
        } // end try .. catch block
        
        return state;
    }
    
    /**
     * Holds value of property gridDrawn.
     */
    protected boolean gridDrawn;
    
    /**
     * Getter for property gridDrawn.
     * @return Value of property gridDrawn.
     */
    public boolean isGridDrawn() {
        return this.gridDrawn;
    }
    
    /**
     * Setter for property gridDrawn.
     * @param gridDrawn New value of property gridDrawn.
     */
    public void setGridDrawn(boolean gridDrawn) {
        this.gridDrawn = gridDrawn;
    }
    
    /**
     * Setter for property screenGridProperty.
     * @param gp New value of property screenGridProperty.
     */
    public void setScreenGridProperty(GridProperty gp) {
        if (screenGrid != null) {
            screenGrid.setGridProperty(gp);
        } // end if
        
        updateUI();
        if (parentFrame != null) parentFrame.updateUI();
    }
    
    /**
     * Getter for property screenGridProperty.
     * @return value of property screenGridProperty.
     */
    public GridProperty getScreenGridProperty() {
        if (screenGrid != null) {
            return screenGrid.getGridProperty();
        } // end if
        
        return null;
    }
    
    /**
     * Holds value of property multiViewEnabled.
     */
    protected boolean multiViewEnabled;
    
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
    }
    
    /**
     * Holds value of property interrogationMode.
     */
    protected boolean interrogationMode;
    
    /**
     * Getter for property interrogationMode.
     * @return Value of property interrogationMode.
     */
    public boolean isInterrogationMode() {
        return this.interrogationMode;
    }
    
    /**
     * Setter for property interrogationMode.
     * @param interrogationMode New value of property interrogationMode.
     */
    public void setInterrogationMode(boolean interrogationMode) {
        this.interrogationMode = interrogationMode;
    }

    /**
     * Registers PropertyInterrogataionListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addPropertyInterrogataionListener(
                                      PropertyInterrogataionListener listener) {
        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(PropertyInterrogataionListener.class, listener);
    }

    /**
     * Removes PropertyInterrogataionListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removePropertyInterrogataionListener(
                                   PropertyInterrogataionListener listener) {
        listenerList.remove(PropertyInterrogataionListener.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event The event to be fired
     */
    protected void firePropertyInterrogataionListenerInterrogatedValue(
                                           PropertyInterrogataionEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList ();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i]==PropertyInterrogataionListener.class) {
                ((PropertyInterrogataionListener)
                          listeners[i+1]).interrogatedValue(event);
            }
        }
    }
    
    /**
     * The current secondary graphics engine
     */
    protected MoleculeViewerGraphicsEngine secondaryGraphicsEngine;
    
    /**
     * Get property of secondaryGraphicsEngine
     * 
     * @return current instance of secondaryGraphicsEngine
     */
    public MoleculeViewerGraphicsEngine getSecondaryGraphicsEngine() {
        return this.secondaryGraphicsEngine;
    }
    
    /**
     * Set property of secondaryGraphicsEngine
     * 
     * @param mvge a new implementation for secondaryGraphicsEngine
     */
    public void setSecondaryGraphicsEngine(MoleculeViewerGraphicsEngine mvge) {
        this.secondaryGraphicsEngine = mvge;
    }
    
} // end of class MoleculeViewer
