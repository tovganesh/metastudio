/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.shell.idebeans.chemnotes;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import org.meta.chemnotes.ChemNotebook;
import org.meta.chemnotes.NotebookGlyph;
import org.meta.chemnotes.NotebookGlyph.GlyphType;
import org.meta.chemnotes.event.NotebookGlyphModifiedEvent;
import org.meta.chemnotes.event.NotebookModificationEvent;
import org.meta.chemnotes.event.NotebookModificationListener;

/**
 * The Notebook main UI
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotebookArea extends JPanel
                          implements MouseListener, MouseMotionListener,
                                     DropTargetListener, NotebookModificationListener { 
    private ChemNotebook chemNotebook;
    private DropTarget notebookDropTarget;
    private JScrollPane notebookAreaScrollPane;
    
    /** Creates new instance of NotebookArea */
    public NotebookArea(ChemNotebook chemNotebook) {
        this.chemNotebook = chemNotebook;        
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new SpringLayout());   
        
        // add listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        this.chemNotebook.addNotebookModificationListener(this);
        
        // add drop target
        notebookDropTarget = new DropTarget(this, this);
        setDropTarget(notebookDropTarget);
                
        notebookAreaScrollPane = new JScrollPane(this);
        setAutoscrolls(true);        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO: 
        String id = System.currentTimeMillis() + "";
        
        NotebookGlyphUI nbui = NotebookGlyphUIFactory.getInstance().createInstance(
                                       id, GlyphType.cnb_text, chemNotebook, 
                                       e.getX(), e.getY(), 100, 100);        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO: 
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO: 
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO: 
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO: 
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        ((JPanel) e.getSource()).scrollRectToVisible(r);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO: 
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO: 
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO: 
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO: 
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO: 
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        // TODO: 
    }

    @Override
    public void notebookModified(NotebookModificationEvent nme) {            
        // TODO:
        if (nme.isGlyphModifedEvent()) {
            NotebookGlyphModifiedEvent ngme = nme.getGlyphModificationEvent();
            
            switch(ngme.getModificationType()) {
                case ADDED:
                {                    
                    NotebookGlyph ng = ngme.getNotebookGlyph();
                    
                    SpringLayout.Constraints sc = new SpringLayout.Constraints();
                    
                    sc.setX(Spring.constant(ng.getX()));
                    sc.setY(Spring.constant(ng.getY()));
                    sc.setHeight(Spring.constant(ng.getHeight()));        
                    sc.setWidth(Spring.constant(ng.getWidth()));
                    
                    add(((NotebookGlyphUI) ng).createUIRepresentation(), sc);                                        
                    
                    int width = ng.getX() + ng.getWidth();
                    int height = ng.getY() + ng.getHeight();
                    
                    width = Math.max(width, getPreferredSize().width);
                    height = Math.max(height, getPreferredSize().height);
                    
                    setPreferredSize(new Dimension(width, height));
                    
                    scrollRectToVisible(new Rectangle(0, height, width, height));                    
                    
                    updateUI();                                                               
                }
            } // end of switch .. case
        } // end if
    }
}
