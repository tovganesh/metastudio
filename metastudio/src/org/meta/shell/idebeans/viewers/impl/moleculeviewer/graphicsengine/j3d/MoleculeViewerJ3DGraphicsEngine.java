/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j3d;

import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.AbstractMoleculeViewerGraphicsEngine;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.*;
import com.sun.j3d.exp.swing.JCanvas3D;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.RenderedImage;
import java.util.Vector;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;

/**
 * Java 3D implementation of MoleculeViewerGraphicsEngine
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeViewerJ3DGraphicsEngine 
             extends AbstractMoleculeViewerGraphicsEngine {

    private JPanel theCanvas3D;    
    private Vector<MoleculeJ3DScene> sceneList;
    
    public MoleculeViewerJ3DGraphicsEngine() {        
    }
    
    /**
     * Create the graphics of the list of basic MoleculeScene objects
     * 
     * @param msl the molecule scene list
     * @return the created graphics in a JPanel
     */
    @Override
    public JPanel createEngineGraphics(Vector<MoleculeScene> msl) { 
        initCanvas3D();
        
        this.sceneList = new Vector<MoleculeJ3DScene>(msl.size());
        
        // build the molecule scene
        for (MoleculeScene ms : msl) {            
            MoleculeJ3DScene scene = new MoleculeJ3DScene(ms);
            
            sceneList.add(scene);
        } // end for               
        
        return theCanvas3D;
    }
    
    /** initialize the Java3D canvas */
    private void initCanvas3D() {
        theCanvas3D = new JCanvas3D(new GraphicsConfigTemplate3D());
        
        ((JCanvas3D) theCanvas3D).setResizeMode(JCanvas3D.RESIZE_DELAYED);                
    }
    
    /**
     * update the engine graphics
     */
    @Override
    public void updateEngineGraphics() {        
        buildJava3DScene();
        theCanvas3D.updateUI();
    }
    
    private SimpleUniverse universe;
    
    /**
     * make the Java3D scene
     *
     * Note: Most of the transformation code written with the help of JMV source
     * and hence is highly influenced from it. However, there are a lot of
     * differences in the structure of this and JMV code and is not a mere copy!
     */
    private void buildJava3DScene() {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        
        // Create the TransformGroup node and initialize it to the
        // identity. Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at run time. Add it to
        // the root of the subgraph.
        
        BoundingBox bb = viewer.getBoundingBox();
        float max = (float) Math.max(Math.max(bb.getXWidth(), 
                                              bb.getYWidth()),
                                              bb.getZWidth());
        if (max == 0.0f) max = 1.0f;
        
        // TODO: transforms
        Transform3D t3dTrans = new Transform3D();
        Point3D center = viewer.getBoundingBox().center();
        t3dTrans.set(new Vector3d(-center.getX(),
                                  -center.getY(),
                                  -center.getZ()-(max*3.0)));
        
        viewer.updateTransforms();
        
        TransformGroup objTrans = new TransformGroup(t3dTrans);
        
        TransformGroup objRot = new TransformGroup();
        objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        // build the molecule scene
        for (MoleculeJ3DScene scene : sceneList) {            
            scene.setTransformGroup(objRot);            
        } // end for
        
        // bg color
        objRot.addChild(new Background(
                                 backgroundColor.getRed()   / 255.0f,
                                 backgroundColor.getGreen() / 255.0f,
                                 backgroundColor.getBlue()  / 255.0f)
                        );
        
        // set up the bounding parameters
        Point3D ul = bb.getUpperLeft();
        Point3D br = bb.getBottomRight();
        javax.media.j3d.BoundingBox bounds = new javax.media.j3d.BoundingBox(
                new Point3d(ul.getX()-100.0, ul.getY()-100.0, ul.getZ()-100.0),
                new Point3d(br.getX()+100.0, br.getY()+100.0, br.getZ()+100.0));
        
        // place the "molecule(s)" in the center:
        Vector3f position = new Vector3f(0.0f, 0.0f, -1.5f*max);
        Transform3D translate = new Transform3D();
        Transform3D trans2 = new Transform3D();
        
        translate.set(2.5/max);
        trans2.set(position);
        
        translate.mul(trans2);
        TransformGroup t = new TransformGroup(translate);
        objTrans.addChild(t);
        
        // bg color
        objTrans.addChild(new Background(
                                 backgroundColor.getRed()   / 255.0f,
                                 backgroundColor.getGreen() / 255.0f,
                                 backgroundColor.getBlue()  / 255.0f)
                        );
        
        objTrans.addChild(objRot);
        objRoot.addChild(objTrans);
        
        // bg color .. on the root node
        objRoot.addChild(new Background(
                                 backgroundColor.getRed()   / 255.0f,
                                 backgroundColor.getGreen() / 255.0f,
                                 backgroundColor.getBlue()  / 255.0f)
                        );
        
        // Set up the ambient light
        Color3f ambientColor = new Color3f(0.3f, 0.3f, 0.3f);
        AmbientLight ambientLightNode = new AmbientLight(ambientColor);
        ambientLightNode.setInfluencingBounds(bounds);
        objRoot.addChild(ambientLightNode);
        
        // Set up the directional lights
        Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
        Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
        Color3f light2Color = new Color3f(1.0f, 1.0f, 0.9f);
        Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
        
        DirectionalLight light1
                = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        objRoot.addChild(light1);
        
        DirectionalLight light2
                = new DirectionalLight(light2Color, light2Direction);
        light2.setInfluencingBounds(bounds);
        objRoot.addChild(light2);
     
        // mouse interactions
        MouseRotate mr = new MouseRotate(theCanvas3D, objRot) {
            @Override
            public void mousePressed(MouseEvent e) {
                // does the user wants the popup?
                if (e.isPopupTrigger() && (ideInstance != null)
                   && showViewerPopup) {
                    popup.updateMenus();
                    popup.show(viewer, e.getX(), e.getY());
                    e.consume();
                } // end if
                
                if (transformState != TransformState.ROTATE_STATE) return;
                
                super.mousePressed(e);
            }
        };
        mr.setSchedulingBounds(bounds);
        mr.setSchedulingInterval(1);
        objRoot.addChild(mr);
        
        MouseWheelZoom mz = new MouseWheelZoom(theCanvas3D, objRot) {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (transformState != TransformState.SCALE_STATE) return;
                
                MouseWheelEvent ne = new MouseWheelEvent(e.getComponent(),
                        e.getID(), e.getWhen(), e.getModifiers(),
                        e.getX(), e.getY(), e.getClickCount(), 
                        e.isPopupTrigger(), e.getScrollType(),
                        e.getScrollAmount(), -e.getWheelRotation());
                
                super.mouseWheelMoved(ne);
            }
        };
        mz.setSchedulingBounds(bounds);
        mz.setSchedulingInterval(1);
        objRoot.addChild(mz);
        
        // compile the scene
        objRoot.compile();
        
        //TODO: this is awful and must not be done like that in final version
        // ... lifted comments ;)  
        if (universe != null) {
            universe.cleanup();
        } // end if        
        
        universe = new SimpleUniverse(
                             ((JCanvas3D) theCanvas3D).getOffscreenCanvas3D());
        
        PhysicalBody pb = universe.getViewer().getPhysicalBody();
        Point3d lefteye = new Point3d(-0.001, 0.0, 0.0);
        
        pb.setLeftEyePosition(lefteye);
        Point3d righteye = new Point3d(0.001, 0.0, 0.0);
        pb.setRightEyePosition(righteye);
        
        Point3d leftp3d = new Point3d(-0.142, 0.135, 0.4572);
        ((JCanvas3D) theCanvas3D).getOffscreenCanvas3D()
        .setLeftManualEyeInImagePlate(leftp3d);
        
        Point3d rightp3d = new Point3d(0.142, 0.135, 0.4572);
        ((JCanvas3D) theCanvas3D).getOffscreenCanvas3D()
        .setRightManualEyeInImagePlate(rightp3d);
        
        // set the background color
        theCanvas3D.setBackground(backgroundColor);
        ((JCanvas3D) theCanvas3D).getOffscreenCanvas3D()
                                 .setBackground(backgroundColor);
        
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.getViewer().getView().setMinimumFrameCycleTime(30);
        universe.getViewer().getView().setBackClipDistance(1000.0d);
        universe.getViewer().getView().setFrontClipDistance(0.01d);  
        universe.addBranchGraph(objRoot);
    }   
    
    /**
     * Return the current screen image
     * 
     * @return the instance of current screen image 
     */
    @Override
    public RenderedImage getScreenImage() {
        try {
            return ((JCanvas3D) theCanvas3D).getOffscreenCanvas3D()
                    .getOffScreenBuffer().getRenderedImage();
        } catch (Exception ignored) {
            System.err.println("Exception in MoleculeViewerJ3DGraphicsEngine." +
                    "getScreenImage(): " + ignored.toString());
            ignored.printStackTrace();
            
            return null;
        } // end of try .. catch block
    }
}
