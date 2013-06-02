/*
 * MoleculeListDialog.java
 *
 * Created on April 2, 2007, 9:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.meta.molecule.Molecule;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.SelectionState;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.TransformState;

/**
 * Displays list of loaded molecules.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeListDialog extends JDialog {
    
    private MeTA ideInstance;
    
    private JPanel moleculeListPanel;
    
    private JComboBox mList;
    
    private JButton okOption, cancelOption;
    
    private boolean okClicked = false;
    
    /** Creates a new instance of MoleculeListDialog */
    public MoleculeListDialog(MeTA ideInstance) {
        super(ideInstance);
        
        this.ideInstance = ideInstance;
        
        setTitle("Select a molecule from the list");
        
        initUI();
    }
    
    /**
     * Initilize UI
     */
    private void initUI() {
        Enumeration<JComponent> frames =
                ideInstance.getWorkspaceDesktop().getFrameList();
        
        Vector<Molecule> molList = new Vector<Molecule>();
        
        while(frames.hasMoreElements()) {
            JComponent frame = frames.nextElement();
            if (frame instanceof MoleculeViewerFrame) {
                Vector<MoleculeScene> scenes =
                        ((MoleculeViewerFrame) frame).getSceneList();
                
                for (MoleculeScene scene : scenes) {
                    Molecule molecule = scene.getMolecule();
                    molList.add(molecule);
                } // end for
            } // end if
        } // end while
        
        
        moleculeListPanel = new JPanel(new BorderLayout());
            JPanel topPanel = new JPanel(new FlowLayout());
            topPanel.add(new JLabel("Select a molecule object: ", 
                                     JLabel.RIGHT));
            final MoleculeViewer moleculeViewer 
                                 = new MoleculeViewer(null, null);
            mList = new JComboBox(molList);
            mList.setEditable(false);
            mList.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent lse)  {
                  Molecule mol = (Molecule) mList.getSelectedObjects()[0];
                  
                  if (moleculeViewer.getSceneList().size() != 0) {
                     moleculeViewer.removeScene(
                                     moleculeViewer.getSceneList().get(0));
                  } // end if 
                  
                  moleculeViewer.addScene(new MoleculeScene(mol));
                  repaint();
               }
            });
            if (molList.size() > 0) mList.setSelectedIndex(0);
            topPanel.add(mList);
        moleculeListPanel.add(topPanel, BorderLayout.NORTH);
            
            moleculeViewer.setSelectionState(SelectionState.NO_STATE);
            moleculeViewer.setTransformState(TransformState.ROTATE_STATE);
            
        moleculeListPanel.add(moleculeViewer, BorderLayout.CENTER);
        
        Container contentPane = getContentPane();
        
        contentPane.setLayout(new BorderLayout());
        contentPane.add(moleculeListPanel, BorderLayout.CENTER);
        
        JPanel optionPanel = new JPanel(new GridLayout(1, 2));
            okOption = new JButton("Ok");            
            okOption.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   okClicked = true;
                   
                   setVisible(false);
                   dispose();
               }
            });
            optionPanel.add(okOption);
            
            cancelOption = new JButton("Cancel");            
            cancelOption.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   okClicked = false;
                   
                   setVisible(false);
                   dispose();
               }
            });
            optionPanel.add(cancelOption);
        contentPane.add(optionPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Return only the panel UI, so that it can be embedded some where else.
     * 
     * @return instance of JPanel containing the UI for molecule list
     */
    public JPanel getMoleculeListPanel() {
        return moleculeListPanel;
    }
    
    /**
     * Show the dialog...          
     */
    public Molecule showListDialog() {            
        setModal(true);
        setSize(new Dimension(400, 350));
        setLocationRelativeTo(ideInstance);
        setVisible(true);
        
        if (okClicked) {
            return getSelectedMolecule();
        } else {
            return null;
        } // end if
    }
    
    /**
     * Return the selected Molecule object
     *
     * @return the Molecule object
     */
    public Molecule getSelectedMolecule() {
        try {
            return (Molecule) mList.getSelectedObjects()[0];
        } catch(Exception e) {
            return null;
        } // end of try .. catch block
    }

    /**
     * Getter for property okOptionText.
     * @return Value of property okOptionText.
     */
    public String getOkOptionText() {
        return okOption.getText();
    }

    /**
     * Setter for property okOptionText.
     * @param okOptionText New value of property okOptionText.
     */
    public void setOkOptionText(String okOptionText) {
        okOption.setText(okOptionText);
    }

    /**
     * Getter for property cancelOptionText.
     * @return Value of property cancelOptionText.
     */
    public String getCancelOptionText() {
        return cancelOption.getText();
    }

    /**
     * Setter for property cancelOptionText.
     * @param cancelOptionText New value of property cancelOptionText.
     */
    public void setCancelOptionText(String cancelOptionText) {
        cancelOption.setText(cancelOptionText);
    }
    
    /**
     * Add an action listener to OK button of this dialog
     *
     * @param al the ActionListener instance.
     */
    public void addOkOptionActionListener(ActionListener al) {
        okOption.addActionListener(al);
    }
    
    /**
     * Add an action listener to CANCEL button of this dialog
     *
     * @param al the ActionListener instance.
     */
    public void addCancelOptionActionListener(ActionListener al) {
        cancelOption.addActionListener(al);
    }
    
} // end of class MoleculeListDialog
